package com.egrocerx.ui.checkout


import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.egrocerx.R
import com.egrocerx.data.Order
import com.egrocerx.network.ApiManager
import com.egrocerx.network.ApiMode
import com.egrocerx.network.ApiResponse
import com.egrocerx.util.AppPreference
import com.egrocerx.util.AppUtils
import kotlinx.android.synthetic.main.fragment_checkout.*

class CheckoutFragment : Fragment(), ApiResponse {

    private var mView: View? = null

    private var totalAmount = 0.0
    private var totalGst = 0.0
    private var totalPayable = 0.0
    private var couponDiscount = 0.0

    private lateinit var checkoutFragmentArgs: CheckoutFragmentArgs


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkoutFragmentArgs = CheckoutFragmentArgs.fromBundle(arguments!!)
        requestInvoiceBreakupApi()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_checkout, container, false)
        }

        return mView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateUI()
        setupCouponInteractions()
        handlePlaceOrderClick()
    }

    private fun handlePlaceOrderClick() {
        btnPlaceOrder.setOnClickListener {
            requestPlaceOrderApi()
        }
    }

    private fun requestPlaceOrderApi() {
        val map = HashMap<String, Any>()
        map["customer_id"] = AppPreference.getInstance().userData.customer_id
        map["address_id"] = checkoutFragmentArgs.addressId
        map["promo_code"] = etCouponCode.text.toString()
        map["delivery_date"] = checkoutFragmentArgs.deliverySlot

        ApiManager.getInstance().requestApi(ApiMode.PLACE_ORDER, map, true, this, "POST")
    }

    private fun setupCouponInteractions() {
        handleApplyButtonClick()
    }

    private fun handleApplyButtonClick() {
        btnApplyCoupon.setOnClickListener {
            if (btnApplyCoupon.text.toString().equals("Remove", true)) {
                totalPayable += couponDiscount
                couponDiscount = 0.0
                populateUI()
            } else {
                val coupon = etCouponCode.text.toString()
                if (!TextUtils.isEmpty(coupon)) {
                    requestValidateCouponApi(coupon)
                }
            }
        }
    }

    private fun requestValidateCouponApi(coupon: String) {
        val map = HashMap<String, Any>()
        map["customer_id"] = AppPreference.getInstance().userData.customer_id
        map["coupon"] = coupon
        map["invoice_amount"] = totalPayable
        ApiManager.getInstance().requestApi(
            ApiMode.APPLY_COUPON, map, true,
            this, "POST"
        )
    }

    @SuppressLint("SetTextI18n")
    private fun populateUI() {
        totalPayable -= couponDiscount
        checkoutTotalAmount.text = AppUtils.getAmountWithCurrency(totalAmount.toString())
        checkoutTotalGst.text = AppUtils.getAmountWithCurrency(totalGst.toString())
        checkoutTotalPayable.text = AppUtils.getAmountWithCurrency(totalPayable.toString())
        checkoutAddress.text = checkoutFragmentArgs.addressText
        checkoutSlot.text = checkoutFragmentArgs.deliverySlot

        if (couponDiscount > 0) {
            etCouponCode.isEnabled = false
            btnApplyCoupon.text = "Remove"
            rowCouponDiscount.visibility = View.VISIBLE
            checkoutCouponDiscount.text = AppUtils.getAmountWithCurrency(couponDiscount.toString())
        } else {
            rowCouponDiscount.visibility = View.GONE
            etCouponCode.isEnabled = true
            etCouponCode.setText("")
            btnApplyCoupon.text = "Apply"
        }
    }


    private fun requestInvoiceBreakupApi() {
        val map = HashMap<String, Any>()
        map["customer_id"] = AppPreference.getInstance().userData.customer_id
        ApiManager.getInstance().requestApi(ApiMode.INVOICE_BREAKUP, map, true, this, "POST")
    }

    override fun onSuccess(jsonObject: JsonObject?, mode: ApiMode?) {

        when (mode) {
            ApiMode.APPLY_COUPON -> {
                couponDiscount = jsonObject?.get("data")?.asDouble!!
                populateUI()
            }
            ApiMode.INVOICE_BREAKUP -> {
                btnPlaceOrder.isEnabled = true
                btnApplyCoupon.isEnabled = true
                loadInvoiceBreakup(jsonObject?.getAsJsonObject("data"))
            }
            ApiMode.PLACE_ORDER -> {
                AppUtils.shortToast(jsonObject?.get("message")?.asString)
                val order =
                    Gson().fromJson(jsonObject?.get("data")?.asJsonObject, Order::class.java)
                val direction =
                    CheckoutFragmentDirections.actionCheckoutFragmentToMakePaymentFragment(order)
                direction.walletBalance = jsonObject?.get("wallet_payable")?.asInt!!
                findNavController().navigate(direction)
            }
        }
    }

    private fun loadInvoiceBreakup(data: JsonObject?) {
        totalAmount = data?.get("total_mrp")?.asDouble!!
        totalGst = data.get("gst")?.asDouble!!
        totalPayable = data.get("total_cost")?.asDouble!!
        couponDiscount = data.get("discount")?.asDouble!!

        populateUI()
    }

    override fun onFailure(errorObject: JsonObject?, mode: ApiMode?) {

        if (mode == ApiMode.PLACE_ORDER) {
            findNavController().popBackStack(R.id.landingScreenFragment, false)
            return
        }
        AppUtils.shortToast(errorObject?.get("message")?.asString)
    }

    override fun onException(e: Exception?, mode: ApiMode?) {
        e?.printStackTrace()
        AppUtils.showException()
    }


}
