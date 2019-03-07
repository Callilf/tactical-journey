package com.dokkaebistudio.tacticaljourney.enums;

/**
 * The different types of damages.
 * @author Callil
 *
 */
public enum DamageType {

	NORMAL(""),
	EXPLOSION("[RED]explosion"),
	FIRE("[ORANGE]fire"),
	POISON("[PURPLE]poison");
	
	private String title;
	
	private DamageType(String title) {
		this.title = title;
	}

	public String title() {
		return title;
	}	
	
}
