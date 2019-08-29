/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bartsource.adventure;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;

import static com.bartsource.adventure.Constants.PIXELS_TO_METERS;

/**
 *
 * @author bart
 */
public class BridgeComponent extends GameComponent {

    private final Adventure mAdventure;
    private final GameObject mParent;

    BridgeComponent(GameObject parent, Adventure adventure) {
        mParent = parent;
        mAdventure = adventure;
        reset();
    }

    public void reset() {
        resetBody();
    }

    @Override
    void screenInit() {
        clearBridge();
    }

    void clearBridge() {

        //center "crossing" part of the bridge
        Rectangle bridgeCrossOver = getBridgeCrossOver();

        Array<Body> bodies = new Array();
        mAdventure.mWorld.getBodies(bodies);

        for (Body body : bodies) {
            if (bridgeCrossOver.contains(body.getPosition())) {
                if (body.getUserData() != null && body.getUserData() instanceof Player) continue;
                body.getFixtureList().first().setSensor(true);
                Utility.destroyBody(mAdventure, body, false);
            }
        }

    }

    boolean playerClearOfBridgeCrossing(Player player) {

        //center "crossing" part of the bridge
        Rectangle bridgeCrossOver = getBridgeCrossOver();

        Body body = player.mBody;

		return bridgeCrossOver.contains(body.getPosition());

	}



    private Rectangle getBridgeCrossOver() {

        return new Rectangle(
            (mParent.getX() + (mParent.getWidth() / 4)) / PIXELS_TO_METERS,
            (mParent.getY()) / PIXELS_TO_METERS,
            mParent.getWidth()/2 / PIXELS_TO_METERS,
            mParent.getHeight() / PIXELS_TO_METERS);

    }

    private void resetBody() {

        //delete default body and create custom for the bridge
        Utility.destroyBody(mAdventure, mParent.mBody, true);

        //body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.fixedRotation = true;
        bodyDef.position.set(
            (mParent.getX() + mParent.getWidth()/2) / PIXELS_TO_METERS,
            (mParent.getY() + mParent.getHeight()/2) / PIXELS_TO_METERS);
        Body body = mAdventure.mWorld.createBody(bodyDef);

        PolygonShape shape;
        FixtureDef fixtureDef;

        //bridge body is sub-divided into 4 verticle blocks.  The inner two blocks (the bridge) are left open (not created)
        int subdivisions = 4;
        int divisionWidth = (int) (mParent.getWidth() / subdivisions);

        //the starting center (left side) is minus half the parent plus half the subdivision
        Vector2 center = new Vector2( (-mParent.getWidth()/2f+divisionWidth/2f) / PIXELS_TO_METERS,0);

        for (int i=0; i<subdivisions; i++) {

            //only create the two outer blocks
            if (i == 0 || i == subdivisions-1) {

                //shape
                shape = new PolygonShape();
                shape.setAsBox(mParent.getWidth()/8 / PIXELS_TO_METERS, mParent.getHeight()/2 / PIXELS_TO_METERS, center, 0);

                //fixture
                fixtureDef = new FixtureDef();
                fixtureDef.shape = shape;
                fixtureDef.density = 0.00005f;
                fixtureDef.friction = 0.0f;
                fixtureDef.restitution = 0.0f;
                fixtureDef.isSensor = true;

                body.createFixture(fixtureDef);
                shape.dispose();
            }

            //increment to the right one subdivision
            center.x+=divisionWidth/PIXELS_TO_METERS;

        }

        body.setLinearDamping(4f);          //drag

        //userdata & object
        body.setUserData(mParent);
        mParent.mBody = body;

    }

    @Override
    public void update(float timeDelta, GameObject parent) {
    }

}

