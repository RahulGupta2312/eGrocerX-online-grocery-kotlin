package com.egrocerx.ui.order.history


import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.egrocerx.R
import com.egrocerx.callback.OnRecyclerViewItemClick
import com.egrocerx.data.OrderHistoryItem
import com.egrocerx.network.ApiManager
import com.egrocerx.network.ApiMode
import com.egrocerx.network.ApiResponse
import com.egrocerx.util.AppPreference
import com.egrocerx.util.AppUtils
import com.egrocerx.util.TbmDatepicker
import kotlinx.android.synthetic.main.fragment_my_orders.*

class MyOrdersFragment : Fragment(), ApiResponse, OnRecyclerViewItemClick<OrderHistoryItem>,
    TbmDatepicker.OnDateSelectedCallback {


    private var mView: View? = null
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        orderHistoryAdapter = OrderHistoryAdapter(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_my_orders, container, false)
        }

        return mView

    }

    override fun onResume() {
        super.onResume()
        requestOrderListApi()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWidgets()
    }

    private fun setupWidgets() {
        setupOrderList()
        setupClickListener()
    }

    private fun setupClickListener() {

        // filter expand collapse
        expandFilterOption.setOnClickListener {
            toggleFilterLayout()
        }

        // reset filter
        btnReset.setOnClickListener {
            startDate.setText("")
            endDate.setText("")
            toggleFilterLayout()
            requestOrderListApi()
        }

        // do filter
        btnFilter.setOnClickListener {
            requestOrderListApi()
        }

        // launch date picker

        startDate.setOnTouchListener { _, motionEvent ->

            if (motionEvent.action == MotionEvent.ACTION_UP) {
                TbmDatepicker(this@MyOrdersFragment).launchDatePicker(
                    TbmDatepicker.DATE_PICKER_TYPE.START_DATE, 0, System.currentTimeMillis()
                )
            }

            true
        }
        endDate.setOnTouchListener { _, motionEvent ->

            if (motionEvent.action == MotionEvent.ACTION_UP) {

                if (TextUtils.isEmpty(startDate.text.toString())) {
                    AppUtils.shortToast("Select start date")
                } else {
                    TbmDatepicker(this@MyOrdersFragment).launchDatePicker(
                        TbmDatepicker.DATE_PICKER_TYPE.END_DATE,
                        AppUtils.stringDateToMiliseconds(startDate.text.toString()),
                        System.currentTimeMillis()
                    )
                }


            }

            true
        }
    }

    private fun toggleFilterLayout() {
        if (filterLayout.visibility == View.VISIBLE) {
            filterLayout.visibility = View.GONE
            expandFilterOption.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0, 0,
                R.drawable.down_arrow_hide, 0
            )
        } else {
            filterLayout.visibility = View.VISIBLE
            expandFilterOption.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0, 0,
                R.drawable.up_arrow_show, 0
            )
        }
    }

    private fun setupOrderList() {
        orderHistoryRecycler.apply {
            adapter = orderHistoryAdapter
        }
    }

    private fun requestOrderListApi() {
        orderHistoryAdapter.clearData()
        progressBar.visibility = View.VISIBLE
        val map = hashMapOf<String, Any>()
        map["customer_id"] = AppPreference.getInstance().userData.customer_id
        map["start_date"] = startDate.text.toString()
        map["end_date"] = endDate.text.toString()
        ApiManager.getInstance().requestApi(
            ApiMode.ORDER_HISTORY, map, false,
            this, "POST"
        )
    }

    override fun onSuccess(jsonObject: JsonObject?, mode: ApiMode?) {
        progressBar.visibility = View.GONE
        val typeToken = object : TypeToken<ArrayList<OrderHistoryItem>>() {}.type
        val list = Gson().fromJson<ArrayList<OrderHistoryItem>>(
            jsonObject?.getAsJsonArray("data"),
            typeToken
        )
        orderHistoryAdapter.addOrders(list)
    }

    override fun onFailure(errorObject: JsonObject?, mode: ApiMode?) {
        AppUtils.shortToast(errorObject?.get("message")?.asString)
    }

    override fun onException(e: Exception?, mode: ApiMode?) {
        AppUtils.showException()
    }

    override fun onDateSelected(date: String, type: TbmDatepicker.DATE_PICKER_TYPE?) {

        if (type == TbmDatepicker.DATE_PICKER_TYPE.START_DATE) {
            endDate.setText("")
            startDate.setText(date)
        } else {
            endDate.setText(date)
        }
    }

    override fun onRecyclerItemClicked(pos: Int, view: View, data: OrderHistoryItem) {
        val direction =
            MyOrdersFragmentDirections.actionMyOrdersFragment2ToOrderDetailFragment2(
                data.orderId,
                data.orderStatus
            )
        findNavController().navigate(direction)
    }


}
