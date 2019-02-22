package com.dokkaebistudio.tacticaljourney.dialog;

import com.badlogic.gdx.math.Vector2;

public class Dialog {

	/** The position to display it. */
	private Vector2 pos;
	
	/** The text. */
	private String text;
	
	/** The duration of display. */
	private float duration;
	
	/** Whether this dialog should be displayed even if another dialog is displayed. */
	private boolean forceDisplay = false;;
	
	
	public Dialog(String text, Vector2 pos) {
		this.text = text;
		this.pos = pos;
		this.forceDisplay = false;
		
		if (text.length() > 0) {
			this.duration = text.length() / 20f;
			if (duration < 1f) duration = 1f;
		}
	}

	public Dialog(String text, Vector2 pos, boolean forceDisplay) {
		this(text, pos);
		this.forceDisplay = forceDisplay;
	}
	
	
	
	// Getters and setters

	public Vector2 getPos() {
		return pos;
	}

	public void setPos(Vector2 pos) {
		this.pos = pos;
	}

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

	public boolean isForceDisplay() {
		return forceDisplay;
	}

	public void setForceDisplay(boolean forceDisplay) {
		this.forceDisplay = forceDisplay;
	}
	
}
