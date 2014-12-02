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

package com.andgate.poke.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by zetta on 10/28/14.
 */
public class HSL
{
    public float h;
    public float s;
    public float l;

    public HSL()
    {
        this(0.0f, 0.0f, 0.0f);
    }

    public HSL(Color color)
    {
        Vector3 hslVec = rgbToHsl(color);
        h = hslVec.x;
        s = hslVec.y;
        l = hslVec.z;
    }

    public HSL(float h, float s, float l)
    {
        this.h = s;
        this.s = s;
        this.l = l;
    }

    /**
     * Converts an HSL color value to RGB. Conversion formula
     * adapted from http://en.wikipedia.org/wiki/HSL_color_space.
     * Assumes h, s, and l are contained in the set [0, 1] and
     * returns r, g, and b in the set [0, 1].
     *
     * @return The RGB representation
     */
    public Color toRGB()
    {
        float r, g, b;

        if(s == 0)
        {
            r = l;
            g = l;
            b = l;
        }
        else
        {
            float q = (l < 0.5f) ? (l * (1.0f + s)) : (l + s - l * s);
            float p = 2.0f * l - q;
            r = hue2rgb(p, q, h + 1.0f / 3.0f);
            g = hue2rgb(p, q, h);
            b = hue2rgb(p, q, h - 1.0f / 3.0f);
        }

        return new Color(r, g, b, 1.0f);
    }

    private static float hue2rgb(float p, float q, float t)
    {
        if(t < 0.0f) t += 1.0f;
        if(t > 1.0f) t -= 1.0f;
        if(t < 1.0f / 6.0f) return p + (q - p) * 6.0f * t;
        if(t < 1.0f / 2.0f) return q;
        if(t < 2.0f / 3.0f) return p + (q - p) * (2.0f / 3.0f - t) * 6.0f;
        return p;
    }

    /**
     * Converts an RGB color value to HSL. Conversion formula
     * adapted from http://en.wikipedia.org/wiki/HSL_color_space.
     * Assumes r, g, and b are contained in the set [0, 1] and
     * returns h, s, and l in the set [0, 1].
     *
     * @param rgba the could value of the
     * @return           The HSL representation
     */
    private static Vector3 rgbToHsl(Color rgba)
    {
        float r = rgba.r;
        float g = rgba.g;
        float b = rgba.b;

        float max = (r > g && r > b) ? r : (g > b) ? g : b;
        float min = (r < g && r < b) ? r : (g < b) ? g : b;

        float h, s, l;
        h = s = l = (max + min) / 2.0f;

        if(max == min){
            h = s = 0.0f;
        }else {
            float d = max - min;
            s = l > 0.5f ? d / (2.0f - max - min) : d / (max + min);

            if (r > g && r > b)
                h = (g - b) / d + (g < b ? 6.0f : 0.0f);
            else if(g > b)
                h = (b - r) / d + 2.0f;
            else
                h = (r - g) / d + 4.0f;

            h /= 6.0f;
        }

        return new Vector3(h, s, l);
    }
}
