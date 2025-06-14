package com.ethan.file

import android.content.Context
import android.os.Environment
import android.os.StatFs
import android.os.storage.StorageManager
import android.text.TextUtils
import android.util.Log
import java.io.*
import java.lang.reflect.Array
import java.nio.channels.Channel
import java.nio.channels.FileChannel
import java.util.*

/**
 * 文件读写、覆盖、追加、删除、复制、移动、遍历
 * 获取文件是否是视频、图片等
 */
object FileUtils {

    private val TAG = FileUtils::class.java.simpleName
    private const val BUFFER_SIZE_DEFAULT = 8 * 1024
    val NEW_LINE = System.getProperty("line.separator")!!

    // 读
    fun read(path: String?): String {
        if (TextUtils.isEmpty(path)) {
            Log.w(TAG, "read file failure: " + NEW_LINE + Log.getStackTraceString(Throwable()))
            return ""
        }
        return read(File(path))
    }

    fun read(path: String?, encoding: String?, separator: String?, bufferLength: Int): String {
        if (TextUtils.isEmpty(path)) {
            Log.w(TAG, "read file failure: " + NEW_LINE + Log.getStackTraceString(Throwable()))
            return ""
        }
        return read(File(path), encoding, separator, bufferLength)
    }

    @JvmOverloads
    fun read(file: File, encoding: String? = null, separator: String? = null, bufferLength: Int = BUFFER_SIZE_DEFAULT): String {
        var separator = separator
        if (separator == null || separator == "") {
            separator = NEW_LINE
        }
        if (!file.exists()) {
            return ""
        }
        val str = StringBuffer()
        var fs: FileInputStream? = null
        var isr: InputStreamReader? = null
        var br: BufferedReader? = null
        try {
            fs = FileInputStream(file)
            isr = if (encoding == null || encoding.trim { it <= ' ' } == "") {
                InputStreamReader(fs)
            } else {
                InputStreamReader(fs, encoding.trim { it <= ' ' })
            }
            br = BufferedReader(isr, bufferLength)
            var data: String?
            while (br.readLine().also { data = it } != null) {
                str.append(data).append(separator)
            }
            return if (str.isNotEmpty()) {
                str.substring(0, str.lastIndexOf(separator))
            } else {
                str.toString()
            }
        } catch (e: IOException) {
            Log.w(TAG, "read file content failure: " + NEW_LINE + Log.getStackTraceString(e))
        } finally {
            close(br)
            close(isr)
            close(fs)
        }
        return ""
    }

    // 写(覆盖)
    fun writeCover(content: String?, path: String?): Boolean {
        if (TextUtils.isEmpty(path)) {
            Log.w(TAG, "write file failure: " + NEW_LINE + Log.getStackTraceString(Throwable()))
            return false
        }
        return writeCover(content, File(path))
    }

    fun writeCover(content: String?, target: File): Boolean {
        return write(content, target, false)
    }

    fun writeCover(contents: List<String?>?, path: String?): Boolean {
        if (TextUtils.isEmpty(path)) {
            Log.w(TAG, "write file failure: " + NEW_LINE + Log.getStackTraceString(Throwable()))
            return false
        }
        return writeCover(contents, File(path))
    }

    private fun writeCover(contents: List<String?>?, target: File): Boolean {
        return write(contents, target, false)
    }

    fun writeCover(`is`: InputStream?, path: String?): Boolean {
        if (TextUtils.isEmpty(path)) {
            Log.w(TAG, "write file failure: " + NEW_LINE + Log.getStackTraceString(Throwable()))
            return false
        }
        return writeCover(`is`, File(path))
    }

    private fun writeCover(`is`: InputStream?, target: File): Boolean {
        return write(`is`, target, false)
    }

    // 写(追加)
    fun writeAppend(content: String?, path: String?): Boolean {
        if (TextUtils.isEmpty(path)) {
            Log.w(TAG, "write file failure: " + NEW_LINE + Log.getStackTraceString(Throwable()))
            return false
        }
        return writeAppend(content, File(path))
    }

