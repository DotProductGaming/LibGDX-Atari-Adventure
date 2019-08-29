package com.bartsource.adventure;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import static com.bartsource.adventure.Constants.*;

public class Adventure extends ApplicationAdapter {

    private SpriteBatch mBatch;
    GameObjectFactory mGameObjectFactory;
    TileMapManager mTileMapManager;
    private TiledMapRenderer mTiledMapRenderer;
    private Viewport mViewPort;
    OrthographicCamera mCamera;
    GameScreenManager mGameScreenManager;
    Box2dManager mBox2dManager;
    GameSoundManager mGameSoundManager;
    GameStateManager mGameStateManager;
    GameControls mControls;

    private Box2DDebugRenderer mDebugRenderer;

    Stage mStage;
    World mWorld;
    int mVirtualWidth;
    int mVirtualHeight;
    private Adventure mThis;

    boolean mDebugPhysics = false;
    boolean mChangeLevelTrigger = false;

    final Pool<Arrow> arrowPool = new Pool<Arrow>() {
        @Override
        protected Arrow newObject() {
            return new Arrow(mThis);
        }
    };

    @Override
    public void create () {

        setup();
        setupViewport();
        setupPhysics();
        createObjects();
        setupScreens();
        setupTileLayers();
        setupInput();
        setupOtherManagers();

    }

    private void setup() {
        mThis = this;
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
    }

    private void setupOtherManagers() {
        mGameSoundManager = new GameSoundManager();
        mGameStateManager = new GameStateManager(this);
    }

    private void setupScreens() {

        mGameScreenManager = new GameScreenManager(this, 1, 5, 7);        //yellow castle
        //mGameScreenManager = new GameScreenManager(this, 1, 2, 10);       //inside black castle
        //mGameScreenManager = new GameScreenManager(this, 1, 2, 8);        //black castle
        //mGameScreenManager = new GameScreenManager(this, 1, 2, 7);          //bridge debug
        //mGameScreenManager = new GameScreenManager(this, 1, 3, 5);          //bow & arrow test
        //mGameScreenManager = new GameScreenManager(this, 1, 5, 4);          //spawner test

    }

    private void setupPhysics() {

        mWorld = new World(new Vector2(0,0), true);
        mDebugRenderer = new Box2DDebugRenderer();
        mWorld.setContactListener(new GameContactListener(this));
        mBox2dManager = new Box2dManager(this);

    }

    private void setupInput() {

        mControls = new GameControls(this);

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        GestureDetector gestureDetector = new GestureDetector(mControls);
        inputMultiplexer.addProcessor(gestureDetector);
        inputMultiplexer.addProcessor(mControls);
        inputMultiplexer.addProcessor(mStage);
        Gdx.input.setInputProcessor(inputMultiplexer);

    }

    private void setupTileLayers() {

        mTileMapManager = new TileMapManager(this);
        mTiledMapRenderer = new OrthogonalTiledMapRenderer(mTileMapManager.loadTileLayer(mGameScreenManager.getTmxName()), TILE_RENDER_SCALE, mBatch);
        mTiledMapRenderer.setView(mCamera);
        mGameScreenManager.screenInit();

    }

    private void setupViewport() {

        mVirtualWidth = VIRTUAL_WIDTH;
        mVirtualHeight = VIRTUAL_HEIGHT;
        mCamera = new OrthographicCamera();
        mViewPort = new StretchViewport(mVirtualWidth, mVirtualHeight, mCamera);
        mBatch = new SpriteBatch();
        mStage = new Stage(mViewPort, mBatch);

    }

    private void createObjects() {

        mGameObjectFactory = new GameObjectFactory(this);
        mStage.addActor(mGameObjectFactory.spawn(GameObjectFactory.GameObjectType.player, 335, 50, null, false, false));

    }

    @Override
    public void render () {

        //main game loop
        gameUpdate();
        gameRender();

    }

    private void gameUpdate() {

        mBox2dManager.update();
        mWorld.step(1f/60f, 6, 2);
        //act calls component updates... might want to split this out for phased objects?
        mStage.act(Gdx.graphics.getDeltaTime());
        //this is a good example - need to update something that doesn't draw onscreen
        mGameStateManager.act(Gdx.graphics.getDeltaTime());

        //have to do it like this because input isn't synchronized
        if (mChangeLevelTrigger) {
            mChangeLevelTrigger = false;
            mGameScreenManager.changeScreen();
            setupTileLayers();
        }

    }

    private void gameRender() {

        Gdx.gl.glClearColor(.666f, .666f, .666f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (mGameStateManager.gameIsWon()) {
            mBatch.setColor(mGameStateManager.mBatchColor);
            mTiledMapRenderer.render();
            mBatch.setColor(Color.WHITE);
        } else
            mTiledMapRenderer.render();

        //todo - draw behind tile layer stage
        mStage.draw();
        //todo - draw in front of tile layer stage

        if (mDebugPhysics) {
            Matrix4 debugMatrix = mBatch.getProjectionMatrix().cpy().scale(PIXELS_TO_METERS, PIXELS_TO_METERS, 0);
            mDebugRenderer.render(mWorld, debugMatrix);
        }

    }

    @Override
    public void resize(int width, int height) {

        mViewPort.update(width,height);
        mCamera.position.set(mCamera.viewportWidth/2, mCamera.viewportHeight/2,0);
        Gdx.app.debug(TAG, this.getClass().getSimpleName() + " : resize w,h = " + mVirtualWidth + ", " + mVirtualHeight);
        mCamera.update();

    }

}

