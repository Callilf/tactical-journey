/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.factory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.BlockVisibilityComponent;
import com.dokkaebistudio.tacticaljourney.components.DestructibleComponent;
import com.dokkaebistudio.tacticaljourney.components.FlammableComponent;
import com.dokkaebistudio.tacticaljourney.components.InspectableComponent;
import com.dokkaebistudio.tacticaljourney.components.creep.CreepComponent;
import com.dokkaebistudio.tacticaljourney.components.creep.CreepComponent.CreepReleasedTurnEnum;
import com.dokkaebistudio.tacticaljourney.components.display.AnimationComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.display.StateComponent;
import com.dokkaebistudio.tacticaljourney.components.loot.DropRate;
import com.dokkaebistudio.tacticaljourney.components.loot.DropRate.ItemPoolRarity;
import com.dokkaebistudio.tacticaljourney.components.loot.LootRewardComponent;
import com.dokkaebistudio.tacticaljourney.components.player.ParentEntityComponent;
import com.dokkaebistudio.tacticaljourney.constants.ZIndexConstants;
import com.dokkaebistudio.tacticaljourney.creeps.CreepBush;
import com.dokkaebistudio.tacticaljourney.creeps.CreepFire;
import com.dokkaebistudio.tacticaljourney.creeps.CreepMud;
import com.dokkaebistudio.tacticaljourney.creeps.CreepPoison;
import com.dokkaebistudio.tacticaljourney.creeps.CreepVinesBush;
import com.dokkaebistudio.tacticaljourney.creeps.CreepWeb;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.enums.StatesEnum;
import com.dokkaebistudio.tacticaljourney.items.pools.ItemPoolSingleton;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.singletons.AnimationSingleton;

/**
 * Factory used to create presets of entities.
 * @author Callil
 *
 */
public final class CreepFactory {
	
	/** The gdx pooled engine. */
	public PooledEngine engine;
	
	/** the entity factory. */
	public EntityFactory entityFactory;
	
	/**
	 * Constructor.
	 * @param e the engine
	 */
	public CreepFactory(PooledEngine e, EntityFactory ef) {
		this.engine = e;
		this.entityFactory = ef;
	}
	
	
	/**
	 * Create a spider web that slows down and alert all spiders.
	 * @param room the room
	 * @param pos the position
	 * @return the creep entity
	 */
	private Entity createCreepBase(Room room, Vector2 pos, EntityFlagEnum flag, RegionDescriptor texture) {
		Entity creepEntity = engine.createEntity();
		creepEntity.flags = flag.getFlag();

		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		if (texture != null) spriteCompo.setSprite(texture);
		creepEntity.add(spriteCompo);

		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		gridPosition.coord(creepEntity, pos, room);
		gridPosition.zIndex = ZIndexConstants.CREEP;
		creepEntity.add(gridPosition);
		
		return creepEntity;
	}

	/**
	 * Create a spider web that slows down and alert all spiders.
	 * @param room the room
	 * @param pos the position
	 * @return the creep entity
	 */
	public Entity createWeb(Room room, Vector2 pos) {
		Entity creepEntity = createCreepBase(room, pos, EntityFlagEnum.CREEP_WEB, Assets.creep_web);
				
		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.WEB_TITLE);
		inspect.setDescription(Descriptions.WEB_DESCRIPTION);
		creepEntity.add(inspect);
		
		DestructibleComponent destructible = engine.createComponent(DestructibleComponent.class);
		destructible.setDestroyableWithWeapon(true);
		creepEntity.add(destructible);
		
		CreepComponent creepCompo = engine.createComponent(CreepComponent.class);
		creepCompo.setType(new CreepWeb());
		creepCompo.setDuration(3);
		creepCompo.setReleasedTurn(CreepReleasedTurnEnum.getReleaseTurn(room.getState()));
		creepEntity.add(creepCompo);
		
		FlammableComponent flammable = engine.createComponent(FlammableComponent.class);
		flammable.setPropagate(true);
		flammable.setDestroy(true);
		flammable.setDestroyedTexture(Assets.creep_web);
		creepEntity.add(flammable);
		
