package com.egrocerx.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.egrocerx.R
import com.egrocerx.callback.OnRecyclerViewItemClick
import com.egrocerx.data.CategoryModel
import com.egrocerx.util.AppUtils
import kotlinx.android.synthetic.main.item_category_grid.view.*

class HomeCategoryItemGridVh(itemView: View, val callback: OnRecyclerViewItemClick<CategoryModel>) :
    RecyclerView.ViewHolder(itemView) {
    fun bind(model: CategoryModel) {

        itemView.itemCategoryGridName.text = model.CategoryName

        itemView.setOnClickListener {
            callback.onRecyclerItemClicked(adapterPosition, itemView, model)
        }

        Glide.with(itemView.context)
            .load(AppUtils.getFullImageUrl(model.CategoryIconPath))
            .thumbnail(0.25f)
            .apply(RequestOptions().encodeQuality(40))
            .placeholder(R.drawable.placeholder_grey_rounded)
            .into(itemView.itemCategoryGridImage)
    }
}