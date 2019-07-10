package com.dokkaebistudio.tacticaljourney.ces.systems.iteratingsystems;

import java.util.Optional;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.dokkaebistudio.tacticaljourney.ces.components.DestructibleComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.KnockbackComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.SolidComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.creep.CreepComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.ces.entity.PublicEntity;
import com.dokkaebistudio.tacticaljourney.ces.systems.NamedIteratingSystem;
import com.dokkaebistudio.tacticaljourney.enums.DirectionEnum;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.Tile;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.MovementHandler;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class KnockbackSystem extends NamedIteratingSystem {

	public Stage stage;

	public KnockbackSystem(Room r, Stage s) {
		super(Family.all(KnockbackComponent.class).get());
		this.priority = 21;
		room = r;
		this.stage = s;
	}

	@Override
	protected void performProcessEntity(final Entity entity, float deltaTime) {

		KnockbackComponent throwbackCompo = Mappers.knockbackComponent.get(entity);

		KnockbackDestinationTile targetedTile = this.getDestinationTile(entity, throwbackCompo.getDirection(), throwbackCompo.getNumberOfTiles(),   room);
		if (targetedTile.noKnockback) {
			// Cancel throwback
			entity.remove(KnockbackComponent.class);
			return;
		}

		createThrowbackImage(entity, targetedTile, throwbackCompo.getAttacker());


	}

	private void createThrowbackImage(final Entity entity, KnockbackDestinationTile destination, Entity attacker) {
		room.pauseState();

		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(entity);

		SpriteComponent spriteComponent = Mappers.spriteComponent.get(entity);
		spriteComponent.hide = true;
		((PublicEntity) entity).hideMarkers();

		final Image image = new Image(spriteComponent.getSprite());
		Action removeImageAction = new Action() {
			@Override
			public boolean act(float delta) {
				finishKnockbackAnimation((PublicEntity) entity, destination, attacker, image);
				return true;
			}
		};

		Vector2 pixelPos = gridPositionComponent.getWorldPos();
		image.setPosition(pixelPos.x, pixelPos.y);
		image.setOrigin(Align.center);

		ParallelAction moveAndRotate = Actions.parallel(
				Actions.moveTo(destination.destination.getAbsolutePos().x, destination.destination.getAbsolutePos().y, destination.distance * 0.15f),
				Actions.rotateBy(360f, destination.distance * 0.15f)
		);
		
		image.addAction(Actions.sequence(moveAndRotate, removeImageAction));

		GameScreen.fxStage.addActor(image);
	}
	
	
	private void finishKnockbackAnimation(final PublicEntity entity, KnockbackDestinationTile destination, final Entity attacker, final Image image) {
		MovementHandler.placeEntity(entity, destination.destination.getGridPos(), room);

		if (destination.bounceDestination == null) {
			entity.remove(KnockbackComponent.class);
	
			entity.showMarkers();
			Mappers.spriteComponent.get(entity).hide = false;
			
			// Creep
			Optional<Entity> creep = TileUtil.getEntityWithComponentOnTile(destination.destination.getGridPos(), CreepComponent.class, room);
			if (creep.isPresent()) {
				DestructibleComponent destructibleComponent = Mappers.destructibleComponent.get(creep.get());
				if (destructibleComponent == null || !destructibleComponent.isDestroyed()) {
					Mappers.creepComponent.get(creep.get()).onWalk(entity, creep.get(), room);
				}
			}
		
			Journal.addEntry(Journal.getLabel(entity) + " was knocked back");
		} else {
			
			// Bounce
			KnockbackDestinationTile bounceDestination = destination.createBounceDestination();
			createThrowbackImage(entity, bounceDestination, attacker);
			
			Mappers.healthComponent.get(entity).hit(5, entity, attacker);
			
			Entity bounceEntity = destination.bounceEntity;
			if (Mappers.healthComponent.has(bounceEntity)) {
				Mappers.healthComponent.get(bounceEntity).hit(5, bounceEntity, attacker);
			}


			
		}

		image.remove();
		room.unpauseState();
	}
	
	
	
	
	private KnockbackDestinationTile getDestinationTile(Entity entity, DirectionEnum direction, int nbTiles, Room room) {
		Tile entityTile = TileUtil.getTileFromEntity(entity, room);
		KnockbackDestinationTile result = getNextTile(entityTile, direction, nbTiles, 1, room);
		return result;
	}

	private KnockbackDestinationTile getNextTile(Tile entityTile, DirectionEnum direction, int nbTiles, int currentNbTiles, Room room) {		
		KnockbackDestinationTile result = new KnockbackDestinationTile();
		
		Vector2 entityTilePos = entityTile.getGridPos();
		
		switch(direction) {
		case UP:
			if (entityTilePos.y + 1 < GameScreen.GRID_H) {
				result.destination = room.grid[(int) entityTilePos.x][(int) (entityTilePos.y + 1)];
			}
			break;
		case UP_LEFT:
			if (entityTilePos.x - 1 >= 0 && entityTilePos.y + 1 < GameScreen.GRID_H) {
				result.destination = room.grid[(int) entityTilePos.x - 1][(int) (entityTilePos.y + 1)];
			}
			break;
		case UP_RIGHT:
			if (entityTilePos.x + 1 < GameScreen.GRID_W && entityTilePos.y + 1 < GameScreen.GRID_H) {
				result.destination = room.grid[(int) entityTilePos.x + 1][(int) (entityTilePos.y + 1)];
			}
			break;
			
		case LEFT:
			if (entityTilePos.x - 1 >= 0) {
				result.destination = room.grid[(int) entityTilePos.x - 1][(int) entityTilePos.y];
			}
			break;
		case RIGHT:
			if (entityTilePos.x + 1 < GameScreen.GRID_W) {
				result.destination = room.grid[(int) entityTilePos.x + 1][(int) entityTilePos.y];
			}
			break;
			
		case DOWN:
			if (entityTilePos.y - 1 >= 0) {
				result.destination = room.grid[(int) entityTilePos.x][(int) (entityTilePos.y - 1)];
			}
			break;
		case DOWN_LEFT:
			if (entityTilePos.x - 1 >= 0 && entityTilePos.y - 1 >= 0) {
				result.destination = room.grid[(int) entityTilePos.x - 1][(int) (entityTilePos.y - 1)];
			}
			break;
		case DOWN_RIGHT:
			if (entityTilePos.x + 1 < GameScreen.GRID_W && entityTilePos.y - 1 >= 0) {
				result.destination = room.grid[(int) entityTilePos.x + 1][(int) (entityTilePos.y - 1)];
			}
			break;
		}
		
		
		if (result.destination != null) {
			Optional<Entity> solid = TileUtil.getEntityWithComponentOnTile(result.destination.getGridPos(), SolidComponent.class, room);
			if (solid.isPresent()) {
				result.bounceEntity = solid.get();
				result.bounceDestination = entityTile;
			} else {
				if (currentNbTiles < nbTiles) {
					KnockbackDestinationTile nextTile = getNextTile(result.destination, direction, nbTiles, currentNbTiles + 1, room);
					if (!nextTile.noKnockback) {
						return nextTile;
					}
				}
			}
		} else {
			result.noKnockback = true;
		}
		
		result.distance = currentNbTiles;
		return result;
	}


	private class KnockbackDestinationTile {
		public Tile destination;
		public int distance;

		public Tile bounceDestination;
		public Entity bounceEntity;
		
		public boolean noKnockback;
		
		public KnockbackDestinationTile createBounceDestination() {
			KnockbackDestinationTile bounceDestination = new KnockbackDestinationTile();
			bounceDestination.destination = this.bounceDestination;
			bounceDestination.distance = 1;
			return bounceDestination;
		}
	}
}
