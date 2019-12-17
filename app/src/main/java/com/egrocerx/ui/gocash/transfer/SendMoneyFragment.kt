package com.egrocerx.ui.gocash.transfer


import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.navigation.fragment.findNavController
import com.google.gson.JsonObject
import com.egrocerx.R
import com.egrocerx.base.BaseFragment
import com.egrocerx.network.ApiManager
import com.egrocerx.network.ApiMode
import com.egrocerx.network.ApiResponse
import com.egrocerx.util.AppPreference
import com.egrocerx.util.AppUtils
import kotlinx.android.synthetic.main.fragment_send_money.*

class SendMoneyFragment : BaseFragment(), ApiResponse {


    override fun getLayoutId(): Int {
        return R.layout.fragment_send_money
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnSendMoney.setOnClickListener {
            validateForm()
        }
    }

    private fun validateForm() {
        if (TextUtils.isEmpty(transferMobile.text.toString().trim())) {
            AppUtils.shortToast("Enter a valid mobile number")
        } else if (TextUtils.isEmpty(transferAmount.text.toString().trim())
            || transferAmount.text.toString().toInt() < 1
        ) {
            AppUtils.shortToast("Enter a valid amount")
        } else {

            requestSendMoneyApi()
        }
    }

    private fun requestSendMoneyApi() {
        val map = HashMap<String, Any>()
        map["customer_id"] = AppPreference.getInstance().userData.customer_id
        map["amount"] = transferAmount.text.toString().trim()
        map["mobile"] = transferMobile.text.toString().trim()

        ApiManager.getInstance().requestApi(ApiMode.SEND_MONEY, map, true, this, "POST")
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
