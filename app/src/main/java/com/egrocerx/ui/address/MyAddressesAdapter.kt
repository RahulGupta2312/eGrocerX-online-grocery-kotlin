package com.egrocerx.ui.address

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.egrocerx.R
import com.egrocerx.callback.OnRecyclerViewItemClick
import com.egrocerx.core.MyApplication
import com.egrocerx.data.AddressModel
import kotlinx.android.synthetic.main.item_address.view.*

class MyAddressesAdapter(val callback: OnRecyclerViewItemClick<AddressModel>) :
    RecyclerView.Adapter<MyAddressesAdapter.MyAddressVh>() {

    private val layoutInflater = LayoutInflater.from(MyApplication.instance.getContext())
    val list = ArrayList<AddressModel>()

    var lastItemClickedPos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAddressVh {
        return MyAddressVh(layoutInflater.inflate(R.layout.item_address, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyAddressVh, position: Int) {
        holder.bind(list[holder.adapterPosition])
    }

    override fun getItemId(position: Int): Long {
        return list[position].id.toLong()
    }

    fun addAddresses(data: ArrayList<AddressModel>) {
        data.forEach {
            list.add(it)
            notifyItemInserted(list.size)
        }
    }

    fun clearDataSet() {
        if (list.isNotEmpty()) {
            list.clear()
            notifyDataSetChanged()
        }
    }

    fun deleteItem(pos: Int) {
        list.removeAt(pos)
        notifyItemRemoved(pos)
    }


    inner class MyAddressVh(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(model: AddressModel) {
            itemView.itemAddressNamePhone.text = model.full_name + ", " + model.phone_no
            itemView.itemAddressCityPin.text = model.city + ", " + model.pincode
            itemView.itemAddressHouseLocality.text = model.house_no + ", " + model.locality
            itemView.itemAddressType.text = model.type

            itemView.apply {
                // edit
                imageView19.setOnClickListener {
                    lastItemClickedPos = adapterPosition
                    callback.onRecyclerItemClicked(adapterPosition, it, model)
                }

                imageView.setOnClickListener {
                    //delete
                    callback.onRecyclerItemClicked(adapterPosition, it, model)
                }
            }
        }
    }


}