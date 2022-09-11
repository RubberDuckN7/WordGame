package com.distantlandgames.violet

import android.util.Log
import android.view.MotionEvent
import android.view.View
import java.util.ArrayList

class KeyEvent {
    var type: Int = 0
    var keyCode: Int = 0
    var keyChar: Char = ' '

    override fun toString(): String {
        val builder = StringBuilder()
        if (type == KEY_DOWN)
            builder.append("key down, ")
        else
            builder.append("key up, ")
        builder.append(keyCode)
        builder.append(",")
        builder.append(keyChar)
        return builder.toString()
    }

    companion object {
        val KEY_DOWN = 0
        val KEY_UP = 1
    }
}

class TouchEvent {
    var type: Int = 0
    var x: Float = 0f
    var y: Float = 0f
    var index: Int = 0

    companion object {
        val TOUCH_DOWN = 0
        val TOUCH_UP = 1
        val TOUCH_DRAGGED = 2
    }
}

class TouchState {
    var touch: Vector2
    var isTouched: Boolean
    var touchIndex: Int

    init {
        touch = Vector2()
        isTouched = false
        touchIndex = -1
    }
}

class InputHandler : View.OnTouchListener {
    val MAX_TOUCHPOINTS = 10
    var touchStates: ArrayList<TouchState>
    var touches: MutableList<TouchEvent> = ArrayList()

    var scaleX: Float = 0.toFloat()
    var scaleY: Float = 0.toFloat()

    init {
        touchStates = arrayListOf<TouchState>()
        for(i in 0 .. MAX_TOUCHPOINTS) {
            touchStates.add(TouchState())
        }
    }

    var onEventCatched: (touchEvent: TouchEvent) -> Unit = { touchEvent ->  }

    fun applyToView(view: View, scaleX: Float, scaleY: Float) {
        view.setOnTouchListener(this)
        this.scaleX = scaleX
        this.scaleY = scaleY
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        // This will need to be synchronized due to, adding to the same list
        // of events, that will be then added/removed to/from.
        synchronized(this) {
            val action = event.getAction() and MotionEvent.ACTION_MASK
            val pointerIndex =
                event.getAction() and MotionEvent.ACTION_POINTER_ID_MASK shr MotionEvent.ACTION_POINTER_ID_SHIFT
            val pointerCount = event.getPointerCount()
            var touchEvent: TouchEvent

            for (i in 0..MAX_TOUCHPOINTS) {
                // Nr of touches is less that passed iteration, rest are then not valid.
                if (i >= pointerCount) {
                    touchStates[i].isTouched = false
                    touchStates[i].touchIndex = -1
                    continue
                }

                val pointerId = event.getPointerId(i)


                if (event.action != MotionEvent.ACTION_MOVE && i != pointerIndex) {
                    // if it's an up/down/cancel/out event, mask the id to see if we should process it for this touch
                    // point
                    continue
                }

                when (action) {
                    MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                        /// TODO: This needs to be replaced with some kind of pool handling.
                        var touchEvent = TouchEvent()

                        // Event itself that is to be processed and added to queue
                        touchEvent.type = TouchEvent.TOUCH_DOWN
                        touchEvent.index = pointerId

                        // Update static array with state values.
                        touchStates[i].touch.x = (event.getX(i) * scaleX)
                        touchEvent.x = touchStates[i].touch.x

                        touchStates[i].touch.y = (event.getY(i) * scaleY)
                        touchEvent.y = touchStates[i].touch.y

                        touchStates[i].isTouched = true
                        touchStates[i].touchIndex = pointerId

                        onEventCatched(touchEvent)
                    }

                    MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_CANCEL -> {
                        /// TODO: This needs to be replaced with some kind of pool handling.
                        var touchEvent = TouchEvent()

                        // Event itself that is to be processed and added to queue
                        touchEvent.type = TouchEvent.TOUCH_UP
                        touchEvent.index = pointerId

                        // Update static array with state values.
                        touchStates[i].touch.x = (event.getX(i) * scaleX)
                        touchEvent.x = touchStates[i].touch.x

                        touchStates[i].touch.y = (event.getY(i) * scaleY)
                        touchEvent.y = touchStates[i].touch.y

                        touchStates[i].isTouched = false
                        touchStates[i].touchIndex = -1

                        onEventCatched(touchEvent)
                    }

                    MotionEvent.ACTION_MOVE -> {
                        /// TODO: This needs to be replaced with some kind of pool handling.
                        var touchEvent = TouchEvent()

                        // Event itself that is to be processed and added to queue
                        touchEvent.type = TouchEvent.TOUCH_DRAGGED
                        touchEvent.index = pointerId

                        // Update static array with state values.
                        touchStates[i].touch.x = (event.getX(i) * scaleX)
                        touchEvent.x = touchStates[i].touch.x

                        touchStates[i].touch.y = (event.getY(i) * scaleY)
                        touchEvent.y = touchStates[i].touch.y

                        touchStates[i].isTouched = true
                        touchStates[i].touchIndex = pointerId

                        onEventCatched(touchEvent)
                    }
                }
            }
            return true
        }
    }
    fun getTouchEvents(): List<TouchEvent> {
        //synchronized(this) {
            if(touches.size > 0)
                Log.d("VIOLET", "Returning touches size ${touches.size}")
            return touches
        //}
    }
}

