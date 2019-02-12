package com.dokkaebistudio.tacticaljourney.enums.creep;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.enums.enemy.EnemyFactionEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;


/**
 * The enum of all the creep..
 * @author Callil
 *
 */
public enum CreepEnum {
		
	/** Add money to the player's wallet. */
	WEB("Spider web", Assets.money_item) {
		
		@Override
		public void onWalk(Entity walker, Entity creep, Room room) {
			// If the player walks on it, all spiders are alerted
			if (Mappers.playerComponent.has(walker)) {
				for(Entity e : room.getEnemies()) {
					EnemyComponent enemyComponent = Mappers.enemyComponent.get(e);
					if (enemyComponent != null && enemyComponent.getFaction() == EnemyFactionEnum.SPIDERS) {
						enemyComponent.setAlerted(true);
					}
				}
			}
		}

	};
	
	
	
	
	
	/** The name displayed. */
	private String label;
	/** The name of the image in the assets. */
	private String imageName;
	
	/**
	 * Constructor for creep
	 * @param label
	 * @param imageName
	 */
	CreepEnum(String label, String imageName) {
		this.setLabel(label);
		this.setImageName(imageName);
	}
	
	
	// Abstract methods
	
	/** Called when the item is used. */
	public abstract void onWalk(Entity walker, Entity creep, Room room);
	
	
	
	// Getters and Setters

	public String getLabel() {
		return label;
	}


	public void setLabel(String label) {
		this.label = label;
	}


	public String getImageName() {
		return imageName;
	}


	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

}
