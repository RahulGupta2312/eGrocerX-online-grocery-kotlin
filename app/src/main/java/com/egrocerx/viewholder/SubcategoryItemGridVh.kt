package com.egrocerx.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.egrocerx.R
import com.egrocerx.callback.OnRecyclerViewItemClick
import com.egrocerx.data.SubCategoryModel
import com.egrocerx.util.AppUtils
import kotlinx.android.synthetic.main.item_category_grid.view.*

class SubcategoryItemGridVh(
    itemView: View,
    val callback: OnRecyclerViewItemClick<SubCategoryModel>
) :
    RecyclerView.ViewHolder(itemView) {
    fun bind(model: SubCategoryModel) {

        itemView.itemCategoryGridName.text = model.SubcategoryL1Name

        itemView.setOnClickListener {
            callback.onRecyclerItemClicked(adapterPosition, itemView, model)
        }

        Glide.with(itemView.context)
            .load(AppUtils.getFullImageUrl(model.SubCategoryIconPath))
            .thumbnail(0.25f)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.placeholder_grey_rounded)
            .transform(RoundedCorners(8))
            .into(itemView.itemCategoryGridImage)
    }
}