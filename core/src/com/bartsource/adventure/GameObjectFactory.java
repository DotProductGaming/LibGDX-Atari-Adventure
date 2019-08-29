/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bartsource.adventure;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

import static com.bartsource.adventure.Constants.*;

/**
 *
 * @author bart
 */
public class GameObjectFactory {

    private final Adventure mAdventure;
    private final Array<MapObject> mMapObjectTemplates;

    public GameObjectFactory(Adventure adventure) {
        mAdventure = adventure;
        mMapObjectTemplates = new Array();
    }

    public enum GameObjectType {

        invalid(-1),
        player (0),
        screenTransition (1),

        item (2),
        itemTemplate (3),

        //spawnItem - itemTypes
        gate (13),
        key (14),
        dragon (15), //todo replace npc
        sword (16),
        chalice (17),
        bridge (18),
        spawner (19),

        //dynamic
        arrow (50),

        //custom
        npc (50),   //todo remove

        // End
        test (9999),
        objectCount(-1);

        private final int mIndex;
        GameObjectType(int index) {
            this.mIndex = index;
        }

        public int index() {
            return mIndex;
        }

        // TODO: Is there any better way to do this?
        public static GameObjectType indexToType(int index) {
            final GameObjectType[] valuesArray = values();
            GameObjectType foundType = invalid;
            for (int x = 0; x < valuesArray.length; x++) {
                GameObjectType type = valuesArray[x];
                if (type.mIndex == index) {
                    foundType = type;
                    break;
                }
            }
            return foundType;
        }

    }

    //layerShape is the object coming from the tmx file, otherwise use x & y to specify spawn location and dimensions will probably be the size of the texture
    public GameObject spawn(GameObjectType type, float x, float y, MapObject layerShape, boolean horzFlip, boolean forceSpawn) {

        GameObject object = null;

        switch(type) {
            case player:
                object = spawnPlayer(x, y);
                break;
            case screenTransition:
                object = spawnScreenTransition(layerShape);
                break;
            case item:
                object = spawnItem(x, y, layerShape, forceSpawn);
                break;
            case itemTemplate:
                mMapObjectTemplates.add(layerShape);
                Gdx.app.debug(TAG, this.getClass().getSimpleName() + " : Storing Item Template: " + layerShape.getName());
                break;
            case arrow:
                object = spawnArrow(x, y);
                break;
            default:
                Gdx.app.error(TAG, this.getClass().getSimpleName() + " : Unhandled Spawn Type: " + type);
                break;
        }

        return object;

    }

    public void spawnItemFromTemplate(String name, float x, float y, boolean forceSpawn) {

        GameObject object = null;

        for (MapObject layerShape : mMapObjectTemplates) {

            if (layerShape.getName().equalsIgnoreCase(name)) {
                object = spawn(GameObjectType.item, x, y, layerShape, false, forceSpawn);
                break;
            }

        }

        if (object != null) {
            mAdventure.mGameScreenManager.addActor(object);
        } else {
            Gdx.app.error(TAG, this.getClass().getSimpleName() + " : spawned null item template : name = " + name);
        }

    }

    //custom spawn routine - TODO: create a player object in Tiled and use spawnItem
    private GameObject spawnPlayer(float x, float y) {

        Player object = new Player(mAdventure);

        object.setTexture(new Texture(Gdx.files.internal("data/player.png")));

        object.setPosition(x-object.getWidth()/2, y-object.getHeight()/2);
        object.setName("player");
        object.mFlag = DATA_STATIC;
        object.mType = GameObjectType.player;

        //body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.fixedRotation = true;
        bodyDef.position.set(
            (object.getX() + object.getWidth()/2) / PIXELS_TO_METERS,
            (object.getY() + object.getHeight()/2) / PIXELS_TO_METERS);
        Body body = mAdventure.mWorld.createBody(bodyDef);

        //shape
        CircleShape shape = new CircleShape();
        shape.setRadius(object.getWidth()/2 / PIXELS_TO_METERS);

        //PolygonShape shape = new PolygonShape();
        //shape.setAsBox(object.getWidth()/2 / PIXELS_TO_METERS, object.getHeight()/2 / PIXELS_TO_METERS);

        //fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;      //mass   0..1, 1 = steel ball, 0 = balloon
        fixtureDef.friction = 1.0f;
        fixtureDef.restitution = 0.0f;  //bounce 0..1, 0 = no bounce
        fixtureDef.isSensor = false;
        body.createFixture(fixtureDef);
        shape.dispose();

        //userdata & object
        body.setUserData(object);
        object.mBody = body;

        return object;

    }

