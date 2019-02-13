package com.dokkaebistudio.tacticaljourney.rendering;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
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
import com.dokkaebistudio.tacticaljourney.components.player.WalletComponent;
import com.dokkaebistudio.tacticaljourney.enums.InventoryDisplayModeEnum;
import com.dokkaebistudio.tacticaljourney.rendering.interfaces.Renderer;
import com.dokkaebistudio.tacticaljourney.rendering.service.PopinService;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class InventoryPopinRenderer implements Renderer, RoomSystem {
	    
	//****************************
	// Main attributes
	
	/** The stage. */
	public Stage stage;
	/** The player. */
	private Entity player;
	/** The current room. */
    private Room room;
	
	/** The inventory component of the player (kept in cache to prevent getting it at each frame). */
	private InventoryComponent inventoryCompo;
	/** The wallet component. */
	private WalletComponent walletCompo;
    
    /** The state before the level up state. */
    private RoomState previousState;
    
    
    
    //***************************
    // BOOLEANS
    
	/** Whether the inventory needs refreshing. */
    private boolean needsRefresh = true;
    
    /** Whether the item popin is displayed. */
    private boolean itemPopinDisplayed = false;
    
    
    //*****************************
    // ACTORS
    
    /** The main table of the popin. */
    private Table mainTable;
    
    /** The inventory table. */
    private Table inventoryTable;
    private Label money;
    private Table[] slots = new Table[16];
    private Image[] slotImages = new Image[16];
    private List<TextButton> inventoryDropButtons = new ArrayList<>();
    
    
    /** The selected item popin. */
    private Table selectedItemPopin;
    private Label itemTitle;
    private Label itemDesc;
    private TextButton dropItemBtn;
    private ChangeListener dropListener;
    private TextButton useItemBtn;
    private ChangeListener useListener;
    

    
    /**
     * Constructor.
     * @param r the room
     * @param s the stage to draw on
     * @param p the player
     */
    public InventoryPopinRenderer(Room r, Stage s, Entity p) {
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
    	
    	if (inventoryCompo == null) {
    		inventoryCompo = Mappers.inventoryComponent.get(player);
    		walletCompo = Mappers.walletComponent.get(player);
    	}

    	
    	// Check if the inventory is displayed
    	if (inventoryCompo.getDisplayMode() == InventoryDisplayModeEnum.INVENTORY && room.getState() != RoomState.INVENTORY_POPIN) {
			//set the state
    		previousState = room.getNextState() != null ? room.getNextState() : room.getState();
    		room.setNextState(RoomState.INVENTORY_POPIN);

    		if (mainTable == null) {	    		
	    		createInventoryTable();	        	
    		}
	    		
    		if (needsRefresh || inventoryCompo.isNeedInventoryRefresh()) {
	        	
    			money.setText("[GOLD]" + walletCompo.getAmount());
    			
    			for(int i=0 ; i<slots.length ; i++) {
    				final Table slot = slots[i];
					Image image = slotImages[i];
    				final Entity item = inventoryCompo.get(i);
    				
    				slot.clearListeners();
    				if (item != null) {
    					// An item is present in this inventory slot
    					final ItemComponent itemComponent = Mappers.itemComponent.get(item);
    					TextureRegionDrawable texture = new TextureRegionDrawable(Assets.getTexture(itemComponent.getItemImageName() + "-full"));
    					image.setDrawable(texture);

    					slot.clearListeners();
    					slot.addListener(new ClickListener() {
    						
    						@Override
    						public void clicked(InputEvent event, float x, float y) {
    							displaySelectedItemPopin( item, slot);
    						}
    					});
    					
    				} else {
    			        boolean activeSlot = i < inventoryCompo.getNumberOfSlots();
    			        if (!activeSlot) {
	    					TextureRegionDrawable texture = new TextureRegionDrawable(Assets.getTexture(Assets.inventory_slot_disabled));
	    					image.setDrawable(texture);
    			        } else {
    			        	image.setDrawable(null);
    			        }

    				}
    			}
    			
	        	// pack the main table and place it at the center
	    		stage.addActor(mainTable);
	    		
	    		finishRefresh();
    		}

    	} else if (inventoryCompo.getDisplayMode() == InventoryDisplayModeEnum.NONE && room.getState() == RoomState.INVENTORY_POPIN) {
    		closePopin();
    	}
    	
    	
    	
    	if (room.getState() == RoomState.INVENTORY_POPIN) {
    		// Draw the table
			stage.act(Gdx.graphics.getDeltaTime());
			stage.draw();
    		
    		// Close the inventory on a left click outside the popin
    		if (InputSingleton.getInstance().leftClickJustPressed) {
    			closePopin();
    		}
    	}
    }

    
	
	//*****************************
	// INVENTORY
	
	
	private void createInventoryTable() {
		mainTable = new Table();
//		mainTable.setDebug(true);

		inventoryTable = new Table();
		inventoryTable.setTouchable(Touchable.enabled);
		inventoryTable.addListener(new ClickListener() {});
			    		
		TextureRegionDrawable topBackground = new TextureRegionDrawable(Assets.getTexture(Assets.inventory_background));
		inventoryTable.setBackground(topBackground);
		
		inventoryTable.align(Align.top);
		
		// 1 - Title and money
		Table topTable = new Table();
		topTable.add().width(Value.percentWidth(0.25f, inventoryTable));
		
		// 1.1 - Title
		Label title = new Label("Inventory", PopinService.hudStyle());
		title.setAlignment(Align.center);
		topTable.add(title).width(Value.percentWidth(0.5f, inventoryTable));
		
		// 1.2 - Money
		Table moneyTable = new Table();
		Image moneyImage = new Image(Assets.getTexture(Assets.inventory_money));
		moneyTable.add(moneyImage);
		money = new Label("[GOLD]" + walletCompo.getAmount(), PopinService.hudStyle());
		moneyTable.add(money);
		money.setAlignment(Align.right);
		topTable.add(moneyTable).width(Value.percentWidth(0.25f, inventoryTable));
		
		inventoryTable.add(topTable).uniformX().pad(20, 0, 20, 0);
		inventoryTable.row();
		
		
		// 2 - Inventory slots
		inventoryDropButtons.clear();
		Table slotsTable = new Table();
		int index = 0;
		for (int row = 0 ; row < 4 ; row++) {
			for (int col=0 ; col<4 ; col++) {
				Table slot = createSlot( index);
				slotsTable.add(slot);
				slots[index] = slot;
				
				index ++;
			}
			slotsTable.row();
		}
		inventoryTable.add(slotsTable);
		
    	mainTable.add(inventoryTable);
    	mainTable.pack();
    	mainTable.setPosition(GameScreen.SCREEN_W/2 - mainTable.getWidth()/2, GameScreen.SCREEN_H/2 - mainTable.getHeight()/2);
	}

    /**
     * Create an inventory slot (filled or empty).
     * @param index the index of the slot
     * @return the created slot
     */
	private Table createSlot(int index) {		
		final Table slot = new Table();
//		slot.setDebug(true);

		//Background
		TextureRegionDrawable slotBackground = new TextureRegionDrawable(Assets.getTexture(Assets.inventory_slot));
		slot.setBackground(slotBackground);

		Image img = new Image();
		slotImages[index] = img;
		slot.add(img);
		
		slot.pack();
		return slot;
	}
	
	
	
	
	
	
	
	//*********************************
	// Selected item popin
	
	
	/**
	 * Display the popin of the selected item with it's title, description and possible actions.
	 * @param item the item selected
	 * @param slot the slot on which the item was
	 */
	private void displaySelectedItemPopin(final Entity item, final Table slot) {
		if (selectedItemPopin == null) {
			selectedItemPopin = new Table();
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
			itemTitle = new Label("Title", PopinService.hudStyle());
			selectedItemPopin.add(itemTitle).top().align(Align.top).pad(20, 0, 20, 0);
			selectedItemPopin.row().align(Align.center);
			
			// 2 - Description
			itemDesc = new Label("Un test de description d'idem qui est assez long pour voir jusqu'ou on peut aller. Un test de description d'idem qui est assez long pour voir jusqu'ou on peut aller. Un test de description d'idem qui est assez long pour voir jusqu'ou on peut aller.", PopinService.hudStyle());
			itemDesc.setWrap(true);
			selectedItemPopin.add(itemDesc).growY().width(textureRegionDrawable.getMinWidth()).left().pad(0, 20, 0, 20);
			selectedItemPopin.row();
			
			// 3 - Action buttons
			Table buttonTable = new Table();
			
			// 3.1 - Close button
			final TextButton closeBtn = new TextButton("Close",PopinService.bigButtonStyle());			
			// continueButton listener
			closeBtn.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					hideSelectedItemPopin();
				}
			});
			buttonTable.add(closeBtn).pad(0, 20,0,20);
			
			// 3.2 - Drop button
			dropItemBtn = new TextButton("Drop",PopinService.bigButtonStyle());			
			buttonTable.add(dropItemBtn).pad(0, 20,0,20);

			// 3.3 - Use button
			useItemBtn = new TextButton("Use",PopinService.bigButtonStyle());			
			buttonTable.add(useItemBtn).pad(0, 20,0,20);
			
			selectedItemPopin.add(buttonTable).pad(20, 0, 20, 0);
			
		}
		
		
		final ItemComponent itemComponent = Mappers.itemComponent.get(item);
		
		// Update the content
		itemTitle.setText(itemComponent.getItemLabel());
		itemDesc.setText(itemComponent.getItemDescription());
		
		// Update the Drop item listener
		updateDropListener(item, slot);
		
		useItemBtn.setText(itemComponent.getItemActionLabel());
		// Update the Use item listener
		updateUseListener(item, slot);
		
		// Place the popin properly
		selectedItemPopin.pack();
		selectedItemPopin.setPosition(GameScreen.SCREEN_W/2 - selectedItemPopin.getWidth()/2, GameScreen.SCREEN_H/2 - selectedItemPopin.getHeight()/2);
	
		itemPopinDisplayed = true;
		this.stage.addActor(selectedItemPopin);
	}

	private void updateUseListener(final Entity item, final Table slot) {
		if (useListener != null) {
			useItemBtn.removeListener(useListener);
		}
		useListener = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				inventoryCompo.requestAction(InventoryActionEnum.USE, item);
				slot.removeListener(this);
				hideSelectedItemPopin();
				closePopin();
			}
		};
		useItemBtn.addListener(useListener);
	}

	private void updateDropListener(final Entity item, final Table slot) {
		if (dropListener != null) {
			dropItemBtn.removeListener(dropListener);
		}
		dropListener = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				//Drop on the floor
				inventoryCompo.requestAction(InventoryActionEnum.DROP, item);
				slot.removeListener(this);
				inventoryCompo.remove(item);
				closePopin();
			}
		};
		dropItemBtn.addListener(dropListener);
	}
	
	private void hideSelectedItemPopin() {
		selectedItemPopin.remove();
		itemPopinDisplayed = false;
	}
	
	
	
	
	//*****************************
	// CLOSE and REFRESH
	
	/**
	 * Perform misc actions and clear all refresh statuses.
	 */
	private void finishRefresh() {	
		needsRefresh = false;
		inventoryCompo.setNeedInventoryRefresh(false);
	}

	/**
	 * Close the level up popin and unpause the game.
	 */
	private void closePopin() {
		inventoryCompo.setDisplayMode(InventoryDisplayModeEnum.NONE);
		
		if (itemPopinDisplayed) hideSelectedItemPopin();

		mainTable.remove();
		needsRefresh = true;
		
		if (room.getNextState() == null) {
			room.setNextState(previousState);
		}
	}

}
