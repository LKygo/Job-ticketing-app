package com.symphony.symphony

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.NetworkError
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.symphony.symphony.databinding.ActivityTicketBinding
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class TicketActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTicketBinding
    private lateinit var ticketNo: String
    private lateinit var customer: String
    private lateinit var faultReported: String
    private lateinit var date: String
    private lateinit var serialNo: String
    private lateinit var jobcardno: String
    private lateinit var servicedate: String
    private lateinit var start_time: String
    private lateinit var end_time: String
    private lateinit var serialno: String
    private lateinit var city: String
    private lateinit var findings: String
    private lateinit var action_taken: String
    private lateinit var recommendations: String
    private lateinit var updated_at: String
    private lateinit var created_at: String
    private lateinit var updatedby: String


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTicketBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val bundle: Bundle? = intent.extras

        ticketNo = bundle?.getString("ticketNo").toString()
        customer = bundle?.getString("customer").toString()
        faultReported = bundle?.getString("faultReported").toString()
        date = bundle?.getString("date").toString()

        val sTime =  bundle?.getString("startTime").toString()

        binding.txvTDTicketNOValue.text = ticketNo
        binding.txvTDClientValue.text = customer
        binding.txvTDFaultReportedValue.text = faultReported
        binding.txvTDServiceDateValue.text = date
        binding.txvTDLocationValue.text = customer
        jobcardno = binding.txvTDSJobCardNoValue.text.toString().trim()
        serialNo = binding.txvTDSerialNoValue.text.toString().trim()
        findings = binding.txvTDFindingsValue.text.toString().trim()
        action_taken = binding.txvTDActionsTakenValue.text.toString().trim()
        recommendations = binding.txvTDRecommendationsValue.text.toString().trim()
        servicedate = LocalDate.now().toString()
        start_time = sTime
        created_at = date










        jobcardno = "kwmkwel394i34"
        servicedate = "2000-20-20T00:00:00.0000Z"
        start_time = "09:46:45"
        end_time = "09:47:45"
        serialno = "mdejnd83"
        city = "nairobi"
        findings = "ksdcksjencjniowemdimweiodmwedmioqwmOM"
        action_taken = "installed UPS"
        recommendations = "new battery"
        updatedby = "3"
        updated_at = "2022-06-06 14:14:49"
        created_at = "2021-02-17 09:46:44"

//        if (ticketNo.isNotEmpty() && date.isNotEmpty() && customer.isNotEmpty() && faultReported.isNotEmpty()) {
//            Log.d("TicketDetails", "$ticketNo, $customer, $faultReported, $date")
//        }

        binding.btnTDUpdate.setOnClickListener {

            val currentTime = LocalTime.now()
            val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
            val endTime = currentTime.format(formatter)
            end_time =endTime

            try {
                sendTicketDetails()
            } catch (e: java.lang.Exception) {
                Log.d("FunPost", e.toString())
            }
        }
    }


    private fun sendTicketDetails() {
        val url = "https://backend.api.symphony.co.ke/upload"

        // Create a JSON object to hold your data
        val jsonObject = JSONObject()
        jsonObject.put("ticket_no", "dmendawod")
        jsonObject.put("jobcardno", 2836846)
        jsonObject.put("ticket_no", "dmendawod")
        jsonObject.put("ticket_no", "dmendawod")
        jsonObject.put("ticket_no", "dmendawod")
        jsonObject.put("ticket_no", "dmendawod")
        jsonObject.put("ticket_no", "dmendawod")
        jsonObject.put("ticket_no", "dmendawod")
        jsonObject.put("ticket_no", "dmendawod")
        jsonObject.put("ticket_no", "dmendawod")
        jsonObject.put("ticket_no", "dmendawod")
        jsonObject.put("ticket_no", "dmendawod")



// Create a request to your server's URL
        val request = object : StringRequest(Method.POST, url,
            { response ->
                // Handle successful response from server
                Toast.makeText(this@TicketActivity, "Successfully updated Ticket", Toast.LENGTH_SHORT).show()

                Log.d("Volley", "Response: $response")
            },
            { error ->
                // Handle error response from server
                Toast.makeText(this@TicketActivity, "Failed to update Ticket", Toast.LENGTH_SHORT).show()

                Log.e("Volley", "Error: $error")
            }) {

            override fun parseNetworkResponse(response: NetworkResponse): Response<String> {
                val status = response.statusCode
                val responseBody = String(response.data)
                Log.d("StatusCode", "Response body: $responseBody")
                Log.d("StatusCode", "Status code: $status")

                if (status == 200) {
                    return Response.success(responseBody, HttpHeaderParser.parseCacheHeaders(response))
                } else {
                    return Response.error(NetworkError())
                }
            }

            override fun getBody(): ByteArray {
                // Convert the JSON object to a byte array
                return jsonObject.toString().toByteArray(Charsets.UTF_8)
            }

            override fun getBodyContentType(): String {
                // Set the content type to "application/json"
                return "application/json"
            }
        }
// Add the request to the request queue
        Volley.newRequestQueue(this@TicketActivity).add(request)

    }

}




