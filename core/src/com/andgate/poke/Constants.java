package com.andgate.poke;

import com.badlogic.gdx.graphics.Color;

public class Constants
{
    public static final String GAME_NAME = "Poke";
    public static final Color BG_COLOR = new Color(0.15f, 0.15f, 0.15f, 1.0f);

    public static final int WORLD_HEIGHT = 30;

    public final static float MINIMUM_SPAWN_TIME = 0.5f;
    public final static float MAXIMUM_SPAWN_TIME = 1.5f;
    public final static float MINIMUM_LIFE_TIME = 1.0f;
    public final static float MAXIMUM_LIFE_TIME = 4.0f;
    public final static int MINIMUM_SPAWN_COUNT = 3;
    public final static int MAXIMUM_SPAWN_COUNT = 10;
    public final static float MINIMUM_CIRCLE_RADIUS = 3.0f;
    public final static float MAXIMUM_CIRCLE_RADIUS = 8.0f;
    public static final float CIRCLE_BORDER_THICKNESS = 0.5f;
    public static final int CIRCLE_SEGMENTS = 100;
    public static final Color DIM_SCREEN_COLOR = new Color(0.0f, 0.0f, 0.0f, 0.5f);
    public static final float BUTTON_LENGTH = 4.0f;

    public static final float DEFAULT_VOLUME = 1.0f;
    public static final float RADIUS_PERCENT_TO_PITCH = -2.0f;
    public static final float PITCH_ADJUSTMENT = 2.0f;
    public static final float DEFAULT_PAN = 1.0f;

    public static final String FONT_LOCATION = "data/fonts/Ubuntu-Title.ttf";
    public static final int LARGE_FONT_SIZE = 10;
    public static final int MEDIUM_FONT_SIZE = 5;
    public static final int MEDIUM_SMALL_FONT_SIZE = 3;
    public static final int SMALL_FONT_SIZE = 2;

    public static final String SKIN_LOCATION = "data/ui/uiskin.json";
    public static final String HIT_SOUND_LOCATION = "data/sounds/hit.wav";
    public static final String MISS_SOUND_LOCATION = "data/sounds/miss.wav";
    public static final String BUTTON_PRESSED_SOUND_LOCATION = "data/sounds/button_pressed.wav";

    public static final String PAUSE_ICON_LOCATION = "data/icons/pause.png";
    public static final String PAUSE_ICON_DOWN_LOCATION = "data/icons/pause_down.png";
    public static final String PLAY_ICON_LOCATION = "data/icons/play.png";
    public static final String PLAY_ICON_DOWN_LOCATION = "data/icons/play_down.png";
    public static final String STOP_ICON_LOCATION = "data/icons/stop.png";
    public static final String STOP_ICON_DOWN_LOCATION = "data/icons/stop_down.png";
    public static final String GO_ICON_LOCATION = "data/icons/go.png";
    public static final String GO_ICON_DOWN_LOCATION = "data/icons/go_down.png";
    public static final String REPLAY_ICON_LOCATION = "data/icons/replay.png";
    public static final String REPLAY_ICON_DOWN_LOCATION = "data/icons/replay_down.png";
}
