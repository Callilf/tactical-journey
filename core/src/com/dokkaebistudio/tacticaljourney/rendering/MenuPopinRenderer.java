package com.dokkaebistudio.tacticaljourney.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameScreen;

public class MenuPopinRenderer implements Renderer {
	    
	public GameScreen gamescreen;
	public Stage stage;
    
	boolean menuDisplayed = false;
    private Table table;    
    
	private LabelStyle hudStyle;
        
    public MenuPopinRenderer(GameScreen gs, Stage s) {
    	this.gamescreen = gs;
        this.stage = s;
        
		hudStyle = new LabelStyle(Assets.font, Color.WHITE);
    }



    @Override
    public void render(float deltaTime) {
    	
    	if (gamescreen.state == GameScreen.GAME_PAUSED) {
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
			table = new Table();
	//			selectedItemPopin.setDebug(true);
	
			// Add an empty click listener to capture the click so that the InputSingleton doesn't handle it
			table.setTouchable(Touchable.enabled);
			table.addListener(new ClickListener() {});
			
			// Place the popin and add the background texture
			table.setPosition(GameScreen.SCREEN_W/2, GameScreen.SCREEN_H/2);
			TextureRegionDrawable textureRegionDrawable = new TextureRegionDrawable(Assets.getTexture(Assets.profile_background));
			table.setBackground(textureRegionDrawable);
			
			table.align(Align.top);
			
			// 1 - Title
			Label title = new Label("Game paused", hudStyle);
			table.add(title).top().align(Align.top).pad(20, 0, 60, 0);
			table.row().align(Align.center);
			
	
			// 2 - Resume button
			Drawable btnUp = new SpriteDrawable(new Sprite(Assets.getTexture(Assets.inventory_item_popin_btn_up)));
			Drawable btnDown = new SpriteDrawable(new Sprite(Assets.getTexture(Assets.inventory_item_popin_btn_down)));
			TextButtonStyle btnStyle = new TextButtonStyle(btnUp, btnDown, null, Assets.font);
			
			final TextButton resumeBtn = new TextButton("Resume",btnStyle);			
			resumeBtn.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					closePopin();
				}
			});
			table.add(resumeBtn).padBottom(20);
			table.row();
			
			// 3 - Return to menu
			final TextButton mainMenuBtn = new TextButton("Main menu",btnStyle);			
			mainMenuBtn.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					//TODO go back to the main menu
					closePopin();
				}
			});
			table.add(mainMenuBtn).padBottom(20);
			table.row();
			
			// 4 - Quit game
			final TextButton quitBtn = new TextButton("Quit game",btnStyle);			
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
			
			table.add(quitBtn).padBottom(20);
			
			
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
