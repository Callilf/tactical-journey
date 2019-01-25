package com.dokkaebistudio.tacticaljourney.systems.display;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.player.ExperienceComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class HudSystem extends IteratingSystem implements RoomSystem {
	    
	public Stage stage;
	
	/** The current room. */
    private Room room;
    
    
    private Label levelLabel;
    private Label expLabel;
    private Label healthLabel;
    

    public HudSystem(Room r, Stage s) {
        super(Family.all(PlayerComponent.class).get());
        room = r;
        this.stage = s;
    }
    
    @Override
    public void enterRoom(Room newRoom) {
    	this.room = newRoom;	
    }

    @Override
    protected void processEntity(Entity player, float deltaTime) {

    	PlayerComponent playerComponent = Mappers.playerComponent.get(player);
    	HealthComponent healthComponent = Mappers.healthComponent.get(player);
    	ExperienceComponent experienceComponent = Mappers.experienceComponent.get(player);

    	
    	
		Table table = new Table();
		table.setPosition(200,30);
		
		LabelStyle hudStyle = new LabelStyle(Assets.font, Color.WHITE);
		
		// LEVEL
		if (levelLabel == null) {
			levelLabel = new Label("", hudStyle);
		}
		levelLabel.setText("Level [YELLOW]" + experienceComponent.getLevel());
		table.add(levelLabel).left().uniformX();
		table.row();
		
		// XP
		if (expLabel == null) {
			expLabel = new Label("", hudStyle);
		}
		expLabel.setText("Exp: [YELLOW]" + experienceComponent.getCurrentXp() + "[]/" + experienceComponent.getNextLevelXp());
		
		table.add(expLabel).left().uniformX();
		table.row();
		
		// LIFE
		if (healthLabel == null) {
			healthLabel = new Label("", hudStyle);
		}
		healthLabel.setText("Hp: " + healthComponent.getHpColor() + healthComponent.getHp() + "[]/" + healthComponent.getMaxHp());
		table.add(healthLabel).left().uniformX();
    	
		
    	table.pack();
		stage.addActor(table);
    	
    	
        stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
    
    }

}
