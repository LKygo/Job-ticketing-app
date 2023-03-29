package com.symphony.symphony

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.NetworkError
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.symphony.symphony.databinding.ActivityTicketBinding
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*



class TicketActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTicketBinding
    private lateinit var ticketNo: String
    private lateinit var customer: String
    private lateinit var faultReported: String
    private lateinit var date: String
    private lateinit var servicedate: String
    private lateinit var start_time: String
    private lateinit var end_time: String
    private lateinit var city: String
    private lateinit var created_at: String
    private lateinit var updatedby: String
    private lateinit var progressB: ProgressBar
    private lateinit var updateT: Button
    private lateinit var takePic: ImageView
    private  lateinit var byteArray : ByteArray
    private lateinit var imageBitmap : Bitmap
    private val REQUEST_IMAGE_CAPTURE = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTicketBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val bundle: Bundle? = intent.extras

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        servicedate = dateFormat.format(Date())
        progressB = binding.pgbTDProgress
        progressB.visibility = View.GONE
        updateT = binding.btnTDUpdate

        byteArray = ByteArray(0)
        ticketNo = bundle?.getString("ticketNo").toString()
        customer = bundle?.getString("customer").toString()
        city = bundle?.getString("location").toString()
        faultReported = bundle?.getString("faultReported").toString()
        date = bundle?.getString("date").toString()
        updatedby = bundle?.getString("userID").toString()
        val sTime = bundle?.getString("startTime").toString()

        binding.txvTDTicketNOValue.text = ticketNo
        binding.txvTDClientValue.text = customer
        binding.txvTDFaultReportedValue.text = faultReported
        binding.txvTDServiceDateValue.text = servicedate
        binding.txvTDLocationValue.text = city
        binding.txvTDStartValue.text = sTime

        start_time = sTime
        created_at = date


        takePic = binding.imgCamera
        takePic.setOnClickListener {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
            ) {
                // Permission is granted

                val picIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(picIntent, REQUEST_IMAGE_CAPTURE)
            } else {
                // Permission is not granted, request it
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.CAMERA),
                    REQUEST_IMAGE_CAPTURE
                )
            }
        }

        updateT.setOnClickListener {

            val currentTime = Date()
            val formatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            end_time = formatter.format(currentTime)
            val jobcardno = binding.edtTDJobCardNoValue.text.toString()
            val serialNo = binding.edtTDSerialNoValue.text.toString()
            val findings = binding.edtTDFindingsValue.text.toString()
            val action_taken = binding.edtTDActionsTakenValue.text.toString()
            val recommendations = binding.edtTDRecommendationsValue.text.toString()

            uploadImage(imageBitmap)

//            if (::imageBitmap.isInitialized) {
//                // Call uploadImage() function with the byteArray
//            } else {
//                // Display an error message that the byteArray is empty
//                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
//            }
//            if (jobcardno.isEmpty() || serialNo.isEmpty() || findings.isEmpty() || action_taken.isEmpty() || recommendations.isEmpty() || byteArray.isEmpty()) {
//
//                Toast.makeText(
//                    this,
//                    "Please fill all the fields with a red star",
//                    Toast.LENGTH_SHORT
//                ).show()
//            } else
//
//                try {
//                    sendTicketDetails(
//                        ticketNo,
//                        jobcardno,
//                        servicedate,
//                        start_time,
//                        end_time,
//                        serialNo,
//                        city,
//                        findings,
//                        action_taken,
//                        recommendations,
//                        updatedby,
//                        created_at
//                    )
//                    uploadImage(byteArray,jobcardno)
//
//
//                } catch (e: java.lang.Exception) {
//                    Log.d("FunPost", e.toString())
//                }
        }
        binding.btnTDClear.setOnClickListener {
            binding.edtTDActionsTakenValue.setText("")
            binding.edtTDFindingsValue.setText("")
            binding.edtTDSerialNoValue.setText("")
            binding.edtTDJobCardNoValue.setText("")
            binding.edtTDRecommendationsValue.setText("")
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Get the image data
            imageBitmap = data?.extras?.get("data") as Bitmap
            binding.imgPreview.setImageBitmap(imageBitmap)

        }
    }


    private fun uploadImage(bitmap: Bitmap) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val image = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)
        Log.d("pic", image)
        val name = "event_image"

        try {
            val jsonObject = JSONObject()
            jsonObject.put("name", name)
            jsonObject.put("image", image)

            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, "https://backend.api.symphony.co.ke/uploadI", jsonObject,
                { response ->
                    try {
                        val message = response.getString("message")
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//                        progressDialog.dismiss()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                { error ->
                    Log.d("pic", error.toString())
                    val errorMessage = error.networkResponse?.data?.toString(Charset.defaultCharset())
                    Log.e("Volley Error", errorMessage ?: "Unknown error")
                    Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()
//                    progressDialog.dismiss()
                }
            )

            val requestQueue = Volley.newRequestQueue(this)
            requestQueue.add(jsonObjectRequest)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }








    private fun sendTicketDetails(
        ticket_no: String,
        jobcard_no: String,
        service_date: String,
        start_time: String,
        end_time: String,
        serial_no: String,
        city: String,
        findings: String,
        action_taken: String,
        recommendations: String,
        updated_by: String,
        created_at: String
    ) {
        updateT.isClickable = false
        updateT.visibility = View.GONE
        progressB.visibility = View.VISIBLE
        val url = "https://backend.api.symphony.co.ke/uploadI"

        // Create a JSON object to hold your data
        val jsonObject = JSONObject()
        jsonObject.put("ticket_no", ticket_no)
        jsonObject.put("jobcard_no", jobcard_no)
        jsonObject.put("service_date", service_date)
        jsonObject.put("start_time", start_time)
        jsonObject.put("end_time", end_time)
        jsonObject.put("serial_no", serial_no)
        jsonObject.put("city", city)
        jsonObject.put("findings", findings)
        jsonObject.put("action_taken", action_taken)
        jsonObject.put("recommendations", recommendations)
        jsonObject.put("updated_by", updated_by)
        jsonObject.put("created_at", created_at)


// Request to server's URL
        val request = object : StringRequest(Method.POST, url,
            { response ->
                // Handle successful response from server

                updateT.isClickable = true
                updateT.visibility = View.VISIBLE
                progressB.visibility = View.GONE
                Toast.makeText(
                    this@TicketActivity,
                    "Successfully updated Ticket",
                    Toast.LENGTH_SHORT
                ).show()

                binding.edtTDActionsTakenValue.setText("")
                binding.edtTDFindingsValue.setText("")
                binding.edtTDSerialNoValue.setText("")
                binding.edtTDJobCardNoValue.setText("")
                binding.edtTDRecommendationsValue.setText("")

            },
            { error ->
                // Handle error response from server

                updateT.isClickable = true
                updateT.visibility = View.VISIBLE
                progressB.visibility = View.GONE
                Toast.makeText(
                    this@TicketActivity,
                    "Failed to update Ticket",
                    Toast.LENGTH_SHORT
                )
                    .show()

                Log.e("Volley", "Error: $error")
            }) {

            override fun parseNetworkResponse(response: NetworkResponse): Response<String> {
                val status = response.statusCode
                val responseBody = String(response.data)
                Log.d("StatusCode", "Response body: $responseBody")
                Log.d("StatusCode", "Status code: $status")

                if (status == 200) {
                    return Response.success(
                        responseBody,
                        HttpHeaderParser.parseCacheHeaders(response)
                    )
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









