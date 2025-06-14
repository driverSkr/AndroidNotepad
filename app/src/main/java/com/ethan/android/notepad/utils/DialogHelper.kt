package com.ethan.android.notepad.utils

import android.graphics.LinearGradient
import android.graphics.Shader
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.ethan.alicom.UserLoginManager
import com.ethan.android.notepad.R
import com.ethan.android.notepad.databinding.DialogExitLoginConfirmationBinding
import com.ethan.android.notepad.databinding.DialogOperatePromptBinding
import com.ethan.android.notepad.databinding.DialogPromptBinding
import com.ethan.android.notepad.extension.antiShakeClick
import com.ethan.android.notepad.theme.ComposeProjectTheme
import com.ethan.android.notepad.theme.Green
import com.ethan.android.notepad.ui.dialog.ComposeNativeDialog
import com.ethan.base.dialog.BaseDialog
import com.ethan.base.utils.AndroidBarUtils
import com.ethan.company.NetConfig
import com.ethan.android.notepad.viewmodel.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object DialogHelper {

    //提示弹窗
    fun showConfirmDialog(
        activity: FragmentActivity,
        tip: String,                           //提示文本
        content: String,                       //内容
        mainTv: String,                        //主按钮内容
        secondaryTv: String? = null,           //次级按钮内容
        mainBtn: () -> Unit,                   //主按钮触发方法
        secondaryBtn: () -> Unit               //次级按钮触发方法
    ) {
        val binding = DialogOperatePromptBinding.inflate(LayoutInflater.from(activity))
        binding.title.text = tip
        binding.promptDescribe.text = content
        binding.btnOneTv.text = secondaryTv
        binding.btnTwoTv.text = mainTv

        val dialog = BaseDialog
            .Builder(activity).setCancelableOutside(false).setView(binding.root).create()
        val show = dialog.show()
        binding.close.antiShakeClick {
            show?.dismiss()
        }
        binding.btnOne.antiShakeClick {
            secondaryBtn.invoke()
            show?.dismiss()
        }
        binding.btnTwo.antiShakeClick {
            mainBtn.invoke()
            show?.dismiss()
        }
    }

    fun showUpdateDialog(activity: FragmentActivity) {
        val binding = DialogOperatePromptBinding.inflate(LayoutInflater.from(activity))

        val dialog = BaseDialog
            .Builder(activity).setCancelableOutside(false).setView(binding.root).create()
        val show = dialog.show()
        binding.close.antiShakeClick {
            show?.dismiss()
        }
        binding.btnOne.antiShakeClick {
            show?.dismiss()
        }
        binding.btnTwo.antiShakeClick {
            show?.dismiss()
        }
    }

    fun showTestDialog(activity: FragmentActivity) {
        val (binding, dialog) = ComposeNativeDialog.composeBSDialog(activity)
        val show = dialog.show()
        binding.composeView.apply {
            setContent {
                ComposeProjectTheme {
                    Box(modifier = Modifier.height(500.dp).background(color = Green))
                }
            }
        }
    }

    /**
     * 温馨提示、使用须知弹窗模板
     */
    private fun showPromptDialog(activity: FragmentActivity, titleTv: String, msgTv: String, agreeCallback: () -> Unit, disagreeCallback: () -> Unit) {
        val binding = DialogPromptBinding.inflate(LayoutInflater.from(activity))
        binding.titleTv.text = titleTv
        binding.msgTv.text = msgTv

        //设置点击超链接
        val span1 = activity.getString(R.string.user_agreement_line)
        val span2 = activity.getString(R.string.privacy_policy_line)
        val click1 = object : ClickableSpan() {
            override fun onClick(widget: View) {
                //用户协议
                LaunchUtils.launchWeb(
                    activity,
                    NetConfig.USER_AGREEMENT,
                    activity.getString(R.string.user_agreement_line)
                )
            }
            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
            }
        }
        val click2 = object : ClickableSpan() {
            override fun onClick(widget: View) {
                //隐私政策
                LaunchUtils.launchWeb(
                    activity,
                    NetConfig.URL_PRIVACY_POLICY,
                    activity.getString(R.string.privacy_policy)
                )
            }
            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
            }
        }
        val spans = arrayOf(
            TextSpanUtils.Span(span1, R.color.text_blue, click1),
            TextSpanUtils.Span(span2, R.color.text_blue, click2)
        )
        TextSpanUtils.setAllSpanText(activity, msgTv, spans, binding.msgTv)

        val dialog = BaseDialog.Builder(activity).apply { params.mDimAmount = 0.8f }
            .setCancelableOutside(false).setView(binding.root).create()
        binding.closeBtn.setOnClickListener {
            disagreeCallback.invoke()
            dialog.dismiss()
        }
        binding.agreeBtn.setOnClickListener {
            agreeCallback.invoke()
            dialog.dismiss()
        }
        binding.disagreeBtn.setOnClickListener {
            disagreeCallback.invoke()
            dialog.dismiss()
        }
        dialog.show()
    }

    /**
     * 未同意启动条款触发弹窗
     */
    fun notAgreeFiringTermDialog(activity: FragmentActivity, agreeCallback: () -> Unit, disagreeCallback: () -> Unit) {
        showPromptDialog(activity,activity.getString(R.string.warm_prompt) , activity.getString(R.string.protocol_prompt_dialog_tv),
            agreeCallback = {
                activity.lifecycleScope.launch(Dispatchers.IO) {
                    UserLoginManager.setToken(UserViewModel().getToken(activity, "dialog") ?: "")
                }
                agreeCallback.invoke()
            },
            disagreeCallback = { disagreeCallback.invoke() })
    }

    /**
     * 退出登录确认弹窗
     */
    suspend fun showDialogExitLoginConfirmation(activity: FragmentActivity) = suspendCoroutine { suspendCoroutine ->
        val binding = DialogExitLoginConfirmationBinding.inflate(LayoutInflater.from(activity))
        // 获取渐变起始颜色和结束颜色
        val startColor = ContextCompat.getColor(activity, R.color.pink)
        val endColor = ContextCompat.getColor(activity, R.color.orange)
        // 创建渐变效果
        val statusBarHeight = AndroidBarUtils.getStatusBarHeight(activity) // 取设备不需要等布局加载完成就能获取的长度
        val shader = LinearGradient(0f, 0f, statusBarHeight.toFloat(), 0f, startColor, endColor, Shader.TileMode.CLAMP)
        binding.btnLogOut.paint.shader = shader

        val dialog = BaseDialog.Builder(activity).apply { params.mDimAmount = 0.8f }
            .setCancelableOutside(false).setView(binding.root).create()
        binding.btnOne.setOnClickListener {
            suspendCoroutine.resume("cancel")
            dialog.dismiss() }
        binding.btnTwo.setOnClickListener {
            suspendCoroutine.resume("exit")
            Toast.makeText(activity, "退出登录成功", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        dialog.show()
    }
}