/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.inventoryItems;

import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.items.AbstractItem;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomType;
import com.dokkaebistudio.tacticaljourney.singletons.AnimationSingleton;
import com.dokkaebistudio.tacticaljourney.singletons.InputSingleton;
import com.dokkaebistudio.tacticaljourney.util.AnimatedImage;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.MovementHandler;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;
import com.dokkaebistudio.tacticaljourney.vfx.VFXUtil;

/**
 * A scroll to duplicate oneself
 * @author Callil
 *
 */
public class ItemScrollOfTeleportation extends AbstractItem {
	
	public ItemScrollOfTeleportation() {
		super(ItemEnum.SCROLL_TELEPORTATION, Assets.scroll_teleportation_item, false, true);
		this.setPaper(true);
	}
	
	@Override
	public String getDescription() {
		return Descriptions.ITEM_SCROLL_TELEPORTATION_DESCRIPTION;		
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
	public boolean use(final Entity user, Entity item, final Room room) {
		if (room.type == RoomType.BOSS_ROOM) {
			Journal.addEntry("[RED]A strong energy prevents you from using the scroll of teleportation here.");
			return false;
		}
		
		final List<Room> rooms = room.floor.getRooms();
		final int roomIndex = RandomSingleton.getInstance().getUnseededRandom().nextInt(rooms.size());
		final Room newRoom = rooms.get(roomIndex);
		
		InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(user);
		inventoryComponent.remove(item);
		room.removeEntity(item);
		
		final GridPositionComponent gridPos = Mappers.gridPositionComponent.get(user);
		Mappers.spriteComponent.get(user).hide = true;
		InputSingleton.inputBlocked = true;
		
		Action firstSmokeAction = new Action(){
		  @Override
		  public boolean act(float delta){
			// Enter here when the first smoke animation is over
			 
			if (newRoom != room) {
				room.floor.enterRoom(rooms.get(roomIndex));
			}
			MovementHandler.placeEntity(GameScreen.player, new Vector2(GameScreen.GRID_W/2, 1), newRoom);
			
			Action secondSmokeAction= new Action(){
			  @Override
			  public boolean act(float delta){			
				// Enter here when the second smoke animation is over

				Mappers.spriteComponent.get(user).hide = false;
				InputSingleton.inputBlocked = false;
				
			    return true;
			  }
			};
			VFXUtil.createSmokeEffect(gridPos.coord(), secondSmokeAction);

		    return true;
		  }
		};
		VFXUtil.createSmokeEffect(gridPos.coord(), firstSmokeAction);

		
		
		Journal.addEntry("Scroll of teleportation sent you to another room.");
		
		return false;
	}


}
