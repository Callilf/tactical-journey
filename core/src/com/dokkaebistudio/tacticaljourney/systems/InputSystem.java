package com.dokkaebistudio.tacticaljourney.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.World;
import com.dokkaebistudio.tacticaljourney.components.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.components.TileComponent;

public class InputSystem extends IteratingSystem {

    private final ComponentMapper<GridPositionComponent> gridPositionM;
    private World world;

    public InputSystem(World w) {
        super(Family.all(PlayerComponent.class, GridPositionComponent.class).get());
        this.gridPositionM = ComponentMapper.getFor(GridPositionComponent.class);
        world = w;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        GridPositionComponent g = gridPositionM.get(entity);
        
        Vector2 newLocation = null;
        
        // keys
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
        	newLocation = new Vector2(Math.max(0, g.coord.x -1), g.coord.y);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
        	newLocation = new Vector2(Math.min(GameScreen.GRID_W -1, g.coord.x +1), g.coord.y);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
        	newLocation = new Vector2(g.coord.x, Math.max(0, g.coord.y -1));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
        	newLocation = new Vector2(g.coord.x, Math.min(GameScreen.GRID_H-1, g.coord.y +1));
        }
        
        if (newLocation != null) {
        	attemptToMove(g, newLocation);
        }
    }

	private void attemptToMove(GridPositionComponent g, Vector2 newLocation) {
		Entity destinationTile = world.grid[(int) newLocation.x][(int) newLocation.y];
		TileComponent destiTileCompo = destinationTile.getComponent(TileComponent.class);
		if (!destiTileCompo.type.isWall() && !destiTileCompo.type.isPit()) {
			g.coord.x = newLocation.x;
			g.coord.y = newLocation.y;
		}
	}
}
