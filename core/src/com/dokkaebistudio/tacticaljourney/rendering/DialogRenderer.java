package com.dokkaebistudio.tacticaljourney.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.assets.SceneAssets;
import com.dokkaebistudio.tacticaljourney.dialog.Dialog;
import com.dokkaebistudio.tacticaljourney.rendering.interfaces.Renderer;
import com.dokkaebistudio.tacticaljourney.rendering.service.PopinService;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;

public class DialogRenderer implements Renderer, RoomSystem {
	    
	public Stage stage;
	
	/** The current room. */
    private Room room;
	private Dialog currentDialog;
        
    //**************************
    // Actors
    
    private Table mainPopin;
    private Label speaker;
    private Label content;
    private Label nextOrClose;

    
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
    	
    	if (currentDialog.duration == 0) {
			if (mainPopin == null) {
				initTable();
			}
			
			updateContent();
			
			// Place the popin properly
			mainPopin.pack();
			mainPopin.setPosition(GameScreen.SCREEN_W/2 - mainPopin.getWidth()/2, 200);

			this.stage.addActor(mainPopin);
    	}
    	
    	currentDialog.duration += deltaTime;

    	
    	if (currentDialog.duration >= 0.3f && room.isCloseDialogRequested()) {
			
			if (currentDialog.getCurrentIndex() == currentDialog.getText().size() - 1) {
				closePopin();
				return;
			} else {
				currentDialog.incrementIndex();
				currentDialog.duration = 0;
				room.setCloseDialogRequested(false);
			}
			
		}
    	
    	
		// Draw the table
        stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	private void updateContent() {
		speaker.setText(currentDialog.getSpeaker());
		content.setText(currentDialog.getText().get(currentDialog.getCurrentIndex()));
		nextOrClose.setText(currentDialog.hasNextLine() ? "Next" : "Close");
	}

	
    

    /**
     * Initialize the popin table (only the first time it is displayed).
     */
	private void initTable() {
		if (mainPopin == null) {
			mainPopin = new Table();
		}
//			mainPopin.setDebug(true);
		
		// Place the popin and add the background texture
		mainPopin.setPosition(GameScreen.SCREEN_W/2, GameScreen.SCREEN_H/2);
		NinePatchDrawable ninePatchDrawable = new NinePatchDrawable(SceneAssets.popinNinePatch);
		ninePatchDrawable.setMinHeight(200);
		mainPopin.setBackground(ninePatchDrawable);
		
		mainPopin.align(Align.top);
		
		// 1 - Title
		Table speakerTable = new Table();
		ninePatchDrawable = new NinePatchDrawable(SceneAssets.popinNinePatch);
		speakerTable.setBackground(ninePatchDrawable);
		speaker = new Label("Title", PopinService.hudStyle());
		speakerTable.add(speaker);
		mainPopin.add(speakerTable).top().left().align(Align.left).pad(-17, -17, 10, 0);
		mainPopin.row().align(Align.center);
		
		// 2 - Description
		content = new Label("Description", PopinService.hudStyle());
		content.setWrap(true);		
		mainPopin.add(content).growY().width(900).left().pad(0, 20, 0, 20);
		mainPopin.row().align(Align.right);

		// 3 - Click to close
		nextOrClose = new Label("Click anywhere to close", PopinService.smallTextStyle());
		mainPopin.add(nextOrClose).right().pad(0, 0, 5, 5);
	}

	
	

	/**
	 * Close the popin and unpause the game.
	 */
	private void closePopin() {
		mainPopin.remove();
		
		if (currentDialog != null) {
			room.removeDialog();
			currentDialog.duration = 0;
			currentDialog = null;
		}
	}

}
