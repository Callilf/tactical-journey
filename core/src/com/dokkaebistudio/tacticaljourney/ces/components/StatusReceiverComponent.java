package com.dokkaebistudio.tacticaljourney.ces.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.ces.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.interfaces.MarkerInterface;
import com.dokkaebistudio.tacticaljourney.ces.components.interfaces.MovableInterface;
import com.dokkaebistudio.tacticaljourney.ces.components.player.AlterationReceiverComponent;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.rendering.HUDRenderer;
import com.dokkaebistudio.tacticaljourney.rendering.service.PopinService;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.statuses.Status;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Marker to indicate that this entity can receive buffs and debuffs.
 * @author Callil
 *
 */
public class StatusReceiverComponent implements Component, Poolable, MovableInterface, MarkerInterface {
	
	/** The list of statuses currently affecting this entity. */
	private List<Status> statuses = new ArrayList<>();
	
	
	private StatusActionEnum currentAction;
	private Status currentStatus;

	public enum StatusActionEnum {
		RECEIVE_STATUS,
		REMOVE_STATUS;
	}
	
	//********
	// DIsplay

	private Map<Status, Table> iconsMap = new HashMap<>();
	private Table statusTable = new Table();
    
	
	
	
	//*************
	// Animations
//	private Image blessingImage;
//	
//	public Image setBlessingImage(AtlasRegion texture, Vector2 startGridPos) {
//		final Image arrow = new Image(texture);
//		Vector2 playerPixelPos = TileUtil.convertGridPosIntoPixelPos(startGridPos);
//		arrow.setPosition(playerPixelPos.x, playerPixelPos.y + 60);
//				
//		Action removeImageAction = new Action(){
//			  @Override
//			  public boolean act(float delta){
//				  arrow.remove();
//				  return true;
//			  }
//		};
//	
//		arrow.setOrigin(Align.center);
//		
//		ScaleToAction init = Actions.scaleTo(0, 0);
//		ScaleToAction appear = Actions.scaleTo(1, 1, 1f);
//		RotateByAction rotate = Actions.rotateBy(3600, 2f, Interpolation.exp5Out);
//		AlphaAction disappear = Actions.alpha(0, 2f);
//		
//		ParallelAction appearance = Actions.parallel(appear, rotate);
//		ParallelAction disappearance = Actions.parallel(disappear, rotate);
//		arrow.addAction(Actions.sequence(init, appearance, disappearance, removeImageAction));
//			
//		this.blessingImage = arrow;
//		return this.blessingImage;
//	}
	
	@Override
	public void showMarker(Entity e) {
		this.displayStatusTable(GameScreen.fxStage);
	}
	
	@Override
	public void hideMarker() {
		this.hideStatusTable();
	}
	
	
	@Override
	public void reset() {
		for (Status s : statuses) {
			if (s.getAnimation() != null) {
				s.getAnimation().remove();
			}
		}
		statuses.clear();
		iconsMap.clear();
		
		if (statusTable != null) {
			statusTable.clear();
			statusTable.remove();
		}
		
		clearCurrentAction();
	}
	
	public void removeStatusTable() {
		statusTable = null;
	}

	
	public void addStatus(Entity entity, Status status, Room room, Stage fxStage) {
		// If the same status has already been received, increase its duration
		for (Status alreadyReceivedStatus : statuses) {
			if (alreadyReceivedStatus.getClass().equals( status.getClass())) {
				alreadyReceivedStatus.addUp(status);
				// update the duration label
				this.updateDuration(alreadyReceivedStatus, 0);
				return;
			}
		}
		
		boolean canBeReceived = true;
		AlterationReceiverComponent alterationReceiverComponent = Mappers.alterationReceiverComponent.get(entity);
		if (alterationReceiverComponent != null) {
			canBeReceived = alterationReceiverComponent.onReceiveStatusEffect(entity, status, room);
		}
		if (!canBeReceived) return;
		
		canBeReceived = status.onReceive(entity, room);
		if (canBeReceived) {
			statuses.add(status);
			
			// Update the statuses display
			if (statusTable != null) {
				addOneStatusTable(status);
				if (statusTable.getParent() == null) {
					GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(entity);
					Vector2 pos = gridPositionComponent.hasAbsolutePos() ?gridPositionComponent.getAbsolutePos() :  gridPositionComponent.getWorldPos();
					statusTable.setPosition(pos.x, pos.y + GameScreen.GRID_SIZE - 20);
		
					fxStage.addActor(statusTable);
				}
			} else {
				HUDRenderer.needStatusRefresh = true;
			}
		}
	}


