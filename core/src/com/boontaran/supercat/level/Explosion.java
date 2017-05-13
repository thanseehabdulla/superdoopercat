package com.boontaran.supercat.level;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Pool;
import com.boontaran.games.Clip;
import com.boontaran.games.platformerLib.Entity;
import com.boontaran.supercat.SuperCat;

/**
 * Created by arifbs on 11/13/15.
 */
public class Explosion extends Entity implements Pool.Poolable {
    private final Level level;
    private final Pool<Explosion> pool;
    private final Clip clip;

    public Explosion(Level lvl, Pool<Explosion> pl) {
        this.level = lvl;
        this.pool = pl;

        setSize(140,140);

        //no interaction
        setNoCollision(true);
        setNoLandCollision(true);
        noGravity = true;

        //list of explosion images
        TextureRegion[] regions = {
                SuperCat.getRegion("exp1"),
                SuperCat.getRegion("exp2"),
                SuperCat.getRegion("exp3"),
                SuperCat.getRegion("exp4"),
        };

        clip = new Clip(regions);
        clip.setFPS(15);
        setClip(clip);

        //remove after animation completed
        clip.addListener(new Clip.ClipListener() {
            @Override
            public void onComplete() {
                level.removeEntity(Explosion.this);
                pool.free(Explosion.this);
            }

            @Override
            public void onFrame(int num) {

            }
        });


    }

    //start animation then added to stage
    @Override
    protected void setStage(Stage stage) {
        super.setStage(stage);
        if(stage != null) clip.playAllFrames(false);
    }

    @Override
    public void reset() {

    }
}
