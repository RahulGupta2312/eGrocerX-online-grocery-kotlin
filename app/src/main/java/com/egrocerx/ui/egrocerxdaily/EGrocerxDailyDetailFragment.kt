package com.egrocerx.ui.egrocerxdaily


import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.egrocerx.R
import com.egrocerx.base.BaseFragment
import com.egrocerx.network.ApiManager
import com.egrocerx.network.ApiMode
import com.egrocerx.network.ApiResponse
import com.egrocerx.util.AppPreference
import com.egrocerx.util.AppUtils
import kotlinx.android.synthetic.main.fragment_egrocerx_daily_detail.*
import kotlinx.android.synthetic.main.fragment_single_product.btnItemProductIncreaseQuantity
import kotlinx.android.synthetic.main.layout_quantity.*

class EGrocerxDailyDetailFragment : BaseFragment(), ApiResponse {

    private lateinit var args: EGrocerxDailyDetailFragmentArgs
    private var productCount = 1
    private var selectedFrequency = 1

    override fun getLayoutId(): Int {
        return R.layout.fragment_egrocerx_daily_detail
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        args = EGrocerxDailyDetailFragmentArgs.fromBundle(arguments!!)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestSubscriptionFrequencyApi()
        populateUi()
        setupListeners()
    }

    private fun setupListeners() {
        btnItemProductIncreaseQuantity.setOnClickListener {
            productCount++
            setProductCount()
        }

        btnItemProductDecreaseQuantity.setOnClickListener {
            if (productCount > 1) {
                productCount--
                setProductCount()
            }
        }

        btnConfirmSubscription.setOnClickListener {
            requestPlaceSubscriptionOrderApi()
        }
    }

    private fun requestPlaceSubscriptionOrderApi() {
        val map = HashMap<String, Any>()
        map["customer_id"] = AppPreference.getInstance().userData.customer_id
        map["product_id"] = args.productId
        map["quantity"] = productCount
        map["subscription_id"] = selectedFrequency

        ApiManager.getInstance().requestApi(ApiMode.SAVE_SUBSCRIPTION, map, true, this, "POST")
    }

    private fun setProductCount() {
        itemProductCartCount.text = productCount.toString()
    }

    private fun populateUi() {
        subscriptionProductName.text = args.productName
        subscriptionProductPrice.text =
            "${resources.getString(R.string.currency_symbol)}${args.productMrp}"
        subscriptionProductSubsPrice.text =
            "${resources.getString(R.string.currency_symbol)}${args.productSubscriptionPrice}"
        subscriptionProductPackaging.text = args.productPacking

        if (!TextUtils.isEmpty(args.productImage)) {
            Glide.with(this).load(
                AppUtils.getFullImageUrl(args.productImage)
            ).into(subscriptionProductImage)
        }
    }

    private fun requestSubscriptionFrequencyApi() {
        ApiManager.getInstance()
            .requestApi(
                ApiMode.SUBSCRIPTION_FREQUENCY, null, true,
                this, "GET"
            )
    }

    override fun onSuccess(jsonObject: JsonObject?, mode: ApiMode?) {

        when (mode) {
            ApiMode.SUBSCRIPTION_FREQUENCY -> {
                populateFrequencyChips(jsonObject?.getAsJsonArray("data"))
            }
            ApiMode.SAVE_SUBSCRIPTION -> {
                AppUtils.shortToast(jsonObject?.get("message")?.asString)
                findNavController().popBackStack()
            }
        }
    }

    @SuppressLint("ResourceType")
    private fun populateFrequencyChips(data: JsonArray?) {
        data?.forEach {
            val chip = Chip(this@EGrocerxDailyDetailFragment.context)
            chip.text = it.asJsonObject?.get("SubscriptionName")?.asString
            chip.isCheckable = true
            chip.id = it.asJsonObject?.get("id")?.asInt!!
            frequencyChipsGroup.addView(chip)
        }
        frequencyChipsGroup.check(1)

        frequencyChipsGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == -1) {
                frequencyChipsGroup.check(selectedFrequency)
                return@setOnCheckedChangeListener
            }

            val chp = group.findViewById<Chip>(checkedId)
            selectedFrequency = chp.id
        }
    }

    override fun onFailure(errorObject: JsonObject?, mode: ApiMode?) {
        AppUtils.shortToast(errorObject?.get("message")?.asString)
    }

    override fun onException(e: Exception?, mode: ApiMode?) {
        e?.printStackTrace()
        AppUtils.showException()
    }


}
