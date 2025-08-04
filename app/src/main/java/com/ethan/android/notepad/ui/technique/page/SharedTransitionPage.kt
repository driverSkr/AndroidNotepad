package com.ethan.android.notepad.ui.technique.page

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import com.ethan.android.notepad.ui.technique.view.SharedTransitionView1
import com.ethan.android.notepad.ui.technique.view.SharedTransitionView2
import com.ethan.android.notepad.ui.technique.view.SharedTransitionView3
import com.ethan.android.notepad.ui.technique.view.SharedTransitionView4
import com.ethan.android.notepad.ui.technique.view.SharedTransitionView5
import com.ethan.android.notepad.ui.technique.view.SharedTransitionView6


@Composable
fun SharedTransitionPage() {
    val selectPage = remember { mutableIntStateOf(0) }

    BackHandler { selectPage.intValue = 0 }
    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(selectPage.intValue, label = "") {
            when (it) {
                1 -> SharedTransitionView1()
                2 -> SharedTransitionView2()
                3 -> SharedTransitionView3()
                4 -> SharedTransitionView4()
                5 -> SharedTransitionView5()
                6 -> SharedTransitionView6()
                else -> SelectPage(selectPage)
            }
        }
    }
}

@Composable
fun SelectPage(selectPage: MutableIntState) {
    Column(modifier = Modifier.fillMaxSize().statusBarsPadding(), horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = { selectPage.intValue = 1 }) {
            Text("页面1")
        }

        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = { selectPage.intValue = 2 }) {
            Text("页面2")
        }

        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = { selectPage.intValue = 3 }) {
            Text("页面3")
        }

        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = { selectPage.intValue = 4 }) {
            Text("页面4")
        }

        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = { selectPage.intValue = 5 }) {
            Text("页面5")
        }

        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = { selectPage.intValue = 6 }) {
            Text("页面6")
        }
    }
}