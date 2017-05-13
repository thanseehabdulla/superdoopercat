package com.boontaran.supercat.level;

import com.boontaran.games.platformerLib.Entity;
import com.boontaran.supercat.SuperCat;

/**
 * Created by arifbs on 11/16/15.
 *
 * key to open all doors
 */
public class Key extends Entity {
    public Key() {
        setSize(25,42);
        setImage(SuperCat.createImage("key"));
    }

}
