/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.factory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.ai.movements.AttackTypeEnum;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.AIComponent;
import com.dokkaebistudio.tacticaljourney.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.components.ExpRewardComponent;
import com.dokkaebistudio.tacticaljourney.components.FlyComponent;
import com.dokkaebistudio.tacticaljourney.components.GravityComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.HumanoidComponent;
import com.dokkaebistudio.tacticaljourney.components.InspectableComponent;
import com.dokkaebistudio.tacticaljourney.components.SolidComponent;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.attack.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.attack.AttackSkill;
import com.dokkaebistudio.tacticaljourney.components.creep.CreepImmunityComponent;
import com.dokkaebistudio.tacticaljourney.components.display.AnimationComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.display.StateComponent;
import com.dokkaebistudio.tacticaljourney.components.loot.DropRate;
import com.dokkaebistudio.tacticaljourney.components.loot.DropRate.ItemPoolRarity;
import com.dokkaebistudio.tacticaljourney.components.loot.LootRewardComponent;
import com.dokkaebistudio.tacticaljourney.constants.ZIndexConstants;
import com.dokkaebistudio.tacticaljourney.creature.enemies.EnemyOrangutan;
import com.dokkaebistudio.tacticaljourney.creature.enemies.EnemyScorpion;
import com.dokkaebistudio.tacticaljourney.creature.enemies.EnemyShinobi;
import com.dokkaebistudio.tacticaljourney.creature.enemies.EnemySmallOrangutan;
import com.dokkaebistudio.tacticaljourney.creature.enemies.EnemyStinger;
import com.dokkaebistudio.tacticaljourney.creature.enemies.enums.AIMoveStrategy;
import com.dokkaebistudio.tacticaljourney.creature.enemies.enums.EnemyFactionEnum;
import com.dokkaebistudio.tacticaljourney.creature.enemies.enums.EnemyTypeEnum;
import com.dokkaebistudio.tacticaljourney.creeps.Creep.CreepType;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.enums.StatesEnum;
import com.dokkaebistudio.tacticaljourney.factory.enemies.EnemyPangolinFactory;
import com.dokkaebistudio.tacticaljourney.factory.enemies.EnemySpiderFactory;
import com.dokkaebistudio.tacticaljourney.factory.enemies.EnemyTribesmenFactory;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.items.pools.ItemPoolSingleton;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.singletons.AnimationSingleton;
import com.dokkaebistudio.tacticaljourney.systems.entitysystems.creatures.subsystems.OrangutanSubSystem;
import com.dokkaebistudio.tacticaljourney.systems.entitysystems.creatures.subsystems.ShinobiSubSystem;
import com.dokkaebistudio.tacticaljourney.systems.entitysystems.creatures.subsystems.StingerSubSystem;
import com.dokkaebistudio.tacticaljourney.vfx.AttackAnimation;

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
	
	public EnemyPangolinFactory pangolinFactory;
	public EnemySpiderFactory spiderFactory;
	public EnemyTribesmenFactory tribesmenFactory;
	
	/**
	 * Constructor.
	 * @param e the engine
	 */
	public EnemyFactory(PooledEngine e, EntityFactory ef) {
		this.engine = e;
		this.entityFactory = ef;
		
		this.pangolinFactory = new EnemyPangolinFactory(e, this);
		this.spiderFactory = new EnemySpiderFactory(e, this);
		this.tribesmenFactory = new EnemyTribesmenFactory(e, this);
	}
	
	public Entity createEnemy(EnemyTypeEnum type, Room room, Vector2 pos) {
		Entity enemy = null;
		
		switch(type) {
		case SPIDER:
			enemy = spiderFactory.createSpider(room, pos);
			break;
		case WEB_SPIDER:
			enemy = spiderFactory.createSpiderWeb(room, pos);
			break;
		case VENOM_SPIDER:
			enemy = spiderFactory.createVenomSpider(room, pos);
			break;
			
		case SCORPION:
			enemy = createScorpion(room, pos);
			break;
		case STINGER:
			enemy = createStinger(room, pos);
			break;
			
		case PANGOLIN_BABY:
			enemy = pangolinFactory.createPangolinBaby(room, pos, null);
			break;
		case PANGOLIN_MATRIARCH:
			enemy = pangolinFactory.createPangolinMother(room, pos);
			break;
			
		case TRIBESMAN_SPEAR:
			enemy = tribesmenFactory.createSpearman(room, pos);
			break;
		case TRIBESMAN_SHIELD:
			enemy = tribesmenFactory.createShieldHolder(room, pos);
			break;
		case TRIBESMAN_SCOUT:
			enemy = tribesmenFactory.createScout(room, pos);
			break;
		case TRIBESMAN_SHAMAN:
			enemy = tribesmenFactory.createShaman(room, pos);
			break;
		case TRIBESMAN_TOTEM:
			enemy = tribesmenFactory.createTotem(room, pos);
			break;
			
			
		case SHINOBI:
			enemy = createShinobi(room, pos, false);
			break;
		case ORANGUTAN_ALPHA:
			enemy = createOrangutanAlpha(room, pos);
			break;
		case ORANGUTAN:
			enemy = createOrangutan(room, pos);
			break;
			
		default:
		}
		
		return enemy;
	}
	
	
	
	/**
	 * Create the base of a lootable.
	 * @param room the room
	 * @param pos the position
	 * @return the lootable entity
	 */
	public Entity createEnemyBase(Room room, Vector2 pos, EntityFlagEnum flag, RegionDescriptor sprite) {
		Entity enemyEntity = engine.createEntity();
		enemyEntity.flags = flag.getFlag();

		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		if (sprite != null) spriteCompo.setSprite(sprite);
		enemyEntity.add(spriteCompo);

		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		gridPosition.coord(enemyEntity, pos, room);
		gridPosition.zIndex = ZIndexConstants.ENEMY;
		enemyEntity.add(gridPosition);
		
		GravityComponent gravityCompo = engine.createComponent(GravityComponent.class);
		enemyEntity.add(gravityCompo);
		
		return enemyEntity;
	}
	
	
	/**
	 * Create a scorpion.
	 * @param pos the position
	 * @return the enemy entity
	 */
	public Entity createScorpion(Room room, Vector2 pos) {
		Entity enemyEntity = createEnemyBase(room, pos, EntityFlagEnum.ENEMY_SCORPION, Assets.enemy_scorpion);
		
		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.ENEMY_SCORPION_TITLE);
		inspect.setDescription(Descriptions.ENEMY_SCORPION_DESCRIPTION);
		inspect.setBigPopup(true);
		enemyEntity.add(inspect);
		
		AIComponent aiComponent = engine.createComponent(AIComponent.class);
		aiComponent.room = room;
		aiComponent.setType(new EnemyScorpion());
		aiComponent.setBasicMoveStrategy(AIMoveStrategy.MOVE_RANDOMLY_BUT_ATTACK_IF_POSSIBLE);
		aiComponent.setAlertedMoveStrategy(AIMoveStrategy.MOVE_TOWARDS_TARGET);
		aiComponent.setAlerted(true, enemyEntity, GameScreen.player);
		enemyEntity.add(aiComponent);
		
		EnemyComponent enemyComponent = engine.createComponent(EnemyComponent.class);
		enemyComponent.setFaction(EnemyFactionEnum.SOLITARY);
		enemyEntity.add(enemyComponent);
		
		MoveComponent moveComponent = engine.createComponent(MoveComponent.class);
		moveComponent.room = room;
		moveComponent.setMoveSpeed(4);
		enemyEntity.add(moveComponent);
		
		AttackComponent attackComponent = engine.createComponent(AttackComponent.class);
		attackComponent.room = room;
		
		AttackSkill as = new AttackSkill();
		as.setName("Sting");
		as.setRangeMax(1);
		as.setStrength(10);
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
		healthComponent.setMaxHp(15);
		healthComponent.setHp(15);
		enemyEntity.add(healthComponent);
		
		ExpRewardComponent expRewardCompo = engine.createComponent(ExpRewardComponent.class);
		expRewardCompo.setExpGain(6);
		enemyEntity.add(expRewardCompo);
		
		LootRewardComponent lootRewardCompo = engine.createComponent(LootRewardComponent.class);
		lootRewardCompo.setItemPool(ItemPoolSingleton.getInstance().scorpion);
		DropRate dropRate = new DropRate();
		dropRate.add(ItemPoolRarity.RARE, 20f);
		dropRate.add(ItemPoolRarity.COMMON, 50f);
		lootRewardCompo.setDropRate(dropRate);
		lootRewardCompo.setDropSeededRandom(RandomSingleton.getInstance().getNextSeededRandom());
		enemyEntity.add(lootRewardCompo);
		
		StatusReceiverComponent statusReceiverCompo = engine.createComponent(StatusReceiverComponent.class);
		enemyEntity.add(statusReceiverCompo);
		
		room.addEnemy(enemyEntity);
		
		return enemyEntity;
	}
	
	/**
	 * Create a stinger.
	 * @param pos the position
	 * @return the enemy entity
	 */
	public Entity createStinger(Room room, Vector2 pos) {
		Entity enemyEntity = createEnemyBase(room, pos, EntityFlagEnum.ENEMY_STINGER, null);
		
		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.ENEMY_STINGER_TITLE);
		inspect.setDescription(Descriptions.ENEMY_STINGER_DESCRIPTION);
		inspect.setBigPopup(true);
		enemyEntity.add(inspect);

		AnimationComponent animationCompo = engine.createComponent(AnimationComponent.class);
		animationCompo.addAnimation(StatesEnum.FLY_STANDING, AnimationSingleton.getInstance().stingerFly);
		animationCompo.addAnimation(StatesEnum.FLY_MOVING, AnimationSingleton.getInstance().stingerFly);
		animationCompo.addAnimation(StatesEnum.STINGER_ATTACK, AnimationSingleton.getInstance().stingerAttack);
		enemyEntity.add(animationCompo);
		
		StateComponent stateCompo = engine.createComponent(StateComponent.class);
		stateCompo.set(StatesEnum.FLY_STANDING);
		enemyEntity.add(stateCompo);		
		
