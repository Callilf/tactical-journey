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
    
    
    //*****************
    // Actors 
    
    /** The main table of the popin. */
    private Table table;
    private Label title;
    private Label maxHpLbl;
    private Label maxArmorLbl;
    private Label strengthLbl;
	private Label moveLbl;
	private Label rangeDistLbl;
	private Label rangeStrengthLbl;
	private Label bombDistLbl;
	private Label bombDmg;
	private Label bombDuration;
	private Label bombRadius;

    
    
    
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
    	
    	if (playerCompo.isProfilePopinDisplayed() && room.getState() != RoomState.PROFILE_POPIN) {
    		// Popin has just been opened
    		
    		previousState = room.getNextState() != null ? room.getNextState() : room.getState();
    		room.setNextState(RoomState.PROFILE_POPIN);
    		
    		if (table == null) {
    			initTable();
    		}
    		
    		refreshTable();
    		
    		stage.addActor(table);
    
    	}
    	
    	if (!playerCompo.isProfilePopinDisplayed() && room.getState() == RoomState.PROFILE_POPIN) {
    		// Popin has just been closed
    		closePopin();
    	}
    
    	
    	if (room.getState() == RoomState.PROFILE_POPIN) {
            stage.act(Gdx.graphics.getDeltaTime());
    		stage.draw();
    		
    		if (InputSingleton.getInstance().leftClickJustPressed) {
    			closePopin();
    		}
    	}
    }

	private void refreshTable() {
		MoveComponent moveComponent = Mappers.moveComponent.get(player);
		AttackComponent attackComponent = Mappers.attackComponent.get(player);
		HealthComponent healthComponent = Mappers.healthComponent.get(player);

		title.setText("Profile");
		maxHpLbl.setText("Max hp: " + healthComponent.getMaxHp());
		maxArmorLbl.setText("Max armor: " + healthComponent.getMaxArmor());
		strengthLbl.setText("Strength: " + attackComponent.getStrength());
		moveLbl.setText("Move: " + moveComponent.moveSpeed);

		AttackComponent rangeAttackCompo = Mappers.attackComponent.get(playerCompo.getSkillRange());
		rangeDistLbl.setText("Bow range: " + rangeAttackCompo.getRangeMin() + "-" + rangeAttackCompo.getRangeMax());
		rangeStrengthLbl.setText("Bow damage: " + rangeAttackCompo.getStrength());

		AttackComponent bombAttackCompo = Mappers.attackComponent.get(playerCompo.getSkillBomb());
		bombDistLbl.setText("Bomb throw range: " + bombAttackCompo.getRangeMax());
		bombDmg.setText("Bomb damage: " + bombAttackCompo.getStrength());
		bombDuration.setText("Bomb dur.: " + bombAttackCompo.getBombTurnsToExplode() + " turns" );
		bombRadius.setText("Bomb radius: " + bombAttackCompo.getBombRadius());
		
		table.pack();
		table.setPosition(GameScreen.SCREEN_W/2 - table.getWidth()/2, GameScreen.SCREEN_H/2 - table.getHeight()/2);
	}

    
    /**
     * Initialize the table the first time the profile is opened.
     */
	private void initTable() {
		table = new Table();
//	    		table.setDebug(true, true);
		table.setPosition(GameScreen.SCREEN_W/2, GameScreen.SCREEN_H/2);
		//table.setTouchable(Touchable.childrenOnly);
		
		TextureRegionDrawable topBackground = new TextureRegionDrawable(Assets.profile_background);
		table.setBackground(topBackground);
		
		table.align(Align.top);
		
		// TITLE
		title = new Label("Profile", PopinService.hudStyle());
		table.add(title).uniformX().pad(20, 0, 20, 0);
		table.row();
		
		maxHpLbl = new Label("Max hp", PopinService.hudStyle());
		table.add(maxHpLbl).uniformX().left();
		table.row();
		
		maxArmorLbl = new Label("Max armor", PopinService.hudStyle());
		table.add(maxArmorLbl).uniformX().left();
		table.row();
		
		strengthLbl = new Label("Strength", PopinService.hudStyle());
		table.add(strengthLbl).uniformX().left();
		table.row();
		
		moveLbl = new Label("Move", PopinService.hudStyle());
		table.add(moveLbl).uniformX().left().padBottom(20);
		table.row();
		
		
		rangeDistLbl = new Label("Bow range", PopinService.hudStyle());
		table.add(rangeDistLbl).uniformX().left();
		table.row();
		rangeStrengthLbl = new Label("Bow damage", PopinService.hudStyle());
		table.add(rangeStrengthLbl).uniformX().left().padBottom(20);
		table.row();
		
		bombDistLbl = new Label("Bomb throw range", PopinService.hudStyle());
		table.add(bombDistLbl).uniformX().left();
		table.row();
		bombDmg = new Label("Bomb damage", PopinService.hudStyle());
		table.add(bombDmg).uniformX().left();
		table.row();
		
		bombDuration = new Label("Bomb dur.", PopinService.hudStyle());
		table.add(bombDuration).uniformX().left();
		table.row();
		bombRadius = new Label("Bomb radius", PopinService.hudStyle());
		table.add(bombRadius).uniformX().left();
		table.row();
	}

	/**
	 * Close the level up popin and unpause the game.
	 */
	private void closePopin() {
		playerCompo.setProfilePopinDisplayed(false);
		table.remove();
		room.setNextState(previousState);
	}

}
