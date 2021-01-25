package com.auto_reply.logout

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.auto_reply.base.BaseFragment
import com.auto_reply.databinding.FragmentLogoutBinding
import com.auto_reply.model.CheckLicenceResponseModel
import com.auto_reply.model.LoginResponseModel
import com.auto_reply.model.UpdatedMessageModel
import com.auto_reply.ui.login.LoginActivity
import com.auto_reply.util.Prefs

class LogoutFragment : BaseFragment() {
    lateinit var mBinding: FragmentLogoutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentLogoutBinding.inflate(layoutInflater, container, false)
        mBinding.btnLogout.setOnClickListener {
            Prefs.putObjectIntoPref(mActivity, null, LoginResponseModel::class.java.name)
            Prefs.putObjectIntoPref(mActivity, null, UpdatedMessageModel::class.java.name)
            Prefs.putObjectIntoPref(
                mActivity,
                null,
                CheckLicenceResponseModel::class.java.simpleName
            )
            startActivity(Intent(mActivity, LoginActivity::class.java))
            mActivity.finish()
        }
        return mBinding.root
    }

}