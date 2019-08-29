/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bartsource.adventure;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

import static com.bartsource.adventure.Constants.DEBUG_LIBGDX_SHAPES;
import static com.bartsource.adventure.Constants.PIXELS_TO_METERS;

/**
 *
 * @author bart
 */
public class GameObject extends Actor {

    private State mCurrentState;
    TextureRegion mTexture;
    public Body mBody;
    final Array<GameComponent> mComponents;
    short mFlag;
    boolean mActionDriven; //translation of sprite & body is dictated by stage actions
    //public String mName;          //defined in tiled (N,S,E,W)
    GameObjectFactory.GameObjectType mType; //defined in tiled (screenTransition)
    Color mBatchColor;

    public void screenInit() {
        //default does nothing
        //called right before game goes live on next screen
        //also calls screenInit for child components
    }

    public enum State {
        RESET,
        SPAWN,
        INVALID
    }

    public GameObject() {
        super();
        mComponents = new Array();
        reset();
        this.setDebug(DEBUG_LIBGDX_SHAPES);
    }

    void reset() {
        mCurrentState = State.RESET;
    }

    public void spawn() {
        mCurrentState = State.SPAWN;
        //todo clear components
    }

    GameComponent findComponent(Class<?> type) {

        if (mComponents != null) {
            for(Object component : mComponents) {
                if (type.isInstance(component)) {
                    return (GameComponent) component;
                }
            }
        }

        return null;

    }

    void setTexture(Texture texture) {
        mTexture = new TextureRegion(texture);
        //by default set w,h to texture size, otherwise won't draw
        this.setSize(texture.getWidth(), texture.getHeight());
        this.setOrigin(Align.center);
    }

    @Override
    public void draw(Batch batch, float alpha){

        //mBatchColor is kindof a hack, but allows object to manipulate color at the GPU layer
        if (mBatchColor != null) batch.setColor(mBatchColor);

        //draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation)
        batch.draw(mTexture,this.getX(),getY(),this.getOriginX(),this.getOriginY(),this.getWidth(),
                this.getHeight(),this.getScaleX(), this.getScaleY(),this.getRotation());

        if (mBatchColor != null) batch.setColor(Color.WHITE);

    }

    @Override
    public void act(float delta){

        if (mComponents != null) {
            for(GameComponent component : mComponents) {
                component.update(delta, this);
            }
        }

        if (mActionDriven) {

            //actions dictate translation
            if (mBody != null) {
                this.mBody.setTransform(
                        (this.getX() + this.getWidth()/2) / PIXELS_TO_METERS,
                        (this.getY() + this.getHeight()/2) / PIXELS_TO_METERS,
                        this.mBody.getAngle());
            }

        } else {

            //physics dictate translation
            if (mBody != null) { // && mBody.getType() != BodyType.StaticBody ) {

                this.setPosition(
                    (mBody.getPosition().x * PIXELS_TO_METERS) - this.getWidth()/2 ,
                    (mBody.getPosition().y * PIXELS_TO_METERS) - this.getHeight()/2);

                this.setRotation((float)Math.toDegrees(mBody.getAngle()));

            }
        }

        //actions
        for(Iterator<Action> iter = this.getActions().iterator(); iter.hasNext();){
            iter.next().act(delta);
        }

    }

}
