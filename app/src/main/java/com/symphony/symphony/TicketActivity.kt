package com.symphony.symphony

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.NetworkError
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.symphony.symphony.databinding.ActivityTicketBinding
import org.json.JSONObject
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
    private lateinit var byteArray: ByteArray
    private lateinit var attachment: String
    private lateinit var imageBitmap: Bitmap
    private var imageData: ByteArray? = null
    private val REQUEST_IMAGE_CAPTURE = 1
    private val PICK_PDF_FILE = 23
    val REQUEST_CODE = 1
    private lateinit var filePath: Uri
    private val root: DatabaseReference = FirebaseDatabase.getInstance().getReference("Jobcards")
    private val reference: StorageReference = FirebaseStorage.getInstance().getReference()


    companion object {
        private const val IMAGE_PICK_CODE = 999
    }


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


        val takePic = binding.imgCamera
        takePic.setOnClickListener {

            openGallery()

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

            if (filePath != null) {
                uploadToFirebase(filePath)
            } else {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            }


//            uploadPdf(filePath)
//            uploadImage(imageBitmap)
//            uploadOk(imageBitmap)
//            uploadVolley()
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

    private fun uploadToFirebase(uri: Uri) {
        val fileRef = reference.child("${System.currentTimeMillis()}" + "." + getFileExtension(uri))
        fileRef.putFile(uri).addOnSuccessListener(OnSuccessListener { taskSnapshot ->
            taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUrl ->
                // Use downloadUrl as needed
                attachment = downloadUrl.toString()
                Log.d("Attachment", "Image download URL: $attachment")
                progressB.visibility = View.GONE
                Toast.makeText(this, "Uploaded successfully", Toast.LENGTH_SHORT).show()

            }.addOnFailureListener { exception ->
                // Handle any errors
                Log.e("Attachment", "Error getting download URL: ${exception.message}", exception)
            }


        })
            .addOnProgressListener(OnProgressListener {
                progressB.visibility = View.VISIBLE
            }).addOnFailureListener(
                OnFailureListener { exception ->
                    Log.e("Firebase", "Upload failed", exception)

                    progressB.visibility = View.GONE

                    Toast.makeText(this, "JobCard upload failed", Toast.LENGTH_SHORT).show()
                })
    }

    private fun getFileExtension(mUri: Uri): String? {
        val contentResolver: ContentResolver = getContentResolver()
        val mime: MimeTypeMap = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(contentResolver.getType(mUri))
    }


    private fun openPdfPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
        }
        startActivityForResult(intent, PICK_PDF_FILE)
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == PICK_PDF_FILE && resultCode == Activity.RESULT_OK) {
//            val fileUri = data?.data
//            if (fileUri != null) {
//                try {
//                    // Open a stream to read the content of the selected file
//                    val inputStream = contentResolver.openInputStream(fileUri)
//
//                    if (inputStream != null) {
//                        // The user has selected a valid file
//                        Toast.makeText(this, "Pdf selected", Toast.LENGTH_SHORT).show()
//
//                        // Get the file path from the file URI
//                         filePath = File(fileUri.path)
//
//
//                    } else {
//                        // Failed to open the stream for the selected file
//                        Toast.makeText(this, "Failed to read the selected file", Toast.LENGTH_SHORT).show()
//                    }
//                } catch (e: Exception) {
//                    // Failed to open the selected file
//                    Toast.makeText(this, "Failed to open the selected file", Toast.LENGTH_SHORT).show()
//                }
//            } else {
//                // The user did not select a valid file
//                Toast.makeText(this, "No Pdf selected", Toast.LENGTH_SHORT).show()
//                // Show an error message or handle the error appropriately
//            }
//        }
//    }


//    @Throws(IOException::class)
//    private fun createImageData(uri: Uri) {
//        val inputStream = contentResolver.openInputStream(uri)
//        inputStream?.buffered()?.use {
//            imageData = it.readBytes()
//        }
//    }

//    @Deprecated("Deprecated in Java")
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            // Get the image data
//            imageBitmap = data?.extras?.get("data") as Bitmap
//            binding.imgPreview.setImageBitmap(imageBitmap)
//
//            val uri = data?.data
//            if (uri != null) {
//                binding.imgPreview.setImageURI(uri)
//                createImageData(uri)
//            }
//        }
//    }


    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE && data != null) {
            filePath = data.data!!

        }
    }


