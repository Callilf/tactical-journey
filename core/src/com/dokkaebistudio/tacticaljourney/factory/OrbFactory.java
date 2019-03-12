/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.factory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.components.display.AnimationComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.display.StateComponent;
import com.dokkaebistudio.tacticaljourney.components.orbs.OrbComponent;
import com.dokkaebistudio.tacticaljourney.constants.ZIndexConstants;
import com.dokkaebistudio.tacticaljourney.enums.AnimationsEnum;
import com.dokkaebistudio.tacticaljourney.orbs.OrbEnergy;
import com.dokkaebistudio.tacticaljourney.orbs.OrbPoison;
import com.dokkaebistudio.tacticaljourney.orbs.OrbVegetal;
import com.dokkaebistudio.tacticaljourney.room.Room;

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
		animCompo.animations.put(0, anim);
		orb.add(animCompo);
		StateComponent stateComponent = engine.createComponent(StateComponent.class);
		stateComponent.set(0);
		orb.add(stateComponent);
		
		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		if (pos != null) {
			gridPosition.coord(orb, pos, room);
		}
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
		Entity orb = createOrbBase(room, pos, EntityFlagEnum.ENERGY_ORB, AnimationsEnum.ENERGY_ORB.getAnimation());
		
		OrbComponent orbCompo = engine.createComponent(OrbComponent.class);
		orbCompo.setType(new OrbEnergy());
		orb.add(orbCompo);
		
		room.addEntity(orb);
		
		return orb;
	}
	
	public Entity createVegetalOrb(Vector2 pos, Room room) {
		Entity orb = createOrbBase(room, pos, EntityFlagEnum.VEGETAL_ORB, AnimationsEnum.VEGETAL_ORB.getAnimation());
		
		OrbComponent orbCompo = engine.createComponent(OrbComponent.class);
		orbCompo.setType(new OrbVegetal());
		orb.add(orbCompo);
		
		room.addEntity(orb);
		
		return orb;
	}

	public Entity createPoisonOrb(Vector2 pos, Room room) {
		Entity orb = createOrbBase(room, pos, EntityFlagEnum.POISON_ORB, AnimationsEnum.POISON_ORB.getAnimation());
		
		OrbComponent orbCompo = engine.createComponent(OrbComponent.class);
		orbCompo.setType(new OrbPoison());
		orb.add(orbCompo);
		
		room.addEntity(orb);
		
		return orb;
	}
}
