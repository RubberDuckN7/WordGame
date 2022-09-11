package com.distantlandgames.tetrisandwords.tetris

import android.content.Context
import android.opengl.GLES20
import android.util.Log
import com.distantlandgames.tetrisandwords.R
import com.distantlandgames.tetrisandwords.viewmodel.GameViewModel
import com.distantlandgames.violet.*
import com.distantlandgames.violet.`object`.Instance3D
import com.distantlandgames.violet.geometries.QuadShape
import com.distantlandgames.violet.helpers.AssetHelper
import com.distantlandgames.violet.mesh.MeshPart
import com.distantlandgames.violet.shaders.pipeline.PipelineSimple3D
import com.distantlandgames.violet.shaders.pipeline.PipelineSimpleParticle3D
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.microedition.khronos.opengles.GL10
import kotlin.math.log2
import com.distantlandgames.tetrisandwords.tetris.LexiconHandler
import java.lang.NullPointerException

enum class GameState {
    NotStarted,
    OnGoing,
    Paused,
    PlayAnimationEraseRow,
    PlayingAnimationCollapseRow,
    GameEnded
}

class TetrisGameView : GLRenderer() {
    // These still work without volatile, but refreshes are not guaranteed to happen.
    @Volatile
    var mDeltaX: Float = 0.toFloat()
    @Volatile
    var mDeltaY: Float = 0.toFloat()
    lateinit var context: Context
    lateinit var viewmodel: GameViewModel

    var quadMesh = MeshPart()

    private var resourceMap: MutableMap<String, Int> = mutableMapOf()
    //private var layer3dSimple3D = Layer3DSimple()
    private var pipelineSimple3D = PipelineSimple3D()
    private var pipeline3Dparticle = PipelineSimpleParticle3D()

    private var tetrisGameLogic =
        TetrisGameLogic()

    private var textureId = 0
    private var textureShadeHeaderId = 0
    private var textureStarId = 0
    private var textureFadeBlockId = 0
    private var instance = Instance3D()

    private var fallDown = 0.0f
    private var fallDownTreshhold = 0.0f

    private var gameState =
        GameState.NotStarted

    private var inputHandlerDepr: InputHandlerDepr = InputHandlerDepr()
    private var inputHandler: InputHandler = InputHandler()

    private var floatCurrentX = 0f
    private var floatCurrentY = 0f

    private var steppingX = 0f
    private var steppingY = 0f

    private var indicesToRowsForDeletion = Vector<Int>()

    private var animationTrackerEraseRow = AnimationTracker()
    private var animationTrackerCollapseRows = AnimationTracker()

    private var paused = false
    private var initialized = false

    private var lexiconHandler =
        LexiconHandler()

    private var particleSystem: ParticleSystem = ParticleSystem()

    var allWords = Vector<String>()

    private var shadeHeaderInstance = Instance3D()

    override fun onCreate(camera: Camera) {
        Log.d("VIOLET", "On Create is called")

        createGame()
    }

    fun createGame() {
        if(initialized == false) {
            pipelineSimple3D = PipelineSimple3D()
            tetrisGameLogic =
                TetrisGameLogic()

            Log.d("VIOLET", "Creating the game")
            initializeGraphics()
            loadAssets()
            initializeGame()
            initialized = true
        }

        loadBoardStates()

        for(cellIndex in 0 .. tetrisGameLogic.nrOfColumns()-1) {
            var cell = tetrisGameLogic.getCell(cellIndex, 0)
            spawnFadingFallingBlock(cell.position)
        }
    }

    fun reCreate() {
        if(initialized == false && paused == true) {
            Log.d("VIOLET", "Re creating the game")
            release()
            initializeGraphics()
            loadAssets()
            initializeGame()
            initialized = true
        }
    }

