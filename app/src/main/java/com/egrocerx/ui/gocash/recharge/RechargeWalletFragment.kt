package com.egrocerx.ui.gocash.recharge


import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.navigation.fragment.findNavController
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.StringRequestListener
import com.google.gson.JsonObject
import com.egrocerx.R
import com.egrocerx.base.BaseFragment
import com.egrocerx.network.ApiManager
import com.egrocerx.network.ApiManager.PROJECT_ROOT_URL
import com.egrocerx.network.ApiMode
import com.egrocerx.network.ApiResponse
import com.egrocerx.util.AppPreference
import com.egrocerx.util.AppUtils
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPGService
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import kotlinx.android.synthetic.main.fragment_recharge_wallet.*
import org.json.JSONObject
import java.util.*

class RechargeWalletFragment : BaseFragment(), ApiResponse {

    private lateinit var orderId: String

    override fun getLayoutId(): Int {
        return R.layout.fragment_recharge_wallet
    }

    override fun onResume() {
        super.onResume()
        orderId = Random().nextLong().toString()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnRechargeWallet.setOnClickListener {
            validateForm()
        }
    }

    private fun validateForm() {
        if (TextUtils.isEmpty(rechargeAmount.text.toString().trim())
            || rechargeAmount.text.toString().toInt() < 1
        ) {
            AppUtils.shortToast("Enter a valid amount")
        } else {
            generateChecksum(rechargeAmount.text.toString().toInt())
        }
    }

    private fun generateChecksum(amount: Int) {
        val jsn = JSONObject()
        jsn.put("ORDER_ID", orderId)
        jsn.put("CUST_ID", AppPreference.getInstance().userData.customer_id)
        jsn.put("TXN_AMOUNT", amount)
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
                    launchPaytm(response!!, amount)
                }
            })
    }

    private fun launchPaytm(checksum: String, amount: Int) {
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
        Service.startPaymentTransaction(
            context,
            true,
            true,
            object : PaytmPaymentTransactionCallback {
                override fun someUIErrorOccurred(inErrorMessage: String) {}

                override fun onTransactionResponse(response: Bundle) {
                    if (response.getString("STATUS") == "TXN_FAILURE")
                        AppUtils.shortToast(response.getString("STATUS"))
                    else
                        saveTransactionToServer(response)
                }

                override fun networkNotAvailable() {}
                override fun clientAuthenticationFailed(inErrorMessage: String) {
                    AppUtils.shortToast(inErrorMessage)
                }

                override fun onErrorLoadingWebPage(
                    iniErrorCode: Int,
                    inErrorMessage: String,
                    inFailingUrl: String
                ) {
                    AppUtils.shortToast(inErrorMessage)
                }

                override fun onBackPressedCancelTransaction() {}
                override fun onTransactionCancel(inErrorMessage: String, inResponse: Bundle) {
                    AppUtils.shortToast(inErrorMessage)
                }
            })
    }


    /**
     *
     * @param response Bundle
     *  /*Bundle[{STATUS=TXN_SUCCESS, BANKNAME=WALLET, ORDERID=19976,
     *  TXNAMOUNT=1.00, TXNDATE=2019-10-16 21:26:23.0, MID=KARLOF06969666333083,
     *  TXNID=20191016111212800110168801093002075, RESPCODE=01, PAYMENTMODE=PPI,
     *  BANKTXNID=124470043649, CURRENCY=INR, GATEWAYNAME=WALLET,
     *  RESPMSG=Txn Success}]*/
     */

    private fun saveTransactionToServer(response: Bundle) {
        val map = HashMap<String, Any>()
        map["STATUS"] = response.getString("STATUS", "")
        map["BANKNAME"] = response.getString("BANKNAME", "")
        map["ORDERID"] = response.getString("ORDERID", "")
        map["TXNAMOUNT"] = response.getString("TXNAMOUNT", "")
        map["TXNDATE"] = response.getString("TXNDATE", "")
        map["TXNID"] = response.getString("TXNID", "")
        map["RESPCODE"] = response.getString("RESPCODE", "")
        map["PAYMENTMODE"] = response.getString("PAYMENTMODE", "")
        map["BANKTXNID"] = response.getString("BANKTXNID", "")
        map["GATEWAYNAME"] = response.getString("GATEWAYNAME", "")
        map["customer_id"] = AppPreference.getInstance().userData.customer_id
        ApiManager.getInstance().requestApi(ApiMode.RECHARGE_WALLET, map, true, this, "POST")

    }

    override fun onSuccess(jsonObject: JsonObject?, mode: ApiMode?) {
        findNavController().popBackStack()
    }

    override fun onFailure(errorObject: JsonObject?, mode: ApiMode?) {

    }

    override fun onException(e: Exception?, mode: ApiMode?) {
        AppUtils.showException()
    }
}
