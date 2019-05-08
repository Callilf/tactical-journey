package com.dokkaebistudio.tacticaljourney.systems;

import java.util.Set;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.assets.SceneAssets;
import com.dokkaebistudio.tacticaljourney.components.InspectableComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.rendering.service.PopinService;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.singletons.InputSingleton;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class InspectSystem extends EntitySystem implements RoomSystem {
	
	private Stage fxStage;
	private Room room;
	private Entity player;
	
	private Table clickAnywhereTable;
	
	private static InspectModeActionEnum requestedAction = InspectModeActionEnum.NONE;
	
	public enum InspectModeActionEnum {
		NONE,
		ACTIVATE,
		DEACTIVATE;
	}
	
		
	public InspectSystem(Entity player, Room r, Stage stage) {
		this.priority = 24;

		this.fxStage = stage;
		this.player = player;
		this.room = r;
		
		this.clickAnywhereTable = new Table();
		NinePatchDrawable ninePatchDrawable = new NinePatchDrawable(SceneAssets.popinOuterNinePatch);
		this.clickAnywhereTable.setBackground(ninePatchDrawable);
		
		Label l = new Label("Click on anything to inspect it", PopinService.hudStyle());
		this.clickAnywhereTable.add(l).pad(5, 5, 5, 5);
		this.clickAnywhereTable.pack();
		
		this.clickAnywhereTable.setPosition(GameScreen.SCREEN_W/2 - this.clickAnywhereTable.getWidth()/2, 
				GameScreen.SCREEN_H/2 - this.clickAnywhereTable.getHeight()/2 + GameScreen.BOTTOM_MENU_HEIGHT);

		this.clickAnywhereTable.addAction(Actions.alpha(0.7f));

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
			clickAnywhereTable.remove();
			room.setNextState(room.getLastInGameState());
			requestedAction = InspectModeActionEnum.NONE;
		}
		
		if (room.getState() == RoomState.INSPECT_MODE_INIT) {
		
			// Click anywhere to inspect
			fxStage.addActor(clickAnywhereTable);
			room.setNextState(RoomState.INSPECT_MODE);
			
		} else if (room.getState() == RoomState.INSPECT_MODE) {
			
			
			// Wait for the user to click somewhere
			if (InputSingleton.getInstance().leftClickJustReleased) {
				PoolableVector2 touchPoint = InputSingleton.getInstance().getTouchPointInGridPos();				
				Set<Entity> inspectableEntities = TileUtil.getEntitiesWithComponentOnTile(touchPoint, InspectableComponent.class, room);
				
				PlayerComponent playerComponent = Mappers.playerComponent.get(player);
				playerComponent.addInspectedEntities(inspectableEntities);
				
				clickAnywhereTable.remove();
			}
			
		
		}
		
	}
	
	
	
	public static void requestAction(InspectModeActionEnum action) {
		requestedAction = action;
	}

}
