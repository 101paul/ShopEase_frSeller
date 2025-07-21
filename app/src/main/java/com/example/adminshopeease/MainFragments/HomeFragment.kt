package com.example.adminshopeease.MainFragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.adminshopeease.Model.Category
import com.example.adminshopeease.Model.Product
import com.example.adminshopeease.R
import com.example.adminshopeease.adapters.CategorisAdapter
import com.example.adminshopeease.adapters.ProductsAdapter
import com.example.adminshopeease.databinding.CustoomAlertBox1Binding
import com.example.adminshopeease.databinding.FragmentHomeBinding
import com.example.adminshopeease.databinding.ProductEditButtonLayoutBinding
import com.example.adminshopeease.objectClass.Utils
import com.example.adminshopeease.objectClass.productData
import com.example.adminshopeease.viewModel.AdminViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeFragment : Fragment() {
    private val viewModel: AdminViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapterProduct: ProductsAdapter
    private var searchJob: Job? = null
    private var shimmerTimeoutJob: Job? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showCategory()
        searchProducts()
        Log.d("hello","hello this is paul")
        viewLifecycleOwner.lifecycleScope.launch{
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.fetchAllProducts("All")
                    .debounce(300) // Avoid rapid repeated emissions on startup
                    .collectLatest { productList ->
                        getAllTheProductsSafely(productList)
                    }
            }
        }
    }

    private fun setupUI() {
        adapterProduct = ProductsAdapter(::onEditbuttonClick)
        binding.rvProducts.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvProducts.adapter = adapterProduct

        binding.shrimer.visibility = View.VISIBLE
        binding.rvProducts.visibility = View.GONE
        binding.productEmpty.visibility = View.GONE

        // Start shimmer timeout fallback (max wait 10 sec)
        shimmerTimeoutJob?.cancel()
        shimmerTimeoutJob = viewLifecycleOwner.lifecycleScope.launch {
            delay(10_000)
            if (adapterProduct.itemCount == 0) {
                showEmptyState()
            }
        }
    }

    private fun getAllTheProductsSafely(productList: List<Product>) {

        viewLifecycleOwner.lifecycleScope.launch {
            // Process large list on Default Dispatcher (for CPU-bound tasks)
            val safeList = withContext(Dispatchers.Default) {
                productList.toList()
            }

            withContext(Dispatchers.Main) {
                binding.shrimer.visibility = View.GONE
                if (safeList.isEmpty()) {
//                    delay(1000)
                    showEmptyState()
                } else {
                    binding.rvProducts.visibility = View.VISIBLE
                    binding.productEmpty.visibility = View.GONE
                    adapterProduct.submitListWithOriginal(safeList)
                }
            }
        }
    }

    private fun showEmptyState() {
        binding.shrimer.visibility = View.GONE
        binding.rvProducts.visibility = View.GONE
        binding.productEmpty.visibility = View.VISIBLE
    }

    private fun searchProducts() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                searchJob?.cancel()
                searchJob = viewLifecycleOwner.lifecycleScope.launch {
                    delay(500) // Debounce time
                    if (::adapterProduct.isInitialized) {
                        adapterProduct.getFilter().filter(query)
                    }
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private var categoryFetchJob: Job? = null

    fun onCategoryClicked(categories: Category) {
        shimmerTimeoutJob?.cancel()
        categoryFetchJob?.cancel() // ✅ Cancel previous Firebase job

        binding.shrimer.visibility = View.VISIBLE
        binding.rvProducts.visibility = View.GONE
        binding.productEmpty.visibility = View.GONE

        shimmerTimeoutJob = viewLifecycleOwner.lifecycleScope.launch {
            delay(10_000)
            if (adapterProduct.itemCount == 0) {
                showEmptyState()
            }
        }

        categoryFetchJob = viewLifecycleOwner.lifecycleScope.launch {
            viewModel.fetchAllProducts(categories.name.toString())
                .distinctUntilChanged()
                .collectLatest { productList ->
                    getAllTheProductsSafely(productList)
                }
        }
    }


    fun onEditbuttonClick(product: Product) {
        val editproduct = ProductEditButtonLayoutBinding.inflate(LayoutInflater.from(requireContext()))

        // Step 1: Keep a copy of the old category BEFORE modification
        val oldCategory = product.productCategory ?: "Unknown"

        // Step 2: Pre-fill current product data
        editproduct.apply {
            eProductTitle.setText(product.productTitle)
            eproductQuantity.setText(product.productQuantity?.toString() ?: "")
            eProductPrice.setText(product.productPrice?.toString() ?: "")
            eunit.setText(product.productUnit ?: "")
            enoofstocks.setText(product.productStock?.toString() ?: "")
            eproductcategory.setText(product.productCategory ?: "")
        }

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(editproduct.root)
            .create()
        alertDialog.show()

        // Step 3: Enable fields for editing
        editproduct.editButton.setOnClickListener {
            editproduct.apply {
                eProductTitle.isEnabled = true
                eproductQuantity.isEnabled = true
                enoofstocks.isEnabled = true
                eunit.isEnabled = true
                eProductPrice.isEnabled = true
                eproductcategory.isEnabled = true
                setStatusCompleteTextViews(editproduct)
            }
        }


        editproduct.saveButton.setOnClickListener {
            val updatedProduct = product.copy( // ✅ create a copy to safely mutate
                productTitle = editproduct.eProductTitle.text.toString().trim(),
                productUnit = editproduct.eunit.text.toString().trim(),
                productPrice = editproduct.eProductPrice.text.toString().toIntOrNull(),
                productCategory = editproduct.eproductcategory.text.toString().trim(),
                productStock = editproduct.enoofstocks.text.toString().toIntOrNull(),
                productQuantity = editproduct.eproductQuantity.text.toString().toIntOrNull()
            )

            // ✅ Preserve existing image URLs
            updatedProduct.productImageUrls = product.productImageUrls

            // Validation
            if (
                updatedProduct.productTitle.isNullOrBlank() ||
                updatedProduct.productUnit.isNullOrBlank() ||
                updatedProduct.productPrice == null ||
                updatedProduct.productCategory.isNullOrBlank() ||
                updatedProduct.productStock == null ||
                updatedProduct.productQuantity == null ||
                updatedProduct.productImageUrls.isNullOrEmpty()
            ) {
                Utils.showtoast(requireContext(), "Please fill all fields correctly")
                return@setOnClickListener
            }

            // Save updated product
            viewModel.saveProduct(
                updatedProduct,
                isEditing = true,
                oldCategory = oldCategory
            )

            alertDialog.dismiss()
            Utils.showtoast(requireContext(), "Changes saved!")
        }

        // Step 5: Close dialog
        editproduct.closeButton.setOnClickListener {
            alertDialog.dismiss()
        }

        // Step 6: Delete product (with confirmation)
        val layout = CustoomAlertBox1Binding.inflate(LayoutInflater.from(requireContext()))
        editproduct.deleteButton.setOnClickListener {
            val alert = AlertDialog.Builder(requireContext())
                .setView(layout.root)
                .create()
            alert.show()

            layout.btnOk.setOnClickListener {
                viewModel.deleteProduct(
                    product.productRandomId.toString(),
                    product.productCategory.toString()
                )
                alert.dismiss()
                alertDialog.dismiss()
                Utils.showtoast(requireContext(), "Product deleted!")
            }

            layout.btnCancel.setOnClickListener {
                alert.dismiss()
            }
        }
    }


    private fun setStatusCompleteTextViews(editProduct: ProductEditButtonLayoutBinding) {
        val units = ArrayAdapter(requireContext(), R.layout.show_list, productData.units)
        val category = ArrayAdapter(requireContext(), R.layout.show_list, productData.categoryProductType)
        editProduct.eunit.setAdapter(units)
        editProduct.eproductcategory.setAdapter(category)
    }

    private fun showCategory() {
        val value = ArrayList<Category>()
        for (i in productData.categoryProductImage.indices) {
            value.add(
                Category(
                    name = productData.categoryProductType[i],
                    Image = productData.categoryProductImage[i]
                )
            )
        }
        binding.rvCategories.adapter = CategorisAdapter(value, ::onCategoryClicked)
    }


}