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
import com.dokkaebistudio.tacticaljourney.alterations.blessings.BlessingOfCalishka;
import com.dokkaebistudio.tacticaljourney.alterations.pools.GoddessStatueAlterationPool;
import com.dokkaebistudio.tacticaljourney.ces.components.AIComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.BlockVisibilityComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.DestructibleComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.GravityComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.HumanoidComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.InspectableComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.SolidComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.SpeakerComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.TutorialComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.attack.AttackComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.attack.AttackSkill;
import com.dokkaebistudio.tacticaljourney.ces.components.creep.CreepImmunityComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.display.AnimationComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.display.StateComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.loot.DropRate;
import com.dokkaebistudio.tacticaljourney.ces.components.loot.LootRewardComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.loot.DropRate.ItemPoolRarity;
import com.dokkaebistudio.tacticaljourney.ces.components.neutrals.CalishkaComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.neutrals.ChaliceComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.neutrals.SewingMachineComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.neutrals.ShopKeeperComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.neutrals.SoulbenderComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.neutrals.StatueComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.neutrals.ChaliceComponent.ChaliceType;
import com.dokkaebistudio.tacticaljourney.ces.components.orbs.OrbCarrierComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.AllyComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.AlterationReceiverComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.AmmoCarrierComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.ExperienceComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.ParentEntityComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.WalletComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.AlterationReceiverComponent.AlterationActionEnum;
import com.dokkaebistudio.tacticaljourney.constants.ZIndexConstants;
import com.dokkaebistudio.tacticaljourney.creature.allies.AllyClone;
import com.dokkaebistudio.tacticaljourney.creature.enemies.enums.AIMoveStrategy;
import com.dokkaebistudio.tacticaljourney.dialog.AbstractDialogs;
import com.dokkaebistudio.tacticaljourney.dialog.pnjs.CalishkaDialogs;
import com.dokkaebistudio.tacticaljourney.dialog.pnjs.ShopkeeperDialogs;
import com.dokkaebistudio.tacticaljourney.dialog.pnjs.SoulbenderDialogs;
import com.dokkaebistudio.tacticaljourney.enums.InventoryDisplayModeEnum;
import com.dokkaebistudio.tacticaljourney.enums.StatesEnum;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.items.pools.ItemPoolSingleton;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.singletons.AnimationSingleton;
import com.dokkaebistudio.tacticaljourney.skills.SkillEnum;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.vfx.AttackAnimation;

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
	

	/**
	 * Constructor.
	 * @param e the engine
	 */
	public PlayerFactory(PooledEngine e, EntityFactory ef) {
		this.engine = e;
		this.entityFactory = ef;
	}
	

	/**
	 * Create the player.
	 * @param pos the position
	 * @param moveSpeed the speed
	 * @return the player entity
	 */
	public Entity createPlayer(String name) {
		Entity playerEntity = engine.createEntity();
		playerEntity.flags = EntityFlagEnum.PLAYER.getFlag();
		
		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(name != null ? name : "The adventurer");
		inspect.setDescription("You, apparently.");
		playerEntity.add(inspect);
		
		// Player anim
		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		playerEntity.add(spriteCompo);
		
		StateComponent stateCompo = engine.createComponent(StateComponent.class);
		stateCompo.set(StatesEnum.STANDING);
		playerEntity.add(stateCompo);
		
		AnimationComponent animCompo = engine.createComponent(AnimationComponent.class);
		animCompo.addAnimation(StatesEnum.STANDING, AnimationSingleton.getInstance().player_standing);
		animCompo.addAnimation(StatesEnum.MOVING, AnimationSingleton.getInstance().player_running);
		animCompo.addAnimation(StatesEnum.FLY_STANDING, AnimationSingleton.getInstance().player_flying);
		animCompo.addAnimation(StatesEnum.FLY_MOVING, AnimationSingleton.getInstance().player_flying);
		playerEntity.add(animCompo);
		
		// Grid position
		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		gridPosition.zIndex = ZIndexConstants.PLAYER;
		playerEntity.add(gridPosition);
		
		AllyComponent allyComponent = engine.createComponent(AllyComponent.class);
		allyComponent.removeMarker();
		playerEntity.add(allyComponent);
		
		// Player compo
		PlayerComponent playerComponent = engine.createComponent(PlayerComponent.class);
		playerComponent.setKarma(0);
		playerEntity.add(playerComponent);
		
		// Humanoid
		HumanoidComponent humanoidCompo = engine.createComponent(HumanoidComponent.class);
		playerEntity.add(humanoidCompo);
		
		// Move compo
		MoveComponent moveComponent = engine.createComponent(MoveComponent.class);
		moveComponent.setMoveSpeed(5);
		moveComponent.playerMovementCompo = true;
		playerEntity.add(moveComponent);
		
		// Attack compo
		AttackComponent attackComponent = engine.createComponent(AttackComponent.class);
		
		AttackSkill as = new AttackSkill();
		as.setRangeMax(1);
		as.setStrength(5);
		as.setAttackType(AttackTypeEnum.MELEE);
		AttackAnimation attackAnimation = new AttackAnimation(
				AnimationSingleton.getInstance().attack_slash,
				AnimationSingleton.getInstance().attack_slash_critical, true);
		as.setAttackAnimation(attackAnimation);
		
		attackComponent.getSkills().add(as);
		attackComponent.setAccuracy(1);
		playerEntity.add(attackComponent);
		
		InventoryComponent inventoryComponent = engine.createComponent(InventoryComponent.class);
		inventoryComponent.init();
		inventoryComponent.player = playerEntity;
		inventoryComponent.setNumberOfSlots(10);
		inventoryComponent.setDisplayMode(InventoryDisplayModeEnum.NONE);
		
		//TEST
//		Entity firePotion = this.entityFactory.itemFactory.createItemFirePotion(null, null);
//		inventoryComponent.store(firePotion, Mappers.itemComponent.get(firePotion), room);
		
		playerEntity.add(inventoryComponent);
		
		// Ammo carrier
		AmmoCarrierComponent ammoCarrierCompo = engine.createComponent(AmmoCarrierComponent.class);
		ammoCarrierCompo.setArrows(0);
		ammoCarrierCompo.setMaxArrows(10);
		ammoCarrierCompo.setBombs(0);
		ammoCarrierCompo.setMaxBombs(5);
		playerEntity.add(ammoCarrierCompo);
		
		// Money carrier
		WalletComponent walletCompo = engine.createComponent(WalletComponent.class);
		walletCompo.setAmount(0);
		playerEntity.add(walletCompo);
		
		// Solid compo
		SolidComponent solidComponent = engine.createComponent(SolidComponent.class);
		playerEntity.add(solidComponent);
		
		// Health compo
		HealthComponent healthComponent = engine.createComponent(HealthComponent.class);
		healthComponent.setMaxHp(50);
		healthComponent.setHp(50);
		healthComponent.setMaxArmor(30);
		healthComponent.setArmor(0);
		healthComponent.removeHpDisplayer();
		playerEntity.add(healthComponent);
		
		// Experience compo
		ExperienceComponent expCompo = engine.createComponent(ExperienceComponent.class);
		expCompo.reset();
		playerEntity.add(expCompo);
		
		// Alteration receiver compo
		AlterationReceiverComponent alterationReceiverCompo = engine.createComponent(AlterationReceiverComponent.class);
		BlessingOfCalishka initialBlessing = new BlessingOfCalishka();
		initialBlessing.setInfused(true);
		alterationReceiverCompo.requestAction(AlterationActionEnum.RECEIVE_BLESSING, initialBlessing);
		playerEntity.add(alterationReceiverCompo);
		
		// Statuses
		StatusReceiverComponent statusReceiverCompo = engine.createComponent(StatusReceiverComponent.class);
		statusReceiverCompo.removeStatusTable();
		playerEntity.add(statusReceiverCompo);
		
		// Orb carrier
		OrbCarrierComponent orbCarrierCompo = engine.createComponent(OrbCarrierComponent.class);
		playerEntity.add(orbCarrierCompo);
		
		// Creep immunity
		CreepImmunityComponent creepImmunityCompo = engine.createComponent(CreepImmunityComponent.class);
		playerEntity.add(creepImmunityCompo);
		
		GravityComponent gravityCompo = engine.createComponent(GravityComponent.class);
		playerEntity.add(gravityCompo);
		
//		statusReceiverCompo.requestAction(StatusActionEnum.RECEIVE_STATUS, new StatusBuffRegen(50));

		// TEST
//		FlyComponent flyCompo = engine.createComponent(FlyComponent.class);
//		playerEntity.add(flyCompo);
		
		//Skills
		entityFactory.createSkill(playerEntity, SkillEnum.SLASH, 1 );
		entityFactory.createSkill(playerEntity, SkillEnum.BOW, 2);
		entityFactory.createSkill(playerEntity, SkillEnum.BOMB, 3);
		entityFactory.createSkill(playerEntity, SkillEnum.THROW, 4);

		engine.addEntity(playerEntity);
		return playerEntity;
	}
	
	/**
	 * Create a clone of the player.
	 * @param pos the position
	 * @return the clone entity
	 */
	public Entity createPlayerClone(Room room, Vector2 position, Entity parent) {
		Entity cloneEntity = engine.createEntity();
		cloneEntity.flags = EntityFlagEnum.ALLY_CLONE.getFlag();
		
		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle("Clone");
		inspect.setDescription("A clone of yourselft.");
		cloneEntity.add(inspect);
		
		// Player anim
		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		cloneEntity.add(spriteCompo);
		
		StateComponent stateCompo = engine.createComponent(StateComponent.class);
		stateCompo.set(StatesEnum.STANDING);
		cloneEntity.add(stateCompo);
		
		AnimationComponent animCompo = engine.createComponent(AnimationComponent.class);
		animCompo.addAnimation(StatesEnum.STANDING, AnimationSingleton.getInstance().player_standing);
		animCompo.addAnimation(StatesEnum.MOVING, AnimationSingleton.getInstance().player_running);
		animCompo.addAnimation(StatesEnum.FLY_STANDING, AnimationSingleton.getInstance().player_flying);
		animCompo.addAnimation(StatesEnum.FLY_MOVING, AnimationSingleton.getInstance().player_flying);
		cloneEntity.add(animCompo);
		
		// Grid position
		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		gridPosition.zIndex = ZIndexConstants.PLAYER;
		gridPosition.coord(cloneEntity, position, room);
		cloneEntity.add(gridPosition);
		
		AllyComponent allyComponent = engine.createComponent(AllyComponent.class);
		cloneEntity.add(allyComponent);
		
		AIComponent aiComponent = engine.createComponent(AIComponent.class);
		aiComponent.room = room;
		aiComponent.setType(new AllyClone());
		aiComponent.setBasicMoveStrategy(AIMoveStrategy.MOVE_RANDOMLY_BUT_ATTACK_IF_POSSIBLE);
		aiComponent.setAlertedMoveStrategy(AIMoveStrategy.MOVE_TOWARDS_TARGET);
		cloneEntity.add(aiComponent);
		
		// Humanoid
		HumanoidComponent humanoidCompo = engine.createComponent(HumanoidComponent.class);
		cloneEntity.add(humanoidCompo);
		
		// Move compo
		MoveComponent moveComponent = engine.createComponent(MoveComponent.class);
		moveComponent.room = room;
		moveComponent.setMoveSpeed(Mappers.moveComponent.get(GameScreen.player).getMoveSpeed());
		cloneEntity.add(moveComponent);
		
		// Attack compo
		AttackComponent attackComponent = engine.createComponent(AttackComponent.class);
		attackComponent.room = room;
		
		AttackSkill as = new AttackSkill();
		as.setRangeMax(Mappers.attackComponent.get(GameScreen.player).getRangeMax());
		as.setStrength(Mappers.attackComponent.get(GameScreen.player).getStrength());
		as.setAttackType(AttackTypeEnum.MELEE);
		AttackAnimation attackAnimation = new AttackAnimation(
				AnimationSingleton.getInstance().attack_slash,
				AnimationSingleton.getInstance().attack_slash_critical, true);
		as.setAttackAnimation(attackAnimation);
		
		attackComponent.getSkills().add(as);
		attackComponent.setAccuracy(1);
		cloneEntity.add(attackComponent);
		
		// Ammo carrier
		AmmoCarrierComponent ammoCarrierCompo = engine.createComponent(AmmoCarrierComponent.class);
		ammoCarrierCompo.setArrows(0);
		ammoCarrierCompo.setMaxArrows(10);
		ammoCarrierCompo.setBombs(0);
		ammoCarrierCompo.setMaxBombs(5);
		cloneEntity.add(ammoCarrierCompo);
		
		// Solid compo
		SolidComponent solidComponent = engine.createComponent(SolidComponent.class);
		cloneEntity.add(solidComponent);
		
		// Health compo
		HealthComponent healthComponent = engine.createComponent(HealthComponent.class);
		healthComponent.room = room;
		healthComponent.setMaxHp(10);
		healthComponent.setHp(10);
		healthComponent.setMaxArmor(0);
		healthComponent.setArmor(0);
		cloneEntity.add(healthComponent);
		
		// Statuses
		StatusReceiverComponent statusReceiverCompo = engine.createComponent(StatusReceiverComponent.class);
		cloneEntity.add(statusReceiverCompo);
		
		// Orb carrier
		OrbCarrierComponent orbCarrierCompo = engine.createComponent(OrbCarrierComponent.class);
		cloneEntity.add(orbCarrierCompo);
		
		GravityComponent gravityCompo = engine.createComponent(GravityComponent.class);
		cloneEntity.add(gravityCompo);
		
		if (parent != null) {
			ParentEntityComponent parentCompo = engine.createComponent(ParentEntityComponent.class);
			parentCompo.setParent(parent);
			cloneEntity.add(parentCompo);
		}
		
		room.addAlly(cloneEntity);
		return cloneEntity;
	}
	
	
	public Entity createCalishka(Vector2 pos, Room room, AbstractDialogs dialogs) {
		return createCalishka(pos, room, dialogs, null);
	}
	
	public Entity createCalishka(Vector2 pos, Room room, AbstractDialogs dialogs, Integer tutorialNumber) {
		Entity calishkaEntity = engine.createEntity();
		calishkaEntity.flags = EntityFlagEnum.CALISHKA.getFlag();

		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.CALISHKA_TITLE);
		inspect.setDescription(Descriptions.CALISHKA_DESCRIPTION);
		calishkaEntity.add(inspect);

		// Sprite
		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		spriteCompo.setSprite(Assets.calishka);
		calishkaEntity.add(spriteCompo);
		
		// Grid position
		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		gridPosition.coord(calishkaEntity, pos, room);
		gridPosition.zIndex = ZIndexConstants.PLAYER;
		calishkaEntity.add(gridPosition);
		
		// Solid compo
		SolidComponent solidComponent = engine.createComponent(SolidComponent.class);
		calishkaEntity.add(solidComponent);
		
		CalishkaComponent calishkaCompo = engine.createComponent(CalishkaComponent.class);
		calishkaEntity.add(calishkaCompo);
		
		SpeakerComponent speakerCompo = engine.createComponent(SpeakerComponent.class);
		speakerCompo.setDialogs(dialogs != null ? dialogs : new CalishkaDialogs());
		calishkaEntity.add(speakerCompo);
		
		if (tutorialNumber != null) {
			TutorialComponent tutoCompo = engine.createComponent(TutorialComponent.class);
			tutoCompo.setTutorialNumber(tutorialNumber);
			calishkaEntity.add(tutoCompo);
		}
		
		room.addNeutral(calishkaEntity);
		return calishkaEntity;
	}
	
	/**
	 * Create a shopkeeper.
	 * @param pos the position
	 * @param room the room
	 * @return the player entity
	 */
	public Entity createShopkeeper(Vector2 pos, Room room) {
		Entity shopKeeperEntity = engine.createEntity();
		shopKeeperEntity.flags = EntityFlagEnum.SHOPKEEPER.getFlag();

		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.SHOPKEEPER_TITLE);
		inspect.setDescription(Descriptions.SHOPKEEPER_DESCRIPTION);
		shopKeeperEntity.add(inspect);

		// Sprite
		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		spriteCompo.setSprite(Assets.shopkeeper);
		shopKeeperEntity.add(spriteCompo);
		
		// Grid position
		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		gridPosition.coord(shopKeeperEntity, pos, room);
		gridPosition.zIndex = ZIndexConstants.PLAYER;
		shopKeeperEntity.add(gridPosition);
		
		// Solid compo
		SolidComponent solidComponent = engine.createComponent(SolidComponent.class);
		shopKeeperEntity.add(solidComponent);
		
