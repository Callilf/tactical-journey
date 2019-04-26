package com.dokkaebistudio.tacticaljourney.enums;

public enum StatesEnum {
	
	STANDING(0),
	MOVING(1),
	FLY_STANDING(10),
	FLY_MOVING(11),
	
	EXPLODING_IN_SEVERAL_TURNS(0),
	EXPLODING_THIS_TURN(1),
	
	EXPLOSION(0),
	FIRE_LOOP(0),
	
	STINGER_ATTACK(100),
	TRIBESMEN_SHAMAN_SUMMONING(100),
	
	SHINOBI_SLEEPING(100),
	SHINOBI_THROWING(101),
	SHINOBI_CLONING(102),
	SHINOBI_ATTACKING(103),
	
	PORTAL(0);

	
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
