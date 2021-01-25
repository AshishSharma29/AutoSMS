package com.auto_reply.template

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.auto_reply.R
import com.auto_reply.base.BaseFragment
import com.auto_reply.databinding.EnglishTemplateFragmentBinding


class FragmentEnglishTemplate : BaseFragment() {
    lateinit var mBinding: EnglishTemplateFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = EnglishTemplateFragmentBinding.inflate(inflater, container, false)
        val adapter = ArrayAdapter(
            mActivity,
            android.R.layout.simple_list_item_1,
            resources.getStringArray(R.array.english_template)
        )

        mBinding.listView.adapter = adapter
        mBinding.listView.setOnItemClickListener { parent, view, position, id ->
            setResult(position)
        }
        return mBinding.root
    }

    fun setResult(position: Int) {
        val data = Intent()
        val text: String = resources.getStringArray(R.array.english_template)[position]
        data.setData(Uri.parse(text))
        mActivity.setResult(RESULT_OK, data)
        mActivity.finish()
    }
}