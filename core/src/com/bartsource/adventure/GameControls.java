/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bartsource.adventure;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import static com.bartsource.adventure.Constants.PIXELS_TO_METERS;
import static com.bartsource.adventure.Constants.TAG;

/**
 *
 * @author bart
 */
class GameControls implements InputProcessor, GestureListener {

    private final Adventure mAdventure;
    private final Player mPlayer;
    private Vector2 mPlayerVelocity;
    private float mKeyboardPlayerSpeed = 2.0f;
    private float mTouchPlayerSpeed = 1.0f;

    private float mLastDragX;
    private float mLastDragY;

    GameControls(Adventure adventure) {
        mAdventure = adventure;
        mPlayer = mAdventure.mStage.getRoot().findActor("player");
        reset();
    }

    private void reset() {
        mKeyboardPlayerSpeed = 2.0f;
        mTouchPlayerSpeed = 1.0f;
    }

    public void freezePlayer() {
        mKeyboardPlayerSpeed = 0.0f;
        mTouchPlayerSpeed = 0.0f;
    }

    @Override
    public boolean keyDown(int keycode) {

        if(keycode == Keys.ESCAPE || keycode == Keys.ENTER)
            Gdx.app.exit();

        if(keycode == Keys.SPACE)
            mPlayer.dropItems();

        //if(keycode == Keys.BACKSPACE)
        //    mPlayer.shootArrow();

        if(keycode == Keys.TAB)
             mAdventure.mDebugPhysics = !mAdventure.mDebugPhysics;

        if(keycode == Keys.P)
            Gdx.app.log(TAG, this.getClass().getSimpleName() +
                    " : Player x,y = " + mPlayer.getX() +
                    " , " + mPlayer.getY()
                    );

        mPlayerVelocity = mPlayer.mBody.getLinearVelocity();

        if(keycode == Keys.UP || keycode == Keys.DPAD_UP)
            mPlayer.mBody.setLinearVelocity(mPlayerVelocity.x,mKeyboardPlayerSpeed);

        if(keycode == Keys.DOWN || keycode == Keys.DPAD_DOWN)
            mPlayer.mBody.setLinearVelocity(mPlayerVelocity.x, -mKeyboardPlayerSpeed);

        if(keycode == Keys.LEFT || keycode == Keys.DPAD_LEFT)
            mPlayer.mBody.setLinearVelocity(-mKeyboardPlayerSpeed,mPlayerVelocity.y);

        if(keycode == Keys.RIGHT || keycode == Keys.DPAD_RIGHT)
            mPlayer.mBody.setLinearVelocity(mKeyboardPlayerSpeed, mPlayerVelocity.y);

        return true;

    }

    @Override
    public boolean keyUp(int keycode) {

        mPlayerVelocity = mPlayer.mBody.getLinearVelocity();

        if(keycode == Keys.LEFT || keycode == Keys.DPAD_LEFT)
            if (mPlayerVelocity.x < 0) mPlayer.mBody.setLinearVelocity(0f, mPlayerVelocity.y);

        if(keycode == Keys.RIGHT || keycode == Keys.DPAD_RIGHT)
            if (mPlayerVelocity.x > 0) mPlayer.mBody.setLinearVelocity(0f, mPlayerVelocity.y);

        if(keycode == Keys.UP || keycode == Keys.DPAD_UP)
            if (mPlayerVelocity.y > 0) mPlayer.mBody.setLinearVelocity(mPlayerVelocity.x, 0f);

        if(keycode == Keys.DOWN || keycode == Keys.DPAD_DOWN)
            if (mPlayerVelocity.y < 0) mPlayer.mBody.setLinearVelocity(mPlayerVelocity.x, 0f);

        return true;

    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    private boolean mJoyActive = false;
    private int mJoyCenterX;
    private int mJoyCenterY;

    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {

        Gdx.app.debug(TAG, this.getClass().getSimpleName() + " : pointer = " + pointer + " : touchDown");
        Vector3 worldCoordinates = new Vector3(x, y, 0);
        mAdventure.mCamera.unproject(worldCoordinates);

        //pointer 0 is the first finger down, pointer 1 is the second
        //simulate second finger touch with right-click
        if (pointer == 1 || button == 1) {

            mPlayer.shootArrow(worldCoordinates.x, worldCoordinates.y);
            return true;

        } else if (pointer == 0) {

            mJoyActive = true;
            Gdx.app.debug(TAG, this.getClass().getSimpleName() + " : mJoyActive true");
            mJoyCenterX = x;
            mJoyCenterY = y;

            if (mPlayer.getTouchRectangle().contains(worldCoordinates.x, worldCoordinates.y)) {
                Gdx.app.debug(TAG, this.getClass().getSimpleName() + " : tapped player");
                mPlayer.dropItems();
            }

            return true;

        } else {

            Gdx.app.debug(TAG, this.getClass().getSimpleName() + " : pointer = " + pointer + " : touchDown, warning event unhandled");

        }

        return false;
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {

        Gdx.app.debug(TAG, this.getClass().getSimpleName() + " : pointer = " + pointer + " : touchUp");

        if (pointer == 0 && button != 1) {
            mJoyActive = false;
            Gdx.app.debug(TAG, this.getClass().getSimpleName() + " : mJoyActive false");
            mPlayer.mBody.setLinearVelocity(0f,0f);
            return true;
        }

        return false;

    }

    @Override
    public boolean touchDragged(int x, int y, int pointer) {

        if (mJoyActive && pointer == 0) {
            int joyMoveX = (int) ((x - mJoyCenterX) * mTouchPlayerSpeed);
            int joyMoveY = (int) ((mJoyCenterY - y) * mTouchPlayerSpeed);
            mPlayer.mBody.setLinearVelocity(joyMoveX/PIXELS_TO_METERS, joyMoveY/PIXELS_TO_METERS);
            return true;
        }

        return false;

    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    @Override
    public boolean scrolled(int i) {
        return false;
    }

    //GESTURES

    @Override
    public boolean touchDown(float f, float f1, int i, int i1) {
        //same as InputProcessor touchDown
        //Gdx.app.log(TAG,"gesture : touchDown");
        return false;
    }

    @Override
    public boolean tap(float f, float f1, int i, int i1) {
        //same as InputProcessor touchDown
        //Gdx.app.log(TAG,"gesture : tap");
        return false;
    }

    @Override
    public boolean longPress(float f, float f1) {
        Gdx.app.log(TAG,"gesture : longPress");  return false;
    }

    @Override
    public boolean fling(float f, float f1, int i) {
        Gdx.app.log(TAG,"gesture : fling");  return false;
    }

    @Override
    public boolean pan(float f, float f1, float f2, float f3) {
        //Gdx.app.log(TAG,"gesture : pan");
        return false;
    }

    @Override
    public boolean panStop(float f, float f1, int i, int i1) {
        Gdx.app.log(TAG,"gesture : panStop");  return false;
    }

    @Override
    public boolean zoom(float f, float f1) {
        Gdx.app.log(TAG,"gesture : zoom");  return false;
    }

    @Override
    public boolean pinch(Vector2 vctr, Vector2 vctr1, Vector2 vctr2, Vector2 vctr3) {
        Gdx.app.log(TAG,"gesture : pinch");  return false;
    }

    @Override
    public void pinchStop() {
        //Automatically Generated Stub
    }

}
