package com.symphony.symphony

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley

class TicketActivity : AppCompatActivity() {
    private lateinit var id : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ticket)

        val bundle : Bundle?= intent.extras

        val ticketNo = bundle!!.getString("ticketNo")
        val customer = bundle.getString("customer")
        val faultReported = bundle.getString("faultReported")
        val date = bundle.getString("date")

        if (ticketNo != null) {
            Log.d("TicketDetails", "$ticketNo, $customer, $faultReported, $date")
        }


    }


}