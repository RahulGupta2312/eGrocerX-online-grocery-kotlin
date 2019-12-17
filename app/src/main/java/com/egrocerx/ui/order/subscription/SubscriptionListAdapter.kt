package com.egrocerx.ui.order.subscription

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.egrocerx.R
import com.egrocerx.data.SubscriptionHistoryItem
import com.egrocerx.network.ApiManager
import com.egrocerx.network.ApiMode
import com.egrocerx.network.ApiResponse
import com.egrocerx.util.AppPreference
import com.egrocerx.util.AppUtils
import kotlinx.android.synthetic.main.item_subscription_order.view.*

class SubscriptionListAdapter : RecyclerView.Adapter<SubscriptionListAdapter.SubscriptionItemVh>() {
    private val list = ArrayList<SubscriptionHistoryItem>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionItemVh {
        return SubscriptionItemVh(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_subscription_order,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: SubscriptionItemVh, position: Int) {
        holder.bind(list[holder.adapterPosition])
    }

    fun addItems(data: ArrayList<SubscriptionHistoryItem>) {
        data.forEach {
            list.add(it)
            notifyItemInserted(list.size)
        }
    }

    fun clearDataset() {
        list.clear()
        notifyDataSetChanged()
    }


    inner class SubscriptionItemVh(itemView: View) : RecyclerView.ViewHolder(itemView),
        ApiResponse {

        lateinit var data: SubscriptionHistoryItem

        fun bind(data: SubscriptionHistoryItem) {
            this.data = data
            itemView.apply {
                subscriptionFrequency.text = data.SubscriptionName
                textView14.text = AppUtils.getAmountWithCurrency(data.ProductSubscriptionPrice)
                subscriptionProductName.text = data.ProductName
                variantValue.text = data.packing_name
                subscriptionStatus.apply {
                    text = if (data.SubscriptionStatus == "1")
                        "Pause" else "Resume"

                    setOnClickListener {
                        requestUpdateSubscriptionApi(data.id, false)
                    }
                }
                textView13.text = data.ProductQty
                txtDeleteSubscription.setOnClickListener {
                    requestUpdateSubscriptionApi(data.id, true)
                }
            }
        }

        private fun requestUpdateSubscriptionApi(id: String, delete: Boolean) {
            val map = HashMap<String, Any>()
            map["subscription_id"] = id
            map["status"] = if (data.SubscriptionStatus == "1") "0" else "1"
            map["customer_id"] = AppPreference.getInstance().userData.customer_id
            ApiManager.getInstance().requestApi(
                if (delete) ApiMode.DELETE_SUBSCRIPTION else ApiMode.UPDATE_SUBSCRIPTION,
                map,
                true,
                this,
                "POST"
            )
        }


        override fun onSuccess(jsonObject: JsonObject?, mode: ApiMode?) {
            when (mode) {
                ApiMode.UPDATE_SUBSCRIPTION -> {
                    data.SubscriptionStatus = jsonObject?.get("data")?.asString
                    notifyItemChanged(adapterPosition)
                }
                ApiMode.DELETE_SUBSCRIPTION -> {
                    notifyItemRemoved(adapterPosition)
                }
            }
        }

        override fun onFailure(errorObject: JsonObject?, mode: ApiMode?) {

        }

        override fun onException(e: Exception?, mode: ApiMode?) {

        }
    }
}