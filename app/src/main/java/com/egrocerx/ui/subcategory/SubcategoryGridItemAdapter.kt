package com.egrocerx.ui.subcategory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.egrocerx.R
import com.egrocerx.callback.OnRecyclerViewItemClick
import com.egrocerx.data.SubCategoryModel
import com.egrocerx.viewholder.SubcategoryItemGridVh


class SubcategoryGridItemAdapter(val callback: OnRecyclerViewItemClick<SubCategoryModel>)
    : RecyclerView.Adapter<SubcategoryItemGridVh>() {
    val list = ArrayList<SubCategoryModel>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubcategoryItemGridVh {
        return SubcategoryItemGridVh(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_category_grid, parent, false
            ),callback
        )
    }

    fun addAll(data: List<SubCategoryModel>) {
        data.forEach {
            add(it)
        }
    }

    private fun add(model: SubCategoryModel) {
        list.add(model)
        notifyItemInserted(list.size)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: SubcategoryItemGridVh, position: Int) {
        holder.bind(list[holder.adapterPosition])
    }

}