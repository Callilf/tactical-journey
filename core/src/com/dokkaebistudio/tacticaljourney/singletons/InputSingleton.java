/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.singletons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

/**
 * Singleton used to manager player's inputs.
 * @author Callil
 *
 */
public class InputSingleton implements InputProcessor{
	
	/** The instance. */
	private static InputSingleton instance;
	
	/** The game screen. */
	private GameScreen gamescreen;
	
	/** The camera. */
	private OrthographicCamera guicam;
	
	/** The viewpoint. */
	private FitViewport viewport;
	
	/** The last touched point. */
	private Vector3 touchPoint = new Vector3();
	
	public static boolean inputBlocked = false;
	
	//*************
	// Mouse
	
	/** Whether the left click has been pressed during this frame. */
    public boolean leftClickJustPressed;
    
    /** Whether the left click has been released this frame. */
    public boolean leftClickJustReleased;
    
	/** Whether the right click has been pressed during this frame. */
    public boolean rightClickJustPressed;
    
    /** Whether the right click has been released this frame. */
    public boolean rightClickJustReleased;
    
    
    
    //****************
    // Keyboard
    
    /** Whether the space bar has been pressed this frame. */
    public boolean spaceJustPressed;
    
    /** Whether the space bar has been released this frame. */
    public boolean spaceJustReleased; 
	
    /** Whether the 1 key has been pressed this frame. */
    public boolean skill1JustPressed;
    
    /** Whether the 1 key has been released this frame. */
    public boolean skill1JustReleased; 
    
    /** Whether the 2 key has been pressed this frame. */
    public boolean skill2JustPressed;
    
    /** Whether the 2 key has been released this frame. */
    public boolean skill2JustReleased; 
    
	
	/**
	 *  Forbidden constructor since it's a singleton.
	 */
	private InputSingleton() {}
	
	/**
	 * Instanciate the InputSingleton with the camera.
	 * @param guicam the game camera
	 */
	public static void createInstance(GameScreen gs, OrthographicCamera guicam, FitViewport viewport) {
		instance = new InputSingleton();
		instance.guicam = guicam;
		instance.viewport = viewport;
		instance.gamescreen = gs;
	}
	
	/**
	 * Get the instance.
	 * @return the {@link InputSingleton} instance.
	 */
	public static InputSingleton getInstance() {
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
		this.skill1JustPressed = false;
		this.skill1JustReleased = false;
		this.skill2JustPressed = false;
		this.skill2JustReleased = false;
	}
	
	/**
	 * Get the x location of the touch.
	 * @return the x location of the touch
	 */
	public int getClickX() {
		touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		//guicam.unproject(touchPoint);
		viewport.unproject(touchPoint);
		return (int) touchPoint.x;
	}
	
	/**
	 * Get the y location of the touch.
	 * @return the y location of the touch
	 */
	public int getClickY() {
		touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		//guicam.unproject(touchPoint);				
		viewport.unproject(touchPoint);
		return (int) touchPoint.y;
	}
	
	/**
	 * Get the location of the touch.
	 * @return the location of the touch
	 */
	public Vector3 getTouchPoint() {
		touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		//guicam.unproject(touchPoint);
		viewport.unproject(touchPoint);
		return touchPoint;
	}
	
	/**
	 * Get the location of the touch in grid position.
	 * @return the location of the touch
	 */
	public PoolableVector2 getTouchPointInGridPos() {
		Vector3 touchPoint2 = getTouchPoint();
		return TileUtil.convertPixelPosIntoGridPos((int)touchPoint2.x, (int)touchPoint2.y);
	}
	
	
	// Input processor
	
	@Override
	public boolean keyDown(int keycode) {
		if (inputBlocked) return false;
		
		if (keycode == Input.Keys.SPACE) {
			spaceJustPressed = true;
			return true;
		}
		
//		if (keycode == Keys.BACK) {
//			gamescreen.state = GameScreen.GAME_PAUSED;
//			return false;
//		}
		
//		if (keycode == Input.Keys.NUM_1) {
//			skill1JustPressed = true;
//			return true;
//		}
//		if (keycode == Input.Keys.NUM_2) {
//			skill2JustPressed = true;
//			return true;
//		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (inputBlocked) return false;
		
		if (keycode == Input.Keys.SPACE) {
			spaceJustReleased = true;
			return true;
		}
		
		if (keycode == Input.Keys.ESCAPE || keycode == Keys.BACK) {
			if (gamescreen.state == GameScreen.GAME_PAUSED) {
				gamescreen.state = GameScreen.GAME_RUNNING;
			} else if (gamescreen.state == GameScreen.GAME_RUNNING){
				gamescreen.state = GameScreen.GAME_PAUSED;
			}
            return false;
		}
		
//		if (keycode == Input.Keys.NUM_1) {
//			skill1JustReleased = true;
//			return true;
//		}
//		if (keycode == Input.Keys.NUM_2) {
//			skill2JustReleased = true;
//			return true;
//		}
		return true;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (inputBlocked) return false;
		
		if (button == Input.Buttons.LEFT) {
			leftClickJustPressed = true;
			return true;
		}
		if (button == Input.Buttons.RIGHT) {
			rightClickJustPressed = true;
			return true;
		}
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (inputBlocked) return false;
		
		if (button == Input.Buttons.LEFT) {
			leftClickJustReleased = true;
			return true;
		}
		if (button == Input.Buttons.RIGHT) {
			rightClickJustReleased = true;
			return true;
		}
		return true;
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
}