    fun initializeGraphics() {
        pipelineSimple3D = PipelineSimple3D()
        pipelineSimple3D.initialize(context)

        pipeline3Dparticle = PipelineSimpleParticle3D()
        pipeline3Dparticle.initialize(context)

        var quadShape = QuadShape()

        var vertexLayout = pipelineSimple3D.getVertexLayout()
        var vertexStreamFromQuadShape = quadShape.constructStreams(vertexLayout, 1.0f)
        var indicesFromQuadShape = quadShape.getIndices()

        quadMesh.create(vertexStreamFromQuadShape,
            indicesFromQuadShape,
            vertexLayout, pipelineSimple3D.vertexAttributes)

        //layer3dSimple3D.initialize(context, quadMesh, camera)

        particleSystem.pipeline = pipeline3Dparticle
        particleSystem.particleMesh = quadMesh

        loadAssets()
    }

    fun spawnExplosion(position: Vector3) {
        return
        var emitter = ParticleSystem.Emitter()

        emitter.create(1f, 200f)

        emitter.resourceId = textureStarId
        emitter.spawnLimit = 9

        emitter.spawnBehavior = { particle ->
            particle.position.x = position.x
            particle.position.y = position.y
            particle.position.z = position.z + 0.2f

            particle.life = 1500f
            particle.alpha = 1.0f
            particle.speed = 2f

            particle.direction.x = -1f + (0f..1f).random()
            particle.direction.y = -1f + (0f..1f).random()
            particle.direction.z = 0f
        }

        emitter.updateBehavior = { particle, deltaTime ->
            var scale = deltaTime / 1000f

            //particle.position.x = 3f
            //particle.position.y = 5f
            //particle.position.z = 0f

            particle.position.x += particle.direction.x * particle.speed * scale
            particle.position.y += particle.direction.y * particle.speed * scale
            particle.position.z += particle.direction.z * particle.speed * scale

            particle.scale.x += 1.5f * scale
            particle.scale.y += 1.5f * scale

            particle.life -= deltaTime
            particle.alpha = particle.life / 1500f
        }

        particleSystem.addemitter(emitter)
    }

    fun spawnFadingFallingBlock(position: Vector3) {

        var emitter = ParticleSystem.Emitter()

        emitter.create(1000f, 20f)

        emitter.resourceId = textureFadeBlockId
        emitter.spawnLimit = 1

        emitter.spawnBehavior = { particle ->
            particle.position.x = position.x
            particle.position.y = position.y
            particle.position.z = position.z + 0.04f

            particle.life = 250f
            particle.alpha = 0.5f
            particle.speed = 0.1f

            particle.direction.x = 0f
            particle.direction.y = -1f
            particle.direction.z = 0f
        }

        emitter.updateBehavior = { particle, deltaTime ->
            var scale = deltaTime / 1000f

            //particle.position.y += particle.direction.y * particle.speed * scale

            particle.life -= deltaTime
            particle.alpha = (particle.life / 250f) * 0.4f
        }

        particleSystem.addemitter(emitter)
    }

    fun spawnFadingBlock(position: Vector3) {
        var emitter = ParticleSystem.Emitter()

        particleSystem.addemitter(emitter)
    }

    fun spawnSwooshLine(position: Vector3) {
        var emitter = ParticleSystem.Emitter()

        particleSystem.addemitter(emitter)
    }

