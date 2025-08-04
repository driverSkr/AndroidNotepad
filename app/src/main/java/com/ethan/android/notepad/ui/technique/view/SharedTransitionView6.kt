package com.ethan.android.notepad.ui.technique.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.SwipeableState
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import com.ethan.android.notepad.R
import com.ethan.android.notepad.theme.Black40
import com.ethan.android.notepad.theme.White
import kotlin.math.roundToInt

private val listSnacks = listOf(
    Snack("Cupcake", "", R.mipmap.cupcake),
    Snack("Donut", "", R.mipmap.donut),
    Snack("Eclair", "", R.mipmap.eclair),
    Snack("Froyo", "", R.mipmap.froyo),
    Snack("Gingerbread", "", R.mipmap.gingerbread),
    Snack("Honeycomb", "", R.mipmap.honeycomb),
)

private val shapeForSharedElement = RoundedCornerShape(16.dp)

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalWearMaterialApi::class)
@Composable
fun SharedTransitionView6() {
    var selectedSnack by remember { mutableStateOf<Snack?>(null) }
    val swipeableState = rememberSwipeableState(initialValue = 0)

    // 当selectedSnack变化时重置swipeableState
    LaunchedEffect(selectedSnack) {
        if (selectedSnack == null) {
            swipeableState.animateTo(0)
        }
    }

    SharedTransitionLayout(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray.copy(alpha = 0.5f))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(listSnacks) { snack ->
                AnimatedVisibility(
                    visible = snack != selectedSnack,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut(),
                    modifier = Modifier.animateItem()
                ) {
                    Box(
                        modifier = Modifier
                            .sharedBounds(
                                sharedContentState = rememberSharedContentState(key = "${snack.name}-bounds"),
                                animatedVisibilityScope = this,
                                clipInOverlayDuringTransition = OverlayClip(shapeForSharedElement)
                            )
                            .background(Color.White, shapeForSharedElement)
                            .clip(shapeForSharedElement)
                    ) {
                        SnackContents(
                            snack = snack,
                            modifier = Modifier.sharedElement(
                                sharedContentState = rememberSharedContentState(key = snack.name),
                                animatedVisibilityScope = this@AnimatedVisibility
                            ),
                            onClick = {
                                selectedSnack = snack
                            }
                        )
                    }
                }
            }
        }
        BottomSheetWithSharedElement(
            snack = selectedSnack,
            swipeableState = swipeableState,
            onConfirmClick = {
                selectedSnack = null
            }
        )
    }
}

@OptIn(ExperimentalWearMaterialApi::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.BottomSheetWithSharedElement(
    snack: Snack?,
    swipeableState: SwipeableState<Int>,
    modifier: Modifier = Modifier,
    onConfirmClick: () -> Unit
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val sheetHeight = screenHeight * 0.7f
    val sheetHeightPx = with(LocalDensity.current) { sheetHeight.toPx() }
    val anchors = mapOf(0f to 0, sheetHeightPx to 1)

    AnimatedVisibility(
        visible = snack != null,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(color = Black40)
                .clickable(onClick = onConfirmClick)
            ) {
                Box(
                    modifier = modifier
                        .offset { IntOffset(0, swipeableState.offset.value.roundToInt()) }
                        .swipeable(
                            state = swipeableState,
                            anchors = anchors,
                            thresholds = { _, _ -> FractionalThreshold(0.3f) },
                            orientation = Orientation.Vertical,
                            reverseDirection = true
                        )
                        .align(Alignment.BottomCenter)
                        .height(sheetHeight)
                        .fillMaxWidth()
                        .background(
                            color = White,
                            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                        )
                ) {
                    snack?.let { targetSnack ->
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .sharedBounds(
                                    sharedContentState = rememberSharedContentState(key = "${targetSnack.name}-bounds"),
                                    animatedVisibilityScope = this@AnimatedVisibility,
                                    clipInOverlayDuringTransition = OverlayClip(shapeForSharedElement)
                                )
                        ) {
                            SnackContents(
                                snack = targetSnack,
                                modifier = Modifier.sharedElement(
                                    sharedContentState = rememberSharedContentState(key = targetSnack.name),
                                    animatedVisibilityScope = this@AnimatedVisibility,
                                ),
                                onClick = onConfirmClick
                            )
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp, end = 8.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = { onConfirmClick() }) {
                                    Text(text = "Save changes")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SnackContents(
    snack: Snack,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onClick()
            }
    ) {
        Image(
            painter = painterResource(id = snack.image),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(20f / 9f),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
        Text(
            text = snack.name,
            modifier = Modifier
                .wrapContentWidth()
                .padding(8.dp),
            style = MaterialTheme.typography.titleSmall
        )
    }
}
