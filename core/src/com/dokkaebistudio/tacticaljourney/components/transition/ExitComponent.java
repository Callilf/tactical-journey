package com.dokkaebistudio.tacticaljourney.components.transition;

import com.badlogic.ashley.core.Component;
import com.dokkaebistudio.tacticaljourney.room.Floor;

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
