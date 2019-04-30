/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.blessings.shinobi;

import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.components.AIComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.Tile;
import com.dokkaebistudio.tacticaljourney.systems.AlterationSystem;
import com.dokkaebistudio.tacticaljourney.util.AIUtil;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;
import com.dokkaebistudio.tacticaljourney.vfx.VFXUtil;

/**
 * Bunshin no jutsu: Chance to clone on room entrance.
 * @author Callil
 *
 */
public class BlessingBunshin extends Blessing {

	private int chanceToProc = 25;

	@Override
	public String title() {
		return "Bunshin no jutsu";
	}
	
	@Override
	public String description() {
		return "On new room entrance, chance to summon a 10hp clone of yourself.";
	}
	
	@Override
	public RegionDescriptor texture() {
		return Assets.blessing_bunshin;
	}
	
	@Override
	public Integer getCurrentProcChance(Entity user) {
		return chanceToProc;
	}

	@Override
	public void onRoomVisited(Entity entity, Room room) {
		PlayerComponent playerComponent = Mappers.playerComponent.get(entity);
		if (!room.hasEnemies() && playerComponent != null) return;
		
		float randomValue = RandomSingleton.getNextChanceWithKarma();
		if (randomValue < getCurrentProcChance(entity)) {
			
			GridPositionComponent playerPos = Mappers.gridPositionComponent.get(entity);
			List<Tile> adjacentTiles = TileUtil.getAdjacentTiles(playerPos.coord(), room);
			for (Tile t : adjacentTiles) {
				if (t.isWalkable(entity) && TileUtil.getSolidEntityOnTile(t.getGridPos(), room) == null) {
					VFXUtil.createSmokeEffect(t.getGridPos());
					Entity clone = room.entityFactory.playerFactory.createPlayerClone(room, t.getGridPos(), GameScreen.player);
					
					
					AIComponent aiComponent = Mappers.aiComponent.get(clone);
					
					Entity target = AIUtil.findClosestTarget(t.getGridPos(), room);
					aiComponent.setAlerted(true, clone, target);
					
					break;
				}
			}
			
			Journal.addEntry("Bunshin no jutsu activated and summoned a clone");
			AlterationSystem.addAlterationProc(this);
		}
		
	}



}
