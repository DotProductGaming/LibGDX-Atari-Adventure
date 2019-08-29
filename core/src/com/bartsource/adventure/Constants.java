
package com.bartsource.adventure;

/**
 *
 * @author bart
 */
public final class Constants {

    public static final String TAG = "ADVENTURE";
    static final int VIRTUAL_WIDTH = 672;
    static final int VIRTUAL_HEIGHT = 448;

    static final float TILE_RENDER_SCALE = 0.5f;    //by design, tiled map size to "atari" resolution
    static final int TILE_BLOCK_WIDTH = 32;          //width of blocks in tiled map
    public static final float PIXELS_TO_METERS = 100f;

    static final boolean DEBUG_COLLISONS = false;
    static final boolean DEBUG_LIBGDX_SHAPES = false;
    static final boolean DEBUG_GAME_GRAPH = false;
    static final boolean DEBUG_GAME_OBJECT_FACTORY = false;

    static final short DATA_STATIC =        0x1;    // 0001                    //persists between screens
    public static final short DATA_TRANSIENT =     0x1 << 1; // 0010 or 0x2 in hex    //disposed between screens

    static final int ARROW_WIDTH = 22;
    public static final float ARROW_FORCE = 10;
    static final float ARROW_VELOCITY = 10;

    //logLevel can be one of the following values:
    //Application.LOG_NONE: mutes all logging.
    //Application.LOG_DEBUG: logs all messages.
    //Application.LOG_ERROR: logs only error messages.
    //Application.LOG_INFO: logs error and normal messages.

    //Gdx.app.debug
    //Gdx.app.error
    //Gdx.app.log //info presumably

    //standard debug msg:
    /*
        Gdx.app.debug(TAG, this.getClass().getSimpleName() + " : spawnItem : name = " + object.getName() +
                " : type = " + object.getType() +
                " : BodyType = " + bodyDef.type +
                " : actionDriven = " + object.mActionDriven
                );

        Gdx.app.debug(TAG, this.getClass().getSimpleName() + " : resetBody");

    */

    public enum PhysicsChangeType {
        setActiveFalse
	}

	static final int[] atariPalette = {
			//0	,
			0xff000000,
			0xff404040,
			0xff6c6c6c,
			0xff909090,
			0xffb0b0b0,
			0xffc8c8c8,
			0xffdcdcdc,
			0xffececec,
			//1	,
			0xff444400,
			0xff646410,
			0xff848424,
			0xffa0a034,
			0xffb8b840,
			0xffd0d050,
			0xffe8e85c,
			0xfffcfc68,
			//2	,
			0xff702800,
			0xff844414,
			0xff985c28,
			0xffac783c,
			0xffbc8c4c,
			0xffcca05c,
			0xffdcb468,
			0xffecc878,
			//3	,
			0xff841800,
			0xff983418,
			0xffac5030,
			0xffc06848,
			0xffd0805c,
			0xffe09470,
			0xffeca880,
			0xfffcbc94,
			//4	,
			0xff880000,
			0xff9c2020,
			0xffb03c3c,
			0xffc05858,
			0xffd07070,
			0xffe08888,
			0xffeca0a0,
			0xfffcb4b4,
			//5	,
			0xff78005c,
			0xff8c2074,
			0xffa03c88,
			0xffb0589c,
			0xffc070b0,
			0xffd084c0,
			0xffdc9cd0,
			0xffecb0e0,
			//6	,
			0xff480078,
			0xff602090,
			0xff783ca4,
			0xff8c58b8,
			0xffa070cc,
			0xffb484dc,
			0xffc49cec,
			0xffd4b0fc,
			//7	,
			0xff140084,
			0xff302098,
			0xff4c3cac,
			0xff6858c0,
			0xff7c70d0,
			0xff9488e0,
			0xffa8a0ec,
			0xffbcb4fc,
			//8	,
			0xff000088,
			0xff1c209c,
			0xff3840b0,
			0xff505cc0,
			0xff6874d0,
			0xff7c8ce0,
			0xff90a4ec,
			0xffa4b8fc,
			//9	,
			0xff00187c,
			0xff1c3890,
			0xff3854a8,
			0xff5070bc,
			0xff6888cc,
			0xff7c9cdc,
			0xff90b4ec,
			0xffa4c8fc,
			//10	,
			0xff002c5c,
			0xff1c4c78,
			0xff386890,
			0xff5084ac,
			0xff689cc0,
			0xff7cb4d4,
			0xff90cce8,
			0xffa4e0fc,
			//11	,
			0xff003c2c,
			0xff1c5c48,
			0xff387c64,
			0xff509c80,
			0xff68b494,
			0xff7cd0ac,
			0xff90e4c0,
			0xffa4fcd4,
			//12	,
			0xff003c00,
			0xff205c20,
			0xff407c40,
			0xff5c9c5c,
			0xff74b474,
			0xff8cd08c,
			0xffa4e4a4,
			0xffb8fcb8,
			//13	,
			0xff143800,
			0xff345c1c,
			0xff507c38,
			0xff6c9850,
			0xff84b468,
			0xff9ccc7c,
			0xffb4e490,
			0xffc8fca4,
			//14	,
			0xff2c3000,
			0xff4c501c,
			0xff687034,
			0xff848c4c,
			0xff9ca864,
			0xffb4c078,
			0xffccd488,
			0xffe0ec9c,
			//15	,
			0xff442800,
			0xff644818,
			0xff846830,
			0xffa08444,
			0xffb89c58,
			0xffd0b46c,
			0xffe8cc7c,
			0xfffce08c,
	};

}
