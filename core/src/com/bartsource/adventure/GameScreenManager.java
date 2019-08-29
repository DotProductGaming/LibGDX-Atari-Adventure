/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bartsource.adventure;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;

import java.util.Iterator;

import static com.bartsource.adventure.Constants.*;

/**
 *
 * @author bart
 */
public class GameScreenManager {

    private final Adventure mAdventure;
    private final Player mPlayer;
    private final int mLevel; private int mX; private int mY;
    private float mSpawnX = 0;
    private float mSpawnY = 0;

    private Group mFromScreenGroup;     //also the "current" screenGroup, when not in transition
    private Group mToScreenGroup;

    GameScreenManager(Adventure adventure, int level, int x, int y) {

        mAdventure = adventure;
        mPlayer = mAdventure.mStage.getRoot().findActor("player");

        //level & screen as on spreadsheet
        mLevel = level;
        mX = x;
        mY = y;

        //setup scene graph group for the starting screen
        mFromScreenGroup = new Group();
        mFromScreenGroup.setName(getTmxName());
        mAdventure.mStage.getRoot().addActor(mFromScreenGroup);

        if (DEBUG_GAME_GRAPH) {
            printDebugScreenActors("mFromScreenGroup", mFromScreenGroup);
        }

    }

    void changeScreen() {

        Gdx.app.log(TAG,"GameScreenManager changeScreen");

        if (DEBUG_GAME_GRAPH) {
            printDebugScreenActors("From ScreenGroup", mFromScreenGroup);
            printDebugScreenActors("To ScreenGroup", mToScreenGroup);
        }

        //clear out bodies, mainly the tiles
        Utility.destroyBodies(mAdventure);

        //hide or remove objects in current screen except for held items
        //use an iterator so its safe to remove items
        Iterator iterator = mFromScreenGroup.getChildren().iterator();
        while(iterator.hasNext()) {

            Actor actor = (Actor) iterator.next();

            if (DEBUG_GAME_GRAPH) Gdx.app.debug(TAG, this.getClass().getSimpleName() + " DEBUG_GAME_GRAPH : processing actor: " + actor.getName());

            //remove non-static items from the game graph (arrows and the like)
            if (actor instanceof GameObject) {
                if (((GameObject)(actor)).mFlag != DATA_STATIC) {
                    if (DEBUG_GAME_GRAPH) Gdx.app.debug(TAG, this.getClass().getSimpleName() + " DEBUG_GAME_GRAPH : removeActor: " + actor.getName());
                    iterator.remove();  //same as Group.removeActor(actor);
                    continue;
                }
            }

            if (actor instanceof GameLayerObject)  {
                if (((GameLayerObject) actor).mIsHeld) {
                    if (DEBUG_GAME_GRAPH) Gdx.app.debug(TAG, this.getClass().getSimpleName() + "found held item: " + actor.getName() );
                    continue;
                }
            }

            actor.setVisible(false);

            //deactivate physics bodies
            if (actor instanceof GameObject) {
                Body body = ((GameObject)actor).mBody;
                if (body != null) {
                    body.setActive(false);
                }
            }

        }

        //unhide and init objects in next screen
        for (Actor actor : mToScreenGroup.getChildren()) {

            actor.setVisible(true);

            //activate physics bodies
            if (actor instanceof GameObject) {
                Body body = ((GameObject)actor).mBody;
                if (body != null) {
                    body.setActive(true);
                }
            }

        }

        mFromScreenGroup = mToScreenGroup;
        mToScreenGroup = null;

        mPlayer.mBody.setTransform(mSpawnX/PIXELS_TO_METERS, mSpawnY/PIXELS_TO_METERS, mPlayer.mBody.getAngle());

        //if (getTmxName().equals("1-2-9.tmx") && mPlayer.holdingItem("amulet")) {    //debug, black castle room
        if (getTmxName().equals("1-5-8.tmx") && mPlayer.holdingItem("amulet")) {    //yellow castle
            Gdx.app.log(TAG,"WIN GAME!!");
            mAdventure.mGameStateManager.winTheGame();
            mAdventure.mGameSoundManager.mWinGoodSound.play();
        }

    }

    public String getTmxName() {
        return "" + mLevel + "-" + mX + "-" + mY + ".tmx";
    }

