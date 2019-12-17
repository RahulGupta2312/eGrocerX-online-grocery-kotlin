package com.egrocerx.data


import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Order(
    @SerializedName("address_id")
    val addressId: String? = "", // 5
    @SerializedName("BaseValue")
    val baseValue: String? = "", // 277.41
    val cancelDate: String? = null, // null
    val cancelDatetime: String? = null, // null
    @SerializedName("cause_of_cancel")
    val causeOfCancel: String? = null, // null
    @SerializedName("CgstAmount")
    val cgstAmount: String? = "", // 24.965
    @SerializedName("created_at")
    val createdAt: String? = "", // 2019-08-24 21:21:57
    @SerializedName("customer_id")
    val customerId: String? = "", // 1563821848
    @SerializedName("delivery_charge")
    val deliveryCharge: String? = "", // 0
    @SerializedName("delivery_date")
    val deliveryDate: String? = "", // 2019-11-06 21:26:42
    @SerializedName("discount_amount")
    val discountAmount: String? = "", // 0
    @SerializedName("GoCashPay")
    val goCashPay: String? = "", // 0
    val id: String? = "", // 11
    @SerializedName("IgstAmount")
    val igstAmount: String? = "", // 0
    @SerializedName("InvoiceAmount")
    val invoiceAmount: String? = "", // 327.34
    @SerializedName("NetAmount")
    val netAmount: String? = "", // 327.34
    @SerializedName("order_date")
    val orderDate: String? = "", // 2019-08-24
    @SerializedName("order_datetime")
    val orderDatetime: String? = "", // 1566661917
    @SerializedName("order_id")
    val orderId: String? = "", // 15666619
    @SerializedName("order_status")
    val orderStatus: String? = "", // 1
    @SerializedName("payment_code")
    val paymentCode: String? = "", // TXN_UNPAID
    @SerializedName("Promocode")
    val promocode: String? = "",
    @SerializedName("PromocodeDiscount")
    val promocodeDiscount: String? = "", // 0
    @SerializedName("RedeemAmount")
    val redeemAmount: String? = "", // 0
    @SerializedName("round_off_amount")
    val roundOffAmount: String? = "", // 0
    @SerializedName("SgstAmount")
    val sgstAmount: String? = "", // 24.965
    val storeId: String? = "", // 1
    @SerializedName("updated_at")
    val updatedAt: String? = "" // 2019-08-24 21:21:57
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(addressId)
        parcel.writeString(baseValue)
        parcel.writeString(cancelDate)
        parcel.writeString(cancelDatetime)
        parcel.writeString(causeOfCancel)
        parcel.writeString(cgstAmount)
        parcel.writeString(createdAt)
        parcel.writeString(customerId)
        parcel.writeString(deliveryCharge)
        parcel.writeString(deliveryDate)
        parcel.writeString(discountAmount)
        parcel.writeString(goCashPay)
        parcel.writeString(id)
        parcel.writeString(igstAmount)
        parcel.writeString(invoiceAmount)
        parcel.writeString(netAmount)
        parcel.writeString(orderDate)
        parcel.writeString(orderDatetime)
        parcel.writeString(orderId)
        parcel.writeString(orderStatus)
        parcel.writeString(paymentCode)
        parcel.writeString(promocode)
        parcel.writeString(promocodeDiscount)
        parcel.writeString(redeemAmount)
        parcel.writeString(roundOffAmount)
        parcel.writeString(sgstAmount)
        parcel.writeString(storeId)
        parcel.writeString(updatedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Order> {
        override fun createFromParcel(parcel: Parcel): Order {
            return Order(parcel)
        }

        override fun newArray(size: Int): Array<Order?> {
            return arrayOfNulls(size)
        }
    }
}