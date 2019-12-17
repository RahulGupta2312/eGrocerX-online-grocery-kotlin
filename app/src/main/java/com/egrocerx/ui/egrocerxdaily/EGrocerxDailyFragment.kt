package com.egrocerx.ui.egrocerxdaily


import android.os.Bundle
import android.view.View
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.egrocerx.NavDashboardDirections
import com.egrocerx.R
import com.egrocerx.base.BaseFragment
import com.egrocerx.callback.OnRecyclerViewItemClick
import com.egrocerx.data.ProductModel
import com.egrocerx.network.ApiManager
import com.egrocerx.network.ApiMode
import com.egrocerx.network.ApiResponse
import com.egrocerx.util.AppPreference
import com.egrocerx.util.AppUtils
import com.egrocerx.util.showSnackbar
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_egrocerx_daily.*


class EGrocerxDailyFragment : BaseFragment(), ApiResponse, OnRecyclerViewItemClick<ProductModel> {


    private lateinit var dailyAdapter: EGrocerxDailyAdapter

    override fun getLayoutId(): Int {
        return R.layout.fragment_egrocerx_daily
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dailyAdapter = EGrocerxDailyAdapter(this, Glide.with(this))
        requestProductListApi()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupProductList()
    }

    private fun setupProductList() {
        dailyProductsRecycler.apply {
            layoutManager = GridLayoutManager(this.context, 2)
            adapter = dailyAdapter
        }
    }

    private fun requestProductListApi() {
        val map = HashMap<String, Any>()
        map["customer_id"] = AppPreference.getInstance().userData.customer_id
        ApiManager.getInstance().requestApi(ApiMode.DAILY_PRODUCTS, map, false, this, "POST")
    }

    override fun onSuccess(jsonObject: JsonObject?, mode: ApiMode?) {
        when (mode) {
            ApiMode.DAILY_PRODUCTS -> {
                progressBar.visibility = View.GONE
                AppUtils.shortToast(jsonObject?.get("message")?.asString)

                val typeToken = object : TypeToken<ArrayList<ProductModel>>() {}.type
                val list =
                    Gson().fromJson<ArrayList<ProductModel>>(jsonObject?.get("data"), typeToken)


                dailyAdapter.addProducts(list)
            }
            ApiMode.NOTIFY_ME -> {
                showAlertDialog(jsonObject?.get("message")?.asString)
            }
        }

    }

    override fun onFailure(errorObject: JsonObject?, mode: ApiMode?) {
//        AppUtils.shortToast(errorObject?.get("message").toString())
        view?.showSnackbar(errorObject?.get("message").toString())
    }

    override fun onException(e: Exception?, mode: ApiMode?) {
        e?.printStackTrace()
//        AppUtils.showException()
        view?.showSnackbar("Something went wrong")

    }

    override fun onRecyclerItemClicked(pos: Int, view: View, data: ProductModel) {
        val directions: NavDirections?

        when (view.id) {
            R.id.itemProductSubscribe -> {
                directions =
                    EGrocerxDailyFragmentDirections.actionKarloffDailyFragment2ToKarloffDailyDetailFragment2(
                        data.ProductName,
                        data.id,
                        data.ProductMrp.toString(),
                        data.ProductSubscriptionPrice.toString(),
                        data.packing_name,
                        data.ProductImagePathSmall
                    )
                findNavController().navigate(directions)
            }
            R.id.btnNotifyForProduct -> {
                requestNotifyMeApi(data.id)
            }
            else -> {
                directions = NavDashboardDirections.actionGlobalSingleProductFragment2(data.id)
                findNavController().navigate(directions)

            }
        }
    }

    private fun requestNotifyMeApi(id: Int) {
        val map = HashMap<String, Any>()
        map["customer_id"] = AppPreference.getInstance().userData.customer_id
        map["product_id"] = id

        ApiManager.getInstance().requestApi(ApiMode.NOTIFY_ME, map, true, this, "POST")
    }

    override fun onPause() {
        ApiManager.getInstance().cancelApi(ApiMode.DAILY_PRODUCTS)
        super.onPause()
    }


}
