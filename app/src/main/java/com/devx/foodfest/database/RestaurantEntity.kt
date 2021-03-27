package com.devx.foodfest.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurant")
data class RestaurantEntity(
    @PrimaryKey val restaurant_Id: String,
    @ColumnInfo(name = "restaurant_name") val restaurantName: String
)