package com.boontaran.supercat.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.loaders.resolvers.LocalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.boontaran.MessageListener;
import com.boontaran.games.Clip;
import com.boontaran.games.Delayer;
import com.boontaran.games.platformerLib.Entity;
import com.boontaran.games.platformerLib.World;
import com.boontaran.games.tiled.TileLayer;
import com.boontaran.supercat.Settings;
import com.boontaran.supercat.SuperCat;
import com.boontaran.supercat.level.controls.ControlPad;
import com.boontaran.supercat.level.enemies.Enemy;
import com.boontaran.supercat.level.enemies.Enemy1;
import com.boontaran.supercat.level.enemies.Enemy2;
import com.boontaran.supercat.level.enemies.Enemy3;
import com.boontaran.supercat.level.ui.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by arifbs on 10/19/15.
 */
public class Level extends World {
    private static final String TAG = "Level";

    //event
    public static final int START_SUB_LEVEL = 1;
    public static final int QUIT = 2;
    public static final int RETRY = 3;
    public static final int NEXT_LEVEL = 4;

    //game state
    private static final int PLAY = 1;
    private static final int PAUSE = 2;
    private static final int COMPLETED = 3;
    private static final int FAILED = 4;
    private static final int LOADING_MUSIC = 5;

    private int state = 0;

    //level data
    private int numCoins,numBeatenEnemies;
    private int totalCoins,totalEnemies;

    //tileset grid
    public static final int GRID = 64;

    //id
    public int id;
    //'please wait' image
    private Texture preloadTexture;

    //the map
    private TiledMap tiledMap;
    private float levelWidth;
    private float levelHeight;

    protected Hero hero;

    //invisible coin to synchronize all coins animation
    private Coin coinReference;

    //sub level tmx
    private String selectedSubLevelTmx;

    //object pools
    private Pool<CoinTrail> poolCoinTrail = new Pool<CoinTrail>() {
        @Override
        protected CoinTrail newObject() {
            return new CoinTrail();
        }
    };
    private Pool<Dust> poolDust = new Pool<Dust>() {
        @Override
        protected Dust newObject() {
            return new Dust();
        }
    };
    private Pool<Bone> poolBone = new Pool<Bone>() {
        @Override
        protected Bone newObject() {
            return new Bone();
        }
    };
    private Pool<BulletEnemy2> poolBulletEnemy2 = new Pool<BulletEnemy2>() {
        @Override
        protected BulletEnemy2 newObject() {
            return new BulletEnemy2();
        }
    };
    private Pool<Explosion> poolExplosion = new Pool<Explosion>() {
        @Override
        protected Explosion newObject() {
            return new Explosion(Level.this, poolExplosion);
        }
    };
    private Pool<Missile> poolMissile = new Pool<Missile>() {
        @Override
        protected Missile newObject() {
            return new Missile(Level.this);
        }
    };
    private Pool<Hit> poolHit = new Pool<Hit>() {
        @Override
        protected Hit newObject() {
            return new Hit(poolHit);
        }
    };

    //top panel UI
    private Panel panel;

    //controls
    private final ControlPad controlPad;
    private final ImageButton jumpBtn, stompBtn, boneBtn;


    //sublevels
    private Map<String, SubLevel> subLevels;

    //dialogs
    private FailedDialog failedDialog;
    private CompletedDialog completedDialog;

    //right level wall boundary
    private Entity wallRight;

    //target
    private float bonusTime;
    private float minCoins;
    private float minEnemies;

    private float time;
    private float bonusTimer;
    private String tmxFile;

    private float delay=0.1f; //delay before build

    private static final String DEFAULT_MUSIC = "music2.ogg";
    private String musicFile=null;

    public Level(int id) {
        this.id = id;

        //level gravity
        gravity.y = -1500;

        //frame skip of collision detection
        collisionSkip = 2;

        //control buttons
        controlPad = new ControlPad(SuperCat.getRegion("control_pad"),SuperCat.getRegion("control_pad_right"),SuperCat.getRegion("control_pad_left"));
        stompBtn = new ImageButton(new TextureRegionDrawable(SuperCat.getRegion("stomp_btn")),new TextureRegionDrawable(SuperCat.getRegion("stomp_btn_down")));
        jumpBtn = new ImageButton(new TextureRegionDrawable(SuperCat.getRegion("jump_btn")),new TextureRegionDrawable(SuperCat.getRegion("jump_btn_down")));
        boneBtn = new ImageButton(new TextureRegionDrawable(SuperCat.getRegion("bone_btn")),new TextureRegionDrawable(SuperCat.getRegion("bone_btn_down")));

        //debug to show entities boundary
        //World.debug = true;
    }

    //set the stars reward requirement
    public void setScoreRule(float minCoins,float minEnemies,float bonusTime) {
        this.minCoins=minCoins;
        this.minEnemies=minEnemies;
        this.bonusTime=bonusTime;

        bonusTimer = bonusTime;
    }


    public Hero getHero() {
        return hero;
    }

    //clean up
    @Override
    public void dispose() {
        tiledMap.dispose();

        //dispose layer map
        for(Actor child : stage.getActors()) {
            if(child instanceof TileLayer) {
                ((TileLayer) child).dispose();
            }
        }

        //dispose sublevels if have
        if(subLevels != null) {
            for(SubLevel subLevel : subLevels.values()) {
                subLevel.dispose();
            }
            subLevels.clear();
            subLevels = null;
        }

        stopMusic();

        SuperCat.media.stopMusic("music3.ogg");
        SuperCat.media.stopMusic("music4.ogg");

        //unload custom music
        if(musicFile != null && !musicFile.equals(DEFAULT_MUSIC)) {
            SuperCat.media.removeMusic(musicFile);
        }

        super.dispose();
    }

    //get a missile
    protected Missile getMissile() {
        return poolMissile.obtain();
    }

    public void setDelay(float delay) {
        this.delay = delay;
    }
    @Override
    protected void create() {
        //show preload image
        preload();

        //check if have custom music
        String customMusic = Settings.LEVEL_MUSICS.get(id);

        if(customMusic != null && !customMusic.isEmpty()) {
            //use custom
            musicFile = customMusic;
            SuperCat.media.addMusic(musicFile);
            state = LOADING_MUSIC;
        } else {
            //use default
            musicFile = DEFAULT_MUSIC;
            //build after some delay
            delayCall(delay, new Delayer.Listener() {
                @Override
                public void onDelay() {
                    build();
                }
            });
        }
    }

    //play music
    private void playMusic() {
        SuperCat.media.playMusic(musicFile,true,0.8f,0);
    }
    //stop music
    private void stopMusic() {
        SuperCat.media.stopMusic(musicFile);
    }

