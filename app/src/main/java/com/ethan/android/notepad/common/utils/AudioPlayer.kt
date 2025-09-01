package com.ethan.android.notepad.common.utils

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.blankj.utilcode.util.FileUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import kotlin.apply
import kotlin.let
import kotlin.ranges.coerceIn
import kotlin.text.isNotEmpty

class AudioPlayer(private val context: Context) {
    var exoPlayer: ExoPlayer? = null
    private var timeUpdateJob: Job? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    var isPlaying by mutableStateOf(PlayState.IDLE)
        private set

    enum class PlayState { IDLE, PLAYING, PAUSED }

    var playProgress by mutableFloatStateOf(0f)
        private set

    var currentTime by mutableLongStateOf(0L)
        private set

    var totalDuration by mutableLongStateOf(0L)
        private set

    // 播放控制
    fun playAudio(
        filePath: String,
        onCompletion: () -> Unit = {},
        onError: (Exception) -> Unit = {},
    ) {
        require(filePath.isNotEmpty()) { "File path cannot be empty" }

        cleanup() // 先清理之前的资源

        try {
            val file = File(filePath)
            if (!file.exists()) {
                return
            }

            exoPlayer = ExoPlayer.Builder(context)
                .setHandleAudioBecomingNoisy(true) // 自动处理耳机断开
                .build()
                .apply {
                    setMediaItem(MediaItem.fromUri(Uri.fromFile(file)))
                    addListener(playerListener(onCompletion, onError))
                    prepare()
                    playWhenReady = true
                }

            isPlaying = PlayState.PLAYING
            startProgressUpdates()
        } catch (e: Exception) {
            onError(e)
            cleanup()
        }
    }

    // 播放网络音频
    @OptIn(UnstableApi::class)
    fun playNetAudio(
        fileUrl: String,
        onCompletion: () -> Unit = {},
        onError: (Exception) -> Unit = {},
    ) {
        cleanup() // 先清理之前的资源

        try {
            // 1. 创建数据源工厂（支持网络、缓存等）
            val dataSourceFactory = DefaultHttpDataSource.Factory()
                .setAllowCrossProtocolRedirects(true) // 允许跨协议重定向

            exoPlayer = ExoPlayer.Builder(context)
                .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory))
                .setHandleAudioBecomingNoisy(true) // 自动处理耳机断开
                .build()
                .apply {
                    setMediaItem(MediaItem.fromUri(fileUrl))
                    addListener(playerListener(onCompletion, onError))
                    prepare()
                    playWhenReady = true
                }

            isPlaying = PlayState.PLAYING
            startProgressUpdates()
        } catch (e: Exception) {
            onError(e)
            cleanup()
        }
    }

    // 暂停/恢复
    fun togglePlayback() {
        exoPlayer?.let {
            if (isPlaying == PlayState.PLAYING) {
                it.pause()
                timeUpdateJob?.cancel()
                isPlaying = PlayState.PAUSED
            } else {
                it.play()
                startProgressUpdates()
                isPlaying = PlayState.PLAYING
            }
        }
    }

    // 删除文件
    fun delete(filePath: String) {
        stop()
        FileUtils.delete(filePath)
    }

    // 停止并释放资源
    fun stop() {
        cleanup()
    }

    // 跳转到指定位置（百分比）
    fun seekTo(percent: Float) {
        exoPlayer?.let {
            val position = (percent * it.duration).toLong()
            it.seekTo(position.coerceIn(0, it.duration))
            currentTime = position
            playProgress = percent
        }
    }

    // 跳转到指定位置
    fun seekTo(position: Long) {
        exoPlayer?.let {
            val progress = (position / it.duration).toFloat()
            it.seekTo(position.coerceIn(0, it.duration))
            currentTime = position
            playProgress = progress
        }
    }

    // 内部方法
    private fun playerListener(
        onCompletion: () -> Unit,
        onError: (Exception) -> Unit,
    ): Player.Listener {
        return object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_ENDED -> {
                        isPlaying = PlayState.IDLE
                        playProgress = 100f
                        currentTime = totalDuration
                        timeUpdateJob?.cancel()
                        onCompletion()
                    }
                    Player.STATE_READY -> {
                        totalDuration = exoPlayer?.duration ?: 0L
                    }

                    Player.STATE_BUFFERING -> {}

                    Player.STATE_IDLE -> {}
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                onError(kotlin.Exception("Playback failed: ${error.errorCodeName}"))
                cleanup()
            }
        }
    }

    private fun startProgressUpdates() {
        timeUpdateJob?.cancel()
        timeUpdateJob = scope.launch {
            while (isActive && isPlaying == PlayState.PLAYING) {
                exoPlayer?.let { player ->
                    if (player.isPlaying) {
                        currentTime = player.currentPosition
                        totalDuration = player.duration
                        playProgress = if (totalDuration > 0) {
                            (currentTime.toFloat() / totalDuration) * 100
                        } else {
                            0f
                        }
                    }
                }
                delay(100) // 更新间隔
            }
        }
    }

    private fun cleanup() {
        timeUpdateJob?.cancel()
        exoPlayer?.release()
        exoPlayer = null
        isPlaying = PlayState.IDLE
        playProgress = 0f
        currentTime = 0L
        totalDuration = 0L
    }

    // 释放所有资源
    fun release() {
        cleanup()
        scope.cancel()
    }
}