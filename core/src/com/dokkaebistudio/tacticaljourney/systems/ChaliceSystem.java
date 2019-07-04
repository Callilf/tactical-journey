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
import java.util.Optional;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.neutrals.ChaliceComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent.PlayerActionEnum;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.singletons.AnimationSingleton;
import com.dokkaebistudio.tacticaljourney.singletons.InputSingleton;
import com.dokkaebistudio.tacticaljourney.util.AnimatedImage;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class ChaliceSystem extends EntitySystem implements RoomSystem {	
	
	private Stage fxStage;
	private Room room;
	
	private List<Entity> chalices = new ArrayList<>();

	public ChaliceSystem(Room r, Stage stage) {
		this.priority = 12;

		this.fxStage = stage;
		this.room = r;
	}
	
	
    @Override
    public void enterRoom(Room newRoom) {
    	this.room = newRoom;	
    }
    
	@Override
	public void update(float deltaTime) {
		
		if (room.getState().canEndTurn()) {
			
			if (InputSingleton.getInstance().leftClickJustReleased) {
				Vector3 touchPoint = InputSingleton.getInstance().getTouchPoint();
				int x = (int) touchPoint.x;
				int y = (int) touchPoint.y;
				PoolableVector2 tempPos = TileUtil.convertPixelPosIntoGridPos(x, y);
				Optional<Entity> chaliceOpt = TileUtil.getEntityWithComponentOnTile(tempPos, ChaliceComponent.class, room);
				
				if (chaliceOpt.isPresent()) {
					Entity chalice = chaliceOpt.get();
					ChaliceComponent chaliceCompo = Mappers.chaliceComponent.get(chalice);
					GridPositionComponent playerPosition = Mappers.gridPositionComponent.get(GameScreen.player);

					int distanceFromStatue = TileUtil.getDistanceBetweenTiles(playerPosition.coord(), tempPos);
					if (distanceFromStatue == 1 && chaliceCompo.isFilled()) {
						//Close from chalice, display popin
						Mappers.playerComponent.get(GameScreen.player).requestAction(PlayerActionEnum.DRINK_CHALICE, chalice);
					}
					
				}
				
				tempPos.free();
				
			}
			
		} 
		
		
		fillChalices();
		for (Entity chalice : chalices) {
			ChaliceComponent chaliceCompo = Mappers.chaliceComponent.get(chalice);
			
			if (chaliceCompo.isFilled() && chaliceCompo.getAura() == null) {
				AnimatedImage aura = createHolyAura(chalice);
				chaliceCompo.setAura(aura);
				chaliceCompo.showMarker(chalice);
			}

		}
		
		

		
	}


	/**
	 * Fills the list of statues of the room. Empty list if no statues.
	 * @param room the room
	 */
	private void fillChalices() {
		chalices.clear();
		room.getNeutrals().stream()
				.filter(e -> Mappers.chaliceComponent.has(e))
				.forEachOrdered(chalices::add);
	}
	
	
	private AnimatedImage createHolyAura(Entity statue) {
		AnimatedImage aura = new AnimatedImage(AnimationSingleton.getInstance().holy, true);
		PoolableVector2 animPos = TileUtil.convertGridPosIntoPixelPos(Mappers.gridPositionComponent.get(statue).coord());
		aura.setPosition(animPos.x + GameScreen.GRID_SIZE/2 - aura.getWidth()/2, animPos.y + GameScreen.GRID_SIZE - 10);
		animPos.free();
		return aura;
	}
}
