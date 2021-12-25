package com.example.shoppingcart

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CartDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addTOCart(items: Cart)

    @Update
    fun updateCart(item: Cart)

    @Query("SELECT * FROM cart_items ORDER BY id DESC")
    fun getWholeCart(): List<Cart>

    @Query("SELECT * FROM cart_items WHERE name LIKE :query OR price LIKE :query ORDER BY id ASC")
    fun searchCart(query: String): LiveData<List<Cart>>

    @Delete
    fun deleteItem(item: Cart)
}