package com.auto_reply.ui.login

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.auto_reply.MainActivity
import com.auto_reply.R
import com.auto_reply.base.BaseActivity
import com.auto_reply.databinding.ActivityLoginBinding
import com.auto_reply.model.LoginResponseModel
import com.auto_reply.model.UpdatedMessageModel
import com.auto_reply.util.Prefs
import com.auto_reply.webservice.ApiCallbacks
import com.auto_reply.webservice.WebApiUrls
import com.auto_reply.webservice.WebServiceCaller
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.judemanutd.autostarter.AutoStartPermissionHelper

class LoginActivity : BaseActivity() {

    lateinit var mBinding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

     //   checkPermissions();
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        mBinding.btnLogin.setOnClickListener {
            if (mBinding.nameEditText.text?.trim()?.isEmpty()!!) {
                showToast(getString(R.string.email_validation))
            } else if (mBinding.passwordEditText.text?.trim()?.isEmpty()!!) {
                showToast(getString(R.string.password_validation))
            } else {
                val jsonObject = JsonObject()
                jsonObject.addProperty("UserName", mBinding.nameEditText.text?.trim().toString())
                jsonObject.addProperty(
                    "Password",
                    mBinding.passwordEditText.text?.trim().toString()
                )
                showProgress()
                WebServiceCaller.callWebApi(jsonObject, WebApiUrls.LOGIN, object : ApiCallbacks {
                    override fun onSuccess(jsonObject: JsonObject, anEnum: String) {
                        val loginResponseModel =
                            Gson().fromJson(jsonObject, LoginResponseModel::class.java)
                        Prefs.putObjectIntoPref(
                            applicationContext,
                            loginResponseModel,
                            LoginResponseModel::class.java.name
                        )
                        val updatedMessageModel = UpdatedMessageModel(
                            loginResponseModel?.responsePacket?.defaultSMS!!,
                            loginResponseModel.responsePacket.defaultSMS!!,
                            loginResponseModel.responsePacket.defaultSMS!!,
                            loginResponseModel.responsePacket.userWebUrl
                        )
                        Prefs.putObjectIntoPref(
                            applicationContext,
                            updatedMessageModel,
                            UpdatedMessageModel::class.java.simpleName
                        )
                        hideProgress()
                        startActivity(Intent(applicationContext, MainActivity::class.java))
                        finish()
                    }

                    override fun onError(jsonObject: String, anEnum: String) {
                        hideProgress()
                        showToast(WebServiceCaller.getResponseMessage(jsonObject))
                    }

                    override fun networkError(message: String?) {
                        hideProgress()
                        showToast(message!!)
                    }

                })


            }
        }
    }

    fun checkPermissions() {
        var permissions: ArrayList<String> = ArrayList()
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(Manifest.permission.READ_PHONE_STATE)
            setautostart()
        }
        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_PHONE_STATE
                ),
                101
            )
        }
    }

    fun setautostart() {
        AutoStartPermissionHelper.getInstance().getAutoStartPermission(this);
    }
}