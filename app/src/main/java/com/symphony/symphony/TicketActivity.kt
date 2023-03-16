package com.symphony.symphony

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
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
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.symphony.symphony.databinding.ActivityTicketBinding
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import java.io.ByteArrayOutputStream
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



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
    private lateinit var byteArray : ByteArray
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


            if (jobcardno.isEmpty() || serialNo.isEmpty() || findings.isEmpty() || action_taken.isEmpty() || recommendations.isEmpty()) {

                Toast.makeText(
                    this,
                    "Please fill all the fields with a red star",
                    Toast.LENGTH_SHORT
                ).show()
            } else

                try {
                    sendTicketDetails(
                        ticketNo,
                        jobcardno,
                        servicedate,
                        start_time,
                        end_time,
                        serialNo,
                        city,
                        findings,
                        action_taken,
                        recommendations,
                        updatedby,
                        created_at
                    )


                } catch (e: java.lang.Exception) {
                    Log.d("FunPost", e.toString())
                }
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
            val imageBitmap = data?.extras?.get("data") as Bitmap

            // Convert the image to a byte array
            val byteArrayOutputStream = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            byteArray = byteArrayOutputStream.toByteArray()


            // Upload the image to the server
            uploadImage(byteArray)
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
            val url = "https://backend.api.symphony.co.ke/upload"

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


fun uploadImage(imageData: ByteArray) {
    val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), imageData)
    val imagePart = MultipartBody.Part.createFormData("image", "image.png", requestBody)

    val retrofit = Retrofit.Builder()
        .baseUrl("https://example.com/api/")
        .build()

    val service = retrofit.create(ApiService::class.java)
    val call = service.uploadImage(imagePart)
    call.enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            // Image uploaded successfully
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            // Image upload failed
        }
    })
}






