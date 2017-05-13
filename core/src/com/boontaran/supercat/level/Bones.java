package com.boontaran.supercat.level;

import com.boontaran.games.platformerLib.Entity;
import com.boontaran.supercat.SuperCat;

/**
 * Created by arifbs on 11/19/15.
 */
public class Bones extends Entity {

    public int num; //number of bones to get when tahe this item
    public Bones(int num) {
        this.num = num;
        setSize(48,50);
        setImage(SuperCat.createImage("bones"));
    }
}
