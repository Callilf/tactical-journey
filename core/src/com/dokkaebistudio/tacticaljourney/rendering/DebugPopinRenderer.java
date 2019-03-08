package com.dokkaebistudio.tacticaljourney.rendering;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
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
import com.dokkaebistudio.tacticaljourney.enums.InventoryDisplayModeEnum;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.rendering.interfaces.Renderer;
import com.dokkaebistudio.tacticaljourney.rendering.service.PopinService;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class DebugPopinRenderer implements Renderer, RoomSystem {
	    
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
    
    /** The state before the level up state. */
    private RoomState previousState;
    
    
    
    //***************************
    // BOOLEANS
    
    private boolean lootDisplayed = false;
    
    
    //*****************************
    // ACTORS
    
    /** The main table of the popin. */
    private Table mainTable;
    
    /** The loot table. */
    private Table lootTable;
    private Label lootTableTitle;
    private ScrollPane lootableItemsScroll;
    private Table lootableItemsTable;
    
    /** The selected item popin. */
    private Table selectedItemPopin;
    private Label itemTitle;
    private Label itemDesc;
    

    
    /**
     * Constructor.
     * @param r the room
     * @param s the stage to draw on
     * @param p the player
     */
    public DebugPopinRenderer(Room r, Stage s, Entity p) {
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
    	}    	
    	
    	// Check if the inventory is displayed
    	if (inventoryCompo.getDisplayMode() == InventoryDisplayModeEnum.DEBUG) {
    		
    		if (room.getState() != RoomState.DEBUG_POPIN && !inventoryCompo.isInventoryActionInProgress()) {
	    		lootDisplayed = true;
	
				// Create the inventory table and set the state
	    		previousState = room.getNextState();
	    		if (previousState == null || previousState == RoomState.DEBUG_POPIN) {
	    			previousState = room.getState();
	    		}
	    		room.setNextState(RoomState.DEBUG_POPIN);
		    		
		    	if (mainTable == null) {
		    		mainTable = new Table();
	//	    		mainTable.setDebug(true);
	
		    		createLootTable();
		    		
		        	mainTable.pack();
		        	mainTable.setPosition(GameScreen.SCREEN_W/2 - mainTable.getWidth()/2, GameScreen.SCREEN_H/2 - mainTable.getHeight()/2);
	    		}
    		}
	    	
    	} else if (inventoryCompo.getDisplayMode() == InventoryDisplayModeEnum.NONE && room.getState() == RoomState.DEBUG_POPIN) {
    		// Close the inventory if inventoryCompo.isInventoryDisplayed() was switched to false
    		closePopin();
    	}
    
    	
    	
    	if (lootDisplayed) {
    		// Draw the table
			stage.act(Gdx.graphics.getDeltaTime());
			stage.draw();
    		
    		// Close the inventory on a left click outside the popin
    		if (InputSingleton.getInstance().leftClickJustPressed) {
    			closePopin();
    		}
    	}
    }


	
	//*******************************
	// LOOT TABLE
	
	
	private void createLootTable() {		
		lootTable = new Table();
//		    		table.setDebug(true, true);
		lootTable.setTouchable(Touchable.enabled);
		lootTable.addListener(new ClickListener() {});
		
		TextureRegionDrawable lootBackground = new TextureRegionDrawable(Assets.inventory_background);
		lootTable.setBackground(lootBackground);
		lootTable.align(Align.top);

		// 1 - Title
		lootTableTitle = new Label("DEBUG MODE", PopinService.hudStyle());
		lootTable.add(lootTableTitle).uniformX().pad(40, 0, 40, 0);
		lootTable.row();
		
		// The table that will contain all loot items
		lootableItemsTable = new Table();
		lootableItemsTable.top();
		lootableItemsTable.pack();
		
		//The scroll pane for the loot items
		lootableItemsScroll = new ScrollPane(lootableItemsTable);
		lootTable.add(lootableItemsScroll).fill().expand().maxHeight(535);
		lootTable.row();
		
		Table btnTable = new Table();
		
		TextButton closeBtn = new TextButton("Close", PopinService.bigButtonStyle());
		// Close listener
		closeBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				closePopin();
			}
		});
		btnTable.add(closeBtn).pad(0, 0, 0, 20);
		btnTable.pack();
		
		lootTable.add(btnTable).pad(40, 0, 40, 0);

		
		lootTable.pack();
		mainTable.add(lootTable).padRight(20);
		
		
		lootableItemsTable.clear();
		ItemEnum[] values = ItemEnum.values();
		for (ItemEnum v : values) {
			Entity item = room.entityFactory.itemFactory.createItem(v);
			Table oneItem = createOneLootItem(item);
			lootableItemsTable.add(oneItem).pad(0, 0, 10, 0).maxWidth(630);
			lootableItemsTable.row();
		}
		
		lootableItemsTable.pack();
		lootableItemsScroll.setWidget(lootableItemsTable);
		lootableItemsScroll.pack();	
		
		stage.addActor(mainTable);
	}

	private Table createOneLootItem(final Entity item) {
		final ItemComponent itemComponent = Mappers.itemComponent.get(item);
		Table oneItem = new Table();
//		oneItem.setDebug(true);

		TextureRegionDrawable lootBackground = new TextureRegionDrawable(Assets.inventory_lootable_item_background);
		oneItem.setBackground(lootBackground);
		
		oneItem.left();
		Image image = new Image(Assets.getTexture(itemComponent.getItemImageName() + "-full"));
		oneItem.add(image).width(Value.percentWidth(1f, image)).pad(0, 20, 0, 20);
		image.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				displaySelectedItemPopin( item);
			}
		});
		
		Label itemName = new Label(itemComponent.getItemLabel(), PopinService.hudStyle());
		itemName.setWrap(true);
		oneItem.add(itemName).fillY().width(Value.percentWidth(0.50f, oneItem)).padRight(20);
		itemName.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				displaySelectedItemPopin( item);
			}
		});
		
		TextButton takeBtn = new TextButton("Take", PopinService.smallButtonStyle());
		takeBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				//add item in inventory and remove it from lootable entity
				Entity clonedItem = room.entityFactory.itemFactory.createItem(itemComponent.getItemType().type);
				boolean pickedUp = itemComponent.pickUp(player, clonedItem, room);
				if (pickedUp) {
					//TODO
				} else {
					//TODO
				}
			}
		});
		
		oneItem.add(takeBtn).padRight(20);
		
		oneItem.pack();
		return oneItem;
	}
	
	
	//*****************************
	// Item popin
	
	/**
	 * Display the popin of the selected item with it's title, description and possible actions.
	 * @param item the item selected
	 * @param slot the slot on which the item was
	 */
	private void displaySelectedItemPopin(final Entity item) {
		if (selectedItemPopin == null) {
			selectedItemPopin = new Table();
//			selectedItemPopin.setDebug(true);

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
			itemDesc = new Label("Description", PopinService.hudStyle());
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
			
			selectedItemPopin.add(buttonTable).pad(20, 0, 20, 0);
			
		}
		
		
		final ItemComponent itemComponent = Mappers.itemComponent.get(item);
		
		// Update the content
		itemTitle.setText(itemComponent.getItemLabel());
		itemDesc.setText(itemComponent.getItemDescription());
		
		// Place the popin properly
		selectedItemPopin.pack();
		selectedItemPopin.setPosition(GameScreen.SCREEN_W/2 - selectedItemPopin.getWidth()/2, GameScreen.SCREEN_H/2 - selectedItemPopin.getHeight()/2);
	
		this.stage.addActor(selectedItemPopin);
	}
	
	
	
	private void hideSelectedItemPopin() {
		selectedItemPopin.remove();
	}

	
	//*****************************
	// CLOSE and REFRESH
	

	/**
	 * Close the level up popin and unpause the game.
	 */
	private void closePopin() {
		lootDisplayed = false;
		inventoryCompo.setDisplayMode(InventoryDisplayModeEnum.NONE);

		mainTable.remove();
		
		if (room.getNextState() == null) {
			room.setNextState(previousState);
		}
	}

}
