package com.dokkaebistudio.tacticaljourney.components.display;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class MoveComponent implements Component, Poolable, RoomSystem {

	/** The room that managed entities.*/
	public Room room;
	
	/** The number of tiles the player can move. */
	public int moveSpeed;
	
	/** The number of tiles the player can move during this turn. */
	public int moveRemaining;
	
	/** Whether we are in free move mode, which mean there are no enemies in the room. */
	public boolean freeMove;
	
	/** The tiles where the player can move. */
	public Set<Entity> allWalkableTiles;
	
	/** The entities used to display the blue tiles where the player can move. */
	public Set<Entity> movableTiles = new HashSet<>();
		
	/** The selected tile for movement. */
	private Entity selectedTile;
	
	/** The arrows displaying the paths to the selected tile. */
	private List<Entity> wayPoints = new ArrayList<>();
	
	public Vector2 currentMoveDestinationTilePos;
	public Vector2 currentMoveDestinationPos;
	public int currentMoveDestinationIndex;
	
	
	@Override
	public void enterRoom(Room newRoom) {
		this.room = newRoom;
	}
	
	@Override
	public void reset() {
		clearMovableTiles();
		this.room = null;
	}
	
	
	/**
	 * Increase the move speed by the given amount.
	 * @param amount the amount to add
	 */
	public void increaseMoveSpeed(int amount) {
		this.moveSpeed += amount;
	}
	
	
	/**
	 * Select the correct target given the currentMoveDestinationIndex
	 * @param gridPositionM the gridPositionMapper
	 */
	public void selectCurrentMoveDestinationTile() {
		Entity target = null;
		if (this.getWayPoints().size() > this.currentMoveDestinationIndex) {
			target = this.getWayPoints().get(this.currentMoveDestinationIndex);
		} else {
			target = this.getSelectedTile();
		}
		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(target);
		this.currentMoveDestinationTilePos = gridPositionComponent.coord();
		this.currentMoveDestinationPos = TileUtil.convertGridPosIntoPixelPos(gridPositionComponent.coord());
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
		if (this.selectedTile != null) {
			room.removeEntity(this.selectedTile);
		}
		this.selectedTile = null;

		for (Entity e : wayPoints) {
			room.removeEntity(e);
		}
		wayPoints.clear();
	}
	
	

	public Entity getSelectedTile() {
		return selectedTile;
	}

	public void setSelectedTile(Entity selectedTile) {
		if (this.selectedTile != null) {
			room.removeEntity(this.selectedTile);
		}
		this.selectedTile = selectedTile;
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
	
	
}
