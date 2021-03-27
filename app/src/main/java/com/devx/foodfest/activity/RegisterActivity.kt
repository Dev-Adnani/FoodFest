package com.devx.foodfest.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.devx.foodfest.R
import com.devx.foodfest.util.ConnectionManager
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {

    lateinit var etName: EditText
    lateinit var etEmail: EditText
    lateinit var etMobileNumber: EditText
    lateinit var etDeliveryAddress: EditText
    lateinit var etPassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var sharedPreferences: SharedPreferences
    lateinit var submitBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        sharedPreferences =
            this.getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE)

        etName = findViewById(R.id.nameRegET)
        etEmail = findViewById(R.id.emailRegET)
        etMobileNumber = findViewById(R.id.mobileRegET)
        etDeliveryAddress = findViewById(R.id.deliveryRegET)
        etPassword = findViewById(R.id.passRegET)
        etConfirmPassword = findViewById(R.id.confirmPassRegET)
        submitBtn = findViewById(R.id.signUpRegBtn)

        submitBtn.setOnClickListener {
            sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()

            if (checkErrors()) {
                if (ConnectionManager().checkConnectivity(this)) {

                    try {
                        val regUser = JSONObject()
                        regUser.put("name", etName.text)
                        regUser.put("mobile_number", etMobileNumber.text)
                        regUser.put("password", etPassword.text)
                        regUser.put("address", etDeliveryAddress.text)
                        regUser.put("email", etEmail.text)


                        val queue = Volley.newRequestQueue(this)
                        val url = "http://13.235.250.119/v2/register/fetch_result/"

                        val jsonRequest = object :
                            JsonObjectRequest(Request.Method.POST, url, regUser, Response.Listener {

                                val response = it.getJSONObject("data")
                                val success = response.getBoolean("success")

                                if (success) {
                                    val data = response.getJSONObject("data")
                                    sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
                                    sharedPreferences.edit()
                                        .putString("user_id", data.getString("user_id")).apply()
                                    sharedPreferences.edit()
                                        .putString("name", data.getString("name")).apply()
                                    sharedPreferences.edit()
                                        .putString("email", data.getString("email")).apply()
                                    sharedPreferences.edit()
                                        .putString(
                                            "mobile_number",
                                            data.getString("mobile_number")
                                        )
                                        .apply()
                                    sharedPreferences.edit()
                                        .putString("address", data.getString("address")).apply()

                                    Toast.makeText(
                                        this,
                                        "User Registered Successfully " + data.getString("name"),
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    userSuccessfullyRegistered()
                                } else {
                                    val errorMessageServer = response.getString("errorMessage")
                                    Toast.makeText(
                                        this,
                                        "${errorMessageServer.toString()} ",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }

                            },
                                Response.ErrorListener {
                                    Toast.makeText(this, "$it Error", Toast.LENGTH_SHORT).show()
                                }) {
                            override fun getHeaders(): MutableMap<String, String> {
                                val headers = HashMap<String, String>()
                                headers["Content-type"] = "application/json"
                                headers["token"] = "9bf534118365f1"
                                return headers
                            }
                        }
                        queue.add(jsonRequest)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                } else {
                    val dialog = AlertDialog.Builder(this)
                    dialog.setTitle("ERROR")
                    dialog.setMessage("Internet Connection  Not Found")
                    dialog.setPositiveButton("Open Settings") { text, listener ->
                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        finish()

                    }

                    dialog.setNegativeButton("Exit") { text, listener ->
                        ActivityCompat.finishAffinity(this)
                    }
                    dialog.create()
                    dialog.show()
                }
            } else {
                Toast.makeText(this, "Some Error Occurred , Please Try Again ", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun userSuccessfullyRegistered() {
        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun checkErrors(): Boolean {
        var noError = 0
        if (etName.text.isBlank() || etName.text.length <= 3) {
            etName.error = "Please Enter Your Name Properly"
        } else {
            noError++
        }

        if (etEmail.text.isBlank()) {
            etEmail.error = "Please Enter Your Email Properly"
        } else {
            noError++
        }

        if (etMobileNumber.text.isBlank() || etMobileNumber.text.length != 10) {
            etMobileNumber.error = " Please Check your Mobile No"
        } else {
            noError++
        }

        if (etDeliveryAddress.text.isBlank()) {
            etDeliveryAddress.error = "Please Check Your Delivery Address"
        } else {
            noError++
        }

        if (etPassword.text.isBlank() || etPassword.text.length <= 4) {
            etPassword.error = "Invalid Password!"
        } else {
            noError++
        }

        if (etConfirmPassword.text.isBlank()) {
            etConfirmPassword.error = "Field Missing!"
        } else {
            noError++
        }

        if (etPassword.text.isNotBlank() || etConfirmPassword.text.isNotBlank()) {
            if (etPassword.text.toString().toInt() == etConfirmPassword.text.toString().toInt()) {
                noError++
            } else {
                etConfirmPassword.error = "Password's Do not Match"
            }
        }

        return noError == 7
    }
}