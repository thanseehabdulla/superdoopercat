package com.boontaran.supercat.level;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.boontaran.games.Clip;
import com.boontaran.games.platformerLib.Entity;

import com.boontaran.games.platformerLib.World;
import com.boontaran.supercat.SuperCat;

/**
 * Created by arifbs on 10/21/15.
 */
public class Brick extends Entity {
    protected Clip clip;
    private Debris[] debrises;

    //contents
    private Coin[] coins;

    public Brick() {
        TextureRegion regions[] = {
                SuperCat.getRegion("brick")
        };

        clip = new Clip(regions);
        clip.singleFrame(0);
        setClip(clip);
        setSize(Level.GRID, Level.GRID);

        //prepare the debris
        debrises = new Debris[]{
            new Debris(1),
            new Debris(2),
            new Debris(3),
            new Debris(4)

        };
    }
    public Debris[] getDebrises() {
        return debrises;
    }

    public void addCoins(int num) {
        coins = new Coin[num];
        while(num-- >0) {
            coins[num] = new Coin();
        }
    }

    public Coin[] getCoins() {
        return coins;
    }
    public static class Debris extends Entity {
        private float time;
        public Debris(int type) {
            //debris with image variation based on type
            Image img = SuperCat.createImage("brick_debris"+type);
            setImage(img);
            setSize(Level.GRID / 2, Level.GRID / 2);

            setNoCollision(true);
        }

        //debris put on stage, set random rotation speed
        @Override
        public void setWorld(World world) {
            super.setWorld(world);
            setASpeed((float) (Math.random() * 720 - 360));
            time = (float) (1 + Math.random()*1);
        }

        //hit land, set random rotation speed again
        @Override
        protected void hitLand(Entity ent) {
            setASpeed((float) (Math.random() * 720 - 360));
        }

        @Override
        public void update(float delta) {
            time -= delta;
            if(time <0) {  //time's up, remove this
                world.removeEntity(this);
            }
        }

        //out screen, remove this
        @Override
        public void onSkipUpdate(float delta) {
            super.onSkipUpdate(delta);
            world.removeEntity(this);
        }
    }
}
