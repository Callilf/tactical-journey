package com.dokkaebistudio.tacticaljourney.persistence;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.dokkaebistudio.tacticaljourney.persistence.GameStatistics.GameStatisticsState;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class Rankings {

	public static List<GameStatistics> getRankings() {
		List<GameStatistics> stats = null;
		
		Kryo kryo = new Kryo();
		kryo.setReferences(false);
		
		try {
			FileHandle rankingsFile = Gdx.files.local("rankings.bin");
			if (rankingsFile.exists()) {
			    Input input = new Input(new FileInputStream(rankingsFile.file()));
			    stats = (List<GameStatistics>) kryo.readClassAndObject(input);
			    input.close();   
			}
		} catch (KryoException | IOException e ) {
			Gdx.app.error("SAVE", "Failed to load the rankings", e);
		}
		
		return (List<GameStatistics>) (stats != null ? stats : Collections.emptyList());
	}
	
	
	/**
	 * Sort the rankings for display.
	 * @param rankings the rankings to sort
	 */
	public static void sort(List<GameStatistics> rankings) {
		rankings.sort(new Comparator<GameStatistics>() {

			@Override
			public int compare(GameStatistics game1, GameStatistics game2) {
				// First sort by WIN
				if (game1.getState() != game2.getState()) {
					if (game1.getState() == GameStatisticsState.WON) return -1;
					if (game2.getState() == GameStatisticsState.WON) return 1;
				} else if (game1.getState() == GameStatisticsState.LOST){
					
					// Sort by FLOOR LEVEL
					if (game2.getFloorLevel() != game1.getFloorLevel()) {
						return game2.getFloorLevel() - game1.getFloorLevel();
					}
					
					// Sort by CHARACTER LEVEL
					if (game2.getCharacterLevel() != game1.getCharacterLevel()) {
						return game2.getFloorLevel() - game1.getFloorLevel();
					}
					
					// Sort by GOLD
					if (game2.getGold() != game1.getGold()) {
						return game2.getGold() - game1.getGold();
					}
						
				}
				
				if (game2.getYear() != game1.getYear()) {
					return game2.getYear() - game1.getYear();
				}
				if (game2.getMonth() != game1.getMonth()) {
					return game2.getMonth() - game1.getMonth();
				}
				if (game2.getDay() != game1.getDay()) {
					return game2.getDay() - game1.getDay();
				}
				
				return 0;
			}
		
		});
	}
	
	
	
	public static void addGameToRankings(GameStatistics game) {
		List<GameStatistics> rankings = getRankings();
		if (rankings.isEmpty()) {
			rankings = new ArrayList<>();
		}
		
		rankings.add(game);
		
		Kryo kryo = new Kryo();
		kryo.setReferences(false);
		
		try {
			FileHandle rankingsFile = Gdx.files.local("rankings.bin");
			if (rankingsFile.exists()) rankingsFile.delete();
			Output output = new Output(new FileOutputStream(rankingsFile.file()));
			kryo.writeClassAndObject(output, rankings);
			output.close();
		} catch (KryoException | IOException e) {
			Gdx.app.error("SAVE", "Failed to save the rankings", e);
		}
		
	}
}
