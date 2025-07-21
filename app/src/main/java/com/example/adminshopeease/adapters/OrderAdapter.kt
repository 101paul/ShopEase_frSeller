package com.example.adminshopeease.adapters
import com.example.adminshopeease.R
import android.content.Context

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

import com.denzcoskun.imageslider.models.SlideModel
import com.example.adminshopeease.Model.OrderedItems
import com.example.adminshopeease.databinding.CustomStatusLayoutBinding
import com.example.adminshopeease.databinding.OrdersItemViewBinding
import com.example.adminshopeease.adapters.OrderAdapter.ViewHolder1


class OrderAdapter(context : Context,
private val update : UpdateDeliveryStatus) :   RecyclerView.Adapter<ViewHolder1>(){
    val context = context
    val diffUtil = object : DiffUtil.ItemCallback<OrderedItems>(){
        override fun areItemsTheSame(
            p0: OrderedItems,
            p1: OrderedItems
        ): Boolean {
            return p0.OrderId == p1.OrderId
        }

        override fun areContentsTheSame(
            p0: OrderedItems,
            p1: OrderedItems
        ): Boolean {
            return p0 == p1
        }
    }
    val differ = AsyncListDiffer(this,diffUtil)
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder1 {
        return ViewHolder1(OrdersItemViewBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }
    override fun onBindViewHolder(
        holder: ViewHolder1,
        position: Int
    ) {
        val order = differ.currentList[position]

        val binding = holder.binding

        // 🟢 Common Fields
        binding.textOrderDate.text = "Date: ${order.date}"
        binding.textProductTitle.text = order.productTitle
        binding.textPrice.text = "₹${order.productPrice}"
        binding.textQuantity.text = "Items: ${order.productQuantity}"

        val imageSlides = ArrayList<SlideModel>()
        order.ImageUrl?.forEach { imageSlides.add(SlideModel(it)) }
        binding.rvSlideImages.setImageList(imageSlides)

        // Lottie Animation Once
        binding.lottiePayment.repeatCount = 0
        binding.lottiePayment.playAnimation()

        // Default Values
        binding.btnUpdateStatus.visibility = View.VISIBLE
        binding.btnUpdateStatus.isEnabled = true
        binding.btnUpdateStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.purple))

        when (order.OrderStatus) {
            0 -> {
                binding.textDeliveryStatus.text = "Packed"
                binding.textDeliveryStatus.setTextColor(ContextCompat.getColor(context, R.color.purple))
                binding.btnUpdateStatus.text = "Update Status"
            }
            1 -> {
                binding.textDeliveryStatus.text = "On the Way"
                binding.textDeliveryStatus.setTextColor(ContextCompat.getColor(context, R.color.purple))
                binding.btnUpdateStatus.text = "Update Status"
            }
            2 -> {
                binding.textDeliveryStatus.text = "Delivered"
                binding.textDeliveryStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark))
                binding.btnUpdateStatus.text = "Congrats! Product Delivered"
                binding.btnUpdateStatus.isEnabled = false
                binding.btnUpdateStatus.setBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray))
            }
            3 -> {
                binding.textDeliveryStatus.text = "Order Cancelled"
                binding.textDeliveryStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
                binding.textPaymentStatus.text = "Refund Process Initiated"
                binding.textPaymentStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
                binding.btnUpdateStatus.text = "Cancelled"
                binding.btnUpdateStatus.isEnabled = false
                binding.btnUpdateStatus.setBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray))
            }
            4 -> {
                binding.textDeliveryStatus.text = "Order Cancelled by the customer"
                binding.textDeliveryStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
                binding.textPaymentStatus.text = "Refund Process Initiated"
                binding.textPaymentStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
                binding.btnUpdateStatus.text = "Cancelled"
                binding.btnUpdateStatus.isEnabled = false
                binding.btnUpdateStatus.setBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray))
            }
        }

        //  Update Status Dialog
        binding.btnUpdateStatus.setOnClickListener {
//            if (order.OrderStatus == 2 ) {
//                Toast.makeText(context, "This order status cannot be changed.", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }

            val layout = CustomStatusLayoutBinding.inflate(LayoutInflater.from(context))
            val dialog = AlertDialog.Builder(context)
                .setView(layout.root)
                .setCancelable(true)
                .create()

            //  Disable buttons based on current status
            layout.btnPacked.isEnabled = order.OrderStatus < 1 // only if not packed
            layout.btnDispatched.isEnabled = order.OrderStatus < 2 // only if not dispatched
            layout.btnCancelOrder.isEnabled = order.OrderStatus < 2 // allow cancel if not delivered

            //  Packed
            layout.btnPacked.setOnClickListener {
                binding.textDeliveryStatus.text = "Packed"
                update.updateDeliveryStatus(order.orderingUserid ?: return@setOnClickListener, orderId = order.OrderId ?: return@setOnClickListener, 0)
                dialog.dismiss()
            }

            //  Dispatched
            layout.btnDispatched.setOnClickListener {
                binding.textDeliveryStatus.text = "On the Way"
                binding.textDeliveryStatus.setTextColor(ContextCompat.getColor(context, R.color.purple))

                binding.textPaymentStatus.text = "" // ✅ Clear refund text if any

                binding.btnUpdateStatus.text = "Update Status"
                binding.btnUpdateStatus.isEnabled = true
                binding.btnUpdateStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.purple))

                update.updateDeliveryStatus(
                    order.orderingUserid ?: return@setOnClickListener,
                    order.OrderId ?: return@setOnClickListener, 1
                )
                dialog.dismiss()
            }
            layout.btnDelivered.setOnClickListener{
                binding.textDeliveryStatus.text = "Your product has been deliverd successfully !"
//                binding.textDeliveryStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark))
                binding.btnUpdateStatus.isEnabled = false
                binding.btnUpdateStatus.text = "Delivered"
                binding.btnUpdateStatus.isEnabled = false
                binding.btnUpdateStatus.setBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray))


                update.updateDeliveryStatus(
                    order.orderingUserid ?: return@setOnClickListener,
                    order.OrderId ?: return@setOnClickListener ,
                    2
                )
                dialog.dismiss()
            }

            //  Cancel
            layout.btnCancelOrder.setOnClickListener {
                binding.textDeliveryStatus.text = "Order Cancelled"
                binding.textDeliveryStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
                binding.textPaymentStatus.text = "Refund Initiated"
                binding.textPaymentStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
                binding.btnUpdateStatus.text = "Cancelled"
                binding.btnUpdateStatus.isEnabled = false
                binding.btnUpdateStatus.setBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray))
                update.updateDeliveryStatus(order.orderingUserid ?: return@setOnClickListener,order.OrderId ?: return@setOnClickListener,3)
                dialog.dismiss()
            }

            dialog.show()
        }

    }

    override fun getItemCount(): Int {
        return  differ.currentList.size
    }
    class ViewHolder1(val binding: OrdersItemViewBinding) : RecyclerView.ViewHolder(binding.root)

}




