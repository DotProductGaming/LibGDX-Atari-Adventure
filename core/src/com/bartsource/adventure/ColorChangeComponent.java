/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bartsource.adventure;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.TimeUtils;

import static com.bartsource.adventure.Constants.atariPalette;

/**
 *
 * @author bart
 */
public class ColorChangeComponent extends GameComponent {

    private int mColorIndex;
    private final int mMaxColorIndex;
    private long mStartTime;

    ColorChangeComponent(GameObject parent, Adventure adventure) {
        mColorIndex = 0;
        mMaxColorIndex = atariPalette.length;
        mStartTime = TimeUtils.millis();
        parent.mBatchColor = colorFromHex(atariPalette[mColorIndex]);
    }

    @Override
    public void update(float timeDelta, GameObject parent) {

        //original atari chalice glow pulses around 150 times a minute - that's 400ms for each pulse through 8 bits of color
        if (TimeUtils.timeSinceMillis(mStartTime) > 400/8) {
            mStartTime = TimeUtils.millis();
            parent.mBatchColor = colorFromHex(atariPalette[mColorIndex]);

            //this is how you change texture color the "hard way" by manipulating texture data on the CPU - I switched to more efficient mBatchColor method, but learned about pixmaps here
            //parent.setTexture(pixMapTexture(parent, atariPalette[mColorIndex]));

            mColorIndex++;
            if (mColorIndex >= mMaxColorIndex) mColorIndex = 0;
        }

    }

    private Texture pixMapTexture(GameObject object, long hexColor) {

        //pixmap of the texture must be captured and stored when the object is created
        Pixmap pixmap = ((GameLayerObject)(object)).mPixmap;
        Color color = colorFromHex(hexColor);

        //replace non-transparent parts with new color pixel by pixel
        for (int y = 0; y < pixmap.getHeight(); y++) {
            for (int x = 0; x < pixmap.getWidth(); x++) {
                //if alpha channel != 0
                if ((pixmap.getPixel(x, y) & 0x000000ff) != 0) {
                    pixmap.setColor(color);
                    pixmap.fillRectangle(x, y, 1, 1);
                }
            }
        }

        return new Texture(pixmap);

    }

    private Texture pixMapTextureTest(long hexColor) {

        Pixmap pixmap = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
        Color color = colorFromHex(hexColor);
        pixmap.setColor(color);
        pixmap.fillRectangle(0, 0, 16, 16);
        return new Texture(pixmap);

    }

    private Color colorFromHex(long hex) {

        float a = (hex & 0xFF000000L) >> 24;
        float r = (hex & 0xFF0000L) >> 16;
        float g = (hex & 0xFF00L) >> 8;
        float b = (hex & 0xFFL);
        return new Color(r/255f, g/255f, b/255f, a/255f);

    }

}

