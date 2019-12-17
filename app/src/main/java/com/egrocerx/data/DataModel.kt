package com.egrocerx.data

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken


data class UserModel(
    val id: String,
    val e_mail: String?,
    val name: String,
    val mobile: String,
    val mobile_verified: Int = 0,
    val customer_id: String,
    val check_premium: String
)

data class AccountItemModel(
    val id: Int,
    val name: String,
    val navDirectionId: Int?,
    val iconResId: Int
)


data class ProductModel(
    val id: Int,
    val ProductName: String,
    val ProductMrp: Float,
    val ProductSubscriptionPrice: Float,
    val ProductOfferprice: Float,
    val ProductClubprice: Float, @SerializedName(
        "CartQuantity",
        alternate = ["cartQuantity"]
    ) var CartQuantity: Int,
    val ProductImagePathSmall: String?,
    val packing_name: String,
    val ProductSgst: Double = 0.0,
    val ProductCgst: Double = 0.0,
    val ProductIgst: Double = 0.0,
    val PseudoStock: Int = 0
) {

    companion object {
        fun getProductsList(jsonObject: JsonObject?): ArrayList<ProductModel> {
            val typeToken = object : TypeToken<ArrayList<ProductModel>>() {}.type
            return Gson().fromJson(jsonObject?.getAsJsonArray("data"), typeToken)
        }
    }

}

data class ProductDetailModel(
    val id: Int,
    val ProductName: String,
    val ProductMrp: Float,
    val ProductSubscriptionPrice: Float,
    val ProductOfferprice: Float,
    val ProductClubprice: Float, @SerializedName(
        "CartQuantity",
        alternate = ["cartQuantity"]
    ) var CartQuantity: Int=0,
    val ProductImagePathSmall: String?,
    val ProductImagePath1: String?,
    val ProductImagePath2: String?,
    val ProductImagePath3: String?,
    val packing_name: String,
    val ProductSgst: Double = 0.0,
    val ProductCgst: Double = 0.0,
    val ProductIgst: Double = 0.0,
    val DeliverySlotFrom: String? = "",
    val DeliverySlotUpto: String? = "",
    val ProductStorageUseage: String? = "",
    val ProductBenefits: String? = "",
    val ProductOtherInfo: String? = "",
    val ProductAbout: String? = "",
    val PseudoStock: Int = 0
) {
//    fun getAboutProduct(): String {
//        return ProductAbout + "\n\nBenefits" + ProductBenefits + "\n\nUsage" + ProductStorageUseage
//    }
}


/*
*  {
      "id": "1",
      "ProductId": "1",
      "ProductName": "Strawberry",
      "ProductImagePathSmall": "",
      "Quantity": "2",
      "ProductMrp": "186.25",
      "ProductOfferprice": "149",
      "ProductPack": "11",
      "total_cost": "372.5",
      "discounted_cost": "298",
      "packing_name": "200 Gm",
      "total_saved": "74.5"
    }
* */

data class BasketModel(
    val id: Int,
    val ProductId: String,
    val ProductName: String,
    val ProductImagePathSmall: String,
    var Quantity: String,
    val ProductMrp: String,
    val ProductOfferprice: String,
    val ProductClubPrice: String,
    val ProductPack: String,
    var total_cost: String,
    var discounted_cost: String,
    var club_cost: String,
    val packing_name: String,
    var total_saved: String,
    var club_saved: String,
    val ProductSgst: Double = 0.0,
    val ProductCgst: Double = 0.0,
    val ProductIgst: Double = 0.0
)


data class OfferModel(
    val id: Int,
    val OfferTitle: String?,
    val Description: String?,
    val ImagePath: String?
)

data class AddressModel(
    val id: Int,
    val full_name: String?,
    val phone_no: String?,
    val house_no: String?,
    val locality: String?,
    val landmark: String?,
    val city: String?,
    val state: String?,
    val type: String?,
    val pincode: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    fun getFullAddress(): String {
        return arrayListOf(full_name, phone_no, house_no, locality, landmark, city, state, pincode)
            .joinToString(separator = ",")
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(full_name)
        parcel.writeString(phone_no)
        parcel.writeString(house_no)
        parcel.writeString(locality)
        parcel.writeString(landmark)
        parcel.writeString(city)
        parcel.writeString(state)
        parcel.writeString(type)
        parcel.writeString(pincode)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AddressModel> {
        override fun createFromParcel(parcel: Parcel): AddressModel {
            return AddressModel(parcel)
        }

        override fun newArray(size: Int): Array<AddressModel?> {
            return arrayOfNulls(size)
        }
    }
}

data class NotificationModel(
    val id: Int,
    val title: String, @SerializedName("notification") val description: String?,
    @SerializedName("notification_img_path") val ImagePath: String?
)

data class SubCategoryModel(
    val id: Int,
    val CategoryId: Int,
    val SubcategoryL1Name: String,
    val SubCategoryIconPath: String?
)

data class WalletHistoryModel(
    val id: Int, val TransactionId: String, val CustomerId: String, val TransactionDate: String,
    val TransactionNote: String, val Credit: Int, val Debit: Int
)

data class OrderDetailItem(val title: String, val value: String)
/*
*  {
      "order_id": "15665545",
      "order_date": "2019-08-23",
      "NetAmount": "1752",
      "order_status": "1"
    }*/
data class OrderHistoryItem(
    @SerializedName("order_id") val orderId: String,
    @SerializedName("order_date") val orderDate: String,
    @SerializedName("NetAmount") val netAmount: String,
    @SerializedName("order_status") val orderStatus: String
)

data class SubscriptionHistoryItem(
    val id: String,
    val SubscriptionName: String,
    val ProductSubscriptionPrice: Double = 0.0,
    val ProductName: String,
    var SubscriptionStatus: String?,
    val SubscriptionStartDate: String,
    val packing_name: String,
    val ProductQty: String
)

data class MembershipItem(
    val id: Int,
    val name: String,
    val description: String?,
    val days: Int,
    val price: Double = 0.0,
    val sale_price: Double = 0.0,
    var isCurrentSubscription: Boolean = false,
    var enabled:Boolean=false
)