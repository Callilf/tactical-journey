package com.dokkaebistudio.tacticaljourney.persistence;

import java.io.File;
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
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.GameTimeSingleton;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.ashley.PublicPooledEngine;
import com.dokkaebistudio.tacticaljourney.ashley.PublicPooledEngine.PooledEntity;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.ChasmComponent;
import com.dokkaebistudio.tacticaljourney.components.DestructibleComponent;
import com.dokkaebistudio.tacticaljourney.components.DialogComponent;
import com.dokkaebistudio.tacticaljourney.components.DoorComponent;
import com.dokkaebistudio.tacticaljourney.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.components.ExpRewardComponent;
import com.dokkaebistudio.tacticaljourney.components.ExplosiveComponent;
import com.dokkaebistudio.tacticaljourney.components.FlammableComponent;
import com.dokkaebistudio.tacticaljourney.components.FlyComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.HumanoidComponent;
import com.dokkaebistudio.tacticaljourney.components.InspectableComponent;
import com.dokkaebistudio.tacticaljourney.components.SolidComponent;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.creep.CreepComponent;
import com.dokkaebistudio.tacticaljourney.components.creep.CreepEmitterComponent;
import com.dokkaebistudio.tacticaljourney.components.display.AnimationComponent;
import com.dokkaebistudio.tacticaljourney.components.display.DamageDisplayComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.display.StateComponent;
import com.dokkaebistudio.tacticaljourney.components.display.TextComponent;
import com.dokkaebistudio.tacticaljourney.components.display.VisualEffectComponent;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.components.loot.LootRewardComponent;
import com.dokkaebistudio.tacticaljourney.components.loot.LootableComponent;
import com.dokkaebistudio.tacticaljourney.components.neutrals.ShopKeeperComponent;
import com.dokkaebistudio.tacticaljourney.components.neutrals.SoulbenderComponent;
import com.dokkaebistudio.tacticaljourney.components.neutrals.StatueComponent;
import com.dokkaebistudio.tacticaljourney.components.orbs.OrbCarrierComponent;
import com.dokkaebistudio.tacticaljourney.components.orbs.OrbComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AlterationReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AmmoCarrierComponent;
import com.dokkaebistudio.tacticaljourney.components.player.ExperienceComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.components.player.ParentEntityComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.components.player.SkillComponent;
import com.dokkaebistudio.tacticaljourney.components.player.WalletComponent;
import com.dokkaebistudio.tacticaljourney.components.player.WheelComponent;
import com.dokkaebistudio.tacticaljourney.components.transition.ExitComponent;
import com.dokkaebistudio.tacticaljourney.descriptors.FontDescriptor;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.factory.EntityFlagEnum;
import com.dokkaebistudio.tacticaljourney.room.Floor;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomClearedState;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.room.RoomType;
import com.dokkaebistudio.tacticaljourney.room.Tile;
import com.dokkaebistudio.tacticaljourney.room.managers.TurnManager;
import com.dokkaebistudio.tacticaljourney.room.rewards.AbstractRoomReward;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;
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
	
	private List<Integer> savedRooms = new ArrayList<>();
	private Map<Integer, Room> loadedRooms = new HashMap<>();

	public Persister(GameScreen gs) {
		this.gameScreen = gs;
		this.engine = gs.engine;
	}
	
	
	
	public void saveGameState() {
		Kryo kryo = new Kryo();
		kryo.setReferences(false);
		this.registerSerializers(kryo, gameScreen.activeFloor, gameScreen.floors);
		
		
		try {
			File f = new File("gamestate.bin");
			if (f.exists()) f.delete();
			f.createNewFile();
			Output output = new Output(new FileOutputStream(f));
			kryo.writeObject(output, gameScreen);
			output.close();
		} catch (KryoException | IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void loadGameState() {
		Kryo kryo = new Kryo();
		kryo.setReferences(false);
		this.registerSerializers(kryo, gameScreen.activeFloor, gameScreen.floors);
		
		try {
			File f = new File("gamestate.bin");
		    Input input = new Input(new FileInputStream(f));
		    kryo.readObject(input, GameScreen.class);
		    input.close();   
		} catch (KryoException | IOException e ) {
			e.printStackTrace();
		}
	}
	

	
	// Serializers
	
	
	public Serializer<GameScreen> getGameScreenSerializer() {
		return new Serializer<GameScreen>() {

			@Override
			public void write(Kryo kryo, Output output, GameScreen gs) {				
				// Save the floors
				output.writeInt(gs.floors.size());
				for (Floor f : gs.floors) {
					kryo.writeClassAndObject(output, f);
				}
				
				// Save the player
				kryo.writeClassAndObject(output, gs.player);
				
				// Save the current time
				output.writeFloat(GameTimeSingleton.getInstance().getElapsedTime());
				
				// Save the random seed and the number of time nextInt has been called
				String seed = RandomSingleton.getInstance().getSeed();
				output.writeString(seed);
				output.writeString(RandomSingleton.getInstance().getStateOfSeededRandom());
			}

			@Override
			public GameScreen read(Kryo kryo, Input input, Class<GameScreen> type) {				
				// load floors
				int floorNb = input.readInt();
				gameScreen.floors.clear();
				for (int i=0 ; i<floorNb ; i++) {
					gameScreen.floors.add((Floor) kryo.readClassAndObject(input));
				}
				
				//TODO change this
				gameScreen.activeFloor = gameScreen.floors.get(0);

				
				// Load the player
				gameScreen.player = (Entity) kryo.readClassAndObject(input);
				
						
				GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(gameScreen.player);
				MovementHandler.placeEntity(gameScreen.player, gridPositionComponent.coord(), gameScreen.activeFloor.getActiveRoom());
				InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(gameScreen.player);
				inventoryComponent.player = gameScreen.player;
				
				// Resotre the time
				GameTimeSingleton.getInstance().setElapsedTime(input.readFloat());
				
				// Init the random
				RandomSingleton.createInstance(input.readString());
				String seed = input.readString();
				RandomSingleton.getInstance().restoreState(seed);
				return null;
			}
			
		};
	}
	
	public Serializer<Floor> getFloorSerializer() {
		return new Serializer<Floor>() {

			@Override
			public void write(Kryo kryo, Output output, Floor floor) {
				output.writeInt(floor.getLevel());
				
				// Save the rooms
				output.writeInt(floor.getRooms().size());
				for (Room r : floor.getRooms()) {
					kryo.writeClassAndObject(output, r);
				}
				
				kryo.writeClassAndObject(output, floor.getRoomPositions());				
				kryo.writeClassAndObject(output, floor.getActiveRoom());
			}

			@Override
			public Floor read(Kryo kryo, Input input, Class<Floor> type) {
				int level = input.readInt();
				Floor f = new Floor(gameScreen, level);
				
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
					output.writeBoolean(roomToSave.isVisited());
					
					output.writeInt(roomToSave.getAllEntities().size);
					for (Entity e : roomToSave.getAllEntities()) {
						kryo.writeClassAndObject(output, e);
					}
					
					kryo.writeClassAndObject(output, roomToSave.getEntitiesAtPosition());
					
					kryo.writeClassAndObject(output, roomToSave.getEnemies());
					kryo.writeClassAndObject(output, roomToSave.getNeutrals());
					kryo.writeClassAndObject(output, roomToSave.getDoors());
					
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
				loadedRoom.setVisited(input.readBoolean());
				
				// All entities
				int entityNb = input.readInt();
				for (int i=0 ; i<entityNb ; i++) {
					Entity loadedEntity = (Entity) kryo.readClassAndObject(input);
					if (isEntityToLoad(loadedEntity)) {
						loadedRoom.addEntity(loadedEntity);
					}
				}
				
				loadedRoom.getEntitiesAtPosition().putAll( (Map<? extends Vector2, ? extends Set<Entity>>) kryo.readClassAndObject(input));
//				for (Entry<Vector2, Set<Entity>> entry : loadedRoom.getEntitiesAtPosition().entrySet()) {
//					
//				}
				
				loadedRoom.getEnemies().addAll((Collection<? extends Entity>) kryo.readClassAndObject(input));
				loadedRoom.getNeutrals().addAll((Collection<? extends Entity>) kryo.readClassAndObject(input));
				loadedRoom.getDoors().addAll((Collection<? extends Entity>) kryo.readClassAndObject(input));

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
		return !movableTile && !attackableTile;
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
				PooledEntity loadedEntity = (PooledEntity) engine.createEntity();
				loadedEntity.flags = input.readInt();
				loadedEntities.put(entityId, loadedEntity);
				
				int componentNumber = input.readInt();
				for (int i=0 ; i<componentNumber ; i++) {
					loadedEntity.add((Component) kryo.readClassAndObject(input));
				}
				
				if (isEntityToLoad(loadedEntity)) {
					GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(loadedEntity);
					if (gridPositionComponent != null && gridPositionComponent.room != null) {
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
	
	
	
	public void registerSerializers(Kryo kryo, Floor currentFloor, List<Floor> floors) {
		
		// general serializers
		kryo.register(GameScreen.class, getGameScreenSerializer());
		kryo.register(Floor.class, getFloorSerializer());
		kryo.register(Room.class, getRoomSerializer());
		kryo.register(PublicPooledEngine.class, getEngineSerializer());
		kryo.register(PooledEntity.class, getEntitySerializer());
		kryo.register(RegionDescriptor.class, RegionDescriptor.getSerializer(engine));
		kryo.register(FontDescriptor.class, FontDescriptor.getSerializer(engine));
		
		
		// Misc serializers
		kryo.register(Tile.class, Tile.getSerializer(engine));

		
		
		// Component serializers
		
		kryo.register(PlayerComponent.class, PlayerComponent.getSerializer(engine));
		kryo.register(EnemyComponent.class, EnemyComponent.getSerializer(engine));

		kryo.register(ShopKeeperComponent.class, ShopKeeperComponent.getSerializer(engine));
		kryo.register(SoulbenderComponent.class, SoulbenderComponent.getSerializer(engine));
		kryo.register(StatueComponent.class, StatueComponent.getSerializer(engine));


		kryo.register(HumanoidComponent.class, HumanoidComponent.getSerializer(engine));

		kryo.register(SpriteComponent.class, SpriteComponent.getSerializer(engine));
		kryo.register(AnimationComponent.class, AnimationComponent.getSerializer(engine));
		kryo.register(StateComponent.class, StateComponent.getSerializer(engine));
		kryo.register(VisualEffectComponent.class, VisualEffectComponent.getSerializer(engine));

		
		kryo.register(ParentEntityComponent.class, ParentEntityComponent.getSerializer(engine));
		kryo.register(GridPositionComponent.class, GridPositionComponent.getSerializer(engine, loadedRooms));
//		kryo.register(TileComponent.class, TileComponent.getSerializer(engine, floor));
		kryo.register(DoorComponent.class, DoorComponent.getSerializer(engine, loadedRooms));
		kryo.register(ExitComponent.class, ExitComponent.getSerializer(engine));
		
		
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

		
		kryo.register(TextComponent.class, TextComponent.getSerializer(engine));
		kryo.register(DamageDisplayComponent.class, DamageDisplayComponent.getSerializer(engine));
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
