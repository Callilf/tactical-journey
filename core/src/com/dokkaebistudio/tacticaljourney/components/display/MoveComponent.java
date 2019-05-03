package com.dokkaebistudio.tacticaljourney.components.display;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.Tile;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class MoveComponent implements Component, Poolable, RoomSystem {

	/** The room that managed entities.*/
	public Room room;
	
	// Serialized attributes
	
	/** The number of tiles the player can move. */
	private int moveSpeed;
	
	/** The number of tiles the player can move during this turn. */
	private int moveRemaining;

	/** Temporary modifier. */
	private boolean frozen;
	
	
	// Other attributes
	
	/** Whether we are in free move mode, which mean there are no enemies in the room. */
	private boolean freeMove;
	
	/** The tiles where the player can move. */
	public Set<Tile> allWalkableTiles;
	
	/** The entities used to display the blue tiles where the player can move. */
	public Set<Entity> movableTiles = new HashSet<>();
		
	/** The selected tile for movement. */
	private Entity selectedTile;
	
	/** The arrows displaying the paths to the selected tile. */
	private List<Entity> wayPoints = new ArrayList<>();
	
	// Continuous move in empty rooms
	
	/** The selected tile for movement that was out of reach at the previous turn. */
	private Tile selectedTileFromPreviousTurn;
	private Tile endTurnTile;
	
	
	// Fast attack
	/** The attack tile that was selected and created the selected tile.
	 * If the user click on it again, the player will move to the selectedTile and attack. */
	private Entity selectedAttackTile;
	private Entity fastAttackTarget;
	
	
	public boolean moving;
	public boolean arrivedOnTile;
	public Vector2 currentMoveDestinationTilePos;
	public Vector2 currentMoveDestinationPos;
	public int currentMoveDestinationIndex;
	
	
	@Override
	public void enterRoom(Room newRoom) {
		this.room = newRoom;
		clearMovableTiles();
		clearSelectedTile();
	}
	
	@Override
	public void reset() {
		clearMovableTiles();
		this.room = null;
		this.selectedAttackTile = null;
		this.frozen = false;
		this.freeMove = false;
	}
	
	
	/**
	 * Increase the move speed by the given amount.
	 * @param amount the amount to add
	 */
	public void increaseMoveSpeed(int amount) {
		this.setMoveSpeed(this.getMoveSpeed() + amount);
	}
	
	
	/**
	 * Select the correct target given the currentMoveDestinationIndex
	 * @param gridPositionM the gridPositionMapper
	 */
	public void selectCurrentMoveDestinationTile(Entity mover) {
		Entity target = null;
		if (this.getWayPoints().size() > this.currentMoveDestinationIndex) {
			target = this.getWayPoints().get(this.currentMoveDestinationIndex);
		} else {
			target = this.getSelectedTile();
		}
		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(target);
		this.currentMoveDestinationTilePos = gridPositionComponent.coord();
		this.currentMoveDestinationPos = TileUtil.convertGridPosIntoPixelPos(gridPositionComponent.coord());
		
//		Mappers.gridPositionComponent.get(mover).coord(mover, this.currentMoveDestinationTilePos, room);
	}
	
	public void incrementCurrentMoveDestinationIndex() {
		if (this.getWayPoints().size() > this.currentMoveDestinationIndex) {
			Entity reachedWayPoint = this.getWayPoints().get(this.currentMoveDestinationIndex);
			SpriteComponent wayPointSprite = Mappers.spriteComponent.get(reachedWayPoint);
			wayPointSprite.hide = true;
		}
		this.currentMoveDestinationIndex++;	
	}
	
	/**
	 * Clear the list of movable tiles and remove all entities associated to it.
	 */
	public void clearMovableTiles() {
		for (Entity e : movableTiles) {
			room.removeEntity(e);
		}
		movableTiles.clear();
		
		if (allWalkableTiles != null) {
			allWalkableTiles.clear();
		}
		
		clearSelectedTile();
	}
	
	/**
	 * Clear the list of movable tiles and remove all entities associated to it.
	 */
	public void clearSelectedTile() {
		if (this.selectedTileFromPreviousTurn == null) {
			if (this.selectedTile != null) {
				room.removeEntity(this.selectedTile);
			}
			this.selectedTile = null;
			
			for (Entity e : wayPoints) {
				room.removeEntity(e);
			}
			wayPoints.clear();
		}
	}
	
	public void removeFirstWaypoints(int numberOfWaypointsToRemove) {
        for (int i=0; i<numberOfWaypointsToRemove ; i++) {
        	Entity entity = this.getWayPoints().remove(0);
        	room.removeEntity(entity);
        }
	}
	
	public void clearSelectedTileFromPreviousTurn() {
		this.selectedTileFromPreviousTurn = null;
		this.endTurnTile = null;
	}
	
	

	public Entity getSelectedTile() {
		return selectedTile;
	}

	public void setSelectedTile(Entity selectedTile) {
		if (this.selectedTile != null) {
			GridPositionComponent newPos = Mappers.gridPositionComponent.get(selectedTile);
			Mappers.gridPositionComponent.get(selectedTile).coord(this.selectedTile, newPos.coord(), room);
		} else {
			this.selectedTile = selectedTile;
		}
	}


	public List<Entity> getWayPoints() {
		return wayPoints;
	}



	public void setWayPoints(List<Entity> wayPoints) {
		for (Entity e : this.wayPoints) {
			room.removeEntity(e);
		}
		this.wayPoints = wayPoints;
	}
	
	
	public void showMovableTiles() {
		if (!freeMove) {
			for (Entity e : movableTiles) {
				SpriteComponent spriteComponent = Mappers.spriteComponent.get(e);
				spriteComponent.hide = false;
			}
		}
	}
	public void hideMovableTiles() {
		for (Entity e : movableTiles) {
			SpriteComponent spriteComponent = Mappers.spriteComponent.get(e);
			spriteComponent.hide = true;
		}
	}
	
	public void hideMovementEntities() {
		for (Entity e : wayPoints) {
			SpriteComponent spriteComponent = Mappers.spriteComponent.get(e);
			spriteComponent.hide = true;
		}
		if (this.selectedTile != null) {
			SpriteComponent spriteComponent = Mappers.spriteComponent.get(this.selectedTile);
			spriteComponent.hide = true;
		}
	}

	public Entity getSelectedAttackTile() {
		return selectedAttackTile;
	}

	public void setSelectedAttackTile(Entity selectedAttackTile) {
		this.selectedAttackTile = selectedAttackTile;
	}

	public Entity getFastAttackTarget() {
		return fastAttackTarget;
	}

	public void setFastAttackTarget(Entity fastAttackTarget) {
		this.fastAttackTarget = fastAttackTarget;
	}

	public int getMoveSpeed() {
		if (frozen) return 0;
		return moveSpeed;
	}

	public void setMoveSpeed(int moveSpeed) {
		this.moveSpeed = moveSpeed;
	}

	public int getMoveRemaining() {
		if (frozen) return 0;
		return moveRemaining;
	}

	public void setMoveRemaining(int moveRemaining) {
		this.moveRemaining = moveRemaining;
	}

	public boolean isFrozen() {
		return frozen;
	}

	public void setFrozen(boolean frozen) {
		this.frozen = frozen;
	}
	
	public boolean isFreeMove() {
		if (isFrozen()) return false;
		return freeMove;
	}

	public void setFreeMove(boolean freeMove) {
		this.freeMove = freeMove;
	}
	
	
	
	
	public static Serializer<MoveComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<MoveComponent>() {

			@Override
			public void write(Kryo kryo, Output output, MoveComponent object) {
				output.writeInt(object.moveSpeed);
				output.writeInt(object.moveRemaining);
				output.writeBoolean(object.frozen);
			}

			@Override
			public MoveComponent read(Kryo kryo, Input input, Class<MoveComponent> type) {
				MoveComponent compo = engine.createComponent(MoveComponent.class);
				compo.moveSpeed = input.readInt();
				compo.moveRemaining = input.readInt();
				compo.frozen = input.readBoolean();
				return compo;
			}
		
		};
	}

	public Tile getSelectedTileFromPreviousTurn() {
		return selectedTileFromPreviousTurn;
	}

	public void setSelectedTileFromPreviousTurn(Tile selectedTileFromPreviousTurn) {
		this.selectedTileFromPreviousTurn = selectedTileFromPreviousTurn;
	}

	public Tile getEndTurnTile() {
		return endTurnTile;
	}

	public void setEndTurnTile(Tile endTurnTile) {
		this.endTurnTile = endTurnTile;
	}
	
}
