/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.rendering;

import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SnapshotArray;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.rendering.interfaces.Renderer;
import com.dokkaebistudio.tacticaljourney.rendering.service.PopinService;
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
	
	// TEST to debug floor layout
	public static boolean FULL_MAP = false;
	
	private Stage stage;
	
	
	/** Whether the map is displayed on screen or not. */
	private boolean mapDisplayed;
				
	/** The floor to render. */
	private Floor floor;
	
	/** The button to open the map. */
	private TextButton noMapBtn;
	private TextButton smallMapBtn;
	private TextButton fullMapBtn;
	
	/** The main table. */
	private ScrollPane scrollPane;
	private Table roomsTable;
	
	/** The background of the map. */
	private Image smallBackground;
	private Image fullBackground;

	
	private Image player;
	
	private static boolean needRefresh;
	
	
	
	//***************************
	// Current floor attributes
	private Integer minX;
	private Integer maxX;
	private Integer minY;
	private Integer maxY;
	private int xRange;
	private int yRange; 
	
	
	/**
	 * Instanciate a Map Renderer.
	 * @param sr the shaperenderer
	 * @param f the floor which map we want to render
	 */
	public MapRenderer(Stage s, Floor f) {
		this.stage = s;
		this.mapDisplayed = true;
		
		
		smallBackground = new Image(Assets.map_background);
		smallBackground.setPosition(GameScreen.SCREEN_W - Assets.map_background.getRegionWidth() - 5, GameScreen.SCREEN_H - Assets.map_background.getRegionHeight() - Assets.map_panel.getRegionHeight() - 5);
		smallBackground.addAction(Actions.alpha(0.5f));
		stage.addActor(smallBackground);
		
		fullBackground = new Image(Assets.menuBackground);
		fullBackground.setPosition(0, 0);
		fullBackground.addAction(Actions.alpha(0.5f));
		
		Table mapTable = new Table();
		mapTable.setPosition(GameScreen.SCREEN_W - Assets.map_panel.getRegionWidth() - 5, GameScreen.SCREEN_H - Assets.map_panel.getRegionHeight() - 5);
		mapTable.setTouchable(Touchable.childrenOnly);
		TextureRegionDrawable panelBackground = new TextureRegionDrawable(Assets.map_panel);
		mapTable.setBackground(panelBackground);
		
		noMapBtn = new TextButton("None", PopinService.smallButtonCheckedStyle());
		smallMapBtn = new TextButton("Small", PopinService.smallButtonCheckedStyle());
		fullMapBtn = new TextButton("Full", PopinService.smallButtonCheckedStyle());
		
		noMapBtn.setProgrammaticChangeEvents(false);
		noMapBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (noMapBtn.isChecked()) {
					mapDisplayed = false;
					smallBackground.remove();
					fullBackground.remove();
					scrollPane.remove();
					
					smallMapBtn.setChecked(false);
					fullMapBtn.setChecked(false);
				} else {
					noMapBtn.setChecked(true);
				}
			}
		});
		
		smallMapBtn.setProgrammaticChangeEvents(false);
		smallMapBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (smallMapBtn.isChecked()) {
					mapDisplayed = true;
					if (mapDisplayed) {
						fullBackground.remove();
						
						stage.addActor(smallBackground);
						smallBackground.toBack();
						if (!scrollPane.hasParent()) {
							stage.addActor(scrollPane);
						}
						scrollPane.setBounds(smallBackground.getX() + 5, smallBackground.getY() + 5, Assets.map_background.getRegionWidth() - 10, Assets.map_background.getRegionHeight() - 10);
						scrollToCurrentRoom();
						
						noMapBtn.setChecked(false);
						fullMapBtn.setChecked(false);
					}
				} else {
					smallMapBtn.setChecked(true);
				}
			}
		});
		smallMapBtn.setChecked(true);
		
		fullMapBtn.setProgrammaticChangeEvents(false);
		fullMapBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (fullMapBtn.isChecked()) {
					mapDisplayed = true;
					if (mapDisplayed) {
						smallBackground.remove();
						stage.addActor(fullBackground);
						fullBackground.toBack();
						if (!scrollPane.hasParent()) {
							stage.addActor(scrollPane);
						}
						scrollPane.setBounds(0, 0, GameScreen.SCREEN_W, GameScreen.SCREEN_H);
						
						scrollToCurrentRoom();
						
						noMapBtn.setChecked(false);
						smallMapBtn.setChecked(false);
					}
				} else {
					fullMapBtn.setChecked(true);
				}
			}
		});
		
		
		
		mapTable.add().width(65);
		mapTable.add(noMapBtn).padRight(2);
		mapTable.add(smallMapBtn).padRight(2);
		mapTable.add(fullMapBtn);

		mapTable.pack();
		stage.addActor(mapTable);
		
		roomsTable = new Table();
		roomsTable.pad(2000, 2000, 2000, 2000);
