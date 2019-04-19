/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.factory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.InspectableComponent;
import com.dokkaebistudio.tacticaljourney.components.display.AnimationComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.display.StateComponent;
import com.dokkaebistudio.tacticaljourney.components.orbs.OrbComponent;
import com.dokkaebistudio.tacticaljourney.constants.ZIndexConstants;
import com.dokkaebistudio.tacticaljourney.orbs.OrbDeath;
import com.dokkaebistudio.tacticaljourney.orbs.OrbEnergy;
import com.dokkaebistudio.tacticaljourney.orbs.OrbFire;
import com.dokkaebistudio.tacticaljourney.orbs.OrbPoison;
import com.dokkaebistudio.tacticaljourney.orbs.OrbVegetal;
import com.dokkaebistudio.tacticaljourney.orbs.OrbVoid;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.singletons.AnimationSingleton;

/**
 * Factory used to create orbs.
 * @author Callil
 *
 */
public final class OrbFactory {
	
	/** The gdx pooled engine. */
	public PooledEngine engine;
	
	/** the entity factory. */
	public EntityFactory entityFactory;
	
	/**
	 * Constructor.
	 * @param e the engine
	 */
	public OrbFactory(PooledEngine e, EntityFactory ef) {
		this.engine = e;
		this.entityFactory = ef;
	}
	
	
	/**
	 * Create an orb.
	 * @param room the room
	 * @param pos the position
	 * @return the orb entity
	 */
	private Entity createOrbBase(Room room, Vector2 pos, EntityFlagEnum flag, Animation<Sprite> anim) {
		Entity orb = engine.createEntity();
		orb.flags = flag.getFlag();

		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		orb.add(spriteCompo);
		
		AnimationComponent animCompo = engine.createComponent(AnimationComponent.class);
		animCompo.addAnimation(null, anim);
		orb.add(animCompo);
		StateComponent stateComponent = engine.createComponent(StateComponent.class);
		stateComponent.set(null);
		orb.add(stateComponent);
		
		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		if (pos != null) {
			gridPosition.coord(orb, pos, room);
		}
		
		int nextInt = RandomSingleton.getInstance().getUnseededRandom().nextInt(11);
		float speedModifier = (float)nextInt / 1000;
		gridPosition.setOrbitSpeed(0.005f + speedModifier);
		gridPosition.setOrbitRadius(10);
		gridPosition.zIndex = ZIndexConstants.ORB;
		orb.add(gridPosition);
				
		return orb;
	}

	/**
	 * Create a an energy orb that deals damage on contact.
	 * @param pos the position
	 * @param room the room
	 * @return the orb entity
	 */
	public Entity createEnergyOrb(Vector2 pos, Room room) {
		Entity orb = createOrbBase(room, pos, EntityFlagEnum.ENERGY_ORB, AnimationSingleton.getInstance().energyOrb);
		
		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.ORB_ENERGY_TITLE);
		inspect.setDescription(Descriptions.ORB_ENERGY_DESCRIPTION);
		orb.add(inspect);
		
		OrbComponent orbCompo = engine.createComponent(OrbComponent.class);
		orbCompo.setType(new OrbEnergy());
		orb.add(orbCompo);
		
		room.addEntity(orb);
		
		return orb;
	}
	
	public Entity createVegetalOrb(Vector2 pos, Room room) {
		Entity orb = createOrbBase(room, pos, EntityFlagEnum.VEGETAL_ORB, AnimationSingleton.getInstance().vegetalOrb);
		
		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.ORB_VEGETAL_TITLE);
		inspect.setDescription(Descriptions.ORB_VEGETAL_DESCRIPTION);
		orb.add(inspect);
		
		OrbComponent orbCompo = engine.createComponent(OrbComponent.class);
		orbCompo.setType(new OrbVegetal());
		orb.add(orbCompo);
		
		room.addEntity(orb);
		
		return orb;
	}

	public Entity createPoisonOrb(Vector2 pos, Room room) {
		Entity orb = createOrbBase(room, pos, EntityFlagEnum.POISON_ORB, AnimationSingleton.getInstance().poisonOrb);
		
		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.ORB_POISON_TITLE);
		inspect.setDescription(Descriptions.ORB_POISON_DESCRIPTION);
		orb.add(inspect);
		
		OrbComponent orbCompo = engine.createComponent(OrbComponent.class);
		orbCompo.setType(new OrbPoison());
		orb.add(orbCompo);
		
		room.addEntity(orb);
		
		return orb;
	}
	
	public Entity createFireOrb(Vector2 pos, Room room) {
		Entity orb = createOrbBase(room, pos, EntityFlagEnum.FIRE_ORB, AnimationSingleton.getInstance().fireOrb);
		
		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.ORB_FIRE_TITLE);
		inspect.setDescription(Descriptions.ORB_FIRE_DESCRIPTION);
		orb.add(inspect);
		
		OrbComponent orbCompo = engine.createComponent(OrbComponent.class);
		orbCompo.setType(new OrbFire());
		orb.add(orbCompo);
		
		room.addEntity(orb);
		
		return orb;
	}
	
	
	public Entity createDeathOrb(Vector2 pos, Room room) {
		Entity orb = createOrbBase(room, pos, EntityFlagEnum.DEATH_ORB, AnimationSingleton.getInstance().deathOrb);
		
		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.ORB_DEATH_TITLE);
		inspect.setDescription(Descriptions.ORB_DEATH_DESCRIPTION);
		orb.add(inspect);
		
		OrbComponent orbCompo = engine.createComponent(OrbComponent.class);
		orbCompo.setType(new OrbDeath());
		orb.add(orbCompo);
		
		room.addEntity(orb);
		
		return orb;
	}
	
	
	public Entity createVoid(Vector2 pos, Room room) {
		Entity orb = createOrbBase(room, pos, EntityFlagEnum.VOID, AnimationSingleton.getInstance().voidOrb);
		
		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.ORB_VOID_TITLE);
		inspect.setDescription(Descriptions.ORB_VOID_DESCRIPTION);
		orb.add(inspect);
		
		OrbComponent orbCompo = engine.createComponent(OrbComponent.class);
		orbCompo.setType(new OrbVoid());
		orb.add(orbCompo);
		
		room.addEntity(orb);
		
		return orb;
	}
}
