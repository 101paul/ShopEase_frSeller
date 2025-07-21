package com.example.adminshopeease.search

import android.widget.Filter
import com.example.adminshopeease.Model.Product
import com.example.adminshopeease.adapters.ProductsAdapter

class FilteringProducts(
    val adapter : ProductsAdapter ,
    val originalList : ArrayList<Product>
) : Filter() {
    override fun performFiltering(constraint: CharSequence?): FilterResults? {
        val filteredList = if (constraint.isNullOrEmpty()) {
            originalList
        } else {
            val query = constraint.toString().trim().lowercase()
            originalList.filter {
                it.productTitle?.lowercase()?.contains(query) == true ||
                        it.productCategory?.lowercase()?.contains(query) == true
            }
        }

        return FilterResults().apply {
            values = filteredList
        }
    }
    override fun publishResults(
        constraint: CharSequence?,
        results: FilterResults?
    ) {
        adapter.differ.submitList(results?.values as List<Product>)


    }
}