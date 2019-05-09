/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room.generation.floor3;

import com.dokkaebistudio.tacticaljourney.factory.EntityFactory;
import com.dokkaebistudio.tacticaljourney.room.RoomType;
import com.dokkaebistudio.tacticaljourney.room.generation.FloorGenerator;

/**
 * @author Callil
 *
 */
public class Floor3Generator extends FloorGenerator {
	
	public static final int TURN_THRESHOLD = 300;

	private int roomIndex = 300;

	public Floor3Generator(EntityFactory ef) {
		this.setRoomGenerator(new Floor3RoomGenerator(ef));
	}
	
	@Override
	protected int getNextRoomIndex() {
		int index = roomIndex;
		roomIndex++;
		return index;
	}
	
	/**
	 * Choose the type of room to create.
	 * @return the type of room
	 */
	@Override
	protected RoomType chooseRoomType() {
		return RoomType.COMMON_ENEMY_ROOM;
	}
	
	protected int getMinRoomNb() {
		return 16;
	}
	protected int getMaxRoomNb() {
		return 22;
	}
	
	protected int getTurnThresholdForTreasureRoom() {
		return TURN_THRESHOLD;
	}
}
