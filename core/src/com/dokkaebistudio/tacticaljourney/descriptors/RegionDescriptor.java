package com.dokkaebistudio.tacticaljourney.descriptors;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.components.DestructibleComponent;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class RegionDescriptor {
	
	private AtlasRegion region;
	private String name;
	
	public RegionDescriptor(String name, AtlasRegion region) {
		this.name = name;
		this.region = region;
	}
	
	
	
	public int getRegionWidth() {
		return this.region.getRegionWidth();
	}
	
	public int getRegionHeight() {
		return this.region.getRegionHeight();
	}
	
	public String getNameFull() {
		return name + "-full";
	}
	
	
	
	public AtlasRegion getRegion() {
		return region;
	}
	public void setRegion(AtlasRegion region) {
		this.region = region;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	
	
	public static Serializer<RegionDescriptor> getSerializer(final PooledEngine engine) {
		return new Serializer<RegionDescriptor>() {

			@Override
			public void write(Kryo kryo, Output output, RegionDescriptor object) {
				output.writeString(object.name);
			}

			@Override
			public RegionDescriptor read(Kryo kryo, Input input, Class<? extends RegionDescriptor> type) {
				String name = input.readString();
				AtlasRegion sprite = Assets.findSprite(name).getRegion();
				RegionDescriptor rd = new RegionDescriptor(name, sprite);
				return rd;
			}
		
		};
	}

}
