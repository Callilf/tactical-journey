package com.dokkaebistudio.tacticaljourney.components.neutrals;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Pool.Poolable;
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
	private boolean receivedCatalyst = false;
	
	private int price = 0;
	
	/** The different sentences the soul bender can say when being talked to. */
	private List<String> mainSpeeches = new ArrayList<>();
	private int currentSpeech;
	
	private List<String> afterInfusionSpeeches = new ArrayList<>();
	private int currentAfterInfusionSpeech;
	
	private String divineCatalystSpeech;
	private String afterCatalystSpeech;

	
	@Override
	public void reset() {
		this.currentSpeech = 0;
		this.mainSpeeches.clear();
		this.hasInfused = false;
		this.receivedCatalyst = false;
		this.currentAfterInfusionSpeech = 0;
		this.afterInfusionSpeeches.clear();
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
	
	
	public void addAfterInfusionSpeech(String s) {
		afterInfusionSpeeches.add(s);
	}
	
	public String getAfterInfusionSpeech() {
		String speech = afterInfusionSpeeches.get(currentAfterInfusionSpeech);
		currentAfterInfusionSpeech ++;
		if (currentAfterInfusionSpeech >= afterInfusionSpeeches.size()) currentAfterInfusionSpeech = 0;
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
	
	
	public void setDivineCatalystSpeech(String divineCatalystSpeech) {
		this.divineCatalystSpeech = divineCatalystSpeech;
	}
	
	public String getDivineCatalystSpeech() {
		return divineCatalystSpeech;
	}
	
	public void setAfterCatalystSpeech(String afterCatalystSpeech) {
		this.afterCatalystSpeech = afterCatalystSpeech;
	}
	
	public String getAfterCatalystSpeech() {
		return afterCatalystSpeech;
	}

	
	
	public static Serializer<SoulbenderComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<SoulbenderComponent>() {

			@Override
			public void write(Kryo kryo, Output output, SoulbenderComponent object) {
				output.writeBoolean(object.hasInfused);
				output.writeInt(object.price);
				kryo.writeClassAndObject(output, object.mainSpeeches);
				output.writeInt(object.currentSpeech);
				kryo.writeClassAndObject(output, object.afterInfusionSpeeches);
				output.writeInt(object.currentAfterInfusionSpeech);
				output.writeString(object.divineCatalystSpeech);
			}

			@Override
			public SoulbenderComponent read(Kryo kryo, Input input, Class<SoulbenderComponent> type) {
				SoulbenderComponent compo = engine.createComponent(SoulbenderComponent.class);
				compo.hasInfused = input.readBoolean();
				compo.price = input.readInt();
				compo.mainSpeeches = (List<String>) kryo.readClassAndObject(input);
				compo.currentSpeech = input.readInt();
				compo.afterInfusionSpeeches = (List<String>) kryo.readClassAndObject(input);
				compo.currentAfterInfusionSpeech = input.readInt();
				compo.divineCatalystSpeech = input.readString();
				return compo;
			}
		
		};
	}



	public boolean isReceivedCatalyst() {
		return receivedCatalyst;
	}



	public void setReceivedCatalyst(boolean receivedCatalyst) {
		this.receivedCatalyst = receivedCatalyst;
	}
	
}
