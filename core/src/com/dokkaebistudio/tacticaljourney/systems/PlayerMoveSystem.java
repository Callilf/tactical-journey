package com.dokkaebistudio.tacticaljourney.systems;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.components.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.components.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.TileComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;

public class PlayerMoveSystem extends IteratingSystem {

	private final ComponentMapper<TileComponent> tileCM;
	private final ComponentMapper<PlayerComponent> playerCM;
    private final ComponentMapper<GridPositionComponent> gridPositionM;
    private final ComponentMapper<SpriteComponent> textureCompoM;
    private Room room;
    private boolean leftClickJustPressed;

    public PlayerMoveSystem(Room r) {
        super(Family.all(PlayerComponent.class, GridPositionComponent.class).get());
        this.tileCM = ComponentMapper.getFor(TileComponent.class);
        this.gridPositionM = ComponentMapper.getFor(GridPositionComponent.class);
        this.playerCM = ComponentMapper.getFor(PlayerComponent.class);
        this.textureCompoM = ComponentMapper.getFor(SpriteComponent.class);
        room = r;
        
        initInputProcessor();
    }

    @Override
    protected void processEntity(Entity moverEntity, float deltaTime) {
    	PlayerComponent playerCompo = playerCM.get(moverEntity);
    	GridPositionComponent moverCurrentPos = gridPositionM.get(moverEntity);
    	
    	switch(room.state) {
    	
    	case PLAYER_MOVE_START:
    		//clear the movable tile
			playerCompo.clearMovableTiles();
    		
    		//Build the movable tiles list
        	buildMoveTilesSet(moverEntity, playerCompo);
	        room.state = RoomState.PLAYER_MOVE_TILES_DISPLAYED;
	        break;
	        
	        
    	case PLAYER_MOVE_TILES_DISPLAYED:
    		//When clicking on a moveTile, display it as the destination
            if (leftClickJustPressed) {
            	int x = Gdx.input.getX();
            	int y = GameScreen.SCREEN_H - Gdx.input.getY();
            	
            	selectDestinationTile(playerCompo, x, y, moverCurrentPos);
            }
            break;
    		
            
    	case PLAYER_MOVE_DESTINATION_SELECTED:
    		//Either click on confirm to move or click on another tile to change the destination
    		if (leftClickJustPressed) {
    			int x = Gdx.input.getX();
            	int y = GameScreen.SCREEN_H - Gdx.input.getY();
    			
    			//First test the confirmation button
            	SpriteComponent confirmationButtonSprite = textureCompoM.get(playerCompo.getMovementConfirmationButton());
            	if (confirmationButtonSprite.containsPoint(x, y)) {
            		//Clicked on the confirmation button, move the entity
            		
            		//TODO move the player
            		GridPositionComponent selectedTilePos = gridPositionM.get(playerCompo.getSelectedTile());
            		moverCurrentPos.coord.set(selectedTilePos.coord);
            		room.state = RoomState.PLAYER_MOVE_START;
            		
            		break;
            	}
    			
    			
    			//No confirmation, check if another tile has been selected
    			selectDestinationTile(playerCompo, x, y, moverCurrentPos);
    			
    		}
    		
    		break;
    		
    	default:
    		break;
    	
    	}
    	
    	leftClickJustPressed = false;

    }

	private void selectDestinationTile(PlayerComponent playerCompo, int x, int y, GridPositionComponent moverCurrentPos) {
		for (Entity tile : playerCompo.movableTiles) {
			SpriteComponent spriteComponent = textureCompoM.get(tile);
			GridPositionComponent gridPos = gridPositionM.get(tile);
			
			if (spriteComponent.containsPoint(x, y)) {
				//Clicked on this tile !!
				//Create an entity to show that this tile is selected as the destination
				Entity destinationTileEntity = room.entityFactory.createDestinationTile(gridPos.coord);
				playerCompo.setSelectedTile(destinationTileEntity);
				
				//Display the confirmation button
				Entity moveConfirmationButton = room.entityFactory.createMoveConfirmationButton(moverCurrentPos.coord);
				playerCompo.setMovementConfirmationButton(moveConfirmationButton);
				
				room.state = RoomState.PLAYER_MOVE_DESTINATION_SELECTED;
		    	break;
			} 

		}
	}

	private void buildMoveTilesSet(Entity moverEntity, PlayerComponent playerCompo) {
		GridPositionComponent gridPositionComponent = gridPositionM.get(moverEntity);
		Entity playerTileEntity = room.grid[(int)gridPositionComponent.coord.x][(int)gridPositionComponent.coord.y];
		
		//Find all walkable tiles
		Set<Entity> allWalkableTiles = findAllWalkableTiles(playerTileEntity, 1, playerCompo.moveSpeed);
		allWalkableTiles.remove(playerTileEntity);
		
		//Create entities for each movable tiles to display them
		for (Entity tileCoord : allWalkableTiles) {
			Entity movableTileEntity = room.entityFactory.createMovableTile(gridPositionM.get(tileCoord).coord);
			playerCompo.movableTiles.add(movableTileEntity);
		}
	}

    
    
    
    //***********************************************
    // Movable tiles search algorithm
    
