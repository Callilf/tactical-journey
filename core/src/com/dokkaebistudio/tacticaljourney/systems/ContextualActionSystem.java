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

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.Vector3;
import com.dokkaebistudio.tacticaljourney.InputSingleton;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.loot.LootableComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.components.transition.ExitComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

/**
 * This system handles all the various actions that can be performed in a room via a popin, like looting,
 * activating something, starting a conversation...
 * @author Callil
 *
 */
public class ContextualActionSystem extends EntitySystem implements RoomSystem {	
	
	private Room room;
	private Entity player;
	private PlayerComponent playerCompo;
	private MoveComponent playerMoveCompo;
	
	public ContextualActionSystem(Entity player, Room r) {
		this.priority = 13;

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
		if (playerCompo == null) {
			playerCompo = Mappers.playerComponent.get(player);
		}
		
		if (room.getState() == RoomState.PLAYER_END_MOVEMENT) {
			
			// The player just arrived on a tile
			checkForLootablesToDisplayPopin();
			checkForExitToDisplayPopin();
			
		} else if (room.getState().canEndTurn()) {
			
			// If the user click on the player and there is an item on this tile, display the popin
			if (InputSingleton.getInstance().leftClickJustReleased) {
				Vector3 touchPoint = InputSingleton.getInstance().getTouchPoint();
				int x = (int) touchPoint.x;
				int y = (int) touchPoint.y;
				
				SpriteComponent spriteComponent = Mappers.spriteComponent.get(player);
				if (spriteComponent.containsPoint(x, y)) {
					
					// Touched the player, if there is a contextual action entity on this tile, display a popin
					checkForLootablesToDisplayPopin();
					checkForExitToDisplayPopin();
				}
				
			}

		}		
	}


	/**
	 * Check whether there is a lootable entity on the current tile, if so
	 * display the "Would you like to loot?" popin.
	 */
	private void checkForLootablesToDisplayPopin() {
		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(player);
		Entity lootable = TileUtil.getEntityWithComponentOnTile(gridPositionComponent.coord(), LootableComponent.class, room);
		if (lootable != null) {
			playerCompo.setLootRequested(lootable);
		}
	}

	/**
	 * Check whether there is a doorway to another floor on the current tile, if so
	 * display the "Would you like to leave?" popin.
	 */
	private void checkForExitToDisplayPopin() {
		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(player);
		Entity exit = TileUtil.getEntityWithComponentOnTile(gridPositionComponent.coord(), ExitComponent.class, room);
		if (exit != null) {
			playerCompo.setExitRequested(exit);
		}
	}


}
