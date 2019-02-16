/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.factory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.ai.movements.AttackTypeEnum;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.components.ExpRewardComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.SolidComponent;
import com.dokkaebistudio.tacticaljourney.components.creep.CreepEmitterComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.loot.DropRate;
import com.dokkaebistudio.tacticaljourney.components.loot.DropRate.ItemPoolRarity;
import com.dokkaebistudio.tacticaljourney.components.loot.LootRewardComponent;
import com.dokkaebistudio.tacticaljourney.constants.ZIndexConstants;
import com.dokkaebistudio.tacticaljourney.creeps.Creep.CreepType;
import com.dokkaebistudio.tacticaljourney.enums.enemy.EnemyFactionEnum;
import com.dokkaebistudio.tacticaljourney.enums.enemy.EnemyMoveStrategy;
import com.dokkaebistudio.tacticaljourney.room.Room;

/**
 * Factory used to create presets of entities.
 * @author Callil
 *
 */
public final class EnemyFactory {
	
	/** The gdx pooled engine. */
	public PooledEngine engine;
	
	/** the entity factory. */
	public EntityFactory entityFactory;
	
	/**
	 * Constructor.
	 * @param e the engine
	 */
	public EnemyFactory(PooledEngine e, EntityFactory ef) {
		this.engine = e;
		this.entityFactory = ef;
	}
	

