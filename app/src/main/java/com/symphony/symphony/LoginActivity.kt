package com.symphony.symphony

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.symphony.symphony.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var progressBar: ProgressBar
    private lateinit var signIn: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root


        progressBar = binding.progressBar


        val signUp = binding.txvSignUp

        signUp.paint.isUnderlineText = true
        signUp.setOnClickListener {
            val intent = Intent(applicationContext, SignUp::class.java)
            startActivity(intent)
        }

        setContentView(view)
        signIn = binding.btnSignIn

        signIn.setOnClickListener {
//            progressBar.visibility = View.VISIBLE
//            signIn.setBackground(getDrawable(R.drawable.button_grey))
            loginNew()
        }

    }

    fun loginNew() {
        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)

// Set up the login endpoint URL and request parameters.
        val url = "https://backend.api.symphony.co.ke/login"
        val params = HashMap<String, String>()
        params["email"] = email
        params["password"] = password

// Create a new POST request with the login endpoint URL and parameters.
        val stringRequest = object : StringRequest(Request.Method.POST, url,
            Response.Listener<String> { response ->
                // Handle the API response on success.
                Toast.makeText(applicationContext, response, Toast.LENGTH_LONG).show()
                val intent = Intent(applicationContext, TechnicianDashboard::class.java)
                startActivity(intent)
//                finish()
            },
            Response.ErrorListener { error ->
                // Handle the API response on error.
                val errorMessage = error.networkResponse?.statusCode?.let {
                    when (it) {
                        401 -> "Incorrect email or password"
                        else -> "Internal Server Error"
                    }
                } ?: "Network Error"
                Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_LONG).show()
            }) {

            // Set the POST request parameters.
            override fun getParams(): Map<String, String> {
                return params
            }
        }

// Add the request to the RequestQueue.
        queue.add(stringRequest)

    }

//    fun login() {
//        val url = "https://backend.api.symphony.co.ke/login"
//        val email = binding.edtEmail
//        val password = binding.edtPassword
//        val emailText = email.text.toString().trim()
//        val passwordText = password.text.toString().trim()
//        val maxRetries = 3
//        val initialTimeoutMs = 5000
//        val backoffMultiplier = 2f
//
//        val jsonObject = JSONObject()
//        jsonObject.put("email", emailText)
//        jsonObject.put("password", passwordText)
//
//        if (emailText.isNotEmpty() && passwordText.isNotEmpty()) {
//
//            val volley = Volley.newRequestQueue(this)
//            val stringRequest = object :
//                StringRequest(Request.Method.POST, url, Response.Listener { response: String ->
////                    progressBar.visibility = View.GONE
////                    signIn.setBackground( getDrawable(R.drawable.button))
//
//                    if (response == "Login successful") {
//                        Toast.makeText(this, "Login successful", Toast.LENGTH_LONG).show()
//                        Log.d("loginsu", response.toString())
//
//                        val intent = Intent(applicationContext, TechnicianDashboard::class.java)
//                        startActivity(intent)
//                        finish()
//                    } else if (response == "Invalid email or password") {
//                        Toast.makeText(this, "Invalid log in Id/Password", Toast.LENGTH_SHORT)
//                            .show()
//                        Log.d("logininvalid", response.toString())
//
//                    }else if(response == "Internal"){
//                        Toast.makeText(this, "Login successful", Toast.LENGTH_LONG).show()
//Log.d("loginInternal", response.toString())
//                    }
//
//                }, Response.ErrorListener { error ->
//                    Toast.makeText(applicationContext, error.toString(), Toast.LENGTH_SHORT).show()
//                    Log.d("loginerr", error.toString().trim())
//
//                }) {
//                override fun getBody(): ByteArray {
//                    // Convert the JSON object to a byte array
//                    return jsonObject.toString().toByteArray(Charsets.UTF_8)
//                }
//                override fun getBodyContentType(): String {
//                    // Set the content type to "application/json"
//                    return "application/json"
//                }
//
//            }
//            stringRequest.retryPolicy = DefaultRetryPolicy(initialTimeoutMs, maxRetries, backoffMultiplier)
//
//            volley.add(stringRequest)
//        } else {
//            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_LONG).show()
//        }
//    }

}

