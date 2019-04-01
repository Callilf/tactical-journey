package com.dokkaebistudio.tacticaljourney.journal;

import java.util.ArrayList;
import java.util.List;

import com.dokkaebistudio.tacticaljourney.rendering.JournalRenderer;
import com.dokkaebistudio.tacticaljourney.singletons.GameTimeSingleton;

public class Journal {
	
	private static Journal instance;

	/**
	 * The current entries.
	 */
	private List<String> entries = new ArrayList<>();
	
	/** The maximum number of entries in the journal. */
	private int maxEntries = 20;
	
	
	private static Journal getInstance() {
		if (instance == null) {
			instance = new Journal();
		}
		return instance;
	}
	
	public static void addEntry(String entry) {
		Journal journal = getInstance();
		journal.entries.add("[WHITE][" + String.format("%.0f", GameTimeSingleton.getInstance().getElapsedTime()) + "] " + entry);
		if (journal.entries.size() > journal.maxEntries) {
			journal.entries.remove(0);
		}
		
		JournalRenderer.requireRefresh();
	}
	
	
	public static String getText() {
		Journal journal = getInstance();
		StringBuilder sb = new StringBuilder();
		for (int i = 0 ; i < journal.entries.size() ; i++) {
			sb.append(journal.entries.get(i));
			if (i != journal.entries.size() - 1) {
				sb.append("\n");
			}
		}
		return sb.toString();
	}
	
	public static void dispose() {
		instance = null;
	}
	
}
