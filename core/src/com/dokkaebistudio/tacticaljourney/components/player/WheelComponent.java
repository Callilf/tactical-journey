package com.dokkaebistudio.tacticaljourney.components.player;

import java.util.LinkedList;
import java.util.List;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.PooledEngine;
import com.dokkaebistudio.tacticaljourney.room.Floor;
import com.dokkaebistudio.tacticaljourney.wheel.Sector;
import com.dokkaebistudio.tacticaljourney.wheel.Sector.Hit;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * This component should be attached to weapons, as it defines the base
 * attack wheel to display (without modifiers from other sources).
 */
public class WheelComponent implements Component {

    public List<Sector> sectors = new LinkedList<Sector>();

    public void addSector(int range, Hit hit) {
        sectors.add(new Sector(range, hit));
    }
    
    
    
	public static Serializer<WheelComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<WheelComponent>() {

			@Override
			public void write(Kryo kryo, Output output, WheelComponent object) {
				kryo.writeClassAndObject(output, object.sectors);
			}

			@Override
			public WheelComponent read(Kryo kryo, Input input, Class<WheelComponent> type) {
				WheelComponent compo = engine.createComponent(WheelComponent.class);
				compo.sectors = (List<Sector>) kryo.readClassAndObject(input);
				return compo;
			}
		
		};
	}

}
