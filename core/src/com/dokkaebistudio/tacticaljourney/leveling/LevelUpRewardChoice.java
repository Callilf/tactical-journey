package com.dokkaebistudio.tacticaljourney.leveling;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.display.TransformComponent;
import com.dokkaebistudio.tacticaljourney.factory.EntityFactory;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class LevelUpRewardChoice {
	
	private LevelUpRewardEnum levelUpRewardEnum;

	private Entity rewardPanel;
	private Entity rewardText;
	private Entity descriptionPanel;
	private Entity descriptionText;
	private Entity frame;
	private Entity claimBtn;
	
	private PooledEngine engine;
	
	private int offset;

	public LevelUpRewardChoice(Vector2 pos, LevelUpRewardEnum levelUpRewardEnum, int choiceNumber, PooledEngine engine, EntityFactory factory) {
		this.levelUpRewardEnum = levelUpRewardEnum;
		this.engine = engine;
		
		frame = engine.createEntity();
		
		SpriteComponent frameSprite = engine.createComponent(SpriteComponent.class);
		frameSprite.setSprite(new Sprite(Assets.getTexture(Assets.lvl_up_choice_frame)));
		frame.add(frameSprite);
		
		TransformComponent framePos = engine.createComponent(TransformComponent.class);
		framePos.pos.set(pos, 500);
		frame.add(framePos);
		
		engine.addEntity(frame);
		
		
		descriptionPanel = engine.createEntity();
		
		SpriteComponent descPanelSprite = engine.createComponent(SpriteComponent.class);
		descPanelSprite.setSprite(new Sprite(Assets.getTexture(Assets.lvl_up_choice_desc_panel)));
		descriptionPanel.add(descPanelSprite);
		
		TransformComponent descPanelPos = engine.createComponent(TransformComponent.class);
		descPanelPos.pos.set(pos, 498 - choiceNumber);
		descriptionPanel.add(descPanelPos);
		
		engine.addEntity(descriptionPanel);
		
				
		descriptionText = factory.createText(new Vector3(pos.x + 30, pos.y + 65, 499 - choiceNumber), levelUpRewardEnum.getDescription(), Assets.smallFont, null);

		
		
		rewardPanel = engine.createEntity();
		
		SpriteComponent rewardPanelSprite = engine.createComponent(SpriteComponent.class);
		rewardPanelSprite.setSprite(new Sprite(Assets.getTexture(Assets.lvl_up_choice_reward_panel)));
		rewardPanel.add(rewardPanelSprite);
		
		TransformComponent rewardPanelPos = engine.createComponent(TransformComponent.class);
		rewardPanelPos.pos.set(pos, 496 - choiceNumber);
		rewardPanel.add(rewardPanelPos);
		
		engine.addEntity(rewardPanel);
		
		rewardText = factory.createText(new Vector3(pos.x + 30, pos.y + 65, 497 - choiceNumber), levelUpRewardEnum.getFinalDescription(), Assets.smallFont, null);
		
	}
	
	
	public boolean containsPoint(int x, int y) {
		SpriteComponent spriteComponent = Mappers.spriteComponent.get(frame);
		return spriteComponent.containsPoint(x, y);
	}
	
	public void clear() {
		engine.removeEntity(rewardPanel);
		engine.removeEntity(rewardText);
		engine.removeEntity(descriptionPanel);
		engine.removeEntity(descriptionText);
		engine.removeEntity(frame);
		if (claimBtn != null) {
			engine.removeEntity(claimBtn);
		}
	}
	
	public boolean openUpChoice() {
		TransformComponent transformComponent = Mappers.transfoComponent.get(descriptionPanel);
		transformComponent.pos.y += 2;
		
		TransformComponent textTransformComponent = Mappers.transfoComponent.get(descriptionText);
		textTransformComponent.pos.y += 2;
		
		offset += 2;
		
		SpriteComponent spriteComponent = Mappers.spriteComponent.get(frame);
		if (offset >= spriteComponent.getSprite().getHeight()) {
			return true;
		}

		return false;
	}
	
	

	// Getters and Setters

	public Entity getRewardPanel() {
		return rewardPanel;
	}

	public void setRewardPanel(Entity rewardPanel) {
		this.rewardPanel = rewardPanel;
	}

	public Entity getDescriptionPanel() {
		return descriptionPanel;
	}

	public void setDescriptionPanel(Entity descriptionPanel) {
		this.descriptionPanel = descriptionPanel;
	}

	public Entity getFrame() {
		return frame;
	}

	public void setFrame(Entity frame) {
		this.frame = frame;
	}

	public Entity getClaimBtn() {
		return claimBtn;
	}

	public void setClaimBtn(Entity claimBtn) {
		this.claimBtn = claimBtn;
	}

	public Entity getRewardText() {
		return rewardText;
	}

	public void setRewardText(Entity rewardText) {
		this.rewardText = rewardText;
	}

	public Entity getDescriptionText() {
		return descriptionText;
	}

	public void setDescriptionText(Entity descriptionText) {
		this.descriptionText = descriptionText;
	}


	public LevelUpRewardEnum getLevelUpRewardEnum() {
		return levelUpRewardEnum;
	}


	public void setLevelUpRewardEnum(LevelUpRewardEnum levelUpRewardEnum) {
		this.levelUpRewardEnum = levelUpRewardEnum;
	}
	

}
