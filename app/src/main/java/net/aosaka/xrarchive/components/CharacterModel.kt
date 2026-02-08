package net.aosaka.xrarchive.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.xr.compose.platform.LocalSession
import androidx.xr.compose.spatial.PlanarEmbeddedSubspace
import androidx.xr.compose.subspace.SceneCoreEntity
import androidx.xr.compose.subspace.layout.SubspaceModifier
import androidx.xr.compose.subspace.layout.scale
import androidx.xr.scenecore.AlphaMode
import androidx.xr.scenecore.GltfModelEntity
import androidx.xr.scenecore.KhronosUnlitMaterial
import androidx.xr.scenecore.Texture
import kotlin.io.path.Path
import net.aosaka.xrarchive.character.CharacterController
import net.aosaka.xrarchive.viewmodel.ModelTransform

const val TAG = "CharacterModel"

@SuppressLint("RestrictedApi")
@Composable
fun CharacterModel(
    modelTransform: ModelTransform,
    animateCharacter: Boolean,
    modifier: SubspaceModifier = SubspaceModifier,
) {
    val xrSession = LocalSession.current
    val character = modelTransform.character
    if (xrSession != null) {
        if (character != null) {
            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()
            val characterController = remember(xrSession, context, coroutineScope, character.fileName) {
                CharacterController(xrSession, context, coroutineScope, character)
            }
            val gltfModel = characterController.gltfModel
            gltfModel?.let { model ->
                PlanarEmbeddedSubspace {
                    var scaleFromLayout by remember { mutableFloatStateOf(1f) }
                    var unlitMaterial by remember { mutableStateOf<KhronosUnlitMaterial?>(null) }
                    LaunchedEffect(xrSession, character.name) {
                        try {
                            val mouth = modelTransform.characterMouth
                            // 動態生成檔案名稱：character_mouth_[x+1]_[y+1].png
                            val fileName = "character_mouth_${mouth.x + 1}_${mouth.y + 1}.png"
                            // 組合完整相對路徑（假設 textures_mouth/ 是 assets 或專案資源目錄下的子資料夾）
                            val texturePath = "textures_mouth/$fileName"
                            val baseTexture = Texture.create(
                                session = xrSession,
                                path = Path(texturePath)
                            )

//                        pbrMaterial = KhronosPbrMaterial.create(
//                            session = xrSession,
//                            alphaMode = AlphaMode.MASK
//                        )
//                        pbrMaterial?.setOcclusionTexture(
//                            texture = baseTexture,
//                            strength = 1.0f
//                        )

                            unlitMaterial = KhronosUnlitMaterial.create(
                                session = xrSession,
                                AlphaMode.MASK
                            )
                            unlitMaterial?.setBaseColorTexture(
                                baseTexture
                            )
                        } catch (e: Exception) {
                            Log.e(TAG, "Error creating material", e)
                        }
                    }
                    LaunchedEffect(
                        modelTransform.characterMouth.x,
                        modelTransform.characterMouth.y,
                    ) {
                        if (unlitMaterial == null) {
                            unlitMaterial = KhronosUnlitMaterial.create(
                                session = xrSession,
                                AlphaMode.MASK
                            )
                        }
                        val mouth = modelTransform.characterMouth

                        // 動態生成檔案名稱：character_mouth_[x+1]_[y+1].png
                        val fileName = "character_mouth_${mouth.x + 1}_${mouth.y + 1}.png"

                        // 組合完整相對路徑（假設 textures_mouth/ 是 assets 或專案資源目錄下的子資料夾）
                        val texturePath = "textures_mouth/$fileName"

                        try {
                            val baseTexture = Texture.create(
                                session = xrSession,
                                path = Path(texturePath)
                            )

                            unlitMaterial?.setBaseColorTexture(baseTexture)

                            Log.d("MouthTexture", "Loaded texture: $texturePath")
                        } catch (e: Exception) {
                            Log.e("MouthTexture", "Failed to load $texturePath", e)
                        }
                    }
                    SceneCoreEntity(
                        factory = {
                            GltfModelEntity.create(xrSession, model)
                        },
                        update = { entity: GltfModelEntity ->
//                        pbrMaterial?.let { newMaterial ->
//                            entity.setMaterialOverride(
//                                material = newMaterial,
//                                "CH0334_Head_Body",
//                                6
//                            )
//                        }
                            try {
                                unlitMaterial?.let { newMaterial ->
                                    entity.setMaterialOverride(
                                        material = newMaterial,
                                        character.mouthNodeName,
                                        character.mouthMaterialIndex
                                    )
                                }
                                if (animateCharacter) {
                                    entity.startAnimation(
                                        loop = true, animationName = character.animationName
                                    )

                                } else {
                                    entity.stopAnimation()
                                }
                            } catch (_: Exception) {
                            }
                        },
                        modifier = modifier.scale(scaleFromLayout)
                    )
                }
            }
        }
    }
}