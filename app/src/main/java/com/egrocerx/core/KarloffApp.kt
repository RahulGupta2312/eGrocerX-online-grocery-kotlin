package com.egrocerx.core

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.interceptors.HttpLoggingInterceptor
import com.crashlytics.android.Crashlytics
import com.google.firebase.FirebaseApp
import com.egrocerx.BuildConfig
import com.egrocerx.ui.login.LoginActivity
import com.egrocerx.util.AppPreference
import com.egrocerx.util.launchActivity
import io.fabric.sdk.android.Fabric

class MyApplication : Application() {
    private lateinit var context: AppCompatActivity

    companion object {
        lateinit var instance: MyApplication
    }


    override fun onCreate() {
        instance = this
        super.onCreate()
        AndroidNetworking.initialize(this)
        if (BuildConfig.DEBUG)
            AndroidNetworking.enableLogging(HttpLoggingInterceptor.Level.BODY)
        FirebaseApp.initializeApp(this)

    }

    fun setContext(ctx: AppCompatActivity) {
        context = ctx
    }

    fun getContext(): AppCompatActivity {
        return context
    }

    fun logoutUser() {
        AppPreference.getInstance().clearAllPreferences()
        launchActivity<LoginActivity> {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        instance.getContext().finish()
    }
}

/*App hash: vQNSYE6y8qV*/