package com.egrocerx.ui.membership


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.egrocerx.R
import com.egrocerx.data.MembershipItem
import com.egrocerx.network.ApiManager
import com.egrocerx.network.ApiMode
import com.egrocerx.network.ApiResponse
import com.egrocerx.ui.splash.MainActivity
import com.egrocerx.util.AppPreference
import com.egrocerx.util.AppUtils
import com.egrocerx.util.PaytmHelper
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import kotlinx.android.synthetic.main.fragment_membership.*

class MembershipFragment : Fragment(), ApiResponse {


    private lateinit var membershipAdapter: MembershipAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        membershipAdapter = MembershipAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_membership, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupRecyclerView()
        setupBuyMembershipButton()
    }

    private fun setupBuyMembershipButton() {
        materialButton2.setOnClickListener {
            val membership = membershipAdapter.getSelectedMembership()
            membership?.let {
                handleMembershipPurchase(it)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (membershipAdapter.itemCount == 0)
            requestMembershipApi()
    }

    private fun handleMembershipPurchase(membership: MembershipItem) {

        PaytmHelper.generateChecksum(membership.sale_price) {

            it.startPaymentTransaction(
                context,
                true,
                true,
                object : PaytmPaymentTransactionCallback {
                    override fun someUIErrorOccurred(inErrorMessage: String) {}

                    override fun onTransactionResponse(response: Bundle) {

                        if (response.getString("STATUS") == "TXN_FAILURE")
                            AppUtils.shortToast(response.getString("STATUS"))
                        else
                            saveTransactionToServer(response, membership)
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
    }

    private fun saveTransactionToServer(response: Bundle, membership: MembershipItem) {
        val map = java.util.HashMap<String, Any>()
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
        map["membership_id"] = membership.id
        map["expiry"] = AppUtils.getFutureDate(membership.days)

        ApiManager.getInstance().requestApi(
            ApiMode.UPGRADE_MEMBERSHIP, map,
            true, this, "POST"
        )

    }


    private fun setupRecyclerView() {
        membershipPackageRecycler.apply {
            adapter = membershipAdapter
            addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
        }
    }

    private fun requestMembershipApi() {
        progressBar.visibility = View.VISIBLE
        val map = HashMap<String, Any>()
        map["customer_id"] = AppPreference.getInstance().userData.customer_id
        ApiManager.getInstance().requestApi(ApiMode.MEMBERSHIPS, map, false, this, "POST")
    }

    override fun onSuccess(jsonObject: JsonObject?, mode: ApiMode?) {

        when (mode) {
            ApiMode.MEMBERSHIPS -> {
                progressBar.visibility = View.GONE
                val typeToken = object : TypeToken<ArrayList<MembershipItem>>() {}.type
                val list = Gson().fromJson<ArrayList<MembershipItem>>(
                    jsonObject?.getAsJsonArray("data"),
                    typeToken
                )
                membershipAdapter.addMemberships(list)

                if (jsonObject?.get("membership_id")?.asInt == -1) {
                    membershipAdapter.setOptionsEnabled()
                    materialButton2.visibility = View.VISIBLE
                }
            }
            ApiMode.UPGRADE_MEMBERSHIP -> {
                AppUtils.shortToast(jsonObject?.get("message")?.asString)
                Intent(context, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(this)
                }
                activity?.finishAffinity()
            }
        }
    }

    override fun onFailure(errorObject: JsonObject?, mode: ApiMode?) {
        AppUtils.shortToast(errorObject?.get("message")?.asString)
    }

    override fun onException(e: Exception?, mode: ApiMode?) {
        AppUtils.showException(e)
    }


}
