package com.ethan.android.notepad.ui.technique.page

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.ethan.android.notepad.R
import com.ethan.android.notepad.utils.antiShakeClick
import com.ethan.maskload.BlurHashDecoder

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionPage() {
    var showDetails by remember { mutableStateOf(false) }

    SharedTransitionLayout {
        AnimatedContent(showDetails, label = "basic_transition") { tragetState ->
            Box(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
                if (!tragetState) {
                    MainContent(
                        onShowDetails = { showDetails = true },
                        animatedVisibilityScope = this@AnimatedContent,
                        sharedTransitionScope = this@SharedTransitionLayout,
                    )
                } else {
                    DetailsContent(
                        onBack = { showDetails = false },
                        animatedVisibilityScope = this@AnimatedContent,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun MainContent(onShowDetails: () -> Unit, sharedTransitionScope: SharedTransitionScope, animatedVisibilityScope: AnimatedVisibilityScope) {
    Row {
        with(sharedTransitionScope) {
            Image(
                painter = painterResource(R.mipmap.img_breast_enlargement_auto_example_before),
                contentDescription = "girl",
                modifier = Modifier
                    .sharedElement(
                        rememberSharedContentState(key = "image"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                    .size(100.dp)
                    .clip(CircleShape)
                    .clickable { onShowDetails.invoke() },
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(20.dp))
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun DetailsContent(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    Column(modifier = modifier) {
        with(sharedTransitionScope) {
            Image(
                painter = painterResource(id = R.mipmap.img_breast_enlargement_auto_example_after),
                contentDescription = "girl",
                modifier = Modifier
                    .sharedElement(
                        rememberSharedContentState(key = "image"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                    .size(200.dp)
                    .clip(CircleShape)
                    .clickable { onBack.invoke() },
                contentScale = ContentScale.Crop
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun AsyncImageView(
    maskCode: String = "LEHV6nWB2yk8pyo0adR*.7kCMdnj",
    onShowDetails: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val isShowMask = remember { mutableStateOf(true) }

    Box(modifier = Modifier.width(120.dp).aspectRatio(0.5625f).clip(RoundedCornerShape(12.dp)).antiShakeClick { onShowDetails.invoke() }) {
        with(sharedTransitionScope) {
            val bitmap = BlurHashDecoder.decode(maskCode, 24, 48)
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("https://material.hitpaw.com/static/c8dcdb02426dea4fd90a303bf3fde30b/upload/475af39b9672e4fce58153cc2de26a3eFin-tasticMermaid.webp")
                    .crossfade(true)
                    .placeholderMemoryCacheKey("async_image")
                    .memoryCacheKey("async_image")
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                onLoading = {
                    isShowMask.value = true
                },
                onSuccess = {
                    isShowMask.value = false
                },
                modifier = Modifier
                    .sharedElement(
                        rememberSharedContentState(key = "async_image"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
            )

            if (isShowMask.value) {
                bitmap?.let { Image(bitmap = it.asImageBitmap(), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop) }
            }
        }
    }
}