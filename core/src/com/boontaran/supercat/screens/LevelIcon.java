package com.boontaran.supercat.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.boontaran.games.Util;
import com.boontaran.supercat.SuperCat;

/**
 * Created by arifbs on 10/19/15.
 */
public class LevelIcon extends Group {
    //
    private final Image bg,lockImg;
    public int id;
    private Label label;

    public LevelIcon(int id) {
        setTransform(false);

        this.id = id;

        //icon bg
        bg = SuperCat.createImage("out/level_icon_bg");
        addActor(bg);

        //define the size
        setSize(bg.getWidth(), bg.getHeight());

        //lock image
        lockImg = SuperCat.createImage("out/level_icon_lock");
        addActor(lockImg);
        lockImg.setX((getWidth()-lockImg.getWidth())/2);
        lockImg.setY((getHeight()-lockImg.getHeight())/2);

        //create label id
        label = Util.createLabel(id+"", SuperCat.font144,new Color(0x000000ff));
        addActor(label);
        label.setFontScale(0.8f);
        label.pack();
        label.setX((getWidth() - label.getWidth()) / 2);
        label.setY((getHeight() - label.getHeight()) / 2 + 20);



        //assign event to this object, not the children
        addCaptureListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                event.setTarget(LevelIcon.this);
                return true;
            }
        });

        //if touched, make it half transparent
        addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                LevelIcon.this.setColor(1, 1, 1, 1);
                super.touchUp(event, x, y, pointer, button);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                LevelIcon.this.setColor(1,1,1,0.6f);
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        ///lock
        setLock(true);
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        //only draw if in screen boundary

        //calculate x position
        float x = 0;
        Actor actor = this;
        while (actor != null) {
            x += actor.getX();
            actor = actor.getParent();
        }

        //outside screen, return
        if(x + getWidth()<0) return;
        if(x > getStage().getWidth()) return;

        super.draw(batch, parentAlpha);
    }

    //set locked or not
    //set the touchable property
    public void setLock(boolean locked) {

        if(locked) {
            setTouchable(Touchable.disabled);
            label.setVisible(false);
            lockImg.setVisible(true);
        } else {
            setTouchable(Touchable.enabled);
            label.setVisible(true);
            lockImg.setVisible(false);

            //get and show the stars
            setStar();
        }

    }

    private void setStar() {
        //get from data
        int stars = SuperCat.data.getStars(id);

        //create 3 stars
        Image star2=SuperCat.createImage("out/star_off_small");
        addActor(star2);
        star2.setX(getWidth()/2 - star2.getWidth()/2);
        star2.setY(16);

        Image star1=SuperCat.createImage("out/star_off_small");
        addActor(star1);
        star1.setY(star2.getY());
        star1.setX(star2.getX()-star1.getWidth()-2);

        Image star3=SuperCat.createImage("out/star_off_small");
        addActor(star3);
        star3.setY(star2.getY());
        star3.setX(star2.getRight()+2);

        //set the on/off stars based on value from data
        if(stars >= 100) {
            stars = stars % 100;
            star1.setDrawable(new TextureRegionDrawable(SuperCat.getRegion("out/star_on_small")));
        }
        if(stars >= 10) {
            stars = stars % 10;
            star2.setDrawable(new TextureRegionDrawable(SuperCat.getRegion("out/star_on_small")));
        }
        if(stars > 0) {
            star3.setDrawable(new TextureRegionDrawable(SuperCat.getRegion("out/star_on_small")));
        }
    }

}
