package com.distantlandgames.violet.shaders.pipeline

import android.content.Context
import android.opengl.GLES20
import android.util.Log
import com.distantlandgames.violet.Matrix4x4
import com.distantlandgames.violet.Vector2
import com.distantlandgames.violet.Vector3
import com.distantlandgames.violet.mesh.VertexTemplate
import com.distantlandgames.violet.helpers.AttributePackage
import com.distantlandgames.violet.helpers.ShaderHelper
import com.distantlandgames.violet.interfaces.IObject
import com.distantlandgames.violet.mesh.VertexLayout

open abstract class PipelineBase : IObject {
    protected var programHandle: Int = 0
    var vertexAttributes = AttributePackage()
        protected set

    protected fun getAttributeHandlesFromNames(vertexAttributeNames: Array<String>): AttributePackage {
        var attributes = AttributePackage()
        for(name in vertexAttributeNames) {
            attributes.addAttribute(name)
            attributes.data.lastElement().attributeHandle = GLES20.glGetAttribLocation(programHandle, name)
            var interesting = ""
        }
        return attributes
    }

    protected fun getUniformHandlesFromNames(vertexAttributeNames: Array<String>): AttributePackage {
        var attributes = AttributePackage()
        for(name in vertexAttributeNames) {
            attributes.addAttribute(name)
            attributes.data.lastElement().attributeHandle = GLES20.glGetUniformLocation(programHandle,name)
        }

        return attributes
    }

    protected fun compileShader(shaderCode: String, shaderType: Int): Int {
        val shader = GLES20.glCreateShader(shaderType)

        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)

        val compiled = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0)

        if (compiled[0] == 0) {
            Log.e("violet", "Could not compile shader")
            Log.e("violet", "Could not compile shader:" +
                        GLES20.glGetShaderInfoLog(shader))
            Log.e(ShaderHelper.getTag(), "Shader code: " + shaderCode)
            GLES20.glDeleteShader(shader)
            return 0
        }

        return shader
    }

    protected fun linkProgramToShaders(vertexShader: Int, fragmentShader: Int) {
        programHandle = GLES20.glCreateProgram()

        if (programHandle !== 0) {
            // Bind the vertex shader to the program.
            GLES20.glAttachShader(programHandle, vertexShader)

            // Bind the fragment shader to the program.
            GLES20.glAttachShader(programHandle, fragmentShader)

            // Link the two shaders together into a program.
            GLES20.glLinkProgram(programHandle)

            // Get the link status.
            val linkStatus = IntArray(1)
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0)

            // If the link failed, delete the program.
            if (linkStatus[0] == 0) {
                Log.e("VIOLET", "Error compiling program: " + GLES20.glGetProgramInfoLog(programHandle))
                GLES20.glDeleteProgram(programHandle)
                programHandle = 0
            }
        }

        if (programHandle === 0) {
            throw RuntimeException("Error creating program.")
        }
    }

    protected fun setData(handle: Int, data: Float) {
        GLES20.glUniform1f(handle, data)
    }

    protected fun setData(handle: Int, data: Vector2) {
        GLES20.glUniform2f(handle, data.x, data.y)
    }

    protected fun setData(handle: Int, data: Vector3) {
        GLES20.glUniform3f(handle, data.x, data.y, data.z)
    }

    protected fun setData(glHandle: Int, textureDataHandle: Int) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0) // For now, hardcode to 0
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureDataHandle)
        GLES20.glUniform1i(glHandle, 0)
    }

    /// TODO: Have this accepting Texture, as data, rather than Int. This is in case there will be a need to set an Int.
    protected fun setData(handle: Int, matrix: Matrix4x4) {
        GLES20.glUniformMatrix4fv(handle, 1, false, matrix.toFloatArray(), 0)
    }

    open fun beginFrame() {
        GLES20.glUseProgram(programHandle)
    }

    fun release() {
        GLES20.glDeleteProgram(programHandle)
    }

    abstract fun initialize(context: Context)

    abstract fun getVertexTemplate(): VertexTemplate

    abstract fun getVertexLayout(): VertexLayout
}

