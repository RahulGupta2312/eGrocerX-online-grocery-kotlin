package com.egrocerx.viewholder


import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.egrocerx.R
import com.egrocerx.callback.OnRecyclerViewItemClick
import com.egrocerx.data.CategoryModel
import com.egrocerx.data.HomeCategoriesModel
import com.egrocerx.ui.home.HomeCategoryGridAdapter
import com.egrocerx.util.ItemDecorationAlbumColumn
import kotlinx.android.synthetic.main.item_categories_recycler.view.*

class HomeCategoryGridVh(itemView: View, val callback:OnRecyclerViewItemClick<Any>) :
    RecyclerView.ViewHolder(itemView), OnRecyclerViewItemClick<CategoryModel> {

    override fun onRecyclerItemClicked(pos: Int, view: View, data: CategoryModel) {
        callback.onRecyclerItemClicked(pos,view,data)
    }

    fun bind(model: HomeCategoriesModel) {

        val homeCategoryGridAdapter = HomeCategoryGridAdapter(this)
        homeCategoryGridAdapter.addAll(model.categories)

        itemView.itemCategoriesRecyclerGrid.apply {
            layoutManager = GridLayoutManager(this.context, 3)
            adapter = homeCategoryGridAdapter
            addItemDecoration(
                ItemDecorationAlbumColumn(
                    itemView.resources.getDimensionPixelSize(R.dimen.grid_divider_width)
                    , 3
                )
            )
        }
    }
}