/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.factory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.AnimationSingleton;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.ai.movements.AttackTypeEnum;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.BlessingOfCalishka;
import com.dokkaebistudio.tacticaljourney.alterations.pools.GodessStatueAlterationPool;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.BlockVisibilityComponent;
import com.dokkaebistudio.tacticaljourney.components.DestructibleComponent;
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
import com.dokkaebistudio.tacticaljourney.components.neutrals.ShopKeeperComponent;
import com.dokkaebistudio.tacticaljourney.components.neutrals.SoulbenderComponent;
import com.dokkaebistudio.tacticaljourney.components.neutrals.StatueComponent;
import com.dokkaebistudio.tacticaljourney.components.orbs.OrbCarrierComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AlterationReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AlterationReceiverComponent.AlterationActionEnum;
import com.dokkaebistudio.tacticaljourney.components.player.AmmoCarrierComponent;
import com.dokkaebistudio.tacticaljourney.components.player.ExperienceComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.components.player.WalletComponent;
import com.dokkaebistudio.tacticaljourney.constants.ZIndexConstants;
import com.dokkaebistudio.tacticaljourney.enums.InventoryDisplayModeEnum;
import com.dokkaebistudio.tacticaljourney.enums.StatesEnum;
import com.dokkaebistudio.tacticaljourney.items.pools.ItemPoolSingleton;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.skills.SkillEnum;
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
	public Entity createPlayer(Vector2 pos, int moveSpeed, Room room) {
		Entity playerEntity = engine.createEntity();
		playerEntity.flags = EntityFlagEnum.PLAYER.getFlag();
		
		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle("The Adventurer");
		inspect.setDescription("You, apparently.");
		playerEntity.add(inspect);
		
		// Player anim
		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		playerEntity.add(spriteCompo);
		
		StateComponent stateCompo = engine.createComponent(StateComponent.class);
		stateCompo.set(StatesEnum.PLAYER_STANDING.getState() );
		playerEntity.add(stateCompo);
		
		AnimationComponent animCompo = engine.createComponent(AnimationComponent.class);
		animCompo.addAnimation(StatesEnum.PLAYER_STANDING.getState(), AnimationSingleton.getInstance().player_standing);
		animCompo.addAnimation(StatesEnum.PLAYER_RUNNING.getState(), AnimationSingleton.getInstance().player_running);
		animCompo.addAnimation(StatesEnum.PLAYER_FLYING.getState(), AnimationSingleton.getInstance().player_flying);
		playerEntity.add(animCompo);
		
		// Grid position
		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		gridPosition.coord(playerEntity, pos, room);
		gridPosition.zIndex = ZIndexConstants.PLAYER;
		playerEntity.add(gridPosition);
		
		// Player compo
		PlayerComponent playerComponent = engine.createComponent(PlayerComponent.class);
		playerEntity.add(playerComponent);
		
		// Humanoid
		HumanoidComponent humanoidCompo = engine.createComponent(HumanoidComponent.class);
		playerEntity.add(humanoidCompo);
		
		// Move compo
		MoveComponent moveComponent = engine.createComponent(MoveComponent.class);
		moveComponent.room = room;
		moveComponent.setMoveSpeed(moveSpeed);
		playerEntity.add(moveComponent);
		
		// Attack compo
		AttackComponent attackComponent = engine.createComponent(AttackComponent.class);
		attackComponent.room = room;
		attackComponent.setRangeMax(1);
		attackComponent.setStrength(5);
		attackComponent.setAccuracy(1);
		attackComponent.setAttackType(AttackTypeEnum.MELEE);
		
		AttackAnimation attackAnimation = new AttackAnimation(
				AnimationSingleton.getInstance().attack_slash,
				AnimationSingleton.getInstance().attack_slash_critical, true);
		attackComponent.setAttackAnimation(attackAnimation);
		
		playerEntity.add(attackComponent);
		
		InventoryComponent inventoryComponent = engine.createComponent(InventoryComponent.class);
		inventoryComponent.init();
		inventoryComponent.player = playerEntity;
		inventoryComponent.setNumberOfSlots(8);
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
		healthComponent.room = room;
		healthComponent.setMaxHp(50);
		healthComponent.setHp(50);
		healthComponent.setMaxArmor(30);
		healthComponent.setArmor(0);
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
		
//		statusReceiverCompo.requestAction(StatusActionEnum.RECEIVE_STATUS, new StatusBuffRegen(50));

		// TEST
//		FlyComponent flyCompo = engine.createComponent(FlyComponent.class);
//		playerEntity.add(flyCompo);
		
		//Skills
		entityFactory.createSkill(room,playerEntity, SkillEnum.SLASH, 1 );
		entityFactory.createSkill(room,playerEntity, SkillEnum.BOW, 2);
		entityFactory.createSkill(room,playerEntity, SkillEnum.BOMB, 3);
		entityFactory.createSkill(room,playerEntity, SkillEnum.THROW, 4);

		engine.addEntity(playerEntity);
		return playerEntity;
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
		attackComponent.setRangeMax(1);
		attackComponent.setStrength(10);
		attackComponent.setAttackType(AttackTypeEnum.MELEE);
		shopKeeperEntity.add(attackComponent);
		
		// Shop keeper component
		ShopKeeperComponent shopKeeperCompo = engine.createComponent(ShopKeeperComponent.class);
		shopKeeperCompo.setItemPool(ItemPoolSingleton.getInstance().basicShopItemPool);
		shopKeeperCompo.stock(room);
		shopKeeperCompo.addSpeech("Hey!\nI'm the shop keeper.");
		shopKeeperCompo.addSpeech("It's good to see a new face around here!");
		shopKeeperCompo.addSpeech("We cut and slice, it makes us feel so very nice.");
		shopKeeperCompo.addSpeech("I like it in here, all brighty and cosy.");
		shopKeeperCompo.addSpeech("I can restock my shop if you want to, given that you can afford it.");
		
		shopKeeperEntity.add(shopKeeperCompo);
		
		room.addNeutral(shopKeeperEntity);
		return shopKeeperEntity;
	}
	
	/**
	 * Create a godess statue.
	 * @param pos the position
	 * @param room the room
	 * @return the statue entity
	 */
	public Entity createGodessStatue(Vector2 pos, Room room) {
		Entity godessStatueEntity = engine.createEntity();
		godessStatueEntity.flags = EntityFlagEnum.GODESS_STATUE.getFlag();

		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle("Godess Statue");
		inspect.setDescription("A statue of a godess.");
		godessStatueEntity.add(inspect);
		
		// Sprite
		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		spriteCompo.setSprite(Assets.godess_statue);
		godessStatueEntity.add(spriteCompo);
		
		// Grid position
		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		gridPosition.coord(godessStatueEntity, pos, room);
		gridPosition.zIndex = ZIndexConstants.STATUE;
		godessStatueEntity.add(gridPosition);
		
		StatueComponent statueComponent = engine.createComponent(StatueComponent.class);
		statueComponent.setAlterationPool(new GodessStatueAlterationPool());
		godessStatueEntity.add(statueComponent);
		
		// Solid compo
		SolidComponent solidComponent = engine.createComponent(SolidComponent.class);
		godessStatueEntity.add(solidComponent);
		
		BlockVisibilityComponent blockVisibilityCompo = engine.createComponent(BlockVisibilityComponent.class);
		godessStatueEntity.add(blockVisibilityCompo);

		
		DestructibleComponent destructible = engine.createComponent(DestructibleComponent.class);
		destructible.setDestroyedTexture(Assets.godess_statue_broken);
		destructible.setRemove(false);
		godessStatueEntity.add(destructible);		
		
		room.addNeutral(godessStatueEntity);
		return godessStatueEntity;
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
		soulbenderCompo.addSpeech("Hello there ! I'm a soul bender.");
		soulbenderCompo.addSpeech("I can infuse items' auras into your soul.");
		soulbenderCompo.addSpeech("This allows you keeping the blessings provided by items permanently, and free an inventory slot.");
		soulbenderCompo.addSpeech("The bad side is that it also keeps the curses provided by the item.");
		soulbenderCompo.addSpeech("And also it's not free.");
		
		soulBenderEntity.add(soulbenderCompo);
		
		room.addNeutral(soulBenderEntity);
		return soulBenderEntity;
	}
	
}
