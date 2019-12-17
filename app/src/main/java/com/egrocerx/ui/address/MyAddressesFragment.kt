package com.egrocerx.ui.address


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
import com.google.gson.reflect.TypeToken
import com.egrocerx.NavGraphProfileDirections
import com.egrocerx.R
import com.egrocerx.callback.OnRecyclerViewItemClick
import com.egrocerx.data.AddressModel
import com.egrocerx.network.ApiManager
import com.egrocerx.network.ApiMode
import com.egrocerx.network.ApiResponse
import com.egrocerx.util.AppPreference
import com.egrocerx.util.AppUtils
import kotlinx.android.synthetic.main.fragment_my_addresses.*

class MyAddressesFragment : Fragment(), ApiResponse, OnRecyclerViewItemClick<AddressModel> {

    private lateinit var addressAdapter: MyAddressesAdapter

    private var mView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addressAdapter = MyAddressesAdapter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_my_addresses, container, false)
        }

        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUiElements()
        requestAddressListApi()
        setupClickListeners()
    }

    private fun setupUiElements() {
//        addressAdapter.setHasStableIds(true)
        addressRecycler.apply {
            adapter = addressAdapter
            addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy > 0)
                        btnAddNewAddress.shrink(true)
                    else
                        btnAddNewAddress.extend(true)
                }
            })
        }


    }

    private fun setupClickListeners() {
        btnAddNewAddress.setOnClickListener {
            val directions =
                NavGraphProfileDirections.actionGlobalAddAddressFragment2("Add Address")
            directions.mode = 1
            findNavController().navigate(directions)
        }
    }

    private fun requestAddressListApi() {
        progressBar.visibility = View.VISIBLE
        addressAdapter.clearDataSet()
        val map = HashMap<String, Any>()
        map["customer_id"] = AppPreference.getInstance().userData.customer_id
        ApiManager.getInstance().requestApi(ApiMode.ALL_ADDRESSES, map, false, this, "POST")
    }


    override fun onSuccess(jsonObject: JsonObject?, mode: ApiMode?) {
        when (mode) {
            ApiMode.ALL_ADDRESSES -> {
//                AppUtils.shortToast(jsonObject?.get("message")?.asString)
                progressBar.visibility = View.GONE

                val typeToken = object : TypeToken<ArrayList<AddressModel>>() {}.type
                val list =
                    Gson().fromJson<ArrayList<AddressModel>>(
                        jsonObject?.getAsJsonArray("data"),
                        typeToken
                    )
                addressAdapter.addAddresses(list)
            }
            ApiMode.DELETE_ADDRESS -> {
                addressAdapter.deleteItem(addressAdapter.lastItemClickedPos)
            }
        }
    }

    override fun onFailure(errorObject: JsonObject?, mode: ApiMode?) {
        AppUtils.shortToast(errorObject?.get("message")?.asString)
    }

    override fun onException(e: Exception?, mode: ApiMode?) {
        e?.printStackTrace()
        AppUtils.showException()
    }

    override fun onRecyclerItemClicked(pos: Int, view: View, data: AddressModel) {

        when (view.id) {
            R.id.imageView -> { // delete
                requestDeleteAddressApi(data.id)
            }
            R.id.imageView19 -> { // edit

                val directions =
                    NavGraphProfileDirections.actionGlobalAddAddressFragment2("Edit Address")
                directions.mode = 2
                directions.data = data
                findNavController().navigate(directions)
            }
        }
    }

    private fun requestDeleteAddressApi(id: Int) {
        val map = HashMap<String, Any>()
        map["id"] = id
        map["customer_id"] = AppPreference.getInstance().userData.customer_id
        ApiManager.getInstance().requestApi(ApiMode.DELETE_ADDRESS, map, true, this, "POST")
    }
}
