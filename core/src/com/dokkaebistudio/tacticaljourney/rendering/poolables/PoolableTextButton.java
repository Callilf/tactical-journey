package com.dokkaebistudio.tacticaljourney.rendering.poolables;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.badlogic.gdx.utils.Pools;

public class PoolableTextButton extends TextButton implements Poolable {

	private static TextButtonStyle defaultStyle = new TextButtonStyle(null, null, null, Assets.font);

	public PoolableTextButton() {
		super((String)null, defaultStyle);
	}

	private PoolableTextButton(String text, TextButtonStyle style) {
		super(text, style);
	}


	public static PoolableTextButton create(String text, TextButtonStyle style) {
		PoolableTextButton txtBtn = Pools.obtain(PoolableTextButton.class);
		txtBtn.setText(text);
		txtBtn.setStyle(style);
		return txtBtn;
	}
	
	
	@Override
	public void reset() {
		this.clear();
	}
}
