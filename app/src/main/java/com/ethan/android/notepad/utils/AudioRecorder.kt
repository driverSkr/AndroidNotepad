package com.ethan.android.notepad.utils

import android.content.Context
import android.media.MediaRecorder
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

class AudioRecorder(private val context: Context) {
    private var recorder: MediaRecorder? = null
    private var exoPlayer: ExoPlayer? = null
    private var timeUpdateJob: Job? = null

    // 录音状态
    var isRecording by mutableStateOf(false)
        private set

    // 播放状态
    var isPlaying by mutableStateOf(false)
        private set

    // 当前播放进度（0-100）
    var playProgress by mutableFloatStateOf(0f)
        private set

    var currentTime by mutableLongStateOf(0L)
        private set

    var totalDuration by mutableLongStateOf(0L)
        private set

    // 格式化时间显示
    val formattedCurrentTime: String
        get() = currentTime.formatMSCTime()

    val formattedTotalTime: String
        get() = totalDuration.formatMSCTime()

    fun startRecording(filePath: String) {
        try {
            Log.d("AudioRecorder", "录音文件路径: $filePath")

            // 重置状态
            currentTime = 0L
            totalDuration = 3 * 60 * 1000L // 3分钟限制
            playProgress = 0f

            recorder = MediaRecorder().apply {
                reset()
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setMaxDuration(totalDuration.toInt())
                setOutputFile(filePath)

                prepare()
                start()
            }
            isRecording = true

            // 启动时间更新协程
            timeUpdateJob?.cancel()
            timeUpdateJob = CoroutineScope(Dispatchers.Main).launch {
                while (isActive && isRecording) {
                    delay(100)
                    currentTime += 100
                    playProgress = (currentTime.toFloat() / totalDuration) * 100
                }
            }
        } catch (e: IllegalStateException) {
            Log.e("AudioRecorder", "初始化录音失败", e)
            cleanup()
        } catch (e: IOException) {
            Log.e("AudioRecorder", "准备录音失败", e)
            cleanup()
        } catch (e: Exception) {
            Log.e("AudioRecorder", "录音失败", e)
            cleanup()
        }
    }

    fun stopRecording() {
        try {
            recorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            Log.e("AudioRecorder", "停止录音失败", e)
        } finally {
            isRecording = false
            recorder = null
            timeUpdateJob?.cancel()
            timeUpdateJob = null
        }
    }

    fun playRecording(
        filePath: String,
        onCompletion: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        if (filePath.isEmpty()) return

        try {
            exoPlayer?.release() // 释放之前的播放器
            currentTime = 0L
            playProgress = 0f

            exoPlayer = ExoPlayer.Builder(context).build().apply {
                val mediaItem = MediaItem.fromUri(Uri.fromFile(File(filePath)))
                setMediaItem(mediaItem)
                prepare()
                totalDuration = duration
                playWhenReady = true

                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        when (playbackState) {
                            Player.STATE_ENDED -> {
                                this@AudioRecorder.isPlaying = false
                                currentTime = totalDuration
                                playProgress = 100f
                                timeUpdateJob?.cancel()
                                onCompletion()
                            }
                        }
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        onError(Exception("ExoPlayer error: ${error.message}"))
                    }
                })
            }

            isPlaying = true

            // 启动时间更新协程
            timeUpdateJob?.cancel()
            timeUpdateJob = CoroutineScope(Dispatchers.Main).launch {
                while (isActive && isPlaying) {
                    delay(100)
                    exoPlayer?.let {
                        currentTime = it.currentPosition
                        playProgress = (currentTime.toFloat() / totalDuration) * 100
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("AudioRecorder", "播放失败", e)
            cleanup()
            onError(e)
        }
    }

    fun stopPlaying() {
        try {
            exoPlayer?.apply {
                if (isPlaying) {
                    pause()
                }
                release()
            }
        } catch (e: Exception) {
            Log.e("AudioRecorder", "停止播放失败", e)
        } finally {
            isPlaying = false
            exoPlayer = null
            timeUpdateJob?.cancel()
            timeUpdateJob = null
        }
    }

    fun cleanup() {
        currentTime = 0L
        totalDuration = 0L
        stopRecording()
        stopPlaying()
    }

    fun deleteRecording(filePath: String): Boolean {
        cleanup()
        return if (filePath.isNotEmpty()) {
            val file = File(filePath)
            val deleted = file.delete()
            deleted
        } else {
            false
        }
    }
}