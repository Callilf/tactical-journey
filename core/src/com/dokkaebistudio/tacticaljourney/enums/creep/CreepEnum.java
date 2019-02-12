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
	WEB("Spider web", Assets.creep_web) {
		
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
		
		@Override
		public int getMovementConsumed(Entity mover) {
			if (Mappers.enemyComponent.has(mover)) {
				if (Mappers.enemyComponent.get(mover).getFaction() == EnemyFactionEnum.SPIDERS) return -1;
			}
			return 100;
		}

	},
	
	/** Add money to the player's wallet. */
	MUD("mud", Assets.mud) {
		
		@Override
		public void onWalk(Entity walker, Entity creep, Room room) {}
		
		@Override
		public int getMovementConsumed(Entity mover) {
			return 1;
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
	
	/** Emit the creep. */
	public void onEmit(Entity emitter, Entity emittedCreep, Room room) {};
	
	/**
	 * Get the movement consumed when walking on this creep.
	 * @param mover the moving entity
	 * @return the number of movement consumed
	 */
	public int getMovementConsumed(Entity mover) {
		return 1;
	}
	
	
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
