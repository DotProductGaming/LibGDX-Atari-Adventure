/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bartsource.adventure;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;
import com.bartsource.adventure.GameObjectFactory.GameObjectType;

import static com.bartsource.adventure.Constants.DEBUG_COLLISONS;
import static com.bartsource.adventure.Constants.TAG;

/**
 *
 * @author bart
 */
class GameContactListener implements ContactListener {

	public GameContactListener(Adventure adventure) {
	}

    @Override
    public void beginContact(Contact contact) {

        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        GameObject a = (GameObject) fixtureA.getBody().getUserData();
        GameObject b = (GameObject) fixtureB.getBody().getUserData();

        if (a == null || b == null) return;

        if (DEBUG_COLLISONS) Gdx.app.debug(TAG, "beginContact between " + fixtureA.getBody().getUserData().getClass().getName() + " and " + fixtureB.getBody().getUserData().getClass().getName());
        if (DEBUG_COLLISONS) Gdx.app.debug(TAG, "beginContact between type " + a.mType + " and " + b.mType);

        /*
            collision types handled:

                player & screenTransition
                player & key
                player & sword
                player & dragon
                player & amulet
                player & bridge
                sword & dragon
                key & gate
                arrow & anything

        */

        //todo - combine player & item check?  (always trigger item)

        if (checkCollision(a, b, GameObjectType.player, GameObjectType.screenTransition)) {
            //trigger screenTransition
            if (a.mType == GameObjectType.screenTransition) ((GameLayerObject)(a)).trigger(b);
            else ((GameLayerObject)(b)).trigger(a);
        } else if (checkCollision(a, b, GameObjectType.player, GameObjectType.key)) {
            //trigger key
            if (a.mType == GameObjectType.key) ((GameLayerObject)(a)).trigger(b);
            else ((GameLayerObject)(b)).trigger(a);
        } else if (checkCollision(a, b, GameObjectType.player, GameObjectType.sword)) {
            //trigger sword
            if (a.mType == GameObjectType.sword) ((GameLayerObject)(a)).trigger(b);
            else ((GameLayerObject)(b)).trigger(a);
        } else if (checkCollision(a, b, GameObjectType.player, GameObjectType.dragon)) {
            //trigger dragon
            if (a.mType == GameObjectType.dragon) ((GameLayerObject)(a)).trigger(b);
            else ((GameLayerObject)(b)).trigger(a);
        } else if (checkCollision(a, b, GameObjectType.player, GameObjectType.chalice)) {
            //trigger amulet
            if (a.mType == GameObjectType.chalice) ((GameLayerObject)(a)).trigger(b);
            else ((GameLayerObject)(b)).trigger(a);
        } else if (checkCollision(a, b, GameObjectType.player, GameObjectType.bridge)) {
            //trigger amulet
            if (a.mType == GameObjectType.bridge) ((GameLayerObject)(a)).trigger(b);
            else ((GameLayerObject)(b)).trigger(a);
        } else if (checkCollision(a, b, GameObjectType.sword, GameObjectType.dragon)) {
            //trigger dragon
            if (a.mType == GameObjectType.dragon) ((GameLayerObject)(a)).trigger(b);
            else ((GameLayerObject)(b)).trigger(a);
        } else if (checkCollision(a, b, GameObjectType.key, GameObjectType.gate)) {
            //trigger gate
            if (a.mType == GameObjectType.gate) ((GameLayerObject)(a)).trigger(b);
            else ((GameLayerObject)(b)).trigger(a);
        } else if (checkCollision(a, b, GameObjectType.arrow)) {
            //trigger arrow
            if (a.mType == GameObjectType.arrow) ((GameLayerObject)(a)).trigger(b);
            else ((GameLayerObject)(b)).trigger(a);
        }

    }

    //check for type combination
    private boolean checkCollision(GameObject a, GameObject b, GameObjectType typeA, GameObjectType typeB) {
        return (a.mType == typeA && b.mType == typeB) || (b.mType == typeA && a.mType == typeB);
    }

    //check for a single type
    private boolean checkCollision(GameObject a, GameObject b, GameObjectType typeA) {
        return (a.mType == typeA || b.mType == typeA);
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        if (DEBUG_COLLISONS) Gdx.app.log(TAG, "endContact between " + fixtureA.toString() + " and " + fixtureB.toString());
    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        if (DEBUG_COLLISONS) Gdx.app.log(TAG, "preSolve between " + fixtureA.toString() + " and " + fixtureB.toString());
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        if (DEBUG_COLLISONS) Gdx.app.log(TAG, "postSolve between " + fixtureA.toString() + " and " + fixtureB.toString());
    }

}
