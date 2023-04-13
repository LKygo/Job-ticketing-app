package com.symphony.symphony

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.android.volley.NetworkError
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.symphony.symphony.databinding.ActivityClaimsBinding
import org.json.JSONObject

class ClaimsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClaimsBinding
    private lateinit var ticketNo: String
    private lateinit var progressB: ProgressBar
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
    private lateinit var others: String
    private var claimAmount = 0
    private var kmClaim = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityClaimsBinding.inflate(layoutInflater)
        val view = binding.root
        super.onCreate(savedInstanceState)
        setContentView(view)

        val toolbar = findViewById<Toolbar>(R.id.my_toolbar)
        setSupportActionBar(toolbar)

        progressB = binding.pgbClaims
        btnClaim = binding.btnClaim
        val bundle: Bundle? = intent.extras
        ticketNo = bundle?.getString("ticketNo").toString()

        binding.txvCTicketNOValue.text = ticketNo




        binding.ClaimsRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == binding.RadioPsv.id) {
                // show views for PSV option
                binding.edtKMCovered.isEnabled = false
                binding.txvKMCovered.alpha = 0.5f

                binding.edtFarePaid.isEnabled = true
                binding.txvPsvFare.alpha = 1f

            } else if (checkedId == binding.RadioPrivate.id) {
                // show views for Private option
                binding.edtFarePaid.isEnabled = false
                binding.txvPsvFare.alpha = 0.5f

                binding.edtKMCovered.isEnabled = true
                binding.txvKMCovered.alpha = 1f

            } else {
                // show default views
                binding.edtKMCovered.isEnabled = false
                binding.txvKMCovered.alpha = 0.5f

                binding.edtFarePaid.isEnabled = true
                binding.txvPsvFare.alpha = 1f
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
            lunch = binding.edtlunch.text.toString()
            others = binding.edtOthers.text.toString()


            updateClaims(ticketNo, claimNo, psvFare, km, accommodation, petties, dinner, lunch, laundry,others)
        }

        binding.btnClearClaims.setOnClickListener {
            binding.edtFarePaid.setText("0.00")
            binding.edtKMCovered.setText("")
            binding.edtAccomodation.setText("")
            binding.edtPetties.setText("")
            binding.edtDinner.setText("")
            binding.edtlunch.setText("")
            binding.edtLaundry.setText("")
            binding.edtOthers.setText("")
            binding.ClaimsRadioGroup.clearCheck()

        }

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
//        kmClaim: String,
        laundry: String,
        others: String,
//        claimAmount: String,
    ) {

        progressB.visibility = View.VISIBLE
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
//        jsonObject.put("kmClaim", kmClaim)
        jsonObject.put("laundry", laundry)
        jsonObject.put("others", others)
//        jsonObject.put("claimAmount", claimAmount)
        Log.d("Claim", "JSON object: $jsonObject")


// Request to server's URL
        val request = object : StringRequest(Method.POST, url, { response ->
            // Handle successful response from server

            btnClaim.isClickable = true
            btnClaim.visibility = View.VISIBLE
            progressB.visibility = View.GONE
            Toast.makeText(
                this@ClaimsActivity, "Successfully claimed", Toast.LENGTH_SHORT
            ).show()

            binding.edtFarePaid.setText("0.00")
            binding.edtKMCovered.setText("")
            binding.edtAccomodation.setText("")
            binding.edtPetties.setText("")
            binding.edtDinner.setText("")
            binding.edtlunch.setText("")
            binding.edtLaundry.setText("")
            binding.edtOthers.setText("")
            binding.ClaimsRadioGroup.clearCheck()

        }, { error ->
            // Handle error response from server

            btnClaim.isClickable = true
            btnClaim.visibility = View.VISIBLE
            progressB.visibility = View.GONE
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

    private fun kmClaim(km: Int){
        if (km > 20){
            var restKm = km - 20
           val extraKm =  restKm * 20
            val startKm = (km - restKm) * 40

        }
    }
    private fun totalClaim(
        psvFare: Int,
        accommodation: Int,
        petties: Int,
        dinner: Int,
        lunch: Int,
        k: Int,
        kmClaim: Int,
        laundry: Int,
        others: Int,
    ): Int {
        val psv = binding.edtFarePaid.text.toString().toInt()
        val accom = binding.edtAccomodation.text.toString().toInt()
        val pet = binding.edtPetties.text.toString().toInt()
        val dinn = binding.edtDinner.text.toString().toInt()
        val lun = binding.edtlunch.text.toString().toInt()
        val k = binding.edtKMCovered.text.toString().toInt()
//    val kmcl = binding.edtFarePaid.text.toString().toInt()
        val laun = binding.edtLaundry.text.toString().toInt()
        val oth = binding.edtOthers.text.toString().toInt()

        claimAmount = psv + accom + pet + lun + dinn + k + laun + oth
        return claimAmount

    }
}