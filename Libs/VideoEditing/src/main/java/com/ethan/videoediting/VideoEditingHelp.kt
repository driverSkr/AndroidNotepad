package com.ethan.videoediting

import android.Manifest


/**
 * @author  PengHaiChen
 * @date    2023/6/27 15:53
 * @email   penghaichen@tenorshare.cn
 * 视频处理之
 * 视频抽帧
 * 视频合成
 * 视频略缩图
 */
object VideoEditingHelp {


    /**
     * 获得略缩图
     * need [Manifest.permission.READ_EXTERNAL_STORAGE]
     * Android 13 need this Permission [Manifest.permission.READ_MEDIA_VIDEO]
     */
    fun genThumbnails(): VideoThumbnails.Builder {
        return VideoThumbnails.Builder()
    }


    /**
     * 获得最后一帧
     * need [Manifest.permission.READ_EXTERNAL_STORAGE]
     * Android 13 need this Permission [Manifest.permission.READ_MEDIA_VIDEO]
     */
    fun getLastFrame(): VideoLastFrame.Builder {
        return VideoLastFrame.Builder()
    }

    /**
     * 获得指定帧
     * need [Manifest.permission.READ_EXTERNAL_STORAGE]
     * Android 13 need this Permission [Manifest.permission.READ_MEDIA_VIDEO]
     */
    fun getSpecifiedFrame(): VideoSpecifiedFrame.Builder {
        return VideoSpecifiedFrame.Builder()
    }

    /**
     * 视频裁切
     * need [Manifest.permission.READ_EXTERNAL_STORAGE]
     * Android 13 need this Permission [Manifest.permission.READ_MEDIA_VIDEO]
     */
    fun doVideoCutting(): VideoCutting.Builder {
        return VideoCutting.Builder()
    }

    /**
     * 视频裁切 这个和VideoCutting不同,VideoCutting是裁切时间,这个是裁切分辨率
     * need [Manifest.permission.READ_EXTERNAL_STORAGE]
     * Android 13 need this Permission [Manifest.permission.READ_MEDIA_VIDEO]
     */
    fun doVideoCrop(): VideoCrop.Builder {
        return VideoCrop.Builder()
    }

    /**
     * 视频合并
     * need [Manifest.permission.READ_EXTERNAL_STORAGE]
     * Android 13 need this Permission [Manifest.permission.READ_MEDIA_VIDEO]
     */
    fun doVideoMerge(): VideoMerge.Builder {
        return VideoMerge.Builder()
    }

    /**
     * 为视频添加音轨
     */
    fun setVideoAudioTrack(): VideoAudioTrack.Builder {
        return VideoAudioTrack.Builder()
    }
}