    //custom spawn routine - TODO: create a player object in Tiled and use spawnItem
    private GameObject spawnArrow(float x, float y) {

        Arrow object = mAdventure.arrowPool.obtain();

        object.setTexture(new Texture(Gdx.files.internal("data/arrow.png")));

        object.setPosition(x-object.getWidth()/2, y-object.getHeight()/2);
        object.setName("arrow");
        object.mType = GameObjectType.arrow;

        //body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.fixedRotation = true;
        bodyDef.position.set(
            (object.getX() + object.getWidth()/2) / PIXELS_TO_METERS,
            (object.getY() + object.getHeight()/2) / PIXELS_TO_METERS);

            //(object.getX()) / PIXELS_TO_METERS,
            //(object.getY()) / PIXELS_TO_METERS);


        Body body = mAdventure.mWorld.createBody(bodyDef);

        //shape
        CircleShape shape = new CircleShape();
        shape.setRadius(object.getWidth()/4 / PIXELS_TO_METERS);    //arrows slightly smaller so the "stick" in things

        //PolygonShape shape = new PolygonShape();
        //shape.setAsBox(object.getWidth()/2/PIXELS_TO_METERS, object.getHeight()/2/PIXELS_TO_METERS);

        //fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.5f;      //mass   0..1, 1 = steel ball, 0 = balloon
        fixtureDef.friction = 1.0f;
        fixtureDef.restitution = 0.0f;  //bounce 0..1, 0 = no bounce
        fixtureDef.isSensor = false;
        body.createFixture(fixtureDef);
        shape.dispose();

        //userdata & object
        body.setUserData(object);
        object.mBody = body;

        return object;

    }

    //screenTransition is RectangleMapObject type in tiled - gets x,y,h,w from Rectangle
    private GameObject spawnScreenTransition(MapObject layerShape) {

        GameLayerObject object = new GameLayerObject(mAdventure);

        object.setName(layerShape.getName());
        object.setType(layerShape.getProperties().get("type").toString());

        //custom player spawn point (on next screen)
        if (hasProperty(layerShape, "spawnX")) object.mSpawnX = Integer.parseInt(layerShape.getProperties().get("spawnX", null).toString());
        if (hasProperty(layerShape, "spawnY")) object.mSpawnY = Integer.parseInt(layerShape.getProperties().get("spawnY", null).toString());

        //tmx portal
        if (hasProperty(layerShape, "isPortal")) {
            object.mIsPortal = true;
            if (hasProperty(layerShape, "portalX")) object.mPortalX = Integer.parseInt(layerShape.getProperties().get("portalX", null).toString());
            if (hasProperty(layerShape, "portalY")) object.mPortalY = Integer.parseInt(layerShape.getProperties().get("portalY", null).toString());
        }

        Rectangle rectangle = ((RectangleMapObject)layerShape).getRectangle();
        object.setDebugTexture(new Texture(Gdx.files.internal("data/collisionRectangle.png")), rectangle);

        //body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.fixedRotation = true;
        bodyDef.position.set(
            (rectangle.x + rectangle.width/2) / PIXELS_TO_METERS * TILE_RENDER_SCALE,
            (rectangle.y + rectangle.height/2) / PIXELS_TO_METERS * TILE_RENDER_SCALE);
        Body body = mAdventure.mWorld.createBody(bodyDef);

        //shape
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(rectangle.width/2 / PIXELS_TO_METERS * TILE_RENDER_SCALE, rectangle.height/2 / PIXELS_TO_METERS * TILE_RENDER_SCALE);

        //fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.1f;
        fixtureDef.friction = 0.0f;
        fixtureDef.restitution = 0.0f;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef);
        shape.dispose();

        //userdata & object
        body.setUserData(object);
        object.mBody = body;

        spawnDebugMessage(object, bodyDef, fixtureDef);

