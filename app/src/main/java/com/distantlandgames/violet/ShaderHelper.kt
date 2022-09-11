package com.distantlandgames.violet.helpers

import android.opengl.GLES20
import android.util.Log
import com.distantlandgames.violet.Matrix4x4
import com.distantlandgames.violet.Vector2
import com.distantlandgames.violet.Vector3
import com.distantlandgames.violet.mesh.VertexTemplate
import java.util.*


/*class VertexTemplate {
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
}*/

class ShaderAttribute(var attributeName: String = "", var attributeHandle: Int = 0)

open class AttributePackage {
    var data: Vector<ShaderAttribute> = Vector()

    fun addAttribute(name: String) = data.add(ShaderAttribute(name))
    fun nrOfAttributes() = data.count()
    fun attributeAt(index: Int) = data.get(index)
    fun clearAttributes() = data.clear()
}

// This can be the base for all instances.
// Since these attributes are relevant for almost every project. Even 2D.
open class InstanceData {
    var position: Vector3
    var rotationAxis: Vector3
    var scale: Vector3
    var rotationAngle: Float

    init {
        position = Vector3()
        rotationAxis = Vector3(0f, 1f, 0f)
        scale = Vector3(1f, 1f, 1f)
        rotationAngle = 0f
    }
}

class ShaderHelper {
    companion object {
        fun getTag() = "VIOLET"

        fun getRawVertexShader3DNormalUv() = "" +
                "uniform mat4 u_MVPMatrix;\t\t// A constant representing the combined model/view/projection matrix.      \t\t       \n" +
                "uniform mat4 u_MVMatrix;\t\t// A constant representing the combined model/view matrix.       \t\t\n" +
                "\t\t  \t\t\t\n" +
                "attribute vec4 a_Position;\t\t// Per-vertex position information we will pass in.   \t\t\t\t\t\t\t\n" +
                "attribute vec3 a_Normal;\t\t// Per-vertex normal information we will pass in.      \n" +
                "attribute vec2 a_TexCoordinate; // Per-vertex texture coordinate information we will pass in. \t\t\n" +
                "\t\t  \n" +
                "varying vec3 v_Position;\t\t// This will be passed into the fragment shader.       \t\t          \t\t\n" +
                "varying vec3 v_Normal;\t\t\t// This will be passed into the fragment shader.  \n" +
                "varying vec2 v_TexCoordinate;   // This will be passed into the fragment shader.    \t\t\n" +
                "\t\t  \n" +
                "// The entry point for our vertex shader.  \n" +
                "void main()                                                 \t\n" +
                "{                                                         \n" +
                "\t// Transform the vertex into eye space. \t\n" +
                "\tv_Position = vec3(u_MVMatrix * a_Position);            \t\t\n" +
                "\t\n" +
                "\t// Pass through the texture coordinate.\n" +
                "\tv_TexCoordinate = a_TexCoordinate;                                      \n" +
                "\t\n" +
                "\t// Transform the normal's orientation into eye space.\n" +
                "    v_Normal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));\n" +
                "          \n" +
                "\t// gl_Position is a special variable used to store the final position.\n" +
                "\t// Multiply the vertex by the matrix to get the final point in normalized screen coordinates.\n" +
                "\tgl_Position = u_MVPMatrix * a_Position;                       \t\t  \n" +
                "}                                                          "

        fun getRawPixelShader3DNormalUv() = "" +
                "precision mediump float;       \t// Set the default precision to medium. We don't need as high of a \n" +
                "\t\t\t\t\t\t\t\t// precision in the fragment shader.\n" +
                "uniform vec3 u_LightPos;       \t// The position of the light in eye space.\n" +
                "uniform sampler2D u_Texture;    // The input texture.\n" +
                "  \n" +
                "varying vec3 v_Position;\t\t// Interpolated position for this fragment.\n" +
                "varying vec3 v_Normal;         \t// Interpolated normal for this fragment.\n" +
                "varying vec2 v_TexCoordinate;   // Interpolated texture coordinate per fragment.\n" +
                "  \n" +
                "// The entry point for our fragment shader.\n" +
                "void main()                    \t\t\n" +
                "{                              \n" +
                "\t// Will be used for attenuation.\n" +
                "    float distance = length(u_LightPos - v_Position);                  \n" +
                "\t\n" +
                "\t// Get a lighting direction vector from the light to the vertex.\n" +
                "    vec3 lightVector = normalize(u_LightPos - v_Position);              \t\n" +
                "\n" +
                "\t// Calculate the dot product of the light vector and vertex normal. If the normal and light vector are\n" +
                "\t// pointing in the same direction then it will get max illumination.\n" +
                "    float diffuse = max(dot(v_Normal, lightVector), 0.0);               \t  \t\t  \t\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                "\n" +
                "\t// Add attenuation. \n" +
                "    diffuse = diffuse * (1.0 / (1.0 + (0.25 * distance)));\n" +
                "    \n" +
                "    // Add ambient lighting\n" +
                "    diffuse = diffuse + 0.7;  \n" +
                "\n" +
                "\t// Multiply the color by the diffuse illumination level and texture value to get final output color.\n" +
                "    gl_FragColor = (texture2D(u_Texture, v_TexCoordinate));                                  \t\t\n" +
                "    if(gl_FragColor.a < 0.5)\n" +
                "       discard;\n" +
                "    gl_FragColor.rgb *= vec3(1.0, 1.0, 1.0);                                  \t\t\n" +
                "}"

        /// TODO: Add scaling for particle vertex!
        fun getRawVertexShader3DNormalUvParticle() = "" +
                "uniform mat4 u_MVPMatrix;\t\t// A constant representing the combined model/view/projection matrix.      \t\t       \n" +
                "uniform mat4 u_MVMatrix;\t\t// A constant representing the combined model/view matrix.       \t\t\n" +
                "\t\t  \t\t\t\n" +
                "attribute vec4 a_Position;\t\t// Per-vertex position information we will pass in.   \t\t\t\t\t\t\t\n" +
                "attribute vec3 a_Normal;\t\t// Per-vertex normal information we will pass in.      \n" +
                "attribute vec2 a_TexCoordinate; // Per-vertex texture coordinate information we will pass in. \t\t\n" +
                "\t\t  \n" +
                "varying vec3 v_Position;\t\t// This will be passed into the fragment shader.       \t\t          \t\t\n" +
                "varying vec3 v_Normal;\t\t\t// This will be passed into the fragment shader.  \n" +
                "varying vec2 v_TexCoordinate;   // This will be passed into the fragment shader.    \t\t\n" +
                "\t\t  \n" +
                "// The entry point for our vertex shader.  \n" +
                "void main()                                                 \t\n" +
                "{                                                         \n" +
                "\t// Transform the vertex into eye space. \t\n" +
                "\tv_Position = vec3(u_MVMatrix * a_Position);            \t\t\n" +
                "\t\n" +
                "\t// Pass through the texture coordinate.\n" +
                "\tv_TexCoordinate = a_TexCoordinate;                                      \n" +
                "\t\n" +
                "\t// Transform the normal's orientation into eye space.\n" +
                "    v_Normal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));\n" +
                "          \n" +
                "\t// gl_Position is a special variable used to store the final position.\n" +
                "\t// Multiply the vertex by the matrix to get the final point in normalized screen coordinates.\n" +
                "\tgl_Position = u_MVPMatrix * a_Position;                       \t\t  \n" +
                "}                                                          "

        /// TODO: Add alpha fading !
        fun getRawPixelShader3DNormalUvParticle() = "" +
                "precision mediump float;       \t// Set the default precision to medium. We don't need as high of a \n" +
                "\t\t\t\t\t\t\t\t// precision in the fragment shader.\n" +
                "uniform vec3 u_LightPos;       \t// The position of the light in eye space.\n" +
                "uniform sampler2D u_Texture;    // The input texture.\n" +
                "uniform float u_Fade;\n" +
                "  \n" +
                "varying vec3 v_Position;\t\t// Interpolated position for this fragment.\n" +
                "varying vec3 v_Normal;         \t// Interpolated normal for this fragment.\n" +
                "varying vec2 v_TexCoordinate;   // Interpolated texture coordinate per fragment.\n" +
                "  \n" +
                "// The entry point for our fragment shader.\n" +
                "void main()                    \t\t\n" +
                "{                              \n" +
                "\t// Will be used for attenuation.\n" +
                "    float distance = length(u_LightPos - v_Position);                  \n" +
                "\t\n" +
                "\t// Get a lighting direction vector from the light to the vertex.\n" +
                "    vec3 lightVector = normalize(u_LightPos - v_Position);              \t\n" +
                "\n" +
                "\t// Calculate the dot product of the light vector and vertex normal. If the normal and light vector are\n" +
                "\t// pointing in the same direction then it will get max illumination.\n" +
                "    float diffuse = max(dot(v_Normal, lightVector), 0.0);               \t  \t\t  \t\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                "\n" +
                "\t// Add attenuation. \n" +
                "    diffuse = diffuse * (1.0 / (1.0 + (0.25 * distance)));\n" +
                "    \n" +
                "    // Add ambient lighting\n" +
                "    diffuse = diffuse + 0.7;  \n" +
                "\n" +
                "\t// Multiply the color by the diffuse illumination level and texture value to get final output color.\n" +
                "    gl_FragColor = (texture2D(u_Texture, v_TexCoordinate));                                  \t\t\n" +
                "    gl_FragColor.rgb *= diffuse;                                  \t\t\n" +
                "    gl_FragColor.a = u_Fade;\n" +
                "}"

        fun setData(handle: Int, data: Float) {
            GLES20.glUniform1f(handle, data)
        }

        fun setData(handle: Int, data: Vector2) {
            GLES20.glUniform2f(handle, data.x, data.y)
        }

        fun setData(handle: Int, data: Vector3) {
            GLES20.glUniform3f(handle, data.x, data.y, data.z)
        }

        fun setData(glHandle: Int, textureDataHandle: Int) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0) // For now, hardcode to 0
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureDataHandle)
            GLES20.glUniform1i(glHandle, 0)
        }

        /// TODO: Have this accepting Texture, as data, rather than Int. This is in case there will be a need to set an Int.
        fun setData(handle: Int, matrix: Matrix4x4) {
            GLES20.glUniformMatrix4fv(handle, 1, false, matrix.toFloatArray(), 0)
        }

        /// TODO: This for now will be totally separated, but it does have common functionality, maybe combine it?
        fun get3DUpdaterMVP(): (attributePackage: AttributePackage, mvp: Matrix4x4) -> Unit {
            return { attributePackage, mvp ->
                setData(attributePackage.data[0].attributeHandle, mvp)
            }
        }

        fun get3DUpdaterMV(): (attributePackage: AttributePackage, mvp: Matrix4x4) -> Unit {
            return { attributePackage, mv ->
                setData(attributePackage.data[1].attributeHandle, mv)
            }
        }

        // This is implementation specifics for shaders.
        fun getLightPos3DUpdaterLightPos(): (attributePackage: AttributePackage, lightPos: Vector3) -> Unit {
            return { attributePackage, lightPos ->
                // Also the point is, don't send handle.
                // Send the whole attribute package, since it knows the INDEX of what attribute it is.
                setData(attributePackage.data[2].attributeHandle, lightPos)
            }
        }

        fun get3DUpdaterTexture(): (attributePackage: AttributePackage, texture: Int) -> Unit {
            return { attributePackage, texture ->
                setData(attributePackage.data[3].attributeHandle, texture)
            }
        }

        /// Particles!
        fun get3DParticleUpdaterMVP(): (attributePackage: AttributePackage, mvp: Matrix4x4) -> Unit {
            return { attributePackage, mvp ->
                setData(attributePackage.data[0].attributeHandle, mvp)
            }
        }

        fun get3DParticleUpdaterMV(): (attributePackage: AttributePackage, mvp: Matrix4x4) -> Unit {
            return { attributePackage, mv ->
                setData(attributePackage.data[1].attributeHandle, mv)
            }
        }

        // This is implementation specifics for shaders.
        fun getLightPos3DParticleUpdaterLightPos(): (attributePackage: AttributePackage, lightPos: Vector3) -> Unit {
            return { attributePackage, lightPos ->
                // Also the point is, don't send handle.
                // Send the whole attribute package, since it knows the INDEX of what attribute it is.
                setData(attributePackage.data[2].attributeHandle, lightPos)
            }
        }

        fun get3DParticleUpdaterTexture(): (attributePackage: AttributePackage, texture: Int) -> Unit {
            return { attributePackage, texture ->
                setData(attributePackage.data[3].attributeHandle, texture)
            }
        }

        fun get3DParticleUpdaterFading(): (attributePackage: AttributePackage, alpha: Float) -> Unit {
            return { attributePackage, alpha ->
                setData(attributePackage.data[4].attributeHandle, alpha)
            }
        }

        /**
         * @description 0 - MVP, 1 - MV, 2 - LightPos, 3 - Texture
         */
        fun getAllUniformsNormalTexture(): AttributePackage {
            var attributePackage: AttributePackage = AttributePackage()

            attributePackage.addAttribute("u_MVPMatrix") // Instance
            attributePackage.addAttribute("u_MVMatrix") // Instance
            attributePackage.addAttribute("u_LightPos") // Frame
            attributePackage.addAttribute("u_Texture") // Per model / texture

            return attributePackage
        }

        fun getAllAtributesNormalTexture(): AttributePackage {
            var attributePackage: AttributePackage = AttributePackage()

            attributePackage.addAttribute("a_Position")
            attributePackage.addAttribute("a_Normal")
            attributePackage.addAttribute("a_TexCoordinate")

            return attributePackage
        }

        /**
         * @description Pos (V4), Normal (V3), UV (V2)
         */
        fun getVTNormalTexture(): VertexTemplate {
            var vertexTemplate: VertexTemplate =
                VertexTemplate()

            vertexTemplate.addElemenet(VertexTemplate.DataType.VECTOR4)
            vertexTemplate.addElemenet(VertexTemplate.DataType.VECTOR3)
            vertexTemplate.addElemenet(VertexTemplate.DataType.VECTOR2)

            return vertexTemplate
        }

        /**
         * @description 0 - MVP, 1 - MV, 2 - LightPos, 3 - Texture
         */
        fun getAllUniformsNormalTextureParticle(): AttributePackage {
            var attributePackage: AttributePackage = AttributePackage()

            attributePackage.addAttribute("u_MVPMatrix") // Instance
            attributePackage.addAttribute("u_MVMatrix") // Instance
            attributePackage.addAttribute("u_LightPos") // Frame
            attributePackage.addAttribute("u_Texture") // Per model / texture
            attributePackage.addAttribute("u_Fade") // Per whatever

            return attributePackage
        }

        fun getAllAtributesNormalTextureParticle(): AttributePackage {
            var attributePackage: AttributePackage = AttributePackage()

            attributePackage.addAttribute("a_Position")
            attributePackage.addAttribute("a_Normal")
            attributePackage.addAttribute("a_TexCoordinate")

            return attributePackage
        }

        /**
         * @description Pos (V4), Normal (V3), UV (V2)
         */
        fun getVTNormalTextureParticle(): VertexTemplate {
            var vertexTemplate: VertexTemplate =
                VertexTemplate()

            vertexTemplate.addElemenet(VertexTemplate.DataType.VECTOR4)
            vertexTemplate.addElemenet(VertexTemplate.DataType.VECTOR3)
            vertexTemplate.addElemenet(VertexTemplate.DataType.VECTOR2)

            return vertexTemplate
        }


        /// Here starts the common functionality.
        fun getVertexType() = GLES20.GL_VERTEX_SHADER

        fun getFragmentType() = GLES20.GL_FRAGMENT_SHADER

        /**
         * @param shaderType GLES20.GL_VERTEX_SHADER or GLES20.GL_FRAGMENT_SHADER
         * @param shaderCode Shader code as string, can be vertex os fragment.
         */
        fun loadShader(shaderCode: String, shaderType: Int): Int {
            val shader = GLES20.glCreateShader(shaderType)

            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)

            val compiled = IntArray(1)
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0)

            if (compiled[0] == 0) {
                Log.e(getTag(), "Could not compile shader")
                Log.e(getTag(), "Could not compile shader:" +
                        GLES20.glGetShaderInfoLog(shader))
                Log.e(getTag(), "Shader code: " + shaderCode)
                GLES20.glDeleteShader(shader)
                return 0
            }

            return shader
        }

        fun createPipeline(vertexShader: String, fragmentShader: String): Int {
            var compiledVertexShader = loadShader(vertexShader, GLES20.GL_VERTEX_SHADER)
            var compiledFragmentShader = loadShader(fragmentShader, GLES20.GL_FRAGMENT_SHADER)

            if(compiledVertexShader == 0 || compiledFragmentShader == 0) {
                return 0
            }

            return createPipeline(compiledVertexShader, compiledFragmentShader)
        }

        fun createPipeline(vertexShader: Int, fragmentShader: Int): Int {
            val program = GLES20.glCreateProgram()
            if(program == 0) {
                return 0
            }

            GLES20.glAttachShader(program, vertexShader)
            GLES20.glAttachShader(program, fragmentShader)

            GLES20.glLinkProgram(program);

            val linked = IntArray(1)
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linked, 0)
            if (linked[0] == 0) {
                Log.e(getTag(), "Could not link program")
                Log.v(getTag(), "Could not link program:" + GLES20.glGetProgramInfoLog(program))
                GLES20.glDeleteProgram(program)
                return 0
            }
            return program
        }
    }
}

