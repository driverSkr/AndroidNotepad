package com.ethan.android.notepad.ui.technique.page

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ethan.android.notepad.R

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalAnimationApi::class)
@Composable
fun SharedTransitionPage2() {
    var showDetails by remember { mutableStateOf(false) }

    SharedTransitionLayout {
        AnimatedContent(
            targetState = showDetails,
            transitionSpec = {
                // 弹窗从底部滑入，从底部滑出
                if (targetState) {
                    slideInVertically { height -> height } with
                            slideOutVertically { height -> height }
                } else {
                    slideInVertically { height -> -height } with
                            slideOutVertically { height -> -height }
                }.using(
                    SizeTransform(clip = false)  // 允许共享元素超出边界
                )
            },
            label = "bottom_sheet_transition"
        ) { targetState ->
            Box(modifier = Modifier.fillMaxSize()) {
                if (!targetState) {
                    // 主页面内容（小图）
                    MainContent(
                        onShowDetails = { showDetails = true },
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this@AnimatedContent
                    )
                } else {
                    // 弹窗内容（大图）
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        Spacer(Modifier.weight(1f)) // 顶部留白
                        DetailsContent(
                            onBack = { showDetails = false },
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this@AnimatedContent,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun MainContent(
    onShowDetails: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        with(sharedTransitionScope) {
            Image(
                painter = painterResource(R.mipmap.img_breast_enlargement_auto_example_before),
                contentDescription = "Preview Image",
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
            Spacer(Modifier.width(16.dp))
            Text("Click to enlarge", style = MaterialTheme.typography.bodyMedium)
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
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        with(sharedTransitionScope) {
            Image(
                painter = painterResource(R.mipmap.img_breast_enlargement_auto_example_after),
                contentDescription = "Enlarged Image",
                modifier = Modifier
                    .sharedElement(
                        rememberSharedContentState(key = "image"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                    .size(300.dp)
                    .clip(CircleShape)
                    .clickable { onBack.invoke() },
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "Tap to close",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.clickable { onBack.invoke() }
            )
        }
    }
}