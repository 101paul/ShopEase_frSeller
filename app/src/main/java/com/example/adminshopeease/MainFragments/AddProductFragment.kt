package com.example.adminshopeease.MainFragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.adminshopeease.activity.AdminActivity
import com.example.adminshopeease.adapters.AdapterSelectedImage
import com.example.adminshopeease.databinding.FragmentAddProductBinding
import com.example.adminshopeease.objectClass.Utils
import com.example.adminshopeease.Model.Product
import com.example.adminshopeease.R
import com.example.adminshopeease.objectClass.productData
import com.example.adminshopeease.viewModel.AdminViewModel
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import java.util.UUID

class AddProductFragment : Fragment() {
    private lateinit var binding: FragmentAddProductBinding
    private var imageUrls: ArrayList<Uri> = arrayListOf()
    private val viewModel: AdminViewModel by viewModels()
    private var existingProduct: Product? = null
    var isSaveTriggered : Boolean = false
    val getMultipleImage =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { listofurl ->
            imageUrls.clear()
            val fiveImages = listofurl.take(5)
            imageUrls.addAll(fiveImages)
            binding.rvProductImage.adapter = AdapterSelectedImage(imageUrls)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAddProductBinding.inflate(layoutInflater, container, false)
        isSaveTriggered = false

        // Check if we are editing an existing product
        existingProduct = arguments?.getParcelable("productToEdit")

        existingProduct?.let { product ->
            binding.ProductTitle.setText(product.productTitle)
            binding.productQuantity.setText(product.productQuantity?.toString() ?: "")
            binding.unit.setText(product.productUnit ?: "")
            binding.ProductPrice.setText(product.productPrice?.toString() ?: "")
            binding.noofstocks.setText(product.productStock?.toString() ?: "")
            binding.productcategory.setText(product.productCategory ?: "")

            // Use existing image URLs if available (for display)
            product.productImageUrls?.let {
                imageUrls = ArrayList(it.map { url -> Uri.parse(url) })
                binding.rvProductImage.adapter = AdapterSelectedImage(imageUrls)
            }
        }

        setStatusCompleteTextViews()
        onImageSelected()
        onAddButton()
        return binding.root
    }


    private fun onAddButton() {
        binding.addproduct.setOnClickListener {

            // Prevent duplicate clicks
            if (isSaveTriggered) return@setOnClickListener
            isSaveTriggered = true

            val productTitle = binding.ProductTitle.text.toString().trim()
            val productQuantity = binding.productQuantity.text.toString().trim()
            val unit = binding.unit.text.toString().trim()
            val noOfStocks = binding.noofstocks.text.toString().trim()
            val productCategory = binding.productcategory.text.toString().trim()
            val productPrice = binding.ProductPrice.text.toString().trim()

            // Validation
            if (productTitle.isBlank() || productPrice.isBlank() || productCategory.isBlank() ||
                productQuantity.isBlank() || unit.isBlank() || noOfStocks.isBlank()
            ) {
                Utils.shutDialog(requireContext())
                Utils.showtoast(requireContext(), "Fill up all fields!")
                isSaveTriggered = false
                return@setOnClickListener
            }

            if (imageUrls.isEmpty()) {
                Utils.shutDialog(requireContext())
                Utils.showtoast(requireContext(), "Please select at least one product image.")
                isSaveTriggered = false
                return@setOnClickListener
            }

            // Start uploading
            Utils.showDialog(requireContext(), "Uploading product...")

            val product = Product(
                productRandomId = existingProduct?.productRandomId ?: UUID.randomUUID().toString(),
                productTitle = productTitle,
                productQuantity = productQuantity.toInt(),
                productPrice = productPrice.toInt(),
                productStock = noOfStocks.toInt(),
                productCategory = productCategory,
                productUnit = unit,
                itemCount = existingProduct?.itemCount ?: 0,
                adminUid = Utils.getCurrentUserid()
            )

            // ✅ Case 1: Reuse image URLs if all are already uploaded (editing case)
            if (imageUrls.all { it.toString().startsWith("http") }) {
                product.productImageUrls = ArrayList(imageUrls.map { it.toString() })
                saveProducts(product)
            } else {
                saveImage(product)
            }
        }
    }

    private fun saveImage(product: Product) {
        // ✅ Reset first before triggering a new upload
        viewModel.resetUploadStatus()

        viewModel.saveImageDB(imageUrls, requireContext())

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.isImageUploaded.collect { isUploaded ->
                if (isUploaded) {
                    getUrls(product)
                }
            }
        }
    }





    private fun getUrls(product: Product) {
        Utils.showDialog(requireContext(), "Publishing product ....")

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.downloadUrls
                .filter { it.isNotEmpty() }
                .take(1)
                .collect { urlList ->
                    Utils.shutDialog(requireContext())
                    product.productImageUrls = urlList
                    saveProducts(product)
                }
        }
    }


private fun saveProducts(product: Product) {
    // Extra safety check if user quickly taps twice
    if (!isSaveTriggered) return
    isSaveTriggered = true

    val isEditing = existingProduct != null
    val oldCategory = existingProduct?.productCategory ?: ""

    viewModel.saveProduct(product, isEditing, oldCategory)

    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
        viewModel.isProductSaved.collect { isSaved ->
            if (isSaved) {
                viewModel.resetUploadStatus()
                Utils.shutDialog(requireContext())
                Utils.showtoast(requireContext(), "Your product is live")
                findNavController().popBackStack()
            }
        }
    }
}


    private fun onImageSelected(){
        binding.btnselectimage.setOnClickListener{
            getMultipleImage.launch("image/*")
        }
    }

    private fun setStatusCompleteTextViews(){
        val units = ArrayAdapter(requireContext() , R.layout.show_list, productData.units)
        val category = ArrayAdapter(requireContext() , R.layout.show_list, productData.categoryProductType)
        binding.apply{
            unit.setAdapter(units)
            productcategory.setAdapter(category)
        }
    }
}