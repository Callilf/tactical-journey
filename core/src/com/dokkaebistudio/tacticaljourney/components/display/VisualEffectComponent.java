/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.dokkaebistudio.tacticaljourney.components.display;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.room.Floor;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Marker to indicate that the entity is a visual effect and has to disappear.
 * @author Callil
 *
 */
public class VisualEffectComponent implements Component, Poolable {
	
	@Override
	public void reset() {
	}
	
	
	
	
	
	
	
	public static Serializer<VisualEffectComponent> getSerializer(final PooledEngine engine, final Floor floor) {
		return new Serializer<VisualEffectComponent>() {

			@Override
			public void write(Kryo kryo, Output output, VisualEffectComponent object) {}

			@Override
			public VisualEffectComponent read(Kryo kryo, Input input, Class<VisualEffectComponent> type) {
				VisualEffectComponent compo = engine.createComponent(VisualEffectComponent.class);
				return compo;
			}
		
		};
	}
}
