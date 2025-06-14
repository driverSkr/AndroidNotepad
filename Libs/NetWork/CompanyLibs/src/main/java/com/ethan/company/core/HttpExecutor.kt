package com.ethan.company.core

import com.ethan.company.callback.FileCallback
import com.ethan.company.callback.SimpleCallback
import com.ethan.company.gson.BaseRObjectResult
import com.ethan.company.gson.BaseResult
import com.ethan.company.gson.IpInfo


class HttpExecutor : IHttpEngine {

    companion object {
        private var executor: HttpExecutor? = null
            get() {
                if (field == null) {
                    field = HttpExecutor()

                }
                return field
            }

        @Synchronized
        fun getInstance(): HttpExecutor {
            return executor!!
        }
    }

    private var engine: IHttpEngine? = null

    init {
        this.engine = HttpEngineImpl.getInstance()
    }

    override fun init(domain: String, debug: Boolean): NetServer {
        return engine!!.init(domain, debug)
    }

    override fun addCommonParams(key: String, value: Any) {
        engine!!.addCommonParams(key, value)
    }

    override fun addCommonParams(params: MutableMap<String, Any>) {
        engine!!.addCommonParams(params)
    }

    override fun addCommonHeaders(key: String, value: Any) {
        engine!!.addCommonHeaders(key, value)
    }

    override fun addCommonHeaders(params: MutableMap<String, Any>) {
        engine!!.addCommonHeaders(params)
    }


    override fun postString(server: NetServer, path: String, params: MutableMap<String, Any>, callback: SimpleCallback) {
        engine!!.postString(server, path, params, callback)
    }

    override suspend fun asyncPostUrl(server: NetServer, path: String, params: MutableMap<String, Any>): BaseResult? {
        return engine!!.asyncPostUrl(server, path, params)
    }

    override fun getString(server: NetServer, path: String, params: MutableMap<String, Any>, callback: SimpleCallback) {
        engine!!.getString(server, path, params, callback)
    }

    override fun getFile(server: NetServer, path: String, callback: FileCallback) {
        return engine!!.getFile(server, path, callback)
    }

    override suspend fun syncGetFile(server: NetServer, path: String, dstPath: String): String {
        return engine!!.syncGetFile(server, path, dstPath)
    }

    override fun upFile(server: NetServer, path: String, filePath: String, params: MutableMap<String, Any>, callback: SimpleCallback) {
        engine!!.upFile(server, path, filePath, params, callback)
    }

    override suspend fun asyncPostString(server: NetServer, path: String, params: MutableMap<String, Any>): BaseResult? {
        return engine!!.asyncPostString(server, path, params)
    }
    override suspend fun asyncPostStringWithCache1H(server: NetServer, path: String, params: MutableMap<String, Any>): BaseResult? {
        return engine!!.asyncPostStringWithCache1H(server, path, params)
    }

    override suspend fun asyncPostStringRObject(server: NetServer, path: String, params: MutableMap<String, Any>): BaseRObjectResult? {
        return engine!!.asyncPostStringRObject(server, path, params)
    }

    override suspend fun consumePostString(server: NetServer, path: String, params: MutableMap<String, Any>): BaseResult? {
        return engine!!.consumePostString(server, path, params)
    }

    override suspend fun asyncGetString(server: NetServer, path: String, params: MutableMap<String, Any>): BaseResult? {
        return engine!!.asyncGetString(server, path, params)
    }

    override suspend fun asyncGetStringIp(server: NetServer, path: String): IpInfo? {
        return engine!!.asyncGetStringIp(server, path)
    }
}