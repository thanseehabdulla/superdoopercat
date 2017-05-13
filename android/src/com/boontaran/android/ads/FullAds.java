package com.boontaran.android.ads;

import android.app.Activity;

/**
 * Created by arifbs on 7/28/15.
 */
public abstract class FullAds {
    protected Activity activity;
    protected Listener listener;
    protected boolean enabled=true;
    protected boolean autoLoad=false;
    public FullAds(Activity activity) {
        this.activity = activity;
    }
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public abstract void load();
    public abstract void show();

    public boolean isLoaded() {
        if(!enabled) return false;
        return true;
    }

    public void setEnabled(boolean enable) {
        this.enabled = enable;
    }

    public void setAutoLoad(boolean autoLoad) {
        this.autoLoad = autoLoad;
        if(autoLoad) load();
    }
   
    
    public void onStart(){}
    public void onResume(){}
    public void onPause(){}
    public void onStop(){}
    public void onDestroy(){}
    public boolean onBackPressed(){
    	return false;
    }
    
    
    //
    
    public static interface Listener {
        void onClosed();
        void onCached();
        void onFailed();
    }
}
