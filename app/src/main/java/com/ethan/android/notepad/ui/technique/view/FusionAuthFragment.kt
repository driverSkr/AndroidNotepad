package com.ethan.android.notepad.ui.technique.view

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextPaint
import android.text.TextUtils
import android.text.style.ClickableSpan
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.alicom.fusion.auth.AlicomFusionAuthCallBack
import com.alicom.fusion.auth.AlicomFusionAuthUICallBack
import com.alicom.fusion.auth.AlicomFusionBusiness
import com.alicom.fusion.auth.AlicomFusionConstant
import com.alicom.fusion.auth.HalfWayVerifyResult
import com.alicom.fusion.auth.error.AlicomFusionEvent
import com.alicom.fusion.auth.numberauth.FusionNumberAuthModel
import com.alicom.fusion.auth.smsauth.AlicomFusionVerifyCodeView
import com.alicom.fusion.auth.token.AlicomFusionAuthToken
import com.alicom.fusion.auth.upsms.AlicomFusionUpSMSView
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.RegexUtils
import com.ethan.alicom.AliConstant
import com.ethan.alicom.UserLoginManager
import com.ethan.android.notepad.NotepadApp
import com.ethan.android.notepad.R
import com.ethan.android.notepad.base.BaseFragmentVBind
import com.ethan.android.notepad.databinding.FragmentFusionAuthBinding
import com.ethan.android.notepad.databinding.FragmentVerificationCodeLoginBinding
import com.ethan.android.notepad.ui.technique.model.UserModel
import com.ethan.base.utils.AndroidBarUtils
import com.ethan.company.NetConfig
import com.ethan.android.notepad.utils.AppTipsHelp
import com.ethan.android.notepad.utils.BitmapUtils
import com.ethan.android.notepad.utils.DataHelper
import com.ethan.android.notepad.utils.DialogHelper
import com.ethan.android.notepad.utils.LaunchUtils
import com.ethan.android.notepad.utils.NetWorkUtils
import com.ethan.android.notepad.utils.TextSpanUtils
import com.ethan.android.notepad.utils.isValidPhoneNumber
import com.ethan.android.notepad.viewmodel.UserViewModel
import com.ethan.flowbus.FlowBus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mobile.auth.gatewayauth.AuthRegisterViewConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * 阿里云融合认证（一键登录、验证码登录）
 */
class FusionAuthFragment: BaseFragmentVBind<FragmentFusionAuthBinding>() {

    private var verifySuccess = false
    private var sum = 0
    private lateinit var alicomFusionAuthCallBack: AlicomFusionAuthCallBack
    private  var alicomFusionBusiness: AlicomFusionBusiness? = null
    private val clipboardManager by lazy { getCurrentActivity()?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }
    private var isReturnSub = -1
    private val TAG = "FusionAuthFragment"