    void addActor(GameObject actor) {
        mFromScreenGroup.addActor(actor);
    }

    void moveActor(GameObject actor) {

        Group itemGroup = actor.getParent();
        itemGroup.removeActor(actor);
        mFromScreenGroup.addActor(actor);

    }

    void screenInit() {

        //init objects
        for (Actor actor : mFromScreenGroup.getChildren()) {

            //activate physics bodies
            if (actor instanceof GameObject) {
                ((GameObject)actor).screenInit();
            }
        }

    }

    void removeActor(GameObject actor) {
        Group itemGroup = actor.getParent();
        itemGroup.removeActor(actor);
        Utility.destroyBody(mAdventure, actor.mBody, true);

    }

    public enum Direction {
        N, S, E, W
    }

    //this is triggered by the player contacting a screenTransition object
    //it sets the mChangeLevelTrigger flag which triggers changeScreen from the main loop
    public void screenTransition(GameLayerObject screenTransition) {

        Direction direction = Direction.valueOf(screenTransition.getName());

        int preX = mX;
        int preY = mY;

        //this modifies the TmxName
        switch (direction) {
            case N:
                mY++;
                mSpawnX = mPlayer.getX() + mPlayer.getWidth() / 2;
                mSpawnY = mPlayer.getHeight();
                break;
            case S:
                mY--;
                mSpawnX = mPlayer.getX() + mPlayer.getWidth() / 2;
                mSpawnY = mAdventure.mVirtualHeight - mPlayer.getHeight();
                break;
            case E:
                mX++;
                mSpawnX = mPlayer.getWidth();
                mSpawnY = mPlayer.getY() + mPlayer.getHeight() / 2;
                break;
            case W:
                mX--;
                mSpawnX = mAdventure.mVirtualWidth - mPlayer.getWidth();
                mSpawnY = mPlayer.getY() + mPlayer.getHeight() / 2;
                break;
            default:
                Gdx.app.error(TAG, this.getClass().getSimpleName() + " : Unhandled Direction Type: [" + direction + "]");
                return;
        }

        //if portal override direction calculated tmx
        if (screenTransition.mIsPortal) {
            mX = screenTransition.mPortalX;
            mY = screenTransition.mPortalY;
        }

        //check we can go there
        if (!Gdx.files.internal("data/" +getTmxName()).exists()) {
            Gdx.app.error(TAG, this.getClass().getSimpleName() + " : TMX File doesn't exist : " + getTmxName() + " Direction [" + direction + "]");
            mX = preX;
            mY = preY;
            return;
        }

        //if the properies spawnX and spawnY are specified as properies in the tiled object, then use those
        //it's sort of a hack that 0,0 can't be used
        if (screenTransition.mSpawnX != 0 || screenTransition.mSpawnY != 0) {
            Gdx.app.debug(TAG, this.getClass().getSimpleName() +
                " : screenTransition : name = " + screenTransition.getName() +
                " : spawnX = " + screenTransition.mSpawnX +
                " : spawnY = " + screenTransition.mSpawnY
                );
            mSpawnX = screenTransition.mSpawnX;
            mSpawnY = screenTransition.mSpawnY;
        }

        //create or find game graph for the next screen
        if (mAdventure.mStage.getRoot().findActor(getTmxName()) == null) {
            mToScreenGroup = new Group();
            mToScreenGroup.setName(getTmxName());
            mAdventure.mStage.getRoot().addActor(mToScreenGroup);
        } else {
            mToScreenGroup = mAdventure.mStage.getRoot().findActor(getTmxName());
        }

        mAdventure.mChangeLevelTrigger = true;
        Gdx.app.log(TAG,"changeScreen = " + direction);

    }

    private void printDebugScreenActors(String screenGroupName, Group screenGroup) {

            Gdx.app.debug(TAG, this.getClass().getSimpleName() + " DEBUG_GAME_GRAPH : " + screenGroupName + " child count: " + screenGroup.getChildren().size);

            for (Actor actor : screenGroup.getChildren()) {
                Gdx.app.debug(TAG, this.getClass().getSimpleName() + " DEBUG_GAME_GRAPH : " + screenGroupName + " child: " + actor.getName());
            }

    }

}
