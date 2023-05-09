package com.symphony.symphony

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.swiperefreshlayout.widget.CircularProgressDrawable.ProgressDrawableSize
import com.android.volley.NetworkError
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.symphony.symphony.databinding.ActivityClaimsBinding
import org.json.JSONObject
import java.nio.charset.Charset

class ClaimsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClaimsBinding
    private lateinit var ticketNo: String
    private lateinit var btnClaim: Button
    private lateinit var ticket_no: String
    private lateinit var claimNo: String
    private lateinit var psvFare: String
    private lateinit var accommodation: String
    private lateinit var petties: String
    private lateinit var dinner: String
    private lateinit var lunch: String
    private lateinit var km: String
    private lateinit var laundry: String
    private lateinit var selectZone: TextView
    private lateinit var zoneChip: ChipGroup
    private lateinit var others: String
    private lateinit var mileageClaim: TextView
    private lateinit var progressDialog : ProgressDialog
    private var claimAmount = 0
    private var kmClaim = 0

    // Declare a variable to hold the selected chip ID
    private var selectedChipId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityClaimsBinding.inflate(layoutInflater)
        val view = binding.root
        super.onCreate(savedInstanceState)
        setContentView(view)

        val toolbar = findViewById<Toolbar>(R.id.my_toolbar)
        setSupportActionBar(toolbar)

        // Enable the back button on the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

