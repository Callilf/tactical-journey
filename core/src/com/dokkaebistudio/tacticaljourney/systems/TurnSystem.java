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
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.rendering.service.PopinService;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class TurnSystem extends IteratingSystem implements RoomSystem {	
	
	private Stage stage;
	private Room room;

	
	public TurnSystem(Room r, Stage s) {
		super(Family.all(PlayerComponent.class).get());
		
		this.priority = 5;

		this.room = r;
		this.stage = s;
	}
	
	
    @Override
    public void enterRoom(Room newRoom) {
    	this.room = newRoom;	
    }
    
    @Override
    protected void processEntity(Entity moverEntity, float deltaTime) {}

	@Override
	public void update(float deltaTime) {
		
		switch(room.getState()) {
		case PLAYER_END_TURN:
			room.setNextState(RoomState.ALLY_TURN_INIT);
			break;
		case ALLY_END_TURN:
			room.setNextState(RoomState.ENEMY_TURN_INIT);
			break;
		case ENEMY_END_TURN:
			room.turnManager.startNewTurn();
			displayNewTurnLabel();
			
		default:
		}

	}


	/**
	 * Show the label "New turn" above the player
	 */
	private void displayNewTurnLabel() {
		final Label newTurnLabel = new Label("NEW TURN", PopinService.hudStyle());
		GridPositionComponent playerPos = Mappers.gridPositionComponent.get(GameScreen.player);
		newTurnLabel.setPosition(playerPos.getWorldPos().x, playerPos.getWorldPos().y + GameScreen.GRID_SIZE);
		
		Action finishAction = new Action(){
		  @Override
		  public boolean act(float delta){
			  newTurnLabel.remove();
		    return true;
		  }
		};
			
		newTurnLabel.addAction(Actions.sequence(Actions.moveBy(0, 50, 1.0f), finishAction));
		stage.addActor(newTurnLabel);
	}
}
