package com.egrocerx.ui.search


import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import com.egrocerx.NavDashboardDirections
import com.egrocerx.R
import com.egrocerx.base.BaseFragment
import com.egrocerx.callback.OnRecyclerViewItemClick
import com.egrocerx.data.ProductModel
import com.egrocerx.network.ApiManager
import com.egrocerx.network.ApiMode
import com.egrocerx.network.ApiResponse
import com.egrocerx.ui.categories.single.SingleCategoryAdapter
import com.egrocerx.util.AppPreference
import com.egrocerx.util.AppUtils
import com.egrocerx.util.showKeyboard
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : BaseFragment(), OnRecyclerViewItemClick<ProductModel>, ApiResponse {


    private lateinit var productsAdapter: SingleCategoryAdapter
    override fun getLayoutId(): Int {
        return R.layout.fragment_search
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productsAdapter = SingleCategoryAdapter(Glide.with(this), this)
        productsAdapter.setHasStableIds(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        handleSearchClick()
    }

    private fun handleSearchClick() {
        searchBar.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchBar.clearFocus()
                searchBar.showKeyboard(false)
                if (TextUtils.isEmpty(searchBar.text.toString())) {
                   AppUtils.shortToast("Enter a valid search text")
                } else {
                    requestSearchApi()
                }
            }
            true
        }
    }

    private fun requestSearchApi() {
        val map = HashMap<String, Any>()
        map["customer_id"] = AppPreference.getInstance().userData.customer_id
        map["keyword"] = "'%" + searchBar.text.toString() + "%'"

        ApiManager.getInstance().requestApi(ApiMode.SEARCH, map, true, this, "POST")
    }

    private fun setupRecyclerView() {
        searchResultsRecycler.apply {
            layoutManager = GridLayoutManager(this.context, 2)
            adapter = productsAdapter
        }
    }

    override fun onRecyclerItemClicked(pos: Int, view: View, data: ProductModel) {
        val directions = NavDashboardDirections
            .actionGlobalSingleProductFragment2(data.id)
        findNavController().navigate(directions)
    }

    override fun onSuccess(jsonObject: JsonObject?, mode: ApiMode?) {
        productsAdapter.addProducts(ProductModel.getProductsList(jsonObject))
        AppUtils.shortToast(jsonObject?.get("message")?.asString)
    }

    override fun onFailure(errorObject: JsonObject?, mode: ApiMode?) {

    }

    override fun onException(e: Exception?, mode: ApiMode?) {
        e?.printStackTrace()
        AppUtils.showException()
    }


}