//private fun uploadOk(bitmap: Bitmap) {
//    val client = OkHttpClient.Builder().build()
//    val mediaType = "application/json".toMediaTypeOrNull()
//    val stream = ByteArrayOutputStream()
//    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
//    val byteArray = stream.toByteArray()
//    val file = File.createTempFile("image", ".jpeg")
//    file.writeBytes(byteArray)
//    val requestBody: RequestBody =
//        MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart(
//            "image",
//            file.name,
//            RequestBody.create("application/octet-stream".toMediaTypeOrNull(), file)
//        ).build()
//
//    val request = okhttp3.Request.Builder()
//        .url("https://backend.api.symphony.co.ke/uploadVolley")
//        .method("POST", requestBody)
//        .addHeader("Content-Type", "application/json")
//        .build()
//
//    Thread {
//        try {
//            val response = client.newCall(request).execute()
//            println(response.body?.string())
//            runOnUiThread {
//                Toast.makeText(this, response.body.toString(), Toast.LENGTH_SHORT).show()
//            }
//            Log.d("okhttp", response.body.toString())
//        } catch (e: Exception) {
//            runOnUiThread {
//                Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
//            }
//            Log.e("okhttp", "Error: ${e.message}")
//        }
//    }.start()
//}

//private fun uploadVolley() {
//    imageData ?: return
////        val postURL = "https://backend.api.symphony.co.ke/uploadI"
//    val postURL = "https://reqbin.com/"
//
//    val request =
//        object : VolleyFileUploadRequest(Request.Method.POST, postURL, Response.Listener {
//            Log.d("newvolley", "$it")
//            println("response is: $it")
//        }, Response.ErrorListener {
//            Log.d("newvolley", "$it")
//            println("error is: $it")
//        }) {
//            override fun getByteData(): MutableMap<String, FileDataPart> {
//                var params = HashMap<String, FileDataPart>()
//                params["imageFile"] = FileDataPart("image", imageData!!, "jpeg")
//                return params
//            }
//        }
//    Volley.newRequestQueue(this).add(request)
//}


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
        val request = object : StringRequest(Method.POST, url, { response ->
            // Handle successful response from server

            updateT.isClickable = true
            updateT.visibility = View.VISIBLE
            progressB.visibility = View.GONE
            Toast.makeText(
                this@TicketActivity, "Successfully updated Ticket", Toast.LENGTH_SHORT
            ).show()

            binding.edtTDActionsTakenValue.setText("")
            binding.edtTDFindingsValue.setText("")
            binding.edtTDSerialNoValue.setText("")
            binding.edtTDJobCardNoValue.setText("")
            binding.edtTDRecommendationsValue.setText("")

        }, { error ->
            // Handle error response from server

            updateT.isClickable = true
            updateT.visibility = View.VISIBLE
            progressB.visibility = View.GONE
            Toast.makeText(
                this@TicketActivity, "Failed to update Ticket", Toast.LENGTH_SHORT
            ).show()

            Log.e("Volley", "Error: $error")
        }) {

            override fun parseNetworkResponse(response: NetworkResponse): Response<String> {
                val status = response.statusCode
                val responseBody = String(response.data)
                Log.d("StatusCode", "Response body: $responseBody")
                Log.d("StatusCode", "Status code: $status")

                if (status == 200) {
                    return Response.success(
                        responseBody, HttpHeaderParser.parseCacheHeaders(response)
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









