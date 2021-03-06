/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.factory.enemies;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.ai.movements.AttackTypeEnum;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.ces.components.AIComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.ExpRewardComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.InspectableComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.SolidComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.attack.AttackComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.attack.AttackSkill;
import com.dokkaebistudio.tacticaljourney.ces.components.creep.CreepEmitterComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.loot.DropRate;
import com.dokkaebistudio.tacticaljourney.ces.components.loot.LootRewardComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.loot.DropRate.ItemPoolRarity;
import com.dokkaebistudio.tacticaljourney.constants.ZIndexConstants;
import com.dokkaebistudio.tacticaljourney.creature.enemies.enums.AIMoveStrategy;
import com.dokkaebistudio.tacticaljourney.creature.enemies.enums.EnemyFactionEnum;
import com.dokkaebistudio.tacticaljourney.creature.enemies.spiders.EnemySpider;
import com.dokkaebistudio.tacticaljourney.creature.enemies.spiders.EnemyVenomSpider;
import com.dokkaebistudio.tacticaljourney.creature.enemies.spiders.EnemyWebSpider;
import com.dokkaebistudio.tacticaljourney.creeps.Creep.CreepType;
import com.dokkaebistudio.tacticaljourney.enums.DamageType;
import com.dokkaebistudio.tacticaljourney.factory.EnemyFactory;
import com.dokkaebistudio.tacticaljourney.factory.EntityFlagEnum;
import com.dokkaebistudio.tacticaljourney.items.pools.ItemPoolSingleton;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.singletons.AnimationSingleton;
import com.dokkaebistudio.tacticaljourney.vfx.AttackAnimation;

/**
 * Factory used to create presets of entities.
 * @author Callil
 *
 */
public final class EnemySpiderFactory {
	
	/** The gdx pooled engine. */
	public PooledEngine engine;
	
	/** the entity factory. */
	public EnemyFactory enemyFactory;
	
	/**
	 * Constructor.
	 * @param e the engine
	 */
	public EnemySpiderFactory(PooledEngine e, EnemyFactory ef) {
		this.engine = e;
		this.enemyFactory = ef;
	}
	

	/**
	 * Create an enemy.
	 * @param pos the position
	 * @return the enemy entity
	 */
	public Entity createSpider(Room room, Vector2 pos) {
		Entity enemyEntity = this.enemyFactory.createEnemyBase(room, pos, EntityFlagEnum.ENEMY_SPIDER, Assets.enemy_spider);

		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.ENEMY_SPIDER_TITLE);
		inspect.setDescription(Descriptions.ENEMY_SPIDER_DESCRIPTION);
		inspect.setBigPopup(true);
		enemyEntity.add(inspect);
		
		AIComponent aiComponent = engine.createComponent(AIComponent.class);
		aiComponent.room = room;
		aiComponent.setType(new EnemySpider());
		aiComponent.setBasicMoveStrategy(AIMoveStrategy.MOVE_RANDOMLY_BUT_ATTACK_IF_POSSIBLE);
		aiComponent.setAlertedMoveStrategy(AIMoveStrategy.MOVE_TOWARDS_TARGET);
		enemyEntity.add(aiComponent);
		
		EnemyComponent enemyComponent = engine.createComponent(EnemyComponent.class);
		enemyComponent.setFaction(EnemyFactionEnum.SPIDERS);
		enemyEntity.add(enemyComponent);
		
		MoveComponent moveComponent = engine.createComponent(MoveComponent.class);
		moveComponent.room = room;
		moveComponent.setMoveSpeed(3);
		enemyEntity.add(moveComponent);
		
		AttackComponent attackComponent = engine.createComponent(AttackComponent.class);
		attackComponent.room = room;
		
		AttackSkill as = new AttackSkill();
		as.setName("Bite");
		as.setRangeMax(1);
		as.setStrength(5);
		as.setAttackType(AttackTypeEnum.MELEE);
		AttackAnimation attackAnimation = new AttackAnimation(
				AnimationSingleton.getInstance().attack_slash, true);
		as.setAttackAnimation(attackAnimation);
		attackComponent.getSkills().add(as);
		
		enemyEntity.add(attackComponent);
		
		SolidComponent solidComponent = engine.createComponent(SolidComponent.class);
		enemyEntity.add(solidComponent);
		
