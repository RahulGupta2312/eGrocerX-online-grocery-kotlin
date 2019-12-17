package com.egrocerx.callback

import android.view.View

interface OnRecyclerViewItemClick<T> {
    fun onRecyclerItemClicked(pos: Int, view: View, data: T)
}