//		roomsTable.setDebug(true);

		scrollPane = new ScrollPane(roomsTable);
		scrollPane.setTouchable(Touchable.disabled);
		scrollPane.setSmoothScrolling(false);
		scrollPane.setBounds(smallBackground.getX() + 5, smallBackground.getY() + 5, Assets.map_background.getRegionWidth() - 10, Assets.map_background.getRegionHeight() - 10);
//		scrollPane.addAction(Actions.alpha(0.5f));

		stage.addActor(scrollPane);

		enterFloor(f);
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
		
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}
	
	/**
	 * Change floor.
	 * @param newFloor the new floor
	 */
	public void enterFloor(Floor newFloor) {
		this.floor = newFloor;
		
		// Create the table containing rooms and corridors images
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
		
		xRange = maxX - minX + 1;
		yRange = maxY - minY + 1;
		createTableLayout(xRange, yRange);
		needRefresh = true;
	}

	/**
	 * Build the table of rooms and corridors.
	 */
	private void buildRooms() {

		for (Entry<Vector2, Room> entry : floor.getRoomPositions().entrySet()) {
			Vector2 coord = entry.getKey();
			int tableX = (int) ((coord.x + Math.abs(minX)) * 2);
			int tableY = (int) (((yRange - (coord.y + Math.abs(minY))) * 2) - 2);
			
			Array<Cell> cells = roomsTable.getCells();
			
			Cell c = findCell(tableX, tableY, cells);
					
			if (c != null) {
				Room room = entry.getValue();
				Stack stack = (Stack) c.getActor();
				SnapshotArray<Actor> children = stack.getChildren();
				Image roomImage = (Image) children.get(0);
				Image playerImage = (Image) children.get(1);
				
				if (FULL_MAP) {
					room.setVisited(true);
				}
				
				if (room.isVisited()) {
					switch(room.type) {
					case START_FLOOR_ROOM:
						roomImage.setDrawable(new TextureRegionDrawable(Assets.map_room_start));
						break;
					case END_FLOOR_ROOM:
						roomImage.setDrawable(new TextureRegionDrawable(Assets.map_room_exit));
						break;
					case COMMON_ENEMY_ROOM:
					case EMPTY_ROOM:
						if (room.hasEnemies()) {
							roomImage.setDrawable(new TextureRegionDrawable(Assets.map_room_enemy));
						} else {
							roomImage.setDrawable(new TextureRegionDrawable(Assets.map_room));
						}
						break;
					case SHOP_ROOM:
						roomImage.setDrawable(new TextureRegionDrawable(Assets.map_room_shop));
						break;
					case STATUE_ROOM:
						if (room.hasEnemies()) {
							roomImage.setDrawable(new TextureRegionDrawable(Assets.map_room_statue_enemy));
						} else {
							roomImage.setDrawable(new TextureRegionDrawable(Assets.map_room_statue));
						}
						break;
					case KEY_ROOM:
						if (room.hasEnemies()) {
							roomImage.setDrawable(new TextureRegionDrawable(Assets.map_room_key_enemy));
						} else {
							roomImage.setDrawable(new TextureRegionDrawable(Assets.map_room_key));
						}
						break;
					case ITEM_ROOM:
						if (room.hasEnemies()) {
							roomImage.setDrawable(new TextureRegionDrawable(Assets.map_room_item_enemy));
						} else {
							roomImage.setDrawable(new TextureRegionDrawable(Assets.map_room_item));
						}
						break;
					case BOSS_ROOM:
						if (room.hasEnemies()) {
							roomImage.setDrawable(new TextureRegionDrawable(Assets.map_room_boss_enemy));
						} else {
							roomImage.setDrawable(new TextureRegionDrawable(Assets.map_room_boss));
						}
						break;
					}

					if (floor.getActiveRoom() == room) {
						playerImage.setDrawable(new TextureRegionDrawable(Assets.map_player));
						player = playerImage;
					} else {
						playerImage.setDrawable(null);
					}
		
					
					drawEastCorridor(tableX, tableY, cells, room);
					
					drawSouthCorridor(tableX, tableY, cells, room);

					
				} else {
					if( (room.getSouthNeighbor() != null && room.getSouthNeighbor().isVisited())
							|| (room.getNorthNeighbor() != null && room.getNorthNeighbor().isVisited())
							|| (room.getWestNeighbor() != null && room.getWestNeighbor().isVisited())
							|| (room.getEastNeighbor() != null && room.getEastNeighbor().isVisited())) {
						// Draw unknown room
						roomImage.setDrawable(new TextureRegionDrawable(Assets.map_room_unknown));
						
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

		roomsTable.pack();
	}



	private void createTableLayout(int xRange, int yRange) {
		roomsTable.clear();
		for (int y=0 ; y<yRange; y++) {
			if (y != 0) {
				// Add the room row
				roomsTable.row();
			}
			
			// Add the columns for the current row
			for (int x=0 ; x<xRange ; x++) {
				Stack s = new Stack();
				// Room image
				s.add(new Image());
				// Player image
				s.add(new Image());
				
				roomsTable.add(s).width(Assets.map_room.getRegionWidth()).height(Assets.map_room.getRegionHeight());
				if (x != xRange - 1) {
					roomsTable.add(new Image()).width(Assets.map_corridor.getRegionWidth()).height(Assets.map_corridor.getRegionHeight());
				}
			}
			
			// Add the corridor row
			if (y != yRange - 1) {
				roomsTable.row();
				
				// Add the columns for the current row
				for (int x=0 ; x<xRange ; x++) {
					roomsTable.add(new Image()).width(Assets.map_room.getRegionWidth()).height(Assets.map_corridor.getRegionHeight());
					if (x != xRange - 1) {
						roomsTable.add(new Image()).width(Assets.map_corridor.getRegionWidth()).height(Assets.map_corridor.getRegionHeight());
					}
				}

			}
		}
	}



	private void drawSouthCorridor(int tableX, int tableY, Array<Cell> cells, Room room) {
		if (room.getSouthNeighbor() != null) {
			Cell result = findCell(tableX, tableY + 1, cells);
			Image corridorImg = (Image) result.getActor();
			corridorImg.setDrawable(new TextureRegionDrawable(Assets.map_corridor));
			result.setActor(corridorImg);
			result.width(Assets.map_corridor.getRegionWidth());
		}
	}



	private void drawEastCorridor(int tableX, int tableY, Array<Cell> cells, Room room) {
		if (room.getEastNeighbor() != null) {
			Cell result = findCell(tableX + 1, tableY, cells);
			Image corridorImg = (Image) result.getActor();
			corridorImg.setDrawable(new TextureRegionDrawable(Assets.map_corridor));
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
