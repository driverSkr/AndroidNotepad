package com.ethan.android.notepad.common.utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.PathUtils
import com.ethan.android.notepad.common.model.MediaType
import java.io.File

object MediaUtils {

    /**
     * 复制一份临时文件并返回
     * 注意： Android 7.0+ 开始，直接文件路径访问被限制。应用只能访问自己的文件空间
     ** 系统返回的可能是：
     *** content://media/external/images/media/123（内容URI）
     *** file:///storage/emulated/0/DCIM/Camera/photo.jpg（文件URI）
     */
    fun getRealPathFromUri(context: Context, uri: Uri): String? {
        var stringPath: String? = null
        // 检查URI的scheme是否为"content://"（不区分大小写）
        // content:// 是Android内容提供者(ContentProvider)的标识
        if ("content".equals(uri.scheme, ignoreCase = true)) {
            runCatching {
                // 定义要查询的列，这里只需要文件名
                val projection = arrayOf(OpenableColumns.DISPLAY_NAME)
                // 通过ContentResolver查询URI对应的内容
                context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                    // 获取DISPLAY_NAME列的索引
                    val nameIndex = cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
                    if (cursor.moveToFirst() && nameIndex != -1) {
                        // 从cursor中获取文件名
                        val fileName = cursor.getString(nameIndex)
                        // 创建临时文件以获取路径
                        val exportPath = "${PathUtils.getExternalAppCachePath()}/export/${fileName}".apply {
                            FileUtils.createOrExistsDir(File(this).parentFile)
                        }
                        val tempFile = File(exportPath)
                        // 打开输出流，将URI对应的内容复制到临时文件中
                        tempFile.outputStream().use { outputStream ->
                            context.contentResolver.openInputStream(uri)?.copyTo(outputStream)
                        }
                        stringPath = tempFile.absolutePath // 绝对路径
                    } else {
                        stringPath = null
                    }
                }
            }
        } else {
            // 如果不是content:// URI（可能是file://），直接使用URI的路径
            stringPath = uri.path
        }

