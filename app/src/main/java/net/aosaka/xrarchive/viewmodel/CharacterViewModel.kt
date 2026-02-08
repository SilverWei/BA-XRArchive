package net.aosaka.xrarchive.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CharacterMouth(
    val x: Int = 3,
    val y: Int = 2
)

// Represents the transform values for the 3D model
data class ModelTransform(
    val characterMouth: CharacterMouth = CharacterMouth(),
    val character: Character? = null,
)

// The single state object for the entire screen
data class CharacterUiState(
    val animateCharacter: Boolean = true,
    val modelTransform: ModelTransform = ModelTransform(),
)

class CharacterViewModel : ViewModel() {
    // Private mutable state
    private val _uiState = MutableStateFlow(CharacterUiState())

    // Public immutable state flow for the UI to observe
    val uiState: StateFlow<CharacterUiState> = _uiState.asStateFlow()

    fun updateCharacterMouth(newCharacterMouth: CharacterMouth) {
        _uiState.update { currentState ->
            currentState.copy(
                modelTransform = currentState.modelTransform.copy(
                    characterMouth = currentState.modelTransform.characterMouth.copy(
                        x = newCharacterMouth.x.coerceIn(0,6),
                        y = newCharacterMouth.y.coerceIn(0,7)
                    )
                )
            )
        }
    }

    fun updateCharacter(newCharacter: Character) {
        _uiState.update { currentState ->
            currentState.copy(
                modelTransform = currentState.modelTransform.copy(
                    character = newCharacter,
                    characterMouth = newCharacter.mouthDefault
                )
            )
        }
    }
}

data class Character(
    val name: String,
    val animationName: String,
    val fileName: String,
    val mouthNodeName: String,
    val mouthMaterialIndex: Int,
    val mouthDefault: CharacterMouth,
    var portraitFileName: String
)