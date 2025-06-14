package com.ethan.android.notepad.ui.media.view

import android.view.LayoutInflater
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.exoplayer.ExoPlayer
import com.ethan.android.notepad.databinding.LayoutViewExoplayFullCropCenterBinding

/**
 * @Time：2025年1月16日23:00:23
 * 使用ExoPlayer的自动循环的视频播放组件集成
 */
@Composable
fun VideoView(videoPath: String) {
    val context = LocalContext.current

    val videoSize = remember { mutableStateOf(IntSize.Zero) }
    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(videoPath)
            addMediaItem(mediaItem)

            this.addListener(object : Player.Listener {
                override fun onVideoSizeChanged(size: VideoSize) {
                    super.onVideoSizeChanged(size)
                    videoSize.value = IntSize(size.width, size.height)
                }
            })
        }
    }

    DisposableEffect(Unit) {
        player.play()
        onDispose {
            player.release()
        }
    }

    Box {
        AndroidView(factory = { context ->
            val binding = LayoutViewExoplayFullCropCenterBinding.inflate(LayoutInflater.from(context))
            binding.playView.player = player
            binding.playView.useController = false
            player.repeatMode = ExoPlayer.REPEAT_MODE_ALL
            player.volume = 0f
            player.prepare()
            player.play()
            binding.root
        })
    }
}