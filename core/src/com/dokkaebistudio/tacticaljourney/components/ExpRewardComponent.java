package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.PooledEngine;
import com.dokkaebistudio.tacticaljourney.room.Floor;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class ExpRewardComponent implements Component {


	/** The amount of xp to gain from this entity. */
	private int expGain;
	
	
	

	public int getExpGain() {
		return expGain;
	}

	public void setExpGain(int expGain) {
		this.expGain = expGain;
	}

	
	
	
	public static Serializer<ExpRewardComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<ExpRewardComponent>() {

			@Override
			public void write(Kryo kryo, Output output, ExpRewardComponent object) {
				output.writeInt(object.expGain);
			}

			@Override
			public ExpRewardComponent read(Kryo kryo, Input input, Class<ExpRewardComponent> type) {
				ExpRewardComponent compo = engine.createComponent(ExpRewardComponent.class);
				compo.expGain = input.readInt();
				return compo;
			}
		
		};
	}
}
