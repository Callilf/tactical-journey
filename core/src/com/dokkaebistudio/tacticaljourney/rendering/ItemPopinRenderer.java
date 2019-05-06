package com.dokkaebistudio.tacticaljourney.rendering;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.assets.SceneAssets;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent.InventoryActionEnum;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent.PlayerActionEnum;
import com.dokkaebistudio.tacticaljourney.rendering.interfaces.Renderer;
import com.dokkaebistudio.tacticaljourney.rendering.service.PopinService;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.singletons.InputSingleton;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class ItemPopinRenderer implements Renderer, RoomSystem {
	    
	public Stage stage;
	
	/** The inventory component of the player (kept in cache to prevent getting it at each frame). */
	private PlayerComponent playerCompo;
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

    
    public ItemPopinRenderer(Room r, Stage s) {
        this.room = r;
        this.stage = s;
    }
    
    @Override
    public void enterRoom(Room newRoom) {
    	this.room = newRoom;	
    }


    @Override
    public void render(float deltaTime) {
    	
    	if (playerInventoryCompo == null) {
    		playerCompo = Mappers.playerComponent.get(GameScreen.player);
    		playerInventoryCompo = Mappers.inventoryComponent.get(GameScreen.player);
    	}
    	
    	if (playerCompo.getRequestedActions().size() == 1 && playerCompo.getRequestedActions().get(0) == PlayerActionEnum.ITEM_POPIN) {
    		if (!room.hasEnemies()) {
				playerInventoryCompo.requestAction(InventoryActionEnum.PICKUP, playerCompo.getActionEntities().get(0));
				playerCompo.clearRequestedAction();
				return;
    		}
    		
    		room.setNextState(RoomState.ITEM_POPIN);
    		
			itemComponent = Mappers.itemComponent.get(playerCompo.getActionEntities().get(0));
			isShop = itemComponent.getPrice() != null;

			if (selectedItemPopin == null) {
				initTable();
				
    			// Close popin with ESCAPE
	    		stage.addListener(new InputListener() {
					@Override
					public boolean keyUp(InputEvent event, int keycode) {
						if (room.getState() == RoomState.ITEM_POPIN && (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK)) {
							closePopin();
							return true;
						}
						return super.keyUp(event, keycode);
					}
				});
			}
			
			refreshTable();
		
			this.stage.addActor(selectedItemPopin);
			
			playerCompo.clearRequestedAction();
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
		Entity item = playerCompo.getActionEntities().get(0);
		
		// Update the content
		itemTitle.setText(itemComponent.getItemLabel());
		if (itemComponent.getPrice() != null) {
			itemTitle.setText(itemTitle.getText() + " ([GOLD]" + itemComponent.getPrice() + "coins[WHITE])");
		}
		if (itemComponent.getItemDescription() != null) {
			itemDesc.setText(itemComponent.getItemDescription());
		}
		
		// Add text if no space in inventory
		if (!isShop && !playerInventoryCompo.canStore(itemComponent)) {
			itemDesc.setText(itemDesc.getText() + "\n\n" 
					+ "[ORANGE]Your inventory is full!");
		}

		if (isShop) {
			pickupItemBtn.setDisabled(false);
			pickupItemBtn.setText("Buy");
			// Update the Drop item listener
			updateBuyListener(item, itemComponent);

			useItemBtn.setVisible(false);
		} else {
			
			pickupItemBtn.setDisabled(!playerInventoryCompo.canStore(itemComponent));
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
		NinePatchDrawable ninePatchDrawable = new NinePatchDrawable(SceneAssets.popinNinePatch);
		selectedItemPopin.setBackground(ninePatchDrawable);
		
		selectedItemPopin.align(Align.top);
		
		// 1 - Title
		itemTitle = new Label("Title", PopinService.hudStyle());
		selectedItemPopin.add(itemTitle).top().align(Align.top).pad(20, 0, 20, 0);
		selectedItemPopin.row().align(Align.center);
		
		// 2 - Description
		if (itemComponent.getItemDescription() != null) {
			itemDesc = new Label("Desc", PopinService.hudStyle());
			itemDesc.setWrap(true);
			selectedItemPopin.add(itemDesc).growY().width(900).left().pad(0, 20, 0, 20);
			selectedItemPopin.row();
		}
		
		// 3 - Action buttons
		Table buttonTable = new Table();
		
		// 3.1 - Close button
		final TextButton closeBtn = new TextButton("Close", PopinService.buttonStyle());			
		// continueButton listener
		closeBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				closePopin();
			}
		});
		buttonTable.add(closeBtn).pad(0, 20,0,20);

		// 3.2 - Take button
		pickupItemBtn = new TextButton("Take", PopinService.buttonStyle());			
		buttonTable.add(pickupItemBtn).pad(0, 20,0,20);

		// 3.3 - Use button
		useItemBtn = new TextButton("Use", PopinService.buttonStyle());			
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
			room.setNextState(room.getLastInGameState());
		}
	}

}
