package com.dokkaebistudio.tacticaljourney.components;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.components.interfaces.MarkerInterface;
import com.dokkaebistudio.tacticaljourney.components.interfaces.MovableInterface;
import com.dokkaebistudio.tacticaljourney.enums.DamageType;
import com.dokkaebistudio.tacticaljourney.enums.HealthChangeEnum;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.rendering.service.PopinService;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Marker to indicate that this entity has health and therefore can be attacked or damaged.
 * @author Callil
 *
 */
public class HealthComponent implements Component, Poolable, MovableInterface, MarkerInterface, RoomSystem {
		
	/** The room.*/
	public Room room;
	
	
	//************
	// HP
	
	/** The max number of h. */
	private int maxHp;
	
	/** The current number of hp. */
	private int hp;
		
	/** The displayer that shows the amount of HP beside the entity (for enemies). */
	private Label hpDisplayer = new Label("", PopinService.hudStyle());

	
	//*************
	// Armor
	
	/** The max amount of armor. */
	private int maxArmor;
	
	/** The current number of armor. */
	private int armor;
	
	
	//***********
	// Resistance
	
	private Map<DamageType, Integer> resitanceMap = new HashMap<>();
	
	
	/** Keep track of the latest attack's damage. */
	private int latestAttackDamage;

	
	
	/** Whether the entity received damages during the previous turn. */
	private boolean receivedDamageLastTurn;
	
	/** The last entity that attacked this entity. */
	private Entity attacker;
	
	/** Whether the entity has been hit or healed at this frame. */
	public Map<HealthChangeEnum, String> healthChangeMap = new HashMap<>();
	
	
	@Override
	public void showMarker(Entity ally) {
		if (hpDisplayer != null) {
			GameScreen.fxStage.addActor(hpDisplayer);
			this.place(Mappers.gridPositionComponent.get(ally).coord());
		}
	}
	
	@Override
	public void hideMarker() {
		hpDisplayer.remove();
	}
	
	
	//****************
	// Hit methods
	
	/**
	 * Receive damages.
	 * @param amountOfDamage the amount of damages
	 */
	public void hit(int amountOfDamage, Entity target, Entity attacker) {
		this.hit(amountOfDamage, target, attacker, DamageType.NORMAL);
	}
	
	/**
	 * Receive damages.
	 * @param amountOfDamage the amount of damages
	 */
	public void hit(int amountOfDamage, Entity target, Entity attacker, DamageType damageType) {
		int realAmountOfDamage = checkResistance(damageType, amountOfDamage);
		this.latestAttackDamage = realAmountOfDamage;
		
		int damageToHealth = Math.max(realAmountOfDamage - this.armor, 0);
		if (this.armor > 0) {
			this.setArmor(Math.max(this.armor - realAmountOfDamage, 0));
		}
		
		this.setHp(Math.max(0, this.getHp() - damageToHealth));

		
		if (attacker != null) {
			this.attacker = attacker;
			this.healthChangeMap.put(HealthChangeEnum.HIT_INTERRUPT, String.valueOf(realAmountOfDamage));
		} else {
			this.healthChangeMap.put(HealthChangeEnum.HIT, String.valueOf(realAmountOfDamage));
		}
		
		addJournalEntry(target, attacker, realAmountOfDamage, damageType);
	}

	private void addJournalEntry(Entity target, Entity attacker, int realAmountOfDamage, DamageType damageType) {
		String damageTypeStr = " " + damageType.title() + "[GRAY]";
		if (damageType == DamageType.NORMAL) damageTypeStr = "";
		
		String targetLabel = Journal.getLabel(target);

		if (attacker != null) {
			String attackerLabel = Journal.getLabel(attacker);
			Journal.addEntry("[GRAY]" + attackerLabel + " attacked " + targetLabel + " for " + realAmountOfDamage + damageTypeStr + " damages");
		} else {
			Journal.addEntry("[GRAY]" + targetLabel + "  took " + realAmountOfDamage + damageTypeStr + " damages");
		}
	}

	
	
	/**
	 * Receive damages that bypasses armor.
	 * @param amountOfDamage the amount of damages
	 */
	public void hitThroughArmor(int amountOfDamage, Entity target, Entity attacker) {
		this.hitThroughArmor(amountOfDamage, target, attacker, DamageType.NORMAL);
	}
	
	/**
	 * Receive damages that bypasses armor.
	 * @param amountOfDamage the amount of damages
	 */
	public void hitThroughArmor(int amountOfDamage, Entity target, Entity attacker, DamageType damageType) {
		int realAmountOfDamage = checkResistance(damageType, amountOfDamage);
		this.latestAttackDamage = realAmountOfDamage;

		this.setHp(Math.max(0, this.getHp() - realAmountOfDamage));

		if (attacker != null) {
			this.attacker = attacker;
			this.healthChangeMap.put(HealthChangeEnum.HIT_INTERRUPT, String.valueOf(realAmountOfDamage));
		} else {
			this.healthChangeMap.put(HealthChangeEnum.HIT, String.valueOf(realAmountOfDamage));
		}
		
		addJournalEntry(target, attacker, realAmountOfDamage, damageType);
	}
	
	
	//**************************
	// Restore methods
	
