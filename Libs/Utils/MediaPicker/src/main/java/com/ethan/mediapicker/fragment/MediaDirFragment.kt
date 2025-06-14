package com.ethan.mediapicker.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.ethan.mediapicker.adapter.MediaDirAdapter
import com.ethan.mediapicker.viewModel.MediaChooseViewModel
import com.ethan.mediapicker.viewModel.MediaPickerViewModel
import com.blankj.utilcode.util.FragmentUtils
import com.ethan.mediapicker.databinding.FragmentMeidaDirBinding

/**
 * @author  PengHaiChen
 * @date    2024/1/24 16:53
 * @email   penghaichen@tenorshare.cn
 */
internal class MediaDirFragment : Fragment() {

    private val bind by lazy {
        FragmentMeidaDirBinding.inflate(LayoutInflater.from(context))
    }
    private val mediaChooseViewModel by lazy {
        ViewModelProvider(activity ?: return@lazy null)[MediaChooseViewModel::class.java]
    }

    private val mediaPickerVM by lazy {
        ViewModelProvider(activity ?: return@lazy null)[MediaPickerViewModel::class.java]
    }
    private val adapter by lazy { MediaDirAdapter() }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initEvent()
        initViewModel()
    }

    private fun initView() {
        bind.dirList.layoutManager = GridLayoutManager(context
            ?: return, 2, GridLayoutManager.VERTICAL, false)
        bind.dirList.adapter = adapter
        mediaChooseViewModel?.paddingBottom?.observe(viewLifecycleOwner) {
            bind.dirList.setPadding(bind.dirList.paddingLeft, bind.dirList.paddingTop, bind.dirList.paddingRight, it)
        }
    }

    private fun initEvent() {
        mediaPickerVM?.getMediaDir()?.let { adapter.dirData = it }
    }

    private fun initViewModel() {
        adapter.itemClick = {
            val isvideo = arguments?.getBoolean("isvideo")
            mediaPickerVM?.refreshMediaByFolderName(it,isvideo?:false)
            FragmentUtils.pop(parentFragmentManager)
        }
    }
}