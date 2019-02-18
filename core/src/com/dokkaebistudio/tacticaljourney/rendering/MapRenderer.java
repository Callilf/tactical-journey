/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.rendering;

import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.rendering.interfaces.Renderer;
import com.dokkaebistudio.tacticaljourney.room.Floor;
import com.dokkaebistudio.tacticaljourney.room.Room;

/**
 * This class allows rendering the map of the current floor.
 * @author Callil
 *
 */
public class MapRenderer implements Renderer {
	
	float offsetX = GameScreen.SCREEN_W - GameScreen.SCREEN_W/8;
	float offsetY = GameScreen.SCREEN_H - GameScreen.SCREEN_H/5;
	
	private Stage stage;
	
	
	/** Whether the map is displayed on screen or not. */
	private boolean mapDisplayed;
	
	/** The game screen. */
	private GameScreen gameScreen;
			
	/** The floor to render. */
	private Floor floor;
	
	/** The button to open the map. */
	private Button openMapBtn;
	
	/** The main table. */
	private ScrollPane scrollPane;
	private Table mainGroup;
	
	/** The background of the map. */
	private Image background;
	
	private Image player;
	
	private static boolean needRefresh = true;
	
	/**
	 * Instanciate a Map Renderer.
	 * @param gs the gamescreen
	 * @param sr the shaperenderer
	 * @param f the floor which map we want to render
	 */
	public MapRenderer(GameScreen gs, Stage s, Floor f) {
		this.stage = s;
		this.gameScreen = gs;
		this.floor = f;
		this.mapDisplayed = true;
		
		gameScreen.guiCam.update();
		
		
		background = new Image(Assets.map_background);
		background.setPosition(GameScreen.SCREEN_W - Assets.map_background.getRegionWidth(), GameScreen.SCREEN_H - Assets.map_background.getRegionHeight());
		background.addAction(Actions.alpha(0.3f));
		stage.addActor(background);

		
		Table mapTable = new Table();
		mapTable.setPosition(1830f, 1047f);
		mapTable.setTouchable(Touchable.childrenOnly);
		
		Drawable mapButtonUp = new SpriteDrawable(new Sprite(Assets.map_minus));
		Drawable mapButtonDown = new SpriteDrawable(new Sprite(Assets.map_minus));
		Drawable mapButtonChecked = new SpriteDrawable(new Sprite(Assets.map_plus));
		ButtonStyle endTurnButtonStyle = new ButtonStyle(mapButtonUp, mapButtonDown,mapButtonChecked);
		openMapBtn = new Button(endTurnButtonStyle);
		
		openMapBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				mapDisplayed = !openMapBtn.isChecked();
				if (mapDisplayed) {
					stage.addActor(background);
					background.toBack();
					stage.addActor(scrollPane);
					
					scrollToCurrentRoom();

				} else {
					background.remove();
					scrollPane.remove();
				}
			}

		});
		mapTable.add(openMapBtn);

		mapTable.pack();
		stage.addActor(mapTable);
		
		mainGroup = new Table();
		mainGroup.pad(200, 200, 200, 200);
//		mainGroup.setDebug(true);

		scrollPane = new ScrollPane(mainGroup);
		scrollPane.setTouchable(Touchable.disabled);
		scrollPane.setBounds(background.getX() + 5, background.getY() + 5, Assets.map_background.getRegionWidth() - 10, Assets.map_background.getRegionHeight() - 40);
