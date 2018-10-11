package com.dokkaebistudio.tacticaljourney.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.InputSingleton;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.components.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.TileComponent;
import com.dokkaebistudio.tacticaljourney.components.TransformComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class PlayerAttackSystem extends IteratingSystem {
	
	private final ComponentMapper<TileComponent> tileCM;
	private final ComponentMapper<PlayerComponent> playerCM;
	private final ComponentMapper<MoveComponent> moveCM;
	private final ComponentMapper<AttackComponent> attackCM;
    private final ComponentMapper<GridPositionComponent> gridPositionM;
    private final ComponentMapper<SpriteComponent> spriteCompoM;
    private final ComponentMapper<TransformComponent> transfoCompoM;
    private final ComponentMapper<HealthComponent> healthCompoM;

    private Room room;

    public PlayerAttackSystem(Room r) {
        super(Family.all(PlayerComponent.class, GridPositionComponent.class).get());
        this.tileCM = ComponentMapper.getFor(TileComponent.class);
        this.gridPositionM = ComponentMapper.getFor(GridPositionComponent.class);
        this.playerCM = ComponentMapper.getFor(PlayerComponent.class);
        this.moveCM = ComponentMapper.getFor(MoveComponent.class);
        this.attackCM = ComponentMapper.getFor(AttackComponent.class);
        this.spriteCompoM = ComponentMapper.getFor(SpriteComponent.class);
        this.transfoCompoM = ComponentMapper.getFor(TransformComponent.class);
        this.healthCompoM = ComponentMapper.getFor(HealthComponent.class);
        room = r;
    }

    @Override
    protected void processEntity(Entity attackerEntity, float deltaTime) {
    	AttackComponent attackCompo = attackCM.get(attackerEntity);
    	MoveComponent moveCompo = moveCM.get(attackerEntity);
    	GridPositionComponent attackerCurrentPos = gridPositionM.get(attackerEntity);
    	PlayerComponent playerCompo = playerCM.get(attackerEntity);
    	
    	if (!room.state.isPlayerTurn()) {
    		return;
    	}
    	    	
    	switch(room.state) {
	        
    	case PLAYER_MOVE_TILES_DISPLAYED:
    		//When clicking on a moveTile, display it as the destination
            if (InputSingleton.getInstance().leftClickJustReleased) {
            	int x = Gdx.input.getX();
            	int y = GameScreen.SCREEN_H - Gdx.input.getY();
            	
            	for (Entity tile : attackCompo.attackableTiles) {
            		SpriteComponent spriteComponent = spriteCompoM.get(tile);
            		if (spriteComponent.containsPoint(x, y)) {
            			//Check the distance of this attackableTile
            			GridPositionComponent gridPositionComponent = gridPositionM.get(tile);
            			int distanceBetweenTiles = TileUtil.getDistanceBetweenTiles(attackerCurrentPos.coord, gridPositionComponent.coord);
            			
            			if (distanceBetweenTiles >= attackCompo.getRangeMin() && distanceBetweenTiles <= attackCompo.getRangeMax()) {
            				//Attack is possible !
	            			Entity target = TileUtil.getAttackableEntityOnTile(gridPositionComponent.coord, room.engine);
	            			
	            			room.attackManager.performAttack(attackerEntity, target);
	            			
	            			moveCompo.clearMovableTiles();
	                		attackCompo.clearAttackableTiles();
	            			room.turnManager.endPlayerTurn();
	            			break;
            			}
            		}
            	}
            	
//            	boolean selected = selectDestinationTile(attackCompo, x, y, attackerCurrentPos);
//            	if (selected) {
//            		room.state = RoomState.PLAYER_MOVE_DESTINATION_SELECTED;
//            	}
            }
            break;
    		
            
    	case PLAYER_MOVE_DESTINATION_SELECTED:

    		
    		break;
    		
    		
    	default:
    		break;
    	
    	}
    	
    }
    
}
