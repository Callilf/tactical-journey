package com.dokkaebistudio.tacticaljourney.rendering;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
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
import com.dokkaebistudio.tacticaljourney.components.WormholeComponent;
import com.dokkaebistudio.tacticaljourney.components.loot.LootableComponent;
import com.dokkaebistudio.tacticaljourney.components.loot.LootableComponent.LootableStateEnum;
import com.dokkaebistudio.tacticaljourney.components.neutrals.ChaliceComponent;
import com.dokkaebistudio.tacticaljourney.components.neutrals.SewingMachineComponent;
import com.dokkaebistudio.tacticaljourney.components.neutrals.ShopKeeperComponent;
import com.dokkaebistudio.tacticaljourney.components.neutrals.SoulbenderComponent;
import com.dokkaebistudio.tacticaljourney.components.neutrals.StatueComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent.PlayerActionEnum;
import com.dokkaebistudio.tacticaljourney.components.transition.ExitComponent;
import com.dokkaebistudio.tacticaljourney.enums.InventoryDisplayModeEnum;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemDivineCatalyst;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemLeather;
import com.dokkaebistudio.tacticaljourney.rendering.interfaces.Renderer;
import com.dokkaebistudio.tacticaljourney.rendering.service.PopinService;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.singletons.InputSingleton;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.MovementHandler;

public class ContextualActionPopinRenderer implements Renderer, RoomSystem {
	    
	public Stage stage;
	
	/** The player component (kept in cache to prevent getting it at each frame). */
	private PlayerComponent playerCompo;
	
	/** The current room. */
    private Room room;
        
    //**************************
    // Actors
    
    private Table mainPopin;
    private Label title;
    private Label desc;
    private TextButton yesBtn;
    private ChangeListener yesBtnListener;
        
    public ContextualActionPopinRenderer(Room r, Stage s) {
        this.room = r;
        this.stage = s;
    }
    
    @Override
    public void enterRoom(Room newRoom) {
    	this.room = newRoom;	
    }


    @Override
    public void render(float deltaTime) {
    	
    	if (playerCompo == null) {
    		playerCompo = Mappers.playerComponent.get(GameScreen.player);
    	}
    	
    	if (playerCompo.getRequestedAction() != PlayerActionEnum.NONE) {
    		room.setNextState(RoomState.CONTEXTUAL_ACTION_POPIN);

			if (mainPopin == null) {
				initTable();
				
    			// Close popin with ESCAPE
	    		stage.addListener(new InputListener() {
					@Override
					public boolean keyUp(InputEvent event, int keycode) {
						if (room.getState() == RoomState.CONTEXTUAL_ACTION_POPIN && (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK)) {
							closePopin();
							return true;
						}
						return super.keyUp(event, keycode);
					}
				});
			}
			
			updateContentForAction();
			
			// Place the popin properly
			mainPopin.pack();
			mainPopin.setPosition(GameScreen.SCREEN_W/2 - mainPopin.getWidth()/2, GameScreen.SCREEN_H/2 - mainPopin.getHeight()/2);
		
			this.stage.addActor(mainPopin);
			
			playerCompo.clearRequestedAction();
    	}
    	
    	if (room.getState() == RoomState.CONTEXTUAL_ACTION_POPIN) {
    		// Draw the table
            stage.act(Gdx.graphics.getDeltaTime());
    		stage.draw();
    		
    		// Close the inventory on a left click outside the popin
    		if (InputSingleton.getInstance().leftClickJustPressed) {
    			closePopin();
    		}
    	}
	}

