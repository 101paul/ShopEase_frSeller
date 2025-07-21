package com.example.adminshopeease.viewModel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adminshopeease.Model.OrdersRetrieveFromFB
import com.example.adminshopeease.Model.Product
import com.example.adminshopeease.objectClass.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

class AdminViewModel : ViewModel(){

    private val _isImageUploaded = MutableStateFlow(false)
    var isImageUploaded : StateFlow<Boolean> = _isImageUploaded


    private val _downloadedUrls = MutableStateFlow<ArrayList<String>>(arrayListOf())
    val downloadUrls : StateFlow<ArrayList<String>> = _downloadedUrls

    private var _isProductSaved = MutableStateFlow(false)
    var isProductSaved : StateFlow<Boolean> = _isProductSaved


    private var valueEventListener: ValueEventListener? = null

    val downloadUrlsArray = ArrayList<String>()

    fun saveImageDB(imageUri : ArrayList<Uri>,context : Context){

        viewModelScope.launch { // need to study about it
            try {
                val urls = withContext(Dispatchers.IO) {
                    coroutineScope {
                        imageUri.map { uri ->
                            async {
                                val imageRef = FirebaseStorage.getInstance().reference
                                    .child(Utils.getCurrentUserid())
                                    .child("Images/${UUID.randomUUID()}")

                                imageRef.putFile(uri).await()
                                imageRef.downloadUrl.await().toString()
                            }
                        }.awaitAll()
                    }
                }
                _downloadedUrls.value = ArrayList(urls)
                _isImageUploaded.value = true

                Utils.shutDialog(context)
                Utils.showtoast(context, "Images uploaded successfully!")

            } catch (e: Exception) {
                Utils.shutDialog(context)
                Utils.showtoast(context, "Upload failed: ${e.message}")
            }
        }
    }
//    fun getAllOrdersForCurrentUser(): Flow<List<OrdersRetrieveFromFB>> = callbackFlow {
//        val userId = Utils.getCurrentUserid()
//
//        val dbRef = FirebaseDatabase.getInstance()
//            .getReference("Admins")
//            .child("Orders")
//            .child(userId) //Now querying by userId, not orderId
//            .orderByChild("orderStatus") // Optional ordering
//
//        val listener = object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val orders = mutableListOf<OrdersRetrieveFromFB>()
//                for (data in snapshot.children) {
//                    val order = data.getValue(OrdersRetrieveFromFB::class.java)
//                    order?.let {
//                        orders.add(it.copy(OrderId = data.key ?: "")) // ✅ Include the key
//                    }
//                }
//                trySend(orders)
//                close()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                trySend(emptyList())
//                close(error.toException())
//            }
//        }
//
//        dbRef.addValueEventListener(listener)
////        awaitClose { dbRef.removeEventListener(listener) }
//        awaitClose{}
//
//    }.flowOn(Dispatchers.IO)

    fun getAllOrdersForAdmin(): Flow<List<OrdersRetrieveFromFB>> = callbackFlow {
        val dbRef = FirebaseDatabase.getInstance()
            .getReference("Admins")
            .child("Orders")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orders = mutableListOf<OrdersRetrieveFromFB>()
                for (userSnapshot in snapshot.children) { // Loop over each userId
                    for (orderSnapshot in userSnapshot.children) { // Loop over each order under user
                        val order = orderSnapshot.getValue(OrdersRetrieveFromFB::class.java)
                        order?.let {
                            orders.add(it.copy(OrderId = orderSnapshot.key ?: ""))
                        }
                    }
                }
                // 🔽 Sort the orders list by timestamp in descending order
                trySend(
                    orders.toList().sortedWith(compareByDescending { it.OrderDate })
                )
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(emptyList())
                close(error.toException())
            }
        }

        dbRef.addValueEventListener(listener)
        awaitClose { dbRef.removeEventListener(listener) }

    }.flowOn(Dispatchers.IO)

    fun getAllOrdersForCurrentUser(): Flow<List<OrdersRetrieveFromFB>> = callbackFlow {
        val userId = Utils.getCurrentUserid()

        val dbRef = FirebaseDatabase.getInstance()
            .getReference("Admins")
            .child("Orders")
            .child(userId)
            .orderByChild("orderStatus")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orders = mutableListOf<OrdersRetrieveFromFB>()
                for (data in snapshot.children) {
                    val order = data.getValue(OrdersRetrieveFromFB::class.java)
                    order?.let {
                        orders.add(it.copy(OrderId = data.key ?: ""))
                    }
                }
                trySend(orders).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(emptyList()).isSuccess
                close(error.toException())
            }
        }

        dbRef.addValueEventListener(listener)

        // ✅ Proper cleanup to avoid memory leaks and repeated calls
        awaitClose {
            dbRef.removeEventListener(listener)
        }

    }.flowOn(Dispatchers.IO)


    fun deleteProduct(productId: String, category: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val dbRef = FirebaseDatabase.getInstance().reference.child("Admins")

                // Delete from AllProducts
                dbRef.child("AllProducts").child(productId).removeValue().await()

                // Delete from productCategory
                dbRef.child("productCategory").child(category).child(productId).removeValue().await()

                Log.d("DeleteProduct", "Product deleted successfully.")
            } catch (e: Exception) {
                Log.e("DeleteProduct", "Failed to delete product: ${e.message}", e)
            }
        }
    }
    fun fetchAllProducts(category: String): Flow<List<Product>> = callbackFlow {
        val dbRef = if (category == "All") {
            FirebaseDatabase.getInstance()
                .getReference("Admins")
                .child("AllProducts")
        } else {
            FirebaseDatabase.getInstance()
                .getReference("Admins")
                .child("productCategory")
                .child(category)
        }

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = mutableListOf<Product>()
                if(snapshot.exists()) {
                    for(productSnap in snapshot.children){
                        try {
                            val product = productSnap.getValue(Product::class.java)
                            product?.let { products.add(it) }
                        } catch (e: Exception) {
                            Log.e("Firebase", "Error parsing product: ${e.message}")
                        }
                    }
                }
                trySend(products)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Database error: ${error.message}")
                trySend(emptyList()) // Send empty list on error
                close(error.toException())
            }
        }

        dbRef.addValueEventListener(listener)
        awaitClose { dbRef.removeEventListener(listener) }
    }.flowOn(Dispatchers.IO) // Ensure this runs on IO dispatcher


    fun updateOrderStatus(userId : String , orderId : String , deliveryStatus : Int){
        val ref = FirebaseDatabase.getInstance()
            .getReference("Admins")
            .child("Orders")
            .child(userId)
            .child(orderId)
            .child("orderStatus")
        ref.setValue(deliveryStatus)
    }
