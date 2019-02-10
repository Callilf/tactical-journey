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
import com.dokkaebistudio.tacticaljourney.components.ShopKeeperComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.display.TextComponent;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent.InventoryActionEnum;
import com.dokkaebistudio.tacticaljourney.components.player.WalletComponent;
import com.dokkaebistudio.tacticaljourney.constants.ZIndexConstants;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class ItemSystem extends EntitySystem implements RoomSystem {	
	
	private Stage fxStage;
	private Room room;
	private Entity player;
	private MoveComponent playerMoveCompo;
	private InventoryComponent playerIventoryCompo;
	
	public ItemSystem(Entity player, Room r, Stage stage) {
		this.priority = 11;

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
		if (playerIventoryCompo == null) {
			playerIventoryCompo = Mappers.inventoryComponent.get(player);
		}
		
		// Item instant pickup
		if (room.getState() == RoomState.PLAYER_MOVING && playerMoveCompo.arrivedOnTile) {
			
			// Items pickup
			List<Entity> items = TileUtil.getItemEntityOnTile(playerMoveCompo.currentMoveDestinationTilePos, room);
			for (Entity item : items) {
				ItemComponent itemComponent = Mappers.itemComponent.get(item);
				if (itemComponent != null && itemComponent.getItemType().isInstantPickUp()) {
					System.out.println("Picked up " + itemComponent.getItemLabel());

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
			
		} else if (playerIventoryCompo.getCurrentAction() == null && room.getState().canEndTurn()) {
			
			// If the user click on the player and there is an item on this tile, display the popin
			if (InputSingleton.getInstance().leftClickJustReleased) {
				Vector3 touchPoint = InputSingleton.getInstance().getTouchPoint();
				int x = (int) touchPoint.x;
				int y = (int) touchPoint.y;
				
				SpriteComponent spriteComponent = Mappers.spriteComponent.get(player);
				if (spriteComponent.containsPoint(x, y)) {
					
					// Touched the player, if there is an item on this tile, display the popin
					checkItemPresenceToDisplayPopin();
				}
				
			}
			
		} else if (playerIventoryCompo.getCurrentAction() != null && playerIventoryCompo.getCurrentAction() != InventoryActionEnum.DISPLAY_POPIN) {
			
			final Entity currentItem = playerIventoryCompo.getCurrentItem();
			final ItemComponent itemComponent = Mappers.itemComponent.get(currentItem);
			
			// An action has been done in the inventory
			switch(playerIventoryCompo.getCurrentAction()) {
			
			case BUY:
				WalletComponent walletComponent = Mappers.walletComponent.get(player);
				Entity shopKeeper = getShopKeeper(room);
				
				if (walletComponent.hasEnoughMoney(itemComponent.getPrice())) {
					walletComponent.use(itemComponent.getPrice());
					room.removeEntity(itemComponent.getPriceDisplayer());
					itemComponent.setPrice(null);
					
					// TEST
					GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(shopKeeper);
					room.entityFactory.createDialogPopin("Good choice !", gridPositionComponent.getWorldPos(), 3f);
				} else {
					
					// TEST
					GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(shopKeeper);
					room.entityFactory.createDialogPopin("Come back when you've got enough gold coins.", gridPositionComponent.getWorldPos(), 3f);
					
					playerIventoryCompo.setCurrentAction(null);
					break;
				}
			
			case PICKUP:
				
				// USE ITEM
				boolean pickedUp = itemComponent.pickUp(player, currentItem, room);
				
				if (pickedUp || (itemComponent.getQuantityPickedUp() != null && itemComponent.getQuantityPickedUp() > 0)) {
					System.out.println("Picked up " + itemComponent.getItemLabel());
					
					// Pickup animation
					List<Image> pickupAnimationImages = itemComponent.getPickupAnimationImage(currentItem);
					for (Image i : pickupAnimationImages) {
						fxStage.addActor(i);
					}

					if (itemComponent.getQuantity() == null || itemComponent.getQuantity() == 0) {
						room.getRemovedItems().add(currentItem);
					}
					room.turnManager.endPlayerTurn();
				} else {
					System.out.println("Impossible to pick up the " + itemComponent.getItemLabel());

					//TODO warn message
				}
				
				playerIventoryCompo.setCurrentAction(null);
				
				
				break;
				
			case PICKUP_AND_USE:
				
				// USE ITEM
				boolean instaUsed = itemComponent.use(player, currentItem, room);
				
				if (instaUsed) {
					System.out.println("Insta used " + itemComponent.getItemType().getLabel());

					room.getRemovedItems().add(currentItem);
					room.removeEntity(currentItem);
					room.turnManager.endPlayerTurn();
				} else {
					System.out.println("Impossible to insta use the " + itemComponent.getItemType().getLabel());

					//TODO warn message
				}
				
				playerIventoryCompo.setCurrentAction(null);
				
				
				break;
			case USE:
				
				// USE ITEM
				boolean used = itemComponent.use(player, currentItem, room);
				
				if (used) {
					System.out.println("Used " + itemComponent.getItemType().getLabel());

					playerIventoryCompo.remove(currentItem);
					room.getRemovedItems().add(currentItem);
					room.removeEntity(currentItem);
					room.turnManager.endPlayerTurn();
				} else {
					System.out.println("Impossible to use the " + itemComponent.getItemType().getLabel());

					//TODO warn message
				}
				
				playerIventoryCompo.setCurrentAction(null);
				
				
				break;
			case DROP:
				
				// Drop animation
				Action finishDropAction = new Action(){
					  @Override
					  public boolean act(float delta){
							System.out.println("Dropped " + itemComponent.getItemType().getLabel());

							itemComponent.drop(player, currentItem, room);

							playerIventoryCompo.remove(currentItem);
							room.getAddedItems().add(currentItem);
							room.turnManager.endPlayerTurn();
							
					    return true;
					  }
				};
				Image dropImage = itemComponent.getDropAnimationImage(player, currentItem, finishDropAction);
				fxStage.addActor(dropImage);
				room.setNextState(RoomState.ITEM_DROP_ANIM);
				
								
				playerIventoryCompo.setCurrentAction(null);
				
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
	
	
		
		
		
		
		//TODO move into shop system
		if (playerIventoryCompo.getCurrentAction() == null && room.getState().canEndTurn()) {
			
			// If the user click on the player and there is an item on this tile, display the popin
			if (InputSingleton.getInstance().leftClickJustReleased) {
				Vector3 touchPoint = InputSingleton.getInstance().getTouchPoint();
				int x = (int) touchPoint.x;
				int y = (int) touchPoint.y;
				Vector2 pixelPos = TileUtil.convertPixelPosIntoGridPos(new Vector2(x,y));
				Entity shopKeeper = TileUtil.getEntityWithComponentOnTile(pixelPos, ShopKeeperComponent.class, room);
				
			if (shopKeeper != null) {
					GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(shopKeeper);
					room.entityFactory.createDialogPopin("Hey!\nI'm the shop keeper.", gridPositionComponent.getWorldPos(), 3f);
				}
				
			}
			
		} 

		
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
				// Open the popin for the first item that is not a "instant pickup" item
				ItemComponent itemComponent = Mappers.itemComponent.get(item);
				if (!itemComponent.getItemType().isInstantPickUp()) {
					playerIventoryCompo.requestAction(InventoryActionEnum.DISPLAY_POPIN, item);
					break;
				}
			}
		}
	}

	/**
	 * Returns the shop keeper in the given room. Null if no shop keeper.
	 * @param room the room
	 * @return the shop keeper entity
	 */
	private Entity getShopKeeper(Room room) {
		for (Entity e : room.getNeutrals()) {
			if (Mappers.shopKeeperComponent.has(e)) {
				return e;
			}
		}
		return null;
	}

}
