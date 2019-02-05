/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.rendering.interfaces;

/**
 * Marker to explain that this class can render things on screen.
 * @author Callil
 *
 */
public interface Renderer {

	/**
	 * Render something lulz.
	 * @param deltaTime the delta time of this frame.
	 */
	void render(float deltaTime);
}
