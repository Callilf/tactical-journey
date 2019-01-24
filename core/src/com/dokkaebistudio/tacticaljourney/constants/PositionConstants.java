package com.dokkaebistudio.tacticaljourney.constants;

import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.GameScreen;

/**
 * Contains constants for positioning the UI
 * @author Callil
 *
 */
public final class PositionConstants {
	
	// UI
	public static Vector2 POS_TIMER = new Vector2(200f, 1030.0f);
	public static float Z_TIMER = 100f;
	
	public static Vector2 POS_TURN = new Vector2(200.0f, 1050.0f);
	public static float Z_TURN = 100f;
	
	public static Vector2 POS_LEVEL = new Vector2(200f, 100f);
	public static float Z_LEVEL = 100f;

	public static Vector2 POS_EXPERIENCE = new Vector2(200f, 80f);
	public static float Z_EXPERIENCE = 100f;
	
	public static Vector2 POS_END_TURN_BTN = new Vector2(5f, 5f);
	public static float Z_END_TURN_BTN = 10f;
	
	public static Vector2 POS_ARROW_SPRITE = new Vector2(1050f,1000f);
	public static float Z_ARROW_SPRITE = 100f;
	
	public static Vector2 POS_ARROW_TEXT = new Vector2(1120f,1040f);
	public static float Z_ARROW_TEXT = 100f;
	
	public static Vector2 POS_BOMB_SPRITE = new Vector2(1250f,1000f);
	public static float Z_BOMB_SPRITE = 100f;
	
	public static Vector2 POS_BOMB_TEXT = new Vector2(1320f,1040f);
	public static float Z_BOMB_TEXT = 100f;
	
	
	// Damage & xp displayers
	public static float Z_DAMAGE_DISPLAYER = 100f;
	public static float Z_EXP_DISPLAYER = 100f;

	
	// SKills	
	public static float Z_SKILL_BTN = 10f;

	public static Vector2 POS_SKILL_1_BTN = new Vector2(1500.0f, 20.0f);
	public static Vector2 POS_SKILL_2_BTN = new Vector2(1580.0f, 20.0f);	
	public static Vector2 POS_SKILL_3_BTN = new Vector2(1660.0f, 20.0f);
	
	
	// Level up notif popin
	public static Vector2 POS_LVL_UP_BACKGROUND = new Vector2(GameScreen.SCREEN_W/2, 550f );
	public static float Z_LVL_UP_BACKGROUND = 510f;

	public static Vector2 POS_LVL_UP_TITLE = new Vector2(GameScreen.SCREEN_W/2, 700f);
	public static float Z_LVL_UP_TITLE = 511;

	public static Vector2 POS_LVL_UP_SUBTITLE = new Vector2(GameScreen.SCREEN_W/2, 650f);
	public static float Z_LVL_UP_SUBTITLE = 511;

	
	public static float Z_LVL_UP_BTN = 500f;
	public static float Z_LVL_UP_BTN_TEXT = 501f;
	public static Vector2 POS_LVL_UP_BTN_1 = new Vector2(GameScreen.SCREEN_W/2, 500f);
	public static Vector2 POS_LVL_UP_BTN_2 = new Vector2(GameScreen.SCREEN_W/2, 430f);
	public static Vector2 POS_LVL_UP_BTN_3 = new Vector2(GameScreen.SCREEN_W/2, 360f);


}
