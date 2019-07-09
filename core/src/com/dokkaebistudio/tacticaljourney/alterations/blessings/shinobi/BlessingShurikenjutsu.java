/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.blessings.shinobi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.ces.components.attack.AttackComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems.AlterationSystem;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

/**
 * Shurikenjutsu: Chance of throwing a shuriken on room entrance.
 * @author Callil
 *
 */
public class BlessingShurikenjutsu extends Blessing {

	private int chanceToProc = 33;

	@Override
	public String title() {
		return "Shurikenjutsu";
	}
	
	@Override
	public String description() {
		return "On new room entrance, chance to throw a shuriken to a random enemy. This shuriken does not alert enemies.";
	}
	
	@Override
	public RegionDescriptor texture() {
		return Assets.blessing_shurikenjutsu;
	}
	
	@Override
	public Integer getCurrentProcChance(Entity user) {
		return chanceToProc;
	}

	@Override
	public void onRoomVisited(Entity entity, Room room) {
		PlayerComponent playerComponent = Mappers.playerComponent.get(entity);
		if (!room.hasEnemies() && playerComponent != null) return;
		
		int numberToThrow = 1;
		int numberThrown = 0;
		float randomValue = RandomSingleton.getNextChanceWithKarma();
		if (randomValue < getCurrentProcChance(entity)) {
			List<Entity> enemies = new ArrayList<>(room.getEnemies());
			Collections.shuffle(enemies, RandomSingleton.getInstance().getUnseededRandom());
			
			for (Entity e : enemies) {
				GridPositionComponent enemyPos = Mappers.gridPositionComponent.get(e);
				
				Entity shuriken = room.entityFactory.itemFactory.createItem(ItemEnum.SHURIKEN);
				playerComponent.setActiveSkill(playerComponent.getSkillThrow());
				AttackComponent attackComponent = Mappers.attackComponent.get(playerComponent.getSkillThrow());
				attackComponent.setThrownEntity(shuriken);
				attackComponent.setTargetedTile(TileUtil.getTileAtGridPos(enemyPos.coord(), room));
				attackComponent.setDoNotConsumeTurn(true);
				attackComponent.setDoNotAlertTarget(true);
				
				room.setNextState(RoomState.PLAYER_THROWING);
				
				numberThrown ++;
				if (numberThrown >= numberToThrow) {
					break;
				}
			}
			
			if (numberThrown > 0) {
				Journal.addEntry("Shurikenjutsu activated and threw " + numberThrown + "shuriken(s)");
				AlterationSystem.addAlterationProc(this);
			}
		}
		
	}
	
	@Override
	public void onPlayerTurnStarts(Entity entity, Room room) {
		PlayerComponent playerComponent = Mappers.playerComponent.get(entity);
		Mappers.attackComponent.get(playerComponent.getSkillThrow()).setDoNotAlertTarget(false);
		Mappers.attackComponent.get(playerComponent.getSkillThrow()).setDoNotConsumeTurn(false);
	}

}