//		scrollPane.addAction(Actions.alpha(0.5f));

		stage.addActor(scrollPane);
	}
	

	
	/**
	 * Render the map of the floor.
	 */
	public void render(float deltaTime) {

		if (needRefresh) {
			buildRooms();
			
			scrollToCurrentRoom();

			needRefresh = false;
		}
		
		
		// 2 - Button
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();

	}

	private void buildRooms() {
		mainGroup.clear();
		
		Integer minX = null;
		Integer maxX = null;
		Integer minY = null;
		Integer maxY = null;
		for (Vector2 coord : floor.getRoomPositions().keySet()) {
			if (minX == null || coord.x < minX.intValue()) {
				minX = (int) coord.x;
			}
			if (maxX == null || coord.x > maxX.intValue()) {
				maxX = (int) coord.x;
			}
			if (minY == null || coord.y < minY.intValue()) {
				minY = (int) coord.y;
			}
			if (maxY == null || coord.y > maxY.intValue()) {
				maxY = (int) coord.y;
			}
		}
		
		int xRange = maxX - minX + 1;
		int yRange = maxY - minY + 1;
		for (int y=0 ; y<yRange; y++) {
			if (y != 0) {
				// Add the room row
				mainGroup.row();
			}
			
			// Add the columns for the current row
			for (int x=0 ; x<xRange ; x++) {
				mainGroup.add().width(Assets.map_room.getRegionWidth()).height(Assets.map_room.getRegionHeight());
				if (x != xRange - 1) {
					mainGroup.add().width(Assets.map_corridor.getRegionWidth()).height(Assets.map_corridor.getRegionHeight());
				}
			}
			
			// Add the corridor row
			if (y != yRange - 1) {
				mainGroup.row();
				
				// Add the columns for the current row
				for (int x=0 ; x<xRange ; x++) {
					mainGroup.add().width(Assets.map_room.getRegionWidth()).height(Assets.map_corridor.getRegionHeight());
					if (x != xRange - 1) {
						mainGroup.add().width(Assets.map_corridor.getRegionWidth()).height(Assets.map_corridor.getRegionHeight());
					}
				}

			}
		}
		
		
		
		for (Entry<Vector2, Room> entry : floor.getRoomPositions().entrySet()) {
			Vector2 coord = entry.getKey();
			int tableX = (int) ((coord.x + Math.abs(minX)) * 2);
			int tableY = (int) (((yRange - (coord.y + Math.abs(minY))) * 2) - 2);
			
			Array<Cell> cells = mainGroup.getCells();
			
			boolean cellFound = false;
			
			Cell c = findCell(tableX, tableY, cells);
					
			if (c != null) {
				Room room = entry.getValue();
				Image roomImg = null;
				
				if (room.isVisited()) {
					switch(room.type) {
					case START_FLOOR_ROOM:
						roomImg = new Image(Assets.map_room_start);
						break;
					case END_FLOOR_ROOM:
						roomImg = new Image(Assets.map_room_exit);
						break;
					case COMMON_ENEMY_ROOM:
					case EMPTY_ROOM:
						if (room.hasEnemies()) {
							roomImg = new Image(Assets.map_room_enemy);
						} else {
							roomImg = new Image(Assets.map_room);
						}
						break;
					case SHOP_ROOM:
						roomImg = new Image(Assets.map_room_shop);
						break;
					}
					
					
					if (floor.getActiveRoom() == room) {
						//Place the player icon
						Stack s = new Stack();
						s.add(roomImg);
						
						player = new Image(Assets.map_player);
						s.add(player);
						
						c.setActor(s);
					} else {
						c.setActor(roomImg);

					}
		
					
					drawEastCorridor(tableX, tableY, cells, room);
					
					drawSouthCorridor(tableX, tableY, cells, room);

					
				} else {
					if( (room.getSouthNeighbor() != null && room.getSouthNeighbor().isVisited())
							|| (room.getNorthNeighbor() != null && room.getNorthNeighbor().isVisited())
							|| (room.getWestNeighbor() != null && room.getWestNeighbor().isVisited())
							|| (room.getEastNeighbor() != null && room.getEastNeighbor().isVisited())) {
						// Draw unknown room
						roomImg = new Image(Assets.map_room_unknown);
						c.setActor(roomImg);
						
						if (room.getSouthNeighbor() != null && room.getSouthNeighbor().isVisited()) {
							// Draw corridor
							drawSouthCorridor(tableX, tableY, cells, room);
						}
						if (room.getEastNeighbor() != null && room.getEastNeighbor().isVisited()) {
							// Draw corridor
							drawEastCorridor(tableX, tableY, cells, room);
						}
					}
				}
				
			} else {
				System.out.println("no cell");
			}

		}

		mainGroup.pack();
	}



	private void drawSouthCorridor(int tableX, int tableY, Array<Cell> cells, Room room) {
		if (room.getSouthNeighbor() != null) {
			Cell result = findCell(tableX, tableY + 1, cells);
			Image corridorImg = new Image(Assets.map_corridor);
			result.setActor(corridorImg);
			result.width(Assets.map_corridor.getRegionWidth());
		}
	}



	private void drawEastCorridor(int tableX, int tableY, Array<Cell> cells, Room room) {
		if (room.getEastNeighbor() != null) {
			Cell result = findCell(tableX + 1, tableY, cells);
			Image corridorImg = new Image(Assets.map_corridor);
			result.setActor(corridorImg);
		}
	}



	/**
	 * Find a cell in the table given a coordinate.
	 * @param tableX the x
	 * @param tableY the y
	 * @param cells the array of cells to search from
	 * @return the cell. Null if no cell at this coordinates.
	 */
	private Cell findCell(int tableX, int tableY, Array<Cell> cells) {
		Cell result = null;
		for (Cell c : cells) {
			if (c.getRow() == tableY && c.getColumn() == tableX) {
				result = c;
			}
		}
		return result;
	}
	
	/**
	 * Scroll to center on the current room.
	 */
	private void scrollToCurrentRoom() {
		Group scrollPos = player.getParent();
		scrollPane.layout();
		scrollPane.scrollTo(scrollPos.getX(), scrollPos.getY(), player.getImageWidth(), player.getImageHeight(), true, true);
	}

	
	// getters and setters
	
	public boolean isMapDisplayed() {
		return mapDisplayed;
	}

	public void setMapDisplayed(boolean mapDisplayed) {
		this.mapDisplayed = mapDisplayed;
	}



	public boolean isNeedRefresh() {
		return needRefresh;
	}



	public static void requireRefresh() {
		needRefresh = true;
	}

}
