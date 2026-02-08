package net.aosaka.xrarchive.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import net.aosaka.xrarchive.R
import net.aosaka.xrarchive.viewmodel.CharacterMouth
import net.aosaka.xrarchive.viewmodel.ModelTransform
import net.aosaka.xrarchive.viewmodel.Character

@Composable
fun GameControls(
    modelTransform: ModelTransform,
    modifier: Modifier = Modifier,
    onCharacterMouthChange: (CharacterMouth) -> Unit,
    onCharacterChange: (Character) -> Unit
) {
    Surface(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.bg1),
                contentDescription = "Steel Continent Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,     // 裁切填滿，最常用
            )

            Column(
                modifier = Modifier.padding(16.dp).fillMaxSize(),  // 讓 Column 填滿可用空間,
                verticalArrangement = Arrangement.Center,
            ) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .padding(top = 24.dp, bottom = 12.dp)           // 與下面 CharacterSelector 的間距
                        .height(44.dp)                     // 高度可調整
                        .fillMaxWidth(1f)               // 寬度不要太滿
                        .background(
                            color = Color(0xFF344B6F),
                            shape = ParallelogramShape(skewX = -0.3f, cornerRadius = 6f)
                        ),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.style1),
                        contentDescription = "Decorative background overlay",
                        modifier = Modifier
                            .align(Alignment.BottomEnd)             // 關鍵：右下對齊
                            .size(80.dp)                            // 圖片大小自行調整（可改 width/height 分開設）
                            .padding(end = 8.dp),     // 離右下邊界一點距離，避免貼邊太死
                        contentScale = ContentScale.Fit             // 或 Crop / Inside，依圖片比例決定
                    )
                    Text(
                        text = "学生清单",
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .align(Alignment.CenterStart),
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                CharacterSelector(
                    currentCharacter = modelTransform.character,
                    onCharacterChange = onCharacterChange,
                    modifier = modifier
                )
                AnimatedVisibility(
                    visible = modelTransform.character != null,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut(),
                ) {
                    Column() {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 12.dp)
                                .padding(top = 24.dp, bottom = 12.dp)           // 與下面 CharacterSelector 的間距
                                .height(44.dp)                     // 高度可調整
                                .fillMaxWidth(1f)               // 寬度不要太滿
                                .background(
                                    color = Color(0xFF344B6F),
                                    shape = ParallelogramShape(skewX = -0.3f, cornerRadius = 6f)
                                ),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.style1),
                                contentDescription = "Decorative background overlay",
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)             // 關鍵：右下對齊
                                    .size(80.dp)                            // 圖片大小自行調整（可改 width/height 分開設）
                                    .padding(end = 8.dp),     // 離右下邊界一點距離，避免貼邊太死
                                contentScale = ContentScale.Fit             // 或 Crop / Inside，依圖片比例決定
                            )
                            Text(
                                text = "嘴形",
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .align(Alignment.CenterStart),
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        // 這裡的內容只有在 character != null 時才會被組成
                        // 而且會有出現/消失動畫
                        MouthShapeGridSelector(
                            currentMouth = modelTransform.characterMouth,
                            onCharacterMouthChange = onCharacterMouthChange,
                            modifier = modifier
                        )
                    }
                }
            }
        }
    }
}

class ParallelogramShape(
    private val skewX: Float = 0.25f,
    private val cornerRadius: Float = 24f
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val radius = cornerRadius * density.density   // 或用 density.run { cornerRadius.dp.toPx() } 如果 cornerRadius 是 dp

        return Outline.Generic(
            Path().apply {
                // 左上圓角起點
                moveTo(radius, 0f)

                // 頂邊 → 向右傾斜
                lineTo(size.width - radius - (size.height * skewX), 0f)
                quadraticTo(                                    // ← 改這裡
                    size.width - (size.height * skewX), 0f,
                    size.width - (size.height * skewX), radius
                )

                // 右邊
                lineTo(size.width, size.height - radius)
                quadraticTo(                                    // ← 改這裡
                    size.width, size.height,
                    size.width - radius, size.height
                )

                // 底邊 → 向左傾斜回來
                lineTo(radius + (size.height * skewX), size.height)
                quadraticTo(                                    // ← 改這裡
                    (size.height * skewX), size.height,
                    (size.height * skewX), size.height - radius
                )

                // 左邊關回
                lineTo(0f, radius)
                quadraticTo(                                    // ← 改這裡
                    0f, 0f,
                    radius, 0f
                )

                close()
            }
        )
    }
}

