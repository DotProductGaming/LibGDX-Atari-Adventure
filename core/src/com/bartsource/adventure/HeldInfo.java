/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bartsource.adventure;

import com.badlogic.gdx.physics.box2d.Joint;

/**
 *
 * @author bart
 */
class HeldInfo {

    final GameObject mHeldObject;
    final Joint mJoint;

    HeldInfo(GameObject mObject, Joint joint){
        //todo - use joint userdata instead
        mHeldObject = mObject;
        mJoint = joint;
    }

}
