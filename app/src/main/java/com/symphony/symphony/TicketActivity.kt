package com.symphony.symphony

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.android.volley.AuthFailureError
import com.android.volley.NetworkError
import com.android.volley.NetworkResponse
import com.android.volley.NoConnectionError
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.ServerError
import com.android.volley.TimeoutError
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.symphony.symphony.databinding.ActivityTicketBinding
import org.json.JSONObject
import java.io.File
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
    private lateinit var jobcardno: String
    private lateinit var serialNo: String
    private lateinit var findings: String
    private lateinit var action_taken: String
    private lateinit var recommendations: String
    private lateinit var attachment: String
    private lateinit var btnClaim: Button
    private lateinit var model : String
    private lateinit var claimsProgress: ProgressBar
    private var filePath: Uri? = null
    private val reference: StorageReference = FirebaseStorage.getInstance().reference
    private val GALLERY_PERMISSION_CODE = 22
    private val REQUEST_IMAGE_CAPTURE = 39


    companion object {
        private const val IMAGE_PICK_CODE = 999
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTicketBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val toolbar = findViewById<Toolbar>(R.id.my_toolbar)
        setSupportActionBar(toolbar)

        // Enable the back button on the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

// Define the action when the back button is pressed
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val bundle: Bundle? = intent.extras

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        servicedate = dateFormat.format(Date())
        progressB = binding.pgbTDProgress
        progressB.visibility = View.GONE
        updateT = binding.btnTDUpdate
        btnClaim = binding.btnClaim
        claimsProgress = binding.claimsProgress!!
        claimsProgress.visibility = View.GONE

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


        start_time = "${servicedate} $sTime"
        created_at = date


        val takePic = binding.btnAttach
        takePic.setOnClickListener {
            showImageSourceDialog()
        }

        updateT.setOnClickListener {
            val currentTime = Date()
            val formatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val endt = formatter.format(currentTime)
            end_time = "$servicedate $endt"
            model = binding.edtModelValue!!.text.toString()
            jobcardno = binding.edtTDJobCardNoValue.text.toString()
            serialNo = binding.edtTDSerialNoValue.text.toString()
            findings = binding.edtTDFindingsValue.text.toString()
            action_taken = binding.edtTDActionsTakenValue.text.toString()
            recommendations = binding.edtTDRecommendationsValue.text.toString()


            if (jobcardno.isEmpty() || serialNo.isEmpty() || findings.isEmpty() ||model.isEmpty() ||  action_taken.isEmpty() || recommendations.isEmpty()) {

                Toast.makeText(
                    this,
                    "Please fill all the fields with a red star",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (filePath != null) {
                    uploadToFirebase(filePath!!)
                } else {

                    Toast.makeText(
                        this,
                        "Please select Job Card image from gallery",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }

        binding.btnClaim.setOnClickListener {

            btnClaim.isClickable = false
            btnClaim.text = ""
            claimsProgress.visibility = View.VISIBLE
            claimsProgress.elevation = 10f

            Snackbar.make(view, "Checking whether ticket has been updated", Snackbar.LENGTH_SHORT).show()

           Handler().postDelayed({
               checkServiceEntry()

           },2000L)


        }

        binding.btnTDClear.setOnClickListener {
            binding.edtTDActionsTakenValue.setText("")
            binding.edtTDFindingsValue.setText("")
            binding.edtTDSerialNoValue.setText("")
            binding.edtTDJobCardNoValue.setText("")
            binding.edtTDRecommendationsValue.setText("")
            binding.edtModelValue?.setText("")
            filePath = null
            binding.imgJobCardValidation.setBackgroundResource(R.drawable.remove)
        }


    }

//

    private fun uploadToFirebase(uri: Uri) {


        val fileRef = reference.child("TicketNo: $ticketNo /$servicedate/$end_time")
        fileRef.putFile(uri).addOnSuccessListener { taskSnapshot ->
            taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUrl ->
                // Use downloadUrl as needed
                attachment = downloadUrl.toString()

                Toast.makeText(this, "Job card picture Uploaded successfully", Toast.LENGTH_SHORT)
                    .show()

//                Handler().postDelayed({
                try {
                    sendTicketDetails(
                        ticketNo,
                        jobcardno,
                        servicedate,
                        start_time,
                        end_time,
                        model,
                        serialNo,
                        city,
                        findings,
                        action_taken,
                        recommendations,
                        attachment,
                        updatedby,
                        created_at
                    )
                    Log.d("attachment", attachment)
                } catch (e: java.lang.Exception) {
                    Log.d("FunPost", e.toString())
                }
//                }, 1000L)


            }.addOnFailureListener { exception ->
                // Handle any errors
                Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
//                Log.e("Attachment", "Error getting download URL: ${exception.message}", exception)
            }


        }.addOnProgressListener {
            progressB.visibility = View.VISIBLE
            progressB.elevation = 10f
            updateT.isClickable = false
            updateT.text = ""
        }.addOnFailureListener { exception ->
            Log.e("Firebase", "Upload failed", exception)

            progressB.visibility = View.GONE

            Toast.makeText(this, "JobCard upload failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getFileExtension(mUri: Uri): String? {
        val contentResolver: ContentResolver = getContentResolver()
        val mime: MimeTypeMap = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(contentResolver.getType(mUri))
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                REQUEST_IMAGE_CAPTURE
            )
        } else {
            openCamera()
        }
    }


    private fun requestGalleryPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                GALLERY_PERMISSION_CODE
            )
        } else {
            openGallery()
        }
    }

    private fun showImageSourceDialog() {
        val dialogView = layoutInflater.inflate(R.layout.attach_image, null)

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val btnCamera = dialogView.findViewById<ImageView>(R.id.btn_camera)
        val btnGallery = dialogView.findViewById<ImageView>(R.id.btn_gallery)
        binding.btnAttach.setBackgroundResource(R.drawable.attachment_filled)

        btnCamera.setOnClickListener {
            // Handle camera button click
            requestCameraPermission()
            alertDialog.dismiss()
            binding.btnAttach.setBackgroundResource(R.drawable.attachment)

        }

        btnGallery.setOnClickListener {
            // Handle gallery button click
            requestGalleryPermission()
            alertDialog.dismiss()
            binding.btnAttach.setBackgroundResource(R.drawable.attachment)

        }

        alertDialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            GALLERY_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, open gallery
                    openGallery()
                } else {
                    // Permission denied, show toast and request permission again when required
                    Toast.makeText(
                        this,
                        "Gallery permission required to take pictures",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            REQUEST_IMAGE_CAPTURE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, open camera
                    openCamera()
                } else {
                    // Permission denied, show toast and request permission again when required
                    Toast.makeText(
                        this,
                        "Camera permission required to take pictures",
                        Toast.LENGTH_SHORT
                    ).show()

                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.CAMERA),
                        REQUEST_IMAGE_CAPTURE
                    )

                }
            }
        }
    }

    private fun openCamera() {

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                REQUEST_IMAGE_CAPTURE
            )
        }

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(this.packageManager)?.also {
                val storageDirs = ContextCompat.getExternalFilesDirs(
                    this, Environment.DIRECTORY_PICTURES
                )
                val storageDir = storageDirs[0]

                val photoFile: File? = File(
                    storageDir, "JPEG_${
                        SimpleDateFormat(
                            "yyyyMMdd_HHmmss", Locale.getDefault()
                        ).format(Date())
                    }.jpg"
                ).apply {
                    createNewFile()
                }

                photoFile?.let {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "${BuildConfig.APPLICATION_ID}.file-provider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                    // Save the file path
                    filePath = photoURI
                    if (filePath != null) {
                        binding.imgJobCardValidation.setBackgroundResource(R.drawable.check)
                    }
                }
            }
        }
    }

    private fun openGallery() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, GALLERY_PERMISSION_CODE)
        } else {
            // Permission not granted, handle the case here
            Toast.makeText(
                this,
                "Gallery permission required to take pictures",
                Toast.LENGTH_SHORT
            ).show()

            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                GALLERY_PERMISSION_CODE
            )
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == GALLERY_PERMISSION_CODE) {
            filePath = data?.data!!
            binding.imgJobCardValidation.setBackgroundResource(R.drawable.check)


        }
    }


    private fun sendTicketDetails(
        ticket_no: String,
        jobcard_no: String,
        service_date: String,
        start_time: String,
        end_time: String,
        model: String,
        serial_no: String,
        city: String,
        findings: String,
        action_taken: String,
        recommendations: String,
        attachment: String,
        updated_by: String,
        created_at: String,
    ) {

        progressB.visibility = View.VISIBLE
        progressB.elevation = 10f
        val url = "https://backend.api.symphony.co.ke/upload"

        // Create a JSON object to hold your data
        val jsonObject = JSONObject()
        jsonObject.put("ticket_no", ticket_no)
        jsonObject.put("jobcard_no", jobcard_no)
        jsonObject.put("service_date", service_date)
        jsonObject.put("start_time", start_time)
        jsonObject.put("end_time", end_time)
        jsonObject.put("model", model)
        jsonObject.put("serial_no", serial_no)
        jsonObject.put("city", city)
        jsonObject.put("findings", findings)
        jsonObject.put("action_taken", action_taken)
        jsonObject.put("recommendations", recommendations)
        jsonObject.put("attachment", attachment)
        jsonObject.put("updated_by", updated_by)
        jsonObject.put("created_at", created_at)
        Log.d("Volley", "JSON object: $jsonObject")


// Request to server's URL
        val request = object : StringRequest(Method.POST, url, { response ->
            // Handle successful response from server

            updateT.isClickable = true
            updateT.text = "Update"
            progressB.visibility = View.GONE
            Toast.makeText(
                this@TicketActivity, "Successfully updated Ticket", Toast.LENGTH_SHORT
            ).show()

            binding.edtTDActionsTakenValue.setText("")
            binding.edtTDFindingsValue.setText("")
            binding.edtTDSerialNoValue.setText("")
            binding.edtTDJobCardNoValue.setText("")
            binding.edtTDRecommendationsValue.setText("")
            binding.edtModelValue?.setText("")
            filePath = null
            binding.imgJobCardValidation.setBackgroundResource(R.drawable.remove)


        }, { error ->
            // Handle error response from server

            updateT.isClickable = true
            updateT.text = "Update"
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

    fun checkServiceEntry() {
        val queue = Volley.newRequestQueue(this)
        val url = "https://backend.api.symphony.co.ke/checkClaims"


        val requestBody = JSONObject()
        requestBody.put("ticketNo", ticketNo)


        val request = JsonObjectRequest(
            Request.Method.POST, url, requestBody,
            { response ->
                // handle successful response
                btnClaim.isClickable = true
                btnClaim.text = "Claim"
                claimsProgress.visibility = View.GONE


                val intent = Intent(this, ClaimsActivity::class.java)
                intent.putExtra("ticketNo", ticketNo)
                startActivity(intent)

                Toast.makeText(this@TicketActivity, "Redirecting you...", Toast.LENGTH_SHORT).show()
                Log.d("Claims", response.toString())
            },
            { error ->
                // handle error
                btnClaim.isClickable = true
                btnClaim.text = "Claim"
                claimsProgress.visibility = View.GONE

                val errorMessage = when (error) {
                    is NoConnectionError -> "No internet connection"
                    is TimeoutError -> "Connection timed out"
                    is ServerError -> "Please update ticket to claim"
                    is AuthFailureError -> "Authentication failure"
                    is NetworkError -> "Network error"
                    is ParseError -> "Parse error"
                    else -> "Unknown error"
                }

                val statusCode = error.networkResponse?.statusCode ?: -1
                val errorString = String(error.networkResponse?.data ?: ByteArray(0))

                val message = errorMessage

                Toast.makeText(this@TicketActivity, message, Toast.LENGTH_LONG).show()
                Log.e("Claims", message)
            }
        )
        queue.add(request)
    }
}









