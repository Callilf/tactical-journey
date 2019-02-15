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
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent.InventoryActionEnum;
import com.dokkaebistudio.tacticaljourney.rendering.interfaces.Renderer;
import com.dokkaebistudio.tacticaljourney.rendering.service.PopinService;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class ItemPopinRenderer implements Renderer, RoomSystem {
	    
	public Stage stage;
	
	private Entity player;
	
	/** The inventory component of the player (kept in cache to prevent getting it at each frame). */
	private InventoryComponent playerInventoryCompo;
	private ItemComponent itemComponent;

	
	/** The current room. */
    private Room room;
    
    private boolean isShop;
    
    
    //**************************
    // Actors
    
    private Table selectedItemPopin;
    private Label itemTitle;
    private Label itemDesc;
    private TextButton pickupItemBtn;
    private ChangeListener pickupListener;
    private ChangeListener buyListener;
    private TextButton useItemBtn;
    private ChangeListener useListener;

    
    
    /** The state before the level up state. */
    private RoomState previousState;
    
    public ItemPopinRenderer(Room r, Stage s, Entity p) {
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
    	
    	if (playerInventoryCompo == null) {
    		playerInventoryCompo = Mappers.inventoryComponent.get(player);
    	}
    	
    	if (playerInventoryCompo.getCurrentAction() == InventoryActionEnum.DISPLAY_POPIN) {
    		previousState = room.getNextState() != null ? room.getNextState() : room.getState();
    		room.setNextState(RoomState.ITEM_POPIN);
    		
			itemComponent = Mappers.itemComponent.get(playerInventoryCompo.getCurrentItem());
			isShop = itemComponent.getPrice() != null;

			if (selectedItemPopin == null) {
				initTable();
			}
			
			refreshTable();
		
			this.stage.addActor(selectedItemPopin);
			
			playerInventoryCompo.clearCurrentAction();
    	}
    	
    	
    	if (room.getState() == RoomState.ITEM_POPIN) {
    		// Draw the table
            stage.act(Gdx.graphics.getDeltaTime());
    		stage.draw();
    		
    		// Close the inventory on a left click outside the popin
    		if (InputSingleton.getInstance().leftClickJustPressed) {
    			closePopin();
    		}
    	}
	}

	private void refreshTable() {
		Entity item = playerInventoryCompo.getCurrentItem();
		
		// Update the content
		itemTitle.setText(itemComponent.getItemLabel());
		if (itemComponent.getPrice() != null) {
			itemTitle.setText(itemTitle.getText() + " ([GOLD]" + itemComponent.getPrice() + "coins[WHITE])");
		}
		if (itemComponent.getItemDescription() != null) {
			itemDesc.setText(itemComponent.getItemDescription());
		}

		if (isShop) {
			pickupItemBtn.setText("Buy");
			// Update the Drop item listener
			updateBuyListener(item, itemComponent);

			useItemBtn.setVisible(false);
		} else {
			pickupItemBtn.setText("Take");

			if (itemComponent.getItemActionLabel() != null) {
				useItemBtn.setVisible(true);
				useItemBtn.setText(itemComponent.getItemActionLabel());
			} else {
				useItemBtn.setVisible(false);
			}
			
			// Update the Drop item listener
			updatePickupListener(item, itemComponent);
			
			if (itemComponent.getItemActionLabel() != null) {
				// Update the Use item listener
				updateUseListener(item);
			}
		}

		
		// Place the popin properly
		selectedItemPopin.pack();
		selectedItemPopin.setPosition(GameScreen.SCREEN_W/2 - selectedItemPopin.getWidth()/2, GameScreen.SCREEN_H/2 - selectedItemPopin.getHeight()/2);
	}
    

    /**
     * Initialize the popin table (only the first time it is displayed).
     */
	private void initTable() {
		selectedItemPopin = new Table();
//		selectedItemPopin.setDebug(true);

		// Add an empty click listener to capture the click so that the InputSingleton doesn't handle it
		selectedItemPopin.setTouchable(Touchable.enabled);
		selectedItemPopin.addListener(new ClickListener() {});
		
		// Place the popin and add the background texture
		selectedItemPopin.setPosition(GameScreen.SCREEN_W/2, GameScreen.SCREEN_H/2);
		TextureRegionDrawable textureRegionDrawable = new TextureRegionDrawable(Assets.inventory_item_popin_background);
		selectedItemPopin.setBackground(textureRegionDrawable);
		
		selectedItemPopin.align(Align.top);
		
		// 1 - Title
		itemTitle = new Label("Title", PopinService.hudStyle());
		selectedItemPopin.add(itemTitle).top().align(Align.top).pad(20, 0, 20, 0);
		selectedItemPopin.row().align(Align.center);
		
		// 2 - Description
		if (itemComponent.getItemDescription() != null) {
			itemDesc = new Label("Desc", PopinService.hudStyle());
			itemDesc.setWrap(true);
			selectedItemPopin.add(itemDesc).growY().width(textureRegionDrawable.getMinWidth()).left().pad(0, 20, 0, 20);
			selectedItemPopin.row();
		}
		
		// 3 - Action buttons
		Table buttonTable = new Table();
		
		// 3.1 - Close button
		final TextButton closeBtn = new TextButton("Close", PopinService.bigButtonStyle());			
		// continueButton listener
		closeBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				closePopin();
			}
		});
		buttonTable.add(closeBtn).pad(0, 20,0,20);

		// 3.2 - Take button
		pickupItemBtn = new TextButton("Take", PopinService.bigButtonStyle());			
		buttonTable.add(pickupItemBtn).pad(0, 20,0,20);

		// 3.3 - Use button
		useItemBtn = new TextButton("Use", PopinService.bigButtonStyle());			
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

	private void updateBuyListener(final Entity item, final ItemComponent itemComponent) {
		if (buyListener != null) {
			pickupItemBtn.removeListener(buyListener);
		}
		buyListener = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				playerInventoryCompo.requestAction(InventoryActionEnum.BUY, item);
				closePopin();
			}
		};
		pickupItemBtn.addListener(buyListener);
	}

	/**
	 * Close the popin and unpause the game.
	 */
	private void closePopin() {
		selectedItemPopin.remove();
		
		if (room.getNextState() == null) {
			room.setNextState(previousState);
		}
	}

}
