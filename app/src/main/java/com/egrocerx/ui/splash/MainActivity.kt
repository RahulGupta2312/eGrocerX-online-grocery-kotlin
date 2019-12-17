package com.egrocerx.ui.splash

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.crashlytics.android.Crashlytics
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.JsonObject
import com.egrocerx.R
import com.egrocerx.core.MyApplication
import com.egrocerx.network.ApiManager
import com.egrocerx.network.ApiMode
import com.egrocerx.network.ApiResponse
import com.egrocerx.ui.dashboard.DashboardActivity
import com.egrocerx.ui.login.LoginActivity
import com.egrocerx.util.AppPreference
import com.egrocerx.util.AppUtils
import com.egrocerx.util.launchActivity

class MainActivity : AppCompatActivity(), ApiResponse {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MyApplication.instance.setContext(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorBlack)
        }

        generateFcmToken()
        val handler = Handler()

        val runnable = Runnable {
            if (AppPreference.getInstance().loggedIn) {
                requestProfileApi()
            } else {
                launchActivity<LoginActivity> { }
                finish()
            }
        }

        handler.postDelayed(runnable, 2000)


    }

    private fun requestProfileApi() {
        val map = HashMap<String, Any>()
        map["id"] = AppPreference.getInstance().userData.customer_id
        ApiManager.getInstance().requestApi(
            ApiMode.USER_PROFILE, map, false,
            this, "POST"
        )
    }

    private fun generateFcmToken() {
        if (!TextUtils.isEmpty(AppPreference.getInstance().fcmToken))
            return

//        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
//            if (it.isSuccessful && it.result != null) {
//                AppPreference.getInstance().saveFcmToken(it.result!!.token)
//            }
//        }
    }

    override fun onSuccess(jsonObject: JsonObject?, mode: ApiMode?) {

        when (mode) {
            ApiMode.USER_PROFILE -> {

                AppPreference.getInstance().saveUserData(
                    jsonObject?.getAsJsonObject("data").toString()
                )
                launchActivity<DashboardActivity> {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
                finish()
            }
        }
    }

    override fun onFailure(errorObject: JsonObject?, mode: ApiMode?) {
        AppUtils.shortToast("Some error occurred, Please restart the app.")
    }

    override fun onException(e: Exception?, mode: ApiMode?) {
        Crashlytics.logException(e)
        AppUtils.shortToast("Some error occurred, Please restart the app.")
    }
}
