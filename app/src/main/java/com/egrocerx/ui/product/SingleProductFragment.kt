package com.egrocerx.ui.product


import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.denzcoskun.imageslider.models.SlideModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.egrocerx.R
import com.egrocerx.base.BaseFragment
import com.egrocerx.data.ProductDetailModel
import com.egrocerx.network.ApiManager
import com.egrocerx.network.ApiMode
import com.egrocerx.network.ApiResponse
import com.egrocerx.util.AppPreference
import com.egrocerx.util.AppUtils
import kotlinx.android.synthetic.main.fragment_single_product.*

class SingleProductFragment : BaseFragment(), ApiResponse {


    private lateinit var productArguments: SingleProductFragmentArgs
    private var productCount = 0
    private var productModel: ProductDetailModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productArguments = SingleProductFragmentArgs.fromBundle(arguments!!)
    }

    override fun getLayoutId(): Int = R.layout.fragment_single_product

//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//
//        if (mView == null) {
//            mView = inflater.inflate(R.layout.fragment_single_product, container, false)
//        }
//
//        return mView
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestProductDetailApi()
    }


    private fun requestProductDetailApi() {
        progressBar.visibility = View.VISIBLE
        productDetailRoot.visibility = View.GONE
        val map = HashMap<String, Any>()
        map["customer_id"] = AppPreference.getInstance().userData.customer_id
        map["product_id"] = productArguments.productId
        ApiManager.getInstance().requestApi(ApiMode.PRODUCT_DETAIL, map, false, this, "POST")
    }

    override fun onSuccess(jsonObject: JsonObject?, mode: ApiMode?) {

        when (mode) {
            ApiMode.PRODUCT_DETAIL -> {
                progressBar.visibility = View.GONE
                productDetailRoot.visibility = View.VISIBLE
                productModel = Gson().fromJson(
                    jsonObject?.getAsJsonObject("data"),
                    ProductDetailModel::class.java
                )
                populateUI(productModel!!)
                setupProductQuantityListener()
            }
            ApiMode.ADD_TO_CART -> {
                productCount = jsonObject?.get("data")?.asInt!!

            }
            ApiMode.REMOVE_FROM_CART -> {
                productCount = jsonObject?.get("data")?.asInt!!
            }
            ApiMode.NOTIFY_ME -> {
                showAlertDialog(jsonObject?.get("message")?.asString)
            }
        }
        updateProductCount()

    }

    override fun onFailure(errorObject: JsonObject?, mode: ApiMode?) {
        AppUtils.shortToast(errorObject?.get("message")?.asString)
    }

    override fun onException(e: Exception?, mode: ApiMode?) {
        AppUtils.showException()
    }

    private fun setupProductQuantityListener() {
        btnItemProductDecreaseQuantity.setOnClickListener {
            if (productCount > 0)
                requestRemoveFromCartApi(productModel!!.id)
        }
        btnItemProductIncreaseQuantity.setOnClickListener {
            requestAddToCartApi(productModel!!.id)
        }
        productAddToCart.setOnClickListener {
            requestAddToCartApi(productModel!!.id)
        }

        // setup Notify me click listener
        btnNotifyForProduct.setOnClickListener {
            requestNotifyMeApi()
        }
    }


    private fun populateUI(productModel: ProductDetailModel) {

        productName.text = productModel.ProductName
        productPackingName.text = productModel.packing_name
        productCost.text = AppUtils.getAmountWithCurrency(productModel.ProductMrp.toString())
        if (productModel.ProductOfferprice < productModel.ProductMrp) {
            productOfferCost.text =
                AppUtils.getAmountWithCurrency(productModel.ProductOfferprice.toString())
            productCost.background = resources.getDrawable(R.drawable.bg_strikethrough)
        }

        productDeliverySlot.text = "${AppUtils.get12HourTime(productModel.DeliverySlotFrom)}-" +
                "${AppUtils.get12HourTime(productModel.DeliverySlotUpto)}"
        aboutProduct.text = productModel.ProductAbout
        productBenefits.text = productModel.ProductBenefits
        productUsage.text = productModel.ProductStorageUseage
        productOtherInfo.text = productModel.ProductOtherInfo
        productCount = productModel.CartQuantity

        setupProductImageSlider()

    }

    private fun updateProductCount() {

        if (productModel?.PseudoStock == 0) {
            outOfStockLayout.visibility = View.VISIBLE
            return
        }

        if (productCount >= 1) {
            productAddToCart.visibility = View.GONE
            productQuantityWidget.visibility = View.VISIBLE

        } else {
            productAddToCart.visibility = View.VISIBLE
            productQuantityWidget.visibility = View.GONE
        }
        itemProductCartCount.text = productCount.toString()
    }


    private fun requestAddToCartApi(id: Int) {
        val map = HashMap<String, Any>()
        map["customer_id"] = AppPreference.getInstance().userData.customer_id
        map["product_id"] = id.toString()

        ApiManager.getInstance().requestApi(ApiMode.ADD_TO_CART, map, true, this, "POST")
    }

    private fun requestRemoveFromCartApi(id: Int) {
        val map = HashMap<String, Any>()
        map["customer_id"] = AppPreference.getInstance().userData.customer_id
        map["product_id"] = id.toString()

        ApiManager.getInstance().requestApi(ApiMode.REMOVE_FROM_CART, map, true, this, "POST")
    }

    private fun requestNotifyMeApi() {
        val map = HashMap<String, Any>()
        map["customer_id"] = AppPreference.getInstance().userData.customer_id
        map["product_id"] = productModel!!.id

        ApiManager.getInstance().requestApi(ApiMode.NOTIFY_ME, map, true, this, "POST")
    }

    private fun setupProductImageSlider() {
        val list = ArrayList<SlideModel>()

        if (!TextUtils.isEmpty(productModel?.ProductImagePathSmall))
            list.add(SlideModel(AppUtils.getFullImageUrl(productModel?.ProductImagePathSmall)))

        if (!TextUtils.isEmpty(productModel?.ProductImagePath1))
            list.add(SlideModel(AppUtils.getFullImageUrl(productModel?.ProductImagePath1)))

        if (!TextUtils.isEmpty(productModel?.ProductImagePath2))
            list.add(SlideModel(AppUtils.getFullImageUrl(productModel?.ProductImagePath2)))
        if (!TextUtils.isEmpty(productModel?.ProductImagePath3))
            list.add(SlideModel(AppUtils.getFullImageUrl(productModel?.ProductImagePath3)))

        if (list.isEmpty())
            list.add(SlideModel("https://cdn.samsung.com/etc/designs/smg/global/imgs/support/cont/NO_IMG_600x600.png"))

        productDetailSlider.setImageList(list)
    }


}
