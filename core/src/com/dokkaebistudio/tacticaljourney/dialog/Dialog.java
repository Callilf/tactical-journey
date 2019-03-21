package com.dokkaebistudio.tacticaljourney.dialog;

public class Dialog {

	/** The speaker. */
	private String speaker;
	
	/** The text. */
	private String text;
	
	/** The duration of display. */
	private float duration;
	
	/** Whether this dialog should be displayed even if another dialog is displayed. */
	private boolean forceDisplay = false;;
	
	
	public Dialog(String speaker, String text) {
		this.text = text;
		this.speaker = speaker;
		this.forceDisplay = false;
		
		if (text.length() > 0) {
			this.duration = text.length() / 20f;
			if (duration < 1f) duration = 1f;
		}
	}

	public Dialog(String speaker, String text, boolean forceDisplay) {
		this(speaker, text);
		this.forceDisplay = forceDisplay;
	}
	
	
	
	// Getters and setters


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

	public String getSpeaker() {
		return speaker;
	}

	public void setSpeaker(String speaker) {
		this.speaker = speaker;
	}
	
}
