/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.rendering.interfaces.Renderer;
import com.dokkaebistudio.tacticaljourney.rendering.service.PopinService;

/**
 * This class allows rendering the journal.
 * @author Callil
 *
 */
public class JournalRenderer implements Renderer {
			
	private Stage stage;
	
	/** Whether the map is displayed on screen or not. */
	private boolean journalDisplayed;
						
	/** The main table. */
	private ScrollPane scrollPane;
	private Table journalTable;
	private Label entries;
	
	/** The background of the journal. */
	private Image smallBackground;

	
	private Image player;
	
	private static boolean needRefresh;
	
	
	/**
	 * Instanciate a jounral Renderer.
	 * @param sr the shaperenderer
	 */
	public JournalRenderer(Stage s) {
		this.stage = s;
		this.journalDisplayed = true;
		
		
		smallBackground = new Image(Assets.journal_background.getRegion());
		smallBackground.setPosition(GameScreen.SCREEN_W - Assets.journal_background.getRegionWidth() - 5, 5);
		smallBackground.addAction(Actions.alpha(0.75f));
		stage.addActor(smallBackground);
				
		
		journalTable = new Table();

		journalTable.bottom().left();
		entries = new Label("", PopinService.smallTextStyle());
		entries.setWrap(true);
		entries.setWidth(Assets.journal_background.getRegionWidth() - 10);
		entries.setAlignment(Align.left);
		journalTable.add(entries).width(Assets.journal_background.getRegionWidth() - 20);
//		roomsTable.setDebug(true);

		scrollPane = new ScrollPane(journalTable);
		scrollPane.setSmoothScrolling(true);
		scrollPane.setBounds(smallBackground.getX() + 10, smallBackground.getY() + 5, Assets.journal_background.getRegionWidth() - 20, Assets.journal_background.getRegionHeight() - 10);
//		scrollPane.addAction(Actions.alpha(0.5f));

		stage.addActor(scrollPane);
		needRefresh = true;
	}
	

	/**
	 * Render the map of the floor.
	 */
	public void render(float deltaTime) {
		if (needRefresh) {
			entries.setText(Journal.getText());
			journalTable.pack();
			scrollPane.layout();
			scrollPane.scrollTo(0, 0, 0, 0);
			needRefresh = false;
		}
		
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}
	





	
	// getters and setters
	
	public boolean isMapDisplayed() {
		return journalDisplayed;
	}

	public void setMapDisplayed(boolean mapDisplayed) {
		this.journalDisplayed = mapDisplayed;
	}

	public boolean isNeedRefresh() {
		return needRefresh;
	}

	public static void requireRefresh() {
		needRefresh = true;
	}

}