        return stringPath
    }

    /**
     * 根据MIME类型获取文件扩展名
     */
    private fun getExtensionFromMimeType(mimeType: String?): String {
        return when (mimeType) {
            "image/jpeg", "image/jpg" -> "jpg"
            "image/png" -> "png"
            "image/gif" -> "gif"
            "image/webp" -> "webp"
            "image/bmp" -> "bmp"
            "video/mp4" -> "mp4"
            "video/3gpp" -> "3gp"
            "video/avi" -> "avi"
            "video/mpeg" -> "mpeg"
            "video/quicktime" -> "mov"
            "video/webm" -> "webm"
            else -> ""
        }
    }

    /**
     * 根据URI获取媒体类型
     * @return 媒体类型枚举
     */
    fun getMediaTypeFromUri(context: Context, uri: Uri): MediaType {
        return try {
            // 1. 首先尝试通过ContentResolver获取MIME类型
            val mimeType = context.contentResolver.getType(uri)

            when {
                mimeType == null -> getMediaTypeFromFallback(context, uri)
                mimeType.startsWith("image/") -> MediaType.IMAGE
                mimeType.startsWith("video/") -> MediaType.VIDEO
                mimeType.startsWith("audio/") -> MediaType.AUDIO
                else -> MediaType.UNKNOWN
            }
        } catch (e: Exception) {
            // 如果获取失败，使用备用方案
            getMediaTypeFromFallback(context, uri)
        }
    }

    /**
     * 备用方案：通过URI路径和查询判断
     */
    private fun getMediaTypeFromFallback(context: Context, uri: Uri): MediaType {
        return when {
            // 检查URI是否包含媒体库路径
            isUriFromMediaStore(uri) -> getMediaTypeFromMediaStore(context, uri)
            // 检查文件扩展名
            isImageByExtension(uri) -> MediaType.IMAGE
            isVideoByExtension(uri) -> MediaType.VIDEO
            isAudioByExtension(uri) -> MediaType.AUDIO
            // 最后尝试查询文件信息
            else -> getMediaTypeByQuery(context, uri)
        }
    }

    /**
     * 根据MediaStore URI路径判断具体类型
     */
    private fun getMediaTypeFromMediaStore(context: Context, uri: Uri): MediaType {
        val uriString = uri.toString()
        return when {
            uriString.contains(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString()) ||
                    uriString.contains(MediaStore.Images.Media.INTERNAL_CONTENT_URI.toString()) -> MediaType.IMAGE

            uriString.contains(MediaStore.Video.Media.EXTERNAL_CONTENT_URI.toString()) ||
                    uriString.contains(MediaStore.Video.Media.INTERNAL_CONTENT_URI.toString()) -> MediaType.VIDEO

            uriString.contains(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString()) ||
                    uriString.contains(MediaStore.Audio.Media.INTERNAL_CONTENT_URI.toString()) -> MediaType.AUDIO

            else -> MediaType.UNKNOWN
        }
    }

    /**
     * 通过查询ContentResolver获取详细的媒体信息
     */
    fun getMediaTypeByQuery(context: Context, uri: Uri): MediaType {
        return try {
            // 查询媒体库信息
            val projection = arrayOf(
                MediaStore.MediaColumns.MIME_TYPE,
                MediaStore.MediaColumns.DISPLAY_NAME
            )

            context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val mimeTypeIndex = cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE)
                    val displayNameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)

                    val mimeType = if (mimeTypeIndex != -1) cursor.getString(mimeTypeIndex) else null
                    val displayName = if (displayNameIndex != -1) cursor.getString(displayNameIndex) else null

                    determineMediaType(mimeType, displayName)
                } else {
                    MediaType.UNKNOWN
                }
            } ?: MediaType.UNKNOWN
        } catch (e: Exception) {
            MediaType.UNKNOWN
        }
    }

    /**
     * 根据MIME类型和文件名确定媒体类型
     */
    private fun determineMediaType(mimeType: String?, displayName: String?): MediaType {
        // 优先使用MIME类型判断
        mimeType?.let {
            return when {
                it.startsWith("image/") -> MediaType.IMAGE
                it.startsWith("video/") -> MediaType.VIDEO
                it.startsWith("audio/") -> MediaType.AUDIO
                else -> MediaType.UNKNOWN
            }
        }

        // 如果没有MIME类型，使用文件名扩展名判断
        displayName?.let {
            val extension = it.substringAfterLast('.', "").lowercase()
            return when {
                imageExtensions.contains(extension) -> MediaType.IMAGE
                videoExtensions.contains(extension) -> MediaType.VIDEO
                audioExtensions.contains(extension) -> MediaType.AUDIO
                else -> MediaType.UNKNOWN
            }
        }

        return MediaType.UNKNOWN
    }

    /**
     * 通过URI路径判断是否来自MediaStore
     */
    fun isUriFromMediaStore(uri: Uri): Boolean {
        val uriString = uri.toString()
        return uriString.contains(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString()) ||
                uriString.contains(MediaStore.Images.Media.INTERNAL_CONTENT_URI.toString()) ||
                uriString.contains(MediaStore.Video.Media.EXTERNAL_CONTENT_URI.toString()) ||
                uriString.contains(MediaStore.Video.Media.INTERNAL_CONTENT_URI.toString()) ||
                uriString.contains(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString()) ||
                uriString.contains(MediaStore.Audio.Media.INTERNAL_CONTENT_URI.toString())
    }

    /**
     * 通过URI路径的文件扩展名判断是否为图片
     */
    fun isImageByExtension(uri: Uri): Boolean {
        val path = uri.path ?: return false
        val extension = path.substringAfterLast('.', "").lowercase()
        return imageExtensions.contains(extension)
    }

    /**
     * 通过URI路径的文件扩展名判断是否为视频
     */
    fun isVideoByExtension(uri: Uri): Boolean {
        val path = uri.path ?: return false
        val extension = path.substringAfterLast('.', "").lowercase()
        return videoExtensions.contains(extension)
    }

    /**
     * 通过URI路径的文件扩展名判断是否为音频
     */
    fun isAudioByExtension(uri: Uri): Boolean {
        val path = uri.path ?: return false
        val extension = path.substringAfterLast('.', "").lowercase()
        return audioExtensions.contains(extension)
    }

    // 支持的扩展名列表
    private val imageExtensions = setOf("jpg", "jpeg", "png", "gif", "bmp", "webp", "heic", "tiff")
    private val videoExtensions = setOf("mp4", "avi", "mov", "mkv", "flv", "wmv", "3gp", "m4v", "webm")
    private val audioExtensions = setOf("mp3", "wav", "ogg", "m4a", "flac", "aac", "wma")
}