package com.ethan.sunny

import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


/**
 * 过滤服务器请求结果，失败抛异常
 * @param block 请求的闭包
 * @param success 请求成功回调
 * @param error 请求失败回调，默认空实现
 */
fun <T> BaseViewModel.request(
    block: suspend () -> DataResult<T>,
    success: (T) -> Unit,
    error: (ApiException) -> Unit = {},
): Job { // 如果需要弹窗 通知Activity/fragment弹窗
    return viewModelScope.launch {
        runCatching {
            // 执行请求体
            block()
        }.onSuccess { result ->
            // 网络请求成功 关闭弹窗
            runCatching {
                // 校验请求结果码是否正确，不正确会抛出异常走下面的onFailure
                executeResponse(result) { t ->
                    // 成功则执行传入的success方法
                    success(t)
                }
            }.onFailure { e ->
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
                // 打印错误消息
                e.message?.let { Log.e("Catching", it) }
                // 失败回调
                getApiException(e)?.let {
                    error(it)
                }
            }
        }.onFailure { e ->
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
            // 打印错误消息
            e.message?.let { Log.e("HttpRequest", it) }
            // 失败回调
            getApiException(e)?.let {
                error(it)
            }
        }
    }
}



/**
 * 过滤服务器请求结果，失败抛异常
 * @param block 请求的闭包
 * @param success 请求成功回调
 * @param error 请求失败回调，默认空实现
 */
fun <T> BaseViewModel.requestListData(
    block: suspend () -> MutableList<T>,
    success: (MutableList<T>) -> Unit,
    error: (ApiException) -> Unit = {},
): Job { // 如果需要弹窗 通知Activity/fragment弹窗
    return viewModelScope.launch {
        runCatching {
            // 执行请求体
            block()
        }.onSuccess { result ->
            // 网络请求成功 关闭弹窗
            runCatching {
                success(result)
            }.onFailure { e ->
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
                // 打印错误消息
                e.message?.let { Log.e("Catching", it) }
                // 失败回调
                getApiException(e)?.let { error(it) }
            }
        }.onFailure { e ->
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
            // 打印错误消息
            e.message?.let { Log.e("HttpRequest", it) }
            // 失败回调
            getApiException(e)?.let { error(it) }
        }
    }
}


/**
 * 过滤服务器请求结果，失败抛异常，成功直接包含最外层返回，业务逻辑，方便处理List相关参数
 * @param block 请求的闭包
 * @param success 请求成功回调
 * @param error 请求失败回调，默认空实现
 */
fun <T> BaseViewModel.requestList(
    block: suspend () -> ListResponse<T>,
    success: (ListResponse<T>) -> Unit,
    error: (ApiException) -> Unit = {},
): Job { // 如果需要弹窗 通知Activity/fragment弹窗
    return viewModelScope.launch {
        runCatching {
            // 执行请求体
            block()
        }.onSuccess { result ->
            // 网络请求成功 关闭弹窗
            runCatching {
                // 校验请求结果码是否正确，不正确会抛出异常走下面的onFailure
                executeResponse(result) {
                    // 成功则执行传入的success方法
                    success(result)
                }
            }.onFailure { e ->
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
                // 打印错误消息
                e.message?.let { Log.e("Catching", it) }
                // 失败回调
                getApiException(e)?.let { error(it) }
            }
        }.onFailure { e ->
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
            // 网络请求异常 关闭弹窗
            // 打印错误消息
            e.message?.let { Log.e("HttpRequest", it) }
            // 失败回调
            getApiException(e)?.let { error(it) }
        }
    }
}

/**
 * 请求结果过滤，判断请求服务器请求结果是否成功，不成功则会抛出异常
 * 此处直接调用DataResult具体实现类的isSuccess()进行判断
 * 由继承了DataResult的数据体实现
 */
suspend fun <T> executeResponse(
    response: DataResult<T>,
    success: suspend CoroutineScope.(T) -> Unit,
) {
    coroutineScope {
        // 网络api进行状态校验
        if (response.isNetworkFun()) {
            when {
                response.isSuccess() -> {
                    success(response.Data())
                }

                else -> {
                    throw ApiException(response.getMsg(), -1)
                }
            }
        }// 非网络api直接返回结果
        else {
            success(response.Data())
        }

    }
}

/**
 * 启动一个在IO协程中的倒计时计时器
 */
fun BaseViewModel.startDownTimeCount(second: Int, finishBlack: (time: Int) -> Unit) = this.apply {
    val timeChannel = Channel<Int>()  // 计时器通道
    viewModelScope.launch {
        // 向通道发送数据
        launch(Dispatchers.IO) {
            for (i in second downTo 0) {
                timeChannel.send(i)
                delay(1000) // 每秒发送一次
            }
            if (!isActive) {
                timeChannel.close()
            }
        }
        // 通道接收数据
        launch(Dispatchers.IO) {
            for (timeSecond in timeChannel) {
                finishBlack(timeSecond)
            }
        }
    }
}