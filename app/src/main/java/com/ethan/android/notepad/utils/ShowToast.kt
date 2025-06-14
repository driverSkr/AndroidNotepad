package com.ethan.android.notepad.utils

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.ToastUtils
import com.ethan.android.notepad.R
import com.ethan.android.notepad.databinding.CustomToastBinding
import com.ethan.android.notepad.extension.findBaseActivityVBind

object ShowToast {

    fun showToast(string: String) {
        ToastUtils.make().setGravity(Gravity.TOP, 0, SizeUtils.dp2px(100F)).show(string)
    }

    fun showToast(context: Context, type: ToastType, string: String) {
        val binding = CustomToastBinding.inflate(LayoutInflater.from(context))
        val icon = when(type) {
            ToastType.SUCCESS -> R.drawable.svg_icon_success
            ToastType.ERROR -> R.drawable.svg_icon_error
            ToastType.WARNING -> R.drawable.svg_icon_warning
            ToastType.HINT -> R.drawable.svg_icon_hint
        }

        binding.toastIcon.setImageResource(icon)
        binding.toastText.text = string

        // 创建并显示 Toast
        val toast = Toast(context)
        toast.duration = Toast.LENGTH_SHORT
        toast.setGravity(Gravity.TOP, 0, 100)
        toast.view = binding.root
        toast.show()
    }
}

fun String.showToast(context: Context, type: ToastType) {
    context.findBaseActivityVBind()?.runOnUiThread {
        ShowToast.showToast(context, type, this)
    }
}

fun Int.showToast(context: Context, type: ToastType) {
    context.findBaseActivityVBind()?.runOnUiThread {
        val content = context.getString(this)
        ShowToast.showToast(context, type, content)
    }
}

enum class ToastType {
    SUCCESS, // 成功
    ERROR, // 错误
    WARNING, // 警告
    HINT // 提示
}