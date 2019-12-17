package com.egrocerx.ui.subcategory


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.egrocerx.R
import com.egrocerx.callback.OnRecyclerViewItemClick
import com.egrocerx.data.BaseListItemModel
import com.egrocerx.data.ProductsModel
import com.egrocerx.data.SubCategoriesModel
import com.egrocerx.network.ApiManager
import com.egrocerx.network.ApiMode
import com.egrocerx.network.ApiResponse
import com.egrocerx.util.AppPreference
import com.egrocerx.util.AppUtils
import flexjson.JSONDeserializer
import kotlinx.android.synthetic.main.fragment_subcategory.*

class SubcategoryFragment : Fragment(), OnRecyclerViewItemClick<BaseListItemModel>, ApiResponse {


    private var mView: View? = null

    private lateinit var subcategoriesAdapter: SubCategoryAdapter

    private lateinit var subcategoryFragmentArgs: SubcategoryFragmentArgs


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subcategoryFragmentArgs = SubcategoryFragmentArgs.fromBundle(arguments!!)
        subcategoriesAdapter = SubCategoryAdapter(this)
        requestSubcategoriesApi()
    }

    private fun requestSubcategoriesApi() {

        val map = HashMap<String, Any>()
        map["category_id"] = subcategoryFragmentArgs.categoryId
        map["customer_id"] = AppPreference.getInstance().userData.customer_id
        ApiManager.getInstance().requestApi(ApiMode.SUBCATEGORIES, map, false, this, "POST")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (mView == null)
            mView = inflater.inflate(R.layout.fragment_subcategory, container, false)

        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        subCategoryRecycler.apply {
            isNestedScrollingEnabled = false
            adapter = subcategoriesAdapter
        }
    }

    override fun onRecyclerItemClicked(pos: Int, view: View, data: BaseListItemModel) {
    }

    override fun onSuccess(jsonObject: JsonObject?, mode: ApiMode?) {
        progressBar.visibility = View.GONE
//        val typeToken = object : TypeToken<ArrayList<BaseListItemModel>>() {}.type
////        val list = Gson().fromJson<ArrayList<BaseListItemModel>>(jsonObject?.getAsJsonArray("data"), typeToken)
        val list = JSONDeserializer<List<Map<String, Array<Any>>>>()
            .deserialize(jsonObject?.getAsJsonArray("data").toString())

        list.forEachIndexed { index, map ->
            run {
                if (index == 0) {
                    subcategoriesAdapter.add(
                        Gson().fromJson(
                            Gson().toJson(map),
                            SubCategoriesModel::class.java
                        )
                    )

                } else {
                    subcategoriesAdapter.add(
                        Gson().fromJson(
                            Gson().toJson(map),
                            ProductsModel::class.java
                        )
                    )
                }
            }
        }
    }

    override fun onFailure(errorObject: JsonObject?, mode: ApiMode?) {

    }

    override fun onException(e: Exception?, mode: ApiMode?) {
        e?.printStackTrace()
        AppUtils.showException()
    }


}
