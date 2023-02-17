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
    private lateinit var ticketNo: String
    private lateinit var customer: String
    private lateinit var faultReported: String
    private lateinit var date: String

    private lateinit var ticket_no: String
    private lateinit var jobcardno: String
    private lateinit var servicedate:String
    private lateinit var start_time: String
    private lateinit var end_time: String
    private lateinit var serialno: String
    private lateinit var city: String
    private lateinit var findings : String
    private lateinit var action_taken: String
    private lateinit var recommendations: String
    private lateinit var updated_at : String
    private lateinit var created_at: String
    private lateinit var updatedby: String






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

        binding.txvTDTicketNOValue.text = ticketNo
        binding.txvTDClientValue.text = customer
        binding.txvTDFaultReportedValue.text = faultReported
        binding.txvTDServiceDateValue.text = date


        ticket_no = "yesno"
        jobcardno = "kwmkwel394i34"
        servicedate = "2000-20-20T00:00:00.0000Z"
        start_time ="09:46:45"
        end_time = "09:47:45"
        serialno ="mdejnd83"
        city = "nairobi"
        findings ="ksdcksjencjniowemdimweiodmwedmioqwmOM"
        action_taken ="installed UPS"
        recommendations= "new battery"
        updatedby="3"
        updated_at ="2022-06-06 14:14:49"
        created_at="2021-02-17 09:46:44"

        if (ticketNo.isNotEmpty() && date.isNotEmpty() && customer.isNotEmpty() && faultReported.isNotEmpty()) {
            Log.d("TicketDetails", "$ticketNo, $customer, $faultReported, $date")
        }

//        binding.btnTDUpdate.setOnClickListener {
//
//                try {
//                    postTicket(ticket_no, jobcardno, servicedate, start_time)
//                }
//                catch (e:java.lang.Exception){
//                    Log.d("FunPost", e.toString())
//                }
//
//
//
//        }



    }
//    private fun sendOkHTTp(){
//        val client = OkHttpClient()
//        val url = "https://backend.api.symphony.co.ke/upload"
//
//        val jsonObject = JSONObject()
//        jsonObject.put("ticket_no", "OkHttp incoming")
//        jsonObject.put("jobcardno", "jobcard123")
//        jsonObject.put("servicedate", "service today 17th")
//
//        val request =  RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), jsonObject.toString())
//        val postRequest = Request.Builder()
//            .url(url)
//            .post(request)
//            .build()
//    }





//    @Throws(IOException::class)
//    private fun uploadTicket(ticketNo: String, customer: String, faultReported: String, date:String) {
//
//        val url = "backend.api.symphony.co.ke/upload"
//        val client = OkHttpClient
//        val jsTicket = "{\"ticketNo\":$ticketNo,\"customer\":$customer,\"faultReported\":$faultReported,\"date\"$date\"}"
//        val body = RequestBody.create(
//            "application/json".toMediaTypeOrNull(), jsTicket)
//
//        val request = Request.Builder()
//            .url(url)
//            .post(body)
//            .build()
//
//        val call = client.newCall(request)
//
//    }

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




