package com.example.adminshopeease.adapters

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.models.SlideModel
import com.example.adminshopeease.search.FilteringProducts
import com.example.adminshopeease.Model.Product
import com.example.adminshopeease.adapters.ProductsAdapter.productViewHolder
import com.example.adminshopeease.databinding.ItemViewProductBinding

class ProductsAdapter(val onEditbuttonClick: (Product) -> Unit) : RecyclerView.Adapter<productViewHolder>(), Filterable{

    val diffUtil = object :  DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(
            oldItem : Product,
            newItem: Product
        ): Boolean {
            return oldItem.productRandomId == newItem.productRandomId
        }

        override fun areContentsTheSame(
            oldItem: Product,
            newItem : Product
        ): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this,diffUtil)
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): productViewHolder {
    return productViewHolder(
        ItemViewProductBinding.inflate(LayoutInflater.from(parent.context),parent,false)
    )}

    override fun onBindViewHolder(holder: productViewHolder, position: Int) {
        holder.binding.apply{
            var current_product = differ.currentList[position]

            var imageList = ArrayList<SlideModel>()

//            current_product.productStock?.toInt()?.let {
//                if(it <= 0){
//                    holder.binding.tvStockStatus.text = "Out of Stock"
//                    holder.binding.tvStockStatus.setTextColor(Color.RED)
//                    holder.binding.tvStockStatus.setTypeface(null,Typeface.BOLD)
//                }else{
//                    holder.binding.tvStockStatus.text = "In Stock $it"
//                    holder.binding.tvStockStatus.setTextColor(Color.parseColor("#4CAF50")) // green
//                    holder.binding.tvStockStatus.setTypeface(null, Typeface.BOLD)
//                }
//            }
            val stock = current_product.productStock ?: 0
            if (stock <= 0) {
                holder.binding.tvStockStatus.text = "Out of Stock"
                holder.binding.tvStockStatus.setTextColor(Color.RED)
                holder.binding.tvStockStatus.setTypeface(null, Typeface.BOLD)
            }
            if(stock>0) {
                holder.binding.tvStockStatus.text = "In Stock: $stock"
                holder.binding.tvStockStatus.setTextColor(Color.WHITE)
                holder.binding.tvStockStatus.setTypeface(null, Typeface.BOLD)
            }
            current_product.productImageUrls?.forEach { imageUrl ->
                imageList.add(SlideModel(imageUrl.toString()))
            }
            tvProductTitle.text = current_product.productTitle
            val quantity =  current_product.productQuantity.toString() + current_product.productUnit.toString()
            tvProductQuantity.text = quantity
            val price =  "₹"+current_product.productPrice.toString()
            tvProductPrice.text = price
            ivImageSldier.setImageList(imageList)

        }
//       holder.binding.tvEditText.setOnClickListener{
//           onEditbuttonClick(differ.currentList[position])
//       }

        holder.binding.tvEditText.setOnClickListener {
            val adapterPosition = holder.adapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION &&
                adapterPosition < differ.currentList.size) {
                onEditbuttonClick(differ.currentList[adapterPosition])
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var originalList = listOf<Product>()
   fun submitListWithOriginal(list: List<Product>) {
    originalList = list // Keep unfiltered copy
    differ.submitList(list)
   }

    override fun getFilter(): Filter {
        return FilteringProducts(this, ArrayList(originalList))
    }
    class productViewHolder(val binding : ItemViewProductBinding ) : RecyclerView.ViewHolder(binding.root)
}