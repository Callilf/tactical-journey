package com.dokkaebistudio.tacticaljourney.descriptors;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.dokkaebistudio.tacticaljourney.assets.SceneAssets;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class FontDescriptor {
	
	private BitmapFont font;
	private String name;
	
	public FontDescriptor(String name, BitmapFont font) {
		this.name = name;
		this.font = font;
	}
	

	
	
	
	public BitmapFont getFont() {
		return font;
	}
	public void setFont(BitmapFont region) {
		this.font = region;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	
	
	public static Serializer<FontDescriptor> getSerializer(final PooledEngine engine) {
		return new Serializer<FontDescriptor>() {

			@Override
			public void write(Kryo kryo, Output output, FontDescriptor object) {
				output.writeString(object.name);
			}

			@Override
			public FontDescriptor read(Kryo kryo, Input input, Class<FontDescriptor> type) {
				String name = input.readString();
				return SceneAssets.findFont(name);
			}
		
		};
	}

}
