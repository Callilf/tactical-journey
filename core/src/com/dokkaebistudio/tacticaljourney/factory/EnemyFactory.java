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
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.components.ExpRewardComponent;
import com.dokkaebistudio.tacticaljourney.components.FlyComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.HumanoidComponent;
import com.dokkaebistudio.tacticaljourney.components.InspectableComponent;
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
import com.dokkaebistudio.tacticaljourney.enemies.EnemyScorpion;
import com.dokkaebistudio.tacticaljourney.enemies.EnemyShinobi;
import com.dokkaebistudio.tacticaljourney.enemies.EnemyStinger;
import com.dokkaebistudio.tacticaljourney.enemies.enums.EnemyFactionEnum;
import com.dokkaebistudio.tacticaljourney.enemies.enums.EnemyMoveStrategy;
import com.dokkaebistudio.tacticaljourney.enums.StatesEnum;
import com.dokkaebistudio.tacticaljourney.factory.enemies.EnemyPangolinFactory;
import com.dokkaebistudio.tacticaljourney.factory.enemies.EnemySpiderFactory;
import com.dokkaebistudio.tacticaljourney.factory.enemies.EnemyTribesmenFactory;
import com.dokkaebistudio.tacticaljourney.items.pools.ItemPoolSingleton;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.singletons.AnimationSingleton;
import com.dokkaebistudio.tacticaljourney.systems.enemies.ShinobiSubSystem;
import com.dokkaebistudio.tacticaljourney.systems.enemies.StingerSubSystem;
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
	

	/**
	 * Create a spider.
	 * @param pos the position
	 * @return the enemy entity
	 */
	public Entity createSpider(Room room, Vector2 pos) {
		return spiderFactory.createSpider(room, pos);
	}
	
	
	/**
	 * Create a spider web that can spit web on the player to alert other spiders
	 * and leaves web creep on the floor when moving.
	 * @param pos the position
	 * @return the enemy entity
	 */
	public Entity createSpiderWeb(Room room, Vector2 pos) {
		return spiderFactory.createSpiderWeb(room, pos);
	}
	
	/**
	 * Create a venom spider
	 * and leaves web creep on the floor when moving.
	 * @param pos the position
	 * @return the enemy entity
	 */
	public Entity createVenomSpider(Room room, Vector2 pos) {
		return spiderFactory.createVenomSpider(room, pos);
	}
	
	
	/**
	 * Create a scorpion.
	 * @param pos the position
	 * @return the enemy entity
	 */
	public Entity createScorpion(Room room, Vector2 pos) {
		Entity enemyEntity = engine.createEntity();
		enemyEntity.flags = EntityFlagEnum.ENEMY_SCORPION.getFlag();
		
		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.ENEMY_SCORPION_TITLE);
		inspect.setDescription(Descriptions.ENEMY_SCORPION_DESCRIPTION);
		inspect.setBigPopup(true);
		enemyEntity.add(inspect);

		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		spriteCompo.setSprite(Assets.enemy_scorpion);
		enemyEntity.add(spriteCompo);

		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		gridPosition.coord(enemyEntity, pos, room);
		gridPosition.zIndex = ZIndexConstants.ENEMY;
		enemyEntity.add(gridPosition);
		
		EnemyComponent enemyComponent = engine.createComponent(EnemyComponent.class);
		enemyComponent.room = room;
		enemyComponent.setType(new EnemyScorpion());
		enemyComponent.setFaction(EnemyFactionEnum.SOLITARY);
		enemyComponent.setBasicMoveStrategy(EnemyMoveStrategy.MOVE_RANDOMLY_BUT_ATTACK_IF_POSSIBLE);
		enemyComponent.setAlertedMoveStrategy(EnemyMoveStrategy.MOVE_TOWARD_PLAYER);
		Entity alertedDisplayer = this.entityFactory.createTextOnTile(pos, "", ZIndexConstants.HEALTH_DISPLAYER, room);
		enemyComponent.setAlertedDisplayer(alertedDisplayer);
		enemyComponent.setAlerted(true);
		enemyEntity.add(enemyComponent);
		
		MoveComponent moveComponent = engine.createComponent(MoveComponent.class);
		moveComponent.room = room;
		moveComponent.setMoveSpeed(4);
		enemyEntity.add(moveComponent);
		
		AttackComponent attackComponent = engine.createComponent(AttackComponent.class);
		attackComponent.room = room;
		attackComponent.setAttackType(AttackTypeEnum.MELEE);
		attackComponent.setRangeMax(1);
		attackComponent.setStrength(10);
		AttackAnimation attackAnimation = new AttackAnimation(AnimationSingleton.getInstance().attack_slash,  true);
		attackComponent.setAttackAnimation(attackAnimation);
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
		lootRewardCompo.setItemPool(ItemPoolSingleton.getInstance().scorpion);
		DropRate dropRate = new DropRate();
		dropRate.add(ItemPoolRarity.RARE, 20);
		dropRate.add(ItemPoolRarity.COMMON, 50);
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
		Entity enemyEntity = engine.createEntity();
		enemyEntity.flags = EntityFlagEnum.ENEMY_STINGER.getFlag();
		
		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.ENEMY_STINGER_TITLE);
		inspect.setDescription(Descriptions.ENEMY_STINGER_DESCRIPTION);
		inspect.setBigPopup(true);
		enemyEntity.add(inspect);
		
		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		enemyEntity.add(spriteCompo);

		AnimationComponent animationCompo = engine.createComponent(AnimationComponent.class);
		animationCompo.addAnimation(StatesEnum.FLY_STANDING, AnimationSingleton.getInstance().stingerFly);
		animationCompo.addAnimation(StatesEnum.FLY_MOVING, AnimationSingleton.getInstance().stingerFly);
		animationCompo.addAnimation(StatesEnum.STINGER_ATTACK, AnimationSingleton.getInstance().stingerAttack);
		enemyEntity.add(animationCompo);
		
		StateComponent stateCompo = engine.createComponent(StateComponent.class);
		stateCompo.set(StatesEnum.FLY_STANDING);
		enemyEntity.add(stateCompo);

		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		gridPosition.coord(enemyEntity, pos, room);
		gridPosition.zIndex = ZIndexConstants.ENEMY;
		enemyEntity.add(gridPosition);
		
		
