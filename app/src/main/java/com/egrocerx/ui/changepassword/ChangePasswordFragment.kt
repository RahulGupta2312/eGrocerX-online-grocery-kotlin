package com.egrocerx.ui.changepassword


import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.gson.JsonObject
import com.egrocerx.R
import com.egrocerx.network.ApiManager
import com.egrocerx.network.ApiMode
import com.egrocerx.network.ApiResponse
import com.egrocerx.util.AppPreference
import com.egrocerx.util.AppUtils
import kotlinx.android.synthetic.main.fragment_change_password.*


class ChangePasswordFragment : Fragment(), ApiResponse {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        btnUpdatePassword.setOnClickListener {
            if (TextUtils.isEmpty(etChangePasswordOld.text.toString().trim())) {
                AppUtils.shortToast("Enter a valid old password")
            } else if (TextUtils.isEmpty(etChangePasswordNew.text.toString().trim()) ||
                etChangePasswordNew.text.toString().length < 4
            ) {
                AppUtils.shortToast("New Password must have at least 4 characters")
            } else {
                requestUpdatePasswordApi()
            }
        }
    }

    private fun requestUpdatePasswordApi() {
        val map = HashMap<String, Any>()
        map["old_password"] = etChangePasswordOld.text.toString().trim()
        map["new_password"] = etChangePasswordOld.text.toString().trim()
        map["mobile"] = AppPreference.getInstance().userData.mobile

        ApiManager.getInstance().requestApi(ApiMode.CHANGE_PASSWORD, map, true, this, "POST")

    }

    override fun onSuccess(jsonObject: JsonObject?, mode: ApiMode?) {
        AppUtils.shortToast(jsonObject?.get("message")?.asString)
        findNavController().popBackStack()
    }

    override fun onFailure(errorObject: JsonObject?, mode: ApiMode?) {
        AppUtils.shortToast(errorObject?.get("message")?.asString)
    }

    override fun onException(e: Exception?, mode: ApiMode?) {
        e?.printStackTrace()
        AppUtils.showException()
    }


}
