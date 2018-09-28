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

package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * Component for entities that want to display text on screen.
 * @author Callil
 */
public class TextComponent implements Component, Poolable {
	
	/** The font to use. */
	private BitmapFont font;
	
	/** the text to display. */
	private String text;
	
	private float width;
	private float height;
	
	public TextComponent(BitmapFont f) {
		font = f;
	}
	

	@Override
	public void reset() {
		font = null;
		setText(null);
	}
	
	

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		
		//Compute width and height
		if (text != null) {
			GlyphLayout layout = new GlyphLayout();
			layout.setText(font, text);
			this.width = layout.width;
			this.height = layout.height;
		} else {
			this.width = 0;
			this.height = 0;
		}
	}
	
	
	public void setFont(BitmapFont font) {
		this.font = font;
	}
	
	public BitmapFont getFont() {
		return font;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}
	
	
		
}
