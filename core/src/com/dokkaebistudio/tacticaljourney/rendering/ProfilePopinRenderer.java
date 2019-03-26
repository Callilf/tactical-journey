package com.dokkaebistudio.tacticaljourney.rendering;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.InputSingleton;
import com.dokkaebistudio.tacticaljourney.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.alterations.Alteration;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AlterationReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.player.ExperienceComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.enums.DamageType;
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
    private Label accuracyLbl;
	private Label moveLbl;
	private Label rangeDistLbl;
	private Label rangeStrengthLbl;
	private Label rangeAccuracyLbl;
	private Label bombDistLbl;
	private Label bombDmg;
	private Label bombDuration;
	private Label bombRadius;
	
	// Resistances
	private Label poisonResist;
	private Label fireResist;
	private Label explosionResist;
	
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
    		
    		room.setNextState(RoomState.PROFILE_POPIN);
    		
    		if (mainTable == null) {
    			mainTable = new Table();
    			initProfileTable();
    			initBlessingTable();
    			initCurseTable();
    			
    			// Close popin with ESCAPE
	    		stage.addListener(new InputListener() {
					@Override
					public boolean keyUp(InputEvent event, int keycode) {
						if (room.getState() == RoomState.PROFILE_POPIN && keycode == Input.Keys.ESCAPE) {
							closePopin();
							return true;
						}
						return super.keyUp(event, keycode);
					}
				});
    		}
    		
    		alterationReceiverCompo.sort();
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
		moveLbl.setText("Move: " + moveComponent.getMoveSpeed());
		strengthLbl.setText("Strength: " + attackComponent.getStrength());
		accuracyLbl.setText("Melee accuracy: " + attackComponent.getAccuracy());

		AttackComponent rangeAttackCompo = Mappers.attackComponent.get(playerCompo.getSkillRange());
		rangeDistLbl.setText("Bow range: " + rangeAttackCompo.getRangeMin() + "-" + rangeAttackCompo.getRangeMax());
		rangeStrengthLbl.setText("Bow damage: " + rangeAttackCompo.getStrength());
		rangeAccuracyLbl.setText("Bow accuracy: " + rangeAttackCompo.getAccuracy());

		AttackComponent bombAttackCompo = Mappers.attackComponent.get(playerCompo.getSkillBomb());
		bombDistLbl.setText("Bomb throw range: " + bombAttackCompo.getRangeMax());
		bombDmg.setText("Bomb damage: " + bombAttackCompo.getStrength());
		bombDuration.setText("Bomb fuse duration: " + bombAttackCompo.getBombTurnsToExplode() + " turns" );
		bombRadius.setText("Bomb radius: " + bombAttackCompo.getBombRadius());
		
		poisonResist.setText("[PURPLE]Poison[] resistance: " + healthComponent.getResistance(DamageType.POISON) + "%");
		fireResist.setText("[ORANGE]Fire[] resistance: " + healthComponent.getResistance(DamageType.FIRE) + "%");
		explosionResist.setText("[RED]Explosion[] resistance: " + healthComponent.getResistance(DamageType.EXPLOSION) + "%");
		
		profileTable.pack();
	}

    
    /**
     * Initialize the table the first time the profile is opened.
     */
	private void initProfileTable() {
		profileTable = new Table();
		
		NinePatchDrawable ninePatchDrawable = new NinePatchDrawable(Assets.popinNinePatch);
		ninePatchDrawable.setMinWidth(500);
		ninePatchDrawable.setMinHeight(680);
		profileTable.setBackground(ninePatchDrawable);
		
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
		
		moveLbl = new Label("Move", PopinService.hudStyle());
		profileTable.add(moveLbl).expandX().left().pad(0, 20, 0, 20);
		profileTable.row();
		
		strengthLbl = new Label("Melee Strength", PopinService.hudStyle());
		profileTable.add(strengthLbl).expandX().left().pad(0, 20, 0, 20);
		profileTable.row();
		
		accuracyLbl = new Label("Melee accuracy", PopinService.hudStyle());
		profileTable.add(accuracyLbl).expandX().left().pad(0, 20, 20, 20);
		profileTable.row();
		

		
		rangeDistLbl = new Label("Bow range", PopinService.hudStyle());
		profileTable.add(rangeDistLbl).expandX().left().pad(0, 20, 0, 20);
		profileTable.row();
		rangeStrengthLbl = new Label("Bow damage", PopinService.hudStyle());
		profileTable.add(rangeStrengthLbl).expandX().left().pad(0, 20, 0, 20);
		profileTable.row();
		rangeAccuracyLbl = new Label("Bow accuracy", PopinService.hudStyle());
		profileTable.add(rangeAccuracyLbl).expandX().left().pad(0, 20, 20, 20);
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
		profileTable.add(bombRadius).expandX().left().pad(0, 20, 20, 20);
		profileTable.row();
		
		poisonResist = new Label("Poison resistance", PopinService.hudStyle());
		profileTable.add(poisonResist).expandX().left().pad(0,20,0,20);
		profileTable.row();
		fireResist = new Label("Fire resistance", PopinService.hudStyle());
		profileTable.add(fireResist).expandX().left().pad(0,20,0,20);
		profileTable.row();
		explosionResist = new Label("Explosion resistance", PopinService.hudStyle());
		profileTable.add(explosionResist).expandX().left().pad(0,20,0,20);
		profileTable.row();

		mainTable.add(profileTable);
	}

	
	
	//*****************
	// BLESSINGS
	

	private void refreshBlessingTable() {
		blessingList.clear();
		for (Alteration blessing : alterationReceiverCompo.getBlessings()) {
			Table oneBlessingTable = createOneAlteration(blessing);
			blessingList.add(oneBlessingTable).fillX().pad(0, 5, 15, 5);
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

		NinePatchDrawable ninePatchDrawable = new NinePatchDrawable(Assets.popinNinePatch);
		ninePatchDrawable.setMinWidth(500);
		ninePatchDrawable.setMinHeight(680);
		blessingTable.setBackground(ninePatchDrawable);
		
		blessingTable.align(Align.top);
		
		// TITLE
		blessingTitle = new Label("Blessings", PopinService.hudStyle());
		blessingTable.add(blessingTitle).uniformX().pad(20, 0, 20, 0);
		blessingTable.row().top();
		
		// Blessings table
		blessingList = new Table();
		blessingList.top();
		
		//Scrollpane
		blessingScroll = new ScrollPane(blessingList);
		blessingTable.add(blessingScroll).fill().expand().maxHeight(580);
		
		mainTable.add(blessingTable).pad(0, 5, 0, 5);
	}
	
	
	
	// *****************
	// CURSES

	private void refreshCurseTable() {
		curseList.clear();
		for (Alteration curse : alterationReceiverCompo.getCurses()) {
			Table oneCurseTable = createOneAlteration(curse);
			curseList.add(oneCurseTable).fillX().pad(0, 5, 15, 5);
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

		NinePatchDrawable ninePatchDrawable = new NinePatchDrawable(Assets.popinNinePatch);
		ninePatchDrawable.setMinWidth(500);
		ninePatchDrawable.setMinHeight(680);
		curseTable.setBackground(ninePatchDrawable);

		curseTable.align(Align.top);

		// TITLE
		curseTitle = new Label("Curses", PopinService.hudStyle());
		curseTable.add(curseTitle).uniformX().pad(20, 0, 20, 0);
		curseTable.row().top();

		// Blessings table
		curseList = new Table();
		curseList.top();

		// Scrollpane
		curseScroll = new ScrollPane(curseList);
		curseTable.add(curseScroll).fill().expand().maxHeight(580);

		mainTable.add(curseTable);
	}
	
	
	
	
	
	

	private Table createOneAlteration(Alteration alteration) {
		Table oneAlterationTable = new Table();
		Table oneAlterationSubTable = new Table();

		NinePatchDrawable ninePatchDrawable = new NinePatchDrawable(Assets.popinInnerNinePatch);
		ninePatchDrawable.setMinWidth(455);
		ninePatchDrawable.setMinHeight(102);
		oneAlterationSubTable.setBackground(ninePatchDrawable);
		
		oneAlterationSubTable.align(Align.left);

		Table upTable = new Table();
		upTable.align(Align.left);
		Image curseImage = new Image(alteration.texture().getRegion());
		upTable.add(curseImage).left().pad(5, 5, 0, 10);
		
		Label curseTitle = new Label(alteration.title(), PopinService.hudStyle());
		curseTitle.setWrap(true);
		curseTitle.setWidth(350);
		upTable.add(curseTitle).width(350).pad(5, 10, 0, 5);
		
		RegionDescriptor itemSprite = alteration.getItemSprite();
		if (itemSprite == null || alteration.isInfused()) itemSprite = Assets.item_infused_icon;
		Image itemImage = new Image(itemSprite.getRegion());
		upTable.add(itemImage).right().top().pad(-40, -20, -20, -40);
		
		upTable.pack();
		oneAlterationSubTable.add(upTable).left();
		
		oneAlterationSubTable.row();
		Label desc = new Label(alteration.description(), PopinService.smallTextStyle());
		desc.setAlignment(Align.left);
		desc.setWidth(Assets.profile_alteration_background.getRegionWidth() - 10);
		desc.setWrap(true);
		oneAlterationSubTable.add(desc).width(Assets.profile_alteration_background.getRegionWidth() - 10).pad(0, 5, 5, 5);
		
		oneAlterationSubTable.pack();
		
		oneAlterationTable.add(oneAlterationSubTable).pad(15, 0, 0, 15);
		oneAlterationTable.pack();
		return oneAlterationTable;
	}
	
	
	
	
	//*************
	// Close
	/**
	 * Close the level up popin and unpause the game.
	 */
	private void closePopin() {
		playerCompo.setProfilePopinDisplayed(false);
		mainTable.remove();
		room.setNextState(room.getLastInGameState());
	}

}
