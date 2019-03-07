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
import com.dokkaebistudio.tacticaljourney.alterations.Alteration;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.alterations.Curse;
import com.dokkaebistudio.tacticaljourney.components.DestructibleComponent;
import com.dokkaebistudio.tacticaljourney.components.StatueComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AlterationReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AlterationReceiverComponent.AlterationActionEnum;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent.PlayerActionEnum;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class AlterationSystem extends EntitySystem implements RoomSystem {	
	
	private Stage fxStage;
	private Room room;
	private Entity player;
	private AlterationReceiverComponent playerAlterationReceiverCompo;
	
	private List<Entity> statues = new ArrayList<>();
	
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
								Mappers.playerComponent.get(player).requestAction(PlayerActionEnum.PRAY, statue);
							} else {
								room.setRequestedDialog("The statue looks cold.", playerPosition.getWorldPos());
//								room.entityFactory.createDialogPopin("The statue looks cold.", playerPosition.getWorldPos(), 2f, room);
							}
						} else {
							if (statueComponent.isHasBlessing()) {
								room.setRequestedDialog("A benevolent aura emanates from this statue.", playerPosition.getWorldPos());
							} else {
								room.setRequestedDialog("The statue looks cold.", playerPosition.getWorldPos());
							}
						}
						
					} else {
						room.setRequestedDialog("That was probably a bad idea.", playerPosition.getWorldPos());
					}
					
				}
				
				tempPos.free();
				
			}
			
		} 
		
		
		fillStatues();
		for (Entity statue : statues) {
			StatueComponent statueComponent = Mappers.statueComponent.get(statue);
			if (statueComponent.wasJustDestroyed()) {
				// Deliver the curse
				playerAlterationReceiverCompo.requestAction(AlterationActionEnum.RECEIVE_CURSE, statueComponent.getCurseToGive());
				statueComponent.setJustDestroyed(false);
			}
		}
		
		
		
		if (!playerAlterationReceiverCompo.getCurrentActions().isEmpty()) {
			int addCount = 0;
			int removeCount = 0;
			
			for (int i = 0 ; i < playerAlterationReceiverCompo.getCurrentActions().size() ; i++) {
				AlterationActionEnum currentAction = playerAlterationReceiverCompo.getCurrentActions().get(i);
				Alteration currentAlteration = playerAlterationReceiverCompo.getCurrentAlterations().get(i);
				switch (currentAction) {
				case RECEIVE_BLESSING:
					Blessing blessing = (Blessing) currentAlteration;
					playerAlterationReceiverCompo.addBlessing(player, blessing, fxStage, addCount);
					addCount ++;
					
					Journal.addEntry("[GREEN]Received blessing: " + blessing.title());
					break;
				case REMOVE_BLESSING:
					blessing = (Blessing) currentAlteration;
					playerAlterationReceiverCompo.removeBlessing(player, blessing, fxStage, removeCount);
					removeCount ++;
					
					Journal.addEntry("[SCARLET]Lost blessing: " + blessing.title());
					break;
				case RECEIVE_CURSE:
					Curse curse = (Curse) currentAlteration;
					playerAlterationReceiverCompo.addCurse(player, curse, fxStage, addCount);
					addCount ++;

					Journal.addEntry("[PURPLE]Received curse: " + curse.title());
					break;
				case REMOVE_CURSE:
					curse = (Curse) currentAlteration;
					playerAlterationReceiverCompo.removeCurse(player, curse, fxStage, removeCount);
					removeCount ++;
					
					Journal.addEntry("[GREEN]Lifted curse: " + curse.title());
					break;
				
				}
			}
			
			playerAlterationReceiverCompo.clearCurrentAction();
		}

		
	}



	/**
	 * Fills the list of statues of the room. Empty list if no statues.
	 * @param room the room
	 */
	private void fillStatues() {
		statues.clear();
		for (Entity e : room.getNeutrals()) {
			if (Mappers.statueComponent.has(e)) {
				statues.add(e);
			}
		}
	}

}
