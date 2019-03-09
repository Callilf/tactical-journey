/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.blessings;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.RandomXS128;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Blessing of the pangolin. Chance to get armor upon receiving damages.
 * @author Callil
 *
 */
public class BlessingOfThePangolin extends Blessing {
	
	private final int initialChanceToProc = 10;
	
	@Override
	public String title() {
		return "Blessing of the Pangolin";
	}
	
	@Override
	public String description() {
		return "Upon receiving damage, chance to receive [CYAN]10 armor[].";
	}
	
	@Override
	public AtlasRegion texture() {
		return Assets.blessing_pangolin;
	}

	
	@Override
	public void onReceiveDamage(Entity user, Entity attacker, Room room) {
		RandomXS128 unseededRandom = RandomSingleton.getInstance().getUnseededRandom();
		int chance = unseededRandom.nextInt(100);
		
		if (chance < initialChanceToProc) {
			HealthComponent healthComponent = Mappers.healthComponent.get(user);
			healthComponent.restoreArmor(10);
			
			Journal.addEntry("[GREEN] Blessing of the pangolin restored 10 [CYAN]armor");
		}
	}

}