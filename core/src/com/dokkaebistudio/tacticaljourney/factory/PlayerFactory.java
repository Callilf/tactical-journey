/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.factory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.ParentRoomComponent;
import com.dokkaebistudio.tacticaljourney.components.SolidComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.display.TransformComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AmmoCarrierComponent;
import com.dokkaebistudio.tacticaljourney.components.player.ExperienceComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.components.player.WheelComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.skills.SkillEnum;

/**
 * Factory used to create presets of entities.
 * @author Callil
 *
 */
public final class PlayerFactory {
	
	/** The gdx pooled engine. */
	public PooledEngine engine;
	
	/** the entity factory. */
	public EntityFactory entityFactory;
	
	// textures are stored so we don't fetch them from the atlas each time (atlas.findRegion is SLOW)
	private TextureAtlas.AtlasRegion playerTexture;
	private TextureAtlas.AtlasRegion arrowsTexture;
	private TextureAtlas.AtlasRegion bombsTexture;


	/**
	 * Constructor.
	 * @param e the engine
	 */
	public PlayerFactory(PooledEngine e, EntityFactory ef) {
		this.engine = e;
		this.entityFactory = ef;
		
		playerTexture = Assets.getTexture(Assets.player);
		arrowsTexture = Assets.getTexture(Assets.arrow_item);
		bombsTexture = Assets.getTexture(Assets.bomb_item);

	}
	

