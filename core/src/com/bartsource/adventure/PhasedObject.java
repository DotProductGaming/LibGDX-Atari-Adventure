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
public class PhasedObject extends BaseObject {

    // so that the function overhead of an getter is non-trivial.

    PhasedObject() {
        super();
    }

    @Override
    public void reset() {

    }

    public void setPhase(int phaseValue) {
        // This is public because the phased is accessed extremely often, so much
    }
}