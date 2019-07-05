/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room.generation.tutorial;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.loot.LootRewardComponent;
import com.dokkaebistudio.tacticaljourney.components.loot.LootableComponent;
import com.dokkaebistudio.tacticaljourney.dialog.pnjs.tutorial.CalishkaTutorial1Dialogs;
import com.dokkaebistudio.tacticaljourney.dialog.pnjs.tutorial.CalishkaTutorial2Dialogs;
import com.dokkaebistudio.tacticaljourney.dialog.pnjs.tutorial.CalishkaTutorial3Dialogs;
import com.dokkaebistudio.tacticaljourney.enums.TileEnum;
import com.dokkaebistudio.tacticaljourney.factory.EntityFactory;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.generation.GeneratedRoom;
import com.dokkaebistudio.tacticaljourney.room.generation.RoomGenerator;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;

/**
 * @author Callil
 *
 */
public class TutorialFloorRoomGenerator extends RoomGenerator {

	public TutorialFloorRoomGenerator(EntityFactory ef) {
		super(ef);
	}

	
	
	@Override
	protected FileHandle chooseRoomPattern(Room currentRoom) {		
		switch(currentRoom.type) {
		case TUTORIAL_ROOM_1:
			currentRoom.roomPattern = "data/rooms/tutorial/tutorialRoom1.csv";
			break;
		case TUTORIAL_ROOM_2:
			currentRoom.roomPattern = "data/rooms/tutorial/tutorialRoom2.csv";
			break;
		case TUTORIAL_ROOM_3:
			currentRoom.roomPattern = "data/rooms/tutorial/tutorialRoom3.csv";
			break;
		case TUTORIAL_ROOM_4:
			currentRoom.roomPattern = "data/rooms/tutorial/tutorialRoom4.csv";
			break;
			
			default:
				currentRoom.roomPattern = "data/rooms/room1.csv";
		}

		return Gdx.files.internal(currentRoom.roomPattern);
	}
	
	
	@Override
	public void generateRoomContent(Room room, GeneratedRoom generatedRoom) {
		
		switch(room.type) {
			
		case TUTORIAL_ROOM_1:

			// Movement tutorial
			entityFactory.playerFactory.createCalishka(new Vector2(5, 6), room, new CalishkaTutorial1Dialogs(), 1);
			room.closeDoors();
			break;
			
		case TUTORIAL_ROOM_2:

			// Items tutorial
			entityFactory.playerFactory.createCalishka(new Vector2(20, 7), room, new CalishkaTutorial2Dialogs(), 2);
			
			entityFactory.creepFactory.createMud(room, new Vector2(17, 6));
			entityFactory.itemFactory.createItem(ItemEnum.PEBBLE, room, new Vector2(19, 11));
			entityFactory.itemFactory.createItem(ItemEnum.PEBBLE, room, new Vector2(20, 9));
			entityFactory.itemFactory.createItem(ItemEnum.PEBBLE, room, new Vector2(21, 4));
			entityFactory.itemFactory.createItem(ItemEnum.PEBBLE, room, new Vector2(19, 3));
			
			Entity satchel = entityFactory.lootableFactory.createSatchel(room, new Vector2(11, 6));
			LootableComponent lootableComponent = Mappers.lootableComponent.get(satchel);
			List<Entity> items = new ArrayList<>();
			items.add(entityFactory.itemFactory.createItem(ItemEnum.BANANA));
			lootableComponent.setItems(items);
			
			room.closeDoors();
			break;
			
		case TUTORIAL_ROOM_3:

			// Combat tutorial
			entityFactory.playerFactory.createCalishka(new Vector2(20, 7), room, new CalishkaTutorial3Dialogs(), 3);
			room.closeDoors();
			
			entityFactory.createWall(room, new Vector2(17, 6));
			Entity spider = entityFactory.enemyFactory.spiderFactory.createSpider(room, new Vector2(8, 6));
			spider.remove(LootRewardComponent.class);
			break;

			
		case TUTORIAL_ROOM_4:

			// Combat tutorial
			entityFactory.playerFactory.createCalishka(new Vector2(20, 7), room, new CalishkaTutorial3Dialogs(), 3);
			room.closeDoors();
			
			break;
			
			default:
				super.generateRoomContent(room, generatedRoom);
				return;
		}
		
	
		// Release poolable vector2
		generatedRoom.releaseSpawns();
	}
	
	
	
	@Override
	protected void placeBush(PoolableVector2 position, GeneratedRoom groom, List<PoolableVector2> emptyTiles,
			int chance, int step) {}

	@Override
	protected void placeCreep(TileEnum type, PoolableVector2 position, GeneratedRoom groom,
			List<PoolableVector2> emptyTiles, int chance, int step) {}
	
	@Override
	protected void placeCreepRiver(TileEnum type, PoolableVector2 position, GeneratedRoom groom,
			List<PoolableVector2> emptyTiles, int chance, int step) {}

	@Override
	protected void placeDestructibles(Room room, RandomSingleton random, List<PoolableVector2> destrPositions) {}
	
	@Override
	protected void placeLootable(int lootableChancePercentage, Room room, RandomSingleton random,
			List<PoolableVector2> spawnPositions) {}
	
	@Override
	protected void placeEnemies(Room room, RandomSingleton random, List<PoolableVector2> spawnPositions, boolean canBeEmpty) {}
}
