package com.example.mobile_smart_pantry_project_iv

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mobile_smart_pantry_project_iv.databinding.ActivityMainBinding
import org.json.JSONObject
import java.io.File
import java.util.UUID

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ProductAdapter
    private val products = mutableListOf<Product>()
    private val fileName = "inventory.json"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        adapter = ProductAdapter(this, products)
        binding.listViewItems.adapter = adapter

        loadPantry()
        setupSearchView()
        setupChipFilters()
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter(newText ?: "")
                return true
            }
        })
    }

    private fun setupChipFilters() {
        binding.chipAll.setOnClickListener { adapter.filterByCategory("Wszystkie") }
        binding.chipDry.setOnClickListener { adapter.filterByCategory("Produkty sypkie") }
        binding.chipSpices.setOnClickListener { adapter.filterByCategory("Przyprawy") }
        binding.chipOils.setOnClickListener { adapter.filterByCategory("Oleje") }
        binding.chipCans.setOnClickListener { adapter.filterByCategory("Konserwy") }
        binding.chipBreakfast.setOnClickListener { adapter.filterByCategory("Produkty śniadaniowe") }
        binding.chipDairy.setOnClickListener { adapter.filterByCategory("Nabiał") }
    }

    private fun loadPantry() {
        val file = File(filesDir, fileName)
        if (file.exists()) {
            try {
                val jsonString = file.readText()
                parseJson(jsonString)
                adapter.updateList(products)
                return
            } catch (e: Exception) {
                Log.e("MarsColony", e.message.toString())
            }
        }

        try {
            val inputStream = resources.openRawResource(R.raw.inventory)
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            parseJson(jsonString)
            adapter.updateList(products)
            savePantry()
        } catch (e: Exception) {
            Log.e("MarsColony", e.message.toString())
            Toast.makeText(this, "Nie udało się wczytać danych", Toast.LENGTH_SHORT).show()
        }
    }

    private fun parseJson(jsonString: String) {
        products.clear()
        val jsonObject = JSONObject(jsonString)
        val jsonArray = jsonObject.getJSONArray("produkty")

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val product = Product(
                id = obj.optString("id", UUID.randomUUID().toString()),
                name = obj.getString("name"),
                quantity = obj.getInt("quantity"),
                category = obj.getString("category"),
                imageRef = obj.optString("imageRef", "ic_launcher_foreground")
            )
            products.add(product)
        }
    }

    fun savePantry() {
        try {
            val jsonArray = org.json.JSONArray()
            for (p in products) {
                val obj = JSONObject().apply {
                    put("id", p.id)
                    put("name", p.name)
                    put("quantity", p.quantity)
                    put("category", p.category)
                    put("imageRef", p.imageRef)
                }
                jsonArray.put(obj)
            }
            val root = JSONObject().put("produkty", jsonArray)
            File(filesDir, fileName).writeText(root.toString(2))
        } catch (e: Exception) {
            Log.e("MarsColony", e.message.toString())
        }
    }

    fun refreshList() {
        savePantry()
        adapter.updateList(products)
    }
}