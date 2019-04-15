/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.curses.basics;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.alterations.Curse;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.systems.AlterationSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Curse of slowness. Reduce the entity's movement speed by 1.
 * @author Callil
 *
 */
public class CurseSlowness extends Curse {

	@Override
	public String title() {
		return "Curse of slowness";
	}
	
	@Override
	public String description() {
		return "Reduce movement by 1";
	}
	
	@Override
	public RegionDescriptor texture() {
		return Assets.curse_slowness;
	}
	
	
	@Override
	public void onReceive(Entity entity) {
		MoveComponent moveCompo = Mappers.moveComponent.get(entity);
		
		if (moveCompo != null) {
			moveCompo.increaseMoveSpeed(-1);
		}
		
		AlterationSystem.addAlterationProc(this);
	}

	@Override
	public void onRemove(Entity entity) {
		MoveComponent moveCompo = Mappers.moveComponent.get(entity);
		
		if (moveCompo != null) {
			moveCompo.increaseMoveSpeed(1);
		}
	}	
}
