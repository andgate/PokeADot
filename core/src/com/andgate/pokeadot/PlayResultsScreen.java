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

package com.andgate.pokeadot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class PlayResultsScreen implements Screen
{
    private final PokeADot game;

    private Stage stage;

    public PlayResultsScreen(PokeADot currentGame)
    {
        game = currentGame;
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        boolean newHighScore = false;
        if(game.mode == PokeADot.GameMode.PLAY)
        {
            HighScore currentHighScore = HighScoreService.get();
            if(game.gameTime >= currentHighScore.time)
            {
                HighScoreService.set(new HighScore(game.gameTime));
                newHighScore = true;
            }
        }

        // Retry and exit buttons
        final ImageButton nextButton = game.createIconButton(Constants.GO_ICON_LOCATION, Constants.GO_ICON_DOWN_LOCATION,
                new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y){
                        game.buttonPressedSound.play();
                        goMainMenu();
                    }
                });

        final ImageButton replayButton = game.createIconButton(Constants.REPLAY_ICON_LOCATION, Constants.REPLAY_ICON_DOWN_LOCATION,
                new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y){
                        game.buttonPressedSound.play();
                        goReplay();
                    }
                });

        // display information about the game depending on the currently selected mode.
        final Label.LabelStyle titleLabelStyle = new Label.LabelStyle(game.largeFont, Color.ORANGE);
        final Label.LabelStyle infoLabelStyle = new Label.LabelStyle(game.mediumSmallFont, Color.LIGHT_GRAY);
        final Label.LabelStyle newHighScoreLabelStyle = new Label.LabelStyle(game.mediumSmallFont, Color.RED);

        final Label resultsLabel = new Label("Result", titleLabelStyle);
        final Label timeLabel = new Label(String.format("Time: %.2f", game.gameTime) + " seconds", infoLabelStyle);
        final Label newHighScoreLabel = new Label("New high score!", newHighScoreLabelStyle);

        //table.debugAll();

        final Table resultsTable = new Table();
        resultsTable.add(resultsLabel).center().spaceBottom(25.0f).row();
        resultsTable.add(timeLabel).top().center().row();

        if(newHighScore) {
            resultsTable.add(newHighScoreLabel).top().center().row();
        }

        final Table buttonsTable = new Table();
        buttonsTable.add(replayButton).left();
        buttonsTable.add(nextButton).expandX().right();

        final Table table = new Table();
        table.setFillParent(true);
        table.add(resultsTable).top().center().spaceBottom(25.0f).row();
        //table.add(replayButton).left();
        //table.add(nextButton).right();
        table.add(buttonsTable).fill();

        stage.addActor(table);
    }

    private void goMainMenu()
    {
        game.resetGame();
        game.setScreen(new MainMenuScreen(game));
        this.dispose();
    }

    private void goReplay()
    {
        PokeADot.GameMode mode = game.mode;

        game.resetGame();
        game.setScreen(new GameScreen(game, mode));
        this.dispose();
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl20.glClearColor(Constants.BG_COLOR.r, Constants.BG_COLOR.g, Constants.BG_COLOR.b, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        stage.draw();
        game.batch.end();
    }

    @Override
    public void resize(int width, int height)
    {
        stage.getViewport().update(width, height, true);
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
