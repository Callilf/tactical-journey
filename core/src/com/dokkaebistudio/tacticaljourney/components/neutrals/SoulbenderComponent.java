package com.dokkaebistudio.tacticaljourney.components.neutrals;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * Marker to indicate that this entity is a soul bender.
 * @author Callil
 *
 */
public class SoulbenderComponent implements Component, Poolable {
	
	private boolean hasInfused = false;
	
	private int price = 0;
	
	/** The different sentences the soul bender can say when being talked to. */
	private List<String> mainSpeeches = new ArrayList<>();
	private int currentSpeech;
	
	@Override
	public void reset() {
		this.currentSpeech = 0;
		this.mainSpeeches.clear();
		this.hasInfused = false;
	}


	
	//**************************
	// Speech related methods
	
	public void addSpeech(String s) {
		mainSpeeches.add(s);
	}
	
	public String getSpeech() {
		String speech = mainSpeeches.get(currentSpeech);
		currentSpeech ++;
		if (currentSpeech >= mainSpeeches.size()) currentSpeech = 0;
		return speech;
	}
	
	
	

	//*********************************
	// Getters and Setters


	public boolean hasInfused() {
		return hasInfused;
	}



	public void setHasInfused(boolean hasInfused) {
		this.hasInfused = hasInfused;
	}



	public int getPrice() {
		return price;
	}



	public void setPrice(int price) {
		this.price = price;
	}
	
	


	
}
