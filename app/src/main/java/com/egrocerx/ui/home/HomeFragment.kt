package com.egrocerx.ui.home


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.egrocerx.NavDashboardDirections
import com.egrocerx.R
import com.egrocerx.callback.OnRecyclerViewItemClick
import com.egrocerx.data.*
import com.egrocerx.network.ApiManager
import com.egrocerx.network.ApiMode
import com.egrocerx.network.ApiResponse
import com.egrocerx.util.showSnackbar
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*

class HomeFragment : Fragment(), ApiResponse, OnRecyclerViewItemClick<Any> {


    private lateinit var homeAdapter: HomeAdapter

    private var mView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeAdapter = HomeAdapter(this)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (mView == null)
            mView = inflater.inflate(R.layout.fragment_home, container, false)
        return mView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()
    }

    private fun setupRecyclerView() {

        homeRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = homeAdapter
            isNestedScrollingEnabled = false
        }

    }

    private fun requestHomeApi() {
        if (homeAdapter.itemCount == 0)
            ApiManager.getInstance().requestApi(ApiMode.HOME, null, false, this, "GET")

    }

    override fun onSuccess(jsonObject: JsonObject?, mode: ApiMode?) {

        progressBar.visibility = View.GONE
        val list = ArrayList<BaseListItemModel>()


        // extract top-slider
        var typeToken = object : TypeToken<ArrayList<ItemSlider>>() {}.type
        var slider = Gson().fromJson<ArrayList<ItemSlider>>(
            jsonObject?.getAsJsonObject("data")
                ?.getAsJsonArray("top_slider"), typeToken
        )

        list.add(HomeSliderModel(slider))

        // extract delivery slot

        list.add(
            Gson().fromJson(
                jsonObject?.getAsJsonObject("data")?.getAsJsonObject("delivery_slots"),
                HomeDeliverySlotModel::class.java
            )
        )

        // extract categories
        list.add(HomeTitleModel("Shop by Category"))
        typeToken = object : TypeToken<ArrayList<CategoryModel>>() {}.type
        val categories = Gson().fromJson<ArrayList<CategoryModel>>(
            jsonObject?.getAsJsonObject("data")
                ?.getAsJsonArray("categories"), typeToken
        )
        list.add(HomeCategoriesModel("", categories))

        // extract banners
        typeToken = object : TypeToken<ArrayList<HomeBannerModel>>() {}.type
        val banners = Gson().fromJson<ArrayList<HomeBannerModel>>(
            jsonObject?.getAsJsonObject("data")
                ?.getAsJsonArray("banners"), typeToken
        )

        list.addAll(banners)

        // extract mid-slider
        typeToken = object : TypeToken<ArrayList<ItemSlider>>() {}.type
        slider = Gson().fromJson<ArrayList<ItemSlider>>(
            jsonObject?.getAsJsonObject("data")
                ?.getAsJsonArray("mid_slider"), typeToken
        )
        list.add(HomeSliderModel(slider))
        homeAdapter.addAll(list)

    }

    override fun onFailure(errorObject: JsonObject?, mode: ApiMode?) {
        view?.showSnackbar("Something went wrong")
//        AppUtils.shortToast("Something went wrong")
    }

    override fun onException(e: Exception?, mode: ApiMode?) {
        e?.printStackTrace()
        view?.showSnackbar("Something went wrong")
//        AppUtils.showException()
    }

    override fun onRecyclerItemClicked(pos: Int, view: View, data: Any) {
        if (data is CategoryModel) {
            val dir = NavDashboardDirections.actionGlobalSubcategoryFragment2(
                data.id, data.CategoryName
            )
            findNavController().navigate(dir)
        }
    }

    override fun onResume() {
        super.onResume()
        requestHomeApi()
    }

    override fun onPause() {
        ApiManager.getInstance().cancelApi(ApiMode.HOME)
        super.onPause()
    }

}
