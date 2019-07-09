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

package com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.ces.components.SpeakerComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.neutrals.SoulbenderComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.WalletComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.InventoryComponent.InventoryActionEnum;
import com.dokkaebistudio.tacticaljourney.ces.systems.NamedSystem;
import com.dokkaebistudio.tacticaljourney.dialog.pnjs.SoulbenderDialogs;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class SoulbenderSystem extends NamedSystem {	
	
	private Entity player;
	private InventoryComponent playerInventoryCompo;
	
	public SoulbenderSystem(Entity player, Room r) {
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
		
		// Infuse item
		if (playerInventoryCompo.getCurrentAction() == InventoryActionEnum.INFUSE) {
			
			Entity soulbender = playerInventoryCompo.getSoulbender();
			SoulbenderComponent soulbenderComponent = Mappers.soulbenderComponent.get(soulbender);
			
			final Entity currentItem = playerInventoryCompo.getCurrentItem();
			final ItemComponent itemComponent = Mappers.itemComponent.get(currentItem);

			WalletComponent walletComponent = Mappers.walletComponent.get(player);			
			if (walletComponent.hasEnoughMoney(soulbenderComponent.getPrice())) {
				// Pay
				walletComponent.use(soulbenderComponent.getPrice());
				
				Journal.addEntry("[PINK]Infused the " + itemComponent.getItemLabel());
				itemComponent.infuse(player, currentItem, room);
				soulbenderComponent.setHasInfused(true);
				room.turnManager.endPlayerTurn();
				
				playerInventoryCompo.setCurrentAction(null);
			} else {
				
				SpeakerComponent speakerComponent = Mappers.speakerComponent.get(soulbender);
				room.setRequestedDialog(speakerComponent.getSpeech(soulbender, SoulbenderDialogs.NOT_ENOUGH_MONEY_TAG));
				playerInventoryCompo.setCurrentAction(null);
			}

		}

	}

}
