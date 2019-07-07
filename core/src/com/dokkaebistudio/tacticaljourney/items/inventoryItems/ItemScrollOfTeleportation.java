/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.inventoryItems;

import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.utils.Array;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.items.AbstractItem;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomType;
import com.dokkaebistudio.tacticaljourney.room.RoomVisitedState;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.MovementHandler;
import com.dokkaebistudio.tacticaljourney.vfx.VFXUtil;

/**
 * A scroll to duplicate oneself
 * @author Callil
 *
 */
public class ItemScrollOfTeleportation extends AbstractItem {
	
	private static final int UNVISITED_ROOM_CHANCE_MULTIPLIER = 20;

	public ItemScrollOfTeleportation() {
		super(ItemEnum.SCROLL_TELEPORTATION, Assets.scroll_teleportation_item, false, true);
		this.setPaper(true);
		setRecyclePrice(3);
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
		
		final Room newRoom = selectNewRoom(room);
		
		InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(user);
		inventoryComponent.remove(item);
		room.removeEntity(item);
		
		final GridPositionComponent gridPos = Mappers.gridPositionComponent.get(user);
		Mappers.spriteComponent.get(user).hide = true;
		room.pauseState();
//		InputSingleton.inputBlocked = true;
		
		Action firstSmokeAction = new Action(){
		  @Override
		  public boolean act(float delta){
			// Enter here when the first smoke animation is over
			 
			if (newRoom != room) {
				room.floor.enterRoom(newRoom);
			}
			MovementHandler.placeEntity(GameScreen.player, new Vector2(GameScreen.GRID_W/2, 1), newRoom);
			
			Action secondSmokeAction= new Action(){
			  @Override
			  public boolean act(float delta){			
				// Enter here when the second smoke animation is over

				Mappers.spriteComponent.get(user).hide = false;
//				InputSingleton.inputBlocked = false;
				room.unpauseState();

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

	private Room selectNewRoom(final Room room) {
		final List<Room> rooms = room.floor.getRooms();
		
		Array<Room> visitedRooms = new Array<>();
		Array<Room> unvisitedRooms = new Array<>();		
		rooms.forEach((Room r) -> { 
			if (r.getVisited() == RoomVisitedState.NEVER_VISITED) {
				unvisitedRooms.add(r);
			} else {
				visitedRooms.add(r);
			}
		});

		
		if (unvisitedRooms.size == 0) {
			int roomIndex = RandomSingleton.getInstance().getUnseededRandom().nextInt(visitedRooms.size);
			return visitedRooms.get(roomIndex);
		} else {
			int unvisitedRoomChance = UNVISITED_ROOM_CHANCE_MULTIPLIER * unvisitedRooms.size;
			if (RandomSingleton.getNextChanceWithKarma() <= unvisitedRoomChance) {
				// Go to new room
				int roomIndex = RandomSingleton.getInstance().getUnseededRandom().nextInt(unvisitedRooms.size);
				return unvisitedRooms.get(roomIndex);
			} else {
				// Go to visited room
				int roomIndex = RandomSingleton.getInstance().getUnseededRandom().nextInt(visitedRooms.size);
				return visitedRooms.get(roomIndex);
			}
		}
	}


}
