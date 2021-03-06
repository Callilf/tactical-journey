package com.dokkaebistudio.tacticaljourney.rendering;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.assets.SceneAssets;
import com.dokkaebistudio.tacticaljourney.ces.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.loot.LootableComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.AmmoCarrierComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.ces.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.enums.InventoryDisplayModeEnum;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.items.ItemArrow;
import com.dokkaebistudio.tacticaljourney.items.ItemBomb;
import com.dokkaebistudio.tacticaljourney.items.orbs.ItemOrb;
import com.dokkaebistudio.tacticaljourney.rendering.interfaces.Renderer;
import com.dokkaebistudio.tacticaljourney.rendering.service.PopinService;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.singletons.InputSingleton;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class LootPopinRenderer implements Renderer, RoomSystem {
	    
	//****************************
	// Main attributes
	
	/** The stage. */
	public Stage stage;
	/** The current room. */
    private Room room;
	
	/** The inventory component of the player (kept in cache to prevent getting it at each frame). */
	private InventoryComponent inventoryCompo;
	private AmmoCarrierComponent ammoCarrierCompo;
	/** The current lootable component. */
	private LootableComponent lootableCompo;
        
    
    //***************************
    // BOOLEANS
    
    private boolean lootDisplayed = false;
    
	/** Whether the inventory needs refreshing. */
    private boolean needsRefresh = true;
    
    /** Whether we are in the process of "take all" items. */
    private boolean takeAllInProgess = false;
    
    /** Whether the item popin is displayed. */
    private boolean itemPopinDisplayed = false;

    
    
    //*****************************
    // ACTORS
    
    /** The main table of the popin. */
    private Table mainTable;
    
    /** The inventory table. */
    private Table inventoryTable;
    private Table slotsTable;
    private ScrollPane slotsScroll;
    private Table[] slots = new Table[96];
    private Image[] slotImages = new Image[96];
    private Label[] slotQuantities = new Label[96];
    private TextButton[] slotDropBtns = new TextButton[96];

    private List<TextButton> inventoryDropButtons = new ArrayList<>();
    
    private Label arrowQuantity;
    private Label bombQuantity;
    
    /** The loot table. */
    private Table lootTable;
    private Label lootTableTitle;
    private ScrollPane lootableItemsScroll;
    private Table lootableItemsTable;
    private List<TextButton> lootTakeBtns = new ArrayList<>();
    private TextButton takeAllBtn;
    private ChangeListener takeAllListener;
    
    /** The selected item popin. */
    private Table selectedItemPopin;
    private Label itemTitle;
    private Label itemDesc;
    

    
    /**
     * Constructor.
     * @param r the room
     * @param s the stage to draw on
     */
    public LootPopinRenderer(Room r, Stage s) {
        this.room = r;
        this.stage = s;
    }
    
    
    @Override
    public void enterRoom(Room newRoom) {
    	this.room = newRoom;	
    }

    
    @Override
    public void render(float deltaTime) {
    	
    	if (inventoryCompo == null) {
    		inventoryCompo = Mappers.inventoryComponent.get(GameScreen.player);
    		ammoCarrierCompo = Mappers.ammoCarrierComponent.get(GameScreen.player);
    	}
    	
    	
    	//Handle interruption
    	boolean interrupted = handleInterruption();
    	if (interrupted) return;
    	
    	
    	// Check if the inventory is displayed
    	if (inventoryCompo.getDisplayMode() == InventoryDisplayModeEnum.LOOT) {
    		
    		if (room.getState() != RoomState.LOOT_POPIN && !inventoryCompo.isInventoryActionInProgress()) {
	    		lootDisplayed = true;
	
				// Create the inventory table and set the state
	    		room.setNextState(RoomState.LOOT_POPIN);
		    		
		    	if (mainTable == null) {
		    		mainTable = new Table();
	//	    		mainTable.setDebug(true);
	
		    		createLootTable();
		    		createInventoryTable();
		    		
		    		arrowQuantity = new Label("0", PopinService.hudStyle());
		    		bombQuantity = new Label("0", PopinService.hudStyle());
		    		Image quiverImage = new Image(Assets.loadAndGetTexture(new ItemArrow().getTexture().getNameFull()).getRegion());
		    		Image bombBagImage = new Image(Assets.loadAndGetTexture(new ItemBomb().getTexture().getNameFull()).getRegion());
		    		Table arrowsAndBombsTable = InventoryPopinRenderer.createArrowsAndBombTable(quiverImage, bombBagImage, 
		    				arrowQuantity, bombQuantity, null, null);
		    		mainTable.add(arrowsAndBombsTable);
		    		
	    			// Close popin with ESCAPE
		    		stage.addListener(new InputListener() {
						@Override
						public boolean keyUp(InputEvent event, int keycode) {
							if (room.getState() == RoomState.LOOT_POPIN && (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK)) {
								closePopin();
								return true;
							}
							return super.keyUp(event, keycode);
						}
					});
		    		
		        	mainTable.pack();
		        	mainTable.setPosition(GameScreen.SCREEN_W/2 - mainTable.getWidth()/2, GameScreen.SCREEN_H/2 - mainTable.getHeight()/2);
	    		}
    		}
	    	
    		if (needsRefresh || inventoryCompo.isNeedInventoryRefresh()) {
    			lootableCompo = Mappers.lootableComponent.get(inventoryCompo.getLootableEntity());

    			refreshLootTable();
    			refreshInventory();
    			
    			if (handleTakeAllAction()) {
		    		stage.addActor(mainTable);
		    		finishRefresh();
    			}
	    		
    		}

    	} else if (inventoryCompo.getDisplayMode() == InventoryDisplayModeEnum.NONE && room.getState() == RoomState.LOOT_POPIN) {
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
		
		NinePatchDrawable ninePatchDrawable = new NinePatchDrawable(SceneAssets.popinNinePatch);
		ninePatchDrawable.setMinWidth(653);
		ninePatchDrawable.setMinHeight(746);
		lootTable.setBackground(ninePatchDrawable);
		lootTable.align(Align.top);

		// 1 - Title
		lootTableTitle = new Label("Title", PopinService.hudStyle());
		lootTable.add(lootTableTitle).uniformX().pad(20, 0, 40, 0);
		lootTable.row();
		
		// The table that will contain all loot items
		lootableItemsTable = new Table();
		lootableItemsTable.top();
		lootableItemsTable.pack();
		
		//The scroll pane for the loot items
		lootableItemsScroll = new ScrollPane(lootableItemsTable, PopinService.smallScrollStyle());
		lootableItemsScroll.setFadeScrollBars(false);
		lootTable.add(lootableItemsScroll).fill().expand().maxHeight(530);
		lootTable.row();
		
		Table btnTable = new Table();
		
		TextButton closeBtn = new TextButton("Close", PopinService.buttonStyle());
		// Close listener
		closeBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				closePopin();
			}
		});
		btnTable.add(closeBtn).pad(0, 0, 0, 20);

		
		takeAllBtn = new TextButton("Take all", PopinService.buttonStyle());
		btnTable.add(takeAllBtn).pad(0, 20, 0, 0);
		btnTable.pack();
		
		lootTable.add(btnTable).pad(40, 0, 40, 0);

		
		lootTable.pack();
		mainTable.add(lootTable).padRight(20);
	}

	private Table createOneLootItem(final Entity item) {
		final ItemComponent itemComponent = Mappers.itemComponent.get(item);
		Table oneItem = new Table();
//		oneItem.setDebug(true);

		
		NinePatchDrawable ninePatchDrawable = new NinePatchDrawable(SceneAssets.popinInnerNinePatch);
		ninePatchDrawable.setMinWidth(600);
		ninePatchDrawable.setMinHeight(100);
		oneItem.setBackground(ninePatchDrawable);
		
		oneItem.left();
		Image image = new Image(Assets.loadAndGetTexture(itemComponent.getItemImageName().getNameFull()).getRegion());
		oneItem.add(image).width(Value.percentWidth(1f, image)).pad(-5, 0, -5, 20);
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
		
		if (!(itemComponent.getItemType() instanceof ItemOrb)) {
			TextButton takeBtn = new TextButton("Take", PopinService.buttonStyle());
			takeBtn.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					//add item in inventory and remove it from lootable entity
					boolean pickedUp = itemComponent.pickUp(GameScreen.player, item, room);
					if (pickedUp) {
						lootableCompo.getItems().remove(item);
						inventoryCompo.setInventoryActionInProgress(true);
						room.turnManager.endPlayerTurn();
						refreshPopin();
					}
				}
			});
			if (inventoryCompo.isInventoryActionInProgress()) takeBtn.setDisabled(true);
			lootTakeBtns.add(takeBtn);
		
			oneItem.add(takeBtn);
		}
		
		oneItem.pack();
		return oneItem;
	}
	
	
	private void refreshLootTable() {
		
		lootTableTitle.setText(lootableCompo.getType().getLabel() + " ([GREEN]" + lootableCompo.getItems().size() + "[WHITE] items)");
	
		lootableItemsTable.clear();
		for (Entity item : lootableCompo.getAllItems()) {
			Table oneItem = createOneLootItem(item);
			lootableItemsTable.add(oneItem).pad(0, 0, 10, 0).maxWidth(630);
			lootableItemsTable.row();
		}
		lootableItemsTable.pack();
		lootableItemsScroll.setWidget(lootableItemsTable);
		lootableItemsScroll.pack();

		
		
		// Take all button
		takeAllBtn.setText("Take all");
		
		if (takeAllListener != null) takeAllBtn.removeListener(takeAllListener);
		takeAllListener = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				takeAllInProgess = true;
				refreshPopin();
			}
		};
		takeAllBtn.addListener(takeAllListener);
		takeAllBtn.setDisabled(inventoryCompo.isInventoryActionInProgress());
	
	}
	
	
	
	//*****************************
	// INVENTORY
	

	private void createInventoryTable() {
		inventoryTable = new Table();
		inventoryTable.setTouchable(Touchable.enabled);
		inventoryTable.addListener(new ClickListener() {});
			    		
		NinePatchDrawable ninePatchDrawable = new NinePatchDrawable(SceneAssets.popinNinePatch);
		ninePatchDrawable.setMinWidth(653);
		ninePatchDrawable.setMinHeight(746);
		inventoryTable.setBackground(ninePatchDrawable);
		
		inventoryTable.align(Align.top);
		
		// 1 - Title 
		Table topTable = new Table();
		Label title = new Label("Inventory", PopinService.hudStyle());
		title.setAlignment(Align.center);
		topTable.add(title);
		
		inventoryTable.add(topTable).uniformX().pad(20, 0, 40, 0);
		inventoryTable.row();
		
		
		// 2 - Inventory slots
		inventoryDropButtons.clear();
		slotsTable = new Table();
		slotsTable.top();
		for (int index = 0 ; index < 96 ; index++) {
			Table slot = createSlot( index);
			slots[index] = slot;
		}
		slotsScroll = new ScrollPane(slotsTable, PopinService.scrollStyle());
		slotsScroll.setFadeScrollBars(false);
		slotsScroll.setScrollbarsOnTop(true);
		slotsScroll.setScrollingDisabled(true, false);
		slotsScroll.setScrollBarPositions(false, true);
		slotsScroll.layout();
		inventoryTable.add(slotsScroll).width(635).height(600);
		
    	mainTable.add(inventoryTable).padRight(20);
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
		NinePatchDrawable ninePatchDrawable = new NinePatchDrawable(SceneAssets.popinInnerNinePatch);
		ninePatchDrawable.setMinWidth(140);
		ninePatchDrawable.setMinHeight(140);
		slot.setBackground(ninePatchDrawable);
		
		final Stack slotStack = new Stack();

		Table imageStackTable = new Table();
		Image img = new Image();
		slotImages[index] = img;
		imageStackTable.add(img);
		slotStack.add(imageStackTable);
		
		Label quantity = new Label("", PopinService.hudStyle());
		quantity.setTouchable(Touchable.disabled);
		quantity.setAlignment(Align.bottomLeft);
		slotQuantities[index] = quantity;
		slotStack.add(quantity);

		//Add the drop button
		final TextButton dropBtn = new TextButton("Drop", PopinService.buttonStyle());
		slotDropBtns[index] = dropBtn;
		inventoryDropButtons.add(dropBtn);
		dropBtn.setVisible(false);
		
		Table slotStackTable = new Table();
		slotStackTable.setTouchable(Touchable.childrenOnly);
		slotStackTable.add(dropBtn);
		slotStack.add(slotStackTable);
		slot.add(slotStack).pad(-5, -5, -5, -5);

		slot.pack();
		return slot;
	}
	
	
	private void refreshInventory() {
		
		// Update arrow and bomb quantities
		arrowQuantity.setText(ammoCarrierCompo.getArrows() + "/" + ammoCarrierCompo.getMaxArrows());
		bombQuantity.setText(ammoCarrierCompo.getBombs() + "/" + ammoCarrierCompo.getMaxBombs());
		
		// Fill the slots table
		float scrollY = slotsScroll.getScrollY();
		slotsTable.clear();
		slotsScroll.layout();
		int nbSlots = inventoryCompo.getNumberOfSlots();
		double nbRows = (int) Math.ceil((double)nbSlots / (double)4);
		int index = 0;
		for (int row = 0 ; row < nbRows ; row++) {
			for (int col=0 ; col<4 ; col++) {
				if (index >= nbSlots) break;
				slotsTable.add(slots[index]).pad(0, 5, 10, 5);
				index++;
			}
			slotsTable.row();
		}
		slotsTable.pack();
		slotsScroll.layout();
		slotsScroll.setScrollY(scrollY);
		
		for(int i=0 ; i < nbSlots ; i++) {
			final Table slot = slots[i];
			Image image = slotImages[i];
			Label quantity = slotQuantities[i];
			final TextButton dropBtn = slotDropBtns[i];
			final Entity item = inventoryCompo.get(i);
			
			image.clearListeners();
			dropBtn.clearListeners();
			slot.setVisible(true);
			if (item != null) {

				// An item is present in this inventory slot
				final ItemComponent itemComponent = Mappers.itemComponent.get(item);
				TextureRegionDrawable texture = new TextureRegionDrawable(Assets.loadAndGetTexture(itemComponent.getItemImageName().getNameFull()).getRegion());
				image.setDrawable(texture);
				quantity.setText(inventoryCompo.getQuantity(i) > 1 ? String.valueOf(inventoryCompo.getQuantity(i)) : "");

				if (!inventoryCompo.isInventoryActionInProgress()) {
					// Listener to display the DROP button
					image.addListener(new ClickListener() {
						
						@Override
						public void clicked(InputEvent event, float x, float y) {
							for (TextButton tb : inventoryDropButtons) {
								tb.setVisible(false);
							}
							dropBtn.setVisible(true);
						}
					});
				}
				
				// Listener to drop the item
				dropBtn.addListener(new ClickListener() {
					
					@Override
					public void clicked(InputEvent event, float x, float y) {
						//Loot mode, drop into the lootable
						itemComponent.drop(GameScreen.player, item, null);
						lootableCompo.getItems().add(item);
						inventoryCompo.setInventoryActionInProgress(true);
						room.turnManager.endPlayerTurn();
						refreshPopin();					
					}
				});
				
				
			} else {
		        boolean activeSlot = i < inventoryCompo.getNumberOfSlots();
	        	image.setDrawable(null);
		        quantity.setText("");
		        dropBtn.setVisible(false);
		        if (!activeSlot) {
					slot.setVisible(false);
		        }
			}
			
		}
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
			NinePatchDrawable ninePatchDrawable = new NinePatchDrawable(SceneAssets.popinNinePatch);
			selectedItemPopin.setBackground(ninePatchDrawable);
			
			selectedItemPopin.align(Align.top);
			
			// 1 - Title
			itemTitle = new Label("Title", PopinService.hudStyle());
			selectedItemPopin.add(itemTitle).top().align(Align.top).pad(20, 0, 20, 0);
			selectedItemPopin.row().align(Align.center);
			
			// 2 - Description
			itemDesc = new Label("Description", PopinService.hudStyle());
			itemDesc.setWrap(true);
			selectedItemPopin.add(itemDesc).growY().width(900).left().pad(0, 20, 0, 20);
			selectedItemPopin.row();
			
			// 3 - Action buttons
			Table buttonTable = new Table();
			
			// 3.1 - Close button
			final TextButton closeBtn = new TextButton("Close",PopinService.buttonStyle());			
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
	
		itemPopinDisplayed = true;
		this.stage.addActor(selectedItemPopin);
	}
	
	
	
	private void hideSelectedItemPopin() {
		selectedItemPopin.remove();
		itemPopinDisplayed = false;
	}

	
	//*****************************
	// Take all and interruption
	
    /**
     * Handle the "Take all" action.
     * Take items one at a time and spend a turn for each item.
     * @return true if the handleTakeAll has been completed, false if it needs a refresh.
     */
	private boolean handleTakeAllAction() {
		boolean takeAllFinished = false;
		if (takeAllInProgess && !inventoryCompo.isInventoryActionInProgress() && !lootableCompo.getItems().isEmpty()) {
			takeAllOrbs();
			
			if (!lootableCompo.getItems().isEmpty()) {
				pickUpItem(0);
				lootableCompo.getItems().removeAll(lootableCompo.getStandByItems());
				refreshPopin();
			} else {
				// Only orbs
				return false;
			}
			
			takeAllFinished = lootableCompo.getItems().isEmpty();
		} else {
			takeAllFinished = true;
		}
		
		
		if (takeAllFinished) {
			takeAllInProgess = false;
			lootableCompo.finishTakeAll();
			refreshPopin();
		}
		return true;
	}


	private void pickUpItem(int index) {
		Entity item = lootableCompo.getItems().get(index);
		ItemComponent itemComponent = Mappers.itemComponent.get(item);
		
		boolean onStandBy = itemComponent.getItemType() instanceof ItemOrb;
		
		if (!onStandBy) {
			onStandBy = !itemComponent.pickUp(GameScreen.player, item, room);
			if (!onStandBy) {
				lootableCompo.getItems().remove(item);
				inventoryCompo.setInventoryActionInProgress(true);
				room.turnManager.endPlayerTurn();
				refreshPopin();
			}
		}
		
		if (onStandBy) {
			lootableCompo.getStandByItems().add(item);
			if (lootableCompo.getItems().size() > index + 1) {
				pickUpItem(index + 1);
			}
		}
	}


    /**
     * Check if the player was interrupted during a looting action.
     * @return true if interrupted
     */
	private boolean handleInterruption() {
		if (inventoryCompo.isInterrupted()) {
    		takeAllInProgess = false;
    		lootableCompo.finishTakeAll();
    		inventoryCompo.setInterrupted(false);
    		closePopin();
    		return true;
    	}
		return false;
	}
	
	

    /**
     * If an action is being processed in the inventory while enemies are playing their turn,
     * reduce the opacity of the inventory to show enemies actions.
     */
	private void displayRoomIfEnemiesArePlaying() {
		if (room.hasEnemies()) {
			if (inventoryCompo.isInventoryActionInProgress()) {
				mainTable.addAction(Actions.alpha(0.6f));
			} else {
				mainTable.addAction(Actions.alpha(1f));
			}
		} else {
			mainTable.addAction(Actions.alpha(1f));
		}
	}
	
	

	private void takeAllOrbs() {
		Iterator<Entity> it = lootableCompo.getItems().iterator();
		while(it.hasNext()) {
			Entity item = it.next();
			ItemComponent itemComponent = Mappers.itemComponent.get(item);
			if (itemComponent.getItemType() instanceof ItemOrb) {
				itemComponent.pickUp(GameScreen.player, item, room);
				it.remove();
				refreshPopin();
			}
		}
	}
	
	
	
	//*****************************
	// CLOSE and REFRESH
	
	/**
	 * Perform misc actions and clear all refresh statuses.
	 */
	private void finishRefresh() {
		displayRoomIfEnemiesArePlaying();
		
		needsRefresh = false;
		inventoryCompo.setNeedInventoryRefresh(false);
	}
	
	private void refreshPopin() {
		needsRefresh = true;		
	}

	/**
	 * Close the level up popin and unpause the game.
	 */
	private void closePopin() {
		
		// Take orbs if there are any
		takeAllOrbs();
		
		lootDisplayed = false;
		inventoryCompo.setDisplayMode(InventoryDisplayModeEnum.NONE);

		mainTable.remove();
		needsRefresh = true;
		
		if (room.getNextState() == null) {
			room.setNextState(room.getLastInGameState());
		}
	}



}
