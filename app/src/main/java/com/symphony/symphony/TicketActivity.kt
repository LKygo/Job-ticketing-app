package com.symphony.symphony

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.symphony.symphony.databinding.ActivityTicketBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody

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

        sendTicketDetails()

    }


    private fun sendTicketDetails() {
val url = "backend.api.symphony.co.ke/upload"

        val client = OkHttpClient().newBuilder()
            .build()
        val mediaType = "application/json".toMediaTypeOrNull();
        val body = RequestBody.create(
            mediaType,
            "{\n\n\"ticket_no\":\"yesno\",\n\"jobcardno\":\"kwmkwel394i34\",\n\"servicedate\":\"2000-20-20T00:00:00.0000Z\",\n\"start_time\":\"09:46:45\",\n\"end_time\":\"09:47:45\",\n\"serialno\":\"mdejnd83\",\n\"city\":\"nairobi\",\n\"findings\":\"ksdcksjencjniowemdimweiodmwedmioqwmOM\",\n\"action_taken\":\"installed UPS\",\n\"recommendations\":\"new battery\",\n\"updatedby\":\"3\",\n\"updated_at\":\"2022-06-06 14:14:49\",\n\"created_at\":\"2021-02-17 09:46:44\"\n\n}\n\n\n"
        )
        val request = okhttp3.Request.Builder()
            .url(url)
            .method("POST", body)
            .addHeader("Content-Type", "application/json")
            .build()
        val response = client.newCall(request).execute();
        Log.d("Post", response.toString())
    }
}


