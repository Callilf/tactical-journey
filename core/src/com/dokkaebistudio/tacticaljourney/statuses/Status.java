/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.statuses;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.room.Room;

/**
 * A status on an entity that provides a temporary buff or a debuff.
 * @author Callil
 *
 */
public abstract class Status {
	
	/** The number of turns this status will last. */
	private Integer duration;
	
	public abstract String title();
	public abstract String description();
	public abstract RegionDescriptor texture();
	public abstract RegionDescriptor fullTexture();

	/** Called when this status is received by an entity. */
	public boolean onReceive(Entity entity, Room room) {
		return true;
	}
	
	/** Called when this status is removed from an entity. */
	public void onRemove(Entity entity, Room room) {}

	/** Called when the entity starts its turn. */
	public void onStartTurn(Entity entity, Room room) {}
	
	/** Called when the entity ends its turn. */
	public void onEndTurn(Entity entity, Room room) {}
	
	
	public void onReceiveDamage(Entity entity, Entity attacker, Room room) {};
	public void onDeath(Entity entity, Room room) {};
	
	
	public String getDurationString() {
		if (duration != null) {
			return String.valueOf(duration);
		} else {
			return "?";
		}
	}
	
	
	/**
	 * Called when an entity already has a status of this type and receive
	 * the same type again.
	 * @param addedStatus the status the was just received
	 */
	public void addUp(Status addedStatus) {
		if (this.duration != null && addedStatus.getDuration() != null) {
			this.setDuration(this.duration + addedStatus.getDuration());
		}
	}
	
	
	//************************
	// getters and setters
	
	public Integer getDuration() {
		return duration;
	}
	
	public void setDuration(Integer duration) {
		this.duration = duration;
	};

	
}
