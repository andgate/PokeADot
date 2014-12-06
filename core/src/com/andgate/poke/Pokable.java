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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Circle;

public class Pokable
{
	final Poke game;

    static enum PokableState {
        ACTIVE,
        HIT,
        DESTRUCT,
        EXPIRED
    }

    private PokableState pokableState = PokableState.ACTIVE;

    public void setState(PokableState newPokableState)
    {
        pokableState = newPokableState;
    }

    public PokableState getState()
    {
        return pokableState;
    }

    private Circle initialCircle;
    private Circle hittableCircle;
	private Circle visualCircle;
	private Color color;

    private float timeLimit;
    private float timeElapsed;

    private final static float NO_TIME = 0.0f;
	
	public Pokable(final Poke newGame, Circle newCircle, Color newColor, float newTimeLimit)
	{
		game = newGame;
		visualCircle = newCircle;
		color = newColor;
        initialCircle = new Circle(newCircle.x, newCircle.y, newCircle.radius);
        hittableCircle = new Circle(newCircle.x, newCircle.y, newCircle.radius);
        visualCircle = new Circle(newCircle.x, newCircle.y, newCircle.radius);

        timeLimit = newTimeLimit;
        timeElapsed = NO_TIME;
	}

    public void update(float delta)
    {
        switch(pokableState)
        {
            case ACTIVE:
                updateActive(delta);
                break;
            case HIT:
                // play the hit sequence
                updateHit(delta);
                break;
            case DESTRUCT:
                break;
            case EXPIRED:
                break;
        }
    }

    private void updateActive(float delta)
    {
        timeElapsed += delta;
        if(visualCircle.radius <= 0.0f)
        {
            pokableState = PokableState.EXPIRED;
            hittableCircle.radius = 0.0f;
        }
        else
        {
            visualCircle.radius = initialCircle.radius * (1.0f - timeElapsed/timeLimit);
            hittableCircle.radius = visualCircle.radius >= Constants.MINIMUM_CIRCLE_RADIUS
                                    ? visualCircle.radius
                                    : Constants.MINIMUM_CIRCLE_RADIUS;
        }
    }

    private void updateHit(float delta)
    {
        pokableState = PokableState.DESTRUCT; // destruct when sequence finishes
    }

	public void render()
	{
        switch(pokableState)
        {
            case ACTIVE:
                renderActiveCircle();
                break;
            case HIT:
                // render hit sequence
                break;
            case DESTRUCT:
                break;
        }
	}

    public void renderActiveCircle()
    {
        float outerRadius = visualCircle.radius;
        float innerRadius = outerRadius - Constants.CIRCLE_BORDER_THICKNESS;
        if(innerRadius < 0.0f)
        {
            innerRadius = 0.0f;
        }

        game.shape.setColor(color.r - 0.3f, color.g - 0.3f, color.b - 0.3f, color.a);
        game.shape.circle(visualCircle.x * game.ppm, visualCircle.y * game.ppm, outerRadius * game.ppm, Constants.CIRCLE_SEGMENTS);

        game.shape.setColor(color);
        game.shape.circle(visualCircle.x * game.ppm, visualCircle.y * game.ppm, innerRadius * game.ppm, Constants.CIRCLE_SEGMENTS);
    }

    public boolean collidesWith(Pokable otherPokable)
    {
        return otherPokable.getVisualCircle().overlaps(visualCircle);
    }

    public float getVisualRadius()
    {
        return visualCircle.radius;
    }

    public Circle getHittableCircle()
    {
        return hittableCircle;
    }

    public Circle getVisualCircle()
    {
        return hittableCircle;
    }
}
