package com.ethan.videoediting

import android.util.Log
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File

/**
 * @author  PengHaiChen
 * @date    2023/6/30 10:54
 * @email   penghaichen@tenorshare.cn
 */
private const val TAG = "VideoMergeBackUp"
class VideoMerge {
    private var inputPathList: List<String> = listOf()
    private var outputPath: String? = null
    private var cachePath: String? = null
    private var audioPath: String? = null
    private var progressCallback: ((Float) -> Unit)? = null
    private var successCallback: ((String) -> Unit)? = {}
    private var failCallback = {}

    class Builder {
        private var videoMerge = VideoMerge()

        /**
         * 需要合并的视频列表
         */
        fun inputPathList(vararg inputPathList: String): Builder {
            videoMerge.inputPathList = inputPathList.toList()
            return this
        }

        /**
         * 输出视频的路径,请给一个文件路径
         */
        fun outOutVideoPath(path: String): Builder {
            videoMerge.outputPath = path
            return this

        }

        /**
         * 成功回调
         */
        fun successCallBack(callback: (String) -> Unit): Builder {
            videoMerge.successCallback = callback
            return this
        }

        /**
         * 失败回调
         */
        fun failCallBack(callback: () -> Unit): Builder {
            videoMerge.failCallback = callback
            return this
        }


        fun progressCallBack(callback: ((Float) -> Unit)) {
            videoMerge.progressCallback = callback
        }

        /**
         * 转码的缓存路径
         */
        fun cacheDir(cachePath: String): Builder {
            videoMerge.cachePath = cachePath
            return this
        }


        fun audioFile(audioPath: String): Builder {
            videoMerge.audioPath = audioPath
            return this
        }

        fun build(): VideoMerge {
            return videoMerge
        }
    }


    /**
     * 综合混合
     */
    fun executionMix() {
        if (!File(cachePath ?: return).isDirectory) {
            return
        }
        val sc = System.currentTimeMillis()
        CoroutineScope(Dispatchers.Default).launch {
            val waitList = mutableListOf<Deferred<Any>>()
            val tsList = mutableListOf<File>()
            val firstVideoInfo = FfmpegVE.getVideoInfo(inputPathList.first())
            val firstVideoW = firstVideoInfo?.videoCodedWidth ?: 1080
            val firstVideoH = firstVideoInfo?.videoCodedHeight ?: 1920
            inputPathList.forEachIndexed { index, path ->
                val async = async { // 转为mp4再转为ts
                    val p4 = File(cachePath, "${index}_cache.mp4")
                    val p4Audio = File(cachePath, "${index}_cache_audio.mp4")
                    val ts = File(cachePath, "${index}_cache.ts")
                    val videoPath = if (index >= 1 && (audioPath != null)) {
                        // val audioCommand = "-i '$path' -i '$audioPath' -c:v copy -c:a copy -map 0:v:0 -map 1:a:0 -shortest '${p4Audio}'"
                        val audioCommand = "-i '$path' -i '$audioPath' -c:v libx264 -c:a aac -strict experimental -shortest '${p4Audio}'"
                        val audioExecute = FFmpegKit.execute(audioCommand)
                        if (!ReturnCode.isSuccess(audioExecute.returnCode)) Log.e(TAG, "execution: 视频添加音频失败 ,视频index : $index")
                        p4Audio
                    } else {
                        path
                    }
                    val p4Command = "-i $videoPath -vf \"scale=w=${firstVideoW}:h=${firstVideoH}:force_original_aspect_ratio=decrease,pad=w=${firstVideoW}:h=${firstVideoH}:x=(ow-iw)/2:y=(oh-ih)/2:color=black\" -c:v libx264 -crf 23 ${p4.absolutePath}"
                    val p4Execute = FFmpegKit.execute(p4Command)
                    if (!ReturnCode.isSuccess(p4Execute.returnCode)) Log.e(TAG, "execution: 视频转换转换到mp4失败 ,视频index : $index")

                    val tsCommand = "-i ${p4.absolutePath} -c copy -bsf:v h264_mp4toannexb -f mpegts ${ts.absolutePath}"
                    val tsExecute = FFmpegKit.execute(tsCommand)
                    if (!ReturnCode.isSuccess(tsExecute.returnCode)) Log.e(TAG, "execution: 视频转换转换到ts失败 ,视频index : $index")
                    tsList.add(ts)
                }
                waitList.add(async)
            }
            waitList.forEach {
                it.await()
            }
            val sc1 = System.currentTimeMillis()
            Log.i(TAG, "execution: 处理完mp4转码，耗时${System.currentTimeMillis() - sc}")

            // 合并视频
            tsList.sortBy { ts ->
                ts.name.split("_")[0].toInt()
            }

            val concatString = StringBuilder()
            tsList.forEach { ts ->
                concatString.append(ts)
                concatString.append("|")
            }

            // 移除最后一个竖线符号
            if (concatString.isNotEmpty()) {
                concatString.deleteCharAt(concatString.length - 1)
            }
            val ffmpegCommand = "-i \"concat:${concatString}\" -c copy -bsf:a aac_adtstoasc -movflags +faststart $outputPath"
            Log.i(TAG, "execution: 合并命令 $ffmpegCommand")
            val execute = FFmpegKit.execute(ffmpegCommand)
            if (ReturnCode.isSuccess(execute.returnCode)) {
                outputPath?.let { successCallback?.invoke(it) }
            } else {
                failCallback.invoke()
            }
            Log.i(TAG, "execution: 合成所有视频，耗时${System.currentTimeMillis() - sc1}")
            clearDirectory(cachePath!!) // 清空所有的缓存咯
        }
    }

