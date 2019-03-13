package com.dokkaebistudio.tacticaljourney.rendering;

import java.util.ArrayList;
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
import com.dokkaebistudio.tacticaljourney.leveling.LevelUpReward;
import com.dokkaebistudio.tacticaljourney.rendering.interfaces.Renderer;
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
    private List<LevelUpReward> rewards = new ArrayList<>();
    
    /** The main table of the popin. */
    private Table table;

    private List<TextButton> claimButtons = new ArrayList<>();
    
    private TextButton continueButton;
    
    /** The number of rewards selected. */
    private int selectedRewards = 0;
    
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
	    		room.setNextState(RoomState.LEVEL_UP_POPIN);
	    		
	    		table = new Table();
	    		table.setPosition(GameScreen.SCREEN_W/2, GameScreen.SCREEN_H/2);
	    		table.setTouchable(Touchable.childrenOnly);
	    		
	    		TextureRegionDrawable background = new TextureRegionDrawable(Assets.lvl_up_background);
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
		this.selectedRewards = 0;
		
		List<LevelUpReward> list = LevelUpReward.getRewards(expCompo.getLevelForPopin(), choicesNumber);
		if (list.size() == 0) return;
		
		RandomXS128 unseededRandom = RandomSingleton.getInstance().getUnseededRandom();

		rewards.clear();
		claimButtons.clear();
		
		Table choiceTable = new Table();
		
		for (int i=1; i<=choicesNumber ; i++) {
			if (!list.isEmpty()) {
				int nextInt = unseededRandom.nextInt(list.size());
				LevelUpReward reward = list.get(nextInt);
				list.remove(nextInt);
				rewards.add(reward);
			}
		}
		
		
		for (int i=0 ; i<rewards.size() ; i++) {
			final LevelUpReward levelUpRewardEnum = rewards.get(i);
			Stack choiceGroup = new Stack();
			
			Stack rewardPanelGroup = new Stack();
			Image rewardTextBackground = new Image(Assets.lvl_up_choice_reward_panel);
			Table rewardTable = new Table();
			Label rewardText = new Label(levelUpRewardEnum.getFinalDescription(), PopinService.smallTextStyle());
			rewardText.setAlignment(Align.center);
			rewardTable.add(rewardText).width(Value.percentWidth(.70F, rewardTable));
			rewardTable.add().width(Value.percentWidth(.30F, rewardTable));
			rewardPanelGroup.addActor(rewardTextBackground);
			rewardPanelGroup.addActor(rewardTable);
			choiceGroup.addActor(rewardPanelGroup);

			final Stack descPanelGroup = new Stack();
			Image descTextBackground = new Image(Assets.lvl_up_choice_desc_panel);
			Table descTable = new Table();
			Label descText = new Label(levelUpRewardEnum.getDescription(), PopinService.smallTextStyle());
			descText.setAlignment(Align.center);
			descTable.add(descText).width(Value.percentWidth(.70F, descTable));
			descTable.add().width(Value.percentWidth(.30F, descTable));
			descPanelGroup.addActor(descTextBackground);
			descPanelGroup.addActor(descTable);
			choiceGroup.addActor(descPanelGroup);
			
			
			
			Table frameTable = new Table();

			TextureRegionDrawable frame = new TextureRegionDrawable(Assets.lvl_up_choice_frame);
			frameTable.setBackground(frame);
			
			final TextButton claimButton = new TextButton("Claim", PopinService.smallButtonStyle());
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
		Table popinTop = new Table();
		
		LabelStyle titleStyle = new LabelStyle(Assets.font, Color.WHITE);
		Label titleLabel = new Label("[GREEN]LEVEL UP",titleStyle);
		popinTop.add(titleLabel).top().uniformX().pad(20, 0, 10, 0);
		
		popinTop.row();
		
		LabelStyle subtitleStyle = new LabelStyle(Assets.font, Color.WHITE);
		Label subTitleLabel = new Label("Congratulations",subtitleStyle);
		popinTop.add(subTitleLabel).top().uniformX();

		popinTop.row();

		Label subTitle2Label = new Label("you reached level [GREEN]" + expCompo.getLevelForPopin(),subtitleStyle);
		popinTop.add(subTitle2Label).top().uniformX().pad(0, 0, 10, 0);
		
		popinTop.row();
		
		Label selectChoiceLabel = new Label("Select [GREEN]" + expCompo.getSelectNumber() + "[WHITE] reward bellow",subtitleStyle);
		popinTop.add(selectChoiceLabel).top().uniformX().padBottom(20);
		
		table.add(popinTop).fillX().uniformX();
		table.row();
	}
	
	

	private void createPopinBottom(final ExperienceComponent expCompo, Table table) {
		Table popinBottom = new Table();
		table.add(popinBottom).fillX().uniformX();
		
		continueButton = new TextButton("Skip", PopinService.bigButtonStyle());
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
		room.setNextState(room.getLastInGameState());
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
	private void claimReward(final Entity player, final LevelUpReward levelUpRewardEnum,
			final Stack descPanelGroup, final Button claimButton) {
		continueButton.setText("Continue");
		continueButton.setTouchable(Touchable.disabled);
		descPanelGroup.addAction(Actions.sequence(Actions.alpha(0, 1), new ApplyRewardAction(player, levelUpRewardEnum)));
		
		for(Button cb : claimButtons) {
			cb.setDisabled(true);
		}
		claimButton.setTouchable(Touchable.disabled);
		
		claimButton.addAction(Actions.hide());
//		claimButton.setDisabled(true);
	}
	
	
	private class ApplyRewardAction extends Action {
		
		private Entity player;
		private LevelUpReward reward;
		
		public ApplyRewardAction(Entity player, LevelUpReward levelUpRewardEnum) {
			this.player = player;
			this.reward = levelUpRewardEnum;
		}
		
	    public boolean act (float delta) {
			reward.select(player, room);
			continueButton.setTouchable(Touchable.enabled);
			
			if (++selectedRewards < expCompo.getSelectNumber()) {
				for(Button cb : claimButtons) {
					cb.setDisabled(false);
				}
			}
	        return true; // An action returns true when it's completed 
	    }
	}

}
