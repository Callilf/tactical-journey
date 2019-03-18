package com.dokkaebistudio.tacticaljourney.enemies.tribesmen;

import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.enemies.Enemy;

public class EnemyTribesmanScout extends Enemy {


	private boolean hasAlertedOthers = false;
	
	@Override
	public String title() {
		return Descriptions.ENEMY_TRIBESMAN_SCOUT_TITLE;
	}
	
	
	
	

	public boolean hasAlertedOthers() {
		return hasAlertedOthers;
	}

	public void setHasAlertedOthers(boolean hasAlertedOthers) {
		this.hasAlertedOthers = hasAlertedOthers;
	}
	
}
