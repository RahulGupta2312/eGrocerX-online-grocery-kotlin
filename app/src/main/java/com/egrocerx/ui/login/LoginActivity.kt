package com.egrocerx.ui.login

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.egrocerx.R
import com.egrocerx.base.BaseActivity
import com.egrocerx.network.ApiManager
import com.egrocerx.network.ApiMode
import com.egrocerx.network.ApiResponse
import com.egrocerx.ui.dashboard.DashboardActivity
import com.egrocerx.ui.forgot.ForgotPasswordActivity
import com.egrocerx.ui.register.RegisterActivity
import com.egrocerx.util.AppPreference
import com.egrocerx.util.AppUtils
import com.egrocerx.util.launchActivity
import com.egrocerx.util.showKeyboard
import com.google.gson.JsonObject
import com.jakewharton.rxbinding2.widget.RxTextView
import com.trello.rxlifecycle3.kotlin.bindToLifecycle
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import kotlinx.android.synthetic.main.activity_login.*
import java.util.concurrent.TimeUnit

class LoginActivity : BaseActivity(), ApiResponse {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setupObservables()
        setupClickListeners()
    }

    @SuppressLint("CheckResult")
    private fun setupObservables() {

        //phone number

        val userObservable: Observable<Boolean> = RxTextView.textChanges(loginPhone)
            .skip(1)
            .debounce(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .map { t -> t.length == 10 }
            .distinctUntilChanged()

        userObservable
            .bindToLifecycle(loginPhone)
            .subscribe { isValid ->
                if (isValid)
                    loginPhoneTIL.isErrorEnabled = false
                else {
                    loginPhoneTIL.isErrorEnabled = false
                    loginPhoneTIL.error = "Phone number length must be 10"
                }
            }

        //password

        val passObservable: Observable<Boolean> = RxTextView.textChanges(loginPassword)
            .skip(1)
            .debounce(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .map { t -> t.length > 4 }
            .distinctUntilChanged()

        passObservable
            .bindToLifecycle(loginPassword)
            .subscribe { isValid ->
                if (isValid)
                    loginPasswordTIL.isErrorEnabled = false
                else {
                    loginPasswordTIL.isErrorEnabled = false
                    loginPasswordTIL.error = "Minimum 5 characters"
                }
            }


        // login button

        val signInEnabled: Observable<Boolean> = Observable.combineLatest(
            userObservable, passObservable, BiFunction { u, p -> u && p })


        signInEnabled.distinctUntilChanged()
            .bindToLifecycle(loginButton)
            .subscribe {
                loginButton.isEnabled = it

                if (it)
                    loginButton.showKeyboard(false)
            }

        signInEnabled.distinctUntilChanged()
            .map { b ->
                if (b) {
                    R.color.colorPrimaryDark
                } else R.color.colorTextDisabled
            }
            .bindToLifecycle(loginButton)
            .subscribe { color ->
                loginButton.backgroundTintList =
                    ContextCompat.getColorStateList(this, color)
            }
    }

    private fun setupClickListeners() {
        loginButton.setOnClickListener {
            requestLoginApi()
        }

        navForgotPassword.setOnClickListener {
            launchActivity<ForgotPasswordActivity> { }
        }

        navSignup.setOnClickListener {
            launchActivity<RegisterActivity> { }
        }
    }

    private fun requestLoginApi() {
        val map = HashMap<String, Any?>()
        map["mobile"] = loginPhone.text.toString()
        map["password"] = loginPassword.text.toString()
        map["fcm_token"] = AppPreference.getInstance().fcmToken
        ApiManager.getInstance().requestApi(ApiMode.LOGIN, map, true, this, "POST")
    }

    override fun onSuccess(jsonObject: JsonObject?, mode: ApiMode?) {

//        val userModel = Gson().fromJson(jsonObject?.getAsJsonObject("data"), UserModel::class.java)
//        if (userModel.mobile_verified == 1) {
        AppPreference.getInstance().saveUserData(jsonObject?.getAsJsonObject("data").toString())
        AppPreference.getInstance().saveLoggedIn(true)
        launchActivity<DashboardActivity> { }
        finishAffinity()
//        } else {
//            launchActivity<VerifyOtpActivity> {
//                putExtra("mobile", userModel.mobile)
//                putExtra("customer_id", userModel.customer_id)
//                putExtra("mode",VerifyOtpActivity.MOBILE_VERIFICATION)
//            }
//        }
    }

    override fun onFailure(errorObject: JsonObject?, mode: ApiMode?) {
        AppUtils.shortToast(errorObject?.get("message")?.asString)
    }

    override fun onException(e: Exception?, mode: ApiMode?) {
        e?.printStackTrace()
        AppUtils.showException()
    }
}