    //tmx file, based on level id
    protected String getTmx() {
        return "level"+id+".tmx";
    }


    protected void build() {
        //remove the preload image
        stage.clear();
        if(preloadTexture != null) {
            preloadTexture.dispose();
            preloadTexture = null;
        }

        //tmx file to load
        tmxFile = getTmx();
        tiledMap = getTiledMap(tmxFile);

        // get & calculate the sizes
        MapProperties prop = tiledMap.getProperties();
        int mapWidth = prop.get("width", Integer.class);
        int mapHeight = prop.get("height", Integer.class);
        int tilePixelWidth = prop.get("tilewidth", Integer.class);
        int tilePixelHeight = prop.get("tileheight", Integer.class);
        levelWidth = mapWidth * tilePixelWidth;
        levelHeight = mapHeight * tilePixelHeight;

        //camera boundary
        camController.setBoundary(new Rectangle(0,0,levelWidth,levelHeight));
        camController.setSpeedScale(10);


        int layerId=0;
        TileLayer tileLayer=null;

        for(MapLayer layer : tiledMap.getLayers()) {
            //put layers

            if(layer instanceof TiledMapTileLayer) {
                if(tileLayer == null) {
                    tileLayer = new TileLayer(camera,tiledMap,layerId,batch);
                    addChild(tileLayer);
                } else {
                    tileLayer.addLayerId(layerId);
                }
            } else {
                tileLayer=null;

                //object layer
                if(layer.getObjects().getCount() > 0) {
                    //layer name 'bricks', create bricks
                    if(layer.getName().equals("bricks")) {
                        for(MapObject object : layer.getObjects()) {
                            createBrick(object);
                        }
                    } else {
                        //other object, coins, enemies, player, etc...
                        for(MapObject object : layer.getObjects()) {
                            createObject(object);
                        }
                    }

                }
            }
            layerId++;
        }

        //level boundary wall
        //left
        Entity wall = new Entity();
        wall.setSize(10,levelHeight);
        wall.setPosition(-5,levelHeight/2);
        addLand(wall,true);

        //top
        wall = new Entity();
        wall.setSize(levelWidth,10);
        wall.setPosition(levelWidth/2,levelHeight + 5);
        addLand(wall,true);

        //right
        wallRight = new Entity();
        wallRight.setSize(10,levelHeight);
        wallRight.setPosition(levelWidth+5,levelHeight/2);
        addLand(wallRight, true);

        //level background
        Image bg;

        //check if using custom bg

        String customBg = Settings.LEVEL_BACKGROUNDS.get(id);

        if(customBg != null && !customBg.isEmpty()) {
            bg = SuperCat.createImage("bg/"+customBg);
        } else {
            bg = SuperCat.createImage("bg/level_bg");
        }

        //add the background
        addBackground(bg,true,false);

        //coin synchronize
        coinReference = new Coin();
        coinReference.setAsReference();
        coinReference.setX(-100);
        addEntity(coinReference);
        coinReference.clip.addListener(new Clip.ClipListener() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onFrame(int num) {
                int i;
                Coin coin;

                //update all coins
                for (i = 0; i < coins.size; i++) {
                    coin = coins.get(i);
                    if(coin.inView) coin.clip.singleFrame(num);
                }
            }
        });

        //camera follow hero
        if(hero != null) {
            camController.setDefaultZoom(1.3f);
            camController.lookAt(hero);
            camController.followObject(hero);
        }

        //activate binary tree to optimize the collision detection
        initBitree(levelWidth, levelHeight, 15);

        //panel
        panel = new Panel(getWidth());
        addOverlayChild(panel);
        panel.setY(getHeight() - panel.getHeight()-4);

        //control
        addOverlayChild(controlPad);
        controlPad.setX(12);
        controlPad.setY(12);

        addOverlayChild(stompBtn);
        stompBtn.setY(controlPad.getY());
        stompBtn.setX(getWidth()-stompBtn.getWidth()-12);

        addOverlayChild(jumpBtn);
        jumpBtn.setX(stompBtn.getX());
        jumpBtn.setY(stompBtn.getTop() + 12);

        addOverlayChild(boneBtn);
        boneBtn.setY(controlPad.getY());
        boneBtn.setX(stompBtn.getX()-boneBtn.getWidth()-12);

        //not used
        if(SuperCat.tester) {
        	controlPad.setColor(1, 1, 1, 0);
        	stompBtn.setColor(1, 1, 1, 0);
        	jumpBtn.setColor(1, 1, 1, 0);
        	boneBtn.setColor(1, 1, 1, 0);
        }


        //initial data
        hero.addBones(5);
        dataChanged();

        //prepare the dialogs
        failedDialog = new FailedDialog(getWidth(),getHeight());

        //the listener
        failedDialog.addListener(new MessageListener() {
            @Override
            protected void receivedMessage(int message, Actor actor) {
                if (message == FailedDialog.RETRY) {
                    call(RETRY);
                } else if (message == FailedDialog.QUIT) {
                    call(QUIT);
                }
            }
        });

        completedDialog = new CompletedDialog(getWidth(), getHeight());
        completedDialog.addListener(new MessageListener() {
            @Override
            protected void receivedMessage(int message, Actor actor) {
                if (message == CompletedDialog.QUIT) {
                    call(QUIT);
                } else if (message == CompletedDialog.NEXT) {
                    call(NEXT_LEVEL);
                }
            }
        });

