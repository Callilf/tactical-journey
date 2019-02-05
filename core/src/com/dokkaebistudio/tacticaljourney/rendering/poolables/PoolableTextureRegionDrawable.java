package com.dokkaebistudio.tacticaljourney.rendering.poolables;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Pool.Poolable;

public class PoolableTextureRegionDrawable extends TextureRegionDrawable implements Poolable {

	public PoolableTextureRegionDrawable() {
		super();
	}
	
	public static PoolableTextureRegionDrawable create(TextureRegion region) {
		PoolableTextureRegionDrawable drawable = Pools.obtain(PoolableTextureRegionDrawable.class);
		drawable.setRegion(region);
		return drawable;
	}
	
	@Override
	public void reset() {}
}
