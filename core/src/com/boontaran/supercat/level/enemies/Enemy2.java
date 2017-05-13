package com.boontaran.supercat.level.enemies;

import com.boontaran.games.Clip;
import com.boontaran.games.platformerLib.Entity;
import com.boontaran.supercat.SuperCat;
import com.boontaran.supercat.level.Level;

/**
 * Created by arifbs on 11/11/15.
 */
public class Enemy2 extends Enemy1 {
    public static final int THROW_BULLET = 200;

    public Enemy2(Level level) {
        super(level);
        checkHeroInterval = 1;
        rangeX = 400;
    }

    @Override
    protected Clip createClip() {
        contentOffsetY = 5;
        return new Clip(SuperCat.getRegion("enemy2"),80,84);
    }


    @Override
    protected void seeHero() {
        //throw bullet
        changeState(ATTACK);
        fireEvent(THROW_BULLET);
        SuperCat.media.playSound("throw_weapon.mp3");
    }
}
