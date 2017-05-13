package com.boontaran.supercat.level;

import com.badlogic.gdx.assets.loaders.resolvers.LocalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.boontaran.MessageListener;
import com.boontaran.supercat.SuperCat;

/**
 * Created by arifbs on 11/15/15.
 */
public class SubLevel extends Level {
    //event
    public static  final int EXIT = 101;
    private String tmx;

    //the door
    private Door doorIn , doorOut;
    //initial hero position
    private Vector2 heroInitPos;

    public SubLevel(int id,String tmx) {
        super(id);
        this.tmx = tmx;
    }


    @Override
    protected void create() {
        build();
    }

    @Override
    public void show() {
        super.show();

        if(hero != null) {
            //put hero at initial position
            hero.setPosition(heroInitPos.x , heroInitPos.y);
            //temporary disable the door
            doorIn.setTmpDisable();
            //camera follow hero
            camController.lookAt(hero);
        }
    }

    //create hero
    private void putHero(Door door) {
        if(hero != null) return;  //already created

        hero = new Hero(this);
        hero.setPosition(door.getX() + door.getWidth() / 2, door.getBottom() + hero.getHeight() / 2);
        createHero(hero);
        door.setTmpDisable();

        heroInitPos = new Vector2(hero.getPos());
        camController.lookAt(hero);
    }
    @Override
    protected void createDoor(Door door) {
        door.clearListeners();
        addEntity(door);

        //initiate door by type
        if(door.getType() == Door.TYPE_IN) {
            doorIn = door;
            putHero(doorIn);
            doorIn.setDisable(true);
        } else if (door.getType() == Door.TYPE_OUT){
            doorOut = door;
            doorOut.addListener(exitDoorListener);
            doorOut.unlock();
            doorOut.setDisable(false);
        } else {
            putHero(door);
            door.addListener(exitDoorListener);
            door.unlock();
            doorIn = door;
        }


    }

    //exit door listener
    private MessageListener exitDoorListener = new MessageListener(){
        @Override
        protected void receivedMessage(int message, Actor actor) {
            if(message == Door.OPEN) {
                ((Door)actor).setTmpDisable();
                call(EXIT);
            }
        }
    };

    @Override
    protected TiledMap getTiledMap(String fileName) {
        String path = "tiled/"+fileName;

        //not used
        if(SuperCat.tester) {
            path = "tiledtest/"+fileName;
        }

        // filter
        TmxMapLoader.Parameters params = new TmxMapLoader.Parameters();
        params.generateMipMaps = true;
        params.textureMinFilter = Texture.TextureFilter.MipMapLinearNearest;
        params.textureMagFilter = Texture.TextureFilter.Linear;

        TiledMap map ;

        if(SuperCat.tester) {
            map = new TmxMapLoader(new LocalFileHandleResolver()).load(path, params);
        } else {
            map = new TmxMapLoader().load(path, params);
        }

        return map;
    }

    @Override
    protected void afterBuild() {
        camController.setDefaultZoom(1.3f);
        camController.lookAt(hero);
        camController.followObject(hero);
    }


    @Override
    protected String getTmx() {
        return tmx;
    }
}
