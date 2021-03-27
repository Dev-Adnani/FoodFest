package com.devx.foodfest.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RestaurantDao {

    @Insert
    fun insertRestaurant(restaurantEntity: RestaurantEntity)

    @Delete
    fun deleteRestaurant(restaurantEntity: RestaurantEntity)

    @Query("SELECT * FROM restaurant")
    fun getAllRestaurant(): List<RestaurantEntity>

    @Query("SELECT * FROM restaurant WHERE restaurant_Id =:restaurantId")
    fun getAllRestaurant(restaurantId: String): RestaurantEntity
}