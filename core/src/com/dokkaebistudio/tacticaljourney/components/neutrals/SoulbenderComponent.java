package com.dokkaebistudio.tacticaljourney.components.neutrals;

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
		
	private String divineCatalystSpeech;
	private String afterCatalystSpeech;

	
	@Override
	public void reset() {
		this.hasInfused = false;
		this.receivedCatalyst = false;
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
				output.writeString(object.divineCatalystSpeech);
			}

			@Override
			public SoulbenderComponent read(Kryo kryo, Input input, Class<? extends SoulbenderComponent> type) {
				SoulbenderComponent compo = engine.createComponent(SoulbenderComponent.class);
				compo.hasInfused = input.readBoolean();
				compo.price = input.readInt();
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
		this.setPrice(0);
	}
	
}
