package com.egrocerx.viewholder

import android.graphics.Color
import android.view.View
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.egrocerx.NavDashboardDirections
import com.egrocerx.R
import com.egrocerx.callback.OnRecyclerViewItemClick
import com.egrocerx.data.ProductModel
import com.egrocerx.data.ProductsModel
import com.egrocerx.ui.categories.single.SingleCategoryAdapter
import com.egrocerx.util.ItemDecorationAlbumColumn
import kotlinx.android.synthetic.main.item_products_recycler.view.*
import kotlinx.android.synthetic.main.item_sub_toolbar.view.*

class ProductGridVh(itemView: View) :
    RecyclerView.ViewHolder(itemView), OnRecyclerViewItemClick<ProductModel> {

    override fun onRecyclerItemClicked(pos: Int, view: View, data: ProductModel) {

        val dir=NavDashboardDirections.actionGlobalSingleProductFragment2(data.id)
        view.findNavController().navigate(dir)
    }

    fun bind(model: ProductsModel) {
        val subcategoryAdapter = SingleCategoryAdapter(Glide.with(itemView), this)
        subcategoryAdapter.addProducts(model.products)

        itemView.subToolbarTitle.text = model.title
        itemView.cardView.setCardBackgroundColor(Color.parseColor(model.background))
        itemView.itemProductsRecyclerGrid.apply {
            this.isNestedScrollingEnabled = false
            layoutManager = GridLayoutManager(this.context, 2)
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