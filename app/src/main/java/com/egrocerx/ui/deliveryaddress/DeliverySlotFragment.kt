package com.egrocerx.ui.deliveryaddress


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.egrocerx.R
import com.egrocerx.data.AddressModel
import com.egrocerx.network.ApiManager
import com.egrocerx.network.ApiMode
import com.egrocerx.network.ApiResponse
import com.egrocerx.util.AppPreference
import com.egrocerx.util.AppUtils
import kotlinx.android.synthetic.main.fragment_delivery_slot.*


class DeliverySlotFragment : Fragment() {

    private var mView: View? = null
    private lateinit var slotsAdapter: SlotsSpinnerAdapter
    private lateinit var deliverySpinnerAdapter: DeliverySpinnerAdapter

    private var selectedAddressPos = 0
    private var selectedSlotPos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        slotsAdapter = SlotsSpinnerAdapter()
        deliverySpinnerAdapter = DeliverySpinnerAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_delivery_slot, container, false)
        }

        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWidgets()
    }

    private fun setupWidgets() {
        setupAddressSpinner()
        setupDeliverySlotsSpinner()

        btnProceed?.setOnClickListener {
            val directions = DeliverySlotFragmentDirections
                .actionDeliverySlotFragmentToCheckoutFragment(
                    deliverySpinnerAdapter.getItem(selectedAddressPos).getFullAddress(),
                    deliverySpinnerAdapter.getItem(selectedAddressPos).id,
                    slotsAdapter.getItem(selectedSlotPos)
                )
            findNavController().navigate(directions)
        }
    }


    private fun setupAddressSpinner() {
        deliveryAddressSpinner.adapter = deliverySpinnerAdapter
        deliveryAddressSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    selectedAddressPos = p2
                }
            }
        requestAddressApi()
    }

    private fun setupDeliverySlotsSpinner() {
        deliverySlotSpinner.adapter = slotsAdapter
        deliverySlotSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    selectedSlotPos = p2
                }
            }
    }

    private fun requestSlotsApi() {
        var slots: java.util.ArrayList<String>
        ApiManager.getInstance().requestApi(
            ApiMode.AVAILABLE_SLOTS, HashMap(), false,
            object : ApiResponse {
                override fun onSuccess(jsonObject: JsonObject?, mode: ApiMode?) {
                    val typeToken = object : TypeToken<ArrayList<String>>() {}.type
                    slots = Gson().fromJson(jsonObject?.getAsJsonArray("data"), typeToken)
                    slotsAdapter.addData(slots)
                    btnProceed.isEnabled=true
                }

                override fun onFailure(errorObject: JsonObject?, mode: ApiMode?) {
                    AppUtils.shortToast(errorObject?.get("message")?.asString)
                }

                override fun onException(e: Exception?, mode: ApiMode?) {
                    AppUtils.showException()
                }
            }, "GET"
        )
    }

    private fun requestAddressApi()/*: ArrayList<AddressModel>*/ {
        var addresess: java.util.ArrayList<AddressModel>
        val map = HashMap<String, Any>()
        map["customer_id"] = AppPreference.getInstance().userData.customer_id
        ApiManager.getInstance().requestApi(
            ApiMode.ALL_ADDRESSES, map, false,
            object : ApiResponse {
                override fun onSuccess(jsonObject: JsonObject?, mode: ApiMode?) {
                    val typeToken = object : TypeToken<ArrayList<AddressModel>>() {}.type
                    addresess = Gson().fromJson(jsonObject?.getAsJsonArray("data"), typeToken)
                    deliverySpinnerAdapter.addData(addresess)
                    if (addresess.isEmpty()) {
                        btnProceed.isEnabled = false
                        AppUtils.shortToast("You have no address on your profile. Please add it first")
                        return
                    }
                    requestSlotsApi()
                }

                override fun onFailure(errorObject: JsonObject?, mode: ApiMode?) {
                    AppUtils.shortToast(errorObject?.get("message")?.asString)
                }

                override fun onException(e: Exception?, mode: ApiMode?) {
                    AppUtils.showException()
                }
            }, "POST"
        )
    }
}
