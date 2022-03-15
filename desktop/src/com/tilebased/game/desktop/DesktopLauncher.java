package com.tilebased.game.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.tilebased.game.TileBased;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setIdleFPS(60);
		config.setDecorated(false);
		config.setWindowedMode(Lwjgl3ApplicationConfiguration.getDisplayMode().width, Lwjgl3ApplicationConfiguration.getDisplayMode().height);
		config.setWindowPosition(0, 0);
		config.setResizable(false);
		config.setInitialVisible(true);
		new Lwjgl3Application(new TileBased(), config);	}
}
