/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room.generation.floor2;

import com.dokkaebistudio.tacticaljourney.factory.EntityFactory;
import com.dokkaebistudio.tacticaljourney.room.RoomType;
import com.dokkaebistudio.tacticaljourney.room.generation.FloorGenerator;

/**
 * @author Callil
 *
 */
public class Floor2Generator extends FloorGenerator {

	public Floor2Generator(EntityFactory ef) {
		this.setRoomGenerator(new Floor2RoomGenerator(ef));
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
