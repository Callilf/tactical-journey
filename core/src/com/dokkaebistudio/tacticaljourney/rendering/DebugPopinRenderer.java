package com.dokkaebistudio.tacticaljourney.rendering;

import java.util.Iterator;
import java.util.List;

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
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.assets.SceneAssets;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.components.player.ExperienceComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.enums.DamageType;
import com.dokkaebistudio.tacticaljourney.enums.InventoryDisplayModeEnum;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.rendering.interfaces.Renderer;
import com.dokkaebistudio.tacticaljourney.rendering.service.PopinService;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.room.RoomType;
import com.dokkaebistudio.tacticaljourney.singletons.InputSingleton;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.util.LootUtil;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class DebugPopinRenderer implements Renderer, RoomSystem {
	    
	//****************************
	// Main attributes
	
	/** The stage. */
	public Stage stage;
	/** The current room. */
    private Room room;
	
	/** The inventory component of the player (kept in cache to prevent getting it at each frame). */
	private InventoryComponent inventoryCompo;    
    
    
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
    
    private Table debugTable;
    

    
    /**
     * Constructor.
     * @param r the room
     * @param s the stage to draw on
     * @param p the player
     */
    public DebugPopinRenderer(Room r, Stage s) {
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
    	}    	
    	
    	// Check if the inventory is displayed
    	if (inventoryCompo.getDisplayMode() == InventoryDisplayModeEnum.DEBUG) {
    		
    		if (room.getState() != RoomState.DEBUG_POPIN && !inventoryCompo.isInventoryActionInProgress()) {
	    		lootDisplayed = true;
	
				// Create the inventory table and set the state
	    		room.setNextState(RoomState.DEBUG_POPIN);
		    		
		    	if (mainTable == null) {
		    		mainTable = new Table();
	//	    		mainTable.setDebug(true);
	
		    		createLootTable();
		    		createDebugTable();
		    		
		        	mainTable.pack();
		        	mainTable.setPosition(GameScreen.SCREEN_W/2 - mainTable.getWidth()/2, GameScreen.SCREEN_H/2 - mainTable.getHeight()/2);
	    		}
		    	
				stage.addActor(mainTable);
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
		
		NinePatchDrawable ninePatchDrawable = new NinePatchDrawable(SceneAssets.popinNinePatch);
		ninePatchDrawable.setMinWidth(653);
		ninePatchDrawable.setMinHeight(746);
		lootTable.setBackground(ninePatchDrawable);
		lootTable.align(Align.top);

		// 1 - Title
		lootTableTitle = new Label("Get item", PopinService.hudStyle());
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
		btnTable.pack();
		
		lootTable.add(btnTable).pad(40, 0, 40, 0);

		
		lootTable.pack();
		mainTable.add(lootTable).padRight(20);
		
		
		lootableItemsTable.clear();
		ItemEnum[] values = ItemEnum.values();
		for (ItemEnum v : values) {
			Entity item = room.entityFactory.itemFactory.createItem(v, null);
			if (item == null) continue;
			Table oneItem = createOneLootItem(item);
			lootableItemsTable.add(oneItem).pad(0, 0, 10, 0).maxWidth(630);
			lootableItemsTable.row();
		}
		
		lootableItemsTable.pack();
		lootableItemsScroll.setWidget(lootableItemsTable);
		lootableItemsScroll.pack();	
		
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
		
		TextButton takeBtn = new TextButton("Take", PopinService.buttonStyle());
		takeBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				//add item in inventory and remove it from lootable entity
				Entity clonedItem = room.entityFactory.itemFactory.createItem(itemComponent.getItemType().type, null);
				ItemComponent clonedItemCompo = Mappers.itemComponent.get(clonedItem);
				boolean pickedUp = clonedItemCompo.pickUp(GameScreen.player, clonedItem, room);
				if (pickedUp) {
					//TODO
				} else {
					//TODO
				}
			}
		});
		
		oneItem.add(takeBtn);
		
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
	
		this.stage.addActor(selectedItemPopin);
	}
	
	
	
	private void hideSelectedItemPopin() {
		selectedItemPopin.remove();
	}
	
	
	//***************************
	// Debug menu
	
	private void createDebugTable() {		
		debugTable = new Table();
//		    		table.setDebug(true, true);
		debugTable.setTouchable(Touchable.enabled);
		debugTable.addListener(new ClickListener() {});
		
		NinePatchDrawable ninePatchDrawable = new NinePatchDrawable(SceneAssets.popinNinePatch);
		ninePatchDrawable.setMinWidth(653);
		ninePatchDrawable.setMinHeight(746);
		debugTable.setBackground(ninePatchDrawable);
		debugTable.align(Align.top);

		// 1 - Title
		Label debugTableTitle = new Label("Debug Menu", PopinService.hudStyle());
		debugTable.add(debugTableTitle).uniformX().pad(40, 0, 40, 0);
		debugTable.row();
		
		// The table that will contain all loot items
		Table optionsTable = new Table();
		optionsTable.top();
		optionsTable.pack();
		
		//The scroll pane for the loot items
		ScrollPane optionsScroll = new ScrollPane(optionsTable, PopinService.scrollStyle());
		optionsScroll.setScrollbarsOnTop(true);
		optionsScroll.setFadeScrollBars(false);
		debugTable.add(optionsScroll).fill().expand().maxHeight(650);
		debugTable.row();

		debugTable.pack();
		mainTable.add(debugTable);
		
		//################################
		// Add all options here
		
		// Mapping
		TextButton fullMap = new TextButton("Show full map", PopinService.buttonStyle());
		fullMap.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				MapRenderer.FULL_MAP = true;
				MapRenderer.requireRefresh();
			}
		});
		optionsTable.add(fullMap).padBottom(20);
		optionsTable.row();
		
		// Kill all
		TextButton killAll = new TextButton("Kill all enemies", PopinService.buttonStyle());
		killAll.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				for (Entity e : room.getEnemies()) {
					HealthComponent healthComponent = Mappers.healthComponent.get(e);
					if (healthComponent != null) {
						healthComponent.setHp(0);
					}
				}
			}
		});
		optionsTable.add(killAll).padBottom(20);
		optionsTable.row();
		
		// Destroy all
		TextButton destroyAll = new TextButton("Break all destructibles", PopinService.buttonStyle());
		destroyAll.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Array<Entity> copy = new Array<>(room.getAllEntities());
				Iterator<Entity> iterator = copy.iterator();
				while(iterator.hasNext()) {
					LootUtil.destroy(iterator.next(), room);
				}
			}
		});
		optionsTable.add(destroyAll).padBottom(20);
		optionsTable.row();
		
		// HP
		Label hpLabel = new Label("Health", PopinService.hudStyle());
		optionsTable.add(hpLabel).padBottom(20);
		optionsTable.row();
		
		Table healthTable = new Table();
		TextButton healthDown = new TextButton("HP -5", PopinService.buttonStyle());
		healthDown.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				HealthComponent healthComponent = Mappers.healthComponent.get(GameScreen.player);
				healthComponent.setHp(healthComponent.getHp() - 5);
			}
		});
		healthTable.add(healthDown).padRight(20);
		TextButton healthUp = new TextButton("HP +5", PopinService.buttonStyle());
		healthUp.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				HealthComponent healthComponent = Mappers.healthComponent.get(GameScreen.player);
				healthComponent.setHp(healthComponent.getHp() + 5);
			}
		});
		healthTable.add(healthUp);
		optionsTable.add(healthTable).padBottom(20);
		optionsTable.row();
		
		// HP MAX
		Table maxHealthTable = new Table();
		TextButton maxHealthDown = new TextButton("HP Max -5", PopinService.buttonStyle());
		maxHealthDown.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				HealthComponent healthComponent = Mappers.healthComponent.get(GameScreen.player);
				healthComponent.increaseMaxHealth(-5);
			}
		});
		maxHealthTable.add(maxHealthDown).padRight(20);
		TextButton maxHealthUp = new TextButton("HP Max +5", PopinService.buttonStyle());
		maxHealthUp.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				HealthComponent healthComponent = Mappers.healthComponent.get(GameScreen.player);
				healthComponent.increaseMaxHealth(5);
			}
		});
		maxHealthTable.add(maxHealthUp);
		optionsTable.add(maxHealthTable).padBottom(20);
		optionsTable.row();

		// ARMOR
		Label armorLabel = new Label("Armor", PopinService.hudStyle());
		optionsTable.add(armorLabel).padBottom(20);
		optionsTable.row();
		
		Table armorTable = new Table();
		TextButton armorDown = new TextButton("Armor -5", PopinService.buttonStyle());
		armorDown.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				HealthComponent healthComponent = Mappers.healthComponent.get(GameScreen.player);
				healthComponent.setArmor(healthComponent.getArmor() - 5);
			}
		});
		armorTable.add(armorDown).padRight(20);
		TextButton armorUp = new TextButton("Armor +5", PopinService.buttonStyle());
		armorUp.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				HealthComponent healthComponent = Mappers.healthComponent.get(GameScreen.player);
				healthComponent.setArmor(healthComponent.getArmor() + 5);
			}
		});
		armorTable.add(armorUp);
		optionsTable.add(armorTable).padBottom(20);
		optionsTable.row();
		
		// ARMOR MAX
		Table maxArmorTable = new Table();
		TextButton maxArmorDown = new TextButton("Armor Max -5", PopinService.buttonStyle());
		maxArmorDown.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				HealthComponent healthComponent = Mappers.healthComponent.get(GameScreen.player);
				healthComponent.increaseMaxArmor(-5);
			}
		});
		maxArmorTable.add(maxArmorDown).padRight(20);
		TextButton maxArmorUp = new TextButton("Armor Max +5", PopinService.buttonStyle());
		maxArmorUp.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				HealthComponent healthComponent = Mappers.healthComponent.get(GameScreen.player);
				healthComponent.increaseMaxArmor(5);
			}
		});
		maxArmorTable.add(maxArmorUp);
		optionsTable.add(maxArmorTable).padBottom(20);
		optionsTable.row();
		
		
		// EXPERIENCE
		Label xpLabel = new Label("Xp", PopinService.hudStyle());
		optionsTable.add(xpLabel).padBottom(20);
		optionsTable.row();
		
		Table xpTable = new Table();
		TextButton xpUp = new TextButton("XP +10", PopinService.buttonStyle());
		xpUp.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				ExperienceComponent xpCompo = Mappers.experienceComponent.get(GameScreen.player);
				xpCompo.earnXp(10);
			}
		});
		xpTable.add(xpUp).padRight(20);
		TextButton xpUpUp = new TextButton("XP +50", PopinService.buttonStyle());
		xpUpUp.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				ExperienceComponent xpCompo = Mappers.experienceComponent.get(GameScreen.player);
				xpCompo.earnXp(50);
			}
		});
		xpTable.add(xpUpUp);
		optionsTable.add(xpTable).padBottom(20);
		optionsTable.row();
		
		
		// inventory
		Label inventoryLabel = new Label("Inventory", PopinService.hudStyle());
		optionsTable.add(inventoryLabel).padBottom(20);
		optionsTable.row();
		
		Table inventoryTable = new Table();
		TextButton inventoryUp = new TextButton("Slot -1", PopinService.buttonStyle());
		inventoryUp.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				InventoryComponent inventoryCompo = Mappers.inventoryComponent.get(GameScreen.player);
				inventoryCompo.removeSlot(room);
			}
		});
		inventoryTable.add(inventoryUp).padRight(20);
		TextButton inventoryDown = new TextButton("Slot +1", PopinService.buttonStyle());
		inventoryDown.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				InventoryComponent inventoryCompo = Mappers.inventoryComponent.get(GameScreen.player);
				inventoryCompo.addSlot();
			}
		});
		inventoryTable.add(inventoryDown);
		optionsTable.add(inventoryTable).padBottom(20);
		optionsTable.row();
		
		// STATS
		Label statsLabel = new Label("Stats", PopinService.hudStyle());
		optionsTable.add(statsLabel).padBottom(20);
		optionsTable.row();
		
		Table karmaTable = new Table();
		TextButton karmaDown = new TextButton("Karma -1", PopinService.buttonStyle());
		karmaDown.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				PlayerComponent playerComponent = Mappers.playerComponent.get(GameScreen.player);
				playerComponent.increaseKarma(-1);
			}
		});
		karmaTable.add(karmaDown).padRight(20);
		TextButton karmaUp = new TextButton("Karma +1", PopinService.buttonStyle());
		karmaUp.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				PlayerComponent playerComponent = Mappers.playerComponent.get(GameScreen.player);
				playerComponent.increaseKarma(1);
			}
		});
		karmaTable.add(karmaUp);
		optionsTable.add(karmaTable).padBottom(20);
		optionsTable.row();

		Table strengthTable = new Table();
		TextButton strengthDown = new TextButton("Strength -1", PopinService.buttonStyle());
		strengthDown.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				AttackComponent attackComponent = Mappers.attackComponent.get(GameScreen.player);
				attackComponent.setStrength(attackComponent.getStrength() - 1);
			}
		});
		strengthTable.add(strengthDown).padRight(20);
		TextButton strengthUp = new TextButton("Strength +1", PopinService.buttonStyle());
		strengthUp.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				AttackComponent attackComponent = Mappers.attackComponent.get(GameScreen.player);
				attackComponent.setStrength(attackComponent.getStrength() + 1);
			}
		});
		strengthTable.add(strengthUp);
		optionsTable.add(strengthTable).padBottom(20);
		optionsTable.row();
		
		Table accuracyTable = new Table();
		TextButton accuracyDown = new TextButton("Melee accuracy -1", PopinService.buttonStyle());
		accuracyDown.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				AttackComponent attackComponent = Mappers.attackComponent.get(GameScreen.player);
				attackComponent.increaseAccuracy(- 1);
			}
		});
		accuracyTable.add(accuracyDown).padRight(20);
		TextButton accuracyUp = new TextButton("Melee accuracy +1", PopinService.buttonStyle());
		accuracyUp.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				AttackComponent attackComponent = Mappers.attackComponent.get(GameScreen.player);
				attackComponent.increaseAccuracy(1);
			}
		});
		accuracyTable.add(accuracyUp);
		optionsTable.add(accuracyTable).padBottom(20);
		optionsTable.row();
		
		Table moveTable = new Table();
		TextButton moveDown = new TextButton("Move -1", PopinService.buttonStyle());
		moveDown.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				MoveComponent moveComponent = Mappers.moveComponent.get(GameScreen.player);
				moveComponent.setMoveSpeed(moveComponent.getMoveSpeed() - 1);
			}
		});
		moveTable.add(moveDown).padRight(20);
		TextButton moveUp = new TextButton("Move +1", PopinService.buttonStyle());
		moveUp.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				MoveComponent moveComponent = Mappers.moveComponent.get(GameScreen.player);
				moveComponent.setMoveSpeed(moveComponent.getMoveSpeed() + 1);
			}
		});
		moveTable.add(moveUp);
		optionsTable.add(moveTable).padBottom(20);
		optionsTable.row();
		
		
		// BOW
		Label bowLabel = new Label("Bow", PopinService.hudStyle());
		optionsTable.add(bowLabel).padBottom(20);
		optionsTable.row();

		Table bowRangeTable = new Table();
		TextButton bowRangeDown = new TextButton("Bow range -1", PopinService.buttonStyle());
		bowRangeDown.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				AttackComponent attackComponent = Mappers.attackComponent.get(Mappers.playerComponent.get(GameScreen.player).getSkillRange());
				attackComponent.setRangeMax(attackComponent.getRangeMax() - 1);
			}
		});
		bowRangeTable.add(bowRangeDown).padRight(20);
		TextButton bowRangeUp = new TextButton("Bow range +1", PopinService.buttonStyle());
		bowRangeUp.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				AttackComponent attackComponent = Mappers.attackComponent.get(Mappers.playerComponent.get(GameScreen.player).getSkillRange());
				attackComponent.setRangeMax(attackComponent.getRangeMax() + 1);
			}
		});
		bowRangeTable.add(bowRangeUp);
		optionsTable.add(bowRangeTable).padBottom(20);
		optionsTable.row();
		
		Table bowDamageTable = new Table();
		TextButton bowDamageDown = new TextButton("Bow damage -1", PopinService.buttonStyle());
		bowDamageDown.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				AttackComponent attackComponent = Mappers.attackComponent.get(Mappers.playerComponent.get(GameScreen.player).getSkillRange());
				attackComponent.increaseStrength(- 1);
			}
		});
		bowDamageTable.add(bowDamageDown).padRight(20);
		TextButton bowDamageUp = new TextButton("Bow damage +1", PopinService.buttonStyle());
		bowDamageUp.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				AttackComponent attackComponent = Mappers.attackComponent.get(Mappers.playerComponent.get(GameScreen.player).getSkillRange());
				attackComponent.increaseStrength(1);
			}
		});
		bowDamageTable.add(bowDamageUp);
		optionsTable.add(bowDamageTable).padBottom(20);
		optionsTable.row();
		
		Table bowAccuracyTable = new Table();
		TextButton bowAccuracyDown = new TextButton("Bow accuracy -1", PopinService.buttonStyle());
		bowAccuracyDown.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				AttackComponent attackComponent = Mappers.attackComponent.get(Mappers.playerComponent.get(GameScreen.player).getSkillRange());
				attackComponent.increaseAccuracy(- 1);
			}
		});
		bowAccuracyTable.add(bowAccuracyDown).padRight(20);
		TextButton bowAccuracyUp = new TextButton("Bow accuracy +1", PopinService.buttonStyle());
		bowAccuracyUp.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				AttackComponent attackComponent = Mappers.attackComponent.get(Mappers.playerComponent.get(GameScreen.player).getSkillRange());
				attackComponent.increaseAccuracy(1);
			}
		});
		bowAccuracyTable.add(bowAccuracyUp);
		optionsTable.add(bowAccuracyTable).padBottom(20);
		optionsTable.row();
		
		
		
		// BOMBS
		Label bombLabel = new Label("Bombs", PopinService.hudStyle());
		optionsTable.add(bombLabel).padBottom(20);
		optionsTable.row();

		Table bombThrowRangeTable = new Table();
		TextButton bombThrowRangeDown = new TextButton("Bomb throw rg -1", PopinService.buttonStyle());
		bombThrowRangeDown.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				AttackComponent attackComponent = Mappers.attackComponent.get(Mappers.playerComponent.get(GameScreen.player).getSkillBomb());
				attackComponent.setRangeMax(attackComponent.getRangeMax() - 1);
			}
		});
		bombThrowRangeTable.add(bombThrowRangeDown).padRight(20);
		TextButton bombThrowRangeUp = new TextButton("Bomb throw rg +1", PopinService.buttonStyle());
		bombThrowRangeUp.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				AttackComponent attackComponent = Mappers.attackComponent.get(Mappers.playerComponent.get(GameScreen.player).getSkillBomb());
				attackComponent.setRangeMax(attackComponent.getRangeMax() + 1);
			}
		});
		bombThrowRangeTable.add(bombThrowRangeUp);
		optionsTable.add(bombThrowRangeTable).padBottom(20);
		optionsTable.row();
		
		Table bombDamageTable = new Table();
		TextButton bombDamageDown = new TextButton("Bomb damage -1", PopinService.buttonStyle());
		bombDamageDown.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				AttackComponent attackComponent = Mappers.attackComponent.get(Mappers.playerComponent.get(GameScreen.player).getSkillBomb());
				attackComponent.increaseStrength(- 1);
			}
		});
		bombDamageTable.add(bombDamageDown).padRight(20);
		TextButton bombDamageUp = new TextButton("Bomb damage +1", PopinService.buttonStyle());
		bombDamageUp.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				AttackComponent attackComponent = Mappers.attackComponent.get(Mappers.playerComponent.get(GameScreen.player).getSkillBomb());
				attackComponent.increaseStrength(1);
			}
		});
		bombDamageTable.add(bombDamageUp);
		optionsTable.add(bombDamageTable).padBottom(20);
		optionsTable.row();
		
		Table bombFuseTable = new Table();
		TextButton bombFuseDown = new TextButton("Bomb fuse -1", PopinService.buttonStyle());
		bombFuseDown.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				AttackComponent attackComponent = Mappers.attackComponent.get(Mappers.playerComponent.get(GameScreen.player).getSkillBomb());
				attackComponent.setBombTurnsToExplode(attackComponent.getBombTurnsToExplode() - 1);
			}
		});
		bombFuseTable.add(bombFuseDown).padRight(20);
		TextButton bombFuseUp = new TextButton("Bomb fuse +1", PopinService.buttonStyle());
		bombFuseUp.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				AttackComponent attackComponent = Mappers.attackComponent.get(Mappers.playerComponent.get(GameScreen.player).getSkillBomb());
				attackComponent.setBombTurnsToExplode(attackComponent.getBombTurnsToExplode() + 1);
			}
		});
		bombFuseTable.add(bombFuseUp);
		optionsTable.add(bombFuseTable).padBottom(20);
		optionsTable.row();
		
		Table bombRadiusTable = new Table();
		TextButton bombRadiusDown = new TextButton("Bomb radius -1", PopinService.buttonStyle());
		bombRadiusDown.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				AttackComponent attackComponent = Mappers.attackComponent.get(Mappers.playerComponent.get(GameScreen.player).getSkillBomb());
				attackComponent.setBombRadius(attackComponent.getBombRadius() - 1);
			}
		});
		bombRadiusTable.add(bombRadiusDown).padRight(20);
		TextButton bombRadiusUp = new TextButton("Bomb radius +1", PopinService.buttonStyle());
		bombRadiusUp.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				AttackComponent attackComponent = Mappers.attackComponent.get(Mappers.playerComponent.get(GameScreen.player).getSkillBomb());
				attackComponent.setBombRadius(attackComponent.getBombRadius() + 1);
			}
		});
		bombRadiusTable.add(bombRadiusUp);
		optionsTable.add(bombRadiusTable).padBottom(20);
		optionsTable.row();
		
		
		// RESISTANCES
		Label resistancesLabel = new Label("Resistances", PopinService.hudStyle());
		optionsTable.add(resistancesLabel).padBottom(20);
		optionsTable.row();
		
		// Poison
		Table poisonResistTable = new Table();
		TextButton poisonResistDown = new TextButton("Poison resist -50", PopinService.buttonStyle());
		poisonResistDown.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				HealthComponent healthComponent = Mappers.healthComponent.get(GameScreen.player);
				healthComponent.reduceResistance(DamageType.POISON, 50);
			}
		});
		poisonResistTable.add(poisonResistDown).padRight(20);
		TextButton poisonResistUp = new TextButton("Poison resist +50", PopinService.buttonStyle());
		poisonResistUp.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				HealthComponent healthComponent = Mappers.healthComponent.get(GameScreen.player);
				healthComponent.addResistance(DamageType.POISON, 50);
			}
		});
		poisonResistTable.add(poisonResistUp);
		optionsTable.add(poisonResistTable).padBottom(20);
		optionsTable.row();
		
		// Fire
		Table fireResistTable = new Table();
		TextButton fireResistDown = new TextButton("Fire resist -10", PopinService.buttonStyle());
		fireResistDown.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				HealthComponent healthComponent = Mappers.healthComponent.get(GameScreen.player);
				healthComponent.reduceResistance(DamageType.FIRE, 10);
			}
		});
		fireResistTable.add(fireResistDown).padRight(20);
		TextButton fireResistUp = new TextButton("Fire resist +10", PopinService.buttonStyle());
		fireResistUp.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				HealthComponent healthComponent = Mappers.healthComponent.get(GameScreen.player);
				healthComponent.addResistance(DamageType.FIRE, 10);
			}
		});
		fireResistTable.add(fireResistUp);
		optionsTable.add(fireResistTable).padBottom(20);
		optionsTable.row();
		
		// Explosion
		Table explosionResistTable = new Table();
		TextButton explosionResistDown = new TextButton("Expl. resist -10", PopinService.buttonStyle());
		explosionResistDown.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				HealthComponent healthComponent = Mappers.healthComponent.get(GameScreen.player);
				healthComponent.reduceResistance(DamageType.EXPLOSION, 10);
			}
		});
		explosionResistTable.add(explosionResistDown).padRight(20);
		TextButton explosionResistUp = new TextButton("Expl. resist +10", PopinService.buttonStyle());
		explosionResistUp.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				HealthComponent healthComponent = Mappers.healthComponent.get(GameScreen.player);
				healthComponent.addResistance(DamageType.EXPLOSION, 10);
			}
		});
		explosionResistTable.add(explosionResistUp);
		optionsTable.add(explosionResistTable).padBottom(20);
		optionsTable.row();
		
		
		// TP
		Label tpLabel  = new Label("Teleport", PopinService.hudStyle());
		optionsTable.add(tpLabel).padBottom(20);
		optionsTable.row();
		
		TextButton nextFloor = new TextButton("Go to next floor", PopinService.buttonStyle());
		nextFloor.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				room.floor.getGameScreen().enterNextFloor();
			}
		});
		optionsTable.add(nextFloor).padBottom(20);
		optionsTable.row();

		TextButton itemRoom = new TextButton("Go to item room", PopinService.buttonStyle());
		itemRoom.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				List<Room> rooms = room.floor.getRooms(RoomType.ITEM_ROOM);
				if (!rooms.isEmpty()) {
					room.floor.enterRoom(rooms.get(0));
				}
			}
		});
		optionsTable.add(itemRoom).padBottom(20);
		optionsTable.row();

		TextButton shopRoom = new TextButton("Go to shop", PopinService.buttonStyle());
		shopRoom.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				List<Room> rooms = room.floor.getRooms(RoomType.SHOP_ROOM);
				if (!rooms.isEmpty()) {
					room.floor.enterRoom(rooms.get(0));
				}
			}
		});
		optionsTable.add(shopRoom).padBottom(20);
		optionsTable.row();

		TextButton statueRoom = new TextButton("Go to statue", PopinService.buttonStyle());
		statueRoom.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				List<Room> rooms = room.floor.getRooms(RoomType.STATUE_ROOM);
				if (!rooms.isEmpty()) {
					room.floor.enterRoom(rooms.get(0));
				}
			}
		});
		optionsTable.add(statueRoom).padBottom(20);
		optionsTable.row();
		
		TextButton giftRoom = new TextButton("Go to gift", PopinService.buttonStyle());
		giftRoom.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				List<Room> rooms = room.floor.getRooms(RoomType.GIFT_ROOM);
				if (!rooms.isEmpty()) {
					room.floor.enterRoom(rooms.get(0));
				}
			}
		});
		optionsTable.add(giftRoom).padBottom(20);
		optionsTable.row();

		TextButton keyRoom = new TextButton("Go to key", PopinService.buttonStyle());
		keyRoom.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				List<Room> rooms = room.floor.getRooms(RoomType.KEY_ROOM);
				if (!rooms.isEmpty()) {
					room.floor.enterRoom(rooms.get(0));
				}
			}
		});
		optionsTable.add(keyRoom).padBottom(20);
		optionsTable.row();

		TextButton exitRoom = new TextButton("Go to exit", PopinService.buttonStyle());
		exitRoom.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				List<Room> rooms = room.floor.getRooms(RoomType.END_FLOOR_ROOM);
				if (!rooms.isEmpty()) {
					room.floor.enterRoom(rooms.get(0));
				}
			}
		});
		optionsTable.add(exitRoom).padBottom(20);
		optionsTable.row();

		
		
		optionsTable.pack();
		optionsScroll.setWidget(optionsTable);
		optionsScroll.pack();	
		
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
			room.setNextState(room.getLastInGameState());
		}
	}

}
