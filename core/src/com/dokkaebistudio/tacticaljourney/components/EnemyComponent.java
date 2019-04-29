package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.creature.enemies.enums.EnemyFactionEnum;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class EnemyComponent implements Component, Poolable {
		
	/** Whether this enemy can activate orbs on contact. */
	private boolean canActivateOrbs = true;
	
	/** The faction of this enemy. Enemies from the same faction won't attack
	 * themselves and can have synergies. */
	private EnemyFactionEnum faction;

	
	@Override
	public void reset() {
		canActivateOrbs = true;
	}
		
	
	
	
	// Getters and Setters

	public EnemyFactionEnum getFaction() {
		return faction;
	}

	public void setFaction(EnemyFactionEnum faction) {
		this.faction = faction;
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
				
				output.writeBoolean(object.canActivateOrbs);
				output.writeString(object.faction.name());
			}

			@Override
			public EnemyComponent read(Kryo kryo, Input input, Class<EnemyComponent> type) {
				EnemyComponent compo = engine.createComponent(EnemyComponent.class);
				
				compo.canActivateOrbs = input.readBoolean();
				compo.faction = EnemyFactionEnum.valueOf(input.readString());
				
				return compo;
			}
		
		};
	}

}
