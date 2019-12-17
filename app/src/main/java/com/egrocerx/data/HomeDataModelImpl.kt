package com.egrocerx.data

import com.egrocerx.R

data class HomeBannerModel(
    val id: String, val ImageName: String, val ImgPath: String?, val SubCategoryId: String,
    val SubCategoryName: String = "", val products:ArrayList<ProductModel>
) :
    BaseListItemModel {
    override fun getLayoutId(): Int {
        return R.layout.item_banner
    }
}

data class ItemSlider(val id: String, val ImageName: String, val ImgPath: String?)

data class HomeSliderModel(val sliderList: List<ItemSlider>) : BaseListItemModel {
    override fun getLayoutId(): Int {
        return R.layout.item_slider
    }
}

data class HomeCategoriesModel(val title: String = "", val categories: List<CategoryModel>) :
    BaseListItemModel {
    override fun getLayoutId(): Int {
        return R.layout.item_categories_recycler
    }
}

data class SubCategoriesModel(val title: String = "", val subcategories: List<SubCategoryModel>) :
    BaseListItemModel {
    override fun getLayoutId(): Int {
        return R.layout.item_categories_recycler
    }
}

data class ProductsModel(
    val title: String = "",
    val products: ArrayList<ProductModel>,
    val background: String = "#ffffff"
) : BaseListItemModel {
    override fun getLayoutId(): Int {
        return R.layout.item_products_recycler
    }
}

data class HomeTitleModel(val data: String) : BaseListItemModel {
    override fun getLayoutId(): Int {
        return R.layout.item_sub_toolbar
    }
}

data class HomeDeliverySlotModel(val DeliverySlotFrom: String, val DeliverySlotUpto: String) :
    BaseListItemModel {
    override fun getLayoutId(): Int {
        return R.layout.item_delivery_slot_home
    }
}