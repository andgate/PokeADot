package com.andgate.pokeadot.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.andgate.pokeadot.PokeADot;

public class DesktopLauncher {
    private static final boolean IS_FREE = false;

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.samples=16;
        //config.useGL30=true;
        config.resizable=false;
        config.vSyncEnabled=true;
		new LwjglApplication(new PokeADot(IS_FREE), config);
	}
}
