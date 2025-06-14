package com.ethan.company.core

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ethan.company.NetConfig
import com.ethan.company.callback.FileCallback
import com.ethan.company.callback.SimpleCallback
import com.ethan.company.gson.BaseRObjectResult
import com.ethan.company.gson.BaseResult
import com.ethan.company.gson.IpInfo
import com.ethan.company.logging.Level
import com.ethan.company.logging.LoggingInterceptor
import com.blankj.utilcode.util.PathUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.internal.platform.Platform.Companion.INFO
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit


class HttpEngineImpl : IHttpEngine {

    companion object {

        const val errorCode = 10001
        private var engine: HttpEngineImpl? = null
            get() {
                if (field == null) {
                    field = HttpEngineImpl()
                }
                return field
            }

        @Synchronized
        fun getInstance(): HttpEngineImpl {
            return engine!!
        }
    }

    private var retrofit: Retrofit? = null
    private var serverMap = HashMap<String, NetServer>()

    override fun init(domain: String, debug: Boolean): NetServer { // create retrofit
        var server = serverMap[domain]
        if (server == null) {
            synchronized(serverMap) {
                server = serverMap[domain]
                if (server == null) {
                    retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create(GsonBuilder().serializeNulls().create()))
                        .baseUrl(domain) // Put your base URL
                        .client(getHttpClient(debug, domain)).build()
                    val create = retrofit!!.create(NetServer::class.java)
                    serverMap[domain] = create
                    server = create
                }
            }
        }
        return server!!
    }

    private fun getHttpClient(debug: Boolean, baseUrl: String): OkHttpClient { // log interceptor

        // headers
        val headers = CommonHeaders.getInstance().getCommonHeaders()
        val interceptor = Interceptor { chain ->
            try {
                val request = chain.request()
                val requestBuilder = request.newBuilder()
                val url = request.url
                val builder = url.newBuilder()
                requestBuilder.url(builder.build()).method(request.method, request.body)
                headers.forEach {
                    requestBuilder.addHeader(it.key, it.value.toString())
                }
                chain.proceed(requestBuilder.build())
            } catch (e: Exception) {
                serverMap.remove(baseUrl)
                return@Interceptor okhttp3.Response.Builder().request(chain.request()).protocol(Protocol.HTTP_1_1).code(errorCode)
                    .message("client config invalid").body("client config invalid".toResponseBody(null)).build()
            }
        }
        val builder = OkHttpClient.Builder().readTimeout(NetConfig.timeout, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS).addInterceptor(interceptor)

        // 100 MiB
        builder.cache(Cache(directory = File(PathUtils.getExternalAppCachePath(), "retrofit_http_cache"), maxSize = 100L * 1024L * 1024L))
        if (debug || NetConfig.InternalTester) {
            builder.addInterceptor(LoggingInterceptor.Builder().setLevel(Level.BASIC).log(INFO).build())
        }
        return builder.build()
    }

    override fun addCommonParams(key: String, value: Any) {
        CommonParams.getInstance().putParam(key, value)
    }

    override fun addCommonParams(params: MutableMap<String, Any>) {
        CommonParams.getInstance().putAllParams(params)
    }

    override fun addCommonHeaders(key: String, value: Any) {
        CommonHeaders.getInstance().putHeader(key, value)
    }

    override fun addCommonHeaders(params: MutableMap<String, Any>) {
        CommonHeaders.getInstance().putAllHeaders(params)
    }

    override fun postString(server: NetServer, path: String, params: MutableMap<String, Any>, callback: SimpleCallback) {
        server.postString(path, createBody(params)).enqueue(object : Callback<BaseResult> {
            override fun onResponse(call: Call<BaseResult>, response: Response<BaseResult>) {
                if (response.body() == null) {
                    try {
                        val result = Gson().fromJson(response.errorBody()?.string(), BaseResult::class.java)
                        callback.onSuccess(result)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        callback.onSuccess(null)
                    }
                } else {
                    callback.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<BaseResult>, t: Throwable) {
                callback.onFailure(-1, t.message)
            }
        })
    }

    override fun getString(server: NetServer, path: String, params: MutableMap<String, Any>, callback: SimpleCallback) {
        val exParams = mutableMapOf<String, String>()
        for ((key, value) in CommonParams.getInstance().getCommonParams()) {
            exParams[key] = value.toString()
        }
        for ((key, value) in params) {
            exParams[key] = value.toString()
        }
        server.getString(path, exParams).enqueue(object : Callback<BaseResult> {
            override fun onResponse(call: Call<BaseResult>, response: Response<BaseResult>) {
                if (response.body() == null) {
                    try {
                        val result = Gson().fromJson(response.errorBody()?.string(), BaseResult::class.java)
                        callback.onSuccess(result)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        callback.onSuccess(null)
                    }
                } else {
                    callback.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<BaseResult>, t: Throwable) {
                callback.onFailure(-1, t.message)
            }
        })
    }

    override fun getFile(server: NetServer, path: String, callback: FileCallback) {
        val call = server.getFile(path)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                CoroutineScope(Job()).launch(Dispatchers.IO) {
                    callback.onStart()
                    val body = response.body()
                    if (body == null) {
                        callback.onFailure(-1, response.message())
                        return@launch
                    }
                    var inputStream: InputStream? = null
                    var fileOpt: FileOutputStream? = null
                    try {
                        inputStream = body.byteStream()
                        val file = File(callback.getDestFileDir(), callback.getDestFileName())
                        fileOpt = FileOutputStream(file)
                        val buf = ByteArray(8 * 1024)

                        var len: Int
                        var writeLen: Long = 0
                        val totalLen = body.contentLength()
                        callback.onProgress(writeLen, totalLen)
                        while (inputStream.read(buf).also { len = it } != -1) {
                            if (call.isCanceled) {
                                break
                            }
                            val lastProgress = writeLen * 100 / totalLen
                            fileOpt.write(buf, 0, len)
                            writeLen += len.toLong()
                            val curProgress = writeLen * 100 / totalLen // 下载进度百分比回调
                            if (curProgress != lastProgress) {
                                callback.onProgress(writeLen, totalLen)
                            }
                        }
                        callback.onFinish()
                    } catch (e: IOException) {
                        callback.onFailure(-1, e.message)
                    } finally {
                        inputStream?.close()
                        fileOpt?.close()
                        body.close()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callback.onFailure(-1, t.message)
            }
        })
    }

    override suspend fun syncGetFile(server: NetServer, path: String, dstPath: String): String {
        val responseBody = server.syncGetFile(path).body()
        // 保存文件
        return saveFile(responseBody, dstPath)
    }

    override fun upFile(server: NetServer, path: String, filePath: String, params: MutableMap<String, Any>, callback: SimpleCallback) {
        val file = File(filePath)
        val fileRQ = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)

        val allParams = CommonParams.getInstance().getCommonParams()
        allParams.putAll(params)

        val mulBody = MultipartBody.Builder()
        allParams.forEach {
            mulBody.addFormDataPart(it.key, it.value.toString())
        }
        mulBody.addFormDataPart("file", file.name, fileRQ)

        server.upFile(path, mulBody.build().parts).enqueue(object : Callback<BaseResult> {
            override fun onResponse(call: Call<BaseResult>, response: Response<BaseResult>) {
                callback.onSuccess(response.body())
            }

            override fun onFailure(call: Call<BaseResult>, t: Throwable) {
                callback.onFailure(-1, t.message)
            }
        })
    }

    override suspend fun asyncPostString(server: NetServer, path: String, params: MutableMap<String, Any>): BaseResult? {
        return try {
            val response = server.asyncPostString(path, createBody(params))
            if (response.body() == null) {
                Gson().fromJson(response.errorBody()?.string(), BaseResult::class.java)
            } else {
                response.body()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun asyncPostStringWithCache1H(server: NetServer, path: String, params: MutableMap<String, Any>): BaseResult? {
        return try {
            val response = server.asyncPostStringWithCache1H(path, createBody(params))
            if (response.body() == null) {
                Gson().fromJson(response.errorBody()?.string(), BaseResult::class.java)
            } else {
                response.body()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun asyncPostUrl(server: NetServer, path: String, params: MutableMap<String, Any>): BaseResult? {
        return try {
            Log.e("TAG", "asyncPostString: $path")
            val response = server.asyncPostUrl(path, createBody(params))
            if (response.body() == null) {
                Gson().fromJson(response.errorBody()?.string(), BaseResult::class.java)
            } else {
                response.body()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun asyncPostStringRObject(server: NetServer, path: String, params: MutableMap<String, Any>): BaseRObjectResult? {
        return try {
            val response = server.asyncPostStringRObject(path, createBody(params))
            if (response.body() == null) {
                Gson().fromJson(response.errorBody()?.string(), BaseRObjectResult::class.java)
            } else {
                response.body()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun consumePostString(server: NetServer, path: String, params: MutableMap<String, Any>): BaseResult? {
        return try {
            val json = JSONObject()
            CommonParams.getInstance().getCommonParams().forEach {
                if (it.key == "pid") {
                    json.put(it.key, NetConfig.PRODUCT_ID)
                } else {
                    json.put(it.key, it.value)
                }
            }
            params.forEach {
                json.put(it.key, it.value)
            }
            val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            val response = server.asyncPostString(path, body)
            if (response.body() == null) {
                Gson().fromJson(response.errorBody()?.string(), BaseResult::class.java)
            } else {
                response.body()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun asyncGetString(server: NetServer, path: String, params: MutableMap<String, Any>): BaseResult? {
        val exParams = mutableMapOf<String, String>()
        for ((key, value) in CommonParams.getInstance().getCommonParams()) {
            exParams[key] = value.toString()
        }
        for ((key, value) in params) {
            exParams[key] = value.toString()
        }
        return try {
            val response = server.asyncGetString(path, exParams)
            if (response.body() == null) {
                Gson().fromJson(response.errorBody()?.string(), BaseResult::class.java)
            } else {
                response.body()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TAG", "asyncGetString: " + e.message)
            return null
        }
    }

    override suspend fun asyncGetStringIp(server: NetServer, path: String): IpInfo? {
        return try {
            val response = server.asyncGetStringIp(path)
            if (response.body() == null) {
                Gson().fromJson(response.errorBody()?.string(), IpInfo::class.java)
            } else {
                response.body()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TAG", "asyncGetString: " + e.message)
            return null
        }
    }

    private fun createBody(params: MutableMap<String, Any>): RequestBody {
        val gson = Gson()
        val toJson = gson.toJson(params)
        val json = JSONObject(toJson)
        CommonParams.getInstance().getCommonParams().forEach {
            json.put(it.key, it.value)
        }
        return json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    }


    private fun saveFile(body: ResponseBody?, pathWhereYouWantToSaveFile: String): String {
        if (body == null) return ""
        var input: InputStream? = null
        try {
            input = body.byteStream()
            val fos = FileOutputStream(pathWhereYouWantToSaveFile)
            fos.use { output ->
                val buffer = ByteArray(4 * 1024) // or other buffer size
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
            return pathWhereYouWantToSaveFile
        } catch (e: Exception) {
            Log.e("saveFile", e.toString())
        } finally {
            input?.close()
        }
        return ""
    }
}