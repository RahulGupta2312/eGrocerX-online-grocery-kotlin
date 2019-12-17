package com.egrocerx.ui.categories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.egrocerx.callback.OnRecyclerViewItemClick
import com.egrocerx.core.MyApplication
import com.egrocerx.data.Subcategory

class SubcategoriesAdapter(private val list: List<Subcategory>, val callback: OnRecyclerViewItemClick<Subcategory>) :
    RecyclerView.Adapter<SubcategoriesAdapter.SubcategoryVh>() {

    private var layoutInflater: LayoutInflater = LayoutInflater.from(MyApplication.instance.getContext())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubcategoryVh {
        return SubcategoryVh(layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: SubcategoryVh, position: Int) {
        holder.bind(list[holder.adapterPosition])
    }


    inner class SubcategoryVh(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(model: Subcategory) {
            (itemView as TextView).text = model.SubcategoryL1Name
            itemView.setOnClickListener {
                callback.onRecyclerItemClicked(adapterPosition, itemView, model)
            }
        }
    }
}