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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MainMenuScreen implements Screen
{
	private final Poke game;
    private Stage stage;

    private static final String PLAY_BUTTON_TEXT = "Play";
    private static final String PRACTICE_BUTTON_TEXT = "Practice";
    private static final String BUY_BUTTON_TEXT = "Buy";

	public MainMenuScreen(final Poke newGame) {
        game = newGame;
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        final LabelStyle titleLabelStyle = new LabelStyle(game.largeFont, Color.CYAN);
        final Label titleLabel = new Label(game.GAME_NAME, titleLabelStyle);


        final TextButtonStyle buttonStyle = new TextButtonStyle(game.skin.getDrawable("default-round"),
                game.skin.getDrawable("default-round-down"),
                game.skin.getDrawable("default-round"),
                game.mediumFont);

        final TextButton playButton = new TextButton(PLAY_BUTTON_TEXT, buttonStyle);
        final TextButton practiceButton = new TextButton(PRACTICE_BUTTON_TEXT, buttonStyle);
        final TextButton buyButton = new TextButton(BUY_BUTTON_TEXT, buttonStyle);

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.buttonPressedSound.play();
                game.setScreen(new GameScreen(game, Poke.GameMode.PLAY));
                MainMenuScreen.this.dispose();
            }
        });

        practiceButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.buttonPressedSound.play();
                game.setScreen(new GameScreen(game, Poke.GameMode.PRACTICE));
                MainMenuScreen.this.dispose();
            }
        });

        buyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.net.openURI("https://play.google.com/store/apps/details?id=com.andgate.poke");
            }
        });

        final LabelStyle currentHighScoreLabelStyle = new LabelStyle(game.smallFont, Color.ORANGE);
        final String currentHighScore = String.format("%.2f", HighScoreService.get().time);
        final Label currentHighScoreLabel = new Label("Best time: " + currentHighScore, currentHighScoreLabelStyle);

        Table table = new Table();

        table.add(titleLabel).center().spaceBottom(25.0f).row();

        table.add(currentHighScoreLabel).center().spaceBottom(25.0f).row();

        table.add(playButton).fill().spaceBottom(20.0f).center().row();
        table.add(practiceButton).fill().spaceBottom(20.0f).center().row();

        if(game.isFree)
        {
            table.add(buyButton).fill().spaceBottom(20.0f).center().row();
        }

        table.setFillParent(true);

        stage.addActor(table);
	}

	@Override
	public void render(float delta)
    {
        Gdx.gl20.glClearColor(game.BG_COLOR.r, game.BG_COLOR.g, game.BG_COLOR.b, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        stage.draw();
        game.batch.end();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
        stage.dispose();
	}
}