    fun initializeGame() {
        animationTrackerEraseRow.setDuration(300f)
        animationTrackerCollapseRows.setDuration(500f)

        animationTrackerEraseRow.onTimeReached = {
            gameState =
                GameState.PlayingAnimationCollapseRow
            animationTrackerEraseRow.reset()

            for(rowIndex in indicesToRowsForDeletion) {
                var row = tetrisGameLogic.getRow(rowIndex)
                for((cellInxed, cell) in row.withIndex()) {
                    var previousCell = tetrisGameLogic.getCell(cellInxed,rowIndex-1)
                    if(previousCell.empty) {
                        //spawnFadingFallingBlock(previousCell.position)
                    }
                    //onRemovedInstance(cell)
                    //cell.offset.y = 0.0f
                }
            }
        }

        animationTrackerCollapseRows.onTimeReached = {
            gameState =
                GameState.OnGoing
            animationTrackerEraseRow.reset()
            animationTrackerCollapseRows.reset()

            if(indicesToRowsForDeletion.size > 0) {
                var last = indicesToRowsForDeletion.last()
                for (row in indicesToRowsForDeletion.reversed()) {
                    tetrisGameLogic.decreaseRowsAbove(row)
                }
                indicesToRowsForDeletion.clear()
            }

            //for(index in indicesToRowsForDeletion) {
            //    var row = tetrisGameLogic.getRow(index)
            //    for(cell in row) {
            //        //onRemovedInstance(cell)
            //        cell.offset.y = 0.0f
            //    }
//
            //    Log.d("VIOLET", "Removing row at index $index")
            //    tetrisGameLogic.decreaseRowsAbove(index)
            //}
//
            //indicesToRowsForDeletion.clear()

            gameState =
                GameState.OnGoing
        }

        gameState =
            GameState.OnGoing

        tetrisGameLogic.setOnInstanceChangedPos { instanceFrom, instanceTo ->
            onResourceChangeInstnace(instanceFrom, instanceTo)
        }

        tetrisGameLogic.setOnInstanceAdded {instance ->
            onAddedInstance(instance)
        }

        tetrisGameLogic.setOnInstanceRemoved {instance ->
            onRemovedInstance(instance)
        }

        tetrisGameLogic.setOnRowFull { indicesToFullRows ->
            onRowFulls(indicesToFullRows)
        }

        tetrisGameLogic.setOnLoseGame {
            onLose()
        }

        tetrisGameLogic.setOnBlockMove { cell ->
            onBlockMove(cell)
        }

        fallDownTreshhold = 1200f
        fallDown = 0.0f

        tetrisGameLogic.createNewGame(10, 16)

        spawnExplosion(tetrisGameLogic.getCell(1, 1).position)

        shadeHeaderInstance.position.z = -1.2f
        shadeHeaderInstance.position.x = 0f
        shadeHeaderInstance.position.y = 4f

        shadeHeaderInstance.scale.x = 10f
        shadeHeaderInstance.scale.y  = 10f
    }

    private fun release() {
        //quadMesh.release()
        //resourceMap.clear()
        //pipelineSimple3D.release()

        initialized = false
        saveBoardStates()
        pipelineSimple3D.release()
        releaseAssets()
    }

