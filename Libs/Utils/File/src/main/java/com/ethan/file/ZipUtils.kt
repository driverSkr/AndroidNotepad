package com.ethan.file

import android.util.Log
import java.io.*
import java.util.*
import java.util.zip.GZIPOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

/**
 * zipFiles          : 批量压缩文件
 * zipFile           : 压缩文件
 * unzipFile         : 解压文件
 * unzipFileByKeyword: 解压带有关键字的文件
 * getFilesPath      : 获取压缩文件中的文件路径链表
 * getComments       : 获取压缩文件中的注释链表
 */
object ZipUtils {

    private const val BUFFER_LEN = 8192

    /**
     * Zip the files.
     *
     * @param srcFiles    The source of files.
     * @param zipFilePath The path of ZIP file.
     * @return `true`: success<br></br>`false`: fail
     * @throws IOException if an I/O error has occurred
     */
    @Throws(IOException::class)
    fun zipFiles(srcFiles: Collection<String>?, zipFilePath: String?): Boolean {
        return zipFiles(srcFiles, zipFilePath, null)
    }

    /**
     * Zip the files.
     *
     * @param srcFilePaths The paths of source files.
     * @param zipFilePath  The path of ZIP file.
     * @param comment      The comment.
     * @return `true`: success<br></br>`false`: fail
     * @throws IOException if an I/O error has occurred
     */
    @Throws(IOException::class)
    fun zipFiles(srcFilePaths: Collection<String>?, zipFilePath: String?,
                 comment: String?): Boolean {
        if (srcFilePaths == null || zipFilePath == null) return false
        var zos: ZipOutputStream? = null
        return try {
            zos = ZipOutputStream(FileOutputStream(zipFilePath))
            for (srcFile in srcFilePaths) {
                if (!zipFile(getFileByPath(srcFile), "", zos, comment)) return false
            }
            true
        } finally {
            if (zos != null) {
                zos.finish()
                zos.close()
            }
        }
    }
    /**
     * Zip the files.
     *
     * @param srcFiles The source of files.
     * @param zipFile  The ZIP file.
     * @param comment  The comment.
     * @return `true`: success<br></br>`false`: fail
     * @throws IOException if an I/O error has occurred
     */
    /**
     * Zip the files.
     *
     * @param srcFiles The source of files.
     * @param zipFile  The ZIP file.
     * @return `true`: success<br></br>`false`: fail
     * @throws IOException if an I/O error has occurred
     */
    @JvmOverloads
    @Throws(IOException::class)
    fun zipFiles(srcFiles: Collection<File?>?, zipFile: File?, comment: String? = null): Boolean {
        if (srcFiles == null || zipFile == null) return false
        var zos: ZipOutputStream? = null
        return try {
            zos = ZipOutputStream(FileOutputStream(zipFile))
            for (srcFile in srcFiles) {
                if (!zipFile(srcFile, "", zos, comment)) return false
            }
            true
        } finally {
            if (zos != null) {
                zos.finish()
                zos.close()
            }
        }
    }

    /**
     * Zip the file.
     *
     * @param srcFilePath The path of source file.
     * @param zipFilePath The path of ZIP file.
     * @return `true`: success<br></br>`false`: fail
     * @throws IOException if an I/O error has occurred
     */
    @Throws(IOException::class)
    fun zipFile(srcFilePath: String, zipFilePath: String): Boolean {
        return zipFile(getFileByPath(srcFilePath), getFileByPath(zipFilePath), null)
    }

