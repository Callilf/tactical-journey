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

package com.dokkaebistudio.tacticaljourney.systems;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.components.neutrals.SoulbenderComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.components.player.WalletComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent.InventoryActionEnum;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent.PlayerActionEnum;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemDivineCatalyst;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.singletons.InputSingleton;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class SoulbenderSystem extends EntitySystem implements RoomSystem {	
	
	private Stage fxStage;
	private Room room;
	private Entity player;
	private InventoryComponent playerInventoryCompo;
	
	private List<Entity> soulbenders = new ArrayList<>();
	
	public SoulbenderSystem(Entity player, Room r, Stage stage) {
		this.priority = 13;

		this.fxStage = stage;
		this.player = player;
		this.room = r;
	}
	
	
    @Override
    public void enterRoom(Room newRoom) {
    	this.room = newRoom;	
    }
    
	@Override
	public void update(float deltaTime) {
		
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
				Entity soulbender = TileUtil.getEntityWithComponentOnTile(tempPos, SoulbenderComponent.class, room);
				
				if (soulbender != null) {
					SoulbenderComponent soulbenderComponent = Mappers.soulbenderComponent.get(soulbender);
					GridPositionComponent playerPosition = Mappers.gridPositionComponent.get(player);
					
					int distanceFromSoulbender = TileUtil.getDistanceBetweenTiles(playerPosition.coord(), tempPos);
					if (distanceFromSoulbender == 1 && !soulbenderComponent.hasInfused()) {
						// Popin to ask for infusion
						PlayerComponent playerComponent = Mappers.playerComponent.get(player);
						playerComponent.requestAction(PlayerActionEnum.INFUSE, soulbender);
						
					} else if(soulbenderComponent.hasInfused()) {
						InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(player);
						if (inventoryComponent.contains(ItemDivineCatalyst.class)) {
							// Player has a divine catalyst
							if (distanceFromSoulbender == 1) {
								PlayerComponent playerComponent = Mappers.playerComponent.get(player);
								playerComponent.requestAction(PlayerActionEnum.GIVE_CATALYST_SOULBENDER, soulbender);
							} else {
								// "You carry a powerful item" speech
								room.setRequestedDialog(Descriptions.SOULBENDER_TITLE,soulbenderComponent.getDivineCatalystSpeech(), true);
							}
						} else {
							// No diving catalyst : "i'm tired" speech
							room.setRequestedDialog(Descriptions.SOULBENDER_TITLE,soulbenderComponent.getAfterInfusionSpeech(), true);
						}
					} else if (soulbenderComponent.isReceivedCatalyst()){
						room.setRequestedDialog(Descriptions.SOULBENDER_TITLE,soulbenderComponent.getAfterCatalystSpeech(), true);
					} else {
						room.setRequestedDialog(Descriptions.SOULBENDER_TITLE,soulbenderComponent.getSpeech(), true);
					}
				}
				
				tempPos.free();
			}
			
		} 
		

//		fillSoulBenders();


		// Buy item
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
				
				playerInventoryCompo.setCurrentAction(null);
			} else {
				
				room.setRequestedDialog(Descriptions.SOULBENDER_TITLE,"Come back when you've got enough gold coins.",  true);
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