    fun releaseAssets() {
        resourceMap.clear()
    }
    fun loadAssets() {
        var totalList: ArrayList<List<String>> = ArrayList()

        // Create words data
        totalList.add(AssetHelper.readTextFileFromRawResource(context, R.raw.output2)
            .split("\n")
            .filter {  s -> s != "null" && s != ""  })

        totalList.add(AssetHelper.readTextFileFromRawResource(context, R.raw.output3)
            .split("\n")
            .filter {  s -> s != "null" && s != ""  })

        totalList.add(AssetHelper.readTextFileFromRawResource(context, R.raw.output4)
            .split("\n")
            .filter {  s -> s != "null" && s != ""  })

        totalList.add(AssetHelper.readTextFileFromRawResource(context, R.raw.output5)
            .split("\n")
            .filter {  s -> s != "null" && s != ""  })

        totalList.add(AssetHelper.readTextFileFromRawResource(context, R.raw.output6)
            .split("\n")
            .filter {  s -> s != "null" && s != ""  })

        totalList.add(AssetHelper.readTextFileFromRawResource(context, R.raw.output7)
            .split("\n")
            .filter {  s -> s != "null" && s != ""  })

        totalList.add(AssetHelper.readTextFileFromRawResource(context, R.raw.output8)
            .split("\n")
            .filter {  s -> s != "null" && s != ""  })

        totalList.add(AssetHelper.readTextFileFromRawResource(context, R.raw.output9)
            .split("\n")
            .filter {  s -> s != "null" && s != ""  })

        totalList.add(AssetHelper.readTextFileFromRawResource(context, R.raw.output10)
            .split("\n")
            .filter {  s -> s != "null" && s != ""  })

        lexiconHandler.allWords = totalList

        resourceMap = mutableMapOf(
            "q" to AssetHelper.loadTexture(context, R.drawable.blocks_q), // q
            "w" to AssetHelper.loadTexture(context, R.drawable.blocks_w), // w
            "e" to AssetHelper.loadTexture(context, R.drawable.blocks_e), // e
            "r" to AssetHelper.loadTexture(context, R.drawable.blocks_r), // r
            "t" to AssetHelper.loadTexture(context, R.drawable.blocks_t), // t
            "y" to AssetHelper.loadTexture(context, R.drawable.blocks_y), // y
            "u" to AssetHelper.loadTexture(context, R.drawable.blocks_u), // u
            "i" to AssetHelper.loadTexture(context, R.drawable.blocks_i), // i
            "o" to AssetHelper.loadTexture(context, R.drawable.blocks_o), // o
            "p" to AssetHelper.loadTexture(context, R.drawable.blocks_p), // p
            "a" to AssetHelper.loadTexture(context, R.drawable.blocks_a), // a
            "s" to AssetHelper.loadTexture(context, R.drawable.blocks_s), // s
            "d" to AssetHelper.loadTexture(context, R.drawable.blocks_d), // d
            "f" to AssetHelper.loadTexture(context, R.drawable.blocks_f), // f
            "g" to AssetHelper.loadTexture(context, R.drawable.blocks_g), // g
            "h" to AssetHelper.loadTexture(context, R.drawable.blocks_h), // h
            "j" to AssetHelper.loadTexture(context, R.drawable.blocks_j), // j
            "k" to AssetHelper.loadTexture(context, R.drawable.blocks_k), // k
            "l" to AssetHelper.loadTexture(context, R.drawable.blocks_l), // l
            "z" to AssetHelper.loadTexture(context, R.drawable.blocks_z), // z
            "x" to AssetHelper.loadTexture(context, R.drawable.blocks_x), // x
            "c" to AssetHelper.loadTexture(context, R.drawable.blocks_c), // c
            "v" to AssetHelper.loadTexture(context, R.drawable.blocks_v), // v
            "b" to AssetHelper.loadTexture(context, R.drawable.blocks_b), // b
            "n" to AssetHelper.loadTexture(context, R.drawable.blocks_n), // n
            "m" to AssetHelper.loadTexture(context, R.drawable.blocks_m), // m
            "_" to textureId
        )

        textureShadeHeaderId = AssetHelper.loadTexture(context, R.drawable.gameplay_header_shade)
        textureId = AssetHelper.loadTexture(context, R.drawable.block_background)
        textureStarId = AssetHelper.loadTexture(context, R.drawable.particle_light)
        textureFadeBlockId = AssetHelper.loadTexture(context, R.drawable.particle_move_block)
    }

    fun setInputHandler(inputHandlerDepr: InputHandlerDepr) {
        this.inputHandlerDepr = inputHandlerDepr
    }

    fun setInputHandler(inputHandler: InputHandler) {
        this.inputHandler = inputHandler
    }

