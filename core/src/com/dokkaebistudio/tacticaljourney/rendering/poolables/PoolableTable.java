/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.rendering.poolables;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;

/**
 * @author Callil
 *
 */
public class PoolableTable extends Table implements Poolable {

	public static PoolableTable create() {
		return Pools.obtain(PoolableTable.class);
	}
	
	
	@Override
	public void reset() {
		this.clear();
	}
}
