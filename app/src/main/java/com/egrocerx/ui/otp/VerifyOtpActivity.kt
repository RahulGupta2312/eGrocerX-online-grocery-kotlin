package com.egrocerx.ui.otp


import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.egrocerx.R
import com.egrocerx.base.BaseActivity
import com.egrocerx.network.ApiManager
import com.egrocerx.network.ApiMode
import com.egrocerx.network.ApiResponse
import com.egrocerx.reciever.SmsBroadcastReceiver
import com.egrocerx.ui.dashboard.DashboardActivity
import com.egrocerx.ui.login.LoginActivity
import com.egrocerx.util.AppPreference
import com.egrocerx.util.AppUtils
import com.egrocerx.util.launchActivity
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_verify_otp.*


class VerifyOtpActivity : BaseActivity(), SmsBroadcastReceiver.OnOtpReceivedCallback, ApiResponse {

    companion object {
        const val FORGOT_PASSWORD = "forgot_password"
        const val MOBILE_VERIFICATION = "mobile_verification"
    }


    private var otpEntered = ""

    private var mobile = ""
    private var customerId = ""

    private var generatedOtp = 0

    private lateinit var smsListener: SmsBroadcastReceiver

    private var mode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_otp)
        setupOtpView()

        getIntentArguments()
    }

    private fun getIntentArguments() {

        mode = intent?.getStringExtra("mode")!!
        mobile = intent?.getStringExtra("mobile")!!

        if (intent!!.hasExtra("customer_id"))
            customerId = intent?.getStringExtra("customer_id")!!

        startSmsListener()


    }

    private fun startSmsListener() {

        smsListener = SmsBroadcastReceiver()
        smsListener.setListener(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
        this.registerReceiver(smsListener, intentFilter)

        val client = SmsRetriever.getClient(this /* context */)
        val task = client.startSmsRetriever()
        task.addOnSuccessListener {

//            requestVerificationOtp()
        }

        task.addOnFailureListener {
            it.printStackTrace()
        }

    }

//    private fun requestVerificationOtp() {
//        generatedOtp = Random().nextInt(999999) + 100000
//
//        val progressDialog = ProgressDialog(this)
//        progressDialog.setCancelable(false)
//        progressDialog.setCanceledOnTouchOutside(false)
//        progressDialog.setMessage("Sending OTP...")
//        progressDialog.show()
//        AndroidNetworking.post(
//            "http://rsms.antikinfotech.com/api/mt/SendSMS?APIKey=" +
//                    "&senderid=&channel=2&DCS=0&flashsms=0"
//        )
//            .addQueryParameter(
//                "text",
//                ""
//            )
//            .addQueryParameter("number", mobile)
//            .build()
//            .getAsJSONObject(object : JSONObjectRequestListener {
//                override fun onResponse(response: JSONObject?) {
//                    progressDialog.dismiss()
//                    if (response?.getString("ErrorMessage") == "Success") {
//                        AppUtils.shortToast("Otp sent successfully")
//                    } else {
//                        AppUtils.shortToast(response?.getString("ErrorMessage"))
//                    }
//                }
//
//                override fun onError(anError: ANError?) {
//                    progressDialog.dismiss()
//                    AppUtils.showException()
//                }
//
//            })
//    }

    private fun setupOtpView() {
        otpView.setPinViewEventListener { pinview, _ ->
            otpEntered = pinview.value
            validateOtp()
        }
    }
//
//    fun onBackClick(view: View) {
//        super.onBackPressed()
//    }

    fun onVerifyClick(view: View) {

        if (TextUtils.isEmpty(otpEntered)) {
            AppUtils.shortToast("Enter a valid otp")
            return
        }

        validateOtp()

    }

    private fun validateOtp() {
        if (otpEntered == generatedOtp.toString()) {

            if (mode == MOBILE_VERIFICATION)
                requestUpdateMobileVerifiedApi()
            else
                requestUpdatePasswordApi()
        }
    }

    private fun requestUpdatePasswordApi() {

        // UPDATE Password to user's mobile number.

        val map = HashMap<String, Any>()
        map["mobile"] = mobile

        ApiManager.getInstance().requestApi(ApiMode.RESET_PASSWORD, map, true, this, "POST")

    }

    private fun requestUpdateMobileVerifiedApi() {
        val map = HashMap<String, Any>()
        map["mobile"] = mobile
        ApiManager.getInstance().requestApi(
            ApiMode.UPDATE_MOBILE_VERIFIED, map, true,
            this, "POST"
        )
    }

    override fun onOtpReceived(otp: String) {
        otpView.value = otp
    }

    override fun onSuccess(jsonObject: JsonObject?, mode: ApiMode?) {

        when (mode) {
            ApiMode.UPDATE_MOBILE_VERIFIED -> {
                // navigate to dashboard
                AppPreference.getInstance()
                    .saveUserData(jsonObject?.getAsJsonObject("data").toString())
                AppPreference.getInstance().saveLoggedIn(true)
                launchActivity<DashboardActivity> {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
                finishAffinity()
            }
            ApiMode.RESET_PASSWORD -> {
                AppUtils.shortToast(jsonObject?.get("message")?.asString)
                sendMessage(
                    "Your eCommerceX password has been set to your mobile number. Kindly login and change your password.",
                    mobile, this
                )
            }
            ApiMode.SEND_MSG -> {
                // finish activity after reset password msg has been sent
                launchActivity<LoginActivity> {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
                finish()

            }
        }

    }

    override fun onFailure(errorObject: JsonObject?, mode: ApiMode?) {
        AppUtils.shortToast(errorObject?.get("message")?.asString)
    }

    override fun onException(e: Exception?, mode: ApiMode?) {
        AppUtils.showException()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(smsListener)
    }

}
