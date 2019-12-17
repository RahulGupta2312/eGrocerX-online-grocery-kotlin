package com.egrocerx.ui.cancel


import android.content.Intent
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
import com.egrocerx.ui.dashboard.DashboardActivity
import com.egrocerx.util.AppPreference
import com.egrocerx.util.AppUtils
import kotlinx.android.synthetic.main.fragment_cancel_order.*

class CancelOrderFragment : Fragment(), ApiResponse {


    private lateinit var cancelOrderFragmentArgs: CancelOrderFragmentArgs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cancelOrderFragmentArgs = CancelOrderFragmentArgs.fromBundle(arguments!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cancel_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListener()
    }

    private fun setupListener() {
        btnConfirmCancel.setOnClickListener {
            val cancelReason = etCancelReason.text.toString()
            if (TextUtils.isEmpty(cancelReason))
                AppUtils.shortToast("Enter a valid reason")
            else {
                requestCancelOrderApi(cancelReason)
            }
        }
    }

    private fun requestCancelOrderApi(cancelReason: String) {
        val map = hashMapOf<String, Any>()
        map["customer_id"] = AppPreference.getInstance().userData.customer_id
        map["order_id"] = cancelOrderFragmentArgs.orderId
        map["reason"] = cancelReason

        ApiManager.getInstance().requestApi(ApiMode.CANCEL_ORDER, map, true, this, "POST")
    }

    override fun onSuccess(jsonObject: JsonObject?, mode: ApiMode?) {
        if (cancelOrderFragmentArgs.source == "ORDER_DETAIL") {
            findNavController().popBackStack()
            return
        }
        AppUtils.shortToast(jsonObject?.get("message")?.asString)
        activity?.finish()
        startActivity(Intent(context, DashboardActivity::class.java))
    }

    override fun onFailure(errorObject: JsonObject?, mode: ApiMode?) {
        AppUtils.shortToast(errorObject?.get("message")?.asString)
    }

    override fun onException(e: Exception?, mode: ApiMode?) {
        AppUtils.showException()
    }
}
