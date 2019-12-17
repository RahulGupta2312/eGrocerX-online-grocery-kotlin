package com.egrocerx.reciever

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status


class SmsBroadcastReceiver : BroadcastReceiver() {


    lateinit var callback: OnOtpReceivedCallback

    fun setListener(callback: OnOtpReceivedCallback) {
        this.callback = callback
    }


    override fun onReceive(context: Context, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            val status = extras!!.get(SmsRetriever.EXTRA_STATUS) as Status?

            when (status!!.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    // Get SMS message contents
                    val message = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String

                    val code = message.substring(0, 6)
                    callback.onOtpReceived(code)
                }

                CommonStatusCodes.TIMEOUT -> {

                }
            }
        }
    }

    interface OnOtpReceivedCallback {
        fun onOtpReceived(otp: String)
    }
}
