package com.dokkaebistudio.tacticaljourney.rendering;

import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.rendering.interfaces.Renderer;
import com.dokkaebistudio.tacticaljourney.wheel.AttackWheel;
import com.dokkaebistudio.tacticaljourney.wheel.Sector;

public class WheelRenderer implements Renderer {
	
	public static final int WHEEL_X = GameScreen.SCREEN_W / 2;
	public static final int WHEEL_Y = GameScreen.SCREEN_H/2;
	
	private static final Color HIT_COLOR = Color.GREEN;
	private static final Color MISS_COLOR = Color.BLACK;
	private static final Color CRITICAL_COLOR = Color.RED;
	private static final Color GRAZE_COLOR = Color.GRAY;
	private static final Color POISON_COLOR = Color.PURPLE;
	
	private static final int WHEEL_RADIUS = 256;
	
	/** The wheel. */
	private AttackWheel wheel;
	
	/** The game screen. */
	private GameScreen gameScreen;
	
	/** The libGDX shape renderer. */
	private ShapeRenderer shapeRenderer;
	
	/** The sprite renderer. */
	private SpriteBatch batcher;
	
	public WheelRenderer (AttackWheel wheel, GameScreen gs, SpriteBatch sb, ShapeRenderer sr) {
		this.wheel = wheel;
		this.gameScreen = gs;
		this.shapeRenderer = sr;
		this.batcher = sb;
	}
	
	public void render(float deltaTime) {
		if (wheel.isDisplayed()) {
			// first normalize sector values
			int total = 0;
			List<Float> normalizeRanges = new LinkedList<Float>();
			for(Sector s: wheel.getSectors()){
				total += s.range;
	
			}
			for(Sector s: wheel.getSectors()){
				normalizeRanges.add(s.range * 360f / (float)total); // the sum of all ranges is 360 now
			}
			
			// begin render
			shapeRenderer.setProjectionMatrix(gameScreen.guiCam.combined);
			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			// Draw a black circle behind (in case of missing sectors)
			shapeRenderer.setColor(MISS_COLOR);
			shapeRenderer.arc(WHEEL_X, WHEEL_Y, WHEEL_RADIUS-1, 0, 360);
			
			int rangeCumul = wheel.getRotationOffset();
			
			for(int i = 0; i< wheel.getSectors().size(); i++) {
				// color
				switch (wheel.getSectors().get(i).hit){
					case HIT:
						shapeRenderer.setColor(HIT_COLOR);
						break;
					case CRITICAL:
						shapeRenderer.setColor(CRITICAL_COLOR);
						break;
					case GRAZE:
						shapeRenderer.setColor(GRAZE_COLOR);
						break;
					case MISS:
						shapeRenderer.setColor(MISS_COLOR);
						break;
					case POISON:
						shapeRenderer.setColor(POISON_COLOR);
						break;
				}
				// draw arc
				shapeRenderer.arc(WHEEL_X, WHEEL_Y, WHEEL_RADIUS, rangeCumul, normalizeRanges.get(i));
				// next arc starts at the end of previous arc
				rangeCumul += normalizeRanges.get(i);
			}
			shapeRenderer.end();
			

			gameScreen.guiCam.update();
			batcher.setProjectionMatrix(gameScreen.guiCam.combined);
			batcher.begin();		
			
			// Render the arrow
			Sprite arrow = wheel.getArrow();
			arrow.setPosition(WHEEL_X - arrow.getWidth()/2, WHEEL_Y - arrow.getHeight()/2);
			arrow.draw(batcher);
			
			batcher.end();
			
		} 
	}

}
