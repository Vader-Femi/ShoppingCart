package com.example.shoppingcart.activities

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.shoppingcart.adapters.CartAdapter
import com.example.shoppingcart.database.CartDatabase
import com.example.shoppingcart.databinding.ActivityMainBinding
import com.example.shoppingcart.model.Cart
import com.example.shoppingcart.utils.SwipeToDelete
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: CartDatabase

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Instantiate Database
        database = CartDatabase.invoke(this)
        val wholeCart = database.cartDao().getWholeCart() as MutableList<Cart>

        //Setup RecyclerView
        binding.shoppingCartRv.adapter = CartAdapter(wholeCart, this@MainActivity)
        setupLayoutManager()

        //Swipe To Delete
        enableSwipeToDeleteAndUndo(binding)
        val swipeHandler = enableSwipeToDeleteAndUndo(binding)
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.shoppingCartRv)

        //Add To Cart Button
        binding.addToCartButton.setOnClickListener {
            addToCart(binding)
        }

        //Delete All Button
        binding.addToCartButton.setOnLongClickListener {
            database.cartDao().nukeCart()
            wholeCart.removeAll { true }
            (binding.shoppingCartRv.adapter as CartAdapter).notifyDataSetChanged()
            Toast.makeText(
                this@MainActivity,
                "Shopping Cart Nuked",
                Toast.LENGTH_SHORT
            ).show()
            true
        }
    }

    private fun addToCart(binding: ActivityMainBinding) {
        binding.apply {
            val adapter = binding.shoppingCartRv.adapter as CartAdapter
            if (addItemName.text.isNullOrEmpty()){
                Toast.makeText(
                    this@MainActivity,
                    "Name cannot be empty",
                    Toast.LENGTH_SHORT
                ).show()
            } else{
                val inputtedItemName = addItemName.text.toString()
                val inputtedItemPrice = if (addItemPrice.text.isNullOrEmpty()) "0" else addItemPrice.text.toString()
                try {
                    inputtedItemPrice.toFloat()
                    val item = Cart(
                        0,
                        inputtedItemName,
                        inputtedItemPrice.toFloat()
                    )
                    adapter.addToCartAndRecyclerView(item, 0, binding)

                    addItemName.text?.clear()
                    addItemPrice.text?.clear()
                } catch (e: NumberFormatException) {
                    Log.d("Save", "Pre Toast")
                    Toast.makeText(
                        this@MainActivity,
                        "Price cannot contain letters or special characters except Dot(.)",
                        Toast.LENGTH_SHORT
                    ).show()
                    addItemPrice.text?.clear()
                }
            }
        }
    }

    private fun setupLayoutManager() {
        when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                binding.shoppingCartRv.layoutManager =
                    StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            }
            Configuration.ORIENTATION_LANDSCAPE -> {
                binding.shoppingCartRv.layoutManager =
                    StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
            }
            Configuration.ORIENTATION_UNDEFINED -> {
                binding.shoppingCartRv.layoutManager =
                    LinearLayoutManager(this)
            }
        }
    }

    private fun enableSwipeToDeleteAndUndo(
        binding: ActivityMainBinding
    ): SwipeToDelete {
        val swipeHandler = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val position = viewHolder.adapterPosition
                val adapter = binding.shoppingCartRv.adapter as CartAdapter

                val item = adapter.getCartAtPosition(position)

                Snackbar.make(
                    binding.parentLayout,// The ID of your coordinator_layout
                    "Deleting ${item.item}",
                    Snackbar.LENGTH_LONG
                ).apply {
                    setAction("UNDO") {
                        adapter.addToCartAndRecyclerView(item, position, binding)
                    }
                    setActionTextColor(Color.MAGENTA)
                }.show()
                (binding.shoppingCartRv.adapter as CartAdapter).removeAt(viewHolder.adapterPosition)
            }
        }
        return swipeHandler
    }
}