//		Persister p = new Persister(engine);
//		EnemyComponent enemyComponent = p.loadStinger();
//		enemyComponent.room = room;
		
		AIComponent aiComponent = engine.createComponent(AIComponent.class);
		aiComponent.room = room;
		aiComponent.setType(new EnemyStinger());
		aiComponent.setSubSystem(new StingerSubSystem());
		aiComponent.setBasicMoveStrategy(AIMoveStrategy.MOVE_RANDOMLY_BUT_ATTACK_IF_POSSIBLE);
		aiComponent.setAlertedMoveStrategy(AIMoveStrategy.MOVE_TOWARDS_TARGET);
		enemyEntity.add(aiComponent);
		
		
		EnemyComponent enemyComponent = engine.createComponent(EnemyComponent.class);
		enemyComponent.setFaction(EnemyFactionEnum.SOLITARY);
		enemyEntity.add(enemyComponent);
		
//		Persister p = new Persister(engine);
//		p.save(enemyComponent);
		
		MoveComponent moveComponent = engine.createComponent(MoveComponent.class);
		moveComponent.room = room;
		moveComponent.setMoveSpeed(3);
		enemyEntity.add(moveComponent);
		
		FlyComponent flyComponent = engine.createComponent(FlyComponent.class);
		enemyEntity.add(flyComponent);
		
		AttackComponent attackComponent = engine.createComponent(AttackComponent.class);
		attackComponent.room = room;
		
		AttackSkill as = new AttackSkill();
		as.setName("Sting");
		as.setRangeMax(1);
		as.setStrength(6);
		as.setAttackType(AttackTypeEnum.MELEE);
		AttackAnimation attackAnimation = new AttackAnimation(
				AnimationSingleton.getInstance().attack_slash, true);
		as.setAttackAnimation(attackAnimation);
		attackComponent.getSkills().add(as);
		enemyEntity.add(attackComponent);
		
		AttackSkill charge = new AttackSkill();
		charge.setName("Stinger charge");
		charge.setRangeMax(1);
		charge.setStrength(7);
		charge.setAttackType(AttackTypeEnum.MELEE);
		AttackAnimation attackAnimation2 = new AttackAnimation(
				AnimationSingleton.getInstance().attack_slash, true);
		charge.setAttackAnimation(attackAnimation2);
		attackComponent.getSkills().add(charge);
		enemyEntity.add(attackComponent);
		
		SolidComponent solidComponent = engine.createComponent(SolidComponent.class);
		enemyEntity.add(solidComponent);
		
		HealthComponent healthComponent = engine.createComponent(HealthComponent.class);
		healthComponent.room = room;
		healthComponent.setMaxHp(8);
		healthComponent.setHp(8);
		enemyEntity.add(healthComponent);
		
		ExpRewardComponent expRewardCompo = engine.createComponent(ExpRewardComponent.class);
		expRewardCompo.setExpGain(4);
		enemyEntity.add(expRewardCompo);
		
		LootRewardComponent lootRewardCompo = engine.createComponent(LootRewardComponent.class);
		lootRewardCompo.setItemPool(ItemPoolSingleton.getInstance().stinger);
		DropRate dropRate = new DropRate();
		dropRate.add(ItemPoolRarity.RARE, 20f);
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
	 * Create a shinobi mini boss.
	 * @param pos the position
	 * @return the enemy entity
	 */
	public Entity createShinobi(Room room, Vector2 pos, boolean clone) {
		Entity enemyEntity = createEnemyBase(room, pos, EntityFlagEnum.ENEMY_SHINOBI, null);
		
		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.ENEMY_SHINOBI_TITLE);
		inspect.setDescription(Descriptions.ENEMY_SHINOBI_DESCRIPTION);
		inspect.setBigPopup(true);
		enemyEntity.add(inspect);
		
		AnimationComponent animationCompo = engine.createComponent(AnimationComponent.class);
		animationCompo.addAnimation(StatesEnum.STANDING, AnimationSingleton.getInstance().shinobiStand);
		animationCompo.addAnimation(StatesEnum.MOVING, AnimationSingleton.getInstance().shinobiRun);
		animationCompo.addAnimation(StatesEnum.SHINOBI_ATTACKING, AnimationSingleton.getInstance().shinobiAttack);
		animationCompo.addAnimation(StatesEnum.SHINOBI_SLEEPING, AnimationSingleton.getInstance().shinobiSleep);
		animationCompo.addAnimation(StatesEnum.SHINOBI_THROWING, AnimationSingleton.getInstance().shinobiThrow);
		animationCompo.addAnimation(StatesEnum.SHINOBI_CLONING, AnimationSingleton.getInstance().shinobiClone);
		enemyEntity.add(animationCompo);
		
		StateComponent stateCompo = engine.createComponent(StateComponent.class);
		stateCompo.set(StatesEnum.SHINOBI_SLEEPING );
		enemyEntity.add(stateCompo);
		
		HumanoidComponent humanoidCompo = engine.createComponent(HumanoidComponent.class);
		enemyEntity.add(humanoidCompo);
		
		AIComponent aiComponent = engine.createComponent(AIComponent.class);
		aiComponent.room = room;
		EnemyShinobi enemyShinobi = new EnemyShinobi();
		if (clone) {
			enemyShinobi.setSmokeBombUsed(true);
			enemyShinobi.setKawarimiActivated(true);
		}
		aiComponent.setType(enemyShinobi);
		aiComponent.setSubSystem(clone ? null : new ShinobiSubSystem());
		aiComponent.setBasicMoveStrategy(AIMoveStrategy.MOVE_RANDOMLY_BUT_ATTACK_IF_POSSIBLE);
		aiComponent.setAlertedMoveStrategy(AIMoveStrategy.MOVE_TOWARDS_TARGET);
		enemyEntity.add(aiComponent);
		
		
		EnemyComponent enemyComponent = engine.createComponent(EnemyComponent.class);
		enemyComponent.setFaction(EnemyFactionEnum.SHINOBI);
		enemyEntity.add(enemyComponent);
		
		
		MoveComponent moveComponent = engine.createComponent(MoveComponent.class);
		moveComponent.room = room;
		moveComponent.setMoveSpeed(5);
		enemyEntity.add(moveComponent);
				
		AttackComponent attackComponent = engine.createComponent(AttackComponent.class);
		attackComponent.room = room;
		
		AttackSkill meleeSkill = new AttackSkill();
		meleeSkill.setName("Katana slash");
		meleeSkill.setRangeMax(1);
		meleeSkill.setStrength(8);
		meleeSkill.setAttackType(AttackTypeEnum.MELEE);
		AttackAnimation attackAnimation = new AttackAnimation(
				AnimationSingleton.getInstance().attack_slash, true);
		meleeSkill.setAttackAnimation(attackAnimation);
		attackComponent.getSkills().add(meleeSkill);
		
		AttackSkill rangeSkill = new AttackSkill();
		rangeSkill.setName("Shuriken throw");
		rangeSkill.setRangeMin(2);
		rangeSkill.setRangeMax(5);
		rangeSkill.setStrength(5);
		rangeSkill.setAttackType(AttackTypeEnum.RANGE);
		AttackAnimation rangeAttackAnimation = new AttackAnimation(
				AnimationSingleton.getInstance().shuriken_projectile, false);
		rangeSkill.setAttackAnimation(rangeAttackAnimation);
		attackComponent.getSkills().add(rangeSkill);
		
		
		AttackSkill throwSkill = new AttackSkill();
		throwSkill.setName("Smokebomb throw");
		throwSkill.setActive(false);
		throwSkill.setRangeMin(2);
		throwSkill.setRangeMax(5);
		throwSkill.setStrength(0);
		throwSkill.setAttackType(AttackTypeEnum.THROW);
		AttackAnimation throwAttackAnimation = new AttackAnimation(null, null, false);
		throwSkill.setAttackAnimation(throwAttackAnimation);
		attackComponent.getSkills().add(throwSkill);
		
		enemyEntity.add(attackComponent);
		
		SolidComponent solidComponent = engine.createComponent(SolidComponent.class);
		enemyEntity.add(solidComponent);
		
		HealthComponent healthComponent = engine.createComponent(HealthComponent.class);
		healthComponent.room = room;
		healthComponent.setMaxHp(25);
		healthComponent.setHp(25);
		enemyEntity.add(healthComponent);
		
		if (!clone) {
			ExpRewardComponent expRewardCompo = engine.createComponent(ExpRewardComponent.class);
			expRewardCompo.setExpGain(30);
			enemyEntity.add(expRewardCompo);
		}
		
		if (!clone) {
			LootRewardComponent lootRewardCompo = engine.createComponent(LootRewardComponent.class);
			lootRewardCompo.setItemPool(ItemPoolSingleton.getInstance().shinobi);
			DropRate dropRate = new DropRate();
			dropRate.add(ItemPoolRarity.COMMON, 100f);
			lootRewardCompo.setDropRate(dropRate);
			lootRewardCompo.setDropSeededRandom(RandomSingleton.getInstance().getNextSeededRandom());
			enemyEntity.add(lootRewardCompo);
		}
		
		StatusReceiverComponent statusReceiverCompo = engine.createComponent(StatusReceiverComponent.class);
		enemyEntity.add(statusReceiverCompo);
		
		room.addEnemy(enemyEntity);
		
		return enemyEntity;
	}
	
	
	/**
	 * Create an orangutan mini boss.
	 * @param pos the position
	 * @return the enemy entity
	 */
	public Entity createOrangutanAlpha(Room room, Vector2 pos) {
		Entity enemyEntity = createEnemyBase(room, pos, EntityFlagEnum.ENEMY_ORANGUTAN_ALPHA, null);
		
		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.ENEMY_ORANGUTAN_ALPHA_TITLE);
		inspect.setDescription(Descriptions.ENEMY_ORANGUTAN_ALPHA_DESCRIPTION);
		inspect.setBigPopup(true);
		enemyEntity.add(inspect);
		
		AnimationComponent animationCompo = engine.createComponent(AnimationComponent.class);
		animationCompo.addAnimation(StatesEnum.STANDING, AnimationSingleton.getInstance().orangutanAlphaStand);
		animationCompo.addAnimation(StatesEnum.MOVING, AnimationSingleton.getInstance().orangutanAlphaStand);
		enemyEntity.add(animationCompo);
		
		StateComponent stateCompo = engine.createComponent(StateComponent.class);
		stateCompo.set(StatesEnum.STANDING );
		enemyEntity.add(stateCompo);
		
		HumanoidComponent humanoidCompo = engine.createComponent(HumanoidComponent.class);
		enemyEntity.add(humanoidCompo);
		
		AIComponent aiComponent = engine.createComponent(AIComponent.class);
		aiComponent.room = room;
		EnemyOrangutan enemyOrangutan = new EnemyOrangutan(RandomSingleton.getInstance().getNextSeededRandom());
		aiComponent.setType(enemyOrangutan);
		aiComponent.setSubSystem(new OrangutanSubSystem());
		aiComponent.setBasicMoveStrategy(AIMoveStrategy.MOVE_RANDOMLY_BUT_ATTACK_IF_POSSIBLE);
		aiComponent.setAlertedMoveStrategy(AIMoveStrategy.MOVE_TOWARDS_TARGET);
		enemyEntity.add(aiComponent);
		
		
		EnemyComponent enemyComponent = engine.createComponent(EnemyComponent.class);
		enemyComponent.setFaction(EnemyFactionEnum.ORANGUTANS);
		enemyEntity.add(enemyComponent);
		
		
		MoveComponent moveComponent = engine.createComponent(MoveComponent.class);
		moveComponent.room = room;
		moveComponent.setMoveSpeed(5);
		enemyEntity.add(moveComponent);
				
		AttackComponent attackComponent = engine.createComponent(AttackComponent.class);
		attackComponent.room = room;
		
		AttackSkill meleeSkill = new AttackSkill();
		meleeSkill.setName("Smash");
		meleeSkill.setRangeMax(1);
		meleeSkill.setStrength(12);
		meleeSkill.setAttackType(AttackTypeEnum.MELEE);
		AttackAnimation attackAnimation = new AttackAnimation(
				AnimationSingleton.getInstance().attack_slash, true);
		meleeSkill.setAttackAnimation(attackAnimation);
		attackComponent.getSkills().add(meleeSkill);
