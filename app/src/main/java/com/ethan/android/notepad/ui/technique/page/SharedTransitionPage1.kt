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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionPage1() {
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