package com.boontaran.supercat.level;

import com.boontaran.games.Clip;
import com.boontaran.games.Delayer;
import com.boontaran.games.platformerLib.Entity;
import com.boontaran.supercat.SuperCat;
import com.boontaran.supercat.level.enemies.Enemy;
import com.boontaran.supercat.level.enemies.Enemy3;

/**
 * Created by arifbs on 10/20/15.
 */
public class Hero extends Entity {
    //Events
    public static final int THROW_BONE = 1;
    public static final int ON_DIE = 2;

    //animation
    private Clip clip;
    private Level level;

    //stomp damage
    private float damage = 1;
    private float fullHealth = 5;

    //data
    private float health = fullHealth;
    private int numBones; //current bones/weapon count

    //animations
    private static final int IDLE = 1;
    private static final int THROW = 2;
    private static final int JUMP = 3;
    private static final int STOMP = 4;
    private static final int RUN = 5;
    private static final int ATTACK = 6;
    private static final int ATTACKED = 7;
    private static final int DIE = 8;

    //animation frames
    private static final int[] IDLE_FRAMES = {1,2,3,2,1,0,1,1,1,1};
    private static final int[] RUN_FRAMES = {5,6,7,8,9,8,7,6};
    private static final int[] JUMP_FRAMES = {10,11};
    private static final int[] ATTACK_FRAMES = {12,13,14};
    private static final int[] THROW_FRAMES = {15,16,17};
    private static final int[] ATTACKED_FRAMES = {18,19,19};
    private static final int[] DIE_FRAMES = {20,21,22};

    //
    private int currentAnimation;

    //interval between attacks
    private float attackIntervalTime;

    //wait for animation completed
    private boolean waitAnimation = false;

    private Delayer delayer = new Delayer(null);

    //invincible time
    private float immuneTime;

    //finish a level
    private boolean hasFinished = false;

    public Hero(Level level) {
        this.level = level;
        dragWithLand= true;
        edgeUpdateLimRatio = 1;

        //define the animation
        clip = new Clip(SuperCat.getRegion("hero"),100,120);
        setClip(clip);
        clip.addListener(new Clip.ClipListener() {
            @Override
            public void onComplete() {
                waitAnimation = false;

                //after THROW animation, change to IDLE
                if(currentAnimation == THROW) {
                    playAnimation(IDLE);
                }
            }

            @Override
            public void onFrame(int i) {

            }
        });


        //no bouncing
        restitution = 0;

        //bound size
        setSize(35, 82);

        //adjust display position
        contentOffsetY = 3;
        contentOffsetX = -3;

        //run speed
        maxSpeedX = 400;

        //initial animation
        playAnimation(IDLE);


    }


    public float getDamage() {
        if(currentAnimation == ATTACK) {  //double damage
            return damage * 2;
        }
        return damage;
    }
    public void addBones(int num) {
        numBones += num;
    }
    public int getNumBones() {
        return numBones;
    }
    //flip graphics based on direction assigned
    public void setDirRight(boolean right) {
        if(right) {
            setScaleX(-1);
        } else {
            setScaleX(1);
        }
    }

    private boolean isAttacking() {
        return currentAnimation == ATTACK;
    }
    public boolean isAttacked() {
        return currentAnimation == ATTACKED;
    }

    private boolean jumpHasReleased = true;
    private boolean throwHasReleased = true;
    private boolean stompHasReleased = true;

