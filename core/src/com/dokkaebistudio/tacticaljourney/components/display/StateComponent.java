/*******************************************************************************
 * Copyright 2014 See AUTHORS file.
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
import com.dokkaebistudio.tacticaljourney.enums.StatesEnum;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class StateComponent implements Component {
	private StatesEnum state = null;
	public float time = 0.0f;
	
	/** Prevents standing or moving animations to replace the current animation. */
	private boolean keepCurrentAnimation;
	
	public StatesEnum get() {
		return state;
	}
	
	public void set(StatesEnum state) {
		this.state = state != null ? state : StatesEnum.STANDING;
		this.time = 0.0f;
		this.keepCurrentAnimation = false;
	}
	
	public void set(StatesEnum state, boolean keepCurrentAnim) {
		this.state = state != null ? state : StatesEnum.STANDING;
		this.time = 0.0f;
		this.keepCurrentAnimation = keepCurrentAnim;
	}
	

	public boolean isKeepCurrentAnimation() {
		return keepCurrentAnimation;
	}

	public void setKeepCurrentAnimation(boolean keepCurrentAnimation) {
		this.keepCurrentAnimation = keepCurrentAnimation;
	}
	
	public static Serializer<StateComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<StateComponent>() {

			@Override
			public void write(Kryo kryo, Output output, StateComponent object) {
				output.writeString(object.state.name());
				output.writeBoolean(object.keepCurrentAnimation);
			}

			@Override
			public StateComponent read(Kryo kryo, Input input, Class<StateComponent> type) {
				StateComponent compo = engine.createComponent(StateComponent.class);
				compo.state = StatesEnum.valueOf(input.readString());
				compo.keepCurrentAnimation = input.readBoolean();
				return compo;
			}
		
		};
	}

}
