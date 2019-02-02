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
import com.badlogic.gdx.math.Vector3;
import com.dokkaebistudio.tacticaljourney.InputSingleton;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent.InventoryActionEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class ItemSystem extends EntitySystem implements RoomSystem {	
	
	private Room room;
	private Entity player;
	private MoveComponent playerMoveCompo;
	private InventoryComponent playerIventoryCompo;
	
	public ItemSystem(Entity player, Room r) {
		this.priority = 11;

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
					System.out.println("Picked up a " + itemComponent.getItemType().getLabel());

					//Pick up this consumable
					itemComponent.pickUp(player, item, room);
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
			
			Entity currentItem = playerIventoryCompo.getCurrentItem();
			ItemComponent itemComponent = Mappers.itemComponent.get(currentItem);

			
			// An action has been done in the inventory
			switch(playerIventoryCompo.getCurrentAction()) {
			
			case PICKUP:
				
				// USE ITEM
				boolean pickedUp = itemComponent.pickUp(player, currentItem, room);
				
				if (pickedUp) {
					System.out.println("Picked up a " + itemComponent.getItemType().getLabel());

					room.turnManager.endPlayerTurn();
				} else {
					System.out.println("Impossible to pick up the " + itemComponent.getItemType().getLabel());

					//TODO warn message
				}
				
				playerIventoryCompo.setCurrentAction(null);
				
				
				break;
				
			case PICKUP_AND_USE:
				
				// USE ITEM
				boolean instaUsed = itemComponent.use(player, currentItem, room);
				
				if (instaUsed) {
					System.out.println("Insta used a " + itemComponent.getItemType().getLabel());

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
					System.out.println("Used a " + itemComponent.getItemType().getLabel());

					playerIventoryCompo.remove(currentItem);
					room.removeEntity(currentItem);
					room.turnManager.endPlayerTurn();
				} else {
					System.out.println("Impossible to use the " + itemComponent.getItemType().getLabel());

					//TODO warn message
				}
				
				playerIventoryCompo.setCurrentAction(null);
				
				
				break;
			case DROP:
				
				// USE ITEM
				boolean dropped = itemComponent.drop(player, currentItem, room);
				
				if (dropped) {
					System.out.println("Dropped a " + itemComponent.getItemType().getLabel());

					playerIventoryCompo.remove(currentItem);
					room.turnManager.endPlayerTurn();
				} else {
					System.out.println("Impossible to drop the " + itemComponent.getItemType().getLabel());

					//TODO warn message
				}
				
				playerIventoryCompo.setCurrentAction(null);
				
				break;
				default :
			
			}
			
			
		}		
	}


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


}
