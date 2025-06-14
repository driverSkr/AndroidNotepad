
Google 授权登录
1、核心类：GoogleLogin
2、接入前请先配置CLIENT_ID
3、CLIENT_ID为：https://console.cloud.google.com/ -凭据-OAuth 2.0客户端id-Web client的客户端id
4、若在OAuth 2.0客户端配置的SHA1指纹为release key指纹，调试时需要用release调试
5、登录成功后的用户若需要入库，请将token等字段上报到后端校验登录合法性
6、后端需要用同一个CLIENT_ID

# 代码实例

private val googleLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
    if (it.resultCode == RESULT_OK) {
        GoogleLogin.handleAccount(this, it.data) { account ->
            if (!account.isEmpty()) {
                val user = GoogleUser(account.id, account.name, account.email, account.token)
                signOutGoogle(this@SettingActivity)
                lifecycleScope.launch {
                    userViewModel.login(this@SettingActivity, user)
                }
            } else {
                "登陆失败".showToast(this@SettingActivity, ToastType.ERROR)
            }
        }
    } else {
        "登陆失败".showToast(this@SettingActivity, ToastType.ERROR)
    }
}

# 启动 Google 登录
GoogleLogin.signWithIntent(this@SettingActivity)?.let { googleLauncher.launch(it) }

# 登出
private fun signOutGoogle(activity: Activity) {
    GoogleLogin.signOut(activity, object : OnCompleteListener {
        override fun onComplete() {
            println("signOutGoogle: GoogleLogin.signOut")
        }
    })
}