/*
    This file is part of Poke.

    Poke is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 2 of the License.

    Poke is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Poke.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.andgate.poke;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Poke extends Game
{
    public enum GameMode
    {
        PLAY,
        PRACTICE,
        NONE
    }

    public SpriteBatch batch;
    public ShapeRenderer shape;

    public Skin skin;
    public Texture pauseBG;

    public BitmapFont largeFont;
    public BitmapFont mediumFont;
    public BitmapFont mediumSmallFont;
    public BitmapFont smallFont;

    public Sound hitSound;
    public Sound missSound;
    public Sound buttonPressedSound;

    public int missed;
    public int hits;
    public float gameTime;

    public int recharges;
    public float rechargeWait;
    public float rechargeTime;

    public Poke.GameMode mode;

    public float pixelDensity;

    public static final String GAME_NAME = "Poke";
    public static final Color BG_COLOR = new Color(0.15f, 0.15f, 0.15f, 1.0f);

    public final static float MINIMUM_SPAWN_TIME = 0.5f;
    public final static float MAXIMUM_SPAWN_TIME = 1.5f;
    public final static float MINIMUM_LIFE_TIME = 1.0f;
    public final static float MAXIMUM_LIFE_TIME = 4.0f;
    public final static int MINIMUM_SPAWN_COUNT = 3;
    public final static int MAXIMUM_SPAWN_COUNT = 10;
    public final static float MINIMUM_CIRCLE_RADIUS_PER_SCREEN_WIDTH = 100.0f / 1920.0f;
    public final static float MAXIMUM_CIRCLE_RADIUS_PER_SCREEN_WIDTH = 250.0f / 1920.0f;
    public static final Color DIM_SCREEN_COLOR = new Color(0.0f, 0.0f, 0.0f, 0.5f);
    public static final float SCREEN_WIDTH_TO_PAUSE_BUTTON_LENGTH = 1.0f / 15.0f;
    public static final float SCREEN_HEIGHT_TO_METER_THICKNESS = 1.0f / 25.0f;

    public static final float DEFAULT_VOLUME = 1.0f;
    public static final float RADIUS_PERCENT_TO_PITCH = -2.0f;
    public static final float PITCH_ADJUSTMENT = 2.0f;
    public static final float DEFAULT_PAN = 1.0f;

    public static int screenWidth;
    public static int screenHeight;

    public static float minCircleRadius;
    public static float medCircleRadius;
    public static float maxCircleRadius;
    public static float buttonIconLength;

    public static float meterThickness;

    private static final String FONT_LOCATION = "data/fonts/Ubuntu-Title.ttf";
    private static final int LARGE_FONT_SIZE = 100;
    private static final int MEDIUM_FONT_SIZE = 50;
    private static final int MEDIUM_SMALL_FONT_SIZE = 30;
    private static final int SMALL_FONT_SIZE = 20;

    private static final String SKIN_LOCATION = "data/ui/uiskin.json";
    private static final String HIT_SOUND_LOCATION = "data/sounds/hit.wav";
    private static final String MISS_SOUND_LOCATION = "data/sounds/miss.wav";
    private static final String BUTTON_PRESSED_SOUND_LOCATION = "data/sounds/button_pressed.wav";

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


    public final boolean isFree;

    public Poke(boolean isFree)
    {
        this.isFree = isFree;
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        shape = new ShapeRenderer();

        Gdx.input.setCatchBackKey(true);

        pixelDensity = Gdx.graphics.getDensity();

        skin = new Skin(Gdx.files.internal(SKIN_LOCATION));

        buildPauseBG();
        loadFonts();

        hitSound = Gdx.audio.newSound(Gdx.files.internal(HIT_SOUND_LOCATION));
        missSound = Gdx.audio.newSound(Gdx.files.internal(MISS_SOUND_LOCATION));
        buttonPressedSound = Gdx.audio.newSound(Gdx.files.internal(BUTTON_PRESSED_SOUND_LOCATION));

        resetGame();

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        minCircleRadius = ((float)screenWidth) * MINIMUM_CIRCLE_RADIUS_PER_SCREEN_WIDTH;
        maxCircleRadius = ((float)screenWidth) * MAXIMUM_CIRCLE_RADIUS_PER_SCREEN_WIDTH;
        medCircleRadius = (minCircleRadius + maxCircleRadius) / 2.0f;
        buttonIconLength = ((float)screenWidth) * SCREEN_WIDTH_TO_PAUSE_BUTTON_LENGTH;

        meterThickness = ((float)screenHeight) * SCREEN_HEIGHT_TO_METER_THICKNESS;

        setScreen(new MainMenuScreen(this));
    }

    public void resetGame() {
        missed = 0;
        hits = 0;
        gameTime = 0.0f;

        mode = GameMode.NONE;

        recharges = 0;
        rechargeWait = 0.0f;
    }

    public ImageButton createIconButton(String upFilename, String downFilename, ClickListener listener)
    {
        TextureRegionDrawable buttonDrawable
                = new TextureRegionDrawable(new TextureRegion(new Texture(upFilename)));
        TextureRegionDrawable buttonDownDrawable
                = new TextureRegionDrawable(new TextureRegion(new Texture(downFilename)));


        ImageButton button = new ImageButton(buttonDrawable, buttonDownDrawable);

        float buttonScale = buttonIconLength / button.getWidth();
        button.getImageCell().width(buttonIconLength);
        button.getImageCell().height(buttonIconLength);

        button.addListener(listener);

        return button;
    }

    private void loadFonts()
    {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(FONT_LOCATION));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = (int) (LARGE_FONT_SIZE * pixelDensity);
        largeFont = generator.generateFont(parameter);

        parameter.size = (int) (MEDIUM_FONT_SIZE * pixelDensity);
        mediumFont = generator.generateFont(parameter);

        parameter.size = (int) (MEDIUM_SMALL_FONT_SIZE * pixelDensity);
        mediumSmallFont = generator.generateFont(parameter);

        parameter.size = (int) (SMALL_FONT_SIZE * pixelDensity);
        smallFont = generator.generateFont(parameter);

        generator.dispose();
    }

    private void buildPauseBG()
    {
        Pixmap blackbox = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        blackbox.setColor(DIM_SCREEN_COLOR);
        blackbox.fill();
        pauseBG = new Texture(blackbox);

    }

    @Override
	public void render() {
		super.render(); // SUPER important! (hahaha...sorry)
	}

    @Override
	public void dispose()
	{
		batch.dispose();
        shape.dispose();

        skin.dispose();

        pauseBG.dispose();

        largeFont.dispose();
        mediumFont.dispose();
        mediumSmallFont.dispose();
		smallFont.dispose();

        hitSound.dispose();
        missSound.dispose();
	}
}
