package com.egrocerx.ui.categories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.egrocerx.R
import com.egrocerx.callback.OnRecyclerViewItemClick
import com.egrocerx.core.MyApplication
import com.egrocerx.data.CategoryModel
import com.egrocerx.data.Subcategory
import com.egrocerx.util.AppUtils
import kotlinx.android.synthetic.main.item_categories_list.view.*
import java.util.*

class CategoriesAdapter(
    val requestManager: RequestManager,
    val callback: OnRecyclerViewItemClick<Subcategory>
) :
    RecyclerView.Adapter<CategoriesAdapter.CategoryVh>() {

    private val list = ArrayList<CategoryModel>()

    lateinit var subcategoriesAdapter: SubcategoriesAdapter

    private var layoutInflater: LayoutInflater =
        LayoutInflater.from(MyApplication.instance.getContext())


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryVh {
        return CategoryVh(layoutInflater.inflate(R.layout.item_categories_list, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemId(position: Int): Long {
        return list[position].id.toLong()
    }

    override fun onBindViewHolder(holder: CategoryVh, position: Int) {
        holder.bind(list[holder.adapterPosition])
    }


    fun addData(categories: ArrayList<CategoryModel>) {
        clearDataSet()
        list.addAll(categories)
        notifyDataSetChanged()
    }

    private fun clearDataSet() {
        list.clear()
        notifyDataSetChanged()
    }


    inner class CategoryVh(itemView: View) : RecyclerView.ViewHolder(itemView),
        OnRecyclerViewItemClick<Subcategory> {


        fun bind(model: CategoryModel) {
            itemView.itemCategoryName.text = model.CategoryName

            if (model.subcategories.isNotEmpty()) {
                itemView.itemCategoryExpand.visibility = View.VISIBLE
                setupSubcategoriesAdapter(model.subcategories)
                itemView.setOnClickListener {
                    if (itemView.itemCategorySubcategoriesList.visibility == View.VISIBLE) {
                        itemView.itemCategorySubcategoriesList.visibility = View.GONE
                        itemView.itemCategoryExpand.rotation = 360.0f
                    } else {
                        itemView.itemCategorySubcategoriesList.visibility = View.VISIBLE
                        itemView.itemCategoryExpand.rotation = 90.0f
                    }
                }
            }

            requestManager.load(AppUtils.getFullImageUrl(model.CategoryIconPath))
                .thumbnail(0.25f)
                .apply(
                    RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.placeholder_grey_rounded)
                        .encodeQuality(40)
                )
                .into(itemView.itemCategoryImage)
        }

        private fun setupSubcategoriesAdapter(subcategories: List<Subcategory>) {
            subcategoriesAdapter = SubcategoriesAdapter(subcategories, this)
            itemView.itemCategorySubcategoriesList.apply {
                addItemDecoration(
                    DividerItemDecoration(
                        this.context,
                        DividerItemDecoration.VERTICAL
                    )
                )
                adapter = subcategoriesAdapter
            }

        }

        override fun onRecyclerItemClicked(pos: Int, view: View, data: Subcategory) {
            callback.onRecyclerItemClicked(pos, view, data)
        }


    }

}