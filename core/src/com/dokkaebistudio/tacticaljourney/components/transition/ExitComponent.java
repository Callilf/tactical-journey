package com.dokkaebistudio.tacticaljourney.components.transition;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.room.Floor;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Represents a transition from a floor to another.
 * @author Callil
 *
 */
public class ExitComponent implements Component {

	/** Whether the door is opened or closed. */
	private boolean opened;
	
	/** The room on the other side of this door. */
	private Floor targetedFloor;

	
	/**
	 * Open the exit door.
	 * @param exit the exit entity
	 */
	public void open(Entity exit) {
		if (!opened) {
			SpriteComponent spriteComponent = Mappers.spriteComponent.get(exit);
			spriteComponent.getSprite().setRegion(Assets.exit_opened);
			
			opened = true;
		}
	}
	
	
	// Getters and Setters
	
	public boolean isOpened() {
		return opened;
	}

	public void setOpened(boolean opened) {
		this.opened = opened;
	}

	public Floor getTargetedFloor() {
		return targetedFloor;
	}

	public void setTargetedFloor(Floor targetedFloor) {
		this.targetedFloor = targetedFloor;
	}


}
