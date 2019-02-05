package com.dokkaebistudio.tacticaljourney.rendering.poolables;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;

public class PoolableScrollPane extends ScrollPane implements Poolable {

	public PoolableScrollPane() {
		super(null);
	}
	
	public static PoolableScrollPane create(Actor widget) {
		PoolableScrollPane scrollPane = Pools.obtain(PoolableScrollPane.class);
		scrollPane.setWidget(widget);
		return scrollPane;
	}
	
	@Override
	public void reset() {
		this.clear();
	}
}
