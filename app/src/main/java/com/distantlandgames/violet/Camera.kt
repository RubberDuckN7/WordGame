package com.distantlandgames.violet

import android.opengl.Matrix

class Camera(var width: Int = 0, var height: Int = 0) {
    private var view: Matrix4x4
    private var proj: Matrix4x4

    private val viewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)

    fun setBounds(width: Int, height: Int) {
        this.width = width
        this.height = height

        val ratio = width.toFloat() / height.toFloat()
        val left = -ratio
        val bottom = -1.0f
        val top = 1.0f
        val near = 1.0f
        val far = 1000.0f

        Matrix.frustumM(proj.toFloatArray(), 0, left, ratio, bottom, top, near, far)
    }

    fun setLookAt(eyeX: Float, eyeY: Float, eyeZ: Float, lookX: Float, lookY: Float, lookZ: Float, upX: Float, upY: Float, upZ: Float) {
        Matrix.setLookAtM(view.toFloatArray(), 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ)
    }

    init {
        val  eyeX = 0.0f
        val  eyeY = 1.0f
        val  eyeZ = -1.0f // more is getting back

        val  lookX = 0.0f
        val  lookY = 1.0f
        val  lookZ = -10.0f

        val  upX = 0.0f
        val  upY = 1.0f
        val  upZ = 0.0f

        view = Matrix4x4()
        proj = Matrix4x4()

        Matrix.setLookAtM(view.toFloatArray(), 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ)
    }

    fun getViewMatrix() = view
    fun getProjectionMatrix() = proj
}