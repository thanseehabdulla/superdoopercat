package com.boontaran.supercat.level;

import com.boontaran.supercat.SuperCat;

/**
 * Created by arifbs on 11/11/15.
 */
public class BulletEnemy2 extends Bullet {
    public BulletEnemy2() {
        setImage(SuperCat.createImage("enemy2_bullet"));
        setRadius(10);
        enemySide = true;
    }

}
