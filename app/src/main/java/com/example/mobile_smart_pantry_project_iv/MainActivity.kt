package com.example.mobile_smart_pantry_project_iv

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mobile_smart_pantry_project_iv.databinding.ActivityMainBinding
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ProductAdapter
    private val productList = mutableListOf<Product>()
    private val fileName = "inventory.json"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize ListView and Adapter
        loadInventory()
        adapter = ProductAdapter(this, productList)
        binding.listView.adapter = adapter

        // Add Button Logic
        binding.btnAdd.setOnClickListener {
            addProduct()
        }
    }

    private fun addProduct() {
        val name = binding.etName.text.toString()
        val qtyString = binding.etQuantity.text.toString()
        val category = binding.etCategory.text.toString()

        if (name.isBlank() || qtyString.isBlank() || category.isBlank()) {
            Toast.makeText(this, "Mission Error: Fill all fields!", Toast.LENGTH_SHORT).show()
            return
        }

        val quantity = qtyString.toIntOrNull() ?: 0
        val newProduct = Product(
            name = name,
            quantity = quantity,
            category = category,
            imageRef = "ic_launcher_foreground" // Default for now
        )

        productList.add(newProduct)
        adapter.notifyDataSetChanged()
        saveInventory()

        // Clear inputs
        binding.etName.text.clear()
        binding.etQuantity.text.clear()
        binding.etCategory.text.clear()
    }

    private fun saveInventory() {
        try {
            val jsonString = Json.encodeToString(productList)
            val file = File(filesDir, fileName)
            file.writeText(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadInventory() {
        try {
            val file = File(filesDir, fileName)
            if (file.exists()) {
                val jsonString = file.readText()
                val loadedList: List<Product> = Json.decodeFromString(jsonString)
                productList.clear()
                productList.addAll(loadedList)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
