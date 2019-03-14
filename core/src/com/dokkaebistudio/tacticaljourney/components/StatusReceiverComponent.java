package com.dokkaebistudio.tacticaljourney.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.interfaces.MovableInterface;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.rendering.service.PopinService;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.statuses.Status;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

/**
 * Marker to indicate that this entity can receive buffs and debuffs.
 * @author Callil
 *
 */
public class StatusReceiverComponent implements Component, Poolable, MovableInterface {
	
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
	public void reset() {
		statuses.clear();
		iconsMap.clear();
		statusTable.clear();
		statusTable.remove();
		
		clearCurrentAction();
	}

	
	public void addStatus(Entity entity, Status status, Room room, Stage fxStage) {
		boolean canBeAdded = status.onReceive(entity, room);
		
		if (canBeAdded) {
			// If the same status has already been received, increase its duration
			for (Status alreadyReceivedStatus : statuses) {
				if (alreadyReceivedStatus.getClass().equals( status.getClass())) {
					alreadyReceivedStatus.addUp(status);
					// update the duration label
					this.updateDuration(alreadyReceivedStatus, 0);
					return;
				}
			}
			
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
			}
		}
	}


	private void addOneStatusTable(Status status) {
		Table oneStatusTable = new Table();
		Image img = new Image(status.texture());
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
	}
	
	public void hideStatusTable() {
		if (statusTable != null && statuses.size() > 0) {
			statusTable.remove();
		}
	}
	
	public void removeStatus(Entity entity, Status status, Room room) {
		if (Mappers.playerComponent.has(entity)) {
			Journal.addEntry("You no longer have the " + status.title() + " status effect");
		}

		status.onRemove(entity, room);
		
		if (statusTable != null) {
			Table table = iconsMap.get(status);
			table.remove();
			
			statusTable.invalidate();
			statusTable.pack();
			iconsMap.remove(status);
		}
		
		statuses.remove(status);
		
		if (statuses.isEmpty()) {
			statusTable.remove();
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
				//TODO remove
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
			}
		}
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
		statusTable.setPosition(statusTable.getX() + xOffset, statusTable.getY() + yOffset);
	}



	@Override
	public void endMovement(Vector2 finalPos) {
		PoolableVector2 newPixelPos = TileUtil.convertGridPosIntoPixelPos(finalPos);
		statusTable.setPosition(newPixelPos.x, newPixelPos.y + GameScreen.GRID_SIZE - 20);
		newPixelPos.free();
	}

	@Override
	public void place(Vector2 tilePos) {
		PoolableVector2 newPixelPos = TileUtil.convertGridPosIntoPixelPos(tilePos);
		statusTable.setPosition(newPixelPos.x, newPixelPos.y + GameScreen.GRID_SIZE - 20);
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
	
}
