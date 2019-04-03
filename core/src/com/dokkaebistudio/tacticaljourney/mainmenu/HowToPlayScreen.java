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
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.Settings;
import com.dokkaebistudio.tacticaljourney.TacticalJourney;
import com.dokkaebistudio.tacticaljourney.persistence.GameStatistics;
import com.dokkaebistudio.tacticaljourney.persistence.GameStatistics.GameStatisticsState;
import com.dokkaebistudio.tacticaljourney.persistence.Rankings;
import com.dokkaebistudio.tacticaljourney.rendering.service.PopinService;

public class HowToPlayScreen extends ScreenAdapter {
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

	public HowToPlayScreen (final TacticalJourney game) {
		this.game = game;

		guiCam = new OrthographicCamera(GameScreen.SCREEN_W, GameScreen.SCREEN_H);
		guiCam.position.set(GameScreen.SCREEN_W / 2, GameScreen.SCREEN_H / 2, 0);
		viewport = new FitViewport(GameScreen.SCREEN_W, GameScreen.SCREEN_H, guiCam);
		hudStage = new Stage(viewport);

		touchPoint = new Vector3();

		// should be already loaded
		menuBackground = Assets.menuBackground.getRegion();
		
		Gdx.input.setInputProcessor(hudStage);
		
		Table mainTable = new Table();
//		mainTable.setDebug(true);
		
		// Main title
		Label newGameLabel = new Label("HOW TO PLAY", PopinService.hudStyle());
		mainTable.add(newGameLabel).padBottom(50);
		mainTable.row();
		
		
				
		Table htpTable = new Table();
		htpTable.top();
		htpTable.setBackground(new NinePatchDrawable(Assets.popinNinePatch));
		
		ScrollPane scrollPane = new ScrollPane(htpTable);
		
		
		Label presentationLabel = new Label("Presentation", PopinService.hudStyle());
		htpTable.add(presentationLabel).left().padBottom(30);
		htpTable.row();

		Label descLabel = new Label("Calishka's Trial is a roguelike game with tactical combat. The purpose is to reach the deepest floor of a proceduraly"
				+ " generated dungeon without dying. When you character dies, you have to start again from the start.\nEach floor has a random number of rooms. Among"
				+ " these rooms is the exit to the lower floor. You don't have to visit the entire floor to go to the next one, but keep in mind that visiting rooms"
				+ " will allow you earning experience, money, and items that will boost your stats.", PopinService.hudStyle());
		descLabel.setWrap(true);
		htpTable.add(descLabel).left().width(1200).padBottom(50);
		htpTable.row();
		
		Label combatsTitle = new Label("Combats", PopinService.hudStyle());
		htpTable.add(combatsTitle).left().padBottom(30);
		htpTable.row();

		Label combatDescLabel = new Label("The combat system is turn-based, which means that each entity of the game will play sequentially."
				+ " During a turn, you can move and then do an action. There are various actions such as attacking, throwing an object, using an item,"
				+ " picking up an item and so on.\n"
				+ "The blue tiles show where you can move. To move, click on the desired destination. A path throughout this tile will be displayed, click again"
				+ " on the destination to confirm the movement.",
				PopinService.hudStyle());
		combatDescLabel.setWrap(true);
		htpTable.add(combatDescLabel).left().width(1200).padBottom(30);
		htpTable.row();
		
		Label combatDescLabel2 = new Label("To attack an enemy, just click on it when you are at range. The attack wheel will be displayed, click again to"
				+ " stop the arrow. You will deal damages depending on the pointed color:\n"
				+ " - [GREEN]Green[WHITE]: normal hit, the amount you deal is equal to your strength\n"
				+ " - [GRAY]Gray[WHITE]: graze, the amount you deal is a bit lower than your strength\n"
				+ " - [BLACK]Black[WHITE]: miss, you don't deal any damage\n"
				+ " - [RED]Red[WHITE]: critical, the amount you deal is 2 times your strength.",
				PopinService.hudStyle());
		combatDescLabel2.setWrap(true);
		htpTable.add(combatDescLabel2).left().width(1200).padBottom(30);
		htpTable.row();

		Label combatDescLabel3 = new Label("Each time you kill an enemy you receive experience points. When you get enough points you can level up and gain rewards"
				+ " such as stats up, healing or items. Once all enemies of a room are dead, the room becomes \"cleared\".\n"
				+ " You can move freely in cleared rooms, the blue tiles are not displayed anymore.",
				PopinService.hudStyle());
		combatDescLabel3.setWrap(true);
		htpTable.add(combatDescLabel3).left().width(1200).padBottom(50);
		htpTable.row();

		
		Label hudLabelTitle = new Label("Interface", PopinService.hudStyle());
		htpTable.add(hudLabelTitle).left().padBottom(30);
		htpTable.row();

		Label hudLabel = new Label("Profile\n"
				+ "The profile panel allows you seeing you stats and active blessings and curses. Blessings and Curses are passive abilities that can be"
				+ "received from item or performing certain actions. Blessings are positive abilities and will make the game easier, whereas Curses are"
				+ "negative abilities that will tend to make it harder.\n"
				+ "Some item will only grant you a blessing, but most of them will grant you both a blessing and a curse. It's up to you to balance them"
				+ " properly.", 
				PopinService.hudStyle());
		hudLabel.setWrap(true);
		htpTable.add(hudLabel).left().width(1200).padBottom(50);
		htpTable.row();
		
		Label hudLabel2 = new Label("Inventory\n"
				+ "The inventory allows you storing items. During your turn, you can open the inventory and click on a item to either use it or"
				+ " throw it. There are plenty of items and not a lot of space in your backpack, so try keeping only what is necessary.", 
				PopinService.hudStyle());
		hudLabel2.setWrap(true);
		htpTable.add(hudLabel2).left().width(1200).padBottom(30);
		htpTable.row();
		
		Label hudLabel3 = new Label("Inspection\n"
				+ "You can inspect any entity of the game to get some information about it and it doesn't consume your turn.", 
				PopinService.hudStyle());
		hudLabel3.setWrap(true);
		htpTable.add(hudLabel3).left().width(1200).padBottom(30);
		htpTable.row();
		
		Label hudLabel4 = new Label("Melee attack\n"
				+ "You can attack any tile using your melee attack, even if there is no enemy on it. Note that the attack wheel will pop up only when you"
				+ " attack an enemy.", 
				PopinService.hudStyle());
		hudLabel4.setWrap(true);
		htpTable.add(hudLabel4).left().width(1200).padBottom(30);
		htpTable.row();
		
		Label hudLabel5 = new Label("Bow\n"
				+ "The bow allows you attacking from a distance, given that you have arrows.", 
				PopinService.hudStyle());
		hudLabel5.setWrap(true);
		htpTable.add(hudLabel5).left().width(1200).padBottom(30);
		htpTable.row();
		
		Label hudLabel6 = new Label("Bombs\n"
				+ "Bombs can be throw and will explode after some turns (2 by default). Be sure not to stay far enough when it explodes or you might be caught "
				+ " in the blast.", 
				PopinService.hudStyle());
		hudLabel6.setWrap(true);
		htpTable.add(hudLabel6).left().width(1200).padBottom(30);
		htpTable.row();
		
		
		htpTable.pack();
		scrollPane.layout();
		mainTable.add(scrollPane).top().width(1350).maxHeight(700).padBottom(50);
		mainTable.row();
			

		

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
