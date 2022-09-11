package com.distantlandgames.violet

import android.util.Log
import com.distantlandgames.violet.`object`.Instance3D
import com.distantlandgames.violet.helpers.AssetHelper
import com.distantlandgames.violet.helpers.ShaderHelper
import com.distantlandgames.violet.mesh.MeshPart
import com.distantlandgames.violet.shaders.pipeline.PipelineSimpleParticle3D
import java.util.*

class ParticleSystem {
    class Particle : Instance3D() {
        var direction: Vector3
        var speed: Float
        var life: Float
        var alpha: Float

        init {
            position = Vector3(0f, 0f, 0f)
            direction = Vector3(0f, 0f, 0f)
            rotationAxis = Vector3(0f, 1f, 1f)

            rotationAngle = 0f
            speed = 0f
            life = 0f
            alpha = 0f
        }
    }

    class Emitter {
        var particles: Vector<Particle> = Vector()
        var life: Float = 0.0f
        var spawnRate: Float = 0.0f
        var spawnRateLimit: Float = 0.0f
        var spawnRateCount: Float = 0.0f
        var resourceId: Int = -1
        var spawnLimit = 0
        var particleSpawned = 0

        var spawnBehavior: (particle: Particle) -> Unit = {}
        var updateBehavior: (particle: Particle, deltaTime: Float) -> Unit = { particle, deltaTime ->  }

        fun create(spawnPerSecond: Float, life: Float) {
            spawnRateLimit = 1000f / spawnPerSecond
        }

        fun update(deltaTime: Float) {
            //Log.d("VIOLET", "spawnRateCount $spawnRateCount spawnRateLimit $spawnRateLimit deltaTime $deltaTime")
            if(particleSpawned < spawnLimit) {
                if (spawnRateCount >= spawnRateLimit) {
                    var particle = Particle()
                    spawnBehavior(particle)
                    particles.addElement(particle)
                    //Log.d("VIOLET", "Spawning particle!")
                    spawnRateCount -= spawnRateLimit
                    particleSpawned++
                }
            }
//
            if(spawnRateCount < 0f) {
                //spawnRateCount = 0f
            }

            spawnRateCount += deltaTime

            var iterator = particles.iterator()

            //for(particle in particles) {
            //    updateBehavior(particle, deltaTime)
            //}

            while(iterator.hasNext()) {
                var particle = iterator.next()
                updateBehavior(particle, deltaTime)
//
                if(particle.life < 0f) {
                    iterator.remove()
                }
            }

            life -= deltaTime
        }

        fun isDead(): Boolean {
            return life < 0f && particles.size== 0
        }
    }

    lateinit var pipeline: PipelineSimpleParticle3D
    lateinit var particleMesh: MeshPart

    private var emitters: Vector<Emitter> = Vector()

    // Emitter should be customized for: spawn/update of a particle!
    fun addemitter(emitter: Emitter) {
        emitters.addElement(emitter)
    }

    fun update(deltaTime: Float) {
        for(emitter in emitters) {
            //Log.d("VIOLET", "Updating emitter!")
            emitter.update(deltaTime)
        }

        //var iterator = emitters.iterator()
        //for(emitter in emitters) {
        //while(iterator.hasNext()) {
        //    Log.d("VIOLET", "Updating emitter!")
        //    var emitter = iterator.next()
        //    emitter.update(deltaTime)
        //    if(emitter.isDead()) {
        //        //iterator.remove()
        //    }
        //}
    }

    // Drawing should be default
    fun draw(camera: Camera) {
        pipeline.beginFrame()
        for(emitter in emitters) {
            Log.d("VIOLET", "Drawing emitter nrOfParticles ${emitter.particles.size}!")
            for(particle in emitter.particles) {
                pipeline.setAlpha(particle.alpha)

                pipeline.setTexture(emitter.resourceId)

                var (mv, mvp) = particle.getViewProjMatrices(camera)
                pipeline.setModelViewMatrix(mv)
                pipeline.setModelViewProjMatrix(mvp)
                particleMesh.draw()
                Log.d("VIOLET", "Drawing particle! alpha ${particle.alpha}")
            }
        }
    }
}

