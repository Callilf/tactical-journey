/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.blessings;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.RandomXS128;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.Tile;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.wheel.Sector;

/**
 * Blessing of fire arrows. Create fire when shooting on an empty tile.
 * @author Callil
 *
 */
public class BlessingOfFireArrows extends Blessing {

	private int initialChanceToProc = 10;
	
	@Override
	public String title() {
		return "Blessing of fire arrows";
	}
	
	@Override
	public String description() {
		return "Arrows fired on an empty tile create fire. Arrows fired on an enemy have a small chance to create fire and inflict the [ORANGE]burning[] status effect.";
	}
	
	@Override
	public RegionDescriptor texture() {
		return Assets.blessing_fire_arrows;
	}

	@Override
	public void onAttack(Entity attacker, Entity target, Sector sector, Room room) {
		// Attacked an enemy
		int chanceToProc = this.initialChanceToProc;
		
		RandomXS128 unseededRandom = RandomSingleton.getInstance().getUnseededRandom();
		int chance = unseededRandom.nextInt(100);
		if (chance < chanceToProc) {
			room.entityFactory.creepFactory.createFire(room, Mappers.gridPositionComponent.get(target).coord(), attacker);
		}
	}
	
	@Override
	public void onAttackEmptyTile(Entity attacker, Tile tile, Room room) {
		// Attacked an empty tile
		room.entityFactory.creepFactory.createFire(room, tile.getGridPos(), attacker);
	}
	

}
