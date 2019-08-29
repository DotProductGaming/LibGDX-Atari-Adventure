/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bartsource.adventure;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.bartsource.adventure.GameObjectFactory.GameObjectType;

import static com.bartsource.adventure.Constants.TAG;
import static com.bartsource.adventure.Constants.TILE_RENDER_SCALE;

/**
 *
 * @author bart
 */
public class GameLayerObject extends GameObject {

    private final Adventure mAdventure;
    final Array<Behavior> mBehaviors;

    boolean mIsHeld;         //held by player
    int mSpawnX, mSpawnY;    //for custom screen transitions
    int mTextureFrames;      //number of frames in the texture
    int mPortalX;
    int mPortalY;
    boolean mIsPortal;
    String mColor;
    Pixmap mPixmap;

    GameLayerObject(Adventure adventure) {
        //todo - gameobject registry instead of passing adventure all around
        mAdventure = adventure;
        Player mPlayer = mAdventure.mStage.getRoot().findActor("player");
        mBehaviors = new Array();
    }

    @Override
    public void reset() {
        super.reset();
        mType = null;
        mIsHeld = false;
    }

    @Override
    public void screenInit() {
        for (GameComponent component: mComponents) {
            component.screenInit();
        }
    }

    void addBehavior(Behavior behavior) {
        mBehaviors.add(behavior);
    }

    Behavior findBehavior(Class<?> type) {

        if (mBehaviors != null) {
            for(Behavior behavior : mBehaviors) {
                if (type.isInstance(behavior)) {
                    return behavior;
                }
            }
        }

        return null;

    }

    public void setType(String type) {
        mType = GameObjectType.valueOf(type);
    }

    public String getType() {
        return mType.toString();
    }

    void trigger(GameObject triggeredByObject) {

        DragonComponent dragon = null;

        Gdx.app.debug(TAG, this.getClass().getSimpleName() +
        " : trigger : name = " + this.getName() +
        " : triggered by = " + triggeredByObject.getName() +
        " : trigger switch type = " + mType
        );

        //todo: behavior.trigger?
        // ok i have behaviors & components - crazy lol

        switch(mType) {
             case screenTransition:
                mAdventure.mGameScreenManager.screenTransition(this);
                break;
             case key:
                mAdventure.mBox2dManager.addWeld(this, triggeredByObject);
                break;
             case gate:
                if (((GameLayerObject)(triggeredByObject)).mColor.equalsIgnoreCase(this.mColor)) {
                    GateComponent gate = (GateComponent) this.findComponent(GateComponent.class);
                    Gdx.app.debug(TAG, this.getClass().getSimpleName()  + " : Toggle Gate : " + gate);
                    gate.toggle();
                }
                break;
            case sword:
                mAdventure.mBox2dManager.addWeld(this, triggeredByObject);
                break;
            case dragon:
                dragon = (DragonComponent) this.findComponent(DragonComponent.class);
                if (triggeredByObject.getName().equalsIgnoreCase("player")) {
                    if (!dragon.mDead) dragon.eatPlayer();
                } if (triggeredByObject.getName().equalsIgnoreCase("yellow sword")) {
                    dragon.die();
                }
                break;
            case chalice:
                mAdventure.mBox2dManager.addWeld(this, triggeredByObject);
                break;
            case bridge:
                mAdventure.mBox2dManager.addWeld(this, triggeredByObject);
                break;
            case arrow:
                if (triggeredByObject.mType == GameObjectType.dragon) {
                    dragon = (DragonComponent) triggeredByObject.findComponent(DragonComponent.class);
                    dragon.die();
                }
                mAdventure.mBox2dManager.queuePhysicsChange(this, Constants.PhysicsChangeType.setActiveFalse);
                break;
            default:
                Gdx.app.error(TAG, this.getClass().getSimpleName() + " : Unhandled Tiled Object Layer Type : " + mType);
                return;
        }

    }

    void setDebugTexture(Texture texture, Rectangle rectangle) {
        mTexture = new TextureRegion(texture);
        this.setSize(rectangle.getWidth() * TILE_RENDER_SCALE, rectangle.getHeight() * TILE_RENDER_SCALE);
    }

    void setTextureFrames(int textureFrames, int initialFrameNumber) {

        Gdx.app.debug(TAG, this.getClass().getSimpleName() + " : setTextureFrames : " + textureFrames);
        mTextureFrames = textureFrames;
        setTextureFrame(initialFrameNumber);

    }

    void setTextureFrame(int frameNumber) {

        //setTextureFrame 0...n

        if (frameNumber >= mTextureFrames) {
            Gdx.app.error(TAG, this.getClass().getSimpleName() + " : setTextureFrame : trying to set frame greater than frames defined : frame = " + frameNumber + " Object Name = " + this.getName());
            frameNumber = 0;
        }

        //assume textures are all the same height, laid out side by side horizontally
        int frameWidth = mTexture.getTexture().getWidth() / mTextureFrames;
        int frameHeight = mTexture.getTexture().getHeight();

        int frameStartX = frameWidth * frameNumber;

        mTexture.setRegion(frameStartX,0,frameWidth,frameHeight);
        this.setSize(frameWidth,frameHeight);

    }

    @Override
    public void act(float delta){

        for (Behavior behavior: mBehaviors) {
            behavior.act(delta, this);
        }

        super.act(delta);

    }

    void setPixmap(Pixmap pixmap) {
        mPixmap = pixmap;
    }

}
