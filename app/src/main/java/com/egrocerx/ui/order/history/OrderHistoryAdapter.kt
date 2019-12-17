package com.egrocerx.ui.order.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.egrocerx.R
import com.egrocerx.callback.OnRecyclerViewItemClick
import com.egrocerx.data.OrderHistoryItem
import com.egrocerx.util.AppUtils
import kotlinx.android.synthetic.main.item_order.view.*

class OrderHistoryAdapter(val callback: OnRecyclerViewItemClick<OrderHistoryItem>) :
    RecyclerView.Adapter<OrderHistoryAdapter.OrderVh>() {
    private val list = ArrayList<OrderHistoryItem>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderVh {
        return OrderVh(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_order, parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun addOrders(data: ArrayList<OrderHistoryItem>) {
        clearData()
        data.forEach {
            list.add(it)
            notifyItemInserted(list.size)
        }
    }

    fun clearData() {
        if (list.isNotEmpty()) {
            list.clear()
            notifyDataSetChanged()
        }
    }

    override fun onBindViewHolder(holder: OrderVh, position: Int) {
        holder.bind(list[holder.adapterPosition])
    }


    inner class OrderVh(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(model: OrderHistoryItem) {
            itemView.orderIdValue.text = model.orderId
            itemView.orderDateValue.text = model.orderDate
            itemView.netAmountValue.text = AppUtils.getAmountWithCurrency(model.netAmount)
            itemView.orderStatusValue.text = AppUtils.getOrderStatus(model.orderStatus)

            itemView.setOnClickListener {
                callback.onRecyclerItemClicked(adapterPosition, it, model)
            }
        }

    }
}