class InputHandlerDepr : View.OnTouchListener {
    private val MAX_TOUCHPOINTS = 10

    @Volatile
    internal var touchStates: ArrayList<TouchState>

    @Volatile
    internal var touchEvents: MutableList<TouchEvent> = ArrayList()

    @Volatile
    internal var touchEventsBuffer: MutableList<TouchEvent> = ArrayList()

    internal var scaleX: Float = 0.toFloat()
    internal var scaleY: Float = 0.toFloat()

    var debugEvents: MutableList<TouchEvent> = ArrayList()

    init {
        touchStates = arrayListOf<TouchState>()
        for(i in 0 .. MAX_TOUCHPOINTS) {
            touchStates.add(TouchState())
        }
    }

    fun applyToView(view: View, scaleX: Float, scaleY: Float) {
        view.setOnTouchListener(this)
        this.scaleX = scaleX
        this.scaleY = scaleY
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        // This will need to be synchronized due to, adding to the same list
        // of events, that will be then added/removed to/from.
        synchronized(this) {
            val action = event.getAction() and MotionEvent.ACTION_MASK
            val pointerIndex = event.getAction() and MotionEvent.ACTION_POINTER_ID_MASK shr MotionEvent.ACTION_POINTER_ID_SHIFT
            val pointerCount = event.getPointerCount()
            var touchEvent: TouchEvent

            for(i in 0 .. MAX_TOUCHPOINTS) {
                // Nr of touches is less that passed iteration, rest are then not valid.
                if (i >= pointerCount) {
                    touchStates[i].isTouched = false
                    touchStates[i].touchIndex = -1
                    continue
                }

                val pointerId = event.getPointerId(i)
                if (event.action != MotionEvent.ACTION_MOVE && i != pointerIndex) {
                    // if it's an up/down/cancel/out event, mask the id to see if we should process it for this touch
                    // point
                    continue
                }

                when (action) {
                    MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                        /// TODO: This needs to be replaced with some kind of pool handling.
                        var touchEvent = TouchEvent()

                        // Event itself that is to be processed and added to queue
                        touchEvent.type = TouchEvent.TOUCH_DOWN
                        touchEvent.index = pointerId

                        // Update static array with state values.
                        touchStates[i].touch.x = (event.getX(i) * scaleX)
                        touchEvent.x = touchStates[i].touch.x

                        touchStates[i].touch.y = (event.getY(i) * scaleY)
                        touchEvent.y = touchStates[i].touch.y

                        touchStates[i].isTouched = true
                        touchStates[i].touchIndex = pointerId

                        touchEventsBuffer.add(touchEvent)
                    }

                    MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_CANCEL -> {
                        /// TODO: This needs to be replaced with some kind of pool handling.
                        var touchEvent = TouchEvent()

                        // Event itself that is to be processed and added to queue
                        touchEvent.type = TouchEvent.TOUCH_UP
                        touchEvent.index = pointerId

                        // Update static array with state values.
                        touchStates[i].touch.x = (event.getX(i) * scaleX)
                        touchEvent.x = touchStates[i].touch.x

                        touchStates[i].touch.y = (event.getY(i) * scaleY)
                        touchEvent.y = touchStates[i].touch.y

                        touchStates[i].isTouched = false
                        touchStates[i].touchIndex = -1

                        touchEventsBuffer.add(touchEvent)
                    }

                    MotionEvent.ACTION_MOVE -> {
                        /// TODO: This needs to be replaced with some kind of pool handling.
                        var touchEvent = TouchEvent()

                        // Event itself that is to be processed and added to queue
                        touchEvent.type = TouchEvent.TOUCH_DRAGGED
                        touchEvent.index = pointerId

                        // Update static array with state values.
                        touchStates[i].touch.x = (event.getX(i) * scaleX)
                        touchEvent.x = touchStates[i].touch.x

                        touchStates[i].touch.y = (event.getY(i) * scaleY)
                        touchEvent.y = touchStates[i].touch.y

                        touchStates[i].isTouched = true
                        touchStates[i].touchIndex = pointerId

                        touchEventsBuffer.add(touchEvent)
                    }
                }
            }

            Log.d("VIOLET", "Event buffer: ${touchEventsBuffer.size}")
            debugEvents.addAll(touchEventsBuffer)
            return true
        }
    }

    fun isTouchDown(pointer: Int): Boolean {
        synchronized(this) {
            val index = getIndex(pointer)
            return if (index < 0 || index >= MAX_TOUCHPOINTS)
                false
            else
                touchStates[index].isTouched
        }
    }

    fun getTouchX(pointer: Int): Float {
        synchronized(this) {
            val index = getIndex(pointer)
            return if (index < 0 || index >= MAX_TOUCHPOINTS)
                0f
            else
                touchStates[index].touch.x
        }
    }

    fun getTouchY(pointer: Int): Float {
        synchronized(this) {
            val index = getIndex(pointer)
            return if (index < 0 || index >= MAX_TOUCHPOINTS)
                0f
            else
                touchStates[index].touch.y
        }
    }

    fun getTouchEvents(): List<TouchEvent> {
        Log.d("VIOLET", "Before synchronizing Event buffer: ${touchEventsBuffer.size}")
        synchronized(this) {
            val len = touchEvents.size
            //for (i in 0 until len)
            //
            //    touchEventPool.free(touchEvents[i])

            //touchEvents.clear()
            touchEvents.addAll(touchEventsBuffer)
            //touchEventsBuffer.clear()


            Log.d("VIOLET", "Returning Event buffer: ${touchEventsBuffer.size}")
            Log.d("VIOLET", "touchEvents: ${debugEvents.size}")
            return debugEvents
        }
    }

    // returns the index for a given pointerId or -1 if no index.
    private fun getIndex(pointerId: Int): Int {
        for (i in 0 until MAX_TOUCHPOINTS) {
            if (touchStates[i].touchIndex == pointerId) {
                return i
            }
        }
        return -1
    }
}