    fun onTouchEvent(event: TouchEvent) {
        if(initialized == false) {
            return
        }

        if(gameState != GameState.OnGoing) {
            return
        }
        when(event.type) {
            TouchEvent.TOUCH_DOWN -> {
                Log.d("VIOLET", "Touch down ${event.x} ${event.y}")
            }
            TouchEvent.TOUCH_UP -> {
                Log.d("VIOLET", "Touch up ${event.x} ${event.y}")
            }
            TouchEvent.TOUCH_DRAGGED -> {
                Log.d("VIOLET", "Touch dragged ${event.x} ${event.y}")
            }
        }

        if (event.type === TouchEvent.TOUCH_UP) {
            //tetris.onReleased(event.x, event.y)
            floatCurrentX = event.x
            floatCurrentY = event.y

            steppingX = 0f
            steppingY = 0f
        }
        else if(event.type == TouchEvent.TOUCH_DOWN) {
            //tetris.onTouched(event.x, event.y)
            floatCurrentX = event.x
            floatCurrentY = event.y

            steppingX = 0f
            steppingX = 0f
        }
        else if(event.type == TouchEvent.TOUCH_DRAGGED) {
            //tetris.onTouchMoved(floatCurrentX, floatCurrentY, event.x, event.y)

            Log.d("VIOLET", "Dragged: x - " + event.x + " y - " + event.y)

            var margin = 20f

            steppingX += event.x - floatCurrentX
            steppingY += event.y - floatCurrentY

            var diffX = steppingX //
            var diffY = steppingY //

            var rotate = false
            if(diffY < -20) {
                if(diffX > 20) {
                    tetrisGameLogic.rotateRight(0)
                    rotate = true

                    steppingX = 0f
                    steppingY = 0f
                }
                if(diffX < -20) {
                    tetrisGameLogic.rotateLeft(0)
                    rotate = true

                    steppingX = 0f
                    steppingY = 0f
                }
            }

            if(rotate == false) {
                if (diffX > 20f) {
                    tetrisGameLogic.moveRight(0)

                    steppingX = 0f
                    steppingY = 0f
                }
                if (diffX < -20f) {
                    tetrisGameLogic.moveLeft(0)

                    steppingX = 0f
                    steppingY = 0f
                }
                if (diffY > 20f) {
                    tetrisGameLogic.moveDown(0)

                    steppingX = 0f
                    steppingY = 0f
                }
            }

            floatCurrentX = event.x
            floatCurrentY = event.y
        }
    }

    private fun handleDebugInput() {

        val events = inputHandler.getTouchEvents()
        if(events.size > 0)
            Log.d("VIOLET", "Gathered events ${events.size}")
    }
    private fun handleInput() {
        /// TODO: Maybe send the whole input queue to board to process?
        val events = inputHandlerDepr.getTouchEvents()

        Log.d("VIOLET", "Reading events: ${events.size}")
        val len = events.size
        for (i in 0 until len) {
            Log.d("VIOLET", "Processing input $i")
            val event = events.get(i)
            if (event.type === TouchEvent.TOUCH_UP) {
                //tetris.onReleased(event.x, event.y)
                floatCurrentX = event.x
                floatCurrentY = event.y

                steppingX = 0f
                steppingY = 0f
            }
            else if(event.type == TouchEvent.TOUCH_DOWN) {
                //tetris.onTouched(event.x, event.y)
                floatCurrentX = event.x
                floatCurrentY = event.y

                steppingX = 0f
                steppingX = 0f
            }
            else if(event.type == TouchEvent.TOUCH_DRAGGED) {
                //tetris.onTouchMoved(floatCurrentX, floatCurrentY, event.x, event.y)

                Log.d("VIOLET", "Dragged: x - " + event.x + " y - " + event.y)

                var margin = 20f

                steppingX += event.x - floatCurrentX
                steppingY += event.y - floatCurrentY

                var diffX = steppingX //
                var diffY = steppingY //

                var rotate = false
                if(diffY < -20) {
                    if(diffX > 20) {
                        tetrisGameLogic.rotateRight(i)
                        rotate = true

                        steppingX = 0f
                        steppingY = 0f
                    }
                    if(diffX < -20) {
                        tetrisGameLogic.rotateLeft(i)
                        rotate = true

                        steppingX = 0f
                        steppingY = 0f
                    }
                }

                if(rotate == false) {
                    if (diffX > 20f) {
                        tetrisGameLogic.moveRight(i)

                        steppingX = 0f
                        steppingY = 0f
                    }
                    if (diffX < -20f) {
                        tetrisGameLogic.moveLeft(i)

                        steppingX = 0f
                        steppingY = 0f
                    }
                    if (diffY > 20f) {
                        tetrisGameLogic.moveDown(i)

                        steppingX = 0f
                        steppingY = 0f
                    }
                }

                floatCurrentX = event.x
                floatCurrentY = event.y
            }
            // }
        }
    }

