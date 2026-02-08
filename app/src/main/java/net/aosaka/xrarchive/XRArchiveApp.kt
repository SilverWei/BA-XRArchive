package net.aosaka.xrarchive

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.annotation.OptIn
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.exoplayer.ExoPlayer
import androidx.xr.compose.platform.LocalSession
import androidx.xr.compose.platform.LocalSpatialCapabilities
import androidx.xr.compose.spatial.ContentEdge
import androidx.xr.compose.spatial.Orbiter
import androidx.xr.compose.spatial.Subspace
import androidx.xr.compose.subspace.MovePolicy
import androidx.xr.compose.subspace.ResizePolicy
import androidx.xr.compose.subspace.SpatialColumn
import androidx.xr.compose.subspace.SpatialPanel
import androidx.xr.compose.subspace.SpatialRow
import androidx.xr.compose.subspace.layout.SubspaceModifier
import androidx.xr.compose.subspace.layout.alpha
import androidx.xr.compose.subspace.layout.fillMaxSize
import androidx.xr.compose.subspace.layout.fillMaxWidth
import androidx.xr.compose.subspace.layout.height
import androidx.xr.compose.subspace.layout.offset
import androidx.xr.compose.subspace.layout.padding
import androidx.xr.compose.subspace.layout.width
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import net.aosaka.xrarchive.environment.EnvironmentController
import net.aosaka.xrarchive.components.CharacterModel
import net.aosaka.xrarchive.components.EnvironmentControls
import net.aosaka.xrarchive.ui.theme.XRArchiveTheme
import net.aosaka.xrarchive.viewmodel.CharacterUiState
import net.aosaka.xrarchive.viewmodel.CharacterViewModel
import net.aosaka.xrarchive.components.GameControls

@Composable
fun XRArchiveApp() {
    val viewModel = CharacterViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val activity = LocalActivity.current
    val session = LocalSession.current
    if (session != null && activity is ComponentActivity) {
        val environmentController = remember(activity) {
            EnvironmentController(session, activity.lifecycleScope)
        }
        if (LocalSpatialCapabilities.current.isSpatialUiEnabled) {
            SpatialLayout(
                primaryContent = {
                    PrimaryContent(
                        uiState = uiState,
                    )
                },
                secondSupportingContent = {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        GameControls(
                            modelTransform = uiState.modelTransform,
                            onCharacterMouthChange = viewModel::updateCharacterMouth,
                            onCharacterChange = viewModel::updateCharacter
                        )
                    }
                },
                environmentController = environmentController
            )
        } else {
            NonSpatialLayout(
                primaryPane = {
                    StartGameView(environmentController = environmentController)
                }
            )
        }
    }
}

@Composable
private fun SpatialLayout(
    primaryContent: @Composable () -> Unit,
    secondSupportingContent: @Composable () -> Unit,
    environmentController: EnvironmentController,
) {
    val animatedAlpha = remember { Animatable(0.5f) }
    LaunchedEffect(Unit) {
        launch {
            animatedAlpha.animateTo(
                1.0f,
                animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
            )
        }
    }
    Subspace {
        SpatialRow(modifier = SubspaceModifier.height(816.dp).fillMaxWidth()) {
            SpatialColumn(modifier = SubspaceModifier.width(400.dp)) {
                SpatialPanel(
                    SubspaceModifier
                        .alpha(animatedAlpha.value)
                        .weight(1f),
                    dragPolicy = MovePolicy(isEnabled = true),
                    resizePolicy = ResizePolicy(isEnabled = true)
                ) {
                    Column {
                        TopAppBar(environmentController = environmentController)
                        secondSupportingContent()
                    }
                }
            }
            SpatialPanel(
                modifier = SubspaceModifier
                    .alpha(animatedAlpha.value)
                    .fillMaxSize()
                    .padding(left = 16.dp),
                dragPolicy = MovePolicy(isEnabled = true),
                resizePolicy = ResizePolicy(isEnabled = true)
            ) {
                primaryContent()
            }
        }
    }
}

@Composable
private fun NonSpatialLayout(
    primaryPane: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    val animatedAlpha = remember { Animatable(0.5f) }
    LaunchedEffect(Unit) {
        launch {
            animatedAlpha.animateTo(
                1.0f,
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            )
        }
    }
    Column(
        modifier = modifier
            .alpha(animatedAlpha.value)
            .systemBarsPadding()
    ) {
        Surface(Modifier) {
            primaryPane()
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun StartGameView(
    environmentController: EnvironmentController,
) {
    val context = LocalContext.current
    val videoUri = "android.resource://${context.packageName}/raw/pv_6_video"

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUri))
            repeatMode = Player.REPEAT_MODE_ALL
            prepare()
            playWhenReady = true
            volume = 0f
        }
    }

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha_breathing"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                environmentController.requestFullSpaceMode()
            }
    ) {
        // 全屏视频
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        )

        // 底部按鈕區域
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Image(
                painter = painterResource(id = R.drawable.style3),
                contentDescription = "Start Game Button",
                modifier = Modifier
                    .size(width = 600.dp, height = 120.dp)
                    .alpha(alpha),
                contentScale = ContentScale.Fit
            )
        }

        // ── 左上角 logo ────────────────────────────────
        Image(
            painter = painterResource(id = R.drawable.style4),
            contentDescription = "Logo",
            modifier = Modifier
                .align(Alignment.TopStart)           // 固定左上角
                .padding(start = 86.dp, top = 0.dp) // 可自行調整邊距
                .offset(y = -(24.dp))
                .size(240.dp)                         // 建議大小，可依設計調整
                // 以下兩行確保點擊會穿透到底層的 clickable
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    enabled = true
                ) { /* 故意留空 → 不 consume 事件，點擊會傳下去 */ }
        )
    }
}

@Composable
private fun TopAppBar(
    environmentController: EnvironmentController
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
    ) {
        Spacer(Modifier.weight(1f))
        Orbiter(
            position = ContentEdge.Top,
            offset = 96.dp,
            alignment = Alignment.CenterHorizontally
        ) {
            EnvironmentControls(environmentController)
        }
        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun PrimaryContent(
    uiState: CharacterUiState,
    modifier: Modifier = Modifier,
) {
    val modelTransform = uiState.modelTransform
    Surface(modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {  // ← 改用 Box 方便疊加
            Image(
                painter = painterResource(id = R.drawable.bg_decagrammatoniron_continent),
                contentDescription = "Steel Continent Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,     // 裁切填滿，最常用
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CharacterModel(
                    modelTransform = modelTransform,
                    animateCharacter = uiState.animateCharacter,
                    modifier = SubspaceModifier
                        .fillMaxSize()
                        .offset(
                            x = 0.dp,
                            y = -(320.0f.dp),
                            z = 400.dp // Relative position from the panel
                        )
                )
            }
        }
    }
}

@Composable
@Preview(device = "spec:width=1920dp,height=1080dp,dpi=160")
@Preview(device = "spec:width=411dp,height=891dp")
fun AppLayoutPreview() {
    XRArchiveTheme {
        XRArchiveApp()
    }
}