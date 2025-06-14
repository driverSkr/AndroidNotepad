package com.ethan.android.notepad.ui.dialog

import android.view.LayoutInflater
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.ScreenUtils
import com.ethan.android.notepad.R
import com.ethan.android.notepad.databinding.DialogComposeContainerBinding
import com.ethan.base.dialog.BaseDialog

import com.google.android.material.bottomsheet.BottomSheetDialog

object ComposeNativeDialog {

    fun composeBaseDialog(activity: FragmentActivity, dismiss: () -> Unit = {}): Pair<DialogComposeContainerBinding, BaseDialog> {
        val binding = DialogComposeContainerBinding.inflate(LayoutInflater.from(activity))
        val dialog = BaseDialog.Builder(activity).setWidth((ScreenUtils.getAppScreenWidth() * 0.8F).toInt())
            .setOnDismissListener { dismiss.invoke() }.setView(binding.root).create()
        return binding to dialog
    }

    /**
     * 底部弹窗
     */
    fun composeBSDialog(activity: FragmentActivity, dismiss: () -> Unit = {}): Pair<DialogComposeContainerBinding, BottomSheetDialog> {
        val binding = DialogComposeContainerBinding.inflate(LayoutInflater.from(activity))
        val sheetDialog = BottomSheetDialog(activity, R.style.ComposeBottomSheetDialog).apply {
            setOnDismissListener { dismiss.invoke() }
            window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
        sheetDialog.setContentView(binding.root)
        return binding to sheetDialog
    }
}