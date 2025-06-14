package com.ethan.mediapicker.viewModel

import android.Manifest
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.net.toUri
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ResourceUtils
import com.blankj.utilcode.util.UriUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.ethan.imagehandler.NCNNFaceDetectUtils
import com.ethan.imagehandler.bean.FaceBean
import com.ethan.mediapicker.NeedCameraDialog
import com.ethan.mediapicker.db.IFaceImageStorage
import com.github.penfeizhou.animation.loader.FileLoader
import com.github.penfeizhou.animation.webp.WebPDrawable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Path
import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


/**
 * @author  PengHaiChen
 * @date    2024/1/23 15:44
 * @email   penghaichen@tenorshare.cn
 */
class MediaPickerViewModel : ViewModel() {

    companion object {
        const val AllCode = "22b4c053-140c-4276-83f8-7aebf245f539"
    }


    var exampleList = MutableLiveData<List<String>>()

    /**
     * 固有Item目前，存放simple和takePhoto和sample
     */
    val intrinsicItem: List<MediaBean>
        get() {
            val list = exampleList.value?.map {
                MediaBean(UriUtils.file2Uri(File(copyAssetToCache(it))), MediaPickerType.SAMPLE, UUID.randomUUID().toString())
            } ?: listOf()
            return listOf(MediaBean(UriUtils.res2Uri("drawable/icon_take_photo"), MediaPickerType.TAKE_PHOTO, "TP"), *list.toTypedArray())
        }


    // 普通媒体列表
    private val archiveMediaBean = CopyOnWriteArrayList<MediaBean>()
    // 人像媒体列表
    private val portraitMediaBean = CopyOnWriteArrayList<MediaBean>()
    
    var mediaList: MutableLiveData<List<MediaBean>> = MutableLiveData()

