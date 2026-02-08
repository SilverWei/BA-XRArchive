package net.aosaka.xrarchive.environment

import android.util.Log
import androidx.xr.runtime.Session
import androidx.xr.scenecore.GltfModel
import androidx.xr.scenecore.SpatialEnvironment
import androidx.xr.scenecore.scene
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.core.net.toUri

class EnvironmentController(private val xrSession: Session, private val coroutineScope: CoroutineScope) {
    private val assetCache: HashMap<String, Any> = HashMap()
    private var activeEnvironmentModelName: String? = null

    fun requestHomeSpaceMode() {
        requestPassthrough()
        xrSession.scene.requestHomeSpaceMode()
    }

    fun requestFullSpaceMode() = xrSession.scene.requestFullSpaceMode()

    fun requestPassthrough() {
        xrSession.scene.spatialEnvironment.preferredSpatialEnvironment = null
        activeEnvironmentModelName = null
    }

    /**
     * Request the system load a custom Environment
     */
    fun requestCustomEnvironment(environmentModelName: String) {
        coroutineScope.launch {
            try {
                if (activeEnvironmentModelName == null ||
                    activeEnvironmentModelName != environmentModelName
                ) {

                    val environmentModel = assetCache[environmentModelName] as GltfModel

                    SpatialEnvironment.SpatialEnvironmentPreference(
                        skybox = null,
                        geometry = environmentModel
                    ).let {
                        xrSession.scene.spatialEnvironment.preferredSpatialEnvironment = it
                    }
                    activeEnvironmentModelName = environmentModelName
                }
                xrSession.scene.spatialEnvironment.preferredPassthroughOpacity = 0f

            } catch (e: Exception) {
                Log.e(
                    "XRArchive",
                    "Failed to update Environment Preference for $environmentModelName: $e"
                )
            }
        }
    }

    fun loadModelAsset(modelName: String) {
        coroutineScope.launch {
            //load the asset if it hasn't been loaded previously
            if (!assetCache.containsKey(modelName)) {
                try {
                    val gltfModel =
                        GltfModel.create(xrSession, modelName.toUri())
                    assetCache[modelName] = gltfModel
                } catch (e: Exception) {
                    Log.e(
                        "XRArchive",
                        "Failed to load model for $modelName: $e"
                    )
                }
            }
        }
    }
}
