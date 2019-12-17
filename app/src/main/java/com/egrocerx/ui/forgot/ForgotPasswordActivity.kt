package com.egrocerx.ui.forgot

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.google.gson.JsonObject
import com.jakewharton.rxbinding2.widget.RxTextView
import com.egrocerx.R
import com.egrocerx.base.BaseActivity
import com.egrocerx.network.ApiManager
import com.egrocerx.network.ApiMode
import com.egrocerx.network.ApiResponse
import com.egrocerx.ui.otp.VerifyOtpActivity
import com.egrocerx.util.AppUtils
import com.egrocerx.util.launchActivity
import com.egrocerx.util.showKeyboard
import com.trello.rxlifecycle3.kotlin.bindToLifecycle
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_forgot_password.*
import java.util.concurrent.TimeUnit

class ForgotPasswordActivity : BaseActivity(), ApiResponse {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        setupObservables()
        setupButtonClick()
    }


    @SuppressLint("CheckResult")
    private fun setupObservables() {


        //phone number
        val phoneObservable: Observable<Boolean> = RxTextView.textChanges(forgotPhone)
            .skip(1)
            .debounce(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .map { t -> t.length == 10 }
            .distinctUntilChanged()

        phoneObservable
            .bindToLifecycle(forgotPhone)
            .subscribe { isValid ->
                if (isValid)
                    forgotPhoneTIL.isErrorEnabled = false
                else {
                    forgotPhoneTIL.isErrorEnabled = false
                    forgotPhoneTIL.error = "Phone number length must be 10"
                }
            }

        val signInEnabled: Observable<Boolean> = phoneObservable


        signInEnabled.distinctUntilChanged()
            .bindToLifecycle(forgotSendOtp)
            .subscribe {
                forgotSendOtp.isEnabled = it

                if (it)
                    forgotSendOtp.showKeyboard(false)
            }

        signInEnabled.distinctUntilChanged()
            .map { b ->
                if (b) {
                    R.color.colorPrimaryDark
                } else R.color.colorTextDisabled
            }
            .bindToLifecycle(forgotSendOtp)
            .subscribe { color ->
                forgotSendOtp.backgroundTintList =
                    ContextCompat.getColorStateList(this, color)
            }
    }


    private fun setupButtonClick() {
        // check if user exists with the given mobile or not
        forgotSendOtp.setOnClickListener {
            requestValidateUserApi()
        }

    }

    private fun requestValidateUserApi() {
        val map = HashMap<String, Any>()
        map["mobile"] = forgotPhone.text.toString().trim()

        ApiManager.getInstance().requestApi(ApiMode.VERIFY_USER_BY_MOBILE, map, true, this, "POST")
    }

    override fun onSuccess(jsonObject: JsonObject?, mode: ApiMode?) {
        // redirect to verify otp activity, and pass mobile number
        launchActivity<VerifyOtpActivity> {
            putExtra("mobile", forgotPhone.text.toString().trim())
            putExtra("mode", VerifyOtpActivity.FORGOT_PASSWORD)
        }
    }

    override fun onFailure(errorObject: JsonObject?, mode: ApiMode?) {
        AppUtils.shortToast(errorObject?.get("message")?.asString)
    }

    override fun onException(e: Exception?, mode: ApiMode?) {
        AppUtils.showException()
    }
}
