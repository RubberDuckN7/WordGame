package com.distantlandgames.violet.mesh

import android.opengl.GLES20
import android.util.Log
import com.distantlandgames.violet.helpers.AttributePackage
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*

class VertexLayout {
    enum class BytesInDataType(val bytes: Int) {
        BYTES_PER_FLOAT(4),
        BYTES_PER_SHORT(2),
    }

    enum class DataType(val nrOfFloats: Int) {
        VECTOR2(2),
        VECTOR3(3),
        VECTOR4(4),
    }

    enum class DataPurpose {
        TEXTURE_UV,
        NORMAL,
        TANGENT,
        POSITION,
        OTHER,
    }

    class ElementType(var type: DataType = DataType.VECTOR2, var dataPurpose: DataPurpose = DataPurpose.OTHER) {
        fun getNrOfBytes() = type.nrOfFloats * BytesInDataType.BYTES_PER_FLOAT.bytes
        fun getNrOfFloats() = type.nrOfFloats
    }

    var elements: Vector<ElementType> = Vector()

    fun addElement(type: DataType, purpose: DataPurpose) {
        elements.add(ElementType(type, purpose))
    }

}

class MeshPart {
    lateinit var vertexLayout: VertexLayout
    val vertexBuffer = IntArray(1)
    val indexBuffer = IntArray(1)
    lateinit var attributePackage: AttributePackage
    var indexCount = 0
    var stride = 0

    fun create(vertices: FloatArray, indices: ShortArray, vertexLayout: VertexLayout, attributePackage: AttributePackage) {
        this.vertexLayout = vertexLayout
        this.attributePackage = attributePackage
        indexCount = indices.count()

        for(element in vertexLayout.elements) {
            stride += element.getNrOfBytes()
        }

        //stride *= VertexTemplate.BYTES_PER_FLOAT()

        // The usual step, same for everyone.
        val vertexDataBuffer = ByteBuffer
            .allocateDirect(vertices.count() * VertexLayout.BytesInDataType.BYTES_PER_FLOAT.bytes).order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexDataBuffer.put(vertices).position(0)

        val indexDataBuffer = ByteBuffer
            .allocateDirect(indices.count() * VertexLayout.BytesInDataType.BYTES_PER_SHORT.bytes).order(ByteOrder.nativeOrder())
            .asShortBuffer()
        indexDataBuffer.put(indices).position(0)

        // Real buffers.
        GLES20.glGenBuffers(1, vertexBuffer, 0)
        GLES20.glGenBuffers(1, indexBuffer, 0)

        if (vertexBuffer[0] > 0 && indexBuffer[0] > 0) {
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBuffer[0])
            GLES20.glBufferData(
                GLES20.GL_ARRAY_BUFFER, vertexDataBuffer.capacity() * VertexTemplate.BYTES_PER_FLOAT(),
                vertexDataBuffer, GLES20.GL_STATIC_DRAW)

            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBuffer[0])
            GLES20.glBufferData(
                GLES20.GL_ELEMENT_ARRAY_BUFFER, indexDataBuffer.capacity()
                        * VertexTemplate.BYTES_PER_SHORT(), indexDataBuffer, GLES20.GL_STATIC_DRAW)

            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0)
        } else {
            Log.d("VIOLET", "Could not create/generate buffers!")
        }
    }

    fun release() {
        if (vertexBuffer[0] > 0) {
            GLES20.glDeleteBuffers(vertexBuffer.count(), vertexBuffer, 0)
            vertexBuffer[0] = 0
        }

        if (indexBuffer[0] > 0) {
            GLES20.glDeleteBuffers(indexBuffer.count(), indexBuffer, 0)
            indexBuffer[0] = 0
        }
    }

    fun bind() {
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBuffer[0])
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBuffer[0])

        var offset: Int = 0
        for((index, e) in attributePackage!!.data.withIndex()) {
            GLES20.glVertexAttribPointer(e.attributeHandle,
                vertexLayout!!.elements[index].getNrOfFloats(),
                GLES20.GL_FLOAT,
                false,
                stride, // constant size
                offset) // Step one vertex, in byte size

            offset +=  vertexLayout!!.elements[index].getNrOfBytes()
            GLES20.glEnableVertexAttribArray(e.attributeHandle)
        }
    }

    fun unbind() {
        // Unbind buffers
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    fun draw() {
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, indexCount, GLES20.GL_UNSIGNED_SHORT, 0)
    }
}

class Model {

}