    private val userViewModel by lazy {
        ViewModelProvider(this)[UserViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initEvent()
        FlowBus.with<Boolean>(FlowBus.EVEN_LOGIN).register(this) {
            isReturnSub = if (!it) 1 else -1
            login()
        }
    }

    override fun onResume() {
        super.onResume()
        val userData = DataHelper.getUserData(requireContext())
        if (userData != null) {
            updateView(userData)
        } else {
            binding.loggedIn.loggedInBtn.visibility = View.GONE
            binding.notLoggedIn.notLoggedInBtn.visibility = View.VISIBLE
        }
    }

    private fun initView() {
        verifySuccess = false
        val userData = DataHelper.getUserData(requireContext())
        if (userData != null) {
            updateView(userData)
        } else {
            binding.loggedIn.loggedInBtn.visibility = View.GONE
            binding.notLoggedIn.notLoggedInBtn.visibility = View.VISIBLE
            lifecycleScope.launch(Dispatchers.Default) {
                initAlicomFusionSdk()
            }
        }
    }

    private fun login() {
        if (NetWorkUtils.isNetworkConnected(requireContext())) {
            if (!TextUtils.isEmpty(UserLoginManager.getToken())) {
                showDialogNoBg()
                //需要Token鉴权后才可使用
                if (verifySuccess) {
                    //开始场景；uiCallback UI回调，自定义UI需通过该回调实现
                    alicomFusionBusiness?.startSceneWithTemplateId(requireContext(), "100001" , uiCallBack)
                } else {
                    dismissProgress()
                    lifecycleScope.launch(Dispatchers.IO) {
                        UserLoginManager.setToken(userViewModel.getToken(requireContext(), "鉴权失败弹窗") ?: "")
                        withContext(Dispatchers.Main) {
                            initAlicomFusionSdk()
                            if (verifySuccess) {
                                //开始场景；uiCallback UI回调，自定义UI需通过该回调实现
                                alicomFusionBusiness?.startSceneWithTemplateId(requireContext(), "100001" , uiCallBack)
                            }
                        }
                    }
                }
            } else {
                dismissProgress()
                lifecycleScope.launch(Dispatchers.IO) {
                    UserLoginManager.setToken(userViewModel.getToken(requireContext(), "鉴权失败弹窗") ?: "")
                }
            }
        } else {
            AppTipsHelp().customToast(requireContext(),true, R.drawable.svg_tips_red, R.color.text_red_color, R.string.network_timeout)
        }

    }

    private fun initEvent() {
        binding.notLoggedIn.notLoggedInBtn.setOnClickListener {
            if (DataHelper.isAgreeInitiatedPermissionDialog(requireContext()) != "agree") {
                DialogHelper.notAgreeFiringTermDialog(requireActivity(),
                    disagreeCallback = {
                        DataHelper.setAgreeInitiatedPermissionDialog(requireContext(), "disagree")
                    } , agreeCallback = {
                        DataHelper.setAgreeInitiatedPermissionDialog(requireContext(), "agree")
                        login()
                    })
            } else login()
        }
        binding.loggedIn.ivExit.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Default) {
                val result = DialogHelper.showDialogExitLoginConfirmation(requireActivity())
                if (result == "exit") {
                    DataHelper.setUserData(requireContext(), null)
                    FlowBus.with<Boolean>(FlowBus.BUS_WORK_UPDATE).postMain(true)
                    FlowBus.with<Boolean>(FlowBus.BUS_RE_FRESH).postMain(true)
                    withContext(Dispatchers.Main) {
                        binding.loggedIn.loggedInBtn.visibility = View.GONE
                        binding.notLoggedIn.notLoggedInBtn.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun initAlicomFusionSdk() {
        //友盟组件依赖设置
        AlicomFusionBusiness.useSDKSupplyUMSDK(false, "Umeng")
        alicomFusionBusiness = AlicomFusionBusiness()
        sum = 0
        //初始化，传入鉴权Token。Token需要从服务端获取
        val token = AlicomFusionAuthToken()
        Log.d(TAG, "获取到的Token = ${UserLoginManager.getToken()}")
        token.authToken = UserLoginManager.getToken()
        alicomFusionBusiness?.initWithToken(requireContext(), AliConstant.SCHEME_CODE, token)

        //主流程回调
        alicomFusionAuthCallBack = object : AlicomFusionAuthCallBack {
            /**
             * token需要更新，只在超时调用获取
             */
            //Token过期前五分钟，通过onSDKTokenUpdate回调获取新的Token
            override fun onSDKTokenUpdate(): AlicomFusionAuthToken {
                Log.d(TAG, "AlicomFusionAuthCallBack---onSDKTokenUpdate")
                val token1 = AlicomFusionAuthToken()
                val latch = CountDownLatch(1)
                lifecycleScope.launch(Dispatchers.IO) {
                    UserLoginManager.setToken(userViewModel.getToken(requireContext(), "token过期") ?: "")
                    latch.countDown()
                }
                try {
                    latch.await()
                    token1.authToken = UserLoginManager.getToken()
                } catch (_: InterruptedException) {}
                return token1
            }

            /**
             * token鉴权成功，通过onSDKTokenAuthSuccess回调，场景操作必须在回调成功后进行；
             */
            override fun onSDKTokenAuthSuccess() {
                Log.d(TAG, "AlicomFusionAuthCallBack---onSDKTokenAuthSuccess")
                verifySuccess = true
            }

            /**
             * Token鉴权失败，token初次鉴权失败&token更新后鉴权失败均会触发此回调
             */
            override fun onSDKTokenAuthFailure(token: AlicomFusionAuthToken?, alicomFusionEvent: AlicomFusionEvent?) {
                Log.d(TAG, "AlicomFusionAuthCallBack---onSDKTokenAuthFailure ${alicomFusionEvent?.getErrorCode()}  ${alicomFusionEvent?.getErrorMsg()}")
                dismissProgress()
                lifecycleScope.launch(Dispatchers.Default) {
                    UserLoginManager.setToken(userViewModel.getToken(requireContext(), "Token鉴权失败") ?: "")
                    val authToken = AlicomFusionAuthToken()
                    authToken.authToken = UserLoginManager.getToken()
                    //主动更新鉴权Token
                    alicomFusionBusiness?.updateToken(authToken)
                }
            }

            /**
             * 认证成功，可使用相应token进行换号或登录操作
             */
            override fun onVerifySuccess(token: String?, nodeName: String?, alicomFusionEvent: AlicomFusionEvent?) {
                Log.d(TAG, "AlicomFusionAuthCallBack---onVerifySuccess  $token")
                lifecycleScope.launch(Dispatchers.Default) {
                    val userData = userViewModel.verifyToken(token)
                    updateBusiness(userData,nodeName)
                }
            }

            /**
             * 中途认证节点，需要知道中途认证结果，否则影响流程继续执行
             */
            override fun onHalfWayVerifySuccess(
                nodeName: String?,
                maskToken: String?,
                alicomFusionEvent: AlicomFusionEvent?,
                halfWayVerifyResult: HalfWayVerifyResult?
            ) {
                Log.d(TAG, "AlicomFusionAuthCallBack---onHalfWayVerifySuccess  $maskToken")
                lifecycleScope.launch(Dispatchers.Default) {
                    val userData = userViewModel.verifyToken(maskToken)
                    updateBusinessHalfWay(userData, halfWayVerifyResult, nodeName)
                }
            }

            /**
             * 认证失败；如号码认证中无法拉起授权页，token获取失败，短信界面无法获取短信
             */
            override fun onVerifyFailed(alicomFusionEvent: AlicomFusionEvent?, s: String?) {
                dismissProgress()
                Log.d(TAG, "AlicomFusionAuthCallBack---onVerifyFailed ${alicomFusionEvent?.getErrorCode()}  ${alicomFusionEvent?.getErrorMsg()}")
                //继续场景，用于场景中断后恢复流程，如获取到号码校验Token后，假设服务端效验失败，可以通过该接口继续进行场景流程。
                //false：当前的认证失败
                alicomFusionBusiness?.continueSceneWithTemplateId("100001", false)
            }

            /**
             * 场景流程结束，正常/异常结束
             */
            override fun onTemplateFinish(alicomFusionEvent: AlicomFusionEvent?) {
                Log.d(TAG, "AlicomFusionAuthCallBack---onTemplateFinish ${alicomFusionEvent?.getErrorCode()}  ${alicomFusionEvent?.getErrorMsg()}")
                sum = 0
                //结束场景；需要在start后才能使用
                alicomFusionBusiness?.stopSceneWithTemplateId("100001")
            }

            /**
             * 场景事件回调
             * @note SDK场景流程中各个界面点击事件&界面跳转事件等UI相关回调
             * @note 本回调接口仅做事件通知，不可再此回调内处理业务逻辑
             */
            override fun onAuthEvent(alicomFusionEvent: AlicomFusionEvent?) {
                Log.d(TAG, "AlicomFusionAuthCallBack---onAuthEvent"+alicomFusionEvent?.getErrorCode())
                if (alicomFusionEvent?.getErrorCode() == "400006") {
                    //Login_Auto
                } else if (alicomFusionEvent?.getErrorCode() == "100003") {
                    dismissProgress()
                }
            }

            /**
             * 填充手机号
             */
            override fun onGetPhoneNumberForVerification(
                nodeId: String?,
                alicomFusionEvent: AlicomFusionEvent?
            ): String {
                Log.d(TAG, "AlicomFusionAuthCallBack---onGetPhoneNumberForVerification")
                return DataHelper.getUserData(requireContext())?.phoneNumber ?: ""
            }

            /**
             * 认证中断
             * @note 触发条件：1. 未勾选隐私协议框，进行认证；2. 验证手机号码输入格式错误，3sdk开始加载某个节点和结束加载某个节点，4、相关的接口可用校验
             */
            override fun onVerifyInterrupt(alicomFusionEvent: AlicomFusionEvent?) {
                Log.d(TAG, "AlicomFusionAuthCallBack---onVerifyInterrupt "+alicomFusionEvent.toString())
            }
        }
        alicomFusionBusiness?.setAlicomFusionAuthCallBack(alicomFusionAuthCallBack)
    }

    private fun updateBusinessHalfWay(userData: UserModel?, verifyResult: HalfWayVerifyResult?, nodeName: String?) {
        activity?.runOnUiThread {
            if (userData != null && !TextUtils.isEmpty(userData.phoneNumber)) {
                Toast.makeText(requireContext(), "校验通过", Toast.LENGTH_SHORT).show()
                verifyResult?.verifyResult(true)
                DataHelper.setUserData(requireContext(), userData)
            } else {
                Toast.makeText(requireContext(), "校验未通过", Toast.LENGTH_SHORT).show()
                if (nodeName?.equals(AlicomFusionConstant.ALICOMFUSIONAUTH_SMSAUTHNODENAME) == true && sum < 3) {
                    sum ++
                } else {
                    verifyResult?.verifyResult(false)
                    sum = 0
                }
            }
        }
    }

    private fun updateBusiness(userData: UserModel?, nodeName: String?) {
        Log.d(TAG, "verifyTokenResult = ${userData?.phoneNumber} , nodeName = $nodeName")
        activity?.runOnUiThread {
            dismissProgress()
            if (userData != null && !TextUtils.isEmpty(userData.phoneNumber)) {
                AppTipsHelp().customToast(NotepadApp.INSTANCE?.applicationContext!!,false, R.drawable.svg_check, R.color.text_black, R.string.login_succeeded)
                FlowBus.with<Boolean>(FlowBus.BUS_WORK_UPDATE).postMain(true)
                lifecycleScope.launch(Dispatchers.Default) {
                    delay(1500)
                    withContext(Dispatchers.Main) {
                        if (nodeName == AlicomFusionConstant.ALICOMFUSIONAUTH_SMSAUTHNODENAME) {
                            alicomFusionBusiness?.stopSceneWithTemplateId("100001")
                            /**登录成功*/
                        } else {
                            //继续场景；当前认证成功
                            alicomFusionBusiness?.continueSceneWithTemplateId("100001", true)
                            /**登录成功*/
                        }
                        DataHelper.setUserData(requireContext(), userData)
                        updateView(userData)
                    }
                }
            } else {
                if (nodeName?.equals(AlicomFusionConstant.ALICOMFUSIONAUTH_SMSAUTHNODENAME) == true) {
                    AppTipsHelp().customToast(requireContext(),true, R.drawable.svg_tips_red, R.color.text_red_color, R.string.verification_code_error)
                } else AppTipsHelp().customToast(requireContext(),true, R.drawable.svg_tips_red, R.color.text_red_color, R.string.login_failed)

                lifecycleScope.launch(Dispatchers.Default) {
                    delay(1500)
                    withContext(Dispatchers.Main) {
                        if (nodeName?.equals(AlicomFusionConstant.ALICOMFUSIONAUTH_SMSAUTHNODENAME) == true && sum < 3) {
                            sum ++
                            //结束场景，当前认证失败
                            alicomFusionBusiness?.stopSceneWithTemplateId("100001")
                        } else {
                            sum = 0
                            //继续场景；当前认证失败
                            alicomFusionBusiness?.continueSceneWithTemplateId("100001", false)
                        }
                    }
                }
            }
        }
    }

    private val uiCallBack = object : AlicomFusionAuthUICallBack {
        //一键登录
        override fun onPhoneNumberVerifyUICustomView(
            templateId: String?,
            nodeId: String?,
            fusionNumberAuthModel: FusionNumberAuthModel?
        ) {
            dismissProgress()
            fusionNumberAuthModel?.builder!!
                //导航栏设置
                .setNavHidden(true)
                //logo设置
                .setLogoHidden(false)
                .setLogoOffsetY(126)
                .setLogoImgPath("app_logo")
                //单独设置授权页协议文本颜色
                .setPrivacyBefore("我同意")
                .setAppPrivacyOne("《用户服务协议》", NetConfig.USER_AGREEMENT)
                .setAppPrivacyTwo("《隐私政策》", NetConfig.URL_PRIVACY_POLICY)
                .setPrivacyOneColor(Color.BLACK)
                .setPrivacyTwoColor(Color.BLACK)
                .setPrivacyOperatorColor(Color.BLACK)
                .setUncheckedImgDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.svg_check_box_normal_black))
                .setCheckedImgDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.svg_check_box_select_black))
                .setProtocolGravity(Gravity.CENTER_HORIZONTAL)
                .setCheckBoxMarginTop(0)
                .setVendorPrivacyPrefix("《")
                .setVendorPrivacySuffix("》")
                .setPrivacyAlertIsNeedShow(false)
                .setPrivacyOffsetY_B(235)
                .setPrivacyMargin(44)
                .setPrivacyTextSizeDp(14)
                //号码栏设置
                .setNumberSizeDp(26)
                .setNumFieldOffsetY(310)
                .setNumberLayoutGravity(Gravity.CENTER_HORIZONTAL)
                .setNumberFieldOffsetX(-3)
                //Slogan设置
                .setSloganOffsetY(289)
                .setSloganTextColor(requireContext().getColor(R.color.text_black_60))
                .setSloganTextSizeDp(12)
                //登录按钮
                .setLogBtnText(requireContext().getString(R.string.one_click_login_with_local_phone_number))
                .setLogBtnHeight(52)
                .setLogBtnBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.selector_theme_16_bg))
                .setLogBtnOffsetY(367)
                .setLogBtnTextSizeDp(16)
                //页面相关函数
                /*.setDialogBottom(true)
                .setDialogHeight(530)
                .setPageBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.bottom_sheet_bg))*/
                //授权页使用系统字体
                .setLogBtnTypeface(Typeface.MONOSPACE)
                .setNumberTypeface(Typeface.defaultFromStyle(Typeface.BOLD))
                .setPrivacyAlertContentTypeface(Typeface.MONOSPACE)

