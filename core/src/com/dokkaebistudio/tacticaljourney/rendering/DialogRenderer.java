package com.dokkaebistudio.tacticaljourney.rendering;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.components.DialogComponent;
import com.dokkaebistudio.tacticaljourney.rendering.interfaces.Renderer;
import com.dokkaebistudio.tacticaljourney.rendering.service.PopinService;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class DialogRenderer implements Renderer, RoomSystem {
	    
	public Stage stage;
	
	/** The current room. */
    private Room room;
	private Entity currentDialog;
        
    //**************************
    // Actors
    
    private Table mainPopin;
    private Label speaker;
    private Label content;

    
    public DialogRenderer(Room r, Stage s) {
        this.room = r;
        this.stage = s;
    }
    
    @Override
    public void enterRoom(Room newRoom) {
    	this.room = newRoom;	
    }


    @Override
    public void render(float deltaTime) {
    	if (room.getDialog() == null) {
    		if (currentDialog != null) closePopin();
    		return;
    	}
    	
    	currentDialog = room.getDialog();
    	DialogComponent dialogComponent = Mappers.dialogComponent.get(currentDialog);
    	
    	if (dialogComponent.getCurrentDuration() == 0) {
			if (mainPopin == null) {
				initTable();
			}
			
			updateContent(dialogComponent);
			
			// Place the popin properly
			mainPopin.pack();
			mainPopin.setPosition(GameScreen.SCREEN_W/2 - mainPopin.getWidth()/2, 200);

			this.stage.addActor(mainPopin);
    	} else {
    		
    		if (dialogComponent.getCurrentDuration() >= dialogComponent.getDuration()) {
    			closePopin();
    			return;
    		}
    		
    	}
		dialogComponent.setCurrentDuration(dialogComponent.getCurrentDuration() + deltaTime);

    	
    	
		// Draw the table
        stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	private void updateContent(DialogComponent dialogComponent) {
		speaker.setText(dialogComponent.getSpeaker());
		content.setText(dialogComponent.getText());
	}

	
    

    /**
     * Initialize the popin table (only the first time it is displayed).
     */
	private void initTable() {
		if (mainPopin == null) {
			mainPopin = new Table();
		}
//			mainPopin.setDebug(true);

		// Add an empty click listener to capture the click so that the InputSingleton doesn't handle it
		mainPopin.setTouchable(Touchable.enabled);
		mainPopin.addListener(new ClickListener() {});
		
		// Place the popin and add the background texture
		mainPopin.setPosition(GameScreen.SCREEN_W/2, GameScreen.SCREEN_H/2);
		NinePatchDrawable ninePatchDrawable = new NinePatchDrawable(Assets.popinNinePatch);
		ninePatchDrawable.setMinHeight(200);
		mainPopin.setBackground(ninePatchDrawable);
		
		mainPopin.align(Align.top);
		
		// 1 - Title
		Table speakerTable = new Table();
		ninePatchDrawable = new NinePatchDrawable(Assets.popinNinePatch);
		speakerTable.setBackground(ninePatchDrawable);
		speaker = new Label("Title", PopinService.hudStyle());
		speakerTable.add(speaker);
		mainPopin.add(speakerTable).top().left().align(Align.left).pad(-17, -17, 10, 0);
		mainPopin.row().align(Align.center);
		
		// 2 - Description
		content = new Label("Description", PopinService.hudStyle());
		content.setWrap(true);
		mainPopin.add(content).growY().width(600).left().pad(0, 20, 0, 20);
	}

	
	

	/**
	 * Close the popin and unpause the game.
	 */
	private void closePopin() {
		mainPopin.remove();
		
		if (currentDialog != null) {
			DialogComponent dialogComponent = Mappers.dialogComponent.get(currentDialog);
			dialogComponent.getRoom().removeDialog();
			currentDialog = null;
		}
	}

}