    fun onBackPressed() {

    }

    fun onStart() {
        if(context == null) {
            Log.d("VIOLET", "Context is null")
        }

    }

    fun onResume() {
        //reCreate()


        //reCreate()

        paused = false
    }

    fun onPause() {
        paused = true
        saveBoardStates()
        //release()
    }

    fun onStop() {

    }

    fun onDestroy() {

    }

    fun loadBoardStates() {
        Log.d("VIOLET", "Resuming the game")
        if(viewmodel.stateOfTheBoard.value != null && viewmodel.stateOfTheBoard.value!!.size > 0) {
            Log.d("VIOLET", "Setting the board")
            tetrisGameLogic.loadExistingGame(viewmodel.stateOfTheBoard.value!!)
        }
        else if(viewmodel.stateOfTheBoard.value == null) {
            Log.d("VIOLET", "Viewmodel is null")
        }
    }

    fun saveBoardStates() {
        // Save board
        Log.d("VIOLET", "Saving board state")

        if(viewmodel != null) {
            tetrisGameLogic.removeActiveGroupFromBoard()
            viewmodel.setTetrisStateBoard(tetrisGameLogic.getBoardInstances())
            //tetrisGameLogic.applyActiveGroupFromBoard()
        }
        else {
            Log.d("VIOLET", "Could not save board, viewmodel is null")
        }
    }



    //private fun initializeBoard() {
    //    instance.position = Vector3(0f, 0f, -5f)
    //}

    //private fun setupInstancesAndAssets() {
    //    for(resource in resourceMap) {
    //        layer3dSimple3D.addResource(resource.value)
    //    }
//
    //    layer3dSimple3D.addResource(textureId)
    //    layer3dSimple3D.addInstance(textureId, instance)
    //}

    private fun onResourceChangeInstnace(instanceFrom: GroupCell, instanceTo: GroupCell) {
        //var resourceId = resourceMap[instanceFrom.letter]!!
        //layer3dSimple3D.addInstance(resourceId, instanceTo)
        //layer3dSimple3D.removeInstance(resourceId, instanceFrom)
    }

    private fun onAddedInstance(instance: GroupCell) {
        //var resourceId = resourceMap[instance.letter]!!
        //layer3dSimple3D.addInstance(resourceId, instance)

        //Log.d("VIOLET", "Adding new instance to resource $resourceId, letter ${instance.letter} pos ${instance.position.x} ${instance.position.y}")
    }

    private fun onRemovedInstance(instance: GroupCell) {
        // Log.d("VIOLET", "Removing instance, letter ${instance.letter} pos ${instance.position.x} ${instance.position.y}")

        //var resourceId = resourceMap[instance.letter]!!
        ////Log.d("VIOLET", "resourceId ${resourceId}")
        //layer3dSimple3D.removeInstance(resourceId, instance)

    }

    private fun onRowFulls(row: Vector<Int>) {
        indicesToRowsForDeletion = row
        gameState =
            GameState.PlayAnimationEraseRow

        var rowStrings = Vector<String>()
        for(index in row) {
            rowStrings.addElement(tetrisGameLogic.getRowString(index))
            var word = tetrisGameLogic.getRowString(index)
            Log.d("VIOLET", "Adding word: $word")


        }

        CoroutineScope(Dispatchers.IO).launch {
            //delay(2000)
            val derp = "derp"
            withContext(Dispatchers.Main) {
                synchronized(viewmodel) {
                    var words = ""
                    var calculatedPoints = 0
                    for(word in rowStrings) {
                        if(word != null) {
                            var foundWords = lexiconHandler.findWords(word)
                            allWords.addAll(foundWords)
                            words += word

                            for ((index, value) in foundWords.withIndex()) {
                                /// TODO: Apply some kind of awesome bonus for multiple words!
                                calculatedPoints += value.length
                            }

                        }
                    }
                    viewmodel.setScore("Score: $calculatedPoints")
//
                    //wordList += "tralala"
                }
            }
        }
    }