/*
class ParticleSystem {
    class Particle : Instance3D() {
        var direction: Vector3
        var speed: Float
        var life: Float

        init {
            position = Vector3(0f, 0f, 0f)
            direction = Vector3(0f, 0f, 0f)
            rotationAxis = Vector3(0f, 1f, 1f)
            rotationAngle = 0f
            speed = 0f
            life = 0f
        }
    }

    class Emitter : Instance3D() {
        private var life: Float = 0f
        private var timeCounter: Float = 0f
        private var timeSpawnLimit: Float = 0f
        private var resourceId: Int = -1

        var onSpawnEmitter: () -> Unit = { }
        var onDieEmitter: () -> Unit = { }
        var onLifeDeacrease: (lifeDecreaseBy: Float) -> Unit = { lifeDecreaseBy ->
            life -= lifeDecreaseBy
        }

        var particleUpdateBehaviour: (particle: Particle, dt: Float) -> Unit = { particle, dt -> particle.life -= dt}
        var particleSpawnBehaviour: (particle: Particle, emitter: Emitter) -> Unit = { particle, emitter -> }

        var particles: ArrayList<Particle>


        init {
            particles = ArrayList()
        }

        fun spawn(resourceId: Int, spawnAt: Vector3, life: Float, spawnRatePerSecond: Float) {
            this.resourceId = resourceId
            this.timeSpawnLimit = 1000f / spawnRatePerSecond
            this.position = Vector3(spawnAt.x, spawnAt.y, spawnAt.z)
            this.life = life
            this.timeCounter = 0f
            //Log.d("VIOLET", "Spawned emitter")
        }

        fun update(dt: Float): Boolean {
            if(life < 0f && particles.size == 0) {
                return false
            }

            if(life > 0f && timeCounter >= timeSpawnLimit) {
                // Log.d("VIOLET", "Spawned particle timeCounter: $timeCounter timeSpawnLimit: $timeSpawnLimit")
                timeCounter = 0f
                particles.add(spawnParticle())
                modelLayer.addInstance(resourceId, particles.last())
            }

            var count = particles.size-1
            while(count >= 0) {
                var particle = particles[count]
                particleUpdateBehaviour(particle, dt)
                if(particle.life < 0f) {
                    particles.remove(particle)
                    modelLayer.removeInstance(resourceId, particle)
                }
                count--
            }

            life -= dt
            timeCounter += dt
            return true
        }

        private fun spawnParticle(): Particle {
            var newParticle = Particle()
            particleSpawnBehaviour(newParticle, this)
            return newParticle
        }
    }

    var emitters: ArrayList<Emitter>
    var camera: Camera

    var mvpUpdater = ShaderHelper.get3DParticleUpdaterMVP()
    var mvUpdater = ShaderHelper.get3DParticleUpdaterMV()
    var lightPosUpdater = ShaderHelper.getLightPos3DParticleUpdaterLightPos()
    var textureUpdater = ShaderHelper.get3DParticleUpdaterTexture()
    var fadeUpdater = ShaderHelper.get3DParticleUpdaterFading()

    init {
        emitters = ArrayList<Emitter>()
        camera = Camera()

    }

    fun create(camera: Camera, particleMesh: MeshPart) {
        this.camera = camera

        var vsShader = ShaderHelper.loadShader(ShaderHelper.getRawVertexShader3DNormalUvParticle(),
            ShaderHelper.getVertexType())
        var psShader = ShaderHelper.loadShader(ShaderHelper.getRawPixelShader3DNormalUvParticle(),
            ShaderHelper.getFragmentType())

        attributesParticle = ShaderHelper.getAllUniformsNormalTextureParticle()
        vertexAttributesParticle = ShaderHelper.getAllAtributesNormalTextureParticle()

        // Before this, attributes needs to be filled with names.
        particlePipeline.create(vsShader, psShader, vertexAttributesParticle)

        particlePipeline.updateAttributeLocation(vertexAttributesParticle)
        particlePipeline.updateUniformLocation(attributesParticle)

        modelLayer.setModel(particleMesh)

        modelLayer.setPerInstance = { instance ->
            // create matrices and set them
            var mvp = Matrix4x4()
            var mv = Matrix4x4()

            var scale = Matrix4x4()
            scale.identity()
            scale.scale(0, instance.scale.x, instance.scale.y, instance.scale.z)

            var rotation = Matrix4x4()
            rotation.identity()
            rotation.rotate(0, instance.rotationAngle,
                instance.rotationAxis.x,
                instance.rotationAxis.y,
                instance.rotationAxis.z)

            var world = Matrix4x4()
            world.identity()
            world.translate(0, instance.position.x,
                instance.position.y,
                instance.position.z)

            var temp = Matrix4x4()
            temp.identity()

            temp.multiply(scale, rotation)
            rotation.multiply(temp, world)
            //temp.multiply(rotation, world)

            mv.multiply(this.camera.getViewMatrix(), rotation)
            mvp.multiply(this.camera.getProjectionMatrix(), mv)

            mvUpdater(attributesParticle, mv)
            mvpUpdater(attributesParticle, mvp)
            fadeUpdater(attributesParticle, instance.life / 2000f)
        }

        modelLayer.setPerResource = { resourceDataHandle ->
            // AttributePackage has GL handle, and resourceDataHandle has data...
            textureUpdater(attributesParticle, resourceDataHandle.textureHandle)
        }

        modelLayer.setPerModel = { mesh ->
            // Set anything per model???
            lightPosUpdater(attributesParticle, Vector3(1f, 0f, 0f))
        }
    }

    fun addParticleResource(resource: ResourceTexture): Int {
        return modelLayer.addResource(resource)
    }

    /// TODO: Spawn Emitter is not synchronized.
    fun addEmitter(resourceId: Int, spawnNrPerSecond: Float, milliSecsToLive: Float, position: Vector3, emitter: Emitter) {
        emitters.add(emitter)
        emitter.spawn(resourceId, position, milliSecsToLive, spawnNrPerSecond)
        // Log.d("VIOLET", "Emitter spawned")
    }

    /// TODO: Not all particles/emitters will die, some of maybe will live on...
    fun update(dt: Float) {
        var count = emitters.size-1
        while(count >= 0) {
            var emitter = emitters[count]
            var result = emitter.update(modelLayer, dt)
            if(result == false) {
                // Log.d("VIOLET", "Removing emitter")
                emitters.remove(emitter)
            }

            count--
        }
    }

    fun draw() {
        particlePipeline.frameBegin()

        particlePipeline.updateAttributeLocation(vertexAttributesParticle)
        particlePipeline.updateUniformLocation(attributesParticle)

        modelLayer.draw()

        //particlePipeline.release()
    }
}*/