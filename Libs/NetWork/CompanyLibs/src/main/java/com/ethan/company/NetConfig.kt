package com.ethan.company

object NetConfig {
    const val STS_SERVER_DOMAIN = BuildConfig.stsServerDomain
    const val SHARE_URL = BuildConfig.shareUrl
    const val PRODUCT_ID = 8193
    const val STS_TIME = 5 * 60L

    //隐私政策
    const val URL_PRIVACY_POLICY = "http://cbs.niuxuezhang.cn/go?pid=8432&a=p"
    //用户协议
    const val USER_AGREEMENT = "http://cbs.niuxuezhang.cn/go?pid=8432&a=tc"
    //登出
    const val LOGIN_OUT = "/ven-app/login-out"

    //Feedback
    const val FEEDBACK_URL = "https://support.tenorshare.com/"
    const val FEEDBACK_REPORT = "api/v1/ticket/feedback"

    // Ai扫描分割
    const val REMOVE_OBJECT_SEG = "api/v2/tourist/from_site/android/interact-seg"
    const val REMOVE_OBJECT_SEG_VIP = "api/v2/vip/from_site/android/interact-seg"

    // ai 纹身的皮肤分割
    const val AI_TATTOO_SKIN_SEG = "/api/v2/image/body-parsing"

    // 删除任务
    const val DELETE_TASK = "/ven-app/del-task"


    const val InternalTester = false // todo 上线前改为false

    var timeout = 60L // HTTP请求超时时间(firebase控制)


    // 素材后台 生产
    const val BASE_TEST_PAG_URL = "https://material.hitpaw.com" //

    // feedback
    const val URL_BASE_UPLOAD = "https://integrated.tenorshare.com/"
    const val URL_LOG_UPLOAD = "api/v1/ticket/upload"

    const val FUSION_AUTH = "https://aiportrait-api.niuxuezhang.cn" //正式
    const val FUSION_AUTH_GET_TOKEN = "/user/access-token"
    const val FUSION_AUTH_VERIFY_TOKEN = "/user/verify-ali-token"
    const val GET_USER_SUBSCRIBE_INFO = "/user/subscription"
}