class InputEventState {
    var processInput = false

    var isLeft = false
    var isRight = false
    var isDown = false
    var isUp = false
}

class GestureHandler {
    enum class Directions {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }

    private var currentStates: InputEventState = InputEventState()

    private var internalOnPressed: (posX: Float, posY: Float) -> Unit = { posX, posY -> }
    private var internalOnMoved: (posX: Float, posY: Float, toX: Float, toY: Float, direction: Directions, currentState: InputEventState) -> Unit = {  posX, posY, toX, toY, direction, currentState ->  }
    private var internalOnReleased: (posX: Float, posY: Float, currentState: InputEventState) -> Unit = {  posX, posY, currentState -> }

    fun setOnPressed(onPressedCb: (posX: Float, posY: Float) -> Unit) {
        internalOnPressed = onPressedCb
    }

    fun setOnMoved(onMovedCb: (posX: Float, posY: Float, toX: Float, toY: Float, direction: Directions, currentState: InputEventState) -> Unit) {
        internalOnMoved = onMovedCb
    }

    fun setOnReleased(onReleasedCb: (posX: Float, posY: Float, currentState: InputEventState) -> Unit) {
        internalOnReleased = onReleasedCb
    }

    fun onTouched(fromX: Float, fromY: Float) {
        internalOnPressed(fromX, fromY)
    }

    // Input handling
    fun onTouchMoved(fromX: Float, fromY: Float, toX: Float, toY: Float) {
        var direction = Directions.TOP_LEFT

        var margin: Float = 25.0f

        var horizontalDiff = Math.abs(fromX - toX)
        var verticalDiff = Math.abs(fromY - toY)

        if (horizontalDiff > margin) {
            if (fromX > toX) {
                currentStates.isLeft = true
            } else {
                currentStates.isRight = true
            }
        }
        if (verticalDiff > margin) {
            if (fromY < toY) {
                currentStates.isDown = true
            } else {
                currentStates.isUp = true
            }
        }

        if(currentStates.isUp && currentStates.isLeft) {
            direction = Directions.TOP_LEFT
        }
        else if(currentStates.isUp && currentStates.isRight) {
            direction = Directions.TOP_RIGHT
        }
        else if(currentStates.isDown && currentStates.isLeft) {
            direction = Directions.BOTTOM_LEFT
        }
        else if(currentStates.isDown && currentStates.isRight) {
            direction = Directions.BOTTOM_RIGHT
        }

        internalOnMoved(fromX, fromY, toX, toY, direction, currentStates)
    }

    fun onReleased(fromX: Float, fromY: Float) {
        internalOnReleased(fromX, fromY, currentStates)

        currentStates.isUp = false
        currentStates.isDown = false
        currentStates.isRight = false
        currentStates.isLeft = false
    }
}