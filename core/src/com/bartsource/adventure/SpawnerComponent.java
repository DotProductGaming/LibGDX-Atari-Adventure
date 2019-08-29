/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bartsource.adventure;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;

import static com.bartsource.adventure.Constants.TAG;

/**
 *
 * @author bart
 */
class SpawnerComponent extends GameComponent {

    private final Adventure mAdventure;
	private long mStartTime;
    private int mSpawnDelay;
    private final int minDelay = 2000;
    private final int maxDelay = 7000;

    public SpawnerComponent(GameObject parent, Adventure adventure) {
		mAdventure = adventure;
        mStartTime = TimeUtils.millis();
        int initalDelay = MathUtils.random(maxDelay);
        mSpawnDelay = MathUtils.random(minDelay, maxDelay) + initalDelay;
    }

    @Override
    public void update(float timeDelta, GameObject parent) {

        if (TimeUtils.timeSinceMillis(mStartTime) > mSpawnDelay) {

            mStartTime = TimeUtils.millis();
            mSpawnDelay = MathUtils.random(minDelay, maxDelay);

            if (parent.mBody.isActive()) {
                Gdx.app.debug(TAG, this.getClass().getSimpleName() + " : spawning > " + parent.getName());
                mAdventure.mGameObjectFactory.spawnItemFromTemplate("Yellow Dragon", parent.getX() + parent.getHeight()/2, parent.getY() + parent.getWidth()/2, true);
            }

        }

    }



}