    /**
     * Zip the file.
     *
     * @param srcFilePath The path of source file.
     * @param zipFilePath The path of ZIP file.
     * @param comment     The comment.
     * @return `true`: success<br></br>`false`: fail
     * @throws IOException if an I/O error has occurred
     */
    @Throws(IOException::class)
    fun zipFile(srcFilePath: String, zipFilePath: String, comment: String?): Boolean {
        return zipFile(getFileByPath(srcFilePath), getFileByPath(zipFilePath), comment)
    }
    /**
     * Zip the file.
     *
     * @param srcFile The source of file.
     * @param zipFile The ZIP file.
     * @param comment The comment.
     * @return `true`: success<br></br>`false`: fail
     * @throws IOException if an I/O error has occurred
     */
    /**
     * Zip the file.
     *
     * @param srcFile The source of file.
     * @param zipFile The ZIP file.
     * @return `true`: success<br></br>`false`: fail
     * @throws IOException if an I/O error has occurred
     */
    @JvmOverloads
    @Throws(IOException::class)
    fun zipFile(srcFile: File?, zipFile: File?, comment: String? = null): Boolean {
        if (srcFile == null || zipFile == null || !srcFile.exists()) return false
        var zos: ZipOutputStream? = null
        return try {
            zos = ZipOutputStream(FileOutputStream(zipFile))
            zipFile(srcFile, "", zos, comment)
        } finally {
            zos?.close()
        }
    }

    @Throws(IOException::class)
    private fun zipFile(srcFile: File?, rootPath: String, zos: ZipOutputStream,
                        comment: String?): Boolean {
        var rPath = rootPath
        rPath = rPath + (if (isSpace(rPath)) "" else File.separator) + srcFile!!.name
        if (srcFile.isDirectory) {
            val fileList = srcFile.listFiles()
            if (fileList == null || fileList.isEmpty()) {
                val entry = ZipEntry("$rPath/")
                entry.comment = comment
                zos.putNextEntry(entry)
                zos.closeEntry()
            } else {
                for (file in fileList) {
                    if (!zipFile(file, rPath, zos, comment)) return false
                }
            }
        } else {
            var inputStream: InputStream? = null
            try {
                inputStream = BufferedInputStream(FileInputStream(srcFile))
                val entry = ZipEntry(rPath)
                entry.comment = comment
                zos.putNextEntry(entry)
                val buffer = ByteArray(BUFFER_LEN)
                var len: Int
                while (inputStream.read(buffer, 0, BUFFER_LEN).also { len = it } != -1) {
                    zos.write(buffer, 0, len)
                }
                zos.closeEntry()
            } finally {
                inputStream?.close()
            }
        }
        return true
    }

    /**
     * Unzip the file.
     *
     * @param zipFilePath The path of ZIP file.
     * @param destDirPath The path of destination directory.
     * @return the unzipped files
     * @throws IOException if unzip unsuccessfully
     */
    @Throws(IOException::class)
    fun unzipFile(zipFilePath: String, destDirPath: String): List<File>? {
        return unzipFileByKeyword(zipFilePath, destDirPath, null)
    }

    /**
     * Unzip the file by keyword.
     *
     * @param zipFilePath The path of ZIP file.
     * @param destDirPath The path of destination directory.
     * @param keyword     The keyboard.
     * @return the unzipped files
     * @throws IOException if unzip unsuccessfully
     */
    @Throws(IOException::class)
    fun unzipFileByKeyword(zipFilePath: String, destDirPath: String,
                           keyword: String?): List<File>? {
        return unzipFileByKeyword(getFileByPath(zipFilePath), getFileByPath(destDirPath), keyword)
    }

