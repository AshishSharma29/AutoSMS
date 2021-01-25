package com.auto_reply.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LoginResponseModel(

    @field:SerializedName("Status")
    var status: String? = null,

    @field:SerializedName("ResponseCode")
    var responseCode: String? = null,

    @field:SerializedName("ResponsePacket")
    var responsePacket: LoginResponsePacket,

    @field:SerializedName("ResponseMessage")
    var responseMessage: String? = null
) : Serializable

data class LoginResponsePacket(

    @field:SerializedName("MobileNo")
    var mobileNo: String? = null,

    @field:SerializedName("MamberImg")
    var mamberImg: String? = null,

    @field:SerializedName("LoginUserId")
    var loginUserId: String? = null,

    @field:SerializedName("CompanyName")
    var companyName: String? = null,

    @field:SerializedName("UserName")
    var userName: String? = null,

    @field:SerializedName("Email")
    var email: String? = null,

    @field:SerializedName("UserWebUrl")
    var userWebUrl: String = "",

    @field:SerializedName("LoginAuthKey")
    var loginAuthKey: String? = null,

    @field:SerializedName("DefaultSMS")
    var defaultSMS: String? = null,

    @field:SerializedName("RegistrationNumber")
    var registrationNumber: String? = "",

    @field:SerializedName("SMS_SERVICE")
    var SMS_SERVICE: Boolean = false,

    @field:SerializedName("WHATSAPP_SERVICE")
    var WHATSAPP_SERVICE: Boolean = false,

    @field:SerializedName("UNIQUE_MESSAGE_SETTING")
    var UNIQUE_MESSAGE_SETTING: Boolean = false,

    @field:SerializedName("REPEAT_MODE")
    var REPEAT_MODE: Boolean = false,

    @field:SerializedName("WEB_URL")
    var WEB_URL: Boolean = false,

    /**
     * 0 - sim 1
     * 1 - sim 2
     */
    @field:SerializedName("SLOT_ID")
    var SLOT_ID: Int = 0,

    ) : Serializable
