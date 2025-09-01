package com.ethan.android.notepad.ui.media.audio.model

enum class AudioEnum {
    /**
     * MediaRecorder 录音
     * MediaPlayer  播放
     */
    MediaRecorderMediaPlayer,

    /**
     * MediaRecorder 录音
     * ExoPlayer  播放
     */
    MediaRecorderExoPlayer,

    /**
     * AudioRecord 录音（采集PCM数据，支持暂停时缓存数据到内存/文件
     * AudioTrack 播放（直接播放PCM数据）
     */
    AudioRecordAudioTrack,

    /**
     * ffmpeg音频裁剪
     */
    AudioCutting
}