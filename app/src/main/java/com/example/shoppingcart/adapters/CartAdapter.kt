package com.example.shoppingcart.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingcart.R
import com.example.shoppingcart.database.CartDatabase
import com.example.shoppingcart.databinding.ActivityMainBinding
import com.example.shoppingcart.databinding.CartItemLayoutBinding
import com.example.shoppingcart.model.Cart

class CartAdapter(private val wholeCart: MutableList<Cart>, context: Context) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private var database: CartDatabase = CartDatabase.invoke(context)

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

    override fun getItemCount() = wholeCart.size

    fun getCartAtPosition(position: Int) = Cart(
        wholeCart[position].id,
        wholeCart[position].item,
        wholeCart[position].price
    )

    fun addToCartAndRecyclerView(item: Cart,position: Int,binding:ActivityMainBinding){
        database.cartDao().addTOCart(item)
        wholeCart.add(position,item)
        binding.shoppingCartRv.adapter?.notifyItemInserted(position)
    }

    fun removeAt(position: Int) {
        database.cartDao().deleteItem(
            Cart(
                wholeCart[position].id,
                wholeCart[position].item,
                wholeCart[position].price
            )
        )
        wholeCart.remove(
            Cart(
                wholeCart[position].id,
                wholeCart[position].item,
                wholeCart[position].price
            )
        )
        notifyItemRemoved(position)
    }
}
