package com.egrocerx.ui.basket


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.egrocerx.R
import com.egrocerx.callback.OnRecyclerViewItemClick
import com.egrocerx.data.BasketModel
import com.egrocerx.network.ApiManager
import com.egrocerx.network.ApiMode
import com.egrocerx.network.ApiResponse
import com.egrocerx.util.AppPreference
import com.egrocerx.util.AppUtils
import kotlinx.android.synthetic.main.fragment_basket.*
import kotlinx.android.synthetic.main.item_sub_toolbar.*

class BasketFragment : Fragment(), ApiResponse, OnRecyclerViewItemClick<BasketModel> {

    private var selectedProductPos: Int = -1
    private lateinit var basketAdapter: BasketAdapter
    private var mView: View? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        basketAdapter = BasketAdapter(this, Glide.with(this))
    }

    override fun onResume() {
        super.onResume()
        requestBasketListApi()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_basket, container, false)
        }

        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWidgets()
    }

    private fun setupWidgets() {
        setupBasketRecycler()
        subToolbarTitle.text = "Basket Items"

        btnProceedToCheckout.setOnClickListener {
            findNavController().navigate(R.id.action_basketFragment2_to_nav_graph_payment)
        }
    }


    private fun requestBasketListApi() {
        basketAdapter.clearDataSet()
        val map = HashMap<String, Any>()
        map["customer_id"] = AppPreference.getInstance().userData.customer_id
        ApiManager.getInstance().requestApi(ApiMode.CART, map, false, this, "POST")

    }

    private fun setupBasketRecycler() {

        basketRecycler.apply {
            isNestedScrollingEnabled = false
            adapter = basketAdapter
            addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
        }

    }

    private fun requestRemoveFromCartApi(id: String) {
        val map = HashMap<String, Any>()
        map["customer_id"] = AppPreference.getInstance().userData.customer_id
        map["product_id"] = id

        ApiManager.getInstance().requestApi(ApiMode.REMOVE_FROM_CART, map, true, this, "POST")
    }

    private fun requestAddToCartApi(id: String) {
        val map = HashMap<String, Any>()
        map["customer_id"] = AppPreference.getInstance().userData.customer_id
        map["product_id"] = id

        ApiManager.getInstance().requestApi(ApiMode.ADD_TO_CART, map, true, this, "POST")
    }

    override fun onSuccess(jsonObject: JsonObject?, mode: ApiMode?) {

        when (mode) {
            ApiMode.CART -> {
                progressBar.visibility = View.GONE
                AppUtils.shortToast(jsonObject?.get("message")?.asString)

                val typeToken = object : TypeToken<ArrayList<BasketModel>>() {}.type
                val list = Gson().fromJson<ArrayList<BasketModel>>(
                    jsonObject?.getAsJsonArray("data"),
                    typeToken
                )
                basketAdapter.addProducts(list)


            }
            ApiMode.ADD_TO_CART -> {
                basketAdapter.updateProductQuantity(
                    jsonObject?.get("data")?.asInt!!,
                    selectedProductPos
                )

            }
            ApiMode.REMOVE_FROM_CART -> {
                basketAdapter.updateProductQuantity(
                    jsonObject?.get("data")?.asInt!!,
                    selectedProductPos
                )
            }
        }

        val finalTotal=jsonObject?.getAsJsonObject("summary")
            ?.get("total_mrp")?.asString
        val gst=jsonObject?.getAsJsonObject("summary")
            ?.get("gst")?.asString
        val totalBill=jsonObject?.getAsJsonObject("summary")
            ?.get("total_cost")?.asString
        val discount=jsonObject?.getAsJsonObject("summary")
            ?.get("discount")?.asString
        updateBasketInformation(finalTotal,gst,totalBill,discount)
    }

    private fun updateBasketInformation(finalTotal: String?, gstAmount: String?,
                                        totalBill: String?, discount:String?) {

        if (basketAdapter.itemCount > 0) {
//            basketAdapter.list.forEach {
//
//                if (AppPreference.getInstance().userData.check_premium == "1") {
//                    totalBill += it.club_cost.toDouble()
//
//                    finalTotal += ((it.club_cost.toDouble() * 100) / (100 +
//                            (it.ProductCgst + it.ProductSgst)))
//                } else {
//                    totalBill += it.discounted_cost.toDouble()
//
//                    finalTotal += ((it.discounted_cost.toDouble() * 100) / (100 +
//                            (it.ProductCgst + it.ProductSgst)))
//                }
//            }
//            totalBill = totalBill.toFloat().formatDecimal(2)
//            finalTotal = finalTotal.toFloat().formatDecimal(2)
//            totalBill.toBigDecimal().setScale(2, RoundingMode.UP)
//                .toDouble()
//            finalTotal.toBigDecimal().setScale(2, RoundingMode.UP)
//                .toDouble()

//            gstAmount = (totalBill - finalTotal).toFloat().formatDecimal(2)

            basketBillAmount.text = "${getString(R.string.currency_symbol)} $finalTotal"
            basketGstAmount.text = "${getString(R.string.currency_symbol)} $gstAmount"
            basketTotalAmount.text = "${getString(R.string.currency_symbol)} $totalBill"
            basketTotalDiscount.text = "${getString(R.string.currency_symbol)} $discount"
            basketInfoLayout.visibility = View.VISIBLE
            emptyCartView.visibility = View.GONE
        } else {
            basketInfoLayout.visibility = View.GONE
            emptyCartView.visibility = View.VISIBLE
        }

    }

    override fun onFailure(errorObject: JsonObject?, mode: ApiMode?) {
        AppUtils.shortToast(errorObject?.get("message")?.asString)
    }

    override fun onException(e: Exception?, mode: ApiMode?) {
        e?.printStackTrace()
        AppUtils.showException()
    }

    override fun onRecyclerItemClicked(pos: Int, view: View, data: BasketModel) {
        selectedProductPos = pos
        when (view.id) {
            R.id.itemCartProductAddQuantity -> {
                requestAddToCartApi(data.ProductId)
            }

            R.id.itemCartProductRemoveQuantity -> {
                requestRemoveFromCartApi(data.ProductId)
            }
            else -> {

            }
        }
    }
}
