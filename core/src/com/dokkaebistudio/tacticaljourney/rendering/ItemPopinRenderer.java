package com.dokkaebistudio.tacticaljourney.rendering;

import com.badlogic.ashley.core.Entity;
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
import com.dokkaebistudio.tacticaljourney.InputSingleton;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent.InventoryActionEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class ItemPopinRenderer implements Renderer, RoomSystem {
	    
	public Stage stage;
	
	private Entity player;
	
	/** The inventory component of the player (kept in cache to prevent getting it at each frame). */
	private InventoryComponent playerInventoryCompo;
	
	/** The current room. */
    private Room room;
    
    
    boolean itemPopinDisplayed = false;
    private Table selectedItemPopin;
    private Label itemTitle;
    private Label itemDesc;
    private TextButton pickupItemBtn;
    private ChangeListener pickupListener;
    private TextButton useItemBtn;
    private ChangeListener useListener;
    
    
	private LabelStyle hudStyle;
    
    /** The state before the level up state. */
    private RoomState previousState;
    
    public ItemPopinRenderer(Room r, Stage s, Entity p) {
        this.room = r;
        this.player = p;
        this.stage = s;
        
		hudStyle = new LabelStyle(Assets.font, Color.WHITE);

    }
    
    @Override
    public void enterRoom(Room newRoom) {
    	this.room = newRoom;	
    }


    @Override
    public void render(float deltaTime) {
    	
    	if (playerInventoryCompo == null) {
    		playerInventoryCompo = Mappers.inventoryComponent.get(player);
    	}
    	
    	if (playerInventoryCompo.getCurrentAction() == InventoryActionEnum.DISPLAY_POPIN) {
    		previousState = room.getNextState() != null ? room.getNextState() : room.getState();
    		room.setNextState(RoomState.ITEM_POPIN);

			initTable();
			
			Entity item = playerInventoryCompo.getCurrentItem();
			ItemComponent itemComponent = Mappers.itemComponent.get(playerInventoryCompo.getCurrentItem());
			
			// Update the content
			itemTitle.setText(itemComponent.getItemType().getLabel());
			itemDesc.setText(itemComponent.getItemType().getDescription());
			useItemBtn.setText(itemComponent.getItemType().getActionLabel());
			
			// Update the Drop item listener
			updatePickupListener(item, itemComponent);
			
			// Update the Use item listener
			updateUseListener(item);
	
			
			// Place the popin properly
			selectedItemPopin.pack();
			selectedItemPopin.setPosition(GameScreen.SCREEN_W/2 - selectedItemPopin.getWidth()/2, GameScreen.SCREEN_H/2 - selectedItemPopin.getHeight()/2);
		
			itemPopinDisplayed = true;
			this.stage.addActor(selectedItemPopin);
			
			playerInventoryCompo.clearCurrentAction();
    	}
    	
    	if (playerInventoryCompo.getCurrentAction() == InventoryActionEnum.DISPLAY_POPIN || room.getState() == RoomState.ITEM_POPIN) {
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
		if (selectedItemPopin == null) {
			selectedItemPopin = new Table();
		}
//			selectedItemPopin.setDebug(true);

		// Add an empty click listener to capture the click so that the InputSingleton doesn't handle it
		selectedItemPopin.setTouchable(Touchable.enabled);
		selectedItemPopin.addListener(new ClickListener() {});
		
		// Place the popin and add the background texture
		selectedItemPopin.setPosition(GameScreen.SCREEN_W/2, GameScreen.SCREEN_H/2);
		TextureRegionDrawable textureRegionDrawable = new TextureRegionDrawable(Assets.getTexture(Assets.inventory_item_popin_background));
		selectedItemPopin.setBackground(textureRegionDrawable);
		
		selectedItemPopin.align(Align.top);
		
		// 1 - Title
		itemTitle = new Label("Title", hudStyle);
		selectedItemPopin.add(itemTitle).top().align(Align.top).pad(20, 0, 20, 0);
		selectedItemPopin.row().align(Align.center);
		
		// 2 - Description
		itemDesc = new Label("Un test de description d'idem qui est assez long pour voir jusqu'ou on peut aller. Un test de description d'idem qui est assez long pour voir jusqu'ou on peut aller. Un test de description d'idem qui est assez long pour voir jusqu'ou on peut aller.", hudStyle);
		itemDesc.setWrap(true);
		selectedItemPopin.add(itemDesc).growY().width(textureRegionDrawable.getMinWidth()).left().pad(0, 20, 0, 20);
		selectedItemPopin.row();
		
		// 3 - Action buttons
		Table buttonTable = new Table();
		Drawable btnUp = new SpriteDrawable(new Sprite(Assets.getTexture(Assets.inventory_item_popin_btn_up)));
		Drawable btnDown = new SpriteDrawable(new Sprite(Assets.getTexture(Assets.inventory_item_popin_btn_down)));
		TextButtonStyle btnStyle = new TextButtonStyle(btnUp, btnDown, null, Assets.font);
		
		// 3.1 - Close button
		final TextButton closeBtn = new TextButton("Close",btnStyle);			
		// continueButton listener
		closeBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				closePopin();
			}
		});
		buttonTable.add(closeBtn).pad(0, 20,0,20);
		
		// 3.2 - Drop button
		pickupItemBtn = new TextButton("Take",btnStyle);			
		buttonTable.add(pickupItemBtn).pad(0, 20,0,20);

		// 3.3 - Use button
		useItemBtn = new TextButton("Use",btnStyle);			
		buttonTable.add(useItemBtn).pad(0, 20,0,20);
		
		selectedItemPopin.add(buttonTable).pad(20, 0, 20, 0);
	}

	private void updateUseListener(final Entity item) {
		if (useListener != null) {
			useItemBtn.removeListener(useListener);
		}
		useListener = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				playerInventoryCompo.requestAction(InventoryActionEnum.PICKUP_AND_USE, item);
				closePopin();
			}
		};
		useItemBtn.addListener(useListener);
	}

	private void updatePickupListener(final Entity item, final ItemComponent itemComponent) {
		if (pickupListener != null) {
			pickupItemBtn.removeListener(pickupListener);
		}
		pickupListener = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				playerInventoryCompo.requestAction(InventoryActionEnum.PICKUP, item);
				closePopin();
			}
		};
		pickupItemBtn.addListener(pickupListener);
	}


	/**
	 * Close the popin and unpause the game.
	 */
	private void closePopin() {
		selectedItemPopin.remove();
		selectedItemPopin.clear();
		
		if (room.getNextState() == null) {
			room.setNextState(previousState);
		}
	}

}
