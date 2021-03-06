/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.statuses.buffs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.ces.components.FlyComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.display.StateComponent;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.enums.StatesEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.statuses.Status;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * The entity can fly.
 * @author Callil
 *
 */
public class StatusBuffFlight extends Status {

	/** The entity that inflicted the poison. */
	private PooledEngine engine;
	
	public StatusBuffFlight() {}
	
	public StatusBuffFlight(int duration, PooledEngine engine) {
		this.setDuration(duration);
		this.engine = engine;
	}
	
	
	@Override
	public String title() {
		return "[CYAN]Flight[]";
	}
	
	@Override
	public String description() {
		return "Can move over chasms and most of the hazards like mud, puddles, fire or spider web.";
	}

	@Override
	public RegionDescriptor texture() {
		return Assets.status_flight;
	}
	@Override
	public RegionDescriptor fullTexture() {
		return Assets.status_flight_full;
	}

	@Override
	public boolean onReceive(Entity entity, Room room) {
		FlyComponent flyCompo = this.engine.createComponent(FlyComponent.class);
		entity.add(flyCompo);
		
		if (Mappers.playerComponent.has(entity)) {
			StateComponent stateComponent = Mappers.stateComponent.get(entity);
			stateComponent.set(StatesEnum.FLY_STANDING);
		}
		return true;
	}

	
	@Override
	public void onRemove(Entity entity, Room room) {
		entity.remove(FlyComponent.class);
		
		if (Mappers.playerComponent.has(entity)) {
			StateComponent stateComponent = Mappers.stateComponent.get(entity);
			stateComponent.set(StatesEnum.STANDING);
		}
	}
	
}