	private void addOneStatusTable(Status status) {
		Table oneStatusTable = new Table();
		Image img = new Image(status.texture().getRegion());
		oneStatusTable.add(img);
		oneStatusTable.row();
		Label label = new Label(status.getDurationString(), PopinService.smallTextStyle());
		oneStatusTable.add(label);
		oneStatusTable.pack();
		
		statusTable.add(oneStatusTable);
		statusTable.pack();
		
		iconsMap.put(status, oneStatusTable);
	}
	
	public void displayStatusTable(Stage fxStage) {
		if (statusTable != null && statuses.size() > 0) {
			fxStage.addActor(statusTable);
		}
		
		for (Status status : statuses) {
			if (status.getAnimation() != null) {
				fxStage.addActor(status.getAnimation());
			}
		}
	}
	
	public void hideStatusTable() {
		if (statusTable != null && statuses.size() > 0) {
			statusTable.remove();
		}
		
		for (Status status : statuses) {
			if (status.getAnimation() != null) {
				status.getAnimation().remove();
			}
		}
	}
	
	public void removeStatus(Entity entity, Status status, Room room) {
		InspectableComponent inspectableComponent = Mappers.inspectableComponent.get(entity);
		if (inspectableComponent != null) {
			Journal.addEntry(inspectableComponent.getTitle() + " no longer have the " + status.title() + " status effect");
		}

		status.onRemove(entity, room);
		statuses.remove(status);
		
		AlterationReceiverComponent alterationReceiverComponent = Mappers.alterationReceiverComponent.get(entity);
		if (alterationReceiverComponent != null) {
			alterationReceiverComponent.onRemoveStatusEffect(entity, status, room);
		}

		if (statusTable != null) {
			Table table = iconsMap.get(status);
			table.remove();
			
			statusTable.invalidate();
			statusTable.pack();
			iconsMap.remove(status);
			
			if (statuses.isEmpty()) {
				statusTable.remove();
			}
		} else {
			HUDRenderer.needStatusRefresh = true;
		}
		
	}
	
	public void removeStatus(Entity entity, Class statusClass, Room room) {
		Status statusToRemove = null;
		for(Status status : statuses) {
			if (status.getClass().equals(statusClass)) {
				statusToRemove = status;
				break;
			}
		}
		
		if (statusToRemove != null) {
			removeStatus(entity, statusToRemove, room);
		}
	}
	
	public void updateDuration(Status status, int value) {
		if (status.getDuration() != null) {
			if (status.getDuration() == 0) {
				//TODO //DEBUG remove
				Journal.addEntry("[RED]DEBUG: status duration = 0");
			}
			status.setDuration(status.getDuration() + value);
			
			// Check if the status is over
			if (status.getDuration().intValue() <= 0) {
				this.requestAction(StatusActionEnum.REMOVE_STATUS, status);
				return;
			}
			
			// Update the duration label
			if (statusTable != null) {
				Table oneStatusTable = iconsMap.get(status);
				Label durationLabel = (Label) oneStatusTable.getCells().get(1).getActor();
				durationLabel.setText(status.getDurationString());
			} else {
				HUDRenderer.needStatusRefresh = true;
			}
		}
	}
	
