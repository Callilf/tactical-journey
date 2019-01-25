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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.player.ExperienceComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class HudSystem extends IteratingSystem implements RoomSystem {

	public Stage stage;

	/** The current room. */
	private Room room;
	
	
	// Health and Experience
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

	public HudSystem(Room r, Stage s) {
		super(Family.all(PlayerComponent.class).get());
		room = r;
		this.stage = s;
	}

	@Override
	public void enterRoom(Room newRoom) {
		this.room = newRoom;
	}

	@Override
	protected void processEntity(Entity player, float deltaTime) {
		PlayerComponent playerComponent = Mappers.playerComponent.get(player);

		displayHealthAndXp(player);
		displaySkillButtons(player);

		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();

	}

	private void displayHealthAndXp(Entity player) {
		HealthComponent healthComponent = Mappers.healthComponent.get(player);
		ExperienceComponent experienceComponent = Mappers.experienceComponent.get(player);

		if (healthAndXptable == null) {
			healthAndXptable = new Table();
			healthAndXptable.setPosition(200, 30);
	
			LabelStyle hudStyle = new LabelStyle(Assets.font, Color.WHITE);
	
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
								rangeSkillButton.setChecked(false);
	
								activateSkill(meleeSkillButton, player);
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
								meleeSkillButton.setChecked(false);
		
								activateSkill(rangeSkillButton, player);
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
		
		
		for (Button btn : allSkillButtons) {
			btn.setDisabled(!room.getState().isSkillChangeAllowed());
			if (room.getState() == RoomState.PLAYER_WHEEL_FINISHED) {
				btn.setProgrammaticChangeEvents(false);
				btn.setChecked(false);
			} else {
				btn.setProgrammaticChangeEvents(true);
			}
		}
		
	}

	private void activateSkill(Button button, Entity player) {
		room.setNextState(RoomState.PLAYER_TARGETING_START);

		PlayerComponent playerComponent = Mappers.playerComponent.get(player);
		if (button == meleeSkillButton) {
			playerComponent.setActiveSkill(playerComponent.getSkillMelee());
		} else if (button == rangeSkillButton) {
			playerComponent.setActiveSkill(playerComponent.getSkillRange());
		} else if (button == bombSkillButton) {
			playerComponent.setActiveSkill(playerComponent.getSkillRange());
		}
	}

	private void deactivateSkill(Entity player) {
		room.setNextState(RoomState.PLAYER_TARGETING_STOP);
	}

}