    private fun onLose() {
        Log.d("VIOLET", "Lost the game tetris board size: ${tetrisGameLogic.getBoardInstances().size}")
        //gameState = GameState.GameEnded
        tetrisGameLogic.createNewGame(10, 16)
    }

    private fun onBlockMove(cell: GroupCell) {
        if(cell.empty == true) {
            //spawnFadingBlock(cell.position)
        }
    }

    override fun onDraw() {
        if(initialized == false) {
            return
        }
        pipelineSimple3D.beginFrame()
        quadMesh.bind()

        shadeHeaderInstance

        pipelineSimple3D.setTexture(textureShadeHeaderId)

        var (mv, mvp) = shadeHeaderInstance.getViewProjMatrices(camera)
        pipelineSimple3D.setModelViewMatrix(mv)
        pipelineSimple3D.setModelViewProjMatrix(mvp)
        quadMesh.draw()

        for(rows in tetrisGameLogic.getBoardInstances()) {
            for(cell in rows) {
                if(cell.empty == false) {
                    var resourceId = 0

                    try {
                        resourceId = resourceMap[cell.letter]!!
                    }
                    catch(e: NullPointerException) {
                        Log.d("VIOLET", "Crashed trying to get resource from id: ${cell.letter}")
                    }

                    pipelineSimple3D.setTexture(resourceId)
                    var offset = Vector3()
                    offset.x = cell.offset.x
                    offset.y = cell.offset.y
                    offset.z = cell.offset.z + 0.1f

                    var (mv, mvp) = cell.getViewProjMatrices(camera, offset)
                    pipelineSimple3D.setModelViewMatrix(mv)
                    pipelineSimple3D.setModelViewProjMatrix(mvp)
                    quadMesh.draw()

                    if(gameState == GameState.PlayingAnimationCollapseRow) {
                        pipelineSimple3D.setTexture(textureFadeBlockId)
                        var offset = Vector3()
                        offset.x = cell.offset.x
                        offset.y = cell.offset.y + 0.2f
                        offset.z = cell.offset.z + 0.09f

                        var (mv, mvp) = cell.getViewProjMatrices(camera, offset)
                        pipelineSimple3D.setModelViewMatrix(mv)
                        pipelineSimple3D.setModelViewProjMatrix(mvp)
                        quadMesh.draw()
                    }
                }

                var offset = Vector3()
                //offset.x = cell.offset.x
                //offset.y = cell.offset.y
                offset.z = cell.offset.z + 0f

                pipelineSimple3D.setTexture(textureId)
                var (mv, mvp) = cell.getViewProjMatrices(camera, offset)
                pipelineSimple3D.setModelViewMatrix(mv)
                pipelineSimple3D.setModelViewProjMatrix(mvp)
                quadMesh.draw()

            }
        }

        particleSystem.draw(camera)



        //         quadMesh.draw()


        // for(resource in resourceMap) {
        //     pipelineSimple3D.setTexture(resource.key)
//
        //     for(instance in resource.value) {
        //         var (mv, mvp) = instance.getViewProjMatrices(camera)
        //         pipelineSimple3D.setModelViewMatrix(mv)
        //         pipelineSimple3D.setModelViewProjMatrix(mvp)
        //         quadMesh.draw()
        //     }
        // }

        //layer3dSimple3D.draw()
    }