    private fun writeAppend(content: String?, target: File): Boolean {
        return write(content, target, true)
    }

    fun writeAppend(contents: List<String?>?, path: String?): Boolean {
        if (TextUtils.isEmpty(path)) {
            Log.w(TAG, "write file failure: " + NEW_LINE + Log.getStackTraceString(Throwable()))
            return false
        }
        return writeAppend(contents, File(path))
    }

    private fun writeAppend(contents: List<String?>?, target: File): Boolean {
        return write(contents, target, true)
    }

    fun writeAppend(`is`: InputStream?, path: String?): Boolean {
        if (TextUtils.isEmpty(path)) {
            Log.w(TAG, "write file failure: " + NEW_LINE + Log.getStackTraceString(Throwable()))
            return false
        }
        return writeAppend(`is`, File(path))
    }

    private fun writeAppend(`is`: InputStream?, target: File): Boolean {
        return write(`is`, target, true)
    }

    // 写
    fun write(content: String?, target: File, append: Boolean): Boolean {
        if (TextUtils.isEmpty(content)) {
            Log.w(TAG, "write file failure: " + NEW_LINE + Log.getStackTraceString(Throwable()))
            return false
        }
        if (target.exists() && target.isDirectory) {
            Log.w(TAG, "write file failure: " + NEW_LINE + Log.getStackTraceString(Throwable()))
            return false
        }
        if (parentNotExists(target)) {
            Log.w(TAG, "write file failure: " + NEW_LINE + Log.getStackTraceString(Throwable()))
            return false
        }
        var fw: FileWriter? = null
        try {
            fw = FileWriter(target, append)
            fw.write(content)
            return true
        } catch (e: IOException) {
            Log.e(TAG, "write file failure: " + NEW_LINE + Log.getStackTraceString(e))
        } finally {
            close(fw)
        }
        return false
    }

    fun write(contents: List<String?>?, target: File, append: Boolean): Boolean {
        if (contents == null || contents.isEmpty()) {
            Log.w(TAG, "write file failure: " + NEW_LINE + Log.getStackTraceString(Throwable()))
            return false
        }
        if (target.exists() && target.isDirectory) {
            Log.w(TAG, "write file failure: " + NEW_LINE + Log.getStackTraceString(Throwable()))
            return false
        }
        if (parentNotExists(target)) {
            Log.w(TAG, "write file failure: " + NEW_LINE + Log.getStackTraceString(Throwable()))
            return false
        }
        var fw: FileWriter? = null
        try {
            fw = FileWriter(target, append)
            for (i in contents.indices) {
                if (i > 0) {
                    fw.write(NEW_LINE)
                }
                fw.write(contents[i])
            }
            return true
        } catch (e: IOException) {
            Log.e(TAG, "write file failure: " + NEW_LINE + Log.getStackTraceString(e))
        } finally {
            close(fw)
        }
        return false
    }

