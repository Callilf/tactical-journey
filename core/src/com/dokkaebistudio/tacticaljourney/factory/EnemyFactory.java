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
import com.dokkaebistudio.tacticaljourney.components.FlyComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.SolidComponent;
import com.dokkaebistudio.tacticaljourney.components.creep.CreepEmitterComponent;
import com.dokkaebistudio.tacticaljourney.components.display.AnimationComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.display.StateComponent;
import com.dokkaebistudio.tacticaljourney.components.loot.DropRate;
import com.dokkaebistudio.tacticaljourney.components.loot.DropRate.ItemPoolRarity;
import com.dokkaebistudio.tacticaljourney.components.loot.LootRewardComponent;
import com.dokkaebistudio.tacticaljourney.constants.ZIndexConstants;
import com.dokkaebistudio.tacticaljourney.creeps.Creep.CreepType;
import com.dokkaebistudio.tacticaljourney.enemies.enums.EnemyFactionEnum;
import com.dokkaebistudio.tacticaljourney.enemies.enums.EnemyMoveStrategy;
import com.dokkaebistudio.tacticaljourney.enemies.enums.EnemyTypeEnum;
import com.dokkaebistudio.tacticaljourney.enums.AnimationsEnum;
import com.dokkaebistudio.tacticaljourney.enums.StatesEnum;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.ScorpionItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.SpiderItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.WebSpiderItemPool;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.systems.enemies.StingerSubSystem;

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
		enemyComponent.setType(EnemyTypeEnum.SPIDER);
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
		lootRewardCompo.setItemPool(new SpiderItemPool());
		DropRate dropRate = new DropRate();
		dropRate.add(ItemPoolRarity.COMMON, 30);
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
		enemyComponent.setType(EnemyTypeEnum.WEB_SPIDER);
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
		lootRewardCompo.setItemPool(new WebSpiderItemPool());
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
		enemyComponent.setType(EnemyTypeEnum.SCORPION);
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
		lootRewardCompo.setItemPool(new ScorpionItemPool());
		DropRate dropRate = new DropRate();
		dropRate.add(ItemPoolRarity.COMMON, 50);
		dropRate.add(ItemPoolRarity.RARE, 20);
		lootRewardCompo.setDropRate(dropRate);
		enemyEntity.add(lootRewardCompo);
		
		room.addEnemy(enemyEntity);
		
		return enemyEntity;
	}
	
	/**
	 * Create a stinger.
	 * @param pos the position
	 * @param moveSpeed the speed
	 * @return the enemy entity
	 */
	public Entity createStinger(Room room, Vector2 pos, int speed) {
		Entity enemyEntity = engine.createEntity();
		enemyEntity.flags = EntityFlagEnum.ENEMY_STINGER.getFlag();
		
		
		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		enemyEntity.add(spriteCompo);

		AnimationComponent animationCompo = engine.createComponent(AnimationComponent.class);
		animationCompo.animations.put(StatesEnum.STINGER_FLY.getState(), AnimationsEnum.STINGER_FLY.getAnimation());
		animationCompo.animations.put(StatesEnum.STINGER_ATTACK.getState(), AnimationsEnum.STINGER_ATTACK.getAnimation());
		enemyEntity.add(animationCompo);
		
		StateComponent stateCompo = engine.createComponent(StateComponent.class);
		stateCompo.set(StatesEnum.STINGER_FLY.getState() );
		enemyEntity.add(stateCompo);

		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		gridPosition.coord(enemyEntity, pos, room);
		gridPosition.zIndex = ZIndexConstants.ENEMY;
		enemyEntity.add(gridPosition);
		
		EnemyComponent enemyComponent = engine.createComponent(EnemyComponent.class);
		enemyComponent.room = room;
		enemyComponent.setType(EnemyTypeEnum.STINGER);
		enemyComponent.setSubSystem(new StingerSubSystem());
		enemyComponent.setFaction(EnemyFactionEnum.SOLITARY);
		enemyComponent.setBasicMoveStrategy(EnemyMoveStrategy.MOVE_RANDOMLY_BUT_ATTACK_IF_POSSIBLE);
		enemyComponent.setAlertedMoveStrategy(EnemyMoveStrategy.MOVE_TOWARD_PLAYER);
		Entity alertedDisplayer = this.entityFactory.createTextOnTile(pos, "", ZIndexConstants.HEALTH_DISPLAYER, room);
		enemyComponent.setAlertedDisplayer(alertedDisplayer);
		enemyEntity.add(enemyComponent);
		
		MoveComponent moveComponent = engine.createComponent(MoveComponent.class);
		moveComponent.room = room;
		moveComponent.moveSpeed = speed;
		enemyEntity.add(moveComponent);
		
		FlyComponent flyComponent = engine.createComponent(FlyComponent.class);
		enemyEntity.add(flyComponent);
		
		AttackComponent attackComponent = engine.createComponent(AttackComponent.class);
		attackComponent.room = room;
		attackComponent.setAttackType(AttackTypeEnum.MELEE);
		attackComponent.setRangeMax(1);
		attackComponent.setStrength(6);
		enemyEntity.add(attackComponent);
		
		SolidComponent solidComponent = engine.createComponent(SolidComponent.class);
		enemyEntity.add(solidComponent);
		
		HealthComponent healthComponent = engine.createComponent(HealthComponent.class);
		healthComponent.room = room;
		healthComponent.setMaxHp(8);
		healthComponent.setHp(8);
		healthComponent.setHpDisplayer(this.entityFactory.createTextOnTile(pos, String.valueOf(healthComponent.getHp()), ZIndexConstants.HEALTH_DISPLAYER, room));
		enemyEntity.add(healthComponent);
		
		ExpRewardComponent expRewardCompo = engine.createComponent(ExpRewardComponent.class);
		expRewardCompo.setExpGain(4);
		enemyEntity.add(expRewardCompo);
		
		LootRewardComponent lootRewardCompo = engine.createComponent(LootRewardComponent.class);
		lootRewardCompo.setItemPool(new ScorpionItemPool());
		DropRate dropRate = new DropRate();
		dropRate.add(ItemPoolRarity.COMMON, 100 );
		dropRate.add(ItemPoolRarity.RARE, 20);
		lootRewardCompo.setDropRate(dropRate);
		enemyEntity.add(lootRewardCompo);
		
		room.addEnemy(enemyEntity);
		
		return enemyEntity;
	}
	
}
