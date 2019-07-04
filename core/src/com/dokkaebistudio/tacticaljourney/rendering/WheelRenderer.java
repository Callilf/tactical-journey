package com.dokkaebistudio.tacticaljourney.rendering;

import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.dokkaebistudio.tacticaljourney.assets.SceneAssets;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.rendering.interfaces.Renderer;
import com.dokkaebistudio.tacticaljourney.rendering.service.PopinService;
import com.dokkaebistudio.tacticaljourney.systems.WheelSystem;
import com.dokkaebistudio.tacticaljourney.wheel.AttackWheel;
import com.dokkaebistudio.tacticaljourney.wheel.Sector;

public class WheelRenderer implements Renderer {
	
	public static final int WHEEL_X = GameScreen.SCREEN_W/2;
	public static final int WHEEL_Y = 700;
	
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
	
	/** The stage. */
	private Stage stage;
	
	private boolean tableDisplayed = false;
	private Table mainTable;
	private Label accuracyLbl;
	
	public WheelRenderer (AttackWheel wheel, GameScreen gs, SpriteBatch sb, ShapeRenderer sr, Stage stage) {
		this.wheel = wheel;
		this.gameScreen = gs;
		this.shapeRenderer = sr;
		this.batcher = sb;
		this.stage = stage;
		
		
		mainTable = new Table();
		mainTable.setBackground(new NinePatchDrawable(SceneAssets.popinNinePatch));
		
		accuracyLbl = new Label("Accuracy", PopinService.hudStyle());
		mainTable.add(accuracyLbl).pad(10, 10, 10, 10);
		mainTable.row();
		
		// Uncomment to add the ATTACK button
//		TextButton attack = new TextButton("ATTACK", PopinService.buttonStyle());
//		attack.addListener(new ClickListener() {			
//			@Override
//			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
//				WheelSystem.attackButtonPressed = true;
//				return super.touchDown(event, x, y, pointer, button);
//			}
//			
//			@Override
//			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
//				super.touchUp(event, x, y, pointer, button);
//				WheelSystem.attackButtonReleased = true;
//				mainTable.remove();
//				tableDisplayed = false;
//			}
//			
//		});
//		mainTable.add(attack).width(300).height(150).pad(10, 10, 10, 10);
//		mainTable.row();
		
		mainTable.pack();
		mainTable.setPosition(GameScreen.SCREEN_W/2 - mainTable.getWidth()/2, 300);
	}
	
	public void render(float deltaTime) {
		if (wheel.isDisplayed()) {
			if (!tableDisplayed) {
				accuracyLbl.setText("Accuracy: " + wheel.getCurrentAccuracy());
				
				this.stage.addActor(mainTable);
				tableDisplayed = true;
			}
			
			
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
		} else {
			if (tableDisplayed) {
				tableDisplayed = false;
				mainTable.remove();
			}
		}
	}

}
