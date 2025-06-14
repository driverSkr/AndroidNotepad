package com.ethan.mediapicker

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.blankj.utilcode.util.ScreenUtils
import com.ethan.mediapicker.databinding.DialogNeedCameraBinding
import kotlin.math.roundToInt

/**
 * @author  PengHaiChen
 * @date    2024/1/25 15:33
 * @email   penghaichen@tenorshare.cn
 */
class NeedCameraDialog : DialogFragment() {

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout((ScreenUtils.getAppScreenWidth() * 0.8).roundToInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return DialogNeedCameraBinding.inflate(inflater).root
    }

    var gotoSettingClick = {}
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bind = DialogNeedCameraBinding.bind(view)
        context
            ?.getString(R.string.needs_your_permission_to_access_your_camera_to_create_more_images)
            ?.let {
                bind.tipsContentText.text = String.format(it, context?.getString(R.string.app_name) ?: "")
            }
        bind.yes.setOnClickListener {
            gotoSettingClick.invoke()
            dismiss()
        }
    }
}