package com.auto_reply.webservice

import android.util.Log
import com.auto_reply.AutoReplyApplication
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*

/**
 * Created by admin on 15-09-2016.
 */
object WebServiceCaller {
    fun callWebApi(
        jsonObject: JsonObject,
        apiName: String,
        apiCallbacks: ApiCallbacks?
    ) {
        Log.e(apiName, jsonObject.toString())

        val call =
            ApiClient.client.create(ApiInterface::class.java)
                .apiCall(
                    AutoReplyApplication.getInstance().getAuthHeaders().header,
                    apiName,
                    jsonObject
                )

        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.body() != null && isSuccess(response.body()!!)) {
                    Log.e("$apiName: Response", response.body()!!.toString())
                    apiCallbacks?.onSuccess(response.body()!!, apiName)
                } else {
                    Log.e("$apiName: Response", response.body().toString())

                    apiCallbacks?.onError(response.body()?.toString() ?: "", apiName)
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                try {
                    Gson().fromJson(
                        jsonObject,
                        JsonObject::class.java
                    )
                    t.message?.let { apiCallbacks!!.onError(it, apiName) }
                } catch (e: Exception) {
                    apiCallbacks!!.networkError(t.message)
                }
            }
        }
        )
    }

    fun callWebApiArray(
        jsonArray: JsonArray,
        apiName: String,
        apiCallbacks: ApiCallbacks?
    ) {
        Log.e(apiName, jsonArray.toString())

        val call =
            ApiClient.client.create(ApiInterface::class.java)
                .apiCallArray(
                    AutoReplyApplication.getInstance().getAuthHeaders().header,
                    apiName,
                    jsonArray
                )

        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.body() != null && isSuccess(response.body()!!)) {
                    Log.e("$apiName: Response", response.body()!!.toString())
                    apiCallbacks?.onSuccess(response.body()!!, apiName)
                } else {
                    Log.e("$apiName: Response", response.body().toString())
                    apiCallbacks?.onError(response.body()?.toString() ?: "", apiName)
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                t.message?.let { apiCallbacks!!.onError(it, apiName) }
            }
        }
        )
    }

    fun callGetCompany(
        call: Call<JsonObject>,
        apiName: String,
        apiCallbacks: ApiCallbacks?
    ) {
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.body() != null && isSuccess(response.body()!!)) {
                    Log.e("$apiName: Response", response.body()!!.toString())
                    apiCallbacks?.onSuccess(response.body()!!, apiName)
                } else {
                    Log.e("$apiName: Response", response.body().toString())
                    apiCallbacks?.onError(response.body().toString(), apiName)
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                t.message?.let { apiCallbacks!!.onError(it, apiName) }
            }
        }
        )
    }

    fun callGetApi(
        apiName: String,
        apiCallbacks: ApiCallbacks?
    ) {
        val call =
            ApiClient.client.create(ApiInterface::class.java)
                .getApiCall(
                    AutoReplyApplication.getInstance().getAuthHeaders().header,
                    apiName
                )
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.body() != null && isSuccess(response.body()!!)) {
                    Log.e("$apiName: Response", response.body()!!.toString())
                    apiCallbacks?.onSuccess(response.body()!!, apiName)
                } else {
                    Log.e("$apiName: Response", response.body().toString())
                    apiCallbacks?.onError(response.body().toString(), apiName)
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                t.message?.let { apiCallbacks!!.onError(it, apiName) }
            }
        }
        )
    }

   
    /**
     * method to check response success
     *
     * @param jsonObject response json object
     * @return true if success
     */
    fun isSuccess(jsonObject: JsonObject): Boolean {
        return jsonObject.get("Status").asString == "Success"
    }

    /**
     * method call to get response message from response json
     *
     * @param jsonObject response json
     * @return message as string
     */
    fun getResponseMessage(jsonObject: String): String {
        return if (Gson().fromJson(
                jsonObject,
                JsonObject::class.java
            ).get("ResponseMessage") != null
        ) Gson().fromJson(
            jsonObject,
            JsonObject::class.java
        ).get("ResponseMessage").asString else "Error"
    }

    /**
     * method call to get response packet as json object
     *
     * @param jsonObject json response
     * @return response packet as json object
     */
    fun getResponsePacket(jsonObject: JsonObject): JsonObject {
        return jsonObject.getAsJsonObject("ResponsePacket")
    }

    /**
     * method call to get api status response packet as json object
     *
     * @param jsonObject json response
     * @return response packet as json object
     */
    fun getApiStatus(jsonObject: JsonObject): JsonArray? {
        return jsonObject.getAsJsonArray("Status")
    }

    /**
     * method call to get response packet as json object
     *
     * @param jsonObject json response
     * @return response packet as json object
     */
    fun getResponseData(jsonObject: JsonObject): JsonObject {
        return jsonObject.getAsJsonObject("Data")
    }

    /**
     * method call to get response packet as json object
     *
     * @param jsonObject json response
     * @return response packet as json object
     */
    fun getResponse(jsonObject: JsonObject): JsonObject {
        return jsonObject
    }

    /**
     * method call to get response packet as json array
     *
     * @param jsonObject json response
     * @return response packet as json array
     */
    fun getResponsePacketArray(jsonObject: JsonObject): JsonArray {
        return jsonObject.getAsJsonArray("Data")
    }

    fun pojo2Map(obj: Any): Map<String, Any> {
        val hashMap = HashMap<String, Any>()
        try {
            val c = obj.javaClass
            val m = c.methods
            for (i in m.indices) {
                if (m[i].name.indexOf("get") == 0) {
                    val name = m[i].name.toLowerCase().substring(3, 4) + m[i].name.substring(4)
                    hashMap[name] = m[i].invoke(obj, *arrayOfNulls(0))
                }
            }
        } catch (e: Throwable) {
            //log error
        }

        return hashMap
    }
}
