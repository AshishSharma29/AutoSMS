package com.auto_reply.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.auto_reply.MainActivity
import com.auto_reply.R
import com.auto_reply.base.BaseFragment
import com.auto_reply.databinding.FragmentHomeBinding
import com.auto_reply.model.LoginResponseModel
import com.auto_reply.util.Prefs
import com.auto_reply.web_view.WebViewActivity
import com.synnapps.carouselview.CarouselView
import com.synnapps.carouselview.ImageListener


class HomeFragment : BaseFragment() {

    private lateinit var homeViewModel: HomeViewModel
    lateinit var mBinding: FragmentHomeBinding
    lateinit var loginResponseModel: LoginResponseModel
    var carouselView: CarouselView? = null
    var sampleImages = intArrayOf(
        R.drawable.banner1,
        R.drawable.banner2,
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        mBinding = FragmentHomeBinding.inflate(inflater, container, false)
        mBinding.cdSmsSetting.setOnClickListener {
            (activity as MainActivity).navController.navigate(R.id.nav_sms_settings)
        }
        loginResponseModel = Prefs.getObjectFromPref(mActivity, LoginResponseModel::class.java.name)
        mBinding.tvRegistrationNumber.text =
            "Registration number " + loginResponseModel.responsePacket.registrationNumber
        mBinding.cdBulkSms.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.nav_gallery)
        }
        mBinding.cdUpdateProfile.setOnClickListener {
            val intent = Intent(mActivity, WebViewActivity::class.java)
            intent.putExtra(
                "url",
                "https://bgraphy.in/Card/LoginUser?UserAuthKey=" + loginResponseModel.responsePacket.loginAuthKey
            )
            startActivity(intent)
        }
        mBinding.cdWhatsappSend.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.nav_whatsapp)
        }
        mBinding.carouselView.setPageCount(sampleImages.size);
        mBinding.carouselView.setImageListener(imageListener);
        val optName: String? = mActivity.getOutput(mActivity, "getCarrierName", 0)
        return mBinding.root
    }

    var imageListener =
        ImageListener { position, imageView -> imageView.setImageResource(sampleImages[position]) }

}
