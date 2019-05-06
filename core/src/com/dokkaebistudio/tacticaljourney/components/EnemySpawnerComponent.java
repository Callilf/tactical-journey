package com.dokkaebistudio.tacticaljourney.components;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.PooledEngine;
import com.dokkaebistudio.tacticaljourney.creature.enemies.enums.EnemyTypeEnum;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Marker to indicate that this entity can spawn enemies.
 * @author Callil
 *
 */
public class EnemySpawnerComponent implements Component {

	/** The rate of spawn (between 0 and 100). */
	private Map<EnemyTypeEnum, Integer> spawnChances = new LinkedHashMap<>();
	
	/** The sum of all rates. */
	private int totalSpawnChance;
	
	
	/**
	 * Add an enemy spawn chance.
	 * @param rate the rate of spawn (on 100)
	 * @param action the action to perform (usually enemyFactor.create...)
	 */
	public void addSpawnChance(int rate, EnemyTypeEnum type) {
		spawnChances.put(type, rate);
		totalSpawnChance += rate;
	}
	
	/**
	 * Get the action matching the given random int.
	 * @param randomInt the random int
	 * @return the action that matches. Null if no action matches, but if randomInt is lower than totalSpawnChance it should never happen.
	 */
	public EnemyTypeEnum getActionForRandomInt(int randomInt) {
		int totalChance = 0;
		for(Entry<EnemyTypeEnum, Integer> entry : spawnChances.entrySet()) {
			totalChance += entry.getValue();
			if (randomInt < totalChance) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	
	// Getters and setters
	
	public int getTotalSpawnChance() {
		return totalSpawnChance;
	}	
	
	
	public static Serializer<EnemySpawnerComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<EnemySpawnerComponent>() {

			@Override
			public void write(Kryo kryo, Output output, EnemySpawnerComponent object) {
				output.writeInt(object.totalSpawnChance);
				kryo.writeClassAndObject(output, object.spawnChances);
			}

			@Override
			public EnemySpawnerComponent read(Kryo kryo, Input input, Class<EnemySpawnerComponent> type) {
				EnemySpawnerComponent compo = engine.createComponent(EnemySpawnerComponent.class);
				compo.totalSpawnChance = input.readInt();
				compo.spawnChances = (Map<EnemyTypeEnum, Integer>) kryo.readClassAndObject(input);
				
				return compo;
			}
		
		};
	}




}
