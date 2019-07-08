/*******************************************************************************
 * Copyright 2014 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.dokkaebistudio.tacticaljourney.systems.entitysystems;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.components.SpeakerComponent;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.components.neutrals.ShopKeeperComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent.InventoryActionEnum;
import com.dokkaebistudio.tacticaljourney.components.player.WalletComponent;
import com.dokkaebistudio.tacticaljourney.dialog.pnjs.ShopkeeperDialogs;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomVisitedState;
import com.dokkaebistudio.tacticaljourney.systems.NamedSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.ShopUtil;

public class ShopSystem extends NamedSystem {	
	private Entity player;
	private InventoryComponent playerInventoryCompo;
	
	private List<Entity> shopKeepers = new ArrayList<>();
	
	public ShopSystem(Entity player, Room r) {
		this.priority = 13;
		this.player = player;
		this.room = r;
	}

    
	@Override
	public void performUpdate(float deltaTime) {
		
		if (playerInventoryCompo == null) {
			playerInventoryCompo = Mappers.inventoryComponent.get(player);
		}
		
		if (!room.getState().isPlayerTurn()) {
			return;
		}

		fillShopKeepers();

		// Shop restock
		for (Entity shopKeeper : shopKeepers) {
			ShopKeeperComponent shopKeeperComponent = Mappers.shopKeeperComponent.get(shopKeeper);
			
			// First stock
			if (room.getVisited() == RoomVisitedState.FIRST_ENTRANCE) {
				shopKeeperComponent.setNumberOfItems(ShopUtil.getNumberOfItemsInShop(shopKeeper, room));
				shopKeeperComponent.stock( room);
			}
			
			// Restock
			if (shopKeeperComponent.isRequestRestock()) {
				WalletComponent walletComponent = Mappers.walletComponent.get(player);
				
				if (walletComponent.hasEnoughMoney(shopKeeperComponent.getRestockPrice())) {
					walletComponent.use(shopKeeperComponent.getRestockPrice());
					shopKeeperComponent.restock(room);
				} else {
					SpeakerComponent speakerComponent = Mappers.speakerComponent.get(shopKeeper);
					room.setRequestedDialog(speakerComponent.getSpeech(shopKeeper, ShopkeeperDialogs.NOT_ENOUGH_MONEY_TAG));
				}
				
				shopKeeperComponent.setRequestRestock(false);
			}
		}
		
		
		// Buy item
		if (playerInventoryCompo.getCurrentAction() == InventoryActionEnum.BUY) {
			final Entity currentItem = playerInventoryCompo.getCurrentItem();
			final ItemComponent itemComponent = Mappers.itemComponent.get(currentItem);

			WalletComponent walletComponent = Mappers.walletComponent.get(player);
			
			// Find the correct shop keeper
			Entity shopKeeper = null;
			ShopKeeperComponent shopKeeperComponent = null;
			for (Entity sk : shopKeepers) {
				ShopKeeperComponent currentShopKeeperComponent = Mappers.shopKeeperComponent.get(sk);
				if (currentShopKeeperComponent.containItem(currentItem)) {
					shopKeeper = sk;
					shopKeeperComponent = currentShopKeeperComponent;
					break;
				}
			}
			
			if (walletComponent.hasEnoughMoney(itemComponent.getPrice())) {
				// Pay
				walletComponent.use(itemComponent.getPrice());
				// Remove the price
				itemComponent.setPrice(null);
				// Remove the item from the shop keeper's inventory
				shopKeeperComponent.removeItem(currentItem);
				// Launch the pickup animation
				playerInventoryCompo.requestAction(InventoryActionEnum.PICKUP, currentItem);
				
				SpeakerComponent speakerComponent = Mappers.speakerComponent.get(shopKeeper);
				room.setRequestedDialog(speakerComponent.getSpeech(shopKeeper, ShopkeeperDialogs.SOLD_TAG));
			} else {
				
				SpeakerComponent speakerComponent = Mappers.speakerComponent.get(shopKeeper);
				room.setRequestedDialog(speakerComponent.getSpeech(shopKeeper, ShopkeeperDialogs.NOT_ENOUGH_MONEY_TAG));
				playerInventoryCompo.setCurrentAction(null);
			}
			
		}
	}



	/**
	 * Fills the list of shop keepers of the room. Empty list if no shop keepers.
	 * @param room the room
	 */
	private void fillShopKeepers() {
		shopKeepers.clear();
		ShopUtil.findShopKeeper(room, shopKeepers);
	}

}
