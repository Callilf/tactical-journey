package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.systems.RenderingSystem;

/**
 * Marker to indicate that this entity has health and therefore can be attacked or damaged.
 * @author Callil
 *
 */
public class HealthComponent implements Component, Poolable, MovableInterface {
	
	/** The engine that managed entities.*/
	public PooledEngine engine;
	
	private int hp;
	
	private Entity hpDisplayer;
	
	
	
	
	@Override
	public void reset() {
		if (hpDisplayer != null) {
			engine.removeEntity(hpDisplayer);		
		}
	}
	
	

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public Entity getHpDisplayer() {
		return hpDisplayer;
	}

	public void setHpDisplayer(Entity hpDisplayer) {
		this.hpDisplayer = hpDisplayer;
	}


	
	//**************************************
	// Movement

	@Override
	public void initiateMovement(Vector2 currentPos) {
		
		if (hpDisplayer != null) {
			TextComponent textCompo = ComponentMapper.getFor(TextComponent.class).get(hpDisplayer);
			
			//Add the tranfo component to the entity to perform real movement on screen
			TransformComponent transfoCompo = engine.createComponent(TransformComponent.class);
			Vector2 startPos = RenderingSystem.convertGridPosIntoPixelPos(currentPos);
			startPos.y = startPos.y + textCompo.getHeight();
			transfoCompo.pos.x = startPos.x;
			transfoCompo.pos.y = startPos.y;
			transfoCompo.pos.z = 100;
			hpDisplayer.add(transfoCompo);
		}
	}



	@Override
	public void performMovement(float xOffset, float yOffset, ComponentMapper<TransformComponent> transfoCM) {
		if (hpDisplayer != null) {
			TransformComponent transformComponent = transfoCM.get(hpDisplayer);
			transformComponent.pos.x = transformComponent.pos.x + xOffset;
			transformComponent.pos.y = transformComponent.pos.y + yOffset;
		}
	}



	@Override
	public void endMovement(Vector2 finalPos, ComponentMapper<GridPositionComponent> gridPositionM) {
		if (hpDisplayer != null) {
			hpDisplayer.remove(TransformComponent.class);
			GridPositionComponent gridPositionComponent = gridPositionM.get(hpDisplayer);
			gridPositionComponent.coord.set(finalPos);
		}
	}


	
}
