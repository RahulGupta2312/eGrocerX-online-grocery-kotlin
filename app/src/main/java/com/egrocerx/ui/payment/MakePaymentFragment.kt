package com.egrocerx.ui.payment


import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.StringRequestListener
import com.google.gson.JsonObject
import com.egrocerx.NavDashboardDirections
import com.egrocerx.R
import com.egrocerx.data.OrderDetailItem
import com.egrocerx.network.ApiManager
import com.egrocerx.network.ApiManager.PROJECT_ROOT_URL
import com.egrocerx.network.ApiMode
import com.egrocerx.network.ApiResponse
import com.egrocerx.ui.dashboard.DashboardActivity
import com.egrocerx.util.AppPreference
import com.egrocerx.util.AppUtils
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPGService
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import kotlinx.android.synthetic.main.fragment_make_payment.*
import kotlinx.android.synthetic.main.item_order_detail_breakup.view.*
import org.json.JSONObject


class MakePaymentFragment : Fragment(), ApiResponse {

    private lateinit var makePaymentFragmentArgs: MakePaymentFragmentArgs
    private var mView: View? = null
    private var walletPay = "no"
    private var orderDetailListAdapter: OrderDetailListAdapter? = null
    private var totalPayable = 0.0
    private var netPayable = 0.0
    private var goCashPay = 0.0
    private var paymentMode = ""

