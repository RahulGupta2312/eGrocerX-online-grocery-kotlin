package com.egrocerx.ui.gocash


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.egrocerx.R
import com.egrocerx.data.WalletHistoryModel
import com.egrocerx.network.ApiManager
import com.egrocerx.network.ApiMode
import com.egrocerx.network.ApiResponse
import com.egrocerx.util.AppPreference
import com.egrocerx.util.AppUtils
import kotlinx.android.synthetic.main.fragment_wallet.*

class WalletFragment : Fragment(), ApiResponse {


    private lateinit var _walletAdapter: WalletHistoryAdapter

    private var _mView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _walletAdapter = WalletHistoryAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (_mView == null)
            _mView = inflater.inflate(R.layout.fragment_wallet, container, false)
        return _mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupHistoryRecycler()
        setupClickListener()
    }

    private fun setupClickListener() {
        transferToFriend.setOnClickListener {
            findNavController().navigate(R.id.action_walletFragment2_to_sendMoneyFragment2)
        }
        rechargeWallet.setOnClickListener {
            findNavController().navigate(R.id.action_walletFragment2_to_rechargeWalletFragment2)
        }
    }

    private fun setupHistoryRecycler() {
        walletHistoryRecycler.apply {
            adapter = _walletAdapter
            addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
        }
    }

    private fun requestWalletStatementApi() {
        val map = HashMap<String, Any>()
        map["customer_id"] = AppPreference.getInstance().userData.customer_id
        ApiManager.getInstance().requestApi(ApiMode.WALLET_HISTORY, map, false, this, "POST")

    }

    override fun onSuccess(jsonObject: JsonObject?, mode: ApiMode?) {
        progressBar.visibility = View.GONE
        walletRootLayout.visibility = View.VISIBLE
        walletFabOptions.visibility = View.VISIBLE
        // populate UI
        goCashAmount.text = resources.getString(R.string.currency_symbol) +
                jsonObject?.getAsJsonObject("data")
                    ?.getAsJsonObject("gocash")?.get("GoCashBalance")?.asString

//        goCashAmount.text = resources.getString(R.string.currency_symbol) + " 200"

        // fetch history
        val typeToken = object : TypeToken<ArrayList<WalletHistoryModel>>() {}.type

        val list = Gson().fromJson<ArrayList<WalletHistoryModel>>(
            jsonObject?.getAsJsonObject("data")?.getAsJsonArray("history").toString(), typeToken
        )

        _walletAdapter.addHistory(list)
    }

    override fun onFailure(errorObject: JsonObject?, mode: ApiMode?) {
        AppUtils.shortToast(errorObject?.get("message")?.asString)
    }

    override fun onException(e: Exception?, mode: ApiMode?) {
        e?.printStackTrace()
        AppUtils.showException()
    }

    override fun onResume() {
        super.onResume()
        requestWalletStatementApi()
    }

}
