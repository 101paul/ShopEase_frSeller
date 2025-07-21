package com.example.adminshopeease.MainFragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminshopeease.Model.OrderedItems
import com.example.adminshopeease.Model.PushNotificationRequest
import com.example.adminshopeease.R
import com.example.adminshopeease.adapters.OrderAdapter
import com.example.adminshopeease.adapters.UpdateDeliveryStatus
import com.example.adminshopeease.databinding.FragmentOrderBinding
import com.example.adminshopeease.viewModel.AdminViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.getValue
import kotlin.text.append
import kotlin.toString

class OrderFragment : Fragment() , UpdateDeliveryStatus{
    private lateinit var binding: FragmentOrderBinding
    private lateinit var adapter: OrderAdapter
    private val viewModel: AdminViewModel by viewModels()
    private var shrimmerJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        getAllOrder()

    }

    private fun setupUi() {
        adapter = OrderAdapter(requireContext(),this)
        binding.rvOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOrders.adapter = adapter

        binding.noorder.visibility = View.GONE
        binding.rvOrders.visibility = View.GONE
        binding.shimmer.visibility = View.VISIBLE

        shrimmerJob?.cancel()
        shrimmerJob = viewLifecycleOwner.lifecycleScope.launch {
            delay(10_000) // 10sec
            if (adapter.itemCount == 0) {
                binding.shimmer.visibility = View.GONE
                binding.noorder.visibility = View.VISIBLE
            }
        }    }


    private fun getAllOrder() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getAllOrdersForAdmin().collect { orderList ->
                    if (!orderList.isNullOrEmpty()) {
                        val orderedList = mutableListOf<OrderedItems>()

                        for (order in orderList) {
                            var totalPrice = 0
                            val imageUrls = ArrayList<String>()
                            val title = StringBuilder()


                            val userId = order.orderingUserid.orEmpty()

                            if (userId.isNotEmpty()) {
                                order.OrderList?.forEach { item ->
                                    val price = item.productPrice?.toIntOrNull() ?: 0
                                    val quantity = item.itemCount ?: 0

                                    totalPrice += price * quantity

                                    item.productTitle?.let {
                                        title.append(it).append(", ")
                                    }

//                                item.productImageUrls?.let {
//                                    if (it.isNotEmpty()) imageUrls.addAll(it)
                                    val image = item.productImageUrls
                                    if (!image.isNullOrEmpty()) {
                                        imageUrls.add(image)  // ✅ Works for a single image URL
                                    }

                                    val orderedItem = OrderedItems(
                                        OrderId = order.OrderId ?: "N/A",
                                        date = order.OrderDate ?: "",
                                        OrderStatus = order.orderStatus ?: 0,
                                        productTitle = title.trimEnd(',', ' ').toString(),
                                        productPrice = totalPrice.toString(),
                                        productQuantity = order.totalItemcount.toString().toInt() ,
                                        ImageUrl = imageUrls,
                                        orderingUserid = order.orderingUserid ?: "",
                                    )

                                    orderedList.add(orderedItem)
                                }

                                binding.rvOrders.visibility = View.VISIBLE
                                binding.shimmer.visibility = View.GONE
                                binding.noorder.visibility = View.GONE
                                adapter.differ.submitList(orderedList)
                            } else {
                                binding.noorder.visibility = View.VISIBLE
                            }
                        }
                    }

                }
            }
        }}

    override fun updateDeliveryStatus(customerId: String, orderId: String , deliveryStatus : Int) {
        viewModel.updateOrderStatus(customerId , orderId, deliveryStatus)
    }
}