	private void updateContentForAction() {
		Entity actionEntity = playerCompo.getActionEntity();
		yesBtn.setDisabled(false);

		switch (playerCompo.getRequestedAction()) {
			
		case LOOT:
			LootableComponent lootableComponent = Mappers.lootableComponent.get(actionEntity);
			// Update the content
			title.setText(lootableComponent.getType().getLabel());
			desc.setText(lootableComponent.getType().getDescription());
			
			if (lootableComponent.getLootableState() == LootableStateEnum.CLOSED) {
				desc.setText(desc.getText() + "\n" + "It will take you [SCARLET]" + lootableComponent.getType().getNbTurnsToOpen() + "[WHITE] turns to open it.");
			} else {
				desc.setText(desc.getText() + "\n" + "It is already [GREEN]opened[WHITE]. It won't take any turn.");
			}
			yesBtn.setText("Open");
	
			// Update the Use item listener
			updateLootListener(actionEntity, lootableComponent);
			break;
			
			
		case EXIT:
			ExitComponent exitComponent = Mappers.exitComponent.get(actionEntity);
			title.setText("Doorway to lower floor");
			if (exitComponent.isOpened()) {
				title.setText("Doorway to lower floor");
				desc.setText("Congratulations, you reached the exit of the first floor. You may go to the next floor but keep in mind that you won't be able to come back.");
				yesBtn.setText("Descend");
				updateExitListener(actionEntity, exitComponent);
			
			} else {
				desc.setText("The door is held close by a big ancient lock. You should probably look for the key.");
				yesBtn.setText("Unlock");
				if (yesBtnListener != null) {
					yesBtn.removeListener(yesBtnListener);
				}
				
				InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(GameScreen.player);
				if (inventoryComponent.hasKey()) {
					updateUnlockListener(actionEntity, exitComponent);
				} else {
					yesBtn.setDisabled(true);
				}
			}
			break;
			
		case WORMHOLE:
			WormholeComponent wormholeComponent = Mappers.wormholeComponent.get(actionEntity);
			
			title.setText("Wormhole");
			desc.setText("Would you like to travel to the wormhole's destination?");
			yesBtn.setText("Yes");
			
			updateWormholeListener(actionEntity, wormholeComponent);
			break;
			
		case PRAY:
			StatueComponent statueComponent = Mappers.statueComponent.get(actionEntity);
			
			title.setText("Statue of the goddess");
			desc.setText("A statue of the goddess Huminodun. You can feel a benevolent energy enveloping you when you get close enough. You can probably channel this energy is you pray for a while.");
			yesBtn.setText("Pray");
			
			updatePrayListener(actionEntity, statueComponent);
			break;
			
		case DRINK_CHALICE:
			ChaliceComponent chaliceComponent = Mappers.chaliceComponent.get(actionEntity);
			
			title.setText("Chalice");
			desc.setText("A chalice filled with an exotic concoction. The smell is very strong and drinking it might not be a pleasant experience.");
			yesBtn.setText("Drink");
			
			updateDrinkChaliceListener(actionEntity, chaliceComponent);
			break;
		case RESTOCK_SHOP:
			ShopKeeperComponent shopKeeperCompo = Mappers.shopKeeperComponent.get(actionEntity);
			
			title.setText("Restock of the shop");
			desc.setText("I have many things of interest in my stuff, I can restock the shop for [GOLD]" + shopKeeperCompo.getRestockPrice() + "gold coins [WHITE]if you want to.");
			yesBtn.setText("Restock");
			
			updateRestockListener(shopKeeperCompo);

			break;
			
		case INFUSE:
			SoulbenderComponent soulBenderComponent = Mappers.soulbenderComponent.get(actionEntity);
			
			title.setText("Infuse an item");
			String descStr = "Would you like to infuse an item ?";
			if (soulBenderComponent.getPrice() == 0) {
				descStr += " The first infusion is free!";
			} else {
				descStr += " It will cost you [GOLDENROD]" + soulBenderComponent.getPrice() + " gold coins[].";
			}
			descStr += "\nKeep in mind that only personal items can be infused.";
			desc.setText(descStr);
			yesBtn.setText("Infuse");
			
			updateInfuseListener(actionEntity, soulBenderComponent);
			break;
			
		case GIVE_CATALYST_SOULBENDER:
			soulBenderComponent = Mappers.soulbenderComponent.get(actionEntity);
			
			title.setText("Soulbender");
			descStr = "You have a Divine catalyst! If I consume it, I can restore my energy and infuse another item for you.\n"
					+ "Will you give it to me?";
			desc.setText(descStr);
			yesBtn.setText("Give");
			
			updateGiveCatalystSoulbenderListener(actionEntity, soulBenderComponent);
			break;
			
			
		case SEW:
			SewingMachineComponent sewingMachineComponent = Mappers.sewingMachineComponent.get(actionEntity);
			
			InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(GameScreen.player);
			boolean hasLeather = inventoryComponent.contains(ItemLeather.class);

			
			title.setText("Sewing machine");
			desc.setText("The sewing machine is ready to be used and there even is spare thread.");
			if (hasLeather) {
				desc.setText(desc.getText() + " You have leather so you can use it.");
			} else {
				desc.setText(desc.getText() + " You just need some leather to use it.");
			}
			yesBtn.setText("Sew");
			
			if (!hasLeather) yesBtn.setDisabled(true);
			
			updateSewListener(actionEntity, sewingMachineComponent);
			break;
			default:
		}
	}
    

