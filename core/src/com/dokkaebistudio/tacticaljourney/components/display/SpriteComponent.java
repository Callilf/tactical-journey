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
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

public class SpriteComponent implements Component, Poolable {
	public boolean hide;
	private Sprite sprite;
	
	
	@Override
	public void reset() {
		hide = false;
		sprite = null;
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
	
	
	

	public Sprite getSprite() {
		return sprite;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}


	
	
	
}