    /**
     * MP4合并,要求保证分辨率相同,格式都为MP4无需转码合并,速度极快
     */
    fun executionMP4() {
        CoroutineScope(Dispatchers.Default).launch {
            val videoInfoList = inputPathList.filter {
                val isMp4 = File(it).extension.uppercase() == "MP4"
                if (!isMp4) {
                    return@launch
                }
                isMp4
            }.map { FfmpegVE.getVideoInfo(it) }
            if (videoInfoList.isEmpty()) {
                return@launch
            }
            val firstVideoInfo = videoInfoList.first()
            val hasSameResolution = videoInfoList.all {
                Log.i(TAG, "executionMP4: ${it?.videoCodedWidth}_${it?.videoCodedHeight} ${firstVideoInfo?.videoCodedWidth}_${firstVideoInfo?.videoCodedHeight}")
                it?.videoCodedWidth == firstVideoInfo?.videoCodedWidth && it?.videoCodedHeight == firstVideoInfo?.videoCodedHeight
            }
            if (!hasSameResolution) {
                Log.e(TAG, "executionMP4: 无法合并，宽高不匹配 ") // return@launch
            }
            val waitTsList = mutableListOf<Deferred<Any>>()
            val tsList = mutableListOf<File>()

            inputPathList.forEachIndexed { index, path ->
                val async = async {
                    val ts = File(cachePath, "${index}_cache.ts")
                    val tsCommand = "-i '$path' -c copy -bsf:v h264_mp4toannexb -f mpegts '${ts.absolutePath}'"
                    val tsExecute = FFmpegKit.execute(tsCommand)
                    if (!ReturnCode.isSuccess(tsExecute.returnCode)) Log.e(TAG, "execution: 视频转换转换到ts失败 ,视频 : $path")
                    tsList.add(ts)
                }
                waitTsList.add(async)
            }
            waitTsList.forEach {
                it.await()
            }
            val concatString = StringBuilder()
            tsList.forEach { ts ->
                concatString.append(ts)
                concatString.append("|")
            }

            // 移除最后一个竖线符号
            if (concatString.isNotEmpty()) {
                concatString.deleteCharAt(concatString.length - 1)
            }
            val ffmpegCommand = "-i \"concat:${concatString}\" -c copy -bsf:a aac_adtstoasc -movflags +faststart $outputPath"
            Log.i(TAG, "execution: 合并命令 $ffmpegCommand")
            val execute = FFmpegKit.execute(ffmpegCommand)
            if (!ReturnCode.isSuccess(execute.returnCode)) { // 添加音轨
                failCallback.invoke()
                return@launch
            }
            if (audioPath == null) {
                Log.i(TAG, "executionMP4: 无音频,返回视频")
                outputPath?.let { successCallback?.invoke(it) }
                return@launch
            }
            val outPathFile = File(outputPath)
            val withAudioPath = outPathFile.parent + "/" + "${outPathFile.nameWithoutExtension} _audio.mp4 "
            File(withAudioPath).delete()
            val command = "-i '$outputPath' -an -i '$audioPath' -c:v copy -c:a libmp3lame -map 0:v:0 -map 1:a:0 '${withAudioPath}'"
            val audioExecute = FFmpegKit.execute(command)
            if (ReturnCode.isSuccess(audioExecute.returnCode)) {
                Log.i(TAG, "executionMP4: 合并音频成功")
                successCallback?.invoke(withAudioPath)
            } else {
                Log.i(TAG, "executionMP4: 合并音频失败,返回视频")
                outputPath?.let { successCallback?.invoke(it) }
            }
        }
    }


    private fun clearDirectory(dirPath: String) {
        val directory = File(dirPath)
        if (directory.exists() && directory.isDirectory) {
            val files = directory.listFiles()
            if (files != null) {
                for (file in files) {
                    file.delete()
                }
            }
        }
    }

}