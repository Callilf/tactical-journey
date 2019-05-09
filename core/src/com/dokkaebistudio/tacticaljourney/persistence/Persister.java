package com.dokkaebistudio.tacticaljourney.persistence;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.ashley.PublicEntity;
import com.dokkaebistudio.tacticaljourney.ashley.PublicPooledEngine;
import com.dokkaebistudio.tacticaljourney.ashley.PublicPooledEngine.PooledEntity;
import com.dokkaebistudio.tacticaljourney.components.AIComponent;
import com.dokkaebistudio.tacticaljourney.components.ChasmComponent;
import com.dokkaebistudio.tacticaljourney.components.DestructibleComponent;
import com.dokkaebistudio.tacticaljourney.components.DialogComponent;
import com.dokkaebistudio.tacticaljourney.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.components.EnemySpawnerComponent;
import com.dokkaebistudio.tacticaljourney.components.ExpRewardComponent;
import com.dokkaebistudio.tacticaljourney.components.ExplosiveComponent;
import com.dokkaebistudio.tacticaljourney.components.FlammableComponent;
import com.dokkaebistudio.tacticaljourney.components.FlyComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.HumanoidComponent;
import com.dokkaebistudio.tacticaljourney.components.InspectableComponent;
import com.dokkaebistudio.tacticaljourney.components.PanelComponent;
import com.dokkaebistudio.tacticaljourney.components.SolidComponent;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.attack.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.attack.AttackSkill;
import com.dokkaebistudio.tacticaljourney.components.creep.CreepComponent;
import com.dokkaebistudio.tacticaljourney.components.creep.CreepEmitterComponent;
import com.dokkaebistudio.tacticaljourney.components.display.AnimationComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.display.StateComponent;
import com.dokkaebistudio.tacticaljourney.components.display.TextComponent;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.components.loot.LootRewardComponent;
import com.dokkaebistudio.tacticaljourney.components.loot.LootableComponent;
import com.dokkaebistudio.tacticaljourney.components.neutrals.ChaliceComponent;
import com.dokkaebistudio.tacticaljourney.components.neutrals.RecyclingMachineComponent;
import com.dokkaebistudio.tacticaljourney.components.neutrals.SewingMachineComponent;
import com.dokkaebistudio.tacticaljourney.components.neutrals.ShopKeeperComponent;
import com.dokkaebistudio.tacticaljourney.components.neutrals.SoulbenderComponent;
import com.dokkaebistudio.tacticaljourney.components.neutrals.StatueComponent;
import com.dokkaebistudio.tacticaljourney.components.orbs.OrbCarrierComponent;
import com.dokkaebistudio.tacticaljourney.components.orbs.OrbComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AllyComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AlterationReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AmmoCarrierComponent;
import com.dokkaebistudio.tacticaljourney.components.player.ExperienceComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.components.player.ParentEntityComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.components.player.SkillComponent;
import com.dokkaebistudio.tacticaljourney.components.player.WalletComponent;
import com.dokkaebistudio.tacticaljourney.components.player.WheelComponent;
import com.dokkaebistudio.tacticaljourney.components.transition.DoorComponent;
import com.dokkaebistudio.tacticaljourney.components.transition.ExitComponent;
import com.dokkaebistudio.tacticaljourney.components.transition.SecretDoorComponent;
import com.dokkaebistudio.tacticaljourney.descriptors.FontDescriptor;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.factory.EntityFlagEnum;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.items.pools.ItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.ItemPoolSingleton;
import com.dokkaebistudio.tacticaljourney.room.Floor;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomClearedState;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.room.RoomType;
import com.dokkaebistudio.tacticaljourney.room.RoomVisitedState;
import com.dokkaebistudio.tacticaljourney.room.Tile;
import com.dokkaebistudio.tacticaljourney.room.managers.TurnManager;
import com.dokkaebistudio.tacticaljourney.room.rewards.AbstractRoomReward;
import com.dokkaebistudio.tacticaljourney.singletons.GameTimeSingleton;
import com.dokkaebistudio.tacticaljourney.statuses.debuffs.StatusDebuffDeathDoor;
import com.dokkaebistudio.tacticaljourney.statuses.debuffs.StatusDebuffStunned;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.util.AnimatedImage;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.MovementHandler;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class Persister {
	
	public GameScreen gameScreen;
	public PublicPooledEngine engine;
	private List<Long> savedEntities = new ArrayList<>();
	private Map<Long, PooledEntity> loadedEntities = new HashMap<>();
	
	private List<Integer> savedFloors = new ArrayList<>();
	private Map<Integer, Floor> loadedFloors = new HashMap<>();

	private List<Integer> savedRooms = new ArrayList<>();
	private Map<Integer, Room> loadedRooms = new HashMap<>();
	
	private int currentLevel;

	public Persister() {}
	public Persister(GameScreen gs) {
		this.gameScreen = gs;
		this.engine = GameScreen.engine;
	}
	
	/**
	 * Return true if there is a saved game.
	 * @return true if there is a game save, false if not.
	 */
	public boolean hasSave() {
		FileHandle statsFile = Gdx.files.local("gamestats_1.bin");
		FileHandle saveFile = Gdx.files.local("gamestate_1.bin");
		return statsFile.exists() && saveFile.exists();
	}
	
	public void deleteSave() {
		FileHandle statsFile = Gdx.files.local("gamestats_1.bin");
		FileHandle saveFile = Gdx.files.local("gamestate_1.bin");
		statsFile.delete();
		saveFile.delete();
	}
	
	
	
	public void saveGameState() {
		
		Kryo kryo = new Kryo();
		kryo.setReferences(false);
		this.registerSerializers(kryo, gameScreen.activeFloor, gameScreen.floors);
		
		// Save the game statistics first
		GameStatistics statistics = GameStatistics.create(gameScreen);

		try {
			FileHandle statsFile = Gdx.files.local("gamestats_1.bin");
			if (statsFile.exists()) statsFile.delete();
			Output output = new Output(new FileOutputStream(statsFile.file()));
			kryo.writeObject(output, statistics);
			output.close();
		} catch (KryoException | IOException e) {
			Gdx.app.error("SAVE", "Failed to save the game statistics", e);
		}
			
		try {
			FileHandle saveFile = Gdx.files.local("gamestate_1.bin");
			if (saveFile.exists()) saveFile.delete();
			Output output = new Output(new FileOutputStream(saveFile.file()));
			kryo.writeObject(output, gameScreen);
			output.close();
		} catch (KryoException | IOException e) {
			Gdx.app.error("SAVE", "Failed to save the game state", e);
		}
		
	}
	
	
	public GameStatistics loadGameStatistics() {
		GameStatistics stats = null;
		Kryo kryo = new Kryo();
		kryo.setReferences(false);
		try {
			FileHandle statsFile = Gdx.files.local("gamestats_1.bin");
		    Input input = new Input(new FileInputStream(statsFile.file()));
		    stats = kryo.readObject(input, GameStatistics.class);
		    input.close();   
		} catch (KryoException | IOException e ) {
			Gdx.app.error("SAVE", "Failed to load the game statistics", e);
		}
		
		return stats;
	}
	
	public void loadGameState() {
		Kryo kryo = new Kryo();
		kryo.setReferences(false);
		this.registerSerializers(kryo, gameScreen.activeFloor, gameScreen.floors);
		
		try {
			FileHandle saveFile = Gdx.files.local("gamestate_1.bin");
		    Input input = new Input(new FileInputStream(saveFile.file()));
		    kryo.readObject(input, GameScreen.class);
		    input.close();   
		} catch (KryoException | IOException e ) {
			Gdx.app.error("SAVE", "Failed to load the game state", e);
		}
	}
	

	
	// Serializers
	
	
	public Serializer<GameScreen> getGameScreenSerializer() {
		return new Serializer<GameScreen>() {

			@Override
			public void write(Kryo kryo, Output output, GameScreen gs) {
				
				// Pre-save the player (just the id) so that when another entity references it, it won't save it
				output.writeLong(((PublicEntity)GameScreen.player).id);
				savedEntities.add(((PublicEntity)GameScreen.player).id);
								
				// Save the current time
				output.writeFloat(GameTimeSingleton.getInstance().getElapsedTime());
				
				// Save the random seed and the number of time nextInt has been called
				String seed = RandomSingleton.getInstance().getSeed();
				output.writeString(seed);
				output.writeString(RandomSingleton.getInstance().getStateOfSeededRandom());
				
				// Save the Item Pools
				for (ItemPool pool : ItemPoolSingleton.getInstance().getAllItemPools()) {
					output.writeInt(pool.getInitialSumOfChances());
				}
				kryo.writeClassAndObject(output, ItemPoolSingleton.getInstance().getRemovedItems());

				currentLevel = gs.activeFloor.getLevel();
				
				// Save the current floor
				output.writeInt(gs.activeFloor.getLevel());

				// Save the floors
				output.writeInt(gs.floors.size());
				for (Floor f : gs.floors) {
					kryo.writeClassAndObject(output, f);
				}
				
				// Save the player for real
				savedEntities.remove(((PublicEntity)GameScreen.player).id);
				kryo.writeClassAndObject(output, GameScreen.player);

				output.writeLong(GameScreen.engine.entityCounter);
			}

			@Override
			public GameScreen read(Kryo kryo, Input input, Class<GameScreen> type) {
				
				// Partial load of the player so that entities that reference it won't crash
				GameScreen.player = (PublicEntity) engine.createEntity();
				((PublicEntity)GameScreen.player).id = input.readLong();
				loadedEntities.put(((PublicEntity)GameScreen.player).id, (PooledEntity) GameScreen.player);
				
				// Restore the time
				GameTimeSingleton.getInstance().setElapsedTime(input.readFloat());
				
				// Init the random
				RandomSingleton.createInstance(input.readString());
				String seed = input.readString();
				RandomSingleton.getInstance().restoreState(seed);
								
				// Restore the item pools
				for (ItemPool pool : ItemPoolSingleton.getInstance().getAllItemPools()) {
					pool.setInitialSumOfChances(input.readInt());
				}
				ItemPoolSingleton.getInstance().restoreRemoveditems((List<ItemEnum>) kryo.readClassAndObject(input));

				
				currentLevel = input.readInt();

				// load floors
				int floorNb = input.readInt();
				gameScreen.floors.clear();
				for (int i=0 ; i<floorNb ; i++) {
					gameScreen.floors.add((Floor) kryo.readClassAndObject(input));
				}
				
				// Restore current floor
				for (Floor f : gameScreen.floors) {
					if (f.getLevel() == currentLevel) {
						gameScreen.activeFloor = f;
						break;
					}
				}
				
				// Real load of the player
				loadedEntities.remove(((PublicEntity)GameScreen.player).id);
				GameScreen.player = (PublicEntity) kryo.readClassAndObject(input);
				
						
				GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(GameScreen.player);
				MovementHandler.placeEntity(GameScreen.player, gridPositionComponent.coord(), gameScreen.activeFloor.getActiveRoom());
				InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(GameScreen.player);
				inventoryComponent.player = GameScreen.player;
				StatusReceiverComponent statusReceiverComponent = Mappers.statusReceiverComponent.get(GameScreen.player);
				statusReceiverComponent.displayStatusTable(GameScreen.fxStage);
				
				// Restore the state of the active room
				gameScreen.activeFloor.getActiveRoom().forceState(RoomState.PLAYER_COMPUTE_MOVABLE_TILES);
				if (!gameScreen.activeFloor.getActiveRoom().hasEnemies()) Mappers.moveComponent.get(gameScreen.player).setFreeMove(true);

				
				long entityCounter = input.readLong();
				GameScreen.engine.entityCounter = entityCounter;
				
				return null;
			}
			
		};
	}
	
	public Serializer<Floor> getFloorSerializer() {
		return new Serializer<Floor>() {

			@Override
			public void write(Kryo kryo, Output output, Floor floor) {
				output.writeInt(floor.getLevel());
				
				// If the floor has already been saved, stop here
				if (floor.getLevel() >= currentLevel && !savedFloors.contains(floor.getLevel())) {
					savedFloors.add(floor.getLevel());

					output.writeInt(floor.getTurns());
					output.writeInt(floor.getTurnThreshold());
					output.writeBoolean(floor.getRooms() != null);
					
					if (floor.getRooms() != null) {
						// Save the rooms
						output.writeInt(floor.getRooms().size());
						for (Room r : floor.getRooms()) {
							kryo.writeClassAndObject(output, r);
						}
						
						kryo.writeClassAndObject(output, floor.getRoomPositions());				
						kryo.writeClassAndObject(output, floor.getActiveRoom());
					}
				}
			}

			@Override
			public Floor read(Kryo kryo, Input input, Class<Floor> type) {
				int level = input.readInt();
				
				// If the entity has been previously loaded, return it
				if (loadedFloors.containsKey(level)) {
					return loadedFloors.get(level);
				}
				
				Floor f = new Floor(gameScreen, level);
				loadedFloors.put(level, f);
				
				// Old level, no need to load more
				if (level < currentLevel) return f;

				f.setTurns(input.readInt());
				f.setTurnThreshold(input.readInt());
				
				boolean isGenerated = input.readBoolean();
				if (isGenerated) {
					// load rooms
					f.setRooms(new ArrayList<Room>());
					int roomNb = input.readInt();
					for (int i=0 ; i<roomNb ; i++) {
						Room room = (Room) kryo.readClassAndObject(input);
						room.floor = f;
						f.getRooms().add(room);
					}
					
					f.setRoomPositions((Map<Vector2, Room>) kryo.readClassAndObject(input));
					f.setActiveRoom( (Room) kryo.readClassAndObject(input));
										
//					// Restore the items' quantity and price displayers
//					for(Entity e : f.getActiveRoom().getAllEntities()) {
//						if (Mappers.itemComponent.has(e)) f.getActiveRoom().getAddedItems().add(e);
//					}
				}
				return f;
			}
			
		};
	}
	
	
	public Serializer<Room> getRoomSerializer() {
		return new Serializer<Room>() {

			@Override
			public void write(Kryo kryo, Output output, Room roomToSave) {
				// Write the room id
				output.writeInt(roomToSave.getIndex());
				
				// If the entity has already been saved, stop here
				if (!savedRooms.contains(roomToSave.getIndex())) {
					// If not, save the whole entity
					savedRooms.add(roomToSave.getIndex());

					output.writeString(roomToSave.type.name());
					
					output.writeInt(roomToSave.turnManager.getTurn());
					
					kryo.writeClassAndObject(output, roomToSave.getRewards());
					kryo.writeClassAndObject(output, roomToSave.grid);
					output.writeString(roomToSave.getCleared().name());
					output.writeString(roomToSave.getVisited().name());
					output.writeBoolean(roomToSave.isDisplayedOnMap());
					
					output.writeInt(roomToSave.getAllEntities().size);
					for (Entity e : roomToSave.getAllEntities()) {
						kryo.writeClassAndObject(output, e);
					}
					
					kryo.writeClassAndObject(output, roomToSave.getEntitiesAtPosition());
					
					kryo.writeClassAndObject(output, roomToSave.getAllies());
					kryo.writeClassAndObject(output, roomToSave.getEnemies());
					kryo.writeClassAndObject(output, roomToSave.getNeutrals());
					kryo.writeClassAndObject(output, roomToSave.getDoors());
					kryo.writeClassAndObject(output, roomToSave.getSecretDoor());
					
					kryo.writeClassAndObject(output, roomToSave.getNorthNeighbor());
					kryo.writeClassAndObject(output, roomToSave.getSouthNeighbor());
					kryo.writeClassAndObject(output, roomToSave.getWestNeighbor());
					kryo.writeClassAndObject(output, roomToSave.getEastNeighbor());

				}
			}

			@Override
			public Room read(Kryo kryo, Input input, Class<Room> type) {
				// Read the room id
				int roomIndex = input.readInt();
				
				// If the entity has been previously loaded, return it
				if (loadedRooms.containsKey(roomIndex)) {
					return loadedRooms.get(roomIndex);
				}
				
				// Otherwise, load it
				RoomType roomType = RoomType.valueOf(input.readString());
				Room loadedRoom = new Room(null, roomIndex, engine, gameScreen.entityFactory, roomType);
				loadedRooms.put(roomIndex, loadedRoom);
				
				loadedRoom.forceState(RoomState.PLAYER_TURN_INIT);
				loadedRoom.turnManager = new TurnManager(loadedRoom);
				loadedRoom.turnManager .setTurn(input.readInt());
				
				loadedRoom.getRewards().addAll((Collection<? extends AbstractRoomReward>) kryo.readClassAndObject(input));
				
				// The tile grid
				loadedRoom.grid = (Tile[][]) kryo.readClassAndObject(input);
				for (int i=0 ; i<loadedRoom.grid.length ; i++) {
					for (int j=0 ; j<loadedRoom.grid[i].length ; j++) {
						loadedRoom.grid[i][j].setRoom(loadedRoom);
					}
				}
				
				loadedRoom.setCleared(RoomClearedState.valueOf(input.readString()));
				loadedRoom.setVisited(RoomVisitedState.valueOf(input.readString()));
				loadedRoom.setDisplayedOnMap(input.readBoolean());
				
				// All entities
				int entityNb = input.readInt();
				for (int i=0 ; i<entityNb ; i++) {
					Entity loadedEntity = (Entity) kryo.readClassAndObject(input);
					if (isEntityToLoad(loadedEntity)) {
						try {
							loadedRoom.addEntity(loadedEntity);
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
				loadedRoom.getEntitiesAtPosition().putAll( (Map<? extends Vector2, ? extends Set<Entity>>) kryo.readClassAndObject(input));
				
				loadedRoom.getAllies().addAll((Collection<? extends Entity>) kryo.readClassAndObject(input));
				loadedRoom.getEnemies().addAll((Collection<? extends Entity>) kryo.readClassAndObject(input));
				loadedRoom.getNeutrals().addAll((Collection<? extends Entity>) kryo.readClassAndObject(input));
				loadedRoom.getDoors().addAll((Collection<? extends Entity>) kryo.readClassAndObject(input));
				loadedRoom.setSecretDoor((Entity) kryo.readClassAndObject(input));
				
				loadedRoom.setNorthNeighbor((Room) kryo.readClassAndObject(input));
				loadedRoom.setSouthNeighbor((Room) kryo.readClassAndObject(input));
				loadedRoom.setWestNeighbor((Room) kryo.readClassAndObject(input));
				loadedRoom.setEastNeighbor((Room) kryo.readClassAndObject(input));

				return loadedRoom;
			}
			
		};
	}
	
	public boolean isEntityToLoad(Entity e) {
		boolean movableTile = e.flags == EntityFlagEnum.MOVABLE_TILE.getFlag();
		boolean attackableTile = e.flags == EntityFlagEnum.ATTACK_TILE.getFlag();
		boolean itemDisplayer = e.flags == EntityFlagEnum.TEXT_QUANTITY_DISPLAYER.getFlag() || e.flags == EntityFlagEnum.TEXT_PRICE_DISPLAYER.getFlag(); 
		return !movableTile && !attackableTile && !itemDisplayer;
	}
	
	
	
	public Serializer<PublicPooledEngine> getEngineSerializer() {
		return new Serializer<PublicPooledEngine>() {

			@Override
			public void write(Kryo kryo, Output output, PublicPooledEngine object) {}

			@Override
			public PublicPooledEngine read(Kryo kryo, Input input, Class<PublicPooledEngine> type) {
				return engine;
			}
			
		};
	}

	public Serializer<PooledEntity> getEntitySerializer() {
		return new Serializer<PooledEntity>() {

			@Override
			public void write(Kryo kryo, Output output, PooledEntity entityToSave) {
				// Write the ID
				output.writeLong(entityToSave.id);
				
				// If the entity has already been saved, stop here
				if (!savedEntities.contains(entityToSave.id)) {
					savedEntities.add(entityToSave.id);
					// If not, save the whole entity
					output.writeInt(entityToSave.flags);
					output.writeInt(entityToSave.getComponents().size());
					for (Component compo : entityToSave.getComponents()) {
						kryo.writeClassAndObject(output, compo);
					}
				}
			}

			@Override
			public PooledEntity read(Kryo kryo, Input input, Class<PooledEntity> type) {
				// Read the ID
				long entityId = input.readLong();
				
				// If the entity has been previously loaded, return it
				if (loadedEntities.containsKey(entityId)) {
					return loadedEntities.get(entityId);
				}
				
				// Otherwise, load it
				PooledEntity loadedEntity = null;
				if (entityId == ((PublicEntity)GameScreen.player).id) {
					// if it's the player, use the already created entity
					loadedEntity = (PooledEntity) GameScreen.player;
				} else {
					loadedEntity = (PooledEntity) engine.createEntity();
				}
				loadedEntity.flags = input.readInt();
				loadedEntity.id = entityId;
				loadedEntities.put(entityId, loadedEntity);
				
				int componentNumber = input.readInt();
				for (int i=0 ; i<componentNumber ; i++) {
					loadedEntity.add((Component) kryo.readClassAndObject(input));
				}
				
				if (isEntityToLoad(loadedEntity)) {
					GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(loadedEntity);
					if (gridPositionComponent != null && gridPositionComponent.room != null && !gridPositionComponent.isInactive()) {
						gridPositionComponent.coord(loadedEntity, gridPositionComponent.coord(), gridPositionComponent.room);
						
						for (Component compo : loadedEntity.getComponents()) {
							if (compo instanceof RoomSystem) {
								((RoomSystem)compo).enterRoom(gridPositionComponent.room);
							}
						}
					}
				}
				
				return loadedEntity;
			}
			
		};
	}
	
	
	
	
	public static Serializer<Image> getImageSerializer(final PooledEngine engine) {
		return new Serializer<Image>() {

			@Override
			public void write(Kryo kryo, Output output, Image object) {
				System.out.println("Image");
			}

			@Override
			public Image read(Kryo kryo, Input input, Class<Image> type) {
				return null;
			}
		
		};
	}
	
	
	
	public void registerSerializers(Kryo kryo, Floor currentFloor, List<Floor> floors) {
		
		// Java mandatory serializers
//		kryo.register(Object[].class);
//		kryo.register(Class.class);
//		kryo.register(SerializedLambda.class);
//		kryo.register(ClosureSerializer.Closure.class, new ClosureSerializer());
		
		// general serializers
		kryo.register(GameScreen.class, getGameScreenSerializer());
		kryo.register(Floor.class, getFloorSerializer());
		kryo.register(Room.class, getRoomSerializer());
		kryo.register(PublicPooledEngine.class, getEngineSerializer());
		kryo.register(PooledEntity.class, getEntitySerializer());
		kryo.register(RegionDescriptor.class, RegionDescriptor.getSerializer(engine));
		kryo.register(FontDescriptor.class, FontDescriptor.getSerializer(engine));
		kryo.register(Image.class, getImageSerializer(engine));

		
		// Misc serializers
		
		kryo.register(Tile.class, Tile.getSerializer(engine));
		kryo.register(AnimatedImage.class, AnimatedImage.getSerializer(engine));
		kryo.register(StatusDebuffDeathDoor.class, StatusDebuffDeathDoor.getStatusDebuffDeathDoorSerializer(engine));
		kryo.register(StatusDebuffStunned.class, StatusDebuffStunned.getStatusDebuffStunnedSerializer(engine));
		kryo.register(AttackSkill.class, AttackSkill.getSerializer(engine));
		
		
		// Component serializers
		
		kryo.register(PlayerComponent.class, PlayerComponent.getSerializer(engine));
		kryo.register(AllyComponent.class, AllyComponent.getSerializer(engine));
		kryo.register(EnemyComponent.class, EnemyComponent.getSerializer(engine));
		kryo.register(AIComponent.class, AIComponent.getSerializer(engine));

		kryo.register(ShopKeeperComponent.class, ShopKeeperComponent.getSerializer(engine));
		kryo.register(SoulbenderComponent.class, SoulbenderComponent.getSerializer(engine));
		kryo.register(StatueComponent.class, StatueComponent.getSerializer(engine));
		kryo.register(ChaliceComponent.class, ChaliceComponent.getSerializer(engine));
		kryo.register(SewingMachineComponent.class, SewingMachineComponent.getSerializer(engine));
		kryo.register(RecyclingMachineComponent.class, RecyclingMachineComponent.getSerializer(engine));


		kryo.register(HumanoidComponent.class, HumanoidComponent.getSerializer(engine));

		kryo.register(SpriteComponent.class, SpriteComponent.getSerializer(engine));
		kryo.register(AnimationComponent.class, AnimationComponent.getSerializer(engine));
		kryo.register(StateComponent.class, StateComponent.getSerializer(engine));
		
		kryo.register(ParentEntityComponent.class, ParentEntityComponent.getSerializer(engine));
		kryo.register(GridPositionComponent.class, GridPositionComponent.getSerializer(engine, loadedRooms));
//		kryo.register(TileComponent.class, TileComponent.getSerializer(engine, floor));
		kryo.register(DoorComponent.class, DoorComponent.getSerializer(engine, loadedRooms));
		kryo.register(SecretDoorComponent.class, SecretDoorComponent.getSerializer(engine));
		kryo.register(ExitComponent.class, ExitComponent.getSerializer(engine));
		kryo.register(PanelComponent.class, PanelComponent.getSerializer(engine));
		
		
		kryo.register(MoveComponent.class, MoveComponent.getSerializer(engine));
		kryo.register(AttackComponent.class, AttackComponent.getSerializer(engine));
		kryo.register(SkillComponent.class, SkillComponent.getSerializer(engine));
		kryo.register(WalletComponent.class, WalletComponent.getSerializer(engine));
		kryo.register(AmmoCarrierComponent.class, AmmoCarrierComponent.getSerializer(engine));
		kryo.register(InventoryComponent.class, InventoryComponent.getSerializer(engine));
		kryo.register(AlterationReceiverComponent.class, AlterationReceiverComponent.getSerializer(engine));
		kryo.register(StatusReceiverComponent.class, StatusReceiverComponent.getSerializer(engine));

		
		kryo.register(ExperienceComponent.class, ExperienceComponent.getSerializer(engine));

		kryo.register(LootableComponent.class, LootableComponent.getSerializer(engine));
		kryo.register(ItemComponent.class, ItemComponent.getSerializer(engine));

		kryo.register(HealthComponent.class, HealthComponent.getSerializer(engine));
		kryo.register(ExpRewardComponent.class, ExpRewardComponent.getSerializer(engine));
		kryo.register(LootRewardComponent.class, LootRewardComponent.getSerializer(engine));
		kryo.register(EnemySpawnerComponent.class, EnemySpawnerComponent.getSerializer(engine));

		
		kryo.register(TextComponent.class, TextComponent.getSerializer(engine));
		kryo.register(DialogComponent.class, DialogComponent.getSerializer(engine));

//		kryo.register(WheelModifierComponent.class, WheelModifierComponent.getSerializer(engine, floor));
		kryo.register(WheelComponent.class, WheelComponent.getSerializer(engine));
		
		kryo.register(InspectableComponent.class, InspectableComponent.getSerializer(engine));
		
		kryo.register(SolidComponent.class, SolidComponent.getSerializer(engine));
		kryo.register(ChasmComponent.class, ChasmComponent.getSerializer(engine));
		kryo.register(FlyComponent.class, FlyComponent.getSerializer(engine));

		kryo.register(CreepComponent.class, CreepComponent.getSerializer(engine));
		kryo.register(CreepEmitterComponent.class, CreepEmitterComponent.getSerializer(engine));
		
		kryo.register(OrbComponent.class, OrbComponent.getSerializer(engine));
		kryo.register(OrbCarrierComponent.class, OrbCarrierComponent.getSerializer(engine));
		
		kryo.register(FlammableComponent.class, FlammableComponent.getSerializer(engine));
		kryo.register(ExplosiveComponent.class, ExplosiveComponent.getSerializer(engine));
		kryo.register(DestructibleComponent.class, DestructibleComponent.getSerializer(engine));

		
	
	}
}
