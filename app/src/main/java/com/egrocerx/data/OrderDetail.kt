package com.egrocerx.data


import com.google.gson.annotations.SerializedName

data class OrderDetail(
    val address: String = "", // \nName: Rahul Gupta \nHouse No: Chdfn\nLocality: Dhdjf\nCity: Xnxn\nState: Xbxnx\nPincode: 742101
    @SerializedName("address_id")
    val addressId: String = "", // 5
    @SerializedName("BaseValue")
    val baseValue: String = "", // 76.27
    val cancelDate: String? = null, // null
    val cancelDatetime: String? = null, // null
    @SerializedName("cause_of_cancel")
    val causeOfCancel: String? = null, // null
    @SerializedName("CgstAmount")
    val cgstAmount: String = "", // 6.865
    @SerializedName("created_at")
    val createdAt: String = "", // 2019-09-20 12:10:02
    @SerializedName("customer_id")
    val customerId: String = "", // 1563821848
    @SerializedName("delivery_charge")
    val deliveryCharge: String = "", // 0
    @SerializedName("delivery_date")
    val deliveryDate: String = "", // 20 Sep, 01:00 pm - 02:59 pm
    @SerializedName("discount_amount")
    val discountAmount: String = "", // 0
    @SerializedName("GoCashPay")
    val goCashPay: String = "", // 90
    val id: String = "", // 59
    @SerializedName("IgstAmount")
    val igstAmount: String = "", // 0
    @SerializedName("InvoiceAmount")
    val invoiceAmount: String = "", // 90
    val items: List<Item> = listOf(),
    @SerializedName("NetAmount")
    val netAmount: String = "", // 90
    @SerializedName("order_date")
    val orderDate: String = "", // 2019-09-20
    @SerializedName("order_datetime")
    val orderDatetime: String = "", // 1568961602
    @SerializedName("order_id")
    val orderId: String = "", // 17131
    @SerializedName("order_status")
    val orderStatus: String = "", // New
    @SerializedName("order_status_id")
    val orderStatusId: Int = -1, // New
    @SerializedName("payment_code")
    val paymentCode: String = "", // TXN_GOCASH
    @SerializedName("Promocode")
    val promocode: String = "",
    @SerializedName("PromocodeDiscount")
    val promocodeDiscount: String = "", // 0
    @SerializedName("RedeemAmount")
    val redeemAmount: String = "", // 0
    @SerializedName("round_off_amount")
    val roundOffAmount: String = "", // 0
    @SerializedName("SgstAmount")
    val sgstAmount: String = "", // 6.865
    val storeId: String = "", // 1
    @SerializedName("updated_at")
    val updatedAt: String = "" // 2019-09-20 12:10:02
) {
    data class Item(
        @SerializedName("BaseValue")
        val baseValue: String = "", // 76.2712
        @SerializedName("CgstAmount")
        val cgstAmount: String = "", // 6.86
        @SerializedName("created_at")
        val createdAt: String = "", // 2019-09-20 12:10:02
        val id: Int = 0, // 69
        @SerializedName("IgstAmount")
        val igstAmount: String = "", // 0
        @SerializedName("net_amount")
        val netAmount: String = "", // 90
        @SerializedName("order_id")
        val orderId: String = "", // 17131
        @SerializedName("product_category_id")
        val productCategoryId: String = "", // 2
        @SerializedName("product_id")
        val productId: String = "", // 39
        @SerializedName("product_subcategory_id")
        val productSubcategoryId: String = "", // 11
        val qty: Int = 0, // 1
        @SerializedName("RoundOffAmount")
        val roundOffAmount: String = "", // 0
        @SerializedName("SgstAmount")
        val sgstAmount: String = "", // 6.86
        @SerializedName("updated_at")
        val updatedAt: String = "", // 2019-09-20 12:10:02
        val ProductName: String = ""
    ) {
        override fun equals(other: Any?): Boolean {
            return this.id == (other as Item).id
        }

        override fun hashCode(): Int {
            return super.hashCode()
        }
    }
}