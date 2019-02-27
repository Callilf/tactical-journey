package com.dokkaebistudio.tacticaljourney.enums;

public enum StatesEnum {
	
	PLAYER_STANDING(0),
	PLAYER_FLYING(1),

	EXPLODING_IN_SEVERAL_TURNS(0),
	EXPLODING_THIS_TURN(1),
	
	EXPLOSION(0),
	
	FIRE_LOOP(0),
	
	STINGER_FLY(0),
	STINGER_ATTACK(1);
	
	private int state;
	
	private StatesEnum(int state) {
		this.setState(state);
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
}
