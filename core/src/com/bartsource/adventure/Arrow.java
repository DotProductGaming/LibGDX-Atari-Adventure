/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bartsource.adventure;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 *
 * @author bart
 */
class Arrow extends GameLayerObject implements Poolable {

    Arrow(Adventure adventure) {
        super(adventure);
    }

    void shoot(double angle, float pushForce, float velocity) {

        if (pushForce == 0) {

            //use velocity
            Vector2 force = new Vector2(velocity, 0f);
            force.setAngle((float) angle);
            this.mBody.setLinearVelocity(force.x, force.y);

        } else {

            //use force to center
            Vector2 force = new Vector2(pushForce ,0f);
            force.setAngle((float) angle);
            this.mBody.applyForceToCenter(force, true);

        }
    }

}
