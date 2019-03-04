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

	public Floor3Generator(EntityFactory ef) {
		this.setRoomGenerator(new Floor3RoomGenerator(ef));
	}
	
	/**
	 * Choose the type of room to create.
	 * @return the type of room
	 */
	@Override
	protected RoomType chooseRoomType() {
		return RoomType.COMMON_ENEMY_ROOM;
	}
	
}
