package com.dokkaebistudio.tacticaljourney.components.neutrals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.room.Floor;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Marker to indicate that this entity is a soul bender.
 * @author Callil
 *
 */
public class SoulbenderComponent implements Component, Poolable {
	
	private boolean hasInfused = false;
	
	private int price = 0;
	
	/** The different sentences the soul bender can say when being talked to. */
	private List<String> mainSpeeches = new ArrayList<>();
	private int currentSpeech;
	
	@Override
	public void reset() {
		this.currentSpeech = 0;
		this.mainSpeeches.clear();
		this.hasInfused = false;
	}


	
	//**************************
	// Speech related methods
	
	public void addSpeech(String s) {
		mainSpeeches.add(s);
	}
	
	public String getSpeech() {
		String speech = mainSpeeches.get(currentSpeech);
		currentSpeech ++;
		if (currentSpeech >= mainSpeeches.size()) currentSpeech = 0;
		return speech;
	}
	
	
	

	//*********************************
	// Getters and Setters


	public boolean hasInfused() {
		return hasInfused;
	}



	public void setHasInfused(boolean hasInfused) {
		this.hasInfused = hasInfused;
	}



	public int getPrice() {
		return price;
	}



	public void setPrice(int price) {
		this.price = price;
	}
	
	

	
	
	public static Serializer<SoulbenderComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<SoulbenderComponent>() {

			@Override
			public void write(Kryo kryo, Output output, SoulbenderComponent object) {
				output.writeBoolean(object.hasInfused);
				output.writeInt(object.price);
				kryo.writeClassAndObject(output, object.mainSpeeches);
				output.writeInt(object.currentSpeech);
			}

			@Override
			public SoulbenderComponent read(Kryo kryo, Input input, Class<SoulbenderComponent> type) {
				SoulbenderComponent compo = engine.createComponent(SoulbenderComponent.class);
				compo.hasInfused = input.readBoolean();
				compo.price = input.readInt();
				compo.mainSpeeches = (List<String>) kryo.readClassAndObject(input);
				compo.currentSpeech = input.readInt();

				return compo;
			}
		
		};
	}
	
}
