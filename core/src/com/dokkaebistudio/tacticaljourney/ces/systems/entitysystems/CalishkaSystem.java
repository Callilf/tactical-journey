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

import java.util.Optional;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;
import com.dokkaebistudio.tacticaljourney.ces.components.SpeakerComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.neutrals.CalishkaComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.neutrals.SoulbenderComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.ces.systems.NamedSystem;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.singletons.InputSingleton;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class CalishkaSystem extends NamedSystem {	
	
	private Entity player;
	private InventoryComponent playerInventoryCompo;
	
	public CalishkaSystem(Entity player, Room r) {
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
		
		// Handle clicks
		if (playerInventoryCompo.getCurrentAction() == null && room.getState().canEndTurn()) {
			
			// If the user click on the shop keeper, either display a dialog or display refill popin
			if (InputSingleton.getInstance().leftClickJustReleased) {
				Vector3 touchPoint = InputSingleton.getInstance().getTouchPoint();
				int x = (int) touchPoint.x;
				int y = (int) touchPoint.y;
				PoolableVector2 tempPos = TileUtil.convertPixelPosIntoGridPos(x, y);
				Optional<Entity> calishkaOpt = TileUtil.getEntityWithComponentOnTile(tempPos, CalishkaComponent.class, room);
				
				if (calishkaOpt.isPresent()) {
					Entity calishka = calishkaOpt.get();
					
					GridPositionComponent playerPosition = Mappers.gridPositionComponent.get(player);
					int distance = TileUtil.getDistanceBetweenTiles(playerPosition.coord(), tempPos);
					if (distance == 1) {
						SpeakerComponent speakerComponent = Mappers.speakerComponent.get(calishka);
						room.setRequestedDialog(speakerComponent.getSpeech(calishka));
					}
				}
				
				tempPos.free();
			}
			
		} 

	}

}
