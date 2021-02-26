package com.auto_reply.ui.sms_setting

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.RadioGroup
import androidx.core.app.ActivityCompat
import com.auto_reply.MainActivity
import com.auto_reply.R
import com.auto_reply.base.BaseFragment
import com.auto_reply.databinding.FragmentSmsSettingBinding
import com.auto_reply.model.LoginResponseModel
import com.auto_reply.model.UpdatedMessageModel
import com.auto_reply.template.TemplateActivity
import com.auto_reply.util.Prefs


class SMSSettingsFragment : BaseFragment() {
    lateinit var mBinding: FragmentSmsSettingBinding
    private var updatedMessageModel: UpdatedMessageModel? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentSmsSettingBinding.inflate(inflater, container, false)
        val loginResponseModel =
            Prefs.getObjectFromPref(
                mActivity,
                LoginResponseModel::class.java.name
            ) as LoginResponseModel?
        getSimName()
        updatedMessageModel = Prefs.getObjectFromPref(
            mActivity,
            UpdatedMessageModel::class.java.simpleName
        ) as UpdatedMessageModel?
        if (updatedMessageModel == null) {
            updatedMessageModel = UpdatedMessageModel(
                loginResponseModel?.responsePacket?.defaultSMS!!,
                loginResponseModel.responsePacket.defaultSMS!!,
                loginResponseModel.responsePacket.defaultSMS!!,
                loginResponseModel.responsePacket.userWebUrl
            )
        }
        mBinding.loginResponseModel = loginResponseModel
        mBinding.updatedMessageModel = updatedMessageModel
        mBinding.smsSettingsFragment = this
        mBinding.btnSaveClick.setOnClickListener {
            Prefs.putObjectIntoPref(
                mActivity,
                mBinding.updatedMessageModel,
                UpdatedMessageModel::class.java.simpleName
            )
            mActivity.showToast("Settings updated")
        }
        mBinding.cbWebsite.setOnCheckedChangeListener { _, checked ->
            loginResponseModel?.responsePacket?.WEB_URL = checked
            Prefs.putObjectIntoPref(
                mActivity,
                loginResponseModel,
                LoginResponseModel::class.java.name
            )
        }

        mBinding.switchSms.setOnCheckedChangeListener { _, p1 ->
            loginResponseModel?.responsePacket?.SMS_SERVICE = p1
            Prefs.putObjectIntoPref(
                mActivity,
                loginResponseModel,
                LoginResponseModel::class.java.name
            )
            if (p1) (mActivity as MainActivity).startService()
            else (mActivity as MainActivity).startService()
        }
        mBinding.switchRepeatMode.setOnCheckedChangeListener { _, p1 ->
            loginResponseModel?.responsePacket?.REPEAT_MODE = p1
            Prefs.putObjectIntoPref(
                mActivity,
                loginResponseModel,
                LoginResponseModel::class.java.name
            )
        }
        mBinding.switchUniqueMessageSetting.setOnCheckedChangeListener { _, p1 ->
            loginResponseModel?.responsePacket?.UNIQUE_MESSAGE_SETTING = p1
            Prefs.putObjectIntoPref(
                mActivity,
                loginResponseModel,
                LoginResponseModel::class.java.name
            )
        }
        mBinding.switchWhatsapp.setOnCheckedChangeListener { _, p1 ->
            loginResponseModel?.responsePacket?.WHATSAPP_SERVICE = p1
            Prefs.putObjectIntoPref(
                mActivity,
                loginResponseModel,
                LoginResponseModel::class.java.name
            )
        }
        mBinding.rgSim.setOnCheckedChangeListener { radioGroup, _ ->
            when (radioGroup?.checkedRadioButtonId) {
                R.id.rb_sim_one -> {
                    loginResponseModel?.responsePacket?.SLOT_ID = 0
                }
                R.id.rb_sim_two -> {
                    loginResponseModel?.responsePacket?.SLOT_ID = 1
                }
            }
            Prefs.putObjectIntoPref(
                mActivity,
                loginResponseModel,
                LoginResponseModel::class.java.name
            )
        }
        if (loginResponseModel?.responsePacket?.SLOT_ID == 0)
            mBinding.rbSimOne.isChecked = true
        else mBinding.rbSimTwo.isChecked = true
        return mBinding.root
    }

    fun incomingMessageClick(view: View) {
        startActivityForResult(Intent(mActivity, TemplateActivity::class.java), 101)
    }

    fun outgoingMessageClick(view: View) {
        startActivityForResult(Intent(mActivity, TemplateActivity::class.java), 102)
    }

    fun missedMessageClick(view: View) {
        startActivityForResult(Intent(mActivity, TemplateActivity::class.java), 103)
    }

    fun getSimName() {
        if (Build.VERSION.SDK_INT > 22) {
            //for dual sim mobile
            val localSubscriptionManager = SubscriptionManager.from(mActivity)
            if (ActivityCompat.checkSelfPermission(
                    mActivity,
                    Manifest.permission.READ_PHONE_STATE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            if (localSubscriptionManager.activeSubscriptionInfoCount > 1) {
                //if there are two sims in dual sim mobile
                val localList: List<*> = localSubscriptionManager.activeSubscriptionInfoList
                val simInfo = localList[0] as SubscriptionInfo
                val simInfo1 = localList[1] as SubscriptionInfo
                val sim1 = simInfo.displayName.toString()
                val sim2 = simInfo1.displayName.toString()
                mBinding.rbSimOne.text = sim1
                mBinding.rbSimTwo.text = sim2
                Log.d("sim name", sim1 + " " + sim2)
            } else {
                //if there is 1 sim in dual sim mobile
                val tManager = mActivity
                    .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                val sim1 = tManager.networkOperatorName
                mBinding.rbSimOne.text = sim1
                mBinding.rbSimOne.isChecked = true
                mBinding.rbSimTwo.visibility = View.GONE
                Log.d("sim name", sim1)
            }
        } else {
            //below android version 22
            val tManager = mActivity
                .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val sim1 = tManager.networkOperatorName
            mBinding.rbSimOne.text = sim1
            mBinding.rbSimOne.isChecked = true
            mBinding.rbSimTwo.visibility = View.GONE

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null)
            return
        when (requestCode) {
            101 -> updatedMessageModel?.incomingMessage = data.data.toString()
            102 -> updatedMessageModel?.outgoingMessage = data.data.toString()
            103 -> updatedMessageModel?.missedMessage = data.data.toString()
        }
        mBinding.updatedMessageModel = updatedMessageModel
    }
}