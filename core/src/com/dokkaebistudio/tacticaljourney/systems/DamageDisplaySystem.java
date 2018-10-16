package com.dokkaebistudio.tacticaljourney.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.dokkaebistudio.tacticaljourney.components.DamageDisplayComponent;
import com.dokkaebistudio.tacticaljourney.components.TextComponent;
import com.dokkaebistudio.tacticaljourney.components.TransformComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;

public class DamageDisplaySystem extends IteratingSystem {
	
	private final ComponentMapper<DamageDisplayComponent> damageDisplayCompoM;
    private final ComponentMapper<TextComponent> textCompoM;
    private final ComponentMapper<TransformComponent> transfoCompoM;
    
    private Room room;

    public DamageDisplaySystem(Room r) {
        super(Family.all(DamageDisplayComponent.class, TransformComponent.class, TextComponent.class).get());
        this.damageDisplayCompoM = ComponentMapper.getFor(DamageDisplayComponent.class);
        this.textCompoM = ComponentMapper.getFor(TextComponent.class);
        this.transfoCompoM = ComponentMapper.getFor(TransformComponent.class);
        room = r;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
    	DamageDisplayComponent damageDisplayComponent = damageDisplayCompoM.get(entity);
    	
    	TransformComponent transfoCompo = transfoCompoM.get(entity);
    	transfoCompo.pos.y = transfoCompo.pos.y + 1;
    	
    	if (transfoCompo.pos.y > damageDisplayComponent.getInitialPosition().y + 100) {
    		room.engine.removeEntity(entity);
    	}
    	
    }

}
