package com.dokkaebistudio.tacticaljourney.rendering.poolables;

import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Pool.Poolable;

public class PoolableStack extends Stack implements Poolable{

	public static PoolableStack create() {
		return Pools.obtain(PoolableStack.class);
	}
	
	@Override
	public void reset() {
		this.clear();
	}
}
