package com.ethan.android.notepad.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Rect
import android.os.Environment
import android.util.Log
import android.util.TypedValue
import android.view.WindowManager
import androidx.exifinterface.media.ExifInterface
import com.blankj.utilcode.util.ImageUtils
import com.ethan.android.notepad.base.Constants
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID
import java.util.regex.Pattern
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object BitmapUtils {

    /**
     * 保存图片(原图保存/720P保存)
     * @param imgPath: 图片路径
     * @param savePath: 保存路径
     * @param saveType: 1 -> 原图保存; 2 -> 压缩到720P保存
     * @param quality: 质量参数，范围为0-100,类型2时，可控制清晰度
     * @return 是否保存成功
     */
    fun saveImg(imgPath: String, savePath: String, saveType: Int, quality: Int = 50): Boolean {
        val srcFile = File(imgPath)
        var bitmap: Bitmap? = null
        val result: Boolean
        when (saveType) {
            //原图保存
            1 -> {
                bitmap = BitmapFactory.decodeFile(srcFile.absolutePath)
                result =  bitmap?.let {
                     saveBitmap(it, savePath, 100)
                } ?: run {
                    Log.d("ethan","原图加载失败")
                    false
                }
            }
            // 压缩到720P保存
            2 -> {
                Log.d("ethan", srcFile.absolutePath)
                bitmap = BitmapFactory.decodeFile(srcFile.absolutePath)
                result =  bitmap?.let {
                    // 缩放到720P分辨率
                    val scaledBitmap = scaleBitmapTo720p(it)
                    saveBitmap(scaledBitmap, savePath, quality)
                } ?: run {
                    Log.d("ethan","原图加载失败")
                    false
                }
            }
            else -> {
                Log.d("ethan","未知的保存类型")
                result = false
            }
        }
        bitmap?.recycle() // 释放原始或缩放后的Bitmap资源
        return result
    }

    // 将Bitmap保存到指定路径
    private fun saveBitmap(bitmap: Bitmap, filePath: String, quality: Int): Boolean {
        val fos: FileOutputStream? = try {
            FileOutputStream(filePath)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
        val result = fos?.let { bitmap.compress(Bitmap.CompressFormat.JPEG, quality /*图片质量*/, it) }
        fos?.close()
        return result ?: false
    }

    /**
     * 将Bitmap按720P尺寸进行缩放
     * */
    private fun scaleBitmapTo720p(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val targetWidth = 1280 // 720P宽度对应的水平像素数
        val targetHeight = 720 // 720P高度对应的垂直像素数

        // 计算保持原图宽高比的缩放因子
        val scaleFactor =
            (targetWidth.toFloat() / width).coerceAtMost(targetHeight.toFloat() / height)

        // 直接返回缩放后的Bitmap对象即可，无需额外的质量压缩和解码过程
        return Bitmap.createScaledBitmap(
            bitmap,
            (width * scaleFactor).toInt(),
            (height * scaleFactor).toInt(),
            true
        )
    }

    /**
     * 将Bitmap按720P尺寸进行缩放
     * */
    fun scaleBitmapTo720p(path: String): Bitmap {
        val decodeFile = BitmapFactory.decodeFile(path)
        val width = decodeFile.width
        val height = decodeFile.height
        val targetWidth = 1280 // 720P宽度对应的水平像素数
        val targetHeight = 720 // 720P高度对应的垂直像素数

        // 计算保持原图宽高比的缩放因子
        val scaleFactor =
            (targetWidth.toFloat() / width).coerceAtMost(targetHeight.toFloat() / height)

        // 直接返回缩放后的Bitmap对象即可，无需额外的质量压缩和解码过程
        return Bitmap.createScaledBitmap(
            decodeFile,
            (width * scaleFactor).toInt(),
            (height * scaleFactor).toInt(),
            true
                                        )
    }

    /** 从给定的路径加载图片，并纠正旋转到正常图片 */
    fun loadBitmap(imgPath: String): String? {
        if (imgPath.isEmpty()) {
            return null
        }
        var bm = BitmapFactory.decodeFile(imgPath)
        var info = 0
        val exif: ExifInterface? = try {
            ExifInterface(imgPath)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
        var filepath = imgPath
        var isXTransverse = false
        var isYTransverse = false
        if (exif != null) { // 读取图片中相机方向信息
            val ori: Int = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
            info = when (ori) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                ExifInterface.ORIENTATION_TRANSPOSE -> {
                    isYTransverse = true
                    90
                }

                ExifInterface.ORIENTATION_TRANSVERSE -> {
                    isXTransverse = true
                    270
                }

                else -> 0
            }
        }
        if (info != 0) { // 旋转图片
            val m = Matrix()
            m.setScale(if (isXTransverse) -1F else 1F, if (isYTransverse) -1F else 1F)
            m.postRotate(info.toFloat())
            bm = Bitmap.createBitmap(bm!!, 0, 0, bm.width, bm.height, m, true)
            filepath = Constants.PATH_IMAGE + "/newPath_" + System.currentTimeMillis() + ".JPEG"
            ImageUtils.save(bm, filepath, Bitmap.CompressFormat.JPEG, 100)
        }
        return filepath
    }

    /** 从给定的路径加载图片，并指定是否自动旋转方向 */
    fun loadBitmap2Bmp(imgPath: String?): Bitmap? {
        if (imgPath.isNullOrEmpty()) {
            return null
        }
        var bm = BitmapFactory.decodeFile(imgPath)
        var info = 0
        val exif: ExifInterface? = try {
            ExifInterface(imgPath)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
        var isXTransverse = false
        var isYTransverse = false
        if (exif != null) { // 读取图片中相机方向信息
            val ori: Int = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
            info = when (ori) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                ExifInterface.ORIENTATION_TRANSPOSE -> {
                    isYTransverse = true
                    90
                }

                ExifInterface.ORIENTATION_TRANSVERSE -> {
                    isXTransverse = true
                    90
                }

                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> {
                    isXTransverse = true
                    0
                }

                ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                    isYTransverse = true
                    0
                }

                else -> 0
            }
        }
        if (info != 0 || isXTransverse || isYTransverse) { // 旋转图片
            val m = Matrix()
            m.setScale(if (isXTransverse) -1F else 1F, if (isYTransverse) -1F else 1F)
            m.postRotate(info.toFloat())
            bm = Bitmap.createBitmap(bm!!, 0, 0, bm.width, bm.height, m, true)
        }
        return bm
    }

    /** 从给定的路径加载图片，并指定是否自动旋转方向 */
    fun loadBitmapToPath(imgPath: String?, block: (isRotate: Boolean) -> Unit): Bitmap? {
        if (imgPath.isNullOrEmpty()) {
            return null
        }
        var bm = BitmapFactory.decodeFile(imgPath)
        var info = 0
        val exif: ExifInterface? = try {
            ExifInterface(imgPath)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
        var isXTransverse = false
        var isYTransverse = false
        if (exif != null) { // 读取图片中相机方向信息
            val ori: Int = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
            info = when (ori) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                ExifInterface.ORIENTATION_TRANSPOSE -> {
                    isYTransverse = true
                    90
                }

                ExifInterface.ORIENTATION_TRANSVERSE -> {
                    isXTransverse = true
                    90
                }

                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> {
                    isXTransverse = true
                    0
                }

                ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                    isYTransverse = true
                    0
                }

                else -> 0
            }
        }
        if (info != 0 || isXTransverse || isYTransverse) { // 旋转图片
            val m = Matrix()
            m.setScale(if (isXTransverse) -1F else 1F, if (isYTransverse) -1F else 1F)
            m.postRotate(info.toFloat())
            bm = Bitmap.createBitmap(bm!!, 0, 0, bm.width, bm.height, m, true)
        }
        return bm
    }

    /**
     * 检查图片是否损坏
     *
     * @param filePath
     * @return
     */
    fun checkImgDamage(filePath: String): Boolean {
        val options: BitmapFactory.Options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, options)
        return options.mCancel || options.outWidth == -1 || options.outHeight == -1
    }

    /**
     * 修复图片需旋转角问题,输出路径
     */
    suspend fun fitImageOrientation(context: Context, imgPath: String): String = suspendCoroutine { suspendCoroutine ->
        if (imgPath.isEmpty()) {
            suspendCoroutine.resume("")
            return@suspendCoroutine
        }
        val bm = BitmapFactory.decodeFile(imgPath)
        var info = 0
        val exif: ExifInterface? = try {
            ExifInterface(imgPath)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
        var isXTransverse = false
        var isYTransverse = false
        if (exif != null) { // 读取图片中相机方向信息
            val ori: Int = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
            info = when (ori) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                ExifInterface.ORIENTATION_TRANSPOSE -> {
                    isYTransverse = true
                    90
                }

                ExifInterface.ORIENTATION_TRANSVERSE -> {
                    isXTransverse = true
                    90
                }

                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> {
                    isXTransverse = true
                    0
                }

                ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                    isYTransverse = true
                    0
                }

                else -> {
                    suspendCoroutine.resume(imgPath)
                    return@suspendCoroutine
                }
            }
            val m = Matrix()
            m.setScale(if (isXTransverse) -1F else 1F, if (isYTransverse) -1F else 1F)
            m.postRotate(info.toFloat())
            val fixBitmap = Bitmap.createBitmap(bm!!, 0, 0, bm.width, bm.height, m, true)
            val imagePathDir = "${context.getExternalFilesDir(Environment.DIRECTORY_DCIM)}/orientation_fix"
            File(imagePathDir).mkdir()
            val imagePath = File("${imagePathDir}/${UUID.randomUUID()}.${File(imgPath).extension}").apply { this.createNewFile() }
            val coverPath = fixBitmap.let {
                if (it == null) return@let imgPath
                val fileOutputStream = FileOutputStream(imagePath)
                it.compress(when (File(imgPath).extension.uppercase()) {
                    "JPEG|JPG" -> Bitmap.CompressFormat.JPEG
                    "PNG" -> Bitmap.CompressFormat.PNG
                    else -> Bitmap.CompressFormat.JPEG
                }, 100, fileOutputStream)
                fileOutputStream.close()
                it.recycle()
                imagePath.absolutePath
            }
            suspendCoroutine.resume(coverPath)
            return@suspendCoroutine
        } else {
            suspendCoroutine.resume(imgPath)
            return@suspendCoroutine
        }
    }

    fun addImageWatermark(context: Context, src: Bitmap, watermark: Bitmap): Bitmap? {
        val width = dp2px(context, 130f)
        val height = dp2px(context, 41f)
        val right = dp2px(context, 17f)
        val bottom = dp2px(context, 12f)
        val viewMaxWidth = getScreenWidth(context)
        val viewMaxHeight = getScreenHeight(context) - dp2px(context, 162f)
        val ret = src.copy(src.config, true)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.alpha = 255
        val canvas = Canvas(ret)
        val rectSrc = Rect(0, 0, watermark.width, watermark.height)
        val drawWidth: Int
        val drawHeight: Int
        val drawRight: Int
        val drawBottom: Int
        val scale = if (src.width >= src.height) viewMaxWidth.toFloat() / src.width.toFloat() else viewMaxHeight.toFloat() / src.height.toFloat()
        drawWidth = (width.toFloat() / scale).toInt()
        drawHeight = (height.toFloat() / scale).toInt()
        drawRight = (right.toFloat() / scale).toInt()
        drawBottom = (bottom.toFloat() / scale).toInt()

        val x = Integer.max(0, src.width - drawWidth - drawRight)
        val y = Integer.max(0, src.height - drawHeight - drawBottom)
        val rectDest = Rect(x, y, x + drawWidth, y + drawHeight)
        canvas.drawBitmap(watermark, rectSrc, rectDest, paint)
        return ret
    }

    fun dp2px(context: Context?, dpVal: Float): Int {
        return TypedValue
            .applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, context?.resources?.displayMetrics)
            .toInt()
    }

    fun getScreenWidth(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val point = Point()
        wm.defaultDisplay.getRealSize(point)
        return point.x
    }

    fun getScreenHeight(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val point = Point()
        wm.defaultDisplay.getRealSize(point)
        return point.y
    }

    fun checkBitmapOutMimeType(outMimeType: String, extension: String): Boolean {
        when (outMimeType) {
            "image/jpeg" -> {
                val pattern = ".*(jfif|jfif-tbnl|jpe|jpg|jpeg).*"
                return Pattern.matches(pattern, extension.lowercase())
            }

            "image/png" -> {
                val pattern = ".*(png).*"
                return Pattern.matches(pattern, extension.lowercase())
            }

            "image/webp" -> {
                val pattern = ".*(webp).*"
                return Pattern.matches(pattern, extension.lowercase())
            }

            "image/heif" -> {
                val pattern = ".*(heif).*"
                return Pattern.matches(pattern, extension.lowercase())
            }

            "image/heic" -> {
                val pattern = ".*(heic).*"
                return Pattern.matches(pattern, extension.lowercase())
            }

            else -> {
                return false
            }

        }
    }

}