//		// Health compo
//		HealthComponent healthComponent = engine.createComponent(HealthComponent.class);
//		healthComponent.room = room;
//		healthComponent.setMaxHp(100);
//		healthComponent.setHp(100);
//		shopKeeperEntity.add(healthComponent);
		
		// Attack compo
		AttackComponent attackComponent = engine.createComponent(AttackComponent.class);
		attackComponent.room = room;
		
		AttackSkill as = new AttackSkill();
		as.setRangeMax(1);
		as.setStrength(10);
		as.setAttackType(AttackTypeEnum.MELEE);
		AttackAnimation attackAnimation = new AttackAnimation(
				AnimationSingleton.getInstance().attack_slash,
				AnimationSingleton.getInstance().attack_slash_critical, true);
		as.setAttackAnimation(attackAnimation);
		attackComponent.getSkills().add(as);
		shopKeeperEntity.add(attackComponent);
		
		// Shop keeper component
		ShopKeeperComponent shopKeeperCompo = engine.createComponent(ShopKeeperComponent.class);
		shopKeeperCompo.setNumberOfItems(5);
		shopKeeperCompo.setItemPool(ItemPoolSingleton.getInstance().basicShopItemPool);
		DropRate dropRate = new DropRate();
		dropRate.add(ItemPoolRarity.RARE, 10f);
		dropRate.add(ItemPoolRarity.COMMON, 90f);
		shopKeeperCompo.setDropRate(dropRate);
		shopKeeperCompo.setDropSeededRandom(RandomSingleton.getInstance().getNextSeededRandom());		
		shopKeeperEntity.add(shopKeeperCompo);
		
		SpeakerComponent speakerCompo = engine.createComponent(SpeakerComponent.class);
		speakerCompo.setDialogs(new ShopkeeperDialogs());
		shopKeeperEntity.add(speakerCompo);
		
		GravityComponent gravityCompo = engine.createComponent(GravityComponent.class);
		shopKeeperEntity.add(gravityCompo);
		
		room.addNeutral(shopKeeperEntity);
		return shopKeeperEntity;
	}
	
	/**
	 * Create a goddess statue.
	 * @param pos the position
	 * @param room the room
	 * @param needsTwoExplosions whether the statue needs two explosions to drop it's loot or only one
	 * @return the statue entity
	 */
	public Entity createGoddessStatue(Vector2 pos, Room room, boolean needsTwoExplosions) {
		Entity goddessStatueEntity = engine.createEntity();
		goddessStatueEntity.flags = EntityFlagEnum.GODDESS_STATUE.getFlag();

		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle("Goddess Statue");
		inspect.setDescription("A statue of a goddess Huminodun.");
		goddessStatueEntity.add(inspect);
		
		// Sprite
		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		spriteCompo.setSprite(Assets.goddess_statue);
		goddessStatueEntity.add(spriteCompo);
		
		// Grid position
		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		gridPosition.coord(goddessStatueEntity, pos, room);
		gridPosition.zIndex = ZIndexConstants.STATUE;
		goddessStatueEntity.add(gridPosition);
		
		StatueComponent statueComponent = engine.createComponent(StatueComponent.class);
		statueComponent.setAlterationPool(new GoddessStatueAlterationPool());
		goddessStatueEntity.add(statueComponent);
		
		// Solid compo
		SolidComponent solidComponent = engine.createComponent(SolidComponent.class);
		goddessStatueEntity.add(solidComponent);
		
		BlockVisibilityComponent blockVisibilityCompo = engine.createComponent(BlockVisibilityComponent.class);
		goddessStatueEntity.add(blockVisibilityCompo);

		
		DestructibleComponent destructible = engine.createComponent(DestructibleComponent.class);
		if (needsTwoExplosions) {
			destructible.setDestroyedTexture(Assets.goddess_statue_broken);
			destructible.setRemove(false);
		}
		goddessStatueEntity.add(destructible);		
		
		LootRewardComponent lootRewardCompo = engine.createComponent(LootRewardComponent.class);
		lootRewardCompo.setItemPool(ItemPoolSingleton.getInstance().statue);
		DropRate dropRate = new DropRate();
		dropRate.add(ItemPoolRarity.RARE, 98f);
		dropRate.add(ItemPoolRarity.COMMON, 2f);
		lootRewardCompo.setDropRate(dropRate);
		lootRewardCompo.setDropSeededRandom(RandomSingleton.getInstance().getNextSeededRandom());
		goddessStatueEntity.add(lootRewardCompo);
		
		GravityComponent gravityCompo = engine.createComponent(GravityComponent.class);
		goddessStatueEntity.add(gravityCompo);
		
		room.addNeutral(goddessStatueEntity);
		return goddessStatueEntity;
	}
	
	/**
	 * Create a chalice on an altar.
	 * @param pos the position
	 * @param room the room
	 * @return the statue entity
	 */
	public Entity createChalice(Vector2 pos, Room room, ChaliceType type) {
		Entity chaliceEntity = engine.createEntity();
		chaliceEntity.flags = EntityFlagEnum.ALTAR.getFlag();

		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle("Chalice");
		inspect.setDescription("A chalice filled with an exotic concoction.");
		chaliceEntity.add(inspect);
		
		// Sprite
		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		spriteCompo.setSprite(Assets.altar);
		chaliceEntity.add(spriteCompo);
		
		// Grid position
		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		gridPosition.coord(chaliceEntity, pos, room);
		gridPosition.zIndex = ZIndexConstants.STATUE;
		chaliceEntity.add(gridPosition);
		
		// Solid compo
		SolidComponent solidComponent = engine.createComponent(SolidComponent.class);
		chaliceEntity.add(solidComponent);
		
		DestructibleComponent destructible = engine.createComponent(DestructibleComponent.class);
		destructible.setDestroyedTexture(Assets.wall_destroyed);
		chaliceEntity.add(destructible);		
		
		ChaliceComponent chaliceComponent = engine.createComponent(ChaliceComponent.class);
		chaliceComponent.setType(type);
		chaliceComponent.setFilled(true);
		chaliceEntity.add(chaliceComponent);
		
		GravityComponent gravityCompo = engine.createComponent(GravityComponent.class);
		chaliceEntity.add(gravityCompo);
		
		room.addNeutral(chaliceEntity);
		return chaliceEntity;
	}
	

	/**
	 * Create a soul bender.
	 * @param pos the position
	 * @param room the room
	 * @return the player entity
	 */
	public Entity createSoulbender(Vector2 pos, Room room) {
		Entity soulBenderEntity = engine.createEntity();
		soulBenderEntity.flags = EntityFlagEnum.SOUL_BENDER.getFlag();

		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.SOULBENDER_TITLE);
		inspect.setDescription(Descriptions.SOULBENDER_DESCRIPTION);
		soulBenderEntity.add(inspect);

		// Sprite
		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		spriteCompo.setSprite(Assets.soulbender);
		soulBenderEntity.add(spriteCompo);
		
		// Grid position
		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		gridPosition.coord(soulBenderEntity, pos, room);
		gridPosition.zIndex = ZIndexConstants.PLAYER;
		soulBenderEntity.add(gridPosition);
		
		// Solid compo
		SolidComponent solidComponent = engine.createComponent(SolidComponent.class);
		soulBenderEntity.add(solidComponent);
		
		// Shop keeper component
		SoulbenderComponent soulbenderCompo = engine.createComponent(SoulbenderComponent.class);
		soulbenderCompo.setPrice(10);
		soulBenderEntity.add(soulbenderCompo);
		
		SpeakerComponent speakerCompo = engine.createComponent(SpeakerComponent.class);
		speakerCompo.setDialogs(new SoulbenderDialogs());
		soulBenderEntity.add(speakerCompo);
		
		GravityComponent gravityCompo = engine.createComponent(GravityComponent.class);
		soulBenderEntity.add(gravityCompo);
		
		room.addNeutral(soulBenderEntity);
		return soulBenderEntity;
	}
	
	
	/**
	 * Create a sewing machine.
	 * @param pos the position
	 * @param room the room
	 * @return the entity
	 */
	public Entity createSewingMachine(Vector2 pos, Room room) {
		Entity sewingMachineEntity = engine.createEntity();
		sewingMachineEntity.flags = EntityFlagEnum.ALTAR.getFlag();

		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle("Sewing Machine");
		inspect.setDescription("A vintage sewing machine. It looks like it should still work, and there is even some thread remaining.");
		sewingMachineEntity.add(inspect);
		
		// Sprite
		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		spriteCompo.setSprite(Assets.sewing_machine);
		sewingMachineEntity.add(spriteCompo);
		
		// Grid position
		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		gridPosition.coord(sewingMachineEntity, pos, room);
		gridPosition.zIndex = ZIndexConstants.STATUE;
		sewingMachineEntity.add(gridPosition);
		
		// Solid compo
		SolidComponent solidComponent = engine.createComponent(SolidComponent.class);
		sewingMachineEntity.add(solidComponent);
			
		SewingMachineComponent sewingMachineComponent = engine.createComponent(SewingMachineComponent.class);
		sewingMachineEntity.add(sewingMachineComponent);
		
		GravityComponent gravityCompo = engine.createComponent(GravityComponent.class);
		sewingMachineEntity.add(gravityCompo);
		
		room.addNeutral(sewingMachineEntity);
		return sewingMachineEntity;
	}
}
