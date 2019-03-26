package com.dokkaebistudio.tacticaljourney.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.ashley.PublicPooledEngine;
import com.dokkaebistudio.tacticaljourney.ashley.PublicPooledEngine.PooledEntity;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.DestructibleComponent;
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
	
	public Persister(PublicPooledEngine engine) {
		this.engine = engine;
	}
	

	
	
	
	
	public void saveEnemy(Entity enemy, Floor floor) {
		Kryo kryo = new Kryo();
		
		this.registerSerializers(kryo, floor);
		
		
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
		Entity player = null;
		Kryo kryo = new Kryo();
		
		this.registerSerializers(kryo, floor);
		
		try {
			File f = new File("entity.bin");
		    Input input = new Input(new FileInputStream(f));
		    player = kryo.readObject(input, PooledEntity.class);
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
		return player;
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
			public void write(Kryo kryo, Output output, PublicPooledEngine object) {
				for (Entity entity : object.getEntities()) {
					if (Mappers.playerComponent.has(entity)) {
						System.out.println("found player");
						kryo.writeObject(output, entity);
					}
				}
			}

			@Override
			public PublicPooledEngine read(Kryo kryo, Input input, Class<PublicPooledEngine> type) {
				// TODO Auto-generated method stub
				return null;
			}
			
		};
	}

	public Serializer getEntitySerializer(final Floor floor) {
		return new Serializer<PooledEntity>() {

			@Override
			public void write(Kryo kryo, Output output, PooledEntity object) {
				output.writeInt(object.flags);
				output.writeInt(object.getComponents().size());
				for (Component compo : object.getComponents()) {
					kryo.writeClassAndObject(output, compo);
				}
			}

			@Override
			public PooledEntity read(Kryo kryo, Input input, Class<PooledEntity> type) {
				PooledEntity createEntity = (PooledEntity) engine.createEntity();
				createEntity.flags = input.readInt();
				
				int componentNumber = input.readInt();
				for (int i=0 ; i<componentNumber ; i++) {
					createEntity.add((Component) kryo.readClassAndObject(input));
				}
				
				
				GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(createEntity);
				if (gridPositionComponent.room != null) {
					gridPositionComponent.coord(createEntity, gridPositionComponent.coord(), gridPositionComponent.room);
					
					for (Component compo : createEntity.getComponents()) {
						if (compo instanceof RoomSystem) {
							((RoomSystem)compo).enterRoom(gridPositionComponent.room);
						}
					}
				}
				
				return createEntity;
			}
			
		};
	}
	
	
	
	public void registerSerializers(Kryo kryo, Floor floor) {
		kryo.register(PooledEntity.class, getEntitySerializer(floor));
		kryo.register(RegionDescriptor.class, RegionDescriptor.getSerializer(engine));
		
		
//		kryo.register(PlayerComponent.class, PlayerComponent.getSerializer(engine, floor));
		kryo.register(EnemyComponent.class, EnemyComponent.getSerializer(engine, floor));

//		kryo.register(ShopKeeperComponent.class, ShopKeeperComponent.getSerializer(engine, floor));
//		kryo.register(SoulbenderComponent.class, SoulbenderComponent.getSerializer(engine, floor));
//		kryo.register(StatueComponent.class, StatueComponent.getSerializer(engine, floor));


		kryo.register(HumanoidComponent.class, HumanoidComponent.getSerializer(engine, floor));

		kryo.register(SpriteComponent.class, SpriteComponent.getSerializer(engine, floor));
		kryo.register(AnimationComponent.class, AnimationComponent.getSerializer(engine, floor));
		kryo.register(StateComponent.class, StateComponent.getSerializer(engine, floor));
		kryo.register(VisualEffectComponent.class, VisualEffectComponent.getSerializer(engine, floor));

		
//		kryo.register(ParentEntityComponent.class, ParentEntityComponent.getSerializer(engine, floor));
		kryo.register(GridPositionComponent.class, GridPositionComponent.getSerializer(engine, floor));
//		kryo.register(TileComponent.class, TileComponent.getSerializer(engine, floor));
//		kryo.register(DoorComponent.class, DoorComponent.getSerializer(engine, floor));
//		kryo.register(ExitComponent.class, ExitComponent.getSerializer(engine, floor));
		
		
		kryo.register(MoveComponent.class, MoveComponent.getSerializer(engine, floor));
		kryo.register(AttackComponent.class, AttackComponent.getSerializer(engine, floor));
//		kryo.register(SkillComponent.class, SkillComponent.getSerializer(engine, floor));
//		kryo.register(WalletComponent.class, WalletComponent.getSerializer(engine, floor));
//		kryo.register(AmmoCarrierComponent.class, AmmoCarrierComponent.getSerializer(engine, floor));
//		kryo.register(InventoryComponent.class, InventoryComponent.getSerializer(engine, floor));
//		kryo.register(AlterationReceiverComponent.class, AlterationReceiverComponent.getSerializer(engine, floor));
		kryo.register(StatusReceiverComponent.class, StatusReceiverComponent.getSerializer(engine, floor));

		
//		kryo.register(ExperienceComponent.class, ExperienceComponent.getSerializer(engine, floor));

//		kryo.register(LootableComponent.class, LootableComponent.getSerializer(engine, floor));
		kryo.register(ItemComponent.class, ItemComponent.getSerializer(engine, floor));

		kryo.register(HealthComponent.class, HealthComponent.getSerializer(engine, floor));
		kryo.register(ExpRewardComponent.class, ExpRewardComponent.getSerializer(engine, floor));
		kryo.register(LootRewardComponent.class, LootRewardComponent.getSerializer(engine, floor));

		
		kryo.register(TextComponent.class, TextComponent.getSerializer(engine, floor));
		kryo.register(DamageDisplayComponent.class, DamageDisplayComponent.getSerializer(engine, floor));
//		kryo.register(DialogComponent.class, DialogComponent.getSerializer(engine, floor));

//		kryo.register(WheelModifierComponent.class, WheelModifierComponent.getSerializer(engine, floor));
//		kryo.register(WheelComponent.class, WheelComponent.getSerializer(engine, floor));
		
		kryo.register(InspectableComponent.class, InspectableComponent.getSerializer(engine, floor));
		
		kryo.register(SolidComponent.class, SolidComponent.getSerializer(engine, floor));
//		kryo.register(ChasmComponent.class, ChasmComponent.getSerializer(engine, floor));
		kryo.register(FlyComponent.class, FlyComponent.getSerializer(engine, floor));

		kryo.register(CreepComponent.class, CreepComponent.getSerializer(engine, floor));
		kryo.register(CreepEmitterComponent.class, CreepEmitterComponent.getSerializer(engine, floor));
		
//		kryo.register(OrbComponent.class, OrbComponent.getSerializer(engine, floor));
//		kryo.register(OrbCarrierComponent.class, OrbCarrierComponent.getSerializer(engine, floor));
		
		kryo.register(FlammableComponent.class, FlammableComponent.getSerializer(engine, floor));
		kryo.register(ExplosiveComponent.class, ExplosiveComponent.getSerializer(engine, floor));
		kryo.register(DestructibleComponent.class, DestructibleComponent.getSerializer(engine, floor));

		
	
	}
}
