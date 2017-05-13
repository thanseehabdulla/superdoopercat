package com.boontaran.supercat.desktop;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;

/**
 * Created by arifbs on 4/18/16.
 */
public class PackerEclipse {
    public static void main(String[] args) {
        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.filterMin = Texture.TextureFilter.MipMapLinearNearest;
        settings.filterMag = Texture.TextureFilter.Linear;

        settings.paddingX = 2;
        settings.paddingY = 2;

        settings.maxHeight = 2048;
        settings.maxWidth = 2048;

        //where to generate?
        TexturePacker.process(settings, "raw_graphics", "../android/assets/images", "pack");

    }
}
