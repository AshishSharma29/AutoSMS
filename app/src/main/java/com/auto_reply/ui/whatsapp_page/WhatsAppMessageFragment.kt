package com.auto_reply.ui.whatsapp_page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.auto_reply.base.BaseFragment
import com.auto_reply.databinding.FragmentWhatsappMessageBinding

class WhatsAppMessageFragment : BaseFragment() {
    lateinit var mBinding: FragmentWhatsappMessageBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentWhatsappMessageBinding.inflate(inflater, container, false)
        mBinding.btnSend.setOnClickListener {
            mActivity.sendDirectSmsToWhatsApp(mBinding.edMobileNumber.text.toString().trim())
        }
        return mBinding.root
    }
}