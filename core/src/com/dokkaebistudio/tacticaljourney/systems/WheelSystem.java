package com.dokkaebistudio.tacticaljourney.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.dokkaebistudio.tacticaljourney.components.WheelComponent;
import com.dokkaebistudio.tacticaljourney.components.WheelModifierComponent;

/**
 * This system's role is to upadte the attack wheel, considering
 * all entities that can affect the wheel (items, stats, weapon type etc.).
 */
public class WheelSystem extends EntitySystem {

    private final ComponentMapper<WheelModifierComponent> wheelModifierComponentMapper;
    private final ComponentMapper<WheelComponent> wheelComponentMapper;

    public WheelSystem() {
        // TODO get the real wheel from GameScreen
        this.wheelModifierComponentMapper = ComponentMapper.getFor(WheelModifierComponent.class);
        this.wheelComponentMapper = ComponentMapper.getFor(WheelComponent.class);
    }

    @Override
    public void update(float deltaTime) {
        // get the entity that defines the wheel
        Entity wheelEntity = getEngine().getEntitiesFor(Family.all(WheelComponent.class).get()).first();
        WheelComponent wheel = wheelComponentMapper.get(wheelEntity);
        // TODO init the real wheel

        // get all entities that affect the wheel
        ImmutableArray<Entity> modifiers = getEngine().getEntitiesFor(Family.all(WheelModifierComponent.class).get());
        WheelModifierComponent modifier;
        for (Entity e: modifiers) {
            modifier = wheelModifierComponentMapper.get(e);
            // TODO modifiy the wheel
            if (modifier.removeCriticalSectors) {
                // TODO ...
            }
        }
    }
}