//    fun saveProduct(product: Product, isEditing: Boolean = false, oldCategory: String? = null) {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val db = FirebaseDatabase.getInstance().reference.child("Admins")
//
//                // If editing and category changed, delete old category node
//                if (isEditing && oldCategory != null && oldCategory != product.productCategory) {
//                    db.child("productCategory").child(oldCategory).child(product.productRandomId).removeValue().await()
//                }
//
//                // Save to AllProducts
//                db.child("AllProducts").child(product.productRandomId).setValue(product).await()
//
//                // Save to productCategory
//                db.child("productCategory").child(product.productCategory.toString()).child(product.productRandomId).setValue(product).await()
//
//                _isProductSaved.value = true
//
//            } catch (e: Exception) {
//                Log.e("SaveProduct", "Failed to save product: ${e.message}", e)
//            }
//        }
//    }

//
//fun saveProduct(product: Product, isEditing: Boolean = false, oldCategory: String = "") {
//    viewModelScope.launch(Dispatchers.IO) {
//        try {
//            val db = FirebaseDatabase.getInstance().reference.child("Admins")
//
//            //  If editing, delete old version (especially if category changed)
//            if (isEditing) {
//                db.child("AllProducts").child(product.productRandomId).removeValue().await()
//                db.child("productCategory").child(oldCategory).child(product.productRandomId).removeValue().await()
//            }
//
//            //  Save to AllProducts
//            db.child("AllProducts/${product.productRandomId}").setValue(product).await()
//
//            //  Save to productCategory
//            db.child("productCategory/${product.productCategory}/${product.productRandomId}")
//                .setValue(product).await()
//
//            _isProductSaved.value = true
//        } catch (e: Exception) {
//            Log.e("SaveProduct", "Failed to save product: ${e.message}", e)
//        }
//    }
//}

    fun saveProduct(product: Product, isEditing: Boolean = false, oldCategory: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // ✅ Validate all critical fields before saving
                if (
                    product.productTitle.isNullOrBlank() ||
                    product.productCategory.isNullOrBlank() ||
                    product.productUnit.isNullOrBlank() ||
                    product.productImageUrls.isNullOrEmpty() ||
                    product.productPrice == null || product.productPrice?:0 <= 0 ||
                    product.productQuantity == null || product.productQuantity?:0 <= 0 ||
                    product.productStock == null || product.productStock?:0 < 0
                ) {
                    Log.e("SaveProduct", "❌ Invalid product — fields are missing or invalid")
                    return@launch
                }

                val db = FirebaseDatabase.getInstance().reference.child("Admins")

                // ✅ Delete old entries if editing
                if (isEditing) {
                    db.child("AllProducts").child(product.productRandomId).removeValue().await()
                    db.child("productCategory").child(oldCategory).child(product.productRandomId).removeValue().await()
                }

                // ✅ Save to AllProducts
                db.child("AllProducts/${product.productRandomId}").setValue(product).await()

                // ✅ Save to productCategory
                db.child("productCategory/${product.productCategory}/${product.productRandomId}")
                    .setValue(product).await()

                _isProductSaved.value = true

            } catch (e: Exception) {
                Log.e("SaveProduct", "❌ Failed to save product: ${e.message}", e)
            }
        }
    }

//fun saveProduct(product: Product, isEditing: Boolean, oldCategory: String) {
//    val db = FirebaseDatabase.getInstance().getReference("Admins")
//
//    // 1. Remove from old category if category changed
//    if (isEditing && oldCategory != product.productCategory) {
//        db.child("productCategory")
//            .child(oldCategory)
//            .child(product.productRandomId!!)
//            .removeValue()
//    }
//
//    // 2. Save to updated category
//    db.child("productCategory")
//        .child(product.productCategory!!)
//        .child(product.productRandomId!!)
//        .setValue(product)
//
//    // 3. Also save under AllProducts
//    db.child("AllProducts")
//        .child(product.productRandomId!!)
//        .setValue(product)
//        .addOnSuccessListener {
//            _isProductSaved.value = true
//        }
//}


//    fun savingUpdataProducts(product : Product){
//    viewModelScope.launch(Dispatchers.IO){
//        FirebaseDatabase.getInstance().reference.child("Admins").child("AllProducts/${product.productRandomId}").setValue(product)
//        FirebaseDatabase.getInstance().reference.child("Admins").child("productCategory").child(product.productCategory.toString()).child(product.productRandomId).setValue(product)
//        }
//    }

    fun resetUploadStatus() {
        _isImageUploaded.value = false
        _downloadedUrls.value = arrayListOf()
        _isProductSaved.value = false
    }
}