    //notify on control state
    public void updateKey(float delta, boolean left, boolean right, boolean jump, boolean stomp, boolean throwing) {
        //default state
        friction = 0.2f;
        a.x = 0;

        if(!isControllabe()) return;

        float acc = 3000;

        //set acceleration and direction, disable friction
        if (right) {
            friction = 0;
            a.x = acc;
            setDirRight(true);
        }
        if (left) {
            friction = 0;
            a.x = -acc;
            setDirRight(false);
        }



        //if in platform/ground
        if(!isInAir()) {

            //play RUN animation if direction is active
            if(currentAnimation!=THROW) {
                if (left || right) {
                    playAnimation(RUN);
                } else {
                    playAnimation(IDLE);
                }
            }

            //jump
            if(jump && jumpHasReleased) {
                jump();
            }

            //stomp
            if(stomp && stompHasReleased) {
                setY(getY()+20);
                attack(24);
            }

            //no direction button pressed
            if (!left && !right) {
                if(v.x != 0f) {
                    //gradually reduce x speed
                    float newVX = v.x * (0.95f-delta);

                    if(v.x > 0 && newVX >=0) {
                        v.x = newVX;
                    }
                    else if(v.x < 0 && newVX <=0) {
                        v.x = newVX;
                    }
                }
            }


        } else {
            //no direction button pressed
            if (!left && !right) {
                if(v.x != 0f) {
                    //gradually reduce x speed
                    float newVX = v.x * (0.95f-delta);

                    if(v.x > 0 && newVX >=0) {
                        v.x = newVX;
                    }
                    else if(v.x < 0 && newVX <=0) {
                        v.x = newVX;
                    }
                }
            }

            //stomp from air
            if(!isAttacking()) {
                if(stomp && stompHasReleased) {
                    attack();
                } else {
                    if(currentAnimation != STOMP) {
                        playAnimation(JUMP);
                    }
                }
            }
        }

        //throw a bone
        if(throwing && throwHasReleased) {
            throwBone();
        }

        jumpHasReleased = !jump;
        throwHasReleased = !throwing;
        stompHasReleased = !stomp;
    }

    private void throwBone() {

        if(numBones <=0) { //don't have
            return;
        }
        numBones--;  //take one

        //event
        playAnimation(THROW);
        fireEvent(THROW_BONE);

        SuperCat.media.playSound("throw_weapon.mp3");
    }

    //if hero can be controlled
    private boolean isControllabe() {
        if(currentAnimation == ATTACKED) return false;
        if(hasDied()) return false;
        if(hasFinished) return false;

        return true;
    }

    //stomp attack
    private void attack() {
        attack(300);
    }
    private void attack(float vy) {
        if(attackIntervalTime >0) return;
        playAnimation(ATTACK);

        if(v.y > -vy) {
            setVY(-vy);
        }

        //next attack window time
        attackIntervalTime = 0.5f;
    }

    //shielded
    public boolean isShielded() {
        if(health <=0) {
            return true;
        }
        return immuneTime > 0;
    }

    //touch ceiling
    @Override
    protected void hitCeil(Entity ent) {
        level.heroTouchBlock(ent);
    }

    @Override
    public void update(float delta) {
        delayer.update(delta);

        //reduce timers
        if(attackIntervalTime > 0) {
            attackIntervalTime-=delta;
        }

        //blinking, invincible
        if(immuneTime > 0) {
            immuneTime -= delta;
            blink(delta);

            if(immuneTime <=0) {
                stopBlink();
            }
        }

        //finish
        if(hasFinished && !isInAir()) {
            float acc = 3000;

            friction = 0;
            a.x = acc;
            setDirRight(true);
            playAnimation(RUN);
        }
    }

    private float blinkTime;
    private void blink(float delta) {
        blinkTime += delta;

        //alternate alpha based on elapsing time
        if(blinkTime % 0.2f < 0.1f) {
            setColor(1,1,1,0.2f);
        } else {
            setColor(1, 1, 1, 1);
        }
    }
    private void stopBlink() {
        blinkTime=0;
        setColor(1, 1, 1, 1);
    }

    //hit land ,platform ,block, or brick from above
    @Override
    protected void hitLand(Entity ent) {

        if(isAttacking()) {
            level.heroTouchBlock(ent);
            level.addDust(getX(), getBottom(),0);

            //little bounce
            setVY(320);
            playAnimation(JUMP);

            SuperCat.media.playSound("hit_ground.mp3");
        } else if (lastV.y < -900) {
            //little bounce
            setVY(320);
            playAnimation(JUMP);
            level.addDust(getX(), getBottom(), 0);

            SuperCat.media.playSound("hit_ground.mp3");
        }

        if(isAttacked()) {
            playAnimation(JUMP);
        }
    }


    @Override
    protected void touchLand(Entity ent) {

        //touching auto-destroy brick
        if(ent instanceof Brick2) {
            Brick2 brick = (Brick2) ent;
            brick.destroy();  //initiate destroying process
        }
    }

