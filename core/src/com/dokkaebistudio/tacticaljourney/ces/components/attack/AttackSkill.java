package com.dokkaebistudio.tacticaljourney.ces.components.attack;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.dokkaebistudio.tacticaljourney.ai.movements.AttackTypeEnum;
import com.dokkaebistudio.tacticaljourney.room.Tile;
import com.dokkaebistudio.tacticaljourney.singletons.AnimationSingleton;
import com.dokkaebistudio.tacticaljourney.vfx.AttackAnimation;
import com.dokkaebistudio.tacticaljourney.wheel.Sector;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class AttackSkill {
	
	/** Whether this component is active or not. */
	private boolean active = true;
	
	/** The attack name, displayed in the inspect popin. */
	private String name;

	/** The type of attack (MELEE, RANGE, THROW...). */
	private AttackTypeEnum attackType;
	
	/** The min attack range. */
	private int rangeMin = 1;
	/** The max attack range. */
	private int rangeMax = 1;

	/** The amount of damage dealt to an ennemy without any protection. */
	private int strength;
	
	// Animations
	private AttackAnimation attackAnimation;

	
	
	
	/**
	 * Increase the strength by the given amount.
	 * @param amount the amount to add
	 */
	public void increaseStrength(int amount) {
		this.strength += amount;
	}
	
	/**
	 * Increase the range max by the given amount.
	 * @param amount the amount to add
	 */
	public void increaseRangeMax(int amount) {
		this.rangeMax += amount;
	}
	
	
	
	//***************************
	// Attack Animations
	
	/**
	 * Set the attack animation to use.
	 * @param texture the texture to use
	 * @param startGridPos the start pos (the attacker pos)
	 * @param finishAttackAction the action to call after the movement is over
	 */
	public boolean setAttackImage(Vector2 startGridPos, Tile targetedTile, Sector pointedSector, Stage fxStage, Action finishAttackAction) {
		if (this.attackAnimation == null) return false;
		
		return this.attackAnimation.setAttackImage(this.attackType, startGridPos, targetedTile, pointedSector, fxStage,
				finishAttackAction);
	}
	
	public void clearAttackImage() {
		if (this.attackAnimation != null) {
			this.attackAnimation.clear();
		}
	}
	
	
	
	
	
	// Getters and setters

	public AttackTypeEnum getAttackType() {
		return attackType;
	}

	public void setAttackType(AttackTypeEnum attackType) {
		this.attackType = attackType;
	}

	public int getRangeMin() {
		return rangeMin;
	}

	public void setRangeMin(int rangeMin) {
		this.rangeMin = rangeMin;
	}

	public int getRangeMax() {
		return rangeMax;
	}

	public void setRangeMax(int rangeMax) {
		this.rangeMax = rangeMax;
	}

	public int getStrength() {
		return strength;
	}

	public void setStrength(int strength) {
		this.strength = strength;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public AttackAnimation getAttackAnimation() {
		return attackAnimation;
	}

	public void setAttackAnimation(AttackAnimation attackAnimation) {
		this.attackAnimation = attackAnimation;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	
	public static Serializer<AttackSkill> getSerializer(final PooledEngine engine) {
		return new Serializer<AttackSkill>() {

			@Override
			public void write(Kryo kryo, Output output, AttackSkill object) {
				
				output.writeBoolean(object.active);
                output.writeString(object.name);
                output.writeString(object.attackType.name());

                // Range
                output.writeInt(object.rangeMin);
                output.writeInt(object.rangeMax);
                
                // Strength
                output.writeInt(object.strength);
				
				// Animations
				output.writeBoolean(object.attackAnimation != null);
				if (object.attackAnimation != null) {
					output.writeInt(object.attackAnimation.getAttackAnim());
					output.writeInt(object.attackAnimation.getCriticalAttackAnim());
					output.writeBoolean(object.attackAnimation.isOriented());
				}
			}

			@Override
			public AttackSkill read(Kryo kryo, Input input, Class<? extends AttackSkill> type) {
				AttackSkill as = new AttackSkill();
				as.active = input.readBoolean();
				as.name = input.readString();
				as.attackType = AttackTypeEnum.valueOf(input.readString());
                
				as.rangeMin = input.readInt();
				as.rangeMax = input.readInt();

				as.strength = input.readInt();
				
				// Animation
				boolean hasAttackAnim = input.readBoolean();
				if (hasAttackAnim) {
					AttackAnimation attackAnimation = new AttackAnimation(
							AnimationSingleton.getInstance().getAnimation(input.readInt()),
							AnimationSingleton.getInstance().getAnimation(input.readInt()),
								input.readBoolean());
					as.setAttackAnimation(attackAnimation);
				}

				return as;
			}
		
		};
	}


}
