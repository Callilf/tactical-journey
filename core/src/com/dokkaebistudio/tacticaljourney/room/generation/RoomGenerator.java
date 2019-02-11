/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room.generation;

import static com.dokkaebistudio.tacticaljourney.GameScreen.GRID_H;
import static com.dokkaebistudio.tacticaljourney.GameScreen.GRID_W;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.LootRewardComponent;
import com.dokkaebistudio.tacticaljourney.components.LootableComponent;
import com.dokkaebistudio.tacticaljourney.components.TileComponent.TileEnum;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.constants.ZIndexConstants;
import com.dokkaebistudio.tacticaljourney.factory.EntityFactory;
import com.dokkaebistudio.tacticaljourney.factory.EntityFlagEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomType;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Util class to generate a room.
 * @author Callil
 *
 */
public class RoomGenerator {

	/** the entity factory. */
	private EntityFactory entityFactory;
	
	/** Private constructor.*/
	public RoomGenerator(EntityFactory ef) {
		this.entityFactory = ef;
	}
	
	
	/**
	 * Generate the tiles for the room currentRoom.
	 * @param currentRoom the room to generate
	 * @param nn the north neighbor
	 * @param en the east neighbor
	 * @param sn the south neighbor
	 * @param wn the west neighbor
	 * @return the generated room object
	 */
	public GeneratedRoom generateRoomLayout(Room currentRoom, Room nn, Room en, Room sn, Room wn) {
		GeneratedRoom groom = new GeneratedRoom();
        groom.setTileEntities(new Entity[GRID_W][GameScreen.GRID_H]);
        groom.setTileTypes(new TileEnum[GRID_W][GRID_H]);
        groom.setPossibleSpawns(new ArrayList<Vector2>());
		
		//Choose the room pattern
		RandomXS128 random = RandomSingleton.getInstance().getSeededRandom();
		FileHandle roomPattern = chooseRoomPattern(currentRoom);
		Reader reader = roomPattern.reader();
		
		//Load the pattern
		Scanner scanner = new Scanner(reader);
		
		//Fill the TileEnum array
		
		//Add a line of walls at the top
		for (int x=0 ; x < GameScreen.GRID_W ; x++) {
			groom.getTileTypes()[x][GameScreen.GRID_H - 1] = TileEnum.WALL;
		}
		
		int y = 0;
		while (scanner.hasNext() && y < GameScreen.GRID_H) {
			int realY = GameScreen.GRID_H - 1 - y;
            String[] line = scanner.nextLine().split(";");
            for (int x=0 ; x < GameScreen.GRID_W ; x++) {
            	String tileValStr = line[x];
            	RoomGenerationTileEnum tileVal = tileValStr.equals("") ? RoomGenerationTileEnum.GROUND : RoomGenerationTileEnum.valueOf(line[x]);
            	switch(tileVal) {
            	case N_DOOR :
            		groom.getTileTypes()[x][realY] = TileEnum.GROUND;
					entityFactory.createDoor(currentRoom, new Vector2(x,realY), nn);
            		break;
            	case E_DOOR :
            		groom.getTileTypes()[x][realY] = TileEnum.GROUND;
					entityFactory.createDoor(currentRoom, new Vector2(x,realY), en);
            		break;
            	case S_DOOR :
            		groom.getTileTypes()[x][realY] = TileEnum.GROUND;
					entityFactory.createDoor(currentRoom, new Vector2(x,realY), sn);
            		break;
            	case W_DOOR :
            		groom.getTileTypes()[x][realY] = TileEnum.GROUND;
					entityFactory.createDoor(currentRoom, new Vector2(x,realY), wn);
            		break;
            		
            	case WALL :
            		groom.getTileTypes()[x][realY] = TileEnum.WALL;
            		break;
            		
            	case PIT :
            		groom.getTileTypes()[x][realY] = TileEnum.PIT;
            		break;
            		
            	case SPAWN:
            		groom.getPossibleSpawns().add(new Vector2(x, realY));
            		groom.getTileTypes()[x][realY] = TileEnum.GROUND;
            		break;
            		
            		default:
            			int nextInt = random.nextInt(10);
            			if (nextInt == 0 && currentRoom.type != RoomType.SHOP_ROOM) {
            				groom.getTileTypes()[x][realY] = TileEnum.MUD;
            			} else {
            				groom.getTileTypes()[x][realY] = TileEnum.GROUND;
            			}
            	
            	}
            }
            y ++;
		}
		
        scanner.close();
        
        
        //Create the tile entities
		for (int x = 0; x < GRID_W; x++) {
			for (y = 0; y < GameScreen.GRID_H; y++) {
				Entity tileEntity = entityFactory.createTile(currentRoom, new Vector2(x, y), groom.getTileTypes()[x][y]);
				groom.getTileEntities()[x][y] = tileEntity;
			}
		}
		

		return groom;
	}


