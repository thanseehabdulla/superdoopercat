package com.boontaran.supercat.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.boontaran.supercat.Callback;
import com.boontaran.supercat.SuperCat;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        //screen size, play with this numbers to simulate different device screen size
        config.width = 800;
        config.height = 480;

        System.out.println("Desktop mode, keys :");
        System.out.println("A : Left");
        System.out.println("D : Right");
        System.out.println("K : Throw Weapon");
        System.out.println("L : Jump");
        System.out.println("comma : Stomp");

        new LwjglApplication(new SuperCat(new Callback() {
            @Override
            public void sendMessage(int message) {
                System.out.println("Desktop , sendMessage : "+message);

                if(message == SuperCat.EXIT_APP) {
                    Gdx.app.exit();
                }
            }

            @Override
            public void trackEvent(String label) {
                System.out.println("Desktop , trackEvent : "+label);
            }

            @Override
            public void trackPage(String name) {
                System.out.println("Desktop , trackPage : "+name);
            }


        }), config);
	}
}