	/**
	 * Create the player.
	 * @param pos the position
	 * @param moveSpeed the speed
	 * @return the player entity
	 */
	public Entity createPlayer(Vector2 pos, int moveSpeed, Room room) {
		Entity playerEntity = engine.createEntity();
		playerEntity.flags = EntityFlagEnum.PLAYER.getFlag();

		// Player sprite
		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		spriteCompo.setSprite(new Sprite(this.playerTexture));
		playerEntity.add(spriteCompo);
		
		// Grid position
		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		gridPosition.coord.set(pos);
		gridPosition.zIndex = 10;
		playerEntity.add(gridPosition);
		
		// Wheel
		WheelComponent baseWheelComponent = engine.createComponent(WheelComponent.class);
		baseWheelComponent.addSector(75, WheelComponent.Hit.HIT);
		baseWheelComponent.addSector(10, WheelComponent.Hit.MISS);
		baseWheelComponent.addSector(10, WheelComponent.Hit.CRITICAL);
		baseWheelComponent.addSector(10, WheelComponent.Hit.MISS);
		baseWheelComponent.addSector(75, WheelComponent.Hit.HIT);
		baseWheelComponent.addSector(20, WheelComponent.Hit.GRAZE);
		baseWheelComponent.addSector(140, WheelComponent.Hit.MISS);
		baseWheelComponent.addSector(20, WheelComponent.Hit.GRAZE);
		playerEntity.add(baseWheelComponent);
		
		
		// Player compo
		PlayerComponent playerComponent = engine.createComponent(PlayerComponent.class);
		playerComponent.engine = this.engine;
		playerComponent.setEndTurnButton(entityFactory.createEndTurnButton(new Vector2(0.0f, 0.0f)));
		playerComponent.setSkill1Button(entityFactory.createSkillButton(SkillEnum.SLASH, SkillEnum.SKILL_1_POSITION));
		playerComponent.setSkill2Button(entityFactory.createSkillButton(SkillEnum.BOW, SkillEnum.SKILL_2_POSITION));
		playerComponent.setActiveSkillIndicator( createSkillIndicator());
		playerEntity.add(playerComponent);
		
		// Move compo
		MoveComponent moveComponent = engine.createComponent(MoveComponent.class);
		moveComponent.engine = this.engine;
		moveComponent.moveSpeed = moveSpeed;
		playerEntity.add(moveComponent);
		
		// Attack compo
		AttackComponent attackComponent = engine.createComponent(AttackComponent.class);
		attackComponent.engine = this.engine;
		attackComponent.setRangeMax(1);
		attackComponent.setStrength(5);
		playerEntity.add(attackComponent);
		
		// Ammo carrier
		AmmoCarrierComponent ammoCarrierCompo = engine.createComponent(AmmoCarrierComponent.class);
		ammoCarrierCompo.setArrows(10);
		ammoCarrierCompo.setMaxArrows(10);
		ammoCarrierCompo.setBombs(0);
		ammoCarrierCompo.setMaxBombs(5);
		
		Vector3 arrowTextDisplayerPos = new Vector3();
		arrowTextDisplayerPos.set(1120,1040, 100);
		Entity arrowsNbText = entityFactory.createText(arrowTextDisplayerPos, "10/10", null);
		ammoCarrierCompo.setArrowsTextDisplayer(arrowsNbText);
		Vector3 arrowSpriteDisplayerPos = new Vector3();
		arrowSpriteDisplayerPos.set(1050,1000, 100);
		Entity arrowsSpriteDisplayer = entityFactory.createSprite(arrowSpriteDisplayerPos, arrowsTexture, EntityFlagEnum.ARROW_NB, null);
		ammoCarrierCompo.setArrowsSpriteDisplayer(arrowsSpriteDisplayer);
		
		Vector3 bombDisplayerPos = new Vector3();
		bombDisplayerPos.set(1320,1040, 100);
		Entity bombsNbText = entityFactory.createText(bombDisplayerPos, "0/5", null);
		ammoCarrierCompo.setBombsTextDisplayer(bombsNbText);
		Vector3 bombSpriteDisplayerPos = new Vector3();
		bombSpriteDisplayerPos.set(1250,1000, 100);
		Entity bombsSpriteDisplayer = entityFactory.createSprite(bombSpriteDisplayerPos, bombsTexture, EntityFlagEnum.BOMB_NB, null);
		ammoCarrierCompo.setBombsSpriteDisplayer(bombsSpriteDisplayer);

		
		playerEntity.add(ammoCarrierCompo);
		
		// Solid compo
		SolidComponent solidComponent = engine.createComponent(SolidComponent.class);
		playerEntity.add(solidComponent);
		
		// Health compo
		HealthComponent healthComponent = engine.createComponent(HealthComponent.class);
		healthComponent.engine = engine;
		healthComponent.setMaxHp(100);
		healthComponent.setHp(100);
		Entity hpText = entityFactory.createTextOnTile(pos, String.valueOf(healthComponent.getHp()), 100, null);
		healthComponent.setHpDisplayer(hpText);
		playerEntity.add(healthComponent);
		
		// Experience compo
		ExperienceComponent expCompo = engine.createComponent(ExperienceComponent.class);
		expCompo.init(engine);
		Entity levelDisp = entityFactory.createText(new Vector3(200, 100, 100), "Lvl: 1", null);
		expCompo.setLevelDisplayer(levelDisp);
		Entity expDisp = entityFactory.createText(new Vector3(200, 80, 100), "Exp: 0/10", null);
		expCompo.setExperienceDisplayer(expDisp);
		playerEntity.add(expCompo);

		
		//Parent room
		ParentRoomComponent parentRoomComponent = engine.createComponent(ParentRoomComponent.class);
		parentRoomComponent.setParentRoom(room);
		playerEntity.add(parentRoomComponent);
		
		
		//Skills
		entityFactory.createSkill(playerEntity, SkillEnum.SLASH, 1 );
		entityFactory.createSkill(playerEntity, SkillEnum.BOW, 2);

		
		engine.addEntity(playerEntity);
		return playerEntity;
	}

	/**
	 * Create the skill indicator, i.e. the sprite that shows which skill is currently active.
	 * @return the entity representing the skill indicator.
	 */
	private Entity createSkillIndicator() {
		Entity indicator = engine.createEntity();
		
		SpriteComponent indicatorSpriteCompo = engine.createComponent(SpriteComponent.class);
		indicatorSpriteCompo.setSprite(new Sprite(Assets.getTexture(Assets.btn_skill_active)));
		indicator.add(indicatorSpriteCompo);
		
		TransformComponent indicatorTransfoCompo = engine.createComponent(TransformComponent.class);
		indicatorTransfoCompo.pos.set(-100, -100, 11);
		indicator.add(indicatorTransfoCompo);
		
		engine.addEntity(indicator);
		return indicator;
	}
	
	
}
