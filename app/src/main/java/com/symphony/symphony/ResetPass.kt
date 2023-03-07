package com.symphony.symphony

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.symphony.symphony.databinding.ActivitySignUpBinding

class ResetPass : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var txvEmail: EditText
    private lateinit var btnReq: Button
    private lateinit var progressBar : ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        progressBar = binding.progressBarR
        progressBar.visibility = View.GONE

        btnReq = binding.btnSignIn
        btnReq.setOnClickListener {
            var email = binding.edtEmail.text.toString()

            if (email.isEmpty()){
                Toast.makeText(this, "Please enter both email", Toast.LENGTH_SHORT).show()

            }else
            resetPass()

        }
    }

    fun resetPass() {
        val email = binding.edtEmail.text.toString()

        btnReq.isClickable = false
        btnReq.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)

// Set up the login endpoint URL and request parameters.
        val url = "https://backend.api.symphony.co.ke/resetpass"
        val params = HashMap<String, String>()
        params["email"] = email

// Create a new POST request with the login endpoint URL and parameters.
        val stringRequest =
            object : StringRequest(Request.Method.POST, url, Response.Listener<String> { response ->

                progressBar.visibility = View.GONE // hide the progress bar
                btnReq.visibility = View.VISIBLE
                btnReq.isClickable = true // enable the sign in button
                // Handle the API response on success.
                Toast.makeText(applicationContext, response, Toast.LENGTH_LONG).show()

            }, Response.ErrorListener { error ->

                progressBar.visibility = View.GONE // hide the progress bar
                btnReq.visibility = View.VISIBLE
                btnReq.isClickable = true // enable the sign in button
                // Handle the API response on error.
                val errorMessage = error.networkResponse?.statusCode?.let {
                    when (it) {
                        404 -> "Email not found"
                        else -> "Internal Server Error"
                    }
                }
                Log.d("Register", error.toString())
                Toast.makeText(applicationContext, errorMessage.toString(), Toast.LENGTH_LONG).show()
            }) {

                // Set the POST request parameters.
                override fun getParams(): Map<String, String> {
                    return params
                }
            }

// Add the request to the RequestQueue.
        queue.add(stringRequest)

    }

}

