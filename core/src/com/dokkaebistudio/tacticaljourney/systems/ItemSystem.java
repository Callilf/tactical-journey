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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.components.attack.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.display.TextComponent;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent.InventoryActionEnum;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.constants.ZIndexConstants;
import com.dokkaebistudio.tacticaljourney.factory.EntityFlagEnum;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.room.RoomVisitedState;
import com.dokkaebistudio.tacticaljourney.singletons.InputSingleton;
import com.dokkaebistudio.tacticaljourney.util.LootUtil;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class ItemSystem extends EntitySystem implements RoomSystem {	
	
	private Stage fxStage;
	private Room room;
	private Entity player;
	private MoveComponent playerMoveCompo;
	private InventoryComponent playerInventoryCompo;
	
	public ItemSystem(Entity player, Room r, Stage stage) {
		this.priority = 12;

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
		
		if (playerMoveCompo == null) {
			playerMoveCompo = Mappers.moveComponent.get(player);
		}
		if (playerInventoryCompo == null) {
			playerInventoryCompo = Mappers.inventoryComponent.get(player);
		}
		
		// Item instant pickup
		if (room.getState() == RoomState.PLAYER_MOVING && playerMoveCompo.arrivedOnTile) {
			
			// Items pickup
			List<Entity> items = TileUtil.getItemEntityOnTile(playerMoveCompo.currentMoveDestinationTilePos, room);
			for (Entity item : items) {
				ItemComponent itemComponent = Mappers.itemComponent.get(item);
				if (itemComponent != null && itemComponent.getItemType().isInstantPickUp()) {
					//Pick up this consumable
					itemComponent.pickUp(player, item, room);
					
					// Pickup animation
					List<Image> pickupAnimationImages = itemComponent.getPickupAnimationImage(item);
					for (Image i : pickupAnimationImages) {
						fxStage.addActor(i);
					}

					room.removeEntity(item);
				}
			}

			
		} else if (room.getState() == RoomState.PLAYER_END_MOVEMENT) {
			
			// The player just arrived on a tile, check if there is an item
			checkItemPresenceToDisplayPopin();
			
		} else if (playerInventoryCompo.getCurrentAction() == null && room.getState().canEndTurn()) {
			
			// If the user click on the player and there is an item on this tile, display the popin
			if (InputSingleton.getInstance().leftClickJustReleased) {
				Vector3 touchPoint = InputSingleton.getInstance().getTouchPoint();
				int x = (int) touchPoint.x;
				int y = (int) touchPoint.y;
				
				if (TileUtil.isPixelPosOnEntity(x, y, player)) {
					// Touched the player, if there is an item on this tile, display the popin
					checkItemPresenceToDisplayPopin();
				}
				
			}
			
		} else if (playerInventoryCompo.getCurrentAction() != null && playerInventoryCompo.getCurrentAction() != InventoryActionEnum.DISPLAY_POPIN) {
			
			final Entity currentItem = playerInventoryCompo.getCurrentItem();
			final ItemComponent itemComponent = Mappers.itemComponent.get(currentItem);
			
			
			
			
			// An action has been done in the inventory
			switch(playerInventoryCompo.getCurrentAction()) {
			
			case PICKUP:
				
				// USE ITEM
				boolean pickedUp = itemComponent.pickUp(player, currentItem, room);
				
				if (pickedUp || (itemComponent.getQuantityPickedUp() != null && itemComponent.getQuantityPickedUp() > 0)) {

					// Pickup animation
					List<Image> pickupAnimationImages = itemComponent.getPickupAnimationImage(currentItem);
					for (Image i : pickupAnimationImages) {
						fxStage.addActor(i);
					}

					room.turnManager.endPlayerTurn();
				}				
				playerInventoryCompo.setCurrentAction(null);
				
				
				break;
				
			case PICKUP_AND_USE:
				
				// USE ITEM
				boolean instaUsed = itemComponent.use(player, currentItem, room);
				
				if (instaUsed) {
					room.removeEntity(currentItem);
					room.turnManager.endPlayerTurn();
				}				
				playerInventoryCompo.setCurrentAction(null);
				
				
				break;
			case USE:
				
				// USE ITEM
				boolean used = itemComponent.use(player, currentItem, room);
				
				if (used) {
					playerInventoryCompo.remove(currentItem);
					room.removeEntity(currentItem);
					room.turnManager.endPlayerTurn();
				}
				
				playerInventoryCompo.setCurrentAction(null);
				
				
				break;
			case DROP:
				Journal.addEntry("Dropped a " + itemComponent.getItemLabel());

				// Drop animation
				Action finishDropAction = new Action(){
					  @Override
					  public boolean act(float delta){
							itemComponent.drop(player, currentItem, room);

//							playerIventoryCompo.remove(currentItem);
							room.turnManager.endPlayerTurn();
							
					    return true;
					  }
				};
				Image dropImage = itemComponent.getDropAnimationImage(player, currentItem, finishDropAction);
				fxStage.addActor(dropImage);
				room.setNextState(RoomState.ITEM_DROP_ANIM);
				
								
				playerInventoryCompo.setCurrentAction(null);
				
				break;
				
			case THROW:
				PlayerComponent playerComponent = Mappers.playerComponent.get(player);
				playerComponent.setActiveSkill(playerComponent.getSkillThrow());
				
				Entity skillThrow = playerComponent.getSkillThrow();
				AttackComponent attackComponent = Mappers.attackComponent.get(skillThrow);
				attackComponent.setThrownEntity(currentItem);
				
				room.setNextState(RoomState.PLAYER_TARGETING_START);
				
				playerInventoryCompo.setCurrentAction(null);
				break;
				
				default :
			
			}

		}		
	
		
		// Fill the content of lootables
		if (room.getVisited() == RoomVisitedState.FIRST_ENTRANCE) {
			List<Entity> lootableEntities = new ArrayList<>();
			LootUtil.findLootables(room, lootableEntities);
			for (Entity l : lootableEntities) {
				LootUtil.fillLootable(Mappers.lootableComponent.get(l), room.entityFactory);
			}
		}
		

	
	}


	/**
	 * Check whether there is an item at the location of the player to display the item popin.
	 */
	private void checkItemPresenceToDisplayPopin() {
		PlayerComponent playerComponent = Mappers.playerComponent.get(player);
		if (playerComponent.isActionDoneAtThisFrame()) return;
		
		// Item popin
		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(player);
		List<Entity> itemEntityOnTile = TileUtil.getItemEntityOnTile(gridPositionComponent.coord(), room);
		if (!itemEntityOnTile.isEmpty()) {
			for (Entity item : itemEntityOnTile) {
				playerComponent.setActionDoneAtThisFrame(true);
				ItemComponent itemComponent = Mappers.itemComponent.get(item);

				if (room.hasEnemies()) {
					// Open the popin for the first item that is not a "instant pickup" item
					if (!itemComponent.getItemType().isInstantPickUp()) {
						playerInventoryCompo.requestAction(InventoryActionEnum.DISPLAY_POPIN, item);
					}
				} else {
					// Auto pickup in cleared rooms
					if (itemComponent.getPrice() == null && playerInventoryCompo.canStore(itemComponent)) {
						playerInventoryCompo.requestAction(InventoryActionEnum.PICKUP, item);
					} else {
						playerInventoryCompo.requestAction(InventoryActionEnum.DISPLAY_POPIN, item);
					}
				}
				break;
			}
		}
	}


}
