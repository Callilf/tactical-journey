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

import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.InputSingleton;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.display.TextComponent;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent.InventoryActionEnum;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.constants.ZIndexConstants;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
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

					room.getRemovedItems().add(item);
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

					if (itemComponent.getQuantity() == null || itemComponent.getQuantity() == 0) {
						room.getRemovedItems().add(currentItem);
					}
					room.turnManager.endPlayerTurn();
				}				
				playerInventoryCompo.setCurrentAction(null);
				
				
				break;
				
			case PICKUP_AND_USE:
				
				// USE ITEM
				boolean instaUsed = itemComponent.use(player, currentItem, room);
				
				if (instaUsed) {
					room.getRemovedItems().add(currentItem);
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
					room.getRemovedItems().add(currentItem);
					room.removeEntity(currentItem);
					room.turnManager.endPlayerTurn();
				} else {
					Journal.addEntry("[RED]Impossible to use the " + itemComponent.getItemLabel());

				}
				
				playerInventoryCompo.setCurrentAction(null);
				
				
				break;
			case DROP:
				Journal.addEntry("Dropped a " + itemComponent.getItemLabel());

				itemComponent.drop(player, currentItem, room);

				// Drop animation
				Action finishDropAction = new Action(){
					  @Override
					  public boolean act(float delta){
//							playerIventoryCompo.remove(currentItem);
							room.getAddedItems().add(currentItem);
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
	
		
		
		
		
	
		// Display items quantities
		for (Entity item : room.getAddedItems()) {
			ItemComponent itemComponent = Mappers.itemComponent.get(item);
			// Display quantity or price
			createValueAndPriceDisplayers(item, itemComponent);
		}
		room.getAddedItems().clear();
	
		for (Entity item : room.getRemovedItems()) {
			ItemComponent itemComponent = Mappers.itemComponent.get(item);
			if (itemComponent.getQuantityDisplayer() != null) {
				room.removeEntity(itemComponent.getQuantityDisplayer());
				itemComponent.setQuantityDisplayer(null);
			}
			if (itemComponent.getPriceDisplayer() != null) {
				room.removeEntity(itemComponent.getPriceDisplayer());
				itemComponent.setPriceDisplayer(null);
			}
		}
		room.getRemovedItems().clear();
	
	}


	/**
	 * Create the displayers for quantity and price on items on the floor.
	 * @param item the item
	 * @param itemComponent the item component
	 */
	private void createValueAndPriceDisplayers(Entity item, ItemComponent itemComponent) {
		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(item);
		Vector2 pixelPos = TileUtil.convertGridPosIntoPixelPos(gridPositionComponent.coord());

		if (itemComponent.getQuantity() != null) {
			Entity quantityDisplayer = this.room.entityFactory.createText(new Vector3(pixelPos, ZIndexConstants.ITEM),
					String.valueOf(itemComponent.getQuantity()), room);
			TextComponent textComponent = Mappers.textComponent.get(quantityDisplayer);
			GridPositionComponent displayerPosCompo = Mappers.gridPositionComponent.get(quantityDisplayer);
			displayerPosCompo.absolutePos(displayerPosCompo.getAbsolutePos().x + 10, displayerPosCompo.getAbsolutePos().y + 10 + textComponent.getHeight());
			itemComponent.setQuantityDisplayer(quantityDisplayer);
		}
		
		if (itemComponent.getPrice() != null) {
			Entity priceDisplayer = this.room.entityFactory.createText(new Vector3(pixelPos, ZIndexConstants.ITEM),
					String.valueOf(itemComponent.getPrice()), room);
			TextComponent textComponent = Mappers.textComponent.get(priceDisplayer);
			GridPositionComponent displayerPosCompo = Mappers.gridPositionComponent.get(priceDisplayer);
			displayerPosCompo.absolutePos(displayerPosCompo.getAbsolutePos().x + GameScreen.GRID_SIZE/2 - textComponent.getWidth()/2,
					displayerPosCompo.getAbsolutePos().y + GameScreen.GRID_SIZE);
			
			textComponent.setText("[GOLD]" + textComponent.getText());
			itemComponent.setPriceDisplayer(priceDisplayer);
		}
	}


	/**
	 * Check whether there is an item at the location of the player to display the item popin.
	 */
	private void checkItemPresenceToDisplayPopin() {
		// Item popin
		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(player);
		List<Entity> itemEntityOnTile = TileUtil.getItemEntityOnTile(gridPositionComponent.coord(), room);
		if (!itemEntityOnTile.isEmpty()) {
			for (Entity item : itemEntityOnTile) {
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
