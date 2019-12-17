package com.egrocerx.ui.offers

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.egrocerx.R
import com.egrocerx.core.MyApplication
import com.egrocerx.data.OfferModel
import com.egrocerx.util.AppUtils
import kotlinx.android.synthetic.main.item_offer.view.*

class OffersAdapter(val requestManager: RequestManager) : RecyclerView.Adapter<OffersAdapter.OfferVh>() {

    private val list = ArrayList<OfferModel>()
    private val layoutInflater = LayoutInflater.from(MyApplication.instance.getContext())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferVh {
        return OfferVh(layoutInflater.inflate(R.layout.item_offer, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: OfferVh, position: Int) {
        holder.bind(list[holder.adapterPosition])
    }

    override fun getItemId(position: Int): Long {
        return list[position].id.toLong()
    }

    override fun onViewRecycled(holder: OfferVh) {
        if (!TextUtils.isEmpty(holder.model.ImagePath)) {
            requestManager.clear(holder.itemView.itemOfferImage)
        }
    }

    fun addAll(data: java.util.ArrayList<OfferModel>) {
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


    inner class OfferVh(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var model: OfferModel
        fun bind(model: OfferModel) {
            this.model = model
            itemView.itemOfferTitle.text = model.OfferTitle
            itemView.itemOfferDescription.text = model.Description
            if (!TextUtils.isEmpty(model.ImagePath)) {
                itemView.itemOfferImage.visibility = View.VISIBLE
                requestManager.load(AppUtils.getFullImageUrl(model.ImagePath))
                    .placeholder(R.drawable.placeholder_grey_rounded)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(itemView.itemOfferImage)
            }
        }
    }
}