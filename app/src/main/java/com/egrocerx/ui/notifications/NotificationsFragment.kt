package com.egrocerx.ui.notifications


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.egrocerx.R
import com.egrocerx.data.NotificationModel
import com.egrocerx.network.ApiManager
import com.egrocerx.network.ApiMode
import com.egrocerx.network.ApiResponse
import com.egrocerx.util.AppUtils
import kotlinx.android.synthetic.main.fragment_notifications.*

class NotificationsFragment : Fragment(), ApiResponse {

    private lateinit var notificationsAdapter: NotificationsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationsAdapter = NotificationsAdapter(Glide.with(this))
        requestNotificationsApi()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNotificationsRecycler()
    }

    private fun setupNotificationsRecycler() {
        notificationRecycler.apply {
            adapter = notificationsAdapter
        }
    }

    private fun requestNotificationsApi() {
        ApiManager.getInstance().requestApi(ApiMode.NOTIFICATIONS, null, false, this, "GET")
    }

    override fun onSuccess(jsonObject: JsonObject?, mode: ApiMode?) {
        progressBar.visibility = View.GONE
        AppUtils.shortToast(jsonObject?.get("message")?.asString)
        val typeToken = object : TypeToken<ArrayList<NotificationModel>>() {}.type
        val list = Gson().fromJson<ArrayList<NotificationModel>>(jsonObject?.getAsJsonArray("data"), typeToken)
        notificationsAdapter.addNotifications(list)
    }

    override fun onFailure(errorObject: JsonObject?, mode: ApiMode?) {
        AppUtils.shortToast(errorObject?.get("message")?.asString)
    }

    override fun onException(e: Exception?, mode: ApiMode?) {
        e?.printStackTrace()
        AppUtils.showException()
    }


}
