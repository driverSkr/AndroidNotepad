package com.ethan.android.notepad.ui.technique.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ethan.android.notepad.theme.Black40
import com.valentinilk.shimmer.shimmer

@Composable
fun ShimmerPage() {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        (0..3).forEach { i ->
            Box(modifier = Modifier
                .shimmer()
                .fillMaxWidth()
                .height(24.dp)
                .background(color = Black40, shape = RoundedCornerShape(6.dp))
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                (0..2).forEach { a ->
                    Box(modifier = Modifier
                        .shimmer()
                        .weight(1f)
                        .aspectRatio(0.7f)
                        .background(color = Black40, shape = RoundedCornerShape(6.dp))
                    )
                }
            }
        }
    }
}