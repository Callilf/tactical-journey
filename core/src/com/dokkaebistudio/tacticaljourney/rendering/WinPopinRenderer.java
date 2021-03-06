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
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.dokkaebistudio.tacticaljourney.assets.SceneAssets;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.persistence.GameStatistics;
import com.dokkaebistudio.tacticaljourney.persistence.GameStatistics.GameStatisticsState;
import com.dokkaebistudio.tacticaljourney.persistence.Rankings;
import com.dokkaebistudio.tacticaljourney.rendering.interfaces.Renderer;
import com.dokkaebistudio.tacticaljourney.rendering.service.PopinService;

public class WinPopinRenderer implements Renderer {
	    
	public GameScreen gamescreen;
	public Stage stage;
    
	boolean menuDisplayed = false;
    private Table table;    
            
    public WinPopinRenderer(GameScreen gs, Stage s) {
    	this.gamescreen = gs;
        this.stage = s;
    }



    @Override
    public void render(float deltaTime) {
    	
    	if (gamescreen.state == GameScreen.GAME_COMPLETED) {
    		RoomRenderer.showBlackFilter();
    		
    		if (!menuDisplayed) {
				initTable();
				menuDisplayed = true;
				this.stage.addActor(table);
				
				// Add game stats to rankings
				GameStatistics gameStats = GameStatistics.create(this.gamescreen);
				gameStats.setState(GameStatisticsState.WON);
				Rankings.addGameToRankings(gameStats);
    		}
    		
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
			table = new Table();
	//			selectedItemPopin.setDebug(true);
	
			// Add an empty click listener to capture the click so that the InputSingleton doesn't handle it
			table.setTouchable(Touchable.enabled);
			table.addListener(new ClickListener() {});
			
			// Place the popin and add the background texture
			table.setPosition(GameScreen.SCREEN_W/2, GameScreen.SCREEN_H/2);
			NinePatchDrawable ninePatchDrawable = new NinePatchDrawable(SceneAssets.popinNinePatch);
			table.setBackground(ninePatchDrawable);
			
			table.align(Align.top);
			
			// 1 - Title
			Label title = new Label("[GREEN]VICTORY", PopinService.hudStyle());
			table.add(title).top().align(Align.top).pad(20, 0, 60, 0);
			table.row();
			
			// 2 - Desc
			Label desc = new Label("Congratulations! You managed to survive the dungeon and claim the universal cure.", PopinService.hudStyle());
			desc.setWrap(true);
			table.add(desc).growY().width(900).left().pad(20, 0, 60, 0);
			table.row();		
			
			// 3 - Action buttons
			Table buttonTable = new Table();

			// 3.1 - Main menu button
			final TextButton mainMenuBtn = new TextButton("Main menu", PopinService.buttonStyle());			
			mainMenuBtn.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					gamescreen.backToMenu();
				}
			});
			buttonTable.add(mainMenuBtn).pad(0, 20,0,20);
			
			
			table.add(buttonTable).padBottom(20);
			
			
			// Place the popin properly
			table.pack();
			table.setPosition(GameScreen.SCREEN_W/2 - table.getWidth()/2, GameScreen.SCREEN_H/2 - table.getHeight()/2);
		
		}
	}

	/**
	 * Close the popin and unpause the game.
	 */
	private void closePopin() {
		RoomRenderer.hideBlackFilter();

		table.remove();
		menuDisplayed = false;
		
		gamescreen.state = GameScreen.GAME_RUNNING;
	}

}
