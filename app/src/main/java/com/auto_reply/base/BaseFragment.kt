package com.auto_reply.base

import android.content.Context
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {

    lateinit var mActivity: BaseActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = activity as BaseActivity;
    }
}