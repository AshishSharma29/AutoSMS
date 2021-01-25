package com.auto_reply.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CheckLicenceResponseModel(

    @field:SerializedName("Status")
    val status: String? = null,

    @field:SerializedName("ResponseCode")
    val responseCode: String? = null,

    @field:SerializedName("ResponsePacket")
    val responsePacket: ResponsePacket? = null,

    @field:SerializedName("ResponseMessage")
    val responseMessage: String? = null
) : Serializable

data class ResponsePacket(

    @field:SerializedName("Status")
    val status: Int? = null,

    @field:SerializedName("Message")
    val message: String? = null,
    val ApkNew: String,
    val ApkUrl: String
) : Serializable