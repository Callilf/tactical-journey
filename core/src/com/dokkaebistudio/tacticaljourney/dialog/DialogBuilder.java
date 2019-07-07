package com.dokkaebistudio.tacticaljourney.dialog;

import java.util.ArrayList;
import java.util.List;

public class DialogBuilder {
	
	private List<String> text = new ArrayList<>();
	private DialogCondition condition;
	private DialogEffect effect;
	private boolean repeat;
	private String speaker;
	
	
	public Dialog build() {
		Dialog d = new Dialog();
		d.setText(this.text);
		d.setCondition(this.condition);
		d.setRepeat(this.repeat);
		d.setSpeaker(this.speaker);
		d.setEffect(this.effect);
		return d;
	}
	
	
	public DialogBuilder addText(String text) {
		this.text.add(text);
		return this;
	}
	
	public DialogBuilder setCondition(DialogCondition condition) {
		this.condition = condition;
		return this;
	}
	
	public DialogBuilder setRepeat(boolean r) {
		this.repeat = r;
		return this;
	}
	
	public DialogBuilder setSpeaker(String speaker) {
		this.speaker = speaker;
		return this;
	}
	
	public DialogBuilder setEffect(DialogEffect effect) {
		this.effect = effect;
		return this;
	}
}