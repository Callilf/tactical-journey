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
import java.util.Optional;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;
import com.dokkaebistudio.tacticaljourney.components.SpeakerComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.components.neutrals.SoulbenderComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent.InventoryActionEnum;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent.PlayerActionEnum;
import com.dokkaebistudio.tacticaljourney.components.player.WalletComponent;
import com.dokkaebistudio.tacticaljourney.dialog.pnjs.SoulbenderDialogs;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemDivineCatalyst;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.singletons.InputSingleton;
import com.dokkaebistudio.tacticaljourney.systems.NamedSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class SoulbenderSystem extends NamedSystem {	
	
	private Entity player;
	private InventoryComponent playerInventoryCompo;
	
	private List<Entity> soulbenders = new ArrayList<>();
	
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
		
		// Handle clicks on a shopkeeper
		if (playerInventoryCompo.getCurrentAction() == null && room.getState().canEndTurn()) {
			
			// If the user click on the shop keeper, either display a dialog or display refill popin
			if (InputSingleton.getInstance().leftClickJustReleased) {
				Vector3 touchPoint = InputSingleton.getInstance().getTouchPoint();
				int x = (int) touchPoint.x;
				int y = (int) touchPoint.y;
				PoolableVector2 tempPos = TileUtil.convertPixelPosIntoGridPos(x, y);
				Optional<Entity> soulbenderOpt = TileUtil.getEntityWithComponentOnTile(tempPos, SoulbenderComponent.class, room);
				
				if (soulbenderOpt.isPresent()) {
					Entity soulbender = soulbenderOpt.get();
					
					SoulbenderComponent soulbenderComponent = Mappers.soulbenderComponent.get(soulbender);
					GridPositionComponent playerPosition = Mappers.gridPositionComponent.get(player);
					
					boolean actionRequested = false;
					int distanceFromSoulbender = TileUtil.getDistanceBetweenTiles(playerPosition.coord(), tempPos);
					if (distanceFromSoulbender == 1 && !soulbenderComponent.hasInfused()) {
						// Popin to ask for infusion
						PlayerComponent playerComponent = Mappers.playerComponent.get(player);
						playerComponent.requestAction(PlayerActionEnum.INFUSE, soulbender);
						actionRequested = true;
						
					} else if(soulbenderComponent.hasInfused()) {
						InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(player);
						if (inventoryComponent.contains(ItemDivineCatalyst.class)) {
							// Player has a divine catalyst
							if (distanceFromSoulbender == 1) {
								PlayerComponent playerComponent = Mappers.playerComponent.get(player);
								playerComponent.requestAction(PlayerActionEnum.GIVE_CATALYST_SOULBENDER, soulbender);
								actionRequested = true;
							}
						} 
					}
					
					if (!actionRequested) {
						SpeakerComponent speakerComponent = Mappers.speakerComponent.get(soulbender);
						room.setRequestedDialog(speakerComponent.getSpeech(soulbender));
					}
				}
				
				tempPos.free();
			}
			
		} 
		

//		fillSoulBenders();


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
				room.setRequestedDialog(speakerComponent.getSpeech(SoulbenderDialogs.NOT_ENOUGH_MONEY_TAG));
				playerInventoryCompo.setCurrentAction(null);
			}

		}

	}



	/**
	 * Fills the list of soul benders of the room. Empty list if no soul benders.
	 * @param room the room
	 */
	private void fillSoulBenders() {
		soulbenders.clear();
		for (Entity e : room.getNeutrals()) {
			if (Mappers.soulbenderComponent.has(e)) {
				soulbenders.add(e);
			}
		}
	}

}
