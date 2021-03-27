package com.devx.foodfest.activity

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.devx.foodfest.R
import com.devx.foodfest.adapter.RestaurantMenuAdapter
import com.devx.foodfest.model.RestaurantMenu
import com.devx.foodfest.util.ConnectionManager

class RestaurantMenuActivity : AppCompatActivity() {

    lateinit var toolBar: androidx.appcompat.widget.Toolbar
    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var menuAdapter: RestaurantMenuAdapter
    lateinit var restaurantId: String
    lateinit var restaurantName: String
    lateinit var proceedToCartLayout: RelativeLayout
    lateinit var btnProceedToCart: Button
    lateinit var menuProgress: RelativeLayout
    var restaurantMenuList = arrayListOf<RestaurantMenu>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resturant_menu)

        proceedToCartLayout = findViewById(R.id.relativeLayoutProceedToCart)
        btnProceedToCart = findViewById(R.id.btnProceedToCart)
        toolBar = findViewById(R.id.toolMenuBar)

        restaurantId = intent.getStringExtra("restaurantId")!!
        restaurantName = intent.getStringExtra("restaurantName")!!
        layoutManager = LinearLayoutManager(this)
        menuProgress = findViewById(R.id.progressMenu)
        recyclerView = findViewById(R.id.recyclerViewRestaurantMenu)

        fetchData()
        setToolBar()

    }

    fun fetchData() {

        val queue = Volley.newRequestQueue(this)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/$restaurantId"

        if (ConnectionManager().checkConnectivity(this)) {

            menuProgress.visibility = View.INVISIBLE
            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                    try {
                        val response = it.getJSONObject("data")
                        val success = response.getBoolean("success")

                        if (success) {
                            restaurantMenuList.clear()
                            val data = response.getJSONArray("data")
                            for (i in 0 until data.length()) {

                                val restaurant = data.getJSONObject(i)
                                val menuObject = RestaurantMenu(
                                    restaurant.getString("id"),
                                    restaurant.getString("name"),
                                    restaurant.getString("cost_for_one")
                                )

                                restaurantMenuList.add(menuObject)
                                menuAdapter = RestaurantMenuAdapter(
                                    this,
                                    restaurantId,
                                    restaurantName,
                                    proceedToCartLayout,
                                    btnProceedToCart,
                                    restaurantMenuList
                                )


                                recyclerView.adapter = menuAdapter
                                recyclerView.layoutManager = layoutManager

                            }

                        } else {
                            Toast.makeText(this, "Some Error", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                }, Response.ErrorListener
                {
                    Toast.makeText(this, "Volley Error", Toast.LENGTH_SHORT).show()
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "9bf534118365f1"
                        return headers
                    }

                }

            queue.add(jsonObjectRequest)

        } else {
            Toast.makeText(this, " Error", Toast.LENGTH_SHORT).show()
        }


    }

    fun setToolBar() {
        setSupportActionBar(toolBar)
        supportActionBar?.title = restaurantName
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
    }

    override fun onBackPressed() {
        if (menuAdapter.getSelectedItemCount() > 0) {

            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this)
            alterDialog.setTitle("Alert!")
            alterDialog.setMessage("Going back will remove everything from cart")
            alterDialog.setPositiveButton("Okay") { _, _ ->
                super.onBackPressed()
            }
            alterDialog.setNegativeButton("Cancel") { _, _ ->

            }
            alterDialog.show()
        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                if (menuAdapter.getSelectedItemCount() > 0) {
                    val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this)
                    alterDialog.setTitle("Alert!")
                    alterDialog.setMessage("Going back will remove everything from cart")
                    alterDialog.setPositiveButton("Okay") { _, _ ->
                        super.onBackPressed()
                    }
                    alterDialog.setNegativeButton("Cancel") { _, _ ->

                    }
                    alterDialog.show()
                } else {
                    super.onBackPressed()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }


}