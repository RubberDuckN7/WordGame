package com.distantlandgames.violet.geometries

//import com.distantlandgames.violet.VertexLayout

class QuadShape : GeometryShape() {
    override fun getSquareIndexedPosV3() = floatArrayOf(
        -0.5f, 0.5f, 0.0f,
        -0.5f, -0.5f, 0.0f,
        0.5f, -0.5f, 0.0f,
        0.5f, 0.5f, 0.0f)

    override fun getSquareIndexedPosV4(scale: Float) = floatArrayOf(
        scale * -0.5f, scale *  0.5f, scale * 0.0f, 1f,
        scale * -0.5f, scale * -0.5f, scale * 0.0f, 1f,
        scale * 0.5f,  scale * -0.5f, scale * 0.0f, 1f,
        scale * 0.5f,  scale *  0.5f, scale * 0.0f, 1f)

    override fun getSquareIndexedNormal() = floatArrayOf(
        -0.5f, 0.5f, 0.0f,
        -0.5f, -0.5f, 0.0f,
        0.5f, -0.5f, 0.0f,
        0.5f, 0.5f, 0.0f)

    override fun getSquareIndexedUv() = floatArrayOf(
        0f, 0f,
        0f, 1f,
        1f, 1f,
        1f, 0f)

    override fun getIndices() = shortArrayOf(0, 1, 2, 0, 2, 3)
}