            fusionNumberAuthModel.setUiClickListener { errorId, _, json ->
                Log.d(TAG, "$errorId , $json")
                val mapType = object : TypeToken<Map<String, String>>(){}.type
                val map: Map<String, String> = Gson().fromJson(json, mapType)
                map.keys.forEach { key ->
                    if (key == "isChecked") {
                        if (map[key] == "false") AppTipsHelp().customToast(requireActivity(),false, R.drawable.svg_tips, R.color.text_black, R.string.agree_the_following_agreements)
                    } else if (key == "url") {
                        LaunchUtils.launchWeb(requireContext(), map[key]!!, map["name"]!!)
                    }
                }
            }

            fusionNumberAuthModel.removeAuthRegisterXmlConfig()
            fusionNumberAuthModel.removeAuthRegisterViewConfig()

            /**
             * 动态添加控件
             * @param: view的ID
             * @param: view的动态配置
             */
            fusionNumberAuthModel.addAuthRegistViewConfig("appName", AuthRegisterViewConfig.Builder()
                //两种加载方式都可以
                .setView(initAppNameView())
                //RootViewId有三个参数
                //AuthRegisterViewConfig.RootViewId.ROOT_VIEW_ID_BODY 导航栏以下部分为body
                //AuthRegisterViewConfig.RootViewId.ROOT_VIEW_ID_TITLE_BAR 导航栏部分 设置导航栏部分记得setNavHidden和setNavReturnHidden显示后才可看到效果
                //AuthRegisterViewConfig.RootViewId.ROOT_VIEW_ID_NUMBER 手机号码部分
                .setRootViewId(AuthRegisterViewConfig.RootViewId.ROOT_VIEW_ID_BODY)
                .build())