		room.addEntity(creepEntity);		
		creepCompo.onAppear(creepEntity, room);
		return creepEntity;
	}
	
	
	
	public Entity createMud(Room room, Vector2 pos) {
		Entity creepEntity = createCreepBase(room, pos, EntityFlagEnum.CREEP_MUD, Assets.mud);
    	
		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.MUD_TITLE);
		inspect.setDescription(Descriptions.MUD_DESCRIPTION);
		creepEntity.add(inspect);
		
    	DestructibleComponent destructibleCompo = engine.createComponent(DestructibleComponent.class);
    	destructibleCompo.setDestroyedTexture(Assets.mud_destroyed);
		creepEntity.add(destructibleCompo);
		
		CreepComponent creepCompo = engine.createComponent(CreepComponent.class);
		creepCompo.setType(new CreepMud());
		creepCompo.setDuration(0);
		creepEntity.add(creepCompo);
    	
		engine.addEntity(creepEntity);
		creepCompo.onAppear(creepEntity, room);
    	return creepEntity;
	}
	
	
	public Entity createBush(Room room, Vector2 pos) {
		Entity bushEntity = createCreepBase(room, pos, EntityFlagEnum.CREEP_BUSH, Assets.tallGrass);

		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.BUSH_TITLE);
		inspect.setDescription(Descriptions.BUSH_DESCRIPTION);
		bushEntity.add(inspect);
    	    	
    	BlockVisibilityComponent blockVisibilityComponent = engine.createComponent(BlockVisibilityComponent.class);
    	bushEntity.add(blockVisibilityComponent);
    	    	
    	DestructibleComponent destructibleCompo = engine.createComponent(DestructibleComponent.class);
    	destructibleCompo.setDestroyedTexture(Assets.tallGrass_destroyed);
    	destructibleCompo.setDestroyableWithWeapon(true);
    	bushEntity.add(destructibleCompo);	
    	
		CreepComponent creepCompo = engine.createComponent(CreepComponent.class);
		creepCompo.setType(new CreepBush());
		creepCompo.setDuration(0);
		bushEntity.add(creepCompo);
		
		FlammableComponent flammable = engine.createComponent(FlammableComponent.class);
		flammable.setPropagate(true);
		flammable.setDestroy(true);
		flammable.setDestroyedTexture(Assets.tallGrass);
		bushEntity.add(flammable);
		
		LootRewardComponent lootRewardCompo = engine.createComponent(LootRewardComponent.class);
		lootRewardCompo.setItemPool(ItemPoolSingleton.getInstance().bush);
		DropRate dropRate = new DropRate();
		dropRate.add(ItemPoolRarity.RARE, 0.3f);
//		dropRate.add(ItemPoolRarity.COMMON, 0);
		lootRewardCompo.setDropRate(dropRate);
		lootRewardCompo.setDropSeededRandom(RandomSingleton.getInstance().getNextSeededRandom());
		bushEntity.add(lootRewardCompo);
    	
		engine.addEntity(bushEntity);

    	return bushEntity;
	}	
	
	public Entity createVinesBush(Room room, Vector2 pos) {
		Entity bushEntity = createCreepBase(room, pos, EntityFlagEnum.CREEP_VINES_BUSH, Assets.vineGrass);

		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.BUSH_TITLE);
		inspect.setDescription(Descriptions.BUSH_DESCRIPTION);
		bushEntity.add(inspect);
    	    	
    	BlockVisibilityComponent blockVisibilityComponent = engine.createComponent(BlockVisibilityComponent.class);
    	bushEntity.add(blockVisibilityComponent);
    	    	
    	DestructibleComponent destructibleCompo = engine.createComponent(DestructibleComponent.class);
    	destructibleCompo.setDestroyedTexture(Assets.tallGrass_destroyed);
    	destructibleCompo.setDestroyableWithWeapon(true);
    	bushEntity.add(destructibleCompo);	
    	
		CreepComponent creepCompo = engine.createComponent(CreepComponent.class);
		creepCompo.setType(new CreepVinesBush());
		creepCompo.setDuration(0);
		bushEntity.add(creepCompo);
		
		FlammableComponent flammable = engine.createComponent(FlammableComponent.class);
		flammable.setPropagate(true);
		flammable.setDestroy(true);
		flammable.setDestroyedTexture(Assets.tallGrass);
		bushEntity.add(flammable);
		
		LootRewardComponent lootRewardCompo = engine.createComponent(LootRewardComponent.class);
		lootRewardCompo.setItemPool(ItemPoolSingleton.getInstance().vineBush);
		DropRate dropRate = new DropRate();
		dropRate.add(ItemPoolRarity.RARE, 1f);