	/**
	 * Whether the entity has the given status.
	 * @param statusClass the status class
	 * @return true if the entity currently has this status
	 */
	public boolean hasStatus(Class statusClass) {
		for(Status status : statuses) {
			if (status.getClass().equals(statusClass)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Return the status with the given class if the entity has this status, null otherwise.
	 * @param statusClass the status class
	 * @return the status or null
	 */
	public Status getStatus(Class statusClass) {
		for(Status status : statuses) {
			if (status.getClass().equals(statusClass)) {
				return status;
			}
		}
		return null;
	}
	
	/**
	 * Whether the entity has the at least one of the given statuses.
	 * @param statusClass the status classes
	 * @return true if the entity currently has one of this statuses
	 */
	public boolean hasAtLeastOneStatus(Class... statusClasses) {
		for (Class statusClass : statusClasses) {
			for(Status status : statuses) {
				if (status.getClass().equals(statusClass)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public void requestAction(StatusActionEnum action, Status status) {
		this.currentAction = action;
		this.currentStatus = status;
	}
	
	public void clearCurrentAction() {
		this.currentAction = null;
		this.currentStatus = null;
	}

	
	
	
	//*************************
	// Movement
	

	@Override
	public void initiateMovement(Vector2 currentPos) {}



	@Override
	public void performMovement(float xOffset, float yOffset) {
		if (statusTable != null) {
			statusTable.setPosition(statusTable.getX() + xOffset, statusTable.getY() + yOffset);
		}
		
		for (Status status : statuses) {
			status.performMovement(xOffset, yOffset);
		}
	}



	@Override
	public void endMovement(Vector2 finalPos) {
		PoolableVector2 newPixelPos = TileUtil.convertGridPosIntoPixelPos(finalPos);
		if (statusTable != null) {
			statusTable.setPosition(newPixelPos.x, newPixelPos.y + GameScreen.GRID_SIZE - 20);
		}
		
		for (Status status : statuses) {
			status.endMovement(newPixelPos);
		}
		newPixelPos.free();
	}

	@Override
	public void place(Vector2 tilePos) {
		PoolableVector2 newPixelPos = TileUtil.convertGridPosIntoPixelPos(tilePos);
		if (statusTable != null) {
			statusTable.setPosition(newPixelPos.x, newPixelPos.y + GameScreen.GRID_SIZE - 20);
		}
		
		for (Status status : statuses) {
			status.place(newPixelPos);
		}
		newPixelPos.free();
	}
	
	
	//***********************
	// Getters and Setters

	public List<Status> getStatuses() {
		return statuses;
	}

	public void setStatuses(List<Status> statuses) {
		this.statuses = statuses;
	}
	
	public StatusActionEnum getCurrentAction() {
		return currentAction;
	}

	public void setCurrentAction(StatusActionEnum currentAction) {
		this.currentAction = currentAction;
	}

	public Status getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(Status currentStatus) {
		this.currentStatus = currentStatus;
	}
	
	
	
	
	
	public static Serializer<StatusReceiverComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<StatusReceiverComponent>() {

			@Override
			public void write(Kryo kryo, Output output, StatusReceiverComponent object) {
				kryo.writeClassAndObject(output, object.statuses);
				output.writeBoolean(object.statusTable == null);
				if (object.statusTable != null) {
					output.writeFloat(object.statusTable.getX());
					output.writeFloat(object.statusTable.getY());
				}
			}

			@Override
			public StatusReceiverComponent read(Kryo kryo, Input input, Class<? extends StatusReceiverComponent> type) {
				StatusReceiverComponent compo = engine.createComponent(StatusReceiverComponent.class);

				List<Status> statusList = (List<Status>) kryo.readClassAndObject(input);
				if(input.readBoolean()) {
					compo.statusTable = null;
				} else {
					compo.statusTable.setPosition(input.readFloat(), input.readFloat());
				}
				
				for (Status status : statusList) {
					compo.statuses.add(status);
					
					// Update the statuses display
					if (compo.statusTable != null) {
						compo.addOneStatusTable(status);
					} else {
						HUDRenderer.needStatusRefresh = true;
					}				
				}
				
				return compo;
			}
		
		};
	}
}
