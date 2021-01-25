package com.auto_reply.ui.whatsapp_page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.auto_reply.base.BaseFragment
import com.auto_reply.databinding.FragmentWhatsappBinding

class WhatsAppFragment : BaseFragment() {
    lateinit var mBinding: FragmentWhatsappBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentWhatsappBinding.inflate(inflater, container, false)
        mBinding.btnSend.setOnClickListener {
            mActivity.sendSmsToWhatsApp(mBinding.edMobileNumber.text.toString().trim())
        }
        return mBinding.root
    }
}
