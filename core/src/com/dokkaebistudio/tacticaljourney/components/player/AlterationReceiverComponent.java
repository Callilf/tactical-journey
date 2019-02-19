package com.dokkaebistudio.tacticaljourney.components.player;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.alterations.Curse;

/**
 * Marker to indicate that this entity can receive blessings and curses.
 * @author Callil
 *
 */
public class AlterationReceiverComponent implements Component, Poolable {
	
	private List<Blessing> blessings = new ArrayList<>();
	private List<Curse> curses = new ArrayList<>();
	
	@Override
	public void reset() {
		blessings.clear();
		curses.clear();
	}

	
	public void addBlessing(Entity entity, Blessing blessing) {
		blessing.onReceive(entity);
		blessings.add(blessing);
	}
	
	public void addCurse(Entity entity, Curse curse) {
		curse.onReceive(entity);
		curses.add(curse);
	}
	
	public void removeBlessing(Entity entity, Blessing blessing) {
		blessing.onRemove(entity);
		blessings.remove(blessing);
	}
	
	public void removeCurse(Entity entity, Curse curse) {
		curse.onRemove(entity);
		curses.remove(curse);
	}
	
	public void removeCurseByClass(Entity entity, Class curseClass) {
		for (Curse curse : curses) {
			if (curse.getClass() == curseClass) {
				removeCurse(entity, curse);
				break;
			}
		}
	}
	
	
	//***********************
	// Getters and Setters
	
	
	public List<Blessing> getBlessings() {
		return blessings;
	}

	public void setBlessings(List<Blessing> blessings) {
		this.blessings = blessings;
	}

	public List<Curse> getCurses() {
		return curses;
	}

	public void setCurses(List<Curse> curses) {
		this.curses = curses;
	}

	
	
}
