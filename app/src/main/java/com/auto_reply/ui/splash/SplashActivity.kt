package com.auto_reply.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.auto_reply.MainActivity
import com.auto_reply.R
import com.auto_reply.base.BaseActivity
import com.auto_reply.model.LoginResponseModel
import com.auto_reply.ui.login.LoginActivity
import com.auto_reply.ui.on_boarding.OnBoardingActivity
import com.auto_reply.util.Prefs

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed(Runnable {
            val loginResponseModel =
                Prefs.getObjectFromPref(
                    this,
                    LoginResponseModel::class.java.name
                ) as LoginResponseModel?
            if (loginResponseModel == null)
                startActivity(Intent(this, OnBoardingActivity::class.java))
            else startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 3000);
    }
}