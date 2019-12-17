package com.egrocerx.ui.categories.single

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.gson.JsonObject
import com.egrocerx.NavDashboardDirections
import com.egrocerx.R
import com.egrocerx.callback.OnRecyclerViewItemClick
import com.egrocerx.core.MyApplication
import com.egrocerx.data.ProductModel
import com.egrocerx.network.ApiManager
import com.egrocerx.network.ApiMode
import com.egrocerx.network.ApiResponse
import com.egrocerx.util.AppPreference
import com.egrocerx.util.AppUtils
import kotlinx.android.synthetic.main.item_product.view.*
import java.math.RoundingMode

class SingleCategoryAdapter(
    val requestManager: RequestManager,
    val callback: OnRecyclerViewItemClick<ProductModel>?
) :
    RecyclerView.Adapter<SingleCategoryAdapter.ProductVh>() {


    var lastSelectedPos = -1
    private val list = ArrayList<ProductModel>()
    private val layoutInflater: LayoutInflater =
        LayoutInflater.from(MyApplication.instance.getContext())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductVh {
        return ProductVh(layoutInflater.inflate(R.layout.item_product, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ProductVh, position: Int) {
        holder.bind(list[holder.adapterPosition])
    }

    override fun getItemId(position: Int): Long {
        return list[position].id.toLong()
    }


    fun addProducts(data: ArrayList<ProductModel>) {
        clearDataSet()

        list.addAll(data)
        notifyDataSetChanged()
    }

    fun getProduct(pos: Int): ProductModel {
        return list[pos]
    }

    fun updateProductQuantity(quantity: Int, pos: Int) {
        list[pos].CartQuantity = quantity
        notifyItemChanged(pos)
    }

    private fun clearDataSet() {
        if (list.isNotEmpty()) {
            list.clear()
            notifyDataSetChanged()
        }

    }

    inner class ProductVh(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener,
        ApiResponse {


        private lateinit var model: ProductModel

        @SuppressLint("SetTextI18n")
        fun bind(model: ProductModel) {
            this.model = model



            itemView.itemProductName.text = model.ProductName
            itemView.itemPackSize.text = model.packing_name
            itemView.itemProductCost.text =
                AppUtils.getAmountWithCurrency(model.ProductMrp.toString())
            itemView.itemProductOfferPrice.text =
                AppUtils.getAmountWithCurrency(model.ProductOfferprice.toString())

            if (model.ProductMrp - model.ProductOfferprice > 0) {
                itemView.itemProductPercentOff.text =
                    (100 - ((model.ProductOfferprice / model.ProductMrp) * 100)).toBigDecimal().setScale(
                        2,
                        RoundingMode.UP
                    ).toPlainString() + "% off"

                itemView.itemProductPercentOff.visibility = View.VISIBLE
            }


            if (model.PseudoStock == 0) {
                itemView.btnNotifyForProduct.visibility = View.VISIBLE
            } else {
                if (model.CartQuantity > 0) {
                    itemView.itemProductCartCount.text = model.CartQuantity.toString()
                    itemView.itemProductCartStateLayout.visibility = View.VISIBLE
                    itemView.itemProductBtnAddToCart.visibility = View.GONE
                } else {

                    itemView.itemProductCartStateLayout.visibility = View.GONE
                    itemView.itemProductBtnAddToCart.visibility = View.VISIBLE
                }
            }

            if (!TextUtils.isEmpty(model.ProductImagePathSmall)) {
                requestManager.load(AppUtils.getFullImageUrl(model.ProductImagePathSmall))
                    .thumbnail(0.25f)
                    .apply(
                        RequestOptions()
                            .encodeQuality(40)
                            .placeholder(R.drawable.placeholder_grey_rounded)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                    )
                    .transform(RoundedCorners(8))
                    .into(itemView.itemProductImage)
            }

            if (AppPreference.getInstance().userData.check_premium == "1") {
                itemView.itemProductClubPrice.text =
                    AppUtils.getAmountWithCurrency(model.ProductClubprice.toString())
                itemView.membershipPriceLayout.visibility = View.VISIBLE
            }

            itemView.itemProductImage.setOnClickListener {
                lastSelectedPos = adapterPosition
//                callback?.onRecyclerItemClicked(adapterPosition, it, model)
                val directions = NavDashboardDirections
                    .actionGlobalSingleProductFragment2(model.id)
                itemView.findNavController().navigate(directions)
            }
            itemView.itemProductBtnAddToCart.setOnClickListener(this)
            itemView.btnItemProductIncreaseQuantity.setOnClickListener(this)
            itemView.btnItemProductDecreaseQuantity.setOnClickListener(this)
            itemView.btnNotifyForProduct.setOnClickListener(this)


        }

        override fun onClick(v: View) {
            when (v.id) {
                R.id.itemProductBtnAddToCart -> {
                    requestAddToCartApi(model.id)
                }
                R.id.btnItemProductIncreaseQuantity -> {
                    requestAddToCartApi(model.id)
                }
                R.id.btnItemProductDecreaseQuantity -> {
                    requestRemoveFromCartApi(model.id)
                }
                R.id.btnNotifyForProduct -> {
                    callback?.onRecyclerItemClicked(adapterPosition, v, model)
                }
                else -> {
                }
            }
        }

        private fun requestAddToCartApi(id: Int) {
            val map = HashMap<String, Any>()
            map["customer_id"] = AppPreference.getInstance().userData.customer_id
            map["product_id"] = id.toString()

            ApiManager.getInstance().requestApi(ApiMode.ADD_TO_CART, map, true, this, "POST")
        }

        private fun requestRemoveFromCartApi(id: Int) {
            val map = HashMap<String, Any>()
            map["customer_id"] = AppPreference.getInstance().userData.customer_id
            map["product_id"] = id.toString()

            ApiManager.getInstance().requestApi(ApiMode.REMOVE_FROM_CART, map, true, this, "POST")
        }

        override fun onSuccess(jsonObject: JsonObject?, mode: ApiMode?) {
            when (mode) {

                ApiMode.ADD_TO_CART -> {

//                    productsAdapter.updateProductQuantity(jsonObject?.get("data")?.asInt!!, selectedProductPos)
                    model.CartQuantity = jsonObject?.get("data")?.asInt!!
                    notifyItemChanged(adapterPosition)

                }
                ApiMode.REMOVE_FROM_CART -> {
                    model.CartQuantity = jsonObject?.get("data")?.asInt!!
                    notifyItemChanged(adapterPosition)
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

    }
}