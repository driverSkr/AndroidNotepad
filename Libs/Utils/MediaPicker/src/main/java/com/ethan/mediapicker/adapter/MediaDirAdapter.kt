package com.ethan.mediapicker.adapter

import android.graphics.Outline
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.recyclerview.widget.RecyclerView
import com.ethan.mediapicker.viewModel.MediaBean
import com.ethan.mediapicker.viewModel.MediaPickerType
import com.ethan.mediapicker.viewModel.MediaPickerViewModel
import com.blankj.utilcode.util.SizeUtils
import com.bumptech.glide.Glide
import com.ethan.mediapicker.R
import com.ethan.mediapicker.databinding.ItemMediaDirBinding

/**
 * @author  PengHaiChen
 * @date    2024/1/24 17:09
 * @email   penghaichen@tenorshare.cn
 */
class MediaDirAdapter : RecyclerView.Adapter<MediaDirAdapter.ViewHolder>() {


    var dirData: Map<String, List<MediaBean>> = mutableMapOf()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemMediaDirBinding.inflate(LayoutInflater.from(parent.context)).root)
    }

    private var isVideo: Boolean? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bind = ItemMediaDirBinding.bind(holder.itemView)
        initLayout(bind)
        val name = dirData.keys.toList()[position]
        if (isVideo == null) {
            isVideo = dirData.values.toList()[0].first().mediaType == MediaPickerType.VIDEO
        }
        bind.dirName.text = if (name == MediaPickerViewModel.AllCode) {
            holder.itemView.context.getString(if (isVideo != true) R.string.all_photo else R.string.all_video)
        } else {
            val nameSpl = name.split("/")
            if (nameSpl.size >= 2) nameSpl[nameSpl.size - 2] else name
        }
        bind.photoNumber.text = (dirData[name]?.size ?: 0).toString()
        Glide.with(bind.root.context).asBitmap().load(dirData[name]?.get(0)?.mediaPath).into(bind.itemImage)
        bind.root.setOnClickListener {
            itemClick.invoke(name)
        }
    }

    private fun initLayout(bind: ItemMediaDirBinding) {
        bind.flItem.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View?, outline: Outline?) {
                outline?.setRoundRect(Rect(0, 0, view!!.width, view.height), SizeUtils.dp2px(24F).toFloat())
            }
        }
        bind.flItem.clipToOutline = true
    }

    override fun getItemCount(): Int {
        return dirData.keys.size
    }

    var itemClick: (String) -> Unit = {}
}