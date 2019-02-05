package com.dokkaebistudio.tacticaljourney.rendering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.player.ExperienceComponent;
import com.dokkaebistudio.tacticaljourney.leveling.LevelUpRewardEnum;
import com.dokkaebistudio.tacticaljourney.rendering.interfaces.Renderer;
import com.dokkaebistudio.tacticaljourney.rendering.poolables.PoolableImage;
import com.dokkaebistudio.tacticaljourney.rendering.poolables.PoolableLabel;
import com.dokkaebistudio.tacticaljourney.rendering.poolables.PoolableStack;
import com.dokkaebistudio.tacticaljourney.rendering.poolables.PoolableTable;
import com.dokkaebistudio.tacticaljourney.rendering.poolables.PoolableTextButton;
import com.dokkaebistudio.tacticaljourney.rendering.poolables.PoolableTextureRegionDrawable;
import com.dokkaebistudio.tacticaljourney.rendering.service.PopinService;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class LevelUpPopinRenderer implements Renderer, RoomSystem {
	    
	public Stage stage;
	
	private Entity player;
	/** The experience component of the player (kept in cache to prevent getting it at each frame). */
	private ExperienceComponent expCompo;
	
	/** The current room. */
    private Room room;
    
    /** The buttons to select the level up reward. */
    private List<LevelUpRewardEnum> rewards = new ArrayList<>();
    
    /** The main table of the popin. */
    private Table table;

    private List<TextButton> claimButtons = new ArrayList<>();
    
    private TextButton continueButton;
    
    /** The state before the level up state. */
    private RoomState previousState;
    
    public LevelUpPopinRenderer(Room r, Stage s, Entity p) {
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
    	
    	if (expCompo == null) {
    		expCompo = Mappers.experienceComponent.get(player);
    	}
    	
    	if (expCompo.isLevelUpPopinDisplayed()) {

    		if (table == null) {
	    		previousState = room.getNextState() != null ? room.getNextState() : room.getState();
	    		room.setNextState(RoomState.LEVEL_UP_POPIN);
	    		
	    		table = PoolableTable.create();
	    		table.setPosition(GameScreen.SCREEN_W/2, GameScreen.SCREEN_H/2);
	    		table.setTouchable(Touchable.childrenOnly);
	    		
	    		TextureRegionDrawable background = PoolableTextureRegionDrawable.create(Assets.getTexture(Assets.lvl_up_background));
	    		table.setBackground(background);

	    		
	    		// TOP PART (labels)
	    		createPopinTop(expCompo, table);
	        	
	        	//CHOICES (choices description and claim button)
	    		createChoices(player, expCompo.getChoicesNumber(), table);
	        	
	        	// BOTTOM PART (continue button)
	    		createPopinBottom(expCompo,table);
	        	
	        	table.pack();
	        	table.setPosition(table.getX() - table.getWidth()/2, table.getY() - table.getHeight()/2 + 100);
	
	    		stage.addActor(table);
    		}
    		
            stage.act(Gdx.graphics.getDeltaTime());
    		stage.draw();

    	}
    
    }


	private void createChoices(final Entity player, int choicesNumber, Table table) {
		List<LevelUpRewardEnum> list = new ArrayList<>(Arrays.asList(LevelUpRewardEnum.values()));
		RandomXS128 unseededRandom = RandomSingleton.getInstance().getUnseededRandom();

		rewards.clear();
		claimButtons.clear();
		
		Table choiceTable = PoolableTable.create();
		
		for (int i=1; i<=choicesNumber ; i++) {
			int nextInt = unseededRandom.nextInt(list.size());
			LevelUpRewardEnum reward = list.get(nextInt);
			list.remove(nextInt);
			rewards.add(reward);
		}
		
		
		for (int i=0 ; i<rewards.size() ; i++) {
			final LevelUpRewardEnum levelUpRewardEnum = rewards.get(i);
			Stack choiceGroup = PoolableStack.create();
			
			Stack rewardPanelGroup = PoolableStack.create();
			Image rewardTextBackground = PoolableImage.create(Assets.getTexture(Assets.lvl_up_choice_reward_panel));
			Table rewardTable = PoolableTable.create();
			Label rewardText = PoolableLabel.create(levelUpRewardEnum.getFinalDescription(), PopinService.smallTextStyle());
			rewardText.setAlignment(Align.center);
			rewardTable.add(rewardText).width(Value.percentWidth(.70F, rewardTable));
			rewardTable.add().width(Value.percentWidth(.30F, rewardTable));
			rewardPanelGroup.addActor(rewardTextBackground);
			rewardPanelGroup.addActor(rewardTable);
			choiceGroup.addActor(rewardPanelGroup);

			final Stack descPanelGroup = PoolableStack.create();
			Image descTextBackground = PoolableImage.create(Assets.getTexture(Assets.lvl_up_choice_desc_panel));
			Table descTable = PoolableTable.create();
			Label descText = PoolableLabel.create(levelUpRewardEnum.getDescription(), PopinService.smallTextStyle());
			descText.setAlignment(Align.center);
			descTable.add(descText).width(Value.percentWidth(.70F, descTable));
			descTable.add().width(Value.percentWidth(.30F, descTable));
			descPanelGroup.addActor(descTextBackground);
			descPanelGroup.addActor(descTable);
			choiceGroup.addActor(descPanelGroup);
			
			
			
			Table frameTable = PoolableTable.create();

			TextureRegionDrawable frame = PoolableTextureRegionDrawable.create(Assets.getTexture(Assets.lvl_up_choice_frame));
			frameTable.setBackground(frame);
			
			final TextButton claimButton = PoolableTextButton.create("Claim", PopinService.smallButtonStyle());
			claimButtons.add(claimButton);
			
			// continueButton listener
			claimButton.addListener(new ChangeListener() {
				
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					claimReward(player, levelUpRewardEnum, descPanelGroup, claimButton);
				}
			});
			
			frameTable.add().width(Value.percentWidth(.70F, descTable));
			frameTable.add(claimButton).center();
			choiceGroup.addActor(frameTable);
			
			choiceTable.add(choiceGroup).padTop(-1);
			choiceTable.row();
		
		}
		
		choiceTable.pack();
		
		table.add(choiceTable).expandY().growX();
		table.row();
	
	}

	private void createPopinTop(ExperienceComponent expCompo, Table table) {
		Table popinTop = PoolableTable.create();
		
		LabelStyle titleStyle = new LabelStyle(Assets.font, Color.WHITE);
		Label titleLabel = PoolableLabel.create("[GREEN]LEVEL UP",titleStyle);
		popinTop.add(titleLabel).top().uniformX().pad(20, 0, 10, 0);
		
		popinTop.row();
		
		LabelStyle subtitleStyle = new LabelStyle(Assets.font, Color.WHITE);
		Label subTitleLabel = PoolableLabel.create("Congratulations",subtitleStyle);
		popinTop.add(subTitleLabel).top().uniformX();

		popinTop.row();

		Label subTitle2Label = PoolableLabel.create("you reached level [GREEN]" + expCompo.getLevelForPopin(),subtitleStyle);
		popinTop.add(subTitle2Label).top().uniformX().pad(0, 0, 10, 0);
		
		popinTop.row();
		
		Label selectChoiceLabel = PoolableLabel.create("Select [GREEN]1[WHITE] reward bellow",subtitleStyle);
		popinTop.add(selectChoiceLabel).top().uniformX().padBottom(20);
		
		table.add(popinTop).fillX().uniformX();
		table.row();
	}
	
	

	private void createPopinBottom(final ExperienceComponent expCompo, Table table) {
		Table popinBottom = PoolableTable.create();
		popinBottom.setZIndex(10);
		table.add(popinBottom).fillX().uniformX();
		
		continueButton = PoolableTextButton.create("Skip", PopinService.bigButtonStyle());
		continueButton.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				// Close the popin and unpause the game
				closePopin(expCompo);
			}
		});
		popinBottom.add(continueButton).pad(10, 0, 20, 0);

	}
	
	
	/**
	 * Close the level up popin and unpause the game.
	 */
	private void closePopin(ExperienceComponent expCompo) {
		stage.clear();
		table = null;
		room.setNextState(previousState);
		expCompo.setLevelUpPopinDisplayed(false);
		expCompo.setNumberOfNewLevelReached(expCompo.getNumberOfNewLevelReached() - 1);
	}
	
	/**
	 * Claim the given reward, i.e. apply the reward to the player, and disable any other reward.
	 * @param player the player
	 * @param levelUpRewardEnum the reward to claim
	 * @param descPanelGroup the panel to fade out
	 * @param claimButton the claim button just pressed
	 */
	private void claimReward(final Entity player, final LevelUpRewardEnum levelUpRewardEnum,
			final Stack descPanelGroup, final Button claimButton) {
		continueButton.setText("Continue");
		continueButton.setTouchable(Touchable.disabled);
		descPanelGroup.addAction(Actions.sequence(Actions.alpha(0, 1), new ApplyRewardAction(player, levelUpRewardEnum)));
		
		for(Button cb : claimButtons) {
			if (cb != claimButton) {
				cb.addAction(Actions.hide());
			}
		}
		claimButton.setTouchable(Touchable.disabled);
		claimButton.setDisabled(true);
	}
	
	
	private class ApplyRewardAction extends Action {
		
		private Entity player;
		private LevelUpRewardEnum reward;
		
		public ApplyRewardAction(Entity player, LevelUpRewardEnum levelUpRewardEnum) {
			this.player = player;
			this.reward = levelUpRewardEnum;
		}
		
	    public boolean act (float delta) {
			reward.select(player);
			continueButton.setTouchable(Touchable.enabled);
	        return true; // An action returns true when it's completed 
	    }
	}

}
