package com.boontaran.supercat.level;

import com.boontaran.games.platformerLib.Entity;

/**
 * Created by arifbs on 11/13/15.
 *
 * objetc to trigger the stone with same id
 */
public class StoneTrigger extends Entity {
    public int id;

    public StoneTrigger(int id) {
        this.id = id;

        setNoLandCollision(true);
        noGravity = true;
    }
}
