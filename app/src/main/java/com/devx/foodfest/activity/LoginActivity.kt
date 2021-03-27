package com.devx.foodfest.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.animation.AnimationUtils
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
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject


class LoginActivity : AppCompatActivity() {

    lateinit var etMobileNumber: EditText
    lateinit var etPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        logoAnim()

        etMobileNumber = findViewById(R.id.mobileLoginET)
        etPassword = findViewById(R.id.passwordLoginET)


        loginBtn.setOnClickListener {

            if (etMobileNumber.text.isBlank() || etMobileNumber.text.length != 10) {
                etMobileNumber.error = "Enter valid Mobile Number"
            } else {
                if (etPassword.text.isBlank() || (etPassword.text.length <= 4)) {
                    etPassword.error = "Enter Valid Password"
                } else {
                    loginUserFun()
                }

            }

        }
        forgetPassTxtView.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgetPasswordActivity::class.java)
            startActivity(intent)
        }
        signUpTxtView.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

    }

    private fun logoAnim() {

        val animSlide = AnimationUtils.loadAnimation(applicationContext, R.anim.slide)
        loginImageView.startAnimation(animSlide)

    }

    private fun loginUserFun() {
        val loginUser = JSONObject()
        loginUser.put("mobile_number", etMobileNumber.text)
        loginUser.put("password", etPassword.text)

        val queue = Volley.newRequestQueue(this)
        val url = "http://13.235.250.119/v2/login/fetch_result/"

        if (ConnectionManager().checkConnectivity(this)) {

            val sharedPreferences = this.getSharedPreferences(
                getString(R.string.shared_preferences),
                Context.MODE_PRIVATE
            )

            try {
                val jsonRequest =
                    object :
                        JsonObjectRequest(Request.Method.POST, url, loginUser, Response.Listener
                        {

                            val response = it.getJSONObject("data")
                            val success = response.getBoolean("success")

                            if (success) {
                                val data = response.getJSONObject("data")
                                sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
                                sharedPreferences.edit()
                                    .putString("user_id", data.getString("user_id")).apply()
                                sharedPreferences.edit().putString("name", data.getString("name"))
                                    .apply()
                                sharedPreferences.edit().putString("email", data.getString("email"))
                                    .apply()
                                sharedPreferences.edit()
                                    .putString("mobile_number", data.getString("mobile_number"))
                                    .apply()
                                sharedPreferences.edit()
                                    .putString("address", data.getString("address")).apply()

                                Toast.makeText(
                                    this,
                                    "Welcome " + data.getString("name"),
                                    Toast.LENGTH_SHORT
                                ).show()

                                userSuccessfullyLoggedIn()
                            } else {
                                Toast.makeText(
                                    this,
                                    " Incorrect Mobile No Or Password",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }


                        }, Response.ErrorListener {
                            Toast.makeText(this, "Volley Error", Toast.LENGTH_SHORT).show()
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
    }

    private fun userSuccessfullyLoggedIn() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


}