package com.egrocerx.ui.subcategory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.egrocerx.R
import com.egrocerx.callback.OnRecyclerViewItemClick
import com.egrocerx.core.MyApplication
import com.egrocerx.data.BaseListItemModel
import com.egrocerx.data.ProductsModel
import com.egrocerx.data.SubCategoriesModel
import com.egrocerx.viewholder.ProductGridVh
import com.egrocerx.viewholder.SubcategoryGridVh

class SubCategoryAdapter(val callback: OnRecyclerViewItemClick<BaseListItemModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val layoutInflater = LayoutInflater.from(MyApplication.instance.getContext())

    val list = ArrayList<BaseListItemModel>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_categories_recycler -> {
                SubcategoryGridVh(layoutInflater.inflate(viewType, parent, false))
            }
            else -> {
                ProductGridVh(layoutInflater.inflate(viewType, parent, false))
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    public fun addAll(data: List<BaseListItemModel>) {
        data.forEach {
            list.add(it)
            notifyItemInserted(list.size)
        }
    }
    public fun add(data: BaseListItemModel) {

            list.add(data)
            notifyItemInserted(list.size)

    }

    override fun getItemViewType(position: Int): Int {
        return list[position].getLayoutId()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            R.layout.item_categories_recycler -> {
                (holder as SubcategoryGridVh).bind(list[holder.adapterPosition] as SubCategoriesModel)
            }
            R.layout.item_products_recycler -> {
                (holder as ProductGridVh).bind(list[holder.adapterPosition] as ProductsModel)
            }
        }
    }
}