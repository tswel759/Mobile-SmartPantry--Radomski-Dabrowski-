package com.example.mobile_smart_pantry_project_iv

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.mobile_smart_pantry_project_iv.databinding.ItemProductBinding

class ProductAdapter(context: Context, private val products: List<Product>) :
    ArrayAdapter<Product>(context, 0, products) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ItemProductBinding
        val view: View

        if (convertView == null) {
            binding = ItemProductBinding.inflate(LayoutInflater.from(context), parent, false)
            view = binding.root
            view.tag = binding
        } else {
            binding = convertView.tag as ItemProductBinding
            view = convertView
        }

        val product = getItem(position)

        product?.let {
            binding.txtName.text = it.name
            binding.txtCategory.text = it.category
            binding.txtQuantity.text = "Qty: ${it.quantity}"

            // Red Alert Logic
            if (it.quantity < 5) {
                binding.itemContainer.setBackgroundColor(Color.parseColor("#FFCDD2")) // Light Red
            } else {
                binding.itemContainer.setBackgroundColor(Color.TRANSPARENT)
            }

            // Image loading
            val resId = context.resources.getIdentifier(it.imageRef, "drawable", context.packageName)
            if (resId != 0) {
                binding.imgProduct.setImageResource(resId)
            } else {
                binding.imgProduct.setImageResource(android.R.drawable.ic_menu_report_image)
            }
        }

        return view
    }
}
