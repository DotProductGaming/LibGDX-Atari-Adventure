/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bartsource.adventure;

import com.bartsource.adventure.Constants.PhysicsChangeType;

/**
 *
 * @author bart
 */
class PhysicsChange {

    public final GameObject mTarget;
    public final PhysicsChangeType mType;

    public PhysicsChange(GameObject target, PhysicsChangeType type){
        mTarget = target;
        mType = type;
    }

}