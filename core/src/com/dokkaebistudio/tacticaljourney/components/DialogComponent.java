package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * Marker to indicate that this entity is a dialog popin
 * @author Callil
 *
 */
public class DialogComponent implements Component, Poolable {

	private Vector2 pos;
	private String text;
	private float currentDuration;
	private float duration;
	
	private Table table;
	
	@Override
	public void reset() {
		currentDuration = 0f;
		duration = 0f;
		table = null;
	}
	
	
	//***************************
	// Getters and Setters
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public float getDuration() {
		return duration;
	}
	public void setDuration(float duration) {
		this.duration = duration;
	}
	public Vector2 getPos() {
		return pos;
	}
	public void setPos(Vector2 pos) {
		this.pos = pos;
	}
	public float getCurrentDuration() {
		return currentDuration;
	}
	public void setCurrentDuration(float currentDuration) {
		this.currentDuration = currentDuration;
	}
	public Table getTable() {
		return table;
	}
	public void setTable(Table table) {
		this.table = table;
	}
	
}
