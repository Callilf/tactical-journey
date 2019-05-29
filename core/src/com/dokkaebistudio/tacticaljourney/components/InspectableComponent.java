package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.room.Floor;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Marker to indicate that this entity can be inspected.
 * @author Callil
 *
 */
public class InspectableComponent implements Component, Poolable {

	private String title;
	private String description;
	
	/** Whether the inspection if displayed in the big popup (for enemies or special entities). */
	private boolean bigPopup;
	
	
	
	@Override
	public void reset() {
		this.setBigPopup(false);
	}
	
	//****************************
	// Getters and Setters
	

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isBigPopup() {
		return bigPopup;
	}

	public void setBigPopup(boolean bigPopup) {
		this.bigPopup = bigPopup;
	} 

	
	
	
	public static Serializer<InspectableComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<InspectableComponent>() {

			@Override
			public void write(Kryo kryo, Output output, InspectableComponent object) {
				output.writeString(object.title);
				output.writeString(object.description);
				output.writeBoolean(object.bigPopup);
			}

			@Override
			public InspectableComponent read(Kryo kryo, Input input, Class<? extends InspectableComponent> type) {
				InspectableComponent compo = engine.createComponent(InspectableComponent.class);
				compo.title = input.readString();
				compo.description = input.readString();
				compo.bigPopup = input.readBoolean();
				return compo;
			}
		
		};
	}
}
