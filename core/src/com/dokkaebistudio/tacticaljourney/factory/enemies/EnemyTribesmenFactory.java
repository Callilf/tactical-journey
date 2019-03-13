/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.factory.enemies;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.ai.movements.AttackTypeEnum;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.components.ExpRewardComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.SolidComponent;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.creep.CreepEmitterComponent;
import com.dokkaebistudio.tacticaljourney.components.display.AnimationComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.display.StateComponent;
import com.dokkaebistudio.tacticaljourney.components.loot.DropRate;
import com.dokkaebistudio.tacticaljourney.components.loot.DropRate.ItemPoolRarity;
import com.dokkaebistudio.tacticaljourney.components.orbs.OrbCarrierComponent;
import com.dokkaebistudio.tacticaljourney.components.loot.LootRewardComponent;
import com.dokkaebistudio.tacticaljourney.constants.ZIndexConstants;
import com.dokkaebistudio.tacticaljourney.creeps.Creep.CreepType;
import com.dokkaebistudio.tacticaljourney.enemies.enums.EnemyFactionEnum;
import com.dokkaebistudio.tacticaljourney.enemies.enums.EnemyMoveStrategy;
import com.dokkaebistudio.tacticaljourney.enemies.spiders.EnemySpider;
import com.dokkaebistudio.tacticaljourney.enemies.spiders.EnemyVenomSpider;
import com.dokkaebistudio.tacticaljourney.enemies.spiders.EnemyWebSpider;
import com.dokkaebistudio.tacticaljourney.enums.AnimationsEnum;
import com.dokkaebistudio.tacticaljourney.enums.DamageType;
import com.dokkaebistudio.tacticaljourney.enums.StatesEnum;
import com.dokkaebistudio.tacticaljourney.factory.EnemyFactory;
import com.dokkaebistudio.tacticaljourney.factory.EntityFlagEnum;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.SpiderItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.TribesmenSpearItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.VenomSpiderItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.WebSpiderItemPool;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.vfx.AttackAnimation;

/**
 * Factory used to create presets of entities.
 * @author Callil
 *
 */
public final class EnemyTribesmenFactory {
	
	/** The gdx pooled engine. */
	public PooledEngine engine;
	
	/** the entity factory. */
	public EnemyFactory enemyFactory;
	
	/**
	 * Constructor.
	 * @param e the engine
	 */
	public EnemyTribesmenFactory(PooledEngine e, EnemyFactory ef) {
		this.engine = e;
		this.enemyFactory = ef;
	}
	

	/**
	 * Create an enemy.
	 * @param pos the position
	 * @return the enemy entity
	 */
	public Entity createSpearman(Room room, Vector2 pos) {
		Entity enemyEntity = engine.createEntity();
		enemyEntity.flags = EntityFlagEnum.ENEMY_TRIBESMEN_SPEAR.getFlag();

		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		enemyEntity.add(spriteCompo);
		
		AnimationComponent animCompo = engine.createComponent(AnimationComponent.class);
		animCompo.animations.put(StatesEnum.TRIBESMEN_SPEAR_STAND.getState(), AnimationsEnum.TRIBESMEN_SPEAR_STAND.getAnimation());
		enemyEntity.add(animCompo);
		
		StateComponent stateCompo = engine.createComponent(StateComponent.class);
		stateCompo.set(StatesEnum.TRIBESMEN_SPEAR_STAND.getState());
		enemyEntity.add(stateCompo);
		
		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		gridPosition.coord(enemyEntity, pos, room);
		gridPosition.zIndex = ZIndexConstants.ENEMY;
		enemyEntity.add(gridPosition);
		
		EnemyComponent enemyComponent = engine.createComponent(EnemyComponent.class);
		enemyComponent.room = room;
		enemyComponent.setType(new EnemySpider());
		enemyComponent.setFaction(EnemyFactionEnum.TRIBESMEN);
		enemyComponent.setBasicMoveStrategy(EnemyMoveStrategy.MOVE_RANDOMLY_BUT_ATTACK_IF_POSSIBLE);
		enemyComponent.setAlertedMoveStrategy(EnemyMoveStrategy.MOVE_TOWARD_PLAYER);
		Entity alertedDisplayer = this.enemyFactory.entityFactory.createTextOnTile(pos, "", ZIndexConstants.HEALTH_DISPLAYER, room);
		enemyComponent.setAlertedDisplayer(alertedDisplayer);
		enemyEntity.add(enemyComponent);
		
		MoveComponent moveComponent = engine.createComponent(MoveComponent.class);
		moveComponent.room = room;
		moveComponent.setMoveSpeed(3);
		enemyEntity.add(moveComponent);
		
		AttackComponent attackComponent = engine.createComponent(AttackComponent.class);
		attackComponent.room = room;
		attackComponent.setAttackType(AttackTypeEnum.MELEE);
		attackComponent.setRangeMax(2);
		attackComponent.setStrength(7);
		AttackAnimation attackAnimation = new AttackAnimation(
				new Animation<>(0.03f, Assets.slash_animation), true);
		attackComponent.setAttackAnimation(attackAnimation);
		enemyEntity.add(attackComponent);
		
		SolidComponent solidComponent = engine.createComponent(SolidComponent.class);
		enemyEntity.add(solidComponent);
		
		HealthComponent healthComponent = engine.createComponent(HealthComponent.class);
		healthComponent.room = room;
		healthComponent.setMaxHp(13);
		healthComponent.setHp(13);
		Entity hpEntity = this.enemyFactory.entityFactory.createTextOnTile(pos, String.valueOf(healthComponent.getHp()), ZIndexConstants.HEALTH_DISPLAYER, room);
		healthComponent.setHpDisplayer(hpEntity);
		enemyEntity.add(healthComponent);
		
		ExpRewardComponent expRewardCompo = engine.createComponent(ExpRewardComponent.class);
		expRewardCompo.setExpGain(6);
		enemyEntity.add(expRewardCompo);
		
		LootRewardComponent lootRewardCompo = engine.createComponent(LootRewardComponent.class);
		lootRewardCompo.setItemPool(new TribesmenSpearItemPool());
		DropRate dropRate = new DropRate();
		dropRate.add(ItemPoolRarity.COMMON, 40);
		dropRate.add(ItemPoolRarity.RARE, 5);
		lootRewardCompo.setDropRate(dropRate);
		enemyEntity.add(lootRewardCompo);
		
		StatusReceiverComponent statusReceiverCompo = engine.createComponent(StatusReceiverComponent.class);
		enemyEntity.add(statusReceiverCompo);
		
		OrbCarrierComponent orbCarrierCompo = engine.createComponent(OrbCarrierComponent.class);
		enemyEntity.add(orbCarrierCompo);
		
		room.addEnemy(enemyEntity);
		
		return enemyEntity;
	}

	
}