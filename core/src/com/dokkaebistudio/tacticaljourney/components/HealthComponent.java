package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.TextComponent;
import com.dokkaebistudio.tacticaljourney.components.interfaces.MovableInterface;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

/**
 * Marker to indicate that this entity has health and therefore can be attacked or damaged.
 * @author Callil
 *
 */
public class HealthComponent implements Component, Poolable, MovableInterface, RoomSystem {
		
	/** The engine that managed entities.*/
	public Room room;
	
	
	//************
	// HP
	
	/** The max number of h. */
	private int maxHp;
	
	/** The current number of hp. */
	private int hp;
	
	/** The displayer that shows the amount of HP beside the entity (for enemies). */
	private Entity hpDisplayer;

	
	//*************
	// Armor
	
	/** The max amount of armor. */
	private int maxArmor;
	
	/** The current number of armor. */
	private int armor;
	
	
	
	/** Whether the entity received damages during the previous turn. */
	private boolean receivedDamageLastTurn;
	
	/** The last entity that attacked this entity. */
	private Entity attacker;
	
	/** Whether the entity has been hit or healed at this frame. */
	private HealthChangeEnum healthChange = HealthChangeEnum.NONE;
	private int healthLostAtCurrentFrame;
	private int healthRecoveredAtCurrentFrame;
	
	public enum HealthChangeEnum {
		NONE,
		HIT,
		HEALED,
		ARMOR;
	}
	
	
	/**
	 * Receive damages.
	 * @param amountOfDamage the amount of damages
	 */
	public void hit(int amountOfDamage, Entity attacker) {
		int damageToHealth = Math.max(amountOfDamage - this.armor, 0);
		if (this.armor > 0) {
			this.armor = Math.max(this.armor - amountOfDamage, 0);
		}
		
		this.setHp(Math.max(0, this.getHp() - damageToHealth));
		this.healthLostAtCurrentFrame += amountOfDamage;
		this.healthChange = HealthChangeEnum.HIT;
		
		this.attacker = attacker;
	}
	
	
	/**
	 * Restore the given amount of health.
	 * @param amount the amount to restore
	 */
	public void restoreHealth(int amount) {
		this.setHp(this.getHp() + amount);
		if (this.getHp() > this.getMaxHp()) {
			this.setHp(this.getMaxHp());
		}
		this.healthRecoveredAtCurrentFrame += amount;
		this.healthChange = HealthChangeEnum.HEALED;
	}
	
	/**
	 * Restore the given amount of armor.
	 * @param amount the amount to restore
	 */
	public void restoreArmor(int amount) {
		this.setArmor(this.getArmor() + amount);
		if (this.getArmor() > this.getMaxArmor()) {
			this.setArmor(this.getMaxArmor());
		}
		this.healthRecoveredAtCurrentFrame += amount;
		this.healthChange = HealthChangeEnum.ARMOR;
	}
	
	public void clearModified() {
		this.healthChange = HealthChangeEnum.NONE;
		this.healthLostAtCurrentFrame = 0;
		this.healthRecoveredAtCurrentFrame = 0;
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
	 * Increase the max armor by the given amount.
	 * @param amount the amount to add.
	 */
	public void increaseMaxArmor(int amount) {
		this.setMaxArmor(this.maxHp + amount);
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
	
	/**
	 * Return the color in which the armor must be displayed on the HUD.
	 * The color depends on the amount or armor
	 * @return the color
	 */
	public String getArmorColor() {
		if (armor > 0) {
			return "[BLUE]";
		} else {
			return "[WHITE]";
		}
	}
	
	@Override
	public void enterRoom(Room newRoom) {
		this.room = newRoom;
	}
	
	@Override
	public void reset() {
		if (hpDisplayer != null) {
			room.removeEntity(hpDisplayer);		
		}
		hpDisplayer = null;
		this.clearModified();
		this.receivedDamageLastTurn = false;
	}
	
	
	
	
	//**************************************
	// Movement

	@Override
	public void initiateMovement(Vector2 currentPos) {
		
		if (hpDisplayer != null) {
			TextComponent textCompo = Mappers.textComponent.get(hpDisplayer);
			GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(hpDisplayer);
			
			//Add the tranfo component to the entity to perform real movement on screen
			Vector2 startPos = TileUtil.convertGridPosIntoPixelPos(currentPos);
			startPos.y = startPos.y + textCompo.getHeight();
			gridPositionComponent.absolutePos((int)startPos.x, (int)startPos.y);
		}
	}



	@Override
	public void performMovement(float xOffset, float yOffset) {
		if (hpDisplayer != null) {
			GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(hpDisplayer);
			gridPositionComponent.absolutePos((int)(gridPositionComponent.getAbsolutePos().x + xOffset), 
					(int)(gridPositionComponent.getAbsolutePos().y + yOffset));
		}
	}



	@Override
	public void endMovement(Vector2 finalPos) {
		if (hpDisplayer != null) {
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


	public int getHealthLostAtCurrentFrame() {
		return healthLostAtCurrentFrame;
	}


	public void setHealthLostAtCurrentFrame(int healthLostAtCurrentFrame) {
		this.healthLostAtCurrentFrame = healthLostAtCurrentFrame;
	}


	public int getHealthRecoveredAtCurrentFrame() {
		return healthRecoveredAtCurrentFrame;
	}


	public void setHealthRecoveredAtCurrentFrame(int healthRecoveredAtCurrentFrame) {
		this.healthRecoveredAtCurrentFrame = healthRecoveredAtCurrentFrame;
	}


	public HealthChangeEnum getHealthChange() {
		return healthChange;
	}


	public void setHealthChange(HealthChangeEnum healthChange) {
		this.healthChange = healthChange;
	}


	public boolean isReceivedDamageLastTurn() {
		return receivedDamageLastTurn;
	}


	public void setReceivedDamageLastTurn(boolean receivedDamageLastTurn) {
		this.receivedDamageLastTurn = receivedDamageLastTurn;
	}


	public Entity getAttacker() {
		return attacker;
	}


	public void setAttacker(Entity attacker) {
		this.attacker = attacker;
	}


	public int getMaxArmor() {
		return maxArmor;
	}


	public void setMaxArmor(int maxArmor) {
		this.maxArmor = maxArmor;
	}


	public int getArmor() {
		return armor;
	}


	public void setArmor(int armor) {
		this.armor = armor;
	}


	
}
