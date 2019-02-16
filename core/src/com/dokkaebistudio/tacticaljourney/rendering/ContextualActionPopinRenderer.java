package com.dokkaebistudio.tacticaljourney.rendering;

import com.badlogic.ashley.core.Entity;
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
import com.dokkaebistudio.tacticaljourney.InputSingleton;
import com.dokkaebistudio.tacticaljourney.components.loot.LootableComponent;
import com.dokkaebistudio.tacticaljourney.components.loot.LootableComponent.LootableStateEnum;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.components.transition.ExitComponent;
import com.dokkaebistudio.tacticaljourney.rendering.interfaces.Renderer;
import com.dokkaebistudio.tacticaljourney.rendering.service.PopinService;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class ContextualActionPopinRenderer implements Renderer, RoomSystem {
	    
	public Stage stage;
	
	private Entity player;
	
	/** The player component (kept in cache to prevent getting it at each frame). */
	private PlayerComponent playerCompo;
	
	/** The current room. */
    private Room room;
        
    //**************************
    // Actors
    
    private Table mainPopin;
    private Label title;
    private Label desc;
    private TextButton yesBtn;
    private ChangeListener yesBtnListener;
    
    
    /** The state before the level up state. */
    private RoomState previousState;
    
    public ContextualActionPopinRenderer(Room r, Stage s, Entity p) {
        this.room = r;
        this.player = p;
        this.stage = s;
    }
    
    @Override
    public void enterRoom(Room newRoom) {
    	this.room = newRoom;	
    }


    @Override
    public void render(float deltaTime) {
    	
    	if (playerCompo == null) {
    		playerCompo = Mappers.playerComponent.get(player);
    	}
    	
    	if (playerCompo.isLootRequested() || playerCompo.isExitRequested()) {
    		previousState = room.getNextState() != null ? room.getNextState() : room.getState();
    		room.setNextState(RoomState.CONTEXTUAL_ACTION_POPIN);

			if (mainPopin == null) {
				initTable();
			}
			
			updateContentForAction();
			
			// Place the popin properly
			mainPopin.pack();
			mainPopin.setPosition(GameScreen.SCREEN_W/2 - mainPopin.getWidth()/2, GameScreen.SCREEN_H/2 - mainPopin.getHeight()/2);
		
			this.stage.addActor(mainPopin);
			
			playerCompo.clearLootRequested();
			playerCompo.clearExitRequested();
    	}
    	
    	if (room.getState() == RoomState.CONTEXTUAL_ACTION_POPIN) {
    		// Draw the table
            stage.act(Gdx.graphics.getDeltaTime());
    		stage.draw();
    		
    		// Close the inventory on a left click outside the popin
    		if (InputSingleton.getInstance().leftClickJustPressed) {
    			closePopin();
    		}
    	}
	}

	private void updateContentForAction() {
		
		if (playerCompo.isLootRequested()) {
			Entity lootableEntity = playerCompo.getLootableEntity();
			LootableComponent lootableComponent = Mappers.lootableComponent.get(lootableEntity);
			// Update the content
			title.setText(lootableComponent.getType().getLabel());
			desc.setText(lootableComponent.getType().getDescription());
			
			if (lootableComponent.getLootableState() == LootableStateEnum.CLOSED) {
				desc.setText(desc.getText() + "\n" + "It will take you [RED]" + lootableComponent.getType().getNbTurnsToOpen() + "[WHITE] turns to open it.");
			} else {
				desc.setText(desc.getText() + "\n" + "It is already [GREEN]opened[WHITE]. It won't take any turn.");
			}
			yesBtn.setText("Open");
	
			// Update the Use item listener
			updateLootListener(lootableEntity, lootableComponent);
			
		} else if (playerCompo.isExitRequested()) {
			Entity exitEntity = playerCompo.getExitEntity();
			ExitComponent exitComponent = Mappers.exitComponent.get(exitEntity);
			title.setText("Doorway to lower floor");
			desc.setText("Congratulations, you reached the exit of the first floor. There will be many floors to explore later, unfortunately at the moment the doorway is stuck. Please come back after a few months."
					+ "\nSadness galore.");
			yesBtn.setText("Wait for months");
			if (yesBtnListener != null) {
				yesBtn.removeListener(yesBtnListener);
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
		TextureRegionDrawable textureRegionDrawable = new TextureRegionDrawable(Assets.inventory_item_popin_background);
		mainPopin.setBackground(textureRegionDrawable);
		
		mainPopin.align(Align.top);
		
		// 1 - Title
		title = new Label("Title", PopinService.hudStyle());
		mainPopin.add(title).top().align(Align.top).pad(20, 0, 20, 0);
		mainPopin.row().align(Align.center);
		
		// 2 - Description
		desc = new Label("Description", PopinService.hudStyle());
		desc.setWrap(true);
		mainPopin.add(desc).growY().width(textureRegionDrawable.getMinWidth()).left().pad(0, 20, 0, 20);
		mainPopin.row();
		
		// 4 - Action buttons
		Table buttonTable = new Table();
		
		// 4.1 - No button
		final TextButton closeBtn = new TextButton("Close",PopinService.bigButtonStyle());			
		// Close listener
		closeBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				closePopin();
			}
		});
		buttonTable.add(closeBtn).pad(0, 20,0,20);

		// 4.2 - Yes button
		yesBtn = new TextButton("Loot",PopinService.bigButtonStyle());			
		buttonTable.add(yesBtn).pad(0, 20,0,20);
		
		mainPopin.add(buttonTable).pad(20, 0, 20, 0);
	}

	private void updateLootListener(final Entity item, final LootableComponent lootableComponent) {
		if (yesBtnListener != null) {
			yesBtn.removeListener(yesBtnListener);
		}
		yesBtnListener = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(player);
				inventoryComponent.setTurnsToWaitBeforeLooting(lootableComponent.getNbTurnsToOpen());
				inventoryComponent.setLootableEntity(playerCompo.getLootableEntity());
				closePopin();
			}
		};
		yesBtn.addListener(yesBtnListener);
	}


	/**
	 * Close the popin and unpause the game.
	 */
	private void closePopin() {
		mainPopin.remove();
		
		if (room.getNextState() == null) {
			room.setNextState(previousState);
		}
	}

}
