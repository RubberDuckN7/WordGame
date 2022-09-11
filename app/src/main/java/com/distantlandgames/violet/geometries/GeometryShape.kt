package com.distantlandgames.violet.geometries

import com.distantlandgames.violet.helpers.GeometryHelperDeprecated
import com.distantlandgames.violet.mesh.VertexLayout
import java.util.*

//import com.distantlandgames.violet.VertexLayout

enum class VeetexAttributeProperty {
    POSITION_V4,
    POSITION_V3,
    NORMAL_V3,
    TEXTURE_V2
}

abstract class GeometryShape {
    protected abstract fun getSquareIndexedPosV3(): FloatArray

    protected abstract fun getSquareIndexedPosV4(scale: Float): FloatArray

    protected abstract fun getSquareIndexedNormal(): FloatArray

    protected abstract fun getSquareIndexedUv(): FloatArray

    abstract fun getIndices(): ShortArray

    private fun getStreamsByVertexLayout(vertexLayout: VertexLayout, scale: Float = 1.0f): Vector<FloatArray> {
        var allStreams: Vector<FloatArray> = Vector<FloatArray>()

        for(element in vertexLayout.elements) {
            when(element.dataPurpose) {
                VertexLayout.DataPurpose.POSITION -> {
                    allStreams.add(GeometryHelperDeprecated.getSquareIndexedPosV4(scale))
                }
                VertexLayout.DataPurpose.NORMAL -> {
                    allStreams.add(GeometryHelperDeprecated.getSquareIndexedNormal())
                }

                VertexLayout.DataPurpose.TEXTURE_UV -> {
                    allStreams.add(GeometryHelperDeprecated.getSquareIndexedUv())
                }

                VertexLayout.DataPurpose.OTHER -> {

                }
            }
        }

        return allStreams
    }

    fun constructStreams(vertexLayout: VertexLayout, scale: Float): FloatArray {
        var streams = getStreamsByVertexLayout(vertexLayout, scale)
        var nrOfVertices: Int = streams.get(0).count() / vertexLayout.elements.firstElement().getNrOfFloats() //All should have same count
        var packedStreams: Vector<Float> = Vector<Float>()

        for (vertexIndex in 0 until nrOfVertices) {
            for((streamIndex, stream) in streams.withIndex()) {
                var nrOfElements: Int = vertexLayout.elements.get(streamIndex).getNrOfFloats()
                var vertexOffset: Int = vertexIndex * nrOfElements
                for(elementIndex in 0 until nrOfElements) {
                    packedStreams.addElement(stream.get(vertexOffset + elementIndex))
                }
            }
        }

        return packedStreams.toFloatArray()
    }
}