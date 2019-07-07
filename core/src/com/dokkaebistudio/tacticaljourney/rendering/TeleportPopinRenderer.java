package com.dokkaebistudio.tacticaljourney.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.dokkaebistudio.tacticaljourney.assets.SceneAssets;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent.PlayerActionEnum;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.rendering.interfaces.Renderer;
import com.dokkaebistudio.tacticaljourney.rendering.service.PopinService;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.singletons.InputSingleton;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.MovementHandler;

public class TeleportPopinRenderer implements Renderer, RoomSystem {
	    
	public Stage stage;
	
	/** The player component (kept in cache to prevent getting it at each frame). */
	private PlayerComponent playerCompo;
	
	/** The current room. */
    private Room room;
    
	private Array<Room> accessibleRooms = new Array<>();

        
    //**************************
    // Actors
    
    private Table choicePopin;
    private Table choicePopinSubTable;
    private Label desc;
    
    public TeleportPopinRenderer(Room r, Stage s) {
        this.room = r;
        this.stage = s;
    }
    
    @Override
    public void enterRoom(Room newRoom) {
    	this.room = newRoom;	
    }


    @Override
    public void render(float deltaTime) {
    	
    	if (playerCompo == null) {
    		playerCompo = Mappers.playerComponent.get(GameScreen.player);
    	}
    	
    	
    	
    	if (playerCompo.getRequestedActions().size() == 1 && playerCompo.getRequestedActions().get(0) == PlayerActionEnum.TELEPORT_POPIN) {
    		room.setNextState(RoomState.TELEPORT_POPIN);

			if (choicePopin == null) {
				initChoiceTable();
				
    			// Close popin with ESCAPE
	    		stage.addListener(new InputListener() {
					@Override
					public boolean keyUp(InputEvent event, int keycode) {
						if (room.getState() == RoomState.TELEPORT_POPIN && (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK)) {
							closePopin();
							return true;
						}
						return super.keyUp(event, keycode);
					}
				});
			}
			
			updateContent();
			
			this.stage.addActor(choicePopin);
			
			playerCompo.clearRequestedAction();
    	}
    	
    	if (room.getState() == RoomState.TELEPORT_POPIN) {
    		// Draw the table
            stage.act(Gdx.graphics.getDeltaTime());
    		stage.draw();
    		
    		// Close the inventory on a left click outside the popin
    		if (InputSingleton.getInstance().leftClickJustPressed) {
    			closePopin();
    		}
    	}
	}

	private void updateContent() {
		choicePopinSubTable.clear();
		accessibleRooms.clear();
		
		for (Room r : room.floor.getRooms()) {
			if (r == room) continue;
			if (r.getSecretDoor() != null && Mappers.secretDoorComponent.get(r.getSecretDoor()).isOpened()) {
				accessibleRooms.add(r);
			}
		}
		
		if (accessibleRooms.size ==0) {
			desc.setText("A secret door that allows reaching other rooms easily. However, you haven't opened any other secret door yet.");
		} else {
			desc.setText("A secret door that allows reaching other rooms easily. Where do you want to go?");

			for (final Room r : accessibleRooms) {
				
				final TextButton btn = new TextButton(r.type.title(),PopinService.buttonStyle());			
				btn.addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						room.floor.enterRoom(r);
						GridPositionComponent secretDoorPos = Mappers.gridPositionComponent.get(r.getSecretDoor());
						MovementHandler.placeEntity(GameScreen.player, secretDoorPos.coord(), r);

						closePopin();
					}
				});
				choicePopinSubTable.add(btn).pad(0, 20,5,20);
				choicePopinSubTable.row();
			}
		}
		choicePopinSubTable.pack();
		choicePopin.pack();
		choicePopin.setPosition(GameScreen.SCREEN_W/2 - choicePopin.getWidth()/2, GameScreen.SCREEN_H/2 - choicePopin.getHeight()/2);

	}


	private void initChoiceTable() {		
		choicePopin = new Table();
		choicePopin.setTouchable(Touchable.enabled);
		choicePopin.addListener(new ClickListener() {});
				
		// Place the popin and add the background texture
		choicePopin.setPosition(GameScreen.SCREEN_W/2, GameScreen.SCREEN_H/2);		
		NinePatchDrawable ninePatchDrawable = new NinePatchDrawable(SceneAssets.popinNinePatch);
		choicePopin.setBackground(ninePatchDrawable);
		
		choicePopin.align(Align.top);

		Label title = new Label("Secret door", PopinService.hudStyle());
		choicePopin.add(title).top().align(Align.top).pad(20, 10, 20, 10);
		choicePopin.row().align(Align.center);
		
		desc = new Label("Description", PopinService.hudStyle());
		desc.setWrap(true);
		choicePopin.add(desc).growY().width(900).left().pad(0, 20, 0, 20);;
		choicePopin.row().align(Align.center);		
		
		choicePopinSubTable = new Table();
		choicePopin.add(choicePopinSubTable).pad(20, 10, 20, 10);
		choicePopin.row().align(Align.center);		

		// Close button
		final TextButton closeBtn = new TextButton("Close",PopinService.buttonStyle());			
		// Close listener
		closeBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				closePopin();
			}
		});
		choicePopin.add(closeBtn).pad(0, 20,0,20);

	}
	
	

	/**
	 * Close the popin and unpause the game.
	 */
	private void closePopin() {
		choicePopin.remove();
		
		if (room.getNextState() == null) {
			room.setNextState(room.getLastInGameState());
		}
	}

}
