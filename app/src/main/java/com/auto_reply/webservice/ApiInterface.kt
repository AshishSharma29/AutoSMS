package com.auto_reply.webservice

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import java.util.HashMap

/**
 * Created by  Ashish on 27/08/2019
 */
interface ApiInterface {

    @POST("token")
    @FormUrlEncoded
    fun login(@HeaderMap headers: Map<String, String>, @FieldMap fields: Map<String, Any>): Call<JsonObject>

    @POST
    fun apiCall(@HeaderMap headers: HashMap<String, String>?, @Url url: String, @Body toJson: JsonObject): Call<JsonObject>

    @POST
    fun apiCallArray(@HeaderMap headers: HashMap<String, String>?, @Url url: String, @Body toJson: JsonArray): Call<JsonObject>

    @GET
    fun getApiCall(@HeaderMap headers: HashMap<String, String>?, @Url url: String): Call<JsonObject>

    @Multipart
    @POST
    fun apiCallMultipart(
        @HeaderMap headers: Map<String, String>,
        @Url url: String,
        @Part PostPhoto: MultipartBody.Part,
        @Part("AssessmentDocId") AssessmentDocId: RequestBody,
        @Part("DocTypeId") DocTypeId: RequestBody,
        @Part("DrawingId") DrawingId: RequestBody,
        @Part("ProjectId") ProjectId: RequestBody,
        @Part("RefId") refId: RequestBody,
        @Part("UniqueIdentifier") signatureIdentifier: RequestBody
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST
    fun apiCallFormData(
        @HeaderMap headers: Map<String, String>,
        @Url url: String,
        @Part PostPhoto: MultipartBody.Part,
        @Part("JsonModel") imageDetails: RequestBody
    ): Call<JsonObject>

    @Multipart
    @POST
    fun uploadPhotos(
        @HeaderMap header: HashMap<String, String>, @Url apiName: String,
        @Part mImage: List<MultipartBody.Part>,
        @Part("DrawingPinId") drawingPinId: RequestBody,
        @Part("DrawingId") DrawingId: RequestBody,
        @Part("ProjectId") projectId: RequestBody,
        @Part("PinPhotoId") pinPhotoId: RequestBody,
        @Part("AuditId") auditId: RequestBody,
        @Part("UniqueIdentifier") photoIdentifier: RequestBody
    ): Call<JsonObject>
}
