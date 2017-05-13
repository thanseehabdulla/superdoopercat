package com.boontaran.supercat.level.enemies;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.boontaran.brashmonkey.spriter.Animation;
import com.boontaran.brashmonkey.spriter.Mainline;
import com.boontaran.brashmonkey.spriter.Player;
import com.boontaran.games.Clip;
import com.boontaran.games.platformerLib.Entity;
import com.boontaran.games.spriter.SpriterClip;
import com.boontaran.supercat.level.Hero;
import com.boontaran.supercat.level.Level;

/**
 * Created by arifbs on 10/22/15.
 */
public abstract class Enemy extends Entity {
    //events
    public static final int REMOVE = 1;
    public static final int BEATEN = 2;


    protected float health = 2;
    protected float damage = 1;

    //moving speed
    protected float speed = 100;

    //timer & the interval to check hero position
    protected float checkHeroInterval = 2;
    protected float checkHeroTime;

    //range to see hero
    protected float rangeX = 300;
    protected float rangeY = 100;

    protected Level level;

    public Enemy(Level level) {
        this.level = level;
        restitution = 0;

        //prevent no drawing when the enemy at the screen edge
        drawEdgeTol = 64;
    }
    protected void die() {
        setNoCollision(true);
        setNoLandCollision(true);
        fireEvent(BEATEN);
    }



    public float getDamage() {
        return damage;
    }


    //out screen
    @Override
    public void onSkipUpdate(float delta) {
        if(health <= 0) {
            //already die, remove it
            fireEvent(REMOVE);
        }
    }

    public boolean hasDied() {
        return health <=0;
    }


    //hit wall, flip it
    @Override
    protected void hitWall(Entity ent) {
        if(ent.getX() < getX()) {
            setMoveRight(true);
        }
        else if(ent.getX() > getX()) {
            setMoveRight(false);
        }
    }

    public void flip() {
        if(isMoveRight()) {
            setMoveRight(false);
        } else {
            setMoveRight(true);
        }
    }

    @Override
    public void update(float delta) {
        //timer to check hero position
        checkHeroTime -=delta;
        if(checkHeroTime < 0) {
            checkHeroTime = (float) (checkHeroInterval + Math.random()*checkHeroInterval/2);
            checkHero();
        }
    }

    public abstract void touchHero(Hero hero);

    private void checkHero() {
        Hero hero =  level.getHero();

        //calculate x distance
        float dx = hero.getX()-getX();
        //if not in range
        if(dx > rangeX || dx < -rangeX) return;

        //calculate y distance
        float dy = hero.getY()-getY();
        //if not in range
        if(dy > rangeY || dy <-rangeY) return;

        //is line of sight?
        if(!level.isLOS(this,hero,32,rangeX*1.5f)) {
            return;
        }

        //change direction
        if(dx>0 && !isMoveRight()) {
            flip();
        }
        else if(dx<0 && isMoveRight()) {
            flip();
        }

        //enemy see hero
        seeHero();
    }

    protected void seeHero() {
        //implemented by sub class
    }


    public boolean isMoveRight() {
        return getScaleX() < 0;
    }
    public void setMoveRight(boolean right) {
        if(right) {
            setScaleX(-1);
        } else {
            setScaleX(1);
        }
    }

    public abstract void attackedBy(Entity ent);
}
