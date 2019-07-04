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
import com.dokkaebistudio.tacticaljourney.components.AIComponent;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomCreatureState;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.statuses.Status;
import com.dokkaebistudio.tacticaljourney.systems.creatures.CreatureSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;
import com.dokkaebistudio.tacticaljourney.util.actions.ActionMoveCircular;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * The entity is stuck on the current tile.
 * @author Callil
 *
 */
public class StatusDebuffStunned extends Status {
	
	private ActionMoveCircular actionCircle;

	public StatusDebuffStunned() {}
	
	public StatusDebuffStunned(int duration) {
		this.setDuration(duration);
	}
	
	
	@Override
	public String title() {
		return "[YELLOW]Stunned[]";
	}

	@Override
	public String description() {
		return "Cannot either move nor attack. When attacking stunned enemies, accuracy is increased by 2";
	}
	
	@Override
	public RegionDescriptor texture() {
		return Assets.status_stunned;
	}
	@Override
	public RegionDescriptor fullTexture() {
		return Assets.status_stunned_full;
	}
	
	@Override
	public boolean onReceive(Entity entity, Room room) {
		
		boolean isPlayer = Mappers.playerComponent.has(entity);
		if (isPlayer && room.getState().isPlayerTurn()) {
			room.setNextState(RoomState.PLAYER_STUNNED);
//			room.turnManager.endPlayerTurn();
		}
			
		boolean isCreature = Mappers.allyComponent.has(entity) || Mappers.enemyComponent.has(entity);
		if (isCreature && (room.getState().isAllyTurn() || room.getState().isEnemyTurn()) && CreatureSystem.creatureCurrentyPlaying == entity) {
			// Finish this creature turn
			room.setCreatureState(RoomCreatureState.NONE);
		}	
		
		PoolableVector2 animPos = TileUtil.convertGridPosIntoPixelPos(Mappers.gridPositionComponent.get(entity).coord());
		createStunnedAnimation(animPos);
		
		return true;
	}

	
	@Override
	public void onStartTurn(Entity entity, Room room) {
		boolean isPlayer = Mappers.playerComponent.has(entity);
		if (isPlayer) {
			Journal.addEntry("You are [YELLOW]stunned[] and cannot play this turn.");
			room.setNextState(RoomState.PLAYER_STUNNED);
		}
			
		AIComponent aiCompo = Mappers.aiComponent.get(entity);
		if (aiCompo != null) {
			Journal.addEntry(Mappers.inspectableComponent.get(entity).getTitle() + "is [YELLOW]stunned[] and cannot play this turn.");
			aiCompo.setTurnOver(true);
		}
	}
	
	@Override
	public void onRemove(Entity entity, Room room) {
		animation.remove();
	}
	
	@Override
	public void onDeath(Entity entity, Room room) {
		animation.remove();
	}
	
	
	private void createStunnedAnimation(PoolableVector2 animPos) {
		animation = new Image(Assets.stunned_animation.getRegion());
		animation.setPosition(animPos.x + 10, animPos.y);
	
		animation.setOrigin(Align.center);
		animation.addAction(Actions.alpha(0.7f));
		animation.addAction(Actions.scaleTo(0.5f, 0.5f));
		
		actionCircle = ActionMoveCircular.actionEllipse(animPos.x, animPos.y + GameScreen.GRID_SIZE/2, 
				GameScreen.GRID_SIZE/2, GameScreen.GRID_SIZE/4, 1, false, 2f);

		SequenceAction scale = Actions.sequence(Actions.scaleTo(0.25f, 0.25f, 1f), Actions.scaleTo(0.5f, 0.5f, 1f));
		
		animation.addAction(Actions.forever(Actions.parallel(actionCircle, scale)));
		animPos.free();

		GameScreen.fxStage.addActor(animation);
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
	
	
	
	public static Serializer<StatusDebuffStunned> getStatusDebuffStunnedSerializer(final PooledEngine engine) {
		return new Serializer<StatusDebuffStunned>() {

			@Override
			public void write(Kryo kryo, Output output, StatusDebuffStunned object) {
				output.writeInt(object.getDuration());
				output.writeFloat(object.actionCircle.getPosition().x);
				output.writeFloat(object.actionCircle.getPosition().y - GameScreen.GRID_SIZE/2);
			}

			@Override
			public StatusDebuffStunned read(Kryo kryo, Input input, Class<? extends StatusDebuffStunned> type) {
				StatusDebuffStunned statusDebuffDeathDoor = new StatusDebuffStunned(input.readInt());
				
				PoolableVector2 pos = PoolableVector2.create(input.readFloat(), input.readFloat());
				statusDebuffDeathDoor.createStunnedAnimation(pos);

				return statusDebuffDeathDoor;
			}
		
		};
	}
	
}
