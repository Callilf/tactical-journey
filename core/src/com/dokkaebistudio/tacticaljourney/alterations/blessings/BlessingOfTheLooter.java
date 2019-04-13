/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.blessings;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.systems.AlterationSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Blessing of the looter. 4 additional inventory slots.
 * @author Callil
 *
 */
public class BlessingOfTheLooter extends Blessing {
	
	private int nbSlotsToAdd = 4;
	
	@Override
	public String title() {
		return "Blessing of the looter";
	}
	
	@Override
	public String description() {
		return "Grants 4 additional inventory slots";
	}
	
	@Override
	public RegionDescriptor texture() {
		return Assets.blessing_looter;
	}
	
	@Override
	public void onReceive(Entity entity) {
		InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(entity);
		for (int i=0 ; i<nbSlotsToAdd ; i++) {
			inventoryComponent.addSlot();
		}
		
		AlterationSystem.addAlterationProc(this);
	}

	@Override
	public void onRemove(Entity entity) {
		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(entity);
		InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(entity);
		for (int i=0 ; i<nbSlotsToAdd ; i++) {
			inventoryComponent.removeSlot(gridPositionComponent.room);
		}
	}

}
