package com.example.mobile_smart_pantry_project_iv

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.content.ContextCompat
import com.example.mobile_smart_pantry_project_iv.databinding.ItemProductBinding

class ProductAdapter(
    private val context: Context,
    private val originalProducts: MutableList<Product>
) : BaseAdapter() {

    private var filteredProducts: MutableList<Product> = originalProducts.toMutableList()
    private var currentQuery: String = ""
    private var currentCategory: String = "Wszystkie"
    private var expandedPosition: Int = -1

    fun updateList(newList: List<Product>) {
        originalProducts.clear()
        originalProducts.addAll(newList)
        applyFilters()
    }

    fun filter(query: String) {
        currentQuery = query
        applyFilters()
    }

    fun filterByCategory(category: String) {
        currentCategory = category
        applyFilters()
    }

    private fun applyFilters() {
        var result = originalProducts.toList()

        if (currentCategory != "Wszystkie") {
            result = result.filter { it.category.equals(currentCategory, ignoreCase = true) }
        }

        if (currentQuery.isNotEmpty()) {
            result = result.filter {
                it.name.contains(currentQuery, ignoreCase = true)
            }
        }

        filteredProducts = result.toMutableList()
        expandedPosition = -1
        notifyDataSetChanged()
    }

    override fun getCount(): Int = filteredProducts.size
    override fun getItem(position: Int): Product = filteredProducts[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
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
            binding.tvQuantity.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
        } else {
            binding.root.setBackgroundColor(Color.TRANSPARENT)
            binding.tvQuantity.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark))
        }

        binding.buttonPanel.visibility = if (position == expandedPosition) View.VISIBLE else View.GONE

        binding.root.setOnClickListener {
            expandedPosition = if (expandedPosition == position) -1 else position
            notifyDataSetChanged()
        }

        binding.buttonAdd.setOnClickListener {
            product.quantity += 1
            notifyDataSetChanged()
            (context as? MainActivity)?.refreshList()
        }

        binding.buttonSubtract.setOnClickListener {
            if (product.quantity > 0) {
                product.quantity -= 1
                notifyDataSetChanged()
                (context as? MainActivity)?.refreshList()
            }
        }

        binding.buttonDelete.setOnClickListener {
            val actualProduct = originalProducts.find { it.id == product.id }
            if (actualProduct != null) {
                originalProducts.remove(actualProduct)
            }
            applyFilters()
            (context as? MainActivity)?.refreshList()
        }

        return binding.root
    }
}