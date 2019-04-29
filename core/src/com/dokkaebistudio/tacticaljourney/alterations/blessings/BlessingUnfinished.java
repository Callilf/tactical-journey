/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.blessings;

import java.util.Set;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.components.DestructibleComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.creep.CreepComponent;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.enums.HealthChangeEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.statuses.Status;
import com.dokkaebistudio.tacticaljourney.statuses.debuffs.StatusDebuffEntangled;
import com.dokkaebistudio.tacticaljourney.systems.AlterationSystem;
import com.dokkaebistudio.tacticaljourney.util.LootUtil;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

/**
 * Blessing of the unfinished. Cut through many hazards + immunity to entangled
 * @author Callil
 *
 */
public class BlessingUnfinished extends Blessing {
		
	@Override
	public String title() {
		return "Blessing of the unfinished";
	}
	
	@Override
	public String description() {
		return "Cut through many hazards and grants immunity to the [FOREST]entangled[] status effect.";
	}
	
	@Override
	public RegionDescriptor texture() {
		return Assets.blessing_unfinished;
	}
	
	@Override
	public void onArriveOnTile(Vector2 gridPos, Entity mover, Room room) {
		boolean activated = false;
		
		Set<Entity> creeps = TileUtil.getEntitiesWithComponentOnTile(gridPos, CreepComponent.class, room);
		for (Entity c : creeps) {
			DestructibleComponent destructibleComponent = Mappers.destructibleComponent.get(c);
			if (destructibleComponent != null && destructibleComponent.isDestroyableWithWeapon()) {
				activated = true;
				LootUtil.destroy(c, room);
			}
		}
		
		if (activated) {
			AlterationSystem.addAlterationProc(this);
		}
	}
	
	
	@Override
	public boolean onReceiveStatusEffect(Entity entity, Status status, Room room) {
		if (status instanceof StatusDebuffEntangled) {
			AlterationSystem.addAlterationProc(this);
			
			HealthComponent healthComponent = Mappers.healthComponent.get(entity);
			if (healthComponent != null) {
				healthComponent.healthChangeMap.put(HealthChangeEnum.RESISTANT, "ENTANGLED IMMUNITY");
			}
			return false;
		}
		
		return true;
	}

}
