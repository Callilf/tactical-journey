package com.dokkaebistudio.tacticaljourney.rendering;

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

public class InventoryPopinRenderer implements Renderer, RoomSystem {
	    
	public Stage stage;
	
	private Entity player;
	
	/** The inventory component of the player (kept in cache to prevent getting it at each frame). */
	private InventoryComponent inventoryCompo;
	
	/** The current room. */
    private Room room;
    
    /** The main table of the popin. */
    private Table mainTable;
    private Table[] slots = new Table[16];
    private Table selectedItemPopin;
    
    boolean itemPopinDisplayer = false;
    private Label itemTitle;
    private Label itemDesc;
    private TextButton dropItemBtn;
    private ChangeListener dropListener;
    private TextButton useItemBtn;
    private ChangeListener useListener;
    
    
	private LabelStyle hudStyle;
    
    /** The state before the level up state. */
    private RoomState previousState;
    
    public InventoryPopinRenderer(Room r, Stage s, Entity p) {
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
    	
    	if (inventoryCompo == null) {
    		inventoryCompo = Mappers.inventoryComponent.get(player);
    	}
    	
    	if (inventoryCompo != null && inventoryCompo.isInventoryDisplayed()) {

    		if (mainTable == null) {
    			// Create the inventory table for the first time
    			
	    		previousState = room.getNextState() != null ? room.getNextState() : room.getState();
	    		room.setNextState(RoomState.INVENTORY_POPIN);
	    			    		
	    		mainTable = new Table();
//	    		table.setDebug(true, true);
	    		mainTable.setTouchable(Touchable.enabled);
	    		mainTable.addListener(new ClickListener() {});
	    		
	        	mainTable.setPosition(GameScreen.SCREEN_W/2, GameScreen.SCREEN_H/2);
	    		//table.setTouchable(Touchable.childrenOnly);
	    		
	    		TextureRegionDrawable topBackground = new TextureRegionDrawable(Assets.getTexture(Assets.inventory_background));
	    		mainTable.setBackground(topBackground);
	    		
	    		mainTable.align(Align.top);
	    		
	    		// 1 - Title
	    		Label title = new Label("Inventory", hudStyle);
	    		mainTable.add(title).uniformX().pad(40, 0, 40, 0);
	    		mainTable.row();
	    		
	    		
	    		// 2 - Inventory slots
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
	    		mainTable.add(slotsTable);
	    		
	        	mainTable.pack();
	        	mainTable.setPosition(mainTable.getX() - mainTable.getWidth()/2, mainTable.getY() - mainTable.getHeight()/2);
	
	    		stage.addActor(mainTable);
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

    /**
     * Create an inventory slot (filled or empty).
     * @param index the index of the slot
     * @return the created slot
     */
	private Table createSlot(int index) {
		boolean activeSlot = index < inventoryCompo.getNumberOfSlots();
		
		final Table slot = new Table();
//		slot.setDebug(true);

		//Background
		TextureRegionDrawable slotBackground = null;
		if (!activeSlot) {
			slotBackground = new TextureRegionDrawable(Assets.getTexture(Assets.inventory_slot_disabled));
		} else {
			slotBackground = new TextureRegionDrawable(Assets.getTexture(Assets.inventory_slot));
		}
		slot.setBackground(slotBackground);
		
		
		// Add the item if needed
		if (activeSlot) {
			final Entity item = inventoryCompo.get(index);
			
			if (item != null) {
				
				// An item is present in this inventory slot
				final ItemComponent itemComponent = Mappers.itemComponent.get(item);
				Image img = new Image(Assets.getTexture(itemComponent.getItemType().getImageName() + "-full"));
				
				slot.addListener(new ClickListener() {
					
					@Override
					public void clicked(InputEvent event, float x, float y) {
						displaySelectedItemPopin( item, slot);
					}
				});
				
				slot.add(img);
				
			} else {
				
				// Empty slot
				Label l = new Label("empty", hudStyle);
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
					hideSelectedItemPopin();
				}
			});
			buttonTable.add(closeBtn).pad(0, 20,0,20);
			
			// 3.2 - Drop button
			dropItemBtn = new TextButton("Drop",btnStyle);			
			buttonTable.add(dropItemBtn).pad(0, 20,0,20);

			// 3.3 - Use button
			useItemBtn = new TextButton("Use",btnStyle);			
			buttonTable.add(useItemBtn).pad(0, 20,0,20);
			
			selectedItemPopin.add(buttonTable).pad(20, 0, 20, 0);
			
		}
		
		
		final ItemComponent itemComponent = Mappers.itemComponent.get(item);
		
		// Update the content
		itemTitle.setText(itemComponent.getItemType().getLabel());
		itemDesc.setText(itemComponent.getItemType().getDescription());
		
		// Update the Drop item listener
		updateDropListener(item, slot, itemComponent);
		
		// Update the Use item listener
		updateUseListener(item, slot);

		
		// Place the popin properly
		selectedItemPopin.pack();
		selectedItemPopin.setPosition(GameScreen.SCREEN_W/2 - selectedItemPopin.getWidth()/2, GameScreen.SCREEN_H/2 - selectedItemPopin.getHeight()/2);
	
		itemPopinDisplayer = true;
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

	private void updateDropListener(final Entity item, final Table slot, final ItemComponent itemComponent) {
		if (dropListener != null) {
			dropItemBtn.removeListener(dropListener);
		}
		dropListener = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				inventoryCompo.requestAction(InventoryActionEnum.DROP, item);
				slot.removeListener(this);
				inventoryCompo.remove(item);
				hideSelectedItemPopin();
				closePopin();
			}
		};
		dropItemBtn.addListener(dropListener);
	}
	
	private void hideSelectedItemPopin() {
		selectedItemPopin.remove();
		itemPopinDisplayer = false;
	}

	/**
	 * Close the level up popin and unpause the game.
	 */
	private void closePopin() {
		inventoryCompo.setInventoryDisplayed(false);
		
		if (itemPopinDisplayer) hideSelectedItemPopin();

		mainTable.clear();
		mainTable.remove();
		mainTable = null;
		
		if (room.getNextState() == null) {
			room.setNextState(previousState);
		}
	}

}
