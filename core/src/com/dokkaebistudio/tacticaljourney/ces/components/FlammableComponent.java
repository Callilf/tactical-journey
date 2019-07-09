package com.dokkaebistudio.tacticaljourney.ces.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Marker to indicate that this entity is flammable.
 * @author Callil
 *
 */
public class FlammableComponent implements Component, Poolable {
	
	/** Whether the entity is currently burning. */
	private boolean burning;
	
	/** Whether this entity can ignite due to propagation. */
	private boolean propagate;
	
	/** Whether this entity is destroyed when ignited. */
	private boolean destroy;
	
	/** The texture to use to make the item disappear. */
	private RegionDescriptor destroyedTexture;


	@Override
	public void reset() {
		burning = false;
		propagate = false;	
		destroy = false;
	}
	
	
	//************************
	// Getters and Setters
	
	public boolean isPropagate() {
		return propagate;
	}

	public void setPropagate(boolean propagate) {
		this.propagate = propagate;
	}


	public boolean isDestroy() {
		return destroy;
	}


	public void setDestroy(boolean destroy) {
		this.destroy = destroy;
	}


	public boolean isBurning() {
		return burning;
	}


	public void setBurning(boolean burning) {
		this.burning = burning;
	}


	public Image getDestroyedTexture(Vector2 pos) {
		if (destroyedTexture != null) {
			final Image img = new Image(this.destroyedTexture.getRegion());
			img.setPosition( pos.x, pos.y);
			
			Action finishAction = new Action(){
				  @Override
				  public boolean act(float delta){
					  img.remove();
					  return true;
				  }
				};
			
			img.addAction(Actions.sequence(Actions.alpha(0, 1f), finishAction));
			
			return img;
		}
		
		return null;
	}


	public void setDestroyedTexture(RegionDescriptor destroyedTexture) {
		this.destroyedTexture = destroyedTexture;
	}
	
	
	
	public static Serializer<FlammableComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<FlammableComponent>() {

			@Override
			public void write(Kryo kryo, Output output, FlammableComponent object) {
				output.writeBoolean(object.burning);
				output.writeBoolean(object.propagate);
				output.writeBoolean(object.destroy);
				
				kryo.writeObjectOrNull(output, object.destroyedTexture, RegionDescriptor.class);
			}

			@Override
			public FlammableComponent read(Kryo kryo, Input input, Class<? extends FlammableComponent> type) {
				FlammableComponent compo = engine.createComponent(FlammableComponent.class);
				compo.burning = input.readBoolean();
				compo.propagate = input.readBoolean();
				compo.destroy = input.readBoolean();
				
				compo.destroyedTexture = kryo.readObjectOrNull(input, RegionDescriptor.class);
				return compo;
			}
		
		};
	}
}