// Define the action when the back button is pressed
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }


        btnClaim = binding.btnClaim
        val bundle: Bundle? = intent.extras
        ticketNo = bundle?.getString("ticketNo").toString()

        binding.txvCTicketNOValue.text = ticketNo



        binding.edtFarePaid.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                calculateTotalClaimValue()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.edtAccomodation.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                calculateTotalClaimValue()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.edtPetties.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                calculateTotalClaimValue()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.edtDinner.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                calculateTotalClaimValue()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.edtLunch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                calculateTotalClaimValue()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.edtLaundry.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                calculateTotalClaimValue()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.edtOthers.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                calculateTotalClaimValue()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        mileageClaim = binding.txvKmClaimValue

        selectZone = binding.txvSelectZone
        zoneChip = binding.chipGroup

        binding.ClaimsRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == binding.RadioPsv.id) {
                // show views for PSV option

                viewGone()
                binding.edtKMCovered.setText("") // set the value to 0
                kmClaim = 0
                binding.edtFarePaid.requestFocus()

                calculateMileage()

            } else if (checkedId == binding.RadioPrivate.id) {
                // show views for Private option
                mileageView()
                Toast.makeText(this, "Select zone to input distance", Toast.LENGTH_SHORT).show()

                binding.edtFarePaid.setText("") // set the value to 0
                calculateMileage()

                binding.edtKMCovered.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        calculateMileage()
                        calculateTotalClaimValue()
                    }

                    override fun afterTextChanged(p0: Editable?) {
                        calculateMileage()
                        calculateTotalClaimValue()
                    }
                })


                // Set the checked change listener outside the calculateMileage() function
                binding.chipGroup.setOnCheckedChangeListener { group, checkedId ->
                    if (checkedId == -1) {

                        Toast.makeText(this, "Select zone to input distance", Toast.LENGTH_LONG).show()
                        binding.edtKMCovered.setText("") // set the value to 0
                        kmClaim = 0
                        binding.edtKMCovered.isEnabled = false

                    } else {
                        // Update the selected chip ID
                        selectedChipId = checkedId
                        binding.edtKMCovered.requestFocus()

                        // Calculate the mileage based on the new selection
                        calculateMileage()
                    }
                }

            } else {
                // show default views

                viewGone()
                binding.edtKMCovered.setText("") // set the value to 0

                calculateMileage()

            }
        }
        btnClaim.setOnClickListener {
            claimNo = binding.txvClaimNOValue.text.toString()
            psvFare = binding.edtFarePaid.text.toString()
            km = binding.edtKMCovered.text.toString()
            accommodation = binding.edtAccomodation.text.toString()
            petties = binding.edtPetties.text.toString()
            dinner = binding.edtDinner.text.toString()
            laundry = binding.edtLaundry.text.toString()
            lunch = binding.edtLunch.text.toString()
            others = binding.edtOthers.text.toString()


            if (km.isEmpty() && psvFare.isEmpty()){
              showUpdateDialog()
            }else{

                progressDialog = showProgressDialog(this, "Filing claim")

                updateClaims(
                    ticketNo,
                    claimNo,
                    psvFare,
                    accommodation,
                    petties,
                    dinner,
                    lunch,
                    km,
                    kmClaim,
                    laundry,
                    others,
                    claimAmount

                )              }
        }

        binding.btnClearClaims.setOnClickListener {
            binding.edtFarePaid.setText("")
            binding.edtKMCovered.setText("")
            binding.edtAccomodation.setText("")
            binding.edtPetties.setText("")
            binding.edtDinner.setText("")
            binding.edtLunch.setText("")
            binding.edtLaundry.setText("")
            binding.edtOthers.setText("")
            binding.txvTotalClaimValue.text = ""
            binding.ClaimsRadioGroup.clearCheck()

        }

        viewGone()

    }


    private fun updateClaims(
        ticket_no: String,
        claimNo: String,
        psvFare: String,
        accommodation: String,
        petties: String,
        dinner: String,
        lunch: String,
        km: String,
        kmClaim: Int,
        laundry: String,
        others: String,
        claimAmount: Int,
    ) {

        btnClaim.isClickable = false
        val url = "https://backend.api.symphony.co.ke/claims"

        // Create a JSON object to hold your data
        val jsonObject = JSONObject()
        jsonObject.put("ticket_no", ticket_no)
        jsonObject.put("claimNo", claimNo)
        jsonObject.put("psvFare", psvFare)
        jsonObject.put("accommodation", accommodation)
        jsonObject.put("petties", petties)
        jsonObject.put("dinner", dinner)
        jsonObject.put("lunch", lunch)
        jsonObject.put("km", km)
        jsonObject.put("kmClaim", kmClaim)
        jsonObject.put("laundry", laundry)
        jsonObject.put("others", others)
        jsonObject.put("claimAmount", claimAmount)
        Log.d("Claim", "JSON object: $jsonObject")


// Request to server's URL
        val request = object : StringRequest(Method.POST, url, { response ->
            // Handle successful response from server

            btnClaim.isClickable = true
            Toast.makeText(
                this@ClaimsActivity, "Successfully  filed claim", Toast.LENGTH_SHORT
            ).show()

            binding.edtFarePaid.setText("")
            binding.edtKMCovered.setText("")
            binding.edtAccomodation.setText("")
            binding.edtPetties.setText("")
            binding.edtDinner.setText("")
            binding.edtLunch.setText("")
            binding.edtLaundry.setText("")
            binding.edtOthers.setText("")
            binding.txvTotalClaimValue.text = ""
            mileageClaim.text = "0"
            binding.ClaimsRadioGroup.clearCheck()


            Handler().postDelayed({
                progressDialog.dismiss()
            },2000L)
        }, { error ->
            // Handle error response from server

            // If the error has a network response, use its status code and error message
            val errorStatus = error.networkResponse.statusCode
            val errorMessage = String(error.networkResponse.data, Charset.defaultCharset())
            Log.e("Volley", "Error $errorStatus: $errorMessage")


            btnClaim.isClickable = true
            Handler().postDelayed({
                progressDialog.dismiss()
            },2000L)
            Toast.makeText(
                this@ClaimsActivity, "Failed to make Claim", Toast.LENGTH_SHORT
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
        Volley.newRequestQueue(this@ClaimsActivity).add(request)

    }

    // Calculating mileage on condition of chip selected
    private fun calculateMileage() {
        when (selectedChipId) {
            R.id.chipOther -> {

                binding.edtKMCovered.isEnabled = true
                calculateMileageOutside()
            }

            R.id.chipNairobi -> {

                binding.edtKMCovered.isEnabled = true
                val kmCoveredStr = binding.edtKMCovered.text.toString()
                val kmCovered = kmCoveredStr.toIntOrNull() ?: 0
                kmClaim = kmCovered * 40
                mileageClaim.text = kmClaim.toString()
            }

            else -> {}
        }
    }


//    Mileage outside of Nairobi
    private fun calculateMileageOutside() {
        val kmCoveredStr = binding.edtKMCovered.text.toString()
        if (kmCoveredStr.isEmpty()) {
            mileageClaim.text = "0"
            return
        }

        val kmCovered = kmCoveredStr.toIntOrNull() ?: 0

        if (kmCovered > 40) {
            val extraKm = (kmCovered - 40) * 20
            val startKm = 40 * 40
            kmClaim = startKm + extraKm
            mileageClaim.text = kmClaim.toString()
        } else {
            kmClaim = kmCovered * 40
            mileageClaim.text = kmClaim.toString()
        }
    }


    private fun calculateTotalClaimValue() {
        val farePaid = binding.edtFarePaid.text.toString().toIntOrNull() ?: 0
        val accommodation = binding.edtAccomodation.text.toString().toIntOrNull() ?: 0
        val petties = binding.edtPetties.text.toString().toIntOrNull() ?: 0
        val dinner = binding.edtDinner.text.toString().toIntOrNull() ?: 0
        val lunch = binding.edtLunch.text.toString().toIntOrNull() ?: 0
        val laundry = binding.edtLaundry.text.toString().toIntOrNull() ?: 0
        val others = binding.edtOthers.text.toString().toIntOrNull() ?: 0

        claimAmount =
            farePaid + accommodation + petties + dinner + lunch + laundry + others + kmClaim

        binding.txvTotalClaimValue.text = String.format(claimAmount.toString())
    }

    private fun viewGone() {
        selectZone.visibility = View.GONE
        zoneChip.visibility = View.GONE

        binding.txvKmClaim.visibility = View.GONE
        binding.txvKmClaimValue.visibility = View.GONE
        binding.txvKMCovered.visibility = View.GONE
        binding.edtKMCovered.visibility = View.GONE


        binding.txvPsvFare.visibility = View.VISIBLE
        binding.edtFarePaid.visibility = View.VISIBLE

//        Updating constraints dynamically
        binding.txvAccomodation.updateLayoutParams<ConstraintLayout.LayoutParams> {
            topToBottom = R.id.edtFarePaid
        }
        binding.txvPetties.updateLayoutParams<ConstraintLayout.LayoutParams> {
            topToBottom = R.id.edtFarePaid
        }

    }

//    Display mileage views
    private fun mileageView() {
        selectZone.visibility = View.VISIBLE
        zoneChip.visibility = View.VISIBLE

        binding.txvKmClaim.visibility = View.VISIBLE
        binding.txvKmClaimValue.visibility = View.VISIBLE
        binding.txvKMCovered.visibility = View.VISIBLE
        binding.edtKMCovered.visibility = View.VISIBLE
        binding.edtKMCovered.isEnabled = false

        binding.txvPsvFare.visibility = View.GONE
        binding.edtFarePaid.visibility = View.GONE


//        Updating constraints dynamically
        binding.txvAccomodation.updateLayoutParams<ConstraintLayout.LayoutParams> {
            topToBottom = R.id.edtKMCovered
        }
        binding.txvPetties.updateLayoutParams<ConstraintLayout.LayoutParams> {
            topToBottom = R.id.txvTotalClaimValue
        }
    }

    private fun showUpdateDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmation")
        builder.setMessage("Both PSV fare and km covered are blank. Are you sure you want to proceed?")
        builder.setPositiveButton("update") { dialog, which ->
            progressDialog = showProgressDialog(this, "Filing claim")


            // User clicked Yes button
            updateClaims(
                ticketNo,
                claimNo,
                psvFare,
                accommodation,
                petties,
                dinner,
                lunch,
                km,
                kmClaim,
                laundry,
                others,
                claimAmount

            )      }
        builder.setNegativeButton("Cancel") { dialog, which ->
            // User clicked No button
              dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    fun showProgressDialog(context: Context, message: String): ProgressDialog {
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage(message)
        progressDialog.isIndeterminate = true
        progressDialog.setCancelable(false)
        progressDialog.show()
        return progressDialog
    }


}