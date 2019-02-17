package com.dokkaebistudio.tacticaljourney.rendering;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
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
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.InputSingleton;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.components.loot.LootableComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.components.player.WalletComponent;
import com.dokkaebistudio.tacticaljourney.enums.InventoryDisplayModeEnum;
import com.dokkaebistudio.tacticaljourney.rendering.interfaces.Renderer;
import com.dokkaebistudio.tacticaljourney.rendering.service.PopinService;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class LootPopinRenderer implements Renderer, RoomSystem {
	    
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
	/** The current lootable component. */
	private LootableComponent lootableCompo;
	/** The wallet component. */
	private WalletComponent walletCompo;
    
    /** The state before the level up state. */
    private RoomState previousState;
    
    
    
    //***************************
    // BOOLEANS
    
    private boolean lootDisplayed = false;
    
	/** Whether the inventory needs refreshing. */
    private boolean needsRefresh = true;
    
    /** Whether we are in the process of "take all" items. */
    private boolean takeAllInProgess = false;
    
    
    //*****************************
    // ACTORS
    
    /** The main table of the popin. */
    private Table mainTable;
    
    /** The inventory table. */
    private Table inventoryTable;
    private Label money;
    private Table[] slots = new Table[16];
    private Image[] slotImages = new Image[16];
    private Label[] slotQuantities = new Label[16];
    private TextButton[] slotDropBtns = new TextButton[16];

    private List<TextButton> inventoryDropButtons = new ArrayList<>();
    
    /** The loot table. */
    private Table lootTable;
    private Label lootTableTitle;
    private ScrollPane lootableItemsScroll;
    private Table lootableItemsTable;
    private List<TextButton> lootTakeBtns = new ArrayList<>();
    private TextButton takeAllBtn;
    private ChangeListener takeAllListener;
    

    
    /**
     * Constructor.
     * @param r the room
     * @param s the stage to draw on
     * @param p the player
     */
    public LootPopinRenderer(Room r, Stage s, Entity p) {
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
    	
    	
    	//Handle interruption
    	boolean interrupted = handleInterruption();
    	if (interrupted) return;
    	
    	
    	// Check if the inventory is displayed
    	if (inventoryCompo.getDisplayMode() == InventoryDisplayModeEnum.LOOT) {
    		
    		if (room.getState() != RoomState.LOOT_POPIN && !inventoryCompo.isInventoryActionInProgress()) {
	    		lootDisplayed = true;
	
				// Create the inventory table and set the state
	    		previousState = room.getNextState();
	    		if (previousState == null || previousState == RoomState.LOOT_POPIN) {
	    			previousState = room.getState();
	    		}
	    		room.setNextState(RoomState.LOOT_POPIN);
		    		
		    	if (mainTable == null) {
		    		mainTable = new Table();
	//	    		mainTable.setDebug(true);
	
		    		createLootTable();
		    		createInventoryTable();
		    		
		        	mainTable.pack();
		        	mainTable.setPosition(GameScreen.SCREEN_W/2 - mainTable.getWidth()/2, GameScreen.SCREEN_H/2 - mainTable.getHeight()/2);
	    		}
    		}
	    	
    		if (needsRefresh || inventoryCompo.isNeedInventoryRefresh()) {
    			lootableCompo = Mappers.lootableComponent.get(inventoryCompo.getLootableEntity());

    			refreshLootTable();
    			refreshInventory();
    			
    			handleTakeAllAction();

	    		stage.addActor(mainTable);
	    		
	    		finishRefresh();
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
		
		TextureRegionDrawable lootBackground = new TextureRegionDrawable(Assets.inventory_background);
		lootTable.setBackground(lootBackground);
		lootTable.align(Align.top);

		// 1 - Title
		lootTableTitle = new Label("Title", PopinService.hudStyle());
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

		
		takeAllBtn = new TextButton("Take all", PopinService.bigButtonStyle());
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

		TextureRegionDrawable lootBackground = new TextureRegionDrawable(Assets.inventory_lootable_item_background);
		oneItem.setBackground(lootBackground);
		
		oneItem.left();
		Image image = new Image(Assets.getTexture(itemComponent.getItemImageName() + "-full"));
		oneItem.add(image).width(Value.percentWidth(1f, image)).pad(0, 20, 0, 20);
		
		Label itemName = new Label(itemComponent.getItemLabel(), PopinService.hudStyle());
		itemName.setWrap(true);
		oneItem.add(itemName).width(Value.percentWidth(0.50f, oneItem)).padRight(20);
		
		TextButton takeBtn = new TextButton("Take", PopinService.smallButtonStyle());
		takeBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				//add item in inventory and remove it from lootable entity
				if (inventoryCompo.canStore(itemComponent)) {
					boolean stored = inventoryCompo.store(item, itemComponent, null);
					if (stored) {
						lootableCompo.getItems().remove(item);
					}
					inventoryCompo.setInventoryActionInProgress(true);
					room.turnManager.endPlayerTurn();
					refreshPopin();
				}
			}
		});
		if (inventoryCompo.isInventoryActionInProgress()) takeBtn.setDisabled(true);
		lootTakeBtns.add(takeBtn);
		
		oneItem.add(takeBtn).padRight(20);
		
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
			    		
		TextureRegionDrawable topBackground = new TextureRegionDrawable(Assets.inventory_background);
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
		Image moneyImage = new Image(Assets.inventory_money);
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
		TextureRegionDrawable slotBackground = new TextureRegionDrawable(Assets.inventory_slot);
		slot.setBackground(slotBackground);

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
		final TextButton dropBtn = new TextButton("Drop", PopinService.smallButtonStyle());
		slotDropBtns[index] = dropBtn;
		inventoryDropButtons.add(dropBtn);
		dropBtn.setVisible(false);
		
		Table slotStackTable = new Table();
		slotStackTable.setTouchable(Touchable.childrenOnly);
		slotStackTable.add(dropBtn);
		slotStack.add(slotStackTable);
		slot.add(slotStack);

		slot.pack();
		return slot;
	}
	
	
	private void refreshInventory() {
		money.setText("[GOLD]" + walletCompo.getAmount());
		for(int i=0 ; i<slots.length ; i++) {
			final Table slot = slots[i];
			Image image = slotImages[i];
			Label quantity = slotQuantities[i];
			final TextButton dropBtn = slotDropBtns[i];
			final Entity item = inventoryCompo.get(i);
			
			image.clearListeners();
			dropBtn.clearListeners();
			if (item != null) {

				// An item is present in this inventory slot
				final ItemComponent itemComponent = Mappers.itemComponent.get(item);
				TextureRegionDrawable texture = new TextureRegionDrawable(Assets.getTexture(itemComponent.getItemImageName() + "-full"));
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
						lootableCompo.getItems().add(item);
						inventoryCompo.remove(item);
						inventoryCompo.setInventoryActionInProgress(true);
						room.turnManager.endPlayerTurn();
						refreshPopin();					
					}
				});
				
				
			} else {
		        boolean activeSlot = i < inventoryCompo.getNumberOfSlots();
		        if (!activeSlot) {
					TextureRegionDrawable texture = new TextureRegionDrawable(Assets.inventory_slot_disabled);
					image.setDrawable(texture);
		        } else {
		        	image.setDrawable(null);
		        }
				quantity.setText("");
		        dropBtn.setVisible(false);
			}
			
		}
	}
	

	
	//*****************************
	// Take all and interruption
	
    /**
     * Handle the "Take all" action.
     * Take items one at a time and spend a turn for each item.
     */
	private void handleTakeAllAction() {
		if (takeAllInProgess && !inventoryCompo.isInventoryActionInProgress() && lootableCompo.getItems() != null && !lootableCompo.getItems().isEmpty()) {
			Entity firstItem = lootableCompo.getItems().get(0);
			ItemComponent itemComponent = Mappers.itemComponent.get(firstItem);
			if (inventoryCompo.canStore(itemComponent)) {
				boolean stored = inventoryCompo.store(firstItem, itemComponent, null);
				if (!stored) {
					lootableCompo.getStandByItems().add(firstItem);
				}
				lootableCompo.getItems().remove(firstItem);
				inventoryCompo.setInventoryActionInProgress(true);
				room.turnManager.endPlayerTurn();
				lootableCompo.getItems().remove(firstItem);
				refreshPopin();
			} else {
				takeAllInProgess = false;
				lootableCompo.finishTakeAll();
				refreshPopin();
			}
		} else {
			takeAllInProgess = false;
			lootableCompo.finishTakeAll();
			refreshPopin();
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
		lootDisplayed = false;
		inventoryCompo.setDisplayMode(InventoryDisplayModeEnum.NONE);

		mainTable.remove();
		needsRefresh = true;
		
		if (room.getNextState() == null) {
			room.setNextState(previousState);
		}
	}

}
