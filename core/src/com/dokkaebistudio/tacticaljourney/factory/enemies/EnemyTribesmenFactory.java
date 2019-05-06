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
import com.dokkaebistudio.tacticaljourney.components.DestructibleComponent;
import com.dokkaebistudio.tacticaljourney.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.components.ExpRewardComponent;
import com.dokkaebistudio.tacticaljourney.components.FlyComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.HumanoidComponent;
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
import com.dokkaebistudio.tacticaljourney.components.orbs.OrbCarrierComponent;
import com.dokkaebistudio.tacticaljourney.constants.ZIndexConstants;
import com.dokkaebistudio.tacticaljourney.creature.enemies.enums.AIMoveStrategy;
import com.dokkaebistudio.tacticaljourney.creature.enemies.enums.EnemyFactionEnum;
import com.dokkaebistudio.tacticaljourney.creature.enemies.tribesmen.EnemyTribesmanScout;
import com.dokkaebistudio.tacticaljourney.creature.enemies.tribesmen.EnemyTribesmanShaman;
import com.dokkaebistudio.tacticaljourney.creature.enemies.tribesmen.EnemyTribesmanShield;
import com.dokkaebistudio.tacticaljourney.creature.enemies.tribesmen.EnemyTribesmanSpear;
import com.dokkaebistudio.tacticaljourney.creature.enemies.tribesmen.EnemyTribesmanTotem;
import com.dokkaebistudio.tacticaljourney.enums.StatesEnum;
import com.dokkaebistudio.tacticaljourney.factory.EnemyFactory;
import com.dokkaebistudio.tacticaljourney.factory.EntityFlagEnum;
import com.dokkaebistudio.tacticaljourney.items.pools.ItemPoolSingleton;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.singletons.AnimationSingleton;
import com.dokkaebistudio.tacticaljourney.systems.creatures.subsystems.tribesmen.TribesmanScoutSubSystem;
import com.dokkaebistudio.tacticaljourney.systems.creatures.subsystems.tribesmen.TribesmanShamanSubSystem;
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

		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.ENEMY_TRIBESMAN_SPEAR_TITLE);
		inspect.setDescription(Descriptions.ENEMY_TRIBESMAN_SPEAR_DESCRIPTION);
		inspect.setBigPopup(true);
		enemyEntity.add(inspect);
		
		// Humanoid
		HumanoidComponent humanoidCompo = engine.createComponent(HumanoidComponent.class);
		enemyEntity.add(humanoidCompo);
		
		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		enemyEntity.add(spriteCompo);
		
		AnimationComponent animCompo = engine.createComponent(AnimationComponent.class);
		animCompo.addAnimation(StatesEnum.STANDING, AnimationSingleton.getInstance().tribesmenSpearStand);
		animCompo.addAnimation(StatesEnum.MOVING, AnimationSingleton.getInstance().tribesmenSpearStand);
		enemyEntity.add(animCompo);
		
		StateComponent stateCompo = engine.createComponent(StateComponent.class);
		stateCompo.set(StatesEnum.STANDING);
		enemyEntity.add(stateCompo);
		
		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		gridPosition.coord(enemyEntity, pos, room);
		gridPosition.zIndex = ZIndexConstants.ENEMY;
		enemyEntity.add(gridPosition);
		
		AIComponent aiComponent = engine.createComponent(AIComponent.class);
		aiComponent.room = room;
		aiComponent.setType(new EnemyTribesmanSpear());
		aiComponent.setBasicMoveStrategy(AIMoveStrategy.MOVE_RANDOMLY_BUT_ATTACK_IF_POSSIBLE);
		aiComponent.setAlertedMoveStrategy(AIMoveStrategy.MOVE_TOWARDS_TARGET);
		enemyEntity.add(aiComponent);
		
		EnemyComponent enemyComponent = engine.createComponent(EnemyComponent.class);
		enemyComponent.setFaction(EnemyFactionEnum.TRIBESMEN);
		enemyEntity.add(enemyComponent);
		
		MoveComponent moveComponent = engine.createComponent(MoveComponent.class);
		moveComponent.room = room;
		moveComponent.setMoveSpeed(3);
		enemyEntity.add(moveComponent);
		
		AttackComponent attackComponent = engine.createComponent(AttackComponent.class);
		attackComponent.room = room;
		
		AttackSkill as = new AttackSkill();
		as.setName("Thrust");
		as.setRangeMax(2);
		as.setStrength(7);
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
		healthComponent.setMaxHp(13);
		healthComponent.setHp(13);
		enemyEntity.add(healthComponent);
		
		ExpRewardComponent expRewardCompo = engine.createComponent(ExpRewardComponent.class);
		expRewardCompo.setExpGain(6);
		enemyEntity.add(expRewardCompo);
		
		LootRewardComponent lootRewardCompo = engine.createComponent(LootRewardComponent.class);
		lootRewardCompo.setItemPool(ItemPoolSingleton.getInstance().tribesmanSpear);
		DropRate dropRate = new DropRate();
		dropRate.add(ItemPoolRarity.RARE, 5f);
		dropRate.add(ItemPoolRarity.COMMON, 40f);
		lootRewardCompo.setDropRate(dropRate);
		lootRewardCompo.setDropSeededRandom(RandomSingleton.getInstance().getNextSeededRandom());
		enemyEntity.add(lootRewardCompo);
		
		StatusReceiverComponent statusReceiverCompo = engine.createComponent(StatusReceiverComponent.class);
		enemyEntity.add(statusReceiverCompo);
		
		OrbCarrierComponent orbCarrierCompo = engine.createComponent(OrbCarrierComponent.class);
		enemyEntity.add(orbCarrierCompo);
		
		room.addEnemy(enemyEntity);
		
		return enemyEntity;
	}

	
	public Entity createShieldHolder(Room room, Vector2 pos) {
		Entity enemyEntity = engine.createEntity();
		enemyEntity.flags = EntityFlagEnum.ENEMY_TRIBESMEN_SHIELD.getFlag();

		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.ENEMY_TRIBESMAN_SHIELD_TITLE);
		inspect.setDescription(Descriptions.ENEMY_TRIBESMAN_SHIELD_DESCRIPTION);
		inspect.setBigPopup(true);
		enemyEntity.add(inspect);
		
		// Humanoid
		HumanoidComponent humanoidCompo = engine.createComponent(HumanoidComponent.class);
		enemyEntity.add(humanoidCompo);

		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		enemyEntity.add(spriteCompo);
		
		AnimationComponent animCompo = engine.createComponent(AnimationComponent.class);
		animCompo.addAnimation(StatesEnum.STANDING, AnimationSingleton.getInstance().tribesmenShieldStand);
		animCompo.addAnimation(StatesEnum.MOVING, AnimationSingleton.getInstance().tribesmenShieldStand);
		enemyEntity.add(animCompo);
		
		StateComponent stateCompo = engine.createComponent(StateComponent.class);
		stateCompo.set(StatesEnum.STANDING);
		enemyEntity.add(stateCompo);
		
		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		gridPosition.coord(enemyEntity, pos, room);
		gridPosition.zIndex = ZIndexConstants.ENEMY;
		enemyEntity.add(gridPosition);
		
		AIComponent aiComponent = engine.createComponent(AIComponent.class);
		aiComponent.room = room;
		aiComponent.setType(new EnemyTribesmanShield());
		aiComponent.setBasicMoveStrategy(AIMoveStrategy.MOVE_RANDOMLY_BUT_ATTACK_IF_POSSIBLE);
		aiComponent.setAlertedMoveStrategy(AIMoveStrategy.MOVE_TOWARDS_TARGET);
		enemyEntity.add(aiComponent);
		
		EnemyComponent enemyComponent = engine.createComponent(EnemyComponent.class);
		enemyComponent.setFaction(EnemyFactionEnum.TRIBESMEN);
		enemyEntity.add(enemyComponent);
		
		MoveComponent moveComponent = engine.createComponent(MoveComponent.class);
		moveComponent.room = room;
		moveComponent.setMoveSpeed(3);
		enemyEntity.add(moveComponent);
		
		AttackComponent attackComponent = engine.createComponent(AttackComponent.class);
		attackComponent.room = room;
		
		AttackSkill as = new AttackSkill();
		as.setName("Shield bash");
		as.setRangeMax(1);
		as.setStrength(6);
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
		healthComponent.setMaxHp(13);
		healthComponent.setHp(13);
		healthComponent.setMaxArmor(7);
		healthComponent.setArmor(7);
		enemyEntity.add(healthComponent);
		
		ExpRewardComponent expRewardCompo = engine.createComponent(ExpRewardComponent.class);
		expRewardCompo.setExpGain(6);
		enemyEntity.add(expRewardCompo);
		
		LootRewardComponent lootRewardCompo = engine.createComponent(LootRewardComponent.class);
		lootRewardCompo.setItemPool(ItemPoolSingleton.getInstance().tribesManShield);
		DropRate dropRate = new DropRate();
		dropRate.add(ItemPoolRarity.RARE, 5f);
		dropRate.add(ItemPoolRarity.COMMON, 40f);
		lootRewardCompo.setDropRate(dropRate);
		lootRewardCompo.setDropSeededRandom(RandomSingleton.getInstance().getNextSeededRandom());
		enemyEntity.add(lootRewardCompo);
		
		StatusReceiverComponent statusReceiverCompo = engine.createComponent(StatusReceiverComponent.class);
		enemyEntity.add(statusReceiverCompo);
		
		OrbCarrierComponent orbCarrierCompo = engine.createComponent(OrbCarrierComponent.class);
		enemyEntity.add(orbCarrierCompo);
		
		room.addEnemy(enemyEntity);
		
		return enemyEntity;
	}
	
	
	public Entity createScout(Room room, Vector2 pos) {
		
		Entity enemyEntity = engine.createEntity();
		enemyEntity.flags = EntityFlagEnum.ENEMY_TRIBESMEN_SCOUT.getFlag();

		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.ENEMY_TRIBESMAN_SCOUT_TITLE);
		inspect.setDescription(Descriptions.ENEMY_TRIBESMAN_SCOUT_DESCRIPTION);
		inspect.setBigPopup(true);
		enemyEntity.add(inspect);
		
		// Humanoid
		HumanoidComponent humanoidCompo = engine.createComponent(HumanoidComponent.class);
		enemyEntity.add(humanoidCompo);

		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		enemyEntity.add(spriteCompo);
		
		AnimationComponent animCompo = engine.createComponent(AnimationComponent.class);
		animCompo.addAnimation(StatesEnum.STANDING, AnimationSingleton.getInstance().tribesmenScoutStand);
		animCompo.addAnimation(StatesEnum.MOVING, AnimationSingleton.getInstance().tribesmenScoutStand);
		enemyEntity.add(animCompo);
		
		StateComponent stateCompo = engine.createComponent(StateComponent.class);
		stateCompo.set(StatesEnum.STANDING);
		enemyEntity.add(stateCompo);
		
		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		gridPosition.coord(enemyEntity, pos, room);
		gridPosition.zIndex = ZIndexConstants.ENEMY;
		enemyEntity.add(gridPosition);
		
		AIComponent aiComponent = engine.createComponent(AIComponent.class);
		aiComponent.room = room;
		aiComponent.setType(new EnemyTribesmanScout());
		aiComponent.setSubSystem(new TribesmanScoutSubSystem());
		aiComponent.setBasicMoveStrategy(AIMoveStrategy.MOVE_RANDOMLY_BUT_ATTACK_IF_POSSIBLE);
		aiComponent.setAlertedMoveStrategy(AIMoveStrategy.MOVE_TOWARDS_TARGET);
		enemyEntity.add(aiComponent);
		
		EnemyComponent enemyComponent = engine.createComponent(EnemyComponent.class);
		enemyComponent.setFaction(EnemyFactionEnum.TRIBESMEN);
		enemyEntity.add(enemyComponent);
		
		MoveComponent moveComponent = engine.createComponent(MoveComponent.class);
		moveComponent.room = room;
		moveComponent.setMoveSpeed(5);
		enemyEntity.add(moveComponent);
		
		AttackComponent attackComponent = engine.createComponent(AttackComponent.class);
		attackComponent.room = room;
		
		AttackSkill as = new AttackSkill();
		as.setName("Rock throw");
		as.setRangeMax(3);
		as.setStrength(3);
		as.setAttackType(AttackTypeEnum.RANGE);
		AttackAnimation attackAnimation = new AttackAnimation(
				AnimationSingleton.getInstance().pebble_projectile, true);
		as.setAttackAnimation(attackAnimation);
		attackComponent.getSkills().add(as);

		enemyEntity.add(attackComponent);
		
		SolidComponent solidComponent = engine.createComponent(SolidComponent.class);
		enemyEntity.add(solidComponent);
		
		HealthComponent healthComponent = engine.createComponent(HealthComponent.class);
		healthComponent.room = room;
		healthComponent.setMaxHp(10);
		healthComponent.setHp(10);
		healthComponent.setMaxArmor(0);
		healthComponent.setArmor(0);
		enemyEntity.add(healthComponent);
		
		ExpRewardComponent expRewardCompo = engine.createComponent(ExpRewardComponent.class);
		expRewardCompo.setExpGain(6);
		enemyEntity.add(expRewardCompo);
		
		LootRewardComponent lootRewardCompo = engine.createComponent(LootRewardComponent.class);
		lootRewardCompo.setItemPool(ItemPoolSingleton.getInstance().tribesmanScout);
		DropRate dropRate = new DropRate();
		dropRate.add(ItemPoolRarity.RARE, 5f);
		dropRate.add(ItemPoolRarity.COMMON, 40f);
		lootRewardCompo.setDropRate(dropRate);
		lootRewardCompo.setDropSeededRandom(RandomSingleton.getInstance().getNextSeededRandom());
		enemyEntity.add(lootRewardCompo);
		
		StatusReceiverComponent statusReceiverCompo = engine.createComponent(StatusReceiverComponent.class);
		enemyEntity.add(statusReceiverCompo);
		
		OrbCarrierComponent orbCarrierCompo = engine.createComponent(OrbCarrierComponent.class);
		enemyEntity.add(orbCarrierCompo);
		
		room.addEnemy(enemyEntity);
		
		return enemyEntity;
	}
	
	
	
	public Entity createShaman(Room room, Vector2 pos) {
		
		Entity enemyEntity = engine.createEntity();
		enemyEntity.flags = EntityFlagEnum.ENEMY_TRIBESMEN_SHAMAN.getFlag();

		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.ENEMY_TRIBESMAN_SHAMAN_TITLE);
		inspect.setDescription(Descriptions.ENEMY_TRIBESMAN_SHAMAN_DESCRIPTION);
		inspect.setBigPopup(true);
		enemyEntity.add(inspect);
		
		// Humanoid
		HumanoidComponent humanoidCompo = engine.createComponent(HumanoidComponent.class);
		enemyEntity.add(humanoidCompo);

		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		enemyEntity.add(spriteCompo);
		
		AnimationComponent animCompo = engine.createComponent(AnimationComponent.class);
		animCompo.addAnimation(StatesEnum.STANDING, AnimationSingleton.getInstance().tribesmenShamanStand);
		animCompo.addAnimation(StatesEnum.MOVING, AnimationSingleton.getInstance().tribesmenShamanStand);
		animCompo.addAnimation(StatesEnum.TRIBESMEN_SHAMAN_SUMMONING, AnimationSingleton.getInstance().tribesmenShamanSummoning);
		enemyEntity.add(animCompo);
		
		StateComponent stateCompo = engine.createComponent(StateComponent.class);
		stateCompo.set(StatesEnum.STANDING);
		enemyEntity.add(stateCompo);
		
		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		gridPosition.coord(enemyEntity, pos, room);
		gridPosition.zIndex = ZIndexConstants.ENEMY;
		enemyEntity.add(gridPosition);
		
		AIComponent aiComponent = engine.createComponent(AIComponent.class);
		aiComponent.room = room;
		aiComponent.setType(new EnemyTribesmanShaman());
		aiComponent.setSubSystem(new TribesmanShamanSubSystem());
		aiComponent.setBasicMoveStrategy(AIMoveStrategy.STANDING_STILL);
		aiComponent.setAlertedMoveStrategy(AIMoveStrategy.STANDING_STILL);
		enemyEntity.add(aiComponent);
		
		EnemyComponent enemyComponent = engine.createComponent(EnemyComponent.class);
		enemyComponent.setFaction(EnemyFactionEnum.TRIBESMEN);
		enemyEntity.add(enemyComponent);
		
		MoveComponent moveComponent = engine.createComponent(MoveComponent.class);
		moveComponent.room = room;
		moveComponent.setMoveSpeed(5);
		enemyEntity.add(moveComponent);
		
		AttackComponent attackComponent = engine.createComponent(AttackComponent.class);
		attackComponent.room = room;
		
		AttackSkill as = new AttackSkill();
		as.setName("Rock throw");
		as.setRangeMax(3);
		as.setStrength(3);
		as.setAttackType(AttackTypeEnum.RANGE);
		AttackAnimation attackAnimation = new AttackAnimation(
				AnimationSingleton.getInstance().pebble_projectile, true);
		as.setAttackAnimation(attackAnimation);
		attackComponent.getSkills().add(as);
		
		enemyEntity.add(attackComponent);
		
		SolidComponent solidComponent = engine.createComponent(SolidComponent.class);
		enemyEntity.add(solidComponent);
		
		HealthComponent healthComponent = engine.createComponent(HealthComponent.class);
		healthComponent.room = room;
		healthComponent.setMaxHp(10);
		healthComponent.setHp(5);
		healthComponent.setMaxArmor(0);
		healthComponent.setArmor(0);
		enemyEntity.add(healthComponent);
		
		ExpRewardComponent expRewardCompo = engine.createComponent(ExpRewardComponent.class);
		expRewardCompo.setExpGain(50);
		enemyEntity.add(expRewardCompo);
		
		LootRewardComponent lootRewardCompo = engine.createComponent(LootRewardComponent.class);
		lootRewardCompo.setItemPool(ItemPoolSingleton.getInstance().tribesmanScout);
		DropRate dropRate = new DropRate();
		dropRate.add(ItemPoolRarity.RARE, 5f);
		dropRate.add(ItemPoolRarity.COMMON, 40f);
		lootRewardCompo.setDropRate(dropRate);
		lootRewardCompo.setDropSeededRandom(RandomSingleton.getInstance().getNextSeededRandom());
		enemyEntity.add(lootRewardCompo);
		
		StatusReceiverComponent statusReceiverCompo = engine.createComponent(StatusReceiverComponent.class);
		enemyEntity.add(statusReceiverCompo);
		
		OrbCarrierComponent orbCarrierCompo = engine.createComponent(OrbCarrierComponent.class);
		enemyEntity.add(orbCarrierCompo);
		
		room.addEnemy(enemyEntity);
		
		return enemyEntity;
	}
	
	
	public Entity createTotem(Room room, Vector2 pos) {
		Entity enemyEntity = engine.createEntity();
		enemyEntity.flags = EntityFlagEnum.ENEMY_TRIBESMEN_TOTEM.getFlag();

		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.ENEMY_TRIBESMAN_TOTEM_TITLE);
		inspect.setDescription(Descriptions.ENEMY_TRIBESMAN_TOTEM_DESCRIPTION);
		inspect.setBigPopup(true);
		enemyEntity.add(inspect);
		
		// Humanoid
		HumanoidComponent humanoidCompo = engine.createComponent(HumanoidComponent.class);
		enemyEntity.add(humanoidCompo);

		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		enemyEntity.add(spriteCompo);
		
		AnimationComponent animCompo = engine.createComponent(AnimationComponent.class);
		animCompo.addAnimation(StatesEnum.STANDING, AnimationSingleton.getInstance().tribesmenTotem);
		animCompo.addAnimation(StatesEnum.MOVING, AnimationSingleton.getInstance().tribesmenTotem);
		animCompo.addAnimation(StatesEnum.FLY_STANDING, AnimationSingleton.getInstance().tribesmenTotem);
		animCompo.addAnimation(StatesEnum.FLY_MOVING, AnimationSingleton.getInstance().tribesmenTotem);
		enemyEntity.add(animCompo);
		
		StateComponent stateCompo = engine.createComponent(StateComponent.class);
		stateCompo.set(StatesEnum.STANDING);
		enemyEntity.add(stateCompo);
		
		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		gridPosition.coord(enemyEntity, pos, room);
		gridPosition.zIndex = ZIndexConstants.ENEMY;
		enemyEntity.add(gridPosition);
		
		AIComponent aiComponent = engine.createComponent(AIComponent.class);
		aiComponent.room = room;
		aiComponent.setType(new EnemyTribesmanTotem());
		aiComponent.setBasicMoveStrategy(AIMoveStrategy.MOVE_TOWARDS_TARGET);
		aiComponent.setAlertedMoveStrategy(AIMoveStrategy.MOVE_TOWARDS_TARGET);
		enemyEntity.add(aiComponent);
		
		EnemyComponent enemyComponent = engine.createComponent(EnemyComponent.class);
		enemyComponent.setCanActivateOrbs(false);
		enemyComponent.setFaction(EnemyFactionEnum.SOLITARY);
		enemyEntity.add(enemyComponent);
		
		MoveComponent moveComponent = engine.createComponent(MoveComponent.class);
		moveComponent.room = room;
		moveComponent.setMoveSpeed(3);
		enemyEntity.add(moveComponent);
		
		AttackComponent attackComponent = engine.createComponent(AttackComponent.class);
		attackComponent.room = room;
		
		AttackSkill as = new AttackSkill();
		as.setRangeMax(0);
		as.setStrength(0);
		as.setAttackType(AttackTypeEnum.MELEE);
		attackComponent.getSkills().add(as);
		
		enemyEntity.add(attackComponent);
		
		SolidComponent solidComponent = engine.createComponent(SolidComponent.class);
		enemyEntity.add(solidComponent);
		
		FlyComponent flyCompo = engine.createComponent(FlyComponent.class);
		enemyEntity.add(flyCompo);
		
		OrbCarrierComponent orbCarrierCompo = engine.createComponent(OrbCarrierComponent.class);
		orbCarrierCompo.room = room;
		enemyEntity.add(orbCarrierCompo);
		
    	DestructibleComponent destructibleCompo = engine.createComponent(DestructibleComponent.class);
    	enemyEntity.add(destructibleCompo);

		room.addEnemy(enemyEntity);
		
		return enemyEntity;
	}
}
