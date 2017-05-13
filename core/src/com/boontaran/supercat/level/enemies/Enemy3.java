package com.boontaran.supercat.level.enemies;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.boontaran.brashmonkey.spriter.Animation;
import com.boontaran.brashmonkey.spriter.Mainline;
import com.boontaran.brashmonkey.spriter.Player;
import com.boontaran.games.Clip;
import com.boontaran.games.platformerLib.Entity;
import com.boontaran.games.spriter.SpriterClip;
import com.boontaran.supercat.SuperCat;
import com.boontaran.supercat.level.Bone;
import com.boontaran.supercat.level.Hero;
import com.boontaran.supercat.level.Level;

/**
 * Created by arifbs on 12/9/15.
 */
public class Enemy3 extends Enemy {
    //states
    private static final int FLY = 1;
    private static final int DIE = 2;
    private static final int ATTACKED =3;
    private int state;

    //animations
    private static final int[] FLY_ANIM = {0,1,2,3,2,1};
    private static final int[] ATTACKED_ANIM = {4,5,5,6,6,6,6};
    private static final int[] DIE_ANIM = {8};

    private float minY,maxY;
    private float duration;
    private int moveDir;

    private Clip clip;
    public Enemy3(Level level) {
        super(level);
        setRadius(22);

        clip = new Clip(SuperCat.getRegion("enemy3"),130,80);
        setClip(clip);
        clip.addListener(new Clip.ClipListener() {
            @Override
            public void onComplete() {
                waiting=false;
                if(state == (ATTACKED)) {
                    changeState(FLY);
                }
            }

            @Override
            public void onFrame(int i) {

            }
        });

        //fly
        changeState(FLY);
        noGravity = true;
    }

    private boolean waiting;
    private void changeState(int newstate) {
        if(state == newstate) return;
        if(waiting) return;

        state = newstate;

        //default fps
        clip.setFPS(12);

        //change animation based on current state
        switch (state) {
            case FLY:
                clip.playFrames(FLY_ANIM, true);
                break;
            case ATTACKED:
                clip.playFrames(ATTACKED_ANIM, false);
                break;
            case DIE:
                clip.playFrames(DIE_ANIM, false);
                break;
        }
    }

    public void setFly(float minY, float maxY) {
        this.minY = minY;
        this.maxY = maxY;

        moveUp();
    }

    //move up
    private void moveUp() {
        duration=(maxY-getY())/100;
        addAction(Actions.moveTo(getX(),maxY,duration, Interpolation.sine));
        moveDir = 1;

        if(state == ATTACKED) {
            changeState(FLY);
        }
    }

    //move down
    private void moveDown() {
        duration=(getY()-minY)/100;
        addAction(Actions.moveTo(getX(), minY, duration, Interpolation.sine));
        moveDir = -1;
    }



    @Override
    public void update(float delta) {
        super.update(delta);



        if(moveDir != 0) {
            //alternate between move up & down
            if(getActions().size == 0) {
                if(moveDir == 1) {
                    moveDown();
                } else {
                    moveUp();
                }
            }
        }
    }

    @Override
    public void touchHero(Hero hero) {

    }

    @Override
    public void attackedBy(Entity ent) {
        float damage=0;

        //attacked
        if(ent instanceof Bone) {
            damage = ((Bone) ent).getDamage();
        }
        else if(ent == level.getHero() ) {
            damage = level.getHero().getDamage();

        }
        health -= damage;

        //check if die
        if(health <=0) {
            die();
        } else {
            changeState(ATTACKED);

            //if current moving direction is up, move it down
            if(moveDir == 1) {
                getActions().clear();
                moveDown();
            }
        }
        SuperCat.media.playSound("hit.mp3");
    }

    @Override
    protected void die() {
        super.die();

        moveDir = 0;
        getActions().clear();

        changeState(DIE);
        noGravity = false;
    }
}
