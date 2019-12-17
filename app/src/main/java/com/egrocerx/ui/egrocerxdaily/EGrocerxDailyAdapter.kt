package com.egrocerx.ui.egrocerxdaily

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.egrocerx.R
import com.egrocerx.callback.OnRecyclerViewItemClick
import com.egrocerx.core.MyApplication
import com.egrocerx.data.ProductModel
import com.egrocerx.util.AppUtils
import kotlinx.android.synthetic.main.item_product_subscribe.view.*

class EGrocerxDailyAdapter(
    val callback: OnRecyclerViewItemClick<ProductModel>,
    val requestManager: RequestManager
) : RecyclerView.Adapter<EGrocerxDailyAdapter.DailyVh>() {


    val list = ArrayList<ProductModel>()

    val layoutInflater: LayoutInflater = LayoutInflater.from(MyApplication.instance.getContext())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyVh {
        return DailyVh(layoutInflater.inflate(R.layout.item_product_subscribe, parent, false))
    }

    override fun getItemId(position: Int): Long {
        return list[position].id.toLong()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: DailyVh, position: Int) {
        holder.bind(list[holder.adapterPosition])
    }

    fun addProducts(data: ArrayList<ProductModel>) {
        clearDataSet()
        data.forEach {
            list.add(it)
            notifyItemInserted(list.size)
        }
    }

    private fun clearDataSet() {
        list.clear()
        notifyDataSetChanged()
    }


    inner class DailyVh(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(model: ProductModel) {
            itemView.itemProductName.text = model.ProductName
            itemView.itemProductCost.text =
                "${itemView.resources.getString(R.string.currency_symbol)}${model.ProductSubscriptionPrice}"
            itemView.itemPackSize.text = model.packing_name

            if(model.PseudoStock==0){
                itemView.btnNotifyForProduct.visibility=View.VISIBLE
                itemView.itemProductSubscribe.visibility=View.GONE
            }

            requestManager.load(AppUtils.getFullImageUrl(model.ProductImagePathSmall))
                .placeholder(R.drawable.placeholder_grey_rounded)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(RoundedCorners(8))
                .into(itemView.itemProductImage)


            itemView.itemBuyOnce.setOnClickListener{
                callback.onRecyclerItemClicked(adapterPosition, it, model)
            }
            itemView.itemProductSubscribe.setOnClickListener {
                callback.onRecyclerItemClicked(adapterPosition, it, model)
            }
            itemView.btnNotifyForProduct.setOnClickListener {
                callback.onRecyclerItemClicked(adapterPosition, it, model)
            }
        }
    }
}