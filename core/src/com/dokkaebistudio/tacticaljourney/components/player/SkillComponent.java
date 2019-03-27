package com.dokkaebistudio.tacticaljourney.components.player;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.room.Floor;
import com.dokkaebistudio.tacticaljourney.skills.SkillEnum;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Represent a skill of the player.
 * @author Callil
 *
 */
public class SkillComponent implements Component, Poolable {
		
	/** The number of this skill. */
	private int skillNumber;
	
	/** The type of skill. */
	private SkillEnum type;
	
	
	/** The entity possessing this skill. */
	private Entity parentEntity;



	
	@Override
	public void reset() {
		skillNumber = 0;
		if (parentEntity != null) {
			parentEntity = null;		
		}
	}

	
	
	public Entity getParentEntity() {
		return parentEntity;
	}

	public void setParentEntity(Entity parentEntity) {
		this.parentEntity = parentEntity;
	}

	public int getSkillNumber() {
		return skillNumber;
	}

	public void setSkillNumber(int skillNumber) {
		this.skillNumber = skillNumber;
	}

	public SkillEnum getType() {
		return type;
	}

	public void setType(SkillEnum type) {
		this.type = type;
	}
	
	
	
	
	public static Serializer<SkillComponent> getSerializer(final PooledEngine engine, final Floor floor) {
		return new Serializer<SkillComponent>() {

			@Override
			public void write(Kryo kryo, Output output, SkillComponent object) {
				output.writeInt(object.skillNumber);
				output.writeString(object.type.name());
				kryo.writeClassAndObject(output, object.parentEntity);
			}

			@Override
			public SkillComponent read(Kryo kryo, Input input, Class<SkillComponent> type) {
				SkillComponent compo = engine.createComponent(SkillComponent.class);

				compo.skillNumber = input.readInt(); 
				compo.type = SkillEnum.valueOf(input.readString()); 
				compo.parentEntity = (Entity) kryo.readClassAndObject(input);
				
				return compo;
			}
		
		};
	}
}
