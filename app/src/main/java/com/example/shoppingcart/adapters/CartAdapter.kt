package com.example.shoppingcart.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingcart.R
import com.example.shoppingcart.database.CartDatabase
import com.example.shoppingcart.databinding.ActivityMainBinding
import com.example.shoppingcart.databinding.CartItemLayoutBinding
import com.example.shoppingcart.model.Cart
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import java.text.NumberFormat
import java.util.*

class CartAdapter(private val wholeCart: MutableList<Cart>, context: Context) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private var database: CartDatabase = CartDatabase.invoke(context)

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val contentBinding = CartItemLayoutBinding.bind(itemView)
        val cardView: MaterialCardView = contentBinding.cardView
        val itemName: TextView = contentBinding.cartItem
        val itemPrice: TextView = contentBinding.cartItemPrice
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        return CartViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.cart_item_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.apply {
            itemName.text = wholeCart[position].item
            val localeID = Locale.getDefault()
            val formatCurrency = NumberFormat.getCurrencyInstance(localeID)
            val amount = formatCurrency.format(wholeCart[position].price.toString().toDouble())
            itemPrice.text = amount
            itemPrice.append(" ${itemView.context.getString(R.string.currency)}")

            cardView.setOnClickListener {
                val edit_item_view = LayoutInflater.from(it.context)
                    .inflate(R.layout.edit_item_layout, null, false)
                val update_name = edit_item_view.findViewById<TextInputEditText>(R.id.update_name)
                update_name.setText(wholeCart[position].item)
                val update_price = edit_item_view.findViewById<TextInputEditText>(R.id.update_price)
                update_price.setText(wholeCart[position].price.toBigDecimal().toString())
                val update_cart_button =
                    edit_item_view.findViewById<MaterialButton>(R.id.update_cart_button)

                val editDialog = MaterialAlertDialogBuilder(it.context)
                    .setTitle("Edit Item")
                    .setView(edit_item_view)
                    .show()

                update_cart_button.setOnClickListener {
                    if (update_name.text.isNullOrEmpty()) {
                        Toast.makeText(
                            it.context,
                            "Name cannot be empty",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val inputtedItemPrice =
                            if (update_price.text.isNullOrEmpty()) "0" else update_price.text.toString()

                        database.cartDao().updateCart(
                            Cart(
                                id = wholeCart[position].id,
                                item = update_name.text.toString().trim(),
                                price = inputtedItemPrice.toFloat()
                            )
                        )
                        wholeCart[position] = Cart(
                            id = wholeCart[position].id,
                            item = update_name.text.toString().trim(),
                            price = inputtedItemPrice.toFloat()
                        )
                        notifyDataSetChanged()
                        Toast.makeText(
                            it.context,
                            "${update_name.text} updated",
                            Toast.LENGTH_SHORT
                        ).show()
                        editDialog.dismiss()
                    }

                }
            }
        }
    }

    override fun getItemCount() = wholeCart.size

    fun getCartAtPosition(position: Int) = Cart(
        id = wholeCart[position].id,
        item = wholeCart[position].item,
        price = wholeCart[position].price
    )

    fun addToCartAndRecyclerView(item: Cart, position: Int, binding: ActivityMainBinding?) {
        database.cartDao().addTOCart(item)
        wholeCart.add(position, item)
        binding?.shoppingCartRv?.adapter?.notifyItemInserted(position)
        binding?.shoppingCartRv?.scrollToPosition(position)
    }

    fun removeAt(position: Int) {
        database.cartDao().deleteItem(
            Cart(
                id = wholeCart[position].id,
                item = wholeCart[position].item,
                price = wholeCart[position].price
            )
        )
        wholeCart.remove(
            Cart(
                id = wholeCart[position].id,
                item = wholeCart[position].item,
                price = wholeCart[position].price
            )
        )
        notifyItemRemoved(position)
    }

}