    /**
     * Unzip the file by keyword.
     *
     * @param zipFile The ZIP file.
     * @param destDir The destination directory.
     * @param keyword The keyboard.
     * @return the unzipped files
     * @throws IOException if unzip unsuccessfully
     */
    @Throws(IOException::class)
    fun unzipFileByKeyword(zipFile: File?, destDir: File?, keyword: String?): List<File>? {
        if (zipFile == null || destDir == null) return null
        val files: MutableList<File> = ArrayList()
        val zf = ZipFile(zipFile)
        val entries: Enumeration<*> = zf.entries()
        if (isSpace(keyword)) {
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement() as ZipEntry
                val entryName = entry.name
                if (entryName.contains("../")) {
                    Log.e("ZipUtils", "it's dangerous!")
                    return files
                }
                if (!unzipChildFile(destDir, files, zf, entry, entryName)) return files
            }
        } else {
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement() as ZipEntry
                val entryName = entry.name
                if (entryName.contains("../")) {
                    Log.e("ZipUtils", "it's dangerous!")
                    return files
                }
                if (entryName.contains(keyword!!)) {
                    if (!unzipChildFile(destDir, files, zf, entry, entryName)) return files
                }
            }
        }
        return files
    }

    private fun getFileByPath(filePath: String): File? {
        return if (isSpace(filePath)) null else File(filePath)
    }

    private fun isSpace(s: String?): Boolean {
        if (s == null) return true
        var i = 0
        val len = s.length
        while (i < len) {
            if (!Character.isWhitespace(s[i])) {
                return false
            }
            ++i
        }
        return true
    }

    @Throws(IOException::class)
    private fun unzipChildFile(destDir: File, files: MutableList<File>, zf: ZipFile,
                               entry: ZipEntry, entryName: String): Boolean {
        val filePath = destDir.toString() + File.separator + entryName
        val file = File(filePath)
        files.add(file)
        if (entry.isDirectory) {
            if (!createOrExistsDir(file)) return false
        } else {
            if (!createOrExistsFile(file)) return false
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                inputStream = BufferedInputStream(zf.getInputStream(entry))
                outputStream = BufferedOutputStream(FileOutputStream(file))
                val buffer = ByteArray(BUFFER_LEN)
                var len: Int
                while (inputStream.read(buffer).also { len = it } != -1) {
                    outputStream.write(buffer, 0, len)
                }
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        }
        return true
    }

    private fun createOrExistsDir(file: File?): Boolean {
        return file != null && if (file.exists()) file.isDirectory else file.mkdirs()
    }

    private fun createOrExistsFile(file: File?): Boolean {
        if (file == null) return false
        if (file.exists()) return file.isFile
        return if (!createOrExistsDir(file.parentFile)) false else try {
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Unzip the file.
     *
     * @param zipFile The ZIP file.
     * @param destDir The destination directory.
     * @return the unzipped files
     * @throws IOException if unzip unsuccessfully
     */
    @Throws(IOException::class)
    fun unzipFile(zipFile: File?, destDir: File?): List<File>? {
        return unzipFileByKeyword(zipFile, destDir, null)
    }

    /**
     * Return the files' path in ZIP file.
     *
     * @param zipFilePath The path of ZIP file.
     * @return the files' path in ZIP file
     * @throws IOException if an I/O error has occurred
     */
    @Throws(IOException::class)
    fun getFilesPath(zipFilePath: String): List<String>? {
        return getFilesPath(getFileByPath(zipFilePath))
    }

    /**
     * Return the files' path in ZIP file.
     *
     * @param zipFile The ZIP file.
     * @return the files' path in ZIP file
     * @throws IOException if an I/O error has occurred
     */
    @Throws(IOException::class)
    fun getFilesPath(zipFile: File?): List<String>? {
        if (zipFile == null) return null
        val paths: MutableList<String> = ArrayList()
        val entries: Enumeration<*> = ZipFile(zipFile).entries()
        while (entries.hasMoreElements()) {
            paths.add((entries.nextElement() as ZipEntry).name)
        }
        return paths
    }

    /**
     * Return the files' comment in ZIP file.
     *
     * @param zipFilePath The path of ZIP file.
     * @return the files' comment in ZIP file
     * @throws IOException if an I/O error has occurred
     */
    @Throws(IOException::class)
    fun getComments(zipFilePath: String): List<String>? {
        return getComments(getFileByPath(zipFilePath))
    }

    /**
     * Return the files' comment in ZIP file.
     *
     * @param zipFile The ZIP file.
     * @return the files' comment in ZIP file
     * @throws IOException if an I/O error has occurred
     */
    @Throws(IOException::class)
    fun getComments(zipFile: File?): List<String>? {
        if (zipFile == null) return null
        val comments: MutableList<String> = ArrayList()
        val entries: Enumeration<*> = ZipFile(zipFile).entries()
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement() as ZipEntry
            comments.add(entry.comment)
        }
        return comments
    }

    /**
     * 压缩
     */
    @Throws(IOException::class)
    fun compress(str: String?): String? {
        if (str == null || str.isEmpty()) {
            return str
        }
        val out = ByteArrayOutputStream()
        val gzip = GZIPOutputStream(out)
        gzip.write(str.toByteArray(charset("UTF-8")))
        gzip.close()
        return out.toString("ISO-8859-1")
    }

}