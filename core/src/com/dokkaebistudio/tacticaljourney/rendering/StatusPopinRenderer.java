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
import com.dokkaebistudio.tacticaljourney.assets.SceneAssets;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.rendering.interfaces.Renderer;
import com.dokkaebistudio.tacticaljourney.rendering.service.PopinService;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.singletons.InputSingleton;
import com.dokkaebistudio.tacticaljourney.statuses.Status;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;

public class StatusPopinRenderer implements Renderer, RoomSystem {
	    
	public Stage stage;
		
	/** The current room. */
    private Room room;
    
    public static Status status;
    
    private RoomState previousState;
    
        
    //**************************
    // Actors
    
    private Table mainPopin;
    private Label title;
    private Label desc;
        
        
    public StatusPopinRenderer(Room r, Stage s) {
        this.room = r;
        this.stage = s;
    }
    
    @Override
    public void enterRoom(Room newRoom) {
    	this.room = newRoom;	
    }


    @Override
    public void render(float deltaTime) {
    	
    	if (status != null && room.getState() != RoomState.STATUS_POPIN) {
    		previousState = room.getNextState() != null ? room.getNextState() : room.getState();
    		room.setNextState(RoomState.STATUS_POPIN);

			if (mainPopin == null) {
				initTable();
				
    			// Close popin with ESCAPE
	    		stage.addListener(new InputListener() {
					@Override
					public boolean keyUp(InputEvent event, int keycode) {
						if (room.getState() == RoomState.STATUS_POPIN && (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK)) {
							closePopin();
							return true;
						}
						return super.keyUp(event, keycode);
					}
				});
			}
			
			updateContent();
			
			// Place the popin properly
			mainPopin.pack();
			mainPopin.setPosition(GameScreen.SCREEN_W/2 - mainPopin.getWidth()/2, GameScreen.SCREEN_H/2 - mainPopin.getHeight()/2);

		
			this.stage.addActor(mainPopin);
			
    	}
    	
    	if (room.getState() == RoomState.STATUS_POPIN) {
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
		mainPopin.setVisible(true);
		title.setText(status.title());
		desc.setText(status.description());
	}
	
	
	
    

    /**
     * Initialize the popin table (only the first time it is displayed).
     */
	private void initTable() {
		if (mainPopin == null) {
			mainPopin = new Table();
		}
//			selectedItemPopin.setDebug(true);

		// Add an empty click listener to capture the click so that the InputSingleton doesn't handle it
		mainPopin.setTouchable(Touchable.enabled);
		mainPopin.addListener(new ClickListener() {});
		
		// Place the popin and add the background texture
		mainPopin.setPosition(GameScreen.SCREEN_W/2, GameScreen.SCREEN_H/2);
		NinePatchDrawable ninePatchDrawable = new NinePatchDrawable(SceneAssets.popinNinePatch);
		mainPopin.setBackground(ninePatchDrawable);
		
		mainPopin.align(Align.top);
		
		// 1 - Title
		title = new Label("Title", PopinService.hudStyle());
		mainPopin.add(title).top().align(Align.top).pad(20, 0, 20, 0);
		mainPopin.row().align(Align.center);
		
		// 2 - Description
		desc = new Label("Description", PopinService.hudStyle());
		desc.setWrap(true);
		mainPopin.add(desc).growY().width(500).left().pad(0, 20, 0, 20);
		mainPopin.row();
		
		// 3 - Action buttons
		Table buttonTable = new Table();
		final TextButton closeBtn = new TextButton("Close",PopinService.buttonStyle());			
		// Close listener
		closeBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				closePopin();
			}
		});
		buttonTable.add(closeBtn).pad(0, 20,0,20);
		
		mainPopin.add(buttonTable).pad(20, 0, 20, 0);
	}


	/**
	 * Close the popin and unpause the game.
	 */
	private void closePopin() {
		mainPopin.remove();
		status = null;
		
		room.setNextState(previousState);
	}

}
