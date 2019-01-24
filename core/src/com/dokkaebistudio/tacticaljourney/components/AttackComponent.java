package com.dokkaebistudio.tacticaljourney.components;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.enums.AmmoTypeEnum;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class AttackComponent implements Component, Poolable {
		
	/** The engine that managed entities.*/
	public PooledEngine engine;
	
	/** The min attack range. */
	private int rangeMin;
	/** The max attack range. */
	private int rangeMax;
	
	/** The amount of damage dealt to an ennemy without any protection. */
	private int strength;
	
	/** The type of ammunition used by this attack component. */
	private AmmoTypeEnum ammoType = AmmoTypeEnum.NONE;
	/** The number of ammos used per attack. */
	private int ammosUsedPerAttack = 1;
	
	/** The target entity. */
	private Entity target;
	
	//*************
	// Skill
	
	/** The skill that corresponds to this attack component. */
	private int skillNumber;
	
	/** The parent attack compo. Used only if the current attack compo belongs to a skill.
	 * If so, the parent attack compo gives the basic strength, and this attack compo's strength
	 * is a differential which is added to the base strength (positive or negative).
	 */
	private AttackComponent parentAttackCompo;
	
	//**************
	// Ammo display
	private Entity ammoDisplayer;
	
	
	
	//**************************************
	// Attack tiles selection and display
	
	/** The tiles where the player can attack. */
	public Set<Entity> allAttackableTiles;
	
	/** The entities used to display the red tiles where the entity can attack. */
	public Set<Entity> attackableTiles = new HashSet<>();
		
	/** The selected tile for attack. */
	private Entity selectedTile;
	
	/** The button used to confirm movements. */
	private Entity attackConfirmationButton;
	
	
	
	@Override
	public void reset() {
		clearAttackableTiles();
		this.target = null;
		if (ammoDisplayer != null) {
			engine.removeEntity(ammoDisplayer);
			ammoDisplayer = null;
		}
	}
	
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

	

	/**
	 * Clear the list of movable tiles and remove all entities associated to it.
	 */
	public void clearAttackableTiles() {
		for (Entity e : attackableTiles) {
			engine.removeEntity(e);
		}
		attackableTiles.clear();
		
		if (allAttackableTiles != null) {
			allAttackableTiles.clear();
		}
		
		if (this.selectedTile != null) {
			engine.removeEntity(this.selectedTile);
		}
		this.selectedTile = null;
		
		if (this.attackConfirmationButton != null) {
			engine.removeEntity(this.attackConfirmationButton);
		}
		this.attackConfirmationButton = null;
	}
	
	

	public Entity getSelectedTile() {
		return selectedTile;
	}

	public void setSelectedTile(Entity selectedTile) {
		if (this.selectedTile != null) {
			engine.removeEntity(this.selectedTile);
		}
		this.selectedTile = selectedTile;
	}



	public Entity getMovementConfirmationButton() {
		return attackConfirmationButton;
	}

	public void setMovementConfirmationButton(Entity movementConfirmationButton) {
		if (this.attackConfirmationButton != null) {
			engine.removeEntity(this.attackConfirmationButton);
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

	
	//***************************
	// Getters and setters

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
		int result = strength;
		if (parentAttackCompo != null) {
			result += parentAttackCompo.getStrength();
		}
		return result;
	}


	public void setStrength(int strength) {
		this.strength = strength;
	}


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


	public Entity getAmmoDisplayer() {
		return ammoDisplayer;
	}


	public void setAmmoDisplayer(Entity ammoDisplayer) {
		this.ammoDisplayer = ammoDisplayer;
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


	public AttackComponent getParentAttackCompo() {
		return parentAttackCompo;
	}


	public void setParentAttackCompo(AttackComponent parentAttackCompo) {
		this.parentAttackCompo = parentAttackCompo;
	}


	
}
