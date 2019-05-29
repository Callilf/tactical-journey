package com.dokkaebistudio.tacticaljourney.components.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class PlayerComponent implements Component {
	
	/** The skill currently active. Null if no skill is active. */
	private Entity activeSkill;
	
	/** The melee skill. */
	private Entity skillMelee;

	/** The range skill. */
	private Entity skillRange;
	
	/** The bomb skill. */
	private Entity skillBomb;
	
	/** The throwing skill. */
	private Entity skillThrow;
	
	
	// Chance
	/** The karma stat. Affects random for drop rate, blessing procs... */
	private int karma;
	
	
	
	/** Whether the profile popin is opened or not. */
	private ProfilePopinDisplayModeEnum profilePopinDisplayMode = ProfilePopinDisplayModeEnum.NONE;
	
	public enum ProfilePopinDisplayModeEnum {
		NONE,
		PROFILE,
		LIFT_CURSE;
		
		public boolean isPopinDisplayed() {
			return this == PROFILE || this == ProfilePopinDisplayModeEnum.LIFT_CURSE;
		}
		public boolean closePopinOnClick() {
			return this == PROFILE;
		}
	}
	
	

	public void increaseKarma(int amount) {
		this.karma += amount;
	}
	
	//**********************
	// Action
	
	public enum PlayerActionEnum {
		ITEM_POPIN,
		TELEPORT_POPIN,
		LOOT,
		EXIT,
		WORMHOLE,
		PRAY,
		DRINK_CHALICE,
		RESTOCK_SHOP,
		INFUSE,
		GIVE_CATALYST_SOULBENDER,
		SEW
	}
	
	/** Whether the popin to ask for loot should open or not. */
	private List<PlayerActionEnum> requestedActions = new ArrayList<>();
	private List<Entity> actionEntities = new ArrayList<>();

	public void requestAction(PlayerActionEnum action, Entity actionEntity) {
		this.requestedActions.add(action);
		this.actionEntities.add(actionEntity);
	}

	public void clearRequestedAction() {
		this.requestedActions.clear();
		this.actionEntities.clear();
	}

	
	//************************
	// inspect
	
	private boolean inspectPopinRequested = false;
	private List<Entity> inspectedEntities = new ArrayList<>();
	
	public List<Entity> getInspectedEntities() {
		return inspectedEntities;
	}
	
	public void addInspectedEntities(Collection<Entity> entities) {
		inspectedEntities.clear();
		inspectedEntities.addAll(entities);
		setInspectPopinRequested(true);
	}
	
	public void clearInspectedEntities() {
		inspectedEntities.clear();
	}

	
	
	//**************************
	// Getter & Setters 

	public Entity getSkillMelee() {
		return skillMelee;
	}

	public void setSkillMelee(Entity skill1) {
		this.skillMelee = skill1;
	}

	
	public Entity getSkillRange() {
		return skillRange;
	}

	public void setSkillRange(Entity skill2) {
		this.skillRange = skill2;
	}

	
	public Entity getActiveSkill() {
		return activeSkill;
	}

	public void setActiveSkill(Entity activeSkill) {
		this.activeSkill = activeSkill;
	}

	public Entity getSkillBomb() {
		return skillBomb;
	}

	public void setSkillBomb(Entity skillBomb) {
		this.skillBomb = skillBomb;
	}

	public boolean isProfilePopinDisplayed() {
		return profilePopinDisplayMode.isPopinDisplayed();
	}
	
	public ProfilePopinDisplayModeEnum getProfilePopinDisplayMode() {
		return profilePopinDisplayMode;
	}

	public void setProfilePopinDisplayed(ProfilePopinDisplayModeEnum displayMode) {
		this.profilePopinDisplayMode = displayMode;
	}


	public Entity getSkillThrow() {
		return skillThrow;
	}

	public void setSkillThrow(Entity skillThrow) {
		this.skillThrow = skillThrow;
	}

	public List<PlayerActionEnum> getRequestedActions() {
		return requestedActions;
	}
	
	public List<Entity> getActionEntities() {
		return actionEntities;
	}


	public boolean isInspectPopinRequested() {
		return inspectPopinRequested;
	}

	public void setInspectPopinRequested(boolean inspectPopinRequested) {
		this.inspectPopinRequested = inspectPopinRequested;
	}

	
	public int getKarma() {
		return karma;
	}

	public void setKarma(int karma) {
		this.karma = karma;
	}
	
	public static Serializer<PlayerComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<PlayerComponent>() {

			@Override
			public void write(Kryo kryo, Output output, PlayerComponent object) {
				kryo.writeClassAndObject(output, object.skillMelee);
				kryo.writeClassAndObject(output, object.skillRange);
				kryo.writeClassAndObject(output, object.skillBomb);
				kryo.writeClassAndObject(output, object.skillThrow);
				output.writeInt(object.karma);
			}

			@Override
			public PlayerComponent read(Kryo kryo, Input input, Class<? extends PlayerComponent> type) {
				PlayerComponent compo = engine.createComponent(PlayerComponent.class);
				compo.skillMelee = (Entity) kryo.readClassAndObject(input);
				compo.skillRange = (Entity) kryo.readClassAndObject(input);
				compo.skillBomb = (Entity) kryo.readClassAndObject(input);
				compo.skillThrow = (Entity) kryo.readClassAndObject(input);
				compo.karma = input.readInt();
				return compo;
			}
		
		};
	}

}
