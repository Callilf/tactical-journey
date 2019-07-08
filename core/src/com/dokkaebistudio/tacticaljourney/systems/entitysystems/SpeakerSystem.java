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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;
import com.dokkaebistudio.tacticaljourney.components.SpeakerComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.components.neutrals.SoulbenderComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent.InventoryActionEnum;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent.PlayerActionEnum;
import com.dokkaebistudio.tacticaljourney.components.player.WalletComponent;
import com.dokkaebistudio.tacticaljourney.dialog.pnjs.SoulbenderDialogs;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemDivineCatalyst;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.singletons.InputSingleton;
import com.dokkaebistudio.tacticaljourney.systems.NamedSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class SpeakerSystem extends NamedSystem {	
	
	private Entity player;	
	private List<Entity> speakers = new ArrayList<>();
	
	public SpeakerSystem(Entity player, Room r) {
		this.priority = 13;

		this.player = player;
		this.room = r;
	}
    
	@Override
	public void performUpdate(float deltaTime) {
		// Handle clicks on a speaker
		if (room.getState().canEndTurn()) {
						
			// If the user click on the shop keeper, either display a dialog or display refill popin
			if (InputSingleton.getInstance().leftClickJustReleased) {
				Vector3 touchPoint = InputSingleton.getInstance().getTouchPoint();
				int x = (int) touchPoint.x;
				int y = (int) touchPoint.y;
				PoolableVector2 tempPos = TileUtil.convertPixelPosIntoGridPos(x, y);
				Optional<Entity> speakerOpt = TileUtil.getEntityWithComponentOnTile(tempPos, SpeakerComponent.class, room);
				
				if (speakerOpt.isPresent()) {
					Entity speaker = speakerOpt.get();
					
					GridPositionComponent playerPosition = Mappers.gridPositionComponent.get(player);
					int distanceFromSoulbender = TileUtil.getDistanceBetweenTiles(playerPosition.coord(), tempPos);
					if (distanceFromSoulbender == 1) {
						SpeakerComponent speakerComponent = Mappers.speakerComponent.get(speaker);
						speakerComponent.turnOffMarker();
						room.setRequestedDialog(speakerComponent.getSpeech(speaker));
						
        				Mappers.spriteComponent.get(speaker).orientSprite(speaker, playerPosition.coord());
        				Mappers.spriteComponent.get(player).orientSprite(player, tempPos);
					}
				}
				
				tempPos.free();
			}
			
		} 
		
		
		// Handle the marker
		if (room.getState() == RoomState.PLAYER_COMPUTE_MOVABLE_TILES) {
			fillSpeakers();
			
			for (Entity speaker : speakers) {
				Mappers.speakerComponent.get(speaker).updateMarker(speaker);
			}
		}
		
	}



	private void fillSpeakers() {
		speakers.clear();
		room.getNeutrals().stream()
			.filter( e -> Mappers.speakerComponent.has(e))
			.forEachOrdered(speakers::add);
	}

}
