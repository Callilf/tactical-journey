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

import java.util.Optional;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.dokkaebistudio.tacticaljourney.components.TutorialComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.factory.EntityFlagEnum;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemPebble;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.room.Tile;
import com.dokkaebistudio.tacticaljourney.systems.NamedSystem;
import com.dokkaebistudio.tacticaljourney.systems.iteratingsystems.PlayerMoveSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;
import com.dokkaebistudio.tacticaljourney.vfx.VFXUtil;

public class TutorialSystem extends NamedSystem {	
	
	private Entity calishka;
	private TutorialComponent tutorialComponent;
	public Stage fxStage;
	
	public TutorialSystem(Room r, Stage fxStage) {
		this.priority = 20;
		this.room = r;
		this.fxStage = fxStage;
	}
	
	
    @Override
    public void enterRoom(Room newRoom) {
    	this.room = newRoom;
    	this.findCalishka();
    	this.findTutorialComponent();
    }
    
	private void findCalishka() {
		for (Entity e : room.getNeutrals()) {
			if (Mappers.calishkaComponent.has(e)) {
				calishka = e;
				break;
			}
		}
	}
	
	private void findTutorialComponent() {
		tutorialComponent = Mappers.tutorialComponent.get(this.calishka);
	}
	
    
	@Override
	public void performUpdate(float deltaTime) {
		GridPositionComponent playerPosCompo = Mappers.gridPositionComponent.get(GameScreen.player);
		
		switch(tutorialComponent.getTutorialNumber()) {
		case 1:
			
			if (!tutorialComponent.isGoal1Reached() && room.getState() == RoomState.PLAYER_END_MOVEMENT) {
				if (playerPosCompo.coord().x == 21 && playerPosCompo.coord().y == 6) {
					displayObjectiveReachedNotif(playerPosCompo);
					tutorialComponent.setGoal1Reached(true);
				}
			}
			
			break;
			
		case 2:
			if (room.isJustEntered()) {
				Mappers.experienceComponent.get(GameScreen.player).setCurrentXp(0);
			}
			
			if (!tutorialComponent.isGoal1Reached()) {
				InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(GameScreen.player);
				if (inventoryComponent.contains(ItemPebble.class)) {
					displayObjectiveReachedNotif(playerPosCompo);
					tutorialComponent.setGoal1Reached(true);
				}
				break;
			}
				
			if (!tutorialComponent.isGoal2Reached()) {
				Tile mudTile = room.getTileAtGridPosition(17, 6);
				Optional<Entity> item = TileUtil.getEntityWithComponentOnTile(mudTile.getGridPos(), ItemComponent.class, room);
				if (item.isPresent() && Mappers.itemComponent.get(item.get()).getItemType() instanceof ItemPebble) {
					displayObjectiveReachedNotif(playerPosCompo);
					tutorialComponent.setGoal2Reached(true);
				}
				break;
			}
			
			if (!tutorialComponent.isGoal3Reached()) {
				if (Mappers.healthComponent.get(GameScreen.player).getArmor() > 0) {
					displayObjectiveReachedNotif(playerPosCompo);
					tutorialComponent.setGoal3Reached(true);
				}
				break;
			}
			
			break;
			
		case 3:
			if (room.isJustEntered()) {
				Mappers.experienceComponent.get(GameScreen.player).setCurrentXp(0);
			}
			
			if (!tutorialComponent.isGoal1Reached()) {
				if (room.turnManager.getTurn() >= 4) {
					displayObjectiveReachedNotif(playerPosCompo);
					tutorialComponent.setGoal1Reached(true);
				}
				break;
			}
				
			if (!tutorialComponent.isGoal2Reached()) {
				if (Mappers.experienceComponent.get(GameScreen.player).getCurrentXp() > 0) {
					displayObjectiveReachedNotif(playerPosCompo);
					tutorialComponent.setGoal2Reached(true);
					room.closeDoors();
				}
				break;
			}
			
			if (!tutorialComponent.isGoal3Reached()) {
				PlayerComponent playerComponent = Mappers.playerComponent.get(GameScreen.player);
				if (playerComponent.getInspectedEntities().size() == 1) {
					Entity entity = playerComponent.getInspectedEntities().get(0);
					if (entity.flags == EntityFlagEnum.ENEMY_STINGER.getFlag()) {
						displayObjectiveReachedNotif(playerPosCompo);
						tutorialComponent.setGoal3Reached(true);
					}
				}
				break;
			}
			
			if (!tutorialComponent.isGoal4Reached()) {
				if (PlayerMoveSystem.enemyHighlighted != null && PlayerMoveSystem.enemyHighlighted.flags == EntityFlagEnum.ENEMY_STINGER.getFlag()) {
					displayObjectiveReachedNotif(playerPosCompo);
					tutorialComponent.setGoal4Reached(true);
				}
				break;
			}
			break;
			
			
		case 4:
			if (room.isJustEntered()) {
				Mappers.experienceComponent.get(GameScreen.player).setCurrentXp(0);
			}
			
			if (!tutorialComponent.isGoal1Reached()) {
				if (Mappers.experienceComponent.get(GameScreen.player).getCurrentXp() == 2) {
					displayObjectiveReachedNotif(playerPosCompo);
					tutorialComponent.setGoal1Reached(true);
					room.closeDoors();
				}
				break;
			}

			if (!tutorialComponent.isGoal2Reached()) {
				if (Mappers.experienceComponent.get(GameScreen.player).getCurrentXp() == 4) {
					displayObjectiveReachedNotif(playerPosCompo);
					tutorialComponent.setGoal2Reached(true);
					room.closeDoors();
				}
				break;
			}
		
			break;
		
			default:
				System.out.println("We should never end up here in the tutorial system !!!");
		}
		
	}


	private void displayObjectiveReachedNotif(GridPositionComponent playerPosCompo) {
		VFXUtil.createStatsUpNotif("OBJECTIVE REACHED", "YELLOW", playerPosCompo.coord());
	}

	

}
