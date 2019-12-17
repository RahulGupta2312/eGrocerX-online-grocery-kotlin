package com.egrocerx.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.egrocerx.R
import com.egrocerx.callback.OnRecyclerViewItemClick
import com.egrocerx.core.MyApplication
import com.egrocerx.data.*
import com.egrocerx.viewholder.*
import java.util.*


class HomeAdapter(val callback:OnRecyclerViewItemClick<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val list = ArrayList<BaseListItemModel>()

    private val layoutInflater = LayoutInflater.from(MyApplication.instance.getContext())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            R.layout.item_banner -> {
                return HomeBannerVh(layoutInflater.inflate(viewType, parent, false))

            }
            R.layout.item_categories_recycler -> {
                return HomeCategoryGridVh(layoutInflater.inflate(viewType, parent, false),callback)
            }
            R.layout.item_sub_toolbar -> {
                return HomeTitleVh(layoutInflater.inflate(viewType, parent, false))
            }
            R.layout.item_delivery_slot_home -> {
                return HomeDeliverySlotVh(layoutInflater.inflate(viewType, parent, false))
            }
            /*R.layout.item_slider*/else -> {
            return HomeSliderVh(layoutInflater.inflate(viewType, parent, false))
        }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            R.layout.item_banner -> {
                (holder as HomeBannerVh).bind(list[holder.adapterPosition] as HomeBannerModel)
            }
            R.layout.item_categories_recycler -> {
                (holder as HomeCategoryGridVh).bind(list[holder.adapterPosition] as HomeCategoriesModel)
            }
            R.layout.item_slider -> {
                (holder as HomeSliderVh).bind(list[holder.adapterPosition] as HomeSliderModel)
            }
            R.layout.item_sub_toolbar -> {
                (holder as HomeTitleVh).bind(list[holder.adapterPosition] as HomeTitleModel)
            }
            R.layout.item_delivery_slot_home -> {
                (holder as HomeDeliverySlotVh).bind(list[holder.adapterPosition] as HomeDeliverySlotModel)
            }
        }
    }

    fun addAll(data: ArrayList<BaseListItemModel>) {
        clearDataSet()
        data.forEach {
            addSingle(it)
        }
    }

    private fun addSingle(data: BaseListItemModel) {
        list.add(data)
        notifyItemInserted(list.size)
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].getLayoutId()
    }

    private fun clearDataSet() {
        list.clear()
        notifyDataSetChanged()
    }


}