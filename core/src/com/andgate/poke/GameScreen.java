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

import com.andgate.poke.util.HSL;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import java.text.DecimalFormat;
import java.util.Iterator;


public class GameScreen implements Screen
{
    private final Poke game;

    public enum GameState
    {
        INTRO,
        PAUSE,
        RUN,
        OVER
    }

    private GameState gameState = GameState.INTRO;

    public void setGameState(GameState newGameState){
        gameState = newGameState;
    }

    InputMultiplexer im;

    Stage introStage;
    Stage pauseMenuStage;
    Stage pauseButtonStage;
    Stage gameOverStage;

    int pokablesExpired;
    Array<Pokable> pokables;

    private float spawnWaitTime;
    private float nextSpawnTime;
    private int spawnCount;

    private static final String HIT_COUNT_TEXT = "Poked";
    private static final String POINTS_TEXT = "Points";
    private static final String GAME_TIME_TEXT = "Time";
    private static final String PAUSE_TEXT = "Pause";
    private static final String INSTRUCTIONS_TEXT = "Don't let them shrink!";
    private static final String POKE_ANYWHERE_TEXT = "Poke circles to start.";
    private static final DecimalFormat doubleDecimal = new DecimalFormat("0.00");

    private static final float GAME_END_PAUSE = 10.0f; // seconds
    private float gameEndWait = 0.0f;
    private static final float TAP_TO_END_START_TIME = 1.0f; //seconds
    private boolean tapToEnd = false;

    public GameScreen(final Poke newGame, Poke.GameMode newMode)
    {
        game = newGame;
        game.mode = newMode;

        im = new InputMultiplexer();
        im.addProcessor(new GameInputProcessor());
        Gdx.input.setInputProcessor(im);

        pokables = new Array<Pokable>();
        pokablesExpired = 0;
        forceSpawn();

        buildStages();

        /*im.setProcessors(new Array<InputProcessor>(
                new InputProcessor[]{new GameInputProcessor()}
        ));*/
    }

    private void buildStages()
    {
        buildIntroStage();
        buildPauseButtonScene();
        buildPauseMenuScene();
        buildGameOverStage();
    }

    private void disposeStages()
    {
        introStage.dispose();
        pauseMenuStage.dispose();
        pauseButtonStage.dispose();
        gameOverStage.dispose();
    }

    private void buildIntroStage()
    {
        introStage = new Stage();
        //introStage.getViewport().setCamera(camera);

        Label.LabelStyle labelStyle = new Label.LabelStyle(game.mediumSmallFont, Color.WHITE);
        Label instructionsLabel = new Label(INSTRUCTIONS_TEXT, labelStyle);
        Label pokeAnywhereLabel = new Label(POKE_ANYWHERE_TEXT, labelStyle);

        Table table = new Table();
        table.add(instructionsLabel).center().row();
        table.add(pokeAnywhereLabel).center();

        table.setFillParent(true);

        introStage.addActor(table);
    }

