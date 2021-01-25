package com.auto_reply.ui.on_boarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.auto_reply.R
import com.auto_reply.base.BaseActivity
import com.auto_reply.databinding.ActivityOnBoardingBinding
import com.auto_reply.ui.login.LoginActivity
import com.auto_reply.web_view.WebViewActivity

class OnBoardingActivity : BaseActivity() {

    lateinit var mBinding: ActivityOnBoardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_on_boarding)
    }

    fun registerOnClick(view: View) {
        val intent = Intent(this, WebViewActivity::class.java)
        intent.putExtra(
            "url",
            "https://bgraphy.in/Card/UserRegister"
        )
        startActivity(intent)
    }

    fun loginOnClick(view: View) {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    fun howWeWorkOnClick(view: View) {
        val intent = Intent(this, WebViewActivity::class.java)
        intent.putExtra(
            "url",
            "https://bgraphy.in/"
        )
        startActivity(intent)
    }
}