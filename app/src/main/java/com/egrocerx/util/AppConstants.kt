package com.egrocerx.util

object AppConstants {

    @JvmField
    val LOCATION_PERMISSIONS =
        arrayOf(
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.ACCESS_FINE_LOCATION"
        )
    const val LOCATION_PERMISSIONS_CODE = 201

    val ALL_PERMISSIONS = arrayOf(
        "android.permission.ACCESS_COARSE_LOCATION",
        "android.permission.ACCESS_FINE_LOCATION"
    )


    const val SMS_GATEWAY_URL =
        "http://rsms.antikinfotech.com/api/mt/SendSMS?APIKey=bPo7MnnLkEqHmIKtuxP4vg&senderid=KARLOF&channel=2&DCS=0&flashsms=0&number=[mobile]&text=[message]"
}