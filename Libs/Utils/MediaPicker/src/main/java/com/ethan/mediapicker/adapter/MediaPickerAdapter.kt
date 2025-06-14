package com.ethan.mediapicker.adapter

import android.graphics.Outline
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ethan.mediapicker.viewModel.MediaBean
import com.ethan.mediapicker.viewModel.MediaPickerType
import com.blankj.utilcode.util.SizeUtils
import com.bumptech.glide.Glide
import com.ethan.mediapicker.R
import com.ethan.mediapicker.databinding.ItemMediaBinding
import java.util.Locale


/**
 * @author  PengHaiChen
 * @date    2024/1/24 9:41
 * @email   penghaichen@tenorshare.cn
 */
internal class MediaPickerAdapter : RecyclerView.Adapter<MediaPickerAdapter.ViewHolder>() {

    private var mRecyclerView: RecyclerView? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    var mediaList: List<MediaBean> = listOf()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemMediaBinding.inflate(LayoutInflater.from(parent.context)).root)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bind = ItemMediaBinding.bind(holder.itemView)
        val mediaBean = mediaList[position]
        if (mediaBean.mediaType == MediaPickerType.SAMPLE) {
            bind.name.visibility = View.VISIBLE
        } else {
            bind.name.visibility = View.GONE
        }
        initLayout(bind)
        Glide.with(bind.root.context).load(mediaBean.mediaPath).into(bind.itemImage)
        if (mediaBean.mediaType == MediaPickerType.VIDEO) {
            bind.timeText.visibility = View.VISIBLE
            bind.timeText.text = formatDuration(mediaBean.duration)
        }
        setMutableView(mediaBean, bind)
        setClickEvent(mediaBean, bind)
    }

    var clickChangeSetting = {}
    private fun setMutableView(mediaBean: MediaBean, bind: ItemMediaBinding) {
        when (mediaBean.mediaType) {
            MediaPickerType.NO_PERMISSION -> {
                mRecyclerView?.context?.getString(R.string.prompt_describe_03)?.let {
                    bind.incNoPermission.tvNoPermission.text = String.format(it, mRecyclerView?.context?.getString(R.string.app_name) ?: "")
                }
                bind.incNoPermission.root.visibility = View.VISIBLE
                bind.incNoPermission.changeSetting.setOnClickListener { clickChangeSetting.invoke() }
            }

            else -> {
                bind.incNoPermission.root.visibility = View.GONE
                bind.incNoPermission.changeSetting.setOnClickListener { }
            }
        }
    }

    private fun initLayout(bind: ItemMediaBinding) {
        bind.flItem.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View?, outline: Outline?) {
                outline?.setRoundRect(Rect(0, 0, view!!.width, view.height), SizeUtils.dp2px(12F).toFloat())
            }
        }
        bind.flItem.clipToOutline = true
    }

    var clickItem: (mediaBean: MediaBean) -> Unit = {}
    var clickTakePhoto: () -> Unit = {}
    var time = 0L

    private fun setClickEvent(mediaBean: MediaBean, bind: ItemMediaBinding) {
        when (mediaBean.mediaType) {
            MediaPickerType.VIDEO, MediaPickerType.IMAGE, MediaPickerType.SAMPLE -> bind.flItem.setOnClickListener {
                click {
                    clickItem.invoke(mediaBean)
                }
            }

            MediaPickerType.TAKE_PHOTO -> bind.flItem.setOnClickListener {
                click {
                    clickTakePhoto.invoke()
                }
            }

            else -> {
                bind.flItem.setOnClickListener {}
            }
        }
    }

    private fun click(block: () -> Unit) {
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - time > 1000) {
            time = currentTimeMillis
            block.invoke()
        }
    }

    override fun getItemCount(): Int {
        return mediaList.size
    }

    class MediaPickerDiffCallback(
        private val old: List<MediaBean>,
        private val new: List<MediaBean>,
                                 ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return old.size
        }

        override fun getNewListSize(): Int {
            return new.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return old[oldItemPosition] == new[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val media = old[oldItemPosition].mediaType == new[newItemPosition].mediaType
            val mediaPath = old[oldItemPosition].mediaPath == new[newItemPosition].mediaPath
            val dateAdded = old[oldItemPosition].dateAdded == new[newItemPosition].dateAdded
            val tag = old[oldItemPosition].tag == new[newItemPosition].tag
            val folderName = old[oldItemPosition].folderName == new[newItemPosition].folderName
            return media && mediaPath && dateAdded && tag && folderName
        }
    }


    // 将毫秒转换为 "00:00" 格式的时间字符串
    private fun formatDuration(time: Long): String {
        val totalSeconds = time / 1000 // 转换为秒
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }

}