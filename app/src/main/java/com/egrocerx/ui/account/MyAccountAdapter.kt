package com.egrocerx.ui.account

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.egrocerx.R
import com.egrocerx.callback.OnRecyclerViewItemClick
import com.egrocerx.core.MyApplication
import com.egrocerx.data.AccountItemModel
import com.egrocerx.util.AppUtils
import kotlinx.android.synthetic.main.item_account.view.*

class MyAccountAdapter(val callback: OnRecyclerViewItemClick<AccountItemModel>) :
    RecyclerView.Adapter<MyAccountAdapter.MyAccountVh>() {

    private val list = AppUtils.getAccountItemList()
    private val layoutInflater: LayoutInflater =
        LayoutInflater.from(MyApplication.instance.getContext())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAccountVh {
        return MyAccountVh(layoutInflater.inflate(R.layout.item_account, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyAccountVh, position: Int) {
        holder.bind(list[holder.adapterPosition])
    }


    inner class MyAccountVh(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(model: AccountItemModel) {
            itemView.itemAccountName.text = model.name
            itemView.itemAccountLeadingImage.setImageResource(model.iconResId)
            itemView.setOnClickListener {
                callback.onRecyclerItemClicked(adapterPosition, it, model)
            }
        }
    }
}