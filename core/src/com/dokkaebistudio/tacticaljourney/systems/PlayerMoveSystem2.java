//package com.dokkaebistudio.tacticaljourney.systems;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import com.badlogic.ashley.core.ComponentMapper;
//import com.badlogic.ashley.core.Entity;
//import com.badlogic.ashley.core.Family;
//import com.badlogic.ashley.systems.IteratingSystem;
//import com.badlogic.gdx.math.Vector2;
//import com.dokkaebistudio.tacticaljourney.Assets;
//import com.dokkaebistudio.tacticaljourney.GameScreen;
//import com.dokkaebistudio.tacticaljourney.components.GridPositionComponent;
//import com.dokkaebistudio.tacticaljourney.components.PlayerComponent;
//import com.dokkaebistudio.tacticaljourney.components.TextureComponent;
//import com.dokkaebistudio.tacticaljourney.components.TileComponent;
//import com.dokkaebistudio.tacticaljourney.room.Room;
//
//public class PlayerMoveSystem2 extends IteratingSystem {
//
//	private final ComponentMapper<TileComponent> tileCM;
//	private final ComponentMapper<PlayerComponent> playerCM;
//    private final ComponentMapper<GridPositionComponent> gridPositionM;
//    private Room world;
//
//    public PlayerMoveSystem2(Room w) {
//        super(Family.all(PlayerComponent.class, GridPositionComponent.class).get());
//        this.tileCM = ComponentMapper.getFor(TileComponent.class);
//        this.gridPositionM = ComponentMapper.getFor(GridPositionComponent.class);
//        this.playerCM = ComponentMapper.getFor(PlayerComponent.class);
//        world = w;
//    }
//
//    @Override
//    protected void processEntity(Entity entity, float deltaTime) {
//    	PlayerComponent playerCompo = playerCM.get(entity);
//    	if (playerCompo.movableTiles.isEmpty()) {
//    		//Build the movable tiles list
//    	
//	        GridPositionComponent gridPosCompo = gridPositionM.get(entity);
//	        
//	        Vector2 currentPosition = gridPosCompo.coord;
//	        int currentX = (int)currentPosition.x;
//	        int currentY = (int)currentPosition.y;
//	        
//	        List<Entity> playerWalkableTiles = playerCompo.movableTiles;
//	        List<Entity> allWalkableTiles = new ArrayList<>();
//	        	        
//	        // 1 - First test the 4 contiguous tiles
//	        List<Entity> previousIterationWalkableTile = checkContiguousTiles(currentX, currentY, allWalkableTiles);
//	        allWalkableTiles.addAll(previousIterationWalkableTile);
//	        
//	        for (Entity tile : previousIterationWalkableTile) {
//	        	GridPositionComponent tilePosCompo = gridPositionM.get(tile);
//	        	List<Entity> returnedTiles = checkContiguousTiles((int) tilePosCompo.coord.x, (int) tilePosCompo.coord.y, allWalkableTiles);
//	        	allWalkableTiles.addAll(returnedTiles);
//	        }
//	        
//	        
//	        
//	        //Create entities for each movable tiles to display them
//	        for (Entity tileCoord : allWalkableTiles) {
//	        	GridPositionComponent tilePosCompo = gridPositionM.get(tileCoord);
//	        	
//	        	Entity movableTileEntity = world.engine.createEntity();
//	        	GridPositionComponent movableTilePos = world.engine.createComponent(GridPositionComponent.class);
//	        	movableTilePos.coord.set(tilePosCompo.coord);
//	        	movableTileEntity.add(movableTilePos);
//	        	
//	        	TextureComponent texture = world.engine.createComponent(TextureComponent.class);
//	        	texture.region = Assets.getTexture(Assets.tile_movable);
//	        	movableTileEntity.add(texture);
//	        	
//	        	playerWalkableTiles.add(movableTileEntity);
//	        	world.engine.addEntity(movableTileEntity);
//	        }
//    	}
//    }
//
//	private List<Entity> checkContiguousTiles(int currentX, int currentY, List<Entity> tilesToIgnore) {
//		List<Entity> walkableTiles = new ArrayList<>();
//		//Left
//		if (currentX > 0) {
//			Entity tileEntity = world.grid[currentX - 1][currentY];
//			checkOneTile(tileEntity, tilesToIgnore, walkableTiles);
//		}
//		//Up
//		if (currentY < GameScreen.GRID_H - 1) {
//			Entity tileEntity = world.grid[currentX][currentY + 1];
//			checkOneTile(tileEntity, tilesToIgnore, walkableTiles);
//		}
//		//Right
//		if (currentX < GameScreen.GRID_W - 1) {
//			Entity tileEntity = world.grid[currentX + 1][currentY];
//			checkOneTile(tileEntity, tilesToIgnore, walkableTiles);
//		}
//		//Down
//		if (currentY > 0) {
//			Entity tileEntity = world.grid[currentX][currentY - 1];
//			checkOneTile(tileEntity, tilesToIgnore, walkableTiles);
//		}
//		
//		return walkableTiles;
//	}
//
//	
//	private void checkOneTile(Entity tileEntity, List<Entity> tilesToIgnore, List<Entity> walkableTiles) {
//		if (!tilesToIgnore.contains(tileEntity)) {
//			TileComponent tileComponent = tileCM.get(tileEntity);
//			if (tileComponent.type.isWalkable()) {
//				walkableTiles.add(tileEntity);
//			}
//		}
//	}
//
//}
