package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.room.Floor;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Marker to indicate that this entity can be destroyed in a blast.
 * @author Callil
 *
 */
public class DestructibleComponent implements Component, Poolable {
	
	/** Whether the entity is already destroyed. */
	private boolean destroyed;

	/**
	 * The sprite of the entity destroyed.
	 */
	private RegionDescriptor destroyedTexture;
	
	/** Whether this can be destroyed with a simple attack. */
	private boolean destroyableWithWeapon;
	
	/** Whether the destroyed entity must be removed. */
	private boolean remove = true;
	
	
	@Override
	public void reset() {
		this.setDestroyedTexture(null);
		this.setRemove(true);
		this.setDestroyed(false);
		this.setDestroyableWithWeapon(false);
	}


	

	// Getters and setters
	

	public RegionDescriptor getDestroyedTexture() {
		return destroyedTexture;
	}


	public void setDestroyedTexture(RegionDescriptor destroyedTexture) {
		this.destroyedTexture = destroyedTexture;
	}




	public boolean isRemove() {
		return remove;
	}




	public void setRemove(boolean remove) {
		this.remove = remove;
	}




	public boolean isDestroyed() {
		return destroyed;
	}




	public void setDestroyed(boolean destroyed) {
		this.destroyed = destroyed;
	}




	public boolean isDestroyableWithWeapon() {
		return destroyableWithWeapon;
	}




	public void setDestroyableWithWeapon(boolean destroyableWithWeapon) {
		this.destroyableWithWeapon = destroyableWithWeapon;
	}
	
	
	
	
	public static Serializer<DestructibleComponent> getSerializer(final PooledEngine engine, final Floor floor) {
		return new Serializer<DestructibleComponent>() {

			@Override
			public void write(Kryo kryo, Output output, DestructibleComponent object) {
				output.writeBoolean(object.destroyed);
				output.writeBoolean(object.destroyableWithWeapon);
				output.writeBoolean(object.remove);
				
				kryo.writeObjectOrNull(output, object.destroyedTexture, RegionDescriptor.class);
			}

			@Override
			public DestructibleComponent read(Kryo kryo, Input input, Class<DestructibleComponent> type) {
				DestructibleComponent compo = engine.createComponent(DestructibleComponent.class);
				compo.destroyed = input.readBoolean();
				compo.destroyableWithWeapon = input.readBoolean();
				compo.remove = input.readBoolean();
				
				compo.destroyedTexture = kryo.readObjectOrNull(input, RegionDescriptor.class);
				return compo;
			}
		
		};
	}
	
}
