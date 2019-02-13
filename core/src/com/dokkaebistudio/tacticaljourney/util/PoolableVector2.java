/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.util;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.NumberUtils;
import com.badlogic.gdx.utils.Pools;

/**
 * Poolable Vector2 to prevent creating new instances all the time.
 * @author Callil
 *
 */
public class PoolableVector2 extends Vector2 implements Poolable {

	public static PoolableVector2 create(int x, int y) {
		PoolableVector2 vector2 = Pools.obtain(PoolableVector2.class);
		vector2.x = (float) x;
		vector2.y = (float) y;
		return vector2;
	}
	
	public static PoolableVector2 create(float x, float y) {
		PoolableVector2 vector2 = Pools.obtain(PoolableVector2.class);
		vector2.x = x;
		vector2.y = y;
		return vector2;
	}
	
	public static PoolableVector2 create(Vector2 pos) {
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
	
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	@Override
	public boolean equals (Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof Vector2)) return false;
		Vector2 other = (Vector2)obj;
		if (NumberUtils.floatToIntBits(x) != NumberUtils.floatToIntBits(other.x)) return false;
		if (NumberUtils.floatToIntBits(y) != NumberUtils.floatToIntBits(other.y)) return false;
		return true;
	}
}
