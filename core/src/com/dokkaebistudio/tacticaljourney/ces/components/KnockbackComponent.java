package com.dokkaebistudio.tacticaljourney.ces.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.enums.DirectionEnum;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Marker to indicate that this entity is being pushed back.
 * @author Callil
 *
 */
public class KnockbackComponent implements Component, Poolable {

	private DirectionEnum direction;
	private int numberOfTiles;
	private Entity attacker;
	
	private int currentNumberOfTilesMoved;
	
	
	@Override
	public void reset() {
		this.attacker = null;	
	}


	
	
	public DirectionEnum getDirection() {
		return direction;
	}


	public void setDirection(DirectionEnum direction) {
		this.direction = direction;
	}


	public int getNumberOfTiles() {
		return numberOfTiles;
	}


	public void setNumberOfTiles(int numberOfTiles) {
		this.numberOfTiles = numberOfTiles;
	}

	public Entity getAttacker() {
		return attacker;
	}

	public void setAttacker(Entity attacker) {
		this.attacker = attacker;
	}

	public static Serializer<KnockbackComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<KnockbackComponent>() {

			@Override
			public void write(Kryo kryo, Output output, KnockbackComponent object) {}

			@Override
			public KnockbackComponent read(Kryo kryo, Input input, Class<? extends KnockbackComponent> type) {
				KnockbackComponent compo = engine.createComponent(KnockbackComponent.class);
				return compo;
			}
		
		};
	}

}
