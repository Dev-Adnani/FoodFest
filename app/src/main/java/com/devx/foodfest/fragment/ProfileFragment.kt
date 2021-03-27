package com.devx.foodfest.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.devx.foodfest.R


class ProfileFragment(val contextParam: Context) : Fragment() {

    lateinit var tvName: TextView
    lateinit var tvMobileNo: TextView
    lateinit var tvEmail: TextView
    lateinit var tvAddress: TextView


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sharedPreferences = contextParam.getSharedPreferences(
            getString(R.string.shared_preferences),
            Context.MODE_PRIVATE
        )
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        tvName = view.findViewById(R.id.displayNameTxtView)
        tvMobileNo = view.findViewById(R.id.displayMobileNoTxtView)
        tvEmail = view.findViewById(R.id.displayEmailTxtView)
        tvAddress = view.findViewById(R.id.displayAddressTxtView)


        tvName.text = "Your Name : " + sharedPreferences.getString("name", "")
        tvMobileNo.text = "Your Mobile Number :" + sharedPreferences.getString("mobile_number", "")
        tvEmail.text = "Your Email :" + sharedPreferences.getString("email", "")
        tvAddress.text = "Your Address :" + sharedPreferences.getString("address", "")



        return view
    }

}