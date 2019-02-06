package com.dokkaebistudio.tacticaljourney.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.rendering.interfaces.Renderer;
import com.dokkaebistudio.tacticaljourney.rendering.poolables.PoolableLabel;
import com.dokkaebistudio.tacticaljourney.rendering.poolables.PoolableTable;
import com.dokkaebistudio.tacticaljourney.rendering.poolables.PoolableTextButton;
import com.dokkaebistudio.tacticaljourney.rendering.poolables.PoolableTextureRegionDrawable;
import com.dokkaebistudio.tacticaljourney.rendering.service.PopinService;

public class GameOverPopinRenderer implements Renderer {
	    
	public GameScreen gamescreen;
	public Stage stage;
    
	boolean menuDisplayed = false;
    private Table table;    
            
    public GameOverPopinRenderer(GameScreen gs, Stage s) {
    	this.gamescreen = gs;
        this.stage = s;
    }



    @Override
    public void render(float deltaTime) {
    	
    	if (gamescreen.state == GameScreen.GAME_OVER) {
			initTable();
			menuDisplayed = true;
    	} else if (menuDisplayed) {
    		closePopin();
    	}
    	
    	if (menuDisplayed) {
    		// Draw the table
            stage.act(Gdx.graphics.getDeltaTime());
    		stage.draw();
    	}
    }
    

    /**
     * Initialize the popin table (only the first time it is displayed).
     */
	private void initTable() {
		if (table == null) {
			table = PoolableTable.create();
	//			selectedItemPopin.setDebug(true);
	
			// Add an empty click listener to capture the click so that the InputSingleton doesn't handle it
			table.setTouchable(Touchable.enabled);
			table.addListener(new ClickListener() {});
			
			// Place the popin and add the background texture
			table.setPosition(GameScreen.SCREEN_W/2, GameScreen.SCREEN_H/2);
			TextureRegionDrawable textureRegionDrawable = PoolableTextureRegionDrawable.create(Assets.getTexture(Assets.inventory_item_popin_background));
			table.setBackground(textureRegionDrawable);
			
			table.align(Align.top);
			
			// 1 - Title
			Label title = PoolableLabel.create("GAME OVER", PopinService.hudStyle());
			table.add(title).top().align(Align.top).pad(20, 0, 60, 0);
			table.row();
			
			// 2 - Desc
			Label desc = PoolableLabel.create("You got screwed hard.", PopinService.hudStyle());
			table.add(desc).top().align(Align.left).pad(20, 0, 60, 0);
			table.row();		
			
			// 3 - Action buttons
			Table buttonTable = PoolableTable.create();
			
			// 3.1 - Quit button
			final TextButton quitBtn = PoolableTextButton.create("Quit game", PopinService.bigButtonStyle());			
			quitBtn.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					//Quit the game
					gamescreen.dispose();
		            Gdx.app.exit();;
				}
			});
			buttonTable.add(quitBtn).pad(0, 20,0,20);

			// 3.2 - Main menu button
			final TextButton mainMenuBtn = PoolableTextButton.create("Main menu", PopinService.bigButtonStyle());			
			mainMenuBtn.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					//TODO go back to the main menu
					//For the moment, quit the game
					gamescreen.dispose();
		            Gdx.app.exit();;
				}
			});
			buttonTable.add(mainMenuBtn).pad(0, 20,0,20);
			
			
			table.add(buttonTable).padBottom(20);
			
			
			// Place the popin properly
			table.pack();
			table.setPosition(GameScreen.SCREEN_W/2 - table.getWidth()/2, GameScreen.SCREEN_H/2 - table.getHeight()/2);
		
			this.stage.addActor(table);
		}
	}

	/**
	 * Close the popin and unpause the game.
	 */
	private void closePopin() {
		table.remove();
		table.clear();
		table = null;
		menuDisplayed = false;
		
		gamescreen.state = GameScreen.GAME_RUNNING;
	}

}
