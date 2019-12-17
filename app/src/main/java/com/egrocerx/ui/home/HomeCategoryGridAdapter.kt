package com.egrocerx.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.egrocerx.R
import com.egrocerx.callback.OnRecyclerViewItemClick
import com.egrocerx.data.CategoryModel
import com.egrocerx.viewholder.HomeCategoryItemGridVh

class HomeCategoryGridAdapter(val callback:OnRecyclerViewItemClick<CategoryModel>) : RecyclerView.Adapter<HomeCategoryItemGridVh>() {
    val list = ArrayList<CategoryModel>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeCategoryItemGridVh {
        return HomeCategoryItemGridVh(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_category_grid, parent, false
            ),callback
        )
    }

    fun addAll(data: List<CategoryModel>) {
        data.forEach {
            add(it)
        }
    }

    private fun add(model: CategoryModel) {
        list.add(model)
        notifyItemInserted(list.size)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: HomeCategoryItemGridVh, position: Int) {
        holder.bind(list[holder.adapterPosition])
    }

}