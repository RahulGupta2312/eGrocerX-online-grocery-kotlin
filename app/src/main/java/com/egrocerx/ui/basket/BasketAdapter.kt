package com.egrocerx.ui.basket

import android.annotation.SuppressLint
import android.text.TextUtils
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
import com.egrocerx.data.BasketModel
import com.egrocerx.util.AppPreference
import com.egrocerx.util.AppUtils
import kotlinx.android.synthetic.main.item_cart.view.*

class BasketAdapter(
    val
    callback: OnRecyclerViewItemClick<BasketModel>,
    val requestManager: RequestManager
) :
    RecyclerView.Adapter<BasketAdapter.BasketVH>() {


    var isClubMember = false
    val list = ArrayList<BasketModel>()
    private val layoutInflater: LayoutInflater =
        LayoutInflater.from(MyApplication.instance.getContext())

    init {
        isClubMember = AppPreference.getInstance().userData.check_premium == "1"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasketVH {
        return BasketVH(layoutInflater.inflate(R.layout.item_cart, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BasketVH, position: Int) {
        holder.bind(list[holder.adapterPosition])
    }

    override fun getItemId(position: Int): Long {
        return list[position].id.toLong()
    }

//    override fun onViewRecycled(holder: BasketVH) {
//        if (!TextUtils.isEmpty(list[holder.adapterPosition].ProductImagePathSmall)) {
//            requestManager.clear(holder.itemView.itemCartProductImage)
//        }
//    }

    fun updateProductQuantity(productCount: Int, selectedProductPos: Int) {

        if (productCount == 0) {
            list.removeAt(selectedProductPos)
            notifyItemRemoved(selectedProductPos)
        } else {
            list[selectedProductPos].Quantity = productCount.toString()

            // update total cost, discounted cost and total saved

            val totalCost =
                (list[selectedProductPos].Quantity.toInt()) * list[selectedProductPos].ProductMrp.toFloat()
            val discountedCost =
                (list[selectedProductPos].Quantity.toInt()) * list[selectedProductPos].ProductOfferprice.toFloat()
            val clubCost =
                (list[selectedProductPos].Quantity.toInt()) * list[selectedProductPos].ProductClubPrice.toFloat()
            val totalSaved = totalCost - discountedCost
            val clubSaved = totalCost - clubCost

            list[selectedProductPos].total_cost = totalCost.toString()
            list[selectedProductPos].discounted_cost = discountedCost.toString()
            list[selectedProductPos].club_cost = clubCost.toString()
            list[selectedProductPos].total_saved = totalSaved.toString()
            list[selectedProductPos].club_saved = clubSaved.toString()


            notifyItemChanged(selectedProductPos)
        }

    }

    fun addProducts(data: ArrayList<BasketModel>) {
        data.forEach {
            list.add(it)
            notifyItemInserted(list.size)

        }
    }

    fun clearDataSet() {
        list.clear()
        notifyDataSetChanged()
    }


    inner class BasketVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(model: BasketModel) {

            itemView.itemCartProductName.text = model.ProductName
            itemView.itemCartProductPackSize.text = model.packing_name
            itemView.itemCartProductQuantity.text = model.Quantity
            itemView.itemCartProductTotalCost.text =
                AppUtils.getAmountWithCurrency(model.total_cost)


            itemView.itemCartProductDiscountedCost.text = AppUtils.getAmountWithCurrency(
                if (isClubMember) model.club_cost else model.discounted_cost
            )


            if (model.total_saved.toFloat() > 0.0f) {
                itemView.itemCartTotalSaved.text = "${AppUtils.getAmountWithCurrency(
                    if (isClubMember) model.club_saved else model.total_saved
                )} \nsaved"
                itemView.itemCartTotalSaved.visibility = View.VISIBLE
                itemView.itemCartProductTotalCost.visibility = View.VISIBLE
            } else {
                itemView.itemCartTotalSaved.visibility = View.GONE
                itemView.itemCartProductTotalCost.visibility = View.INVISIBLE
            }

            if (!TextUtils.isEmpty(model.ProductImagePathSmall))
                requestManager.load(AppUtils.getFullImageUrl(model.ProductImagePathSmall))
                    .placeholder(R.drawable.placeholder_grey_rounded)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transform(RoundedCorners(8))
                    .into(itemView.itemCartProductImage)


            itemView.itemCartProductAddQuantity.setOnClickListener {
                callback.onRecyclerItemClicked(adapterPosition, it, model)
            }
            itemView.itemCartProductRemoveQuantity.setOnClickListener {
                callback.onRecyclerItemClicked(adapterPosition, it, model)
            }

        }
    }
}