    private var transactionOrderId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makePaymentFragmentArgs = MakePaymentFragmentArgs.fromBundle(arguments!!)
        netPayable = makePaymentFragmentArgs.order.netAmount?.toDouble()!!
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (mView == null) {
            return inflater.inflate(R.layout.fragment_make_payment, container, false)
        }
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateUI()
        handleMakePaymentButton()
        handleCancelOrderButton()
    }

    private fun handleCancelOrderButton() {
        btnCancelOrder.setOnClickListener {
            showMakePaymentOrCancelWarningDialog()
        }
    }

    private fun handleMakePaymentButton() {
        btnMakePayment.setOnClickListener {
            if (TextUtils.isEmpty(paymentMode)) {
                AppUtils.shortToast("Please select a payment mode")
                return@setOnClickListener
            }

            if (paymentMode == "TXN_CARD") {
                generateChecksum()
            } else {
                updatePaymentForTheOrder()
            }
        }
    }

    private fun updatePaymentForTheOrder() {

        val map = hashMapOf<String, Any>()
        map["customer_id"] = AppPreference.getInstance().userData.customer_id
        map["order_id"] = makePaymentFragmentArgs.order.orderId!!
        map["wallet_pay"] = goCashPay
        map["payment_code"] = paymentMode

        ApiManager.getInstance().requestApi(
            ApiMode.UPDATE_ORDER, map, true,
            this, "POST"
        )
    }

    override fun onSuccess(jsonObject: JsonObject?, mode: ApiMode?) {
        AppUtils.shortToast(jsonObject?.get("message")?.asString)
        activity?.finish()
        startActivity(Intent(context, DashboardActivity::class.java))
    }

    override fun onFailure(errorObject: JsonObject?, mode: ApiMode?) {
        AppUtils.shortToast(errorObject?.get("message")?.asString)
    }

    override fun onException(e: Exception?, mode: ApiMode?) {
        e?.printStackTrace()
        AppUtils.showException()
    }

    private fun generateChecksum() {
        transactionOrderId = System.currentTimeMillis() / 10000
        val jsn = JSONObject()
        jsn.put("ORDER_ID", transactionOrderId.toString())
        jsn.put("CUST_ID", makePaymentFragmentArgs.order.customerId)
        jsn.put("TXN_AMOUNT", totalPayable.toString())
        jsn.put("MID", "KARLOF06969666333083")
        jsn.put("INDUSTRY_TYPE_ID", "Retail109")
        jsn.put("CHANNEL_ID", "WAP")
        jsn.put("WEBSITE", "KARLOFWAP")
        jsn.put(
            "CALLBACK_URL",
            "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=${makePaymentFragmentArgs.order.orderId}"
        )

        AndroidNetworking.post(PROJECT_ROOT_URL + "paytm/generateChecksum.php")
            .addJSONObjectBody(jsn)
            .build()
            .getAsString(object : StringRequestListener {
                override fun onError(anError: ANError?) {

                }

                override fun onResponse(response: String?) {
                    launchPaytm(response!!)
                }
            })
    }

    //TODO: change total payable
    private fun launchPaytm(checksum: String) {
        val Service = PaytmPGService.getProductionService() // production service
        val paramMap = HashMap<String, String>()
        paramMap["MID"] = "KARLOF06969666333083"
        paramMap["ORDER_ID"] = transactionOrderId.toString()
        paramMap["CUST_ID"] = makePaymentFragmentArgs.order.customerId!!
        paramMap["CHANNEL_ID"] = "WAP"
        paramMap["TXN_AMOUNT"] = totalPayable.toString()
        paramMap["WEBSITE"] = "KARLOFWAP"
        paramMap["INDUSTRY_TYPE_ID"] = "Retail109"
        paramMap["CALLBACK_URL"] =
            "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=${makePaymentFragmentArgs.order.orderId}"
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
                    if (response.getString("STATUS") == "TXN_FAILURE") {
                        // generate new order id so that user can retry payment
                        AppUtils.shortToast(response.getString("STATUS"))
                    } else
                        saveTransactionToServer(response)
                }

                override fun networkNotAvailable() {}
                override fun clientAuthenticationFailed(inErrorMessage: String) {}
                override fun onErrorLoadingWebPage(
                    iniErrorCode: Int,
                    inErrorMessage: String,
                    inFailingUrl: String
                ) {
                }

                override fun onBackPressedCancelTransaction() {}
                override fun onTransactionCancel(inErrorMessage: String, inResponse: Bundle) {}
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
        map["order_id"] = makePaymentFragmentArgs.order.id!!

        ApiManager.getInstance().requestApi(ApiMode.SAVE_TRANSACTION, map, true, this, "POST")

    }


    private fun showMakePaymentOrCancelWarningDialog() {
        AlertDialog.Builder(activity)
            .setTitle("Warning")
            .setMessage("Please make the payment to finalize your order.")
            .setPositiveButton("Continue payment") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .setNegativeButton("Cancel my order") { dialogInterface, _ ->
                dialogInterface.dismiss()
                val dir =
                    NavDashboardDirections.actionGlobalCancelOrderFragment3(
                        makePaymentFragmentArgs.order.orderId!!
                        , "CHECKOUT"
                    )
                findNavController().navigate(dir)
            }
            .setCancelable(false)
            .create()
            .show()

    }

    private fun setTotalPayable() {
        totalPayable = netPayable - goCashPay
        payoutTotalPayable.text =
            "Total Payable: ${AppUtils.getAmountWithCurrency(totalPayable)}"

        if (totalPayable == 0.0) {
            paymentMode = "TXN_GOCASH"
            layoutPaymentMode.visibility = View.GONE
        } else {
            paymentMode = ""
            layoutPaymentMode.visibility = View.VISIBLE
        }
    }

    private fun populateUI() {
        setTotalPayable()
        populateOrderData()
        populateWalletPay()
        setupWalletPayCheckedListener()
        setupPaymentRadioCheckedListener()

    }


    private fun setupPaymentRadioCheckedListener() {
        paymentModeRbGrp.setOnCheckedChangeListener { radioGroup, i ->
            val id = radioGroup.checkedRadioButtonId
            if (id == R.id.paymentCod)
                paymentMode = "TXN_COD"
            else if (id == R.id.paymentOnline) {
                paymentMode = "TXN_CARD"
            }
        }
    }

    private fun setupWalletPayCheckedListener() {
        payWalletCb.setOnCheckedChangeListener { compoundButton, b ->
            if (!b) {
                goCashPay = 0.0
                walletPay = "no"
            } else {
                goCashPay = makePaymentFragmentArgs.walletBalance.toDouble()
                walletPay = "yes"
            }
            setTotalPayable()
        }
    }

    private fun populateOrderData() {

        val list = ArrayList<OrderDetailItem>()

        list.add(OrderDetailItem("Order Id", makePaymentFragmentArgs.order.orderId!!))
        list.add(OrderDetailItem("Order Date", makePaymentFragmentArgs.order.orderDate!!))
        list.add(
            OrderDetailItem(
                "Product Cost",
                AppUtils.getAmountWithCurrency(makePaymentFragmentArgs.order.baseValue!!)
            )
        )

        if (!TextUtils.isEmpty(makePaymentFragmentArgs.order.promocode)) {
            list.add(OrderDetailItem("Promocode", makePaymentFragmentArgs.order.promocode!!))
            list.add(
                OrderDetailItem(
                    "Promocode Discount",
                    AppUtils.getAmountWithCurrency(makePaymentFragmentArgs.order.promocodeDiscount!!)
                )
            )
        }
        list.add(
            OrderDetailItem(
                "CGST",
                AppUtils.getAmountWithCurrency(makePaymentFragmentArgs.order.cgstAmount!!)
            )
        )
        list.add(
            OrderDetailItem(
                "SGST",
                AppUtils.getAmountWithCurrency(makePaymentFragmentArgs.order.sgstAmount!!)
            )
        )
        if (makePaymentFragmentArgs.order.deliveryCharge != "0") {
            list.add(
                OrderDetailItem(
                    "Delivery Charge",
                    AppUtils.getAmountWithCurrency(makePaymentFragmentArgs.order.deliveryCharge!!)
                )
            )
        }
        list.add(
            OrderDetailItem(
                "Net Amount",
                AppUtils.getAmountWithCurrency(makePaymentFragmentArgs.order.netAmount!!)
            )
        )
        list.add(
            OrderDetailItem(
                "Round off",
                AppUtils.getAmountWithCurrency(makePaymentFragmentArgs.order.roundOffAmount!!)
            )
        )
        list.add(
            OrderDetailItem(
                "Final Amount",
                AppUtils.getAmountWithCurrency(makePaymentFragmentArgs.order.netAmount!!)
            )
        )

        orderDetailListAdapter = OrderDetailListAdapter(list)
        payoutOrderDetailRecycler.apply {
            adapter = orderDetailListAdapter
            addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
            isNestedScrollingEnabled = false
        }
    }

    private fun populateWalletPay() {
        if (makePaymentFragmentArgs.walletBalance > 0) {
            payWalletCb.text =
                "You can pay ${AppUtils.getAmountWithCurrency(
                    makePaymentFragmentArgs.walletBalance
                        .toString()
                )} using eCommerceXPay"
            walletPayLayout.visibility = View.VISIBLE
        } else {
            walletPayLayout.visibility = View.GONE
        }
    }
}

class OrderDetailListAdapter(val list: ArrayList<OrderDetailItem>) :
    RecyclerView.Adapter<OrderDetailListAdapter.OrderDetailVh>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailVh {
        return OrderDetailVh(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_order_detail_breakup,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: OrderDetailVh, position: Int) {
        holder.bind(list[holder.adapterPosition])
    }

    inner class OrderDetailVh(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(orderDetailItem: OrderDetailItem) {
            itemView.orderDetailItemTitle.text = orderDetailItem.title
            itemView.orderDetailItemValue.text = orderDetailItem.value
        }
    }
}
