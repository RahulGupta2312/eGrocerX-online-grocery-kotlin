package com.egrocerx.viewholder

import android.view.View
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.egrocerx.NavDashboardDirections
import com.egrocerx.R
import com.egrocerx.data.HomeBannerModel
import com.egrocerx.ui.categories.single.SingleCategoryAdapter
import com.egrocerx.util.AppUtils
import kotlinx.android.synthetic.main.item_banner.view.*

class HomeBannerVh(itemView: View) : RecyclerView.ViewHolder(itemView)/*, ApiResponse*/ {


    private var productsAdapter: SingleCategoryAdapter =
        SingleCategoryAdapter(Glide.with(itemView), null)

    fun bind(model: HomeBannerModel) {

        productsAdapter.addProducts(model.products)
        // setup recyclerview grid
        itemView.bannerRelatedProductsRecycler.apply {
            adapter = productsAdapter
            isNestedScrollingEnabled = false
        }
        // load banner image
        Glide.with(itemView.context)
            .load(AppUtils.getFullImageUrl(model.ImgPath))
            .thumbnail(0.25f)
            .placeholder(R.drawable.placeholder_grey_rounded)
            .apply(RequestOptions().encodeQuality(30).diskCacheStrategy(DiskCacheStrategy.ALL))
            .into(itemView.bannerImage)

        // click listener
        itemView.bannerImage.setOnClickListener {
            val dir = NavDashboardDirections.actionGlobalSingleCategoryFragment2(
                model.SubCategoryId,
                model.SubCategoryName
            )
            itemView.findNavController().navigate(dir)
        }
    }

//    private fun loadRandomFourProducts(id: String) {
//        itemView.bannerRelatedProductsRecycler.visibility = View.GONE
//        val map = HashMap<String, Any>()
//        map["customer_id"] = AppPreference.getInstance().userData.customer_id
//        map["subcategory_id"] = id
//        map["limit"] = 4
//
////        ApiManager.getInstance().requestApi(ApiMode.PRODUCTS, map, false, this, "POST")
//
//        AndroidNetworking.post(ApiManager.BASE_URL + "products")
//            .addApplicationJsonBody(map)
//            .build()
//            .getAsJSONObject(object : JSONObjectRequestListener {
//                override fun onResponse(response: JSONObject?) {
//                    if (response == null)
//                        return
//                    if (response.getInt("status") == 200) {
//                        val typeToken = object : TypeToken<ArrayList<ProductModel>>() {}.type
//                        val list = Gson().fromJson<ArrayList<ProductModel>>(
//                            response.getJSONArray("data").toString(),
//                            typeToken
//                        )
//
//                        productsAdapter.addProducts(list)
//                        itemView.bannerRelatedProductsRecycler.visibility = View.VISIBLE
//                    }
//                }
//
//                override fun onError(anError: ANError?) {
//                    anError?.printStackTrace()
//                }
//
//            })
//    }
//
//    override fun onSuccess(jsonObject: JsonObject?, mode: ApiMode?) {
//        when (mode) {
//            ApiMode.PRODUCTS -> {
//                itemView.progressBar.visibility = View.GONE
//                val typeToken = object : TypeToken<ArrayList<ProductModel>>() {}.type
//                val list = Gson().fromJson<ArrayList<ProductModel>>(
//                    jsonObject?.getAsJsonArray("data"),
//                    typeToken
//                )
//
//                productsAdapter.addProducts(list)
//            }
//
////            ApiMode.ADD_TO_CART -> {
////                productsAdapter.updateProductQuantity(
////                    jsonObject?.get("data")?.asInt!!,
////                    productsAdapter.lastSelectedPos
////                )
////
////            }
////            ApiMode.REMOVE_FROM_CART -> {
////                productsAdapter.updateProductQuantity(
////                    jsonObject?.get("data")?.asInt!!,
////                    productsAdapter.lastSelectedPos
////                )
////            }
//
//        }
//    }
//
//    override fun onFailure(errorObject: JsonObject?, mode: ApiMode?) {
//
//    }
//
//    override fun onException(e: Exception?, mode: ApiMode?) {
//
//    }
}