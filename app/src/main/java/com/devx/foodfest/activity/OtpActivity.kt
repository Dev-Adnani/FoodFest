package com.devx.foodfest.activity

import android.app.AlertDialog
import android.content.Intent
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

class OtpActivity : AppCompatActivity() {

    lateinit var etOTP: EditText
    lateinit var etPass: EditText
    lateinit var etConfirmPass: EditText
    lateinit var resetBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        etOTP = findViewById(R.id.otpET)
        etPass = findViewById(R.id.newPassET)
        etConfirmPass = findViewById(R.id.confirmPassET)
        resetBtn = findViewById(R.id.resetBtn)

        var mobileNumber: String? = "82000705603"
        mobileNumber = intent.getStringExtra("mobile_number")

        resetBtn.setOnClickListener {
            if (etOTP.text.isBlank() || etOTP.text.length != 4) {
                etOTP.error = "Please Check Your OTP Once Again"
            } else {
                if (etPass.text.isBlank() || etConfirmPass.text.isBlank()) {
                    etPass.error = "Please Check Your Password Once"
                } else {
                    if (etPass.text.isNotBlank() || etPass.text.isNotBlank()) {
                        if (etPass.text.toString().toInt() == etPass.text.toString().toInt()) {

                            if (ConnectionManager().checkConnectivity(this)) {
                                try {
                                    val resetPass = JSONObject()
                                    resetPass.put("mobile_number", mobileNumber.toString())
                                    resetPass.put("password", etPass.text)
                                    resetPass.put("otp", etOTP.text)

                                    val queue = Volley.newRequestQueue(this)
                                    val url = "http://13.235.250.119/v2/reset_password/fetch_result"

                                    val jsonRequest = object :
                                        JsonObjectRequest(Request.Method.POST,
                                            url,
                                            resetPass,
                                            Response.Listener
                                            {

                                                val response = it.getJSONObject("data")
                                                val success = response.getBoolean("success")

                                                if (success) {
                                                    val serverMessage =
                                                        response.getString("successMessage")

                                                    Toast.makeText(
                                                        this,
                                                        serverMessage,
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    backToLogin()
                                                } else {
                                                    val responseMessageServer =
                                                        response.getString("errorMessage")
                                                    print("RESPONSE ERROR$responseMessageServer")
                                                    Toast.makeText(
                                                        this,
                                                        responseMessageServer.toString(),
                                                        Toast.LENGTH_SHORT
                                                    ).show()

                                                }

                                            },
                                            Response.ErrorListener {
                                                Toast.makeText(
                                                    this,
                                                    "$it Error",
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
                            etConfirmPass.error = "Password's Do not Match"
                        }

                    }
                }


            }
        }
    }

    private fun backToLogin() {
        val startAct = Intent(this@OtpActivity, LoginActivity::class.java)
        startActivity(startAct)
        finish()
    }
}