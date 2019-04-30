/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.inventoryItems;

import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.components.AIComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.items.AbstractItem;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.Tile;
import com.dokkaebistudio.tacticaljourney.util.AIUtil;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;
import com.dokkaebistudio.tacticaljourney.vfx.VFXUtil;

/**
 * A scroll to duplicate oneself
 * @author Callil
 *
 */
public class ItemScrollOfDoppelganger extends AbstractItem {

	public ItemScrollOfDoppelganger() {
		super(ItemEnum.SCROLL_DOPPELGANGER, Assets.scroll_doppelganger_item, false, true);
		this.setPaper(true);
	}
	
	@Override
	public String getDescription() {
		return Descriptions.ITEM_SCROLL_DOPPELGANGER_DESCRIPTION;		
	}
	
	@Override
	public String getActionLabel() {
		return "Read";
	}
	
	@Override
	public boolean isStackable() {
		return true;
	}
	
	@Override
	public boolean use(Entity user, Entity item, Room room) {
		if (!room.hasEnemies()) {
			Journal.addEntry("Scroll of Doppelganger cannot be used in a cleared room");
			return false;
		}
			
		GridPositionComponent playerPos = Mappers.gridPositionComponent.get(user);
		List<Tile> adjacentTiles = TileUtil.getAdjacentTiles(playerPos.coord(), room);
		for (Tile t : adjacentTiles) {
			if (t.isWalkable(user) && TileUtil.getSolidEntityOnTile(t.getGridPos(), room) == null) {
				VFXUtil.createSmokeEffect(t.getGridPos());
				Entity clone = room.entityFactory.playerFactory.createPlayerClone(room, t.getGridPos(), GameScreen.player);
				
				AIComponent aiComponent = Mappers.aiComponent.get(clone);
				
				Entity target = AIUtil.findClosestTarget(t.getGridPos(), room);
				aiComponent.setAlerted(true, clone, target);
				
				break;
			}
		}
		
		Journal.addEntry("Scroll of Doppelganger summoned a clone");
		
		return true;
	}

}
