package com.egrocerx.ui.account


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.gson.JsonObject
import com.egrocerx.R
import com.egrocerx.callback.OnRecyclerViewItemClick
import com.egrocerx.data.AccountItemModel
import com.egrocerx.network.ApiManager
import com.egrocerx.network.ApiMode
import com.egrocerx.network.ApiResponse
import com.egrocerx.ui.login.LoginActivity
import com.egrocerx.util.AppPreference
import kotlinx.android.synthetic.main.fragment_my_account.*
import java.lang.Exception


class MyAccountFragment : Fragment(), OnRecyclerViewItemClick<AccountItemModel>, ApiResponse {

    private lateinit var accountAdapter: MyAccountAdapter

    private var mView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (mView == null)
            mView = inflater.inflate(R.layout.fragment_my_account, container, false)
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupMyAccountRecycler()
    }

    private fun setupMyAccountRecycler() {
        accountAdapter = MyAccountAdapter(this)
        myAccountRecycler.apply {
            addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
            setHasFixedSize(true)
            adapter = accountAdapter
        }
    }

    override fun onRecyclerItemClicked(pos: Int, view: View, data: AccountItemModel) {
        if (data.navDirectionId != null) {
            findNavController().navigate(data.navDirectionId)
        } else {
            requestLogoutApi()
        }
    }

    private fun requestLogoutApi() {
        val map=HashMap<String,Any>()
        map["customer_id"]=AppPreference.getInstance().userData.customer_id
        ApiManager.getInstance().requestApi(ApiMode.LOGOUT,map,true,this,"POST")
    }

    override fun onSuccess(jsonObject: JsonObject?, mode: ApiMode?) {
       processLogout()
    }

    override fun onFailure(errorObject: JsonObject?, mode: ApiMode?) {

    }

    override fun onException(e: Exception?, mode: ApiMode?) {

    }

    private fun processLogout() {
        AppPreference.getInstance().clearAllPreferences()
        startActivity(Intent(context, LoginActivity::class.java))
        activity?.finishAffinity()
    }


}
