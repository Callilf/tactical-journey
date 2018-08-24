package com.dokkaebistudio.tacticaljourney.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.components.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.PlayerComponent;

public class InputSystem extends IteratingSystem {

    private final ComponentMapper<GridPositionComponent> gridPositionM;

    public InputSystem() {
        super(Family.all(PlayerComponent.class, GridPositionComponent.class).get());
        this.gridPositionM = ComponentMapper.getFor(GridPositionComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        GridPositionComponent g = gridPositionM.get(entity);
        // keys
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            g.coord.x = Math.max(0, g.coord.x -1);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            g.coord.x = Math.min(GameScreen.GRID_W -1, g.coord.x +1);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            g.coord.y = Math.max(0, g.coord.y -1);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            g.coord.y = Math.min(GameScreen.GRID_H-1, g.coord.y +1);
        }
    }
}
