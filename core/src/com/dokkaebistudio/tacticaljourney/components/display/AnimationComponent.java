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
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.AnimationSingleton;
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
	
	
	public void addAnimation(int state, Animation<Sprite> anim) {
		int index = AnimationSingleton.getInstance().getIndex(anim);
		if (index == -1) {
			System.out.println("EPIC FAIL in animation component");
		} else {
			animationsIndexes.put(state, AnimationSingleton.getInstance().getIndex(anim));
		}
	}
	
	public Animation<Sprite> getAnimation(int state) {
		return AnimationSingleton.getInstance().getAnimation(animationsIndexes.get(state));
	}
	
	
	
	
	
	
	public static Serializer<AnimationComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<AnimationComponent>() {

			@Override
			public void write(Kryo kryo, Output output, AnimationComponent object) {
				kryo.writeClassAndObject(output, object.animationsIndexes);
			}

			@Override
			public AnimationComponent read(Kryo kryo, Input input, Class<AnimationComponent> type) {
				AnimationComponent compo = engine.createComponent(AnimationComponent.class);
				compo.animationsIndexes.putAll((IntMap<Integer>) kryo.readClassAndObject(input));
				return compo;
			}
		
		};
	}
	
}
