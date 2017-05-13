package com.boontaran.supercat.level;

import com.badlogic.gdx.utils.Pool;
import com.boontaran.games.platformerLib.Entity;
import com.boontaran.supercat.SuperCat;

/**
 * Created by arifbs on 12/9/15.
 *
 * hit splash animation
 */
public class Hit extends Entity implements Pool.Poolable {
    private final Pool<Hit> pool;
    private float time;

    public Hit(Pool<Hit> pool) {
        this.pool=pool;
        setImage(SuperCat.createImage("hit"));

        //no interaction
        setNoLandCollision(true);
        setNoCollision(true);
        noGravity = true;

        reset();
    }

    //out screen, remove this
    @Override
    public void onSkipUpdate(float delta) {
        world.removeEntity(this);
        pool.free(this);
    }

    @Override
    public void update(float delta) {
        //remove when time's up
        time -= delta;
        if(time < 0) {
            world.removeEntity(this);
            pool.free(this);
        }
    }

    //restore timer
    @Override
    public void reset() {
        time = 0.1f;
    }
}
