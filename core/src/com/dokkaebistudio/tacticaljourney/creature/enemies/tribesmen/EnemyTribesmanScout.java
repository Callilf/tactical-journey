package com.dokkaebistudio.tacticaljourney.creature.enemies.tribesmen;

import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.creature.Creature;

public class EnemyTribesmanScout extends Creature {


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
