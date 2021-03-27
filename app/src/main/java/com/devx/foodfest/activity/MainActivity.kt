package com.devx.foodfest.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import com.devx.foodfest.R
import com.devx.foodfest.fragment.*
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var navigationView: NavigationView
    lateinit var tvName: TextView
    lateinit var tvMobileNo: TextView
    lateinit var txtUser: TextView
    lateinit var txtNumber: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences =
            this.getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE)


        var previousMenuItem: MenuItem? = null
        supportActionBar?.title = "Dashboard"

        navigationView = findViewById(R.id.naView)
        val headerView = navigationView.getHeaderView(0)

        txtUser = headerView.findViewById(R.id.tvNameDrawer)
        txtNumber = headerView.findViewById(R.id.tvPhoneDrawer)

        txtUser.text = sharedPreferences.getString("name", "UserName")
        txtNumber.text = "+91- ${sharedPreferences.getString("mobile_number", "9999999999")}"

        naView.setNavigationItemSelectedListener {

            if (previousMenuItem != null) {
                previousMenuItem!!.isChecked = false
            }

            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it

            when (it.itemId) {


                R.id.home -> {
                    openHomeFrag()
                }

                R.id.favourites -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, FavoriteFragment(this))
                        .commit()

                    supportActionBar?.title = "Favorite"
                    drawerLayout.closeDrawers()
                }

                R.id.profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, ProfileFragment(this))
                        .commit()

                    supportActionBar?.title = "Profile"
                    drawerLayout.closeDrawers()
                }


                R.id.faq -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, FaqFragment())
                        .commit()

                    supportActionBar?.title = "Frequently Asked Questions"
                    drawerLayout.closeDrawers()
                }

                R.id.order_history -> {
                    val intent = Intent(this@MainActivity, OrderHistoryActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawers()
                }


                R.id.logout -> {
                    val dialog = AlertDialog.Builder(this)
                    dialog.setTitle("Are You Sure")
                    dialog.setMessage("Do you Want To Logout")
                    dialog.setPositiveButton("Yes") { text, listener ->
                        sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()
                        ActivityCompat.finishAffinity(this)
                        finish()

                    }

                    dialog.setNegativeButton("No") { text, listener ->

                    }
                    dialog.create()
                    dialog.show()
                }
            }

            return@setNavigationItemSelectedListener true
        }

        setUpToolbar()
        toggleActionBar()
        openHomeFrag()
    }


    private fun setUpToolbar() {
        setSupportActionBar(toolBar)
        supportActionBar?.title = "Dashboard"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun toggleActionBar() {
        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@MainActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        return super.onOptionsItemSelected(item)
    }

    private fun openHomeFrag() {
        val fragment = HomeFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, fragment)
        transaction.commit()
        supportActionBar?.title = "Home"
        naView.setCheckedItem(R.id.home)
        drawerLayout.closeDrawers()
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frame)

        when (frag) {
            !is HomeFragment -> openHomeFrag()

            else -> super.onBackPressed()
        }
    }

}