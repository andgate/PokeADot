package com.andgate.poke.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.andgate.poke.Poke;

public class DesktopLauncher {
    private static final boolean IS_FREE = false;

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new Poke(IS_FREE), config);
	}
}
