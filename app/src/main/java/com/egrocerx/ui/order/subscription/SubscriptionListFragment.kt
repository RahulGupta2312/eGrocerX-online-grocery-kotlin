package com.egrocerx.ui.order.subscription


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.egrocerx.R
import com.egrocerx.data.SubscriptionHistoryItem
import com.egrocerx.network.ApiManager
import com.egrocerx.network.ApiMode
import com.egrocerx.network.ApiResponse
import com.egrocerx.util.AppPreference
import kotlinx.android.synthetic.main.fragment_subscription_list.*

class SubscriptionListFragment : Fragment(), ApiResponse {

    private lateinit var subscriptionListAdapter: SubscriptionListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subscriptionListAdapter = SubscriptionListAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_subscription_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler()
    }

    private fun setupRecycler() {
        subscriptionList.apply {
            adapter = subscriptionListAdapter
//            addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
        }
    }


    override fun onResume() {
        super.onResume()
        requestSubscriptionsApi()
    }

    private fun requestSubscriptionsApi() {
        progressBar.visibility = View.VISIBLE
        val map = HashMap<String, Any>()
        map["customer_id"] = AppPreference.getInstance().userData.customer_id
        ApiManager.getInstance().requestApi(ApiMode.SUBSCRIPTIONS, map, false, this, "POST")
    }

    override fun onSuccess(jsonObject: JsonObject?, mode: ApiMode?) {
        progressBar.visibility = View.GONE
        val typeToken = object : TypeToken<ArrayList<SubscriptionHistoryItem>>() {}.type
        val list = Gson().fromJson<ArrayList<SubscriptionHistoryItem>>(
            jsonObject?.getAsJsonArray("data"),
            typeToken
        )
        subscriptionListAdapter.addItems(list)
    }

    override fun onFailure(errorObject: JsonObject?, mode: ApiMode?) {

    }

    override fun onException(e: Exception?, mode: ApiMode?) {

    }

    override fun onPause() {
        ApiManager.getInstance().cancelApi(ApiMode.SUBSCRIPTIONS)
        super.onPause()
    }


}
