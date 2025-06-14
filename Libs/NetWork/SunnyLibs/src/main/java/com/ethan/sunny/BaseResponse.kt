package com.ethan.sunny

import com.google.gson.annotations.Expose

/**
 * 请求数据返回封装的抽象方法
 */
abstract class DataResult<T> {
    abstract fun isSuccess(): Boolean
    abstract fun Data(): T
    abstract fun getMsg(): String
    abstract fun isNetworkFun(): Boolean
}

/**
 * Response基础封装，若返回的接口不是列表型或数据字段为"Data"的数据型
 * 则需要自己继承该类实现相关数据Bean类
 */
abstract class BaseResponse<T>(
    var code: Int? = null,
    var message: String? = null,
) : DataResult<T>() {
    override fun isSuccess(): Boolean = code == 200

    override fun getMsg(): String = message ?: ""
}

/**
 * 数据类型DataResponse，继承后使用
 */
data class DataResponse<T>(
    var data: T
) : BaseResponse<T>() {
    override fun Data(): T = data
    override fun isNetworkFun(): Boolean = true
}

/**
 * 用于无数据仅返回status和msg的接口
 */
data class EmptyResponse(
    @Expose(serialize = false, deserialize = false)
    var data: Any
) : BaseResponse<Any>() {
    override fun Data(): Any = ""
    override fun isNetworkFun(): Boolean = true
}

/**
 * 列表类型Response，继承后使用
 */
data class ListResponse<T>(
    var data: T,

) : BaseResponse<T>() {
    override fun Data(): T = data
    override fun isNetworkFun(): Boolean = true
}


/**
 * Response基础封装，若返回的接口不是列表型或数据字段为"Data"的数据型
 * 则需要自己继承该类实现相关数据Bean类
 */
abstract class ResBaseResponse<T>(
    private var code: String? = null,
    private var message: String? = null,
) : DataResult<T>() {
    override fun isSuccess(): Boolean = code.equals("200", true)
    override fun getMsg(): String = message ?: ""
}

/**
 * 数据类型DataResponse，继承后使用
 */
data class ResDataResponse<T>(
    var data: T
) : ResBaseResponse<T>() {
    override fun Data(): T = data
    override fun isNetworkFun(): Boolean = true
}

abstract class PayBaseResponse<T>(
    var status: String? = null,
    private var msg: String? = null,
) : DataResult<T>() {
    override fun isSuccess(): Boolean = status.equals("200", true)
    override fun getMsg(): String = msg ?: ""
}




/**
 * 数据类型DataResponse，继承后使用
 */
data class PayDataResponse<T>(
    var data: T
) : PayBaseResponse<T>() {
    override fun Data(): T = data
    override fun isNetworkFun(): Boolean = true
}

/**
 * cms反馈基础类
 * 则需要自己继承该类实现相关数据Bean类
 */
abstract class BaseCMSResponse<T>(
    var status: Int? = null,
    var message: String? = null,
) : DataResult<T>() {
    override fun isSuccess(): Boolean = status == 200

    override fun getMsg(): String = message ?: ""
}

data class EmptyCMSResponse(
    @Expose(serialize = false, deserialize = false)
    var data: Any
) : BaseCMSResponse<Any>() {
    override fun Data(): Any = ""
    override fun isNetworkFun(): Boolean = true
}

/**
 * 数据类型DataNotNetWorkResponse，继承后使用,非网络数据异步调用
 */
data class DataNotNetWorkResponse<T>(
    var data: T
) : BaseResponse<T>() {
    override fun Data(): T = data
    override fun isNetworkFun(): Boolean = false

}

