/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.blessings;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.ai.movements.AttackTypeEnum;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.ces.components.attack.AttackComponent;
import com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems.AlterationSystem;
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
		return "Arrows fired on an empty tile have [YELLOW]100%[] chance of creating fire. "
				+ "Arrows fired on an enemy have a small chance to create fire and inflict the [ORANGE]burning[] status effect.";
	}
	
	@Override
	public RegionDescriptor texture() {
		return Assets.blessing_fire_arrows;
	}
	
	@Override
	public Integer getCurrentProcChance(Entity user) {
		return initialChanceToProc;
	}

	@Override
	public void onAttack(Entity attacker, Entity target, Sector sector, AttackComponent attackCompo, Room room) {
		if (attackCompo != null && attackCompo.getActiveSkill().getAttackType() == AttackTypeEnum.RANGE) {			
			float randomValue = RandomSingleton.getNextChanceWithKarma();
			if (randomValue < getCurrentProcChance(attacker)) {
				AlterationSystem.addAlterationProc(this);
				room.entityFactory.creepFactory.createFire(room, Mappers.gridPositionComponent.get(target).coord(), attacker);
			}
		}
	}
	
	@Override
	public void onAttackEmptyTile(Entity attacker, Tile tile, AttackComponent attackCompo, Room room) {
		if (attackCompo != null && attackCompo.getActiveSkill().getAttackType() == AttackTypeEnum.RANGE) {
			// Attacked an empty tile
			room.entityFactory.creepFactory.createFire(room, tile.getGridPos(), attacker);
		}
	}
	

}
