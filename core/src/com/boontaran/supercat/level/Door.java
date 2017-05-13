package com.boontaran.supercat.level;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.boontaran.games.platformerLib.Entity;
import com.boontaran.supercat.SuperCat;
import com.boontaran.ui.NButton;

/**
 * Created by arifbs on 11/15/15.
 */
public class Door extends Entity {
    //event
    public static final int OPEN = 1;

    //types
    public static final int TYPE_IN = 1;
    public static final int TYPE_OUT = 2;
    private int type = 0;

    //unique id
    public int id;
    //in-out pair
    private Door pair;


    private float buttonTime;

    //enter button
    private NButton button;

    //locked image
    private Image lockedImg;

    //level to enter
    private Level level;
    //level tmx
    private String tmx;

    private boolean locked=true;
    private boolean tmpDisable;
    private boolean disable = false;

    public Door(Level level,int id,float w,float h,String tmx) {
        setSize(w,h);
        this.id=id;
        this.tmx = tmx;
        this.level = level;
        noGravity = true;
        setNoLandCollision(true);

        //create the enter button
        button = new NButton(SuperCat.getRegion("door_btn"));
        button.setVisible(false);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                fireEvent(OPEN);
            }
        });

        //create the locked image
        lockedImg = SuperCat.createImage("locked");
        lockedImg.setVisible(false);
    }

    //unlock the door
    public void unlock() {
        locked = false;
        if(lockedImg != null) {
            level.removeChild(lockedImg);
            lockedImg = null;
        }

    }
    public boolean isLocked(){
        return locked;
    }

    //just after hero exit, disable it
    public void setTmpDisable() {
        tmpDisable = true;
        button.setVisible(false);
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    //hero at the door
    public void touchHero() {
        if(disable) return;
        buttonTime = 0.4f;
    }
    public String getTmx() {
        return tmx;
    }

    //added to stage, rearrange the button
    @Override
    protected void setStage(Stage stage) {
        super.setStage(stage);

        if(stage != null) {
            level.addChild(button);

            if(lockedImg != null) {
                level.addChild(lockedImg);
            }

            positionChanged();
        }
    }

    //set the button position
    @Override
    protected void positionChanged() {
        button.setPosition(getX() - button.getWidth()/2, getY() + 50);

        if(lockedImg != null) {
            lockedImg.setPosition(getX()-lockedImg.getWidth()/2 , getY() + 50);
        }
    }

    @Override
    public void update(float delta) {

        if(buttonTime > 0) {
            if(locked) {
                lockedImg.setVisible(true);

                buttonTime -= delta;
                if(buttonTime <=0) {
                    lockedImg.setVisible(false);
                    tmpDisable = false;
                }
            } else {
                if(!tmpDisable) button.setVisible(true);


                buttonTime -= delta;
                if(buttonTime <=0) {
                    //hide btn
                    button.setVisible(false);
                    tmpDisable = false;
                }
            }
        }
    }

    public Door getPair() {
        return pair;
    }

    public void setPair(Door pair) {
        this.pair = pair;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
