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

/**
 * Created by zetta on 10/28/14.
 */
public class HighScore
{
    public float time;

    public HighScore()
    {
        this(0.0f);
    }

    public HighScore(float time)
    {
        this.time = time;
    }
}
