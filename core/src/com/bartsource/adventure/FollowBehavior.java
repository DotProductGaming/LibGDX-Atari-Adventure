/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bartsource.adventure;

import com.badlogic.gdx.math.Vector2;

/**
 *
 * @author bart
 */
public class FollowBehavior extends Behavior {

    private final Player mPlayer;

    public FollowBehavior(Adventure adventure) {
        mPlayer = adventure.mStage.getRoot().findActor("player");
    }

    @Override
    public void act(float timeDelta, GameObject parent) {

        float chaseSpeed = 1.5f;

        //get angle between objects
        Vector2 line = mPlayer.mBody.getPosition().sub(parent.mBody.getPosition());
        double angle = Math.toDegrees(Math.atan2(line.y, line.x));

        //push item away
        Vector2 force = new Vector2(chaseSpeed, 0f);
        //Gdx.app.log(TAG, "angle = " + angle);
        force.setAngle((float) angle);

        parent.mBody.setLinearVelocity(force.x, force.y);
        //mBody.applyForceToCenter(force, true);

    }

}
