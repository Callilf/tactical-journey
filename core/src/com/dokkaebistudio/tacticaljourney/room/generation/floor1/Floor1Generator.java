/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room.generation.floor1;

import com.dokkaebistudio.tacticaljourney.factory.EntityFactory;
import com.dokkaebistudio.tacticaljourney.room.generation.FloorGenerator;

/**
 * @author Callil
 *
 */
public class Floor1Generator extends FloorGenerator {
	
	public static final int TURN_THRESHOLD = 250;

	private int roomIndex = 100;
	
	public Floor1Generator(EntityFactory ef) {
		this.setRoomGenerator(new Floor1RoomGenerator(ef));
	}
	
	@Override
	protected int getNextRoomIndex() {
		int index = roomIndex;
		roomIndex++;
		return index;
	}
	
	protected int getMinRoomNb() {
		return 15;
	}
	protected int getMaxRoomNb() {
		return 19;
	}
	
	protected int getTurnThresholdForTreasureRoom() {
		return TURN_THRESHOLD;
	}
}
