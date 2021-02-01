package com.auto_reply.ui.whatsapp_page

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.auto_reply.base.BaseFragment
import com.auto_reply.databinding.FragmentWhatsappBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import java.io.File

class WhatsAppFragment : BaseFragment() {
    lateinit var mBinding: FragmentWhatsappBinding
    var file: File? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentWhatsappBinding.inflate(inflater, container, false)
        mBinding.btnSend.setOnClickListener {
            mActivity.sendSmsToWhatsApp(
                mBinding.edMobileNumber.text.toString().trim(),
                file,
                mBinding.edMessage.text.trim().toString()
            )
        }
        mBinding.btnAddImage.setOnClickListener {
            ImagePicker.with(this)
                .compress(1024)         //Final image size will be less than 1 MB(Optional)
                .maxResultSize(
                    1080,
                    1080
                )  //Final image resolution will be less than 1080 x 1080(Optional)
                .start { resultCode, data ->
                    if (resultCode == Activity.RESULT_OK) {
                        val fileUri = data?.data
                        mBinding.imvImage.setImageURI(fileUri)
                        file = ImagePicker.getFile(data)

                        //You can also get File Path from intent
                        val filePath: String? = ImagePicker.getFilePath(data)
                    } else if (resultCode == ImagePicker.RESULT_ERROR) {
                        Toast.makeText(context, ImagePicker.getError(data), Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(context, "Task Cancelled", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        return mBinding.root
    }
}
