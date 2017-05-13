package com.boontaran.supercat.level;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.boontaran.games.Clip;
import com.boontaran.games.platformerLib.Entity;
import com.boontaran.supercat.SuperCat;

/**
 * Created by arifbs on 10/21/15.
 */
public class Coin extends Entity {
    //event
    public static final int REMOVE = 1;

    protected final Clip clip;
    //public int id;

    public Coin() {
        //set the animation
        clip = new Clip(SuperCat.getRegion("coin"),36,36);
        setClip(clip);
        setRadius(14);
        setFloat(true);
    }

    //set this object as the referece to synchronize all coins animation
    public void setAsReference() {
        ignoreSkipDraw = true;
        clip.setFPS(12);
        clip.playAllFrames(true);
    }


    private float time;
    //if animation only, using when the coins are in mystery box and hit from below.
    public void setAnimationOnly() {
        setNoLandCollision(true);
        setNoCollision(true);
        noGravity = false;
        time = 0.4f;
    }

    @Override
    public void update(float delta) {
        //if animation only, remove after the time's up
        if(time > 0) {
            time -= delta;
            if(time <0) {
                fireEvent(REMOVE);
            }
        }
    }


    public void setFloat(boolean floating) {
        if(floating) {
            //floating in air
            setNoLandCollision(true);
            noGravity = true;
        } else {
            setNoLandCollision(false);
            noGravity = false;
        }
    }
}
