/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.blessings;

import java.util.Collections;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.ces.components.creep.CreepImmunityComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems.AlterationSystem;
import com.dokkaebistudio.tacticaljourney.creature.enemies.EnemyOrangutan;
import com.dokkaebistudio.tacticaljourney.creeps.Creep.CreepType;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.Tile;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

/**
 * Blessing of the orangutan. Drops banana peels when receiving damages + banana peels immunity.
 * @author Callil
 *
 */
public class BlessingOfTheOrangutan extends Blessing {
	
	private final int initialChanceToProc = 75;
	
	@Override
	public String title() {
		return "Blessing of the Orangutan";
	}
	
	@Override
	public String description() {
		return "Upon receiving damage, chance to drop banana peels.\nGrants immunity to banana peels.\nEating a banana grants 10hp.";
	}
	
	@Override
	public RegionDescriptor texture() {
		return Assets.blessing_orangutan;
	}
	
	@Override
	public Integer getCurrentProcChance(Entity user) {
		return initialChanceToProc;
	}
	
	@Override
	public void onReceive(Entity entity) {
		super.onReceive(entity);
		
		// Banana peel immunity
		CreepImmunityComponent creepImmunityComponent = Mappers.creepImmunityComponent.get(entity);
		if (creepImmunityComponent == null) {
			creepImmunityComponent = GameScreen.engine.createComponent(CreepImmunityComponent.class);
			entity.add(creepImmunityComponent);
		}
		
		creepImmunityComponent.getTypes().add(CreepType.BANANA);
	}
	
	@Override
	public void onRemove(Entity entity) {
		super.onRemove(entity);
		// Remove banana peel immunity
		CreepImmunityComponent creepImmunityComponent = Mappers.creepImmunityComponent.get(entity);
		if (creepImmunityComponent != null) {
			creepImmunityComponent.getTypes().remove(CreepType.BANANA);
		}

	}

	
	@Override
	public void onReceiveDamage(Entity user, Entity attacker, Room room) {
		float randomValue = RandomSingleton.getNextChanceWithKarma();
		
		if (randomValue < getCurrentProcChance(user)) {
			int number = 1 + RandomSingleton.getInstance().getUnseededRandom().nextInt(3);
			
			// When taking damage, drops 3 banana peels on close tiles that doesn't already have a banana peel
			GridPositionComponent orangutanPos = Mappers.gridPositionComponent.get(user);
			List<Tile> tilesAtProximity = TileUtil.getTilesAtProximity(orangutanPos.coord(), 3, room);
			Collections.shuffle(tilesAtProximity, RandomSingleton.getInstance().getUnseededRandom());
			
			tilesAtProximity.parallelStream()
				.filter(t -> t.isWalkable() && !EnemyOrangutan.hasBanana(t, room))
				.limit(number)
				.forEachOrdered(t -> EnemyOrangutan.throwBananas(orangutanPos.coord(), t, room));
			
			
			Journal.addEntry("Blessing of the Orangutan threw " + number + " banana peels.");
			AlterationSystem.addAlterationProc(this);
		}
	}

}
