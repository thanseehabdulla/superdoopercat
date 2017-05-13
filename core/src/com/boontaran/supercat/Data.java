package com.boontaran.supercat;

import com.boontaran.DataManager;
import com.boontaran.Encryptor;

/**
 * Created by arifbs on 10/19/15.
 */
public class Data {
    private DataManager manager;

    public Data() {
        //create persistence preference
        manager = new DataManager("cat3", false);

        //do some encryption
        manager.setEncryptor(new Encryptor() {
            @Override
            public String encrypt(int value, long value2) {
                return DataManager.md5(String.valueOf(2 * value + value2 - 11233));
            }
        });

        //is user tring to edit the data? if seems yes, reset the data
        manager.setListener(new DataManager.Listener() {
            @Override
            public void onTampered() {
                manager.clear();

                int i=0;
                while(i==0){ //and crash it..

                }
            }
        });
    }

    //write to storage
    public void flush() {
        manager.flush();
    }

    //progress of played leve
    public int getProgress() {
        return manager.getInt("prgs",1);
    }
    public void setProgress(int progress) {
        if(progress <= getProgress()) return;
        manager.saveInt("prgs", progress);
    }

    //save the reward of stars
    public void setStars(int levelId,int data) {
        //check the previous stars
        int prev = getStars(levelId);
        boolean star1=false,star2=false,star3=false;

        //check individually, is the some stars are already collected?
        if(prev >= 100) {
            star1 = true;
            prev = prev % 100;
        }
        if(prev >= 10) {
            star2 = true;
            prev = prev % 10;
        }
        if(prev > 0) {
            star3 = true;
        }

        //the new collected stars
        boolean newStar1=false,newStar2=false,newStar3=false;
        if(data >= 100) {
            newStar1 = true;
            data = data % 100;
        }
        if(data >= 10) {
            newStar2 = true;
            data = data % 10;
        }
        if(data > 0) {
            newStar3 = true;
        }

        //merger the new & previous stars
        newStar1 = newStar1 || star1;
        newStar2 = newStar2 || star2;
        newStar3 = newStar3 || star3;

        data = 0;
        if(newStar1) data += 100;
        if(newStar2) data += 10;
        if(newStar3) data += 1;

        //save the data
        String key = "slt"+levelId;
        data += levelId;
        manager.saveInt(key,data);
    }
    //get the stars of level id, stars saved on 3 digit(1 or 0 each digits) representing the 3 stars of each levels
    public int getStars(int levelId) {
        String key = "slt"+levelId;
        int value = manager.getInt(key,0);

        if(value == 0) return 0;
        return value-levelId;
    }

    //get the star type (is it 1,2, or 3)  --> star 1, star2 star 3
    public boolean isStar(int levelId, int type) {
        int data = getStars(levelId);

        if(data >= 100) {
            if(type==1) {
                return true;
            }
            data = data % 100;
        }
        if(data >= 10) {
            if(type == 2) {
                return true;
            }
            data = data % 10;
        }
        if(data > 0) {
            if(type == 3) {
                return true;
            }
        }
        return false;
    }

    //IAP
    //check the value, is it contain the same defined value
    public boolean isRemoveAds() {
        return manager.getInt("asdrr",0) == 552890441;
    }
    //set the value to indicate that the app has no-ads
    public void setRemoveAds() {
        manager.saveInt("asdrr",552890441);
    }

    //the sound state
    public boolean isMuted() {
        return manager.getBoolean("muted",false);
    }
    public void setMute(boolean muted) {
        manager.setBoolean("muted",muted);
        SuperCat.media.setMute(muted);
    }

}
