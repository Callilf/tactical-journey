package com.dokkaebistudio.tacticaljourney.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
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
import com.dokkaebistudio.tacticaljourney.room.Floor;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class Persister {
	
	public PublicPooledEngine engine;
	private List<Long> savedEntities = new ArrayList<>();
	private Map<Long, PooledEntity> loadedEntities = new HashMap<>();
	
	public Persister(PublicPooledEngine engine) {
		this.engine = engine;
	}
	

	
	
	
	
	public void saveEnemy(Entity enemy, Floor floor) {
		Kryo kryo = new Kryo();
		
		this.registerSerializers(kryo, floor, floor.getGameScreen().floors);
		
		
		try {
			File f = new File("entity.bin");
			if (f.exists()) f.delete();
			f.createNewFile();
			Output output = new Output(new FileOutputStream(f));
			kryo.writeObject(output, enemy);
			output.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KryoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	

	public Entity loadEnemy(Floor floor) {
		Entity loadedEntity = null;
		Kryo kryo = new Kryo();
		
		this.registerSerializers(kryo, floor, floor.getGameScreen().floors);
		
		try {
			File f = new File("entity.bin");
		    Input input = new Input(new FileInputStream(f));
		    loadedEntity = kryo.readObject(input, PooledEntity.class);
		    input.close();   
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KryoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		floor.getActiveRoom().addEnemy(loadedEntity);
		return loadedEntity;
	}
	
	
	
	
	// TEST
	public void save(EnemyComponent enemyCompo) {
		
		Kryo kryo = new Kryo();
		
		kryo.register(EnemyComponent.class, EnemyComponent.getSerializer(engine, null));
		
		try {
			File f = new File("stinger.bin");
			if (f.exists()) f.delete();
			f.createNewFile();
			Output output = new Output(new FileOutputStream(f));
			kryo.writeObject(output, enemyCompo);
			output.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KryoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public EnemyComponent loadStinger() {
		EnemyComponent object2 = null;
		Kryo kryo = new Kryo();
		
		kryo.register(EnemyComponent.class, EnemyComponent.getSerializer(engine, null));
		
		try {
			File f = new File("stinger.bin");
		    Input input = new Input(new FileInputStream(f));
		    object2 = kryo.readObject(input, EnemyComponent.class);
		    input.close();   
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KryoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object2;
	}
	
	
	
	
	public Serializer getEngineSerializer() {
		return new Serializer<PublicPooledEngine>() {

			@Override
			public void write(Kryo kryo, Output output, PublicPooledEngine object) {}

			@Override
			public PublicPooledEngine read(Kryo kryo, Input input, Class<PublicPooledEngine> type) {
				return engine;
			}
			
		};
	}

	public Serializer getEntitySerializer(final Floor floor) {
		return new Serializer<PooledEntity>() {

			@Override
			public void write(Kryo kryo, Output output, PooledEntity entityToSave) {
				// Write the ID
				output.writeLong(entityToSave.id);
				
				// If the entity has already been saved, stop here
				if (!savedEntities.contains(entityToSave.id)) {
					// If not, save the whole entity
					output.writeInt(entityToSave.flags);
					output.writeInt(entityToSave.getComponents().size());
					for (Component compo : entityToSave.getComponents()) {
						kryo.writeClassAndObject(output, compo);
					}
					savedEntities.add(entityToSave.id);
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
				
				int componentNumber = input.readInt();
				for (int i=0 ; i<componentNumber ; i++) {
					loadedEntity.add((Component) kryo.readClassAndObject(input));
				}
				
				GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(loadedEntity);
				if (gridPositionComponent.room != null) {
					gridPositionComponent.coord(loadedEntity, gridPositionComponent.coord(), gridPositionComponent.room);
					
					for (Component compo : loadedEntity.getComponents()) {
						if (compo instanceof RoomSystem) {
							((RoomSystem)compo).enterRoom(gridPositionComponent.room);
						}
					}
				}
				
				loadedEntities.put(entityId, loadedEntity);
				return loadedEntity;
			}
			
		};
	}
	
	
	
	public void registerSerializers(Kryo kryo, Floor currentFloor, List<Floor> floors) {
		kryo.register(PooledEntity.class, getEntitySerializer(currentFloor));
		kryo.register(RegionDescriptor.class, RegionDescriptor.getSerializer(engine));
		kryo.register(FontDescriptor.class, FontDescriptor.getSerializer(engine));
		
		
		kryo.register(PlayerComponent.class, PlayerComponent.getSerializer(engine, currentFloor));
		kryo.register(EnemyComponent.class, EnemyComponent.getSerializer(engine, currentFloor));

		kryo.register(ShopKeeperComponent.class, ShopKeeperComponent.getSerializer(engine, currentFloor));
		kryo.register(SoulbenderComponent.class, SoulbenderComponent.getSerializer(engine, currentFloor));
		kryo.register(StatueComponent.class, StatueComponent.getSerializer(engine, currentFloor));


		kryo.register(HumanoidComponent.class, HumanoidComponent.getSerializer(engine, currentFloor));

		kryo.register(SpriteComponent.class, SpriteComponent.getSerializer(engine, currentFloor));
		kryo.register(AnimationComponent.class, AnimationComponent.getSerializer(engine, currentFloor));
		kryo.register(StateComponent.class, StateComponent.getSerializer(engine, currentFloor));
		kryo.register(VisualEffectComponent.class, VisualEffectComponent.getSerializer(engine, currentFloor));

		
		kryo.register(ParentEntityComponent.class, ParentEntityComponent.getSerializer(engine, currentFloor));
		kryo.register(GridPositionComponent.class, GridPositionComponent.getSerializer(engine, currentFloor));
//		kryo.register(TileComponent.class, TileComponent.getSerializer(engine, floor));
		kryo.register(DoorComponent.class, DoorComponent.getSerializer(engine, currentFloor));
		kryo.register(ExitComponent.class, ExitComponent.getSerializer(engine, currentFloor, floors));
		
		
		kryo.register(MoveComponent.class, MoveComponent.getSerializer(engine, currentFloor));
		kryo.register(AttackComponent.class, AttackComponent.getSerializer(engine, currentFloor));
		kryo.register(SkillComponent.class, SkillComponent.getSerializer(engine, currentFloor));
		kryo.register(WalletComponent.class, WalletComponent.getSerializer(engine, currentFloor));
		kryo.register(AmmoCarrierComponent.class, AmmoCarrierComponent.getSerializer(engine, currentFloor));
		kryo.register(InventoryComponent.class, InventoryComponent.getSerializer(engine, currentFloor));
		kryo.register(AlterationReceiverComponent.class, AlterationReceiverComponent.getSerializer(engine, currentFloor));
		kryo.register(StatusReceiverComponent.class, StatusReceiverComponent.getSerializer(engine, currentFloor));

		
		kryo.register(ExperienceComponent.class, ExperienceComponent.getSerializer(engine, currentFloor));

		kryo.register(LootableComponent.class, LootableComponent.getSerializer(engine, currentFloor));
		kryo.register(ItemComponent.class, ItemComponent.getSerializer(engine, currentFloor));

		kryo.register(HealthComponent.class, HealthComponent.getSerializer(engine, currentFloor));
		kryo.register(ExpRewardComponent.class, ExpRewardComponent.getSerializer(engine, currentFloor));
		kryo.register(LootRewardComponent.class, LootRewardComponent.getSerializer(engine, currentFloor));

		
		kryo.register(TextComponent.class, TextComponent.getSerializer(engine, currentFloor));
		kryo.register(DamageDisplayComponent.class, DamageDisplayComponent.getSerializer(engine, currentFloor));
		kryo.register(DialogComponent.class, DialogComponent.getSerializer(engine, currentFloor));

//		kryo.register(WheelModifierComponent.class, WheelModifierComponent.getSerializer(engine, floor));
		kryo.register(WheelComponent.class, WheelComponent.getSerializer(engine, currentFloor));
		
		kryo.register(InspectableComponent.class, InspectableComponent.getSerializer(engine, currentFloor));
		
		kryo.register(SolidComponent.class, SolidComponent.getSerializer(engine, currentFloor));
		kryo.register(ChasmComponent.class, ChasmComponent.getSerializer(engine, currentFloor));
		kryo.register(FlyComponent.class, FlyComponent.getSerializer(engine, currentFloor));

		kryo.register(CreepComponent.class, CreepComponent.getSerializer(engine, currentFloor));
		kryo.register(CreepEmitterComponent.class, CreepEmitterComponent.getSerializer(engine, currentFloor));
		
		kryo.register(OrbComponent.class, OrbComponent.getSerializer(engine, currentFloor));
		kryo.register(OrbCarrierComponent.class, OrbCarrierComponent.getSerializer(engine, currentFloor));
		
		kryo.register(FlammableComponent.class, FlammableComponent.getSerializer(engine, currentFloor));
		kryo.register(ExplosiveComponent.class, ExplosiveComponent.getSerializer(engine, currentFloor));
		kryo.register(DestructibleComponent.class, DestructibleComponent.getSerializer(engine, currentFloor));

		
	
	}
}
