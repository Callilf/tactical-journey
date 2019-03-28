package com.dokkaebistudio.tacticaljourney.components.transition;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Represents a transition from a floor to another.
 * @author Callil
 *
 */
public class ExitComponent implements Component {

	/** Whether the door is opened or closed. */
	private boolean opened;
	
	/** The room on the other side of this door. */
	private int targetedFloorIndex;

	
	/**
	 * Open the exit door.
	 * @param exit the exit entity
	 */
	public void open(Entity exit) {
		if (!opened) {
			SpriteComponent spriteComponent = Mappers.spriteComponent.get(exit);
			spriteComponent.getSprite().setRegion(Assets.exit_opened.getRegion());
			
			opened = true;
		}
	}
	
	
	// Getters and Setters
	
	public boolean isOpened() {
		return opened;
	}

	public void setOpened(boolean opened) {
		this.opened = opened;
	}

	public int getTargetedFloorIndex() {
		return targetedFloorIndex;
	}

	public void setTargetedFloorIndex(int targetedFloorIndex) {
		this.targetedFloorIndex = targetedFloorIndex;
	}


	
	
	public static Serializer<ExitComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<ExitComponent>() {

			@Override
			public void write(Kryo kryo, Output output, ExitComponent object) {
				output.writeBoolean(object.opened);
				output.writeInt(object.targetedFloorIndex);
			}

			@Override
			public ExitComponent read(Kryo kryo, Input input, Class<ExitComponent> type) {
				ExitComponent compo = engine.createComponent(ExitComponent.class);
				compo.opened = input.readBoolean();
				compo.targetedFloorIndex = input.readInt();
				return compo;
			}
		
		};
	}
}
