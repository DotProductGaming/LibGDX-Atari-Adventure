/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bartsource.adventure;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.badlogic.gdx.utils.Array;
import com.bartsource.adventure.Constants.PhysicsChangeType;

import static com.bartsource.adventure.Constants.TAG;

/**
 *
 * @author bart
 */
class Box2dManager {

    private final Adventure mAdventure;

	private final Array<CollisionInfo> mCollisionBuffer = new Array<CollisionInfo>();
    private final Array<PhysicsChange> mPhysicsChanges = new Array<PhysicsChange>();

    Box2dManager(Adventure adventure) {
        mAdventure = adventure;
		Player mPlayer = mAdventure.mStage.getRoot().findActor("player");
    }

    public void update() {

        applyPhysicsChanges();
        createWelds();

    }

    private void createWelds() {

        while (mCollisionBuffer.size > 0) {

            CollisionInfo collision = mCollisionBuffer.removeIndex(0);
            WeldJointDef jointDef = new WeldJointDef();
            jointDef.initialize(collision.mA.mBody, collision.mB.mBody, collision.mA.mBody.getWorldCenter());
            Joint joint = mAdventure.mWorld.createJoint(jointDef);

            if (collision.mA instanceof Player) {
                ((Player)(collision.mA)).holdItem(collision.mB, joint);
            } else if (collision.mB instanceof Player)  {
                ((Player)(collision.mB)).holdItem(collision.mA, joint);
            }
        }

    }

    void addWeld(GameObject a, GameObject b) {
        //queues createWelds
        mCollisionBuffer.add(new CollisionInfo(a, b));
    }

    void queuePhysicsChange(GameObject target, PhysicsChangeType type) {
        PhysicsChange change = new PhysicsChange(target, type);
        mPhysicsChanges.add(change);
    }

    private void applyPhysicsChanges() {

        while (mPhysicsChanges.size > 0) {

            PhysicsChange change = mPhysicsChanges.removeIndex(0);
            PhysicsChangeType type = change.mType;
            GameObject target = change.mTarget;

            switch (type) {
                case setActiveFalse:
                    if (target.mBody.isActive()) {
                        target.mBody.setActive(false);
                        Gdx.app.debug(TAG, this.getClass().getSimpleName() + " : applying physicsChange " + type + " to body: " + target.getName());
                    } else {
                        Gdx.app.debug(TAG, this.getClass().getSimpleName() + " : warning - tried to apply physicsChange " + type + " to body: " + target.getName() + " which is already inactive");
                    }
                    break;
                default:
                    Gdx.app.error(TAG, this.getClass().getSimpleName() + " : physicsChange [" + change + "]");
                    //return;
            }

        }

    }

}
