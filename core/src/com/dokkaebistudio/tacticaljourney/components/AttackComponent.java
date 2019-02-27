package com.dokkaebistudio.tacticaljourney.components;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.ai.movements.AttackTypeEnum;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.enums.AmmoTypeEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.Tile;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.util.ActionsUtil;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class AttackComponent implements Component, Poolable, RoomSystem {
		
	/** The room.*/
	public Room room;
	
	/** The type of attack (MELEE, RANGE, THROW...). */
	private AttackTypeEnum attackType;
	
	/** The min attack range. */
	private int rangeMin;
	/** The max attack range. */
	private int rangeMax;
	
	
	/** The amount of damage dealt to an ennemy without any protection. */
	private int strength;
	private int additionnalStrength;
	
	/** Whether the value of strength is a differential from the parentAttackCompo's strength or not. */
	private boolean isStrengthDifferential = true;
	
	/** The type of ammunition used by this attack component. */
	private AmmoTypeEnum ammoType = AmmoTypeEnum.NONE;
	/** The number of ammos used per attack. */
	private int ammosUsedPerAttack = 1;
	
	/** The target entity. */
	private Entity target;
	/** The targeted tile entity. */
	private Entity targetedTile;
	
	//************
	// Bombs
	
	private int bombRadius;
	private int bombTurnsToExplode;
	
	//************
	// Throwing
	
	private Entity thrownEntity;
	
	
	
	private Image projectileImage;

	
	//*************
	// Skill
	
	/** The skill that corresponds to this attack component. */
	private int skillNumber;
	
	/** The parent attack compo. Used only if the current attack compo belongs to a skill.
	 * If so, the parent attack compo gives the basic strength, and this attack compo's strength
	 * is a differential which is added to the base strength (positive or negative).
	 */
	private AttackComponent parentAttackCompo;
	
	
	
	//**************************************
	// Attack tiles selection and display
	
	/** The tiles where the player can attack. */
	public Set<Tile> allAttackableTiles;
	
	/** The entities used to display the red tiles where the entity can attack. */
	public Set<Entity> attackableTiles = new HashSet<>();
		
	/** The selected tile for attack. */
	private Entity selectedTile;
	
	/** The button used to confirm movements. */
	private Entity attackConfirmationButton;
	
	
	
	@Override
	public void enterRoom(Room newRoom) {
		this.room = newRoom;
	}
	
	
	@Override
	public void reset() {
		clearAttackableTiles();
		this.target = null;
		this.attackType = null;
		this.room = null;
		this.isStrengthDifferential = true;
		this.additionnalStrength = 0;
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
	
	
	
	/**
	 * Set the projectile image to use.
	 * @param texture the texture to use
	 * @param startGridPos the start pos (the attacker pos)
	 * @param targetGridPos the end pos (the target pos)
	 * @param orientedTowardDirection whether the projectile has to be oriented towards the target
	 * @param finishAttackAction the action to call after the movement is over
	 */
	public void setProjectileImage(AtlasRegion texture, Vector2 startGridPos, Vector2 targetGridPos, boolean orientedTowardDirection, Action finishAttackAction) {
		Image arrow = new Image(texture);
		Vector2 playerPixelPos = TileUtil.convertGridPosIntoPixelPos(startGridPos);
		arrow.setPosition(playerPixelPos.x, playerPixelPos.y);
		
		Vector2 targetPosInPixel = TileUtil.convertGridPosIntoPixelPos(targetGridPos);

		if (orientedTowardDirection) {
			double degrees = Math.atan2(
					targetPosInPixel.y - playerPixelPos.y,
				    targetPosInPixel.x - playerPixelPos.x
				) * 180.0d / Math.PI;
			arrow.setOrigin(Align.center);
			arrow.setRotation((float) degrees);
		}
		
		arrow.setOrigin(Align.center);
		
		double distance = Math.hypot(playerPixelPos.x-targetPosInPixel.x, playerPixelPos.y-targetPosInPixel.y);
		double nbTiles = Math.ceil(distance / GameScreen.GRID_SIZE);
		float duration = (float) (nbTiles * 0.1f);
		
		if (orientedTowardDirection) {
			ActionsUtil.move(arrow, targetPosInPixel, duration, finishAttackAction);
		} else {
			float rotation = (float) (nbTiles * 90);
			ActionsUtil.moveAndRotate(arrow, targetPosInPixel, rotation, duration, finishAttackAction);
		}
			
		this.setProjectileImage(arrow);
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
		if (isStrengthDifferential && parentAttackCompo != null) {
			result += parentAttackCompo.getStrength();
		}
		result += additionnalStrength;
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

	public Entity getTargetedTile() {
		return targetedTile;
	}

	public void setTargetedTile(Entity targetedTile) {
		this.targetedTile = targetedTile;
	}

	public AttackTypeEnum getAttackType() {
		return attackType;
	}

	public void setAttackType(AttackTypeEnum attackType) {
		this.attackType = attackType;
	}


	public Image getProjectileImage() {
		return projectileImage;
	}


	public void setProjectileImage(Image image) {
		this.projectileImage = image;
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
	
	
	
}
