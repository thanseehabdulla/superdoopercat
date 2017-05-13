package com.boontaran.supercat.level;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.boontaran.games.platformerLib.Entity;
import com.boontaran.supercat.SuperCat;

/**
 * Created by arifbs on 11/12/15.
 */
public class Elevator extends Entity {
    //positions to visit, should not be diagonally
    private Vector2[] positions;

    //current target id
    private int targetPosId;
    private float speed = 100;
    private float cSpeed; //current speed

    //indicate vert / horz moving
    private boolean moveVertically = false;

    public Elevator(float w, float h) {
        setSize(w, h);

        //calculate how many pieces needed to construct elevator
        int num = (int) (w/64);

        //arrange the images
        for(int i=0;i<num;i++) {
            Image img;
            if(i==0) {
                img = SuperCat.createImage("elevator_left");
                img.setX(i*64);
            } else if(i==num-1) {
                img = SuperCat.createImage("elevator_right");
                img.setX(i*64);
            } else {
                img = SuperCat.createImage("elevator_mid");
                img.setX(i*64-4);
            }
            addActor(img);
        }

        //center it
        contentY = -32;
        contentX = -num*64/2;

        //update until half out of screen
        edgeUpdateLimRatio = 0.5f;
    }


    private void goTo(int target) {
        if(target == targetPosId) return; //already go to the position
        if(target == positions.length) {  //return to the first position
            target = 0;
        }

        //get the postion
        targetPosId = target;
        Vector2 pos = positions[targetPosId];

        //difference
        float dx = pos.x - getX();
        float dy = pos.y - getY();

        //define if move vertically or horizontally and set the speed
        if(Math.abs(dy) > Math.abs(dx)) {
            moveVertically = true;
            if(dy>0) {
                cSpeed = speed;
            } else {
                cSpeed = -speed;
            }
        } else {
            moveVertically = false;
            if(dx>0) {
                cSpeed = speed;
            } else {
                cSpeed = -speed;
            }
        }
    }

    //create position list based in property
    private void createPositions(String pos) {
        //split by space
        String posArr[] = pos.split(" ");
        positions = new Vector2[posArr.length+1];
        positions[0] = new Vector2(getX(), getY());
        int j=1;
        for(int i=0 ;i<posArr.length;i++) {
            //split comma
            String[] s = posArr[i].split(",");

            //create vector
            Vector2 v = new Vector2(getX()+Float.parseFloat(s[0])*Level.GRID, getY()+Float.parseFloat(s[1])*Level.GRID);
            positions[j] = v;
            j++;
        }
    }

    //pass the position string, format --> "x1,y1 x2,y2, etc....."
    public void setup(String pos) {
        createPositions(pos);
        goTo(1);
    }

    @Override
    public void update(float delta) {
        //get teh position
        Vector2 pos = positions[targetPosId];
        float dx = pos.x - getX();
        float dy = pos.y - getY();

        //move based on mode (vert / horz)
        if(moveVertically) {
            setX(pos.x);
            setVX(0);
            setVY(cSpeed);

            //if pass the target point, goto next position
            if(cSpeed > 0 && dy <0) {
                setY(pos.y);
                goTo(targetPosId+1);
            }
            else if (cSpeed < 0 && dy >0) {
                setY(pos.y);
                goTo(targetPosId+1);
            }
        } else {
            setY(pos.y);
            setVX(cSpeed);
            setVY(0);

            //if pass the target point, goto next position
            if(cSpeed > 0 && dx <0) {
                setX(pos.x);
                goTo(targetPosId+1);
            }
            else if (cSpeed < 0 && dx >0) {
                setX(pos.x);
                goTo(targetPosId+1);
            }
        }

    }
}