        state = PLAY;
        afterBuild();

    }

    //some data updated, reflect the changes
    private void dataChanged() {
        float heroHealthRatio = hero.getHealthRatio();
        panel.setHealth(heroHealthRatio);
        panel.setBones(hero.getNumBones());
        panel.setCoins(numCoins);

        if(hero.getNumBones() <=0) {
            boneBtn.setVisible(false);
        } else {
            boneBtn.setVisible(true);
        }
    }

    //set buttons visibility
    private void setButtonVisible(boolean visible) {
        if(visible) {
            controlPad.setVisible(true);
            jumpBtn.setVisible(true);
            stompBtn.setVisible(true);

            if(hero.getNumBones() > 0) {
                boneBtn.setVisible(true);
            }
        } else {
            controlPad.setVisible(false);
            jumpBtn.setVisible(false);
            stompBtn.setVisible(false);
            boneBtn.setVisible(false);
        }
    }

    //current level data
    public LevelData getLevelData() {
        LevelData lData = new LevelData();
        lData.heroHealth = hero.getHealth();
        lData.bullets = hero.getNumBones();
        lData.coins = numCoins;
        lData.bonusTimer = bonusTimer;
        lData.bonusTime = bonusTime;

        return lData;
    }

    //set level data with external data (for example modify data from sub level)
    public void setLevelData(LevelData lData) {
        hero.setHealth(lData.heroHealth);
        hero.setBones(lData.bullets);
        numCoins = lData.coins;
        bonusTimer = lData.bonusTimer;
        bonusTime = lData.bonusTime;
    }


    protected void afterBuild() {
        //inspect doors
        for (Door door : doors) {
            if (door.getTmx() == null) continue;

            //prepare and create sublevels
            if (subLevels == null) {
                subLevels = new HashMap<String, SubLevel>();
            }

            //level id of sublevels are offset by 1000
            SubLevel subLevel = new SubLevel(1000 + id, door.getTmx());
            subLevel.setBatch(batch);
            subLevels.put(door.getTmx(), subLevel);

            //add the total coins & enemies
            totalCoins += subLevel.getTotalCoins();
            totalEnemies += subLevel.getTotalEnemies();

        }

        //show challenge dialog
        showChallenge();
    }

    private void showChallenge() {
        pauseLevel(false);

        //calculate percentage
        int coinPct = (int) (minCoins*100);
        int enemyPct = (int) (minEnemies*100);

        //bonus time
        int time = (int) bonusTime;

        //create the dialog
        final ChallengeDialog cDialog = new ChallengeDialog(
                getWidth(),
                getHeight(),
                id,
                coinPct,
                enemyPct,
                time);

        //show
        addOverlayChild(cDialog);

        //monitor the feedback
        cDialog.addListener(new MessageListener(){
            @Override
            protected void receivedMessage(int message, Actor actor) {
                if(message == ChallengeDialog.ACCEPT) {
                    removeOverlayChild(cDialog);
                    resumeLevel();
                }
            }
        });

        SuperCat.media.playSound("dumdum.mp3");
    }

    //listen hero
    private MessageListener heroListener = new MessageListener(){
        @Override
        protected void receivedMessage(int message, Actor actor) {
            //hero throw bone
            if(message == Hero.THROW_BONE) {
                //get from pool and add to stage
                Bone bone = poolBone.obtain();
                bone.addListener(boneListener);
                addEntity(bone);
                bone.setY(hero.getY());

                //facing left
                if(hero.getScaleX() > 0) {
                    bone.setX(hero.getLeft());
                    bone.setASpeed(1200);
                    bone.setVX(-500);
                } else {
                    //facing right
                    bone.setX(hero.getRight());
                    bone.setASpeed(-1200);
                    bone.setVX(500);
                }

                //v speed same with hero v speed
                bone.setVY(hero.v.y);
                dataChanged();
            }
            else if(message == Hero.ON_DIE) {
                //stop camera following hero
                camController.followObject(null);

                //showing level failed dialog after some delay
                delayCall(1, new Delayer.Listener() {
                    @Override
                    public void onDelay() {
                        levelFailed();
                    }
                });
            }
        }
    };

    //
    private void levelFailed() {
        state = FAILED;
        stopMusic();
        pauseWorld();

        //show dialog
        addOverlayChild(failedDialog);

        SuperCat.trackEvent("fail_"+tmxFile);
        SuperCat.media.playMusic("music4.ogg",true,1,1);
    }

    //bone/ weapon listener
    private MessageListener boneListener = new MessageListener(){
        //remove
        @Override
        protected void receivedMessage(int message, Actor actor) {
            if(message == Bone.REMOVE) {
                removeBone((Bone) actor);
            }
        }
    };


    private void removeBone(Bone bone) {
        //remove from world
        removeEntity(bone);
        //clear listener
        bone.removeListener(boneListener);

        //add dust in inscreen
        if(!bone.skipDraw) {
            if(bone.dir != -1) {
                addDust(bone.getX() , bone.getY(), bone.dir);
            }
            SuperCat.media.playSound("weapon_hit.mp3");
        }

        //put back to pool
        poolBone.free(bone);
    }

    private Array<Coin> coins = new Array<Coin>();
    private void addCoin(Coin coin) {
        addEntity(coin);
        //store to array
        coins.add(coin);

        //coin listener
        coin.addListener(coinListener);
    }
    private void removeCoin(Coin coin) {
        //remove from world
        removeEntity(coin);
        //remove from array
        coins.removeValue(coin, true);
        //remove lisetener
        coin.removeListener(coinListener);

        //add the trail
        int num = (int) (Math.random()*2 + 3);
        while(num-- >0) {
            //get the object
            CoinTrail trail = poolCoinTrail.obtain();

            //put to world
            trail.setPosition(coin.getX(),coin.getY());
            trail.moveBy((float) (32 * Math.random() - 16), (float) (32 * Math.random() - 16));
            trail.addListener(coinTrailListener);
            addEntity(trail);

            //set random move
            if(trail.getX() > coin.getX()) {
                trail.setVX((float) (50 * Math.random()));
            } else {
                trail.setVX(-(float) (50 * Math.random()));
            }
            if(trail.getY() > coin.getY()) {
                trail.setVY((float) (50 * Math.random()));
            } else {
                trail.setVY(-(float) (50 * Math.random()));
            }
        }
    }

    //coin listener
    private MessageListener coinListener = new MessageListener(){
        @Override
        protected void receivedMessage(int message, Actor actor) {
            //remove coin
            if(message == Coin.REMOVE) {
                removeCoin((Coin) actor);
            }
        }
    };
    private MessageListener coinTrailListener = new MessageListener(){
        @Override
        protected void receivedMessage(int message, Actor actor) {
            //remove coin trail
            CoinTrail trail = (CoinTrail) actor;
            if(message == CoinTrail.REMOVE) {
                //remove from world, clear listener, and free the pool
                removeEntity(trail);
                trail.removeListener(coinTrailListener);
                poolCoinTrail.free(trail);
            }
        }
    };
    private MessageListener dustListener = new MessageListener(){
        @Override
        protected void receivedMessage(int message, Actor actor) {
            Dust dust = (Dust) actor;
            if(message == Dust.REMOVE) {
                //remove from world, clear listener, and free the pool
                removeEntity(dust);
                poolDust.free(dust);
                dust.removeListener(dustListener);
            }
        }
    };

    //dust spread direction
    //dir : 0, 1 ,2 ,3  --> top, right, bot, left
    public void addDust(float x, float y, int dir) {
        int num = (int) (4 + Math.random()*3);

        while(num-- >0) {
            //create the object
            Dust dust = poolDust.obtain();
            dust.setX(x);
            dust.setY(y);

            //set random speed based on direction
            if(dir == 0) {
                dust.moveBy((float) (Math.random() * 20 - 10), (float) (Math.random() * 10) + 1);
            } else if (dir == 1) {
                dust.moveBy((float) (Math.random() * 10 + 1), (float) (Math.random() * 20 - 10));
            } else if(dir == 2) {
                dust.moveBy((float) (Math.random() * 20 - 10), -(float) (Math.random() * 10) - 1);
            } else {
                dust.moveBy((float) -(Math.random() * 10 + 1), (float) (Math.random() * 20 - 10));
            }

            if(dust.getX() > x) {
                dust.setVX((float) (100 * Math.random()));
            } else {
                dust.setVX(-(float) (100 * Math.random()));
            }

            if(dust.getY() > y) {
                dust.setVY((float) (100 * Math.random()));
            } else {
                dust.setVY(-(float) (100 * Math.random()));
            }

            //put to stage
            addEntity(dust);
            dust.addListener(dustListener);
        }
    }

    private void createBrick(MapObject object) {
        Rectangle rect=null;
        if(object instanceof RectangleMapObject) {
            rect = ((RectangleMapObject) object).getRectangle();
        }

        //how many bricks can be put on the rectangle
        int numX = (int) (rect.width/Level.GRID);
        int numY = (int) (rect.height/Level.GRID);

        //put bricks on the rectangle
        for(int row=0;row<numY;row++) {
            for(int col=0;col<numX;col++) {
                Brick brick = null;

                if(object.getName() != null) {

                    //if auto destroy brick
                    if(object.getName().equals("break")) {
                        brick = new Brick2(this);
                        brick.setPosition(rect.x + col*Level.GRID + Level.GRID/2 , rect.y + row*Level.GRID + Level.GRID/2);
                        addLand(brick,false);
                    }
                } else {

                    //normal brick
                    brick = new Brick();
                    brick.setPosition(rect.x + col*Level.GRID + Level.GRID/2 , rect.y + row*Level.GRID + Level.GRID/2);
                    addLand(brick, true);


                }

                if(brick != null) {

                    //if the brick has coins, add it
                    if (object.getProperties().get("coins") != null) {
                        int coins = Integer.parseInt(object.getProperties().get("coins").toString());
                        brick.addCoins(coins);
                        totalCoins += coins;
                    }
                }
            }
        }
    }

    // add to world, set listener
    protected void createHero(Hero hero) {
        addEntity(hero);
        hero.setDirRight(true);
        hero.addListener(heroListener);
    }
    private void createObject(MapObject object) {
        String name=object.getName();
        Rectangle rect=null;

        //get the rectangle
        if(object instanceof RectangleMapObject) {
            rect = ((RectangleMapObject) object).getRectangle();
        }

        if(name !=null) {
            //create object based on the name
            if(rect!=null) {
                if(name.equals("hero")) {
                    hero = new Hero(this);
                    hero.setPosition(rect.x + rect.width/2 , rect.y + hero.getHeight()/2);
                    createHero(hero);
                }
                else if(name.equals("coin")) {
                    //create coin, set position
                    Coin coin = new Coin();
                    coin.setPosition(rect.x + rect.width / 2, rect.y + rect.height / 2);
                    addCoin(coin);

                    totalCoins++;
                }
                else if(name.equals("mystery")) {
                    createMysteryBlock(object);
                }
                else if(name.equals("enemy1")) {
                    //create enemy
                    Enemy1 enemy = new Enemy1(this);
                    enemy.setX(rect.x + rect.width/2);
                    enemy.setY(rect.y + enemy.getHeight() / 2);

                    addEnemy(enemy);
                    totalEnemies++;
                }
                else if(name.equals("enemy2")) {
                    //create enemy
                    Enemy2 enemy = new Enemy2(this);
                    enemy.setX(rect.x + rect.width/2);
                    enemy.setY(rect.y + enemy.getHeight() / 2);

                    addEnemy(enemy);
                    totalEnemies++;
                }
                else if(name.equals("enemy3")) {
                    //create enemy
                    Enemy3 enemy = new Enemy3(this);
                    enemy.setX(rect.x + rect.width / 2);
                    enemy.setY(rect.y + enemy.getHeight() / 2);
                    addEnemy(enemy);

                    //the vertical flying distance
                    int dy = Integer.parseInt(object.getProperties().get("dy").toString());
                    enemy.setFly(enemy.getY() , enemy.getY()+dy* Level.GRID);

                    totalEnemies++;
                }
                else if(name.equals("flip")) {
                    //flip object
                    Flip flip = new Flip();
                    flip.setSize(rect.width, rect.height);
                    flip.setPosition(rect.x + rect.width / 2, rect.y + rect.height / 2);
                    addEntity(flip);
                }
                else if(name.equals("elevator")) {
                    //elevator
                    Elevator elevator = new Elevator(rect.width,rect.height);
                    elevator.setPosition(rect.x + rect.width/2 , rect.y + rect.height/2);
                    addLand(elevator, false);

                    //set the positions
                    elevator.setup((String) object.getProperties().get("pos"));
                }
                else if(name.equals("missile_launcher")) {
                    //missile launcher
                    MissileLauncher launcher = new MissileLauncher(this);
                    launcher.setPosition(rect.x + rect.width/2 , rect.y + launcher.getHeight()/2-4);
                    addEntity(launcher);

                }
                else if(name.equals("stone_left")) {
                    //falling stone, move left
                    int id = Integer.parseInt(object.getProperties().get("id").toString());
                    Stone stone = new Stone(this,id,false);
                    stone.setPosition(rect.x + rect.width/2 , rect.y + stone.getHeight()/2);
                    addEntity(stone);
                }
                else if(name.equals("stone_right")) {
                    //falling stone, move right
                    int id = Integer.parseInt(object.getProperties().get("id").toString());
                    Stone stone = new Stone(this,id,true);
                    stone.setPosition(rect.x + rect.width/2 , rect.y + stone.getHeight()/2);
                    addEntity(stone);
                }
                else if(name.equals("stone_trigger")) {
                    //falling stone trigger
                    int id = Integer.parseInt(object.getProperties().get("id").toString());
                    StoneTrigger trigger = new StoneTrigger(id);
                    trigger.setSize(rect.width, rect.height);
                    trigger.setPosition(rect.x + rect.width / 2, rect.y + rect.height / 2);
                    addEntity(trigger);
                }
                else if(name.equals("door") || name.equals("door_in") || name.equals("door_out")) {

                    int id = 0;
                    if(object.getProperties().get("id") != null) {
                        id = Integer.parseInt(object.getProperties().get("id").toString());
                    }

                    //tmx to load when entering door
                    String tmx = null;
                    if(object.getProperties().get("tmx") != null) {
                        tmx = object.getProperties().get("tmx").toString();
                    }

                    Door door = new Door(this,id,rect.width,rect.height,tmx);
                    door.setPosition(rect.x + rect.width / 2, rect.y + rect.height / 2);
                    door.addListener(doorListener);

                    //set door type
                    if(name.equals("door_in")) {
                        door.setType(Door.TYPE_IN);
                    }
                    else if(name.equals("door_out")) {
                        door.setType(Door.TYPE_OUT);
                        door.setDisable(true);
                    }

                    //no key is needed, unlock it
                    if(object.getProperties().containsKey("nokey")) {
                        if(object.getProperties().get("nokey").equals("true")) {
                            door.unlock();
                        }
                    }

                    createDoor(door);
                }
                else if(name.equals("key")) {
                    Key key = new Key();
                    key.setPosition(rect.x + rect.width / 2, rect.y + key.getHeight() / 2);
                    addEntity(key);
                }
                else if(name.equals("bones")) {
                    //bones / weapon pack
                    int num = 10;  //default number of weapon

                    //override the number if set
                    if(object.getProperties().get("num") != null) {
                        num = Integer.parseInt(object.getProperties().get("num").toString());
                    }

                    //create
                    Bones bones = new Bones(num);
                    addEntity(bones);
                    bones.setPosition(rect.x + rect.width / 2, rect.y + bones.getHeight() / 2);
                }
                else if(name.equals("finish")) {
                    //the finish object
                    Finish finish = new Finish();
                    finish.setSize(rect.width, rect.height);
                    finish.setPosition(rect.x + rect.width / 2, rect.y + rect.height / 2);
                    addEntity(finish);
                }
                else if(name.equals("sign")) {
                    //sign
                    //get the text
                    String text = object.getProperties().get("text").toString();
                    Sign sign = new Sign(text);
                    sign.setX(rect.x + rect.width/2);
                    sign.setY(rect.y + sign.getHeight()/2);
                    sign.addListener(signListener);
                    addEntity(sign);
                }
            }
        } else {

            if(rect!=null) {
                //don't have a name, should be a platform
                Entity ent = new Entity();
                ent.setSize(rect.width,rect.height);
                ent.setPosition(rect.x + rect.width / 2, rect.y + rect.height / 2);
                addLand(ent, true);
            }
        }
    }

    private MessageListener signListener = new MessageListener(){
        @Override
        protected void receivedMessage(int message, Actor actor) {
            Sign sign = (Sign) actor;

            //read the sign
            if(message == Sign.READ) {
                //pause
                pauseLevel(false);

                //add the dialog of reading
                final ReadDialog dialog = new ReadDialog(getWidth(),getHeight(),sign.getText());
                addOverlayChild(dialog);

                //listen on close event
                dialog.addListener(new MessageListener(){
                    @Override
                    protected void receivedMessage(int message, Actor actor) {
                        if(message == ReadDialog.CLOSE) {
                            removeOverlayChild(dialog);
                            resumeLevel();
                        }
                    }
                });
            }
        }
    };

    private Array<Door> doors = new Array<Door>();
    protected void createDoor(Door door) {
        //if type in or out, find the pair with the same id
        if(door.getType() !=0) {
            for (Entity ent : getEntityList()) {
                if (ent instanceof Door) {
                    Door other = (Door) ent;

                    //found it
                    if (door.id == other.id) {
                        door.setPair(other);
                        other.setPair(door);

                        break;
                    }
                }
            }
        }

        addEntity(door);
        doors.add(door);
    }

    private Door enteringDoor;


    protected MessageListener doorListener = new MessageListener(){
        @Override
        protected void receivedMessage(int message, Actor actor) {
            Door door = (Door) actor;

            //open door
            if(message == Door.OPEN) {
                //tmx to load
                String tmx = door.getTmx();

                //start the sublevel
                startSubLevel(tmx);
                door.setTmpDisable();
                enteringDoor = door;
            }
        }
    };



    public SubLevel getSubLevel() {
        SubLevel subLevel=null;
        subLevel = subLevels.get(selectedSubLevelTmx);
        subLevel.setLevelData(getLevelData());

        return subLevel;
    }
    private void startSubLevel(String tmx) {
        selectedSubLevelTmx = tmx;
        call(START_SUB_LEVEL);
    }

    //put missile, add listener
    protected void addMissile(MissileLauncher launcher , Missile missile) {
        addEntityBefore(launcher, missile);
        missile.addListener(missileListener);
    }

    private MessageListener missileListener = new MessageListener(){
        @Override
        protected void receivedMessage(int message, Actor actor) {
            Missile missile = (Missile) actor;

            //missile explode
            if(message == Missile.EXPLODE) {
                //clear listener
                missile.removeListener(missileListener);



                //if in screen, add explosion
                if(missile.inView) {

                    Explosion exp = poolExplosion.obtain();
                    exp.setPosition(missile.getX(), missile.getY());
                    addEntity(exp);
                    addHit(missile.getX(), missile.getY());

                    SuperCat.media.playSound("missile_explode.mp3");

                    //if hero in damage radius
                    if(!hero.isShielded()) {
                        float damageRad = 100;
                        float heroRad = World.pointDist2(hero.getX(), hero.getY(), missile.getX(), missile.getY());

                        if (heroRad < damageRad * damageRad) {
                            heroRad = (float) Math.sqrt(heroRad);
                            float damage = (damageRad - heroRad) / damageRad * missile.getDamage();
                            hero.attackedBy(damage,0, missile.getX(), missile.getY());
                        }
                        dataChanged();
                    }
                }

                //remove & free the pool
                removeEntity(missile);
                poolMissile.free(missile);


            }
        }
    };

    //add enemy and set listener
    private void addEnemy(Enemy enemy) {
        addEntity(enemy);
        enemy.addListener(enemyListener);
    }

    //remove enemy and clear listener
    private void removeEnemy(Enemy enemy) {
        enemy.removeListener(enemyListener);
        removeEntity(enemy);
    }

    private MessageListener enemyListener = new MessageListener(){
        @Override
        protected void receivedMessage(int message, Actor actor) {
            if(message == Enemy.REMOVE) {
                removeEnemy((Enemy) actor);
            }

            //enemy2 throw bullet
            else if(message == Enemy2.THROW_BULLET) {
                addBulletEnemy2(actor.getX() , actor.getY() + 20, actor.getScaleX() == -1);
            }
            //enemy is beaten by hero
            else if(message == Enemy.BEATEN) {
                numBeatenEnemies++;
            }
        }
    };

    private void addBulletEnemy2(float x, float y, boolean toRight) {
        //get bullet
        Bullet bullet = poolBulletEnemy2.obtain();
        //set position, add to world, and set listener
        bullet.setPosition(x,y);
        addEntity(bullet);
        bullet.addListener(bulletListener);

        //where to throw
        if(toRight) {
            bullet.setVX(500);
        } else {
            bullet.setVX(-500);
        }
    }
    private void removeBullet(Bullet bullet) {
        bullet.removeListener(bulletListener);
        removeEntity(bullet);

        //free pool
        if(bullet instanceof BulletEnemy2) {
            poolBulletEnemy2.free((BulletEnemy2) bullet);

        }
    }
    private MessageListener bulletListener = new MessageListener(){
        @Override
        protected void receivedMessage(int message, Actor actor) {
            if(message == Bullet.EXPLODE) {
                //remove bullet
                removeBullet((Bullet) actor);

                int dir;
                Bullet bullet = (Bullet) actor;
                if(bullet.lastV.x <0) {
                    dir = 3;
                } else {
                    dir = 1;
                }
                //dust splash
                addDust(actor.getX() , actor.getY(), dir);

            }
            else if(message == Bullet.REMOVE) {
                removeBullet((Bullet) actor);
            }
        }
    };

    @Override
    public void addEntity(Entity ent) {
        super.addEntity(ent);

        //hero always on top
        if(hero !=null) {
            hero.getStage().addActor(hero);
        }
    }

    private void createMysteryBlock(MapObject object) {
        Rectangle rect = ((RectangleMapObject) object).getRectangle();

        Mystery mystery = new Mystery();
        mystery.setPosition(rect.x + rect.width / 2, rect.y + rect.height / 2);
        addLand(mystery, false);

        //the content
        //has coins
        if(object.getProperties().get("coins") !=null) {
            mystery.addCoin(Integer.parseInt(object.getProperties().get("coins").toString()));
            totalCoins += Integer.parseInt(object.getProperties().get("coins").toString());
        }
        //bullets
        else if(object.getProperties().get("bones") !=null) {
            mystery.addBullet(Integer.parseInt(object.getProperties().get("bones").toString()));
        }
        //enemy
        else if(object.getProperties().get("enemy") !=null) {
            mystery.addEnemy(Integer.parseInt(object.getProperties().get("enemy").toString()));
            totalEnemies++;
        }
    }

    //get the path to tmx file
    protected TiledMap getTiledMap(String fileName) {
        String path = "tiled/"+fileName;

        //not used
        if(SuperCat.tester) {
            path = "tiledtest/test.tmx";
        }

        // filter
        TmxMapLoader.Parameters params = new TmxMapLoader.Parameters();
        params.generateMipMaps = true;
        params.textureMinFilter = Texture.TextureFilter.MipMapLinearNearest;
        params.textureMagFilter = Texture.TextureFilter.Linear;

        TiledMap map ;

        //load the tmx
        if(SuperCat.tester) { // not used
            map = new TmxMapLoader(new LocalFileHandleResolver()).load(path, params);
        } else {
            map = new TmxMapLoader().load(path, params);
        }

        return map;
    }

    //monitor the collision
    @Override
    public void onCollide(Entity entA, Entity entB, float delta) {
        //hero hit some object
        if(entA == hero) {
            heroHitObject(entB);
            dataChanged();
            return;
        }
        if(entB == hero) {
            heroHitObject(entA);
            dataChanged();
            return;
        }

        //enemy hit some object
        if(entA instanceof Enemy) {
            enemyHitObject((Enemy) entA, entB);
            return;
        }
        if(entB instanceof Enemy) {
            enemyHitObject((Enemy) entB,entA);
            return;
        }

        //missile hit missile
        if(entA instanceof Missile && entB instanceof Missile) {
            ((Missile)entA).explode();
            ((Missile)entB).explode();
            return;
        }

        //bone / weapon hit missile
        if(entA instanceof Bone && entB instanceof Missile) {
            Missile missile = (Missile) entB;

            //missile explode
            if(missile.isActivated()) {
                ((Bone) entA).hitObject(entB);
                ((Missile) entB).explode();
            }
            return;
        }
        if(entB instanceof Bone && entA instanceof Missile) {
            Missile missile = (Missile) entA;

            //missile explode
            if(missile.isActivated()) {
                ((Bone) entB).hitObject(entA);
                ((Missile) entA).explode();
            }
            return;
        }


    }

    //enemy hit enemy
    private void enemyHitEnemy(Enemy enemy1, Enemy enemy2) {
        //flip them
        if(enemy2.getX() > enemy1.getX()) {
            if(enemy1.isMoveRight()) {
                enemy1.flip();
            }
            if(!enemy2.isMoveRight()) {
                enemy2.flip();
            }
        }
        else if(enemy2.getX() < enemy1.getX()) {
            if(enemy2.isMoveRight()) {
                enemy2.flip();
            }
            if(!enemy1.isMoveRight()) {
                enemy1.flip();
            }
        }

    }
    private void enemyHitObject(Enemy enemy, Entity ent) {
        if(enemy.hasDied()) return;

        //enemy hit weapon
        if(ent instanceof Bone) {
            Bone bone = (Bone) ent;

            //attack enemy
            enemy.attackedBy(bone);
            bone.hitObject(enemy);

            return;
        }

        //enemy hit other enemy
        if(ent instanceof Enemy) {
            Enemy enemy2 = (Enemy) ent;
            enemyHitEnemy(enemy, enemy2);

            return;
        }

        //enemy hit flip object
        if(ent instanceof Flip && !enemy.isInAir()) {
            //change direction
            if(ent.getX() < enemy.getX() ) {
                if(!enemy.isMoveRight()) {
                    enemy.setMoveRight(true);
                }
            }
            else  {
                if(enemy.isMoveRight()) {
                    enemy.setMoveRight(false);
                }
            }
            return;
        }
    }

    //add hit animation
    private void addHit(float x, float y) {
        Hit hit = poolHit.obtain();
        hit.setPosition(x,y);
        addEntity(hit);
    }
    private void heroHitObject(Entity ent) {
        if(state != PLAY) return;

        //hero hit coin
        if(ent instanceof Coin) {
            removeCoin((Coin) ent);
            numCoins++;
            SuperCat.media.playSound("coin.mp3");
            return;
        }

        //hero hit enemy
        if(ent instanceof Enemy) {
            Enemy enemy = (Enemy) ent;

            //is hero stomping?
            if(enemy.getY() < hero.getY() && hero.v.y < 0 && !hero.isAttacked()) {
                if(hero.stomp(enemy)) {
                    enemy.attackedBy(hero);
                    hero.afterStompAttack();

                    addHit(hero.getX(), hero.getBottom());
                }
            }
            //enemy atack hero
            else if(!hero.isShielded()) {
                enemy.touchHero(hero);
                hero.attackedBy(enemy);
            }

            return;
        }

        //hero get weapon pack
        if(ent instanceof  Bones) {
            removeEntity(ent);

            //add bones
            hero.addBones(((Bones) ent).num);
            SuperCat.media.playSound("special_item.mp3");

            return;
        }

        //hero hit stone trigger
        if(ent instanceof StoneTrigger) {
            int id = ((StoneTrigger)ent).id;

            Array<Entity> entities  = getEntityList();
            int i;
            Entity e;

            //find the stone object and roll it!
            Stone stone;
            for(i=0;i<entities.size;i++) {
                e = entities.get(i);
                if(e instanceof Stone) {
                    stone = (Stone) e;
                    if(stone.id == id) {
                        stone.setActivated();
                    }
                }
            }

            return;
        }


        if(ent instanceof Door) {
            Door door = (Door) ent;

            //unlock if have key
            if(door.isLocked() && hero.isHaveKey()) {
                door.unlock();
            }
            //show enter button or 'locked' image
            door.touchHero();
            return;
        }

        //get key
        if(ent instanceof Key) {
            removeEntity(ent);
            hero.setHaveKey();
            SuperCat.media.playSound("special_item.mp3");

            return;
        }

        //reach finish
        if(ent instanceof Finish) {

            state = COMPLETED;


            camController.followObject(null);
            ent.setNoCollision(true);
            hero.reachFinish();
            removeLand(wallRight);

            //show level completed screen after delay
            delayCall(0.45f, new Delayer.Listener() {
                @Override
                public void onDelay() {
                    levelCompleted();
                }
            });

            return;
        }
        if(ent instanceof Sign) {
            ((Sign)ent).touchHero();
            return;
        }
        if(!hero.isShielded()) {
            //hero hit bullet
            if(ent instanceof Bullet) {
                Bullet bullet = (Bullet) ent;
                if(bullet.enemySide) {
                    //hero attached by enemy's bullet
                    hero.attackedBy(bullet);
                    bullet.hitObject(hero);
                }
                return;
            }
            if(ent instanceof Missile) {
                Missile missile = (Missile) ent;
                //hero attacked by missile
                if(missile.isActivated()) {
                    hero.attackedBy(missile.getDamage(),missile.v.x, missile.getX(), missile.getY());
                    missile.explode();
                }
                return;
            }
            if(ent instanceof Stone) {
                //hero hit by stone
                if(hero.getBottom() > ent.getTop() - 20) {
                    hero.setVY(450);
                } else {
                    hero.attackedBy(ent);
                }
                return;
            }
            return;
        }

    }

    private void levelCompleted() {
        stopMusic();
        pauseWorld();

        //update progress
        SuperCat.data.setProgress(id+1);
        addOverlayChild(completedDialog);

        //calculate result
        final float coinR = 100*(float)numCoins/totalCoins;
        final float enemyR = 100*(float)numBeatenEnemies/totalEnemies;
        final int lastStars = SuperCat.data.getStars(id);

        //the new stars reward
        int newStars=0;
        if(coinR > minCoins*100) {
            newStars += 100;
        }
        if(enemyR > minEnemies*100) {
            newStars += 10;
        }
        if(time < bonusTime) {
            newStars += 1;
        }
        final int finalNewStars = newStars;
        SuperCat.data.setStars(id,newStars);

        //show completed dialog
        completedDialog.setData(coinR,enemyR,time);

        SuperCat.media.playSound("level_completed.mp3");

        //consecutive showing 'tick' with delays

        //tick 1
        delayCall(0.3f, new Delayer.Listener() {
            @Override
            public void onDelay() {
                if(coinR > minCoins*100) {
                    completedDialog.setTick1();
                }
            }
        });
        //tick 2
        delayCall(0.6f, new Delayer.Listener() {
            @Override
            public void onDelay() {
                if(enemyR > minEnemies*100) {
                    completedDialog.setTick2();
                }
            }
        });
        //tick 3
        delayCall(0.9f, new Delayer.Listener() {
            @Override
            public void onDelay() {
                if(time < bonusTime) {
                    completedDialog.setTick3();
                }
            }
        });

        delayCall(1.5f, new Delayer.Listener() {
            @Override
            public void onDelay() {
                completedDialog.showStars(lastStars, finalNewStars);
            }
        });
        delayCall(1.6f, new Delayer.Listener() {
            @Override
            public void onDelay() {
                completedDialog.showBtn();
                SuperCat.media.playSound("level_completed2.mp3");
            }
        });

        SuperCat.trackEvent("complete_"+tmxFile);
        SuperCat.media.playMusic("music3.ogg",true,1,2);
    }

    @Override
    protected void update(float delta) {
        if(state == PLAY) {
            //check button state
            boolean leftBtn = Gdx.input.isKeyPressed(Input.Keys.A) || controlPad.isLeft();
            boolean rightBtn = Gdx.input.isKeyPressed(Input.Keys.D) || controlPad.isRight();
            boolean jumpKey = Gdx.input.isKeyPressed(Input.Keys.L) || jumpBtn.isPressed() ;
            boolean stompKey = Gdx.input.isKeyPressed(Input.Keys.COMMA) || stompBtn.isPressed();
            boolean throwKey = Gdx.input.isKeyPressed(Input.Keys.K) || boneBtn.isPressed();

            //pass the states to hero
            hero.updateKey(delta, leftBtn, rightBtn, jumpKey,stompKey, throwKey);

            //hero fall
            if(!hero.hasDied() && hero.getTop() <0) {
                hero.fall();
            }

            //reduce bonus timer
            bonusTimer -= delta;

            //update ui
            panel.setTime(bonusTimer/bonusTime);
            time += delta;
        }

        //load the custom music
        else if(state == LOADING_MUSIC) {
            if(SuperCat.media.updateAssets()) {
                //completed
                build();
            }
        }
    }


    //show image while level is being built
    protected void preload() {
        //load the texture
        preloadTexture = new Texture(Gdx.files.internal("please_wait.png"));
        preloadTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        //put to screen
        Image img = new Image(preloadTexture);
        addChild(img);
        centerActorXY(img);
    }


    public void heroTouchBlock(Entity ent) {
        //hero touch brick
        if(ent instanceof Brick) {
            //hit from below
            if(hero.getY() < ent.getY()) {
                destroyBrick((Brick) ent);
            } else { //hit from above
                destroyBrick((Brick) ent, false);
            }
            return;
        }

        //hit mystery box
        if(ent instanceof Mystery) {
            Mystery mystery = (Mystery) ent;

            if(hero.getY() > ent.getY()) {
                if(!isHole(ent.getX(),ent.getBottom()-GRID/2)) {
                    return;
                }
            }

            //has empty
            if(mystery.hasExpired())  {
                return;
            }

            mystery.touched();


            Coin coin = mystery.getCoin();

            //contain coin
            if(coin != null) {
                if(hero.getY() < ent.getY()) {
                    //set coin position
                    coin.setPosition(mystery.getX(), mystery.getTop() + coin.getHeight() / 2);

                    //animation only
                    coin.setAnimationOnly();
                    coin.setVY(400);

                    numCoins++;
                    dataChanged();

                } else {
                    //add coin below block
                    coin.setPosition(mystery.getX(), mystery.getBottom() - coin.getHeight() / 2);
                    coin.setVY(-400);
                    coin.setVX((float) (600*Math.random()-300));
                    coin.setFloat(false);
                }
                addCoin(coin);
            }

            //contain weapon/bone
            else if(mystery.getBones() != null) {
                //add weapon pack
                Bones bones = mystery.getBones();
                bones.setX(ent.getX());

                //set the position
                if(hero.getY() < ent.getY()) {
                    bones.setY(ent.getTop() + bones.getHeight()/2);
                    bones.setVY(400);
                } else {
                    bones.setY(ent.getBottom() - bones.getHeight()/2);
                    bones.setVY(-200);
                }
                addEntity(bones);
            }

            //contain enemy
            else if(mystery.getEnemy() !=null) {
                //get the enemy
                Enemy enemy = mystery.getEnemy();
                enemy.setX(ent.getX());

                //set position
                if(hero.getY() < ent.getY()) {
                    enemy.setY(ent.getTop() + enemy.getHeight()/2);
                    enemy.setVY(400);
                } else {
                    enemy.setY(ent.getBottom() - enemy.getHeight()/2);
                    enemy.setVY(-200);
                }
                addEnemy(enemy);
            }
        }
    }
    protected void destroyBrick(Brick brick) {
        destroyBrick(brick,true);
    }
    protected void destroyBrick(Brick brick,boolean fromBellow) {
        //remove the brick
        removeLand(brick);

        //put the debris
        Brick.Debris[] debrises = brick.getDebrises();

        for(int i=0;i<debrises.length;i++) {
            Brick.Debris d = debrises[i];
            //adjust the positions
            if(i==0) {
                d.setPosition(brick.getX() - d.getWidth() / 2, brick.getY() + d.getHeight() / 2);
                d.setV(-120,250);
            }
            else if(i==1) {
                d.setPosition(brick.getX() + d.getWidth() / 2, brick.getY() + d.getHeight() / 2);
                d.setV(120, 250);
            }
            else if(i==2) {
                d.setPosition(brick.getX() - d.getWidth() / 2, brick.getY() - d.getHeight() / 2);
                d.setV(-100, 200);
            }
            else {
                d.setPosition(brick.getX() + d.getWidth() / 2, brick.getY() - d.getHeight() / 2);
                d.setV(100, 200);
            }

            if(!fromBellow) {
                d.setVY(-d.v.y);
            }

            addEntity(d);
        }

        //brick has coins??
        Coin[] coins = brick.getCoins();
        if(coins != null) {
            for(int i=0;i<coins.length;i++) {
                Coin coin = coins[i];

                //put into the right position
                if(coins.length==1) {
                    coin.setX(brick.getX());
                    coin.setY(brick.getY());
                    coin.setVY(200);
                } else {
                    //more than one coin, put on random position inside brick
                    coin.setX(brick.getLeft() + (float) (Math.random() * (brick.getWidth() - coin.getWidth())) + coin.getWidth() / 2);
                    coin.setY(brick.getBottom() + (float) (Math.random() * (brick.getHeight() - coin.getHeight())) + coin.getHeight() / 2);
                    coin.setVDeg(200, (float) (Math.random()*180));
                }
                addCoin(coin);
                coin.setFloat(false);

                if(!fromBellow) {
                    coin.setVY(-coin.v.y);
                }

            }
        }

        if(!brick.skipDraw) SuperCat.media.playSound("brick.mp3");
    }

    //monitor back key
    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.P) {
            if(state == PLAY) {
                pauseLevel(false);
            }
            else if(state == PAUSE) {
                resumeLevel();
            }
            return true;
        }

        //pause game
        if(keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE) {
            pauseLevel(true);
            return true;
        }

        return super.keyDown(keycode);
    }
    private void pauseLevel(boolean withDialog) {
        if(state != PLAY) return;
        state = PAUSE;

        stopMusic();
        pauseWorld();

        //hide button
        setButtonVisible(false);

        //show pause dialog
        if(withDialog) {
            final PauseDialog dialog = new PauseDialog(getWidth(),getHeight());
            addOverlayChild(dialog);

            //listen the dialog
            dialog.addListener(new MessageListener() {
                @Override
                protected void receivedMessage(int message, Actor actor) {
                    if (message == PauseDialog.RESUME) {
                        removeOverlayChild(dialog);
                        resumeLevel();
                    }
                    if (message == PauseDialog.QUIT) {
                        removeOverlayChild(dialog);
                        quitLevel();
                    }
                }
            });
            SuperCat.media.playSound("dumdum.mp3");
        }
    }

    @Override
    public void pause() {
        if(state == PLAY) {
            pauseWorld();
        }
    }

    @Override
    public void resume() {
        if(state == PLAY) {
            resumeWorld();
        }
    }

    private void quitLevel() {
        SuperCat.trackEvent("level_"+id+"_quit");
        call(QUIT);
    }

    private void resumeLevel() {
        state = PLAY;
        resumeWorld();
        setButtonVisible(true);
        playMusic();
    }

    public void backFromSubLevel() {
        if(enteringDoor.getType() == Door.TYPE_IN) {
            //move hero to exit door
            hero.setX(enteringDoor.getPair().getX());
            hero.setY(enteringDoor.getPair().getBottom() + hero.getHeight());
        }

        enteringDoor = null;
    }

    public int getTotalCoins() {
        return totalCoins;
    }

    public int getTotalEnemies() {
        return totalEnemies;
    }
}
