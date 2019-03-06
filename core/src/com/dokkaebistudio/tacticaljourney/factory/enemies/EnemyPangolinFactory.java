/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.factory.enemies;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.ai.movements.AttackTypeEnum;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.components.ExpRewardComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.SolidComponent;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.display.AnimationComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.display.StateComponent;
import com.dokkaebistudio.tacticaljourney.components.loot.DropRate;
import com.dokkaebistudio.tacticaljourney.components.loot.DropRate.ItemPoolRarity;
import com.dokkaebistudio.tacticaljourney.components.loot.LootRewardComponent;
import com.dokkaebistudio.tacticaljourney.constants.ZIndexConstants;
import com.dokkaebistudio.tacticaljourney.enemies.enums.EnemyFactionEnum;
import com.dokkaebistudio.tacticaljourney.enemies.enums.EnemyMoveStrategy;
import com.dokkaebistudio.tacticaljourney.enemies.pangolins.EnemyPangolinBaby;
import com.dokkaebistudio.tacticaljourney.enemies.pangolins.EnemyPangolinMother;
import com.dokkaebistudio.tacticaljourney.enums.AnimationsEnum;
import com.dokkaebistudio.tacticaljourney.enums.DamageType;
import com.dokkaebistudio.tacticaljourney.enums.StatesEnum;
import com.dokkaebistudio.tacticaljourney.factory.EnemyFactory;
import com.dokkaebistudio.tacticaljourney.factory.EntityFlagEnum;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.StingerItemPool;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.systems.enemies.pangolins.PangolinBabySubSystem;
import com.dokkaebistudio.tacticaljourney.systems.enemies.pangolins.PangolinMotherSubSystem;

/**
 * Factory used to create presets of entities.
 * @author Callil
 *
 */
public final class EnemyPangolinFactory {
	
	/** The gdx pooled engine. */
	public PooledEngine engine;
	
	/** the entity factory. */
	public EnemyFactory enemyFactory;
	
