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
	FIRST_VISIT,
	VISITED;
	
	public boolean isVisited() {
		return this == FIRST_ENTRANCE || this == FIRST_VISIT || this == RoomVisitedState.VISITED;
	}
}


