package com.dokkaebistudio.tacticaljourney.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.dokkaebistudio.tacticaljourney.TacticalJourney;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
        	return new GwtApplicationConfiguration(480, 800);
        }

        @Override
        public ApplicationListener getApplicationListener () {
        	return new TacticalJourney();
        }

		@Override
		public void setApplicationLogger(ApplicationLogger applicationLogger) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public ApplicationLogger getApplicationLogger() {
			// TODO Auto-generated method stub
			return null;
		}
}