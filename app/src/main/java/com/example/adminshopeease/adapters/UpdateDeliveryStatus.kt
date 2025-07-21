package com.example.adminshopeease.adapters

interface UpdateDeliveryStatus {
    fun updateDeliveryStatus(customerId : String , orderId : String,deliveryStatus : Int)
}