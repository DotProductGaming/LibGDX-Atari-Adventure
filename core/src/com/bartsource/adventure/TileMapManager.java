/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bartsource.adventure;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.bartsource.adventure.GameObjectFactory.GameObjectType;

import static com.bartsource.adventure.Constants.*;

/**
 *
 * @author bart
 */
class TileMapManager {

    private Adventure mAdventure;
    private TiledMap mTiledMap;
    TiledMapTileLayer mPrimaryTileLayer;
    private MapLayer mCollisionObjectLayer;

    TileMapManager(Adventure adventure) {
        mAdventure = adventure;
    }

    TiledMap loadTileLayer(String tmxName) {

        Gdx.graphics.setTitle(tmxName);
        if (mTiledMap != null) mTiledMap.dispose();
        mTiledMap = new TmxMapLoader().load("data/" + tmxName);
        //Gdx.app.log(TAG,"Tiled Map Layer Count = " + mTiledMap.getLayers().getCount() );

        //mPrimaryTileLayer is the tiles, walls etc
        mPrimaryTileLayer = (TiledMapTileLayer)mTiledMap.getLayers().get(0);
        createTileBodies(mPrimaryTileLayer);

        //layer 1 is a Tile Non-CollisionLayer
        //(TiledMapTileLayer)mTiledMap.getLayers().get(1);

        //mCollisionObjectLayer contains the zones that generate events like switching rooms, etc
        mCollisionObjectLayer = mTiledMap.getLayers().get(2);
        parseTiledObjectLayer();

        return mTiledMap;
    }

    private void createTileBodies (TiledMapTileLayer layer) {

        Cell cell;

        int blockWidth = (int) layer.getTileWidth();
        int blockHeight = (int) layer.getTileHeight();

        for (int x=0; x < layer.getWidth(); x++) {
            for (int y=0; y < layer.getHeight(); y++) {
                cell = layer.getCell(x, y);
                if (cell != null) {

                    //create static physics body for each tile
                    //todo: only create bodies for edge tiles
                    BodyDef bodyDef = new BodyDef();
                    bodyDef.type = BodyDef.BodyType.StaticBody;

                    bodyDef.position.set(
                        (x * blockWidth + blockWidth/2f) / PIXELS_TO_METERS * TILE_RENDER_SCALE,
                        (y * blockHeight + blockHeight/2f) / PIXELS_TO_METERS * TILE_RENDER_SCALE);

                    Body body = mAdventure.mWorld.createBody(bodyDef);

                    PolygonShape shape = new PolygonShape();
                    shape.setAsBox(blockWidth/2f / PIXELS_TO_METERS * TILE_RENDER_SCALE, blockHeight/2f / PIXELS_TO_METERS * TILE_RENDER_SCALE);

                    //playing around with other shape types

                    //ChainShape shape = new ChainShape();

                    //CircleShape shape = new CircleShape();
                    //shape.setRadius(blockWidth/2 / PIXELS_TO_METERS);

                    /*
                    EdgeShape shape = new EdgeShape();

                    Vector2 v1 = new Vector2(0,0);
                    Vector2 v2 = new Vector2(blockWidth/2 / PIXELS_TO_METERS,0);

                    shape.set(v1, v2);
                    shape.setVertex0(v2);
                    shape.setVertex3(v2);
                    shape.setHasVertex0(true);
                    shape.setHasVertex3(true);
                    */

                    FixtureDef fixtureDef = new FixtureDef();
                    fixtureDef.shape = shape;
                    fixtureDef.density = 1.0f;
                    fixtureDef.friction = 0.0f;
                    fixtureDef.restitution = 0.0f;

                    //body.setUserData(cell);   //GameContactListener assumes userdata is always a GameObject, so this doesn't work
                    GameObject object = new GameObject();
                    body.setUserData(object);

                    body.createFixture(fixtureDef);
                    shape.dispose();

                }
            }
        }
    }

    private void parseTiledObjectLayer () {

        MapObjects layerShapes = mCollisionObjectLayer.getObjects();
        Gdx.app.debug(TAG,this.getClass().getSimpleName() + " : shape Count = " + layerShapes.getCount());

        if (layerShapes.getCount() == 0) return;

        GameObject object;

        for(MapObject layerShape : layerShapes) {

            Gdx.app.log(TAG,this.getClass().getSimpleName() + " : Parsing Layer Object Name : " + layerShape.getName());
            Gdx.app.log(TAG,this.getClass().getSimpleName() + " : Parsing Layer Class Name : " + layerShape.getClass().getSimpleName());

            if (layerShape.getClass().getSimpleName().equalsIgnoreCase("RectangleMapObject") || layerShape.getClass().getSimpleName().equalsIgnoreCase("TiledMapTileMapObject")) {

                //assume all objects on the tiled object layer correspond to game object types
                //String type = ((RectangleMapObject)layerShape).getProperties().get("type").toString();
                String type = layerShape.getProperties().get("type").toString();
                GameObjectType objectType = GameObjectType.valueOf(type);

                Gdx.app.log(TAG,this.getClass().getSimpleName() + " : RectangleMapObject : " + type);

                object = mAdventure.mGameObjectFactory.spawn(objectType, 0f, 0f, layerShape, false, false);

                if (object != null) {
                    mAdventure.mGameScreenManager.addActor(object);
                }
            }
        }
    }
}
