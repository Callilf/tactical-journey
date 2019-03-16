package com.dokkaebistudio.tacticaljourney.rendering;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.InputSingleton;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.InspectableComponent;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.rendering.interfaces.Renderer;
import com.dokkaebistudio.tacticaljourney.rendering.service.PopinService;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.statuses.Status;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class InspectPopinRenderer implements Renderer, RoomSystem {
	    
	public Stage stage;
	
	private Entity player;
	
	/** The player component (kept in cache to prevent getting it at each frame). */
	private PlayerComponent playerCompo;
	
	/** The current room. */
    private Room room;
    
        
    //**************************
    // Actors
    
    private Table choicePopin;
    private Table choicePopinSubTable;

    
    private Table mainPopin;
    private Label title;
    private Label desc;
    
    private Table bigPopin;
    private Label bigTitle;
    private Label bigDesc;
    private Label bigStats;
	private Table statusTable;
	private Map<Status, Table> statusesMap;

        
    
    
    private boolean needRefresh;
    
    public InspectPopinRenderer(Room r, Stage s, Entity p) {
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
    	
    	if (playerCompo.isInspectPopinRequested() || needRefresh) {
    		room.setNextState(RoomState.INSPECT_POPIN);
    		playerCompo.setInspectPopinRequested(false);

			if (mainPopin == null) {
				initTable();
				initChoiceTable();
				initBigTable();
			}
			
			updateContent();
			
			// Place the popin properly
			mainPopin.pack();
			mainPopin.setPosition(GameScreen.SCREEN_W/2 - mainPopin.getWidth()/2, GameScreen.SCREEN_H/2 - mainPopin.getHeight()/2);

		
			this.stage.addActor(mainPopin);
			this.stage.addActor(bigPopin);
			this.stage.addActor(choicePopin);
			
			playerCompo.clearRequestedAction();
    	}
    	
    	if (room.getState() == RoomState.INSPECT_POPIN) {
    		// Draw the table
            stage.act(Gdx.graphics.getDeltaTime());
    		stage.draw();
    		
    		// Close the inventory on a left click outside the popin
    		if (InputSingleton.getInstance().leftClickJustPressed) {
    			closePopin();
    		}
    	}
	}

	private void updateContent() {
		needRefresh = false;
		mainPopin.setVisible(false);
		choicePopin.setVisible(false);
		bigPopin.setVisible(false);

		if (!playerCompo.getInspectedEntities().isEmpty()) {
			
			if (playerCompo.getInspectedEntities().size() == 1) {
				
				// ONE entity: display the inspect popin
				updateContentForEntity(playerCompo.getInspectedEntities().get(0));
			} else {
				
				// More than ONE entity: display the choice popin
				choicePopin.setVisible(true);
				choicePopinSubTable.clear();
				
				for (final Entity e : playerCompo.getInspectedEntities()) {
					InspectableComponent inspectableComponent = Mappers.inspectableComponentMapper.get(e);
					
					final TextButton btn = new TextButton(inspectableComponent.getTitle(),PopinService.bigButtonStyle());			
					// Close listener
					btn.addListener(new ChangeListener() {
						@Override
						public void changed(ChangeEvent event, Actor actor) {
							playerCompo.clearInspectedEntities();
							playerCompo.getInspectedEntities().add(e);
							needRefresh = true;
						}
					});
					choicePopinSubTable.add(btn).pad(0, 20,5,20);
					choicePopinSubTable.row();
				}
				choicePopinSubTable.pack();
				choicePopin.pack();
				choicePopin.setPosition(GameScreen.SCREEN_W/2 - choicePopin.getWidth()/2, GameScreen.SCREEN_H/2 - choicePopin.getHeight()/2);

			}

		} else {
			
			mainPopin.setVisible(true);
			title.setText("Remains of Nothing");
			desc.setText("There's nothing here...");

		}
	}

	private void updateContentForEntity(Entity entity) {

		InspectableComponent inspectableComponent = Mappers.inspectableComponentMapper.get(entity);
		if (inspectableComponent.isBigPopup()) {
			// BIG popup mode : inspecting an enemy
			bigPopin.setVisible(true);

			bigTitle.setText(inspectableComponent.getTitle());
			EnemyComponent enemyComponent = Mappers.enemyComponent.get(entity);
			if (enemyComponent != null && enemyComponent.isAlerted()) {
				bigTitle.setText(bigTitle.getText() + " ([SCARLET]alerted![])");
			}
				
			bigDesc.setText(inspectableComponent.getDescription());
			
			StringBuilder sb = new StringBuilder();
			HealthComponent healthComponent = Mappers.healthComponent.get(entity);
			if (healthComponent != null) {
				sb.append("Hp: " + healthComponent.getHpColor() + healthComponent.getHp() + "[]/" + healthComponent.getMaxHp());
				sb.append("  -  ");
				sb.append("Armor: " + healthComponent.getArmorColor() + healthComponent.getArmor() + "[]/" + healthComponent.getMaxArmor());
				sb.append("\n");
			}
			
			AttackComponent attackComponent = Mappers.attackComponent.get(entity);
			if (attackComponent != null) {
				sb.append("Strength: " + attackComponent.getStrength());
				sb.append("  -  ");
			}
			
			MoveComponent moveComponent = Mappers.moveComponent.get(entity);
			if (moveComponent != null) {
				sb.append("Move: " + moveComponent.getMoveSpeed());
				sb.append("  -  ");
			}
			
			if (attackComponent != null) {
				if (attackComponent.getRangeMin() != attackComponent.getRangeMax()) {
					sb.append("Range: " + attackComponent.getRangeMin() + "-" + attackComponent.getRangeMax());
				} else {
					sb.append("Range: " + attackComponent.getRangeMin());
				}
				sb.append("\n");
			}
			
			bigStats.setText(sb.toString());
			
			statusTable.clear();
			StatusReceiverComponent statusReceiverComponent = Mappers.statusReceiverComponent.get(entity);
			if (statusReceiverComponent != null) {
				for (Status status : statusReceiverComponent.getStatuses()) {
					Table oneStatusTable  = new Table();
						Image image = new Image(status.fullTexture());
						oneStatusTable.add(image);
						oneStatusTable.row();
						Label dur = new Label(status.getDurationString(), PopinService.hudStyle());
						oneStatusTable.add(dur).bottom();
						statusTable.add(oneStatusTable).left().pad(0,2, 0, 2);
				}
			}
			statusTable.pack();

			bigPopin.pack();
			bigPopin.setPosition(GameScreen.SCREEN_W/2 - bigPopin.getWidth()/2, GameScreen.SCREEN_H/2 - bigPopin.getHeight()/2);

		} else {
			mainPopin.setVisible(true);

			title.setText(inspectableComponent.getTitle());
			desc.setText(inspectableComponent.getDescription());
		}
	}
	
	
	
    

    /**
     * Initialize the popin table (only the first time it is displayed).
     */
	private void initTable() {
		if (mainPopin == null) {
			mainPopin = new Table();
		}
//			selectedItemPopin.setDebug(true);

		// Add an empty click listener to capture the click so that the InputSingleton doesn't handle it
		mainPopin.setTouchable(Touchable.enabled);
		mainPopin.addListener(new ClickListener() {});
		
		// Place the popin and add the background texture
		mainPopin.setPosition(GameScreen.SCREEN_W/2, GameScreen.SCREEN_H/2);
		TextureRegionDrawable textureRegionDrawable = new TextureRegionDrawable(Assets.inventory_item_popin_background);
		mainPopin.setBackground(textureRegionDrawable);
		
		mainPopin.align(Align.top);
		
		// 1 - Title
		title = new Label("Title", PopinService.hudStyle());
		mainPopin.add(title).top().align(Align.top).pad(20, 0, 20, 0);
		mainPopin.row().align(Align.center);
		
		// 2 - Description
		desc = new Label("Description", PopinService.hudStyle());
		desc.setWrap(true);
		mainPopin.add(desc).growY().width(textureRegionDrawable.getMinWidth()).left().pad(0, 20, 0, 20);
		mainPopin.row();
		
		// 3 - Action buttons
		Table buttonTable = new Table();
		final TextButton closeBtn = new TextButton("Close",PopinService.bigButtonStyle());			
		// Close listener
		closeBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				closePopin();
			}
		});
		buttonTable.add(closeBtn).pad(0, 20,0,20);
		
		mainPopin.add(buttonTable).pad(20, 0, 20, 0);
	}

	private void initChoiceTable() {		
		choicePopin = new Table();
		choicePopin.setTouchable(Touchable.enabled);
		choicePopin.addListener(new ClickListener() {});
		
		// Place the popin and add the background texture
		choicePopin.setPosition(GameScreen.SCREEN_W/2, GameScreen.SCREEN_H/2);
		TextureRegionDrawable textureRegionDrawable = new TextureRegionDrawable(Assets.map_background);
		choicePopin.setBackground(textureRegionDrawable);
		
		choicePopin.align(Align.top);

		Label title = new Label("What do you want to inspect?", PopinService.hudStyle());
		choicePopin.add(title).top().align(Align.top).pad(20, 10, 20, 10);
		choicePopin.row().align(Align.center);
		
		choicePopinSubTable = new Table();
		choicePopin.add(choicePopinSubTable).pad(20, 10, 20, 10);
	}
	
	
	private void initBigTable() {
		if (bigPopin == null) {
			bigPopin = new Table();
		}

		// Add an empty click listener to capture the click so that the InputSingleton doesn't handle it
		bigPopin.setTouchable(Touchable.enabled);
		bigPopin.addListener(new ClickListener() {});
		
		// Place the popin and add the background texture
		bigPopin.setPosition(GameScreen.SCREEN_W/2, GameScreen.SCREEN_H/2);
		TextureRegionDrawable textureRegionDrawable = new TextureRegionDrawable(Assets.inventory_item_popin_background);
		bigPopin.setBackground(textureRegionDrawable);
		
		bigPopin.align(Align.top);
		
		// 1 - Title
		bigTitle = new Label("Title", PopinService.hudStyle());
		bigPopin.add(bigTitle).top().align(Align.top).pad(20, 0, 20, 0);
		bigPopin.row().align(Align.center);
		
		// 2 - Description
		bigDesc = new Label("Description", PopinService.hudStyle());
		bigDesc.setWrap(true);
		bigPopin.add(bigDesc).growY().width(textureRegionDrawable.getMinWidth()).left().pad(0, 20, 0, 20);
		bigPopin.row();
		
		// 3 - Stats
		bigStats = new Label("Stats", PopinService.hudStyle());
		bigStats.setWrap(true);
		bigPopin.add(bigStats).growY().width(textureRegionDrawable.getMinWidth()).left().pad(20, 20, 0, 20);
		bigPopin.row();
		
		// 4 - Status effects
		statusesMap = new HashMap<>();
		statusTable = new Table();
		bigPopin.add(statusTable).growY().width(textureRegionDrawable.getMinWidth()).left().pad(20, 20, 0, 20);
		bigPopin.row();

		
		// 5 - Action buttons
		Table buttonTable = new Table();
		final TextButton closeBtn = new TextButton("Close",PopinService.bigButtonStyle());			
		// Close listener
		closeBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				closePopin();
			}
		});
		buttonTable.add(closeBtn).pad(0, 20,0,20);
		
		bigPopin.add(buttonTable).pad(20, 0, 20, 0);
	}
	

	/**
	 * Close the popin and unpause the game.
	 */
	private void closePopin() {
		mainPopin.remove();
		choicePopin.remove();
		bigPopin.remove();
		
		if (room.getNextState() == null) {
			room.setNextState(room.getLastInGameState());
		}
	}

}
