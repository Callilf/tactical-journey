package com.dokkaebistudio.tacticaljourney.rendering;

import java.util.ArrayList;
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
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.assets.SceneAssets;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.components.neutrals.SoulbenderComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AmmoCarrierComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent.InventoryActionEnum;
import com.dokkaebistudio.tacticaljourney.enums.InventoryDisplayModeEnum;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.items.AbstractItem;
import com.dokkaebistudio.tacticaljourney.items.ItemArrow;
import com.dokkaebistudio.tacticaljourney.items.ItemBomb;
import com.dokkaebistudio.tacticaljourney.rendering.interfaces.Renderer;
import com.dokkaebistudio.tacticaljourney.rendering.service.PopinService;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.singletons.InputSingleton;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class InventoryPopinRenderer implements Renderer, RoomSystem {
	    
	//****************************
	// Main attributes
	
	/** The stage. */
	public Stage stage;
	/** The current room. */
    private Room room;
	
	/** The inventory component of the player (kept in cache to prevent getting it at each frame). */
	private InventoryComponent inventoryCompo;
	private AmmoCarrierComponent ammoCarrierCompo;
    
    
    //***************************
    // BOOLEANS
    
	/** Whether the inventory needs refreshing. */
    private boolean needsRefresh = true;
    
    /** Whether the item popin is displayed. */
    private boolean itemPopinDisplayed = false;
    private boolean infusionPopinDisplayed = false;
    private boolean recyclingPopinDisplayed = false;
    
    //*****************************
    // ACTORS
    
    /** The main table of the popin. */
    private Table mainTable;
    private Label title;
    private TextButton closeBtn;
    private Image quiverImage;
    private Image bombBagImage;
    private Label arrowQuantity;
    private Label bombQuantity;
    private Label arrowPrice;
    private Label bombPrice;
    private Table arrowSlot;
    private Table bombSlot;
    
    /** The inventory table. */
    private Table inventoryTable;
//    private Label money;
    private Table slotsTable;
    private ScrollPane slotsScroll;
    private Table[] slots = new Table[96];
    private Image[] slotImages = new Image[96];
    private Label[] slotQuantities = new Label[96];
    private Label[] slotPrices = new Label[96];
    private List<TextButton> inventoryDropButtons = new ArrayList<>();
    
    
    /** The selected item popin. */
    private Table selectedItemPopin;
    private Label itemTitle;
    private Label itemDesc;
    private TextButton dropItemBtn;
    private ChangeListener dropListener;
    private TextButton useItemBtn;
    private ChangeListener useListener;
    private TextButton throwItemBtn;
    private ChangeListener throwListener;


    /** The Infusion popin. */
    private Table infusionPopin;
    private Label infusionTitle;
    private Label infusionDesc;
    private TextButton infuseBtn;
    private ChangeListener infuseListener;

    
    /** The Recycle popin. */
    private Table recyclingPopin;
    private Label recyclingTitle;
    private Label recyclingDesc;
    private TextButton recyclingBtn;
    private ChangeListener recyclingListener;
    
    // Used for arrow and bomb recycling
    private ItemArrow itemArrow;
    private ItemBomb itemBomb;
    
    /**
     * Constructor.
     * @param r the room
     * @param s the stage to draw on
     * @param p the player
     */
    public InventoryPopinRenderer(Room r, Stage s) {
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
    		itemArrow = new ItemArrow();
    		itemArrow.setLabel("Arrow");
    		itemBomb = new ItemBomb();
    		itemBomb.setLabel("Bomb");
    	}

    	
    	// Check if the inventory is displayed
    	if (inventoryCompo.getDisplayMode().isInventoryPopin() && (room.getState() != RoomState.INVENTORY_POPIN || needsRefresh)) {
			//set the state
    		room.setNextState(RoomState.INVENTORY_POPIN);

    		if (mainTable == null) {
	    		createInventoryTable();
	    		
    			// Close popin with ESCAPE
	    		stage.addListener(new InputListener() {
					@Override
					public boolean keyUp(InputEvent event, int keycode) {
						if (room.getState() == RoomState.INVENTORY_POPIN && (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK)) {
							closePopin();
							return true;
						}
						return super.keyUp(event, keycode);
					}
				});
	    	}
	    		
    		if (needsRefresh || inventoryCompo.isNeedInventoryRefresh()) {
	        	
//    			money.setText("[GOLD]" + walletCompo.getAmount());
    			switch(inventoryCompo.getDisplayMode()) {
    			case INFUSION:
    				title.setText("Select the item to infuse");
    				break;
    			case RECYCLING:
    				title.setText("Select the item to sell");
    				break;
    				
    			default:
        				title.setText("Inventory");
    			}
    			
    			// Update arrow and bomb quantities
    			quiverImage.addAction(Actions.alpha(1f));
    			bombBagImage.addAction(Actions.alpha(1f));
    			arrowQuantity.setText(ammoCarrierCompo.getArrows() + "/" + ammoCarrierCompo.getMaxArrows());
    			bombQuantity.setText(ammoCarrierCompo.getBombs() + "/" + ammoCarrierCompo.getMaxBombs());
				if (inventoryCompo.getDisplayMode() == InventoryDisplayModeEnum.RECYCLING) {
					arrowPrice.setText("[GOLDENROD]" + itemArrow.getRecyclePrice());
					bombPrice.setText("[GOLDENROD]" + itemBomb.getRecyclePrice());
					if (ammoCarrierCompo.getArrows() <= 0) quiverImage.addAction(Actions.alpha(0.3f));
					if (ammoCarrierCompo.getBombs() <= 0) bombBagImage.addAction(Actions.alpha(0.3f));
				} else {
					arrowPrice.setText("");
					bombPrice.setText("");
				}
				arrowSlot.clearListeners();
				bombSlot.clearListeners();
				if (inventoryCompo.getDisplayMode() == InventoryDisplayModeEnum.RECYCLING && ammoCarrierCompo.getArrows() > 0) {
					arrowSlot.addListener(new ClickListener() {
						@Override
						public void clicked(InputEvent event, float x, float y) {
							displayRecyclingPopin(null, itemArrow, arrowSlot);
						}
					});
				}
				if (inventoryCompo.getDisplayMode() == InventoryDisplayModeEnum.RECYCLING && ammoCarrierCompo.getBombs() > 0) {
					bombSlot.addListener(new ClickListener() {
						@Override
						public void clicked(InputEvent event, float x, float y) {
							displayRecyclingPopin(null, itemBomb, bombSlot);
						}
					});
				}



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
    			
    			for(int i=0 ; i<nbSlots ; i++) {
    				final Table slot = slots[i];
					Image image = slotImages[i];
					Label quantity = slotQuantities[i];
					Label price = slotPrices[i];
    				final Entity item = inventoryCompo.get(i);
    				
    				slot.clearListeners();
    				slot.setVisible(true);
    				image.addAction(Actions.alpha(1f));
    				if (item != null) {
    					// An item is present in this inventory slot
    					final ItemComponent itemComponent = Mappers.itemComponent.get(item);
    					TextureRegionDrawable texture = new TextureRegionDrawable(Assets.loadAndGetTexture(itemComponent.getItemImageName().getNameFull()).getRegion());
    					image.setDrawable(texture);
    					if (inventoryCompo.getDisplayMode() == InventoryDisplayModeEnum.INFUSION && !itemComponent.isInfusable()) {
    						image.addAction(Actions.alpha(0.3f));
    					}
    					
    					quantity.setText(inventoryCompo.getQuantity(i) > 1 ? String.valueOf(inventoryCompo.getQuantity(i)) : "");
    					
    					if (inventoryCompo.getDisplayMode() == InventoryDisplayModeEnum.RECYCLING) {
	    					if (itemComponent.getItemRecyclePrice() != null) {
	    						price.setText("[GOLDENROD]" + itemComponent.getItemRecyclePrice());
	    					} else {
	    						image.addAction(Actions.alpha(0.3f));
	    						price.setText("");
	    					}
    					} else {
    						price.setText("");
    					}
    					
    					slot.clearListeners();
    					
    					if (inventoryCompo.getDisplayMode() == InventoryDisplayModeEnum.INVENTORY) {
	    					slot.addListener(new ClickListener() {
	    						@Override
	    						public void clicked(InputEvent event, float x, float y) {
	    							displaySelectedItemPopin( item, slot);
	    						}
	    					});
    					} else if (inventoryCompo.getDisplayMode() == InventoryDisplayModeEnum.INFUSION && itemComponent.isInfusable()) {
    						slot.addListener(new ClickListener() {
	    						@Override
	    						public void clicked(InputEvent event, float x, float y) {
	    							displayInfusionPopin( item, slot);
	    						}
	    					});
    					} else if (inventoryCompo.getDisplayMode() == InventoryDisplayModeEnum.RECYCLING && itemComponent.getItemRecyclePrice() != null) {
    						slot.addListener(new ClickListener() {
	    						@Override
	    						public void clicked(InputEvent event, float x, float y) {
	    							displayRecyclingPopin( item, itemComponent.getItemType(), slot);
	    						}
	    					});
    					}

    					
    				} else {
    			        boolean activeSlot = i < inventoryCompo.getNumberOfSlots();
			        	image.setDrawable(null);
    			        quantity.setText("");
    			        price.setText("");
    			        if (!activeSlot) {
	    					slot.setVisible(false);
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
			    		
		NinePatchDrawable ninePatchDrawable = new NinePatchDrawable(SceneAssets.popinNinePatch);
		ninePatchDrawable.setMinWidth(653);
		ninePatchDrawable.setMinHeight(746);
		inventoryTable.setBackground(ninePatchDrawable);

		
		inventoryTable.align(Align.top);
		
		// 1 - Title
		Table topTable = new Table();
		title = new Label("Inventory", PopinService.hudStyle());
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
		inventoryTable.row();
		
		closeBtn = new TextButton("Close", PopinService.buttonStyle());
		closeBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				closePopin();
			}
		});
		inventoryTable.add(closeBtn).pad(20, 0, 20, 0);
		

		arrowQuantity = new Label("0", PopinService.hudStyle());
		bombQuantity = new Label("0", PopinService.hudStyle());
		quiverImage = new Image(Assets.loadAndGetTexture(itemArrow.getTexture().getNameFull()).getRegion());
		bombBagImage = new Image(Assets.loadAndGetTexture(itemBomb.getTexture().getNameFull()).getRegion());
		arrowPrice = new Label("0", PopinService.hudStyle());
		bombPrice = new Label("0", PopinService.hudStyle());
		Table arrowsAndBombs = createArrowsAndBombTable(quiverImage, bombBagImage, 
				arrowQuantity, bombQuantity,
				arrowPrice, bombPrice);
		arrowSlot = (Table) arrowsAndBombs.getChildren().get(1);
		bombSlot = (Table) arrowsAndBombs.getChildren().get(3);
		
		
		
    	mainTable.add(inventoryTable).padRight(5);
    	mainTable.add(arrowsAndBombs);
    	mainTable.pack();
    	mainTable.setPosition(GameScreen.SCREEN_W/2 - mainTable.getWidth()/2, GameScreen.SCREEN_H/2 - mainTable.getHeight()/2);
	}


	public static Table createArrowsAndBombTable(Image arrowImage, Image bombImage, Label arrowQuantity, Label bombQuantity,
			Label arrowPrice, Label bombPrice) {
		Table arrowsAndBombs = new Table();
		arrowsAndBombs.setTouchable(Touchable.enabled);
		arrowsAndBombs.addListener(new ClickListener() {});
			    		
		NinePatchDrawable arrowsAndBombsBackground = new NinePatchDrawable(SceneAssets.popinNinePatch);
		arrowsAndBombs.setBackground(arrowsAndBombsBackground);

		arrowsAndBombs.align(Align.top);
		
		// 1 - Title
		Label arrowsLabel = new Label("Quiver", PopinService.hudStyle());
		arrowsLabel.setAlignment(Align.center);
		arrowsAndBombs.add(arrowsLabel).uniformX().pad(20, 0, 10, 0);
		arrowsAndBombs.row();
		
		Table quiverTable = new Table();
		NinePatchDrawable slotBackground = new NinePatchDrawable(SceneAssets.popinInnerNinePatch);
		slotBackground.setMinWidth(140);
		slotBackground.setMinHeight(140);
		quiverTable.setBackground(slotBackground);

		Stack quiver = new Stack();
		quiver.add(arrowImage);
		arrowQuantity.setAlignment(Align.bottomLeft);
		quiver.add(arrowQuantity);
		if (arrowPrice != null) {
			arrowPrice.setAlignment(Align.topLeft);
			quiver.add(arrowPrice);
		}
		quiverTable.add(quiver);
		arrowsAndBombs.add(quiverTable).uniformX().pad(10, 0, 40, 0);
		arrowsAndBombs.row();

		Label bombsLabel = new Label("Bomb bag", PopinService.hudStyle());
		bombsLabel.setAlignment(Align.center);
		arrowsAndBombs.add(bombsLabel).uniformX().pad(20, 0, 10, 0);
		arrowsAndBombs.row();
		
		Table bombBagTable = new Table();
		bombBagTable.setBackground(slotBackground);
		Stack bombBag = new Stack();
		bombBag.add(bombImage);
		bombQuantity.setAlignment(Align.bottomLeft);
		bombBag.add(bombQuantity);
		if (bombPrice != null) {
			bombPrice.setAlignment(Align.topLeft);
			bombBag.add(bombPrice);
		}
		bombBagTable.add(bombBag);
		arrowsAndBombs.add(bombBagTable).uniformX().pad(10, 0, 20, 0);
		arrowsAndBombs.row();
		return arrowsAndBombs;
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

		Stack imageStack = new Stack();

		Image img = new Image();
		slotImages[index] = img;
		imageStack.add(img);
		
		Label quantity = new Label("", PopinService.hudStyle());
		quantity.setAlignment(Align.bottomLeft);
		slotQuantities[index] = quantity;
		imageStack.add(quantity);
		
		Label price = new Label("", PopinService.hudStyle());
		price.setAlignment(Align.topLeft);
		slotPrices[index] = price;
		imageStack.add(price);
		
		slot.add(imageStack).expand().pad(-5, -5, -5, -5);
		
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
			NinePatchDrawable ninePatchDrawable = new NinePatchDrawable(SceneAssets.popinNinePatch);
			selectedItemPopin.setBackground(ninePatchDrawable);
			
			selectedItemPopin.align(Align.top);
			
			// 1 - Title
			itemTitle = new Label("Title", PopinService.hudStyle());
			selectedItemPopin.add(itemTitle).top().align(Align.top).pad(20, 0, 20, 0);
			selectedItemPopin.row().align(Align.center);
			
			// 2 - Description
			itemDesc = new Label("Un test de description d'idem qui est assez long pour voir jusqu'ou on peut aller. Un test de description d'idem qui est assez long pour voir jusqu'ou on peut aller. Un test de description d'idem qui est assez long pour voir jusqu'ou on peut aller.", PopinService.hudStyle());
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
			
			// 3.2 - Drop button
			dropItemBtn = new TextButton("Drop",PopinService.buttonStyle());			
			buttonTable.add(dropItemBtn).pad(0, 20,0,20);

			// 3.3 - Use button
			useItemBtn = new TextButton("Use",PopinService.buttonStyle());			
			buttonTable.add(useItemBtn).pad(0, 20,0,20);
			
			// 3.4 - Throw button
			throwItemBtn = new TextButton("Throw",PopinService.buttonStyle());		
			buttonTable.add(throwItemBtn).pad(0, 20,0,20);

			
			selectedItemPopin.add(buttonTable).pad(20, 0, 20, 0);
			
		}
		
		
		final ItemComponent itemComponent = Mappers.itemComponent.get(item);
		
		// Update the content
		itemTitle.setText(itemComponent.getItemLabel());
		itemDesc.setText(itemComponent.getItemDescription());
		
		if (itemComponent.getItemActionLabel() != null) {
			useItemBtn.setVisible(true);
			useItemBtn.setText(itemComponent.getItemActionLabel());
		} else {
			useItemBtn.setVisible(false);
		}

		// Update the Drop item listener
		updateDropListener(item, slot);
		
		// Update the Use item listener
		updateUseListener(item, slot);
		
		// Update the throw item listener
		updateThrowListener(item, slot);
		
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
	
	private void updateThrowListener(final Entity item, final Table slot) {
		if (throwListener != null) {
			throwItemBtn.removeListener(throwListener);
		}
		throwListener = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				inventoryCompo.requestAction(InventoryActionEnum.THROW, item);
				slot.removeListener(this);
				hideSelectedItemPopin();
				closePopin();
			}
		};
		throwItemBtn.addListener(throwListener);
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
				closePopin();
			}
		};
		dropItemBtn.addListener(dropListener);
	}
	
	private void hideSelectedItemPopin() {
		selectedItemPopin.remove();
		itemPopinDisplayed = false;
	}
	
	
	
	
	
	
	//*********************************
	// Infusion popin
	
	
	/**
	 * Display the popin to validate the infusion.
	 * @param item the item selected
	 * @param slot the slot on which the item was
	 */
	private void displayInfusionPopin(final Entity item, final Table slot) {
		if (infusionPopin == null) {
			infusionPopin = new Table();
//			infusionPopin.setDebug(true);

			// Add an empty click listener to capture the click so that the InputSingleton doesn't handle it
			infusionPopin.setTouchable(Touchable.enabled);
			infusionPopin.addListener(new ClickListener() {});
			
			// Place the popin and add the background texture
			infusionPopin.setPosition(GameScreen.SCREEN_W/2, GameScreen.SCREEN_H/2);
			NinePatchDrawable ninePatchDrawable = new NinePatchDrawable(SceneAssets.popinNinePatch);
			infusionPopin.setBackground(ninePatchDrawable);
			
			infusionPopin.align(Align.top);
			
			// 1 - Title
			infusionTitle = new Label("Title", PopinService.hudStyle());
			infusionPopin.add(infusionTitle).top().align(Align.top).pad(20, 0, 20, 0);
			infusionPopin.row().align(Align.center);
			
			// 2 - Description
			infusionDesc = new Label("Are you sure ?", PopinService.hudStyle());
			infusionDesc.setWrap(true);
			infusionPopin.add(infusionDesc).growY().width(900).left().pad(0, 20, 0, 20);
			infusionPopin.row();
			
			// 3 - Action buttons
			Table buttonTable = new Table();
			
			// 3.1 - Close button
			final TextButton closeBtn = new TextButton("Cancel",PopinService.buttonStyle());			
			// continueButton listener
			closeBtn.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					hideInfusionPopin();
				}
			});
			buttonTable.add(closeBtn).pad(0, 20,0,20);
			
			// 3.2 - Drop button
			infuseBtn = new TextButton("Infuse",PopinService.buttonStyle());			
			buttonTable.add(infuseBtn).pad(0, 20,0,20);

			
			infusionPopin.add(buttonTable).pad(20, 0, 20, 0);
			
		}
		
		
		final ItemComponent itemComponent = Mappers.itemComponent.get(item);
		
		// Update the content
		infusionTitle.setText("Infusion");
		
		SoulbenderComponent soulbenderComponent = Mappers.soulbenderComponent.get(inventoryCompo.getSoulbender());
		if (soulbenderComponent.getPrice() == 0) {
			infusionDesc.setText("Are you sure you want to infuse the " + itemComponent.getItemLabel() + "?");
		} else {
			infusionDesc.setText("Are you sure you want to infuse the " + itemComponent.getItemLabel() 
				+ " for [GOLDENROD]" + soulbenderComponent.getPrice() + " gold coins[]?");
		}
		
		// Update the throw item listener
		updateInfuseListener(item, slot);
		
		// Place the popin properly
		infusionPopin.pack();
		infusionPopin.setPosition(GameScreen.SCREEN_W/2 - infusionPopin.getWidth()/2, GameScreen.SCREEN_H/2 - infusionPopin.getHeight()/2);
	
		infusionPopinDisplayed = true;
		this.stage.addActor(infusionPopin);
	}
	
	private void updateInfuseListener(final Entity item, final Table slot) {
		if (infuseListener != null) {
			infuseBtn.removeListener(infuseListener);
		}
		infuseListener = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				inventoryCompo.requestAction(InventoryActionEnum.INFUSE, item);
				slot.removeListener(this);				
				closePopin();
			}
		};
		infuseBtn.addListener(infuseListener);
	}
	
	private void hideInfusionPopin() {
		infusionPopin.remove();
		infusionPopinDisplayed = false;
	}
	
	
	
	//*********************************
	// Infusion popin
	
	
	/**
	 * Display the popin to validate the infusion.
	 * @param item the item selected
	 * @param slot the slot on which the item was
	 */
	private void displayRecyclingPopin(final Entity itemEntity, final AbstractItem item, final Table slot) {
		if (recyclingPopin == null) {
			recyclingPopin = new Table();
//			recyclingPopin.setDebug(true);

			// Add an empty click listener to capture the click so that the InputSingleton doesn't handle it
			recyclingPopin.setTouchable(Touchable.enabled);
			recyclingPopin.addListener(new ClickListener() {});
			
			// Place the popin and add the background texture
			recyclingPopin.setPosition(GameScreen.SCREEN_W/2, GameScreen.SCREEN_H/2);
			NinePatchDrawable ninePatchDrawable = new NinePatchDrawable(SceneAssets.popinNinePatch);
			recyclingPopin.setBackground(ninePatchDrawable);
			
			recyclingPopin.align(Align.top);
			
			// 1 - Title
			recyclingTitle = new Label("Title", PopinService.hudStyle());
			recyclingPopin.add(recyclingTitle).top().align(Align.top).pad(20, 0, 20, 0);
			recyclingPopin.row().align(Align.center);
			
			// 2 - Description
			recyclingDesc = new Label("Are you sure ?", PopinService.hudStyle());
			recyclingDesc.setWrap(true);
			recyclingPopin.add(recyclingDesc).growY().width(900).left().pad(0, 20, 0, 20);
			recyclingPopin.row();
			
			// 3 - Action buttons
			Table buttonTable = new Table();
			
			// 3.1 - Close button
			final TextButton closeBtn = new TextButton("Cancel",PopinService.buttonStyle());			
			// continueButton listener
			closeBtn.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					hideRecyclingPopin();
				}
			});
			buttonTable.add(closeBtn).pad(0, 20,0,20);
			
			// 3.2 - Drop button
			recyclingBtn = new TextButton("Recycle",PopinService.buttonStyle());			
			buttonTable.add(recyclingBtn).pad(0, 20,0,20);

			
			recyclingPopin.add(buttonTable).pad(20, 0, 20, 0);
			
		}
		
				
		// Update the content
		recyclingTitle.setText("Infusion");
		recyclingDesc.setText("Are you sure you want to recycle the " + item.getLabel()
				+ " in exchange of [GOLDENROD]" + item.getRecyclePrice() + " gold coins[]?");
		
		// Update the throw item listener
		updateRecycleListener(itemEntity, item, slot);
		
		// Place the popin properly
		recyclingPopin.pack();
		recyclingPopin.setPosition(GameScreen.SCREEN_W/2 - recyclingPopin.getWidth()/2, GameScreen.SCREEN_H/2 - recyclingPopin.getHeight()/2);
	
		recyclingPopinDisplayed = true;
		this.stage.addActor(recyclingPopin);
	}
	
	private void updateRecycleListener(final Entity itemEntity, final AbstractItem item, final Table slot) {
		if (recyclingListener != null) {
			recyclingBtn.removeListener(recyclingListener);
		}
		recyclingListener = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (itemEntity != null) {
					Mappers.recyclingMachineComponent.get(inventoryCompo.getRecycler()).recycle(itemEntity, room);
				} else {
					Mappers.recyclingMachineComponent.get(inventoryCompo.getRecycler()).recycle(item, room);
				}
				slot.removeListener(this);
				
				if (room.hasEnemies()) {
					closePopin();
					room.turnManager.endPlayerTurn();
				} else {
					hideRecyclingPopin();
					needsRefresh = true;
				}
			}
		};
		recyclingBtn.addListener(recyclingListener);
	}
	
	private void hideRecyclingPopin() {
		recyclingPopin.remove();
		recyclingPopinDisplayed = false;
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
		if (infusionPopinDisplayed) hideInfusionPopin();
		if (recyclingPopinDisplayed) hideRecyclingPopin();

		mainTable.remove();
		needsRefresh = true;
		
		if (room.getNextState() == null) {
			room.setNextState(room.getLastInGameState());
		}
	}

}
