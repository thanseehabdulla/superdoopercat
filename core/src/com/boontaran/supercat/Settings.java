package com.boontaran.supercat;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by arifbs on 12/22/15.
 */
public class Settings {
    //num of worlds/ level list pages
    public static final int WORLDS = 1;

    //arrange the table of levels in one world
    public static final int ROWS = 2;
    public static final int COLS = 4;

    //level list page background
    public static final Map<Integer, String> PAGE_BACKGROUNDS;
    static
    {
        PAGE_BACKGROUNDS = new HashMap<Integer, String>();
        //Set custom background on level list page

        /*
        put the background file in raw_graphics/bg and run Packer
        Example :

        PAGE_BACKGROUNDS.put(3, "level_list_bg_custom");   // page 3 use custom bg
        PAGE_BACKGROUNDS.put(5, "level_list_bg_custom2");   // page 5 use another custom bg
        */
    }


    //showing "coming soon" screen
    public static final boolean SHOW_COMING_SOON = true;

    //for Debugging only !!, unlock all levels
    public static final boolean UNLOCK_ALL = false;

    //level background
    public static final Map<Integer, String> LEVEL_BACKGROUNDS;
    static
    {
        LEVEL_BACKGROUNDS = new HashMap<Integer, String>();
        /*
        put the background file in raw_graphics/bg and run Packer
        Example

        LEVEL_BACKGROUNDS.put(3, "level3_bg"); // level 3 using "level3_bg.png"
        LEVEL_BACKGROUNDS.put(5, "level5_bg"); // level 5 using "level5_bg.png"

         */
    }

    //level music bg
    public static final Map<Integer, String> LEVEL_MUSICS;
    static
    {
        LEVEL_MUSICS = new HashMap<Integer, String>();

        /*
        put the music files in android/assets/musics folder

        Example :

        LEVEL_MUSICS.put(3, "level3.ogg");  // level 3 use other music
         */


    }

}
