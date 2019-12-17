package com.egrocerx.viewholder

import android.view.View
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.egrocerx.NavDashboardDirections
import com.egrocerx.R
import com.egrocerx.callback.OnRecyclerViewItemClick
import com.egrocerx.data.SubCategoriesModel
import com.egrocerx.data.SubCategoryModel
import com.egrocerx.ui.subcategory.SubcategoryGridItemAdapter
import com.egrocerx.util.ItemDecorationAlbumColumn
import kotlinx.android.synthetic.main.item_categories_recycler.view.*

class SubcategoryGridVh(itemView: View) :
    RecyclerView.ViewHolder(itemView), OnRecyclerViewItemClick<SubCategoryModel> {

    override fun onRecyclerItemClicked(pos: Int, view: View, data: SubCategoryModel) {
        val dir = NavDashboardDirections.actionGlobalSingleCategoryFragment2(
            data.id.toString(),
            data.SubcategoryL1Name
        )
        view.findNavController().navigate(dir)
    }

    fun bind(model: SubCategoriesModel) {

        val subcategoryAdapter = SubcategoryGridItemAdapter(this)
        subcategoryAdapter.addAll(model.subcategories)

        itemView.itemCategoriesRecyclerGrid.apply {
            this.isNestedScrollingEnabled = false
            layoutManager = GridLayoutManager(this.context, 3)
            adapter = subcategoryAdapter
            addItemDecoration(
                ItemDecorationAlbumColumn(
                    itemView.resources.getDimensionPixelSize(R.dimen.grid_divider_width)
                    , 3
                )
            )
        }
    }
}