	/**
	 * Constructor.
	 * @param e the engine
	 */
	public EnemyPangolinFactory(PooledEngine e, EnemyFactory ef) {
		this.engine = e;
		this.enemyFactory = ef;
	}
	

	
	/**
	 * Create a baby pangolin.
	 * @param pos the position
	 * @return the enemy entity
	 */
	public Entity createPangolinBaby(Room room, Vector2 pos, Entity mother) {
		Entity enemyEntity = engine.createEntity();
		enemyEntity.flags = EntityFlagEnum.ENEMY_PANGOLIN_BABY.getFlag();
		
		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		enemyEntity.add(spriteCompo);

		AnimationComponent animationCompo = engine.createComponent(AnimationComponent.class);
		animationCompo.animations.put(StatesEnum.PANGOLIN_BABY_STAND.getState(), AnimationsEnum.PANGOLIN_BABY_STAND.getAnimation());
		animationCompo.animations.put(StatesEnum.PANGOLIN_BABY_ROLLED.getState(), AnimationsEnum.PANGOLIN_BABY_ROLLED.getAnimation());
		animationCompo.animations.put(StatesEnum.PANGOLIN_BABY_ROLLING.getState(), AnimationsEnum.PANGOLIN_BABY_ROLLING.getAnimation());
		enemyEntity.add(animationCompo);
		
		StateComponent stateCompo = engine.createComponent(StateComponent.class);
		stateCompo.set(StatesEnum.PANGOLIN_BABY_STAND.getState() );
		enemyEntity.add(stateCompo);

		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		gridPosition.coord(enemyEntity, pos, room);
		gridPosition.zIndex = ZIndexConstants.ENEMY;
		enemyEntity.add(gridPosition);
		
		EnemyComponent enemyComponent = engine.createComponent(EnemyComponent.class);
		enemyComponent.room = room;
		enemyComponent.setType(new EnemyPangolinBaby(mother));
		enemyComponent.setSubSystem(new PangolinBabySubSystem());
		enemyComponent.setFaction(EnemyFactionEnum.PANGOLINS);
		enemyComponent.setBasicMoveStrategy(EnemyMoveStrategy.MOVE_RANDOMLY);
		enemyComponent.setAlertedMoveStrategy(EnemyMoveStrategy.MOVE_TOWARD_PLAYER);
		Entity alertedDisplayer = this.enemyFactory.entityFactory.createTextOnTile(pos, "", ZIndexConstants.HEALTH_DISPLAYER, room);
		enemyComponent.setAlertedDisplayer(alertedDisplayer);
		enemyEntity.add(enemyComponent);
		
		MoveComponent moveComponent = engine.createComponent(MoveComponent.class);
		moveComponent.room = room;
		moveComponent.moveSpeed = 2;
		enemyEntity.add(moveComponent);
				
		AttackComponent attackComponent = engine.createComponent(AttackComponent.class);
		attackComponent.room = room;
		attackComponent.setAttackType(AttackTypeEnum.MELEE);
		attackComponent.setRangeMax(1);
		attackComponent.setStrength(9);
		enemyEntity.add(attackComponent);
		
		SolidComponent solidComponent = engine.createComponent(SolidComponent.class);
		enemyEntity.add(solidComponent);
		
		HealthComponent healthComponent = engine.createComponent(HealthComponent.class);
		healthComponent.room = room;
		healthComponent.addResistance(DamageType.EXPLOSION, 75);
		healthComponent.setMaxHp(12);
		healthComponent.setHp(12);
		healthComponent.setMaxArmor(20);
		healthComponent.setHpDisplayer(this.enemyFactory.entityFactory.createTextOnTile(pos, String.valueOf(healthComponent.getHp()), ZIndexConstants.HEALTH_DISPLAYER, room));
		enemyEntity.add(healthComponent);
		
		ExpRewardComponent expRewardCompo = engine.createComponent(ExpRewardComponent.class);
		expRewardCompo.setExpGain(5);
		enemyEntity.add(expRewardCompo);
		
		LootRewardComponent lootRewardCompo = engine.createComponent(LootRewardComponent.class);
		lootRewardCompo.setItemPool(new StingerItemPool());
		DropRate dropRate = new DropRate();
		dropRate.add(ItemPoolRarity.COMMON, 30 );
		dropRate.add(ItemPoolRarity.RARE, 20);
		lootRewardCompo.setDropRate(dropRate);
		enemyEntity.add(lootRewardCompo);
		
		StatusReceiverComponent statusReceiverCompo = engine.createComponent(StatusReceiverComponent.class);
		enemyEntity.add(statusReceiverCompo);
		
		room.addEnemy(enemyEntity);
		
		return enemyEntity;
	}
	
	
	/**
	 * Create the mother pangolin.
	 * @param pos the position
	 * @return the enemy entity
	 */
	public Entity createPangolinMother(Room room, Vector2 pos) {
		Entity enemyEntity = engine.createEntity();
		enemyEntity.flags = EntityFlagEnum.ENEMY_PANGOLIN_MOTHER.getFlag();
		
		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		enemyEntity.add(spriteCompo);

		AnimationComponent animationCompo = engine.createComponent(AnimationComponent.class);
		animationCompo.animations.put(StatesEnum.PANGOLIN_MOTHER_STAND.getState(), AnimationsEnum.PANGOLIN_MOTHER_STAND.getAnimation());
		animationCompo.animations.put(StatesEnum.PANGOLIN_MOTHER_ENRAGED_STAND.getState(), AnimationsEnum.PANGOLIN_MOTHER_ENRAGED_STAND.getAnimation());
		animationCompo.animations.put(StatesEnum.PANGOLIN_MOTHER_CRYING.getState(), AnimationsEnum.PANGOLIN_MOTHER_CRYING.getAnimation());
		enemyEntity.add(animationCompo);
		
		StateComponent stateCompo = engine.createComponent(StateComponent.class);
		stateCompo.set(StatesEnum.PANGOLIN_MOTHER_STAND.getState() );
		enemyEntity.add(stateCompo);

		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		gridPosition.coord(enemyEntity, pos, room);
		gridPosition.zIndex = ZIndexConstants.ENEMY;
		enemyEntity.add(gridPosition);
		
		EnemyComponent enemyComponent = engine.createComponent(EnemyComponent.class);
		enemyComponent.room = room;
		enemyComponent.setType(new EnemyPangolinMother());
		enemyComponent.setSubSystem(new PangolinMotherSubSystem());
		enemyComponent.setFaction(EnemyFactionEnum.PANGOLINS);
		enemyComponent.setBasicMoveStrategy(EnemyMoveStrategy.MOVE_RANDOMLY_BUT_ATTACK_IF_POSSIBLE);
		enemyComponent.setAlertedMoveStrategy(EnemyMoveStrategy.MOVE_TOWARD_PLAYER);
		Entity alertedDisplayer = this.enemyFactory.entityFactory.createTextOnTile(pos, "", ZIndexConstants.HEALTH_DISPLAYER, room);
		enemyComponent.setAlertedDisplayer(alertedDisplayer);
		enemyEntity.add(enemyComponent);
		
		MoveComponent moveComponent = engine.createComponent(MoveComponent.class);
		moveComponent.room = room;
		moveComponent.moveSpeed = 3;
		enemyEntity.add(moveComponent);
				
		AttackComponent attackComponent = engine.createComponent(AttackComponent.class);
		attackComponent.room = room;
		attackComponent.setAttackType(AttackTypeEnum.MELEE);
		attackComponent.setRangeMax(1);
		attackComponent.setStrength(12);
		enemyEntity.add(attackComponent);
		
		SolidComponent solidComponent = engine.createComponent(SolidComponent.class);
		enemyEntity.add(solidComponent);
		
		HealthComponent healthComponent = engine.createComponent(HealthComponent.class);
		healthComponent.room = room;
		healthComponent.addResistance(DamageType.EXPLOSION, 75);
		healthComponent.setHpDisplayer(this.enemyFactory.entityFactory.createTextOnTile(pos, String.valueOf(healthComponent.getHp()), ZIndexConstants.HEALTH_DISPLAYER, room));
		healthComponent.setMaxHp(20);
		healthComponent.setHp(20);
		healthComponent.setMaxArmor(50);
		healthComponent.setArmor(50);
		enemyEntity.add(healthComponent);
		
		ExpRewardComponent expRewardCompo = engine.createComponent(ExpRewardComponent.class);
		expRewardCompo.setExpGain(50);
		enemyEntity.add(expRewardCompo);
		
		LootRewardComponent lootRewardCompo = engine.createComponent(LootRewardComponent.class);
		lootRewardCompo.setItemPool(new StingerItemPool());
		DropRate dropRate = new DropRate();
		dropRate.add(ItemPoolRarity.COMMON, 30 );
		dropRate.add(ItemPoolRarity.RARE, 20);
		lootRewardCompo.setDropRate(dropRate);
		enemyEntity.add(lootRewardCompo);
		
		StatusReceiverComponent statusReceiverCompo = engine.createComponent(StatusReceiverComponent.class);
		enemyEntity.add(statusReceiverCompo);
		
		room.addEnemy(enemyEntity);
		
		return enemyEntity;
	}
}