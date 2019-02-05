package com.dokkaebistudio.tacticaljourney.rendering.poolables;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Scaling;

public class PoolableImage extends Image implements Poolable {

	public PoolableImage() {}
	
	public static PoolableImage create(TextureRegion region) {
		PoolableImage image = Pools.obtain(PoolableImage.class);
		image.setDrawable(PoolableTextureRegionDrawable.create(region));
		image.setScaling(Scaling.stretch);
		image.setAlign(Align.center);;
		image.setSize(image.getPrefWidth(), image.getPrefHeight());
		return image;
	}
	
	@Override
	public void reset() {
		this.clear();
	}
}