//		Persister p = new Persister(engine);
//		EnemyComponent enemyComponent = p.loadStinger();
//		enemyComponent.room = room;
		
		EnemyComponent enemyComponent = engine.createComponent(EnemyComponent.class);
		enemyComponent.room = room;
		enemyComponent.setType(new EnemyStinger());
		enemyComponent.setSubSystem(new StingerSubSystem());
		enemyComponent.setFaction(EnemyFactionEnum.SOLITARY);
		enemyComponent.setBasicMoveStrategy(EnemyMoveStrategy.MOVE_RANDOMLY_BUT_ATTACK_IF_POSSIBLE);
		enemyComponent.setAlertedMoveStrategy(EnemyMoveStrategy.MOVE_TOWARD_PLAYER);
		Entity alertedDisplayer = this.entityFactory.createTextOnTile(pos, "", ZIndexConstants.HEALTH_DISPLAYER, room);
		enemyComponent.setAlertedDisplayer(alertedDisplayer);
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
		attackComponent.setAttackType(AttackTypeEnum.MELEE);
		attackComponent.setRangeMax(1);
		attackComponent.setStrength(6);
		AttackAnimation attackAnimation = new AttackAnimation(AnimationSingleton.getInstance().attack_slash,  true);
		attackComponent.setAttackAnimation(attackAnimation);
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
		lootRewardCompo.setItemPool(ItemPoolSingleton.getInstance().stinger);
		DropRate dropRate = new DropRate();
		dropRate.add(ItemPoolRarity.RARE, 20);
		dropRate.add(ItemPoolRarity.COMMON, 30 );
		lootRewardCompo.setDropRate(dropRate);
		lootRewardCompo.setDropSeededRandom(RandomSingleton.getInstance().getNextSeededRandom());
		enemyEntity.add(lootRewardCompo);
		
		StatusReceiverComponent statusReceiverCompo = engine.createComponent(StatusReceiverComponent.class);
		enemyEntity.add(statusReceiverCompo);
		
		room.addEnemy(enemyEntity);
		
		return enemyEntity;
	}
	
	
	/**
	 * Create a shinobi.
	 * @param pos the position
	 * @return the enemy entity
	 */
	public Entity createShinobi(Room room, Vector2 pos) {
		Entity enemyEntity = engine.createEntity();
		enemyEntity.flags = EntityFlagEnum.ENEMY_SHINOBI.getFlag();
		
		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.ENEMY_SHINOBI_TITLE);
		inspect.setDescription(Descriptions.ENEMY_SHINOBI_DESCRIPTION);
		inspect.setBigPopup(true);
		enemyEntity.add(inspect);
		
		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		enemyEntity.add(spriteCompo);

		AnimationComponent animationCompo = engine.createComponent(AnimationComponent.class);
		animationCompo.addAnimation(StatesEnum.STANDING, AnimationSingleton.getInstance().shinobiStand);
		animationCompo.addAnimation(StatesEnum.MOVING, AnimationSingleton.getInstance().shinobiRun);
		animationCompo.addAnimation(StatesEnum.SHINOBI_SLEEPING, AnimationSingleton.getInstance().shinobiSleep);
		enemyEntity.add(animationCompo);
		
		StateComponent stateCompo = engine.createComponent(StateComponent.class);
		stateCompo.set(StatesEnum.SHINOBI_SLEEPING );
		enemyEntity.add(stateCompo);

		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		gridPosition.coord(enemyEntity, pos, room);
		gridPosition.zIndex = ZIndexConstants.ENEMY;
		enemyEntity.add(gridPosition);
		
		HumanoidComponent humanoidCompo = engine.createComponent(HumanoidComponent.class);
		enemyEntity.add(humanoidCompo);

		EnemyComponent enemyComponent = engine.createComponent(EnemyComponent.class);
		enemyComponent.room = room;
		enemyComponent.setType(new EnemyShinobi());
		enemyComponent.setSubSystem(new ShinobiSubSystem());
		enemyComponent.setFaction(EnemyFactionEnum.SOLITARY);
		enemyComponent.setBasicMoveStrategy(EnemyMoveStrategy.MOVE_RANDOMLY_BUT_ATTACK_IF_POSSIBLE);
		enemyComponent.setAlertedMoveStrategy(EnemyMoveStrategy.MOVE_TOWARD_PLAYER);
		Entity alertedDisplayer = this.entityFactory.createTextOnTile(pos, "", ZIndexConstants.HEALTH_DISPLAYER, room);
		enemyComponent.setAlertedDisplayer(alertedDisplayer);
		enemyEntity.add(enemyComponent);
		
