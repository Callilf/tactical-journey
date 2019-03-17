package com.dokkaebistudio.tacticaljourney.systems;

import java.util.Set;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.InputSingleton;
import com.dokkaebistudio.tacticaljourney.components.InspectableComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent.PlayerActionEnum;
import com.dokkaebistudio.tacticaljourney.rendering.service.PopinService;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class InspectSystem extends EntitySystem implements RoomSystem {
	
	private Stage fxStage;
	private Room room;
	private Entity player;
	
	private Label clickAnywhereLabel;
	
	private static InspectModeActionEnum requestedAction = InspectModeActionEnum.NONE;
	
	public enum InspectModeActionEnum {
		NONE,
		ACTIVATE,
		DEACTIVATE;
	}
	
		
	public InspectSystem(Entity player, Room r, Stage stage) {
		this.priority = 21;

		this.fxStage = stage;
		this.player = player;
		this.room = r;
		
		this.clickAnywhereLabel = new Label("Click on anything to inspect it", PopinService.hudStyle());
		clickAnywhereLabel.setPosition(GameScreen.SCREEN_W/2 - clickAnywhereLabel.getWidth()/2, 
				GameScreen.SCREEN_H/2 - clickAnywhereLabel.getHeight()/2 + GameScreen.BOTTOM_MENU_HEIGHT);

	}
	
	@Override
    public void enterRoom(Room newRoom) {
    	this.room = newRoom;	
    }
    
	
	@Override	
	public void update(float deltaTime) {
		
		if (requestedAction == InspectModeActionEnum.ACTIVATE) {
			room.setNextState(RoomState.INSPECT_MODE_INIT);
			requestedAction = InspectModeActionEnum.NONE;
		} else if (requestedAction == InspectModeActionEnum.DEACTIVATE) {
			clickAnywhereLabel.remove();
			room.setNextState(room.getLastInGameState());
			requestedAction = InspectModeActionEnum.NONE;
		}
		
		if (room.getState() == RoomState.INSPECT_MODE_INIT) {
		
			// Click anywhere to inspect
			fxStage.addActor(clickAnywhereLabel);
			room.setNextState(RoomState.INSPECT_MODE);
			
		} else if (room.getState() == RoomState.INSPECT_MODE) {
			
			
			// Wait for the user to click somewhere
			if (InputSingleton.getInstance().leftClickJustReleased) {
				PoolableVector2 touchPoint = InputSingleton.getInstance().getTouchPointInGridPos();				
				Set<Entity> inspectableEntities = TileUtil.getEntitiesWithComponentOnTile(touchPoint, InspectableComponent.class, room);
				
				PlayerComponent playerComponent = Mappers.playerComponent.get(player);
				playerComponent.addInspectedEntities(inspectableEntities);
				
				clickAnywhereLabel.remove();
			}
			
		
		}
		
	}
	
	
	
	public static void requestAction(InspectModeActionEnum action) {
		requestedAction = action;
	}

}
