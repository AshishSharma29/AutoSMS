package com.auto_reply

import android.Manifest
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.auto_reply.base.BaseActivity
import com.auto_reply.model.CheckLicenceResponseModel
import com.auto_reply.model.LoginResponseModel
import com.auto_reply.util.ForegroundService
import com.auto_reply.util.Prefs
import com.auto_reply.webservice.ApiCallbacks
import com.auto_reply.webservice.WebApiUrls
import com.auto_reply.webservice.WebServiceCaller
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method


class MainActivity : BaseActivity() {
    lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkLicence()
        checkPermissions()
        getSimName()
//        CallReceiver.sendSMS(
//            "121",
//            "new message new message new message new message new message new message new message new message new message new message new message new message new message",
//            this,
//            0
//        )
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_sms_settings,
                R.id.nav_gallery,
                R.id.nav_whatsapp, R.id.nav_logout
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    fun startService() {
        val serviceIntent = Intent(this, ForegroundService::class.java)
        serviceIntent.putExtra("inputExtra", "using call states in background")
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    fun stopService() {
        val serviceIntent = Intent(this, ForegroundService::class.java)
        stopService(serviceIntent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_whatsapp -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.nav_WhatsAppMessageFragment)
            }
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun checkLicence() {
        val loginResponseModel =
            Prefs.getObjectFromPref(
                applicationContext,
                LoginResponseModel::class.java.name
            ) as LoginResponseModel?
        val jsonObject = JsonObject()
        jsonObject.addProperty("LoginUserId", loginResponseModel?.responsePacket?.loginUserId)
        jsonObject.addProperty(
            "LoginAuthKey",
            loginResponseModel?.responsePacket?.loginAuthKey
        )
        showProgress()
        WebServiceCaller.callWebApi(jsonObject, WebApiUrls.CHECK_LICENCE, object : ApiCallbacks {
            override fun onSuccess(jsonObject: JsonObject, anEnum: String) {
                val checkLicenceResponseModel =
                    Gson().fromJson(jsonObject, CheckLicenceResponseModel::class.java);
                hideProgress()
                Prefs.putObjectIntoPref(
                    applicationContext,
                    checkLicenceResponseModel,
                    CheckLicenceResponseModel::class.java.simpleName
                )
                if (checkLicenceResponseModel.responsePacket?.status != 200) {
                    showToast(checkLicenceResponseModel?.responsePacket?.message!!)
                }
                checkVersion(Gson().fromJson(jsonObject, CheckLicenceResponseModel::class.java))
            }

            override fun onError(jsonObject: String, anEnum: String) {
                hideProgress()
                Prefs.putObjectIntoPref(
                    applicationContext,
                    Gson().fromJson(jsonObject, CheckLicenceResponseModel::class.java),
                    CheckLicenceResponseModel::class.java.simpleName
                )
                showToast(WebServiceCaller.getResponseMessage(jsonObject))
                checkVersion(Gson().fromJson(jsonObject, CheckLicenceResponseModel::class.java))
            }

            override fun networkError(message: String?) {
                hideProgress()
            }

        })
    }

    private fun checkVersion(fromJson: CheckLicenceResponseModel?) {
        if (fromJson?.responsePacket?.ApkNew.equals("YES")) {
            val alertDialog: AlertDialog = AlertDialog.Builder(this@MainActivity).create()
            alertDialog.setTitle(R.string.app_name)
            alertDialog.setMessage("Please update application")
            alertDialog.setButton(
                AlertDialog.BUTTON_NEUTRAL, "OK",
                DialogInterface.OnClickListener { dialog, which ->
                    run {
                        val appPackageName = packageName

                        try {
                            startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id=$appPackageName")
                                )
                            )
                        } catch (anfe: ActivityNotFoundException) {
                            startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                                )
                            )
                        }
                    }
                })
            alertDialog.show()
        }
    }

    fun checkPermissions() {
        var permissions: ArrayList<String> = ArrayList()
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(Manifest.permission.READ_PHONE_STATE)
        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.FOREGROUND_SERVICE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                permissions.add(Manifest.permission.FOREGROUND_SERVICE)
            }
        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(Manifest.permission.READ_CONTACTS)
        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(Manifest.permission.WRITE_CONTACTS)
        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.PROCESS_OUTGOING_CALLS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(Manifest.permission.PROCESS_OUTGOING_CALLS)
        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(Manifest.permission.SEND_SMS)
        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.FOREGROUND_SERVICE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                permissions.add(Manifest.permission.FOREGROUND_SERVICE)
            }
        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CALL_LOG
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(Manifest.permission.READ_CALL_LOG)
        }
        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.PROCESS_OUTGOING_CALLS,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS,
                    Manifest.permission.FOREGROUND_SERVICE
                ),
                101
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun getSimName() {
        if (Build.VERSION.SDK_INT > 22) {
            //for dual sim mobile
            val localSubscriptionManager = SubscriptionManager.from(this)
            if (ActivityCompat.checkSelfPermission(
                    this,
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
                Log.d("sim name", sim1 + " " + sim2)
            } else {
                //if there is 1 sim in dual sim mobile
                val tManager = baseContext
                    .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                val sim1 = tManager.networkOperatorName
                Log.d("sim name", sim1)
            }
        } else {
            //below android version 22
            val tManager = baseContext
                .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val sim1 = tManager.networkOperatorName
        }
    }

    fun sendSMS(
        ctx: Context,
        simID: Int,
        toNum: String?,
        centerNum: String?,
        smsText: String?,
        sentIntent: PendingIntent?,
        deliveryIntent: PendingIntent?
    ): Boolean {
        val name: String
        try {
            name = if (simID == 0) {
                "isms"
                // for model : "Philips T939" name = "isms0"
            } else if (simID == 1) {
                "isms2"
            } else {
                throw Exception("can not get service which for sim '$simID', only 0,1 accepted as values")
            }
            var method: Method = Class.forName("android.os.ServiceManager").getDeclaredMethod(
                "getService",
                String::class.java
            )
            method.setAccessible(true)
            val param: Any = method.invoke(null, name)
            method = Class.forName("com.android.internal.telephony.ISms\$Stub").getDeclaredMethod(
                "asInterface",
                IBinder::class.java
            )
            method.setAccessible(true)
            val stubObj: Any = method.invoke(null, param)
            if (Build.VERSION.SDK_INT < 18) {
                method = stubObj.javaClass.getMethod(
                    "sendText",
                    String::class.java,
                    String::class.java,
                    String::class.java,
                    PendingIntent::class.java,
                    PendingIntent::class.java
                )
                method.invoke(stubObj, toNum, centerNum, smsText, sentIntent, deliveryIntent)
            } else {
                method = stubObj.javaClass.getMethod(
                    "sendText",
                    String::class.java,
                    String::class.java,
                    String::class.java,
                    String::class.java,
                    PendingIntent::class.java,
                    PendingIntent::class.java
                )
                method.invoke(
                    stubObj,
                    ctx.packageName,
                    toNum,
                    centerNum,
                    smsText,
                    sentIntent,
                    deliveryIntent
                )
            }
            return true
        } catch (e: ClassNotFoundException) {
            Log.e("apipas", "ClassNotFoundException:" + e.message)
        } catch (e: NoSuchMethodException) {
            Log.e("apipas", "NoSuchMethodException:" + e.message)
        } catch (e: InvocationTargetException) {
            Log.e("apipas", "InvocationTargetException:" + e.message)
        } catch (e: IllegalAccessException) {
            Log.e("apipas", "IllegalAccessException:" + e.message)
        } catch (e: Exception) {
            Log.e("apipas", "Exception:" + e.message)
        }
        return false
    }
}