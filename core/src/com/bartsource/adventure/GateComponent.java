/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bartsource.adventure;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.bartsource.adventure.Constants.PhysicsChangeType;

import static com.bartsource.adventure.Constants.TAG;

/**
 *
 * @author bart
 */
public class GateComponent extends GameComponent {

    private final Adventure mAdventure;
    private final GameObject mParent;
    private Vector2 mParentVelocity;
    private boolean mOpen;
    private boolean mAnimating;

    private final Action completeAction = new Action() {
        public boolean act( float delta ) {
            mAnimating = false;
            Gdx.app.debug(TAG, this.getClass().getSimpleName() + " : completeAction" );
            return true;
        }
    };

    public GateComponent(GameObject parent, Adventure adventure) {
        mParent = parent;
        mAdventure = adventure;
    }

    @Override
    public void update(float timeDelta, GameObject parent) {

    }

    void toggle() {
        if (!mAnimating) {
            if (mOpen) closeAnimation();
            else openAnimation();
            mOpen = !mOpen;
        }
    }

    private void openAnimation() {

        mAnimating = true;

        //temporarily deactivate collisions so you can get through the gate holding the key
        mAdventure.mBox2dManager.queuePhysicsChange(mParent, PhysicsChangeType.setActiveFalse);

        SequenceAction sequenceAction = new SequenceAction();
        MoveToAction moveAction = new MoveToAction();
        moveAction.setPosition(mParent.getX(), mParent.getY() + mParent.getHeight() - 32);
        moveAction.setDuration(1f);

        sequenceAction.addAction(moveAction);
        sequenceAction.addAction(completeAction);

        mParent.addAction(sequenceAction);

    }

    private void closeAnimation() {

        mAnimating = true;

        SequenceAction sequenceAction = new SequenceAction();
        MoveToAction moveAction = new MoveToAction();
        moveAction.setPosition(mParent.getX(), mParent.getY() - mParent.getHeight() + 32);
        moveAction.setDuration(1f);

        sequenceAction.addAction(moveAction);
        sequenceAction.addAction(completeAction);

        mParent.addAction(sequenceAction);
    }

}

