/**
 * 
 */
package com.dokkaebistudio.tacticaljourney;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

/**
 * Singleton used to manager player's inputs.
 * @author Callil
 *
 */
public class InputSingleton {
	
	/** The instance. */
	private static InputSingleton instance;
	
	/** Whether the left click has been pressed during this frame. */
    public boolean leftClickJustPressed;
    
    /** Whether the left click has been released this frame. */
    public boolean leftClickJustReleased;
    
	/** Whether the right click has been pressed during this frame. */
    public boolean rightClickJustPressed;
    
    /** Whether the right click has been released this frame. */
    public boolean rightClickJustReleased;
    
    /** Whether the space bar has been pressed this frame. */
    public boolean spaceJustPressed;
    
    /** Whether the space bar has been released this frame. */
    public boolean spaceJustReleased; 
	
	
	/**
	 *  Forbidden constructor since it's a singleton.
	 */
	private InputSingleton() {}
	
	/**
	 * Get the instance.
	 * @return the {@link InputSingleton} instance.
	 */
	public static InputSingleton getInstance() {
		if (instance == null) {
			instance = new InputSingleton();
			instance.initInputProcessor();
		}
		
		return instance;
	}
	
	
	/**
	 * Reset all booleans.
	 */
	public void resetEvents() {
		this.leftClickJustPressed = false;
		this.leftClickJustReleased = false;
		this.rightClickJustPressed = false;
		this.rightClickJustReleased = false;
		this.spaceJustPressed = false;
		this.spaceJustReleased = false;
	}
	
	
	/**
	 * Initialize the inputProcessor.
	 */
	private void initInputProcessor() {
		Gdx.input.setInputProcessor(new InputProcessor() {

			@Override
			public boolean keyDown(int keycode) {
				if (keycode == Input.Keys.SPACE) {
					spaceJustPressed = true;
					return true;
				}
				return false;
			}

			@Override
			public boolean keyUp(int keycode) {
				if (keycode == Input.Keys.SPACE) {
					spaceJustReleased = true;
					return true;
				}
				return false;
			}

			@Override
			public boolean keyTyped(char character) {
				return false;
			}

			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				if (button == Input.Buttons.LEFT) {
					leftClickJustPressed = true;
					return true;
				}
				if (button == Input.Buttons.RIGHT) {
					rightClickJustPressed = true;
					return true;
				}
				return false;
			}

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
				if (button == Input.Buttons.LEFT) {
					leftClickJustReleased = true;
					return true;
				}
				if (button == Input.Buttons.RIGHT) {
					rightClickJustReleased = true;
					return true;
				}
				return false;
			}

			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) {
				return false;
			}

			@Override
			public boolean mouseMoved(int screenX, int screenY) {
				return false;
			}

			@Override
			public boolean scrolled(int amount) {
				return false;
			}

        });
	}
	
}