            fusionNumberAuthModel.addAuthRegistViewConfig("otherPhoneLogin", AuthRegisterViewConfig.Builder()
                //两种加载方式都可以
                .setView(initOtherPhoneLoginView())
                .setRootViewId(AuthRegisterViewConfig.RootViewId.ROOT_VIEW_ID_BODY)
                .setCustomInterface {
                    //跳转到验证码登录界面
                    fusionNumberAuthModel.otherPhoneLogin()
                }
                .build())

            fusionNumberAuthModel.addAuthRegistViewConfig("backBtn", AuthRegisterViewConfig.Builder()
                //两种加载方式都可以
                .setView(initBackView())
                .setRootViewId(AuthRegisterViewConfig.RootViewId.ROOT_VIEW_ID_BODY)
                .setCustomInterface {
                    //销毁服务；若想继续使用SDK，需重新初始化
                    alicomFusionBusiness?.destory()
                    initAlicomFusionSdk()
                }
                .build())
        }

        //短信验证码
        override fun onSMSCodeVerifyUICustomView(
            templateId: String?,
            s: String?,
            isAutoInput: Boolean,
            alicomFusionVerifyCodeView: AlicomFusionVerifyCodeView?
        ) {
            activity?.runOnUiThread {
                dismissProgress()
                val rootRl = alicomFusionVerifyCodeView?.rootRl
                alicomFusionVerifyCodeView?.apply {
                    rootRl?.removeAllViews()
                }

                val binding = FragmentVerificationCodeLoginBinding.inflate(layoutInflater)
                val statusBarHeight = AndroidBarUtils.getStatusBarHeight(requireContext())
                // 获取渐变起始颜色和结束颜色
                val startColor = ContextCompat.getColor(requireContext(), R.color.pink)
                val endColor = ContextCompat.getColor(requireContext(), R.color.orange)
                // 文字创建渐变效果
                val shader = LinearGradient(0f, 0f, statusBarHeight.toFloat(), 0f, startColor, endColor, Shader.TileMode.CLAMP)
                binding.getVerificationCodeBtn.paint.shader = shader
                binding.verificationCodeLoginBtn.apply {
                    alpha = 0.2f
                    isEnabled = false
                }

                binding.etPhoneNumber.doOnTextChanged { text, _, _, _ ->
                    val content = binding.etVerificationCode.text

                    if (text?.length!! < 11 || content.length < 6 || !binding.checkVerificationCodeLoginMsg.isChecked) {
                        binding.verificationCodeLoginBtn.alpha = 0.2f
                        binding.verificationCodeLoginBtn.isEnabled = false
                    } else {
                        binding.verificationCodeLoginBtn.alpha = 1f
                        binding.verificationCodeLoginBtn.isEnabled = true
                    }
                }
                binding.etVerificationCode.doOnTextChanged { text, _, _, _ ->
                    val content = binding.etPhoneNumber.text
                    if (text?.length!! < 6 || content.length < 11 || !binding.checkVerificationCodeLoginMsg.isChecked) {
                        binding.verificationCodeLoginBtn.alpha = 0.2f
                        binding.verificationCodeLoginBtn.isEnabled = false
                    } else {
                        binding.verificationCodeLoginBtn.alpha = 1f
                        binding.verificationCodeLoginBtn.isEnabled = true
                    }
                }

                binding.checkVerificationCodeLoginMsg.setOnClickListener {
                    if (binding.checkVerificationCodeLoginMsg.isChecked && binding.etVerificationCode.text.length == 6 && binding.etPhoneNumber.text.length == 11) {
                        binding.verificationCodeLoginBtn.alpha = 1f
                        binding.verificationCodeLoginBtn.isEnabled = true
                    } else {
                        binding.verificationCodeLoginBtn.alpha = 0.2f
                        binding.verificationCodeLoginBtn.isEnabled = false
                    }
                }

                binding.etPhoneNumber.setOnClickListener {
                    formatClipboardToDigits()
                }
                binding.etPhoneNumber.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        formatClipboardToDigits()
                    }
                }

                binding.etVerificationCode.setOnClickListener {
                    formatClipboardToDigits()
                }
                binding.etVerificationCode.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        formatClipboardToDigits()
                    }
                }

                val rl = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
                rl.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                binding.root.layoutParams = rl
                rootRl?.addView(binding.root)

                //设置协议超链接
                setTextLine(binding.agreeAgreement2Tv)
                //获取验证码
                binding.getVerificationCodeBtn.setOnClickListener {
                    if (NetWorkUtils.isNetworkConnected(requireContext())) {
                        if (binding.checkVerificationCodeLoginMsg.isChecked) {
                            if (RegexUtils.isTel(binding.etPhoneNumber.text) || binding.etPhoneNumber.text.toString().isValidPhoneNumber()) {
                                val shaderGrey = LinearGradient(0f, 0f, statusBarHeight.toFloat(), 0f, requireActivity().resources.getColor(R.color.text_black_60), requireActivity().resources.getColor(R.color.text_black_60), Shader.TileMode.CLAMP)
                                binding.getVerificationCodeBtn.paint.shader = shaderGrey
                                binding.getVerificationCodeBtn.text = requireContext().getString(R.string.sending)
                                alicomFusionVerifyCodeView?.verifyCodeBtnClick(binding.etPhoneNumber.text.toString())
                                /**获得验证码*/
                                countNum(requireActivity(), binding.getVerificationCodeBtn, shader)
                                binding.etVerificationCode.requestFocus()
                                binding.etVerificationCode.isCursorVisible = true
                            } else AppTipsHelp().customToast(requireContext(),false, R.drawable.svg_tips, R.color.text_black, R.string.please_enter_correct_phone_number)
                        } else AppTipsHelp().customToast(requireContext(),false, R.drawable.svg_tips, R.color.text_black, R.string.agree_the_following_agreements)
                    } else AppTipsHelp().customToast(requireContext(),true, R.drawable.svg_tips_red, R.color.text_red_color, R.string.network_timeout)
                }

                //验证码登录
                binding.verificationCodeLoginBtn.setOnClickListener {
                    if (NetWorkUtils.isNetworkConnected(requireContext())) {
                        if (binding.checkVerificationCodeLoginMsg.isChecked) {
                            if ((RegexUtils.isTel(binding.etPhoneNumber.text) || binding.etPhoneNumber.text.toString().isValidPhoneNumber()) && binding.etVerificationCode.text.length == 6) {
                                binding.verificationCodeLoginBtn.apply {
                                    isEnabled = false
                                    text = requireActivity().getString(R.string.logging_in)
                                }
                                alicomFusionVerifyCodeView?.submitVerifyCodeBtnClick(binding.etPhoneNumber.text.toString(), binding.etVerificationCode.text.toString())
                            } else AppTipsHelp().customToast(requireContext(),true, R.drawable.svg_tips_red, R.color.text_red_color, R.string.login_failed)
                        } else AppTipsHelp().customToast(requireContext(),false, R.drawable.svg_tips, R.color.text_black, R.string.agree_the_following_agreements)
                    } else AppTipsHelp().customToast(requireContext(),true, R.drawable.svg_tips_red, R.color.text_red_color, R.string.network_timeout)
                }

                binding.back.setOnClickListener {
                    //销毁服务；若想继续使用SDK，需重新初始化
                    alicomFusionBusiness?.destory()
                    initAlicomFusionSdk()
                }
            }
        }

        //用户主动发短信
        override fun onSMSSendVerifyUICustomView(
            templateId: String?,
            nodeId: String?,
            view: AlicomFusionUpSMSView?,
            receivePhoneNumber: String?,
            verifyCode: String?
        ) {
            activity?.runOnUiThread {
                dismissProgress()
                val statusBarHeight = BarUtils.getStatusBarHeight()
                val titleRl = view?.titleRl
                val layoutParams = titleRl?.layoutParams as RelativeLayout.LayoutParams
                layoutParams.setMargins(0, statusBarHeight, 0, 0)
                titleRl.layoutParams = layoutParams
            }
        }
    }

    private fun initOtherPhoneLoginView(): View {
        val otherPhoneTv = TextView(requireContext())
        val layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        //一键登录按钮默认marginTop 270dp
        layoutParams.setMargins(0, BitmapUtils.dp2px(requireContext(), 440f), 0, 0)
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
        return otherPhoneTv.apply {
            text = "其他手机号码登录"
            setTextColor(requireContext().getColor(R.color.text_black_60))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16.0f)
            this.layoutParams = layoutParams
        }
    }

    /**
     * 返回按钮
     */
    private fun initBackView(): View {
        val backView = ImageView(requireContext())
        val layoutParams = RelativeLayout.LayoutParams(BitmapUtils.dp2px(requireContext(), 32f), BitmapUtils.dp2px(requireContext(), 32f))
        //一键登录按钮默认marginTop 270dp
        layoutParams.setMargins(BitmapUtils.dp2px(requireContext(), 12f), BitmapUtils.dp2px(requireContext(), 8f), 0, 0)
        return backView.apply {
            setImageResource(R.drawable.svg_back_black_no_bg)
            this.layoutParams = layoutParams
        }
    }

    /**
     * AppName标题
     */
    private fun initAppNameView(): View {
        val otherPhoneTv = TextView(requireContext())
        val layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        //一键登录按钮默认
        layoutParams.setMargins(0, BitmapUtils.dp2px(requireContext(), 228f), 0, 0)
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
        return otherPhoneTv.apply {
            text = requireContext().getString(R.string.app_name)
            setTextColor(requireContext().getColor(R.color.text_black))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20.0f)
            typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            this.layoutParams = layoutParams
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateView(userData: UserModel) {
        binding.loggedIn.loggedInBtn.visibility = View.VISIBLE
        binding.loggedIn.ivCustomerType.isEnabled = false
        binding.notLoggedIn.notLoggedInBtn.visibility = View.GONE
        binding.loggedIn.userName.text = "用户：${userData.userId}"

        lifecycleScope.launch(Dispatchers.Default) {
            val subInfo = userViewModel.getUserSubscribeInfo(userData.token)
            withContext(Dispatchers.Main) {
                if (subInfo != null && subInfo.status == "Active") {
                    binding.loggedIn.ivCustomerType.isEnabled = true
                    binding.loggedIn.userIdentity.text = requireContext().getString(R.string.identity_member_users, subInfo.activeTime)
                } else {
                    binding.loggedIn.ivCustomerType.isEnabled = false
                    binding.loggedIn.userIdentity.text = requireContext().getString(R.string.identity_ordinary_users)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun countNum(activity: FragmentActivity, getCodeBtn: TextView, shader: LinearGradient) {
        var executors = Executors.newScheduledThreadPool(1)
        var count = 60
        executors?.scheduleWithFixedDelay({
            count--
            if (count == 0) {
                executors?.shutdown()
                executors = null
                activity.runOnUiThread {
                    getCodeBtn.isEnabled = true
                    getCodeBtn.paint.shader = shader
                    getCodeBtn.text = activity.resources.getString(R.string.resend)
                }
            } else {
                activity.runOnUiThread {
                    getCodeBtn.isEnabled = false
                    getCodeBtn.text = "${count}s"
                }
            }
        }, 0, 1000, TimeUnit.MILLISECONDS)
    }

    fun formatClipboardToDigits() {
        val clipData = clipboardManager.primaryClip ?: return

        if (clipData.itemCount > 0) {
            val originalText = clipData.getItemAt(0).text.toString()
            var formattedText = originalText.filter { it.isDigit() }
            if (originalText.startsWith("+86")) {
                formattedText = formattedText.drop(2) // 移除开头的 "+86"
            }

            if (formattedText != originalText) {
                val newClipData = ClipData.newPlainText("Formatted Numbers", formattedText)
                clipboardManager.setPrimaryClip(newClipData)
            }
        }
    }

    private fun setTextLine(textView: TextView) {
        //设置点击超链接
        val content = requireActivity().getString(R.string.agree_agreement_2)
        val span1 = requireActivity().getString(R.string.user_service_agreement_line)
        val span2 = requireActivity().getString(R.string.privacy_policy_line)
        val click1 = object : ClickableSpan() {
            override fun onClick(widget: View) {
                //用户服务协议
                LaunchUtils.launchWeb(activity, NetConfig.USER_AGREEMENT, requireActivity().getString(R.string.user_service_agreement_line))
            }
            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
            }
        }
        val click2 = object : ClickableSpan() {
            override fun onClick(widget: View) {
                //隐私政策
                LaunchUtils.launchWeb(activity, NetConfig.URL_PRIVACY_POLICY, requireActivity().getString(R.string.privacy_policy))
            }
            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
            }
        }
        val spans = arrayOf(TextSpanUtils.Span(span1, R.color.text_black, click1), TextSpanUtils.Span(span2, R.color.text_black, click2))
        TextSpanUtils.setSpanText(requireContext(), content, spans, textView)
    }

    override fun onDestroy() {
        super.onDestroy()
        alicomFusionBusiness?.destory()
    }
}