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
import com.dokkaebistudio.tacticaljourney.ces.components.ThrowbackComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.creep.CreepComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.ces.entity.PublicEntity;
import com.dokkaebistudio.tacticaljourney.ces.systems.NamedIteratingSystem;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.Tile;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.MovementHandler;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class ThrowbackSystem extends NamedIteratingSystem {

	public Stage stage;

	public ThrowbackSystem(Room r, Stage s) {
		super(Family.all(ThrowbackComponent.class).get());
		this.priority = 21;
		room = r;
		this.stage = s;
	}

	@Override
	protected void performProcessEntity(final Entity entity, float deltaTime) {

		ThrowbackComponent throwbackCompo = Mappers.throwbackComponent.get(entity);

		Optional<Tile> targetedTile = TileUtil.getNextTileForDirection(entity, throwbackCompo.getDirection(), throwbackCompo.getNumberOfTiles(),  room);
		if (!targetedTile.isPresent()) {
			// Cancel throwback
		}

		createThrowbackImage(entity, targetedTile.get());


	}

	private void createThrowbackImage(final Entity entity, Tile destination) {
		room.pauseState();

		ThrowbackComponent throwbackCompo = Mappers.throwbackComponent.get(entity);
		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(entity);

		SpriteComponent spriteComponent = Mappers.spriteComponent.get(entity);
		spriteComponent.hide = true;
		((PublicEntity) entity).hideMarkers();

		final Image image = new Image(spriteComponent.getSprite());
		Action removeImageAction = new Action() {
			@Override
			public boolean act(float delta) {

				MovementHandler.placeEntity(entity, destination.getGridPos(), room);
				entity.remove(ThrowbackComponent.class);
				((PublicEntity) entity).showMarkers();
				Mappers.spriteComponent.get(entity).hide = false;


				// Creep
				Optional<Entity> creep = TileUtil.getEntityWithComponentOnTile(destination.getGridPos(), CreepComponent.class, room);
				if (creep.isPresent()) {
					DestructibleComponent destructibleComponent = Mappers.destructibleComponent.get(creep.get());
					if (destructibleComponent == null || !destructibleComponent.isDestroyed()) {
						Mappers.creepComponent.get(creep.get()).onWalk(entity, creep.get(), room);
					}
				}

//			  // Del 30 damages
//			  Mappers.healthComponent.get(entity).hit(30, entity, null);

				Journal.addEntry(Journal.getLabel(entity) + " was pushed back");

				image.remove();
				room.unpauseState();
				return true;
			}
		};

		Vector2 pixelPos = gridPositionComponent.getWorldPos();
		image.setPosition(pixelPos.x, pixelPos.y);
		image.setOrigin(Align.center);

		ParallelAction moveAndRotate = Actions.parallel(
				Actions.moveTo(destination.getAbsolutePos().x, destination.getAbsolutePos().y, throwbackCompo.getNumberOfTiles() * 0.15f),
				Actions.rotateBy(360f, throwbackCompo.getNumberOfTiles() * 0.15f)
		);
		
		image.addAction(Actions.sequence(moveAndRotate, removeImageAction));

		GameScreen.fxStage.addActor(image);
	}

}
