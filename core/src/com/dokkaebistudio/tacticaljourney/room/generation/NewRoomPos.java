package com.dokkaebistudio.tacticaljourney.room.generation;

import com.dokkaebistudio.tacticaljourney.room.generation.FloorGenerator.GenerationMoveEnum;

public class NewRoomPos {
	public int newCoord;
	public GenerationMoveEnum direction;
	
	public NewRoomPos(int c, GenerationMoveEnum d) {
		this.newCoord = c;
		this.direction = d;
	}
}