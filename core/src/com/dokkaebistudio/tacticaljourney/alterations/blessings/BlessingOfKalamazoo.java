/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.blessings;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Blessing of Kalamazoo. Restore between 0 and 10 health after clearing a room.
 * @author Callil
 *
 */
public class BlessingOfKalamazoo extends Blessing {
	
	@Override
	public String title() {
		return "Blessing of Kalamazoo";
	}
	
	@Override
	public String description() {
		return "Restore between 0 and 10 hp after clearing a room";
	}
	
	@Override
	public AtlasRegion texture() {
		return Assets.blessing_of_kalamazoo;
	}

	@Override
	public void onRoomCleared(Entity entity, Room room) {
		HealthComponent healthComponent = Mappers.healthComponent.get(entity);
		
		if (healthComponent != null) {
			int healAmount = RandomSingleton.getInstance().getUnseededRandom().nextInt(10);
			healthComponent.restoreHealth(healAmount);
			
			System.out.println("Blessing of Kalamazoo granted " + healAmount + " hp.");
		}
	}

}
