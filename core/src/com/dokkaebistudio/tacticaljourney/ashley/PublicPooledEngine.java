/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.ashley;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.ReflectionPool;

/**
 * @author Callil
 *
 */
public class PublicPooledEngine extends PooledEngine {
	
	public long entityCounter = 0;
	private EntityPool entityPool;
	private ComponentPools componentPools;
			
	/**
	 * Creates a new PooledEngine with a maximum of 100 entities and 100 components of each type. Use
	 * {@link #PooledEngine(int, int, int, int)} to configure the entity and component pools.
	 */
	public PublicPooledEngine () {
		this(10, 100, 10, 100);
	}

	/**
	 * Creates new PooledEngine with the specified pools size configurations.
	 * @param entityPoolInitialSize initial number of pre-allocated entities.
	 * @param entityPoolMaxSize maximum number of pooled entities.
	 * @param componentPoolInitialSize initial size for each component type pool.
	 * @param componentPoolMaxSize maximum size for each component type pool.
	 */
	public PublicPooledEngine (int entityPoolInitialSize, int entityPoolMaxSize, int componentPoolInitialSize, int componentPoolMaxSize) {
		super();

		entityPool = new EntityPool(entityPoolInitialSize, entityPoolMaxSize);
		componentPools = new ComponentPools(componentPoolInitialSize, componentPoolMaxSize);
	}

	/** @return Clean {@link Entity} from the Engine pool. In order to add it to the {@link Engine}, use {@link #addEntity(Entity)}. */
	public Entity createEntity () {
		PooledEntity entity = entityPool.obtain();
		entity.id = entityCounter;
		entityCounter++;
		return entity;
	}

	/**
	 * Retrieves a new {@link Component} from the {@link Engine} pool. It will be placed back in the pool whenever it's removed
	 * from an {@link Entity} or the {@link Entity} itself it's removed.
	 */
	public <T extends Component> T createComponent (Class<T> componentType) {
		return componentPools.obtain(componentType);
	}

	/**
	 * Removes all free entities and components from their pools. Although this will likely result in garbage collection, it will
	 * free up memory.
	 */
	public void clearPools () {
		entityPool.clear();
		componentPools.clear();
	}

	@Override
	protected void removeEntityInternal (Entity entity) {
		super.removeEntityInternal(entity);

		if (entity instanceof PooledEntity) {
			entityPool.free((PooledEntity)entity);
		}
	}
			
	public class PooledEntity extends PublicEntity implements Poolable {
		@Override
		public Component remove (Class<? extends Component> componentClass) {
			Component component = super.remove(componentClass);

			if (component != null) {
				componentPools.free(component);
			}

			return component;
		}

		@Override
		public void reset () {
			removeAll();
			flags = 0;
			componentAdded.removeAllListeners();
			componentRemoved.removeAllListeners();
			scheduledForRemoval = false;
		}
	}
	
	private class EntityPool extends Pool<PooledEntity> {

		public EntityPool (int initialSize, int maxSize) {
			super(initialSize, maxSize);
		}

		@Override
		protected PooledEntity newObject () {
			return new PooledEntity();
		}
	}

	private class ComponentPools {
		private ObjectMap<Class<?>, ReflectionPool> pools;
		private int initialSize;
		private int maxSize;

		public ComponentPools (int initialSize, int maxSize) {
			this.pools = new ObjectMap<Class<?>, ReflectionPool>();
			this.initialSize = initialSize;
			this.maxSize = maxSize;
		}

		public <T> T obtain (Class<T> type) {
			ReflectionPool pool = pools.get(type);

			if (pool == null) {
				pool = new ReflectionPool(type, initialSize, maxSize);
				pools.put(type, pool);
			}

			return (T)pool.obtain();
		}

		public void free (Object object) {
			if (object == null) {
				throw new IllegalArgumentException("object cannot be null.");
			}

			ReflectionPool pool = pools.get(object.getClass());

			if (pool == null) {
				return; // Ignore freeing an object that was never retained.
			}

			pool.free(object);
		}

		public void freeAll (Array objects) {
			if (objects == null) throw new IllegalArgumentException("objects cannot be null.");

			for (int i = 0, n = objects.size; i < n; i++) {
				Object object = objects.get(i);
				if (object == null) continue;
				free(object);
			}
		}

		public void clear () {
			for (Pool pool : pools.values()) {
				pool.clear();
			}
		}
	}
}