	/**
	 * Restore the given amount of health.
	 * @param amount the amount to restore
	 */
	public void restoreHealth(int amount) {
		this.setHp(this.getHp() + amount);
		this.healthChangeMap.put(HealthChangeEnum.HEALED, String.valueOf(amount));
	}
	
	/**
	 * Restore the given amount of armor.
	 * @param amount the amount to restore
	 */
	public void restoreArmor(int amount) {
		this.setArmor(this.getArmor() + amount);
		this.healthChangeMap.put(HealthChangeEnum.ARMOR, String.valueOf(amount));
	}
	
	public void clearModified() {
		this.healthChangeMap.clear();
		
		if (this.getArmor() > this.getMaxArmor()) {
			this.setArmor(this.getMaxArmor());
		}

		if (this.getHp() > this.getMaxHp()) {
			this.setHp(this.getMaxHp());
		}
	}
	
	/**
	 * Increase the max hp by the given amount.
	 * @param amount the amount to add.
	 */
	public void increaseMaxHealth(int amount) {
		this.setMaxHp(this.maxHp + amount);
		this.setHp(this.hp + amount);
	}
	
	/**
	 * Increase the max armor by the given amount.
	 * @param amount the amount to add.
	 */
	public void increaseMaxArmor(int amount) {
		this.setMaxArmor(this.maxArmor + amount);
	}
	
	/**
	 * Whether this entity is dead.
	 * @return true if the entity is dead.
	 */
	public boolean isDead() {
		return hp <= 0;
	}
	
	/**
	 * Return the color in which the health must be displayed on the HUD.
	 * The color depends on the remaining life
	 * @return the color
	 */
	public String getHpColor() {
		if (hp >= (maxHp * 0.66f)) {
			return "[GREEN]";
		} else if (hp >= (maxHp * 0.33f) && hp < (maxHp * 0.66f)) {
			return "[ORANGE]";
		} else {
			return "[RED]";
		}
	}
	
	/**
	 * Return the color in which the armor must be displayed on the HUD.
	 * The color depends on the amount or armor
	 * @return the color
	 */
	public String getArmorColor() {
		if (armor > 0) {
			return "[CYAN]";
		} else {
			return "[WHITE]";
		}
	}
	
	
	
	//****************************
	// Resistance related methods
	
	public void addResistance(DamageType dt, int percentage) {
		Integer currentPercentage = this.resitanceMap.get(dt);
		if (currentPercentage != null) {
			this.resitanceMap.put(dt, currentPercentage.intValue() + percentage);
		} else {
			this.resitanceMap.put(dt, percentage);
		}
	}
	
	public void reduceResistance(DamageType dt, int percentage) {
		Integer currentPercentage = this.resitanceMap.get(dt);
		if (currentPercentage != null) {
			this.resitanceMap.put(dt, currentPercentage.intValue() - percentage);
		}
	}
	
	public void removeResistance(DamageType dt) {
		this.resitanceMap.remove(dt);
	}
	
	public int getResistance(DamageType dt) {
		if (this.resitanceMap.containsKey(dt)) {
			return this.resitanceMap.get(dt);
		} else {
			return 0;
		}
	}
	
	/**
	 * Compute the new amount of damage given an amount and a type of damage.
	 * @param damageType the type of damage
	 * @param amountOfDamage the input amount of damage prior to resistance damage reduction
	 * @return the damage reduced given the resistance.
	 */
	private int checkResistance(DamageType damageType, int amountOfDamage) {
		float realAmountOfDamage = amountOfDamage;
		Integer resistancePercentageFromMap = this.resitanceMap.get(damageType);
		if (resistancePercentageFromMap != null && resistancePercentageFromMap.intValue() > 0) {
			int resistancePercentage = resistancePercentageFromMap.intValue() > 100 ? 100 : resistancePercentageFromMap.intValue();

			realAmountOfDamage = (realAmountOfDamage * (100 - resistancePercentage)) / 100;
			
			this.healthChangeMap.put(HealthChangeEnum.RESISTANT, damageType.name() + " RESISTANT");
		}
		return (int) Math.ceil(realAmountOfDamage);
	}
	
	
	//********************
	// Overridden methods
	
	@Override
	public void enterRoom(Room newRoom) {
		this.room = newRoom;
	}
	
