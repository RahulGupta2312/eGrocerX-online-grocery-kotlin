package com.egrocerx.ui.membership

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.egrocerx.R
import com.egrocerx.data.MembershipItem
import com.egrocerx.util.AppUtils
import kotlinx.android.synthetic.main.item_membership.view.*

class MembershipAdapter : RecyclerView.Adapter<MembershipAdapter.MembershipVh>() {

    private val list = ArrayList<MembershipItem>()
    private var lastPos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MembershipVh {
        return MembershipVh(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_membership, parent, false
            )
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: MembershipVh, position: Int) {
        holder.bind(list[holder.adapterPosition])

        holder.itemView.appCompatCheckBox.setOnClickListener {
            if(list[holder.adapterPosition].enabled){

                if(lastPos==-1){
                    lastPos=holder.adapterPosition
                    list[lastPos].isCurrentSubscription=true
                }

                else{
                    val m=list[holder.adapterPosition]
                    if(!m.isCurrentSubscription){
                        m.isCurrentSubscription=true
                        list[lastPos].isCurrentSubscription=false
                        lastPos=holder.adapterPosition
                    }
                }
                notifyDataSetChanged()
            }
        }
    }

    fun addMemberships(data: java.util.ArrayList<MembershipItem>) {
        list.clear()
        data.forEach {
            list.add(it)
            notifyItemInserted(list.size)
        }
    }

    fun setOptionsEnabled() {
        list.forEachIndexed { index, membershipItem ->
            membershipItem.enabled = true
            notifyItemChanged(index)
        }
    }

    fun getSelectedMembership(): MembershipItem? {
        var membershipItem: MembershipItem? = null
        list.forEach {
            if (it.isCurrentSubscription) {
                membershipItem = it
                return@forEach
            }
        }
        return membershipItem
    }


    inner class MembershipVh(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(data: MembershipItem) {
            with(data) {
                itemView.apply {
                    membershipPackageName.text = name
                    membershipPackageDetail.text = description
                    membershipPackagePrice.text = AppUtils.getAmountWithCurrency(price)
                    membershipPackageSalePrice.text = AppUtils.getAmountWithCurrency(sale_price)
                    appCompatCheckBox.isEnabled = enabled

                    if (isCurrentSubscription) {
                        appCompatCheckBox.setImageResource(android.R.drawable.checkbox_on_background)
                    } else {
                        appCompatCheckBox.setImageResource(android.R.drawable.checkbox_off_background)
                    }


                }
            }
        }
    }
}