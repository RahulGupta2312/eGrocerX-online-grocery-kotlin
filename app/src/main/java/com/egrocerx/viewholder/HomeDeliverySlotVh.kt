package com.egrocerx.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.egrocerx.data.HomeDeliverySlotModel
import com.egrocerx.util.AppUtils
import kotlinx.android.synthetic.main.item_delivery_slot_home.view.*

class HomeDeliverySlotVh(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(model: HomeDeliverySlotModel) {
        itemView.itemHomeDeliverySlotName.text =
            AppUtils.get12HourTime(model.DeliverySlotFrom) + " to " + AppUtils.get12HourTime(model.DeliverySlotUpto)
    }
}