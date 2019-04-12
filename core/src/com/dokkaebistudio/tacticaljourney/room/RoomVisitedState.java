/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room;

/**
 * @author Callil
 *
 */
public enum RoomVisitedState {
	NEVER_VISITED,
	FIRST_ENTRANCE,
	ENTRANCE,
	VISITED;
	
	public boolean isVisited() {
		return this == FIRST_ENTRANCE || this == ENTRANCE || this == RoomVisitedState.VISITED;
	}
	
	public boolean justEntered() {
		return this == FIRST_ENTRANCE || this == ENTRANCE;
	}
}


