package com.distantlandgames.violet

import android.content.Context
import com.distantlandgames.violet.`object`.Instance3D
import com.distantlandgames.violet.mesh.MeshPart
import com.distantlandgames.violet.mesh.VertexLayout
import com.distantlandgames.violet.shaders.pipeline.PipelineBase
import java.util.*

abstract class LayerBase {
    protected var resourceMap: HashMap<Int, Vector<Instance3D>> = HashMap()
    protected lateinit var meshPart: MeshPart
    protected lateinit var camera: Camera

    abstract fun initialize(context: Context, meshPart: MeshPart, camera: Camera)
    abstract fun draw()
    abstract fun getVertexLayout(): VertexLayout
    abstract fun release()

    fun addResource(resourceId: Int) {
        resourceMap[resourceId] = Vector<Instance3D>()
    }

    fun removeResource(resourceId: Int) {
        resourceMap.remove(resourceId)
    }

    fun addInstance(resourceId: Int, instance: Instance3D) {
        resourceMap[resourceId]?.addElement(instance)
    }

    fun removeInstance(resourceId: Int, instance: Instance3D) {
        resourceMap[resourceId]?.removeElement(instance)
    }
}