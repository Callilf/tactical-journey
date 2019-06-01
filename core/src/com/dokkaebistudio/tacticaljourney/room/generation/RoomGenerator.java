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
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.loot.DropRate.ItemPoolRarity;
import com.dokkaebistudio.tacticaljourney.components.neutrals.ChaliceComponent.ChaliceType;
import com.dokkaebistudio.tacticaljourney.constants.ZIndexConstants;
import com.dokkaebistudio.tacticaljourney.creature.enemies.enums.EnemyTypeEnum;
import com.dokkaebistudio.tacticaljourney.enums.TileEnum;
import com.dokkaebistudio.tacticaljourney.factory.EntityFactory;
import com.dokkaebistudio.tacticaljourney.factory.EntityFlagEnum;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.items.pools.ItemPoolSingleton;
import com.dokkaebistudio.tacticaljourney.items.pools.PooledItemDescriptor;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomType;
import com.dokkaebistudio.tacticaljourney.room.Tile;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

/**
 * Util class to generate a room.
 * @author Callil
 *
 */
public abstract class RoomGenerator {

	/** the entity factory. */
	protected EntityFactory entityFactory;
	
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
        groom.setTiles(new Tile[GRID_W][GameScreen.GRID_H]);
        groom.setTileTypes(new TileEnum[GRID_W][GRID_H]);
        groom.setPossibleSpawns(new ArrayList<PoolableVector2>());
        groom.setPossibleDestr(new ArrayList<PoolableVector2>());

		//Choose the room pattern
		RandomSingleton random = RandomSingleton.getInstance();
		FileHandle roomPattern = chooseRoomPattern(currentRoom);
		Reader reader = roomPattern.reader();
		
		//Load the pattern
		Scanner scanner = new Scanner(reader);
		
		//Fill the TileEnum array
		
		//Add a line of walls at the top
		for (int x=0 ; x < GameScreen.GRID_W ; x++) {
			groom.getTileTypes()[x][GameScreen.GRID_H - 1] = TileEnum.WALL;
		}
		
		List<PoolableVector2> emptyTiles = new ArrayList<>();
		
		Entity createdDoor = null;
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
            		if (nn != null) {
            			groom.getTileTypes()[x][realY] = TileEnum.GROUND;
            			createdDoor = entityFactory.createDoor(currentRoom, tempPos, nn);
            		} else {
                		groom.getTileTypes()[x][realY] = TileEnum.H_WALL;
            		}
            		break;
            	case E_DOOR :
            		if (en != null) {
                		groom.getTileTypes()[x][realY] = TileEnum.GROUND;
                		createdDoor = entityFactory.createDoor(currentRoom, tempPos, en);
            		} else {
                		groom.getTileTypes()[x][realY] = TileEnum.H_WALL;
            		}
            		break;

            	case S_DOOR :
            		if (sn != null) {
            			groom.getTileTypes()[x][realY] = TileEnum.GROUND;
            			createdDoor = entityFactory.createDoor(currentRoom, tempPos, sn);
            		} else {
                		groom.getTileTypes()[x][realY] = TileEnum.H_WALL;
            		}
            		break;

            	case W_DOOR :
            		if (wn != null) {
                		groom.getTileTypes()[x][realY] = TileEnum.GROUND;
                		createdDoor = entityFactory.createDoor(currentRoom, tempPos, wn);
            		} else {
                		groom.getTileTypes()[x][realY] = TileEnum.H_WALL;
            		}
            		break;

            	case H_WALL :
            		groom.getTileTypes()[x][realY] = TileEnum.H_WALL;
            		break;
            	case WALL :
            		groom.getTileTypes()[x][realY] = TileEnum.WALL;
            		break;
            		
            	case PIT :
            		groom.getTileTypes()[x][realY] = TileEnum.PIT;
            		break;
            		
