package com.dokkaebistudio.tacticaljourney.skills;

import com.dokkaebistudio.tacticaljourney.enums.AmmoTypeEnum;


public enum SkillEnum {

	SLASH(1, 1, 0, AmmoTypeEnum.NONE, 0, false),
	BOW(2, 5, -2, AmmoTypeEnum.ARROWS, 1, false),
	BOMB(0, 2, 10, AmmoTypeEnum.BOMBS, 1, true);
	
	
	private int rangeMin;
	private int rangeMax;
	private int strength;
	private AmmoTypeEnum ammosType;
	private int nbOfAmmosPerAttack;
	private boolean throwing;
	
	SkillEnum(int rangeMin, int rangeMax, int strength, AmmoTypeEnum ammoType, int nbAmmoUsed, boolean throwing) {
		this.setRangeMin(rangeMin);
		this.setRangeMax(rangeMax);
		this.setStrength(strength);
		this.setAmmosType(ammoType);
		this.setNbOfAmmosPerAttack(nbAmmoUsed);
		this.setThrowing(throwing);
	}

	
	
	//Getters and Setters


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



	public AmmoTypeEnum getAmmosType() {
		return ammosType;
	}



	public void setAmmosType(AmmoTypeEnum ammosType) {
		this.ammosType = ammosType;
	}



	public int getNbOfAmmosPerAttack() {
		return nbOfAmmosPerAttack;
	}



	public void setNbOfAmmosPerAttack(int nbOfAmmosPerAttack) {
		this.nbOfAmmosPerAttack = nbOfAmmosPerAttack;
	}



	public boolean isThrowing() {
		return throwing;
	}



	public void setThrowing(boolean throwing) {
		this.throwing = throwing;
	}




	
	

	
	
}
