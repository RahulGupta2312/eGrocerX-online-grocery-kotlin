package com.egrocerx.ui.categories


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.egrocerx.NavDashboardDirections
import com.egrocerx.R
import com.egrocerx.callback.OnRecyclerViewItemClick
import com.egrocerx.data.CategoryModel
import com.egrocerx.data.Subcategory
import com.egrocerx.network.ApiManager
import com.egrocerx.network.ApiMode
import com.egrocerx.network.ApiResponse
import com.egrocerx.util.showSnackbar
import kotlinx.android.synthetic.main.fragment_categories.*

class CategoriesFragment : Fragment(), ApiResponse, OnRecyclerViewItemClick<Subcategory> {


    private lateinit var categoriesAdapter: CategoriesAdapter

    private var mView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        categoriesAdapter = CategoriesAdapter(Glide.with(this), this)
        categoriesAdapter.setHasStableIds(true)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (mView == null)
            mView = inflater.inflate(R.layout.fragment_categories, container, false)

        return mView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // load views
        setupCategoriesList()

    }

    private fun setupCategoriesList() {

        categoriesRecycler.apply {
            isNestedScrollingEnabled = false
            adapter = categoriesAdapter
            addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
        }
    }

    private fun requestCategoriesApi() {
        ApiManager.getInstance().requestApi(
            ApiMode.CATEGORIES, null, false,
            this, "GET"
        )
    }


    override fun onSuccess(jsonObject: JsonObject?, mode: ApiMode?) {
        progressBar.visibility = GONE

        val typeToken = object : TypeToken<ArrayList<CategoryModel>>() {}.type
        val list = Gson().fromJson<ArrayList<CategoryModel>>(
            jsonObject?.getAsJsonArray("data"),
            typeToken
        )
        updateCategoriesAdapter(list)
    }

    override fun onFailure(errorObject: JsonObject?, mode: ApiMode?) {
//        AppUtils.shortToast(errorObject?.get("message")?.asString)
        view?.showSnackbar(errorObject?.get("message")?.asString!!)
    }

    override fun onException(e: Exception?, mode: ApiMode?) {
        e?.printStackTrace()
//        AppUtils.showException()
        view?.showSnackbar("Something went wrong")
    }

    private fun updateCategoriesAdapter(categories: ArrayList<CategoryModel>) {
        categoriesAdapter.addData(categories)
    }


    override fun onRecyclerItemClicked(pos: Int, view: View, data: Subcategory) {

        val dir =
            NavDashboardDirections.actionGlobalSingleCategoryFragment2(
                data.id,
                data.SubcategoryL1Name
            )

        findNavController().navigate(dir)
    }

    override fun onResume() {
        super.onResume()
        requestCategoriesApi()
    }

    override fun onPause() {
        ApiManager.getInstance().cancelApi(ApiMode.CATEGORIES)
        super.onPause()
    }


}