    /**
     * @param jump2Setting 如果没有权限，跳转到设置页面
     * @param onlyCheck 仅检查
     */
    suspend fun checkPhotoPermission(onlyCheck: Boolean = true, jump2Setting: Boolean = false) = suspendCoroutine { suspendCoroutine ->
        val photoPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            listOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }.toTypedArray()
        val isA14 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
        if (isA14) {
            if (PermissionUtils.isGranted(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)) {
                suspendCoroutine.resume(true)
                return@suspendCoroutine
            }
        }
        val granted = PermissionUtils.isGranted(*photoPermission)
        if (granted) {
            suspendCoroutine.resume(true)
        } else {
            if (onlyCheck) {
                suspendCoroutine.resume(false)
                return@suspendCoroutine
            }
            PermissionUtils.permission(*photoPermission).callback { isAllGranted, _, deniedForever, _ ->
                suspendCoroutine.resume(isAllGranted)
                if (jump2Setting) {
                    if (deniedForever.isNotEmpty()) PermissionUtils.launchAppDetailsSettings()
                }
            }.request()
        }
    }

    suspend fun checkVideoPermission(onlyCheck: Boolean = true, jump2Setting: Boolean = false) = suspendCoroutine { suspendCoroutine ->
        val photoPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(Manifest.permission.READ_MEDIA_VIDEO)
        } else {
            listOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }.toTypedArray()
        val isA14 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
        if (isA14) {
            if (PermissionUtils.isGranted(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)) {
                if (!PermissionUtils.isGranted(*photoPermission)) {
                    suspendCoroutine.resume(false)
                    return@suspendCoroutine
                }
                suspendCoroutine.resume(true)
                return@suspendCoroutine
            }
        }
        val granted = PermissionUtils.isGranted(*photoPermission)
        if (granted) {
            suspendCoroutine.resume(true)
        } else {
            if (onlyCheck) {
                suspendCoroutine.resume(false)
                return@suspendCoroutine
            }
            PermissionUtils.permission(*photoPermission).callback { isAllGranted, _, deniedForever, _ ->
                suspendCoroutine.resume(isAllGranted)
                if (jump2Setting) {
                    if (deniedForever.isNotEmpty()) PermissionUtils.launchAppDetailsSettings()
                }
            }.request()
        }
    }


    private val needCameraDialog by lazy {
        NeedCameraDialog()
    }

    suspend fun checkCameraPermission(fragmentManager: FragmentManager) = suspendCoroutine<Boolean> { suspendCoroutine ->
        PermissionUtils.permission(Manifest.permission.CAMERA).callback { isAllGranted, _, deniedForever, _ ->
            if (isAllGranted) {
                suspendCoroutine.resume(true)
            } else {
                needCameraDialog.apply {
                    gotoSettingClick = {
                        PermissionUtils.launchAppDetailsSettings()
                    }
                    show(fragmentManager, "needCameraDialog")
                }
                suspendCoroutine.resume(false)
            }
        }.request()
    }

    fun getArchiveMediaBean(): List<MediaBean> {
        return archiveMediaBean
    }

    fun getMediaDir(): Map<String, List<MediaBean>> {
        val map = mutableMapOf<String, MutableList<MediaBean>>()
        map[AllCode] = archiveMediaBean.toMutableList()
        archiveMediaBean.forEach {
            if (map.contains(it.folderName)) {
                map[it.folderName]?.add(it)
            } else {
                map[it.folderName] = mutableListOf(it)
            }
        }
        return map
    }

    var currDirPath: MutableLiveData<String> = MutableLiveData(AllCode)

    fun refreshMediaByFolderName(folderName: String, isVideo: Boolean = false) {
        val listMediaBean = mutableListOf<MediaBean>()
        if (!isVideo) {
            listMediaBean.addAll(intrinsicItem.toTypedArray())
        }
        if (folderName == AllCode) {
            listMediaBean.addAll(archiveMediaBean)
        } else {
            listMediaBean.addAll(archiveMediaBean.filter { it.folderName == folderName })
        }
        mediaList.postValue(listMediaBean)
        currDirPath.postValue(folderName)
    }

    fun getMedia(context: Context, needFilter: Boolean = false) {
        // 不清空列表，直接使用现有数据
        val currentIntrinsicItems = intrinsicItem
        
        viewModelScope.launch(Dispatchers.Default) {
            var queryTime = System.currentTimeMillis()
            val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            val query = context.contentResolver.query(
                contentUri, 
                arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED),
                MediaStore.MediaColumns.SIZE + ">0", 
                null, 
                "${MediaStore.Images.Media.DATE_ADDED} DESC"
            )
            
            // 临时列表用于收集新数据
            val tempList = mutableListOf<MediaBean>()
            
            query?.let {
                while (it.moveToNext()) {
                    val data = it.getString(0)
                    val dateAdded = it.getString(1)
                    val dirName = FileUtils.getDirName(data) ?: "Unknown"
                    
                    // 检查路径是否有效
                    if (data.isNullOrEmpty()) continue
                    if (data.isEmpty()) continue
                    val file = File(data)
                    if (!file.exists()) continue
                    if (FileUtils.getFileExtension(data)=="gif"||FileUtils.getFileExtension(data)=="GIF") continue
                    try {
                        tempList.add(
                            MediaBean(
                                file.toUri(), 
                                MediaPickerType.IMAGE,
                                "", 
                                folderName = dirName, 
                                dateAdded.toLongOrNull() ?: 0
                            )
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            query?.close()
            
            // 更新archiveMediaBean
            archiveMediaBean.clear()
            archiveMediaBean.addAll(tempList)
            
            queryTime = System.currentTimeMillis() - queryTime
            Log.i("TAG", "getMedia: 耗时是${queryTime}")

            // 构建最终显示列表
            val finalList = ArrayList<MediaBean>()
            
            // 根据是否有最近使用来决定intrinsicItem的位置
            val filteredList = if (needFilter) {
                if (currDirPath.value == AllCode) {
                    archiveMediaBean
                } else {
                    archiveMediaBean.filter { it.folderName == currDirPath.value }
                }
            } else {
                archiveMediaBean
            }
            
            // 如果有最近使用的图片，就把intrinsicItem放在最前面
            if (filteredList.isNotEmpty()) {
                finalList.addAll(currentIntrinsicItems)
                finalList.addAll(filteredList)
            } else {
                // 如果没有最近使用的图片，就把intrinsicItem放在最后面
                finalList.addAll(filteredList)
                finalList.addAll(currentIntrinsicItems)
            }
            
            mediaList.postValue(finalList)
        }
    }



    fun getVideoMedia(context: Context, needFilter: Boolean = false) {
        archiveMediaBean.clear()
        val listMediaBean = mutableListOf<MediaBean>()
        
        viewModelScope.launch(Dispatchers.Default) {
            var queryTIme = System.currentTimeMillis()
            val contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

            val query = context.contentResolver.query(contentUri,
                arrayOf(MediaStore.Video.Media.DATA, MediaStore.Video.Media.DATE_ADDED, MediaStore.Video.Media.DURATION),
                MediaStore.MediaColumns.SIZE + ">0", null, "${MediaStore.Images.Media.DATE_ADDED} DESC")
            query?.let {
                while (it.moveToNext()) {
                    val data = it.getString(0)
                    val dateAdded = it.getString(1)
                    val duration = it.getLong(2) // 获取视频时长
                    val dirName = FileUtils.getDirName(data)
                    archiveMediaBean.add(
                        MediaBean(File(data).toUri(), MediaPickerType.VIDEO, "", folderName = dirName, dateAdded.toLongOrNull() ?: 0,
                            duration))
                }
            }
            query?.close()
            queryTIme = System.currentTimeMillis() - queryTIme
            Log.i("TAG", "getMedia: 耗时是${queryTIme}")

            // 视频模式下只显示视频列表，不显示intrinsicItem
            if (needFilter) {
                if (currDirPath.value == AllCode) {
                    listMediaBean.addAll(archiveMediaBean)
                } else {
                    listMediaBean.addAll(archiveMediaBean.filter { it.folderName == currDirPath.value })
                }
            } else {
                listMediaBean.addAll(archiveMediaBean)
            }
            mediaList.postValue(listMediaBean)
        }
    }


    fun showWithNoPermission() {
        mediaList.postValue(mutableListOf(*intrinsicItem.toTypedArray(), MediaBean(Uri.EMPTY, MediaPickerType.NO_PERMISSION, "NP")))
    }

    fun showWithNoVideoPermission() {
        mediaList.postValue(mutableListOf(MediaBean(Uri.EMPTY, MediaPickerType.NO_PERMISSION, "NP")))
    }


    suspend fun examineChooseMedia(context: Context?, item: MediaBean?) = suspendCoroutine<MediaBean?> { suspendCoroutine ->
        if (item == null || context == null) {
            suspendCoroutine.resume(null)
            return@suspendCoroutine
        }

        try {
            val file = UriUtils.uri2File(item.mediaPath)
            if (!file.exists()) {
                suspendCoroutine.resume(null)
                return@suspendCoroutine
            }

            val externalCacheDir = PathUtils.getExternalAppCachePath()
            val cachePath = "/safe_image_convert_cache/"
            val parentPath = externalCacheDir + cachePath
            FileUtils.createOrExistsDir(parentPath)

            when (file.extension.uppercase()) {
                "GIF" -> {
                    Glide.with(context).asGif().load(file.absolutePath).into(object : CustomTarget<GifDrawable>() {
                        override fun onResourceReady(resource: GifDrawable, transition: Transition<in GifDrawable>?) {
                            try {
                                val dstPath = parentPath + FileUtils.getFileNameNoExtension(file) + ".jpeg"
                                val dstFile = File(dstPath)
                                val outputStream = dstFile.outputStream()
                                resource.firstFrame.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                                outputStream.close()
                                suspendCoroutine.resume(
                                    MediaBean(dstFile.toUri(), item.mediaType, "", folderName = item.folderName, item.dateAdded))
                            } catch (e: Exception) {
                                e.printStackTrace()
                                suspendCoroutine.resume(null)
                            }
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                        }

                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            super.onLoadFailed(errorDrawable)
                            suspendCoroutine.resume(null)
                        }
                    })
                }

                "WEBP" -> {
                    try {
                        val fileLoader = FileLoader(file.absolutePath)
                        val webpDrawable = WebPDrawable(fileLoader)
                        val frameBitmap = webpDrawable.frameSeqDecoder.getFrameBitmap(0)
                        val dstPath = parentPath + FileUtils.getFileNameNoExtension(file) + ".jpeg"
                        val dstFile = File(dstPath)
                        val outputStream = dstFile.outputStream()
                        frameBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                        outputStream.close()
                        frameBitmap.recycle()
                        suspendCoroutine.resume(
                            MediaBean(dstFile.toUri(), item.mediaType, "", folderName = item.folderName, item.dateAdded))
                    } catch (e: Exception) {
                        e.printStackTrace()
                        suspendCoroutine.resume(null)
                    }
                }

                else -> {
                    try {
                        // 处理图片可能带旋转角以及损坏的问题
                        if (checkImgDamage(file.absolutePath)) {
                            suspendCoroutine.resume(null)
                            return@suspendCoroutine
                        }
                        viewModelScope.launch(Dispatchers.Default) {
                            val fixImage = fitImageOrientation(context, file.absolutePath)
                            if (fixImage.isNotEmpty()) {
                                suspendCoroutine.resume(
                                    MediaBean(File(fixImage).toUri(), item.mediaType, "", folderName = item.folderName, item.dateAdded))
                            } else {
                                suspendCoroutine.resume(null)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        suspendCoroutine.resume(null)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            suspendCoroutine.resume(null)
        }
    }


    suspend fun examineChooseMediaVideo(context: Context?, item: MediaBean?) = suspendCoroutine<MediaBean?> { suspendCoroutine ->
        if (item == null || item.mediaPath.path.isNullOrEmpty() || context == null) {
            suspendCoroutine.resume(null)
            return@suspendCoroutine
        }
        viewModelScope.launch(Dispatchers.Default) {
            val videoFile = UriUtils.uri2File(item.mediaPath)
            suspendCoroutine.resume(
                MediaBean(UriUtils.file2Uri(videoFile), MediaPickerType.VIDEO, "", folderName = item.folderName, item.dateAdded,
                    duration = item.duration))
        }

    }


    /**
     * 检查图片是否损坏
     *
     * @param filePath
     * @return
     */
    private fun checkImgDamage(filePath: String): Boolean {
        val options: BitmapFactory.Options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, options)
        return options.mCancel || options.outWidth == -1 || options.outHeight == -1
    }

    /**
     * 修复图片需旋转角问题,输出路径
     */
    private suspend fun fitImageOrientation(context: Context, imgPath: String): String = suspendCoroutine { suspendCoroutine ->
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

    private fun copyAssetToCache(fileName: String): String {
        val cacheFile = File("${PathUtils.getExternalAppCachePath()}/${fileName}")
        ResourceUtils.copyFileFromAssets(fileName, cacheFile.path)
        return cacheFile.absolutePath
    }

    // 人脸图片存储接口
    private var faceImageStorage: IFaceImageStorage? = null
    // 人脸检测工具类
    private val faceDetectUtils = NCNNFaceDetectUtils.getInstance()
    // 是否正在扫描标记
    private var isScanning = false
    // 是否暂停扫描标记
    private var isPaused = false
    // 记录上次扫描的图片路径，用于恢复扫描时的位置
    private var lastScannedPath: String? = null
    // 保存媒体查询的游标，用于恢复扫描
    private var lastQueryCursor: Cursor? = null

    /**
     * 设置人脸图片存储实现
     * @param storage 存储实现接口
     */
    fun setFaceImageStorage(storage: IFaceImageStorage) {
        faceImageStorage = storage
    }

    /**
     * 获取人像媒体
     */
    fun getPortraitMedia(context: Context, needFilter: Boolean = false) {
        // 如果正在扫描且未暂停，则不重复扫描
        if (isScanning && !isPaused) return
        
        // 如果是暂停状态，则恢复扫描
        if (isPaused && lastScannedPath != null) {
            resumeScanning(context)
            return
        }
        
        // 初始化扫描状态
        isScanning = true
        isPaused = false
        lastScannedPath = null
        lastQueryCursor?.close()
        lastQueryCursor = null
        
        // 只有在当前标签是人像时才更新UI
        if (lastSelectedTab == 2) {
            // 显示当前已有的人像列表
            mediaList.postValue(ArrayList(portraitMediaBean))
        }
        
        viewModelScope.launch(Dispatchers.Default) {
            var queryTime = System.currentTimeMillis()
            val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            
            // 构建查询条件，根据选择的文件夹路径进行过滤
            val selection = if (currDirPath.value != AllCode) {
                "${MediaStore.MediaColumns.SIZE}>0 AND ${MediaStore.Images.Media.DATA} LIKE ?"
            } else {
                "${MediaStore.MediaColumns.SIZE}>0"
            }
            
            // 构建查询参数
            val selectionArgs = if (currDirPath.value != AllCode) {
                arrayOf("${currDirPath.value}%")
            } else {
                null
            }
            
            val query = context.contentResolver.query(
                contentUri,
                arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED),
                selection,
                selectionArgs,
                "${MediaStore.Images.Media.DATE_ADDED} DESC"
            )
            
            lastQueryCursor = query
            query?.let { cursor ->
                scanImages(context, cursor, null)
            }
            
            queryTime = System.currentTimeMillis() - queryTime
            Log.i("TAG", "getPortraitMedia: 耗时是${queryTime}")
        }
    }

    /**
     * 暂停扫描
     */
    fun pauseScanning() {
        if (isScanning) {
            isPaused = true
            // 记录当前扫描位置
            lastQueryCursor?.let { cursor ->
                if (!cursor.isClosed && cursor.position >= 0) {
                    lastScannedPath = cursor.getString(0)
                }
            }
        }
    }

    /**
     * 恢复扫描
     */
    private fun resumeScanning(context: Context) {
        if (isPaused && lastScannedPath != null) {
            isPaused = false
            // 重新开始扫描，从上次暂停的位置继续
            viewModelScope.launch(Dispatchers.Default) {
                val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                val selection = if (currDirPath.value != AllCode) {
                    "${MediaStore.MediaColumns.SIZE}>0 AND ${MediaStore.Images.Media.DATA} LIKE ?"
                } else {
                    "${MediaStore.MediaColumns.SIZE}>0"
                }
                val selectionArgs = if (currDirPath.value != AllCode) {
                    arrayOf("${currDirPath.value}%")
                } else {
                    null
                }
                
                val query = context.contentResolver.query(
                    contentUri,
                    arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED),
                    selection,
                    selectionArgs,
                    "${MediaStore.Images.Media.DATE_ADDED} DESC"
                )
                
                lastQueryCursor?.close()
                lastQueryCursor = query
                query?.let { cursor ->
                    scanImages(context, cursor, lastScannedPath)
                }
            }
        }
    }

    /**
     * 扫描图片的具体实现
     * @param context 上下文
     * @param cursor 媒体库查询游标
     * @param startFromPath 开始扫描的路径，用于恢复扫描
     */
    private suspend fun scanImages(context: Context, cursor: Cursor, startFromPath: String?) {
        var foundStartPath = startFromPath == null
        val currentBatchResults = mutableListOf<MediaBean>()
        var batchCount = 0
        val BATCH_SIZE = 3
        
        try {
            while (!isPaused && cursor.moveToNext()) {
                val imagePath = cursor.getString(0)
                if (imagePath.isNullOrEmpty()) continue
                
                // 如果需要从特定路径开始扫描
                if (!foundStartPath) {
                    if (imagePath == startFromPath) {
                        foundStartPath = true
                    }
                    continue
                }

                // 检查数据库中是否已有记录
                val hasFace = faceImageStorage?.getFaceData(imagePath)
                if (hasFace == true) {
                    // 如果数据库中有记录为true，直接添加到当前批次
                    try {
                        val imageFile = File(imagePath)

                        if (!imageFile.exists()) continue
                        
                        currentBatchResults.add(
                            MediaBean(
                                imageFile.toUri(),
                                MediaPickerType.IMAGE,
                                "",
                                folderName = FileUtils.getDirName(imagePath) ?: "Unknown",
                                System.currentTimeMillis()
                            )
                        )
                        batchCount++
                        
                        // 达到批次大小时更新UI
                        if (batchCount >= BATCH_SIZE) {
                            updateUIWithNewResults(currentBatchResults)
                            currentBatchResults.clear()
                            batchCount = 0
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    continue
                }

                // 对未扫描的图片进行人脸检测
                try {
                    val imageFile = File(imagePath)
                    if (!imageFile.exists()) continue
                    
                    val bitmap = BitmapFactory.decodeFile(imagePath) ?: continue

                    val faceList = ArrayList<FaceBean>()
                    val hasFaceResult = faceDetectUtils.detailFaceByBitmap(bitmap, faceList, false)
                    Log.e("TAG", "scanImages: $hasFaceResult")
                    
                    if (faceList.isNotEmpty()) {
                        // 保存人脸检测结果到数据库
                        faceImageStorage?.insertFaceData(
                            imagePath,
                            System.currentTimeMillis()
                        )

                        // 添加到当前批次
                        currentBatchResults.add(
                            MediaBean(
                                imageFile.toUri(),
                                MediaPickerType.IMAGE,
                                "",
                                folderName = FileUtils.getDirName(imagePath) ?: "Unknown",
                                System.currentTimeMillis()
                            )
                        )
                        batchCount++
                        
                        // 达到批次大小时更新UI
                        if (batchCount >= BATCH_SIZE) {
                            updateUIWithNewResults(currentBatchResults)
                            currentBatchResults.clear()
                            batchCount = 0
                        }
                    }
                    
                    bitmap.recycle()
                    
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            
            // 处理最后一个不完整的批次
            if (!isPaused && currentBatchResults.isNotEmpty()) {
                updateUIWithNewResults(currentBatchResults)
            }
            
            // 扫描完成时的清理工作
            if (!isPaused) {
                isScanning = false
                lastQueryCursor?.close()
                lastQueryCursor = null
                lastScannedPath = null
            }
            
        } catch (e: Exception) {
            e.printStackTrace()
            isScanning = false
            lastQueryCursor?.close()
            lastQueryCursor = null
            lastScannedPath = null
            
            // 发生错误时也要确保显示固有项
            val errorList = ArrayList<MediaBean>()
            errorList.addAll(intrinsicItem)
            mediaList.postValue(errorList)
        }
    }

    /**
     * 更新UI显示新的扫描结果
     * @param newResults 新扫描到的结果列表
     */
    private fun updateUIWithNewResults(newResults: List<MediaBean>) {
        // 将新结果添加到portraitMediaBean
        portraitMediaBean.addAll(newResults)
        
        // 只有在当前标签是人像时才更新UI
        if (lastSelectedTab == 2) {
            mediaList.postValue(ArrayList(portraitMediaBean))
        }
    }

    /**
     * ViewModel清理时释放资源
     */
    override fun onCleared() {
        super.onCleared()
        lastQueryCursor?.close()
    }

    // 添加记录当前标签的变量
    var lastSelectedTab = 0
    
    // 更新标签
    fun updateSelectedTab(tab: Int) {
        if (lastSelectedTab != tab) {
            lastSelectedTab = tab
            
            // 根据标签显示对应的列表
            when (tab) {
                2 -> { // 切换到人像标签
                    // 如果人像列表为空且未在扫描，则开始扫描
                    if (portraitMediaBean.isEmpty() && !isScanning) {
                        isScanning = false
                        isPaused = false
                        lastScannedPath = null
                        lastQueryCursor?.close()
                        lastQueryCursor = null
                    }
                    // 显示当前人像列表
                    mediaList.postValue(ArrayList(portraitMediaBean))
                }
                1 -> { // 切换到最近使用
                    // 如果正在扫描人像，暂停扫描
                    if (isScanning && !isPaused) {
                        pauseScanning()
                    }
                    // 显示最近使用列表（包含固有项）
                    val finalList = ArrayList<MediaBean>()
                    finalList.addAll(intrinsicItem)
                    if (currDirPath.value == AllCode) {
                        finalList.addAll(archiveMediaBean)
                    } else {
                        finalList.addAll(archiveMediaBean.filter { it.folderName == currDirPath.value })
                    }
                    mediaList.postValue(finalList)
                }
            }
        }
    }


    private var recentImageStorage: IRecentImageStorage? = null

    fun setRecentImageStorage(storage: IRecentImageStorage) {
        recentImageStorage = storage
    }

    fun addRecentImage(imagePath: String) {
        recentImageStorage?.addRecentImage(imagePath)
    }


    fun ishaveSingeFace( path: String):Int
    {
        val bmp = ImageUtils.getBitmap(path)
        val faceList = ArrayList<FaceBean>()
        val hasFaceResult = faceDetectUtils.detailFaceByBitmap(bmp, faceList, false)
        Log.e("TAG", "scanImages: $hasFaceResult")

        if (faceList.isNotEmpty()) {
            return if (faceList.size>1) {
                0
            } else {
                if (((faceList[0].w<bmp.width/5)|| (faceList[0].h<bmp.height/5)))
                {
                    1
                }
                else
                {
                    2
                }

            }
        }
        return 0
    }
}

enum class MediaPickerType {
    IMAGE, VIDEO, SAMPLE, TAKE_PHOTO, NO_PERMISSION
}

/**
 * @param tag 用来判断sample，其他类型可以为空tag
 */
data class MediaBean(
    val mediaPath: Uri, val mediaType: MediaPickerType, val tag: String = "",
    var folderName: String = "",
    val dateAdded: Long = 0,
    var duration: Long = 0,
                    )

/**
 * 最近使用的图片存储接口
 */
interface IRecentImageStorage {
    fun addRecentImage(imagePath: String)
    fun getRecentImages(): List<String>
    fun clearRecentImages()
}

