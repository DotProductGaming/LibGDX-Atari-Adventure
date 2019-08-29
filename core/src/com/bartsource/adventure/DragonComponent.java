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
public class DragonComponent extends GameComponent {

    private final Adventure mAdventure;
    private final GameObject mParent;
    public boolean mDead;

    class RemoveDragonBody implements CallBack {
        public void callBack() {
            mAdventure.mGameScreenManager.removeActor(mParent);
        }
    }

    class StopEating implements CallBack {
        public void callBack() {
            ((GameLayerObject)(mParent)).setTextureFrame(0);
        }
    }

    public DragonComponent(GameObject parent, Adventure adventure) {
        mParent = parent;
        mAdventure = adventure;
    }

    @Override
    public void update(float timeDelta, GameObject parent) {
    }

    void die() {

        if (mDead) return;
        mDead = true;
        mAdventure.mGameSoundManager.mSlaySound.play();
        GameLayerObject parent = ((GameLayerObject)(mParent));
        FollowBehavior followBehavior = (FollowBehavior) parent.findBehavior(FollowBehavior.class);
        parent.mBehaviors.removeValue(followBehavior, true);
        parent.setTextureFrame(2);
        parent.mComponents.add(new DelayCallBackComponent(new DragonComponent.RemoveDragonBody(), 1000));

    }

    void eatPlayer() {
        mAdventure.mGameSoundManager.mChompSound.play();
        ((GameLayerObject)(mParent)).setTextureFrame(1);
        GameLayerObject parent = ((GameLayerObject)(mParent));
        parent.mComponents.add(new DelayCallBackComponent(new DragonComponent.StopEating(), 1000));
    }

}

