package com.egrocerx.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.egrocerx.data.HomeTitleModel
import kotlinx.android.synthetic.main.item_sub_toolbar.view.*

class HomeTitleVh(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(model: HomeTitleModel) {
        itemView.subToolbarTitle.text = model.data
    }
}
