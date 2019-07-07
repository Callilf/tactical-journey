package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;

public class TutorialComponent implements Component {

	private int tutorialNumber;
	
	private boolean goal1Reached = false;
	private boolean goal2Reached = false;
	private boolean goal3Reached = false;
	private boolean goal4Reached = false;
	

	public int getTutorialNumber() {
		return tutorialNumber;
	}

	public void setTutorialNumber(int tutorialNumber) {
		this.tutorialNumber = tutorialNumber;
	}

	public boolean isGoal1Reached() {
		return goal1Reached;
	}

	public void setGoal1Reached(boolean goal1) {
		this.goal1Reached = goal1;
	}

	public boolean isGoal2Reached() {
		return goal2Reached;
	}

	public void setGoal2Reached(boolean goal2) {
		this.goal2Reached = goal2;
	}

	public boolean isGoal3Reached() {
		return goal3Reached;
	}

	public void setGoal3Reached(boolean goal3) {
		this.goal3Reached = goal3;
	}

	public boolean isGoal4Reached() {
		return goal4Reached;
	}

	public void setGoal4Reached(boolean goal4Reached) {
		this.goal4Reached = goal4Reached;
	}

}
