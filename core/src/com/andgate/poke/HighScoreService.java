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
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

public class HighScoreService
{
    private static final String HIGHSCORE_EXTERNAL_PATH
            = "Android/data/com.andgate.poke/highscore.data";

    public static HighScore get()
    {
        FileHandle saveFile = Gdx.files.external(HIGHSCORE_EXTERNAL_PATH);

        if(saveFile.exists())
        {

            String jsonString;
            try
            {
                jsonString = Base64Coder.decodeString(saveFile.readString());
            }
            catch (java.lang.IllegalArgumentException e)
            {
                // The highscore has been tampered with,
                // so now they get a new one.
                return new HighScore();
            }

            Json json = new Json();
            /*json.setTypeName(null);
            json.setUsePrototypes(false);
            json.setIgnoreUnknownFields(true);
            json.setOutputType(JsonWriter.OutputType.json);*/

            HighScore highScore = json.fromJson(HighScore.class, jsonString);
            if(highScore != null)
            {
                return highScore;
            }
        }

        return new HighScore();
    }

    public static void set(HighScore newHighScore)
    {
        Json json = new Json();
        String jsonString = json.toJson(newHighScore);
        String encodedString = Base64Coder.encodeString(jsonString);
        FileHandle saveFile = Gdx.files.external(HIGHSCORE_EXTERNAL_PATH);
        saveFile.writeString(encodedString, false);
    }
}
