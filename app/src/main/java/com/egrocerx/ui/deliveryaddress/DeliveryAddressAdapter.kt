package com.egrocerx.ui.deliveryaddress

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.egrocerx.data.AddressModel

class DeliverySpinnerAdapter : BaseAdapter() {
    private val list = ArrayList<AddressModel>()
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var v = p1

        if (v == null) {
            v = LayoutInflater.from(p2?.context).inflate(android.R.layout.simple_list_item_1, p2, false)
        }

        // set data
        (v as TextView).text = list[p0].getFullAddress()

        return v

    }

    override fun getItem(p0: Int): AddressModel {
        return list[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }

    fun addData(data: ArrayList<AddressModel>) {
        list.addAll(data)
        notifyDataSetChanged()
    }

}