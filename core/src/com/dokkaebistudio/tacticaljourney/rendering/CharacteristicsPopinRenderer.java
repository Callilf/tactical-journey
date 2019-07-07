package com.dokkaebistudio.tacticaljourney.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
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
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;

public class CharacteristicsPopinRenderer implements Renderer, RoomSystem {
	    
	public Stage stage;
		
	/** The current room. */
    private Room room;
        
    private RoomState previousState;
    
    public static boolean display;

    
        
    //**************************
    // Actors
    
    private Table mainPopin;        
        
    public CharacteristicsPopinRenderer(Room r, Stage s) {
        this.room = r;
        this.stage = s;
    }
    
    @Override
    public void enterRoom(Room newRoom) {
    	this.room = newRoom;	
    }


    @Override
    public void render(float deltaTime) {
    	
    	if (display && room.getState() != RoomState.CHARACTERISTICS_POPIN) {
    		previousState = room.getNextState() != null ? room.getNextState() : room.getState();
    		room.setNextState(RoomState.CHARACTERISTICS_POPIN);

			if (mainPopin == null) {
				initTable();
				
    			// Close popin with ESCAPE
	    		stage.addListener(new InputListener() {
					@Override
					public boolean keyUp(InputEvent event, int keycode) {
						if (room.getState() == RoomState.CHARACTERISTICS_POPIN && (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK)) {
							closePopin();
							return true;
						}
						return super.keyUp(event, keycode);
					}
				});
			}
			
			// Place the popin properly
			mainPopin.pack();
			mainPopin.setPosition(GameScreen.SCREEN_W/2 - mainPopin.getWidth()/2, GameScreen.SCREEN_H/2 - mainPopin.getHeight()/2);

		
			this.stage.addActor(mainPopin);
			
    	}
    	
    	if (room.getState() == RoomState.CHARACTERISTICS_POPIN) {
    		// Draw the table
            stage.act(Gdx.graphics.getDeltaTime());
    		stage.draw();
    		
    		// Close the inventory on a left click outside the popin
    		if (InputSingleton.getInstance().leftClickJustPressed) {
    			closePopin();
    		}
    	}
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
		Label title = new Label("Characteristics explanation", PopinService.hudStyle());
		mainPopin.add(title).top().align(Align.top).pad(20, 0, 20, 0);
		mainPopin.row().align(Align.center);
		
		
		// 2 - Explanations
		Table profileInnerTable = new Table();
		
		Label nameLbl = new Label("[GOLDENROD]Name[]: The character's name", 
				PopinService.hudStyle());
		nameLbl.setWrap(true);
		profileInnerTable.add(nameLbl).width(960).left().pad(0, 20, 20, 20);
		profileInnerTable.row();

		Label maxHpLbl = new Label("[GOLDENROD]Max hp[]: The maximum number of hp. Even if you heal, you cannot have more hp", 
				PopinService.hudStyle());
		maxHpLbl.setWrap(true);
		profileInnerTable.add(maxHpLbl).width(960).left().pad(0, 20, 20, 20);
		profileInnerTable.row();
		
		Label maxArmorLbl = new Label("[GOLDENROD]Max armor[]: The maximum number of armor points. If you use an armor item while being at full armor, it will be wasted", 
				PopinService.hudStyle());
		maxArmorLbl.setWrap(true);
		profileInnerTable.add(maxArmorLbl).width(960).left().pad(0, 20, 20, 20);
		profileInnerTable.row();
		
		Label karmaLbl = new Label("[GOLDENROD]Karma[]: A strange characteristic. The higher your karma is, the more likely you are to find items. "
				+ "It also increases the chance of blessing effects to activate and reduces the chance of curses effects to happen", 
				PopinService.hudStyle());
		karmaLbl.setWrap(true);
		profileInnerTable.add(karmaLbl).width(960).left().pad(0, 20, 20, 20);
		profileInnerTable.row();

		Label moveLbl = new Label("[GOLDENROD]Move[]: The number of tiles you can move during one turn", 
				PopinService.hudStyle());
		moveLbl.setWrap(true);
		profileInnerTable.add(moveLbl).width(960).left().pad(0, 20, 20, 20);
		profileInnerTable.row();
		
		Label strengthLbl = new Label("[GOLDENROD]Melee Strength[]: The base amount of damage you deal with the melee attack", 
				PopinService.hudStyle());
		strengthLbl.setWrap(true);
		profileInnerTable.add(strengthLbl).width(960).left().pad(0, 20, 20, 20);
		profileInnerTable.row();
		
		Label accuracyLbl = new Label("[GOLDENROD]Melee accuracy[]: Affects the wheel's arrow spinning velocity when using the melee attack."
				+ " The higher the accuracy is, the slower the arrow will spin", 
				PopinService.hudStyle());
		accuracyLbl.setWrap(true);
		profileInnerTable.add(accuracyLbl).width(960).left().pad(0, 20, 20, 20);
		profileInnerTable.row();
		

		
		Label rangeDistLbl = new Label("[GOLDENROD]Bow range[]: The min-max range at which you can attack with the bow", PopinService.hudStyle());
		rangeDistLbl.setWrap(true);
		profileInnerTable.add(rangeDistLbl).width(960).left().pad(0, 20, 20, 20);		profileInnerTable.row();
		Label rangeStrengthLbl = new Label("[GOLDENROD]Bow damage[]: The base amount of damage you deal with the bow", PopinService.hudStyle());
		rangeStrengthLbl.setWrap(true);
		profileInnerTable.add(rangeStrengthLbl).width(960).left().pad(0, 20, 20, 20);
		profileInnerTable.row();
		Label rangeAccuracyLbl = new Label("[GOLDENROD]Bow accuracy[]: Affects the wheel's arrow spinning velocity when using the bow."
				+ " The higher the accuracy is, the slower the arrow will spin", PopinService.hudStyle());
		rangeAccuracyLbl.setWrap(true);
		profileInnerTable.add(rangeAccuracyLbl).width(960).left().pad(0, 20, 20, 20);
		profileInnerTable.row();
		
		Label bombDistLbl = new Label("[GOLDENROD]Bomb throw range[]: The maximum number of tiles where you can throw a bomb", PopinService.hudStyle());
		bombDistLbl.setWrap(true);
		profileInnerTable.add(bombDistLbl).width(960).left().pad(0, 20, 20, 20);
		profileInnerTable.row();
		Label bombDmg = new Label("[GOLDENROD]Bomb damage[]: The base amount of damage you deal with bombs", PopinService.hudStyle());
		bombDmg.setWrap(true);
		profileInnerTable.add(bombDmg).width(960).left().pad(0, 20, 20, 20);
		profileInnerTable.row();
		
		Label bombDuration = new Label("[GOLDENROD]Bomb fuse duration[]: The number of turns the bombs take to explode", PopinService.hudStyle());
		bombDuration.setWrap(true);
		profileInnerTable.add(bombDuration).width(960).left().pad(0, 20, 20, 20);
		profileInnerTable.row();
		Label bombRadius = new Label("[GOLDENROD]Bomb radius[]: The range of the blast of your bombs, in number of tiles", PopinService.hudStyle());
		bombRadius.setWrap(true);
		profileInnerTable.add(bombRadius).width(960).left().pad(0, 20, 20, 20);
		profileInnerTable.row();
		
		Label resist = new Label("[GOLDENROD]XXX resistance[]: The pourcentage of resistance to a given type of damage."
				+ " When at 100 or above, you are immune to this kind of damage", 
				PopinService.hudStyle());
		resist.setWrap(true);
		profileInnerTable.add(resist).width(960).left().pad(0,20,20,20);
		profileInnerTable.row();
		
		
		
		
		ScrollPane scrollPane = new ScrollPane(profileInnerTable, PopinService.scrollStyle());
		scrollPane.setFadeScrollBars(false);
		mainPopin.add(scrollPane).fill().expand().maxHeight(400).width(1100);
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
		display = false;
		
		room.setNextState(previousState);
	}

}
