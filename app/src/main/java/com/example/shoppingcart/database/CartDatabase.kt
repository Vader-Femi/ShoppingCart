package com.example.shoppingcart.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.shoppingcart.model.Cart

@Database(
    version = 1,
    entities = [Cart::class]
)

abstract class CartDatabase : RoomDatabase(){
    abstract fun cartDao(): CartDao

    companion object {

        @Volatile
        private var instance: CartDatabase? = null
        private val LOCK = Any()

        fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: createDatabase(context).also {
                instance = it
            }
        }
        private fun createDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            CartDatabase::class.java,
            "cart_database"
        )
            .allowMainThreadQueries()
            .build()
    }
}