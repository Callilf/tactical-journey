/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.factory.enemies;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.ai.movements.AttackTypeEnum;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.AIComponent;
import com.dokkaebistudio.tacticaljourney.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.components.ExpRewardComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.InspectableComponent;
import com.dokkaebistudio.tacticaljourney.components.SolidComponent;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.attack.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.attack.AttackSkill;
import com.dokkaebistudio.tacticaljourney.components.display.AnimationComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.display.StateComponent;
import com.dokkaebistudio.tacticaljourney.components.loot.DropRate;
import com.dokkaebistudio.tacticaljourney.components.loot.DropRate.ItemPoolRarity;
import com.dokkaebistudio.tacticaljourney.components.loot.LootRewardComponent;
import com.dokkaebistudio.tacticaljourney.constants.ZIndexConstants;
import com.dokkaebistudio.tacticaljourney.creature.enemies.enums.AIMoveStrategy;
import com.dokkaebistudio.tacticaljourney.creature.enemies.enums.EnemyFactionEnum;
import com.dokkaebistudio.tacticaljourney.creature.enemies.pangolins.EnemyPangolinBaby;
import com.dokkaebistudio.tacticaljourney.creature.enemies.pangolins.EnemyPangolinMother;
import com.dokkaebistudio.tacticaljourney.enums.DamageType;
import com.dokkaebistudio.tacticaljourney.enums.StatesEnum;
import com.dokkaebistudio.tacticaljourney.factory.EnemyFactory;
import com.dokkaebistudio.tacticaljourney.factory.EntityFlagEnum;
import com.dokkaebistudio.tacticaljourney.items.pools.ItemPoolSingleton;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.singletons.AnimationSingleton;
import com.dokkaebistudio.tacticaljourney.systems.entitysystems.creatures.subsystems.pangolins.PangolinMotherSubSystem;
import com.dokkaebistudio.tacticaljourney.vfx.AttackAnimation;

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
		Entity enemyEntity = this.enemyFactory.createEnemyBase(room, pos, EntityFlagEnum.ENEMY_PANGOLIN_BABY, null);
		
		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.ENEMY_BABY_PANGOLIN_TITLE);
		inspect.setDescription(Descriptions.ENEMY_BABY_PANGOLIN_DESCRIPTION);
		inspect.setBigPopup(true);
		enemyEntity.add(inspect);

		AnimationComponent animationCompo = engine.createComponent(AnimationComponent.class);
		animationCompo.addAnimation(StatesEnum.STANDING, AnimationSingleton.getInstance().pangolinBabyStand);
		animationCompo.addAnimation(StatesEnum.MOVING, AnimationSingleton.getInstance().pangolinBabyStand);
		enemyEntity.add(animationCompo);
		
		StateComponent stateCompo = engine.createComponent(StateComponent.class);
		stateCompo.set(StatesEnum.STANDING);
		enemyEntity.add(stateCompo);
		
		AIComponent aiComponent = engine.createComponent(AIComponent.class);
		aiComponent.room = room;
		aiComponent.setType(new EnemyPangolinBaby(mother));
		aiComponent.setBasicMoveStrategy(AIMoveStrategy.MOVE_RANDOMLY);
		aiComponent.setAlertedMoveStrategy(AIMoveStrategy.MOVE_TOWARDS_TARGET);
		enemyEntity.add(aiComponent);
		
		EnemyComponent enemyComponent = engine.createComponent(EnemyComponent.class);
		enemyComponent.setFaction(EnemyFactionEnum.PANGOLINS);
		enemyEntity.add(enemyComponent);
		
		MoveComponent moveComponent = engine.createComponent(MoveComponent.class);
		moveComponent.room = room;
		moveComponent.setMoveSpeed(2);
		enemyEntity.add(moveComponent);
				
		AttackComponent attackComponent = engine.createComponent(AttackComponent.class);
		attackComponent.room = room;
		
		AttackSkill as = new AttackSkill();
		as.setName("Headbutt");
		as.setRangeMax(1);
		as.setStrength(9);
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
		healthComponent.addResistance(DamageType.EXPLOSION, 75);
		healthComponent.setMaxHp(12);
		healthComponent.setHp(12);
		healthComponent.setMaxArmor(20);
		enemyEntity.add(healthComponent);
		
		ExpRewardComponent expRewardCompo = engine.createComponent(ExpRewardComponent.class);
		expRewardCompo.setExpGain(5);
		enemyEntity.add(expRewardCompo);
		
		LootRewardComponent lootRewardCompo = engine.createComponent(LootRewardComponent.class);
		lootRewardCompo.setItemPool(ItemPoolSingleton.getInstance().pangolin);
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
	
	
	/**
	 * Create the mother pangolin.
	 * @param pos the position
	 * @return the enemy entity
	 */
	public Entity createPangolinMother(Room room, Vector2 pos) {
		Entity enemyEntity = this.enemyFactory.createEnemyBase(room, pos, EntityFlagEnum.ENEMY_PANGOLIN_MOTHER, null);
		
		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.ENEMY_PANGOLIN_MATRIARCH_TITLE);
		inspect.setDescription(Descriptions.ENEMY_PANGOLIN_MATRIARCH_DESCRIPTION);
		inspect.setBigPopup(true);
		enemyEntity.add(inspect);

		AnimationComponent animationCompo = engine.createComponent(AnimationComponent.class);
		animationCompo.addAnimation(StatesEnum.STANDING, AnimationSingleton.getInstance().pangolinMotherStand);
		animationCompo.addAnimation(StatesEnum.MOVING, AnimationSingleton.getInstance().pangolinMotherStand);
		enemyEntity.add(animationCompo);
		
		StateComponent stateCompo = engine.createComponent(StateComponent.class);
		stateCompo.set(StatesEnum.STANDING);
		enemyEntity.add(stateCompo);
		
		AIComponent aiComponent = engine.createComponent(AIComponent.class);
		aiComponent.room = room;
		aiComponent.setType(new EnemyPangolinMother());
		aiComponent.setSubSystem(new PangolinMotherSubSystem());
		aiComponent.setBasicMoveStrategy(AIMoveStrategy.MOVE_RANDOMLY_BUT_ATTACK_IF_POSSIBLE);
		aiComponent.setAlertedMoveStrategy(AIMoveStrategy.MOVE_TOWARDS_TARGET);
		enemyEntity.add(aiComponent);
		
		EnemyComponent enemyComponent = engine.createComponent(EnemyComponent.class);
		enemyComponent.setFaction(EnemyFactionEnum.PANGOLINS);
		enemyEntity.add(enemyComponent);
		
		MoveComponent moveComponent = engine.createComponent(MoveComponent.class);
		moveComponent.room = room;
		moveComponent.setMoveSpeed(3);
		enemyEntity.add(moveComponent);
				
		AttackComponent attackComponent = engine.createComponent(AttackComponent.class);
		attackComponent.room = room;
		
		AttackSkill as = new AttackSkill();
		as.setName("Headbutt");
		as.setRangeMax(1);
		as.setStrength(12);
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
		healthComponent.addResistance(DamageType.EXPLOSION, 75);
		healthComponent.setMaxHp(20);
		healthComponent.setHp(20);
		healthComponent.setMaxArmor(50);
		healthComponent.setArmor(50);
		enemyEntity.add(healthComponent);
		
		ExpRewardComponent expRewardCompo = engine.createComponent(ExpRewardComponent.class);
		expRewardCompo.setExpGain(50);
		enemyEntity.add(expRewardCompo);
		
		LootRewardComponent lootRewardCompo = engine.createComponent(LootRewardComponent.class);
		lootRewardCompo.setItemPool(ItemPoolSingleton.getInstance().pangolinMatriarch);
		DropRate dropRate = new DropRate();
		dropRate.add(ItemPoolRarity.RARE, 0f);
		dropRate.add(ItemPoolRarity.COMMON, 100f);
		lootRewardCompo.setDropRate(dropRate);
		lootRewardCompo.setDropSeededRandom(RandomSingleton.getInstance().getNextSeededRandom());
		enemyEntity.add(lootRewardCompo);
		
		StatusReceiverComponent statusReceiverCompo = engine.createComponent(StatusReceiverComponent.class);
		enemyEntity.add(statusReceiverCompo);
		
		room.addEnemy(enemyEntity);
		
		return enemyEntity;
	}
}
