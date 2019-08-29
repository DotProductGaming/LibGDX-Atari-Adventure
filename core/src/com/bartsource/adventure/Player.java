/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bartsource.adventure;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.Array;
import com.bartsource.adventure.GameObjectFactory.GameObjectType;

import static com.bartsource.adventure.Constants.*;
import static com.bartsource.adventure.Utility.touchPointToBox2DCoords;

/**
 *
 * @author bart
 */
class Player extends GameObject {

    private final Adventure mAdventure;
    private final Array<HeldInfo> mHeldItems;

    Player(Adventure adventure) {
        super();
        mAdventure = adventure;
        mHeldItems = new Array();

        //this works, but offset to how I draw (up an to the right) todo: rethink how i'm offsetting the draw & box2d bodies, might want to go with default of bottom left-hand corner
        this.addListener(new InputListener() {

            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                //Gdx.app.log(TAG, "PLAYER touchdown");
                //return true;
                return false;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                //Gdx.app.log(TAG, "PLAYER touchUp");
            }
        });

    }

    void dropItems() {

        while (mHeldItems.size > 0) {

            mAdventure.mGameSoundManager.mDropSound.play();
            HeldInfo held = mHeldItems.removeIndex(0);

            if (held.mHeldObject.mType == GameObjectType.bridge) {
                BridgeComponent bridge = (BridgeComponent) held.mHeldObject.findComponent(BridgeComponent.class);

                //todo: change to see if player is clear of tiles
                /*if (!bridge.playerClearOfBridgeCrossing(this)) {
                    continue;
                }*/

                bridge.clearBridge();
                //when we drop the bridge, don't push it away
                physicallyDrop(held, 0f);
            } else {
                physicallyDrop(held, .001f);
            }

            Gdx.app.debug(TAG, this.getClass().getSimpleName() + " : drop items");

            if (held.mHeldObject instanceof GameLayerObject) ((GameLayerObject)held.mHeldObject).mIsHeld = false;
            else Gdx.app.error(TAG, this.getClass().getSimpleName() + " attempting to drop object that is not a GameLayerObject");

            //move item to the screen where it was dropped
            mAdventure.mGameScreenManager.moveActor(held.mHeldObject);

        }

    }

    private void physicallyDrop(HeldInfo held, float pushForce) {

        //get angle between objects
        Vector2 line = held.mJoint.getBodyA().getPosition().sub(held.mJoint.getBodyB().getPosition());
        double angle = Math.toDegrees(Math.atan2(line.y, line.x));

        mAdventure.mWorld.destroyJoint(held.mJoint);

        //push item away
        Vector2 force = new Vector2(pushForce ,0f);
        Gdx.app.debug(TAG, this.getClass().getSimpleName() + " : drop items angle = " + angle);
        force.setAngle((float) angle);
        held.mHeldObject.mBody.applyForceToCenter(force, true);

    }

    void holdItem(GameObject gameObject, Joint joint) {

        dropItems();

        /*
        //cannot hold a bridge if player is on the bridge crossing
        if (gameObject.mType == GameObjectType.bridge) {
            BridgeComponent bridge = (BridgeComponent) gameObject.findComponent(BridgeComponent.class);
            if (!bridge.playerClearOfBridgeCrossing(this)) {
                return;
            }
        }
        */

        mAdventure.mGameSoundManager.mPickupSound.play();
        mHeldItems.add(new HeldInfo(gameObject, joint));
        if (gameObject instanceof GameLayerObject) ((GameLayerObject)gameObject).mIsHeld = true;
        else Gdx.app.error(TAG, this.getClass().getSimpleName() + " attempting to hold object that is not a GameLayerObject");
    }

    boolean holdingItem(String itemName) {

        boolean isHoldingItem = false;

        for (HeldInfo held : mHeldItems) {

            if (held.mHeldObject.getName().equalsIgnoreCase(itemName)) {
                isHoldingItem = true;
                break;
            }

        }

        return isHoldingItem;

    }

    public Rectangle getRectangle() {
        return new Rectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    //hard to touch on mobile, so enflate the touch area * 3
    Rectangle getTouchRectangle() {
        return new Rectangle(this.getX() - this.getWidth(), this.getY()-this.getHeight(), this.getWidth()*3, this.getHeight()*3);
    }

    void shootArrow(float touchTargetX, float touchTargetY) {
        //note : touchTarget's are in World Coordinates (mCamera.unproject)

        //get angle between player and touch point
        Vector2 target = new Vector2(touchTargetX, touchTargetY);
        target = touchPointToBox2DCoords(target);

        Vector2 line = target.sub(this.mBody.getPosition());
        float angle = (float)Math.toDegrees(Math.atan2(line.y, line.x));

        //position item
        Vector2 offsetVector = new Vector2(ARROW_WIDTH ,0.0f);
        offsetVector.setAngle(angle);

        GameObject object = mAdventure.mGameObjectFactory.spawn(GameObjectType.arrow, this.getX() + this.getHeight()/2 + offsetVector.x, this.getY() + this.getWidth()/2 + offsetVector.y, null, false, false);

        if (object != null) {

            object.mBody.setTransform(object.mBody.getPosition(), (float) Math.toRadians(angle));
            //object.mBody.applyTorque(0.01f,true);
            ((Arrow)object).shoot(angle, 0, ARROW_VELOCITY);
            mAdventure.mGameScreenManager.addActor(object);

        } else {
            Gdx.app.error(TAG, this.getClass().getSimpleName() + " : shootArrow spawned null arrow");
        }

    }

}


