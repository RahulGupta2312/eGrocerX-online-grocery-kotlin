package com.egrocerx.ui.register

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
import com.egrocerx.util.AppUtils
import com.egrocerx.util.showKeyboard
import com.trello.rxlifecycle3.kotlin.bindToLifecycle
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function3
import kotlinx.android.synthetic.main.activity_register.*
import java.util.concurrent.TimeUnit

class RegisterActivity : BaseActivity(), ApiResponse {

    private lateinit var nameObservable: Observable<Boolean>
    private lateinit var phoneObservable: Observable<Boolean>

    private lateinit var passwordObservable: Observable<Boolean>
    private lateinit var signUpEnabled: Observable<Boolean>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setupObservables()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        buttonRegister.setOnClickListener {
            requestRegisterApi()
        }

        navLogin.setOnClickListener {
            onBackPressed()
        }

    }

    private fun setupObservables() {
        setupFullName()
        setupPhone()
//        setupEmail()
        setupPassword()
//        setupConfirmPassword()
        setupRegisterButton()
    }

    @SuppressLint("CheckResult")
    private fun setupRegisterButton() {
        signUpEnabled = Observable.combineLatest(
            nameObservable,
            phoneObservable,
            passwordObservable,
            Function3 { na, ph, c -> na && ph && c })


        signUpEnabled.distinctUntilChanged()
            .bindToLifecycle(buttonRegister)
            .subscribe {
                buttonRegister.isEnabled = it

                if (it)
                    buttonRegister.showKeyboard(false)
            }

        signUpEnabled.distinctUntilChanged()
            .map { b ->
                if (b) {
                    R.color.colorPrimaryDark
                } else R.color.colorTextDisabled
            }
            .bindToLifecycle(buttonRegister)
            .subscribe { color ->
                buttonRegister.backgroundTintList =
                    ContextCompat.getColorStateList(this, color)
            }
    }

    @SuppressLint("CheckResult")
    private fun setupPassword() {
        passwordObservable = RxTextView.textChanges(registerPassword)
            .skip(1)
            .debounce(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .map { t -> t.length > 4 }
            .distinctUntilChanged()

        passwordObservable
            .bindToLifecycle(registerPassword)
            .subscribe { isValid ->
                if (isValid)
                    registerPasswordTIL.isErrorEnabled = false
                else {
                    registerPasswordTIL.isErrorEnabled = false
                    registerPasswordTIL.error = "Minimum 5 characters"
                }
            }

    }
//
//    @SuppressLint("CheckResult")
//    private fun setupEmail() {
//        emailObservable = RxTextView.textChanges(registerEmail)
//            .skip(1)
//            .debounce(1, TimeUnit.SECONDS)
//            .observeOn(AndroidSchedulers.mainThread())
//            .map { t -> t.length > 3 && t.matches(Patterns.EMAIL_ADDRESS.toRegex()) }
//            .distinctUntilChanged()
//
//        emailObservable
//            .bindToLifecycle(registerEmail)
//            .subscribe { isValid ->
//                if (isValid)
//                    registerEmailTIL.isErrorEnabled = false
//                else {
//                    registerEmailTIL.isErrorEnabled = false
//                    registerEmailTIL.error = "Enter a valid email"
//                }
//            }
//
//    }

    @SuppressLint("CheckResult")
    private fun setupPhone() {
        //phone number

        phoneObservable = RxTextView.textChanges(registerPhone)
            .skip(1)
            .debounce(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .map { t -> t.length == 10 }
            .distinctUntilChanged()

        phoneObservable
            .bindToLifecycle(registerPhone)
            .subscribe { isValid ->
                if (isValid)
                    registerPhoneTIL.isErrorEnabled = false
                else {
                    registerPhoneTIL.isErrorEnabled = false
                    registerPhoneTIL.error = "Phone number length must be 10"
                }
            }
    }

    @SuppressLint("CheckResult")
    private fun setupFullName() {
        nameObservable = RxTextView.textChanges(registerName)
            .skip(1)
            .debounce(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .map { t -> t.length > 2 }
            .distinctUntilChanged()

        nameObservable
            .bindToLifecycle(registerName)
            .subscribe { isValid ->
                if (isValid)
                    registerNameTIL.isErrorEnabled = false
                else {
                    registerNameTIL.isErrorEnabled = false
                    registerNameTIL.error = "Enter a valid name"
                }
            }
    }

    // TODO: ADD OTP VERIFICATION FOR MOBILE
    private fun requestRegisterApi() {
        val map = HashMap<String, Any>()
        map["mobile"] = registerPhone.text.toString()
        map["password"] = registerPassword.text.toString()
        map["name"] = registerName.text.toString()
        map["email"] = registerEmail.text.toString()
        map["push_token"] = "12345678"

        ApiManager.getInstance().requestApi(ApiMode.REGISTER, map, true, this, "POST")
    }

    override fun onSuccess(jsonObject: JsonObject?, mode: ApiMode?) {
        AppUtils.shortToast(jsonObject?.get("message")?.asString)
        finish()
    }

    override fun onFailure(errorObject: JsonObject?, mode: ApiMode?) {
        AppUtils.shortToast(errorObject?.get("message")?.asString)
    }

    override fun onException(e: Exception?, mode: ApiMode?) {
        e?.printStackTrace()
        AppUtils.showException()
    }
}
