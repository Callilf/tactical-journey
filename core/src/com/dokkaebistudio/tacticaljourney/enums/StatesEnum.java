package com.dokkaebistudio.tacticaljourney.enums;

public enum StatesEnum {
	
	PLAYER_STANDING(0),
	PLAYER_RUNNING(1),
	PLAYER_FLYING(2),

	EXPLODING_IN_SEVERAL_TURNS(0),
	EXPLODING_THIS_TURN(1),
	
	EXPLOSION(0),
	PORTAL(0),
	
	FIRE_LOOP(0),
	
	STINGER_FLY(0),
	STINGER_ATTACK(1),
	
	PANGOLIN_BABY_STAND(0),
	PANGOLIN_BABY_ROLLED(1),
	PANGOLIN_BABY_ROLLING(2),
	
	PANGOLIN_MOTHER_STAND(0),
	PANGOLIN_MOTHER_ENRAGED_STAND(1),
	PANGOLIN_MOTHER_CRYING(2),
	
	TRIBESMEN_SPEAR_STAND(0),
	TRIBESMEN_SHIELD_STAND(0),
	TRIBESMEN_SCOUT_STAND(0),
	TRIBESMEN_SHAMAN_STAND(0),
	TRIBESMEN_SHAMAN_SUMMONING(10),
	TRIBESMEN_TOTEM(0);

	
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
