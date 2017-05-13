package com.boontaran.supercat.level.enemies;

import com.boontaran.games.Clip;
import com.boontaran.games.platformerLib.Entity;
import com.boontaran.supercat.SuperCat;
import com.boontaran.supercat.level.Bone;
import com.boontaran.supercat.level.Hero;
import com.boontaran.supercat.level.Level;


/**
 * Created by arifbs on 10/22/15.
 *
 * Enemy1 is the enemy that walk and jump
 */
public class Enemy1 extends Enemy {

    //enemy can jump up or down if the next platform is below this value
    protected float jumpRange = 2* Level.GRID;

    private Clip clip;

    //animation, based on the spritesheet
    private static final int[] WALK_ANIM = {0,1,2,3,4,3,2,1};
    private static final int[] JUMP_ANIM = {5,6};
    private static final int[] ATTACK_ANIM = {7,8,9,9,9};
    private static final int[] ATTACKED_ANIM = {10,11,11,11};
    private static final int[] ATTACKED2_ANIM = {12,13,14};
    private static final int[] DIE_ANIM = {15,16};


    //states
    protected static final int WALK = 1;
    protected static final int JUMP = 2;
    protected static final int ATTACK = 3;
    protected static final int ATTACKED = 4;
    protected static final int ATTACKED2 = 5;
    protected static final int DIE = 6;
    private int state;

    public Enemy1(Level level) {
        super(level);
        edgeUpdateLimRatio = 0.2f;

        //can jump up to 2 grid
        jumpRange = Level.GRID*2;
        setSize(36, 48);

        //animation
        clip = createClip();
        setClip(clip);
        clip.playFrames(WALK_ANIM, true);
        clip.addListener(new Clip.ClipListener() {
            @Override
            public void onComplete() {
                waiting=false;

                if(state == ATTACKED) {
                    changeState(WALK);
                }
            }

            @Override
            public void onFrame(int i) {

            }
        });

    }


    protected Clip createClip() {
        contentOffsetY = 2;
        return new Clip(SuperCat.getRegion("enemy1"),80,80);
    }

    private boolean waiting;


    protected void changeState(int newState) {
        changeState(newState,false);
    }

    protected void changeState(int newState, boolean force) {
        if(newState==state) return; //already at the state
        if(force) waiting=false; //forced to change no need to wait
        if(waiting) return; //still waiting prev animation

        state = newState;
        clip.setFPS(12); //default fps

        //play animation based on state
        switch (state) {
            case WALK:
                clip.playFrames(WALK_ANIM, true);
                break;

            case JUMP:
                clip.playFrames(JUMP_ANIM, false);
                break;

            case ATTACK:
                clip.playFrames(ATTACK_ANIM, false);
                waiting = true;
                break;

            case ATTACKED:
                clip.playFrames(ATTACKED_ANIM, false);
                break;

            case ATTACKED2:
                clip.playFrames(ATTACKED2_ANIM, false);
                break;

            case DIE:
                clip.playFrames(DIE_ANIM, false);
                break;
        }

    }



    @Override
    protected void hitWall(Entity ent) {
        if(!isInAir()) {
            //can jump?
            checkJump();


            if(v.y > 0) {
                //should be jumping right now
            } else {
               super.hitWall(ent);
            }
        }
    }



    @Override
    public void update(float delta) {
        super.update(delta);

        if(hasDied()) return;
        if(state == ATTACKED || state == ATTACKED2) {
            return;
        }

        if(!isInAir()) {
            //walk
            changeState(WALK);

                //set speed
                if(isMoveRight()) {
                    setVX(speed);
                } else {
                    setVX(-speed);
                }

        } else {
            //in air, jump state
            changeState(JUMP);

                //set speed
                if(isMoveRight()) {
                    setVX(speed);
                } else {
                    setVX(-speed);
                }

        }

    }

    //hit land
    @Override
    protected void hitLand(Entity ent) {
        super.hitLand(ent);

        //change state if needed
        if(state == ATTACKED2) {
            changeState(WALK);
        }
    }

    @Override
    public void touchHero(Hero hero) {
        changeState(ATTACK);

        //follow hero direction
        if(hero.getX() > getX()) {
            setMoveRight(true);
        } else {
            setMoveRight(false);
        }
    }



    @Override
    public void attackedBy(Entity ent) {
        setVY(0);
        checkHeroTime = 2;

        float damage=0;

        //hit by bone/weapon
        if(ent instanceof Bone) {
            damage = ((Bone) ent).getDamage();

            //throw
            setVY(300);
            if(ent.v.x > 0) {
                setMoveRight(false);
                setVX(200);
            } else {
                setMoveRight(true);
                setVX(-200);
            }
            changeState(ATTACKED2, true);
        } else if(ent == level.getHero() ) {

            //stomped by hero
            damage = level.getHero().getDamage();
            setVX(0);
            changeState(ATTACKED, true);
        }

        //reduce damage
        health -= damage;

        //die
        if(health <=0) {
            changeState(DIE, true);
            die();
        }

        SuperCat.media.playSound("hit.mp3");
    }

    private void checkJump() {
        //point to check
        float px,py ;

        if(isMoveRight()) {
            px = getRight()+Level.GRID/2;
        } else {
            px = getLeft() - Level.GRID/2;
        }


        //got a wall
        if(!isHole(px,getY()) ) {
            //calculate distance to top
            float distToTop = calculateDistToTop(px,getBottom());

            float px2;
            if(isMoveRight()) {
                px2 =px-Level.GRID/2;
            } else {
                px2 =px+Level.GRID/2;
            }

            //distance to jump, reachable or not
            if(distToTop <=jumpRange && isHole(px2,getBottom()+distToTop)) {
                jumpUp(distToTop);
            }
        }
    }

    //jump
    private void jumpUp(float dist) {
        float vJump = 500;

        //speed based on how high to jump
        if(dist <= Level.GRID) {
            vJump = 500;
        }
        else if(dist <= Level.GRID*2) {
            vJump = 700;
        }

        setVY(vJump);
        changeState(JUMP);

        if(inView) SuperCat.media.playSound("jump.mp3");
    }


    private float calculateDistToTop(float px, float y) {
        float step = Level.GRID;
        float max = 1000;
        float dist=0;

        float py=y;

        //loop until get a 'free' space, means got no land object
        while (dist < max) {
            if(level.isHole(px,py)) {
                break;
            }
            dist += step;
            py += step;
        }

        return dist;
    }


}
