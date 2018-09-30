package com.dokkaebistudio.tacticaljourney.systems;

import java.util.List;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.ai.movements.MovableTileSearchUtil;
import com.dokkaebistudio.tacticaljourney.components.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.components.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.TileComponent;
import com.dokkaebistudio.tacticaljourney.components.TransformComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;

public class PlayerMoveSystem extends IteratingSystem {
	
	private final ComponentMapper<TileComponent> tileCM;
	private final ComponentMapper<PlayerComponent> playerCM;
	private final ComponentMapper<MoveComponent> moveCM;
    private final ComponentMapper<GridPositionComponent> gridPositionM;
    private final ComponentMapper<SpriteComponent> textureCompoM;
    private final ComponentMapper<TransformComponent> transfoCompoM;

    private Room room;
    private boolean leftClickJustPressed;
    private boolean leftClickJustReleased;
    private boolean spaceJustPressed;
    private boolean spaceJustReleased;

    public PlayerMoveSystem(Room r) {
        super(Family.all(PlayerComponent.class, GridPositionComponent.class).get());
        this.tileCM = ComponentMapper.getFor(TileComponent.class);
        this.gridPositionM = ComponentMapper.getFor(GridPositionComponent.class);
        this.playerCM = ComponentMapper.getFor(PlayerComponent.class);
        this.moveCM = ComponentMapper.getFor(MoveComponent.class);
        this.textureCompoM = ComponentMapper.getFor(SpriteComponent.class);
        this.transfoCompoM = ComponentMapper.getFor(TransformComponent.class);
        room = r;
        
        initInputProcessor();
    }