@Composable
fun MouthShapeGridSelector(
    currentMouth: CharacterMouth,                  // 當前選中的嘴形位置
    onCharacterMouthChange: (CharacterMouth) -> Unit,
    modifier: Modifier = Modifier,
) {
    // 總共 7 列 (x: 0..6) × 8 行 (y: 0..7) → 56 個
    val columns = 8
    val rows = 7
    val totalItems = columns * rows

    val spriteBitmap = rememberMouthSpriteBitmap()  // 記住的 Bitmap

    // sprite sheet 尺寸（8x8 格）
    val spriteCols = 8
    val spriteRows = 8


    LazyVerticalGrid(
        columns = GridCells.Fixed(3),               // 每行固定 3 個
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),    // 垂直間距
        horizontalArrangement = Arrangement.spacedBy(8.dp),  // 水平間距
        contentPadding = PaddingValues(8.dp)
    ) {
        items(totalItems) { index ->
            val x = index / columns
            val y = index % columns

            val mouth = CharacterMouth(x = x, y = y)

            // 是否為當前選中
            val isSelected = mouth == currentMouth

            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        Log.d("MouthSelector", "Selected: $mouth")
                        onCharacterMouthChange(mouth)
                    }
            ) {
                // 1. 背景圖片 - style2.png 自適應填滿
                Image(
                    painter = painterResource(id = R.drawable.style2),
                    contentDescription = null,
                    modifier = Modifier
                        .matchParentSize()
                        .border(
                            width = 4.dp,
                            color = Color.White,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentScale = ContentScale.Crop,          // 填滿 + 裁切多餘，保持比例
                    // 替代選項：
                    // contentScale = ContentScale.FillBounds, // 強制填滿（可能輕微變形）
                    // alpha = if (isSelected) 1f else 0.85f   // 可選：選中時更亮
                )

                // 2. 選中時的覆蓋層（半透明主色調，突出選中）
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                            )
                    )
                }

                // 3. 嘴巴 sprite（如果有）
                if (spriteBitmap != null) {
                    val cellSrcWidth = spriteBitmap.width / spriteCols
                    val cellSrcHeight = spriteBitmap.height / spriteRows
                    val srcX = x * cellSrcWidth
                    val srcY = y * cellSrcHeight

                    Canvas(modifier = Modifier.matchParentSize()) {
                        drawImage(
                            image = spriteBitmap,
                            srcOffset = IntOffset(srcY, srcX),  // 注意：你的原碼是 IntOffset(srcY, srcX)，確認是否正確
                            srcSize = IntSize(cellSrcWidth, cellSrcHeight),
                            dstOffset = IntOffset.Zero,
                            dstSize = IntSize(size.width.toInt(), size.height.toInt()),
                            filterQuality = FilterQuality.High
                        )
                    }
                } else {
                    Box(Modifier.matchParentSize(), contentAlignment = Alignment.Center) {
                        Text("Loading...", style = MaterialTheme.typography.labelSmall)
                    }
                }

                // 5. 選中邊框（保持原樣）
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .border(
                                width = 4.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(8.dp)
                            )
                    )
                }

                // 4. 右下角標籤（顏色根據選中狀態調整）
                Text(
                    text = "${x + 1}-${y + 1}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) Color.White
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                        .background(
                            Color.Black.copy(alpha = 0.65f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun rememberMouthSpriteBitmap(): ImageBitmap? {
    val context = LocalContext.current
    return remember {
        try {
            context.assets.open("character_mouth.png").use { input ->
                BitmapFactory.decodeStream(input)?.asImageBitmap()
            }
        } catch (e: Exception) {
            // Log.e("Sprite", "Failed to load character_mouth.png", e)
            null
        }
    }
}

@Composable
fun CharacterSelector(
    currentCharacter: Character?,                  // 當前選中的角色（可為 null，表示未選）
    onCharacterChange: (Character) -> Unit,        // 點擊時觸發的回調
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val aris = Character(
        name = "Aris",
        animationName = "CH0334_Cafe_Idle",
        fileName = "tendou_aris_battle.glb",
        mouthNodeName = "CH0334_Head_Body",
        mouthMaterialIndex = 6,
        mouthDefault = CharacterMouth(3,2),
        portraitFileName = "tendou_aris_battle.png"
    )
    val kei = Character(
        name = "Kei",
        animationName = "CH0335_Cafe_Idle",
        fileName = "kei.glb",
        mouthNodeName = "CH0335_Body",
        mouthMaterialIndex = 6,
        mouthDefault = CharacterMouth(4,5),
        portraitFileName = "kei.png"
    )
    val rio = Character(
        name = "Rio",
        animationName = "CH0331_Cafe_Idle",
        fileName = "rio.glb",
        mouthNodeName = "CH0331_Body",
        mouthMaterialIndex = 5,
        mouthDefault = CharacterMouth(1,2),
        portraitFileName = "rio.png"
    )
    val himari = Character(
        name = "Himari",
        animationName = "CH0332_Cafe_Idle",
        fileName = "himari.glb",
        mouthNodeName = "CH0332_Body",
        mouthMaterialIndex = 5,
        mouthDefault = CharacterMouth(0,5),
        portraitFileName = "himari.png"
    )
    val toki = Character(
        name = "Toki",
        animationName = "CH0333_Cafe_Idle",
        fileName = "toki.glb",
        mouthNodeName = "CH0333_Body",
        mouthMaterialIndex = 5,
        mouthDefault = CharacterMouth(1,2),
        portraitFileName = "toki.png"
    )
    val yuzu = Character(
        name = "Yuzu",
        animationName = "CH0336_Carrier_Cafe_Idle",
        fileName = "yuzu.glb",
        mouthNodeName = "CH0336_Body",
        mouthMaterialIndex = 3,
        mouthDefault = CharacterMouth(4,0),
        portraitFileName = "yuzu.png"
    )
    val momoi = Character(
        name = "Momoi",
        animationName = "NP0267_Idle",
        fileName = "momoi.glb",
        mouthNodeName = "NP0267_Body",
        mouthMaterialIndex = 2,
        mouthDefault = CharacterMouth(6,5),
        portraitFileName = "momoi.png"
    )
    val midori = Character(
        name = "Midori",
        animationName = "NP0268_Idle",
        fileName = "midori.glb",
        mouthNodeName = "NP0268_Body",
        mouthMaterialIndex = 2,
        mouthDefault = CharacterMouth(3,2),
        portraitFileName = "midori.png"
    )
    val availableCharacters = listOf(aris, kei, rio, himari, toki, yuzu, momoi, midori)    // 來自你之前定義的 Character 實例

    LazyHorizontalGrid(
        rows = GridCells.Fixed(1),           // ← 關鍵：固定只有1行
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)                   // ← 重要！要給明確高度，不然會崩潰
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),   // 卡片之間的水平間距
        verticalArrangement = Arrangement.spacedBy(0.dp),      // 只有一行，幾乎沒影響
        contentPadding = PaddingValues(bottom = 8.dp)
    ) {
        items(availableCharacters.size) { index ->  // ← 這裡的參數是 character: Character
            val character = availableCharacters[index]
            val isSelected = currentCharacter == character

            Card(
                modifier = Modifier
                    .aspectRatio(404f / 456f)
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        width = if (isSelected) 3.dp else 1.5.dp,
                        color = if (isSelected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { onCharacterChange(character) },
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (isSelected) 6.dp else 2.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceContainerLowest
                )
            ) {
                Box(modifier = Modifier.fillMaxSize()) {

                    val painter = rememberAssetPainter(character.portraitFileName, context)

                    Image(
                        painter = painter,
                        contentDescription = "${character.name} portrait",
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.Center),
                        contentScale = ContentScale.Crop
                    )

                    // 底部名字區域（選中時文字顏色也變成 primary）
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        if (isSelected)
                                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f)
                                        else
                                            Color.Black.copy(alpha = 0.65f)
                                    )
                                )
                            )
                            .padding(vertical = 10.dp, horizontal = 12.dp)
                    ) {
                        Text(
                            text = character.name,
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected)
                                MaterialTheme.colorScheme.onPrimaryContainer  // 對比色更清晰
                            else
                                Color.White,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun rememberAssetPainter(
    fileName: String,
    context: android.content.Context = LocalContext.current
): Painter {
    val assetManager = context.assets
    return remember(fileName) {
        try {
            val inputStream = assetManager.open("student_portrait/${fileName}")
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            BitmapPainter(bitmap.asImageBitmap())
        } catch (e: Exception) {
            // 圖片讀取失敗時的 fallback（可換成 placeholder）
            ColorPainter(Color.Gray)
        }
    }
}
