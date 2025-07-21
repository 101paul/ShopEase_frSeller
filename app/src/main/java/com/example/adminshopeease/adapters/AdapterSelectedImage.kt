package com.example.adminshopeease.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.adminshopeease.databinding.ListitemBinding

class AdapterSelectedImage(val imguri : ArrayList<Uri>) :
    RecyclerView.Adapter<AdapterSelectedImage.selectedimageviewholder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        position: Int
    ): selectedimageviewholder {
        val binding = ListitemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return selectedimageviewholder(binding)
    }

    override fun onBindViewHolder(
        holder: selectedimageviewholder,
        position: Int
    ) {
        val img = imguri[position]
      holder.binding.apply{
          img1.setImageURI(img)
      }
//      holder.binding.closebutton.setOnClickListener{
//          if(position < imguri.size && position >= 0) {
//              imguri.removeAt(position)
//              imguri.size - 1
//              notifyItemRemoved(position)
//          }
//      }
        holder.binding.closebutton.setOnClickListener {
            val pos = holder.adapterPosition
            if (pos != RecyclerView.NO_POSITION && pos < imguri.size) {
                imguri.removeAt(pos)
                notifyItemRemoved(pos)
                notifyItemRangeChanged(pos, imguri.size) // to rebind positions safely
            }
        }

    }

    override fun getItemCount(): Int {
        return imguri.size
    }

    class selectedimageviewholder(val binding : ListitemBinding ) :
                RecyclerView.ViewHolder(binding.root)
}