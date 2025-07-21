package com.example.adminshopeease.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.adminshopeease.Model.Category
import com.example.adminshopeease.adapters.CategorisAdapter.categoryViewHolder
import com.example.adminshopeease.databinding.CategoryBinding

class CategorisAdapter(val arr: ArrayList<Category>, val onCategoryClicked: (Category) -> Unit) :
          RecyclerView.Adapter<categoryViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        position: Int
    ): categoryViewHolder {
        val binding = CategoryBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return categoryViewHolder(binding)
    }

    override fun onBindViewHolder(
        Holder: categoryViewHolder,
        position: Int
    ) {
        val category = arr[position]
        Holder.binding.apply{
            categoryName.text = category.name.toString()
            categoryImage.setImageResource(category.Image!!.toInt())
        }
        Holder.itemView.setOnClickListener{
            onCategoryClicked(category)
        }
    }

    override fun getItemCount(): Int {
        return arr.size
    }

    class categoryViewHolder(val binding : CategoryBinding ) : RecyclerView.ViewHolder(binding.root){}


}