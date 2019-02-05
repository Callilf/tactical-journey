package com.dokkaebistudio.tacticaljourney.rendering;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Pools;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.InputSingleton;
import com.dokkaebistudio.tacticaljourney.components.LootableComponent;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent.InventoryActionEnum;
import com.dokkaebistudio.tacticaljourney.enums.InventoryDisplayModeEnum;
import com.dokkaebistudio.tacticaljourney.rendering.poolables.PoolableImage;
import com.dokkaebistudio.tacticaljourney.rendering.poolables.PoolableLabel;
import com.dokkaebistudio.tacticaljourney.rendering.poolables.PoolableScrollPane;
import com.dokkaebistudio.tacticaljourney.rendering.poolables.PoolableTable;
import com.dokkaebistudio.tacticaljourney.rendering.poolables.PoolableTextButton;
import com.dokkaebistudio.tacticaljourney.rendering.poolables.PoolableTextureRegionDrawable;
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
	/** The current lootable component. */
	private LootableComponent lootableCompo;
    
    /** The state before the level up state. */
    private RoomState previousState;
    
    
    
    //***************************
    // BOOLEANS
    
	/** Whether we are in loot mode or just plain inventory mode. */
	private boolean isLoot;

	/** Whether the inventory needs refreshing. */
    private boolean needsRefresh = true;
    
    /** Whether the item popin is displayed. */
    private boolean itemPopinDisplayed = false;

    /** Whether we are in the process of "take all" items. */
    private boolean takeAllInProgess = false;
    
    
    //*****************************
    // ACTORS
    
    /** The main table of the popin. */
    private Table mainTable;
    
    /** The inventory table. */
    private Table inventoryTable;
    private Table[] slots = new Table[16];
    private List<TextButton> inventoryDropButtons = new ArrayList<>();
    
    /** The loot table. */
    private Table lootTable;
    private List<TextButton> lootTakeBtns = new ArrayList<>();
    
    /** The selected item popin. */
    private Table selectedItemPopin;
    private Label itemTitle;
    private Label itemDesc;
    private TextButton dropItemBtn;
    private ChangeListener dropListener;
    private TextButton useItemBtn;
    private ChangeListener useListener;
    
    
    //*****************************
    // STYLES
    
	private LabelStyle hudStyle;
    private TextButtonStyle bigButtonStyle;
    private TextButtonStyle smallButtonStyle;

    
    /**
     * Constructor. Initialize the styles and the main attributes.
     * @param r the room
     * @param s the stage to draw on
     * @param p the player
     */
    public InventoryPopinRenderer(Room r, Stage s, Entity p) {
        this.room = r;
        this.player = p;
        this.stage = s;
        
		hudStyle = new LabelStyle(Assets.font, Color.WHITE);
		
		Drawable btnUp = new SpriteDrawable(new Sprite(Assets.getTexture(Assets.inventory_item_popin_btn_up)));
		Drawable btnDown = new SpriteDrawable(new Sprite(Assets.getTexture(Assets.inventory_item_popin_btn_down)));
		Sprite disableSprite = new Sprite(Assets.getTexture(Assets.inventory_item_popin_btn_up));
		disableSprite.setAlpha(0.5f);
		Drawable btnDisabled = new SpriteDrawable(disableSprite);
		bigButtonStyle = new TextButtonStyle(btnUp, btnDown, null, Assets.font);
		bigButtonStyle.disabled = btnDisabled;

		Drawable sbtnUp = new SpriteDrawable(new Sprite(Assets.getTexture(Assets.lvl_up_choice_claim_btn)));
		Drawable sbtnDown = new SpriteDrawable(new Sprite(Assets.getTexture(Assets.lvl_up_choice_claim_btn_pushed)));
		Sprite sdisableSprite = new Sprite(Assets.getTexture(Assets.lvl_up_choice_claim_btn));
		sdisableSprite.setAlpha(0.5f);
		Drawable sbtnDisabled = new SpriteDrawable(sdisableSprite);
		smallButtonStyle = new TextButtonStyle(sbtnUp, sbtnDown, null, Assets.font);
		smallButtonStyle.disabled = sbtnDisabled;

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
    	
    	if (inventoryCompo != null && inventoryCompo.getDisplayMode() != InventoryDisplayModeEnum.NONE) {
    		this.isLoot = inventoryCompo.getDisplayMode() == InventoryDisplayModeEnum.LOOT;

    		if (mainTable == null) {
    			// Create the inventory table for the first time
	    		previousState = room.getNextState() != null ? room.getNextState() : room.getState();
	    		room.setNextState(RoomState.INVENTORY_POPIN);
	    		
	    		mainTable = Pools.obtain(PoolableTable.class);
    		}
	    		
    		if (needsRefresh || inventoryCompo.isNeedInventoryRefresh()) {
    			mainTable.clear();
    			
	    		if (this.isLoot) {
	    			createLootTable();
		    		mainTable.add(lootTable).padRight(20);
	    		}
	    			    		
	    		createInventoryTable();	        	
	        	mainTable.add(inventoryTable);
	        	
	        	mainTable.pack();
	        	mainTable.setPosition(GameScreen.SCREEN_W/2 - mainTable.getWidth()/2, GameScreen.SCREEN_H/2 - mainTable.getHeight()/2);
	        	
	    		stage.addActor(mainTable);
	    		
	    		
	    		if (takeAllInProgess && !inventoryCompo.isInventoryActionInProgress() && lootableCompo.getItems() != null && !lootableCompo.getItems().isEmpty()) {
	    			Entity firstItem = lootableCompo.getItems().get(0);
	    			if (inventoryCompo.canStore()) {
						inventoryCompo.store(firstItem, null);
						inventoryCompo.setInventoryActionInProgress(true);
						room.turnManager.endPlayerTurn();
						lootableCompo.getItems().remove(firstItem);
						refreshPopin();
					} else {
						takeAllInProgess = false;
					}
	    		} else {
	    			takeAllInProgess = false;
	    		}
	    		
	    		
	    		needsRefresh = false;
	    		inventoryCompo.setNeedInventoryRefresh(false);
    		}
    		
    		// Draw the table
            stage.act(Gdx.graphics.getDeltaTime());
    		stage.draw();
    		
    		// Close the inventory on a left click outside the popin
    		if (InputSingleton.getInstance().leftClickJustPressed) {
    			closePopin();
    		}

    	} else if (room.getState() == RoomState.INVENTORY_POPIN) {
    		// Close the inventory if inventoryCompo.isInventoryDisplayed() was switched to false
    		closePopin();
    	}
    
    }

	private void createLootTable() {
		lootableCompo = Mappers.lootableComponent.get(inventoryCompo.getLootableEntity());
		
		lootTable = Pools.obtain(PoolableTable.class);
//		    		table.setDebug(true, true);
		lootTable.setTouchable(Touchable.enabled);
		lootTable.addListener(new ClickListener() {});
		
		TextureRegionDrawable lootBackground = PoolableTextureRegionDrawable.create(Assets.getTexture(Assets.inventory_background));
		lootTable.setBackground(lootBackground);
		lootTable.align(Align.top);

		// 1 - Title
		Label title = PoolableLabel.create(lootableCompo.getType().getLabel() + " ([GREEN]" + lootableCompo.getItems().size() + "[WHITE] items)", hudStyle);
		lootTable.add(title).uniformX().pad(40, 0, 40, 0);
		lootTable.row();
		
		Table lootableItemsTable = Pools.obtain(PoolableTable.class);
		lootableItemsTable.top();
		lootTakeBtns.clear();
		for (Entity item : lootableCompo.getItems()) {
			Table oneItem = createOneLootItem(item);
			lootableItemsTable.add(oneItem).pad(0, 10, 10, 10);
			lootableItemsTable.row();
		}
		
		lootableItemsTable.pack();
		
		ScrollPane lootableItems = PoolableScrollPane.create(lootableItemsTable);
		lootTable.add(lootableItems).fill().expand().maxHeight(535);
		lootTable.row();
		
		Table btnTable = Pools.obtain(PoolableTable.class);
		
		TextButton closeBtn = PoolableTextButton.create("Close", bigButtonStyle);
		// Close listener
		closeBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				closePopin();
			}
		});
		btnTable.add(closeBtn).pad(0, 0, 0, 20);

		
		TextButton takeAllBtn = PoolableTextButton.create("Take all", bigButtonStyle);
		// Close listener
		takeAllBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				takeAllInProgess = true;
				refreshPopin();
			}
		});
		if (inventoryCompo.isInventoryActionInProgress()) takeAllBtn.setDisabled(true);

		btnTable.add(takeAllBtn).pad(0, 20, 0, 0);
		btnTable.pack();
		
		lootTable.add(btnTable).pad(40, 0, 40, 0);

		
		lootTable.pack();
	}

	private Table createOneLootItem(final Entity item) {
		ItemComponent itemComponent = Mappers.itemComponent.get(item);
		Table oneItem = Pools.obtain(PoolableTable.class);
//		oneItem.setDebug(true);

		TextureRegionDrawable lootBackground = PoolableTextureRegionDrawable.create(Assets.getTexture(Assets.inventory_lootable_item_background));
		oneItem.setBackground(lootBackground);
		
		oneItem.left();
		Image image = PoolableImage.create(Assets.getTexture(itemComponent.getItemType().getImageName() + "-full"));
		oneItem.add(image).width(Value.percentWidth(1f, image)).pad(0, 20, 0, 20);
		
		Label itemName = PoolableLabel.create(itemComponent.getItemType().getLabel(), hudStyle);
		itemName.setWrap(true);
		oneItem.add(itemName).width(Value.percentWidth(0.50f, oneItem)).padRight(20);
		
		TextButton takeBtn = PoolableTextButton.create("Take", smallButtonStyle);
		takeBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				//TODO add item in inventory and remove it from lootable entity
				if (inventoryCompo.canStore()) {
					inventoryCompo.store(item, null);
					inventoryCompo.setInventoryActionInProgress(true);
					room.turnManager.endPlayerTurn();
					lootableCompo.getItems().remove(item);
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

	private void createInventoryTable() {
		inventoryTable = Pools.obtain(PoolableTable.class);
//	    		table.setDebug(true, true);
		inventoryTable.setTouchable(Touchable.enabled);
		inventoryTable.addListener(new ClickListener() {});
			    		
		TextureRegionDrawable topBackground = PoolableTextureRegionDrawable.create(Assets.getTexture(Assets.inventory_background));
		inventoryTable.setBackground(topBackground);
		
		inventoryTable.align(Align.top);
		
		// 1 - Title
		Label title = PoolableLabel.create("Inventory", hudStyle);
		inventoryTable.add(title).uniformX().pad(40, 0, 40, 0);
		inventoryTable.row();
		
		
		// 2 - Inventory slots
		inventoryDropButtons.clear();
		Table slotsTable = Pools.obtain(PoolableTable.class);
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
		
		inventoryTable.pack();
	}

    /**
     * Create an inventory slot (filled or empty).
     * @param index the index of the slot
     * @return the created slot
     */
	private Table createSlot(int index) {
		boolean activeSlot = index < inventoryCompo.getNumberOfSlots();
		
		final Table slot = Pools.obtain(PoolableTable.class);
//		slot.setDebug(true);

		//Background
		TextureRegionDrawable slotBackground = null;
		if (!activeSlot) {
			slotBackground = PoolableTextureRegionDrawable.create(Assets.getTexture(Assets.inventory_slot_disabled));
		} else {
			slotBackground = PoolableTextureRegionDrawable.create(Assets.getTexture(Assets.inventory_slot));
		}
		slot.setBackground(slotBackground);
		
		
		// Add the item if needed
		if (activeSlot) {
			final Entity item = inventoryCompo.get(index);
			
			if (item != null) {
				
				// An item is present in this inventory slot
				final ItemComponent itemComponent = Mappers.itemComponent.get(item);
				Image img = PoolableImage.create(Assets.getTexture(itemComponent.getItemType().getImageName() + "-full"));
				
				if (isLoot) {
					
					// LOOT case :
					// Display the item. If there is a click on it, display a "Drop button".
					// On a click on this drop button, store the item in the lootable.
					
					final Stack slotStack = new Stack();
					Table imageStackTable = Pools.obtain(PoolableTable.class);
					imageStackTable.add(img);
					slotStack.add(imageStackTable);

					//Add the drop button
					final TextButton dropBtn = PoolableTextButton.create("Drop", smallButtonStyle);
					dropBtn.setVisible(false);
					inventoryDropButtons.add(dropBtn);
					
					if (!inventoryCompo.isInventoryActionInProgress()) {
						img.addListener(new ClickListener() {
							
							@Override
							public void clicked(InputEvent event, float x, float y) {
								for (TextButton tb : inventoryDropButtons) {
									tb.setVisible(false);
								}
								dropBtn.setVisible(true);
							}
						});
					}
					
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
					
					
					
					Table slotStackTable = Pools.obtain(PoolableTable.class);
					slotStackTable.add(dropBtn);
					slotStack.add(slotStackTable);
					slot.add(slotStack);
				} else {
					
					// Simple inventory case : 
					// display the image, and on a click on it display the item popin
					slot.add(img);

					slot.addListener(new ClickListener() {
						
						@Override
						public void clicked(InputEvent event, float x, float y) {
							displaySelectedItemPopin( item, slot);
						}
					});
					
				}
				
				
			} else {
				
				// Empty slot
				Label l = PoolableLabel.create("empty", hudStyle);
				slot.add(l);
			
			}
		}
		slot.pack();

		return slot;
	}
	
	
	/**
	 * DIsplay the popin of the selected item with it's title, description and possible actions.
	 * @param item the item selected
	 * @param slot the slot on which the item was
	 */
	private void displaySelectedItemPopin(final Entity item, final Table slot) {
		if (selectedItemPopin == null) {
			selectedItemPopin = Pools.obtain(PoolableTable.class);
//			selectedItemPopin.setDebug(true);

			// Add an empty click listener to capture the click so that the InputSingleton doesn't handle it
			selectedItemPopin.setTouchable(Touchable.enabled);
			selectedItemPopin.addListener(new ClickListener() {});
			
			// Place the popin and add the background texture
			selectedItemPopin.setPosition(GameScreen.SCREEN_W/2, GameScreen.SCREEN_H/2);
			TextureRegionDrawable textureRegionDrawable = PoolableTextureRegionDrawable.create(Assets.getTexture(Assets.inventory_item_popin_background));
			selectedItemPopin.setBackground(textureRegionDrawable);
			
			selectedItemPopin.align(Align.top);
			
			// 1 - Title
			itemTitle = PoolableLabel.create("Title", hudStyle);
			selectedItemPopin.add(itemTitle).top().align(Align.top).pad(20, 0, 20, 0);
			selectedItemPopin.row().align(Align.center);
			
			// 2 - Description
			itemDesc = PoolableLabel.create("Un test de description d'idem qui est assez long pour voir jusqu'ou on peut aller. Un test de description d'idem qui est assez long pour voir jusqu'ou on peut aller. Un test de description d'idem qui est assez long pour voir jusqu'ou on peut aller.", hudStyle);
			itemDesc.setWrap(true);
			selectedItemPopin.add(itemDesc).growY().width(textureRegionDrawable.getMinWidth()).left().pad(0, 20, 0, 20);
			selectedItemPopin.row();
			
			// 3 - Action buttons
			Table buttonTable = Pools.obtain(PoolableTable.class);
			
			// 3.1 - Close button
			final TextButton closeBtn = PoolableTextButton.create("Close",bigButtonStyle);			
			// continueButton listener
			closeBtn.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					hideSelectedItemPopin();
				}
			});
			buttonTable.add(closeBtn).pad(0, 20,0,20);
			
			// 3.2 - Drop button
			dropItemBtn = PoolableTextButton.create("Drop",bigButtonStyle);			
			buttonTable.add(dropItemBtn).pad(0, 20,0,20);

			if (!this.isLoot) {
				// 3.3 - Use button
				useItemBtn = PoolableTextButton.create("Use",bigButtonStyle);			
				buttonTable.add(useItemBtn).pad(0, 20,0,20);
			}
			
			selectedItemPopin.add(buttonTable).pad(20, 0, 20, 0);
			
		}
		
		
		final ItemComponent itemComponent = Mappers.itemComponent.get(item);
		
		// Update the content
		itemTitle.setText(itemComponent.getItemType().getLabel());
		itemDesc.setText(itemComponent.getItemType().getDescription());
		
		// Update the Drop item listener
		updateDropListener(item, slot);
		
		if (!this.isLoot) {
			useItemBtn.setText(itemComponent.getItemType().getActionLabel());
			// Update the Use item listener
			updateUseListener(item, slot);
		}
		
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
		selectedItemPopin = null;
		itemPopinDisplayed = false;
	}
	
	private void refreshPopin() {
		needsRefresh = true;		
	}

	/**
	 * Close the level up popin and unpause the game.
	 */
	private void closePopin() {
		inventoryCompo.setDisplayMode(InventoryDisplayModeEnum.NONE);
		
		if (itemPopinDisplayed) hideSelectedItemPopin();

		mainTable.clear();
		mainTable.remove();
		mainTable = null;
		needsRefresh = true;
		
		if (room.getNextState() == null) {
			room.setNextState(previousState);
		}
	}

}
