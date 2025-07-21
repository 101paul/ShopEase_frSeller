package com.example.adminshopeease.Model

data class OrderedItems(
    var productTitle: String = "",
    var productQuantity: Int = 0,
    var productUnit: String = "",
    var productPrice: String = "",
    var productCategory: String = "",
    var itemCount: Int = 0,
    var date : String="",
    var OrderId : String = "N/A",
    var orderingUserid : String = "",
    var OrderStatus : Int = 0 ,
    var ImageUrl : ArrayList<String> ?=null
)
