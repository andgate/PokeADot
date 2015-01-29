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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Circle;

public class Pokable
{
	final PokeADot game;

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

    private float activeSpeed;
    private float implodeSpeed;
    private float explodeSpeed;

    private final static float NO_TIME = 0.0f;

    private static final float MAX_POP_TIME
            = 0.25f;
    private static final float MAX_IMPLODE_TIME
            = MAX_POP_TIME * (2.0f / 3.0f);
    private static final float MAX_EXPLODE_TIME
            = MAX_POP_TIME * (1.0f / 3.0f);
    private final static float EXPLODE_FACTOR
            = 0.2f;
	
	public Pokable(final PokeADot game, Circle circle, Color color, float timeLimit)
	{
		this.game = game;
		this.color = new Color(color);
        this.initialCircle = new Circle(circle);
        this.hittableCircle = new Circle(circle);
        this.visualCircle = new Circle(circle);

        this.timeLimit = timeLimit;
        timeElapsed = NO_TIME;

        activeSpeed = -(initialCircle.radius / timeLimit);
        implodeSpeed = -(initialCircle.radius / MAX_IMPLODE_TIME);
        explodeSpeed = initialCircle.radius * EXPLODE_FACTOR / MAX_EXPLODE_TIME;
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
        grow(activeSpeed * delta, PokableState.EXPIRED);
    }

    private float explodeAccumulator = 0.0f;
    private void updateHit(float delta)
    {
        float distance = implodeSpeed * delta;

        explodeAccumulator += delta;
        if(explodeAccumulator < MAX_EXPLODE_TIME)
        {
            distance = explodeSpeed * delta;
        }

        grow(distance, PokableState.DESTRUCT);
    }

    private void grow(final float distance, final PokableState endState)
    {
        if(visualCircle.radius <= 0.0f)
        {
            pokableState = endState;
            visualCircle.radius = 0.0f;
        }
        else
        {
            visualCircle.radius += distance;
            if(endState == PokableState.EXPIRED)
            {
                hittableCircle.radius = visualCircle.radius >= Constants.MINIMUM_CIRCLE_RADIUS
                        ? visualCircle.radius
                        : Constants.MINIMUM_CIRCLE_RADIUS;
            }
            else
            {
                hittableCircle.radius = 0.0f;
            }
        }
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
                renderActiveCircle();
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
