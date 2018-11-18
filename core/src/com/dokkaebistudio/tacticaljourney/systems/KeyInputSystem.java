package com.dokkaebistudio.tacticaljourney.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.dokkaebistudio.tacticaljourney.components.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;

public class KeyInputSystem extends IteratingSystem implements RoomSystem {

	/** the current room. */
    private Room room;

    public KeyInputSystem(Room r) {
        super(Family.all(PlayerComponent.class, GridPositionComponent.class).get());
        room = r;
    }
    
    @Override
    public void enterRoom(Room newRoom) {
    	this.room = newRoom;	
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
	
//    	PlayerComponent playerCompo = Mappers.playerComponent.get(entity);
//        GridPositionComponent gridPosCompo = Mappers.gridPositionComponent.get(entity);
//        
//        Vector2 newLocation = null;
//        
//        // keys
//        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
//        	newLocation = new Vector2(Math.max(0, gridPosCompo.coord.x -1), gridPosCompo.coord.y);
//        }
//        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
//        	newLocation = new Vector2(Math.min(GameScreen.GRID_W -1, gridPosCompo.coord.x +1), gridPosCompo.coord.y);
//        }
//        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
//        	newLocation = new Vector2(gridPosCompo.coord.x, Math.max(0, gridPosCompo.coord.y -1));
//        }
//        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
//        	newLocation = new Vector2(gridPosCompo.coord.x, Math.min(GameScreen.GRID_H-1, gridPosCompo.coord.y +1));
//        }
//        
//        if (newLocation != null) {
//        	attemptToMove(gridPosCompo, playerCompo, newLocation);
//        }
    }

//	private void attemptToMove(GridPositionComponent g, PlayerComponent playerCompo, Vector2 newLocation) {
//		Entity destinationTile = room.grid[(int) newLocation.x][(int) newLocation.y];
//		TileComponent destiTileCompo = Mappers.tileComponent.get(destinationTile);
//		if (!destiTileCompo.type.isWall() && !destiTileCompo.type.isPit()) {
//			g.coord.x = newLocation.x;
//			g.coord.y = newLocation.y;
//			
//			room.state = RoomState.PLAYER_COMPUTE_MOVABLE_TILES;
//		}
//	}
}
