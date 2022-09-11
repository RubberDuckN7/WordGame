package com.distantlandgames.violet

import android.content.Context
import com.distantlandgames.violet.`object`.Instance3D
import com.distantlandgames.violet.mesh.MeshPart
import com.distantlandgames.violet.shaders.pipeline.PipelineBase
import com.distantlandgames.violet.shaders.pipeline.PipelineSimple3D
import java.util.*

class Layer3DSimple : LayerBase() {
    private var pipeline: PipelineSimple3D = PipelineSimple3D()

    override fun initialize(context: Context, meshPart: MeshPart, camera: Camera) {
        pipeline.initialize(context)

        this.meshPart = meshPart
        this.camera = camera
    }

    override fun release() {
        pipeline.release()
        resourceMap.clear()
        meshPart.release()
    }

    override fun draw() {
        pipeline.beginFrame()
        meshPart.bind()

        for(resource in resourceMap) {
            pipeline.setTexture(resource.key)

            for(instance in resource.value) {
                var (mv, mvp) = instance.getViewProjMatrices(camera)
                pipeline.setModelViewMatrix(mv)
                pipeline.setModelViewProjMatrix(mvp)
                meshPart.draw()
            }
        }
    }

    override fun getVertexLayout() = pipeline.getVertexLayout()
}