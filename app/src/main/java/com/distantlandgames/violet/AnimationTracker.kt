package com.distantlandgames.violet

class AnimationTracker {
    private var timeStart = 0f
    private var timeEnd = 0f
    private var timeCounter = 0f

    fun timeLerp() = (timeCounter / timeEnd)

    fun setDuration(end: Float) {
        timeStart = 0f
        timeEnd = end
        timeCounter = 0f
    }

    fun reset() {
        timeStart = 0f
        timeCounter = 0f
    }

    fun update(dt: Float): Boolean {
        timeCounter += dt
        if(timeCounter > timeEnd) {
            reset()
            onTimeReached()
            return false
        }
        return true
    }

    var onTimeReached: () -> Unit = {  }
}