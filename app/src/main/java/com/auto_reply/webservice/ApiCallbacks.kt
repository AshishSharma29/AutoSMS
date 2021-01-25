package com.auto_reply.webservice

import com.google.gson.JsonObject

/**
 * Created by Ashish on 27/08/2019
 */

interface ApiCallbacks {
    fun onSuccess(jsonObject: JsonObject, anEnum: String)

    fun onError(jsonObject: String, anEnum: String)
    fun networkError(message: String?)
}
