package com.egrocerx.ui.categories.single


import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.egrocerx.R
import com.egrocerx.base.BaseFragment
import com.egrocerx.callback.OnRecyclerViewItemClick
import com.egrocerx.data.ProductModel
import com.egrocerx.network.ApiManager
import com.egrocerx.network.ApiMode
import com.egrocerx.network.ApiResponse
import com.egrocerx.util.AppPreference
import com.egrocerx.util.AppUtils
import kotlinx.android.synthetic.main.fragment_single_category.*

class SingleCategoryFragment : BaseFragment(), OnRecyclerViewItemClick<ProductModel>, ApiResponse {


    private lateinit var categoryArguments: SingleCategoryFragmentArgs

    private lateinit var productsAdapter: SingleCategoryAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        categoryArguments = SingleCategoryFragmentArgs.fromBundle(arguments!!)
        productsAdapter = SingleCategoryAdapter(Glide.with(this), this)
        productsAdapter.setHasStableIds(true)
        requestProductsApi()
    }

    override fun getLayoutId(): Int = R.layout.fragment_single_category

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupProductListRecycler()
    }

    private fun setupProductListRecycler() {
        productsRecycler.apply {
            isNestedScrollingEnabled = false
            layoutManager = GridLayoutManager(this.context, 2)
            adapter = productsAdapter
        }

    }

    override fun onRecyclerItemClicked(pos: Int, view: View, data: ProductModel) {
        requestNotifyMeApi(data.id)
    }

    private fun requestNotifyMeApi(id: Int) {
        val map = HashMap<String, Any>()
        map["customer_id"] = AppPreference.getInstance().userData.customer_id
        map["product_id"] = id.toString()

        ApiManager.getInstance().requestApi(ApiMode.NOTIFY_ME, map, true, this, "POST")
    }

    private fun requestProductsApi() {
        val map = HashMap<String, Any>()
        map["customer_id"] = AppPreference.getInstance().userData.customer_id
        map["subcategory_id"] = categoryArguments.categoryId

        ApiManager.getInstance().requestApi(ApiMode.PRODUCTS, map, false, this, "POST")

    }

    override fun onSuccess(jsonObject: JsonObject?, mode: ApiMode?) {
        updateProgressBar(View.GONE)


        when (mode) {
            ApiMode.PRODUCTS -> {
                AppUtils.shortToast(jsonObject?.get("message")?.asString)
                val typeToken = object : TypeToken<ArrayList<ProductModel>>() {}.type
                val list = Gson().fromJson<ArrayList<ProductModel>>(
                    jsonObject?.getAsJsonArray("data"),
                    typeToken
                )

                productsAdapter.addProducts(list)
            }

            ApiMode.NOTIFY_ME -> {
                showAlertDialog(jsonObject?.get("message")?.asString)
            }
        }
    }

    override fun onFailure(errorObject: JsonObject?, mode: ApiMode?) {
        AppUtils.shortToast(errorObject?.get("message")?.asString)
    }

    override fun onException(e: Exception?, mode: ApiMode?) {
        e?.printStackTrace()
        AppUtils.showException()
    }

    private fun updateProgressBar(visibility: Int) {
        progressBar.visibility = visibility

    }
}
