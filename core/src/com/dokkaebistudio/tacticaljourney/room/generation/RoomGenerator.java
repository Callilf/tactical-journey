/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room.generation;

import static com.dokkaebistudio.tacticaljourney.GameScreen.GRID_H;
import static com.dokkaebistudio.tacticaljourney.GameScreen.GRID_W;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.TileComponent.TileEnum;
import com.dokkaebistudio.tacticaljourney.factory.EntityFactory;
import com.dokkaebistudio.tacticaljourney.room.Room;

/**
 * Util class to generate a room.
 * @author Callil
 *
 */
public class RoomGenerator {

	/** the entity factory. */
	private EntityFactory entityFactory;
	
	/** Private constructor.*/
	public RoomGenerator(EntityFactory ef) {
		this.entityFactory = ef;
	}
	
	
	/**
	 * Generate the tiles for the room currentRoom.
	 * @param currentRoom the room to generate
	 * @param nn the north neighbor
	 * @param en the east neighbor
	 * @param sn the south neighbor
	 * @param wn the west neighbor
	 * @return the generated array of tiles
	 */
	public Entity[][] generateRoom(Room currentRoom, Room nn, Room en, Room sn, Room wn) {
        Entity[][] grid = null;
		TileEnum[][] tiles = new TileEnum[GRID_W][GRID_H];
		
		//Choose the room pattern
		RandomXS128 random = RandomSingleton.getInstance().getRandom();
		int roomNb = 1 + random.nextInt(7);
		FileHandle roomPattern = Gdx.files.internal("data/rooms/room" + roomNb + ".csv");
		File file = roomPattern.file();
		
		//Load the pattern
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(file.getPath()));
		} catch (FileNotFoundException e) {
			System.out.println("Failed to read the room pattern at " + file.getPath());
		}
		
		//Fill the TileEnum array
		
		//Add a line of walls at the top
		for (int x=0 ; x < GameScreen.GRID_W ; x++) {
			tiles[x][GameScreen.GRID_H - 1] = TileEnum.WALL;
		}
		
		int y = 0;
		while (scanner.hasNext() && y < GameScreen.GRID_H) {
			int realY = GameScreen.GRID_H - 1 - y;
            String[] line = scanner.nextLine().split(";");
            for (int x=0 ; x < GameScreen.GRID_W ; x++) {
            	String tileValStr = line[x];
            	RoomGenerationTileEnum tileVal = tileValStr.equals("") ? RoomGenerationTileEnum.GROUND : RoomGenerationTileEnum.valueOf(line[x]);
            	switch(tileVal) {
            	case N_DOOR :
            		tiles[x][realY] = TileEnum.GROUND;
					entityFactory.createDoor(currentRoom, new Vector2(x,realY), nn);
            		break;
            	case E_DOOR :
            		tiles[x][realY] = TileEnum.GROUND;
					entityFactory.createDoor(currentRoom, new Vector2(x,realY), en);
            		break;
            	case S_DOOR :
            		tiles[x][realY] = TileEnum.GROUND;
					entityFactory.createDoor(currentRoom, new Vector2(x,realY), sn);
            		break;
            	case W_DOOR :
            		tiles[x][realY] = TileEnum.GROUND;
					entityFactory.createDoor(currentRoom, new Vector2(x,realY), wn);
            		break;
            		
            	case WALL :
            		tiles[x][realY] = TileEnum.WALL;
            		break;
            		
            	case PIT :
            		tiles[x][realY] = TileEnum.PIT;
            		break;
            		
            		default:
            			tiles[x][realY] = TileEnum.GROUND;
            	
            	}
            }
            y ++;
		}
		
        scanner.close();
        
        
        //Create the tile entities
        grid = new Entity[GRID_W][GameScreen.GRID_H];
		for (int x = 0; x < GRID_W; x++) {
			for (y = 0; y < GameScreen.GRID_H; y++) {
				Entity tileEntity = entityFactory.createTile(currentRoom, new Vector2(x, y), tiles[x][y]);
				grid[x][y] = tileEntity;
			}
		}
		

		return grid;
	}
}
