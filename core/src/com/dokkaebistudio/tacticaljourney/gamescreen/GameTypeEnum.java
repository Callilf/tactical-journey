package com.dokkaebistudio.tacticaljourney.gamescreen;

public enum GameTypeEnum {
	NEW_GAME,
	LOAD_GAME,
	TUTORIAL;
	
	public boolean isNewGame() {
		return this == GameTypeEnum.NEW_GAME;
	}
	
	public boolean isLoadGame() {
		return this == GameTypeEnum.LOAD_GAME;
	}
	
	public boolean isTutorial() {
		return this == GameTypeEnum.TUTORIAL;
	}
}
