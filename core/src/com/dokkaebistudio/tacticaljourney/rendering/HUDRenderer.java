package com.dokkaebistudio.tacticaljourney.rendering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.ces.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.attack.AttackComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.AlterationReceiverComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.AmmoCarrierComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.ExperienceComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.SkillComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.WalletComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.PlayerComponent.ProfilePopinDisplayModeEnum;
import com.dokkaebistudio.tacticaljourney.ces.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems.InspectSystem;
import com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems.InspectSystem.InspectModeActionEnum;
import com.dokkaebistudio.tacticaljourney.enums.HealthChangeEnum;
import com.dokkaebistudio.tacticaljourney.enums.InventoryDisplayModeEnum;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.rendering.interfaces.Renderer;
import com.dokkaebistudio.tacticaljourney.rendering.service.PopinService;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomClearedState;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.room.rewards.AbstractRoomReward;
import com.dokkaebistudio.tacticaljourney.singletons.GameTimeSingleton;
import com.dokkaebistudio.tacticaljourney.statuses.Status;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class HUDRenderer implements Renderer, RoomSystem {
	
	
	public static Vector2 POS_FLOOR = new Vector2(0, 1030.0f);
	public static Vector2 POS_TIMER = new Vector2(200f, 1030.0f);
	public static Vector2 POS_STATUSES = new Vector2(5f, 160f);
	public static Vector2 POS_END_TURN_BTN = new Vector2(5f, 5f);
	public static Vector2 POS_KEY_SLOT = new Vector2(650f,1000f);
	public static Vector2 POS_MONEY = new Vector2(730f,1000f);
	public static Vector2 POS_ARROW_SPRITE = new Vector2(1050f,1000f);
	public static Vector2 POS_BOMB_SPRITE = new Vector2(1230f,1000f);

	public static Vector2 POS_PROFILE = new Vector2(640f, 20f);
	public static Vector2 POS_INVENTORY = new Vector2(720f, 30f);
	
	public static Vector2 POS_SKILLS = new Vector2(1050f, 20f);

	public static Vector2 POS_FPS = new Vector2(5f, 130f);

	public static Boolean isTargeting;
	public static boolean displayLevelUpButton = false;
	public static boolean levelUpButtonDisplayed = false;


	public Stage stage;
	public Room room;
		
	// Time and turns
	private boolean debug = true;
	private Table fps;
	private Label fpsLabel;
	
	
	// Floor
	private Table floor;
	private Label floorLabel;
	private Label roomLabel;

	
	// Time and turns
	private Table timeAndTurnTable;
	private Label timeLabel;
	private Label turnLabel;
	
	// Key
	private Image key;
	
	// Money
	private Table moneyTable;
	private Label moneyLabel;
	
	// Status effects
	private Table statusTable;
	private Map<Status, Table> statusesMap;
	public static boolean needStatusRefresh;
	

	// End turn, Health and Experience, profile
	private Table bottomLeftTable; 
	private TextButton endTurnBtn;
	private Table healthTable;
	private Label healthLabel;
	private Label armorLabel;
	private Table xpTable;
	private Label levelLabel;
	private Label expLabel;
	
	Container<TextButton> levelUpButtonContainer;
	
	private Table profileTable; 
	private Button profileBtn;
	private Button inventoryBtn;
	private Button inspectBtn;

	// Skills
	private Table skillsTable;
	private List<Button> allSkillButtons = new ArrayList<>();;
	private Button meleeSkillButton;
	private Button rangeSkillButton;
	private Button bombSkillButton;
	
	// Ammos
	private Table ammoArrowTable;
	private Label arrowLabel;
	private Table ammoBombTable;
	private Label bombLabel;
	
	
	// Room cleared image
	private Table roomClearedTable;
	private Label reward;
	
	
	// Target of the selected creature
	private static Image targetImage;
	
	
	// Components
	private WalletComponent walletComponent;
	private InventoryComponent inventoryComponent;	
	private HealthComponent healthComponent;
	private ExperienceComponent experienceComponent;
	private MoveComponent moveComponent;
	private AttackComponent attackComponent;
	private AmmoCarrierComponent ammoCarrierComponent;
	private StatusReceiverComponent statusReceiverComponent;
	
	
	public HUDRenderer(Stage s) {
		this.stage = s;
		
		targetImage = new Image(Assets.target_marker.getRegion());
		targetImage.addAction(Actions.forever(Actions.sequence(
				Actions.moveBy(0, 30, 1f, Interpolation.pow2), 
				Actions.moveBy(0, -30, 1f, Interpolation.pow2))));
	}
	
	@Override
	public void enterRoom(Room newRoom) {
		this.room = newRoom;
	}

	
	public void render(float deltaTime) {
		if (debug) {
			displayFPS();
		}
		
		displayFloor();
		displayTimeAndTurns();
		displayMoney();
		displayStatuses();
		displayBottomLeftHud();
		displaySkillButtons();
		displayAmmos();
		
		
		if (room.getCleared() == RoomClearedState.JUST_CLEARED) {
			displayRoomCleared();
		}


		if (room.getState().updateNeeded()) {
			// If pause, do not play "act" to prevent the room cleared popup to show while another popup is already displayed
			stage.act(Gdx.graphics.getDeltaTime());
		}
		stage.draw();
		
	}

	
	//***************
	// FPS (debug only)
	private void displayFPS() {
		if (fps == null) {
			fps = new Table();
			fps.setPosition(POS_FPS.x, POS_FPS.y);
			fps.align(Align.left);
			// Turns
			fpsLabel = new Label("", PopinService.hudStyle());
			fps.add(fpsLabel).left().uniformX();

			fps.pack();
			stage.addActor(fps);
		}
		fpsLabel.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
	}

	
	
	//***************
	// Floor
	
	private void displayFloor() {
		if (floor == null) {
			floor = new Table();
			floor.setPosition(POS_FLOOR.x, POS_FLOOR.y - 20);
			floor.align(Align.left);
			// Turns
			floorLabel = new Label("", PopinService.hudStyle());
			floor.add(floorLabel).left().uniformX();

			floor.row();
			
			roomLabel = new Label("", PopinService.hudStyle());
			floor.add(roomLabel).uniformX().left();

			floor.pack();
			stage.addActor(floor);
		}
		floorLabel.setText("Floor " + room.floor.getLevel());
		roomLabel.setText(room.type.title());
	}
	
	
	//************************************
	// TIME AND TURNS
	//
	
	
	private void displayTimeAndTurns() {
		
		if (timeAndTurnTable == null) {
			timeAndTurnTable = new Table();
			timeAndTurnTable.left();
			timeAndTurnTable.setPosition(POS_TIMER.x, POS_TIMER.y - 20);
			
			// Turns
			turnLabel = new Label("", PopinService.hudStyle());
			timeAndTurnTable.add(turnLabel).uniformX().left();

			timeAndTurnTable.row();

			// Time
			timeLabel = new Label("", PopinService.hudStyle());
			timeAndTurnTable.add(timeLabel).uniformX().left();

			timeAndTurnTable.pack();
			stage.addActor(timeAndTurnTable);
		}
		
		GameTimeSingleton gtSingleton = GameTimeSingleton.getInstance();
		timeLabel.setText("Time: " + String.format("%.1f", gtSingleton.getElapsedTime()));
		//timeLabel.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
		turnLabel.setText("Turn " + room.turnManager.getTurn());
		if (room.isCleared()) {
			turnLabel.setText(turnLabel.getText() +  " (CLEARED)");
		}
	}
	
	
	private void displayMoney() {
		if (walletComponent == null) walletComponent = Mappers.walletComponent.get(GameScreen.player);
		if (inventoryComponent == null) inventoryComponent = Mappers.inventoryComponent.get(GameScreen.player);
		
		if (key == null) {
			key = new Image(Assets.key_slot.getRegion());
			key.setPosition(POS_KEY_SLOT.x, POS_KEY_SLOT.y);
			stage.addActor(key);
		}
		if (inventoryComponent.hasKeyChanged()) {
			if (inventoryComponent.hasKey()) key.setDrawable(new TextureRegionDrawable(Assets.key.getRegion()));
			else key.setDrawable(new TextureRegionDrawable(Assets.key_slot.getRegion()));
		}
		
		if (moneyTable == null) {
			moneyTable = new Table();
			moneyTable.setPosition(POS_MONEY.x, POS_MONEY.y);
			
			Image moneyImage = new Image(Assets.inventory_money.getRegion());
			moneyTable.add(moneyImage).uniformX();
			
			// Turns
			moneyLabel = new Label("", PopinService.hudStyle());
			moneyTable.add(moneyLabel).left().uniformX();
			
			moneyTable.pack();
			stage.addActor(moneyTable);
		}
		
		moneyLabel.setText("[GOLD]" + String.valueOf(walletComponent.getAmount()));
	}
	
	
	
	//***************************
	// Status effects
	
	private void displayStatuses() {
		if (statusReceiverComponent == null) statusReceiverComponent = Mappers.statusReceiverComponent.get(GameScreen.player);
		
		if (statusTable == null) {
			statusesMap = new HashMap<>();
			
			statusTable = new Table();
			statusTable.setPosition(POS_STATUSES.x, POS_STATUSES.y);
			
			stage.addActor(statusTable);
		}
		
		if (needStatusRefresh) {
			for (final Status status : statusReceiverComponent.getStatuses()) {
				Table oneStatusTable = statusesMap.get(status);
				if (oneStatusTable == null) {
					
					oneStatusTable = new Table();
					Image image = new Image(status.fullTexture().getRegion());
					image.addListener(new ClickListener() {
						@Override
						public void clicked(InputEvent event, float x, float y) {
							StatusPopinRenderer.status = status;
						}
					});
					
					oneStatusTable.add(image);
					Label dur = new Label(status.getDurationString(), PopinService.hudStyle());
					oneStatusTable.add(dur).bottom();
					statusTable.add(oneStatusTable).left().padTop(2);
					statusTable.row();
					
					statusesMap.put(status, oneStatusTable);
				} else {
					Label l = (Label) oneStatusTable.getCells().get(1).getActor();
					l.setText(status.getDurationString());
				}
			}
			
			// Check the statuses to remove
			for (Entry<Status, Table> entry : statusesMap.entrySet()) {
				Status status = entry.getKey();
				if (!statusReceiverComponent.getStatuses().contains(status)) {
					entry.getValue().remove();
				}
			}
			
			statusTable.pack();			
			needStatusRefresh = false;
		}
	}
	
	
	//************************************
	// LIFE, EXPERIENCE, END TURN
	//

	private void displayBottomLeftHud() {
		if (healthComponent == null) healthComponent = Mappers.healthComponent.get(GameScreen.player);
		if (experienceComponent == null) experienceComponent = Mappers.experienceComponent.get(GameScreen.player);
		if (moveComponent == null) moveComponent = Mappers.moveComponent.get(GameScreen.player);
		if (attackComponent == null) attackComponent = Mappers.attackComponent.get(GameScreen.player);
		
		if (bottomLeftTable == null) {
			bottomLeftTable = new Table();
//			bottomLeftTable.setDebug(true);
			bottomLeftTable.setPosition(POS_END_TURN_BTN.x, POS_END_TURN_BTN.y);
			bottomLeftTable.setTouchable(Touchable.childrenOnly);
			
			endTurnBtn = new TextButton("END TURN\n(spacebar)", PopinService.checkedButtonStyle());
			endTurnBtn.addListener( new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					endTurnBtn.setChecked(false);
					if (room.getState().canEndTurn()) {
						moveComponent.clearMovableTiles();
						attackComponent.clearAttackableTiles();
						room.turnManager.endPlayerTurn();
					} else {
						Boolean b = (Boolean) endTurnBtn.getUserObject();
						if (b != null && b.booleanValue() == true) {
							// Cancel targeting
							isTargeting = false;
							deactivateSkill(GameScreen.player);
						}
					}
				}
			});
			
			// Add shortcut to activate the end turn button
			stage.addListener(new InputListener() {
				public boolean keyDown(InputEvent event, int keycode) {
					if (keycode == Input.Keys.SPACE) {
						if (!endTurnBtn.isDisabled()) {
							endTurnBtn.setProgrammaticChangeEvents(false);
							endTurnBtn.setChecked(true);
						}
						return false;
					}
					return super.keyUp(event, keycode);
				}
				@Override
				public boolean keyUp(InputEvent event, int keycode) {
					if (keycode == Input.Keys.SPACE) {
						if (!endTurnBtn.isDisabled()) {
							endTurnBtn.setProgrammaticChangeEvents(true);
							endTurnBtn.toggle();
						}
						return false;
					}
					return super.keyUp(event, keycode);
				}
			});

			bottomLeftTable.add(endTurnBtn).width(180).height(110);
			
			bottomLeftTable.pack();
			stage.addActor(bottomLeftTable);
		}
		if (isTargeting != null) {
			if (isTargeting) {
				endTurnBtn.setUserObject(Boolean.TRUE);
				endTurnBtn.setText("CANCEL\n(spacebar)");
			} else {
				endTurnBtn.setUserObject(Boolean.FALSE);
				endTurnBtn.setText("END TURN\n(spacebar)");
			}
			isTargeting = null;
		}
		
		// HEALTH
		if (healthTable == null) {
			healthTable = new Table();
			healthTable.setPosition(200, 30);

			// LIFE
			if (healthLabel == null) {
				healthLabel = new Label("", PopinService.hudStyle());
			}
			healthLabel.setText("Hp: " + healthComponent.getHpColor() + healthComponent.getHp() + "[]/" + healthComponent.getMaxHp());
			healthTable.add(healthLabel).left().uniformX();
			healthTable.row();
			
			// ARMOR
			if (armorLabel == null) {
				armorLabel = new Label("", PopinService.hudStyle());
			}
			armorLabel.setText("Armor: " + healthComponent.getArmor() + "/" + healthComponent.getMaxArmor());
			healthTable.add(armorLabel).left().uniformX();
	
			healthTable.pack();
			stage.addActor(healthTable);
		}
		
		healthLabel.setText("Hp: " + healthComponent.getHpColor() + healthComponent.getHp() + "[]/" + healthComponent.getMaxHp());
		armorLabel.setText("Armor: " + healthComponent.getArmorColor() + healthComponent.getArmor() + "[]/" + healthComponent.getMaxArmor());

		
		// XP
		if (xpTable == null) {
			xpTable = new Table();
			xpTable.setPosition(450, 30);
		
			// LEVEL
			if (levelLabel == null) {
				levelLabel = new Label("", PopinService.hudStyle());
			}
			levelLabel.setText("Level [YELLOW]" + experienceComponent.getLevel());
			xpTable.add(levelLabel).left().uniformX();
			xpTable.row();
	
			// XP
			if (expLabel == null) {
				expLabel = new Label("", PopinService.hudStyle());
			}
			expLabel.setText("Exp: [YELLOW]" + experienceComponent.getCurrentXp() + "[]/" + experienceComponent.getNextLevelXp());
			xpTable.add(expLabel).left().uniformX();	
			
			xpTable.pack();
			stage.addActor(xpTable);
		}
		
		levelLabel.setText("Level [YELLOW]" + experienceComponent.getLevel());
		expLabel.setText("Exp: [YELLOW]" + experienceComponent.getCurrentXp() + "[]/" + experienceComponent.getNextLevelXp());
		
		// Level up button
		handleLevelUpButton();
		
		// PROFILE and INVENTORY
		final PlayerComponent playerComponent = Mappers.playerComponent.get(GameScreen.player);
		final InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(GameScreen.player);

		if (profileTable == null) {
			profileTable = new Table();
			profileTable.setPosition(POS_PROFILE.x, POS_PROFILE.y);
		
			// Profile btn
			Table profileButtonTable = new Table();
			Drawable profileButtonUp = new SpriteDrawable(new Sprite(Assets.btn_profile.getRegion()));
			Drawable profileButtonDown = new SpriteDrawable(
					new Sprite(Assets.btn_profile_pushed.getRegion()));
			ButtonStyle profileButtonStyle = new ButtonStyle(profileButtonUp, profileButtonDown, null);
			profileBtn = new Button(profileButtonStyle);
			
			profileBtn.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					inventoryComponent.setDisplayMode(InventoryDisplayModeEnum.NONE);
					playerComponent.setProfilePopinDisplayed(playerComponent.getProfilePopinDisplayMode().isPopinDisplayed() ?
							ProfilePopinDisplayModeEnum.NONE : ProfilePopinDisplayModeEnum.PROFILE);
					if (playerComponent.isProfilePopinDisplayed()) {
						InspectSystem.requestAction(InspectModeActionEnum.DEACTIVATE);
					}
				}
			});
			// P to open profile
			stage.addListener(new InputListener() {
				@Override
				public boolean keyUp(InputEvent event, int keycode) {
					if (keycode == Input.Keys.P) {
						if (!profileBtn.isDisabled()) {
							profileBtn.toggle();
						}
						return false;
					}
					return super.keyUp(event, keycode);
				}
			});
			profileButtonTable.add(profileBtn).center();
			profileButtonTable.row();
			Label profileShortcut = new Label(" (P)", PopinService.hudStyle());
			profileButtonTable.add(profileShortcut).center();
			profileTable.add(profileButtonTable);

			
			// Inventory btn
			Table inventoryButtonTable = new Table();
			Drawable inventoryButtonUp = new SpriteDrawable(new Sprite(Assets.btn_inventory.getRegion()));
			Drawable inventoryButtonDown = new SpriteDrawable(
					new Sprite(Assets.btn_inventory_pushed.getRegion()));
			ButtonStyle inventoryButtonStyle = new ButtonStyle(inventoryButtonUp, inventoryButtonDown, null);
			inventoryBtn = new Button(inventoryButtonStyle);
			
			inventoryBtn.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					playerComponent.setProfilePopinDisplayed(ProfilePopinDisplayModeEnum.NONE);

					if (inventoryComponent.getDisplayMode() != InventoryDisplayModeEnum.NONE) {
						inventoryComponent.setDisplayMode(InventoryDisplayModeEnum.NONE);
					} else {
						if (room.getState().canOpenInventory()) {
							inventoryComponent.setDisplayMode(InventoryDisplayModeEnum.INVENTORY);
							InspectSystem.requestAction(InspectModeActionEnum.DEACTIVATE);
						}
					}
				}
			});
			// I to open inventory
			stage.addListener(new InputListener() {
				@Override
				public boolean keyUp(InputEvent event, int keycode) {
					if (keycode == Input.Keys.I) {
						if (!inventoryBtn.isDisabled()) {
							inventoryBtn.toggle();
						}
						return false;
					}
					return super.keyUp(event, keycode);
				}
			});
			inventoryButtonTable.add(inventoryBtn).center();
			inventoryButtonTable.row();
			Label inventoryShortcut = new Label(" (I)", PopinService.hudStyle());
			inventoryButtonTable.add(inventoryShortcut).center();
			profileTable.add(inventoryButtonTable);
			
			// inspect btn
			Table inspectButtonTable = new Table();
			Drawable inspectButtonUp = new SpriteDrawable(new Sprite(Assets.btn_inspect.getRegion()));
			Drawable inspectButtonDown = new SpriteDrawable(new Sprite(Assets.btn_inspect_pushed.getRegion()));
			Drawable inspectButtonChecked = new SpriteDrawable(new Sprite(Assets.btn_inspect_checked.getRegion()));
			ButtonStyle inspectButtonStyle = new ButtonStyle(inspectButtonUp, inspectButtonDown, inspectButtonChecked);
			inspectBtn = new Button(inspectButtonStyle);
			inspectBtn.setProgrammaticChangeEvents(false);
			inspectBtn.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					if (inspectBtn.isChecked()) {
						playerComponent.setProfilePopinDisplayed(ProfilePopinDisplayModeEnum.NONE);
						inventoryComponent.setDisplayMode(InventoryDisplayModeEnum.NONE);
						InspectSystem.requestAction(InspectModeActionEnum.ACTIVATE);
					} else {
						InspectSystem.requestAction(InspectModeActionEnum.DEACTIVATE);
					}
				}
			});
			// L to inspect
			stage.addListener(new InputListener() {
				@Override
				public boolean keyUp(InputEvent event, int keycode) {
					if (keycode == Input.Keys.O) {
						if (!inspectBtn.isDisabled()) {
							inspectBtn.setProgrammaticChangeEvents(true);
							inspectBtn.toggle();
						}
						return false;
					}
					return super.keyUp(event, keycode);
				}
			});
			inspectButtonTable.add(inspectBtn).center();
			inspectButtonTable.row();
			Label inspectShortcut = new Label(" (O)", PopinService.hudStyle());
			inspectButtonTable.add(inspectShortcut).center();
			profileTable.add(inspectButtonTable);

			
			profileTable.pack();
			stage.addActor(profileTable);
		}
		
		if (inspectBtn.isChecked() && !room.getState().isInspectMode() 
				&& !(room.getNextState() != null && room.getNextState().isInspectMode())) {
			inspectBtn.setChecked(false);
		}
		
	}

	private void handleLevelUpButton() {
		if (levelUpButtonContainer == null) {
			TextButton levelUpButton = new TextButton("LEVEL UP", PopinService.checkedButtonStyle());
			levelUpButton.setProgrammaticChangeEvents(false);
			levelUpButton.addListener( new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					levelUpButton.setChecked(false);
					levelUpButtonContainer.remove();
					levelUpButtonDisplayed = false;
					experienceComponent.setLevelUpPopinDisplayed(true);
					
					AlterationReceiverComponent alterationReceiverComponent = Mappers.alterationReceiverComponent.get(GameScreen.player);
	    			alterationReceiverComponent.onLevelUp(GameScreen.player, room);
				}
			});
	
			levelUpButtonContainer = new Container<>(levelUpButton);
		    levelUpButtonContainer.setTransform(true);   // for enabling scaling and rotation
		    levelUpButtonContainer.pack();
		    levelUpButtonContainer.setPosition(POS_PROFILE.x - levelUpButtonContainer.getWidth()/5, 130);
		    levelUpButtonContainer.setZIndex(100);
		    levelUpButtonContainer.setOrigin(Align.center);
			levelUpButtonContainer.addAction(Actions.forever(
					Actions.sequence(
							Actions.delay(2f), 
							Actions.scaleTo(1.2f, 1.2f, 0.1f),
							Actions.scaleTo(1f, 1f, 0.5f, Interpolation.elasticOut)
							)
					));
		}
		
		
		if (HUDRenderer.displayLevelUpButton) {
			levelUpButtonDisplayed = true;
			
			Image levelUpAura = new Image(Assets.level_up_aura.getRegion());
			levelUpAura.setTouchable(Touchable.disabled);
			levelUpAura.setOrigin(Align.center);
			levelUpAura.setPosition(levelUpButtonContainer.getX() + levelUpButtonContainer.getWidth()/2, 
					levelUpButtonContainer.getY() + levelUpButtonContainer.getHeight()/2, Align.center);
			levelUpAura.addAction(Actions.alpha(0f));
			levelUpAura.addAction(Actions.scaleTo(0f, 0f));
			
			Action removeImageAction = new Action(){
				  @Override
				  public boolean act(float delta){
					levelUpAura.remove();
				    return true;
				  }
				};
				
			levelUpAura.addAction( Actions.sequence( Actions.parallel(
						Actions.alpha(0.8f, 2f),
						Actions.scaleTo(1f, 1f, 1f),
						Actions.rotateBy(360f, 4f),
						Actions.sequence(Actions.delay(2f), Actions.fadeOut(1f))),
						removeImageAction
					));
			stage.addActor(levelUpAura);
			stage.addActor(levelUpButtonContainer);
			
			HUDRenderer.displayLevelUpButton = false;
		}
	}

	
	//************************************
	// SKILLS
	//
	
	private void displaySkillButtons() {
		
		if (skillsTable == null) {
			allSkillButtons.clear();
			
			skillsTable = new Table();
			skillsTable.setPosition(POS_SKILLS.x, POS_SKILLS.y);
			skillsTable.setTouchable(Touchable.enabled);
	
			if (meleeSkillButton == null) {
				Table meleeSkillButtonTable = new Table();
				Drawable meleeSkillButtonUp = new SpriteDrawable(new Sprite(Assets.btn_skill_attack.getRegion()));
				Drawable meleeSkillButtonDown = new SpriteDrawable(
						new Sprite(Assets.btn_skill_attack_pushed.getRegion()));
				Drawable meleeSkillButtonChecked = new SpriteDrawable(
						new Sprite(Assets.btn_skill_attack_checked.getRegion()));
				ButtonStyle meleeSkillButtonStyle = new ButtonStyle(meleeSkillButtonUp, meleeSkillButtonDown,
						meleeSkillButtonChecked);
				meleeSkillButton = new Button(meleeSkillButtonStyle);
				meleeSkillButton.setProgrammaticChangeEvents(true);
	
				meleeSkillButton.addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
	
						if (room.getState().isSkillChangeAllowed()) {
							if (meleeSkillButton.isChecked()) {
								boolean activated = activateSkill(meleeSkillButton);
								if (activated) {
									uncheckSkill(rangeSkillButton);
									uncheckSkill(bombSkillButton);
								} else {
									uncheckSkill(meleeSkillButton);
								}
	
							} else {
								deactivateSkill(GameScreen.player);
							}
						}
					}
	
				});
	
				// Add shortcut to activate the button
				stage.addListener(new InputListener() {
					@Override
					public boolean keyUp(InputEvent event, int keycode) {
						if (keycode == Input.Keys.NUM_1) {
							if (!meleeSkillButton.isDisabled()) {
								meleeSkillButton.toggle();
							}
							return false;
						}
						return super.keyUp(event, keycode);
					}
				});
				allSkillButtons.add(meleeSkillButton);

				meleeSkillButtonTable.add(meleeSkillButton).center();
				meleeSkillButtonTable.row();
				Label meleeShortcut = new Label(" (1)", PopinService.hudStyle());
				meleeSkillButtonTable.add(meleeShortcut).center();
				skillsTable.add(meleeSkillButtonTable);

			}
			
			
	
			if (rangeSkillButton == null) {
				Table rangeSkillButtonTable = new Table();
				Drawable rangeSkillButtonUp = new SpriteDrawable(new Sprite(Assets.btn_skill_bow.getRegion()));
				Drawable rangeSkillButtonDown = new SpriteDrawable(
						new Sprite(Assets.btn_skill_bow_pushed.getRegion()));
				Drawable rangeSkillButtonChecked = new SpriteDrawable(
						new Sprite(Assets.btn_skill_bow_checked.getRegion()));
				ButtonStyle rangeSkillButtonStyle = new ButtonStyle(rangeSkillButtonUp, rangeSkillButtonDown,
						rangeSkillButtonChecked);
				rangeSkillButton = new Button(rangeSkillButtonStyle);
				rangeSkillButton.setProgrammaticChangeEvents(true);
	
				rangeSkillButton.addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						
						if (room.getState().isSkillChangeAllowed()) {
							if (rangeSkillButton.isChecked()) {
								boolean activated = activateSkill(rangeSkillButton);
								if (activated) {
									uncheckSkill(meleeSkillButton);
									uncheckSkill(bombSkillButton);
								} else {
									uncheckSkill(rangeSkillButton);
								}
	
							} else {
								deactivateSkill(GameScreen.player);
							}
						} else {
							rangeSkillButton.setChecked(!rangeSkillButton.isChecked());
						}
					}
	
				});
	
				// Add shortcut to activate the button
				stage.addListener(new InputListener() {
					@Override
					public boolean keyUp(InputEvent event, int keycode) {
						if (keycode == Input.Keys.NUM_2) {
							if (!rangeSkillButton.isDisabled()) {
								rangeSkillButton.toggle();
							}
							return false;
						}
						return super.keyUp(event, keycode);
					}
				});
	
				allSkillButtons.add(rangeSkillButton);
				
				
				rangeSkillButtonTable.add(rangeSkillButton).center();
				rangeSkillButtonTable.row();
				Label rangeShortcut = new Label(" (2)", PopinService.hudStyle());
				rangeSkillButtonTable.add(rangeShortcut).center();
				skillsTable.add(rangeSkillButtonTable);
			}			
			
			if (bombSkillButton == null) {
				Table bombSkillButtonTable = new Table();
				Drawable rangeSkillButtonUp = new SpriteDrawable(new Sprite(Assets.btn_skill_bomb.getRegion()));
				Drawable rangeSkillButtonDown = new SpriteDrawable(
						new Sprite(Assets.btn_skill_bomb_pushed.getRegion()));
				Drawable rangeSkillButtonChecked = new SpriteDrawable(
						new Sprite(Assets.btn_skill_bomb_checked.getRegion()));
				ButtonStyle rangeSkillButtonStyle = new ButtonStyle(rangeSkillButtonUp, rangeSkillButtonDown,
						rangeSkillButtonChecked);
				bombSkillButton = new Button(rangeSkillButtonStyle);
				bombSkillButton.setProgrammaticChangeEvents(true);

				bombSkillButton.addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						
						if (room.getState().isSkillChangeAllowed()) {
							if (bombSkillButton.isChecked()) {							
								boolean activated = activateSkill(bombSkillButton);
								if (activated) {
									uncheckSkill(meleeSkillButton);
									uncheckSkill(rangeSkillButton);
								} else {
									uncheckSkill(bombSkillButton);
								}

							} else {
								deactivateSkill(GameScreen.player);
							}
						} else {
							bombSkillButton.setChecked(!bombSkillButton.isChecked());
						}
					}

				});

				// Add shortcut to activate the button
				stage.addListener(new InputListener() {
					@Override
					public boolean keyUp(InputEvent event, int keycode) {
						if (keycode == Input.Keys.NUM_3) {
							if (!bombSkillButton.isDisabled()) {
								bombSkillButton.toggle();
							}
							return false;
						}
						return super.keyUp(event, keycode);
					}
				});

				allSkillButtons.add(bombSkillButton);
				
				
				bombSkillButtonTable.add(bombSkillButton).center();
				bombSkillButtonTable.row();
				Label bombShortcut = new Label(" (3)", PopinService.hudStyle());
				bombSkillButtonTable.add(bombShortcut).center();
				skillsTable.add(bombSkillButtonTable);
			}			

	
			skillsTable.pack();
			stage.addActor(skillsTable);
		}
		
		
		for (Button btn : allSkillButtons) {
			btn.setDisabled(!room.getState().isSkillChangeAllowed());
			if (room.getState() == RoomState.PLAYER_END_TURN || room.getState() == RoomState.PLAYER_TARGETING_STOP) {
				btn.setProgrammaticChangeEvents(false);
				btn.setChecked(false);
				isTargeting = false;
			} else {
				btn.setProgrammaticChangeEvents(true);
			}
		}
		
	}

	private boolean activateSkill(Button button) {
		boolean canActivate = false;

		PlayerComponent playerComponent = Mappers.playerComponent.get(GameScreen.player);
		AmmoCarrierComponent ammoCarrierComponent = Mappers.ammoCarrierComponent.get(GameScreen.player);
		if (button == meleeSkillButton) {
			SkillComponent skillComponent = Mappers.skillComponent.get(playerComponent.getSkillMelee());
			if (ammoCarrierComponent.canUseAmmo(skillComponent.getType().getAmmosType(), skillComponent.getType().getNbOfAmmosPerAttack())) {
				playerComponent.setActiveSkill(playerComponent.getSkillMelee());
				canActivate = true;
			}
		} else if (button == rangeSkillButton) {
			SkillComponent skillComponent = Mappers.skillComponent.get(playerComponent.getSkillRange());
			if (ammoCarrierComponent.canUseAmmo(skillComponent.getType().getAmmosType(), skillComponent.getType().getNbOfAmmosPerAttack())) {
				playerComponent.setActiveSkill(playerComponent.getSkillRange());
				canActivate = true;
			}
		} else if (button == bombSkillButton) {
			SkillComponent skillComponent = Mappers.skillComponent.get(playerComponent.getSkillBomb());
			if (ammoCarrierComponent.canUseAmmo(skillComponent.getType().getAmmosType(), skillComponent.getType().getNbOfAmmosPerAttack())) {
				playerComponent.setActiveSkill(playerComponent.getSkillBomb());
				canActivate = true;
			}
		}
	
		if (canActivate) {
			room.setNextState(RoomState.PLAYER_TARGETING_START);
		} else {
			//TODO improve
			if (button == rangeSkillButton) {
				healthComponent.healthChangeMap.put(HealthChangeEnum.HIT, "NO ARROW");
			} else if (button == bombSkillButton) {
				healthComponent.healthChangeMap.put(HealthChangeEnum.HIT, "NO BOMB");
			}
//			Journal.addEntry("[SCARLET]No ammos to activate the skill!");
		}
		return canActivate;
	}

	private void deactivateSkill(Entity player) {
		room.setNextState(RoomState.PLAYER_TARGETING_STOP);
	}
	
	
	private void uncheckSkill(Button button) {
		button.setProgrammaticChangeEvents(false);
		button.setChecked(false);
		button.setProgrammaticChangeEvents(true);
	}

	
	
	//************************************
	// AMMOS
	//
	
	
	private void displayAmmos() {
		if (ammoCarrierComponent == null) ammoCarrierComponent = Mappers.ammoCarrierComponent.get(GameScreen.player);
		
		if (ammoArrowTable == null) {
			ammoArrowTable = new Table();
			ammoArrowTable.setPosition(POS_ARROW_SPRITE.x, POS_ARROW_SPRITE.y);
			
			// Arrows
			Image arrowImage = new Image(Assets.arrow_item.getRegion());
			ammoArrowTable.add(arrowImage).uniformX();
			
			arrowLabel = new Label("", PopinService.hudStyle());
			ammoArrowTable.add(arrowLabel).left().uniformX();
			
			ammoArrowTable.pack();
			stage.addActor(ammoArrowTable);
		}
		
		if (ammoBombTable == null) {
			ammoBombTable = new Table();
			ammoBombTable.setPosition(POS_BOMB_SPRITE.x, POS_BOMB_SPRITE.y);

			// Bombs
			Image bombImage = new Image(Assets.bomb_item.getRegion());
			ammoBombTable.add(bombImage).uniformX();
			
			bombLabel = new Label("", PopinService.hudStyle());
			ammoBombTable.add(bombLabel).left().uniformX();			

			ammoBombTable.pack();
			stage.addActor(ammoBombTable);
		}
		
		arrowLabel.setText(ammoCarrierComponent.getArrows() + "/" + ammoCarrierComponent.getMaxArrows());
		bombLabel.setText(ammoCarrierComponent.getBombs() + "/" + ammoCarrierComponent.getMaxBombs());
	}
	
	
	
	//***********************
	// Animations
	
	/**
	 * Display the animation when a room was just cleared.
	 */
	private void displayRoomCleared() {
		if (roomClearedTable == null) {
			
			this.roomClearedTable = new Table();
			TextureRegionDrawable background = new TextureRegionDrawable(Assets.small_popin_background.getRegion());
			roomClearedTable.setBackground(background);
			
			Label roomCleared = new Label("ROOM CLEARED", PopinService.hudStyle());
			this.roomClearedTable.add(roomCleared).pad(0, 0, 20, 0);
			this.roomClearedTable.row();
			
			this.reward = new Label("", PopinService.hudStyle());
			this.roomClearedTable.add(reward);
			this.roomClearedTable.setOrigin(Align.center);
			this.roomClearedTable.setVisible(false);
			this.roomClearedTable.pack();
			this.roomClearedTable.setPosition(GameScreen.SCREEN_W/2 - background.getRegion().getRegionWidth()/2, GameScreen.SCREEN_H/2 - roomClearedTable.getHeight()/2);

			stage.addActor(this.roomClearedTable);
		}
		
//		this.roomClearedImage.setVisible(true);
//		this.roomClearedImage.addAction(Actions.sequence(Actions.alpha(1),
//				Actions.scaleBy(20, 20),
//				Actions.scaleTo(1, 1, 1, Interpolation.pow5Out),
//				Actions.delay(2.5f),
//				Actions.fadeOut(1, Interpolation.pow5In)));
		
		this.roomClearedTable.setVisible(true);
		String rewardsText = buildRewards(room.getRewards());
		this.reward.setText(rewardsText);
		float duration = rewardsText.length()/20f;
		
		this.roomClearedTable.pack();
		this.roomClearedTable.addAction(Actions.sequence(Actions.alpha(0f),
				Actions.alpha(0.8f, 1, Interpolation.pow5Out),
				Actions.delay(duration),
				Actions.fadeOut(1, Interpolation.pow5In)));

	}
	
	private String buildRewards(List<AbstractRoomReward> rewards) {
		StringBuilder sb = new StringBuilder();
		sb.append("REWARDS\n");
		
		for ( int i=0 ; i < rewards.size() ; i++) {
			AbstractRoomReward reward = rewards.get(i);
			sb.append(reward.getColor() + reward.getQuantity() + " " + reward.getTitle());
			if (i != rewards.size() - 1) {
				sb.append("\n");
			}
		}
		
		return sb.toString();
	}
	
	
	public static void displayTargetMaker(Vector2 gridPos) {
		if (targetImage.hasParent()) return; 
		
		PoolableVector2 pixelPos = TileUtil.convertGridPosIntoPixelPos(gridPos);
		pixelPos.x += GameScreen.GRID_SIZE/2 - targetImage.getWidth()/2;
		pixelPos.y += 60;
		targetImage.setPosition(pixelPos.x, pixelPos.y);
		pixelPos.free();
		
		for (Action a : targetImage.getActions()) {
			a.restart();
		}
		GameScreen.fxStage.addActor(targetImage);
	}
	
	public static void hideTargetMaker() {
		targetImage.remove();
	}
	
	
}
