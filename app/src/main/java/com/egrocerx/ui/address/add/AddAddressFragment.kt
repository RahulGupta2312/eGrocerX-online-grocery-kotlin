package com.egrocerx.ui.address.add


import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.RadioButton
import androidx.navigation.fragment.findNavController
import com.google.gson.JsonObject
import com.egrocerx.R
import com.egrocerx.base.BaseFragment
import com.egrocerx.data.AddressModel
import com.egrocerx.network.ApiManager
import com.egrocerx.network.ApiMode
import com.egrocerx.network.ApiResponse
import com.egrocerx.util.AppPreference
import com.egrocerx.util.AppUtils
import kotlinx.android.synthetic.main.fragment_add_address.*
import java.util.*
import kotlin.collections.HashMap

class AddAddressFragment : BaseFragment(), ApiResponse {


    private lateinit var mAddAddressFragmentArgs: AddAddressFragmentArgs

    private var addressType = "HOME"
    private var mode = 1
    private lateinit var addressModel: AddressModel

    override fun getLayoutId(): Int {
        return R.layout.fragment_add_address
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAddAddressFragmentArgs = AddAddressFragmentArgs.fromBundle(arguments!!)
        mode = mAddAddressFragmentArgs.mode
        if (mAddAddressFragmentArgs.data != null)
            addressModel = mAddAddressFragmentArgs.data!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateUI()
        handleSubmitButton()
        handleAddressType()
    }

    private fun handleAddressType() {
        addressTypeRadioGrp.setOnCheckedChangeListener { radioGroup, i ->
            addressType =
                radioGroup.findViewById<RadioButton>(radioGroup.checkedRadioButtonId)
                    .text.toString().toUpperCase(Locale.ENGLISH)
        }
    }

    private fun handleSubmitButton() {
        btnSubmitAddress.setOnClickListener {
            validateForm()
        }
    }

    private fun validateForm() {
        when {
            TextUtils.isEmpty(etFullName.text.toString()) -> AppUtils.shortToast("Enter a valid name")
            TextUtils.isEmpty(etPhone.text.toString()) -> AppUtils.shortToast("Phone cannot be empty")
            etPhone.text.toString().length < 10 -> AppUtils.shortToast("Enter a valid 10 digit phone number")
            TextUtils.isEmpty(etHouseNumber.text.toString()) -> AppUtils.shortToast("Enter a valid house number")
            TextUtils.isEmpty(etLocality.text.toString()) -> AppUtils.shortToast("Enter a valid locality")
            TextUtils.isEmpty(etLandmark.text.toString()) -> AppUtils.shortToast("Enter a valid landmark")
            TextUtils.isEmpty(etCity.text.toString()) -> AppUtils.shortToast("Enter a valid city")
            TextUtils.isEmpty(etState.text.toString()) -> AppUtils.shortToast("Enter a valid city")
            TextUtils.isEmpty(etPincode.text.toString()) -> AppUtils.shortToast("Enter a valid pincode")
            etPincode.text.toString().length < 6 -> AppUtils.shortToast("Pincode must be of 6 digit")
            else -> {
                requestSubmitAddressApi()
            }
        }
    }

    private fun populateUI() {
        if (mAddAddressFragmentArgs.mode == 2) { // edit mode
            etFullName.setText(addressModel.full_name)
            etPhone.setText(addressModel.phone_no)
            etHouseNumber.setText(addressModel.house_no)
            etLocality.setText(addressModel.locality)
            etLandmark.setText(addressModel.landmark)
            etCity.setText(addressModel.city)
            etState.setText(addressModel.state)
            etPincode.setText(addressModel.pincode)

            if (addressModel.type == "HOME") {
                addressTypeRadioGrp.check(R.id.addressTypeHome)
            }
            if (addressModel.type == "OFFICE") {
                addressTypeRadioGrp.check(R.id.addressTypeOffice)
            }
        }
    }

    private fun requestSubmitAddressApi() {
        val map = HashMap<String, Any>()
        map["customer_id"] = AppPreference.getInstance().userData.customer_id
        map["full_name"] = etFullName.text.toString()
        map["house_no"] = etHouseNumber.text.toString()
        map["locality"] = etLocality.text.toString()
        map["landmark"] = etLandmark.text.toString()
        map["city"] = etCity.text.toString()
        map["state"] = etState.text.toString()
        map["type"] = addressType
        map["pincode"] = etPincode.text.toString()
        map["phone_no"] = etPhone.text.toString()
        map["mode"] = mode
        if (mode == 2)
            map["id"] = addressModel.id

        ApiManager.getInstance().requestApi(ApiMode.ADD_ADDRESS, map, true, this, "POST")
    }


    override fun onSuccess(jsonObject: JsonObject?, mode: ApiMode?) {
        AppUtils.shortToast(jsonObject?.get("message")?.asString)
        findNavController().popBackStack()
    }

    override fun onFailure(errorObject: JsonObject?, mode: ApiMode?) {
        AppUtils.shortToast(errorObject?.get("message")?.asString)
    }

    override fun onException(e: Exception?, mode: ApiMode?) {
        e?.printStackTrace()
        AppUtils.showException()
    }
}
