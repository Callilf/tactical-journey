/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.rendering;

import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.room.Floor;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.generation.FloorGenerator.GenerationMoveEnum;

/**
 * This class allows rendering the map of the current floor.
 * @author Callil
 *
 */
public class MapRenderer implements Renderer {
	
	// Constants
	private static final float MAP_ROOM_WIDTH = 20;
	private static final float MAP_ROOM_HEIGHT = 20;
	private static final float MAP_CORRIDOR_LENGTH = 10;
	
	float offsetX = GameScreen.SCREEN_W - GameScreen.SCREEN_W/8;
	float offsetY = GameScreen.SCREEN_H - GameScreen.SCREEN_H/5;
	
	private Stage stage;
	
	
	/** Whether the map is displayed on screen or not. */
	private boolean mapDisplayed;
	
	/** The game screen. */
	private GameScreen gameScreen;
	
	/** The libGDX shape renderer. */
	private ShapeRenderer shapeRenderer;
	
	/** The sprite renderer. */
	private SpriteBatch batcher;
	
	/** The floor to render. */
	private Floor floor;
	
	/** The button to open the map. */
	private Button openMapBtn;
	
	/** The background of the map. */
	private Sprite background;
	
	/**
	 * Instanciate a Map Renderer.
	 * @param gs the gamescreen
	 * @param sr the shaperenderer
	 * @param f the floor which map we want to render
	 */
	public MapRenderer(GameScreen gs, Stage s, SpriteBatch sb, ShapeRenderer sr, Floor f) {
		this.stage = s;
		this.gameScreen = gs;
		this.batcher = sb;
		this.shapeRenderer = sr;
		this.floor = f;
		this.mapDisplayed = true;
		
		gameScreen.guiCam.update();
		
		Table mapTable = new Table();
		mapTable.setPosition(1830f, 1047f);
		mapTable.setTouchable(Touchable.childrenOnly);
		
		Drawable mapButtonUp = new SpriteDrawable(new Sprite(Assets.getTexture(Assets.map_minus)));
		Drawable mapButtonDown = new SpriteDrawable(new Sprite(Assets.getTexture(Assets.map_minus)));
		Drawable mapButtonChecked = new SpriteDrawable(new Sprite(Assets.getTexture(Assets.map_plus)));
		ButtonStyle endTurnButtonStyle = new ButtonStyle(mapButtonUp, mapButtonDown,mapButtonChecked);
		openMapBtn = new Button(endTurnButtonStyle);
		
		openMapBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				mapDisplayed = !openMapBtn.isChecked();
			}

		});
		mapTable.add(openMapBtn);

		mapTable.pack();
		stage.addActor(mapTable);

				
		background = new Sprite(Assets.getTexture(Assets.map_background));
		background.setAlpha(0.5f);
		background.setPosition(GameScreen.SCREEN_W - background.getWidth(), GameScreen.SCREEN_H - background.getHeight());
	}
	

	
	/**
	 * Render the map of the floor.
	 */
	public void render(float deltaTime) {

		// 1 - Background
		if (mapDisplayed) {
			gameScreen.guiCam.update();
			batcher.setProjectionMatrix(gameScreen.guiCam.combined);
			batcher.begin();
	
			// Render the background if the map is opened
			background.draw(batcher);
					
			batcher.end();
		}
		
		
		// 2 - Button
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
		
		if (!mapDisplayed) return;
		

		// 3 - Map
		gameScreen.guiCam.update();
		shapeRenderer.setProjectionMatrix(gameScreen.guiCam.combined);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

		
		for(Entry<Room, Vector2> entry : floor.getRoomPositions().entrySet()) {
			Room room = entry.getKey();
			Vector2 pos = entry.getValue();
			
			if (!room.isVisited()) continue;
			
			
			//draw room
			switch(room.type) {
			case START_FLOOR_ROOM:
				shapeRenderer.setColor(Color.PINK);
				break;
			case END_FLOOR_ROOM:
				shapeRenderer.setColor(Color.GOLD);
				break;
			case COMMON_ENEMY_ROOM:
			case EMPTY_ROOM:
				shapeRenderer.setColor(Color.CYAN);
				break;

			}
			
			float rx = offsetX + (pos.x * (MAP_ROOM_WIDTH + MAP_CORRIDOR_LENGTH));
			float ry = offsetY + (pos.y * (MAP_ROOM_HEIGHT + MAP_CORRIDOR_LENGTH));
			shapeRenderer.rect(rx, ry, MAP_ROOM_WIDTH, MAP_ROOM_HEIGHT);
			
			//draw player pos
			if (floor.getActiveRoom() == room) {
				shapeRenderer.setColor(Color.BLACK);
				shapeRenderer.circle(rx + 10, ry + 10, 5);
			}
			
			//draw corridors
			renderCorridor(room, rx, ry, room.getNorthNeighbor(), GenerationMoveEnum.NORTH);
			renderCorridor(room, rx, ry, room.getSouthNeighbor(), GenerationMoveEnum.SOUTH);
			renderCorridor(room, rx, ry, room.getEastNeighbor(), GenerationMoveEnum.EAST);
			renderCorridor(room, rx, ry, room.getWestNeighbor(), GenerationMoveEnum.WEST);

		}
		

		shapeRenderer.end();
	}

	/**
	 * Render a corridor between two rooms, and also render a ghost room if the room on the other side
	 * hasn't been visited yet.
	 * @param room the current room
	 * @param rx the x pos of the current room
	 * @param ry the y pos of the current room
	 * @param neighbor the neighbor to where the corridor must lead
	 * @param corridorDirection the direction of the corrider
	 */
	private void renderCorridor(Room room, float rx, float ry, Room neighbor, GenerationMoveEnum corridorDirection) {
		if (neighbor != null) {
			
			//Draw the corridor
			shapeRenderer.setColor(Color.OLIVE);
			switch(corridorDirection) {
			case NORTH:
				shapeRenderer.rect(rx + 5, ry + MAP_ROOM_HEIGHT, 10, MAP_CORRIDOR_LENGTH);
				break;
			case SOUTH:
				shapeRenderer.rect(rx + 5, ry - MAP_CORRIDOR_LENGTH, 10, MAP_CORRIDOR_LENGTH);
				break;
			case EAST:
				shapeRenderer.rect(rx + MAP_ROOM_WIDTH, ry + 5, MAP_CORRIDOR_LENGTH, 10);
				break;
			case WEST:
				shapeRenderer.rect(rx - MAP_CORRIDOR_LENGTH, ry + 5, MAP_CORRIDOR_LENGTH, 10);
				break;
			}
			
			if (!neighbor.isVisited()) {
				//Draw ghost room
				shapeRenderer.setColor(Color.LIGHT_GRAY);
				Vector2 neighborPos = floor.getRoomPositions().get(neighbor);
				float grx = offsetX + (neighborPos.x * (MAP_ROOM_WIDTH + MAP_CORRIDOR_LENGTH));
				float gry = offsetY + (neighborPos.y * (MAP_ROOM_HEIGHT + MAP_CORRIDOR_LENGTH));
				shapeRenderer.rect(grx, gry, MAP_ROOM_WIDTH, MAP_ROOM_HEIGHT);
			}
		}
	}


	
	
	// getters and setters
	
	public boolean isMapDisplayed() {
		return mapDisplayed;
	}

	public void setMapDisplayed(boolean mapDisplayed) {
		this.mapDisplayed = mapDisplayed;
	}

}
