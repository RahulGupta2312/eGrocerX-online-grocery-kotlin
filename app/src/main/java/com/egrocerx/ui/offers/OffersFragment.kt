package com.egrocerx.ui.offers


import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.egrocerx.R
import com.egrocerx.data.OfferModel
import com.egrocerx.network.ApiManager
import com.egrocerx.network.ApiMode
import com.egrocerx.network.ApiResponse
import com.egrocerx.util.AppUtils
import com.egrocerx.util.showSnackbar
import kotlinx.android.synthetic.main.fragment_offers.*

class OffersFragment : Fragment(), ApiResponse {


    private lateinit var offersAdapter: OffersAdapter

    private var mView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        offersAdapter = OffersAdapter(Glide.with(this))
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (mView == null)
            mView = inflater.inflate(R.layout.fragment_offers, container, false)

        return mView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupOfferRecycler()
    }

    private fun setupOfferRecycler() {
//        subToolbarTitle.text = "Offers"
        offerRecycler.apply {
            adapter = offersAdapter
            addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
        }
    }

    private fun requestOffersApi() {
        Handler().postDelayed(Runnable {
            ApiManager.getInstance().requestApi(ApiMode.OFFERS, HashMap(), false, this, "GET")
        }, 500)
    }

    override fun onSuccess(jsonObject: JsonObject?, mode: ApiMode?) {
        progressBar.visibility = View.GONE
        AppUtils.shortToast(jsonObject?.get("message")?.asString)
        val typeToken = object : TypeToken<ArrayList<OfferModel>>() {}.type
        val list = Gson().fromJson<ArrayList<OfferModel>>(jsonObject?.getAsJsonArray("data"), typeToken)

        offersAdapter.addAll(list)
    }

    override fun onFailure(errorObject: JsonObject?, mode: ApiMode?) {
//        AppUtils.shortToast(errorObject?.get("message")?.asString)
        view?.showSnackbar(errorObject?.get("message").toString())
    }

    override fun onException(e: Exception?, mode: ApiMode?) {
        e?.printStackTrace()
//        AppUtils.showException()
        view?.showSnackbar("Something went wrong")
    }

    override fun onResume() {
        super.onResume()
        requestOffersApi()
    }

    override fun onPause() {
        ApiManager.getInstance().cancelApi(ApiMode.OFFERS)
        super.onPause()
    }
}





