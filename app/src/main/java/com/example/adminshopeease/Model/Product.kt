package com.example.adminshopeease.Model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class Product(
   var productRandomId: String = UUID.randomUUID().toString(),
   var productTitle: String? = null,
   var productQuantity: Int? = null,
   var productUnit: String? = null,
   var productPrice: Int? = null,
   var productStock: Int? = null,
   var productCategory: String? = null,
   var itemCount: Int? = null,
   var adminUid: String? = null,
   var productImageUrls: ArrayList<String>? = null
) : Parcelable
