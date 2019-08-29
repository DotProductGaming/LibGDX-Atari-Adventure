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
public abstract class GameComponent extends PhasedObject {

    void screenInit() {
        //default does nothing
        //called right before game goes live on next screen
    }

    // Defines high-level buckets within which components may choose to run.
    public enum ComponentPhases {
        THINK,                  // decisions are made
        PHYSICS,                // impulse velocities are summed
        POST_PHYSICS,           // inertia, friction, and bounce
        MOVEMENT,               // position is updated
        COLLISION_DETECTION,    // intersections are detected
        COLLISION_RESPONSE,     // intersections are resolved
        POST_COLLISION,         // position is now final for the frame
        ANIMATION,              // animations are selected
        PRE_DRAW,               // drawing state is initialized
        DRAW,                   // drawing commands are scheduled.
        FRAME_END,              // final cleanup before the next update
    }

    GameComponent() {
        super();
        boolean shared = false;
    }

}