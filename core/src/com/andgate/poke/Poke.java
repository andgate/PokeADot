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
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
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

    public Poke.GameMode mode;

    public float ppm = 0.0f;
    public float worldWidth = 0.0f;
    public float worldHeight = (float)Constants.WORLD_HEIGHT;

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

        screenAdjustments(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        skin = new Skin(Gdx.files.internal(Constants.SKIN_LOCATION));

        createPauseBG();
        createFonts();

        hitSound = Gdx.audio.newSound(Gdx.files.internal(Constants.HIT_SOUND_LOCATION));
        missSound = Gdx.audio.newSound(Gdx.files.internal(Constants.MISS_SOUND_LOCATION));
        buttonPressedSound = Gdx.audio.newSound(Gdx.files.internal(Constants.BUTTON_PRESSED_SOUND_LOCATION));

        resetGame();

        setScreen(new MainMenuScreen(this));
    }

    public void screenAdjustments(int width, int height)
    {
        ppm = (float)height / worldHeight;
        worldWidth = worldHeight * (float)width / (float)height;

        Matrix4 matrix = new Matrix4();
        matrix.setToOrtho2D(0, 0, width, height);
        batch.setProjectionMatrix(matrix);
    }

    public void resetGame() {
        missed = 0;
        hits = 0;
        gameTime = 0.0f;
        mode = GameMode.NONE;
    }

    public ImageButton createIconButton(String upFilename, String downFilename, ClickListener listener)
    {
        Texture buttonTexture = new Texture(upFilename);
        buttonTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

        Texture buttonDownTexture = new Texture(upFilename);
        buttonDownTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

        TextureRegionDrawable buttonDrawable
                = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        TextureRegionDrawable buttonDownDrawable
                = new TextureRegionDrawable(new TextureRegion(buttonDownTexture));


        ImageButton button = new ImageButton(buttonDrawable, buttonDownDrawable);
        button.getImageCell().width(Constants.BUTTON_LENGTH * ppm);
        button.getImageCell().height(Constants.BUTTON_LENGTH * ppm);

        button.addListener(listener);

        return button;
    }

    private void createFonts()
    {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(Constants.FONT_LOCATION));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = (int) ((float)Constants.LARGE_FONT_SIZE * ppm);
        largeFont = generator.generateFont(parameter);

        parameter.size = (int) ((float)Constants.MEDIUM_FONT_SIZE * ppm);
        mediumFont = generator.generateFont(parameter);

        parameter.size = (int) ((float)Constants.MEDIUM_SMALL_FONT_SIZE * ppm);
        mediumSmallFont = generator.generateFont(parameter);

        parameter.size = (int) ((float)Constants.SMALL_FONT_SIZE * ppm);
        smallFont = generator.generateFont(parameter);

        generator.dispose();
    }

    private void createPauseBG()
    {
        Pixmap blackbox = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        blackbox.setColor(Constants.DIM_SCREEN_COLOR);
        blackbox.fill();
        pauseBG = new Texture(blackbox);
    }

    @Override
    public void dispose()
    {
        batch.dispose();
        shape.dispose();

        skin.dispose();

        pauseBG.dispose();

        disposeFonts();

        hitSound.dispose();
        missSound.dispose();
    }

    public void disposeFonts()
    {
        largeFont.dispose();
        mediumFont.dispose();
        mediumSmallFont.dispose();
        smallFont.dispose();
    }

    @Override
	public void render() {
		super.render(); // SUPER important! (hahaha...sorry)
	}

    @Override
    public void resize(int width, int height)
    {
        screenAdjustments(width, height);
        //disposeFonts();
        createFonts();
    }
}
