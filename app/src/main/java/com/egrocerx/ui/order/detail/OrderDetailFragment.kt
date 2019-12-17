package com.egrocerx.ui.order.detail


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.egrocerx.NavDashboardDirections
import com.egrocerx.R
import com.egrocerx.data.OrderDetail
import com.egrocerx.network.ApiManager
import com.egrocerx.network.ApiMode
import com.egrocerx.network.ApiResponse
import com.egrocerx.util.AppPreference
import com.egrocerx.util.AppUtils
import kotlinx.android.synthetic.main.fragment_order_detail.*
import kotlinx.android.synthetic.main.item_order_product.view.*

class OrderDetailFragment : Fragment(), ApiResponse {


    private var mView: View? = null
    private val itemsAdapter = OrderItemsAdapter()

    private lateinit var orderDetailFragmentArgs: OrderDetailFragmentArgs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fetchDataFromBundle()
    }

    private fun fetchDataFromBundle() {
        if (arguments == null)
            return

        orderDetailFragmentArgs = OrderDetailFragmentArgs.fromBundle(arguments!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_order_detail, container, false)
        }
        return mView?.rootView
    }

    override fun onResume() {
        super.onResume()
        requestOrderDetailApi()
    }

    private fun requestOrderDetailApi() {
        progressBar.visibility = View.VISIBLE
        rootLayout.visibility = View.GONE
        val map = HashMap<String, Any>()
        map["order_id"] = orderDetailFragmentArgs.orderId
        map["order_status"] = orderDetailFragmentArgs.orderStatus
        map["customer_id"] = AppPreference.getInstance().userData.customer_id
        ApiManager.getInstance().requestApi(ApiMode.ORDER_DETAIL, map, false, this, "POST")
    }

    override fun onSuccess(jsonObject: JsonObject?, mode: ApiMode?) {
        progressBar.visibility = View.GONE
        rootLayout.visibility = View.VISIBLE

        val orderDetail =
            Gson().fromJson(jsonObject?.getAsJsonObject("data"), OrderDetail::class.java)

        populateUI(orderDetail)

        setupClickListener()
    }

    private fun setupClickListener() {
        btnCancelOrder.setOnClickListener {

            val directions =
                NavDashboardDirections.actionGlobalCancelOrderFragment3(
                    orderDetailFragmentArgs.orderId, "ORDER_DETAIL"
                )
            findNavController().navigate(directions)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun populateUI(orderDetail: OrderDetail) {

        orderDate.text = "Order Date: ${orderDetail.orderDate}"
        orderId.text = "Order Id: ${orderDetail.orderId}"
        amountPaid.text = "Amount Paid: ${orderDetail.invoiceAmount}"
        orderStatus.text = "Order Status: ${orderDetail.orderStatus}"
        orderAddress.text = "Delivery Address: ${orderDetail.address}"

        if (orderDetail.orderStatusId < 3) {
            btnCancelOrder.visibility = View.VISIBLE
        } else {
            btnCancelOrder.visibility = View.GONE
        }

        itemsAdapter.addItems(orderDetail.items)

        orderItemsRecycler.apply {
            adapter = itemsAdapter
            addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
            isNestedScrollingEnabled = false
        }

    }

    override fun onFailure(errorObject: JsonObject?, mode: ApiMode?) {
        AppUtils.shortToast(errorObject?.get("message")?.asString)
    }

    override fun onException(e: Exception?, mode: ApiMode?) {
        AppUtils.showException()
    }
}

class OrderItemsAdapter : RecyclerView.Adapter<OrderItemsAdapter.OrderItemVh>() {

    private val list = ArrayList<OrderDetail.Item>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemVh {
        return OrderItemVh(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_order_product, parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemId(position: Int): Long {
        return list[position].id.toLong()
    }

    fun addItems(data: List<OrderDetail.Item>) {
        data.forEach {

            list.add(it)
            notifyItemInserted(list.size)

        }
    }

    override fun onBindViewHolder(holder: OrderItemVh, position: Int) {
        holder.bind(list[holder.adapterPosition])
    }

    inner class OrderItemVh(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(model: OrderDetail.Item) {

            itemView.apply {
                this.orderItemProductName.text = model.ProductName
                this.orderItemProductQty.text = model.qty.toString()
                this.orderItemProductAmount.text = AppUtils.getAmountWithCurrency(model.netAmount)
            }
        }
    }
}
