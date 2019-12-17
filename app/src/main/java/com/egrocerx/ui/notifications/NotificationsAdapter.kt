package com.egrocerx.ui.notifications

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.egrocerx.R
import com.egrocerx.core.MyApplication
import com.egrocerx.data.NotificationModel
import com.egrocerx.util.AppUtils
import kotlinx.android.synthetic.main.item_notifications.view.*

class NotificationsAdapter(val requestManager: RequestManager) :
    RecyclerView.Adapter<NotificationsAdapter.NotificationVh>() {
    private val layoutInflater = LayoutInflater.from(MyApplication.instance.getContext())
    val list = ArrayList<NotificationModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationVh {
        return NotificationVh(layoutInflater.inflate(R.layout.item_notifications, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: NotificationVh, position: Int) {
        holder.bind(list[holder.adapterPosition])
    }

    override fun getItemId(position: Int): Long {
        return list[position].id.toLong()
    }

    fun addNotifications(data: ArrayList<NotificationModel>) {
        data.forEach {
            list.add(it)
            notifyItemInserted(list.size)
        }
    }


    inner class NotificationVh(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(model: NotificationModel) {
            itemView.itemNotificationTitle.text = model.title
            itemView.itemNotificationDesc.text = model.description

            if (!TextUtils.isEmpty(model.ImagePath)) {
                itemView.itemNotificationImage.visibility = View.VISIBLE
                requestManager.load(AppUtils.getFullImageUrl(model.ImagePath))
                    .placeholder(R.drawable.placeholder_grey)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(itemView.itemNotificationImage)
            }
        }
    }
}