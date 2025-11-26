package com.ethan.android.notepad.ui.media.image.view

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Compose图片缩放组件
 * @param imagePath 图片路径（文件路径或URL）
 * @param placeholder 占位图Painter
 * @param modifier Modifier
 * @param minScale 最小缩放比例
 * @param maxScale 最大缩放比例
 * @param boundaryLimitPercentage 边界限制百分比
 * @param onScaleChange 缩放比例变化回调
 */
@Composable
fun ZoomableImage(
    imagePath: String? = null,
    placeholder: Painter? = null,
    modifier: Modifier = Modifier,
    minScale: Float = 0.5f,
    maxScale: Float = 8f,
    boundaryLimitPercentage: Float = 0.125f, // 12.5%
    onScaleChange: ((Float) -> Unit)? = null
) {
    val context = LocalContext.current

    // 图片状态管理
    val imageState = rememberImageState()

    // 变换状态管理
    val transformState = rememberTransformState(
        minScale = minScale,
        maxScale = maxScale,
        boundaryLimitPercentage = boundaryLimitPercentage
    )

    // 加载图片
    LaunchedEffect(imagePath) {
        if (imagePath != null) {
            imageState.loadImage(context, imagePath)
        }
    }

    // 图片加载完成后自动适应
    LaunchedEffect(imageState.bitmap, transformState.isInitialized) {
        if (imageState.bitmap != null && !transformState.isInitialized) {
            transformState.initializeWithImage(
                imageWidth = imageState.bitmap!!.width.toFloat(),
                imageHeight = imageState.bitmap!!.height.toFloat(),
                viewWidth = context.resources.displayMetrics.widthPixels.toFloat(),
                viewHeight = context.resources.displayMetrics.heightPixels.toFloat()
            )
            transformState.isInitialized = true
        }
    }

    // 缩放变化回调
    LaunchedEffect(transformState.scale) {
        onScaleChange?.invoke(transformState.scale)
    }

    Box(
        modifier = modifier
            .background(Color.Black)
            .clipToBounds()
            .zoomableGesture(transformState)
    ) {
        when {
            imageState.isLoading -> {
                // 加载中显示占位图
                placeholder?.let {
                    androidx.compose.foundation.Image(
                        painter = it,
                        contentDescription = "Loading",
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.Center),
                        contentScale = ContentScale.Fit
                    )
                } ?: run {
                    // 默认加载中状态
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.DarkGray)
                            .align(Alignment.Center)
                    )
                }
            }

            imageState.bitmap != null -> {
                // 绘制变换后的图片
                TransformedImage(
                    bitmap = imageState.bitmap!!,
                    transformState = transformState,
                    modifier = Modifier.fillMaxSize()
                )
            }

            else -> {
                // 错误状态
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.DarkGray)
                        .align(Alignment.Center)
                )
            }
        }

        // 调试信息显示（可选）
        if (false) {
            DebugOverlay(transformState)
        }
    }
}

/**
 * 图片状态管理
 */
@Composable
fun rememberImageState(): ImageState {
    return remember {
        ImageState()
    }
}

class ImageState {
    var bitmap: ImageBitmap? by mutableStateOf(null)
    var isLoading: Boolean by mutableStateOf(false)
    var error: Throwable? by mutableStateOf(null)

