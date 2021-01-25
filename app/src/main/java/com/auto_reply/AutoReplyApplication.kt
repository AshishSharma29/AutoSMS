package com.auto_reply

import android.app.Application
import android.content.Context
import android.provider.Settings
import android.telephony.TelephonyManager
import androidx.appcompat.app.AppCompatActivity
import com.auto_reply.model.LoginResponseModel
import com.auto_reply.util.Prefs
import com.auto_reply.webservice.AuthHeaders
import com.google.gson.Gson
import java.util.*


open class AutoReplyApplication : Application() {

    init {
        instance = this
    }

    companion object {
        private var instance: AutoReplyApplication? = null
        var mCurrentActivity: AppCompatActivity? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }

        fun getInstance(): AutoReplyApplication {
            return instance!!
        }

        fun getCurrentActivity(): AppCompatActivity? {
            return mCurrentActivity
        }

        fun setCurrentActivity(mCurrentActivity: AppCompatActivity?) {
            this.mCurrentActivity = mCurrentActivity
        }
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun getApplicationContext(): Context {
        return super.getApplicationContext()
    }

    var accessToken: String = ""
    private var mAuthHeaders: AuthHeaders? = null

    open fun getAuthHeaders(): AuthHeaders {
        val loginResponseModel =
            Prefs.getObjectFromPref(
                applicationContext,
                LoginResponseModel::class.java.name
            ) as LoginResponseModel?
        val deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        mAuthHeaders = if (mAuthHeaders != null) mAuthHeaders else AuthHeaders()
        mAuthHeaders!!.header = HashMap()
        mAuthHeaders!!.header?.put("AppVersion", BuildConfig.VERSION_CODE.toString())
        mAuthHeaders!!.header?.put("PackageName", BuildConfig.APPLICATION_ID)
        mAuthHeaders!!.header?.put("DeviceId", deviceID)
        mAuthHeaders!!.header?.put("Latitude", "")
        mAuthHeaders!!.header?.put("Longitude", "")
        mAuthHeaders!!.header?.put("SimSerialNumber", "")
        if (loginResponseModel != null) {

        }
        return mAuthHeaders as AuthHeaders
    }

    fun setmAuthHeaders(mAuthHeaders: AuthHeaders) {
        this.mAuthHeaders = mAuthHeaders
    }
}
