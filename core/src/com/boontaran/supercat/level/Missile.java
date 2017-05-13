package com.boontaran.supercat.level;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool;
import com.boontaran.games.Clip;
import com.boontaran.games.platformerLib.Entity;
import com.boontaran.games.spriter.SpriterClip;
import com.boontaran.supercat.SuperCat;

/**
 * Created by arifbs on 11/12/15.
 */
public class Missile extends Entity implements Pool.Poolable {
    public static final int EXPLODE = 1;
    private final Clip clip;


    private Level level;
    private float speed = 300;
    private float turnSpeed = 600; //max turn speed, deg per sec
    private float targetDeg;
    private boolean activated = false;
    private float activatedTime=0;

    public Missile(Level level) {
        this.level = level;

        //animation
        clip = new Clip(SuperCat.getRegion("missile"),120,36);
        setClip(clip);
        setRadius(18);
        contentOffsetX = -30;
        edgeUpdateLimRatio = 1.3f;

        reset();
    }
    public boolean isActivated() {
        return activated;
    }
    public float getDamage() {
        return 4;
    }
    public void start() {
        activatedTime = 0.5f;
    }
    private float checkTime = 0;
    private float directionTime = 0;


    @Override
    public void update(float delta) {
        if(!activated) {
            if(activatedTime > 0) {
                activatedTime -= delta;
                if(activatedTime <=0) {
                    activated = true;
                    noGravity = true;

                    //check hero position
                    checkHero();
                    //find the direction
                    findDirection();

                    //play frames
                    clip.playFrames(new int[]{1,2,3,4,3,2}, true);
                    SuperCat.media.playSound("missile.mp3");
                }
            }
            return;
        }

        //check hero position
        checkTime -= delta;
        if(checkTime <0) {
            checkTime = 0.6f;
            checkHero();

        }

        //check direction
        directionTime -= delta;
        if(directionTime <0) {
            directionTime = 0.1f;
            findDirection();
        }


    }

    private void findDirection() {
        //difference of current angle vs target angle
        float diffAngle = targetDeg - getRotation();

        //normalize
        if(diffAngle < -180) diffAngle += 360;
        if(diffAngle > 180) diffAngle -= 360;

        //calculate rotational speed, based on diff angle
        float tSpeed = diffAngle/360 * turnSpeed;
        setASpeed(tSpeed);

        //apply speed
        setVDeg(speed, getRotation());
    }
    private void checkHero() {
        Hero hero = level.getHero();

        //calculate target angle
        float dy = hero.getY()-getY();
        float dx = hero.getX()-getX();
        float heroAngle = MathUtils.atan2(dy,dx)*180f/3.14f;
        targetDeg = heroAngle;

    }

    //out of calculation, destroy this
    @Override
    public void onSkipUpdate(float delta) {
        explode();
    }

    //hit land, explode
    @Override
    public void hitEntityLand(Entity land) {
        explode();
    }

    //send explode event
    public void explode() {
        fireEvent(EXPLODE);
    }

    //back to object pool
    @Override
    public void reset() {
        activated = false;
        activatedTime=0;
        clip.singleFrame(0);
        setRotation(90);
        noGravity = false;
        setASpeed(0);
        setV(0,0);
    }
}
