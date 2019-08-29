/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bartsource.adventure;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;

/**
 *
 * @author bart
 */
public class TileCollisionComponent extends GameComponent {

    private final Adventure mAdventure;
    private Vector2 mParentVelocity;

    public TileCollisionComponent(Adventure adventure) {
        //todo phases
        //todo use an object registry
        mAdventure = adventure;
    }

    @Override
    public void update(float timeDelta, GameObject parent) {

        //this isn't really working because input is event driven so I can't control the order between this check and translation
        //switched over to box2d collision detection

        /*
        mParentVelocity = parent.mBody.getLinearVelocity();

        boolean collisionX = false;
        boolean collisionY = false;

        float parentHeight = parent.getHeight();
        float parentWidth = parent.getWidth();

        float x = parent.getX();
        float y = parent.getY();

        float nextX = (parent.mBody.getPosition().x * PIXELS_TO_METERS) - parentWidth/2 + (mParentVelocity.x * timeDelta * PIXELS_TO_METERS);
        float nextY = (parent.mBody.getPosition().y * PIXELS_TO_METERS) - parentHeight/2 + (mParentVelocity.y * timeDelta * PIXELS_TO_METERS);

        float tileWidth = mAdventure.mTileMapManager.mCollisionLayer.getTileWidth();

        //bottom left
        collisionX = collisionCell((int)(nextX / TILE_RENDER_SCALE / tileWidth),(int)(y / TILE_RENDER_SCALE / tileWidth));
        collisionY = collisionCell((int)(x / TILE_RENDER_SCALE / tileWidth),(int)(nextY / TILE_RENDER_SCALE / tileWidth));

        //bottom right
        collisionX = collisionX || collisionCell((int)((nextX + parentWidth) / TILE_RENDER_SCALE / tileWidth),(int)(y / TILE_RENDER_SCALE / tileWidth));
        collisionY = collisionY || collisionCell((int)((x + parentWidth) / TILE_RENDER_SCALE / tileWidth),(int)(nextY / TILE_RENDER_SCALE / tileWidth));

        //top left
        collisionX = collisionX || collisionCell((int)(nextX / TILE_RENDER_SCALE / tileWidth),(int)((y + parentHeight) / TILE_RENDER_SCALE / tileWidth));
        collisionY = collisionY || collisionCell((int)(x / TILE_RENDER_SCALE / tileWidth),(int)((nextY + parentHeight) / TILE_RENDER_SCALE / tileWidth));

        //top right
        collisionX = collisionX || collisionCell((int)((nextX + parentWidth) / TILE_RENDER_SCALE / tileWidth),(int)((y + parentHeight) / TILE_RENDER_SCALE / tileWidth));
        collisionY = collisionY || collisionCell((int)((x + parentWidth) / TILE_RENDER_SCALE / tileWidth),(int)((nextY + parentHeight) / TILE_RENDER_SCALE / tileWidth));

        if (collisionX) parent.mBody.setLinearVelocity(0f, mParentVelocity.y);
        if (collisionY) parent.mBody.setLinearVelocity(mParentVelocity.x, 0f);
        */
    }

    public boolean collisionCell(int x, int y) {
        //mAdventure.
        TiledMapTileLayer.Cell collisionCell = mAdventure.mTileMapManager.mPrimaryTileLayer.getCell(x,y);
        return collisionCell != null && collisionCell.getTile() != null;
        //i was maintaining the TILE_COLLISION_PROPERTY in the tmx file, but it became bitchy to implement across tmx's so i'm going to collide with all non-null tiles for now
        //&& collisionCell.getTile().getProperties().containsKey(TILE_COLLISION_PROPERTY);
    }

}
