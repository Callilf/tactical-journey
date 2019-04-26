/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.statuses.debuffs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.InspectableComponent;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.statuses.Status;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;
import com.dokkaebistudio.tacticaljourney.util.actions.ActionMoveCircular;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * The entity dies in one shot.
 * @author Callil
 *
 */
public class StatusDebuffDeathDoor extends Status {
	
	private ActionMoveCircular actionCircle;
	
	public StatusDebuffDeathDoor() {}
	
	public StatusDebuffDeathDoor(int duration) {
		this.setDuration(duration);
	}
	
	
	@Override
	public String title() {
		return "[BLACK]At death's door[]";
	}
	
	@Override
	public String description() {
		return "Die in one hit, no matter how much damage that hit does.";
	}

	@Override
	public RegionDescriptor texture() {
		return Assets.status_death_door;
	}
	@Override
	public RegionDescriptor fullTexture() {
		return Assets.status_death_door_full;
	}
	
	
	@Override
	public boolean onReceive(Entity entity, Room room) {
		// TODO : handle immunity
		
		PoolableVector2 animPos = TileUtil.convertGridPosIntoPixelPos(Mappers.gridPositionComponent.get(entity).coord());
		createSkullAnimation(animPos);
		
		return true;
	}

	private void createSkullAnimation(PoolableVector2 animPos) {
		animation = new Image(Assets.death_door_animation.getRegion());
		animation.setPosition(animPos.x + 10, animPos.y);
	
		animation.setOrigin(Align.center);
		animation.addAction(Actions.alpha(0.7f));
		animation.addAction(Actions.scaleTo(0.5f, 0.5f));
		
		actionCircle = ActionMoveCircular.actionEllipse(animPos.x, animPos.y + GameScreen.GRID_SIZE/2, 
				GameScreen.GRID_SIZE/2, GameScreen.GRID_SIZE/4, 1, false, 3f);

		SequenceAction scale = Actions.sequence(Actions.scaleTo(0.25f, 0.25f, 1.5f), Actions.scaleTo(0.5f, 0.5f, 1.5f));
		
		animation.addAction(Actions.forever(Actions.parallel(actionCircle, scale)));
		animPos.free();

		GameScreen.fxStage.addActor(animation);
	}
	
	@Override
	public void onReceiveDamage(Entity entity, Entity attacker, Room room) {
		
		InspectableComponent inspectableComponent = Mappers.inspectableComponent.get(entity);
		Journal.addEntry(inspectableComponent.getTitle() + " received damage at [BLACK]death's door[]");
		
		HealthComponent healthComponent = Mappers.healthComponent.get(entity);
		healthComponent.setHp(0);
	}
	
	
	

	@Override
	public void onRemove(Entity entity, Room room) {
		animation.remove();
	}
	
	@Override
	public void onDeath(Entity entity, Room room) {
		animation.remove();
	}
	
	
	//********************
	// Movements
	
	public void performMovement(float xOffset, float yOffset) {
		if (animation != null) {
			animation.setPosition(animation.getX() + xOffset, animation.getY() + yOffset);
			actionCircle.setPosition(actionCircle.getPosition().x + xOffset, actionCircle.getPosition().y + yOffset);
		}
	}
	
	public void endMovement(Vector2 finalPos) {
		if (animation != null) {
			animation.setPosition(finalPos.x, finalPos.y);
			actionCircle.setPosition(finalPos.x, finalPos.y + GameScreen.GRID_SIZE/2);
		}
	}
	
	public void place(Vector2 tilePos) {
		if (animation != null) {
			animation.setPosition(tilePos.x, tilePos.y);
			actionCircle.setPosition(tilePos.x, tilePos.y + GameScreen.GRID_SIZE/2);
		}
	}
	
	
	
	
	public static Serializer<StatusDebuffDeathDoor> getStatusDebuffDeathDoorSerializer(final PooledEngine engine) {
		return new Serializer<StatusDebuffDeathDoor>() {

			@Override
			public void write(Kryo kryo, Output output, StatusDebuffDeathDoor object) {
				output.writeInt(object.getDuration());
				output.writeFloat(object.actionCircle.getPosition().x);
				output.writeFloat(object.actionCircle.getPosition().y - GameScreen.GRID_SIZE/2);
			}

			@Override
			public StatusDebuffDeathDoor read(Kryo kryo, Input input, Class<StatusDebuffDeathDoor> type) {
				StatusDebuffDeathDoor statusDebuffDeathDoor = new StatusDebuffDeathDoor(input.readInt());
				
				PoolableVector2 pos = PoolableVector2.create(input.readFloat(), input.readFloat());
				statusDebuffDeathDoor.createSkullAnimation(pos);

				return statusDebuffDeathDoor;
			}
		
		};
	}
	
	
}
