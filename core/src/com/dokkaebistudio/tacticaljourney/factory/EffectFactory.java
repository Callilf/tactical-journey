/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.factory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.components.display.AnimationComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.display.StateComponent;
import com.dokkaebistudio.tacticaljourney.components.display.VisualEffectComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;

/**
 * Factory used to create visual effects.
 * @author Callil
 *
 */
public final class EffectFactory {
	
	/** The gdx pooled engine. */
	public PooledEngine engine;
	
	/** the entity factory. */
	public EntityFactory entityFactory;
	

	/**
	 * Constructor.
	 * @param e the engine
	 */
	public EffectFactory(PooledEngine e, EntityFactory ef) {
		this.engine = e;
		this.entityFactory = ef;
	}
	
	
	
	/**
	 * Create an explosion effect.
	 * @param room the parent room
	 * @param tilePos the position in tiles
	 * @return the entity created
	 */
	public Entity createExplosionEffect(Room room, Vector2 tilePos) {
		Entity explosion = engine.createEntity();
		explosion.flags = EntityFlagEnum.EXPLOSION_EFFECT.getFlag();

		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		explosion.add(spriteCompo);

		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		gridPosition.coord(explosion, tilePos, room);
		gridPosition.zIndex = 10;
		explosion.add(gridPosition);
		
		AnimationComponent animationCompo = engine.createComponent(AnimationComponent.class);
		Animation<Sprite> explosionAnim = new Animation<Sprite>(0.1f, Assets.getAnimation(Assets.explosion_animation), PlayMode.NORMAL);
		animationCompo.animations.put(0, explosionAnim);
		explosion.add(animationCompo);
		
		StateComponent stateCompo = engine.createComponent(StateComponent.class);
		stateCompo.set(0);
		explosion.add(stateCompo);
		
		VisualEffectComponent veCompo = engine.createComponent(VisualEffectComponent.class);
		explosion.add(veCompo);
		
		engine.addEntity(explosion);
		
		return explosion;
	}

	
	
}
