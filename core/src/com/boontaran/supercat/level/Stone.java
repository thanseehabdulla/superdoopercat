package com.boontaran.supercat.level;

import com.boontaran.games.platformerLib.Entity;
import com.boontaran.supercat.SuperCat;

/**
 * Created by arifbs on 11/13/15.
 */
public class Stone extends Entity {
    //id that the trigger will look for
    public int id;
    //direction
    private boolean moveRight;
    private Level level;

    //states
    private boolean activated = false;
    private boolean expired = false;

    public Stone(Level level,int id,boolean moveRight) {
        this.level = level;
        this.id = id;
        this.moveRight = moveRight;
        //collision radius
        setRadius(47);

        restitution = 0.7f;
        friction = 0;

        setNoCollision(true);
    }
    public float getDamage() {
        return 2;
    }

    //start rolling
    public void setActivated() {
        if(activated) return;

        activated=true;
        edgeUpdateLimRatio = 3f;

        //set the display
        setImage(SuperCat.createImage("stone"));
        setNoCollision(false);
    }

    @Override
    public void update(float delta) {
        if(activated) {
            float v,a;

            //define the speed based on the direction
            v = 300;
            a = -520;

            if(!moveRight) {
                v = -v;
                a = -a;
            }

            //set speed & angular speed
            setVX(v);
            setASpeed(a);
        }

    }


    @Override
    public void onSkipUpdate(float delta) {
        if(expired) {
            //out of screen & has been expired
            //remove this
            level.removeEntity(this);
        }
    }

    //hitting a wall
    @Override
    protected void hitWall(Entity ent) {
        if(activated) {
            activated = false;
            expired = true;

            setNoLandCollision(true);
            SuperCat.media.playSound("hit_ground.mp3");
        }
    }

    //hit ground, just play a sound
    @Override
    protected void hitLand(Entity ent) {
        super.hitLand(ent);
        SuperCat.media.playSound("hit_ground.mp3");
    }
}
