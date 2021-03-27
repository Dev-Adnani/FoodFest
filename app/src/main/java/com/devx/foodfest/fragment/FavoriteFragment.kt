package com.devx.foodfest.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.Image
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.devx.foodfest.R
import com.devx.foodfest.adapter.RestaurantMenuAdapter
import com.devx.foodfest.adapter.RestaurantRecycleAdapter
import com.devx.foodfest.database.RestaurantDatabase
import com.devx.foodfest.database.RestaurantEntity
import com.devx.foodfest.model.Restaurant
import com.devx.foodfest.util.ConnectionManager
import kotlinx.android.synthetic.main.fragment_favorite.*
import org.json.JSONException


class FavoriteFragment(val contextParam: Context) : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var favoriteAdapter: RestaurantRecycleAdapter
    lateinit var favoriteProgressLayout: RelativeLayout
    var restaurantInfoList = arrayListOf<Restaurant>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_favorite, container, false)

        layoutManager = LinearLayoutManager(activity)
        recyclerView = view.findViewById(R.id.favRecycler)
        favoriteProgressLayout = view.findViewById(R.id.progressFavLayout)
        fetchData()

        return view
    }

    fun fetchData() {
        if (ConnectionManager().checkConnectivity(activity as Context)) {
            favoriteProgressLayout.visibility = View.INVISIBLE
            try {
                val queue = Volley.newRequestQueue(activity as Context)
                val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

                val jsonObjectRequest =
                    object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {

                        val response = it.getJSONObject("data")
                        val success = response.getBoolean("success")

                        if (success) {
                            restaurantInfoList.clear()
                            val data = response.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val restaurantJsonObject = data.getJSONObject(i)
                                val restaurantEntity = RestaurantEntity(
                                    restaurantJsonObject.getString("id"),
                                    restaurantJsonObject.getString("name")
                                )
                                if (DBAsyncTask(contextParam, restaurantEntity, 1).execute()
                                        .get()
                                ) {
                                    val restaurantObject = Restaurant(
                                        restaurantJsonObject.getString("id"),
                                        restaurantJsonObject.getString("name"),
                                        restaurantJsonObject.getString("rating"),
                                        restaurantJsonObject.getString("cost_for_one"),
                                        restaurantJsonObject.getString("image_url")
                                    )
                                    restaurantInfoList.add(restaurantObject)
                                    favoriteAdapter = RestaurantRecycleAdapter(
                                        activity as Context,
                                        restaurantInfoList
                                    )
                                    recyclerView.adapter = favoriteAdapter
                                    recyclerView.layoutManager = layoutManager
                                }
                            }
                            if (restaurantInfoList.size == 0) {
                                nothingFavIV.visibility = View.VISIBLE
                                nothingFavTV.visibility = View.VISIBLE
                            }
                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Some Error occurred!!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }, Response.ErrorListener {
                        Toast.makeText(
                            activity as Context,
                            "Some Error occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = "9bf534118365f1"
                            return headers
                        }
                    }
                queue.add(jsonObjectRequest)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        } else {

            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(activity as Context)
            alterDialog.setTitle("No Internet")
            alterDialog.setMessage("Check Internet Connection!")
            alterDialog.setPositiveButton("Open Settings") { _, _ ->
                val settingsIntent = Intent(Settings.ACTION_SETTINGS)
                startActivity(settingsIntent)
            }
            alterDialog.setNegativeButton("Exit") { _, _ ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            alterDialog.setCancelable(false)
            alterDialog.create()
            alterDialog.show()
        }
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


        }

        return false
    }
}