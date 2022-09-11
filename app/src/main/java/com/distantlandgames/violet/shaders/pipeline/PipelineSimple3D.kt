package com.distantlandgames.violet.shaders.pipeline

import android.content.Context
import android.opengl.GLES20
import com.distantlandgames.tetrisandwords.R
import com.distantlandgames.violet.Matrix4x4
import com.distantlandgames.violet.Vector3
//import com.distantlandgames.violet.VertexLayout
import com.distantlandgames.violet.mesh.VertexTemplate
import com.distantlandgames.violet.helpers.AssetHelper
import com.distantlandgames.violet.helpers.AttributePackage
import com.distantlandgames.violet.mesh.VertexLayout


class PipelineSimple3D : PipelineBase() {
    private var textureHandle = AttributePackage()
    private var lightHandle = AttributePackage()
    private var worldMatricesHandle = AttributePackage()

    override fun initialize(context: Context) {
        // 1: Get shader source
        var vertexShaderSource = AssetHelper.readTextFileFromRawResource(context, R.raw.vertex_shader_simple)
        var fragmentShaderSource = AssetHelper.readTextFileFromRawResource(context, R.raw.fragment_shader_simple)

        // 2: Compile shaders
        var compiledVertexShader = compileShader(vertexShaderSource, GLES20.GL_VERTEX_SHADER)
        var compiledFragmentShader = compileShader(fragmentShaderSource, GLES20.GL_FRAGMENT_SHADER)

        // 3: Link shaders to program
        linkProgramToShaders(compiledVertexShader, compiledFragmentShader)

        // 4: Get attribute locations from shaders after the program is linked
        vertexAttributes = getAttributeHandlesFromNames(arrayOf("a_Position", "a_Normal", "a_TexCoordinate"))
        worldMatricesHandle = getUniformHandlesFromNames(arrayOf("u_MVPMatrix", "u_MVMatrix")) // Instance
        lightHandle = getAttributeHandlesFromNames(arrayOf("u_LightPos")) // Frame
        textureHandle = getAttributeHandlesFromNames(arrayOf("u_Texture")) // Per model / texture
    }

    /**
     * @description Pos (V4), Normal (V3), UV (V2)
     */

    override fun getVertexTemplate(): VertexTemplate {
        var vertexTemplate: VertexTemplate =
            VertexTemplate()

        vertexTemplate.addElemenet(VertexTemplate.DataType.VECTOR4)
        vertexTemplate.addElemenet(VertexTemplate.DataType.VECTOR3)
        vertexTemplate.addElemenet(VertexTemplate.DataType.VECTOR2)

        return vertexTemplate
    }

    override fun getVertexLayout(): VertexLayout {
        var vertexLayout: VertexLayout = VertexLayout()

        vertexLayout.addElement(VertexLayout.DataType.VECTOR4, VertexLayout.DataPurpose.POSITION)
        vertexLayout.addElement(VertexLayout.DataType.VECTOR3, VertexLayout.DataPurpose.NORMAL)
        vertexLayout.addElement(VertexLayout.DataType.VECTOR2, VertexLayout.DataPurpose.TEXTURE_UV)

        return vertexLayout
    }

   /* override fun getVertexLayout(): VertexLayout {
        var vertexLayout = VertexLayout()

        vertexLayout.addElement(VertexLayout.DataType.VECTOR4, VertexLayout.DataPurpose.POSITION_V4)
        vertexLayout.addElement(VertexLayout.DataType.VECTOR3, VertexLayout.DataPurpose.NORMAL_V3)
        vertexLayout.addElement(VertexLayout.DataType.VECTOR2, VertexLayout.DataPurpose.TEXTURE_COORDINATES_V2)

        return vertexLayout
    }*/

    fun setModelViewProjMatrix(mvp: Matrix4x4) {
        setData(worldMatricesHandle.data[0].attributeHandle, mvp)
    }

    fun setModelViewMatrix(mvp: Matrix4x4) {
        setData(worldMatricesHandle.data[1].attributeHandle, mvp)
    }

    fun setLightPos(pos: Vector3) {
        setData(lightHandle.data[0].attributeHandle, pos)
    }

    fun setTexture(textureObjectHandle: Int) {
        setData(textureHandle.data[0].attributeHandle, textureObjectHandle)
    }
}