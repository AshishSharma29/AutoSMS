package com.auto_reply.template

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.auto_reply.R
import com.auto_reply.base.BaseFragment
import com.auto_reply.databinding.HindiTemplateFragmentBinding

class FragmentHindiTemplate : BaseFragment() {
    lateinit var mBinding: HindiTemplateFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = HindiTemplateFragmentBinding.inflate(inflater, container, false)
        val adapter = ArrayAdapter(
            mActivity,
            android.R.layout.simple_list_item_1,
            resources.getStringArray(com.auto_reply.R.array.hindi_template)
        )
        mBinding.listView.adapter = adapter
        mBinding.listView.setOnItemClickListener { parent, view, position, id ->
            setResult(position)
        }
        return mBinding.root
    }


    fun setResult(position: Int) {
        val data = Intent()
        val text: String = resources.getStringArray(R.array.hindi_template)[position]
        data.setData(Uri.parse(text))
        mActivity.setResult(Activity.RESULT_OK, data)
        mActivity.finish()
    }
}