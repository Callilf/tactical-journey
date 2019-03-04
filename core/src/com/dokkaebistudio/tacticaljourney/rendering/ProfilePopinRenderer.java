package com.dokkaebistudio.tacticaljourney.rendering;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.InputSingleton;
import com.dokkaebistudio.tacticaljourney.alterations.Alteration;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.BlessingCelerity;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.BlessingStrength;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.BlessingVigor;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AlterationReceiverComponent;
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
	/** The player's alteration receiver component in cache. */
	private AlterationReceiverComponent alterationReceiverCompo;
	
	/** The current room. */
    private Room room;
    
    
    //*****************
    // Actors 
    
    private Table mainTable;
    
    // Profile
    
    /** The main table of the popin. */
    private Table profileTable;
    private Label profileTitle;
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
	
	// Blessings
    private Table blessingTable;
    private ScrollPane blessingScroll;
    private Table blessingList;
    private Label blessingTitle;

	
	// Curses
    private Table curseTable;
    private ScrollPane curseScroll;
    private Table curseList;
    private Label curseTitle;


    
    
    
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
    	if (alterationReceiverCompo == null) {
    		alterationReceiverCompo = Mappers.alterationReceiverComponent.get(player);
    	}
    	
    	if (playerCompo.isProfilePopinDisplayed() && room.getState() != RoomState.PROFILE_POPIN) {
    		// Popin has just been opened
    		
    		previousState = room.getNextState() != null ? room.getNextState() : room.getState();
    		room.setNextState(RoomState.PROFILE_POPIN);
    		
    		if (mainTable == null) {
    			mainTable = new Table();
    			initProfileTable();
    			initBlessingTable();
    			initCurseTable();
    		}
    		
    		refreshProfileTable();
    		refreshBlessingTable();
    		refreshCurseTable();
    		mainTable.pack();
    		mainTable.setPosition(GameScreen.SCREEN_W/2 - mainTable.getWidth()/2, GameScreen.SCREEN_H/2 - mainTable.getHeight()/2);
    		
    		stage.addActor(mainTable);
    
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
    
    
    
    //************************
    // PROFILE

	private void refreshProfileTable() {
		MoveComponent moveComponent = Mappers.moveComponent.get(player);
		AttackComponent attackComponent = Mappers.attackComponent.get(player);
		HealthComponent healthComponent = Mappers.healthComponent.get(player);

		profileTitle.setText("Profile");
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
		bombDuration.setText("Bomb fuse duration: " + bombAttackCompo.getBombTurnsToExplode() + " turns" );
		bombRadius.setText("Bomb radius: " + bombAttackCompo.getBombRadius());
		
		profileTable.pack();
	}

    
    /**
     * Initialize the table the first time the profile is opened.
     */
	private void initProfileTable() {
		profileTable = new Table();
		
		TextureRegionDrawable topBackground = new TextureRegionDrawable(Assets.profile_background);
		profileTable.setBackground(topBackground);
		
		profileTable.align(Align.top);
		
		// TITLE
		profileTitle = new Label("Profile", PopinService.hudStyle());
		profileTable.add(profileTitle).expandX().pad(20, 0, 20, 0);
		profileTable.row();
		
		maxHpLbl = new Label("Max hp", PopinService.hudStyle());
		profileTable.add(maxHpLbl).expandX().left().pad(0, 20, 0, 20);
		profileTable.row();
		
		maxArmorLbl = new Label("Max armor", PopinService.hudStyle());
		profileTable.add(maxArmorLbl).expandX().left().pad(0, 20, 0, 20);
		profileTable.row();
		
		strengthLbl = new Label("Strength", PopinService.hudStyle());
		profileTable.add(strengthLbl).expandX().left().pad(0, 20, 0, 20);
		profileTable.row();
		
		moveLbl = new Label("Move", PopinService.hudStyle());
		profileTable.add(moveLbl).expandX().left().pad(0, 20, 20, 20);
		profileTable.row();
		
		
		rangeDistLbl = new Label("Bow range", PopinService.hudStyle());
		profileTable.add(rangeDistLbl).expandX().left().pad(0, 20, 0, 20);
		profileTable.row();
		rangeStrengthLbl = new Label("Bow damage", PopinService.hudStyle());
		profileTable.add(rangeStrengthLbl).expandX().left().pad(0, 20, 20, 20);
		profileTable.row();
		
		bombDistLbl = new Label("Bomb throw range", PopinService.hudStyle());
		profileTable.add(bombDistLbl).expandX().left().pad(0, 20, 0, 20);
		profileTable.row();
		bombDmg = new Label("Bomb damage", PopinService.hudStyle());
		profileTable.add(bombDmg).expandX().left().pad(0, 20, 0, 20);
		profileTable.row();
		
		bombDuration = new Label("Bomb fuse duration", PopinService.hudStyle());
		profileTable.add(bombDuration).expandX().left().pad(0, 20, 0, 20);
		profileTable.row();
		bombRadius = new Label("Bomb radius", PopinService.hudStyle());
		profileTable.add(bombRadius).expandX().left().pad(0, 20, 0, 20);
		profileTable.row();
		
		mainTable.add(profileTable);
	}

	
	
	//*****************
	// BLESSINGS
	

	private void refreshBlessingTable() {
		blessingList.clear();
		for (Alteration blessing : alterationReceiverCompo.getBlessings()) {
			Table oneBlessingTable = createOneAlteration(blessing);
			blessingList.add(oneBlessingTable).fillX().pad(0, 15, 15, 15);
			blessingList.row();
		}
				
		blessingList.pack();
		blessingScroll.layout();
	}

    
    /**
     * Initialize the blessing table the first time the profile is opened.
     */
	private void initBlessingTable() {
		blessingTable = new Table();

		TextureRegionDrawable topBackground = new TextureRegionDrawable(Assets.profile_background);
		blessingTable.setBackground(topBackground);
		
		blessingTable.align(Align.top);
		
		// TITLE
		blessingTitle = new Label("Blessings", PopinService.hudStyle());
		blessingTable.add(blessingTitle).uniformX().pad(20, 0, 20, 0);
		blessingTable.row().top();
		
		// Blessings table
		blessingList = new Table();
		blessingList.top().left();
		
		//Scrollpane
		blessingScroll = new ScrollPane(blessingList);
		blessingTable.add(blessingScroll).fill().expand().maxHeight(590);
		
		mainTable.add(blessingTable).pad(0, 5, 0, 5);
	}
	
	
	
	// *****************
	// BLESSINGS

	private void refreshCurseTable() {
		curseList.clear();
		for (Alteration curse : alterationReceiverCompo.getCurses()) {
			Table oneCurseTable = createOneAlteration(curse);
			curseList.add(oneCurseTable).fillX().pad(0, 15, 15, 15);
			curseList.row();
		}
		
		curseList.pack();
		curseScroll.layout();
	}

	/**
	 * Initialize the blessing table the first time the profile is opened.
	 */
	private void initCurseTable() {
		curseTable = new Table();

		TextureRegionDrawable topBackground = new TextureRegionDrawable(Assets.profile_background);
		curseTable.setBackground(topBackground);

		curseTable.align(Align.top);

		// TITLE
		curseTitle = new Label("Curses", PopinService.hudStyle());
		curseTable.add(curseTitle).uniformX().pad(20, 0, 20, 0);
		curseTable.row().top();

		// Blessings table
		curseList = new Table();
		curseList.top().left();

		// Scrollpane
		curseScroll = new ScrollPane(curseList);
		curseTable.add(curseScroll).fill().expand().maxHeight(590);

		mainTable.add(curseTable).pad(0, 5, 0, 5);
	}
	
	
	
	
	
	

	private Table createOneAlteration(Alteration alteration) {
		Table oneCurseTable = new Table();

		TextureRegionDrawable background = new TextureRegionDrawable(Assets.profile_alteration_background);
		oneCurseTable.setBackground(background);
		
		oneCurseTable.align(Align.left);

		Table upTable = new Table();
		upTable.align(Align.left);
		Image curseImage = new Image(alteration.texture());
		upTable.add(curseImage).left().pad(5, 5, 0, 10);
		
		Label curseTitle = new Label(alteration.title(), PopinService.hudStyle());
		upTable.add(curseTitle).pad(5, 10, 0, 5);
		upTable.pack();
		oneCurseTable.add(upTable).left();
		
		oneCurseTable.row();
		Label desc = new Label(alteration.description(), PopinService.smallTextStyle());
		desc.setAlignment(Align.left);
		desc.setWidth(Assets.profile_alteration_background.getRegionWidth() - 20);
		desc.setWrap(true);
		oneCurseTable.add(desc).width(Assets.profile_alteration_background.getRegionWidth() - 20).pad(0, 5, 5, 5);
		
		oneCurseTable.pack();
		return oneCurseTable;
	}
	
	
	
	
	//*************
	// Close
	/**
	 * Close the level up popin and unpause the game.
	 */
	private void closePopin() {
		playerCompo.setProfilePopinDisplayed(false);
		mainTable.remove();
		room.setNextState(previousState);
	}

}
