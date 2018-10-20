package com.dokkaebistudio.tacticaljourney.systems;

import java.util.List;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.InputSingleton;
import com.dokkaebistudio.tacticaljourney.ai.movements.TileSearchUtil;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.components.TileComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.display.TransformComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.util.MovementHandler;

public class PlayerMoveSystem extends IteratingSystem {
	
	private final ComponentMapper<TileComponent> tileCM;
	private final ComponentMapper<PlayerComponent> playerCM;
	private final ComponentMapper<MoveComponent> moveCM;
	private final ComponentMapper<AttackComponent> attackCM;
    private final ComponentMapper<GridPositionComponent> gridPositionM;
    private final ComponentMapper<SpriteComponent> textureCompoM;
    private final ComponentMapper<TransformComponent> transfoCompoM;
    
    private final MovementHandler movementHandler;

    private Room room;

    public PlayerMoveSystem(Room r) {
        super(Family.all(PlayerComponent.class, GridPositionComponent.class).get());
        this.tileCM = ComponentMapper.getFor(TileComponent.class);
        this.gridPositionM = ComponentMapper.getFor(GridPositionComponent.class);
        this.playerCM = ComponentMapper.getFor(PlayerComponent.class);
        this.moveCM = ComponentMapper.getFor(MoveComponent.class);
        this.attackCM = ComponentMapper.getFor(AttackComponent.class);
        this.textureCompoM = ComponentMapper.getFor(SpriteComponent.class);
        this.transfoCompoM = ComponentMapper.getFor(TransformComponent.class);
        room = r;
        movementHandler = new MovementHandler(r.engine);
    }

    @Override
    protected void processEntity(Entity moverEntity, float deltaTime) {
    	MoveComponent moveCompo = moveCM.get(moverEntity);
    	AttackComponent attackCompo = attackCM.get(moverEntity);
    	GridPositionComponent moverCurrentPos = gridPositionM.get(moverEntity);
    	PlayerComponent playerCompo = playerCM.get(moverEntity);
    	
    	if (!room.state.isPlayerTurn()) {
    		return;
    	}
    	
    	//Check if the end turn button has been pushed
    	handleEndTurnButton(moveCompo, attackCompo, playerCompo);
    	
    	switch(room.state) {
    	
    	case PLAYER_TURN_INIT:
    		moveCompo.moveRemaining = moveCompo.moveSpeed;
    		room.state = RoomState.PLAYER_COMPUTE_MOVABLE_TILES;
    	
    	case PLAYER_COMPUTE_MOVABLE_TILES:
    		//clear the movable tile
			moveCompo.clearMovableTiles();
			if (attackCompo != null) attackCompo.clearAttackableTiles();
    		
    		//Build the movable tiles list
        	TileSearchUtil.buildMoveTilesSet(moverEntity, moveCompo, room, gridPositionM, tileCM);
        	if (attackCompo != null) TileSearchUtil.buildAttackTilesSet(moverEntity, moveCompo, attackCompo, room, gridPositionM, tileCM);
	        room.state = RoomState.PLAYER_MOVE_TILES_DISPLAYED;
	        break;
	        
	        
    	case PLAYER_MOVE_TILES_DISPLAYED:
    		//When clicking on a moveTile, display it as the destination
            if (InputSingleton.getInstance().leftClickJustReleased) {
            	int x = Gdx.input.getX();
            	int y = GameScreen.SCREEN_H - Gdx.input.getY();
            	
            	boolean selected = selectDestinationTile(moveCompo, x, y, moverCurrentPos);
            	if (selected) {
            		room.state = RoomState.PLAYER_MOVE_DESTINATION_SELECTED;
            	}
            }
            break;
    		
            
    	case PLAYER_MOVE_DESTINATION_SELECTED:
    		//Either click on confirm to move or click on another tile to change the destination
    		if (InputSingleton.getInstance().leftClickJustReleased) {
    			int x = Gdx.input.getX();
            	int y = GameScreen.SCREEN_H - Gdx.input.getY();
    			
            	SpriteComponent selectedTileSprite = textureCompoM.get(moveCompo.getSelectedTile());
            	SpriteComponent playerSprite = textureCompoM.get(moverEntity);
            	
            	if (selectedTileSprite.containsPoint(x, y)) {
            		//Confirm movement is we click on the selected tile again
            		
            		//Initiate movement
            		movementHandler.initiateMovement(moverEntity);
            		
            		room.state = RoomState.PLAYER_MOVING;
            	} else if (playerSprite.containsPoint(x, y)) {
            		//Cancel movement is we click on the character
            		moveCompo.clearSelectedTile();
            		room.state = RoomState.PLAYER_MOVE_TILES_DISPLAYED;
            	} else {
	    			//No confirmation, check if another tile has been selected
	    			selectDestinationTile(moveCompo, x, y, moverCurrentPos);
	    			room.state = RoomState.PLAYER_MOVE_DESTINATION_SELECTED;
            	}
    			
    		}
    		
    		break;
    		
    	case PLAYER_MOVING:
    		TransformComponent transfoCompo = transfoCompoM.get(moverEntity);
    		moveCompo.selectCurrentMoveDestinationTile(gridPositionM);
    		
    		//Do the movement on screen
    		boolean movementFinished = movementHandler.performRealMovement(moverEntity, room);
    		if (movementFinished) room.state = RoomState.PLAYER_END_MOVEMENT;
    		
    		break;
    		
    	case PLAYER_END_MOVEMENT:
    		movementHandler.finishRealMovement(moverEntity);
    		
    		//Compute the cost of this move
    		int cost = computeCostOfMovement(moveCompo);
    		moveCompo.moveRemaining = moveCompo.moveRemaining - cost;
    		
    		room.state = RoomState.PLAYER_COMPUTE_MOVABLE_TILES;
    		break;
    		
    	default:
    		break;
    	
    	}
    }

