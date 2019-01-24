package com.dokkaebistudio.tacticaljourney.systems;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.InputSingleton;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.player.ExperienceComponent;
import com.dokkaebistudio.tacticaljourney.leveling.LevelUpRewardEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class ExperienceSystem extends IteratingSystem implements RoomSystem {
	    
	public Stage stage;
	
	/** The current room. */
    private Room room;
    
    /** The buttons to select the level up reward. */
    private List<LevelUpRewardEnum> rewards = new ArrayList<>();

    /** The state before the level up state. */
    private RoomState previousState;
    
    
    private boolean test = true;

    public ExperienceSystem(Room r, Stage s) {
        super(Family.all(ExperienceComponent.class).get());
        room = r;
        this.stage = s;
    }
    
    @Override
    public void enterRoom(Room newRoom) {
    	this.room = newRoom;	
    }

    @Override
    protected void processEntity(final Entity player, float deltaTime) {
    	
    	ExperienceComponent expCompo = Mappers.experienceComponent.get(player);
    	int choicesNumber = expCompo.getChoicesNumber();
    	
    	//TODO remove
    	if (test) {
    		test = false;
    		expCompo.earnXp(10);
    	}
    	
    	if (expCompo.hasLeveledUp()) {
    		expCompo.setLeveledUp(false);
    		previousState = room.state;
    		room.state = RoomState.LEVEL_UP_POPIN;
    		
    		
    		Gdx.input.setInputProcessor(stage);

    		
    		Table table = new Table();
    		stage.addActor(table);

    		table.setPosition(GameScreen.SCREEN_W/2, GameScreen.SCREEN_H/2);
    		table.setTouchable(Touchable.childrenOnly);
    		
    		
    		// TOP PART
    		Table popinTop = new Table();
    		TextureRegionDrawable topBackground = new TextureRegionDrawable(Assets.getTexture(Assets.lvl_up_background_top));
    		popinTop.setBackground(topBackground);
    		
    		LabelStyle titleStyle = new LabelStyle(Assets.font, Color.WHITE);
    		Label titleLabel = new Label("LEVEL UP",titleStyle);
    		popinTop.add(titleLabel).top().uniformX().pad(0, 0, 10, 0);
    		
    		popinTop.row();
    		
    		LabelStyle subtitleStyle = new LabelStyle(Assets.font, Color.WHITE);
    		Label subTitleLabel = new Label("Congratulations",subtitleStyle);
    		popinTop.add(subTitleLabel).top().uniformX();

    		popinTop.row();

    		Label subTitle2Label = new Label("you reached level " + expCompo.getLevel(),subtitleStyle);
    		popinTop.add(subTitle2Label).top().uniformX();
    		
        	table.add(popinTop).fillX().uniformX().padTop(-1);
        	table.row();
        	
        	
        	//CHOICES
    		List<LevelUpRewardEnum> list = new ArrayList<>(Arrays.asList(LevelUpRewardEnum.values()));
    		RandomXS128 unseededRandom = RandomSingleton.getInstance().getUnseededRandom();

    		for (int i=1; i<=choicesNumber ; i++) {
	    		int nextInt = unseededRandom.nextInt(list.size());
	    		LevelUpRewardEnum reward = list.get(nextInt);
	    		list.remove(nextInt);
	    		rewards.add(reward);
    		}
        	
    		
    		for (int i=0 ; i<rewards.size() ; i++) {
    			final LevelUpRewardEnum levelUpRewardEnum = rewards.get(i);
	    		LabelStyle smallTitleStyle = new LabelStyle(Assets.smallFont, Color.WHITE);
	        	Stack choiceGroup = new Stack();
	        	
	        	Stack rewardPanelGroup = new Stack();
	        	Image rewardTextBackground = new Image(Assets.getTexture(Assets.lvl_up_choice_reward_panel));
	        	Table rewardTable = new Table();
	    		Label rewardText = new Label(levelUpRewardEnum.getFinalDescription(),smallTitleStyle);
	    		rewardTable.add(rewardText).left().pad(0, 20, 0, 0);
	    		rewardPanelGroup.addActor(rewardTextBackground);
	    		rewardPanelGroup.addActor(rewardTable);
	        	choiceGroup.addActor(rewardPanelGroup);
	
	        	final Stack descPanelGroup = new Stack();
	        	Image descTextBackground = new Image(Assets.getTexture(Assets.lvl_up_choice_desc_panel));
	        	Table descTable = new Table();
	    		Label descText = new Label(levelUpRewardEnum.getDescription(),smallTitleStyle);
	    		descText.setAlignment(Align.center);
	    		descTable.add(descText).width(Value.percentWidth(.85F, descTable));
	    		descTable.add().width(Value.percentWidth(.15F, descTable));
	    		descPanelGroup.addActor(descTextBackground);
	    		descPanelGroup.addActor(descTable);
	        	choiceGroup.addActor(descPanelGroup);
	        	
	        	
	        	
	        	Table frameTable = new Table();
	    		TextureRegionDrawable frame = new TextureRegionDrawable(Assets.getTexture(Assets.lvl_up_choice_frame));
	    		frameTable.setBackground(frame);
	    		
	    		Drawable claimButtonUp = new SpriteDrawable(new Sprite(Assets.getTexture(Assets.lvl_up_choice_claim_btn)));
	    		Drawable claimButtonDown = new SpriteDrawable(new Sprite(Assets.getTexture(Assets.lvl_up_choice_claim_btn_pushed)));
	    		ButtonStyle claimButtonStyle = new ButtonStyle(claimButtonUp, claimButtonDown, null);
	        	Button claimButton = new Button(claimButtonStyle);
	        	
	    		// continueButton listener
	        	claimButton.addListener(new ClickListener() {
	
	    			@Override
	    			public void clicked(InputEvent event, float x, float y) {
	    				descPanelGroup.addAction(Actions.alpha(0, 2));
	    				
	    	    		// Apply the reward and close the notification popin
	    	    		levelUpRewardEnum.select(player);
	    			}
	    		});
	    		
	        	frameTable.add().width(Value.percentWidth(.85F, descTable));
	        	frameTable.add(claimButton);
	        	choiceGroup.addActor(frameTable);
	        	
	        	table.add(choiceGroup).fillX().uniformX().padTop(-1);
	        	table.row();
        	
    		}
        	
        	
        	// BOTTOM PART
    		Table popinBottom = new Table();
    		popinBottom.setZIndex(10);
    		TextureRegionDrawable bottomBackground = new TextureRegionDrawable(Assets.getTexture(Assets.lvl_up_background_bottom));
    		popinBottom.setBackground(bottomBackground);
        	table.add(popinBottom).fillX().uniformX().padTop(-1);
        	
    		Drawable continueButtonUp = new SpriteDrawable(new Sprite(Assets.getTexture(Assets.lvl_up_continue_btn)));
    		Drawable continueButtonDown = new SpriteDrawable(new Sprite(Assets.getTexture(Assets.lvl_up_continue_btn_pushed)));
    		ButtonStyle continueButtonStyle = new ButtonStyle(continueButtonUp, continueButtonDown, null);
        	Button continueButton = new Button(continueButtonStyle);
        	popinBottom.add(continueButton);
        	
    		// continueButton listener
        	continueButton.addListener(new ClickListener() {

    			@Override
    			public void clicked(InputEvent event, float x, float y) {
		    		// Clear all entities of the popin
    				stage.clear();
    				room.state = previousState;
    				InputSingleton.getInstance().initInputProcessor();
    			}
    		});

        	
        	table.pack();
        	table.setPosition(table.getX() - table.getWidth()/2, table.getY() - table.getHeight()/2 + 100);
    		
    		
    	}
    	
    	
    	if (room.state == RoomState.LEVEL_UP_POPIN) {
            stage.act(Gdx.graphics.getDeltaTime());
    		stage.draw();
    	}
    
    }

}