    suspend fun loadImage(context: android.content.Context, imagePath: String) {
        isLoading = true
        error = null

        try {
            // 使用Android原生方式加载图片
            val options = android.graphics.BitmapFactory.Options().apply {
                inPreferredConfig = android.graphics.Bitmap.Config.ARGB_8888
            }

            val androidBitmap = when {
                imagePath.startsWith("http://") || imagePath.startsWith("https://") -> {
                    // 网络图片加载（需要添加网络权限和Coil依赖）
                    loadNetworkImage(context, imagePath)
                }
                imagePath.startsWith("/") || imagePath.startsWith("file://") -> {
                    // 本地文件
                    android.graphics.BitmapFactory.decodeFile(imagePath, options)
                }
                else -> {
                    // 资源文件
                    val resourceId = context.resources.getIdentifier(
                        imagePath, "drawable", context.packageName
                    )
                    if (resourceId != 0) {
                        android.graphics.BitmapFactory.decodeResource(context.resources, resourceId, options)
                    } else {
                        null
                    }
                }
            }

            bitmap = androidBitmap?.asImageBitmap()
        } catch (e: Exception) {
            error = e
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    private fun loadNetworkImage(context: android.content.Context, url: String): android.graphics.Bitmap? {
        // 简化的网络图片加载，实际项目中建议使用Coil或Glide
        return try {
            val connection = java.net.URL(url).openConnection() as java.net.HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            android.graphics.BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            null
        }
    }
}

/**
 * 变换状态管理
 */
@Composable
fun rememberTransformState(
    minScale: Float = 0.5f,
    maxScale: Float = 8f,
    boundaryLimitPercentage: Float = 0.125f
): TransformState {
    return remember {
        TransformState(
            minScale = minScale,
            maxScale = maxScale,
            boundaryLimitPercentage = boundaryLimitPercentage
        )
    }
}

class TransformState(
    private val minScale: Float = 0.5f,
    private val maxScale: Float = 8f,
    private val boundaryLimitPercentage: Float = 0.125f
) {
    // 变换参数
    var scale by mutableStateOf(1f)
    var offset by mutableStateOf(Offset.Zero)

    // 状态标志
    var isInitialized by mutableStateOf(false)

    // 图片和视图尺寸
    private var imageSize: Size = Size.Zero
    private var viewSize: Size = Size.Zero

    // 动画
    private val animatableScale = Animatable(1f)
    private val animatableOffset = Animatable(Offset.Zero, Offset.VectorConverter)

    /**
     * 初始化变换状态
     */
    fun initializeWithImage(imageWidth: Float, imageHeight: Float, viewWidth: Float, viewHeight: Float) {
        imageSize = Size(imageWidth, imageHeight)
        viewSize = Size(viewWidth, viewHeight)

        // 计算初始缩放比例以适应屏幕
        val fitScale = calculateFitScale()
        scale = fitScale

        // 计算居中位置
        val centeredOffset = calculateCenteredOffset(fitScale)
        offset = centeredOffset

        isInitialized = true
    }

    /**
     * 应用手势变换
     */
    fun applyGesture(
        scaleChange: Float,
        panChange: Offset,
        gestureCenter: Offset
    ) {
        // 应用缩放
        val newScale = (scale * scaleChange).coerceIn(minScale, maxScale)

        // 应用平移（考虑缩放中心）
        val scaleFactor = newScale / scale
        val focusVector = gestureCenter - offset
        val newOffset = offset + panChange + focusVector * (1 - scaleFactor)

        // 应用边界限制
        val boundedOffset = applyBoundaryLimit(newOffset, newScale)

        // 更新状态
        scale = newScale
        offset = boundedOffset
    }

    /**
     * 重置变换（带动画）
     */
    fun reset() {
        val fitScale = calculateFitScale()
        val centeredOffset = calculateCenteredOffset(fitScale)

        // 使用动画过渡
        CoroutineScope(Dispatchers.Default).launch {
            animatableScale.animateTo(fitScale, tween(300))
            animatableOffset.animateTo(centeredOffset, tween(300))

            scale = animatableScale.value
            offset = animatableOffset.value
        }
    }

    /**
     * 计算适应屏幕的缩放比例
     */
    private fun calculateFitScale(): Float {
        if (imageSize == Size.Zero || viewSize == Size.Zero) return 1f

        val widthScale = viewSize.width / imageSize.width
        val heightScale = viewSize.height / imageSize.height

        return min(widthScale, heightScale).coerceIn(minScale, maxScale)
    }

    /**
     * 计算居中偏移量
     */
    private fun calculateCenteredOffset(currentScale: Float): Offset {
        if (imageSize == Size.Zero || viewSize == Size.Zero) return Offset.Zero

        val scaledWidth = imageSize.width * currentScale
        val scaledHeight = imageSize.height * currentScale

        val offsetX = (viewSize.width - scaledWidth) / 2
        val offsetY = (viewSize.height - scaledHeight) / 2

        return Offset(offsetX, offsetY)
    }

    /**
     * 应用边界限制
     */
    private fun applyBoundaryLimit(proposedOffset: Offset, currentScale: Float): Offset {
        if (imageSize == Size.Zero || viewSize == Size.Zero) return proposedOffset

        val scaledWidth = imageSize.width * currentScale
        val scaledHeight = imageSize.height * currentScale

        val padding = viewSize.width * boundaryLimitPercentage

        // 计算边界
        val minX = -scaledWidth + padding
        val maxX = viewSize.width - padding
        val minY = -scaledHeight + padding
        val maxY = viewSize.height - padding

        // 限制偏移量在边界内
        val boundedX = proposedOffset.x.coerceIn(minX, maxX)
        val boundedY = proposedOffset.y.coerceIn(minY, maxY)

        return Offset(boundedX, boundedY)
    }

    /**
     * 检查当前是否在边界内
     */
    fun isWithinBoundary(): Boolean {
        val currentOffset = offset
        val currentScale = scale
        val boundedOffset = applyBoundaryLimit(currentOffset, currentScale)

        return currentOffset == boundedOffset
    }
}

/**
 * 手势处理修饰符
 */
private fun Modifier.zoomableGesture(transformState: TransformState): Modifier {
    return this.pointerInput(transformState) {
        detectTransformGestures(
            onGesture = { centroid, pan, zoom, rotation ->
                transformState.applyGesture(zoom, pan, centroid)
            }
        )
    }
}

/**
 * 绘制变换后的图片
 */
@Composable
private fun TransformedImage(
    bitmap: ImageBitmap,
    transformState: TransformState,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        withTransform({
            // 应用变换：先平移，再缩放
            translate(
                left = transformState.offset.x,
                top = transformState.offset.y
            )
            scale(
                scaleX = transformState.scale,
                scaleY = transformState.scale,
                pivot = Offset.Zero
            )
        }) {
            // 绘制图片
            drawImage(
                image = bitmap,
                dstSize = IntSize(bitmap.width, bitmap.height)
            )
        }
    }
}

/**
 * 调试信息覆盖层
 */
@Composable
private fun DebugOverlay(transformState: TransformState) {
    Box(
        modifier = Modifier
            .fillMaxSize()
//            .align(Alignment.TopStart)
    ) {
        Text(
            text = "缩放: ${"%.2f".format(transformState.scale)}x\n" +
                    "偏移: (${"%.1f".format(transformState.offset.x)}, ${"%.1f".format(transformState.offset.y)})",
            color = Color.White,
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.7f))
                .padding(8.dp)
        )
    }
}