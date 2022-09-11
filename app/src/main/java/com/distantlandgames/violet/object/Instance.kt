package com.distantlandgames.violet.`object`

import com.distantlandgames.violet.Camera
import com.distantlandgames.violet.Matrix4x4
import com.distantlandgames.violet.Vector3

open class Instance3D {
    var position: Vector3
    var rotationAxis: Vector3
    var scale: Vector3
    var rotationAngle: Float

    var modelViewProjection: Matrix4x4
    var modelView: Matrix4x4

    init {
        position = Vector3()
        rotationAxis = Vector3(0f, 1f, 0f)
        scale = Vector3(1f, 1f, 1f)
        rotationAngle = 0f

        modelViewProjection = Matrix4x4()
        modelView = Matrix4x4()
    }

    fun getViewProjMatrices(camera: Camera, offset: Vector3 = Vector3()): Pair<Matrix4x4, Matrix4x4> {
        var mvp = Matrix4x4()
        var mv = Matrix4x4()

        var scaleMatrix = Matrix4x4()
        scaleMatrix.identity()
        scaleMatrix.scale(0, scale.x, scale.y, scale.z)

        var rotationMatrix = Matrix4x4()
        rotationMatrix.identity()
        rotationMatrix.rotate(0, rotationAngle,
            rotationAxis.x,
            rotationAxis.y,
            rotationAxis.z)

        var world = Matrix4x4()
        world.identity()
        world.translate(0, position.x + offset.x,
            position.y + offset.y,
            position.z + offset.z)

        var temp = Matrix4x4()
        temp.identity()

        var temp2 = Matrix4x4()
        temp2.identity()

        temp.multiply(world, rotationMatrix)
        temp2.multiply(temp, scaleMatrix)

        //temp.multiply(rotationMatrix, scaleMatrix)
        //rotationMatrix.multiply(temp, world)

        mv.multiply(camera.getViewMatrix(), temp2)
        mvp.multiply(camera.getProjectionMatrix(), mv)

        var result = Pair(mv, mvp)
        return result
    }

    fun getViewProjMatricesSave(camera: Camera, offset: Vector3 = Vector3()): Pair<Matrix4x4, Matrix4x4> {
        var mvp = Matrix4x4()
        var mv = Matrix4x4()

        var scaleMatrix = Matrix4x4()
        scaleMatrix.identity()
        scaleMatrix.scale(0, scale.x, scale.y, scale.z)

        var rotationMatrix = Matrix4x4()
        rotationMatrix.identity()
        rotationMatrix.rotate(0, rotationAngle,
            rotationAxis.x,
            rotationAxis.y,
            rotationAxis.z)

        var world = Matrix4x4()
        world.identity()
        world.translate(0, position.x + offset.x,
            position.y + offset.y,
            position.z + offset.z)

        var temp = Matrix4x4()
        temp.identity()

        temp.multiply(scaleMatrix, rotationMatrix)
        rotationMatrix.multiply(temp, world)
        //temp.multiply(rotation, world)

        mv.multiply(camera.getViewMatrix(), rotationMatrix)
        mvp.multiply(camera.getProjectionMatrix(), mv)

        var result = Pair(mv, mvp)
        return result
    }
}