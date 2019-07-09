package com.dokkaebistudio.tacticaljourney.ces.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.enums.DirectionEnum;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Marker to indicate that this entity is being pushed.
 * @author Callil
 *
 */
public class ThrowbackComponent implements Component, Poolable {

	private DirectionEnum direction;
	private int numberOfTiles;
	
	private int currentNumberOfTilesMoved;
	
	
	@Override
	public void reset() {
		this.currentNumberOfTilesMoved = 0;		
	}

	
	
	public void increaseCurrentNumberOfTilesMoved() {
		this.currentNumberOfTilesMoved ++;
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


	public int getCurrentNumberOfTilesMoved() {
		return currentNumberOfTilesMoved;
	}


	public void setCurrentNumberOfTilesMoved(int currentNumberOfTilesMoved) {
		this.currentNumberOfTilesMoved = currentNumberOfTilesMoved;
	}

	
	public static Serializer<ThrowbackComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<ThrowbackComponent>() {

			@Override
			public void write(Kryo kryo, Output output, ThrowbackComponent object) {}

			@Override
			public ThrowbackComponent read(Kryo kryo, Input input, Class<? extends ThrowbackComponent> type) {
				ThrowbackComponent compo = engine.createComponent(ThrowbackComponent.class);
				return compo;
			}
		
		};
	}








}
