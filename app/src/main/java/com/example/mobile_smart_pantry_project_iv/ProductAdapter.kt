package com.example.mobile_smart_pantry_project_iv

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.mobile_smart_pantry_project_iv.databinding.ItemProductBinding

class ProductAdapter(
    private val context: Context,
    private val allProducts: MutableList<Product>
) : BaseAdapter() {

    private var filteredProducts: List<Product> = allProducts.toList()
    private var searchQuery: String = ""
    private var selectedCategory: String = "Wszystkie"
    private var expandedPosition: Int = -1


    private fun applyFilters() {
        filteredProducts = allProducts.filter { product ->
            val matchesCategory = selectedCategory == "Wszystkie" || product.category.equals(selectedCategory, ignoreCase = true)
            val matchesSearch = product.name.contains(searchQuery, ignoreCase = true)
            
            matchesCategory && matchesSearch
        }
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        searchQuery = query
        applyFilters()
    }

    fun filterByCategory(category: String) {
        selectedCategory = category
        applyFilters()
    }

    fun updateList() {
        applyFilters()
    }

    override fun getCount(): Int = filteredProducts.size
    override fun getItem(position: Int): Product = filteredProducts[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = if (convertView == null) {
            ItemProductBinding.inflate(LayoutInflater.from(context), parent, false)
        } else {
            ItemProductBinding.bind(convertView)
        }

        val product = filteredProducts[position]

        binding.textProduct.text = product.name
        binding.tvCategory.text = product.category
        binding.tvQuantity.text = product.quantity.toString()

        val imageResId = context.resources.getIdentifier(product.imageRef, "drawable", context.packageName)
        if (imageResId != 0) {
            binding.ivProductImage.setImageResource(imageResId)
        } else {
            binding.ivProductImage.setImageResource(R.drawable.ic_launcher_foreground)
        }

        if (product.quantity < 5) {
            binding.root.setBackgroundColor(Color.parseColor("#FFCDD2"))
            binding.tvQuantity.setTextColor(Color.RED)
        } else {
            binding.root.setBackgroundColor(Color.TRANSPARENT)
            binding.tvQuantity.setTextColor(Color.parseColor("#4CAF50"))
        }

        binding.buttonPanel.visibility = if (position == expandedPosition) View.VISIBLE else View.GONE
        binding.root.setOnClickListener {
            expandedPosition = if (expandedPosition == position) -1 else position
            notifyDataSetChanged()
        }

        binding.buttonAdd.setOnClickListener {
            product.quantity++
            (context as MainActivity).refreshList()
        }

        binding.buttonSubtract.setOnClickListener {
            if (product.quantity > 0) {
                product.quantity--
                (context as MainActivity).refreshList()
            }
        }

        binding.buttonDelete.setOnClickListener {
            allProducts.removeAll { it.id == product.id }
            (context as MainActivity).refreshList()
        }

        return binding.root
    }
}
