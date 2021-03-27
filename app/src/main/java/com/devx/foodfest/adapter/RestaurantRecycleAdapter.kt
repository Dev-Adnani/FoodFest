package com.devx.foodfest.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.devx.foodfest.R
import com.devx.foodfest.activity.RestaurantMenuActivity
import com.devx.foodfest.database.RestaurantDatabase
import com.devx.foodfest.database.RestaurantEntity
import com.devx.foodfest.model.Restaurant
import com.squareup.picasso.Picasso

class RestaurantRecycleAdapter(val context: Context, private var itemList: ArrayList<Restaurant>) :
    RecyclerView.Adapter<RestaurantRecycleAdapter.RestaurantViewHolder>() {
    class RestaurantViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txtFav: TextView = view.findViewById(R.id.favTextView)
        val txtRestaurantName: TextView = view.findViewById(R.id.txtFoodName)
        val txtRestaurantRating: TextView = view.findViewById(R.id.txtFoodRating)
        val txtRestaurantPrice: TextView = view.findViewById(R.id.txtFoodPrice)
        val restaurantImage: ImageView = view.findViewById(R.id.foodImageView)
        val rlContent: RelativeLayout = view.findViewById(R.id.rlContent)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_dashboard, parent, false)

        return RestaurantViewHolder(view)

    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {

        val restaurant = itemList[position]
        val restaurantEntity = RestaurantEntity(restaurant.restaurantId, restaurant.restaurantName)

        holder.txtRestaurantName.text = restaurant.restaurantName
        holder.txtRestaurantRating.text = restaurant.restaurantRating
        holder.txtRestaurantPrice.text = "${restaurant.restaurantPrice}/Person"

        Picasso.get().load(restaurant.restaurantImage).error(R.drawable.ic_food_delivery)
            .into(holder.restaurantImage)

        holder.rlContent.setOnClickListener {
            val intent = Intent(context, RestaurantMenuActivity::class.java)
            intent.putExtra("restaurantId", restaurant.restaurantId)
            intent.putExtra("restaurantName", holder.txtRestaurantName.text.toString())
            context.startActivity(intent)
        }

        val checkFav = DBAsyncTask(context, restaurantEntity, 1).execute()
        val isFav = checkFav.get()

        if (isFav) {
            holder.txtFav.tag = "liked"
            holder.txtFav.background = context.resources.getDrawable(R.drawable.ic_fav_fill)
        } else {
            holder.txtFav.tag = "unliked"
            holder.txtFav.background = context.resources.getDrawable(R.drawable.ic_fav_outline)
        }

        holder.txtFav.setOnClickListener {
            if (!DBAsyncTask(context, restaurantEntity, 1).execute().get()) {
                val result = DBAsyncTask(context, restaurantEntity, 2).execute().get()
                if (result) {
                    Toast.makeText(
                        context,
                        "${restaurant.restaurantName} Successfully Added To Fav",
                        Toast.LENGTH_SHORT
                    ).show()
                    holder.txtFav.tag = "liked"
                    holder.txtFav.background = context.resources.getDrawable(R.drawable.ic_fav_fill)
                } else {
                    Toast.makeText(
                        context,
                        "Some Unknown Error Occured Please Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                val result = DBAsyncTask(context, restaurantEntity, 3).execute().get()
                if (result) {
                    Toast.makeText(
                        context,
                        "${restaurant.restaurantName} Successfully Removed To Fav",
                        Toast.LENGTH_SHORT
                    ).show()
                    holder.txtFav.tag = "unliked"
                    holder.txtFav.background =
                        context.resources.getDrawable(R.drawable.ic_fav_outline)
                } else {
                    Toast.makeText(
                        context,
                        "Some Unknown Error Occured Please Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }

    private fun filterList(filteredList: ArrayList<Restaurant>) {
        itemList = filteredList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return itemList.size
    }


}

class DBAsyncTask(val context: Context, val restaurantEntity: RestaurantEntity, val mode: Int) :
    AsyncTask<Void, Void, Boolean>() {

    //1  - > Check  In DB
    //2 -> Save  Into Db as Fav
    //3 -> Dele  From Db as Fav

    val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant-db").build()

    override fun doInBackground(vararg params: Void?): Boolean {

        when (mode) {

            1 -> {
                //1  - > Check  In DB
                val restaurant: RestaurantEntity =
                    db.restaurantDao().getAllRestaurant(restaurantEntity.restaurant_Id.toString())
                db.close()
                return restaurant != null

            }

            2 -> {
                //2 -> Save  Into Db as Fav

                db.restaurantDao().insertRestaurant(restaurantEntity)
                db.close()
                return true

            }

            3 -> {
                //3 -> Delete  From Db as Fav
                db.restaurantDao().deleteRestaurant(restaurantEntity)
                db.close()
                return true

            }

        }

        return false
    }

}