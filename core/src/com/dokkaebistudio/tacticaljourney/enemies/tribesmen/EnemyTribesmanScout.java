package com.dokkaebistudio.tacticaljourney.enemies.tribesmen;

import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.enemies.Enemy;

public class EnemyTribesmanScout extends Enemy {


	private boolean hasFriendsNotAlerted = false;
	
	@Override
	public String title() {
		return Descriptions.ENEMY_TRIBESMAN_SCOUT_TITLE;
	}

	
	
	
	public boolean hasFriendsNotAlerted() {
		return hasFriendsNotAlerted;
	}

	public void setHasFriendsNotAlerted(boolean hasFriendsNotAlerted) {
		this.hasFriendsNotAlerted = hasFriendsNotAlerted;
	}
	
	
	
	

	
}
