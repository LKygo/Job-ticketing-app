package com.symphony.symphony

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.symphony.symphony.databinding.ActivityTicketBinding

class TicketActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTicketBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTicketBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val bundle: Bundle? = intent.extras

        val ticketNo = bundle?.getString("ticketNo")
        val customer = bundle?.getString("customer")
        val faultReported = bundle?.getString("faultReported")
        val date = bundle?.getString("date")

        binding.txvTDTicketNOValue.text = ticketNo
        binding.txvTDClientValue.text = customer
        binding.txvTDFaultReportedValue.text = faultReported
        binding.txvTDServiceDateValue.text = date

        if (ticketNo != null) {
            Log.d("TicketDetails", "$ticketNo, $customer, $faultReported, $date")
        }


    }


}