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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.dokkaebistudio.tacticaljourney.InputSingleton;
import com.dokkaebistudio.tacticaljourney.components.DestructibleComponent;
import com.dokkaebistudio.tacticaljourney.components.StatueComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AlterationReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class AlterationSystem extends EntitySystem implements RoomSystem {	
	
	private Stage fxStage;
	private Room room;
	private Entity player;
	private AlterationReceiverComponent playerAlterationReceiverCompo;
	private InventoryComponent playerIventoryCompo;
	
	public AlterationSystem(Entity player, Room r, Stage stage) {
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
		
		if (playerAlterationReceiverCompo == null) {
			playerAlterationReceiverCompo = Mappers.alterationReceiverComponent.get(player);
		}
		
		if (room.getState().canEndTurn()) {
			
			if (InputSingleton.getInstance().leftClickJustReleased) {
				Vector3 touchPoint = InputSingleton.getInstance().getTouchPoint();
				int x = (int) touchPoint.x;
				int y = (int) touchPoint.y;
				PoolableVector2 tempPos = TileUtil.convertPixelPosIntoGridPos(x, y);
				Entity statue = TileUtil.getEntityWithComponentOnTile(tempPos, StatueComponent.class, room);
				
				if (statue != null) {
					StatueComponent statueComponent = Mappers.statueComponent.get(statue);
					DestructibleComponent destructibleComponent = Mappers.destructibleComponent.get(statue);
					GridPositionComponent playerPosition = Mappers.gridPositionComponent.get(player);

					if (!destructibleComponent.isDestroyed()) {
						int distanceFromStatue = TileUtil.getDistanceBetweenTiles(playerPosition.coord(), tempPos);
						if (distanceFromStatue == 1) {
							//Close from statue, display popin
							if (statueComponent.isHasBlessing()) {
								Mappers.playerComponent.get(player).setPrayRequested(statue);
							} else {
								room.entityFactory.createDialogPopin("The statue looks cold.", playerPosition.getWorldPos(), 2f);
							}
						} else {
							if (statueComponent.isHasBlessing()) {
								room.entityFactory.createDialogPopin("A benevolent aura emanates from this statue.", playerPosition.getWorldPos(), 2f);
							} else {
								room.entityFactory.createDialogPopin("The statue looks cold.", playerPosition.getWorldPos(), 2f);
							}
						}
						
					} else {
						room.entityFactory.createDialogPopin("That was probably a bad idea.", playerPosition.getWorldPos(), 2f);
					}
					
				}
				
				tempPos.free();
				
			}
			
		} 

		
	}



	/**
	 * Returns the shop keeper in the given room. Null if no shop keeper.
	 * @param room the room
	 * @return the shop keeper entity
	 */
	private Entity getStatue(Room room) {
		for (Entity e : room.getNeutrals()) {
			if (Mappers.statueComponent.has(e)) {
				return e;
			}
		}
		return null;
	}

}
