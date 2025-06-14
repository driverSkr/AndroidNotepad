package com.ethan.videoediting

class VideoInfoBean {
    var videoCodecName: String = ""
    var videoCodedWidth: Int = -1
    var ration: Float = 0.0F
    var videoCodedHeight: Int = -1
    var videoBitRate: Int = -1      //帧率
    var videoDuration: Float = -1F      //时长（秒）
    var videoNbFrames: Int = -1
    var videoAvgFrameRate: Float = -1F      //平均帧率
    var audioCodecName: String = ""
    var audioTractCount: Int = 0

    val videoFramesRate: Float
        get() {
            return videoNbFrames / videoDuration
        }
}