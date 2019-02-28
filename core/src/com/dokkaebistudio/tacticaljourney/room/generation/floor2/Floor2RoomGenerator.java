/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room.generation.floor2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.constants.ZIndexConstants;
import com.dokkaebistudio.tacticaljourney.factory.EntityFactory;
import com.dokkaebistudio.tacticaljourney.factory.EntityFlagEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.generation.GeneratedRoom;
import com.dokkaebistudio.tacticaljourney.room.generation.RoomGenerator;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;

/**
 * @author Callil
 *
 */
public class Floor2RoomGenerator extends RoomGenerator {

	public Floor2RoomGenerator(EntityFactory ef) {
		super(ef);
	}
	
	
	@Override
	public void generateRoomContent(Room room, GeneratedRoom generatedRoom) {
		RandomXS128 random = RandomSingleton.getInstance().getSeededRandom();

		List<PoolableVector2> possibleSpawns = generatedRoom.getPossibleSpawns();
		List<PoolableVector2> spawnPositions = null;
		
		switch(room.type) {
		case COMMON_ENEMY_ROOM :
		case SHOP_ROOM:
		case STATUE_ROOM:
			super.generateRoomContent(room, generatedRoom);
			return;
			
		case KEY_ROOM:
			
			// TODO change this
			// No key atm, so that its impossible to go deeper
			
			break;
			
		case START_FLOOR_ROOM:

			entityFactory.enemyFactory.createStinger(room, new Vector2(3, 3), 3);		
			entityFactory.enemyFactory.createStinger(room, new Vector2(3, 9), 3);
			entityFactory.enemyFactory.createStinger(room, new Vector2(19, 3), 3);		
			entityFactory.enemyFactory.createStinger(room, new Vector2(19, 9), 3);
			
			entityFactory.enemyFactory.createScorpion(room, new Vector2(11, 1), 4);		
			entityFactory.enemyFactory.createScorpion(room, new Vector2(11, 11), 4);

			break;
		case END_FLOOR_ROOM:
			if (possibleSpawns.size() == 0) return;
			int nextInt = random.nextInt(possibleSpawns.size());
			Vector2 pos = possibleSpawns.get(nextInt);
			entityFactory.createExit(room, pos, false);
			default:
			break;
		}
		
		placeDestructibles(room, random, generatedRoom.getPossibleDestr());
	
	
		// Release poolable vector2
		generatedRoom.releaseSpawns();
	}

}
