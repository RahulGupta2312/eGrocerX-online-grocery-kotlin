package com.egrocerx.ui.profile


import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.jakewharton.rxbinding2.widget.RxTextView
import com.egrocerx.R
import com.egrocerx.data.UserModel
import com.egrocerx.network.ApiManager
import com.egrocerx.network.ApiMode
import com.egrocerx.network.ApiResponse
import com.egrocerx.util.AppPreference
import com.egrocerx.util.AppUtils
import com.egrocerx.util.showKeyboard
import com.trello.rxlifecycle3.kotlin.bindToLifecycle
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import kotlinx.android.synthetic.main.fragment_my_profile.*
import java.util.concurrent.TimeUnit

class MyProfileFragment : Fragment(), ApiResponse {


    private lateinit var nameObservable: Observable<Boolean>
    private lateinit var emailObservable: Observable<Boolean>
    private lateinit var updateEnabled: Observable<Boolean>

    private lateinit var userModel: UserModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupClickListeners()

        requestProfileApi()
    }

    private fun requestProfileApi() {
        val map = HashMap<String, Any>()
        map["id"] = AppPreference.getInstance().userData.customer_id
        ApiManager.getInstance().requestApi(ApiMode.USER_PROFILE, map, true, this, "POST")
    }

    override fun onSuccess(jsonObject: JsonObject?, mode: ApiMode?) {
        when (mode) {
            ApiMode.UPDATE_PROFILE -> {
                AppUtils.shortToast(jsonObject?.get("message")?.asString)
            }
            ApiMode.USER_PROFILE -> {
                userModel = Gson().fromJson(
                    jsonObject?.getAsJsonObject("data"),
                    UserModel::class.java
                )
                setupObservables()

                profileName.setText(userModel.name)
                profilePhone.setText(userModel.mobile)
                profileEmail.setText(userModel.e_mail)
            }
        }
    }

    override fun onFailure(errorObject: JsonObject?, mode: ApiMode?) {
        AppUtils.shortToast(errorObject?.get("message")?.asString)
    }

    override fun onException(e: Exception?, mode: ApiMode?) {
        e?.printStackTrace()
        AppUtils.showException()
    }


    private fun setupObservables() {
        setupFullName()
        setupEmail()
        setupUpdateButton()
    }

    @SuppressLint("CheckResult")
    private fun setupUpdateButton() {
        updateEnabled = Observable.combineLatest(
            nameObservable,
            emailObservable,
            BiFunction { na, e -> na && e })


        updateEnabled.distinctUntilChanged()
            .bindToLifecycle(buttonUpdateProfile)
            .subscribe {
                buttonUpdateProfile.isEnabled = it

                if (it)
                    buttonUpdateProfile.showKeyboard(false)
            }

        updateEnabled.distinctUntilChanged()
            .map { b ->
                if (b) {
                    R.color.colorPrimaryDark
                } else R.color.colorTextDisabled
            }
            .bindToLifecycle(buttonUpdateProfile)
            .subscribe { color ->
                buttonUpdateProfile.backgroundTintList =
                    ContextCompat.getColorStateList(context!!, color)
            }
    }

    @SuppressLint("CheckResult")
    private fun setupEmail() {
        emailObservable = RxTextView.textChanges(profileEmail)
            .skip(1)
            .debounce(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .map { t -> t.length > 3 && t.matches(Patterns.EMAIL_ADDRESS.toRegex()) }
            .distinctUntilChanged()

        emailObservable
            .bindToLifecycle(profileEmail)
            .subscribe { isValid ->
                if (isValid)
                    profileEmailTIL.isErrorEnabled = false
                else {
                    profileEmailTIL.isErrorEnabled = false
                    profileEmailTIL.error = "Enter a valid email"
                }
            }

    }

    @SuppressLint("CheckResult")
    private fun setupFullName() {
        nameObservable = RxTextView.textChanges(profileName)
            .skip(1)
            .debounce(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .map { t -> t.length > 2 }
            .distinctUntilChanged()

        nameObservable
            .bindToLifecycle(profileName)
            .subscribe { isValid ->
                if (isValid)
                    profileNameTIL.isErrorEnabled = false
                else {
                    profileNameTIL.isErrorEnabled = false
                    profileNameTIL.error = "Enter a valid name"
                }
            }
    }


    private fun setupClickListeners() {
        buttonUpdateProfile.setOnClickListener {
            requestUpdateProfileApi()
        }
    }

    private fun requestUpdateProfileApi() {
        val map = HashMap<String, Any>()
        map["name"] = profileName.text.toString()
        map["customer_id"] = AppPreference.getInstance().userData.customer_id
        map["email"] = profileEmail.text.toString()
        ApiManager.getInstance().requestApi(ApiMode.UPDATE_PROFILE, map, true, this, "POST")
    }


}
