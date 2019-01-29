package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.TextComponent;
import com.dokkaebistudio.tacticaljourney.components.display.TransformComponent;
import com.dokkaebistudio.tacticaljourney.components.interfaces.MovableInterface;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

/**
 * Marker to indicate that this entity has health and therefore can be attacked or damaged.
 * @author Callil
 *
 */
public class HealthComponent implements Component, Poolable, MovableInterface {
		
	/** The engine that managed entities.*/
	public PooledEngine engine;
	
	/** The max number of h. */
	private int maxHp;
	
	/** The current number of hp. */
	private int hp;
	
	private Entity hpDisplayer;
	
	/**
	 * Restore the given amount of health.
	 * @param amount the amount to restore
	 */
	public void restoreHealth(int amount) {
		this.setHp(this.getHp() + amount);
		if (this.getHp() > this.getMaxHp()) {
			this.setHp(this.getMaxHp());
		}
	}
	
	/**
	 * Increase the max hp by the given amount.
	 * @param amount the amount to add.
	 */
	public void increaseMaxHealth(int amount) {
		this.setMaxHp(this.maxHp + amount);
		this.setHp(this.hp + amount);
	}
	
	/**
	 * Whether this entity is dead.
	 * @return true if the entity is dead.
	 */
	public boolean isDead() {
		return hp <= 0;
	}
	
	/**
	 * Return the color in which the health must be displayed on the HUD.
	 * The color depends on the remaining life
	 * @return the color
	 */
	public String getHpColor() {
		if (hp >= (maxHp * 0.66f)) {
			return "[GREEN]";
		} else if (hp >= (maxHp * 0.33f) && hp < (maxHp * 0.66f)) {
			return "[ORANGE]";
		} else {
			return "[RED]";
		}
	}
	
	
	@Override
	public void reset() {
		if (hpDisplayer != null) {
			engine.removeEntity(hpDisplayer);		
		}
	}
	
	
	
	
	//**************************************
	// Movement

	@Override
	public void initiateMovement(Vector2 currentPos) {
		
		if (hpDisplayer != null) {
			TextComponent textCompo = Mappers.textComponent.get(hpDisplayer);
			
			//Add the tranfo component to the entity to perform real movement on screen
			TransformComponent transfoCompo = engine.createComponent(TransformComponent.class);
			Vector2 startPos = TileUtil.convertGridPosIntoPixelPos(currentPos);
			startPos.y = startPos.y + textCompo.getHeight();
			transfoCompo.pos.x = startPos.x;
			transfoCompo.pos.y = startPos.y;
			transfoCompo.pos.z = 100;
			hpDisplayer.add(transfoCompo);
		}
	}



	@Override
	public void performMovement(float xOffset, float yOffset) {
		if (hpDisplayer != null) {
			TransformComponent transformComponent = Mappers.transfoComponent.get(hpDisplayer);
			if (transformComponent != null) {
				transformComponent.pos.x = transformComponent.pos.x + xOffset;
				transformComponent.pos.y = transformComponent.pos.y + yOffset;
			}
		}
	}



	@Override
	public void endMovement(Vector2 finalPos) {
		if (hpDisplayer != null) {
			hpDisplayer.remove(TransformComponent.class);
			GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(hpDisplayer);
			gridPositionComponent.coord(finalPos);
		}
	}

	@Override
	public void place(Vector2 tilePos) {
		if (hpDisplayer != null) {
			GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(hpDisplayer);
			gridPositionComponent.coord(tilePos);
		}
	}
	
	
	// Getters and Setters
	
	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
		if (hpDisplayer != null) {
			TextComponent textComponent = Mappers.textComponent.get(hpDisplayer);
			textComponent.setText(String.valueOf(this.hp));
		}
	}

	public Entity getHpDisplayer() {
		return hpDisplayer;
	}

	public void setHpDisplayer(Entity hpDisplayer) {
		this.hpDisplayer = hpDisplayer;
	}

	public int getMaxHp() {
		return maxHp;
	}

	public void setMaxHp(int maxHp) {
		this.maxHp = maxHp;
	}


	
}