        //if (DEBUG_COLLISONS) return object;   //this was making the game crash when DEBUG_COLLISONS was on and you would try to go back to a previous screen.
        //probably because some part of the object is blown away in destroyBodies
        //this can be used (to render a screen transition with the debug texture, but you can't every go back to a previous screen
        return null;

    }

    //item is MapObject type tile - gets x,y from Rectangle, h&w from texture
    private GameObject spawnItem(float x, float y, MapObject mapObject, boolean forceSpawn) {

        String name = mapObject.getName();

        if (!forceSpawn) {
            //if item already exists, don't create
            if (mAdventure.mStage.getRoot().findActor(name) != null) return null;
        }

        GameLayerObject object = new GameLayerObject(mAdventure);
        object.setTexture(new Texture(Gdx.files.internal("data/" + mapObject.getProperties().get("texture", null))));

        //other properties - do this first, sets things that affect size & shape
        parseOtherProperties(mapObject, object);

        //if the x & y are passed in then use those ... it's sort of a hack that 0,0 can't be used
        if (x != 0 || y != 0) {
            object.setPosition(x - (object.getWidth()/2), y - (object.getHeight()/2));
        } else {
            //else use x, y stored in the mapObject
            //this essentially centers the item texture where the tile was placed in the editor
            //did this so the item appears directly where placed in the tiled editor
            //Rectangle rectangle = ((TiledMapTileMapObject)mapObject).getRectangle();  //not available for TiledMapTileMapObject
            Vector2 itemCenter = new Vector2( ((TiledMapTileMapObject) mapObject).getX() + TILE_BLOCK_WIDTH / 2f,  ((TiledMapTileMapObject) mapObject).getY() + TILE_BLOCK_WIDTH / 2f);
            object.setPosition(
                (itemCenter.x) * TILE_RENDER_SCALE - (object.getWidth()/2),
                (itemCenter.y) * TILE_RENDER_SCALE - (object.getHeight()/2) );
        }

        object.setName(name);
        object.mFlag = DATA_STATIC;

        //body
        BodyDef bodyDef = new BodyDef();
        String bodyTypeProperty = mapObject.getProperties().get("bodyType", null);
        if (bodyTypeProperty != null ) {
            bodyDef.type = BodyDef.BodyType.valueOf(bodyTypeProperty);
        } else {
            //default for item
            bodyDef.type = BodyDef.BodyType.DynamicBody;
        }
        bodyDef.fixedRotation = true;
        bodyDef.position.set(
            (object.getX() + object.getWidth()/2) / PIXELS_TO_METERS,
            (object.getY() + object.getHeight()/2) / PIXELS_TO_METERS);
        Body body = mAdventure.mWorld.createBody(bodyDef);

        //shape
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(object.getWidth()*object.getScaleX()/2 / PIXELS_TO_METERS, object.getHeight()*object.getScaleY()/2 / PIXELS_TO_METERS);

        //fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.0001f;
        fixtureDef.friction = 0.0f;
        fixtureDef.restitution = 0.0f;

        String sensor = mapObject.getProperties().get("sensor", null);
        if (sensor != null ) {
            fixtureDef.isSensor = Boolean.valueOf(sensor);
        } else {
            //default for item
            fixtureDef.isSensor = true;
        }
        body.createFixture(fixtureDef);
        shape.dispose();
        body.setLinearDamping(8f);          //drag

        //userdata & object
        body.setUserData(object);
        object.mBody = body;

        //add components - make sure after creating body
        addComponents(object);

        spawnDebugMessage(object, bodyDef, fixtureDef);

        return object;

    }

    private void parseOtherProperties(MapObject layerShape, GameLayerObject object) {

        //if the item is action driven, then Actor.Actions dictate translation of the sprite & physics body
        if (hasProperty(layerShape, "actionDriven")) object.mActionDriven = true;

        if (hasProperty(layerShape, "frames")) object.setTextureFrames(Integer.parseInt(layerShape.getProperties().get("frames", null).toString()), 0);
        if (hasProperty(layerShape, "color")) object.mColor = layerShape.getProperties().get("color", null).toString();

        //set overall scale, or XY seperate
        if (hasProperty(layerShape, "scale")) {
            object.setScale(Float.parseFloat(layerShape.getProperties().get("scale", null).toString()));
        } else {
            if (hasProperty(layerShape, "scaleX")) object.setScaleX(Float.parseFloat(layerShape.getProperties().get("scaleX", null).toString()));
            if (hasProperty(layerShape, "scaleY")) object.setScaleY(Float.parseFloat(layerShape.getProperties().get("scaleY", null).toString()));
        }

        //mType is the itemType property if definded, otherwise main "Type" property in Tiled
        if (hasProperty(layerShape, "itemType")) {
            object.setType((layerShape).getProperties().get("itemType").toString());
        } else {
            object.setType((layerShape).getProperties().get("type").toString());
        }

    }

    private void addComponents(GameLayerObject object) {

        //add components and behaviors based on itemType
        switch(object.mType) {

            case gate:
                object.mComponents.add(new GateComponent(object, mAdventure));
                break;
            case dragon:
                object.mComponents.add(new DragonComponent(object, mAdventure));
                object.addBehavior(new FollowBehavior(mAdventure));
                break;
            case chalice:
                object.setPixmap(getPixmap(object));
                object.mComponents.add(new ColorChangeComponent(object, mAdventure));
            case bridge:
                Gdx.app.debug(TAG, this.getClass().getSimpleName() + " : adding BridgeComponent : name = " + object.getName());
                object.mComponents.add(new BridgeComponent(object, mAdventure));
                break;
            case spawner:
                SpawnerComponent spawnerComponent = new SpawnerComponent(object, mAdventure);
                object.mComponents.add(spawnerComponent);
                break;

        }
    }

    private void spawnDebugMessage(GameLayerObject object, BodyDef bodyDef, FixtureDef fixtureDef) {

        if (!DEBUG_GAME_OBJECT_FACTORY) return;

        Gdx.app.debug(TAG, this.getClass().getSimpleName() +
        " : spawnItem : name = " + object.getName() +
        " : type = " + object.getType() +
        " : BodyType = " + bodyDef.type +
        " : actionDriven = " + object.mActionDriven +
        " : isSensor = " + fixtureDef.isSensor +
        " : frames = " + object.mTextureFrames +
        " : color = " + object.mColor
        );
    }

    private boolean hasProperty(MapObject layerShape, String property) {
        return (layerShape.getProperties().get(property, null) != null );
    }

    private Pixmap getPixmap(GameObject object) {

        Texture texture = object.mTexture.getTexture();
        TextureData textureData = texture.getTextureData();
        textureData.prepare();
        return texture.getTextureData().consumePixmap();

    }

}
