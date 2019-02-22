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
import com.dokkaebistudio.tacticaljourney.InputSingleton;
import com.dokkaebistudio.tacticaljourney.components.ShopKeeperComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent.InventoryActionEnum;
import com.dokkaebistudio.tacticaljourney.components.player.WalletComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class ShopSystem extends EntitySystem implements RoomSystem {	
	
	private Stage fxStage;
	private Room room;
	private Entity player;
	private InventoryComponent playerInventoryCompo;
	
	private List<Entity> shopKeepers = new ArrayList<>();
	
	public ShopSystem(Entity player, Room r, Stage stage) {
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

		fillShopKeepers();
		if (playerInventoryCompo.getCurrentAction() == null && room.getState().canEndTurn()) {
			
			// If the user click on the shop keeper, either display a dialog or display refill popin
			if (InputSingleton.getInstance().leftClickJustReleased) {
				Vector3 touchPoint = InputSingleton.getInstance().getTouchPoint();
				int x = (int) touchPoint.x;
				int y = (int) touchPoint.y;
				PoolableVector2 tempPos = TileUtil.convertPixelPosIntoGridPos(x, y);
				Entity shopKeeper = TileUtil.getEntityWithComponentOnTile(tempPos, ShopKeeperComponent.class, room);
				tempPos.free();
				
				if (shopKeeper != null) {
					GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(shopKeeper);
					room.entityFactory.createDialogPopin("Hey!\nI'm the shop keeper.", gridPositionComponent.getWorldPos(), 3f);
				}
				
			}
			
		} 
		
		
		
		// Buy item
		if (playerInventoryCompo.getCurrentAction() == InventoryActionEnum.BUY) {
			final Entity currentItem = playerInventoryCompo.getCurrentItem();
			final ItemComponent itemComponent = Mappers.itemComponent.get(currentItem);

			WalletComponent walletComponent = Mappers.walletComponent.get(player);
			
			// Find the correct shop keeper
			Entity shopKeeper = null;
			for (Entity sk : shopKeepers) {
				ShopKeeperComponent shopKeeperComponent = Mappers.shopKeeperComponent.get(sk);
				if (shopKeeperComponent.getSoldItems().contains(currentItem)) {
					shopKeeper = sk;
					break;
				}
			}
			
			if (walletComponent.hasEnoughMoney(itemComponent.getPrice())) {
				walletComponent.use(itemComponent.getPrice());
				room.removeEntity(itemComponent.getPriceDisplayer());
				itemComponent.setPrice(null);
				
				playerInventoryCompo.requestAction(InventoryActionEnum.PICKUP, currentItem);
				
				// TEST
				GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(shopKeeper);
				room.entityFactory.createDialogPopin("Good choice !", gridPositionComponent.getWorldPos(), 3f);
			} else {
				
				// TEST
				GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(shopKeeper);
				room.entityFactory.createDialogPopin("Come back when you've got enough gold coins.", gridPositionComponent.getWorldPos(), 3f);
				playerInventoryCompo.setCurrentAction(null);
			}
			
		}

	}



	/**
	 * Fills the list of shop keepers of the room. Empty list if no shop keepers.
	 * @param room the room
	 */
	private void fillShopKeepers() {
		for (Entity e : room.getNeutrals()) {
			if (Mappers.shopKeeperComponent.has(e)) {
				shopKeepers.add(e);
			}
		}
	}

}