	@Override
	public void reset() {
		resitanceMap.clear();
		if (hpDisplayer != null) {
			hpDisplayer.setText("");
			hpDisplayer.remove();		
		} else {
			hpDisplayer = new Label("", PopinService.hudStyle());
		}
		this.clearModified();
		this.receivedDamageLastTurn = false;
		this.latestAttackDamage = 0;
		this.maxArmor = 0;
		this.armor = 0;
	}
	
	
	
	//**************************************
	// Movement

	@Override
	public void initiateMovement(Vector2 currentPos) {
		if (hpDisplayer != null) {
			hpDisplayer.layout();
			Vector2 startPos = TileUtil.convertGridPosIntoPixelPos(currentPos);
			startPos.y = startPos.y + hpDisplayer.getGlyphLayout().height - 10;
			hpDisplayer.setPosition(startPos.x, startPos.y);
		}
	}



	@Override
	public void performMovement(float xOffset, float yOffset) {
		if (hpDisplayer != null) {
			hpDisplayer.layout();
			hpDisplayer.setPosition(hpDisplayer.getX() + xOffset, hpDisplayer.getY() + yOffset);
		}
	}



	@Override
	public void endMovement(Vector2 finalPos) {
		if (hpDisplayer != null) {
			hpDisplayer.layout();
			Vector2 startPos = TileUtil.convertGridPosIntoPixelPos(finalPos);
			startPos.y = startPos.y + hpDisplayer.getGlyphLayout().height - 10;
			hpDisplayer.setPosition(startPos.x, startPos.y);
		}
	}

	@Override
	public void place(Vector2 tilePos) {
		if (hpDisplayer != null) {
			hpDisplayer.layout();
			Vector2 startPos = TileUtil.convertGridPosIntoPixelPos(tilePos);
			startPos.y = startPos.y + hpDisplayer.getGlyphLayout().height - 10;
			hpDisplayer.setPosition(startPos.x, startPos.y);
		}
	}

	
	
	//*************************
	// Getters and Setters
	
	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
		if (hpDisplayer != null) {
			hpDisplayer.setText(String.valueOf(this.hp));
			
			if (this.armor > 0) {
				hpDisplayer.setText(hpDisplayer.getText() + " + [CYAN]" + this.armor);
			}
		}
	}
	

	public int getArmor() {
		return armor;
	}


	public void setArmor(int armor) {
		this.armor = armor;
		if (hpDisplayer != null) {
			hpDisplayer.setText(String.valueOf(this.hp));
			
			if (this.armor > 0) {
				hpDisplayer.setText(hpDisplayer.getText() + " + [CYAN]" + this.armor);
			}
		}
	}

	public Label getHpDisplayer() {
		return hpDisplayer;
	}
	
	public void removeHpDisplayer() {
		hpDisplayer = null;
	}

	public int getMaxHp() {
		return maxHp;
	}

	public void setMaxHp(int maxHp) {
		this.maxHp = maxHp;
	}


	public Map<HealthChangeEnum, String> getHealthChangeMap() {
		return healthChangeMap;
	}

	public void setHealthChangeMap(Map<HealthChangeEnum, String> healthChangeMap) {
		this.healthChangeMap = healthChangeMap;
	}

	public boolean isReceivedDamageLastTurn() {
		return receivedDamageLastTurn;
	}


	public void setReceivedDamageLastTurn(boolean receivedDamageLastTurn) {
		this.receivedDamageLastTurn = receivedDamageLastTurn;
	}


	public Entity getAttacker() {
		return attacker;
	}


	public void setAttacker(Entity attacker) {
		this.attacker = attacker;
	}


	public int getMaxArmor() {
		return maxArmor;
	}


	public void setMaxArmor(int maxArmor) {
		this.maxArmor = maxArmor;
	}

	public int getLatestAttackDamage() {
		return latestAttackDamage;
	}

    

	
	
	public static Serializer<HealthComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<HealthComponent>() {

			@Override
			public void write(Kryo kryo, Output output, HealthComponent object) {
				
				output.writeInt(object.maxHp);
				output.writeInt(object.hp);
				
				output.writeBoolean(object.hpDisplayer == null);

				// Armor
				output.writeInt(object.maxArmor);
				output.writeInt(object.armor);

				// Resistance
				kryo.writeClassAndObject(output, object.resitanceMap);
			}

			@Override
			public HealthComponent read(Kryo kryo, Input input, Class<? extends HealthComponent> type) {
				HealthComponent compo = engine.createComponent(HealthComponent.class);
				
				compo.setMaxHp(input.readInt());
				compo.setHp(input.readInt());
				
				boolean hpDisplayerHidden = input.readBoolean();
				if (hpDisplayerHidden) compo.removeHpDisplayer();

				compo.setMaxArmor(input.readInt());
				compo.setArmor(input.readInt());

				compo.resitanceMap = (Map<DamageType, Integer>) kryo.readClassAndObject(input);

				return compo;
			}
		
		};
	}

	
}
