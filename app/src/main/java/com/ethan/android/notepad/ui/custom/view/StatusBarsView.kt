package com.ethan.android.notepad.ui.custom.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ethan.android.notepad.R
import com.ethan.android.notepad.extension.findBaseActivityVBind
import com.ethan.android.notepad.theme.Black

@Composable
fun StatusBarsView(title: String, canBack: Boolean = true, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Row(modifier = modifier
        .statusBarsPadding()
        .fillMaxWidth()
        .height(50.dp)
        .padding(start = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (canBack) {
            Image(painter = painterResource(R.drawable.svg_back_black), contentDescription = null, modifier = Modifier
                .clickable { context.findBaseActivityVBind()?.finish() })
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text = title, color = Black, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}

@Preview
@Composable
fun PreStatusBarsView() {
    StatusBarsView("示例")
}