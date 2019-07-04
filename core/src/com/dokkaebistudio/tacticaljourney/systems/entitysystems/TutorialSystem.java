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
import com.dokkaebistudio.tacticaljourney.components.creep.CreepComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.creeps.CreepBush;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemPebble;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.systems.NamedSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

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
		
		switch(tutorialComponent.getTutorialNumber()) {
		case 1:
			
			if (!tutorialComponent.isGoal1Reached() && room.getState() == RoomState.PLAYER_END_MOVEMENT) {
				GridPositionComponent playerPosCompo = Mappers.gridPositionComponent.get(GameScreen.player);
				Optional<Entity> creep = TileUtil.getEntityWithComponentOnTile(playerPosCompo.coord(), CreepComponent.class, room);
				if (creep.isPresent()) {
					if (Mappers.creepComponent.get(creep.get()).getType() instanceof CreepBush) {
						// Reached the bush
						tutorialComponent.setGoal1Reached(true);
					}
				}
			}
			
			break;
			
		case 2:
			if (!tutorialComponent.isGoal1Reached()) {
				InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(GameScreen.player);
				if (inventoryComponent.contains(ItemPebble.class)) {
					tutorialComponent.setGoal1Reached(true);;
				}
			}
			
			break;
		
		
			default:
				System.out.println("We should never end up here in the tutorial system !!!");
		}
		
	}

	

}
