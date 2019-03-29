/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.blessings.basics;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Blessing of celerity. Increase the entity's movement speed by 1.
 * @author Callil
 *
 */
public class BlessingCelerity extends Blessing {
	
	@Override
	public String title() {
		return "Blessing of celerity";
	}
	
	@Override
	public String description() {
		return "Increase movement by 1";
	}
	
	@Override
	public RegionDescriptor texture() {
		return Assets.blessing_celerity;
	}

	@Override
	public void onReceive(Entity entity) {
		MoveComponent moveCompo = Mappers.moveComponent.get(entity);
		
		if (moveCompo != null) {
			moveCompo.increaseMoveSpeed(1);
		}
	}

	@Override
	public void onRemove(Entity entity) {
		MoveComponent moveCompo = Mappers.moveComponent.get(entity);
		
		if (moveCompo != null) {
			moveCompo.increaseMoveSpeed(-1);
		}
	}

}