    /**
     * Handle the end turn button.
     * @param moveCompo the move component
     * @param attackCompo the attack component
     * @param playerCompo the player component
     */
	private void handleEndTurnButton(MoveComponent moveCompo, AttackComponent attackCompo, PlayerComponent playerCompo) {
		if (InputSingleton.getInstance().leftClickJustPressed) {
    		int x = Gdx.input.getX();
        	int y = GameScreen.SCREEN_H - Gdx.input.getY();
        	
        	//If click on the endTurnButton, make it look pushed but don't to any thing else.
        	//The real action is on click release.
        	SpriteComponent spriteComponent = textureCompoM.get(playerCompo.getEndTurnButton());
        	if (spriteComponent.containsPoint(x, y)) {
        		spriteComponent.setSprite(new Sprite(Assets.getTexture(Assets.btn_end_turn_pushed)));
        	}
    	}
    	if (InputSingleton.getInstance().leftClickJustReleased) {
    		int x = Gdx.input.getX();
        	int y = GameScreen.SCREEN_H - Gdx.input.getY();
        	
        	//If release on the endTurnButton, restore the original texture and end the turn.
        	SpriteComponent spriteComponent = textureCompoM.get(playerCompo.getEndTurnButton());
    		spriteComponent.setSprite(new Sprite(Assets.getTexture(Assets.btn_end_turn)));
        	if (spriteComponent.containsPoint(x, y)) {
        		moveCompo.clearMovableTiles();
        		attackCompo.clearAttackableTiles();
    			room.turnManager.endPlayerTurn();
        	}
    	}
    	if (InputSingleton.getInstance().spaceJustPressed) {
    		//If space pressed, make the endTurnButton look pushed.
        	SpriteComponent spriteComponent = textureCompoM.get(playerCompo.getEndTurnButton());
        	spriteComponent.setSprite(new Sprite(Assets.getTexture(Assets.btn_end_turn_pushed)));
    	}
    	if (InputSingleton.getInstance().spaceJustReleased) {
    		//If space released, restore the button texture and end the turn.
        	SpriteComponent spriteComponent = textureCompoM.get(playerCompo.getEndTurnButton());
    		spriteComponent.setSprite(new Sprite(Assets.getTexture(Assets.btn_end_turn)));
    		moveCompo.clearMovableTiles();
    		attackCompo.clearAttackableTiles();
			room.turnManager.endPlayerTurn();
    	}
	}

    /**
     * Return the cost of movement
     * @param moveCompo the moveComponent
     * @return the cost of movement
     */
	private int computeCostOfMovement(MoveComponent moveCompo) {
		int cost = 0;
		for (Entity wp : moveCompo.getWayPoints()) {
			GridPositionComponent gridPositionComponent = gridPositionM.get(wp);
			cost = cost + getCostOfTileAtPos(gridPositionComponent.coord);
		}
		GridPositionComponent gridPositionComponent = gridPositionM.get(moveCompo.getSelectedTile());		
		cost = cost + getCostOfTileAtPos(gridPositionComponent.coord);
		return cost;
	}

	/**
	 * Return the cost of move to a given tile.
	 * @param pos the position of the tile.
	 * @return the cost
	 */
	private int getCostOfTileAtPos(Vector2 pos) {
		Entity tileEntity = room.getTileAtGridPosition(pos);
		TileComponent tileComponent = tileCM.get(tileEntity);
		return tileComponent.type.getMoveConsumed();
	}

	/**
	 * Set the destination of the movement.
	 * @param moveCompo the moveComponent
	 * @param x the abscissa of the destination
	 * @param y the ordinate of the destination
	 * @param moverCurrentPos the current position of the mover
	 */
	private boolean selectDestinationTile(MoveComponent moveCompo, int x, int y, GridPositionComponent moverCurrentPos) {
		for (Entity tile : moveCompo.movableTiles) {
			SpriteComponent spriteComponent = textureCompoM.get(tile);
			GridPositionComponent destinationPos = gridPositionM.get(tile);
			
			if (destinationPos.coord.equals(moverCurrentPos.coord)) {
				//Cannot move to the tile we already are
				continue;
			}
			
			if (spriteComponent.containsPoint(x, y)) {
				//Clicked on this tile !!
				//Create an entity to show that this tile is selected as the destination
				Entity destinationTileEntity = room.entityFactory.createDestinationTile(destinationPos.coord);
				moveCompo.setSelectedTile(destinationTileEntity);

				//Display the way to go to this point
				List<Entity> waypoints = TileSearchUtil.buildWaypointList(moveCompo, moverCurrentPos, destinationPos, room, gridPositionM);
            	moveCompo.setWayPoints(waypoints);
				
		    	return true;
			} 

		}
		return false;
	}

}