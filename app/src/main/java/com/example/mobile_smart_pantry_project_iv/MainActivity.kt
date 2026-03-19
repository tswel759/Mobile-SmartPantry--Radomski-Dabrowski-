package com.example.mobile_smart_pantry_project_iv
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mobile_smart_pantry_project_iv.databinding.ActivityMainBinding
import org.json.JSONObject
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ProductAdapter
    private val products = mutableListOf<Product>()
    private val fileName = "inventory.json"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        binding.chipGroupFilters.setOnCheckedStateChangeListener { _, checkedIds ->
            val category = when (checkedIds.firstOrNull()) {
                R.id.chipDry -> "Produkty sypkie"
                R.id.chipSpices -> "Przyprawy"
                R.id.chipOils -> "Oleje"
                R.id.chipCans -> "Konserwy"
                R.id.chipBreakfast -> "Produkty śniadaniowe"
                R.id.chipDairy -> "Nabiał"
                else -> "Wszystkie"
            }
            adapter.filterByCategory(category)
        }
    }

    private fun loadPantry() {
        val file = File(filesDir, fileName)
        val jsonString = if (file.exists()) file.readText() else loadFromResources()
        parseJson(jsonString)
        adapter.updateList(products)
    }

    private fun loadFromResources(): String {
        val json = resources.openRawResource(R.raw.inventory).bufferedReader().use { it.readText() }
        File(filesDir, fileName).writeText(json)
        return json
    }

    private fun parseJson(jsonString: String) {
        products.clear()
        val jsonArray = JSONObject(jsonString).getJSONArray("produkty")
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            products.add(Product(
                id = obj.getString("id"),
                name = obj.getString("name"),
                quantity = obj.getInt("quantity"),
                category = obj.getString("category"),
                imageRef = obj.getString("imageRef")
            ))
        }
    }

    fun savePantry() {
        val jsonArray = org.json.JSONArray()
        products.forEach { p ->
            jsonArray.put(JSONObject().apply {
                put("id", p.id)
                put("name", p.name)
                put("quantity", p.quantity)
                put("category", p.category)
                put("imageRef", p.imageRef)
            })
        }
        File(filesDir, fileName).writeText(JSONObject().put("produkty", jsonArray).toString(2))
    }

    fun refreshList() {
        savePantry()
        adapter.updateList(products)
    }
}