class ShaderPipeline(var programHandle: Int = 0) {
    fun updateAttributeLocation(attributes: AttributePackage) {
        for(attribute in attributes.data) {
            attribute.attributeHandle = GLES20.glGetAttribLocation(programHandle, attribute.attributeName)
        }
    }

    fun updateUniformLocation(attributes: AttributePackage) {
        for(attribute in attributes.data) {
            attribute.attributeHandle = GLES20.glGetUniformLocation(programHandle, attribute.attributeName)
        }
    }

    /// TODO: Readup on why this needs actual DATA handle from texture, and how many calls does it really needs.
    fun generateAndBindTexture(textureDataHandle: Int) {
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D)

        /// TODO: Something is fishy here....
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureDataHandle)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureDataHandle)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR)
    }

    fun bindTexture(textureDataHandle: Int, textureHandle: Int, slot: Int) {
        // Pass in the texture information
        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0) // For now, hardcode to 0

        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureDataHandle)

        // Tell the texture uniform sampler to use this texture in the
        // shader by binding to texture unit 0.
        GLES20.glUniform1i(textureHandle, 0)
    }

    /***
     * @description Call this only after created everything
     */
    fun create(vertexShader: Int, fragmentShader: Int, attributes: AttributePackage) {
        programHandle = GLES20.glCreateProgram()

        if (programHandle !== 0) {
            // Bind the vertex shader to the program.
            GLES20.glAttachShader(programHandle, vertexShader)

            // Bind the fragment shader to the program.
            GLES20.glAttachShader(programHandle, fragmentShader)

            //for((index, attribute) in attributes.getAttributes().withIndex()) {
            for((index, attribute) in attributes.data.withIndex()) {
                //GLES20.glBindAttribLocation(programHandle, index, attribute.attributeName)
            }

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

    fun frameBegin() {
        GLES20.glUseProgram(programHandle)
    }

    fun release() {
        GLES20.glDeleteProgram(programHandle)
    }
}