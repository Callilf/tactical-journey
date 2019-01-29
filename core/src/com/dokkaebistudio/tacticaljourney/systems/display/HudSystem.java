package com.dokkaebistudio.tacticaljourney.systems.display;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameTimeSingleton;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AmmoCarrierComponent;
import com.dokkaebistudio.tacticaljourney.components.player.ExperienceComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.components.player.SkillComponent;
import com.dokkaebistudio.tacticaljourney.constants.PositionConstants;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class HudSystem extends IteratingSystem implements RoomSystem {

	public Stage stage;

	/** The current room. */
	private Room room;
	
	private LabelStyle hudStyle;
	
	// Time and turns
	private Table timeAndTurnTable;
	private Label timeLabel;
	private Label turnLabel;
	

	// End turn, Health and Experience
	private Table bottomLeftTable; 
	private Button endTurnBtn;
	private Table healthAndXptable; 
	private Label levelLabel;
	private Label expLabel;
	private Label healthLabel;

	// Skills
	private Table skillsTable;
	private List<Button> allSkillButtons = new ArrayList<>();;
	private Button meleeSkillButton;
	private Button rangeSkillButton;
	private Button bombSkillButton;
	
	// Ammos
	private Table ammoTable;
	private Label arrowLabel;
	private Label bombLabel;

	public HudSystem(Room r, Stage s) {
		super(Family.all(PlayerComponent.class).get());
		room = r;
		this.stage = s;
		
		hudStyle = new LabelStyle(Assets.font, Color.WHITE);
	}

	@Override
	public void enterRoom(Room newRoom) {
		this.room = newRoom;
	}

	@Override
	protected void processEntity(Entity player, float deltaTime) {
		displayTimeAndTurns();
		displayBottomLeftHud(player);
		displaySkillButtons(player);
		displayAmmos(player);


		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();

	}
	
	
	
	//************************************
	// TIME AND TURNS
	//
	
	
	private void displayTimeAndTurns() {
		
		if (timeAndTurnTable == null) {
			timeAndTurnTable = new Table();
			timeAndTurnTable.setPosition(PositionConstants.POS_TIMER.x, PositionConstants.POS_TIMER.y - 20);
			
			// Turns
			turnLabel = new Label("", hudStyle);
			timeAndTurnTable.add(turnLabel).uniformX();

			timeAndTurnTable.row();

			// Time
			timeLabel = new Label("", hudStyle);
			timeAndTurnTable.add(timeLabel).uniformX();

			timeAndTurnTable.pack();
			stage.addActor(timeAndTurnTable);
		}
		
		GameTimeSingleton gtSingleton = GameTimeSingleton.getInstance();
		timeLabel.setText("Time: " + String.format("%.1f", gtSingleton.getElapsedTime()));
		
		turnLabel.setText("Turn " + room.turnManager.getTurn());
	}
	
	
	
	//************************************
	// LIFE, EXPERIENCE, END TURN
	//

	private void displayBottomLeftHud(Entity player) {
		HealthComponent healthComponent = Mappers.healthComponent.get(player);
		ExperienceComponent experienceComponent = Mappers.experienceComponent.get(player);
		final MoveComponent moveComponent = Mappers.moveComponent.get(player);
		final AttackComponent attackComponent = Mappers.attackComponent.get(player);
		
		if (bottomLeftTable == null) {
			bottomLeftTable = new Table();
			bottomLeftTable.setPosition(PositionConstants.POS_END_TURN_BTN.x, PositionConstants.POS_END_TURN_BTN.y);
			bottomLeftTable.setTouchable(Touchable.childrenOnly);
			
			Drawable endTurnButtonUp = new SpriteDrawable(new Sprite(Assets.getTexture(Assets.btn_end_turn)));
			Drawable endTurnButtonDown = new SpriteDrawable(new Sprite(Assets.getTexture(Assets.btn_end_turn_pushed)));
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

		if (healthAndXptable == null) {
			healthAndXptable = new Table();
			healthAndXptable.setPosition(200, 30);
		
			// LEVEL
			if (levelLabel == null) {
				levelLabel = new Label("", hudStyle);
			}
			levelLabel.setText("Level [YELLOW]" + experienceComponent.getLevel());
			healthAndXptable.add(levelLabel).left().uniformX();
			healthAndXptable.row();
	
			// XP
			if (expLabel == null) {
				expLabel = new Label("", hudStyle);
			}
			expLabel.setText("Exp: [YELLOW]" + experienceComponent.getCurrentXp() + "[]/" + experienceComponent.getNextLevelXp());
	
			healthAndXptable.add(expLabel).left().uniformX();
			healthAndXptable.row();
	
			// LIFE
			if (healthLabel == null) {
				healthLabel = new Label("", hudStyle);
			}
			healthLabel.setText("Hp: " + healthComponent.getHpColor() + healthComponent.getHp() + "[]/" + healthComponent.getMaxHp());
			healthAndXptable.add(healthLabel).left().uniformX();
	
			healthAndXptable.pack();
			stage.addActor(healthAndXptable);
		}
		
		levelLabel.setText("Level [YELLOW]" + experienceComponent.getLevel());
		expLabel.setText("Exp: [YELLOW]" + experienceComponent.getCurrentXp() + "[]/" + experienceComponent.getNextLevelXp());
		healthLabel.setText("Hp: " + healthComponent.getHpColor() + healthComponent.getHp() + "[]/" + healthComponent.getMaxHp());
	}

	
	//************************************
	// SKILLS
	//
	
	private void displaySkillButtons(final Entity player) {
		
		if (skillsTable == null) {
			allSkillButtons.clear();
			
			skillsTable = new Table();
			skillsTable.setPosition(1500, 30);
			skillsTable.setTouchable(Touchable.enabled);
	
			if (meleeSkillButton == null) {
				Drawable meleeSkillButtonUp = new SpriteDrawable(new Sprite(Assets.getTexture(Assets.btn_skill_attack)));
				Drawable meleeSkillButtonDown = new SpriteDrawable(
						new Sprite(Assets.getTexture(Assets.btn_skill_attack_pushed)));
				Drawable meleeSkillButtonChecked = new SpriteDrawable(
						new Sprite(Assets.getTexture(Assets.btn_skill_attack_checked)));
				ButtonStyle meleeSkillButtonStyle = new ButtonStyle(meleeSkillButtonUp, meleeSkillButtonDown,
						meleeSkillButtonChecked);
				meleeSkillButton = new Button(meleeSkillButtonStyle);
				meleeSkillButton.setProgrammaticChangeEvents(true);
	
				meleeSkillButton.addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
	
						if (room.getState().isSkillChangeAllowed()) {
							if (meleeSkillButton.isChecked()) {
								boolean activated = activateSkill(meleeSkillButton, player);
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
				Drawable rangeSkillButtonUp = new SpriteDrawable(new Sprite(Assets.getTexture(Assets.btn_skill_bow)));
				Drawable rangeSkillButtonDown = new SpriteDrawable(
						new Sprite(Assets.getTexture(Assets.btn_skill_bow_pushed)));
				Drawable rangeSkillButtonChecked = new SpriteDrawable(
						new Sprite(Assets.getTexture(Assets.btn_skill_bow_checked)));
				ButtonStyle rangeSkillButtonStyle = new ButtonStyle(rangeSkillButtonUp, rangeSkillButtonDown,
						rangeSkillButtonChecked);
				rangeSkillButton = new Button(rangeSkillButtonStyle);
				rangeSkillButton.setProgrammaticChangeEvents(true);
	
				rangeSkillButton.addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						
						if (room.getState().isSkillChangeAllowed()) {
							if (rangeSkillButton.isChecked()) {
								boolean activated = activateSkill(rangeSkillButton, player);
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
			}			
			skillsTable.add(rangeSkillButton);
	
			skillsTable.pack();
			stage.addActor(skillsTable);
		}
		
		
		
		
		if (bombSkillButton == null) {
			Drawable rangeSkillButtonUp = new SpriteDrawable(new Sprite(Assets.getTexture(Assets.btn_skill_bomb)));
			Drawable rangeSkillButtonDown = new SpriteDrawable(
					new Sprite(Assets.getTexture(Assets.btn_skill_bomb_pushed)));
			Drawable rangeSkillButtonChecked = new SpriteDrawable(
					new Sprite(Assets.getTexture(Assets.btn_skill_bomb_checked)));
			ButtonStyle rangeSkillButtonStyle = new ButtonStyle(rangeSkillButtonUp, rangeSkillButtonDown,
					rangeSkillButtonChecked);
			bombSkillButton = new Button(rangeSkillButtonStyle);
			bombSkillButton.setProgrammaticChangeEvents(true);

			bombSkillButton.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					
					if (room.getState().isSkillChangeAllowed()) {
						if (bombSkillButton.isChecked()) {							
							boolean activated = activateSkill(bombSkillButton, player);
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
		}			
		skillsTable.add(bombSkillButton);
		
		
		
		
		
		
		for (Button btn : allSkillButtons) {
			btn.setDisabled(!room.getState().isSkillChangeAllowed());
			if (room.getState() == RoomState.PLAYER_WHEEL_FINISHED || room.getState() == RoomState.PLAYER_THROWING) {
				btn.setProgrammaticChangeEvents(false);
				btn.setChecked(false);
			} else {
				btn.setProgrammaticChangeEvents(true);
			}
		}
		
	}

	private boolean activateSkill(Button button, Entity player) {
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
	
	
	private void displayAmmos(Entity player) {
		AmmoCarrierComponent ammoCarrierComponent = Mappers.ammoCarrierComponent.get(player);
		
		if (ammoTable == null) {
			ammoTable = new Table();
			ammoTable.setPosition(PositionConstants.POS_ARROW_SPRITE.x, PositionConstants.POS_ARROW_SPRITE.y);
			
			// Arrows
			Table arrowTable = new Table();

			Image arrowImage = new Image(Assets.getTexture(Assets.arrow_item));
			arrowTable.add(arrowImage).uniformX();
			
			arrowLabel = new Label("", hudStyle);
			arrowTable.add(arrowLabel).uniformX().padLeft(-15);
			
			ammoTable.add(arrowTable).uniformX();
			
			// Bombs
			Table bombTable = new Table();
			Image bombImage = new Image(Assets.getTexture(Assets.bomb_item));
			bombTable.add(bombImage).uniformX();
			
			bombLabel = new Label("", hudStyle);
			bombTable.add(bombLabel).uniformX().padLeft(-15);
			
			ammoTable.add(bombTable).uniformX();
			

			ammoTable.pack();
			stage.addActor(ammoTable);
		}
		
		arrowLabel.setText(ammoCarrierComponent.getArrows() + "/" + ammoCarrierComponent.getMaxArrows());
		bombLabel.setText(ammoCarrierComponent.getBombs() + "/" + ammoCarrierComponent.getMaxBombs());
	}
	
}
