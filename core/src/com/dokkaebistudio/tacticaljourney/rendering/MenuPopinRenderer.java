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
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.enums.InventoryDisplayModeEnum;
import com.dokkaebistudio.tacticaljourney.rendering.interfaces.Renderer;
import com.dokkaebistudio.tacticaljourney.rendering.service.PopinService;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class MenuPopinRenderer implements Renderer {
	    
	public GameScreen gamescreen;
	public Stage stage;
    
	boolean menuDisplayed = false;
    private Table table;    
            
    public MenuPopinRenderer(GameScreen gs, Stage s) {
    	this.gamescreen = gs;
        this.stage = s;
    }



    @Override
    public void render(float deltaTime) {
    	
    	if (gamescreen.state == GameScreen.GAME_PAUSED) {
    		RoomRenderer.showBlackFilter();

    		if (!menuDisplayed) {
    			//Init the menu
    			initTable();
    			this.stage.addActor(table);
				menuDisplayed = true;
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
			
			Table buttonTable = new Table();
	//			selectedItemPopin.setDebug(true);
	
			// Add an empty click listener to capture the click so that the InputSingleton doesn't handle it
			buttonTable.setTouchable(Touchable.enabled);
			buttonTable.addListener(new ClickListener() {});
			
			// Place the popin and add the background texture
			NinePatchDrawable ninePatchDrawable = new NinePatchDrawable(Assets.popinNinePatch);
			ninePatchDrawable.setMinWidth(400);
			buttonTable.setBackground(ninePatchDrawable);
			
			buttonTable.align(Align.top);
			
			// 1 - Title
			Label title = new Label("Game paused", PopinService.hudStyle());
			buttonTable.add(title).top().align(Align.top).pad(20, 0, 60, 0);
			buttonTable.row().align(Align.center);
			
	
			// 2 - Resume button
			
			final TextButton resumeBtn = new TextButton("Resume", PopinService.buttonStyle());			
			resumeBtn.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					closePopin();
				}
			});
			buttonTable.add(resumeBtn).minWidth(200).padBottom(20);
			buttonTable.row();
			
			if (GameScreen.debugMode) {
				final TextButton debugBtn = new TextButton("Debug", PopinService.buttonStyle());			
				debugBtn.addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(gamescreen.player);
						inventoryComponent.setDisplayMode(InventoryDisplayModeEnum.DEBUG);
						closePopin();
					}
				});
				buttonTable.add(debugBtn).minWidth(200).padBottom(20);
				buttonTable.row();
			}
			
			// 3 - Return to menu
			final TextButton mainMenuBtn = new TextButton("Main menu", PopinService.buttonStyle());			
			mainMenuBtn.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					//TODO go back to the main menu
					closePopin();
				}
			});
			buttonTable.add(mainMenuBtn).minWidth(200).padBottom(20);
			buttonTable.row();
			
			// 4 - Quit game
			final TextButton quitBtn = new TextButton("Quit game", PopinService.buttonStyle());			
			quitBtn.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					//Quit the game
					gamescreen.dispose();
		            Gdx.app.exit();;
				}
			});
			
//			stage.addListener(new InputListener() {
//				@Override
//				public boolean keyUp(InputEvent event, int keycode) {
//					if (keycode == Input.Keys.ESCAPE) {
//						gamescreen.state = GameScreen.GAME_RUNNING;
//					}
//					return super.keyUp(event, keycode);
//				}
//			});
			
			buttonTable.add(quitBtn).minWidth(200).padBottom(20);

			// Place the popin properly
			buttonTable.pack();
			table.add(buttonTable).padBottom(50);
			table.row();
			
			Table seedTable = new Table();
			ninePatchDrawable = new NinePatchDrawable(Assets.popinNinePatch);
			ninePatchDrawable.setMinWidth(400);
			seedTable.setBackground(ninePatchDrawable);
			Label seed = new Label("Seed: " + RandomSingleton.getInstance().getSeed(), PopinService.hudStyle());
			seedTable.add(seed);
			table.add(seedTable);
			
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
