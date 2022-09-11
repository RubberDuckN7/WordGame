package com.distantlandgames.violet

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

import android.opengl.GLES20
import android.util.Log
import com.distantlandgames.tetrisandwords.R
import com.distantlandgames.tetrisandwords.viewmodel.GameViewModel
import com.distantlandgames.violet.`object`.Instance3D
import com.distantlandgames.violet.geometries.QuadShape
import com.distantlandgames.violet.helpers.*
import com.distantlandgames.violet.mesh.MeshPart
import com.distantlandgames.violet.shaders.pipeline.PipelineSimple3D
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

abstract class GLRenderer : GLSurfaceView.Renderer {
    private var color = 0f
    private var timeThisRound = 0f
    private var timeLastRound = 0f
    private var fpm = 0

    protected var camera: Camera = Camera()

    abstract fun onCreate(camera: Camera)
    abstract fun onDraw()
    abstract fun onUpdate(deltaTime: Float)

    final override fun onDrawFrame(unused: GL10){
        //GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        //GLES20.glEnable(GLES20.GL_DEPTH_TEST)
//
        //GLES20.glEnable(GL10.GL_TEXTURE_2D)
        //GLES20.glEnable(GLES20.GL_BLEND)
//
        //GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        //GLES20.glEnable(GLES20.GL_ALPHA)

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)

        GLES20.glEnable(GL10.GL_TEXTURE_2D)
        GLES20.glEnable(GLES20.GL_BLEND)

        GLES20.glEnable(GLES20.GL_ALPHA)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)


        timeThisRound = System.nanoTime()/1000000.0f
        var deltaTimeThisRound = timeThisRound-timeLastRound  // to use for certain calculations
        fpm++

        onDraw()
        onUpdate(deltaTimeThisRound)

        timeLastRound = timeThisRound
    }

    final override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        GLES20.glClearColor(0f, 0.0f, 0f, 1.0f)
        /** Note from the book with example code:
        The synchronization is necessary, since the members we manipulate within the synchronized block
        could be manipulated in the onPause() method on the UI thread. That’s something we have to prevent, so
        we use an object as a lock. We could have also used the GLGame instance itself, or a proper lock.

        Code:
        glGraphics.setGL(gl);
        synchronized(stateChanged) {
        if(state == GLGameState.Initialized)
        screen = getStartScreen();
        state = GLGameState.Running;
        screen.resume();
        startTime = System.nanoTime();
        }
         */
    }

    final override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        Log.d("VIOLET", "Surface changed")
        camera.setBounds(width, height)
        onCreate(camera)
    }

    fun setClearColor(color: Float) {
        this.color = color
    }
}

/*open class GLRenderer : GLSurfaceView.Renderer {
    private var color = 0f
    private var timeThisRound = 0f
    private var timeLastRound = 0f
    private var fpm = 0

    // These still work without volatile, but refreshes are not guaranteed to happen.
    @Volatile
    var mDeltaX: Float = 0.toFloat()
    @Volatile
    var mDeltaY: Float = 0.toFloat()

    var updated: (deltaTime: Float) -> Unit = {}
    var draw: () -> Unit = {}
    var surfaceChanged: (width: Int, height: Int) -> Unit = { width, height ->  }
    var created: () -> Unit = {}
    var destroyed: () -> Unit = {}

    var camera: Camera = Camera()

    var start = true

    lateinit var context: Context
    lateinit var viewmodel: GameViewModel

    var wordList: String = "Words:"

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig){
        GLES20.glClearColor(0f, 1.0f, 0f, 1.0f)
        created()
    }

    var meshPart = MeshPart()
    var textureId: Int = 0
    var textureId2: Int = 0

    var testInstance: Instance3D = Instance3D()
    var instance = Instance3D()

    var pipelineImproved = PipelineSimple3D()

    var layer3dSimple3D = Layer3DSimple()

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int){
        GLES20.glViewport(0, 0, width, height)
        camera.setBounds(width, height)
        surfaceChanged(width, height)

        pipelineImproved.initialize(context)
        textureId = AssetHelper.loadTexture(context, R.drawable.test_block)
        textureId2 = AssetHelper.loadTexture(context, R.drawable.collection_test)

        var vertexLayout = pipelineImproved.getVertexLayout()

        var quadShape = QuadShape()
        var vertexStreamFromQuadShape = quadShape.constructStreams(vertexLayout, 1.0f)
        var indicesFromQuadShape = quadShape.getIndices()

        meshPart.create(vertexStreamFromQuadShape,
            indicesFromQuadShape,
            vertexLayout, pipelineImproved.vertexAttributes)

        layer3dSimple3D.initialize(context, meshPart, camera)
        layer3dSimple3D.addResource(textureId)
        layer3dSimple3D.addInstance(textureId, instance)

        layer3dSimple3D.addResource(textureId2)
        layer3dSimple3D.addInstance(textureId2, testInstance)

        testInstance.position =
            Vector3(1f, 0f, -5f)
        instance.position =
            Vector3(0f, 0f, -5f)
    }

    fun draw() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)

        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glEnable(GLES20.GL_BLEND)

        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        GLES20.glEnable(GLES20.GL_ALPHA)

        layer3dSimple3D.draw()
    }

    override fun onDrawFrame(unused: GL10){
        draw()
    }

    fun setClearColor(color: Float) {
        this.color = color
    }
}*/

/**
Actually, EGL is responsible for context and surface creation and destruction. EGL is another Khronos Group
standard; it defines how an operating system’s UI works together with OpenGL ES and how the operating system
grants OpenGL ES access to the underlying graphics hardware. This includes surface creation as well as context
management. Since GLSurfaceView handles all the EGL stuff for us, we can safely ignore it in almost all cases.
 */

/**
Besides registering a Renderer listener, we also have to call GLSurfaceView.onPause()/onResume() in
our activity’s onPause()/onResume() methods. The reason for this is simple. The GLSurfaceView will start up
the rendering thread in its onResume() method and tear it down in its onPause()method. This means that
our listener will not be called while our activity is paused, since the rendering thread that calls our listener
will also be paused.
 */
class GameView : GLSurfaceView {
    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    fun setRenderer(renderer: GLRenderer, density: Float) {
        super.setRenderer(renderer)
    }
}