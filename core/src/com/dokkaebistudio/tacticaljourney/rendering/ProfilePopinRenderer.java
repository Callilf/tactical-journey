package com.dokkaebistudio.tacticaljourney.rendering;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.InputSingleton;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.player.ExperienceComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class ProfilePopinRenderer implements Renderer, RoomSystem {
	    
	public Stage stage;
	
	private Entity player;
	
	/** The player component of the player (kept in cache to prevent getting it at each frame). */
	private PlayerComponent playerCompo;
	/** The experience component of the player (kept in cache to prevent getting it at each frame). */
	private ExperienceComponent expCompo;
	
	/** The current room. */
    private Room room;
    
    /** The main table of the popin. */
    private Table table;
    
	private LabelStyle hudStyle;
    
    /** The state before the level up state. */
    private RoomState previousState;
    
    public ProfilePopinRenderer(Room r, Stage s, Entity p) {
        this.room = r;
        this.player = p;
        this.stage = s;
        
		hudStyle = new LabelStyle(Assets.font, Color.WHITE);

    }
    
    @Override
    public void enterRoom(Room newRoom) {
    	this.room = newRoom;	
    }

    @Override
	public void render(float deltaTime) {
    	
    	if (playerCompo == null) {
    		playerCompo = Mappers.playerComponent.get(player);
    	}
    	if (expCompo == null) {
    		expCompo = Mappers.experienceComponent.get(player);
    	}
    	
    	if (playerCompo != null && playerCompo.isProfilePopinDisplayed()) {

    		if (table == null) {
	    		previousState = room.getNextState() != null ? room.getNextState() : room.getState();
	    		room.setNextState(RoomState.PROFILE_POPIN);
	    		
	    		MoveComponent moveComponent = Mappers.moveComponent.get(player);
	    		AttackComponent attackComponent = Mappers.attackComponent.get(player);
	    		HealthComponent healthComponent = Mappers.healthComponent.get(player);
	    		
	    		table = new Table();
//	    		table.setDebug(true, true);
	    		table.setPosition(361, 24);
	    		//table.setTouchable(Touchable.childrenOnly);
	    		
	    		TextureRegionDrawable topBackground = new TextureRegionDrawable(Assets.getTexture(Assets.profile_background));
	    		table.setBackground(topBackground);
	    		
	    		table.align(Align.top);
	    		
	    		// TITLE
	    		Label title = new Label("Profile", hudStyle);
	    		table.add(title).uniformX().pad(20, 0, 20, 0);
	    		table.row();
	    		
	    		Label maxHphLbl = new Label("Max hp: " + healthComponent.getMaxHp(), hudStyle);
	    		table.add(maxHphLbl).uniformX().left();
	    		table.row();
	    		
	    		Label strengthLbl = new Label("Strength: " + attackComponent.getStrength(), hudStyle);
	    		table.add(strengthLbl).uniformX().left();
	    		table.row();
	    		
	    		Label moveLbl = new Label("Move: " + moveComponent.moveSpeed, hudStyle);
	    		table.add(moveLbl).uniformX().left().padBottom(20);
	    		table.row();
	    		
	    		
	    		AttackComponent rangeAttackCompo = Mappers.attackComponent.get(playerCompo.getSkillRange());
	    		Label rangeDistLbl = new Label("Bow range: " + rangeAttackCompo.getRangeMin() + "-" + rangeAttackCompo.getRangeMax(), hudStyle);
	    		table.add(rangeDistLbl).uniformX().left();
	    		table.row();
	    		Label rangeStrengthLbl = new Label("Bow damage: " + rangeAttackCompo.getStrength(), hudStyle);
	    		table.add(rangeStrengthLbl).uniformX().left().padBottom(20);
	    		table.row();
	    		
	    		AttackComponent bombAttackCompo = Mappers.attackComponent.get(playerCompo.getSkillBomb());
	    		Label bombDistLbl = new Label("Bomb throw range: " + bombAttackCompo.getRangeMax(), hudStyle);
	    		table.add(bombDistLbl).uniformX().left();
	    		table.row();
	    		Label bombDmg = new Label("Bomb damage: " + bombAttackCompo.getStrength(), hudStyle);
	    		table.add(bombDmg).uniformX().left();
	    		table.row();
	    		
	    		Label bombDuration = new Label("Bomb dur.: " + bombAttackCompo.getBombTurnsToExplode() + " turns" , hudStyle);
	    		table.add(bombDuration).uniformX().left();
	    		table.row();
	    		Label bombRadius = new Label("Bomb radius: " + bombAttackCompo.getBombRadius() , hudStyle);
	    		table.add(bombRadius).uniformX().left();
	    		table.row();

	        	
	        	table.pack();
	
	    		stage.addActor(table);
    		}
    		
            stage.act(Gdx.graphics.getDeltaTime());
    		stage.draw();
    		
    		
    		if (InputSingleton.getInstance().leftClickJustPressed) {
    			closePopin();
    		}

    	} else if (room.getState() == RoomState.PROFILE_POPIN) {
    		closePopin();
    	}
    
    }

	/**
	 * Close the level up popin and unpause the game.
	 */
	private void closePopin() {
		playerCompo.setProfilePopinDisplayed(false);

		table.clear();
		table.remove();
		table = null;
		room.setNextState(previousState);
	}

}
