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

package com.dokkaebistudio.tacticaljourney.ces.components.display;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.enums.StatesEnum;
import com.dokkaebistudio.tacticaljourney.singletons.AnimationSingleton;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class AnimationComponent implements Component, Poolable {
	private IntMap<Integer> animationsIndexes = new IntMap<>();
//	private IntMap<Animation<Sprite>> animations = new IntMap<>();
	
	@Override
	public void reset() {
		animationsIndexes.clear();
//		animations.clear();
	}
	
	
	public void addAnimation(StatesEnum state, Animation<Sprite> anim) {
		int index = AnimationSingleton.getInstance().getIndex(anim);
		if (index == -1) {
			System.out.println("EPIC FAIL in animation component");
		} else {
			animationsIndexes.put(state != null ? state.getState() : 0, AnimationSingleton.getInstance().getIndex(anim));
		}
	}
	
	public Animation<Sprite> getAnimation(StatesEnum state) {
		int s = state != null ? state.getState() : 0;
		if (animationsIndexes.containsKey(s)) {
			return AnimationSingleton.getInstance().getAnimation(animationsIndexes.get(s));
		} else {
			return AnimationSingleton.getInstance().getAnimation(animationsIndexes.get(0));
		}
	}
	
	
	
	
	
	
	public static Serializer<AnimationComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<AnimationComponent>() {

			@Override
			public void write(Kryo kryo, Output output, AnimationComponent object) {
				kryo.writeClassAndObject(output, object.animationsIndexes);
			}

			@Override
			public AnimationComponent read(Kryo kryo, Input input, Class<? extends AnimationComponent> type) {
				AnimationComponent compo = engine.createComponent(AnimationComponent.class);
				compo.animationsIndexes.putAll((IntMap<Integer>) kryo.readClassAndObject(input));
				return compo;
			}
		
		};
	}
	
}
