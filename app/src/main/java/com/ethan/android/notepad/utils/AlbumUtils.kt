package com.ethan.android.notepad.utils

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.exifinterface.media.ExifInterface
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.BlurTransformation
import java.io.File
import java.io.IOException
import java.io.OutputStream

object AlbumUtils {

    /**
     * 图片加载：从模糊到清晰
     */
    fun loadImageBlurToClear(context: Context, url: String, imageView: ImageView) {
        //第一步：快速加载模糊的小图
        val thumbnailRequest = Glide.with(context)
            .load(url)
            .override(100) //小尺寸
            .apply(RequestOptions.bitmapTransform(BlurTransformation(25)))
        //第二步：加载高清原图
        Glide.with(context)
            .load(url)
//            .placeholder(R.mipmap.load_error_img) //默认占位图
            .thumbnail(thumbnailRequest) //绑定缩略图
            .transition(DrawableTransitionOptions.withCrossFade(4000)) //渐变效果
            .into(imageView)
    }

    // 保存图片到相册，包括迁移exif
    fun save2Album(srcPath: String?, dirName: String): File? {
        val bitmap = BitmapFactory.decodeFile(srcPath)
        val fileName = "hpImg_" + System.currentTimeMillis().toString() + ".jpg"
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (!PermissionUtils.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Log.e("ImageUtils", "save to album need storage permission")
                return null
            }
            val picDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            val destFile = File(picDir, "$dirName/$fileName")
            if (!ImageUtils.save(bitmap, destFile, Bitmap.CompressFormat.JPEG, 100, false)) {
                return null
            }
            FileUtils.notifySystemToScan(destFile)
            destFile
        } else {
            val contentValues = ContentValues()
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/*")
            val contentUri: Uri = if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            } else {
                MediaStore.Images.Media.INTERNAL_CONTENT_URI
            }
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + "/" + dirName)
            contentValues.put(MediaStore.MediaColumns.IS_PENDING, 1)
            val uri = Utils.getApp().contentResolver.insert(contentUri, contentValues) ?: return null
            var os: OutputStream? = null
            try {
                os = Utils.getApp().contentResolver.openOutputStream(uri)
                os?.let { bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, it) }
                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                Utils.getApp().contentResolver.update(uri, contentValues, null, null)
                val path = com.blankj.utilcode.util.UriUtils.uri2File(uri)
                path
            } catch (e: Exception) {
                Utils.getApp().contentResolver.delete(uri, null, null)
                e.printStackTrace()
                null
            } finally {
                try {
                    os?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }


    fun saveBitmap2Album(bitmap: Bitmap, originPath: String?, dirName: String): File? {
        val fileName = "hpImg_" + System.currentTimeMillis().toString() + ".jpg"
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (!PermissionUtils.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Log.e("ImageUtils", "save to album need storage permission")
                return null
            }
            val picDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            val destFile = File(picDir, "$dirName/$fileName")
            if (!ImageUtils.save(bitmap, destFile, Bitmap.CompressFormat.JPEG, 100, false)) {
                return null
            }
            // transferExif(originPath, destFile.absolutePath)
            FileUtils.notifySystemToScan(destFile)
            destFile
        } else {
            val contentValues = ContentValues()
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/*")
            val contentUri: Uri = if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            } else {
                MediaStore.Images.Media.INTERNAL_CONTENT_URI
            }
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + "/" + dirName)
            contentValues.put(MediaStore.MediaColumns.IS_PENDING, 1)
            val uri = Utils.getApp().contentResolver.insert(contentUri, contentValues) ?: return null
            var os: OutputStream? = null
            try {
                os = Utils.getApp().contentResolver.openOutputStream(uri)
                os?.let { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                Utils.getApp().contentResolver.update(uri, contentValues, null, null)
                val path = com.blankj.utilcode.util.UriUtils.uri2File(uri)
                // transferExif(originPath, path.absolutePath)
                path
            } catch (e: Exception) {
                Utils.getApp().contentResolver.delete(uri, null, null)
                e.printStackTrace()
                null
            } finally {
                try {
                    os?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }


    private fun transferExif(oldPath: String?, newPath: String?) {
        if (oldPath.isNullOrEmpty() || newPath.isNullOrEmpty()) return
        val oldExif = ExifInterface(oldPath)
        val newExif = ExifInterface(newPath)
        newExif.setAttribute(ExifInterface.TAG_DATETIME, oldExif.getAttribute(ExifInterface.TAG_DATETIME))
        newExif.setAttribute(ExifInterface.TAG_MAKE, oldExif.getAttribute(ExifInterface.TAG_MAKE))
        newExif.setAttribute(ExifInterface.TAG_MODEL, oldExif.getAttribute(ExifInterface.TAG_MODEL))
        newExif.setAttribute(ExifInterface.TAG_FLASH, oldExif.getAttribute(ExifInterface.TAG_FLASH))
        newExif.setAttribute(ExifInterface.TAG_IMAGE_LENGTH, oldExif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH))
        newExif.setAttribute(ExifInterface.TAG_IMAGE_WIDTH, oldExif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH))
        newExif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, oldExif.getAttribute(ExifInterface.TAG_GPS_LATITUDE))
        newExif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, oldExif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE))
        newExif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, oldExif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF))
        newExif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, oldExif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF))
        newExif.setAttribute(ExifInterface.TAG_EXPOSURE_TIME, oldExif.getAttribute(ExifInterface.TAG_EXPOSURE_TIME))
        newExif.setAttribute(ExifInterface.TAG_FOCAL_LENGTH_IN_35MM_FILM, oldExif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH_IN_35MM_FILM))
        newExif.setAttribute(ExifInterface.TAG_METERING_MODE, oldExif.getAttribute(ExifInterface.TAG_METERING_MODE))
        newExif.setAttribute(ExifInterface.TAG_MAX_APERTURE_VALUE, oldExif.getAttribute(ExifInterface.TAG_MAX_APERTURE_VALUE))
        newExif.setAttribute(ExifInterface.TAG_EXPOSURE_BIAS_VALUE, oldExif.getAttribute(ExifInterface.TAG_EXPOSURE_BIAS_VALUE))
        newExif.setAttribute(ExifInterface.TAG_EXPOSURE_INDEX, oldExif.getAttribute(ExifInterface.TAG_EXPOSURE_INDEX))
        newExif.setAttribute(ExifInterface.TAG_EXPOSURE_MODE, oldExif.getAttribute(ExifInterface.TAG_EXPOSURE_MODE))
        newExif.setAttribute(ExifInterface.TAG_EXPOSURE_PROGRAM, oldExif.getAttribute(ExifInterface.TAG_EXPOSURE_PROGRAM))
        newExif.setAttribute(ExifInterface.TAG_RECOMMENDED_EXPOSURE_INDEX, oldExif.getAttribute(ExifInterface.TAG_RECOMMENDED_EXPOSURE_INDEX))
        newExif.setAttribute(ExifInterface.TAG_RESOLUTION_UNIT, oldExif.getAttribute(ExifInterface.TAG_RESOLUTION_UNIT))
        newExif.setAttribute(ExifInterface.TAG_BRIGHTNESS_VALUE, oldExif.getAttribute(ExifInterface.TAG_BRIGHTNESS_VALUE))
        newExif.setAttribute(ExifInterface.TAG_DIGITAL_ZOOM_RATIO, oldExif.getAttribute(ExifInterface.TAG_DIGITAL_ZOOM_RATIO))
        newExif.setAttribute(ExifInterface.TAG_EXIF_VERSION, oldExif.getAttribute(ExifInterface.TAG_EXIF_VERSION))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            newExif.setAttribute(ExifInterface.TAG_F_NUMBER, oldExif.getAttribute(ExifInterface.TAG_F_NUMBER))
            newExif.setAttribute(ExifInterface.TAG_ISO_SPEED_RATINGS, oldExif.getAttribute(ExifInterface.TAG_ISO_SPEED_RATINGS))
            newExif.setAttribute(ExifInterface.TAG_SUBSEC_TIME_ORIGINAL, oldExif.getAttribute(ExifInterface.TAG_SUBSEC_TIME_ORIGINAL))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            newExif.setAttribute(ExifInterface.TAG_DATETIME_DIGITIZED, oldExif.getAttribute(ExifInterface.TAG_DATETIME_DIGITIZED))
            newExif.setAttribute(ExifInterface.TAG_SUBSEC_TIME, oldExif.getAttribute(ExifInterface.TAG_SUBSEC_TIME))
        }
        newExif.setAttribute(ExifInterface.TAG_GPS_ALTITUDE, oldExif.getAttribute(ExifInterface.TAG_GPS_ALTITUDE))
        newExif.setAttribute(ExifInterface.TAG_GPS_ALTITUDE_REF, oldExif.getAttribute(ExifInterface.TAG_GPS_ALTITUDE_REF))
        newExif.setAttribute(ExifInterface.TAG_GPS_TIMESTAMP, oldExif.getAttribute(ExifInterface.TAG_GPS_TIMESTAMP))
        newExif.setAttribute(ExifInterface.TAG_GPS_DATESTAMP, oldExif.getAttribute(ExifInterface.TAG_GPS_DATESTAMP))
        newExif.setAttribute(ExifInterface.TAG_WHITE_BALANCE, oldExif.getAttribute(ExifInterface.TAG_WHITE_BALANCE))
        newExif.setAttribute(ExifInterface.TAG_FOCAL_LENGTH, oldExif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH))
        newExif.setAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD, oldExif.getAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD))
        newExif.saveAttributes()
    }
}