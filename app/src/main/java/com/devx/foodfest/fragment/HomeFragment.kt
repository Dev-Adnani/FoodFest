package com.devx.foodfest.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.devx.foodfest.R
import com.devx.foodfest.adapter.RestaurantRecycleAdapter
import com.devx.foodfest.model.Restaurant
import com.devx.foodfest.util.ConnectionManager
import kotlinx.android.synthetic.main.sort_data.view.*
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap


class HomeFragment : Fragment() {

    lateinit var recyclerDashboard: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: RestaurantRecycleAdapter
    lateinit var homeProgress: RelativeLayout
    lateinit var radioButtonView: View

    val restaurantInfoList = arrayListOf<Restaurant>()

    var ratingComp = Comparator<Restaurant> { res1, res2 ->

        if (res1.restaurantRating.compareTo(res2.restaurantRating, true) == 0) {
            res1.restaurantName.compareTo(res2.restaurantRating, true)
        } else {
            res1.restaurantRating.compareTo(res2.restaurantRating, true)
        }

    }

    var costComparator = Comparator<Restaurant> { rest1, rest2 ->

        rest1.restaurantPrice.compareTo(rest2.restaurantPrice, true)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        setHasOptionsMenu(true)


        recyclerDashboard = view.findViewById(R.id.recyclerDashboard)
        layoutManager = LinearLayoutManager(activity)
        homeProgress = view.findViewById(R.id.progressHomeLayout)

        val queue = Volley.newRequestQueue(activity as Context)

        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"
        if (ConnectionManager().checkConnectivity(activity as Context)) {
            homeProgress.visibility = View.INVISIBLE
            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                    try {
                        val response = it.getJSONObject("data")
                        val success = response.getBoolean("success")

                        if (success) {
                            val data = response.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val restaurantJsonObject = data.getJSONObject(i)
                                val restaurantObject = Restaurant(
                                    restaurantJsonObject.getString("id"),
                                    restaurantJsonObject.getString("name"),
                                    restaurantJsonObject.getString("rating"),
                                    restaurantJsonObject.getString("cost_for_one"),
                                    restaurantJsonObject.getString("image_url")
                                )

                                restaurantInfoList.add(restaurantObject)
                                recyclerAdapter = RestaurantRecycleAdapter(
                                    activity as Context,
                                    restaurantInfoList
                                )

                                recyclerDashboard.adapter = recyclerAdapter
                                recyclerDashboard.layoutManager = layoutManager

                            }
                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Some Error",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }, Response.ErrorListener {
                    if (activity != null) {
                        Toast.makeText(activity as Context, "Volley Error", Toast.LENGTH_SHORT)
                            .show()
                    }
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
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("ERROR")
            dialog.setMessage("Internet Connection  Not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()

            }

            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }



        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.menu_dashboard, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.sort -> {
                radioButtonView = View.inflate(context, R.layout.sort_data, null)

                androidx.appcompat.app.AlertDialog.Builder(
                    activity as Context,
                    R.style.MyDialogTheme
                )
                    .setTitle("Sort By?")
                    .setView(radioButtonView)
                    .setPositiveButton("OK") { _, _ ->

                        if (radioButtonView.radio_high_to_low.isChecked) {
                            Collections.sort(restaurantInfoList, costComparator)
                            restaurantInfoList.reverse()
                            recyclerAdapter.notifyDataSetChanged()
                        }
                        if (radioButtonView.radio_low_to_high.isChecked) {
                            Collections.sort(restaurantInfoList, costComparator)
                            recyclerAdapter.notifyDataSetChanged()
                        }
                        if (radioButtonView.radio_rating.isChecked) {
                            Collections.sort(restaurantInfoList, ratingComp)
                            restaurantInfoList.reverse()
                            recyclerAdapter.notifyDataSetChanged()
                        }

                    }
                    .setNegativeButton("Cancel") { _, _ ->

                    }
                    .create()
                    .show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

}