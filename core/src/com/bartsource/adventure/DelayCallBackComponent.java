/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bartsource.adventure;

import com.badlogic.gdx.utils.TimeUtils;

/**
 *
 * @author bart
 */
public class DelayCallBackComponent extends GameComponent {

    private final CallBack mCallBack;
    private final long mDelay;
    private final long mStartTime;

    public DelayCallBackComponent(CallBack callBack, long delay) {
        mCallBack = callBack;
        mDelay = delay;
        mStartTime = TimeUtils.millis();
    }

    @Override
    public void update(float timeDelta, GameObject parent) {
        if (TimeUtils.timeSinceMillis(mStartTime) > mDelay) {
            mCallBack.callBack();
            //nature of delay component is we only fire once and then remove ourselves
            parent.mComponents.removeValue(this, false);
        }
    }

}

