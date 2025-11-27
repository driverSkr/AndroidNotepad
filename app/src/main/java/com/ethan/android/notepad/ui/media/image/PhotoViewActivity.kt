package com.ethan.android.notepad.ui.media.image

import android.graphics.Matrix
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import com.bumptech.glide.Glide
import com.ethan.android.notepad.base.BaseActivityVBind
import com.ethan.android.notepad.databinding.ActivityPhotoViewBinding
import io.getstream.photoview.OnMatrixChangedListener
import androidx.core.view.isVisible

class PhotoViewActivity : BaseActivityVBind<ActivityPhotoViewBinding>() {

    private var isTopVisible = true
    private var isSyncing = false // 防止循环同步

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setupImages()
        setupClickListeners()
        setupSyncZoom()
    }

    private fun setupImages() {
        // 使用相同的图片加载配置确保图片尺寸一致
        Glide.with(this)
            .load("/storage/emulated/0/Android/data/com.ethan.android.notepad/cache/export/1000092143.jpg")
            .into(binding.topPhotoView)

        Glide.with(this)
            .load("/storage/emulated/0/Android/data/com.ethan.android.notepad/cache/export/1000092130.jpg")
            .into(binding.bottomPhotoView)
    }

    private fun setupClickListeners() {
        binding.btnVisible.setOnClickListener {
            toggleTopImageVisibility()
        }

        binding.btnReset.setOnClickListener {
            resetZoom()
        }
    }

    private fun toggleTopImageVisibility() {
        isTopVisible = !isTopVisible
        if (isTopVisible) {
            // 淡入显示上层图片
            binding.topPhotoView.animate().alpha(1f).setDuration(300).withStartAction {
                binding.topPhotoView.visibility = View.VISIBLE
            }.start()
            binding.btnVisible.text = "隐藏上层图片"
        } else {
            // 淡出隐藏上层图片
            binding.topPhotoView.animate().alpha(0f).setDuration(300).withEndAction {
                binding.topPhotoView.visibility = View.INVISIBLE
                binding.topPhotoView.alpha = 1f // 重置透明度
            }.start()
            binding.btnVisible.text = "显示上层图片"
        }
    }

    private fun setupSyncZoom() {
        // 创建矩阵变化监听器
        val matrixChangeListener = OnMatrixChangedListener { rect ->
            if (isSyncing) return@OnMatrixChangedListener

            isSyncing = true

            // 获取当前变换矩阵
            val matrix = Matrix()
            when {
                binding.topPhotoView.isVisible -> {
                    binding.topPhotoView.getDisplayMatrix(matrix)
                    binding.bottomPhotoView.setDisplayMatrix(matrix)
                }
                else -> {
                    binding.bottomPhotoView.getDisplayMatrix(matrix)
                    binding.topPhotoView.setDisplayMatrix(matrix)
                }
            }

            // 使用 post 确保同步完成后再允许下一次同步
            binding.main.post {
                isSyncing = false
            }
        }

        // 为两个 PhotoView 设置相同的监听器
        binding.topPhotoView.setOnMatrixChangeListener(matrixChangeListener)
        binding.bottomPhotoView.setOnMatrixChangeListener(matrixChangeListener)

        // 设置相同的缩放级别和双击缩放行为
        setupZoomConfig()
    }

    private fun setupZoomConfig() {
        // 确保两个 PhotoView 有相同的缩放配置
        val minScale = 0.5f
        val midScale = 1.0f
        val maxScale = 3.0f

        binding.topPhotoView.setScaleLevels(minScale, midScale, maxScale)
        binding.bottomPhotoView.setScaleLevels(minScale, midScale, maxScale)

        // 允许父容器拦截边缘事件
        binding.topPhotoView.setAllowParentInterceptOnEdge(true)
        binding.bottomPhotoView.setAllowParentInterceptOnEdge(true)
    }

    private fun resetZoom() {
        // 重置两个图片的缩放和位置
        binding.topPhotoView.setScale(1.0f, true)
        binding.bottomPhotoView.setScale(1.0f, true)

        // 重置到中心位置
        val matrix = Matrix()
        binding.topPhotoView.setDisplayMatrix(matrix)
        binding.bottomPhotoView.setDisplayMatrix(matrix)
    }

    override fun onDestroy() {
        super.onDestroy()
        // 清理监听器防止内存泄漏
        binding.topPhotoView.setOnMatrixChangeListener(null)
        binding.bottomPhotoView.setOnMatrixChangeListener(null)
    }
}