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
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.components.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.components.TextureComponent;
import com.dokkaebistudio.tacticaljourney.components.TileComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;

public class PlayerMoveSystem extends IteratingSystem {

	private final ComponentMapper<TileComponent> tileCM;
	private final ComponentMapper<PlayerComponent> playerCM;
    private final ComponentMapper<GridPositionComponent> gridPositionM;
    private final ComponentMapper<TextureComponent> textureCompoM;
    private Room world;

    public PlayerMoveSystem(Room w) {
        super(Family.all(PlayerComponent.class, GridPositionComponent.class).get());
        this.tileCM = ComponentMapper.getFor(TileComponent.class);
        this.gridPositionM = ComponentMapper.getFor(GridPositionComponent.class);
        this.playerCM = ComponentMapper.getFor(PlayerComponent.class);
        this.textureCompoM = ComponentMapper.getFor(TextureComponent.class);
        world = w;
    }

    @Override
    protected void processEntity(Entity moverEntity, float deltaTime) {
    	PlayerComponent playerCompo = playerCM.get(moverEntity);
    	
    	//First, check whether the set of movetiles hasn't already been computed
    	if (playerCompo.movableTiles.isEmpty()) {
    		
    		//Build the movable tiles list
        	GridPositionComponent gridPositionComponent = gridPositionM.get(moverEntity);
        	Entity playerTileEntity = world.grid[(int)gridPositionComponent.coord.x][(int)gridPositionComponent.coord.y];
	        
	        //Find all walkable tiles
	        Set<Entity> allWalkableTiles = findAllWalkableTiles(playerTileEntity, 1, playerCompo.moveSpeed);
	        allWalkableTiles.remove(playerTileEntity);
	        
	        //Create entities for each movable tiles to display them
	        for (Entity tileCoord : allWalkableTiles) {
	        	GridPositionComponent tilePosCompo = gridPositionM.get(tileCoord);
	        	
	        	//TODO: move this into a factory
	        	Entity movableTileEntity = world.engine.createEntity();
	        	GridPositionComponent movableTilePos = world.engine.createComponent(GridPositionComponent.class);
	        	movableTilePos.coord.set(tilePosCompo.coord);
	        	movableTileEntity.add(movableTilePos);
	        	
	        	TextureComponent texture = world.engine.createComponent(TextureComponent.class);
	        	texture.region = Assets.getTexture(Assets.tile_movable);
	        	movableTileEntity.add(texture);
	        	
	        	playerCompo.movableTiles.add(movableTileEntity);
	        	world.engine.addEntity(movableTileEntity);
	        }
	        
    	} else {
    		
    		//Handle mouse click movable tiles
            if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
            	int x = Gdx.input.getX();
            	int y = GameScreen.SCREEN_H - Gdx.input.getY();
            	
            	for (Entity tile : playerCompo.movableTiles) {
            		TextureComponent textureComponent = textureCompoM.get(tile);
            		GridPositionComponent gridPos = gridPositionM.get(tile);
            		float tileStartX = gridPos.coord.x * GameScreen.GRID_SIZE + GameScreen.LEFT_RIGHT_PADDING;
            		float tileStartY = gridPos.coord.y * GameScreen.GRID_SIZE + GameScreen.BOTTOM_MENU_HEIGHT;
            		float tileEndX = tileStartX + textureComponent.region.getRegionWidth();
            		float tileEndY = tileStartY + textureComponent.region.getRegionHeight();
            		
            		if (tileStartX < x && tileEndX > x && tileStartY < y && tileEndY > y) {
            			//Clic on this tile !!
            			Entity redCross = world.engine.createEntity();
            			
            			//TODO: move this into a factory
            			GridPositionComponent movableTilePos = world.engine.createComponent(GridPositionComponent.class);
        	        	movableTilePos.coord.set(gridPos.coord);
        	        	redCross.add(movableTilePos);
        	        	TextureComponent texture = world.engine.createComponent(TextureComponent.class);
        	        	texture.region = Assets.getTexture(Assets.tile_movable_selected);
        	        	redCross.add(texture);
            			
        	        	//TODO use a setter that clears beforehand
        	        	playerCompo.clearSelectedTile();
        	        	playerCompo.selectedTile = redCross;
        	        	world.engine.addEntity(redCross);
        	        	break;
            		} 

            	}
            	
            }
    		
    		
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
			Entity tileEntity = world.grid[currentX - 1][currentY];
			checkOneTile(tileEntity, walkableTiles, tilesToIgnore);
		}
		//Up
		if (currentY < GameScreen.GRID_H - 1) {
			Entity tileEntity = world.grid[currentX][currentY + 1];
			checkOneTile(tileEntity, walkableTiles, tilesToIgnore);
		}
		//Right
		if (currentX < GameScreen.GRID_W - 1) {
			Entity tileEntity = world.grid[currentX + 1][currentY];
			checkOneTile(tileEntity, walkableTiles, tilesToIgnore);
		}
		//Down
		if (currentY > 0) {
			Entity tileEntity = world.grid[currentX][currentY - 1];
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

}
