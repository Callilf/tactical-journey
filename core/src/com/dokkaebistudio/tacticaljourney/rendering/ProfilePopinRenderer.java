package com.dokkaebistudio.tacticaljourney.rendering;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.InputSingleton;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.player.ExperienceComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.rendering.interfaces.Renderer;
import com.dokkaebistudio.tacticaljourney.rendering.poolables.PoolableLabel;
import com.dokkaebistudio.tacticaljourney.rendering.poolables.PoolableTable;
import com.dokkaebistudio.tacticaljourney.rendering.poolables.PoolableTextureRegionDrawable;
import com.dokkaebistudio.tacticaljourney.rendering.service.PopinService;
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
    
    /** The state before the level up state. */
    private RoomState previousState;
    
    public ProfilePopinRenderer(Room r, Stage s, Entity p) {
        this.room = r;
        this.player = p;
        this.stage = s;
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
	    		
	    		table = PoolableTable.create();
//	    		table.setDebug(true, true);
	    		table.setPosition(GameScreen.SCREEN_W/2, GameScreen.SCREEN_H/2);
	    		//table.setTouchable(Touchable.childrenOnly);
	    		
	    		TextureRegionDrawable topBackground = PoolableTextureRegionDrawable.create(Assets.getTexture(Assets.profile_background));
	    		table.setBackground(topBackground);
	    		
	    		table.align(Align.top);
	    		
	    		// TITLE
	    		Label title = PoolableLabel.create("Profile", PopinService.hudStyle());
	    		table.add(title).uniformX().pad(20, 0, 20, 0);
	    		table.row();
	    		
	    		Label maxHphLbl = PoolableLabel.create("Max hp: " + healthComponent.getMaxHp(), PopinService.hudStyle());
	    		table.add(maxHphLbl).uniformX().left();
	    		table.row();
	    		
	    		Label strengthLbl = PoolableLabel.create("Strength: " + attackComponent.getStrength(), PopinService.hudStyle());
	    		table.add(strengthLbl).uniformX().left();
	    		table.row();
	    		
	    		Label moveLbl = PoolableLabel.create("Move: " + moveComponent.moveSpeed, PopinService.hudStyle());
	    		table.add(moveLbl).uniformX().left().padBottom(20);
	    		table.row();
	    		
	    		
	    		AttackComponent rangeAttackCompo = Mappers.attackComponent.get(playerCompo.getSkillRange());
	    		Label rangeDistLbl = PoolableLabel.create("Bow range: " + rangeAttackCompo.getRangeMin() + "-" + rangeAttackCompo.getRangeMax(), PopinService.hudStyle());
	    		table.add(rangeDistLbl).uniformX().left();
	    		table.row();
	    		Label rangeStrengthLbl = PoolableLabel.create("Bow damage: " + rangeAttackCompo.getStrength(), PopinService.hudStyle());
	    		table.add(rangeStrengthLbl).uniformX().left().padBottom(20);
	    		table.row();
	    		
	    		AttackComponent bombAttackCompo = Mappers.attackComponent.get(playerCompo.getSkillBomb());
	    		Label bombDistLbl = PoolableLabel.create("Bomb throw range: " + bombAttackCompo.getRangeMax(), PopinService.hudStyle());
	    		table.add(bombDistLbl).uniformX().left();
	    		table.row();
	    		Label bombDmg = PoolableLabel.create("Bomb damage: " + bombAttackCompo.getStrength(), PopinService.hudStyle());
	    		table.add(bombDmg).uniformX().left();
	    		table.row();
	    		
	    		Label bombDuration = PoolableLabel.create("Bomb dur.: " + bombAttackCompo.getBombTurnsToExplode() + " turns" , PopinService.hudStyle());
	    		table.add(bombDuration).uniformX().left();
	    		table.row();
	    		Label bombRadius = PoolableLabel.create("Bomb radius: " + bombAttackCompo.getBombRadius() , PopinService.hudStyle());
	    		table.add(bombRadius).uniformX().left();
	    		table.row();

	        	
	        	table.pack();
	    		table.setPosition(GameScreen.SCREEN_W/2 - table.getWidth()/2, GameScreen.SCREEN_H/2 - table.getHeight()/2);
	
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