    private void buildPauseButtonScene()
    {
        pauseButtonStage = new Stage();
        //pauseButtonStage.getViewport().setCamera(camera);

        ImageButton pauseButton = game.createIconButton(Constants.PAUSE_ICON_LOCATION, Constants.PAUSE_ICON_DOWN_LOCATION,
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        gameState = GameState.PAUSE;
                        game.buttonPressedSound.play();
                        im.setProcessors(new Array<InputProcessor>(
                                new InputProcessor[]{pauseMenuStage, new GameInputProcessor()}
                        ));
                    }
                });

        Table table = new Table(game.skin);
        table.bottom().left();
        table.add(pauseButton).bottom().left();

        table.setFillParent(true);

        pauseButtonStage.addActor(table);
    }

    private void buildPauseMenuScene()
    {
        pauseMenuStage = new Stage();
        //pauseMenuStage.getViewport().setCamera(camera);

        ImageButton playButtonWrapper = game.createIconButton(Constants.PLAY_ICON_LOCATION, Constants.PLAY_ICON_DOWN_LOCATION,
                new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y){
                        gameState = GameState.RUN;
                        game.buttonPressedSound.play();
                        im.setProcessors(
                                new Array<InputProcessor>(
                                        new InputProcessor[]{pauseButtonStage, new GameInputProcessor()}
                                )
                        );
                    }
                });

        ImageButton stopButtonWrapper = game.createIconButton(Constants.STOP_ICON_LOCATION, Constants.STOP_ICON_DOWN_LOCATION,
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        game.buttonPressedSound.play();
                        gameState = GameState.OVER;
                    }
                });

        final Label.LabelStyle pauseLabelStyle = new Label.LabelStyle(game.largeFont, new Color(1.0f, 1.0f, 1.0f, 0.7f));
        final Label pauseLabel = new Label(PAUSE_TEXT, pauseLabelStyle);

        Table buttonTable = new Table();
        buttonTable.add(playButtonWrapper).left();
        buttonTable.add(stopButtonWrapper).expandX().right();

        Table table = new Table(game.skin);
        table.add(pauseLabel).center().spaceBottom(25.0f).row();
        table.add(buttonTable).fill();

        table.setFillParent(true);

        pauseMenuStage.addActor(table);
    }

    private void buildGameOverStage()
    {
        gameOverStage = new Stage();
        //gameOverStage.getViewport().setCamera(camera);

        final Label.LabelStyle largeLabelStyle = new Label.LabelStyle(game.largeFont, new Color(1.0f, 0.0f, 0.0f, 0.7f));
        final Label.LabelStyle smallLabelStyle = new Label.LabelStyle(game.smallFont, new Color(1.0f, 1.0f, 1.0f, 0.7f));

        final Label gameOverLabel = new Label("Game Over", largeLabelStyle);
        final Label continueLabel = new Label(" ", smallLabelStyle);

        Table table = new Table(game.skin);
        table.add(gameOverLabel).center().row();
        table.add(continueLabel).center().top().row();

        table.setFillParent(true);

        gameOverStage.addActor(table);
    }

    private void buildGameOverContinueStage()
    {
        gameOverStage = new Stage();
        //gameOverStage.getViewport().setCamera(camera);

        final Label.LabelStyle largeLabelStyle = new Label.LabelStyle(game.largeFont, new Color(0.7f, 0.0f, 0.0f, 0.7f));
        final Label.LabelStyle smallLabelStyle = new Label.LabelStyle(game.smallFont, new Color(1.0f, 1.0f, 1.0f, 0.7f));

        final Label gameOverLabel = new Label("Game Over", largeLabelStyle);
        final Label continueLabel = new Label("tap to continue", smallLabelStyle);

        Table table = new Table(game.skin);
        table.add(gameOverLabel).center().row();
        table.add(continueLabel).center().top().row();

        table.setFillParent(true);

        gameOverStage.addActor(table);
    }

	@Override
	public void render(float delta)
    {
        switch(gameState) {
            case INTRO:
                updateIntro();
                renderGraphics();
                break;
            case RUN:
                update(delta);
                renderGraphics();
                break;
            case PAUSE:
                // throw up a pause menu
                renderGraphics();
                break;
            case OVER:
                gameOverUpdate(delta);
                renderGraphics();
                break;
        }
	}

    private void update(float delta)
    {
        game.gameTime += delta;
        updatePokables(delta);
        clearExpiredCircles();

        modeUpdate(delta);

        stepSpawn(delta);
    }

    private void updateIntro()
    {
        update(0.0f);

        if(Gdx.input.isTouched())
        {
            gameState = GameState.RUN;
            im.setProcessors(
                    new Array<InputProcessor>(
                            new InputProcessor[]{pauseButtonStage, new GameInputProcessor()}
                    )
            );
        }
    }

    private void clearExpiredCircles()
    {
        Iterator<Pokable> it = pokables.iterator();
        while(it.hasNext())
        {
            Pokable pokable = it.next();

            if(pokable.getState() == Pokable.PokableState.DESTRUCT)
            {
                it.remove();
            }
            if(pokable.getState() == Pokable.PokableState.EXPIRED)
            {
                it.remove();
                pokablesExpired++;
            }
        }
    }

    private void modeUpdate(float delta)
    {
        switch(game.mode)
        {
            case PLAY:
                playUpdate(delta);
                break;
            case PRACTICE:
                practiceUpdate(delta);
                break;
            case NONE:
                gameState = GameState.OVER;
                break;
        }
    }

    private void playUpdate(float delta)
    {
        if(pokablesExpired > 0)
        {
            gameState = GameState.OVER;
        }
    }

    private void practiceUpdate(float delta)
    {
        // Nothing to update, just here to look pretty for now.
    }

    private void updatePokables(float delta)
    {
        for(Pokable pokable : pokables)
        {
            pokable.update(delta);
        }
    }

    private void renderGraphics() {
        renderSetup();

        game.shape.begin(ShapeRenderer.ShapeType.Filled);
        renderPokables();
        game.shape.end();

        game.batch.begin();
        renderText();
        if (gameState != GameState.RUN)
            game.batch.draw(game.pauseBG, 0.0f, 0.0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // dims the screen
        game.batch.end();

        game.batch.begin();
        switch (gameState)
        {
            case INTRO:
                introStage.draw();
                break;
            case RUN:
                pauseButtonStage.draw();
                break;
            case PAUSE:
                pauseMenuStage.draw();
                break;
            case OVER:
                gameOverStage.draw();
                break;
        }
        game.batch.end();
    }

    private void renderPokables()
    {
        for(Pokable pokable : pokables)
        {
            pokable.render();
        }
    }

    private void renderText()
    {
        Color colorBackup = game.smallFont.getColor();
        game.smallFont.setColor(1.0f, 1.0f, 1.0f, 0.5f);

        switch(game.mode) {
            case PLAY:
                renderTimeText();
                break;
            case PRACTICE:
                renderTimeText();
                renderHitsText();
                break;
        }

        game.smallFont.setColor(colorBackup);
    }

    private void renderHitsText()
    {
        String pointsText = game.hits + "p";

        float pointsTextWidth = game.smallFont.getBounds(pointsText).width;
        float pointsTextHeight = game.smallFont.getBounds(pointsText).height;

        float centerPointsText = Gdx.graphics.getWidth() / 2.0f - pointsTextWidth / 2.0f;
        float bottomPointsText = (Constants.BUTTON_LENGTH*game.ppm) / 2.0f - pointsTextHeight / 2.0f;

        game.smallFont.draw(game.batch, pointsText, centerPointsText, bottomPointsText);
    }

    private void renderTimeText()
    {
        String formattedTime = String.format("%.2fs", game.gameTime);

        float timeTextWidth = game.smallFont.getBounds(formattedTime).width;
        float timeTextHeight = game.smallFont.getBounds(formattedTime).height;

        float centerTimeText = game.worldWidth * game.ppm / 2.0f - timeTextWidth / 2.0f;
        float topTimeText = game.worldHeight * game.ppm - timeTextHeight / 2.0f;

        game.smallFont.draw(game.batch, formattedTime, centerTimeText, topTimeText);
    }

    private void renderSetup()
    {
        Gdx.gl20.glClearColor(Constants.BG_COLOR.r, Constants.BG_COLOR.g, Constants.BG_COLOR.b, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // tell the camera to update its matrices.
        //camera.update();
        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        //game.batch.setProjectionMatrix(camera.combined);

        //game.batch.enableBlending();
        //game.batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    private void forceSpawn()
    {
        newSpawn();
        nextSpawnTime = 0.0f;
        stepSpawn(0.0f);
    }

    private void stepSpawn(float delta) {
        spawnWaitTime += delta;
        if (spawnWaitTime >= nextSpawnTime) {
            for (int i = 0; i < spawnCount; i++)
                spawnPokable();
            newSpawn();
        }
    }

    private void newSpawn() {
        spawnWaitTime = 0.0f;
        nextSpawnTime = MathUtils.random(Constants.MINIMUM_SPAWN_TIME, Constants.MAXIMUM_SPAWN_TIME);
        spawnCount = MathUtils.random(Constants.MINIMUM_SPAWN_COUNT, Constants.MAXIMUM_SPAWN_COUNT);
    }

    private void spawnPokable()
    {
        Pokable pokable;

        int attempts = 10;
        while (attempts > 0)
        {
            pokable = generateRandomPokable();

            if (!collidesWithPokables(pokable))
            {
                pokables.add(pokable);
                attempts = 0;
            }

            attempts--;
        }
    }

    private Pokable generateRandomPokable()
    {
        Circle newCircle = new Circle();
        newCircle.radius = MathUtils.random(Constants.MINIMUM_CIRCLE_RADIUS, Constants.MAXIMUM_CIRCLE_RADIUS);
        newCircle.x = MathUtils.random(0.0f + newCircle.radius, game.worldWidth - newCircle.radius);
        newCircle.y = MathUtils.random(Constants.BUTTON_LENGTH + newCircle.radius,
                                       game.worldHeight - newCircle.radius);

        /*Color newColor = new Color();
        newColor.r = MathUtils.random(0.5f, 1.0f);
        newColor.g = MathUtils.random(0.5f, 1.0f);
        newColor.b = MathUtils.random(0.5f, 1.0f);
        newColor.a = 1.0f;*/

        HSL newHSL = new HSL();
        newHSL.h = MathUtils.random(0.0f, 1.0f);
        newHSL.s = 0.7f;
        newHSL.l = 0.7f;

        Color newColor = newHSL.toRGB();

        float newLifeSpan = MathUtils.random(Constants.MINIMUM_LIFE_TIME, Constants.MAXIMUM_LIFE_TIME);

        return new Pokable(game, newCircle, newColor, newLifeSpan);
    }

    private boolean collidesWithPokables(Pokable testPokable)
    {
        for (Pokable otherPokable : pokables) {
            if (otherPokable.collidesWith(testPokable)) {
                return true;
            }
        }
        return false;
    }

    private void gameOverUpdate(float delta)
    {
        // Some last minute updates
        gameEndWait += delta;


        if(gameEndWait >=  TAP_TO_END_START_TIME && !tapToEnd)
        {
            buildGameOverContinueStage();
            tapToEnd = true;
        }

        if(tapToEnd && Gdx.input.isTouched() || gameEndWait >= GAME_END_PAUSE)
            showResultsScreen();
    }

    private void showResultsScreen()
    {
        switch(game.mode)
        {
            case PLAY:
                game.setScreen(new PlayResultsScreen(game));
                this.dispose();
                break;
            case PRACTICE:
                game.setScreen(new PracticeResultsScreen(game));
                this.dispose();
                break;
        }
    }

    @Override
    public void resize(int width, int height)
    {
        game.resize(width, height);
        disposeStages();
        buildStages();
    }

	@Override
	public void show() {
		// start the playback of the background music
		// when the screen is shown
		//music.play();
        // There is no music good enough for poke! HA!
        // Except the music you choose, of course :)
        if(gameState == GameState.PAUSE)
        {
            setGameState(GameState.RUN);
        }
	}

	@Override
	public void hide()
    {
        if(gameState == GameState.RUN)
        {
            setGameState(GameState.PAUSE);
        }
	}

	@Override
	public void pause()
    {
        if(gameState == GameState.RUN)
        {
            setGameState(GameState.PAUSE);
        }
	}

	@Override
	public void resume()
    {
        if(gameState == GameState.PAUSE)
        {
            setGameState(GameState.RUN);
        }
	}

	@Override
	public void dispose() {
        disposeStages();
	}

    public class GameInputProcessor implements InputProcessor {
        @Override
        public boolean keyDown (int keycode) {
            return false;
        }

        @Override
        public boolean keyUp (int keycode) {
            return false;
        }

        @Override
        public boolean keyTyped (char character) {
            return false;
        }

        @Override
        public boolean touchDown (int pixelX, int pixelY, int pointer, int button)
        {
            if (gameState == GameState.RUN || gameState == GameState.INTRO)
            {
                float worldX = (float) pixelX / game.ppm;
                float worldY = game.worldHeight - (float) pixelY / game.ppm;

                boolean miss = true;

                for(Pokable pokable : pokables)
                {
                    if (pokable.getHittableCircle().contains(worldX, worldY))
                    {
                        game.hits++;

                        pokable.setState(Pokable.PokableState.HIT);
                        miss = false;

                        float radiusPercent = pokable.getVisualRadius() / Constants.MAXIMUM_CIRCLE_RADIUS;
                        float volume = Constants.DEFAULT_VOLUME;
                        float pitch = Constants.RADIUS_PERCENT_TO_PITCH * radiusPercent + Constants.PITCH_ADJUSTMENT;
                        float pan = Constants.DEFAULT_PAN;
                        game.hitSound.play(volume, pitch, pan);
                    }
                }

                if (miss)
                {
                    game.missSound.play();
                    game.missed++;
                }
            }

            return false;
        }

        @Override
        public boolean touchUp (int x, int y, int pointer, int button) {
            return false;
        }

        @Override
        public boolean touchDragged (int x, int y, int pointer) {
            return false;
        }

        @Override
        public boolean mouseMoved (int x, int y) {
            return false;
        }

        @Override
        public boolean scrolled (int amount) {
            return false;
        }
    }
}
