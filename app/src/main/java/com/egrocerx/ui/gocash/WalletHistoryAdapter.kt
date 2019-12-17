package com.egrocerx.ui.gocash

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.egrocerx.R
import com.egrocerx.data.WalletHistoryModel
import kotlinx.android.synthetic.main.item_wallet_history.view.*

class WalletHistoryAdapter : RecyclerView.Adapter<WalletHistoryAdapter.WalletVh>() {
    val list = ArrayList<WalletHistoryModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletVh {
        return WalletVh(LayoutInflater.from(parent.context).inflate(R.layout.item_wallet_history, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: WalletVh, position: Int) {
        holder.bind(list[holder.adapterPosition])
    }

    fun addHistory(data: ArrayList<WalletHistoryModel>) {
        list.clear()
        notifyDataSetChanged()

        data.forEach {
            list.add(it)
            notifyItemInserted(list.size)
        }
    }


    inner class WalletVh(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(model: WalletHistoryModel) {
            itemView.itemWalletTxnId.text = "Txn Id: ${model.TransactionId}"
            itemView.itemWalletTxnDate.text = "Txn Date: ${model.TransactionDate}"
            itemView.itemWalletTxnNote.text = "Note: ${model.TransactionNote}"
            setFormattedAmount(model.Credit, model.Debit, itemView.itemWalletTxnAmount)

        }

        private fun setFormattedAmount(credit: Int, debit: Int, textView: TextView) {
            val str: String
            if (debit > 0) {
                str = " - " + textView.context.getString(R.string.currency_symbol) + debit
                textView.setTextColor(ContextCompat.getColor(textView.context, R.color.colorRed))
            } else {
                str = " + " + textView.context.getString(R.string.currency_symbol) + credit
                textView.setTextColor(ContextCompat.getColor(textView.context, R.color.colorPrimary))
            }

            textView.text = str

        }
    }
}