//		
//		AttackSkill rangeSkill = new AttackSkill();
//		rangeSkill.setName("Shuriken throw");
//		rangeSkill.setRangeMin(2);
//		rangeSkill.setRangeMax(5);
//		rangeSkill.setStrength(5);
//		rangeSkill.setAttackType(AttackTypeEnum.RANGE);
//		AttackAnimation rangeAttackAnimation = new AttackAnimation(
//				AnimationSingleton.getInstance().shuriken_projectile, false);
//		rangeSkill.setAttackAnimation(rangeAttackAnimation);
//		attackComponent.getSkills().add(rangeSkill);
//		
//		
//		AttackSkill throwSkill = new AttackSkill();
//		throwSkill.setName("Smokebomb throw");
//		throwSkill.setActive(false);
//		throwSkill.setRangeMin(2);
//		throwSkill.setRangeMax(5);
//		throwSkill.setStrength(0);
//		throwSkill.setAttackType(AttackTypeEnum.THROW);
//		AttackAnimation throwAttackAnimation = new AttackAnimation(null, null, false);
//		throwSkill.setAttackAnimation(throwAttackAnimation);
//		attackComponent.getSkills().add(throwSkill);
		
		enemyEntity.add(attackComponent);
		
		SolidComponent solidComponent = engine.createComponent(SolidComponent.class);
		enemyEntity.add(solidComponent);
		
		HealthComponent healthComponent = engine.createComponent(HealthComponent.class);
		healthComponent.room = room;
		healthComponent.setMaxHp(35);
		healthComponent.setHp(35);
		enemyEntity.add(healthComponent);
		
		ExpRewardComponent expRewardCompo = engine.createComponent(ExpRewardComponent.class);
		expRewardCompo.setExpGain(30);
		enemyEntity.add(expRewardCompo);
		
		LootRewardComponent lootRewardCompo = engine.createComponent(LootRewardComponent.class);
		lootRewardCompo.setItemPool(ItemPoolSingleton.getInstance().orangutan);
		DropRate dropRate = new DropRate();
		dropRate.add(ItemPoolRarity.COMMON, 100f);
		lootRewardCompo.setDropRate(dropRate);
		lootRewardCompo.setDropSeededRandom(RandomSingleton.getInstance().getNextSeededRandom());
		enemyEntity.add(lootRewardCompo);
	
		StatusReceiverComponent statusReceiverCompo = engine.createComponent(StatusReceiverComponent.class);
		enemyEntity.add(statusReceiverCompo);
		
		// Creep immunity
		CreepImmunityComponent creepImmunityCompo = engine.createComponent(CreepImmunityComponent.class);
		creepImmunityCompo.getTypes().add(CreepType.BANANA);
		enemyEntity.add(creepImmunityCompo);
		
		room.addEnemy(enemyEntity);
		
		return enemyEntity;
	}
	
	/**
	 * Create an orangutan mini boss.
	 * @param pos the position
	 * @return the enemy entity
	 */
	public Entity createOrangutan(Room room, Vector2 pos) {
		Entity enemyEntity = createEnemyBase(room, pos, EntityFlagEnum.ENEMY_ORANGUTAN, null);
		
		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.ENEMY_ORANGUTAN_TITLE);
		inspect.setDescription(Descriptions.ENEMY_ORANGUTAN_DESCRIPTION);
		inspect.setBigPopup(true);
		enemyEntity.add(inspect);
		
		AnimationComponent animationCompo = engine.createComponent(AnimationComponent.class);
		animationCompo.addAnimation(StatesEnum.STANDING, AnimationSingleton.getInstance().orangutanStand);
		animationCompo.addAnimation(StatesEnum.MOVING, AnimationSingleton.getInstance().orangutanStand);
		enemyEntity.add(animationCompo);
		
		StateComponent stateCompo = engine.createComponent(StateComponent.class);
		stateCompo.set(StatesEnum.STANDING);
		enemyEntity.add(stateCompo);
		
		HumanoidComponent humanoidCompo = engine.createComponent(HumanoidComponent.class);
		enemyEntity.add(humanoidCompo);
		
		AIComponent aiComponent = engine.createComponent(AIComponent.class);
		aiComponent.room = room;
		aiComponent.setType(new EnemySmallOrangutan());
		aiComponent.setBasicMoveStrategy(AIMoveStrategy.MOVE_RANDOMLY_BUT_ATTACK_IF_POSSIBLE);
		aiComponent.setAlertedMoveStrategy(AIMoveStrategy.MOVE_TOWARDS_TARGET);
		enemyEntity.add(aiComponent);
		
		EnemyComponent enemyComponent = engine.createComponent(EnemyComponent.class);
		enemyComponent.setFaction(EnemyFactionEnum.ORANGUTANS);
		enemyEntity.add(enemyComponent);
		
		MoveComponent moveComponent = engine.createComponent(MoveComponent.class);
		moveComponent.room = room;
		moveComponent.setMoveSpeed(5);
		enemyEntity.add(moveComponent);
				
		AttackComponent attackComponent = engine.createComponent(AttackComponent.class);
		attackComponent.room = room;
		
		AttackSkill meleeSkill = new AttackSkill();
		meleeSkill.setName("Punch");
		meleeSkill.setRangeMax(1);
		meleeSkill.setStrength(6);
		meleeSkill.setAttackType(AttackTypeEnum.MELEE);
		AttackAnimation attackAnimation = new AttackAnimation(
				AnimationSingleton.getInstance().attack_slash, true);
		meleeSkill.setAttackAnimation(attackAnimation);
		attackComponent.getSkills().add(meleeSkill);
		enemyEntity.add(attackComponent);
		
		SolidComponent solidComponent = engine.createComponent(SolidComponent.class);
		enemyEntity.add(solidComponent);
		
		HealthComponent healthComponent = engine.createComponent(HealthComponent.class);
		healthComponent.room = room;
		healthComponent.setMaxHp(11);
		healthComponent.setHp(11);
		enemyEntity.add(healthComponent);
		
		ExpRewardComponent expRewardCompo = engine.createComponent(ExpRewardComponent.class);
		expRewardCompo.setExpGain(4);
		enemyEntity.add(expRewardCompo);
		
		LootRewardComponent lootRewardCompo = engine.createComponent(LootRewardComponent.class);
		lootRewardCompo.setItemPool(ItemPoolSingleton.getInstance().smallOrangutan);
		DropRate dropRate = new DropRate();
//		dropRate.add(ItemPoolRarity.RARE, 100f);
		dropRate.add(ItemPoolRarity.COMMON, 100f);
		lootRewardCompo.setDropRate(dropRate);
		lootRewardCompo.setDropSeededRandom(RandomSingleton.getInstance().getNextSeededRandom());
		enemyEntity.add(lootRewardCompo);
	
		StatusReceiverComponent statusReceiverCompo = engine.createComponent(StatusReceiverComponent.class);
		enemyEntity.add(statusReceiverCompo);
		
		// Creep immunity
		CreepImmunityComponent creepImmunityCompo = engine.createComponent(CreepImmunityComponent.class);
		creepImmunityCompo.getTypes().add(CreepType.BANANA);
		enemyEntity.add(creepImmunityCompo);
		
		room.addEnemy(enemyEntity);
		
		return enemyEntity;
	}

}