		HealthComponent healthComponent = engine.createComponent(HealthComponent.class);
		healthComponent.room = room;
		healthComponent.setMaxHp(10);
		healthComponent.setHp(10);
		enemyEntity.add(healthComponent);
		
		ExpRewardComponent expRewardCompo = engine.createComponent(ExpRewardComponent.class);
		expRewardCompo.setExpGain(2);
		enemyEntity.add(expRewardCompo);
		
		LootRewardComponent lootRewardCompo = engine.createComponent(LootRewardComponent.class);
		lootRewardCompo.setItemPool(ItemPoolSingleton.getInstance().spider);
		DropRate dropRate = new DropRate();
		dropRate.add(ItemPoolRarity.RARE, 5f);
		dropRate.add(ItemPoolRarity.COMMON, 20f);
		lootRewardCompo.setDropRate(dropRate);
		lootRewardCompo.setDropSeededRandom(RandomSingleton.getInstance().getNextSeededRandom());
		enemyEntity.add(lootRewardCompo);
		
		StatusReceiverComponent statusReceiverCompo = engine.createComponent(StatusReceiverComponent.class);
		enemyEntity.add(statusReceiverCompo);
		
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
	public Entity createSpiderWeb(Room room, Vector2 pos) {
		Entity enemyEntity = this.enemyFactory.createEnemyBase(room, pos, EntityFlagEnum.ENEMY_SPIDER_WEB, Assets.enemy_spider_web);

		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.ENEMY_WEB_SPIDER_TITLE);
		inspect.setDescription(Descriptions.ENEMY_WEB_SPIDER_DESCRIPTION);
		inspect.setBigPopup(true);
		enemyEntity.add(inspect);
		
		AIComponent aiComponent = engine.createComponent(AIComponent.class);
		aiComponent.room = room;
		aiComponent.setType(new EnemyWebSpider());
		aiComponent.setBasicMoveStrategy(AIMoveStrategy.MOVE_RANDOMLY_BUT_ATTACK_FROM_RANGE_IF_POSSIBLE);
		aiComponent.setAlertedMoveStrategy(AIMoveStrategy.MOVE_RANDOMLY_BUT_ATTACK_FROM_RANGE_IF_POSSIBLE);
		enemyEntity.add(aiComponent);
		
		EnemyComponent enemyComponent = engine.createComponent(EnemyComponent.class);
		enemyComponent.setFaction(EnemyFactionEnum.SPIDERS);
		enemyEntity.add(enemyComponent);
		
		MoveComponent moveComponent = engine.createComponent(MoveComponent.class);
		moveComponent.room = room;
		moveComponent.setMoveSpeed(4);
		enemyEntity.add(moveComponent);
		
		AttackComponent attackComponent = engine.createComponent(AttackComponent.class);
		attackComponent.room = room;
		
		AttackSkill as = new AttackSkill();
		as.setName("Web throw");
		as.setRangeMin(2);
		as.setRangeMax(3);
		as.setStrength(3);
		as.setAttackType(AttackTypeEnum.RANGE);
		AttackAnimation attackAnimation = new AttackAnimation(
				AnimationSingleton.getInstance().web_projectile, true);
		as.setAttackAnimation(attackAnimation);
		attackComponent.getSkills().add(as);
		
		enemyEntity.add(attackComponent);
		
		SolidComponent solidComponent = engine.createComponent(SolidComponent.class);
		enemyEntity.add(solidComponent);
		
		HealthComponent healthComponent = engine.createComponent(HealthComponent.class);
		healthComponent.room = room;
		healthComponent.setMaxHp(5);
		healthComponent.setHp(5);
		enemyEntity.add(healthComponent);
		
		ExpRewardComponent expRewardCompo = engine.createComponent(ExpRewardComponent.class);
		expRewardCompo.setExpGain(4);
		enemyEntity.add(expRewardCompo);
		
		LootRewardComponent lootRewardCompo = engine.createComponent(LootRewardComponent.class);
		lootRewardCompo.setItemPool(ItemPoolSingleton.getInstance().webSpider);
		DropRate dropRate = new DropRate();
		dropRate.add(ItemPoolRarity.RARE, 10f);
		dropRate.add(ItemPoolRarity.COMMON, 60f);
		lootRewardCompo.setDropRate(dropRate);
		lootRewardCompo.setDropSeededRandom(RandomSingleton.getInstance().getNextSeededRandom());
		enemyEntity.add(lootRewardCompo);
		
