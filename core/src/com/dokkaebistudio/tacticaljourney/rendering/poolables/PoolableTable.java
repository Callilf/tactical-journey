/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.rendering.poolables;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * @author Callil
 *
 */
public class PoolableTable extends Table implements Poolable {

	@Override
	public void reset() {
		this.clear();
	}
}
