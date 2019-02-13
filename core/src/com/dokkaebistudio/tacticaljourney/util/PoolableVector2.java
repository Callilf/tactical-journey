/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.util;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;

/**
 * Poolable Vector2 to prevent creating new instances all the time.
 * @author Callil
 *
 */
public class PoolableVector2 extends Vector2 implements Poolable {

	public static Vector2 create(int x, int y) {
		PoolableVector2 vector2 = Pools.obtain(PoolableVector2.class);
		vector2.x = (float) x;
		vector2.y = (float) y;
		return vector2;
	}
	
	public static Vector2 create(float x, float y) {
		PoolableVector2 vector2 = Pools.obtain(PoolableVector2.class);
		vector2.x = x;
		vector2.y = y;
		return vector2;
	}
	
	public static Vector2 create(Vector2 pos) {
		PoolableVector2 vector2 = Pools.obtain(PoolableVector2.class);
		vector2.x = pos.x;
		vector2.y = pos.y;
		return vector2;
	}
	
	@Override
	public void reset() {
		x = -100;
		y = -100;
	}
	
}
