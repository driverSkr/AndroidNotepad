package com.ethan.company.core

import com.ethan.company.callback.FileCallback
import com.ethan.company.callback.SimpleCallback
import com.ethan.company.gson.BaseRObjectResult
import com.ethan.company.gson.BaseResult
import com.ethan.company.gson.IpInfo

interface IHttpEngine {

    fun init(domain: String, debug: Boolean): NetServer

    fun addCommonParams(key: String, value: Any)

    fun addCommonParams(params: MutableMap<String, Any>)

    fun addCommonHeaders(key: String, value: Any)

    fun addCommonHeaders(params: MutableMap<String, Any>)

    fun postString(server: NetServer, path: String, params: MutableMap<String, Any>, callback: SimpleCallback)

    suspend fun asyncPostUrl(server: NetServer, path: String, params: MutableMap<String, Any>): BaseResult?
    fun getString(server: NetServer, path: String, params: MutableMap<String, Any>, callback: SimpleCallback)

    fun getFile(server: NetServer, path: String, callback: FileCallback)

    suspend fun syncGetFile(server: NetServer, path: String, dstPath: String): String

    fun upFile(server: NetServer, path: String, filePath: String, params: MutableMap<String, Any>, callback: SimpleCallback)

    suspend fun asyncPostString(server: NetServer, path: String, params: MutableMap<String, Any>): BaseResult?

    suspend fun asyncPostStringRObject(server: NetServer, path: String, params: MutableMap<String, Any>): BaseRObjectResult?

    suspend fun consumePostString(server: NetServer, path: String, params: MutableMap<String, Any>): BaseResult?

    suspend fun asyncGetString(server: NetServer, path: String, params: MutableMap<String, Any>): BaseResult?

    suspend fun asyncGetStringIp(server: NetServer, path: String): IpInfo?

   suspend fun asyncPostStringWithCache1H(server: NetServer, path: String, params: MutableMap<String, Any>): BaseResult?
}