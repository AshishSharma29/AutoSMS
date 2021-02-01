package com.auto_reply.base

import android.R.id
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.auto_reply.BuildConfig
import com.auto_reply.R
import java.io.File
import java.lang.reflect.Method


open class BaseActivity : AppCompatActivity() {
    lateinit var progressBar: View
    fun getOperatorName(): String? {
        var tel: TelephonyManager
        tel = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return tel.simOperatorName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    /**
     * method use to initialize progress bar
     */
    fun init() {
        // Inflate your custom layout
        progressBar = layoutInflater.inflate(
            R.layout.progress_dialog_layout, null
        )
        progressBar.setOnClickListener(View.OnClickListener { })
        val v = this.findViewById<View>(android.R.id.content).rootView
        val viewGroup = v as ViewGroup
        viewGroup.addView(progressBar)
    }


    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * method use to show progress bar
     */
    fun showProgress() {
        runOnUiThread(object : Runnable {
            override fun run() {
                progressBar.visibility = View.VISIBLE
            }
        })
    }

    /**
     * method use to hide progress bar
     */
    fun hideProgress() {
        try {
            runOnUiThread(object : Runnable {
                override fun run() {
                    progressBar.visibility = View.GONE
                }
            })
        } catch (e: Exception) {
        }
    }

    fun getOutput(context: Context, methodName: String, slotId: Int): String? {
        val telephony = context.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        val telephonyClass: Class<*>
        var reflectionMethod: String? = null
        var output: String? = null
        try {
            telephonyClass = Class.forName(telephony.javaClass.name)
            for (method in telephonyClass.methods) {
                val name = method.name
                if (name.contains(methodName)) {
                    val params = method.parameterTypes
                    if (params.size == 1 && params[0].name == "int") {
                        reflectionMethod = name
                    }
                }
            }
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
        if (reflectionMethod != null) {
            try {
                output = getOpByReflection(telephony, reflectionMethod, slotId, false)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        return output
    }

    fun getOpByReflection(
        telephony: TelephonyManager,
        predictedMethodName: String,
        slotID: Int,
        isPrivate: Boolean
    ): String? {

        //Log.i("Reflection", "Method: " + predictedMethodName+" "+slotID);
        var result: String? = null
        try {
            val telephonyClass = Class.forName(telephony.javaClass.name)
            val parameter = arrayOfNulls<Class<*>?>(1)
            parameter[0] = Int::class.javaPrimitiveType
            val getSimID: Method?
            getSimID = if (slotID != -1) {
                if (isPrivate) {
                    telephonyClass.getDeclaredMethod(predictedMethodName, *parameter)
                } else {
                    telephonyClass.getMethod(predictedMethodName, *parameter)
                }
            } else {
                if (isPrivate) {
                    telephonyClass.getDeclaredMethod(predictedMethodName)
                } else {
                    telephonyClass.getMethod(predictedMethodName)
                }
            }
            val ob_phone: Any
            val obParameter = arrayOfNulls<Any>(1)
            obParameter[0] = slotID
            if (getSimID != null) {
                ob_phone = if (slotID != -1) {
                    getSimID.invoke(telephony, obParameter)
                } else {
                    getSimID.invoke(telephony)
                }
                if (ob_phone != null) {
                    result = ob_phone.toString()
                }
            }
        } catch (e: java.lang.Exception) {
            //e.printStackTrace();
            return null
        }
        //Log.i("Reflection", "Result: " + result);
        return result
    }

    fun sendSmsToWhatsApp(number: String, file: File?, message: String) {
        var toNumber = "+91 $number"
        toNumber = toNumber.replace("+", "").replace(" ", "")

        val sendIntent = Intent("android.intent.action.MAIN")
        sendIntent.putExtra(
            Intent.EXTRA_STREAM,
            FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file!!)
        )
        sendIntent.putExtra("jid", "$toNumber@s.whatsapp.net")
        sendIntent.putExtra(Intent.EXTRA_TEXT, message)
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.setPackage("com.whatsapp")
        sendIntent.type = "image/png"
        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(sendIntent)

    }

    fun sendDirectSmsToWhatsApp(number: String) {
        val mobileNumber: String = number
        val installed = appInstalledOrNot("com.whatsapp")
        if (installed) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data =
                Uri.parse("http://api.whatsapp.com/send?phone=+91$mobileNumber")
            startActivity(intent)
        } else {
            Toast.makeText(
                this,
                "Whats app not installed on your device",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun showWarningDialog(context: Context, message: String) {
        AlertDialog.Builder(context)
            .setMessage(message)
            .setNegativeButton("cancel",
                DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
            .setCancelable(true)
            .create().show()
    }

    //Create method appInstalledOrNot
    fun appInstalledOrNot(url: String): Boolean {
        val packageManager = packageManager
        val app_installed: Boolean
        app_installed = try {
            packageManager.getPackageInfo(url, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
        return app_installed
    }
}