    /**
     * Find all tiles where the entity can move.
     * Recursive method that stops when currentDepth becomes higher than maxDepth
     * @param currentTileEntity the starting tile
     * @param currentDepth the current depth of the search
     * @param maxDepth the max depth of the search
     * @return the set of tiles where the entity can move
     */
	private Set<Entity> findAllWalkableTiles(Entity currentTileEntity, int currentDepth, int maxDepth) {
    	Map<Integer, Set<Entity>> allTilesByDepth = new HashMap<>();
    	return findAllWalkableTiles(currentTileEntity, currentDepth, maxDepth, allTilesByDepth);
	}
	
    /**
     * Find all tiles where the entity can move.
     * Recursive method that stops when currentDepth becomes higher than maxDepth
     * @param currentTileEntity the starting tile
     * @param currentDepth the current depth of the search
     * @param maxDepth the max depth of the search
     * @param allTilesByDepth the map containing for each depth all the tiles the entity can move onto
     * @return the set of tiles where the entity can move
     */
	private Set<Entity> findAllWalkableTiles(Entity currentTileEntity, int currentDepth, int maxDepth, Map<Integer, Set<Entity>> allTilesByDepth) {		
		Set<Entity> walkableTiles = new LinkedHashSet<>();
		
		//Check whether we reached the maxDepth or not
		if (currentDepth <= maxDepth) {
			GridPositionComponent gridPosCompo = gridPositionM.get(currentTileEntity);
	        Vector2 currentPosition = gridPosCompo.coord;
	        int currentX = (int)currentPosition.x;
	        int currentY = (int)currentPosition.y;
			
	        //Check the 4 contiguous tiles and retrieve the ones we can move onto
	        Set<Entity> tilesToIgnore = null;
	        if (allTilesByDepth.containsKey(currentDepth)) {
	        	tilesToIgnore = allTilesByDepth.get(currentDepth);
	        }
			Set<Entity> previouslyReturnedTiles = check4ContiguousTiles(currentX, currentY, tilesToIgnore);
			walkableTiles.addAll(previouslyReturnedTiles);
			
			//Fill the map
			Set<Entity> set = allTilesByDepth.get(currentDepth);
			if (set == null) set = new LinkedHashSet<>();
			set.addAll(previouslyReturnedTiles);
			allTilesByDepth.put(currentDepth, set);
			
			//For each retrieved tile, redo a search until we reach max depth
			for (Entity tile : previouslyReturnedTiles) {
				TileComponent tileComponent = tileCM.get(tile);
				int moveConsumed = tileComponent.type.getMoveConsumed();
	        	Set<Entity> returnedTiles = findAllWalkableTiles(tile, currentDepth + moveConsumed, maxDepth, allTilesByDepth);
	        	walkableTiles.addAll(returnedTiles);
	        }
		}
		
		return walkableTiles;
	}

	/**
	 * Check the 4 contiguous tiles.
	 * @param currentX the current tile X
	 * @param currentY the current tile Y
	 * @return the set of tile entities where it's possible to move
	 */
	private Set<Entity> check4ContiguousTiles(int currentX, int currentY, Set<Entity> tilesToIgnore) {
		Set<Entity> walkableTiles = new LinkedHashSet<>();
		//Left
		if (currentX > 0) {
			Entity tileEntity = room.grid[currentX - 1][currentY];
			checkOneTile(tileEntity, walkableTiles, tilesToIgnore);
		}
		//Up
		if (currentY < GameScreen.GRID_H - 1) {
			Entity tileEntity = room.grid[currentX][currentY + 1];
			checkOneTile(tileEntity, walkableTiles, tilesToIgnore);
		}
		//Right
		if (currentX < GameScreen.GRID_W - 1) {
			Entity tileEntity = room.grid[currentX + 1][currentY];
			checkOneTile(tileEntity, walkableTiles, tilesToIgnore);
		}
		//Down
		if (currentY > 0) {
			Entity tileEntity = room.grid[currentX][currentY - 1];
			checkOneTile(tileEntity, walkableTiles, tilesToIgnore);
		}
		return walkableTiles;
	}

	/**
	 * Check whether the tileEntity can be moved on.
	 * @param tileEntity the tile to check
	 * @param walkableTiles the set of movable entities
	 */
	private void checkOneTile(Entity tileEntity, Set<Entity> walkableTiles, Set<Entity> tilesToIgnore) {
		if (tilesToIgnore != null && tilesToIgnore.contains(tileEntity)) {
			return;
		}
		
		TileComponent tileComponent = tileCM.get(tileEntity);
		//TODO: this condition will have to change when we will have to handle items that allow
		//moving past pits for example.
		if (tileComponent.type.isWalkable()) {
			walkableTiles.add(tileEntity);
		}
	}
	
    // End of movable tiles search algorithm
    //***********************************************

	
	/**
	 * Initialize the inputProcessor.
	 */
	private void initInputProcessor() {
		Gdx.input.setInputProcessor(new InputProcessor() {

			@Override
			public boolean keyDown(int keycode) {
				return false;
			}

			@Override
			public boolean keyUp(int keycode) {
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
				return false;
			}

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
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
