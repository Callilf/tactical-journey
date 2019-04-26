package com.dokkaebistudio.tacticaljourney.components.attack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.ai.movements.AttackTypeEnum;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.enums.AmmoTypeEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.Tile;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class AttackComponent implements Component, Poolable, RoomSystem {
	
	/** The highest possible value for accuracy. */
	public static final int MAX_ACCURACY = 20;
		
	/** The room.*/
	public Room room;
	
	//**********************************
	// Serialized attributes
	
	/** Whether this component is active or not. */
	public boolean active = true;
	
	
	// Skills - allow having different kinds of attack
	private List<AttackSkill> skills = new ArrayList<>();
	private AttackSkill activeSkill;

	// Strength
	private int additionnalStrength;
	/** Whether the value of strength is a differential from the parentAttackCompo's strength or not. */
	private boolean isStrengthDifferential = true;
	
	// Accuracy
	private int accuracy = 1;
	private int realAccuracy = 1;

	
	// Ammos
	/** The type of ammunition used by this attack component. */
	private AmmoTypeEnum ammoType = AmmoTypeEnum.NONE;
	/** The number of ammos used per attack. */
	private int ammosUsedPerAttack = 1;
	
	
	// Bombs
	private int bombRadius;
	private int bombTurnsToExplode;
	
	// End turn
	private boolean doNotConsumeTurn;
	private boolean doNotAlertTarget;
	
	
	// Skill
	
	/** The skill that corresponds to this attack component. */
	private int skillNumber;
	

	
	
	
	//**********************************
	// Not serialized attributes
	
	
	// Target
	
	/** The target entity. */
	private Entity target;
	/** The targeted tile entity. */
	private Tile targetedTile;
	
	// Throwing
	private Entity thrownEntity;
	
	// Attack tiles selection and display
	
	/** The tiles where the player can attack. */
	public Set<Tile> allAttackableTiles;
	
	/** The entities used to display the red tiles where the entity can attack. */
	public Set<Entity> attackableTiles = new HashSet<>();
		
	/** The selected tile for attack. */
	private Entity selectedTile;
	
	/** The button used to confirm movements. */
	private Entity attackConfirmationButton;
	
	
	
	/** The parent attack compo. Used only if the current attack compo belongs to a skill.
	 * If so, the parent attack compo gives the basic strength, and this attack compo's strength
	 * is a differential which is added to the base strength (positive or negative).
	 */
	private Entity parentEntity;
	
	
	@Override
	public void enterRoom(Room newRoom) {
		this.room = newRoom;
		clearAttackableTiles();
	}
	
	
	@Override
	public void reset() {
		clearAttackableTiles();
		this.skills.clear();
		this.target = null;
		this.room = null;
		this.isStrengthDifferential = true;
		this.additionnalStrength = 0;
		this.active = true;
		this.accuracy = 1;
		this.realAccuracy = 1;
		this.doNotConsumeTurn = false;
		this.doNotAlertTarget = false;
		this.activeSkill = null;
	}
	
	/**
	 * Increase the strength by the given amount.
	 * @param amount the amount to add
	 */
	public void increaseStrength(int amount) {
		for (AttackSkill skill : this.skills) {
			skill.increaseStrength(amount);
		}
	}
	
	/**
	 * Increase the range max by the given amount.
	 * @param amount the amount to add
	 */
	public void increaseRangeMax(int amount) {
		for (AttackSkill skill : this.skills) {
			skill.increaseRangeMax(amount);
		}	
	}

	/**
	 * Modify the accuracy by the given amount.
	 * @param amount the amount to add
	 */
	public void increaseAccuracy(int amount) {
		this.realAccuracy += amount;
		this.accuracy = Math.min(MAX_ACCURACY, this.realAccuracy);
	}
	

	/**
	 * Clear the list of movable tiles and remove all entities associated to it.
	 */
	public void clearAttackableTiles() {
		for (Entity e : attackableTiles) {
			room.removeEntity(e);
		}
		attackableTiles.clear();
		
		if (allAttackableTiles != null) {
			allAttackableTiles.clear();
		}
		
		if (this.selectedTile != null) {
			room.removeEntity(this.selectedTile);
		}
		this.selectedTile = null;
		
		if (this.attackConfirmationButton != null) {
			room.removeEntity(this.attackConfirmationButton);
		}
		this.attackConfirmationButton = null;
		
		this.target = null;
		this.targetedTile = null;
	}
	
	

	public Entity getSelectedTile() {
		return selectedTile;
	}

	public void setSelectedTile(Entity selectedTile) {
		if (this.selectedTile != null) {
			room.removeEntity(this.selectedTile);
		}
		this.selectedTile = selectedTile;
	}



	public Entity getMovementConfirmationButton() {
		return attackConfirmationButton;
	}

	public void setMovementConfirmationButton(Entity movementConfirmationButton) {
		if (this.attackConfirmationButton != null) {
			room.removeEntity(this.attackConfirmationButton);
		}
		this.attackConfirmationButton = movementConfirmationButton;
	}
	
	
	public void showAttackableTiles() {
		for (Entity e : attackableTiles) {
			SpriteComponent spriteComponent = Mappers.spriteComponent.get(e);
			spriteComponent.hide = false;
		}
	}
	public void hideAttackableTiles() {
		for (Entity e : attackableTiles) {
			SpriteComponent spriteComponent = Mappers.spriteComponent.get(e);
			spriteComponent.hide = true;
		}
	}
	
	public void hideAttackEntities() {
		if (this.selectedTile != null) {
			SpriteComponent spriteComponent = Mappers.spriteComponent.get(this.selectedTile);
			spriteComponent.hide = true;
		}
		if (this.attackConfirmationButton != null) {
			SpriteComponent spriteComponent = Mappers.spriteComponent.get(this.attackConfirmationButton);
			spriteComponent.hide = true;
		}
	}
	
	
	public AttackSkill getMainSkill() {
		return skills.get(0);
	}
	
    public int getStrength() {
        int result = getActiveSkill().getStrength();
        if (isStrengthDifferential && parentEntity != null) {
            AttackComponent parentAttackCompo = Mappers.attackComponent.get(parentEntity);
            if (parentAttackCompo != null) {
                result += parentAttackCompo.getStrength();
            }
        }
        result += additionnalStrength;
        return result;
    }
    
    public int getRangeMin() {
    	return getActiveSkill().getRangeMin();
    }
    public int getRangeMax() {
    	return getActiveSkill().getRangeMax();
    }
    public AttackTypeEnum getAttackType() {
    	return getActiveSkill().getAttackType();
    }
    
    
	//***************************
	// Getters and setters


	public Entity getTarget() {
		return target;
	}


	public void setTarget(Entity target) {
		this.target = target;
	}


	public int getSkillNumber() {
		return skillNumber;
	}


	public void setSkillNumber(int skillNumber) {
		this.skillNumber = skillNumber;
	}

	public AmmoTypeEnum getAmmoType() {
		return ammoType;
	}


	public void setAmmoType(AmmoTypeEnum ammoType) {
		this.ammoType = ammoType;
	}


	public int getAmmosUsedPerAttack() {
		return ammosUsedPerAttack;
	}


	public void setAmmosUsedPerAttack(int ammosUsedPerAttack) {
		this.ammosUsedPerAttack = ammosUsedPerAttack;
	}


	public Entity getParentEntity() {
		return parentEntity;
	}


	public void setParentEntity(Entity parentEntity) {
		this.parentEntity = parentEntity;
	}

	public Tile getTargetedTile() {
		return targetedTile;
	}

	public void setTargetedTile(Tile targetedTile) {
		this.targetedTile = targetedTile;
	}

	public int getBombRadius() {
		return bombRadius;
	}
	public void setBombRadius(int bombRadius) {
		this.bombRadius = bombRadius;
	}
	public int getBombTurnsToExplode() {
		return bombTurnsToExplode;
	}
	public void setBombTurnsToExplode(int bombTurnsToExplode) {
		this.bombTurnsToExplode = bombTurnsToExplode;
	}


	public boolean isStrengthDifferential() {
		return isStrengthDifferential;
	}


	public void setStrengthDifferential(boolean isStrengthDifferential) {
		this.isStrengthDifferential = isStrengthDifferential;
	}


	public Entity getThrownEntity() {
		return thrownEntity;
	}


	public void setThrownEntity(Entity thrownEntity) {
		this.thrownEntity = thrownEntity;
	}


	public int getAdditionnalStrength() {
		return additionnalStrength;
	}


	public void setAdditionnalStrength(int additionnalStrength) {
		this.additionnalStrength = additionnalStrength;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}


	public int getAccuracy() {
		return accuracy;
	}
	
	public void setAccuracy(int accuracy) {
		this.realAccuracy = accuracy;
		this.accuracy = Math.min(MAX_ACCURACY, this.realAccuracy);
	}
	
	public boolean isDoNotConsumeTurn() {
		return doNotConsumeTurn;
	}

	public void setDoNotConsumeTurn(boolean doNotConsumeTurn) {
		this.doNotConsumeTurn = doNotConsumeTurn;
	}
	
	public boolean isDoNotAlertTarget() {
		return doNotAlertTarget;
	}

	public void setDoNotAlertTarget(boolean doNotAlertTarget) {
		this.doNotAlertTarget = doNotAlertTarget;
	}
	
	public List<AttackSkill> getSkills() {
		return skills;
	}
	
	public AttackSkill getActiveSkill() {
		if (activeSkill != null) {
			return activeSkill;
		} else {
			for (AttackSkill as : this.skills) {
				if (as.isActive()) return as;
			}
		}
		return null;
	}
	
	public void setActiveSkill(AttackSkill activeSkill) {
		this.activeSkill = activeSkill;
	}
	
	public static Serializer<AttackComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<AttackComponent>() {

			@Override
			public void write(Kryo kryo, Output output, AttackComponent object) {
				
				output.writeBoolean(object.active);
				
				kryo.writeClassAndObject(output, object.skills);
				
				output.writeInt(object.additionnalStrength);
				output.writeBoolean(object.isStrengthDifferential);
				kryo.writeClassAndObject(output, object.parentEntity);
				
				// Accuracy
				output.writeInt(object.accuracy);
				output.writeInt(object.realAccuracy);

				
				// Ammos
				output.writeString(object.ammoType.name());
				output.writeInt(object.ammosUsedPerAttack);

				// Bombs
				output.writeInt(object.bombRadius);
				output.writeInt(object.bombTurnsToExplode);

				output.writeBoolean(object.doNotConsumeTurn);
				output.writeBoolean(object.doNotAlertTarget);
				
				// Skill
				output.writeInt(object.skillNumber);
				
//				// Animations
//				output.writeBoolean(object.attackAnimation != null);
//				if (object.attackAnimation != null) {
//					output.writeInt(object.attackAnimation.getAttackAnim());
//					output.writeInt(object.attackAnimation.getCriticalAttackAnim());
//					output.writeBoolean(object.attackAnimation.isOriented());
//				}
			}

			@Override
			public AttackComponent read(Kryo kryo, Input input, Class<AttackComponent> type) {
				AttackComponent compo = engine.createComponent(AttackComponent.class);
				compo.active = input.readBoolean();
				
				compo.skills = (List<AttackSkill>) kryo.readClassAndObject(input);
				
				compo.additionnalStrength = input.readInt();
				compo.isStrengthDifferential = input.readBoolean();
				compo.parentEntity = (Entity) kryo.readClassAndObject(input);
				
				compo.accuracy = input.readInt();
				compo.realAccuracy = input.readInt();

				compo.ammoType = AmmoTypeEnum.valueOf(input.readString());
				compo.ammosUsedPerAttack = input.readInt();
				
				compo.bombRadius = input.readInt();
				compo.bombTurnsToExplode = input.readInt();
				
				compo.doNotConsumeTurn = input.readBoolean();
				compo.doNotAlertTarget = input.readBoolean();
				
				compo.skillNumber = input.readInt();
				
//				// Animation
//				boolean hasAttackAnim = input.readBoolean();
//				if (hasAttackAnim) {
//					AttackAnimation attackAnimation = new AttackAnimation(
//							AnimationSingleton.getInstance().getAnimation(input.readInt()),
//							AnimationSingleton.getInstance().getAnimation(input.readInt()),
//								input.readBoolean());
//					compo.setAttackAnimation(attackAnimation);
//				}

				return compo;
			}
		
		};
	}

}
