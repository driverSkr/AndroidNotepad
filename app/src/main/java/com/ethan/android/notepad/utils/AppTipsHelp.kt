package com.ethan.android.notepad.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.SpannableStringBuilder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.FloatRange
import androidx.annotation.StringRes
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SpanUtils
import com.ethan.android.notepad.R
import com.ethan.android.notepad.databinding.ItemCustomToastBinding
import com.ethan.android.notepad.databinding.TipsBaseBinding
import com.ethan.android.notepad.extension.dp
import com.ethan.android.notepad.extension.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

class AppTipsHelp {

    lateinit var activity: FragmentActivity
    var showTime = 2000

    var tipString = SpannableStringBuilder()
    var tipsImage = R.mipmap.ic_launcher
    var tipsColor = Color.parseColor("#FFFFFF")
    var tipsRounded = 12.dpF
    var paddingVertical = 8.dp

    @FloatRange(from = 0.0, to = 1.0)
    var tipsYPosition = 0.7F

    fun show() {
        val tipsBaseBinding = TipsBaseBinding.inflate(LayoutInflater.from(activity))
        tipsBaseBinding.tipsCard.radius = tipsRounded
        tipsBaseBinding.tipsCard.setCardBackgroundColor(tipsColor)
        tipsBaseBinding.tipsImage.setImageResource(tipsImage)
        tipsBaseBinding.tipsText.text = tipString
        tipsBaseBinding.tipsContent.updatePadding(top = paddingVertical, bottom = paddingVertical)
        val popupWindow = PopupWindow(activity)
        popupWindow.animationStyle = R.style.pop_win_anim_style
        popupWindow.contentView = tipsBaseBinding.root
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow.width = ViewGroup.LayoutParams.MATCH_PARENT
        val rootView = activity.window?.decorView?.findViewById<View>(android.R.id.content)
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, (ScreenUtils.getAppScreenHeight() * tipsYPosition).roundToInt())
        activity.lifecycleScope.launch(Dispatchers.IO) {
            delay(showTime.toLong())
            withContext(Dispatchers.Main) {
                popupWindow.dismiss()
            }
        }
    }

    fun initNetWorkError(activity: FragmentActivity): AppTipsHelp {
        return init {
            this.activity = activity
            tipString = SpanUtils()
                .append(activity.getString(R.string.something_went_wrong_please_check))
                .setForegroundColor(Color.parseColor("#F04438")).setFontSize(12.sp).create()
            tipsImage = R.drawable.svg_no_net
            tipsColor = Color.argb(255, 252, 218, 215)
            tipsRounded = 12.dpF
            tipsYPosition = 0.8F
        }
    }

    fun initNormalTips(
        activity: FragmentActivity, tipsString: String,
        @DrawableRes image: Int = R.drawable.svg_tips,
        tipsY: Float = 0.38F,
                      ): AppTipsHelp {
        return init {
            this.activity = activity
            tipString = SpanUtils().append(tipsString)
                .setForegroundColor(Color.parseColor("#0C0F0D")).setFontSize(12.sp).create()
            tipsImage = image
            tipsColor = Color.argb(255, 255, 255, 255)
            paddingVertical = 4.dp
            tipsYPosition = tipsY
        }
    }

    fun init(block: AppTipsHelp.() -> Unit): AppTipsHelp {
        block()
        return this
    }

    fun customToast(
        context: Context,
        error: Boolean,
        @DrawableRes iconId: Int,
        @ColorRes msgColorId: Int,
        @StringRes msgResourceId: Int? = null,
        yOffset: Float = 0.85f,
        msg: String = "",
    ) {
        val binding = ItemCustomToastBinding.inflate(LayoutInflater.from(context))
        binding.toastLayout.isActivated = error
        binding.toastIcon.setImageResource(iconId)

        val messageText: CharSequence = if (msgResourceId != null) {
            context.getString(msgResourceId)
        } else {
            msg
        }
        binding.toastMsg.text = messageText
        binding.toastMsg.setTextColor(context.resources.getColor(msgColorId))

        val toast = Toast(context)
        toast.apply {
            duration = Toast.LENGTH_LONG
            view = binding.root
            setGravity(Gravity.BOTTOM, 0, (ScreenUtils.getAppScreenHeight() * yOffset).roundToInt())
            show()
        }
    }
}