	/**
	 * Create an enemy.
	 * @param pos the position
	 * @param moveSpeed the speed
	 * @return the enemy entity
	 */
	public Entity createSpider(Room room, Vector2 pos, int speed) {
		Entity enemyEntity = engine.createEntity();
		enemyEntity.flags = EntityFlagEnum.ENEMY_SPIDER.getFlag();

		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		spriteCompo.setSprite(new Sprite(Assets.enemy_spider));
		enemyEntity.add(spriteCompo);

		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		gridPosition.coord(enemyEntity, pos, room);
		gridPosition.zIndex = ZIndexConstants.ENEMY;
		enemyEntity.add(gridPosition);
		
		EnemyComponent enemyComponent = engine.createComponent(EnemyComponent.class);
		enemyComponent.room = room;
		enemyComponent.setFaction(EnemyFactionEnum.SPIDERS);
		enemyComponent.setBasicMoveStrategy(EnemyMoveStrategy.MOVE_RANDOMLY_BUT_ATTACK_IF_POSSIBLE);
		enemyComponent.setAlertedMoveStrategy(EnemyMoveStrategy.MOVE_TOWARD_PLAYER);
		Entity alertedDisplayer = this.entityFactory.createTextOnTile(pos, "", ZIndexConstants.HEALTH_DISPLAYER, room);
		enemyComponent.setAlertedDisplayer(alertedDisplayer);
		enemyEntity.add(enemyComponent);
		
		MoveComponent moveComponent = engine.createComponent(MoveComponent.class);
		moveComponent.room = room;
		moveComponent.moveSpeed = speed;
		enemyEntity.add(moveComponent);
		
		AttackComponent attackComponent = engine.createComponent(AttackComponent.class);
		attackComponent.room = room;
		attackComponent.setAttackType(AttackTypeEnum.MELEE);
		attackComponent.setRangeMax(1);
		attackComponent.setStrength(5);
		enemyEntity.add(attackComponent);
		
		SolidComponent solidComponent = engine.createComponent(SolidComponent.class);
		enemyEntity.add(solidComponent);
		
		HealthComponent healthComponent = engine.createComponent(HealthComponent.class);
		healthComponent.room = room;
		healthComponent.setMaxHp(10);
		healthComponent.setHp(10);
		Entity hpEntity = this.entityFactory.createTextOnTile(pos, String.valueOf(healthComponent.getHp()), ZIndexConstants.HEALTH_DISPLAYER, room);
		healthComponent.setHpDisplayer(hpEntity);
		enemyEntity.add(healthComponent);
		
		ExpRewardComponent expRewardCompo = engine.createComponent(ExpRewardComponent.class);
		expRewardCompo.setExpGain(2);
		enemyEntity.add(expRewardCompo);
		
		LootRewardComponent lootRewardCompo = engine.createComponent(LootRewardComponent.class);
		DropRate dropRate = new DropRate();
		dropRate.add(ItemPoolRarity.COMMON, 50);
		dropRate.add(ItemPoolRarity.RARE, 10);
		lootRewardCompo.setDropRate(dropRate);
		enemyEntity.add(lootRewardCompo);
		
		room.addEnemy(enemyEntity);
		
		return enemyEntity;
	}
	
	
	/**
	 * Create a spider web that can spit web on the player to alert other spiders
	 * and leaves web creep on the floor when moving.
	 * @param pos the position
	 * @param moveSpeed the speed
	 * @return the enemy entity
	 */
	public Entity createSpiderWeb(Room room, Vector2 pos, int speed) {
		Entity enemyEntity = engine.createEntity();
		enemyEntity.flags = EntityFlagEnum.ENEMY_SPIDER_WEB.getFlag();

		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		spriteCompo.setSprite(new Sprite(Assets.enemy_spider_web));
		enemyEntity.add(spriteCompo);

		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		gridPosition.coord(enemyEntity, pos, room);
		gridPosition.zIndex = ZIndexConstants.ENEMY;
		enemyEntity.add(gridPosition);
		
		EnemyComponent enemyComponent = engine.createComponent(EnemyComponent.class);
		enemyComponent.room = room;
		enemyComponent.setFaction(EnemyFactionEnum.SPIDERS);
		enemyComponent.setBasicMoveStrategy(EnemyMoveStrategy.MOVE_RANDOMLY_BUT_ATTACK_FROM_RANGE_IF_POSSIBLE);
		enemyComponent.setAlertedMoveStrategy(EnemyMoveStrategy.MOVE_RANDOMLY_BUT_ATTACK_FROM_RANGE_IF_POSSIBLE);
		Entity alertedDisplayer = this.entityFactory.createTextOnTile(pos, "", ZIndexConstants.HEALTH_DISPLAYER, room);
		enemyComponent.setAlertedDisplayer(alertedDisplayer);
		enemyEntity.add(enemyComponent);
		
		MoveComponent moveComponent = engine.createComponent(MoveComponent.class);
		moveComponent.room = room;
		moveComponent.moveSpeed = speed;
		enemyEntity.add(moveComponent);
		
		AttackComponent attackComponent = engine.createComponent(AttackComponent.class);
		attackComponent.room = room;
		attackComponent.setAttackType(AttackTypeEnum.RANGE);
		attackComponent.setRangeMin(2);
		attackComponent.setRangeMax(3);
		attackComponent.setStrength(3);
		enemyEntity.add(attackComponent);
		
		SolidComponent solidComponent = engine.createComponent(SolidComponent.class);
		enemyEntity.add(solidComponent);
		
		HealthComponent healthComponent = engine.createComponent(HealthComponent.class);
		healthComponent.room = room;
		healthComponent.setMaxHp(5);
		healthComponent.setHp(5);
		Entity hpEntity = this.entityFactory.createTextOnTile(pos, String.valueOf(healthComponent.getHp()), ZIndexConstants.HEALTH_DISPLAYER, room);
		healthComponent.setHpDisplayer(hpEntity);
		enemyEntity.add(healthComponent);
		
		ExpRewardComponent expRewardCompo = engine.createComponent(ExpRewardComponent.class);
		expRewardCompo.setExpGain(4);
		enemyEntity.add(expRewardCompo);
		
		LootRewardComponent lootRewardCompo = engine.createComponent(LootRewardComponent.class);
		DropRate dropRate = new DropRate();
		dropRate.add(ItemPoolRarity.COMMON, 70);
		dropRate.add(ItemPoolRarity.RARE, 10);
		lootRewardCompo.setDropRate(dropRate);
		enemyEntity.add(lootRewardCompo);
		
		CreepEmitterComponent creepEmitter = engine.createComponent(CreepEmitterComponent.class);
		creepEmitter.setType(CreepType.WEB);
		enemyEntity.add(creepEmitter);
		
		room.addEnemy(enemyEntity);
		
		return enemyEntity;
	}
	
	
	/**
	 * Create a scorpion.
	 * @param pos the position
	 * @param moveSpeed the speed
	 * @return the enemy entity
	 */
	public Entity createScorpion(Room room, Vector2 pos, int speed) {
		Entity enemyEntity = engine.createEntity();
		enemyEntity.flags = EntityFlagEnum.ENEMY_SCORPION.getFlag();

		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		spriteCompo.setSprite(new Sprite(Assets.enemy_scorpion));
		enemyEntity.add(spriteCompo);

		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		gridPosition.coord(enemyEntity, pos, room);
		gridPosition.zIndex = ZIndexConstants.ENEMY;
		enemyEntity.add(gridPosition);
		
		EnemyComponent enemyComponent = engine.createComponent(EnemyComponent.class);
		enemyComponent.room = room;
		enemyComponent.setFaction(EnemyFactionEnum.SOLITARY);
		enemyComponent.setBasicMoveStrategy(EnemyMoveStrategy.MOVE_TOWARD_PLAYER);
		enemyComponent.setAlertedMoveStrategy(EnemyMoveStrategy.MOVE_TOWARD_PLAYER);
		Entity alertedDisplayer = this.entityFactory.createTextOnTile(pos, "", ZIndexConstants.HEALTH_DISPLAYER, room);
		enemyComponent.setAlertedDisplayer(alertedDisplayer);
		enemyEntity.add(enemyComponent);
		
		MoveComponent moveComponent = engine.createComponent(MoveComponent.class);
		moveComponent.room = room;
		moveComponent.moveSpeed = speed;
		enemyEntity.add(moveComponent);
		
		AttackComponent attackComponent = engine.createComponent(AttackComponent.class);
		attackComponent.room = room;
		attackComponent.setAttackType(AttackTypeEnum.MELEE);
		attackComponent.setRangeMax(1);
		attackComponent.setStrength(10);
		enemyEntity.add(attackComponent);
		
		SolidComponent solidComponent = engine.createComponent(SolidComponent.class);
		enemyEntity.add(solidComponent);
		
		HealthComponent healthComponent = engine.createComponent(HealthComponent.class);
		healthComponent.room = room;
		healthComponent.setMaxHp(15);
		healthComponent.setHp(15);
		healthComponent.setHpDisplayer(this.entityFactory.createTextOnTile(pos, String.valueOf(healthComponent.getHp()), ZIndexConstants.HEALTH_DISPLAYER, room));
		enemyEntity.add(healthComponent);
		
		ExpRewardComponent expRewardCompo = engine.createComponent(ExpRewardComponent.class);
		expRewardCompo.setExpGain(6);
		enemyEntity.add(expRewardCompo);
		
		LootRewardComponent lootRewardCompo = engine.createComponent(LootRewardComponent.class);
		DropRate dropRate = new DropRate();
		dropRate.add(ItemPoolRarity.COMMON, 50);
		dropRate.add(ItemPoolRarity.RARE, 30);
		lootRewardCompo.setDropRate(dropRate);
		enemyEntity.add(lootRewardCompo);
		
		room.addEnemy(enemyEntity);
		
		return enemyEntity;
	}
	
}
