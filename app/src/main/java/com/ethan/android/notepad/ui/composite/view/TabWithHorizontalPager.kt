@file:kotlin.OptIn(ExperimentalMaterial3Api::class)

package com.ethan.android.notepad.ui.composite.view

import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.ethan.android.notepad.R
import com.ethan.android.notepad.theme.Black20
import com.ethan.android.notepad.theme.NO_PADDING_TEXT_STYLE
import com.ethan.android.notepad.theme.Purple712FE7
import com.ethan.android.notepad.theme.Purple9258FA
import com.ethan.android.notepad.theme.Purple9B64FF
import com.ethan.android.notepad.theme.Transparent
import com.ethan.android.notepad.theme.White
import com.ethan.android.notepad.theme.White50
import com.ethan.android.notepad.theme.White60
import com.ethan.android.notepad.theme.White8
import com.ethan.android.notepad.ui.composite.model.VoiceItemDetail
import com.ethan.android.notepad.ui.composite.model.voiceDataSource
import com.ethan.android.notepad.ui.custom.view.ScrollableTabRow
import com.ethan.android.notepad.utils.ToastType
import com.ethan.android.notepad.utils.antiShakeClick
import com.ethan.android.notepad.utils.rememberLifecycleEvent
import com.ethan.android.notepad.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
@Composable
fun TabWithHorizontalPager(sheetState: SheetState) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val pagerState = rememberPagerState(initialPage = 0) { 6 }
    val pageList = listOf("所有","我的声音","男性","女性","青年","老年")
    val brushColor = Brush.horizontalGradient(colorStops = arrayOf(0.5f to Purple9B64FF, 1f to Purple712FE7))
    val selectVoice = remember { mutableStateOf(voiceDataSource.category[0].detail[0]) }

    val isPlayer = remember { mutableStateOf(false) }
    //支持 网络 + 本地 数据源
    val dataSourceFactory = DefaultDataSource.Factory(context, DefaultHttpDataSource.Factory().setUserAgent("").setAllowCrossProtocolRedirects(true))
    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory))
            .setHandleAudioBecomingNoisy(true) // 自动处理耳机断开
            .build()
            .apply {
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(state: Int) {
                        when (state) {
                            Player.STATE_ENDED -> {
                                isPlayer.value = false
                            }
                            else -> {}
                        }
                    }
                    override fun onPlayerError(error: PlaybackException) {}
                })
            }
    }

    val lifecycleEvent = rememberLifecycleEvent()
    LaunchedEffect(lifecycleEvent) {
        when (lifecycleEvent) {
            Lifecycle.Event.ON_RESUME -> if (exoPlayer.isPlaying) exoPlayer.play()
            Lifecycle.Event.ON_PAUSE -> exoPlayer.pause()
            else -> {}
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        selectedTabIndex = pagerState.currentPage
    }

    LaunchedEffect(selectedTabIndex) {
        isPlayer.value = false
        exoPlayer.pause()
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.stop()
            exoPlayer.clearMediaItems()
            exoPlayer.release()
        }
    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.7f)
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(top = 8.dp, start = 12.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(painter = painterResource(R.drawable.svg_icon_close_white_32), contentDescription = null, modifier = Modifier
                .antiShakeClick {
                    scope.launch(Dispatchers.Default) { sheetState.hide() }
                }
            )
            Text("音色", fontSize = 14.sp, color = White, textAlign = TextAlign.Center, style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W600), modifier = Modifier.weight(1f))
            Text(
                text = "完成",
                color = White,
                fontSize = 12.sp,
                style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W400),
                modifier = Modifier
                    .background(brush = brushColor, shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .antiShakeClick { }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            edgePadding = 12.dp,
            containerColor = Transparent,
            divider = {},
            indicator = { tabPositions ->
                if (tabPositions.isNotEmpty()) {
                    PagerTabIndicator(tabPositions = tabPositions, pagerState = pagerState)
                }
            },
        ) {
            pageList.forEachIndexed { index, pageName ->
                Tab(selected = selectedTabIndex == index,
                    onClick = {
                        selectedTabIndex = index
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(horizontal = 8.dp)
                ) {
                    Box(modifier = Modifier.padding(start = 4.dp, end = 4.dp, bottom = 6.dp)) {
                        Text(
                            text = pageName,
                            color = if (selectedTabIndex == index) White else White60,
                            fontSize = 14.sp,
                            style = NO_PADDING_TEXT_STYLE.copy(fontWeight = if (selectedTabIndex == index) FontWeight.W600 else FontWeight.W400)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        HorizontalPager(pagerState, beyondViewportPageCount = 2, userScrollEnabled = true) {
            when (it) {
                // All
                0 -> {
                    VoiceList(voiceDataSource.category[it].detail, exoPlayer, isPlayer, selectVoice, "all")
                }
                // My Voice
                1 -> {
                    VoiceList(voiceDataSource.category[it].detail, exoPlayer, isPlayer, selectVoice, "my")
                }
                // Male
                2 -> {
                    VoiceList(voiceDataSource.category[it].detail, exoPlayer, isPlayer, selectVoice, "male")
                }
                // Female
                3 -> {
                    VoiceList(voiceDataSource.category[it].detail, exoPlayer, isPlayer, selectVoice, "female")
                }
                // young
                4 -> {
                    VoiceList(voiceDataSource.category[it].detail, exoPlayer, isPlayer, selectVoice, "young")
                }
                // old
                5 -> {
                    VoiceList(voiceDataSource.category[it].detail, exoPlayer, isPlayer, selectVoice, "old")
                }
            }
        }
    }
}

/** 音频列表 */
@Composable
fun VoiceList(voiceList: List<VoiceItemDetail>, exoPlayer: ExoPlayer, isPlaying: MutableState<Boolean>, selectVoice: MutableState<VoiceItemDetail>, source: String = "all") {
    val context = LocalContext.current
    LazyColumn(
        contentPadding = PaddingValues(start = 5.dp, end = 5.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp), // 添加项间距
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    // 如果向上滑动超过阈值才允许关闭
                    if (dragAmount > 100f) { // 100dp 的阈值
//                        scope.launch { sheetState.hide() }
                        "你已超过滑动阈值".showToast(context, ToastType.HINT)
                    }
                }
            }
    ) {
        itemsIndexed(voiceList) { index, voice ->
            VoiceItemView(
                exoPlayer = exoPlayer,
                voice = voice,
                isPlaying = isPlaying,
                isSelected = selectVoice.value == voice,
                index = index,
                source = source,
                onclick = {selectVoice.value = it}
            )
        }
    }
}

/** 音频项 */
@Composable
fun VoiceItemView(
    modifier: Modifier = Modifier,
    exoPlayer: ExoPlayer,
    voice: VoiceItemDetail,
    isPlaying: MutableState<Boolean>,
    isSelected: Boolean,
    index: Int,
    source: String = "all",
    onclick: (voice: VoiceItemDetail) -> Unit = {},
) {
    val recordPlayBtn = if (isSelected && isPlaying.value) R.drawable.svg_icon_voice_play else R.drawable.svg_icon_voice_pause
    val voiceItemImg = when(source) {
        "my" -> R.mipmap.voice_item_my_img
        "male" -> R.mipmap.voice_item_male_img
        "female" -> R.mipmap.voice_item_female_img
        "young" -> R.mipmap.voice_item_young_img
        "old" -> R.mipmap.voice_item_old_img
        "all" -> when (voice.age) {
            "young" -> R.mipmap.voice_item_young_img
            "old" -> R.mipmap.voice_item_old_img
            else -> {
                when (voice.gender) {
                    "male" -> R.mipmap.voice_item_male_img
                    "female" -> R.mipmap.voice_item_female_img
                    else -> R.mipmap.voice_item_young_img
                }
            }
        }
        else -> R.mipmap.voice_item_young_img
    }

    Row(modifier = modifier
        .fillMaxWidth()
        .height(80.dp)
        .border(
            width = 2.dp,
            color = if (isSelected) Purple9258FA else Transparent,
            shape = RoundedCornerShape(12.dp)
        )
        .background(color = if (isSelected) Black20 else Transparent, shape = RoundedCornerShape(12.dp))
        .padding(horizontal = 7.dp)
        .antiShakeClick {
            exoPlayer.apply {
                clearMediaItems()
                val mediaItem = MediaItem.fromUri(voice.preViewUrl)
                addMediaItem(index, mediaItem)
            }
            exoPlayer.prepare()

            if (isSelected) {
                exoPlayer.playWhenReady = !exoPlayer.playWhenReady
                isPlaying.value = exoPlayer.playWhenReady
            } else {
                exoPlayer.playWhenReady = true
                isPlaying.value = true
            }

            onclick.invoke(voice)
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            Image(painter = painterResource(voiceItemImg), contentScale = ContentScale.Crop, contentDescription = null, modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp))
            )

            Image(painter = painterResource(recordPlayBtn), contentDescription = null, modifier = Modifier.align(Alignment.Center))
        }

        Column(modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .padding(start = 12.dp, end = 8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = voice.name,
                color = White,
                fontSize = 14.sp,
                style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W500)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.wrapContentSize()) {
                Text(
                    text = voice.age,
                    color = White50,
                    fontSize = 10.sp,
                    style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W400),
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .background(color = White8, shape = RoundedCornerShape(24.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )

                Text(
                    text = voice.gender,
                    color = White50,
                    fontSize = 10.sp,
                    style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W400),
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .background(color = White8, shape = RoundedCornerShape(24.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}