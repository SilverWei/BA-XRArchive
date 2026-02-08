package net.aosaka.xrarchive.character

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.xr.runtime.Session
import androidx.xr.scenecore.GltfModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.aosaka.xrarchive.viewmodel.Character

class CharacterController(
    private val xrSession: Session?,
    private val context: Context,
    private val coroutineScope: CoroutineScope,
    val character: Character
) {
    var gltfModel by mutableStateOf<GltfModel?>(null)

    init {
        loadCharacterModel()
    }

    private fun loadCharacterModel() {
        coroutineScope.launch {
            gltfModel = CharacterGltfModelCache.getOrLoadModel(xrSession, context, character)
        }
    }
}

private object CharacterGltfModelCache {
    private val cache = mutableMapOf<String, GltfModel?>()
    @SuppressLint("RestrictedApi")
    suspend fun getOrLoadModel(
        xrCoreSession: Session?, context: Context, character: Character
    ): GltfModel? {
        xrCoreSession ?: run {
            Log.w(TAG, "Cannot load model, session is null.")
            return null
        }
        val fileName = character.fileName

        return cache.getOrPut(fileName) {
            try {
                context.assets.open(fileName).use { input ->
                    GltfModel.create(xrCoreSession, input.readBytes(), character.name)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading GLTF model", e)
                null
            }
        }
    }

    fun clearCache() {
        cache.clear()
    }

    const val TAG = "CharacterGltfModelCache"
}