package com.egrocerx.base

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    protected var mView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (mView == null)
            mView = inflater.inflate(getLayoutId(), container, false)

        return mView
    }

    abstract fun getLayoutId(): Int


    fun showAlertDialog(message: String?) {
        AlertDialog.Builder(activity)
            .setMessage(message)
            .setCancelable(false)
            .setNeutralButton("OKAY") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()
            .show()
    }
}