package com.dokkaebistudio.tacticaljourney.rendering.poolables;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;
import com.dokkaebistudio.tacticaljourney.Assets;

public class PoolableLabel extends Label implements Poolable {

	private static LabelStyle defaultStyle = new LabelStyle(Assets.font.getFont(), Color.WHITE);
	 
	public PoolableLabel() {
		super((CharSequence)null, defaultStyle);
	}
	
	private PoolableLabel(CharSequence text, LabelStyle style) {
		super(text, style);
	}
	
	public static PoolableLabel create(CharSequence text, LabelStyle style) {
		PoolableLabel label = Pools.obtain(PoolableLabel.class);
		label.setText(text);
		label.setStyle(style);
		return label;
	}

	@Override
	public void reset() {
		this.clear();
	}
}
