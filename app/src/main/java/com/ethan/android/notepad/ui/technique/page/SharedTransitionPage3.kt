package com.ethan.android.notepad.ui.technique.page

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ethan.android.notepad.R

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionPage3() {
    val listSnacks = listOf(
        Snack("item1", "sdnjfndjsww", R.drawable.svg_no_net),
        Snack("item2", "sdnjfndjsww", R.drawable.svg_check),
        Snack("item3", "sdnjfndjsww", R.drawable.svg_check_box_select_black),
        Snack("item4", "sdnjfndjsww", R.drawable.svg_icon_error),
    )

    SharedTransitionLayout {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = "home"
        ) {
            composable("home") {
                HomeScreen(
                    listSnacks,
                    this@SharedTransitionLayout,
                    this@composable
                ) { navController.navigate("details/$it") }
            }
            composable(
                "details/{item}",
                arguments = listOf(navArgument("item") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("item")
                val snack = listSnacks[id!!]
                DetailsScreen(
                    id,
                    snack,
                    this@SharedTransitionLayout,
                    this@composable
                ) {
                    navController.navigate("home")
                }
            }
        }
    }

}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DetailsScreen(
    id: Int,
    snack: Snack,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onBackPressed: () -> Unit
) {
    with(sharedTransitionScope) {
        Column(
            Modifier
                .fillMaxSize()
                .clickable {
                    onBackPressed()
                }
        ) {
            Image(
                painterResource(id = snack.image),
                contentDescription = snack.description,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .sharedElement(
                        sharedTransitionScope.rememberSharedContentState(key = "image-$id"),
                        animatedVisibilityScope = animatedContentScope
                    )
                    .aspectRatio(1f)
                    .fillMaxWidth()
            )
            Text(
                snack.name, fontSize = 18.sp,
                modifier =
                Modifier
                    .sharedElement(
                        sharedTransitionScope.rememberSharedContentState(key = "text-$id"),
                        animatedVisibilityScope = animatedContentScope
                    )
                    .fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
    listSnacks: List<Snack>,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onItemClick: (Int) -> Unit,
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(listSnacks) { index, item ->
            Row(
                Modifier.clickable {
                    onItemClick(index)
                }
            ) {
                Spacer(modifier = Modifier.width(8.dp))
                with(sharedTransitionScope) {
                    Image(
                        painterResource(id = item.image),
                        contentDescription = item.description,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .sharedElement(
                                sharedTransitionScope.rememberSharedContentState(key = "image-$index"),
                                animatedVisibilityScope = animatedContentScope
                            )
                            .size(100.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        item.name, fontSize = 18.sp,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .sharedElement(
                                sharedTransitionScope.rememberSharedContentState(key = "text-$index"),
                                animatedVisibilityScope = animatedContentScope,
                            )
                    )
                }
            }
        }
    }
}

data class Snack(
    val name: String,
    val description: String,
    @DrawableRes val image: Int
)