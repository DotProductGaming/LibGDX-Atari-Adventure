/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bartsource.adventure;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;

import static com.bartsource.adventure.Constants.DATA_STATIC;
import static com.bartsource.adventure.Constants.PIXELS_TO_METERS;

/**
 *
 * @author bart
 */
final class Utility {

    static void destroyBodies(Adventure mAdventure) {

        Array<Body> bodies = new Array();
        mAdventure.mWorld.getBodies(bodies);

        for (Body body : bodies) {
            destroyBody(mAdventure, body, false);
        }
    }

    static void destroyBody(Adventure mAdventure, Body body, boolean forceDestroy) {

        if (body != null) {

            Object data = body.getUserData();

            if (data instanceof GameObject) {
                if (((GameObject)(data)).mFlag == DATA_STATIC && !forceDestroy) return;
            }

            mAdventure.mWorld.destroyBody(body);
            body.setUserData(null);                 //todo, use Gameobject pool and free instead of null
            body = null;

        }

    }

    static Vector2 touchPointToBox2DCoords(Vector2 screenCoords) {
        //note : screenCoords are in World Coordinates (mCamera.unproject)
        return new Vector2(screenCoords.x / PIXELS_TO_METERS, screenCoords.y / PIXELS_TO_METERS);
    }

}
