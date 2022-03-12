package com.tilebased.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.tilebased.game.TileBased;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.foregroundFPS = 60;
		config.backgroundFPS = 60;
		config.undecorated = true;
		config.height = LwjglApplicationConfiguration.getDesktopDisplayMode().height;
		config.width = LwjglApplicationConfiguration.getDesktopDisplayMode().width;
		config.fullscreen = false;
		config.resizable = false;
		config.forceExit = false;
		new LwjglApplication(new TileBased(), config);
	}
}