            	case BUSH:
            		int nextSeededInt = random.nextSeededInt(20);
            		if (nextSeededInt == 0) {
                		groom.getTileTypes()[x][realY] = TileEnum.VINES_BUSH;
            		} else {
                		groom.getTileTypes()[x][realY] = TileEnum.BUSH;
            		}
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
        				groom.getTileTypes()[x][realY] = TileEnum.GROUND;
        				emptyTiles.add(PoolableVector2.create(x,realY));

            	}
            	tempPos.free();
            	
            	if (createdDoor != null) {
            		currentRoom.addDoor(createdDoor);
            		createdDoor = null;
            	}
            }
            y ++;
		}
		
        scanner.close();
        
        if (currentRoom.type != RoomType.SHOP_ROOM) {
        	int max = (int) Math.ceil(emptyTiles.size() / 50);
	        int mudRandom = random.nextSeededInt(Math.max(max, 1));
	        for (int i=0 ; i< mudRandom ; i++) {
	        	int start = random.nextSeededInt(emptyTiles.size());
	        	PoolableVector2 startPos = emptyTiles.get(start);
	        	placeCreepRiver(TileEnum.MUD, startPos, groom, emptyTiles, 100, 10);
	        }
	        
	        int bushRandom = random.nextSeededInt(Math.max(max, 1));
	        for (int i=0 ; i< bushRandom ; i++) {
	        	int start = random.nextSeededInt(emptyTiles.size());
	        	PoolableVector2 startPos = emptyTiles.get(start);
	        	placeBush(startPos, groom, emptyTiles, 70, 10);
	        }
        }
        
        //Create the tile entities
		for (int x = 0; x < GRID_W; x++) {
			for (y = 0; y < GameScreen.GRID_H; y++) {
				Vector2 pos = new Vector2(x, y);
				groom.getTiles()[x][y] = new Tile(currentRoom, pos);
				entityFactory.createTerrain(currentRoom, pos, groom.getTileTypes()[x][y]);
			}
		}
		

		return groom;
	}

	protected FileHandle chooseRoomPattern(Room currentRoom) {
		RandomSingleton random = RandomSingleton.getInstance();
		
		switch(currentRoom.type) {
		case START_FLOOR_ROOM:
			currentRoom.roomPattern = "data/rooms/room1.csv";

			break;
		case COMMON_ENEMY_ROOM:
		case KEY_ROOM:	
		case END_FLOOR_ROOM:
			int roomNb = 1 + random.nextSeededInt(14);
			currentRoom.roomPattern = "data/rooms/room" + roomNb + ".csv";

			break;
		case SHOP_ROOM:
			currentRoom.roomPattern = "data/rooms/shopRoom.csv";
			break;

		case STATUE_ROOM:
			int statueRoomNb = 1 + random.nextSeededInt(2);
			currentRoom.roomPattern = "data/rooms/statueRoom" + statueRoomNb + ".csv";
			break;
			
		case GIFT_ROOM:
			int giftRoomNbr = 1 + random.nextSeededInt(3);
			currentRoom.roomPattern = "data/rooms/giftRoom" + giftRoomNbr + ".csv";
			break;
			
		case CHALICE_ROOM:
		case MINI_BOSS_ROOM:
			default:
				currentRoom.roomPattern = "data/rooms/room1.csv";
		}

		return Gdx.files.internal(currentRoom.roomPattern);
	}
	
	
	
	public void generateRoomContent(Room room, GeneratedRoom generatedRoom) {
		RandomSingleton random = RandomSingleton.getInstance();

		List<PoolableVector2> possibleSpawns = generatedRoom.getPossibleSpawns();
		List<PoolableVector2> spawnPositions = null;
		
		switch(room.type) {
		case COMMON_ENEMY_ROOM :
			if (possibleSpawns.size() == 0) return;
			
			// Retrieve the spawn points and shuffle them
			spawnPositions = new ArrayList<>(possibleSpawns);
			Collections.shuffle(spawnPositions, random.getNextSeededRandom());
			
			// Place a loot
			placeLootable(90, room, random, spawnPositions);
			
			// Place random potions to simplify the game
//			boolean hasPotion = random.nextSeededInt(5) == 0;
//			if (hasPotion) {
//				entityFactory.itemFactory.createItem(ItemEnum.POTION_SMALL_HEALTH, room, spawnPositions.get(0));
//				spawnPositions.remove(0);
//			}
			
			// Place enemies
			placeEnemies(room, random, spawnPositions, false);
			

			break;
			
		case ITEM_ROOM:
			// Retrieve the spawn points and shuffle them
			spawnPositions = new ArrayList<>(possibleSpawns);
			Collections.shuffle(spawnPositions, random.getNextSeededRandom());

			Vector2 lootPos = spawnPositions.get(0);
			Entity belongings = entityFactory.lootableFactory.createPersonalBelongings(room, lootPos);
			spawnPositions.remove(0);
			
			Vector2 secretDoorPos = spawnPositions.get(0);
			room.setSecretDoor(entityFactory.createSecretDoor(room, secretDoorPos));
			spawnPositions.remove(0);
			
			// Place enemies
			placeEnemies(room, random, spawnPositions, false);
			break;
			
		case KEY_ROOM:
			if (possibleSpawns.size() == 0) return;
			
			// Retrieve the spawn points and shuffle them
			spawnPositions = new ArrayList<>(possibleSpawns);
			Collections.shuffle(spawnPositions, random.getNextSeededRandom());
			
			entityFactory.itemFactory.createItemKey(room, spawnPositions.get(0));
			spawnPositions.remove(0);
			
			secretDoorPos = spawnPositions.get(0);
			room.setSecretDoor(entityFactory.createSecretDoor(room, secretDoorPos));
			spawnPositions.remove(0);
			
			placeEnemies(room, random, spawnPositions, false);
			break;
			
		case SHOP_ROOM:
			entityFactory.playerFactory.createShopkeeper(new Vector2(11, 7), room);
			
			Entity carpet = entityFactory.createSpriteOnTile(new Vector2(11,5), 
					ZIndexConstants.TILE,
					Assets.shopkeeper_carpet, 
					EntityFlagEnum.SHOP_CARPET, room);
			Mappers.gridPositionComponent.get(carpet).overlap = 0;
			
			room.setSecretDoor(entityFactory.createSecretDoor(room, new Vector2(16, 8)));
			entityFactory.createRecyclingMachine(room, new Vector2(6, 8));
			
			break;
			
		case STATUE_ROOM:
			boolean needsTwoExplosions = random.nextSeededInt(2) == 0;
			entityFactory.playerFactory.createGoddessStatue(new Vector2(11, 6), room, needsTwoExplosions);
			
			if (possibleSpawns.size() == 0) return;
			// Retrieve the spawn points and shuffle them
			spawnPositions = new ArrayList<>(possibleSpawns);
			Collections.shuffle(spawnPositions, random.getNextSeededRandom());
			
			secretDoorPos = spawnPositions.get(0);
			room.setSecretDoor(entityFactory.createSecretDoor(room, secretDoorPos));
			spawnPositions.remove(0);
			
			placeEnemies(room, random, spawnPositions, true);
			break;
			
		case GIFT_ROOM:
			List<PooledItemDescriptor> itemTypes = ItemPoolSingleton.getInstance().personalBelongings.getItemTypes(1, ItemPoolRarity.COMMON, 
					RandomSingleton.getInstance().getNextSeededRandom());
			entityFactory.itemFactory.createItem(itemTypes.get(0).getType(), room, possibleSpawns.get(0));
			break;
			
		case CHALICE_ROOM:

			int chaliceTypeIndex = random.nextSeededInt(ChaliceType.values().length);
			entityFactory.playerFactory.createChalice(new Vector2(11,6), room, ChaliceType.values()[chaliceTypeIndex]);
			
			if (possibleSpawns.size() == 0) return;
			// Retrieve the spawn points and shuffle them
			spawnPositions = new ArrayList<>(possibleSpawns);
			Collections.shuffle(spawnPositions, random.getNextSeededRandom());
			
			secretDoorPos = spawnPositions.get(0);
			room.setSecretDoor(entityFactory.createSecretDoor(room, secretDoorPos));
			spawnPositions.remove(0);
			
			placeEnemies(room, random, spawnPositions, true);
			break;
			
		case MINI_BOSS_ROOM:
			
			int miniBossRandom = random.nextSeededInt(2);
			if (miniBossRandom == 0) {
				entityFactory.enemyFactory.createShinobi(room, new Vector2(11, 6), false);
			} else {
				entityFactory.enemyFactory.createOrangutanAlpha(room, new Vector2(11, 6));
			}
			break;
			
		case START_FLOOR_ROOM:
			
//			entityFactory.createChasm(room, new Vector2(10,10));
//			entityFactory.createChasm(room, new Vector2(11,10));
//			entityFactory.createChasm(room, new Vector2(12,10));
//			entityFactory.createWoodenPanel(room, new Vector2(15, 8));
//			Entity createEnemy = entityFactory.enemyFactory.createEnemy(EnemyTypeEnum.ORANGUTAN, room, new Vector2(14, 7));
//			Mappers.lootRewardComponent.get(createEnemy).getDropRate().getRatePerRarity().put(ItemPoolRarity.COMMON, 100f);
			
//			entityFactory.enemyFactory.createEnemy(EnemyTypeEnum.TRIBESMAN_SPEAR, room, new Vector2(10, 10));

//			Entity createAmmoCrate = entityFactory.createAmmoCrate(room, new Vector2(12,10));
//			LootRewardComponent lootRewardComponent = Mappers.lootRewardComponent.get(createAmmoCrate);
//			lootRewardComponent.setDrop( generateEnemyLoot(lootRewardComponent.getItemPool(), lootRewardComponent.getDropRate()));

//			Entity shaman = entityFactory.enemyFactory.createTribesmenShaman(room, new Vector2(11, 2));

//			Entity totem = entityFactory.enemyFactory.createTribesmenTotem(room, new Vector2(14, 8));
//			OrbCarrierComponent orbCarrierComponent = Mappers.orbCarrierComponent.get(totem);
//			orbCarrierComponent.acquire(totem, entityFactory.orbFactory.createEnergyOrb(null, room));

//			Entity createTribesmenSpear = entityFactory.enemyFactory.createTribesmenSpear(room, new Vector2(14, 8));
//			Mappers.statusReceiverComponent.get(createTribesmenSpear).requestAction(StatusActionEnum.RECEIVE_STATUS, new StatusDebuffDeathDoor(5));
			
//						entityFactory.enemyFactory.createTribesmenShield(room, new Vector2(14, 5));
//			entityFactory.enemyFactory.createSpider(room, new Vector2(2, 8));
//			entityFactory.creepFactory.createVinesBush(room, new Vector2(12, 8));

//			entityFactory.enemyFactory.createStinger(room, new Vector2(14, 5), 3);			
//			LootRewardComponent lootRewardComponent = Mappers.lootRewardComponent.get(enemy);
//			lootRewardComponent.setDrop( generateEnemyLoot(lootRewardComponent.getItemPool(), lootRewardComponent.getDropRate()));

//			entityFactory.itemFactory.createItemKey(room, new Vector2(12, 6));
//			entityFactory.createExit(room, new Vector2(12, 4), false);

//			entityFactory.playerFactory.createGoddessStatue(new Vector2(12, 6), room);
			
//			entityFactory.itemFactory.createItemVigor(room, new Vector2(10, 10));
//			entityFactory.itemFactory.createItemFrailty(room, new Vector2(9, 10));
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
			entityFactory.itemFactory.createItemTutorialPage(5,room, new Vector2(8, 5));

//			entityFactory.lootableFactory.createOrbBag(room, new Vector2(13, 9));
//			entityFactory.lootableFactory.createPersonalBelongings(room, new Vector2(14, 9));
//
			
//			entityFactory.createExit(this, new Vector2(16, 4));
//			entityFactory.enemyFactory.createScorpion(room, new Vector2(14, 5));			
//			entityFactory.enemyFactory.createScorpion(room, new Vector2(14, 6));			
//			entityFactory.enemyFactory.createScorpion(room, new Vector2(14, 7));			
//			entityFactory.enemyFactory.createScorpion(room, new Vector2(14, 8));			
//			entityFactory.enemyFactory.createScorpion(room, new Vector2(14, 9));			
//			entityFactory.enemyFactory.createScorpion(room, new Vector2(14, 10));			
//			entityFactory.enemyFactory.createScorpion(room, new Vector2(14, 11));			
//			entityFactory.enemyFactory.createScorpion(room, new Vector2(14, 4));			
//			Entity enemy4 = entityFactory.enemyFactory.createEnemy(EnemyTypeEnum.SPIDER, room, new Vector2(11, 2));
			
//			Entity enemy2 = entityFactory.enemyFactory.createSpider(room, new Vector2(10, 8), 1);
//			LootRewardComponent lootRewardComponent = Mappers.lootRewardComponent.get(enemy2);
//			lootRewardComponent.setDrop( generateEnemyLoot(100f));
			
//			Entity enemy3 = entityFactory.enemyFactory.createSpider(room, new Vector2(3, 10));
//			entityFactory.enemyFactory.createSpiderWeb(	room, new Vector2(14, 5), 3);
			
//			entityFactory.creepFactory.createFire(room, new Vector2(15, 6), null);
//			entityFactory.creepFactory.createWeb(room, new Vector2(16,6));
//			entityFactory.creepFactory.createWeb(room, new Vector2(17,6));

			spawnPositions = new ArrayList<>(possibleSpawns);
			Collections.shuffle(spawnPositions, random.getNextSeededRandom());

			secretDoorPos = spawnPositions.get(0);
			room.setSecretDoor(entityFactory.createSecretDoor(room, secretDoorPos));
			spawnPositions.remove(0);
			
			break;
		case END_FLOOR_ROOM:
			if (possibleSpawns.size() == 0) return;
			
			spawnPositions = new ArrayList<>(possibleSpawns);
			Collections.shuffle(spawnPositions, random.getNextSeededRandom());
			
			Vector2 pos = spawnPositions.get(0);
			Entity exit = entityFactory.createExit(room, pos, false);
			List<Tile> adjacentTiles = TileUtil.getAdjacentTiles(pos, room);
			for (Tile t : adjacentTiles) {
				if (t.isWalkable(exit)) {
					entityFactory.createWoodenPanel(room, t.getGridPos());
					break;
				}
			}
			
			pos = spawnPositions.get(1);
			room.setSecretDoor(entityFactory.createSecretDoor(room, pos));
			
			pos = spawnPositions.get(2);
			entityFactory.playerFactory.createSoulbender(	pos, room);
			
			pos = spawnPositions.get(3);
			entityFactory.playerFactory.createSewingMachine(pos, room);
			break;
			
		case TREASURE_ROOM:
			spawnPositions = new ArrayList<>(possibleSpawns);
			Collections.shuffle(spawnPositions, random.getNextSeededRandom());

			int soulbenderRand = random.nextSeededInt(3);
			if (soulbenderRand == 0) {
				Entity sb = entityFactory.playerFactory.createSoulbender(spawnPositions.get(0), room);
				Mappers.soulbenderComponent.get(sb).setPrice(0);
				spawnPositions.remove(0);
			}
			
			int infusableRand = random.nextSeededInt(4);
			if (infusableRand < 3) {
				entityFactory.lootableFactory.createPersonalBelongings(room, spawnPositions.get(0));
				spawnPositions.remove(0);
			}
			
			int chaliceRand = random.nextSeededInt(4);
			if (chaliceRand == 0) {
				chaliceTypeIndex = random.nextSeededInt(ChaliceType.values().length);
				entityFactory.playerFactory.createChalice(spawnPositions.get(0), room, ChaliceType.values()[chaliceTypeIndex]);
				spawnPositions.remove(0);
			}
			
			int statueRand = random.nextSeededInt(4);
			if (statueRand == 0) {
				needsTwoExplosions = random.nextSeededInt(2) == 0;
				entityFactory.playerFactory.createGoddessStatue(spawnPositions.get(0), room, needsTwoExplosions);
				spawnPositions.remove(0);
			}
			
			int leatherRand = random.nextSeededInt(5);
			if (leatherRand == 0) {
				entityFactory.itemFactory.createItem(ItemEnum.LEATHER, room, spawnPositions.get(0));
				spawnPositions.remove(0);
			}
			
			int divineCatalystRand = random.nextSeededInt(5);
			if (divineCatalystRand == 0) {
				entityFactory.itemFactory.createItem(ItemEnum.DIVINE_CATALYST, room, spawnPositions.get(0));
				spawnPositions.remove(0);
			}
			
			int satchelRand = random.nextSeededInt(2);
			if (satchelRand == 0) {
				entityFactory.lootableFactory.createSatchel(room, spawnPositions.get(0));
				spawnPositions.remove(0);
			}
			
			break;
			
			default:
			break;
		}
		
		placeDestructibles(room, random, generatedRoom.getPossibleDestr());
	
	
		// Release poolable vector2
		generatedRoom.releaseSpawns();
	}


	protected void placeLootable(int lootableChancePercentage, Room room, RandomSingleton random, List<PoolableVector2> spawnPositions) {
		int lootRandom = random.nextSeededInt(10);
		boolean isLoot = lootRandom < lootableChancePercentage;
		if (isLoot) {
			lootRandom = random.nextSeededInt(16);
			Vector2 lootPos = spawnPositions.get(0);
			if (lootRandom <= 5) {
				entityFactory.lootableFactory.createBones(room, lootPos);				
			} else if (lootRandom <= 9) {
				entityFactory.lootableFactory.createSatchel(room, lootPos);
			} else if (lootRandom <= 12) {
				entityFactory.lootableFactory.createSpellBook(room, lootPos);
			} else {
				entityFactory.lootableFactory.createOrbBag(room, lootPos);
			}
			spawnPositions.remove(0);
		}
	}


	/**
	 * Generate a random number of enemies and place them in a room.
	 * @param room the room
	 * @param random the random
	 * @param spawnPositions the possible spawn positions
	 * @param canBeEmpty true if there can be no enemies
	 */
	protected abstract void placeEnemies(Room room, RandomSingleton random, List<PoolableVector2> spawnPositions, boolean canBeEmpty);
	
	/**
	 * Scan all destructible possible locations and randomly place a destructible on it
	 * @param room the room
	 * @param random the random
	 * @param destrPositions the possible spawn positions for destructibles
	 */
	protected void placeDestructibles(Room room, RandomSingleton random, List<PoolableVector2> destrPositions) {
		if (destrPositions == null || destrPositions.isEmpty()) return;

		Iterator<PoolableVector2> iterator = destrPositions.iterator();
		while (iterator.hasNext()) {
			PoolableVector2 location = iterator.next();
			
			int nextInt = random.nextSeededInt(15);
			
			if (nextInt <= 2) {
				int vaseRandom = random.nextSeededInt(100);
				if (vaseRandom == 0) {
					entityFactory.destructibleFactory.createGoldenVase(room, location);
				} else {
					entityFactory.destructibleFactory.createVase(room, location);
				}
			} else if (nextInt <= 4) {
				entityFactory.destructibleFactory.createAmmoCrate(room, location);
			} else {
				continue;
			}
		}
	}
	
	
	
	// CREEP placement
	

	protected void placeCreep(TileEnum type, PoolableVector2 position, GeneratedRoom groom, List<PoolableVector2> emptyTiles, int chance, int step) {
		RandomSingleton random = RandomSingleton.getInstance();
    	groom.getTileTypes()[(int) position.x][(int) position.y] = type; 
    	emptyTiles.remove(position);
    	
    	if (chance <= 0) return;
    	
    	PoolableVector2 north = PoolableVector2.create(position.x, position.y + 1);
    	if (emptyTiles.contains(north) && random.nextSeededInt(100) <= chance) {
    		placeCreep(type, north, groom, emptyTiles, chance - step, step);
    	}
    	PoolableVector2 south = PoolableVector2.create(position.x, position.y - 1);
    	if (emptyTiles.contains(south) && random.nextSeededInt(100) <= chance) {
    		placeCreep(type, south, groom, emptyTiles, chance - step, step);
    	}
    	PoolableVector2 east = PoolableVector2.create(position.x + 1, position.y);
    	if (emptyTiles.contains(east) && random.nextSeededInt(100) <= chance) {
    		placeCreep(type, east, groom, emptyTiles, chance - step, step);
    	}
    	PoolableVector2 west = PoolableVector2.create(position.x - 1, position.y);
    	if (emptyTiles.contains(west) && random.nextSeededInt(100) <= chance) {
    		placeCreep(type, west, groom, emptyTiles, chance - step, step);
    	}
	}

	protected void placeBush(PoolableVector2 position, GeneratedRoom groom, List<PoolableVector2> emptyTiles, int chance, int step) {
		RandomSingleton random = RandomSingleton.getInstance();
		
		float unit = (float) random.nextSeededInt(100);
		float decimal = random.nextSeededFloat();
		float bushType = unit + decimal;

		TileEnum type = null;
		if (bushType < 0.3f) {
			type = TileEnum.CLOVER_BUSH;
		} else if (bushType < 3) {
			type = TileEnum.VINES_BUSH;
		} else {
			type = TileEnum.BUSH;
		}
    	groom.getTileTypes()[(int) position.x][(int) position.y] = type; 
    	emptyTiles.remove(position);
    	
    	if (chance <= 0) return;
    	
    	PoolableVector2 north = PoolableVector2.create(position.x, position.y + 1);
    	if (emptyTiles.contains(north) && random.nextSeededInt(100) <= chance) {
    		placeBush(north, groom, emptyTiles, chance - step, step);
    	}
    	PoolableVector2 south = PoolableVector2.create(position.x, position.y - 1);
    	if (emptyTiles.contains(south) && random.nextSeededInt(100) <= chance) {
    		placeBush(south, groom, emptyTiles, chance - step, step);
    	}
    	PoolableVector2 east = PoolableVector2.create(position.x + 1, position.y);
    	if (emptyTiles.contains(east) && random.nextSeededInt(100) <= chance) {
    		placeBush(east, groom, emptyTiles, chance - step, step);
    	}
    	PoolableVector2 west = PoolableVector2.create(position.x - 1, position.y);
    	if (emptyTiles.contains(west) && random.nextSeededInt(100) <= chance) {
    		placeBush(west, groom, emptyTiles, chance - step, step);
    	}
	}
	
	protected void placeCreepRiver(TileEnum type, PoolableVector2 position, GeneratedRoom groom, List<PoolableVector2> emptyTiles, int chance, int step) {
		RandomSingleton random = RandomSingleton.getInstance();
    	groom.getTileTypes()[(int) position.x][(int) position.y] = type; 
    	emptyTiles.remove(position);
    	
    	if (chance <= 0) return;
    	
    	List<PoolableVector2> possibleDirections = new ArrayList<>();
    	PoolableVector2 north = PoolableVector2.create(position.x, position.y + 1);
    	if (emptyTiles.contains(north)) {
    		possibleDirections.add(north);
    	}
    	PoolableVector2 south = PoolableVector2.create(position.x, position.y - 1);
    	if (emptyTiles.contains(south)) {
    		possibleDirections.add(south);
    	}
    	PoolableVector2 east = PoolableVector2.create(position.x + 1, position.y);
    	if (emptyTiles.contains(east)) {
    		possibleDirections.add(east);
    	}
    	PoolableVector2 west = PoolableVector2.create(position.x - 1, position.y);
    	if (emptyTiles.contains(west)) {
    		possibleDirections.add(west);
    	}
    	
    	if (possibleDirections.isEmpty()) return;
    	
    	int nextSeededInt = random.nextSeededInt(possibleDirections.size());
    	PoolableVector2 direction = possibleDirections.get(nextSeededInt);
    	placeCreepRiver(type, direction, groom, emptyTiles, chance - step, step);
	}
}