    @Override
    protected void processEntity(Entity moverEntity, float deltaTime) {
    	MoveComponent moveCompo = moveCM.get(moverEntity);
    	GridPositionComponent moverCurrentPos = gridPositionM.get(moverEntity);
    	PlayerComponent playerCompo = playerCM.get(moverEntity);
    	
    	if (!room.state.isPlayerTurn()) {
    		return;
    	}
    	
    	//TODO refacto
    	//Check if the end turn button has been pushed
    	if (leftClickJustPressed) {
    		int x = Gdx.input.getX();
        	int y = GameScreen.SCREEN_H - Gdx.input.getY();
        	
        	SpriteComponent spriteComponent = textureCompoM.get(playerCompo.getEndTurnButton());
        	if (spriteComponent.containsPoint(x, y)) {
        		spriteComponent.setSprite(new Sprite(Assets.getTexture(Assets.btn_end_turn_pushed)));
        	}
    	}
    	if (leftClickJustReleased) {
    		int x = Gdx.input.getX();
        	int y = GameScreen.SCREEN_H - Gdx.input.getY();
        	
        	SpriteComponent spriteComponent = textureCompoM.get(playerCompo.getEndTurnButton());
    		spriteComponent.setSprite(new Sprite(Assets.getTexture(Assets.btn_end_turn)));
        	if (spriteComponent.containsPoint(x, y)) {
        		moveCompo.clearMovableTiles();
    			room.turnManager.endPlayerTurn();
        	}
    	}
    	if (spaceJustPressed) {
        	SpriteComponent spriteComponent = textureCompoM.get(playerCompo.getEndTurnButton());
        	spriteComponent.setSprite(new Sprite(Assets.getTexture(Assets.btn_end_turn_pushed)));
    	}
    	if (spaceJustReleased) {
        	SpriteComponent spriteComponent = textureCompoM.get(playerCompo.getEndTurnButton());
    		spriteComponent.setSprite(new Sprite(Assets.getTexture(Assets.btn_end_turn)));
    		moveCompo.clearMovableTiles();
			room.turnManager.endPlayerTurn();
    	}

    	
    	switch(room.state) {
    	
    	case PLAYER_TURN_INIT:
    		moveCompo.moveRemaining = moveCompo.moveSpeed;
    		room.state = RoomState.PLAYER_COMPUTE_MOVABLE_TILES;
    	
    	case PLAYER_COMPUTE_MOVABLE_TILES:
    		//clear the movable tile
			moveCompo.clearMovableTiles();
    		
    		//Build the movable tiles list
        	MovableTileSearchUtil.buildMoveTilesSet(moverEntity, moveCompo, room, gridPositionM, tileCM);
	        room.state = RoomState.PLAYER_MOVE_TILES_DISPLAYED;
	        break;
	        
	        
    	case PLAYER_MOVE_TILES_DISPLAYED:
    		//When clicking on a moveTile, display it as the destination
            if (leftClickJustReleased) {
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
    		if (leftClickJustReleased) {
    			int x = Gdx.input.getX();
            	int y = GameScreen.SCREEN_H - Gdx.input.getY();
    			
    			//First test the confirmation button
            	SpriteComponent confirmationButtonSprite = textureCompoM.get(moveCompo.getMovementConfirmationButton());
            	if (confirmationButtonSprite.containsPoint(x, y)) {
            		//Clicked on the confirmation button, move the entity
            		
            		//Initiate movement
            		moveCompo.initiateMovement(moverEntity, moverCurrentPos);
            		
            		room.state = RoomState.PLAYER_MOVING;
            		
            		break;
            	}
    			
    			
    			//No confirmation, check if another tile has been selected
    			selectDestinationTile(moveCompo, x, y, moverCurrentPos);
    			room.state = RoomState.PLAYER_MOVE_DESTINATION_SELECTED;
    			
    		}
    		
    		break;
    		
    	case PLAYER_MOVING:
    		TransformComponent transfoCompo = transfoCompoM.get(moverEntity);
    		moveCompo.selectCurrentMoveDestinationTile(gridPositionM);
    		
    		//Do the movement on screen
    		boolean movementFinished = MovableTileSearchUtil.performRealMovement(moveCompo, transfoCompo, room);
    		if (movementFinished) room.state = RoomState.PLAYER_END_MOVEMENT;
    		
    		break;
    		
    	case PLAYER_END_MOVEMENT:
    		moverEntity.remove(TransformComponent.class);
    		GridPositionComponent selectedTilePos = gridPositionM.get(moveCompo.getSelectedTile());
    		moverCurrentPos.coord.set(selectedTilePos.coord);

    		
    		//Compute the cost of this move
    		int cost = computeCostOfMovement(moveCompo);
    		moveCompo.moveRemaining = moveCompo.moveRemaining - cost;
    		
    		if (moveCompo.moveRemaining <= 0) {
    			moveCompo.clearMovableTiles();
    			room.turnManager.endPlayerTurn();
    		} else {
    			room.state = RoomState.PLAYER_COMPUTE_MOVABLE_TILES;
    		}
    		break;
    		
    	default:
    		break;
    	
    	}
    	
    	leftClickJustPressed = false;
    	leftClickJustReleased = false;
    	spaceJustPressed = false;
    	spaceJustReleased = false;
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
			
			if (spriteComponent.containsPoint(x, y)) {
				//Clicked on this tile !!
				//Create an entity to show that this tile is selected as the destination
				Entity destinationTileEntity = room.entityFactory.createDestinationTile(destinationPos.coord);
				moveCompo.setSelectedTile(destinationTileEntity);
				
				//Display the confirmation button
				Entity moveConfirmationButton = room.entityFactory.createMoveConfirmationButton(moverCurrentPos.coord);
				moveCompo.setMovementConfirmationButton(moveConfirmationButton);
				
				//Display the way to go to this point
				List<Entity> waypoints = MovableTileSearchUtil.buildWaypointList(moveCompo, moverCurrentPos, destinationPos, room, gridPositionM);
            	moveCompo.setWayPoints(waypoints);
				
		    	return true;
			} 

		}
		return false;
	}

	

    
    
    
    

	
	/**
	 * Initialize the inputProcessor.
	 */
	private void initInputProcessor() {
		Gdx.input.setInputProcessor(new InputProcessor() {

			@Override
			public boolean keyDown(int keycode) {
				if (keycode == Input.Keys.SPACE) {
					spaceJustPressed = true;
					return true;
				}
				return false;
			}

			@Override
			public boolean keyUp(int keycode) {
				if (keycode == Input.Keys.SPACE) {
					spaceJustReleased = true;
					return true;
				}
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
				if (button == Input.Buttons.LEFT) {
					leftClickJustReleased = true;
					return true;
				}
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
