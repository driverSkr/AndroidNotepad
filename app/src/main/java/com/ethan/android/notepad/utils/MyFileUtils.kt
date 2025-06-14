package com.ethan.android.notepad.utils

import android.content.Context
import android.os.StatFs
import android.util.Log
import com.blankj.utilcode.util.EncryptUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.PathUtils
import com.ethan.file.LogWriter
import java.io.File
import java.time.Instant
import java.time.temporal.ChronoUnit

object MyFileUtils {

    fun isInCache(url: String, exName: String, isOriginalVideo: Boolean): String? {
        val encryptMD5ToString = EncryptUtils.encryptMD5ToString(url)
        val fileName = "${encryptMD5ToString}." + exName
        val type = if (isOriginalVideo) "OriginalVideo" else "Videoenhancer"
        val filepath = "${PathUtils.getExternalAppCachePath()}/cache_video/${type}/${fileName}".apply {
            FileUtils.createOrExistsDir(File(this).parentFile)
        }
        if (FileUtils.isFileExists(filepath) && FileUtils.getFileLength(filepath) > 0) {
            Log.d("Ethan", "视频已在内存中：$filepath")
            return filepath
        }

        return null
    }

    /**
     * 删除指定日期的文件
     */
    fun deleteOldFiles(cacheDir: File, daysThreshold: Long = 7): Boolean {
        // 获取当前时间
        val now = Instant.now()

        // 遍历目录下的所有文件和子目录
        cacheDir.walkBottomUp().forEach { file ->
            // 如果是文件（排除目录）
            if (file.isFile) {
                // 获取文件的最后修改时间
                val lastModifiedInstant = Instant.ofEpochMilli(file.lastModified())

                // 计算文件的最后修改时间和当前时间的差值
                val ageOfTheFile = ChronoUnit.DAYS.between(lastModifiedInstant, now)

                // 如果文件的年龄超过了阈值天数，则删除文件
                if (ageOfTheFile > daysThreshold) {
                    file.delete()
                    println("Deleted: ${file.absolutePath}")
                    LogWriter.append("Deleted: ${file.absolutePath}")
                } else {
                    println("Kept: ${file.absolutePath} (Age: $ageOfTheFile days)")
                    LogWriter.append("Kept: ${file.absolutePath} (Age: $ageOfTheFile days)")
                }
            }
        }
        return true
    }

    fun deleteMinFiles(cacheDir: File, minutesThreshold: Long = 5): Boolean {
        // 获取当前时间
        val now = Instant.now()

        // 遍历目录下的所有文件和子目录
        cacheDir.walkBottomUp().forEach { file ->
            // 如果是文件（排除目录）
            if (file.isFile) {
                // 获取文件的最后修改时间
                val lastModifiedInstant = Instant.ofEpochMilli(file.lastModified())

                // 计算文件的最后修改时间和当前时间的差值（以分钟为单位）
                val ageOfTheFile = ChronoUnit.DAYS.between(lastModifiedInstant, now)

                // 如果文件的年龄超过了阈值分钟数，则删除文件
                if (ageOfTheFile > minutesThreshold) {
                    if (file.delete()) {
                        println("Deleted: ${file.absolutePath}")
                        LogWriter.append("Deleted: ${file.absolutePath}")
                    } else {
                        println("Failed to delete: ${file.absolutePath}")
                        LogWriter.append("Failed to delete: ${file.absolutePath}")
                    }
                } else {
                    println("Kept: ${file.absolutePath} (Age: $ageOfTheFile minutes)")
                    LogWriter.append("Kept: ${file.absolutePath} (Age: $ageOfTheFile minutes)")
                }
            }
        }

        return true
    }

    /**
     * 查询指定日期的文件大小
     */
    fun getOldFilesSize(cacheDir: File, daysThreshold: Long = 7): Long {
        // 获取当前时间
        val now = Instant.now()

        var totalSize: Long = 0

        // 遍历目录下的所有文件和子目录
        cacheDir.walkBottomUp().forEach { file ->
            // 如果是文件（排除目录）
            if (file.isFile) {
                // 获取文件的最后修改时间
                val lastModifiedInstant = Instant.ofEpochMilli(file.lastModified())

                // 计算文件的最后修改时间和当前时间的差值
                val ageOfTheFile = ChronoUnit.DAYS.between(lastModifiedInstant, now)

                // 如果文件的年龄超过了阈值天数，则累加文件大小
                if (ageOfTheFile > daysThreshold) {
                    totalSize += FileUtils.getLength(file)
                    println("Included in size calculation: ${file.absolutePath} (Size: ${file.length()} bytes)")
                    LogWriter.append("Included in size calculation: ${file.absolutePath} (Size: ${file.length()} bytes)")
                } else {
                    println("Not included in size calculation: ${file.absolutePath} (Age: $ageOfTheFile days)")
                    LogWriter.append("Not included in size calculation: ${file.absolutePath} (Age: $ageOfTheFile days)")
                }
            }
        }

        return totalSize
    }

    fun getMinFilesSize(cacheDir: File, minutesThreshold: Long = 5): Long {
        // 获取当前时间
        val now = Instant.now()

        var totalSize: Long = 0

        // 遍历目录下的所有文件和子目录
        cacheDir.walkBottomUp().forEach { file ->
            // 如果是文件（排除目录）
            if (file.isFile) {
                // 获取文件的最后修改时间
                val lastModifiedInstant = Instant.ofEpochMilli(file.lastModified())

                // 计算文件的最后修改时间和当前时间的差值（以分钟为单位）
                val ageOfTheFile = ChronoUnit.MINUTES.between(lastModifiedInstant, now)

                // 如果文件的年龄超过了阈值分钟数，则累加文件大小
                if (ageOfTheFile > minutesThreshold) {
                    totalSize += file.length()
                    println("Included in size calculation: ${file.absolutePath} (Size: ${file.length()} bytes)")
                    LogWriter.append("Included in size calculation: ${file.absolutePath} (Size: ${file.length()} bytes)")
                } else {
                    println("Not included in size calculation: ${file.absolutePath} (Age: $ageOfTheFile minutes)")
                    LogWriter.append("Not included in size calculation: ${file.absolutePath} (Age: $ageOfTheFile minutes)")
                }
            }
        }

        return totalSize
    }

    /**
     * 判断设备是否有足够空间
     */
    fun availableSpace(context: Context, requiredSpace: Long): Boolean {
        return try {
            val storageDir = context.getExternalFilesDir(null) ?: return false
            val stat = StatFs(storageDir.path)
            val bytesAvailable = stat.blockSizeLong * stat.availableBlocksLong
            val minRequiredSpace = requiredSpace + 20 * 1024 * 1024 // 额外增加 20 MB 缓冲
            bytesAvailable > minRequiredSpace
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}