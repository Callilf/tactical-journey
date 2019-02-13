package com.dokkaebistudio.tacticaljourney.enums;

public enum StatesEnum {

	EXPLODING_IN_SEVERAL_TURNS(0),
	EXPLODING_THIS_TURN(1),
	
	FIRE_LOOP(0);
	
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
