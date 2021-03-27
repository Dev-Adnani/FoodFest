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
import kotlinx.android.synthetic.main.activity_forget_password.*
import org.json.JSONObject
import java.lang.Exception

class ForgetPasswordActivity : AppCompatActivity() {

    lateinit var etMobileNumber: EditText
    lateinit var etEmail: EditText
    lateinit var nextBtn: Button
    lateinit var impData: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

        etMobileNumber = findViewById(R.id.mobileForgetET)
        etEmail = findViewById(R.id.emailForgetET)
        nextBtn = findViewById(R.id.nextBtn)

        nextBtn.setOnClickListener {
            if (etMobileNumber.text.isBlank() || etMobileNumber.text.length != 10) {
                etMobileNumber.error = "Invalid Mobile No"
            } else {
                impData = etMobileNumber.text.toString()
                if (etEmail.text.isBlank()) {
                    etEmail.error = "Please Check Your Email Once"
                } else {


                    if (ConnectionManager().checkConnectivity(this)) {
                        try {
                            val loginUser = JSONObject()
                            loginUser.put("mobile_number", etMobileNumber.text)
                            loginUser.put("email", etEmail.text)

                            val queue = Volley.newRequestQueue(this)
                            val url = "http://13.235.250.119/v2/forgot_password/fetch_result"


                            val jsonRequest = object : JsonObjectRequest(Request.Method.POST,
                                url,
                                loginUser,
                                Response.Listener {

                                    val response = it.getJSONObject("data")
                                    val success = response.getBoolean("success")

                                    if (success) {
                                        val firstTry = response.getBoolean("first_try")


                                        if (firstTry) {
                                            Toast.makeText(
                                                this,
                                                "OTP SENT SUCCESSFULLY",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            userOTP()
                                        } else {
                                            Toast.makeText(
                                                this,
                                                "OTP ALREADY SENT",
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                            userOTP()
                                        }

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


                }
            }
        }
    }


    private fun userOTP() {
        val intent = Intent(this@ForgetPasswordActivity, OtpActivity::class.java)
        intent.putExtra("mobile_number", impData)
        startActivity(intent)
        finish()
    }

}
