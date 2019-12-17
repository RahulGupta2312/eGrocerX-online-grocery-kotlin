package com.egrocerx.ui.deliveryaddress

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class SlotsSpinnerAdapter : BaseAdapter() {
    private val list = ArrayList<String>()
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var v = p1

        if (v == null) {
            v = LayoutInflater.from(p2?.context).inflate(android.R.layout.simple_spinner_dropdown_item, p2, false)
        }

        // set data
        (v as TextView).text = list[p0]

        return v

    }

    override fun getItem(p0: Int): String {
        return list[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }

    fun addData(data: ArrayList<String>) {
        list.addAll(data)
        notifyDataSetChanged()
    }

}