    fun write(`is`: InputStream?, target: File, append: Boolean): Boolean {
        if (`is` == null) {
            Log.w(TAG, "write file failure: " + NEW_LINE + Log.getStackTraceString(Throwable()))
            return false
        }
        if (target.exists() && target.isDirectory) {
            Log.w(TAG, "write file failure: " + NEW_LINE + Log.getStackTraceString(Throwable()))
            return false
        }
        if (parentNotExists(target)) {
            Log.w(TAG, "write file failure: " + NEW_LINE + Log.getStackTraceString(Throwable()))
            return false
        }
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(target, append)
            val data = ByteArray(BUFFER_SIZE_DEFAULT)
            var length: Int
            while (`is`.read(data).also { length = it } != -1) {
                fos.write(data, 0, length)
            }
            fos.flush()
            return true
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            close(`is`)
            close(fos)
        }
        return false
    }

    private fun parentNotExists(target: File?): Boolean {
        return if (target != null) {
            !target.exists() && !target.parentFile.exists() && !target.parentFile.mkdirs()
        } else true
    }

    // 复制
    fun copy(source: File?, dest: File?): Boolean {
        if (source == null || dest == null) {
            Log.w(TAG, "copy file failure: " + NEW_LINE + Log.getStackTraceString(Throwable()))
            return false
        }
        if (!source.exists() && source.isDirectory) {
            Log.w(TAG, "copy file failure: " + NEW_LINE + Log.getStackTraceString(Throwable()))
            return false
        }
        if (dest.exists()) {
            Log.w(TAG, "copy file failure: " + NEW_LINE + Log.getStackTraceString(Throwable()))
            return false
        }
        dest.parentFile.mkdirs()
        var fis: FileInputStream? = null
        var fos: FileOutputStream? = null
        var ifc: FileChannel? = null
        var ofc: FileChannel? = null
        return try {
            fis = FileInputStream(source)
            fos = FileOutputStream(dest)
            ifc = fis.channel
            ofc = fos.channel
            val mbb = ifc.map(FileChannel.MapMode.READ_ONLY, 0, ifc.size())
            ofc.write(mbb)
            true
        } catch (e: IOException) {
            Log.w(TAG, "copy file failure: " + NEW_LINE + Log.getStackTraceString(Throwable()))
            false
        } finally {
            close(ifc)
            close(ofc)
            close(fis)
            close(fos)
        }
    }

    @JvmStatic
    @Throws(IOException::class)
    fun copy(inputStream: InputStream, destFile: File) {
        if (destFile.exists()) {
            destFile.delete()
        }
        val out = FileOutputStream(destFile)
        try {
            val buffer = ByteArray(4096)
            var len: Int
            while (inputStream.read(buffer).also { len = it } > 0) {
                out.write(buffer, 0, len)
            }
        } finally {
            out.flush()
            try {
                out.fd.sync()
            } catch (ignored: IOException) {
            }
            out.close()
        }
    }

    @JvmStatic
    @Throws(IOException::class)
    fun copy(inputStream: InputStream, outputStream: OutputStream) {
        try {
            val buffer = ByteArray(4096)
            var len: Int
            while (inputStream.read(buffer).also { len = it } > 0) {
                outputStream.write(buffer, 0, len)
            }
        } finally {
            outputStream.flush()
            outputStream.close()
        }
    }

    // 删除
    fun delete(path: String?): Boolean {
        if (TextUtils.isEmpty(path)) {
            Log.w(TAG, "delete file failure: " + NEW_LINE + Log.getStackTraceString(Throwable()))
            return false
        }
        return delete(File(path))
    }

    fun delete(file: File?): Boolean {
        if (file == null) {
            Log.w(TAG, "delete file failure: " + NEW_LINE + Log.getStackTraceString(Throwable()))
            return false
        }
        if (!file.exists()) {
            Log.w(TAG, "delete file failure: " + NEW_LINE + Log.getStackTraceString(Throwable()))
            return false
        }
        if (file.isDirectory) {
            val children = file.list()
            for (i in children.indices) {
                val child = File(file, children[i])
                Log.d("hzx", "文件夹路径: " + child.absolutePath)
                val success = delete(child)
                if (!success) {
                    return false
                }
            }
        }
        var success = false
        try {
            success = file.delete()
            Log.d("hzx", "文件路径: " + file.absolutePath + ", 结果: " + success)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (!success) {
            Log.w(TAG, "delete file failure: " + NEW_LINE + file.absolutePath + NEW_LINE + Log.getStackTraceString(Throwable()))
        }
        return success
    }

    // 移动
    fun move(source: File?, dest: File?): Boolean {
        if (source == null || dest == null) {
            Log.w(TAG, "move file failure: " + NEW_LINE + Log.getStackTraceString(Throwable()))
            return false
        }
        if (!source.exists() && source.isDirectory) {
            Log.w(TAG, "move file failure: " + NEW_LINE + Log.getStackTraceString(Throwable()))
            return false
        }
        if (dest.exists()) {
            Log.w(TAG, "move file failure: " + NEW_LINE + Log.getStackTraceString(Throwable()))
            return false
        }
        return source.renameTo(dest) || copy(source, dest) && delete(source)
    }

    // 关闭
    fun close(closeable: Closeable?) {
        if (closeable == null) {
            return
        }
        try {
            closeable.close()
        } catch (e: IOException) {
            Log.w(TAG, "stream close failure: " + NEW_LINE + Log.getStackTraceString(Throwable()))
        }
    }

    @JvmStatic
    fun close(channel: Channel?) {
        if (channel == null) {
            return
        }
        try {
            channel.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    } //获取根目录

    //判断SD卡是否挂载
    val sDPath: String
        get() {
            var sdDir: File? = null
            val adCardExit = Environment.getExternalStorageState()
                .endsWith(Environment.MEDIA_MOUNTED) //判断SD卡是否挂载
            if (adCardExit) {
                sdDir = Environment.getExternalStorageDirectory() //获取根目录
            }
            return sdDir?.toString() ?: ""
        }

    //获取所有sd卡根路径，包括内置外置等
    @JvmStatic
    fun getStoragePath(mContext: Context): List<String> {
        val pathList = ArrayList<String>() //使用getSystemService(String)检索一个StorageManager用于访问系统存储功能。
        val mStorageManager = mContext.getSystemService(Context.STORAGE_SERVICE) as StorageManager
        try {
            val storageVolumeClazz = Class.forName("android.os.storage.StorageVolume")
            val getVolumeList = mStorageManager.javaClass.getMethod("getVolumeList")
            val getDirectory = storageVolumeClazz.getMethod("getDirectory")
            val isRemovable = storageVolumeClazz.getMethod("isRemovable")
            getVolumeList.invoke(mStorageManager)?.let {
                for (i in 0 until Array.getLength(it)) {
                    val storageVolumeElement = Array.get(it, i)
                    val dir = getDirectory.invoke(storageVolumeElement) as File?
                    val removable = isRemovable.invoke(storageVolumeElement) as Boolean?
                    if (dir != null) {
                        pathList.add(dir.absolutePath)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } //判断一定要有内置sd卡路径
        val sdPath = sDPath
        if (!pathList.contains(sdPath)) {
            pathList.add(sdPath)
        }
        pathList.add("/data/hw_init") //针对华为手机额外目录
        pathList.add("/system")
        return pathList
    }

    //获取内置sd卡剩余可用空间
    val sDFreeSize: Long
        get() {
            val sf = StatFs(sDPath) // 获取单个数据块的大小(Byte)
            val blockSize = sf.blockSizeLong // 空闲的数据块的数量
            val freeBlocks = sf.availableBlocksLong // 返回SD卡空闲大小
            return freeBlocks * blockSize //单位Byte
        }

    fun saveAsFile(data: String, file: File?) {
        if (TextUtils.isEmpty(data) || file == null) return
        if (file.exists()) {
            safelyDelete(file)
        }
        var fos: FileOutputStream? = null
        try {
            file.createNewFile()
            fos = FileOutputStream(file)
            fos.write(data.toByteArray())
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun safelyDelete(file: File): Boolean {
        try {
            return file.delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun getStringFromFile(fileName: String?): String {
        return getStringFromFile(File(fileName))
    }

    @JvmStatic
    fun getStringFromFile(file: File?): String {
        if (file == null || !file.exists()) return ""
        val sb = StringBuilder()
        var fr: FileReader? = null
        try {
            fr = FileReader(file)
            val buffer = CharArray(BUFFER_SIZE_DEFAULT)
            var len: Int
            while (fr.read(buffer).also { len = it } > 0) {
                sb.append(buffer, 0, len)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fr?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return sb.toString()
    }

    /**
     * 递归遍历文件夹
     *
     * @param strPath
     * @return
     */
    fun refreshUsbMediaFileList(strPath: String?, filelist: MutableList<File?>) {
        val dir = File(strPath)
        val files = dir.listFiles() ?: return
        for (file in files) {
            if (file.isDirectory) {
                refreshUsbMediaFileList(file.absolutePath, filelist)
            } else {
                val endStr = file.name.lowercase(Locale.getDefault())
                if (endStr.endsWith(FileConstants.VIDEO_MP4) || endStr.endsWith(FileConstants.VIDEO_MPG) || endStr.endsWith(
                        FileConstants.VIDEO_3GP) || endStr.endsWith(FileConstants.VIDEO_AVI) || endStr.endsWith(
                        FileConstants.VIDEO_MKV) || endStr.endsWith(FileConstants.VIDEO_FLV) || endStr.endsWith(
                        FileConstants.VIDEO_M4V) || endStr.endsWith(FileConstants.VIDEO_MOV) || endStr.endsWith(
                        FileConstants.PICTURE_JPG) || endStr.endsWith(FileConstants.PICTURE_JPEG) || endStr.endsWith(
                        FileConstants.PICTURE_PNG) || endStr.endsWith(FileConstants.PICTURE_WEBP) || endStr.endsWith(
                        FileConstants.PICTURE_GIF) || endStr.endsWith(FileConstants.PICTURE_BMP)) {
                    Log.d(TAG, "refreshUsbMediaFileList file path: " + file.absolutePath)
                    filelist.add(file)
                }
            }
        }
    }

    fun getCurrPathFileList(currPath: String?): List<File>? {
        val fileList: MutableList<File> = ArrayList()
        val dir = File(currPath)
        val files = dir.listFiles() ?: return null
        for (file in files) {
            if (file.isDirectory) {
                fileList.add(file)
            } else {
                val endStr = file.name.lowercase(Locale.getDefault())
                if (endStr.endsWith(FileConstants.VIDEO_MP4) || endStr.endsWith(FileConstants.VIDEO_MPG) || endStr.endsWith(
                        FileConstants.VIDEO_3GP) || endStr.endsWith(FileConstants.VIDEO_AVI) || endStr.endsWith(
                        FileConstants.VIDEO_MKV) || endStr.endsWith(FileConstants.VIDEO_FLV) || endStr.endsWith(
                        FileConstants.VIDEO_M4V) || endStr.endsWith(FileConstants.VIDEO_MOV) || endStr.endsWith(
                        FileConstants.PICTURE_JPG) || endStr.endsWith(FileConstants.PICTURE_JPEG) || endStr.endsWith(
                        FileConstants.PICTURE_PNG) || endStr.endsWith(FileConstants.PICTURE_WEBP) || endStr.endsWith(
                        FileConstants.PICTURE_GIF) || endStr.endsWith(FileConstants.PICTURE_BMP)) {
                    Log.d(TAG, "refreshUsbMediaFileList file path: " + file.absolutePath)
                    fileList.add(file)
                }
            }
        }
        return fileList
    }

    /**
     * 根据文件名判断是否是视频文件
     *
     * @param fileName
     * @return
     */
    fun isVideo(fileName: String): Boolean {
        return (fileName.endsWith(FileConstants.VIDEO_MP4) || fileName.endsWith(FileConstants.VIDEO_MPG) || fileName.endsWith(
            FileConstants.VIDEO_3GP) || fileName.endsWith(FileConstants.VIDEO_AVI) || fileName.endsWith(
            FileConstants.VIDEO_MKV) || fileName.endsWith(FileConstants.VIDEO_FLV) || fileName.endsWith(
            FileConstants.VIDEO_M4V) || fileName.endsWith(FileConstants.VIDEO_MOV))
    }

    /**
     * 根据文件名判断是否是图片文件
     *
     * @param fileName
     * @return
     */
    fun isPicture(fileName: String): Boolean {
        return (fileName.endsWith(FileConstants.PICTURE_JPG) || fileName.endsWith(FileConstants.PICTURE_JPEG) || fileName.endsWith(
            FileConstants.PICTURE_PNG) || fileName.endsWith(FileConstants.PICTURE_WEBP) || fileName.endsWith(
            FileConstants.PICTURE_GIF) || fileName.endsWith(FileConstants.PICTURE_BMP))
    }

    /*
 * 文件操作 获取不带扩展名的文件名
 */
    fun getFileNameNoEx(filename: String?): String? {
        if (filename != null && filename.isNotEmpty()) {
            val dot = filename.lastIndexOf('.')
            if (dot > -1 && dot < filename.length) {
                return filename.substring(0, dot)
            }
        }
        return filename
    }

}