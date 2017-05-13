package com.boontaran.supercat.level;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.boontaran.games.Clip;
import com.boontaran.games.platformerLib.Entity;

import com.boontaran.games.platformerLib.World;
import com.boontaran.supercat.SuperCat;

/**
 * Created by arifbs on 10/21/15.
 *
 * auto destroy brick
 */
public class Brick2 extends Brick {
    private float maxTime = 1.5f;
    private float time =0;
    private Level level;
    private boolean destroying = false;

    public Brick2(Level level) {
        this.level = level;

        //image sequences of auto destroy
        TextureRegion regions[] = {
                SuperCat.getRegion("brick_b1"),
                SuperCat.getRegion("brick_b2"),
                SuperCat.getRegion("brick_b3"),
                SuperCat.getRegion("brick_b4"),
        };

        clip = new Clip(regions);
        clip.singleFrame(0);
        setClip(clip);

        edgeUpdateLimRatio = 1;
    }

    @Override
    public void update(float delta) {
        if(destroying) {
            //calculate time
            time += delta;

            //destroy
            if(time > maxTime) {
                destroying = false;
                level.destroyBrick(this,false);
                return;
            }

            //set the image based on the elapsed time
            int id = (int) ((time)/maxTime * 4);
            clip.singleFrame(id);
        }
    }

    public void destroy() {
        if(destroying) return;
        destroying = true;
    }
}
