/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.blessings;

import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.wheel.AttackWheel;
import com.dokkaebistudio.tacticaljourney.wheel.Sector;
import com.dokkaebistudio.tacticaljourney.wheel.Sector.Hit;

/**
 * Blessing of the black mamba. Chance to poison the enemy.
 * @author Callil
 *
 */
public class BlessingPoisoner extends Blessing {


	@Override
	public String title() {
		return "Blessing of the poisoner";
	}
	
	@Override
	public String description() {
		return "On the attack wheel, replace a random zone by a [PURPLE]poison[] zone which inflict the [PURPLE]poison[] status effect for 5 turns.";
	}
	
	@Override
	public AtlasRegion texture() {
		return Assets.blessing_poisoner;
	}

	@Override
	public void onModifyWheelSectors(AttackWheel wheel, Entity entity, Room room) {
		List<Sector> sectors = wheel.getSectors();
		int index = RandomSingleton.getInstance().getUnseededRandom().nextInt(sectors.size());
		Sector oldSector = sectors.remove(index);
		Sector newSector = new Sector(oldSector.range, Hit.POISON);
		sectors.add(index, newSector);
	}

}
