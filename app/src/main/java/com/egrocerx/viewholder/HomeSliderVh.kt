package com.egrocerx.viewholder


import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.models.SlideModel
import com.egrocerx.data.HomeSliderModel
import com.egrocerx.util.AppUtils
import kotlinx.android.synthetic.main.item_slider.view.*

class HomeSliderVh(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(model: HomeSliderModel) {
        if (model.sliderList.isNotEmpty()) {
            val list = ArrayList<SlideModel>()
            model.sliderList.forEach {
                list.add(SlideModel(AppUtils.getFullImageUrl(it.ImgPath)))
            }
            itemView.homeImageSlider.setImageList(list)
        }
    }

}