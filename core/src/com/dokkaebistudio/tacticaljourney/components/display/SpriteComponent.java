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
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class SpriteComponent implements Component, Poolable {
	public boolean hide;
	public boolean flipX;
	
	private String regionName;
	private Sprite sprite = new Sprite();
	
	
	@Override
	public void reset() {
		hide = false;
		flipX = false;
		sprite = new Sprite();
	}
	
	
	/**
	 * Return true if the point is inside the sprite.
	 * @param point the point to test
	 * @return true if the point is inside the sprite
	 */
	public boolean containsPoint(Vector2 point) {
		return sprite != null && sprite.getBoundingRectangle().contains(point);
	}
	
	/**
	 * Return true if the point at (x,y) is inside the sprite.
	 * @param x the absciss
	 * @param y the ordinate
	 * @return true if the point is inside the sprite
	 */
	public boolean containsPoint(float x, float y) {
		return sprite != null && sprite.getBoundingRectangle().contains(x, y);
	}
	
	
	public void setSprite(RegionDescriptor texture) {
		this.sprite = new Sprite(texture.getRegion());
		this.regionName = texture.getName();
	}
	
	public void updateSprite(RegionDescriptor texture) {
		this.sprite.setRegion(texture.getRegion());
		this.regionName = texture.getName();
	}
	
	
	

	public Sprite getSprite() {
		return sprite;
	}

	private void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}


	public String getTextureRegion() {
		return regionName;
	}


	public void setTextureRegion(String textureRegion) {
		this.regionName = textureRegion;
	}


	
	
	public static Serializer<SpriteComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<SpriteComponent>() {

			@Override
			public void write(Kryo kryo, Output output, SpriteComponent object) {
				output.writeBoolean(object.hide);
				output.writeBoolean(object.flipX);
				output.writeString(object.regionName != null ? object.regionName : "");
			}

			@Override
			public SpriteComponent read(Kryo kryo, Input input, Class<SpriteComponent> type) {
				SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
				spriteCompo.hide = input.readBoolean();
				spriteCompo.flipX = input.readBoolean();
				
				String regionName = input.readString();
				if (regionName != "") {
					spriteCompo.setSprite(Assets.findSprite(regionName));
				}
				
				return spriteCompo;
			}
		
		};
	}
	
	
}
