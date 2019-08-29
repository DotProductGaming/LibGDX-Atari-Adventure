/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bartsource.adventure;

/**
 *
 * @author bart
 */
class CollisionInfo {

    final GameObject mA;
    final GameObject mB;

    CollisionInfo(GameObject a, GameObject b){
        mA = a;
        mB = b;
    }

}