package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.ashley.PublicEntity;
import com.dokkaebistudio.tacticaljourney.dialog.AbstractDialogs;
import com.dokkaebistudio.tacticaljourney.dialog.Dialog;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Indicate that this entity can speak with the player.
 * @author Callil
 */
public class SpeakerComponent implements Component, Poolable {

	private AbstractDialogs dialogs;
	
	@Override
	public void reset() {}
	
	
	public Dialog getSpeech(Entity speakerEntity) {
		return this.dialogs.getDialog((PublicEntity) speakerEntity);
	}
	
	public Dialog getSpeech(String tag) {
		return this.dialogs.getDialog(tag);
	}
	
	public void setDialogs(AbstractDialogs dialogs) {
		this.dialogs = dialogs;
	}
	
	
	public static Serializer<SpeakerComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<SpeakerComponent>() {

			@Override
			public void write(Kryo kryo, Output output, SpeakerComponent object) {
				kryo.writeClassAndObject(output, object.dialogs);
			}

			@Override
			public SpeakerComponent read(Kryo kryo, Input input, Class<? extends SpeakerComponent> type) {
				SpeakerComponent compo = engine.createComponent(SpeakerComponent.class);
				compo.dialogs = (AbstractDialogs) kryo.readClassAndObject(input);
				return compo;
			}
		
		};
	}
}
