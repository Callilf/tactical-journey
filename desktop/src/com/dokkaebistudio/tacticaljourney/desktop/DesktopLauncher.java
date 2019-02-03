package com.dokkaebistudio.tacticaljourney.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.dokkaebistudio.tacticaljourney.TacticalJourney;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Tactical Journey";
		config.width = 1920;
		config.height = 1080;
		config.fullscreen = false;
		config.forceExit = true;
		new LwjglApplication(new TacticalJourney(), config);
	}
}
