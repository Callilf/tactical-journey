package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.enemies.Enemy;
import com.dokkaebistudio.tacticaljourney.enemies.enums.EnemyFactionEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class EnemyComponent implements Component, Poolable {
		
	/** The type of enemy. */
	private Enemy type;

	/** Whether this enemy can activate orbs on contact. */
	private boolean canActivateOrbs = true;
	
	/** The faction of this enemy. Enemies from the same faction won't attack
	 * themselves and can have synergies. */
	private EnemyFactionEnum faction;

	
	@Override
	public void reset() {
		type = null;
		canActivateOrbs = true;
	}
	
	
	
	
	//************************
	// Events

	public void onRoomVisited(Entity enemy, Room room) {
		this.type.onRoomVisited(enemy, room);
	}
	
	public void onStartTurn(Entity enemy, Room room) {
		this.type.onStartTurn(enemy, room);
	}
	
	public void onEndTurn(Entity enemy, Room room) {
		this.type.onEndTurn(enemy, room);
	}
	
	
	
	public void onAttack(Entity enemy, Entity target, Room room) {
		this.type.onAttack(enemy, target, room);
	}
	
	public boolean onReceiveAttack(Entity enemy, Entity attacker, Room room) {
		return this.type.onReceiveAttack(enemy, attacker, room);
	}
	
	public void onReceiveDamage(int damage, Entity enemy, Entity attacker, Room room) {
		this.type.onReceiveDamage(damage, enemy, attacker, room);
	}
	
	public void onDeath(Entity enemy, Entity attacker, Room room) {
		this.type.onDeath(enemy, attacker, room);
	}
	
	
	
	
	
	
	
	
	
	// Getters and Setters

	public EnemyFactionEnum getFaction() {
		return faction;
	}

	public void setFaction(EnemyFactionEnum faction) {
		this.faction = faction;
	}

	public Enemy getType() {
		return type;
	}

	public void setType(Enemy type) {
		this.type = type;
	}

	public boolean canActivateOrbs() {
		return canActivateOrbs;
	}

	public void setCanActivateOrbs(boolean canActivateOrbs) {
		this.canActivateOrbs = canActivateOrbs;
	}

	
	
	
	public static Serializer<EnemyComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<EnemyComponent>() {

			@Override
			public void write(Kryo kryo, Output output, EnemyComponent object) {
				
				kryo.writeClassAndObject(output, object.type);
				output.writeBoolean(object.canActivateOrbs);
				output.writeString(object.faction.name());
			}

			@Override
			public EnemyComponent read(Kryo kryo, Input input, Class<EnemyComponent> type) {
				EnemyComponent compo = engine.createComponent(EnemyComponent.class);
				
				compo.type = (Enemy) kryo.readClassAndObject(input);
				compo.canActivateOrbs = input.readBoolean();
				compo.faction = EnemyFactionEnum.valueOf(input.readString());
				
				return compo;
			}
		
		};
	}

}
