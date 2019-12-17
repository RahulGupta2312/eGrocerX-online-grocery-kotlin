package com.egrocerx.util

import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.StringRequestListener
import com.egrocerx.network.ApiManager.PROJECT_ROOT_URL
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPGService
import org.json.JSONObject
import java.util.*

object PaytmHelper {

    private var orderId: String = (System.currentTimeMillis() / 10000).toString()

    fun generateChecksum(amount: Double, paytmService: (PaytmPGService) -> Unit) {
        val jsn = JSONObject()
        jsn.put("ORDER_ID", orderId)
        jsn.put("CUST_ID", AppPreference.getInstance().userData.customer_id)
        jsn.put("TXN_AMOUNT", amount.toString())
        jsn.put("MID", "KARLOF06969666333083")
        jsn.put("INDUSTRY_TYPE_ID", "Retail109")
        jsn.put("CHANNEL_ID", "WAP")
        jsn.put("WEBSITE", "KARLOFWAP")
        jsn.put(
            "CALLBACK_URL",
            "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=${orderId}"
        )

        AndroidNetworking.post(PROJECT_ROOT_URL + "paytm/generateChecksum.php")
            .addJSONObjectBody(jsn)
            .build()
            .getAsString(object : StringRequestListener {
                override fun onError(anError: ANError?) {
                    AppUtils.showException()
                }

                override fun onResponse(response: String?) {
                    launchPaytm(response!!, amount, paytmService)
                }
            })
    }

    private fun launchPaytm(
        checksum: String,
        amount: Double,
        paytmService: (PaytmPGService) -> Unit
    ) {
        val Service = PaytmPGService.getProductionService() // production service
        val paramMap = HashMap<String, String>()
        paramMap["MID"] = "KARLOF06969666333083"
        paramMap["ORDER_ID"] = orderId
        paramMap["CUST_ID"] = AppPreference.getInstance().userData.customer_id
        paramMap["CHANNEL_ID"] = "WAP"
        paramMap["TXN_AMOUNT"] = amount.toString()
        paramMap["WEBSITE"] = "KARLOFWAP"
        paramMap["INDUSTRY_TYPE_ID"] = "Retail109"
        paramMap["CALLBACK_URL"] =
            "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=${orderId}"
        paramMap["CHECKSUMHASH"] = checksum

        val Order = PaytmOrder(paramMap)
        Service.initialize(Order, null)
        paytmService(Service)
    }
}