		CreepEmitterComponent creepEmitter = engine.createComponent(CreepEmitterComponent.class);
		creepEmitter.setType(CreepType.WEB);
		enemyEntity.add(creepEmitter);
		
		StatusReceiverComponent statusReceiverCompo = engine.createComponent(StatusReceiverComponent.class);
		enemyEntity.add(statusReceiverCompo);
		
		room.addEnemy(enemyEntity);
		
		return enemyEntity;
	}
	
	

	/**
	 * Create a venom spider that can poison on hit.
	 * @param pos the position
	 * @return the enemy entity
	 */
	public Entity createVenomSpider(Room room, Vector2 pos) {
		Entity enemyEntity = this.enemyFactory.createEnemyBase(room, pos, EntityFlagEnum.ENEMY_SPIDER_VENOM, Assets.enemy_spider_venom);

		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.ENEMY_VENOM_SPIDER_TITLE);
		inspect.setDescription(Descriptions.ENEMY_VENOM_SPIDER_DESCRIPTION);
		inspect.setBigPopup(true);
		enemyEntity.add(inspect);
		
		AIComponent aiComponent = engine.createComponent(AIComponent.class);
		aiComponent.room = room;
		aiComponent.setType(new EnemyVenomSpider());
		aiComponent.setBasicMoveStrategy(AIMoveStrategy.MOVE_RANDOMLY_BUT_ATTACK_IF_POSSIBLE);
		aiComponent.setAlertedMoveStrategy(AIMoveStrategy.MOVE_TOWARDS_TARGET);
		enemyEntity.add(aiComponent);
		
		EnemyComponent enemyComponent = engine.createComponent(EnemyComponent.class);
		enemyComponent.setFaction(EnemyFactionEnum.SPIDERS);
		enemyEntity.add(enemyComponent);
		
		MoveComponent moveComponent = engine.createComponent(MoveComponent.class);
		moveComponent.room = room;
		moveComponent.setMoveSpeed(3);
		enemyEntity.add(moveComponent);
		
		AttackComponent attackComponent = engine.createComponent(AttackComponent.class);
		attackComponent.room = room;
		
		AttackSkill as = new AttackSkill();
		as.setName("Venomous bite");
		as.setRangeMax(1);
		as.setStrength(5);
		as.setAttackType(AttackTypeEnum.MELEE);
		AttackAnimation attackAnimation = new AttackAnimation(
				AnimationSingleton.getInstance().attack_slash, true);
		as.setAttackAnimation(attackAnimation);
		attackComponent.getSkills().add(as);
		enemyEntity.add(attackComponent);
		
		SolidComponent solidComponent = engine.createComponent(SolidComponent.class);
		enemyEntity.add(solidComponent);
		
		HealthComponent healthComponent = engine.createComponent(HealthComponent.class);
		healthComponent.room = room;
		healthComponent.setMaxHp(10);
		healthComponent.setHp(10);
		healthComponent.addResistance(DamageType.POISON, 100);
		enemyEntity.add(healthComponent);
		
		ExpRewardComponent expRewardCompo = engine.createComponent(ExpRewardComponent.class);
		expRewardCompo.setExpGain(4);
		enemyEntity.add(expRewardCompo);
		
		LootRewardComponent lootRewardCompo = engine.createComponent(LootRewardComponent.class);
		lootRewardCompo.setItemPool(ItemPoolSingleton.getInstance().venomSpider);
		DropRate dropRate = new DropRate();
		dropRate.add(ItemPoolRarity.RARE, 10f);
		dropRate.add(ItemPoolRarity.COMMON, 30f);
		lootRewardCompo.setDropRate(dropRate);
		lootRewardCompo.setDropSeededRandom(RandomSingleton.getInstance().getNextSeededRandom());
		enemyEntity.add(lootRewardCompo);
		
		StatusReceiverComponent statusReceiverCompo = engine.createComponent(StatusReceiverComponent.class);
		enemyEntity.add(statusReceiverCompo);
		
		room.addEnemy(enemyEntity);
		
		return enemyEntity;
	}
	
}