    /**
     * Initialize the popin table (only the first time it is displayed).
     */
	private void initTable() {
		if (mainPopin == null) {
			mainPopin = new Table();
		}
//			selectedItemPopin.setDebug(true);

		// Add an empty click listener to capture the click so that the InputSingleton doesn't handle it
		mainPopin.setTouchable(Touchable.enabled);
		mainPopin.addListener(new ClickListener() {});
		
		// Place the popin and add the background texture
		mainPopin.setPosition(GameScreen.SCREEN_W/2, GameScreen.SCREEN_H/2);
		NinePatchDrawable ninePatchDrawable = new NinePatchDrawable(SceneAssets.popinNinePatch);
		mainPopin.setBackground(ninePatchDrawable);
		
		mainPopin.align(Align.top);
		
		// 1 - Title
		title = new Label("Title", PopinService.hudStyle());
		mainPopin.add(title).top().align(Align.top).pad(20, 0, 20, 0);
		mainPopin.row().align(Align.center);
		
		// 2 - Description
		desc = new Label("Description", PopinService.hudStyle());
		desc.setWrap(true);
		mainPopin.add(desc).growY().width(900).left().pad(0, 20, 0, 20);
		mainPopin.row();
		
		// 4 - Action buttons
		Table buttonTable = new Table();
		
		// 4.1 - No button
		final TextButton closeBtn = new TextButton("Close",PopinService.buttonStyle());			
		// Close listener
		closeBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				closePopin();
			}
		});
		buttonTable.add(closeBtn).pad(0, 20,0,20);

		// 4.2 - Yes button
		yesBtn = new TextButton("Loot",PopinService.buttonStyle());			
		buttonTable.add(yesBtn).pad(0, 20,0,20);
		
		mainPopin.add(buttonTable).pad(20, 0, 20, 0);
	}

	private void updateLootListener(final Entity lootable, final LootableComponent lootableComponent) {
		if (yesBtnListener != null) {
			yesBtn.removeListener(yesBtnListener);
		}
		yesBtnListener = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(GameScreen.player);
				inventoryComponent.setTurnsToWaitBeforeLooting(lootableComponent.getNbTurnsToOpen());
				inventoryComponent.setLootableEntity(lootable);
				closePopin();
			}
		};
		yesBtn.addListener(yesBtnListener);
	}
	
	private void updateExitListener(final Entity exit, final ExitComponent exitComponent) {
		if (yesBtnListener != null) {
			yesBtn.removeListener(yesBtnListener);
		}
		yesBtnListener = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				room.floor.getGameScreen().enterNextFloor();
				closePopin();
			}
		};
		yesBtn.addListener(yesBtnListener);
	}
	
	private void updateUnlockListener(final Entity exit, final ExitComponent exitComponent) {
		if (yesBtnListener != null) {
			yesBtn.removeListener(yesBtnListener);
		}
		yesBtnListener = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(GameScreen.player);
				inventoryComponent.removeKey();
				
				exitComponent.open(exit);
				room.turnManager.endPlayerTurn();
				closePopin();
			}
		};
		yesBtn.addListener(yesBtnListener);
	}
	
	private void updateWormholeListener(final Entity wormhole, final WormholeComponent wormholeComponent) {
		if (yesBtnListener != null) {
			yesBtn.removeListener(yesBtnListener);
		}
		yesBtnListener = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Vector2 destination = wormholeComponent.getDestination();
				MovementHandler.placeEntity(GameScreen.player, destination, room);
				room.setNextState(RoomState.PLAYER_COMPUTE_MOVABLE_TILES);
				closePopin();
			}
		};
		yesBtn.addListener(yesBtnListener);
	}
	

	private void updatePrayListener(final Entity statue, final StatueComponent statueComponent) {
		if (yesBtnListener != null) {
			yesBtn.removeListener(yesBtnListener);
		}
		yesBtnListener = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {				
				statueComponent.pray();
				room.turnManager.endPlayerTurn();
				closePopin();
			}
		};
		yesBtn.addListener(yesBtnListener);
	}
	
	private void updateDrinkChaliceListener(final Entity statue, final ChaliceComponent chaliceComponent) {
		if (yesBtnListener != null) {
			yesBtn.removeListener(yesBtnListener);
		}
		yesBtnListener = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {				
				chaliceComponent.drink();
				room.turnManager.endPlayerTurn();
				closePopin();
			}
		};
		yesBtn.addListener(yesBtnListener);
	}
	
	
	private void updateRestockListener(final ShopKeeperComponent shopKeeperCompo) {
		if (yesBtnListener != null) {
			yesBtn.removeListener(yesBtnListener);
		}
		yesBtnListener = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				shopKeeperCompo.setRequestRestock(true);
				closePopin();
			}
		};
		yesBtn.addListener(yesBtnListener);
	}
	
	private void updateInfuseListener(final Entity soulbender, final SoulbenderComponent soulbenderComponent) {
		if (yesBtnListener != null) {
			yesBtn.removeListener(yesBtnListener);
		}
		yesBtnListener = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				
				//Open inventory popin to select the item to infuse.
				InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(GameScreen.player);
				inventoryComponent.setDisplayMode(InventoryDisplayModeEnum.INFUSION);
				inventoryComponent.setSoulbender(soulbender);

				closePopin();
			}
		};
		yesBtn.addListener(yesBtnListener);
	}
	
	private void updateGiveCatalystSoulbenderListener(final Entity soulbender, final SoulbenderComponent soulbenderComponent) {
		if (yesBtnListener != null) {
			yesBtn.removeListener(yesBtnListener);
		}
		yesBtnListener = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				
				//Open inventory popin to select the item to infuse.
				InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(GameScreen.player);
				int indexOfCatalyst = inventoryComponent.indexOf(ItemDivineCatalyst.class);
				Entity catalyst = inventoryComponent.getAndRemove(indexOfCatalyst);
				room.removeEntity(catalyst);
				
				soulbenderComponent.setHasInfused(false);
				soulbenderComponent.setReceivedCatalyst(true);
				closePopin();
			}
		};
		yesBtn.addListener(yesBtnListener);
	}
	
	private void updateSewListener(final Entity statue, final SewingMachineComponent sewingMachineComponent) {
		if (yesBtnListener != null) {
			yesBtn.removeListener(yesBtnListener);
		}
		yesBtnListener = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {				
				sewingMachineComponent.sew(room);
				room.turnManager.endPlayerTurn();
				closePopin();
			}
		};
		yesBtn.addListener(yesBtnListener);
	}
	
	
	// CLOSE

	/**
	 * Close the popin and unpause the game.
	 */
	private void closePopin() {
		mainPopin.remove();
		
		if (room.getNextState() == null) {
			room.setNextState(room.getLastInGameState());
		}
	}

}
