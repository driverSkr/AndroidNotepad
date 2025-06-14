package com.ethan.mediapicker.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import com.ethan.mediapicker.adapter.MediaPickerAdapter
import com.ethan.mediapicker.viewModel.MediaBean
import com.ethan.mediapicker.viewModel.MediaChooseViewModel
import com.ethan.mediapicker.viewModel.MediaPickerType
import com.ethan.mediapicker.viewModel.MediaPickerViewModel
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.FragmentUtils
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.UriUtils
import com.ethan.mediapicker.R
import com.ethan.mediapicker.databinding.FragmentMeidaPickerBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


private const val TAG = "MediaPickerFragment"

/**
 * @author  PengHaiChen
 * @date    2024/1/23 14:32
 * @email   penghaichen@tenorshare.cn
 */
class MediaPickerFragment : Fragment() {


    private val mediaChooseViewModel by lazy { ViewModelProvider(activity ?: return@lazy null)[MediaChooseViewModel::class.java] }


    private lateinit var binding: FragmentMeidaPickerBinding
    private val mediaPickerVM by lazy { ViewModelProvider(activity ?: return@lazy null)[MediaPickerViewModel::class.java] }
    private val adapter by lazy { MediaPickerAdapter() }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentMeidaPickerBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initEvent()
        initViewModel()
    }

    private lateinit var layoutManager: GridLayoutManager
    private fun initView() {
        layoutManager = GridLayoutManager(context ?: return, 3, GridLayoutManager.VERTICAL, false)
        binding.mediaView.layoutManager = layoutManager
        binding.mediaView.adapter = adapter
        mediaChooseViewModel?.paddingBottom?.observe(this.viewLifecycleOwner) {
            binding.mediaView.setPadding(binding.mediaView.paddingLeft, binding.mediaView.paddingTop,
                binding.mediaView.paddingRight, it)
        }
    }

    private fun initEvent() {
        lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_RESUME -> {
                        lifecycleScope.launch(Dispatchers.Default) {
                            val checkPermission = mediaPickerVM?.checkPhotoPermission() ?: false
                            if (checkPermission) {
                                mediaPickerVM?.getMedia(context ?: return@launch, true)
                            } else {
                                mediaPickerVM?.showWithNoPermission()
                            }
                        }
                    }

                    else -> {}
                }
            }
        })
        lifecycleScope.launch(Dispatchers.Default) {
            mediaPickerVM?.checkPhotoPermission(false, jump2Setting = false)
        }
        adapter.clickChangeSetting = {
            lifecycleScope.launch(Dispatchers.Default) {
                mediaPickerVM?.checkPhotoPermission(false, jump2Setting = true)
            }
        }
        adapter.clickItem = {
            selectMedia(it, MediaChooseViewModel.From.SELECT)
        }
        adapter.clickTakePhoto = {
            lifecycleScope.launch(Dispatchers.Default) {
                val cameraPermission = mediaPickerVM?.checkCameraPermission(parentFragmentManager)
                if (cameraPermission == true) {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, getTakePhotoUri())
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    intent.putExtra("android.intent.extras.CAMERA_FACING", 1)
                    launcherTackPhoto.launch(intent)
                }
            }
        }

        val mediaDirFragment = MediaDirFragment()
        parentFragmentManager.addOnBackStackChangedListener {
            if (mediaDirFragment.isVisible) {
                RotateAnimation(0F, 180F, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f).apply {
                    duration = 500
                    fillAfter = true
                    binding.dirArrow.startAnimation(this)
                }
            } else {
                RotateAnimation(180F, 0F, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f).apply {
                    duration = 500
                    fillAfter = true
                    binding.dirArrow.startAnimation(this)
                }
            }
        }
        binding.switchDir.setOnClickListener {
            if (mediaPickerVM?.getArchiveMediaBean()?.size == 0) {
                return@setOnClickListener
            }
            if (mediaDirFragment.isVisible) {
                FragmentUtils.pop(parentFragmentManager)
            } else {
                FragmentUtils.replace(parentFragmentManager, mediaDirFragment, binding.dirSelect.id, true, R.anim.anim_dir_enter,
                    R.anim.anim_dir_exit, R.anim.anim_dir_enter, R.anim.anim_dir_exit)
            }
        }
    }

    private fun selectMedia(it: MediaBean, from: MediaChooseViewModel.From) {
        mediaChooseViewModel?.chooseMedia?.postValue(MediaChooseViewModel.MediaChooseState(MediaChooseViewModel.State.SELECT, null, from))
        lifecycleScope.launch(Dispatchers.Default) {
            val mediaBean = mediaPickerVM?.examineChooseMedia(context, it)
            if (mediaBean == null) {
                mediaChooseViewModel?.chooseMedia?.postValue(
                    MediaChooseViewModel.MediaChooseState(MediaChooseViewModel.State.ERROR, null, from))
            } else {
                mediaChooseViewModel?.chooseMedia?.postValue(
                    MediaChooseViewModel.MediaChooseState(MediaChooseViewModel.State.SUCCESS, mediaBean, from))
            }
        }
    }

    private var launcherTackPhoto = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            if (takePhotoPath.isBlank()) return@registerForActivityResult
            selectMedia(MediaBean(UriUtils.file2Uri(File(takePhotoPath)), MediaPickerType.TAKE_PHOTO, "", "", 0),
                MediaChooseViewModel.From.CAMERA)
        }
    }


    private fun initViewModel() {
        mediaPickerVM?.mediaList?.observe(this.viewLifecycleOwner) {
            val calculateDiff = DiffUtil.calculateDiff(MediaPickerAdapter.MediaPickerDiffCallback(adapter.mediaList, it), true)
            adapter.mediaList = it
            calculateDiff.dispatchUpdatesTo(adapter)
            val filter = it.filter { va -> va.mediaType == MediaPickerType.NO_PERMISSION }
            if (filter.isNotEmpty()) {
                layoutManager.spanSizeLookup = object : SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        if (position == it.indexOf(filter[0])) return 3
                        return 1
                    }
                }
            } else {
                layoutManager.spanSizeLookup = object : SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return 1
                    }
                }
            }
        }
        mediaPickerVM?.currDirPath?.observe(this.viewLifecycleOwner) {
            binding.dirName.text = if (it == MediaPickerViewModel.AllCode) {
                context?.getString(R.string.all_photo)
            } else {
                val nameSpl = it.split("/")
                if (nameSpl.size >= 2) nameSpl[nameSpl.size - 2] else it
            }
        }
    }

    private var takePhotoPath = ""
    private fun getTakePhotoUri(): Uri? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storeDir = File("${PathUtils.getExternalAppFilesPath()}/take_photo")
        FileUtils.createOrExistsDir(storeDir)
        val file = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storeDir)
        takePhotoPath = file.absolutePath
        return FileProvider.getUriForFile(context ?: return null, "${activity?.packageName}.mediapicker.fileprovider", file)
    }

}