//		dropRate.add(ItemPoolRarity.COMMON, 0);
		lootRewardCompo.setDropRate(dropRate);
		lootRewardCompo.setDropSeededRandom(RandomSingleton.getInstance().getNextSeededRandom());
		bushEntity.add(lootRewardCompo);
    	
		engine.addEntity(bushEntity);

    	return bushEntity;
	}	
	
	
	public Entity createFire(Room room, Vector2 pos, Entity parentEntity) {
		Entity creepEntity = createCreepBase(room, pos, EntityFlagEnum.CREEP_FIRE, null);
    			
		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.FIRE_TITLE);
		inspect.setDescription(Descriptions.FIRE_DESCRIPTION);
		creepEntity.add(inspect);
		
		CreepComponent creepCompo = engine.createComponent(CreepComponent.class);
		creepCompo.setType(new CreepFire());
		creepCompo.setDuration(3);
		creepCompo.setReleasedTurn(CreepReleasedTurnEnum.getReleaseTurn(room.getState()));
		creepEntity.add(creepCompo);
		
		AnimationComponent animationCompo = engine.createComponent(AnimationComponent.class);
		animationCompo.addAnimation(StatesEnum.FIRE_LOOP, AnimationSingleton.getInstance().fire);
		creepEntity.add(animationCompo);
		
		StateComponent stateCompo = engine.createComponent(StateComponent.class);
		stateCompo.set(StatesEnum.FIRE_LOOP);
		creepEntity.add(stateCompo);
		
		if (parentEntity != null) {
			ParentEntityComponent parentCompo = engine.createComponent(ParentEntityComponent.class);
			parentCompo.setParent(parentEntity);
			creepEntity.add(parentCompo);
		}
    	
		engine.addEntity(creepEntity);
		creepCompo.onAppear(creepEntity, room);
    	return creepEntity;
	}
	
	/**
	 * Create a poison puddle.
	 * @param room the room
	 * @param pos the position
	 * @param duration the duration of the creep
	 * @return the creep entity
	 */
	public Entity createPoison(Room room, Vector2 pos, Entity parentEntity) {
		Entity creepEntity = createCreepBase(room, pos, EntityFlagEnum.CREEP_POISON, Assets.creep_poison);
				
		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.POISON_TITLE);
		inspect.setDescription(Descriptions.POISON_DESCRIPTION);
		creepEntity.add(inspect);
		
		DestructibleComponent destructible = engine.createComponent(DestructibleComponent.class);
		creepEntity.add(destructible);
		
		CreepComponent creepCompo = engine.createComponent(CreepComponent.class);
		creepCompo.setType(new CreepPoison());
		creepCompo.setDuration(5);
		creepCompo.setReleasedTurn(CreepReleasedTurnEnum.getReleaseTurn(room.getState()));
		creepEntity.add(creepCompo);
		
		if (parentEntity != null) {
			ParentEntityComponent parentCompo = engine.createComponent(ParentEntityComponent.class);
			parentCompo.setParent(parentEntity);
			creepEntity.add(parentCompo);
		}
		
		room.addEntity(creepEntity);		
		creepCompo.onAppear(creepEntity, room);
		return creepEntity;
	}
	
}