	private FileHandle chooseRoomPattern(Room currentRoom) {
		RandomXS128 random = RandomSingleton.getInstance().getSeededRandom();
		
		FileHandle roomPattern = null;
		switch(currentRoom.type) {
		case START_FLOOR_ROOM:
			roomPattern = Gdx.files.internal("data/rooms/room1.csv");

			break;
		case COMMON_ENEMY_ROOM:
		case END_FLOOR_ROOM:
			
			int roomNb = 1 + random.nextInt(10);
			roomPattern = Gdx.files.internal("data/rooms/room" + roomNb + ".csv");

			break;
		case SHOP_ROOM:
			roomPattern = Gdx.files.internal("data/rooms/shopRoom.csv");

			break;
			default:
				roomPattern = Gdx.files.internal("data/rooms/room1.csv");
		}

		return roomPattern;
	}
	
	
	
	public void generateRoomContent(Room room, GeneratedRoom generatedRoom) {
		RandomXS128 random = RandomSingleton.getInstance().getSeededRandom();

		List<Vector2> possibleSpawns = generatedRoom.getPossibleSpawns();

		switch(room.type) {
		case COMMON_ENEMY_ROOM :
			int enemyNb = random.nextInt(Math.min(possibleSpawns.size(), 5));
			
			// Retrieve the spawn points and shuffle them
			List<Vector2> enemyPositions = new ArrayList<>(possibleSpawns);
			Collections.shuffle(enemyPositions, random);
			
			// Place a loot
			int lootRandom = random.nextInt(10);
			boolean isLoot = lootRandom != 0;
			if (isLoot) {
				Vector2 lootPos = enemyPositions.get(0);
				if (lootRandom <= 5) {
					Entity bones = entityFactory.createRemainsBones(room, lootPos);
					fillLootable(bones, 1);
					
				} else {
					Entity satchel = entityFactory.createRemainsSatchel(room, lootPos);
					fillLootable(satchel, 2);

				}
				enemyPositions.remove(0);
			}
			
			// Place enemies
			Iterator<Vector2> iterator = enemyPositions.iterator();
			for (int i=0 ; i<enemyNb ; i++) {
				Entity enemy = null;
				if (random.nextInt(5) == 0) {
					enemy = entityFactory.enemyFactory.createScorpion(room, new Vector2(iterator.next()), 4);
				} else {
					enemy = entityFactory.enemyFactory.createSpider(room, new Vector2(iterator.next()), 3);
				}
				
				LootRewardComponent lootRewardComponent = Mappers.lootRewardComponent.get(enemy);
				lootRewardComponent.setDrop( generateEnemyLoot(lootRewardComponent.getDropRate()));
				
				iterator.remove();
			}
			
			break;
			
		case SHOP_ROOM:
			entityFactory.playerFactory.createShopkeeper(new Vector2(11, 7), room);

			entityFactory.createSpriteOnTile(new Vector2(9, 5), 
					ZIndexConstants.WALL, 
					Assets.getTexture(Assets.shop_item_background), 
					EntityFlagEnum.SHOP_ITEM_BACKGROUND, room);
			Entity firstItem = entityFactory.itemFactory.createItemHealthUp(room, new Vector2(9, 5));
			ItemComponent ic = Mappers.itemComponent.get(firstItem);
			ic.setPrice(5);
			
			entityFactory.createSpriteOnTile(new Vector2(11, 5), 
					ZIndexConstants.WALL, 
					Assets.getTexture(Assets.shop_item_background), 
					EntityFlagEnum.SHOP_ITEM_BACKGROUND, room);
			Entity secondItem = entityFactory.itemFactory.createItemArrows(room, new Vector2(11, 5));
			ic = Mappers.itemComponent.get(secondItem);
			ic.setQuantity(5);
			ic.setPrice(10);

			entityFactory.createSpriteOnTile(new Vector2(13, 5), 
					ZIndexConstants.WALL, 
					Assets.getTexture(Assets.shop_item_background), 
					EntityFlagEnum.SHOP_ITEM_BACKGROUND, room);
			Entity thirdItem = entityFactory.itemFactory.createItemBombs(room, new Vector2(13, 5));
			ic = Mappers.itemComponent.get(thirdItem);
			ic.setQuantity(2);
			ic.setPrice(6);

			
			
			break;
			
		case START_FLOOR_ROOM:
			
			entityFactory.itemFactory.createItemHealthUp(room, new Vector2(5, 3));
			entityFactory.itemFactory.createItemLightArmor(room, new Vector2(5, 5));
			entityFactory.itemFactory.createItemArmorPiece(room, new Vector2(6, 5));

			
			//entityFactory.itemFactory.createItemArrows(room, new Vector2(16, 8));
			//Entity money = entityFactory.itemFactory.createItemMoney(room, new Vector2(9, 10));
			//Mappers.itemComponent.get(money).setQuantity(10);
			
			entityFactory.itemFactory.createItemTutorialPage(1,room, new Vector2(8, 9));
			entityFactory.itemFactory.createItemTutorialPage(2,room, new Vector2(8, 8));
			entityFactory.itemFactory.createItemTutorialPage(3,room, new Vector2(8, 7));
			entityFactory.itemFactory.createItemTutorialPage(4,room, new Vector2(8, 6));

			Entity bones = entityFactory.createRemainsBones(room, new Vector2(12, 9));
			fillLootable(bones, 1);
			
			
//			entityFactory.createExit(this, new Vector2(16, 4));
//			Entity enemy = entityFactory.enemyFactory.createScorpion(room, new Vector2(14, 5), 4);			
//			Entity enemy2 = entityFactory.enemyFactory.createSpider(room, new Vector2(10, 8), 1);
//			LootRewardComponent lootRewardComponent = Mappers.lootRewardComponent.get(enemy2);
//			lootRewardComponent.setDrop( generateEnemyLoot(100f));
//			Entity enemy3 = entityFactory.enemyFactory.createSpider(this, new Vector2(12, 8), 3);
			
			break;
		case END_FLOOR_ROOM:
			int nextInt = random.nextInt(possibleSpawns.size());
			Vector2 pos = possibleSpawns.get(nextInt);
			entityFactory.createExit(room, pos);
			default:
			break;
		}
	}
	
