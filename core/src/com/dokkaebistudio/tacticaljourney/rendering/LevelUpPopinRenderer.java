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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.dokkaebistudio.tacticaljourney.assets.SceneAssets;
import com.dokkaebistudio.tacticaljourney.components.player.ExperienceComponent;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.leveling.AbstractLevelUpReward;
import com.dokkaebistudio.tacticaljourney.rendering.interfaces.Renderer;
import com.dokkaebistudio.tacticaljourney.rendering.service.PopinService;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class LevelUpPopinRenderer implements Renderer, RoomSystem {
	    
	public Stage stage;
	
	/** The experience component of the player (kept in cache to prevent getting it at each frame). */
	private ExperienceComponent expCompo;
	
	/** The current room. */
    private Room room;
    
    /** The buttons to select the level up reward. */
    private List<AbstractLevelUpReward> rewards = new ArrayList<>();
    
    /** The main table of the popin. */
    private Table table;

    private List<TextButton> claimButtons = new ArrayList<>();
    
    private TextButton continueButton;
    
    /** The number of rewards selected. */
    private int selectedRewards = 0;
    
    
    /** The selected item popin. */
    private Table confirmationPopin;
    private boolean confirmationPopinDisplayed = false;

    
    public LevelUpPopinRenderer(Room r, Stage s) {
        this.room = r;
        this.stage = s;
    }
    
    @Override
    public void enterRoom(Room newRoom) {
    	this.room = newRoom;	
    }

    @Override
	public void render(float deltaTime) {
    	
    	if (expCompo == null) {
    		expCompo = Mappers.experienceComponent.get(GameScreen.player);
    	}
    	
    	if (expCompo.isLevelUpPopinDisplayed()) {

    		if (table == null) {
	    		room.setNextState(RoomState.LEVEL_UP_POPIN);
	    		
	    		table = new Table();
	    		table.setPosition(GameScreen.SCREEN_W/2, GameScreen.SCREEN_H/2);
	    		table.setTouchable(Touchable.childrenOnly);
	    		
	    		NinePatchDrawable ninePatchDrawable = new NinePatchDrawable(SceneAssets.popinNinePatch);
	    		ninePatchDrawable.setMinWidth(600);
	    		ninePatchDrawable.setMinHeight(500);
	    		table.setBackground(ninePatchDrawable);

	    		
	    		// TOP PART (labels)
	    		createPopinTop(expCompo, table);
	        	
	        	//CHOICES (choices description and claim button)
	    		createChoices(GameScreen.player, expCompo.getChoicesNumber(), table);
	        	
	        	// BOTTOM PART (continue button)
	    		createPopinBottom(table);
	        	
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
		
		ExperienceComponent experienceComponent = Mappers.experienceComponent.get(player);
		RandomXS128 levelUpSeededRandom = experienceComponent.getLevelUpSeededRandom();
		
		List<AbstractLevelUpReward> list = AbstractLevelUpReward.getRewards(
				expCompo.getLevelForPopin(), choicesNumber, levelUpSeededRandom);
		if (list.size() == 0) return;

		rewards.clear();
		claimButtons.clear();
		
		Table choiceTable = new Table();
		
		for (int i=1; i<=choicesNumber ; i++) {
			if (!list.isEmpty()) {
				int nextInt = levelUpSeededRandom.nextInt(list.size());
				AbstractLevelUpReward reward = list.get(nextInt);
				list.remove(nextInt);
				rewards.add(reward);
			}
		}
		
		
		for (int i=0 ; i<rewards.size() ; i++) {
			final AbstractLevelUpReward levelUpRewardEnum = rewards.get(i);
			Stack choiceGroup = new Stack();
			
			Table rewardTable = new Table();
    		NinePatchDrawable ninePatchDrawable = new NinePatchDrawable(SceneAssets.popinInnerNinePatch);
    		ninePatchDrawable.setMinWidth(200);
    		ninePatchDrawable.setMinHeight(80);
    		rewardTable.setBackground(ninePatchDrawable);
			
			Label rewardText = new Label(levelUpRewardEnum.getFinalDescription(), PopinService.hudStyle());
			rewardText.setWrap(true);
			rewardText.setAlignment(Align.center);
			rewardTable.add(rewardText).width(500);
			choiceGroup.addActor(rewardTable);

			
			final Table descTable = new Table();
    		ninePatchDrawable = new NinePatchDrawable(SceneAssets.popinOuterNinePatch);
    		ninePatchDrawable.setMinWidth(200);
    		ninePatchDrawable.setMinHeight(80);
    		descTable.setBackground(ninePatchDrawable);

			Label descText = new Label(levelUpRewardEnum.getDescription(), PopinService.hudStyle());
			descText.setWrap(true);
			descText.setAlignment(Align.center);
			descText.setAlignment(Align.center);
			descTable.add(descText).width(500);

			choiceGroup.addActor(descTable);



			Table frameTable = new Table();
			frameTable.add(choiceGroup).left().padRight(20);

			final TextButton claimButton = new TextButton("Claim", PopinService.buttonStyle());
			claimButtons.add(claimButton);
			
			// continueButton listener
			claimButton.addListener(new ChangeListener() {
				
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					claimReward(player, levelUpRewardEnum, descTable, claimButton);
				}
			});
			
			frameTable.add(claimButton).right();
			
			choiceTable.add(frameTable).padBottom(10);
			choiceTable.row();
		
		}
		
		choiceTable.pack();
		
		table.add(choiceTable).expandY().growX().pad(10, 0, 10, 0);
		table.row();
	
	}

	private void createPopinTop(ExperienceComponent expCompo, Table table) {
		Table popinTop = new Table();
		
		LabelStyle titleStyle = new LabelStyle(SceneAssets.font.getFont(), Color.WHITE);
		Label titleLabel = new Label("[GREEN]LEVEL UP",titleStyle);
		popinTop.add(titleLabel).top().uniformX().pad(20, 0, 10, 0);
		
		popinTop.row();
		
		LabelStyle subtitleStyle = new LabelStyle(SceneAssets.font.getFont(), Color.WHITE);
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
	
	

	private void createPopinBottom(Table table) {
		Table popinBottom = new Table();
		table.add(popinBottom).fillX().uniformX();
		
		continueButton = new TextButton("Skip", PopinService.buttonStyle());
		continueButton.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				if ("Skip".equals(continueButton.getText().toString())) {
					displayConfirmationPopin();
				} else {
					// Close the popin and unpause the game
					closePopin();
				}
			}
		});
		popinBottom.add(continueButton).pad(10, 0, 20, 0);

	}
	
	
	
	
	//*********************************
	// Confirmation popin
	
	
	/**
	 * Display the popin of the selected item with it's title, description and possible actions.
	 * @param item the item selected
	 * @param slot the slot on which the item was
	 */
	private void displayConfirmationPopin() {
		if (confirmationPopin == null) {
			confirmationPopin = new Table();
	
			// Add an empty click listener to capture the click so that the InputSingleton doesn't handle it
			confirmationPopin.setTouchable(Touchable.enabled);
			confirmationPopin.addListener(new ClickListener() {});
			
			// Place the popin and add the background texture
			confirmationPopin.setPosition(GameScreen.SCREEN_W/2, GameScreen.SCREEN_H/2);
			NinePatchDrawable ninePatchDrawable = new NinePatchDrawable(SceneAssets.popinNinePatch);
			confirmationPopin.setBackground(ninePatchDrawable);
			
			confirmationPopin.align(Align.top);
			
			// Title
			Label itemTitle = new Label("Are you sure you want to skip?", PopinService.hudStyle());
			confirmationPopin.add(itemTitle).top().align(Align.top).pad(20, 0, 20, 0);
			confirmationPopin.row().align(Align.center);
			
			// Action buttons
			Table buttonTable = new Table();
			
			// Cancel button
			final TextButton closeBtn = new TextButton("Cancel",PopinService.buttonStyle());			
			closeBtn.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					hideSelectedItemPopin();
				}
			});
			buttonTable.add(closeBtn).pad(0, 20,0,20);
	
			// Yes button
			final TextButton yesBtn = new TextButton("Yes",PopinService.buttonStyle());	
			yesBtn.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					closePopin();
				}
			});
			buttonTable.add(yesBtn).pad(0, 20,0,20);
			
			confirmationPopin.add(buttonTable).pad(20, 0, 20, 0);
			
		}
		
		// Place the popin properly
		confirmationPopin.pack();
		confirmationPopin.setPosition(GameScreen.SCREEN_W/2 - confirmationPopin.getWidth()/2, GameScreen.SCREEN_H/2 - confirmationPopin.getHeight()/2);
	
		confirmationPopinDisplayed = true;
		this.stage.addActor(confirmationPopin);

	}
		

	
	private void hideSelectedItemPopin() {
		confirmationPopin.remove();
		confirmationPopinDisplayed = false;
	}
	
	/**
	 * Close the level up popin and unpause the game.
	 */
	private void closePopin() {
		if (confirmationPopinDisplayed) hideSelectedItemPopin();

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
	private void claimReward(final Entity player, final AbstractLevelUpReward levelUpRewardEnum,
			final Table descTable, final Button claimButton) {
		continueButton.setTouchable(Touchable.disabled);
		descTable.addAction(Actions.sequence(Actions.alpha(0, 1), new ApplyRewardAction(player, levelUpRewardEnum)));
		
		for(Button cb : claimButtons) {
			cb.setDisabled(true);
		}
		claimButton.setTouchable(Touchable.disabled);
		
		claimButton.addAction(Actions.hide());
//		claimButton.setDisabled(true);
	}
	
	
	private class ApplyRewardAction extends Action {
		
		private Entity player;
		private AbstractLevelUpReward reward;
		
		public ApplyRewardAction(Entity player, AbstractLevelUpReward levelUpRewardEnum) {
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
			} else {
				continueButton.setText("Continue");
			}
	        return true; // An action returns true when it's completed 
	    }
	}

}
