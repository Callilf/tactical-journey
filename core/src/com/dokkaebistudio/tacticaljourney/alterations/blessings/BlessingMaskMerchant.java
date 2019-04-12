/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.blessings;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.components.neutrals.ShopKeeperComponent;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Floor;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.systems.AlterationSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.ShopUtil;

/**
 * Blessing of the black mamba. Chance to poison the enemy.
 * @author Callil
 *
 */
public class BlessingMaskMerchant extends Blessing {


	@Override
	public String title() {
		return "Blessing of the mask merchant";
	}
	
	@Override
	public String description() {
		return "Shopkeepers sell 2 more items. Already discovered shops will have new items only when asked for a restock.";
	}
	
	@Override
	public RegionDescriptor texture() {
		return Assets.blessing_mask_merchant;
	}


	@Override
	public int onShopNumberOfItems(Entity entity, Entity shopkeeper, Room room) {
		Journal.addEntry("Blessing of the Mask Merchant added 2 items in the shop.");
		AlterationSystem.addAlterationProc(this);
		
		return 2;
	}
	
	@Override
	public void onReceive(Entity entity) {
		List<Entity> shopKeepers = new ArrayList<>();
		Floor floor = Mappers.gridPositionComponent.get(entity).room.floor;
		for (Room room : floor.getRooms()) {
			if (room.isVisited()) {
				ShopUtil.findShopKeeper(room, shopKeepers);
			}
		}
		
		for(Entity shopkeeper : shopKeepers) {
			ShopKeeperComponent shopKeeperComponent = Mappers.shopKeeperComponent.get(shopkeeper);
			shopKeeperComponent.increaseNumberOfItems(2);
		}

		if (!shopKeepers.isEmpty()) {
			Journal.addEntry("Blessing of the Mask Merchant affected already visited shops on this floor.");
			AlterationSystem.addAlterationProc(this);
		}
	}
	
	@Override
	public void onRemove(Entity entity) {
		List<Entity> shopKeepers = new ArrayList<>();
		Floor floor = Mappers.gridPositionComponent.get(entity).room.floor;
		for (Room room : floor.getRooms()) {
			if (room.isVisited()) {
				ShopUtil.findShopKeeper(room, shopKeepers);
			}
		}
		
		for (Entity shopkeeper : shopKeepers) {
			ShopKeeperComponent shopKeeperComponent = Mappers.shopKeeperComponent.get(shopkeeper);
			shopKeeperComponent.increaseNumberOfItems(-2);
		}
	}


}
