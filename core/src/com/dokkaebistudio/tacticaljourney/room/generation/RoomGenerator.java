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
import java.util.Map.Entry;
import java.util.Scanner;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.TileComponent.TileEnum;
import com.dokkaebistudio.tacticaljourney.components.loot.DropRate;
import com.dokkaebistudio.tacticaljourney.components.loot.DropRate.ItemPoolRarity;
import com.dokkaebistudio.tacticaljourney.components.loot.LootRewardComponent;
import com.dokkaebistudio.tacticaljourney.components.loot.LootableComponent;
import com.dokkaebistudio.tacticaljourney.constants.ZIndexConstants;
import com.dokkaebistudio.tacticaljourney.factory.EntityFactory;
import com.dokkaebistudio.tacticaljourney.factory.EntityFlagEnum;
import com.dokkaebistudio.tacticaljourney.items.pools.PooledItemDescriptor;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.EnemyItemPool;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomType;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;

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
        groom.setPossibleSpawns(new ArrayList<PoolableVector2>());
        groom.setPossibleDestr(new ArrayList<PoolableVector2>());

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
            	RoomGenerationTileEnum tileVal = null;
				try {
					tileVal = tileValStr.equals("") ? RoomGenerationTileEnum.GROUND : RoomGenerationTileEnum.valueOf(line[x]);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	
				PoolableVector2 tempPos = PoolableVector2.create(x,realY);

            	switch(tileVal) {
            	case N_DOOR :
            		groom.getTileTypes()[x][realY] = TileEnum.GROUND;
					entityFactory.createDoor(currentRoom, tempPos, nn);
            		break;
            	case E_DOOR :
            		groom.getTileTypes()[x][realY] = TileEnum.GROUND;
					entityFactory.createDoor(currentRoom, tempPos, en);
            		break;
            	case S_DOOR :
            		groom.getTileTypes()[x][realY] = TileEnum.GROUND;
					entityFactory.createDoor(currentRoom, tempPos, sn);
            		break;
            	case W_DOOR :
            		groom.getTileTypes()[x][realY] = TileEnum.GROUND;
					entityFactory.createDoor(currentRoom, tempPos, wn);
            		break;
            		
            	case WALL :
            		groom.getTileTypes()[x][realY] = TileEnum.WALL;
            		break;
            		
            	case PIT :
            		groom.getTileTypes()[x][realY] = TileEnum.PIT;
            		break;
            		
            	case SPAWN:
            		groom.getPossibleSpawns().add(PoolableVector2.create(tempPos));
            		groom.getTileTypes()[x][realY] = TileEnum.GROUND;
            		break;
            		
            	case DESTR:
            		groom.getPossibleDestr().add(PoolableVector2.create(tempPos));
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
            	tempPos.free();
            }
            y ++;
		}
		
        scanner.close();
        
        
        //Create the tile entities
		for (int x = 0; x < GRID_W; x++) {
			for (y = 0; y < GameScreen.GRID_H; y++) {
				PoolableVector2 tempPos = PoolableVector2.create(x, y);
				Entity tileEntity = entityFactory.createTile(currentRoom, tempPos, groom.getTileTypes()[x][y]);
				groom.getTileEntities()[x][y] = tileEntity;
				tempPos.free();
			}
		}
		

		return groom;
	}


	private FileHandle chooseRoomPattern(Room currentRoom) {
		RandomXS128 random = RandomSingleton.getInstance().getSeededRandom();
		
		switch(currentRoom.type) {
		case START_FLOOR_ROOM:
			currentRoom.roomPattern = "data/rooms/room1.csv";

			break;
		case COMMON_ENEMY_ROOM:
		case KEY_ROOM:
		case END_FLOOR_ROOM:
			
			int roomNb = 1 + random.nextInt(11);
			currentRoom.roomPattern = "data/rooms/room" + roomNb + ".csv";

			break;
		case SHOP_ROOM:
			currentRoom.roomPattern = "data/rooms/shopRoom.csv";
			break;

		case STATUE_ROOM:
			int statueRoomNb = 1 + random.nextInt(2);
			currentRoom.roomPattern = "data/rooms/statueRoom" + statueRoomNb + ".csv";

			break;
			default:
				currentRoom.roomPattern = "data/rooms/room1.csv";
		}

		return Gdx.files.internal(currentRoom.roomPattern);
	}
	
	
	
	public void generateRoomContent(Room room, GeneratedRoom generatedRoom) {
		RandomXS128 random = RandomSingleton.getInstance().getSeededRandom();

		List<PoolableVector2> possibleSpawns = generatedRoom.getPossibleSpawns();
		List<PoolableVector2> spawnPositions = null;
		
		switch(room.type) {
		case COMMON_ENEMY_ROOM :
			if (possibleSpawns.size() == 0) return;
			
			// Retrieve the spawn points and shuffle them
			spawnPositions = new ArrayList<>(possibleSpawns);
			Collections.shuffle(spawnPositions, random);
			
			// Place a loot
			int lootRandom = random.nextInt(10);
			boolean isLoot = lootRandom != 0;
			if (isLoot) {
				Vector2 lootPos = spawnPositions.get(0);
				if (lootRandom <= 5) {
					Entity bones = entityFactory.lootableFactory.createRemainsBones(room, lootPos);
					fillLootable(bones);
					
				} else {
					Entity satchel = entityFactory.lootableFactory.createRemainsSatchel(room, lootPos);
					fillLootable(satchel);

				}
				spawnPositions.remove(0);
			}
			

			// Place enemies
			placeEnemies(room, random, spawnPositions, false);
			
			break;
			
		case KEY_ROOM:
			if (possibleSpawns.size() == 0) return;
			
			// Retrieve the spawn points and shuffle them
			spawnPositions = new ArrayList<>(possibleSpawns);
			Collections.shuffle(spawnPositions, random);
			
			entityFactory.itemFactory.createItemKey(room, spawnPositions.get(0));
			spawnPositions.remove(0);
			
			placeEnemies(room, random, spawnPositions, false);
			break;
			
		case SHOP_ROOM:
			Entity shopKeeper = entityFactory.playerFactory.createShopkeeper(new Vector2(11, 7), room);
			
			entityFactory.createSpriteOnTile(new Vector2(9, 5), 
					ZIndexConstants.WALL, 
					Assets.shop_item_background, 
					EntityFlagEnum.SHOP_ITEM_BACKGROUND, room);
			
			entityFactory.createSpriteOnTile(new Vector2(11, 5), 
					ZIndexConstants.WALL, 
					Assets.shop_item_background, 
					EntityFlagEnum.SHOP_ITEM_BACKGROUND, room);

			entityFactory.createSpriteOnTile(new Vector2(13, 5), 
					ZIndexConstants.WALL, 
					Assets.shop_item_background, 
					EntityFlagEnum.SHOP_ITEM_BACKGROUND, room);
			
			break;
			
		case STATUE_ROOM:
			entityFactory.playerFactory.createGodessStatue(new Vector2(11, 6), room);
			
			if (possibleSpawns.size() == 0) return;
			// Retrieve the spawn points and shuffle them
			spawnPositions = new ArrayList<>(possibleSpawns);
			Collections.shuffle(spawnPositions, random);
			placeEnemies(room, random, spawnPositions, true);
			break;
			
		case START_FLOOR_ROOM:
			
//			entityFactory.itemFactory.createItemKey(room, new Vector2(12, 6));
//			entityFactory.playerFactory.createGodessStatue(new Vector2(12, 6), room);
			
//			entityFactory.itemFactory.createItemVigor(room, new Vector2(10, 10));
//			entityFactory.itemFactory.createItemFrailty(room, new Vector2(9, 10));
			
//			entityFactory.itemFactory.createItemWebSack(room, new Vector2(10, 9));
//			entityFactory.itemFactory.createItemWebSack(room, new Vector2(10, 8));

//			entityFactory.itemFactory.createItemFirePotion(room, new Vector2(5, 4));
//			
//			entityFactory.itemFactory.createItemHealthUp(room, new Vector2(5, 3));
//			entityFactory.itemFactory.createItemLightArmor(room, new Vector2(5, 5));
//			entityFactory.itemFactory.createItemArmorPiece(room, new Vector2(6, 5));
//
//			
//			entityFactory.itemFactory.createItemArrows(room, new Vector2(16, 8));
//			entityFactory.itemFactory.createItemBombs(room, new Vector2(17, 8));
//
//			Entity money = entityFactory.itemFactory.createItemMoney(room, new Vector2(9, 10));
//			Mappers.itemComponent.get(money).setQuantity(10);
			
			entityFactory.itemFactory.createItemTutorialPage(1,room, new Vector2(8, 9));
			entityFactory.itemFactory.createItemTutorialPage(2,room, new Vector2(8, 8));
			entityFactory.itemFactory.createItemTutorialPage(3,room, new Vector2(8, 7));
			entityFactory.itemFactory.createItemTutorialPage(4,room, new Vector2(8, 6));

//			Entity bones = entityFactory.createRemainsBones(room, new Vector2(12, 9));
//			fillLootable(bones);
//			Entity satchel = entityFactory.createRemainsSatchel(room, new Vector2(13, 9));
//			fillLootable(satchel);

			
//			entityFactory.createExit(this, new Vector2(16, 4));
//			Entity enemy = entityFactory.enemyFactory.createScorpion(room, new Vector2(14, 5), 4);			
//			Entity enemy4 = entityFactory.enemyFactory.createSpider(room, new Vector2(11, 8), 1);
			
//			Entity enemy2 = entityFactory.enemyFactory.createSpider(room, new Vector2(10, 8), 1);
//			LootRewardComponent lootRewardComponent = Mappers.lootRewardComponent.get(enemy2);
//			lootRewardComponent.setDrop( generateEnemyLoot(100f));
			
//			Entity enemy3 = entityFactory.enemyFactory.createSpider(room, new Vector2(12, 8), 1);
//			entityFactory.enemyFactory.createSpiderWeb(	room, new Vector2(14, 5), 3);
			
//			entityFactory.creepFactory.createFire(room, new Vector2(15, 6), null);
//			entityFactory.creepFactory.createWeb(room, new Vector2(16,6));
//			entityFactory.creepFactory.createWeb(room, new Vector2(17,6));

			
			

			
			break;
		case END_FLOOR_ROOM:
			if (possibleSpawns.size() == 0) return;
			int nextInt = random.nextInt(possibleSpawns.size());
			Vector2 pos = possibleSpawns.get(nextInt);
			entityFactory.createExit(room, pos);
			default:
			break;
		}
		
		placeDestructibles(room, random, generatedRoom.getPossibleDestr());
	
	
		// Release poolable vector2
		generatedRoom.releaseSpawns();
	}


	/**
	 * Generate a random number of enemies and place them in a room.
	 * @param room the room
	 * @param random the random
	 * @param spawnPositions the possible spawn positions
	 * @param canBeEmpty true if there can be no enemies
	 */
	private void placeEnemies(Room room, RandomXS128 random, List<PoolableVector2> spawnPositions, boolean canBeEmpty) {
		if (spawnPositions.size() == 0) return;

		int enemyNb = random.nextInt(Math.min(spawnPositions.size(), 5));
		if (enemyNb == 0 && !canBeEmpty) enemyNb = 1;
		
		Iterator<PoolableVector2> iterator = spawnPositions.iterator();
		for (int i=0 ; i<enemyNb ; i++) {
			if (!iterator.hasNext()) break;
			
			Entity enemy = null;
			int enemyTypeRandom = random.nextInt(6);
			if (enemyTypeRandom == 0) {
				enemy = entityFactory.enemyFactory.createScorpion(room, new Vector2(iterator.next()), 4);
				iterator.remove();
			} else if (enemyTypeRandom == 1) {
				enemy = entityFactory.enemyFactory.createSpiderWeb(room, new Vector2(iterator.next()), 4);
				iterator.remove();
				if (iterator.hasNext()) {
					enemy = entityFactory.enemyFactory.createSpider(room, new Vector2(iterator.next()), 3);
				}
			} else {
				enemy = entityFactory.enemyFactory.createSpider(room, new Vector2(iterator.next()), 3);
				iterator.remove();
			}
			
			LootRewardComponent lootRewardComponent = Mappers.lootRewardComponent.get(enemy);
			lootRewardComponent.setDrop( generateEnemyLoot(lootRewardComponent.getItemPool(), lootRewardComponent.getDropRate()));
		}
	}
	
	/**
	 * Scan all destructible possible locations and randomly place a destructible on it
	 * @param room the room
	 * @param random the random
	 * @param destrPositions the possible spawn positions for destructibles
	 */
	private void placeDestructibles(Room room, RandomXS128 random, List<PoolableVector2> destrPositions) {
		if (destrPositions == null || destrPositions.isEmpty()) return;

		Iterator<PoolableVector2> iterator = destrPositions.iterator();
		while (iterator.hasNext()) {
			PoolableVector2 location = iterator.next();
			
			int nextInt = random.nextInt(10);
			Entity destructible = null;
			
			if (nextInt <= 3) {
				destructible = entityFactory.createVase(room, location);
			} else if (nextInt <= 4) {
				//TODO place big vase
				continue;
			} else {
				continue;
			}
	
			LootRewardComponent lootRewardComponent = Mappers.lootRewardComponent.get(destructible);
			lootRewardComponent.setDrop( generateEnemyLoot(lootRewardComponent.getItemPool(), lootRewardComponent.getDropRate()));
		}
	}
	
	private void fillLootable(Entity lootable) {
		LootableComponent lootableComponent = Mappers.lootableComponent.get(lootable);
		
		RandomXS128 random = RandomSingleton.getInstance().getSeededRandom();
		
		int nbLoot = random.nextInt(lootableComponent.getMaxNumberOfItems() + 1);
		if (nbLoot > 0) {
				List<PooledItemDescriptor> itemTypes = lootableComponent.getItemPool().getItemTypes(nbLoot);
				for (PooledItemDescriptor pid : itemTypes) {
					Entity item = entityFactory.itemFactory.createItem(pid.getType(), null, null);
					lootableComponent.getItems().add(item);
				}
			
		}
	}
	
	private Entity generateEnemyLoot(EnemyItemPool itemPool, DropRate dropRate) {
		RandomXS128 random = RandomSingleton.getInstance().getSeededRandom();
		
		float unit = (float) random.nextInt(100);
		float decimal = random.nextFloat();
		float randomValue = unit + decimal;
		
		int chance = 0;
		ItemPoolRarity rarity = null;
		for (Entry<ItemPoolRarity, Integer> entry : dropRate.getRatePerRarity().entrySet()) {
			if (randomValue >= chance && randomValue < chance + entry.getValue().intValue()) {
				rarity = entry.getKey();
				break;
			}
			chance += entry.getValue().intValue();
		}
		
		if (rarity != null) {
			List<PooledItemDescriptor> itemTypes = itemPool.getItemTypes(1, rarity);
			PooledItemDescriptor itemType = itemTypes.get(0);
			
			return entityFactory.itemFactory.createItem(itemType.getType(), null, null);
		}
		
		return null;
	}
}
