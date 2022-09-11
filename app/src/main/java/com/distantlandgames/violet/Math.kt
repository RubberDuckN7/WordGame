package com.distantlandgames.violet

import android.opengl.Matrix

class Vector2(var x: Float = 0f, var y: Float = 0f)

class Vector3(var x: Float = 0f, var y: Float = 0f, var z: Float = 0f)

class Matrix4x4 {
    val data: FloatArray = FloatArray(16)

    fun identity() = Matrix.setIdentityM(data, 0)

    fun scale(offset: Int, scaleX: Float, scaleY: Float, scaleZ: Float) = Matrix.scaleM(data, offset, scaleX, scaleY, scaleZ)

    fun scale(offset: Int, scale: Vector3) = Matrix.scaleM(data, offset, scale.x, scale.y, scale.z)

    fun rotate(offset: Int, a: Float, x: Float, y: Float, z: Float) = Matrix.rotateM(data, offset, a, x, y, z)

    fun translate(offset: Int, x: Float, y: Float, z: Float) = Matrix.translateM(data, offset, x, y, z)

    fun copyTo(copyTo: Matrix4x4) = System.arraycopy(data, 0, copyTo.toFloatArray(), 0, 16)

    fun multiply(left: Matrix4x4, right: Matrix4x4) = Matrix.multiplyMM(data, 0, left.toFloatArray(), 0, right.toFloatArray(), 0)

    fun toFloatArray() = data
}