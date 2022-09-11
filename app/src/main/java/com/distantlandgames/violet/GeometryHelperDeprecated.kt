package com.distantlandgames.violet.helpers

import com.distantlandgames.violet.Vector3
import com.distantlandgames.violet.mesh.VertexLayout
import com.distantlandgames.violet.mesh.VertexTemplate
import java.util.*

/**
 * Created by Shade on 01/05/2018.
 */
class GeometryHelperDeprecated {
    companion object {
        fun <T : InstanceData>placeAsPlane(plane: Vector<Vector<T>>,
                                           nrOfColumns: Int,
                                           nrOfRows: Int,
                                           blockSize: Float, offset: Vector3 = Vector3()
        ) {

            var startPosX: Float = (blockSize*nrOfColumns.toFloat()) * -0.5f + blockSize * 0.5f
            var startPosY: Float = (blockSize*nrOfRows.toFloat()) * 0.5f + blockSize * 0.5f

            for(r in 0 until nrOfRows) {
                for (c in 0 until nrOfColumns) {
                    var planeBit = plane[r][c]

                    planeBit.position =
                        Vector3(
                            startPosX + offset.x,
                            startPosY + offset.y, offset.z
                        )

                    planeBit.rotationAxis =
                        Vector3(0f, 1f, 1f)
                    planeBit.rotationAngle = 0f

                    startPosX += blockSize
                }

                startPosX = (blockSize*nrOfColumns.toFloat()) * -0.5f + blockSize * 0.5f
                startPosY -= blockSize
            }
        }

        // Square
        fun getSquareIndexedPosV3() = floatArrayOf(
            -0.5f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            0.5f, 0.5f, 0.0f)

        fun getSquareIndexedPosV4(scale: Float) = floatArrayOf(
            scale * -0.5f, scale *  0.5f, scale * 0.0f, 1f,
            scale * -0.5f, scale * -0.5f, scale * 0.0f, 1f,
            scale * 0.5f,  scale * -0.5f, scale * 0.0f, 1f,
            scale * 0.5f,  scale *  0.5f, scale * 0.0f, 1f)

        fun getSquareIndexedNormal() = floatArrayOf(
            -0.5f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            0.5f, 0.5f, 0.0f)

        fun getSquareIndexedUv() = floatArrayOf(
            0f, 0f,
            0f, 1f,
            1f, 1f,
            1f, 0f)

        fun constructFromStreams(streams: Vector<FloatArray>, vertexTemplate: VertexTemplate): FloatArray {
            var nrOfVertices: Int = streams.get(0).count() / vertexTemplate.getElement(0).nrOfFloatElements //All should have same count
            var allStreams: Vector<Float> = Vector<Float>()

            for (vertexIndex in 0 until nrOfVertices) {
                for((streamIndex, stream) in streams.withIndex()) {
                    var nrOfElements: Int = vertexTemplate.getElement(streamIndex).nrOfFloatElements
                    var offset: Int = vertexIndex * nrOfElements
                    for(e in 0 until nrOfElements) {
                        allStreams.addElement(stream.get(offset+e))
                    }
                }
            }

            return allStreams.toFloatArray()
        }

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

        fun constructFromStreams(vertexLayout: VertexLayout, scale: Float): FloatArray {
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

        fun constructFromStreams(streams: Vector<FloatArray>, vertexLayout: VertexLayout): FloatArray {
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

        fun getSquareIndices() = shortArrayOf(0, 1, 2, 0, 2, 3)

        // Triangle
        fun getTrianglePos() = floatArrayOf(
            0.0f, 0.622008459f, 0.0f,
            -0.5f, -0.311004243f, 0.0f,
            0.5f, -0.311004243f, 0.0f)

        fun getCoordPerVertex() = 3
    }
}