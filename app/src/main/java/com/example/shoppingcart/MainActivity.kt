package com.example.shoppingcart

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppingcart.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: CartDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = CartDatabase.invoke(this)
        val wholeCart = database.cartDao().getWholeCart()

        binding.shoppingCartRv.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = CartAdapter(wholeCart)
        }

        binding.apply {
            addToCartButton.setOnClickListener {
                val imputedItemName = addItemName.text.toString()
                val imputedItemPrice = if (addItemPrice.text.isNullOrEmpty()) 0 else addItemPrice.text.toString().toInt()
                database.cartDao().addTOCart(
                    Cart(
                        id = 0,
                        item = imputedItemName,
                        price = imputedItemPrice
                    )
                )
                val intent = Intent(this@MainActivity, MainActivity::class.java)
                startActivity(intent)
                overridePendingTransition(0,0)
                finish()
                overridePendingTransition(0,0)
                addItemName.setText("")
                addItemPrice.setText("")
            }
        }
    }
}