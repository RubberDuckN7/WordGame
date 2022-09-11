package com.distantlandgames.violet.mesh

import android.opengl.GLES20
import android.util.Log
import com.distantlandgames.violet.mesh.VertexTemplate.Companion.BYTES_PER_FLOAT
import com.distantlandgames.violet.mesh.VertexTemplate.Companion.BYTES_PER_SHORT
import com.distantlandgames.violet.helpers.AttributePackage
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*

class VertexTemplate {
    companion object {
        fun BYTES_PER_FLOAT() = 4
        fun BYTES_PER_SHORT() = 2
    }

    enum class DataType(val nrOfFloatElements: Int) {
        VECTOR2(2), VECTOR3(3), VECTOR4(4), NONE(0)
    }

    class Element(var type: DataType = DataType.NONE, var nrOfFloatElements: Int = 0) {
        fun getSizeInBytes() = nrOfFloatElements * BYTES_PER_FLOAT()
    }

    private var elements: Vector<Element> = Vector<Element>()
    private var nrOfElements: Int = 0
    private var stride: Int = 0

    fun getElementsPerVertex() = nrOfElements
    fun getStride() = stride
    fun getElements() = elements

    fun addElemenet(type: DataType) {
        elements.add(
            Element(
                type,
                type.nrOfFloatElements
            )
        )
        nrOfElements += type.nrOfFloatElements
    }

    fun getElement(index: Int): Element {
        return elements.get(index)
    }

    fun nrOfElements(): Int {
        return elements.count()
    }

    fun clearElements() {
        elements.clear()
    }
}

class IndexedMesh {
    var vertexTemplate: VertexTemplate? = null
    var vertexLayout: VertexLayout? = null
    var attributePackage: AttributePackage? = null
    val vertexBuffer = IntArray(1)
    val indexBuffer = IntArray(1)
    var indexCount: Int
    var stride: Int

    init {
        indexCount = 0
        stride = 0
    }

    fun create(vertices: Array<Float>, indices: Array<Short>, vertexTemplate: VertexTemplate, attributePackage: AttributePackage) {
        create(vertices.toFloatArray(), indices.toShortArray(), vertexTemplate, attributePackage)
    }