//		Persister p = new Persister(engine);
//		p.save(enemyComponent);
		
		MoveComponent moveComponent = engine.createComponent(MoveComponent.class);
		moveComponent.room = room;
		moveComponent.setMoveSpeed(5);
		enemyEntity.add(moveComponent);
				
		AttackComponent attackComponent = engine.createComponent(AttackComponent.class);
		attackComponent.room = room;
		attackComponent.setAttackType(AttackTypeEnum.RANGE);
		attackComponent.setRangeMax(5);
		attackComponent.setStrength(5);
		AttackAnimation attackAnimation = new AttackAnimation(AnimationSingleton.getInstance().shuriken_projectile,  false);
		attackComponent.setAttackAnimation(attackAnimation);
		enemyEntity.add(attackComponent);
		
		SolidComponent solidComponent = engine.createComponent(SolidComponent.class);
		enemyEntity.add(solidComponent);
		
		HealthComponent healthComponent = engine.createComponent(HealthComponent.class);
		healthComponent.room = room;
		healthComponent.setMaxHp(22);
		healthComponent.setHp(22);
		healthComponent.setHpDisplayer(this.entityFactory.createTextOnTile(pos, String.valueOf(healthComponent.getHp()), ZIndexConstants.HEALTH_DISPLAYER, room));
		enemyEntity.add(healthComponent);
		
		ExpRewardComponent expRewardCompo = engine.createComponent(ExpRewardComponent.class);
		expRewardCompo.setExpGain(30);
		enemyEntity.add(expRewardCompo);
		
		LootRewardComponent lootRewardCompo = engine.createComponent(LootRewardComponent.class);
		lootRewardCompo.setItemPool(ItemPoolSingleton.getInstance().stinger);
		DropRate dropRate = new DropRate();
		dropRate.add(ItemPoolRarity.RARE, 20);
		dropRate.add(ItemPoolRarity.COMMON, 30 );
		lootRewardCompo.setDropRate(dropRate);
		lootRewardCompo.setDropSeededRandom(RandomSingleton.getInstance().getNextSeededRandom());
		enemyEntity.add(lootRewardCompo);
		
		StatusReceiverComponent statusReceiverCompo = engine.createComponent(StatusReceiverComponent.class);
		enemyEntity.add(statusReceiverCompo);
		
		room.addEnemy(enemyEntity);
		
		return enemyEntity;
	}
	
	
	/**
	 * Create a baby pangolin.
	 * @param pos the position
	 * @return the enemy entity
	 */
	public Entity createPangolinBaby(Room room, Vector2 pos, Entity mother) {
		return pangolinFactory.createPangolinBaby(room, pos, mother);
	}
	
	/**
	 * Create a mother pangolin.
	 * @param pos the position
	 * @return the enemy entity
	 */
	public Entity createPangolinMother(Room room, Vector2 pos) {
		return pangolinFactory.createPangolinMother(room, pos);
	}
	
	
	
	//**********************
	// Tribesmen
	
	public Entity createTribesmenSpear(Room room, Vector2 pos) {
		return tribesmenFactory.createSpearman(room, pos);
	}
	
	public Entity createTribesmenShield(Room room, Vector2 pos) {
		return tribesmenFactory.createShieldHolder(room, pos);
	}
	
	public Entity createTribesmenScout(Room room, Vector2 pos) {
		return tribesmenFactory.createScout(room, pos);
	}
	
	public Entity createTribesmenShaman(Room room, Vector2 pos) {
		return tribesmenFactory.createShaman(room, pos);
	}
	
	public Entity createTribesmenTotem(Room room, Vector2 pos) {
		return tribesmenFactory.createTotem(room, pos);
	}
}
