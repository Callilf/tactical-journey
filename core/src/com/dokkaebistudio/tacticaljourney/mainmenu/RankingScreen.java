package com.dokkaebistudio.tacticaljourney.mainmenu;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.dokkaebistudio.tacticaljourney.Settings;
import com.dokkaebistudio.tacticaljourney.TacticalJourney;
import com.dokkaebistudio.tacticaljourney.assets.MenuAssets;
import com.dokkaebistudio.tacticaljourney.assets.SceneAssets;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.persistence.GameStatistics;
import com.dokkaebistudio.tacticaljourney.persistence.GameStatistics.GameStatisticsState;
import com.dokkaebistudio.tacticaljourney.persistence.Rankings;
import com.dokkaebistudio.tacticaljourney.rendering.service.PopinService;

public class RankingScreen extends ScreenAdapter {
	TacticalJourney game;
	OrthographicCamera guiCam;
	Vector3 touchPoint;
	FitViewport viewport;
	Stage hudStage;

	TextureRegion menuBackground;
	
	boolean enteredSeed = false;
	TextField seedField;
	
	boolean enteredName = false;
	TextField nameField;

	public RankingScreen (final TacticalJourney game) {
		this.game = game;

		guiCam = new OrthographicCamera(GameScreen.SCREEN_W, GameScreen.SCREEN_H);
		guiCam.position.set(GameScreen.SCREEN_W / 2, GameScreen.SCREEN_H / 2, 0);
		viewport = new FitViewport(GameScreen.SCREEN_W, GameScreen.SCREEN_H, guiCam);
		hudStage = new Stage(viewport);

		touchPoint = new Vector3();

		// should be already loaded
		menuBackground = MenuAssets.menuBackground.getRegion();
		
		Gdx.input.setInputProcessor(hudStage);
		
		Table mainTable = new Table();
//		mainTable.setDebug(true);
		
		// Main title
		Label newGameLabel = new Label("RANKING", PopinService.hudStyle());
		mainTable.add(newGameLabel).padBottom(50);
		mainTable.row();
		
		
		// Display games list
		
		List<GameStatistics> rankings = Rankings.getRankings();
		Rankings.sort(rankings);
		
		if (rankings.isEmpty()) {
			Label noGamesLabel = new Label("No games yet", PopinService.hudStyle());
			mainTable.add(noGamesLabel).padBottom(700);
			mainTable.row();
		} else {
			Table statsTable = new Table();
			statsTable.top();
//			statsTable.setDebug(true);
			ScrollPane scrollPane = new ScrollPane(statsTable);
//			scrollPane.setDebug(true);
			
			for (GameStatistics gameStat : rankings) {
				Table oneGameTable = createOneGameTable(gameStat);
				statsTable.add(oneGameTable).padBottom(5);
				statsTable.row();
			}
			
			statsTable.pack();
			scrollPane.layout();
			mainTable.add(scrollPane).top().width(1350).maxHeight(700).padBottom(50);
			mainTable.row();
			
		}

		

		// Back button
		
		TextButton backBtn = new TextButton("Back", PopinService.buttonStyle());
		backBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {				
				game.setScreen(new MainMenuScreen(game));
			}
		});
		mainTable.add(backBtn).padBottom(50);
		mainTable.row();

		
		mainTable.pack();
		mainTable.setPosition(GameScreen.SCREEN_W/2 - mainTable.getWidth()/2, 0);
		hudStage.addActor(mainTable);
	}
	
	
	public Table createOneGameTable(GameStatistics gameStat) {
		Table oneGameTable = new Table();
		NinePatchDrawable ninePatchDrawable = new NinePatchDrawable(SceneAssets.popinNinePatch);
		oneGameTable.setBackground(ninePatchDrawable);
		
		Label characterName = new Label("Name: " + gameStat.getCharacterName(), PopinService.hudStyle());
		characterName.setWrap(true);
		oneGameTable.add(characterName).left().width(300).pad(0, 10, 0, 10);
		
		Table part2 = new Table();
		Label characterLevel = new Label("Level: " + gameStat.getCharacterLevel(), PopinService.hudStyle());
		part2.add(characterLevel).left();
		part2.row();
		Label floor = new Label("Floor: " + gameStat.getFloorLevel(), PopinService.hudStyle());
		part2.add(floor).left();
		part2.row();
		Label gold = new Label("Money: [GOLDENROD]" + gameStat.getGold(), PopinService.hudStyle());
		part2.add(gold).left();
		oneGameTable.add(part2).width(200).pad(0, 10, 0, 10);
		
		Table part3 = new Table();
		Label totalTime = new Label("Total time: " + String.format("%.1f", gameStat.getTotalTime()), PopinService.hudStyle());
		part3.add(totalTime).left();
		part3.row();
		Label totalTurns = new Label("Total turns: " + gameStat.getTotalTurns(), PopinService.hudStyle());
		part3.add(totalTurns).left();
		oneGameTable.add(part3).width(300).pad(0, 10, 0, 10);

		Table part4 = new Table();
		Label killer = new Label("Killed by: " + gameStat.getKiller(), PopinService.hudStyle());
		killer.setWrap(true);
		if (gameStat.getState() == GameStatisticsState.LOST) {
			killer.setText("Killed by: " + gameStat.getKiller());
		} else {
			killer.setText("[GREEN]Obtained the Universal Cure!");
		}
		part4.add(killer).left().width(280);
		oneGameTable.add(part4).width(400).pad(0, 10, 0, 10);
		
		return oneGameTable;
	}
	
	
	
	

	public void update () {}

	public void draw () {
		GL20 gl = Gdx.gl;
		gl.glClearColor(0, 0, 0, 1);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		guiCam.update();
		game.batcher.setProjectionMatrix(guiCam.combined);


//		game.batcher.enableBlending();
		game.batcher.begin();
		game.batcher.draw(menuBackground, 0, 0, 1920, 1080);
		game.batcher.end();	
		
		hudStage.act();
		hudStage.draw();
	}

	@Override
	public void render (float delta) {
		update();
		draw();
	}

	@Override
	public void pause () {
		Settings.save();
	}
	
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
}
