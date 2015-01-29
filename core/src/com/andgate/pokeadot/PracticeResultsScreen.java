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
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.text.DecimalFormat;

public class PracticeResultsScreen implements Screen
{
    private final PokeADot game;

    private Stage stage;

    private static final DecimalFormat doubleDecimal = new DecimalFormat("0.00");

    public PracticeResultsScreen(PokeADot currentGame)
    {
        game = currentGame;
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        // Retry and exit buttons
        ImageButton nextButton = game.createIconButton(Constants.GO_ICON_LOCATION, Constants.GO_ICON_DOWN_LOCATION,
                new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y){
                        game.buttonPressedSound.play();
                        goMainMenu();
                    }
                });

        ImageButton replayButton = game.createIconButton(Constants.REPLAY_ICON_LOCATION, Constants.REPLAY_ICON_DOWN_LOCATION,
                new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y){
                        game.buttonPressedSound.play();
                        goReplay();
                    }
                });

        // display information about the game depending on the currently selected mode.
        LabelStyle titleLabelStyle = new LabelStyle(game.largeFont, Color.ORANGE);
        LabelStyle infoLabelStyle = new LabelStyle(game.mediumSmallFont, Color.LIGHT_GRAY);

        Label resultsLabel = new Label("Results", titleLabelStyle);

        Label timeTitleLabel = new Label("Time: ", infoLabelStyle);
        Label timeLabel = new Label(doubleDecimal.format(game.gameTime) + " seconds", infoLabelStyle);

        Label hitsTitleLabel = new Label("Hit: ", infoLabelStyle);
        Label hitsLabel = new Label(((int)game.hits) + " p", infoLabelStyle);

        Label missedTitleLabel = new Label("Missed: ", infoLabelStyle);
        Label missedLabel = new Label(((int)game.missed) + " p", infoLabelStyle);

        String accuracyString;
        if(game.missed > 0) {
            float accuracy = (float) game.hits / (game.hits + game.missed);
            accuracyString = doubleDecimal.format(accuracy) + "%";
        } else {
            accuracyString = "-";
        }

        Label acurracyTitleLabel = new Label("Accuracy: ", infoLabelStyle);
        Label accuracyLabel = new Label(accuracyString, infoLabelStyle);

        Table table = new Table();
        //table.debugAll();

        table.add(resultsLabel).center().spaceBottom(25.0f).row();

        table.add(timeTitleLabel).top().left();
        table.add(timeLabel).expand().top().left().row();

        table.add(hitsTitleLabel).top().left();
        table.add(hitsLabel).expand().top().left().row();

        table.add(missedTitleLabel).top().left();
        table.add(missedLabel).expand().top().left().row();

        table.add(acurracyTitleLabel).top().left();
        table.add(accuracyLabel).expand().top().left().row();

        table.add(replayButton).bottom().center();
        table.add(nextButton).expand().bottom().center();

        table.setFillParent(true);

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
