package com.ethan.company.callback


abstract class FileCallback {

    abstract fun onStart()

    abstract fun onProgress(currLength: Long, totalLength: Long)

    abstract fun onFailure(code: Int, msg: String?)

    abstract fun onFinish()

    abstract fun getDestFileDir(): String

    abstract fun getDestFileName(): String

}