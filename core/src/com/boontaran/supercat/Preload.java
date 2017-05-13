package com.boontaran.supercat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.boontaran.games.StageGame;

/**
 * Created by arifbs on 10/19/15.
 */
public class Preload extends StageGame {
    private Texture texture;

    @Override
    protected void create() {
        //create texture
        texture = new Texture(Gdx.files.internal("please_wait.png"));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        //put the image on center of screen
        Image img = new Image(texture);
        addChild(img);
        centerActorXY(img);
    }

    @Override
    public void hide() {
        texture.dispose();
    }
}