    override fun onUpdate(deltaTime: Float) {
        if(initialized == false) {
            return
        }
        //handleInput()
        handleDebugInput()
        var nrOfRows = tetrisGameLogic.nrOfRows()
        var nrOfColumns = tetrisGameLogic.nrOfColumns()

        if(gameState == GameState.OnGoing) {
            if (fallDown >= fallDownTreshhold) {
                fallDown = 0.0f
                tetrisGameLogic.updateMovement()
            }

            fallDown += deltaTime
        }
        else if(gameState == GameState.PlayAnimationEraseRow) {
            if(paused) {
                // Special handling, wrap it up faster!
                for(rowIndex in nrOfRows-1 downTo 0) {
                    for(columnIndex in 0 until nrOfColumns) {
                        if(indicesToRowsForDeletion.contains(rowIndex)) {
                            var boardCell = tetrisGameLogic.getCell(columnIndex, rowIndex)
                            //Log.d("VIOLET", "Blowing up block at: " + explosionAt)
                            //Log.d("VIOLET", "Timne lerp: " + removeRowAnimation.timeLerp())
                            // Here is the instance removed from layer!
                            if (boardCell.empty == false) {
                                //removeInstance(boardCell.letter, boardCell)
                                //addEmitterExplode(boardCell.position)
                                boardCell.letter = "_"
                                boardCell.empty = true


                            }
                        }
                    }
                }

                for(index in indicesToRowsForDeletion) {
                    var row = tetrisGameLogic.getRow(index)
                    for(cell in row) {
                        //onRemovedInstance(cell)
                        cell.offset.y = 0.0f
                    }

                    Log.d("VIOLET", "Removing row at index $index")
                    tetrisGameLogic.decreaseRowsAbove(index)
                }
            }
            else {
                animationTrackerEraseRow.update(deltaTime)
                var lerp = animationTrackerEraseRow.timeLerp()

                for (rowIndex in nrOfRows - 1 downTo 0) {
                    for (columnIndex in 0 until nrOfColumns) {
                        if (indicesToRowsForDeletion.contains(rowIndex)) {
                            var explosionAt = (lerp * nrOfColumns).toInt()

                            if (columnIndex <= explosionAt + 1) {
                                var boardCell = tetrisGameLogic.getCell(columnIndex, rowIndex)
                                //Log.d("VIOLET", "Blowing up block at: " + explosionAt)
                                //Log.d("VIOLET", "Timne lerp: " + removeRowAnimation.timeLerp())
                                // Here is the instance removed from layer!
                                if (boardCell.empty == false) {
                                    //removeInstance(boardCell.letter, boardCell)
                                    //addEmitterExplode(boardCell.position)
                                    boardCell.letter = "_"
                                    boardCell.empty = true

                                    spawnFadingFallingBlock(boardCell.position)

                                    //spawnExplosion(boardCell.position)
                                }
                            }
                        }
                    }
                }
            }
        }
        else if(gameState == GameState.PlayingAnimationCollapseRow) {
            animationTrackerCollapseRows.update(deltaTime)
            var offsetY = 0f
            var nrOfRowsToBeDeleted = 0

            // A little confusion center is happening here.
            // Work with grid, not model layer instances directly.

            // From bottom up rows
            /// TODO: Test this: In theory it probably works! :D
            for(rowIndex in nrOfRows-1 downTo 0) {
                if(indicesToRowsForDeletion.contains(rowIndex)) {
                    nrOfRowsToBeDeleted++
                    offsetY = animationTrackerCollapseRows.timeLerp() * nrOfRowsToBeDeleted.toFloat()
                }

                for(columnIndex in 0 until nrOfColumns) {
                    var boardCell = tetrisGameLogic.getCell(columnIndex, rowIndex)
                    boardCell.offset.y = -offsetY
                    //Log.d("VIOLET", "Offset: " + offsetY)
                }
            }
        }
        else if(gameState == GameState.Paused) {

        }
        else if(gameState == GameState.GameEnded) {

        }
        else {

        }

        particleSystem.update(deltaTime)
    }
}