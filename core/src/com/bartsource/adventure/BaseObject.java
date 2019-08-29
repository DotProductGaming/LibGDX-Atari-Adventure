/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bartsource.adventure;

/**
 *
 * @author chris pruett, bart
 */
abstract class BaseObject  {

    static ObjectRegistry sSystemRegistry = new ObjectRegistry();

    BaseObject() {
        super();
    }

    /**
     * Update this object.
     * @param timeDelta  The duration since the last update (in seconds).
     * @param parent  The parent of this object (may be NULL).
     */
    public void update(float timeDelta, GameObject parent) {
        // Base class does nothing.
    }


    protected abstract void reset();

}