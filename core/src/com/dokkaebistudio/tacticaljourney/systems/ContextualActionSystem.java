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

import java.util.Optional;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.Vector3;
import com.dokkaebistudio.tacticaljourney.components.WormholeComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.loot.LootableComponent;
import com.dokkaebistudio.tacticaljourney.components.neutrals.RecyclingMachineComponent;
import com.dokkaebistudio.tacticaljourney.components.neutrals.SewingMachineComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent.PlayerActionEnum;
import com.dokkaebistudio.tacticaljourney.components.transition.ExitComponent;
import com.dokkaebistudio.tacticaljourney.components.transition.SecretDoorComponent;
import com.dokkaebistudio.tacticaljourney.enums.InventoryDisplayModeEnum;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.singletons.InputSingleton;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;
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
			checkForWormholeToDisplayPopin();
			checkForSecretDoorToDisplayPopin();
			
		} else if (room.getState().canEndTurn()) {
			
			// If the user click on the player and there is an item on this tile, display the popin
			if (InputSingleton.getInstance().leftClickJustReleased) {
				Vector3 touchPoint = InputSingleton.getInstance().getTouchPoint();
				int x = (int) touchPoint.x;
				int y = (int) touchPoint.y;
				
				if (TileUtil.isPixelPosOnEntity(x, y, player)) {
					// Touched the player, if there is a contextual action entity on this tile, display a popin
					checkForLootablesToDisplayPopin();
					checkForExitToDisplayPopin();
					checkForWormholeToDisplayPopin();
					checkForSecretDoorToDisplayPopin();
				}
				
				
				PoolableVector2 gridPos = TileUtil.convertPixelPosIntoGridPos(x, y);
				int distanceFromPlayer = TileUtil.getDistanceBetweenTiles(gridPos, Mappers.gridPositionComponent.get(player).coord());
				if (distanceFromPlayer == 1) {
					
					// At close range
					
					Optional<Entity> sewingMachine = TileUtil.getEntityWithComponentOnTile(gridPos, SewingMachineComponent.class, room);
					if (sewingMachine.isPresent()) {
						playerCompo.requestAction(PlayerActionEnum.SEW, sewingMachine.get());
					}
					Optional<Entity> recyclingMachine = TileUtil.getEntityWithComponentOnTile(gridPos, RecyclingMachineComponent.class, room);
					if (recyclingMachine.isPresent()) {
						InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(GameScreen.player);
						inventoryComponent.setRecycler(recyclingMachine.get());
						inventoryComponent.setDisplayMode(InventoryDisplayModeEnum.RECYCLING);
					}

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
		Optional<Entity> lootable = TileUtil.getEntityWithComponentOnTile(gridPositionComponent.coord(), LootableComponent.class, room);
		if (lootable.isPresent()) {

			if (room.hasEnemies()) {
				playerCompo.requestAction(PlayerActionEnum.LOOT, lootable.get());
			} else {
				LootableComponent lootableComponent = Mappers.lootableComponent.get(lootable.get());
				InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(player);
				inventoryComponent.setTurnsToWaitBeforeLooting(lootableComponent.getNbTurnsToOpen());
				inventoryComponent.setLootableEntity(lootable.get());
			}
		}
	}

	/**
	 * Check whether there is a doorway to another floor on the current tile, if so
	 * display the "Would you like to leave?" popin.
	 */
	private void checkForExitToDisplayPopin() {
		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(player);
		Optional<Entity> exit = TileUtil.getEntityWithComponentOnTile(gridPositionComponent.coord(), ExitComponent.class, room);
		if (exit.isPresent()) {
			playerCompo.requestAction(PlayerActionEnum.EXIT, exit.get());
		}
	}

	/**
	 * Check whether there is a wormhole to display the "Would you like to use it" popin.
	 */
	private void checkForWormholeToDisplayPopin() {
		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(player);
		Optional<Entity> wormhole = TileUtil.getEntityWithComponentOnTile(gridPositionComponent.coord(), WormholeComponent.class, room);
		if (wormhole.isPresent()) {
			playerCompo.requestAction(PlayerActionEnum.WORMHOLE, wormhole.get());
		}
	}
	
	/**
	 * Check whether there is a secret door on the current tile, if so
	 * display the teleport popin.
	 */
	private void checkForSecretDoorToDisplayPopin() {
		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(player);
		Optional<Entity> secretDoor = TileUtil.getEntityWithComponentOnTile(gridPositionComponent.coord(), SecretDoorComponent.class, room);
		if (secretDoor.isPresent() && Mappers.secretDoorComponent.get(secretDoor.get()).isOpened()) {
			playerCompo.requestAction(PlayerActionEnum.TELEPORT_POPIN, secretDoor.get());
		}
	}

}
