/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bartsource.adventure;

/**
 *
 * @author bart
 */
public class GameStateManager extends GameObject {

    private final Adventure mAdventure;
    private Player mPlayer;
    private State mState;

    class DelayCallBack implements CallBack {
        public void callBack() {
            mComponents.clear();
            mState = State.DONE;
        }
    }

    public enum State {
        PLAY,
        WON,
        DONE
    }

    public GameStateManager(Adventure adventure) {
        super();
        mAdventure = adventure;
        reset();
    }

    public void reset() {
        mState = State.PLAY;
    }

    public void winTheGame() {
        mComponents.add(new DelayCallBackComponent(new DelayCallBack(), 4000));
        mComponents.add(new ColorChangeComponent(this, mAdventure));
        mState = State.WON;
        mAdventure.mControls.freezePlayer();
    }

    public boolean gameIsWon() {
        return mState == State.WON;
    }


}