	private void fillLootable(Entity lootable, int nbMaxItems) {
		LootableComponent lootableComponent = Mappers.lootableComponent.get(lootable);
		
		RandomXS128 random = RandomSingleton.getInstance().getSeededRandom();
		
		boolean isMoney = random.nextInt(2) == 0;
		if (isMoney) {
			Entity moneyItem = entityFactory.itemFactory.createItemMoney(null, null);
			lootableComponent.getItems().add(moneyItem);
		}
		
		int nbLoot = random.nextInt(nbMaxItems + 1);
		if (nbLoot > 0) {
			for (int i=0 ; i<nbLoot ; i++) {
				int nextInt = random.nextInt(4);
				Entity item = null;
				
				if (nextInt == 0) {
					item = entityFactory.itemFactory.createItemHealthUp(null, null);
				} else if (nextInt == 1) {
					item = entityFactory.itemFactory.createItemArrows( null, null);
				} else if (nextInt == 2) {
					item = entityFactory.itemFactory.createItemBombs( null, null);
				} else if (nextInt == 3) {
					item = entityFactory.itemFactory.createItemTutorialPage( 1 +random.nextInt(4), null, null);
				}
				lootableComponent.getItems().add(item);
			}
		}
	}
	
	private Entity generateEnemyLoot(float dropRate) {
		RandomXS128 random = RandomSingleton.getInstance().getSeededRandom();
		
		float unit = (float) random.nextInt(100);
		float decimal = random.nextFloat();
		float randomValue = unit + decimal;
		
		if (randomValue <= dropRate) {
			int nextInt = random.nextInt(5);
			if (nextInt == 0) {
				return entityFactory.itemFactory.createItemMoney(null, null);
			} else if (nextInt == 1) {
				return entityFactory.itemFactory.createItemHealthUp(null, null);
			} else if (nextInt == 2) {
				return entityFactory.itemFactory.createItemArrows(null, null);
			} else if (nextInt == 3) {
				return entityFactory.itemFactory.createItemBombs(null, null);
			} else if (nextInt == 4) {
				return entityFactory.itemFactory.createItemTutorialPage(1 + random.nextInt(4), null, null);
			}
		}
		
		return null;
	}
}
