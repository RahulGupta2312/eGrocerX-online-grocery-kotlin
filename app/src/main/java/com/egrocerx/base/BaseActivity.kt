package com.egrocerx.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.egrocerx.core.MyApplication
import com.egrocerx.network.ApiManager
import com.egrocerx.network.ApiMode
import com.egrocerx.network.ApiResponse

abstract class BaseActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MyApplication.instance.setContext(this)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    fun onBackClick(view: View) {
        super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        MyApplication.instance.setContext(this)
    }

    fun sendMessage(msg: String, mobile: String, response: ApiResponse) {
        ApiManager.getInstance().sendMessage(msg, mobile, ApiMode.SEND_MSG, response)
    }


}