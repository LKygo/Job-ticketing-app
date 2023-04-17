package com.symphony.symphony

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
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
import java.nio.charset.Charset

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
    private lateinit var mileageClaim: TextView
    private var claimAmount = 0
    private var kmClaim = 0


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

            progressB = binding.pgbClaims
            progressB.visibility = View.GONE
            btnClaim = binding.btnClaim
            val bundle: Bundle? = intent.extras
            ticketNo = bundle?.getString("ticketNo").toString()

            binding.txvCTicketNOValue.text = ticketNo


            binding.edtKMCovered.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    calculateMileage()
                    calculateTotalClaimValue()
                }

                override fun afterTextChanged(p0: Editable?) {}
            })
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



            binding.ClaimsRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                if (checkedId == binding.RadioPsv.id) {
                    // show views for PSV option
                    binding.edtKMCovered.isEnabled = false
                    binding.txvKMCovered.alpha = 0.5f
                    binding.edtKMCovered.setText("") // set the value to 0
                    kmClaim = 0

                    binding.edtFarePaid.isEnabled = true
                    binding.txvPsvFare.alpha = 1f
                    binding.edtFarePaid.requestFocus()

                    calculateMileage()


                } else if (checkedId == binding.RadioPrivate.id) {
                    // show views for Private option
                    binding.edtFarePaid.isEnabled = false
                    binding.txvPsvFare.alpha = 0.5f
                    binding.edtFarePaid.setText("") // set the value to 0


                    binding.edtKMCovered.isEnabled = true
                    binding.txvKMCovered.alpha = 1f
                    binding.edtKMCovered.requestFocus()
                    calculateMileage()


                } else {
                    // show default views
                    binding.edtKMCovered.isEnabled = false
                    binding.txvKMCovered.alpha = 0.5f
                    binding.edtKMCovered.setText("") // set the value to 0


                    binding.edtFarePaid.isEnabled = true
                    binding.txvPsvFare.alpha = 1f

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

                )
                Log.d(
                    "Claims",
                    "$ticketNo, $claimNo, $psvFare, $km, ${kmClaim.toString()}, $accommodation, $petties, $dinner, $lunch, $laundry, $others, ${claimAmount.toString()}"
                )
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

            progressB.visibility = View.VISIBLE
            btnClaim.isClickable = false
            btnClaim.visibility = View.GONE
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
                btnClaim.visibility = View.VISIBLE
                progressB.visibility = View.GONE
                Toast.makeText(
                    this@ClaimsActivity, "Successfully claimed", Toast.LENGTH_SHORT
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

            }, { error ->
                // Handle error response from server

                // If the error has a network response, use its status code and error message
                val errorStatus = error.networkResponse.statusCode
                val errorMessage = String(error.networkResponse.data, Charset.defaultCharset())
                Log.e("Volley", "Error $errorStatus: $errorMessage")


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

        private fun calculateMileage() {
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

    }