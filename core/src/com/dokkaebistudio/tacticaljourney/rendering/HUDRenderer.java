package com.dokkaebistudio.tacticaljourney.rendering;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameTimeSingleton;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AmmoCarrierComponent;
import com.dokkaebistudio.tacticaljourney.components.player.ExperienceComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.components.player.SkillComponent;
import com.dokkaebistudio.tacticaljourney.components.player.WalletComponent;
import com.dokkaebistudio.tacticaljourney.enums.InventoryDisplayModeEnum;
import com.dokkaebistudio.tacticaljourney.rendering.interfaces.Renderer;
import com.dokkaebistudio.tacticaljourney.rendering.service.PopinService;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class HUDRenderer implements Renderer, RoomSystem {
	
	public static Vector2 POS_TIMER = new Vector2(200f, 1030.0f);
	public static Vector2 POS_END_TURN_BTN = new Vector2(5f, 5f);
	public static Vector2 POS_KEY_SLOT = new Vector2(650f,1000f);
	public static Vector2 POS_MONEY = new Vector2(730f,1000f);
	public static Vector2 POS_ARROW_SPRITE = new Vector2(1050f,1000f);
	public static Vector2 POS_BOMB_SPRITE = new Vector2(1230f,1000f);

	
	public static Vector2 POS_INVENTORY = new Vector2(780f, 30f);



	public Stage stage;
	public Room room;
	public Entity player;
		
	// Time and turns
	private boolean debug = true;
	private Table fps;
	private Label fpsLabel;
	
	// Time and turns
	private Table timeAndTurnTable;
	private Label timeLabel;
	private Label turnLabel;
	
	// Key
	private Image key;
	
	// Money
	private Table moneyTable;
	private Label moneyLabel;
	

	// End turn, Health and Experience, profile
	private Table bottomLeftTable; 
	private Button endTurnBtn;
	private Table healthTable;
	private Label healthLabel;
	private Label armorLabel;
	private Table xpTable;
	private Label levelLabel;
	private Label expLabel;
	
	private Table profileTable; 
	private Button profileBtn;
	private Button inventoryBtn;

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

	
	
	// Components
	private WalletComponent walletComponent;
	private InventoryComponent inventoryComponent;	
	private HealthComponent healthComponent;
	private ExperienceComponent experienceComponent;
	private MoveComponent moveComponent;
	private AttackComponent attackComponent;
	private AmmoCarrierComponent ammoCarrierComponent;
	
	
	public HUDRenderer(Stage s, Entity player) {
		this.stage = s;
		this.player = player;
	}
	
	@Override
	public void enterRoom(Room newRoom) {
		this.room = newRoom;
	}

	
	public void render(float deltaTime) {
		if (debug) {
			displayFPS();
		}
		
		displayTimeAndTurns();
		displayMoney();
		displayBottomLeftHud();
		displaySkillButtons();
		displayAmmos();

		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();

	}

	
	//***************
	// FPS (debug only)
	private void displayFPS() {
		if (fps == null) {
			fps = new Table();
			fps.setPosition(0, 1050);
			fps.align(Align.left);
			// Turns
			fpsLabel = new Label("", PopinService.hudStyle());
			fps.add(fpsLabel).left().uniformX();

			fps.pack();
			stage.addActor(fps);
		}
		fpsLabel.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
	}
	
	
	
	//************************************
	// TIME AND TURNS
	//
	
	
	private void displayTimeAndTurns() {
		
		if (timeAndTurnTable == null) {
			timeAndTurnTable = new Table();
			timeAndTurnTable.setPosition(POS_TIMER.x, POS_TIMER.y - 20);
			
			// Turns
			turnLabel = new Label("", PopinService.hudStyle());
			timeAndTurnTable.add(turnLabel).uniformX();

			timeAndTurnTable.row();

			// Time
			timeLabel = new Label("", PopinService.hudStyle());
			timeAndTurnTable.add(timeLabel).uniformX();

			timeAndTurnTable.pack();
			stage.addActor(timeAndTurnTable);
		}
		
		GameTimeSingleton gtSingleton = GameTimeSingleton.getInstance();
		timeLabel.setText("Time: " + String.format("%.1f", gtSingleton.getElapsedTime()));
		//timeLabel.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
		turnLabel.setText("Turn " + room.turnManager.getTurn());
	}
	
	
	private void displayMoney() {
		if (walletComponent == null) walletComponent = Mappers.walletComponent.get(player);
		if (inventoryComponent == null) inventoryComponent = Mappers.inventoryComponent.get(player);
		
		if (key == null) {
			key = new Image(Assets.key_slot);
			key.setPosition(POS_KEY_SLOT.x, POS_KEY_SLOT.y);
			stage.addActor(key);
		}
		if (inventoryComponent.hasKeyChanged()) {
			if (inventoryComponent.hasKey()) key.setDrawable(new TextureRegionDrawable(Assets.key));
			else key.setDrawable(new TextureRegionDrawable(Assets.key_slot));
		}
		
		if (moneyTable == null) {
			moneyTable = new Table();
			moneyTable.setPosition(POS_MONEY.x, POS_MONEY.y);
			
			Image moneyImage = new Image(Assets.inventory_money);
			moneyTable.add(moneyImage).uniformX();
			
			// Turns
			moneyLabel = new Label("", PopinService.hudStyle());
			moneyTable.add(moneyLabel).left().uniformX();
			
			moneyTable.pack();
			stage.addActor(moneyTable);
		}
		
		moneyLabel.setText("[GOLD]" + String.valueOf(walletComponent.getAmount()));
	}
	
	
	//************************************
	// LIFE, EXPERIENCE, END TURN
	//

	private void displayBottomLeftHud() {
		if (healthComponent == null) healthComponent = Mappers.healthComponent.get(player);
		if (experienceComponent == null) experienceComponent = Mappers.experienceComponent.get(player);
		if (moveComponent == null) moveComponent = Mappers.moveComponent.get(player);
		if (attackComponent == null) attackComponent = Mappers.attackComponent.get(player);
		
		if (bottomLeftTable == null) {
			bottomLeftTable = new Table();
//			bottomLeftTable.setDebug(true);
			bottomLeftTable.setPosition(POS_END_TURN_BTN.x, POS_END_TURN_BTN.y);
			bottomLeftTable.setTouchable(Touchable.childrenOnly);
			
			Drawable endTurnButtonUp = new SpriteDrawable(new Sprite(Assets.btn_end_turn));
			Drawable endTurnButtonDown = new SpriteDrawable(new Sprite(Assets.btn_end_turn_pushed));
			ButtonStyle endTurnButtonStyle = new ButtonStyle(endTurnButtonUp, endTurnButtonDown,null);
			endTurnBtn = new Button(endTurnButtonStyle);
			
			endTurnBtn.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {

					if (room.getState().canEndTurn()) {
						moveComponent.clearMovableTiles();
						attackComponent.clearAttackableTiles();
						room.turnManager.endPlayerTurn();
					}
				}

			});
			
			// Add shortcut to activate the end turn button
			stage.addListener(new InputListener() {
				@Override
				public boolean keyUp(InputEvent event, int keycode) {
					if (keycode == Input.Keys.SPACE) {
						if (!endTurnBtn.isDisabled()) {
							endTurnBtn.toggle();
						}
						return false;
					}
					return super.keyUp(event, keycode);
				}
			});

			bottomLeftTable.add(endTurnBtn);
			
			bottomLeftTable.pack();
			stage.addActor(bottomLeftTable);
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
		
		
		// PROFILE and INVENTORY
		final PlayerComponent playerComponent = Mappers.playerComponent.get(player);
		final InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(player);

		if (profileTable == null) {
			profileTable = new Table();
			profileTable.setPosition(700, 30);
		
			// Profile btn
			Drawable profileButtonUp = new SpriteDrawable(new Sprite(Assets.btn_profile));
			Drawable profileButtonDown = new SpriteDrawable(
					new Sprite(Assets.btn_profile_pushed));
			ButtonStyle profileButtonStyle = new ButtonStyle(profileButtonUp, profileButtonDown, null);
			profileBtn = new Button(profileButtonStyle);
			
			profileBtn.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					inventoryComponent.setDisplayMode(InventoryDisplayModeEnum.NONE);
					playerComponent.setProfilePopinDisplayed(!playerComponent.isProfilePopinDisplayed());
				}
			});
			profileTable.add(profileBtn);

			
			// Inventory btn
			Drawable inventoryButtonUp = new SpriteDrawable(new Sprite(Assets.btn_inventory));
			Drawable inventoryButtonDown = new SpriteDrawable(
					new Sprite(Assets.btn_inventory_pushed));
			ButtonStyle inventoryButtonStyle = new ButtonStyle(inventoryButtonUp, inventoryButtonDown, null);
			inventoryBtn = new Button(inventoryButtonStyle);
			
			inventoryBtn.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					playerComponent.setProfilePopinDisplayed(false);

					if (inventoryComponent.getDisplayMode() != InventoryDisplayModeEnum.NONE) {
						inventoryComponent.setDisplayMode(InventoryDisplayModeEnum.NONE);
					} else {
						inventoryComponent.setDisplayMode(InventoryDisplayModeEnum.INVENTORY);
					}
				}
			});
			profileTable.add(inventoryBtn);

			
			profileTable.pack();
			stage.addActor(profileTable);
		}
		
		
		
	}

	
	//************************************
	// SKILLS
	//
	
	private void displaySkillButtons() {
		
		if (skillsTable == null) {
			allSkillButtons.clear();
			
			skillsTable = new Table();
			skillsTable.setPosition(1500, 30);
			skillsTable.setTouchable(Touchable.enabled);
	
			if (meleeSkillButton == null) {
				Drawable meleeSkillButtonUp = new SpriteDrawable(new Sprite(Assets.btn_skill_attack));
				Drawable meleeSkillButtonDown = new SpriteDrawable(
						new Sprite(Assets.btn_skill_attack_pushed));
				Drawable meleeSkillButtonChecked = new SpriteDrawable(
						new Sprite(Assets.btn_skill_attack_checked));
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
								deactivateSkill(player);
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
			}
			skillsTable.add(meleeSkillButton);
			
			
	
			if (rangeSkillButton == null) {
				Drawable rangeSkillButtonUp = new SpriteDrawable(new Sprite(Assets.btn_skill_bow));
				Drawable rangeSkillButtonDown = new SpriteDrawable(
						new Sprite(Assets.btn_skill_bow_pushed));
				Drawable rangeSkillButtonChecked = new SpriteDrawable(
						new Sprite(Assets.btn_skill_bow_checked));
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
								deactivateSkill(player);
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
				skillsTable.add(rangeSkillButton);

			}			
			
			if (bombSkillButton == null) {
				Drawable rangeSkillButtonUp = new SpriteDrawable(new Sprite(Assets.btn_skill_bomb));
				Drawable rangeSkillButtonDown = new SpriteDrawable(
						new Sprite(Assets.btn_skill_bomb_pushed));
				Drawable rangeSkillButtonChecked = new SpriteDrawable(
						new Sprite(Assets.btn_skill_bomb_checked));
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
								deactivateSkill(player);
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
				skillsTable.add(bombSkillButton);
			}			

	
			skillsTable.pack();
			stage.addActor(skillsTable);
		}
		
		
		for (Button btn : allSkillButtons) {
			btn.setDisabled(!room.getState().isSkillChangeAllowed());
			if (room.getState() == RoomState.PLAYER_END_TURN) {
				btn.setProgrammaticChangeEvents(false);
				btn.setChecked(false);
			} else {
				btn.setProgrammaticChangeEvents(true);
			}
		}
		
	}

	private boolean activateSkill(Button button) {
		boolean canActivate = false;

		PlayerComponent playerComponent = Mappers.playerComponent.get(player);
		AmmoCarrierComponent ammoCarrierComponent = Mappers.ammoCarrierComponent.get(player);
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
		if (ammoCarrierComponent == null) ammoCarrierComponent = Mappers.ammoCarrierComponent.get(player);
		
		if (ammoArrowTable == null) {
			ammoArrowTable = new Table();
			ammoArrowTable.setPosition(POS_ARROW_SPRITE.x, POS_ARROW_SPRITE.y);
			
			// Arrows
			Image arrowImage = new Image(Assets.arrow_item);
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
			Image bombImage = new Image(Assets.bomb_item);
			ammoBombTable.add(bombImage).uniformX();
			
			bombLabel = new Label("", PopinService.hudStyle());
			ammoBombTable.add(bombLabel).left().uniformX();			

			ammoBombTable.pack();
			stage.addActor(ammoBombTable);
		}
		
		arrowLabel.setText(ammoCarrierComponent.getArrows() + "/" + ammoCarrierComponent.getMaxArrows());
		bombLabel.setText(ammoCarrierComponent.getBombs() + "/" + ammoCarrierComponent.getMaxBombs());
	}
	
}