    private void playAnimation(int name) {
        playAnimation(name, false);
    }
    private void playAnimation(int name, boolean wait) {
        if(waitAnimation) return;
        if(currentAnimation == name) return;

        //only allow die animaition
        if(health <=0 && name != DIE) {
            return;
        }

        waitAnimation = wait;

        clip.setFPS(15);  //default fps

        //select animation
        switch(name) {
            case IDLE:
                clip.setFPS(8);
                clip.playFrames(IDLE_FRAMES, true);
                break;
            case RUN :
                clip.playFrames(RUN_FRAMES,true);
                break;
            case JUMP :
                clip.setFPS(8);
                clip.playFrames(JUMP_FRAMES,false);
                break;
            case ATTACK :
                clip.setFPS(15);
                clip.playFrames(ATTACK_FRAMES,false);
                break;
            case THROW :
                clip.playFrames(THROW_FRAMES,false);
                break;
            case ATTACKED :
                clip.playFrames(ATTACKED_FRAMES,false);
                break;
            case DIE :
                clip.playFrames(DIE_FRAMES,false);
                break;
        }

        //store current animation
        currentAnimation = name;
    }
    private void jump() {
        setVY(790);
        playAnimation(JUMP);

        SuperCat.media.playSound("jump.mp3");
    }

    //attacked by something
    public void attackedBy(Entity ent) {
        float damage = 0;

        //check the attacking object and get the damage
        if(ent instanceof Stone) {
            damage = ((Stone) ent).getDamage();
        }
        else if(ent instanceof Enemy) {
            damage = ((Enemy) ent).getDamage();
        }
        else if(ent instanceof Bullet) {
            damage = ((Bullet) ent).getDamage();
        }

        //attack by that object
        attackedBy(damage, ent.v.x,  ent.getX(), ent.getY());

        //if attacked by Enemy3
        if(ent instanceof Enemy3) {
            if(ent.getBottom() > getY()) {
                setVY(-200);
            }
        }
    }
    public void attackedBy(float damage,float vx, float x, float y) {
        //set the speed caused by attack
        if(v.x == 0) {
            if(x > getX()) {
                setDirRight(true);
                setVX(-200);
            }
            else {
                setDirRight(false);
                setVX(200);
            }
        } else {
            if(vx < 0) {
                setDirRight(true);
                setVX(-200);
            }
            else {
                setDirRight(false);
                setVX(200);
            }
        }

        //reduce health
        health -= damage;

        //bounce up
        setVY(350);

        //check if died
        if(health <=0) {
            health = 0;
            die();
        } else {
            immuneTime = 3f;
            playAnimation(ATTACKED);
        }

        SuperCat.media.playSound("hit.mp3");
    }


    //player died
    private void die() {
        health = 0;
        playAnimation(DIE);
        setVY(450);
        setVX(0);
        setNoCollision(true);
        setNoLandCollision(true);

        fireEvent(ON_DIE);
    }

    public boolean hasDied() {
        return health <=0;
    }

    //is hero have an enough room to stomp an enemy?
    public boolean stomp(Enemy enemy) {
        if(!isHole(getX() , enemy.getTop() + getHeight() + 1)) {
            return false;
        }
        setY(enemy.getTop() + getHeight() / 2 + 1);
        return true;
    }

    //after stomping, little bounce
    public void afterStompAttack() {
        setVY(450);
        playAnimation(JUMP);
    }


    //hero have keys to sub level
    private boolean haveKey = false;
    public void setHaveKey() {
        haveKey = true;
    }
    public boolean isHaveKey() {
        return haveKey;
    }

    //currrent health comparing the full healht
    public float getHealthRatio() {
        return health/fullHealth;
    }
    public float getHealth() {
        return health;
    }
    public void setHealth(float health) {
        this.health = health;
    }

    //set bones count
    public void setBones(int num) {
        numBones  =num;
    }

    //readh finish
    public void reachFinish() {
        hasFinished=true;
    }

    public void fall() {
        playAnimation(DIE);
        setVX(0);
        setNoCollision(true);
        setNoLandCollision(true);

        health=0;
        fireEvent(ON_DIE);
    }
}
