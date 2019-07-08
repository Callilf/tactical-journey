package com.dokkaebistudio.tacticaljourney.dialog;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Entity;

/**
 * Represents one sentence that can be told to the player.
 * @author Callil
 *
 */
public class Dialog {

	/**
	 * The sentence to be told.
	 */
	private List<String> text = new ArrayList<>();
	private int currentIndex;
	
	/**
	 * The entity that is speaking.
	 */
	private Entity speaker;
	
	/**
	 * A condition for this sentence to be told.
	 * The entity in the predicate is the entity that is speaking (soulbender, shopkeeper, calishka...)
	 */
	private DialogCondition condition;
	
	/**
	 * Whether this sentence can be told once again. If so, once it's selected it will be
	 * stored again at the end of the list of sentences.
	 */
	private boolean repeat;
	
	
	public float duration;
	
	/**
	 * An effect that can be played one the dialog is over.
	 */
	private DialogEffect effect;
	
	
	public Dialog() {}
	
	
	public boolean hasNextLine() {
		return text.size() > 0 && currentIndex < text.size() - 1;
	}
	
	
	//************************
	// Getters and setters
	
	public List<String> getText() {
		return text;
	}
	
	public void setText(List<String> text) {
		this.text = text;
	}
	
	public void addText(String text) {
		this.text.add(text);
	}
	public DialogCondition getCondition() {
		return condition;
	}
	public void setCondition(DialogCondition condition) {
		this.condition = condition;
	}
	
	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}
	
	public boolean isRepeat() {
		return repeat;
	}
	
	public Entity getSpeaker() {
		return speaker;
	}
	
	public void setSpeaker(Entity speaker) {
		this.speaker = speaker;
	}


	public int getCurrentIndex() {
		return currentIndex;
	}


	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}
	
	public void incrementIndex() {
		this.currentIndex++;
	}


	public DialogEffect getEffect() {
		return effect;
	}


	public void setEffect(DialogEffect effect) {
		this.effect = effect;
	}
	
}
