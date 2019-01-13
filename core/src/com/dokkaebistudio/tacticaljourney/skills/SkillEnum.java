package com.dokkaebistudio.tacticaljourney.skills;

import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;


public enum SkillEnum {

	SLASH(Assets.btn_skill_attack, Assets.btn_skill_attack_pushed, 1, 1, 5, -1),
	BOW(Assets.btn_skill_bow, Assets.btn_skill_bow_pushed, 2, 5, 3, 10);
	
	
	public final static Vector2 SKILL_1_POSITION = new Vector2(1500.0f, 20.0f);
	public final static Vector2 SKILL_2_POSITION = new Vector2(1580.0f, 20.0f);
	public final static Vector2 SKILL_3_POSITION = new Vector2(1660.0f, 20.0f);

	/** The image of this skill. */
	private String btnTexture;
	/** The image of this skill when pushed. */
	private String btnPushedTexture;
	
	private int rangeMin;
	private int rangeMax;
	private int strength;
	private int ammos;
	
	SkillEnum(String texture, String pushedTexture, int rangeMin, int rangeMax, int strength, int ammos) {
		setBtnTexture(texture);
		setBtnPushedTexture(pushedTexture);
		this.setRangeMin(rangeMin);
		this.setRangeMax(rangeMax);
		this.setStrength(strength);
		this.setAmmos(ammos);
	}

	
	
	//Getters and Setters
	
	
	public String getBtnTexture() {
		return btnTexture;
	}

	public void setBtnTexture(String btnTexture) {
		this.btnTexture = btnTexture;
	}


	public String getBtnPushedTexture() {
		return btnPushedTexture;
	}

	public void setBtnPushedTexture(String btnPushedTexture) {
		this.btnPushedTexture = btnPushedTexture;
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



	public int getAmmos() {
		return ammos;
	}



	public void setAmmos(int ammos) {
		this.ammos = ammos;
	}
	
	

	
	
}
