package com.example.shoppingcart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingcart.databinding.CartItemLayoutBinding

class CartAdapter(private val wholeCart: List<Cart>) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val contentBinding = CartItemLayoutBinding.bind(itemView)
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
            itemPrice.text = wholeCart[position].price.toString()
            itemPrice.append(" ${itemView.context.getString(R.string.currency)}")
        }
    }

    override fun getItemCount()= wholeCart.size
}