    fun create(vertices: FloatArray, indices: ShortArray, vertexTemplate: VertexTemplate, attributePackage: AttributePackage) {
        this.vertexTemplate = vertexTemplate
        this.attributePackage = attributePackage
        indexCount = indices.count()

        for(element in vertexTemplate.getElements()) {
            stride += element.nrOfFloatElements
        }

        stride *= BYTES_PER_FLOAT()

        // The usual step, same for everyone.
        val vertexDataBuffer = ByteBuffer
            .allocateDirect(vertices.count() * BYTES_PER_FLOAT()).order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexDataBuffer.put(vertices).position(0)

        val indexDataBuffer = ByteBuffer
            .allocateDirect(indices.count() * BYTES_PER_SHORT()).order(ByteOrder.nativeOrder())
            .asShortBuffer()
        indexDataBuffer.put(indices).position(0)

        // Real buffers.
        GLES20.glGenBuffers(1, vertexBuffer, 0)
        GLES20.glGenBuffers(1, indexBuffer, 0)

        if (vertexBuffer[0] > 0 && indexBuffer[0] > 0) {
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBuffer[0])
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexDataBuffer.capacity() * BYTES_PER_FLOAT(),
                vertexDataBuffer, GLES20.GL_STATIC_DRAW)

            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBuffer[0])
            GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexDataBuffer.capacity()
                    * BYTES_PER_SHORT(), indexDataBuffer, GLES20.GL_STATIC_DRAW)

            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0)
        } else {
            Log.d("VIOLET", "Could not create/generate buffers!")
        }
    }

    fun create(vertices: FloatArray, indices: ShortArray, vertexLayout: VertexLayout, attributePackage: AttributePackage) {
        this.vertexLayout = vertexLayout
        this.attributePackage = attributePackage
        indexCount = indices.count()

        for(element in vertexLayout.elements) {
            stride += element.getNrOfBytes()
        }

        stride *= BYTES_PER_FLOAT()

        // The usual step, same for everyone.
        val vertexDataBuffer = ByteBuffer
            .allocateDirect(vertices.count() * BYTES_PER_FLOAT()).order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexDataBuffer.put(vertices).position(0)

        val indexDataBuffer = ByteBuffer
            .allocateDirect(indices.count() * BYTES_PER_SHORT()).order(ByteOrder.nativeOrder())
            .asShortBuffer()
        indexDataBuffer.put(indices).position(0)

        // Real buffers.
        GLES20.glGenBuffers(1, vertexBuffer, 0)
        GLES20.glGenBuffers(1, indexBuffer, 0)

        if (vertexBuffer[0] > 0 && indexBuffer[0] > 0) {
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBuffer[0])
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexDataBuffer.capacity() * BYTES_PER_FLOAT(),
                vertexDataBuffer, GLES20.GL_STATIC_DRAW)

            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBuffer[0])
            GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexDataBuffer.capacity()
                    * BYTES_PER_SHORT(), indexDataBuffer, GLES20.GL_STATIC_DRAW)

            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0)
        } else {
            Log.d("VIOLET", "Could not create/generate buffers!")
        }
    }

    fun bind() {
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBuffer[0])
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBuffer[0])

        var offset: Int = 0
        for((index, e) in attributePackage!!.data.withIndex()) {
            GLES20.glVertexAttribPointer(e.attributeHandle,
                vertexTemplate!!.getElement(index).nrOfFloatElements,
                GLES20.GL_FLOAT,
                false,
                stride, // constant size
                offset) // Step one vertex, in byte size

            offset +=  vertexTemplate!!.getElement(index).getSizeInBytes()
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

    fun drawLegacy() {
        bind()
        draw()
        unbind()
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
}

/**
 * Created by Shade on 02/06/2018.
 */
/*
class VertexLayout {
    companion object {
        fun BYTES_PER_FLOAT() = 4
        fun BYTES_PER_SHORT() = 2
    }

    enum class ByteValues(val bytesPerData: Int) {
        BYTES_PER_FLOAT(4),
        BYTES_PER_SHORT(2),
    }

    enum class DataType(val nrOfFloatElements: Int) {
        VECTOR2(2),
        VECTOR3(3),
        VECTOR4(4),
    }

    enum class DataPurpose() {
        POSITION_V4,
        POSITION_V3,
        NORMAL_V3,
        TANGENT_V3,
        TEXTURE_COORDINATES_V3,
        TEXTURE_COORDINATES_V2
    }

    class Element(var type: DataType, var purpose: DataPurpose) {
        fun getSizeInBytes() = type.nrOfFloatElements * ByteValues.BYTES_PER_FLOAT.bytesPerData
    }

    var elements: Vector<Element> = Vector()

    fun getNrOfElements() = elements.size

    var stride: Int = 0
        private set

    fun addElement(type: DataType, purpose: DataPurpose) {
        elements.add(Element(type, purpose))
        //totalNrOfElements += type.nrOfFloatElements
    }
}

class VertexTemplate {
    companion object {
        fun BYTES_PER_FLOAT() = 4
        fun BYTES_PER_SHORT() = 2
    }

    enum class DataType(val nrOfFloatElements: Int) {
        VECTOR2(2), VECTOR3(3), VECTOR4(4), NONE(0)
    }

    class Element(var type: DataType = DataType.NONE, var nrOfFloatElements: Int = 0) {
        fun getSizeInBytes() = nrOfFloatElements * BYTES_PER_FLOAT()
    }

    private var elements: Vector<Element> = Vector<Element>()
    private var nrOfElements: Int = 0
    private var stride: Int = 0

    fun getElementsPerVertex() = nrOfElements
    fun getStride() = stride
    fun getElements() = elements

    fun addElemenet(type: DataType) {
        elements.add(Element(type, type.nrOfFloatElements))
        nrOfElements += type.nrOfFloatElements
    }

    fun getElement(index: Int): Element {
        return elements.get(index)
    }

    fun nrOfElements(): Int {
        return elements.count()
    }

    fun clearElements() {
        elements.clear()
    }
}

class IndexedMesh {
    //var vertexTemplate: VertexTemplate? = null
    var vertexLayout: VertexLayout? = null
    var vertexTemplate: VertexTemplate? = null
    var attributePackage: AttributePackage? = null
    val vertexBuffer = IntArray(1)
    val indexBuffer = IntArray(1)
    var indexCount: Int
    var stride: Int

    init {
        indexCount = 0
        stride = 0
    }

    fun create(vertices: Array<Float>, indices: Array<Short>, vertexLayout: VertexTemplate, attributePackage: AttributePackage) {
        create(vertices.toFloatArray(), indices.toShortArray(), vertexLayout, attributePackage)
    }

    fun create(vertices: FloatArray, indices: ShortArray, vertexTemplate: VertexTemplate, attributePackage: AttributePackage) {
        this.vertexTemplate = vertexTemplate
        this.attributePackage = attributePackage
        indexCount = indices.count()

        for(element in vertexTemplate.getElements()) {
            stride += element.getSizeInBytes()
        }

        stride *= BYTES_PER_FLOAT()

        // The usual step, same for everyone.
        val vertexDataBuffer = ByteBuffer
            .allocateDirect(vertices.count() * BYTES_PER_FLOAT()).order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexDataBuffer.put(vertices).position(0)

        val indexDataBuffer = ByteBuffer
            .allocateDirect(indices.count() * BYTES_PER_SHORT()).order(ByteOrder.nativeOrder())
            .asShortBuffer()
        indexDataBuffer.put(indices).position(0)

        // Real buffers.
        GLES20.glGenBuffers(1, vertexBuffer, 0)
        GLES20.glGenBuffers(1, indexBuffer, 0)

        if (vertexBuffer[0] > 0 && indexBuffer[0] > 0) {
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBuffer[0])
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexDataBuffer.capacity() * BYTES_PER_FLOAT(),
                vertexDataBuffer, GLES20.GL_STATIC_DRAW)

            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBuffer[0])
            GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexDataBuffer.capacity()
                    * BYTES_PER_SHORT(), indexDataBuffer, GLES20.GL_STATIC_DRAW)

            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0)
        } else {
            Log.d("VIOLET", "Could not create/generate buffers!")
        }
    }

    fun bind() {
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBuffer[0])

        var offset: Int = 0
        for((index, e) in attributePackage!!.data.withIndex()) {
            GLES20.glVertexAttribPointer(e.attributeHandle,
                vertexTemplate!!.getElement(index).nrOfFloatElements,
                GLES20.GL_FLOAT,
                false,
                stride,
                offset) // Step one vertex, in byte size

            offset +=  vertexTemplate!!.getElement(index).getSizeInBytes()
            GLES20.glEnableVertexAttribArray(e.attributeHandle)
        }

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBuffer[0])
    }

    fun unbind() {
        // Unbind buffers
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    fun draw() {
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, indexCount, GLES20.GL_UNSIGNED_SHORT, 0)
    }

    fun drawLegacy() {
        bind()
        draw()
        unbind()
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
}*/