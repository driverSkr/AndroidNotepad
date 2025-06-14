package com.ethan.sunny

import com.ethan.company.NetConfig
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

/**
 *@time : 2021/8/13
 *@author : tzy
 */
internal interface ApiService {
    /**
     * 请求主页分类分页信息
     */
//    @POST(NetConfig.PAG_API)
//    suspend fun getCategory(@Body data: RequestBody): ResDataResponse<MainData>

//    @POST(NetConfig.GOOGLE_LOGIN)
//    suspend fun getLoginInfo(@Body data: RequestBody): DataResponse<User>

    @POST(NetConfig.LOGIN_OUT)
    suspend fun loginOut(@Header("Authorization") tokenString: String): EmptyResponse

//    @GET(NetConfig.SUB_QUERY)
//    suspend fun getUserSubInfo(@Header("Authorization") tokenString: String, @Header("user-unique") userkey: String, @Query("pid") pid: Int, @Query("op_id") op_id: String): DataResponse<UserSubData>

//    @POST(NetConfig.SUBS_REPORT)
//    suspend fun subReport(@Header("Authorization") tokenString: String,@Body data: RequestBody): DataResponse<ReportResp>

//    @GET(NetConfig.TASK_LIST)
//    suspend fun getTaskList(@Header("Authorization") tokenString: String, @Header("user-unique") userkey: String): DataResponse<List<TaskModel>?>

//    @POST(NetConfig.TASK_STATUS)
//    suspend fun getTaskStatus(@Header("Authorization") tokenString: String, @Header("user-unique") userkey: String,@Body data: RequestBody): DataResponse<List<TaskModel>>

    @POST(NetConfig.DELETE_TASK)
    suspend fun deleteTask(@Header("user-unique") userkey: String,@Body data: RequestBody): EmptyResponse

//    @POST(NetConfig.GUEST_LOGIN)
//    suspend fun getTokenNotVipInfo(@Header("user-unique") userkey: String,@Body data: RequestBody): DataResponse<User>

    @POST(NetConfig.FEEDBACK_REPORT)
    suspend fun postFeedbackReport(@Body data: RequestBody): EmptyCMSResponse

//    @POST(NetConfig.VIDEO_ENHANCE)
//    suspend fun postVideoEnhancer(@Header("Authorization") tokenString: String, @Header("user-unique") userkey: String,@Header("Video-Length") videoTime: Double, @Header("is-sub") isSub: Boolean, @Body data: RequestBody): DataResponse<VideoEnhancerResponse>

//    @POST(NetConfig.AI_HUMAN)
//    suspend fun postAihuman(@Header("Authorization") tokenString: String, @Header("user-unique") userkey: String,@Header("Video-Length") videoTime: Double, @Header("is-sub") isSub: Boolean, @Body data: RequestBody): DataResponse<VideoEnhancerResponse>

//    @POST(NetConfig.REMOVE_BG)
//    suspend fun postRemoveBg(@Header("authorization") tokenString: String, @Header("Video-Length") videoTime: Double, @Header("user-unique") userkey: String, @Header("is-sub") isSub: Boolean, @Body data: RequestBody): DataResponse<RemoveBgResponse>

    // @POST(NetConfig.GET_VE_RESULUT)
    // suspend fun postResult(@Header("Authorization") tokenString: String ,@Body data: RequestBody): DataResponse<MutableList<VeResultResponse>>

//    @POST(NetConfig.GIFT_COINS)
//    suspend fun postPointsGiveaway(@Header("authorization") tokenString: String , @Header("user-unique") userkey: String, @Body data: RequestBody): DataResponse<PointsGiveawayResponse>

//    @GET(NetConfig.POINT_RECORD)
//    suspend fun getPointRecord(@Header("Authorization") tokenString: String, @Query("pid") pid: Int, @Query("limit") limit: Int, @Query("page") page: Int, @Query("type") type: Int): DataResponse<PointRecord>
    /**
     * 提交cms日志
     */
    @Multipart
    @POST(NetConfig.URL_LOG_UPLOAD)
    suspend fun sendCMSLogOk(@Part data: List<MultipartBody.Part>): EmptyCMSResponse
}