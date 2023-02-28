package com.symphony.symphony

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.symphony.symphony.databinding.ActivityLoginBinding
import org.json.JSONObject
import org.mindrot.jbcrypt.BCrypt

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var progressBar: ProgressBar
    private lateinit var signIn: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root


        progressBar = binding.progressBar


        val contactAdmin = binding.txvCAdministrator

        contactAdmin.paint.isUnderlineText = true

        setContentView(view)
        signIn = binding.btnSignIn

        signIn.setOnClickListener {
//            progressBar.visibility = View.VISIBLE
//            signIn.setBackground(getDrawable(R.drawable.button_grey))
            jsonLogin()
        }

    }

    fun jsonLogin() {

        val url = "https://backend.api.symphony.co.ke/login"
        val email = binding.edtEmail
        val password = binding.edtPassword
        val emailText = email.text.toString().trim()
        val passwordText = password.text.toString().trim()
        val salt = BCrypt.gensalt(10)
        val hashedPassword: String = BCrypt.hashpw(passwordText, salt)

        val queue = Volley.newRequestQueue(this)

        val jsonBody = JSONObject()
        jsonBody.put("email", emailText)
        jsonBody.put("hahsedPassword", hashedPassword)

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonBody,
            { response ->
                // Handle successful login response
                val token = response.getString("token")
                // Save token for future requests

                // Make subsequent authenticated requests with token
                val authenticatedUrl = "http://example.com/api/some-endpoint"
                val authenticatedRequest = object : JsonObjectRequest(
                    Method.GET, authenticatedUrl, null,
                    { authenticatedResponse ->
                        // Handle authenticated response
                        Log.d("LoginA", "Login successful")
                    },
                    { error ->
                        // Handle authenticated error
                        Log.d("LoginE", "Log in credentials do not match")
                    }
                ) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Authorization"] = "Bearer $token"
                        return headers
                    }
                }

                queue.add(authenticatedRequest)
            },
            { error ->
                // Handle login error
                Log.d("LoginER", error.toString())
            }
        )

        queue.add(request)
    }

//    fun login() {
//        val url = "https://backend.api.symphony.co.ke/login"
//        val email = binding.edtEmail
//        val password = binding.edtPassword
//        val emailText = email.text.toString().trim()
//        val passwordText = password.text.toString().trim()
//        val salt = BCrypt.gensalt(10)
//        val hashedPassword: String = BCrypt.hashpw(passwordText, salt)
//        val maxRetries = 3
//        val initialTimeoutMs = 5000
//        val backoffMultiplier = 2f
//
//        val jsonObject = JSONObject()
//        jsonObject.put("email", emailText)
//        jsonObject.put("hashedPassword", hashedPassword)
//
//        Log.d("Hash", hashedPassword)
//        if (emailText.isNotEmpty() && passwordText.isNotEmpty()) {
//
//            val volley = Volley.newRequestQueue(this)
//            val stringRequest = object :
//                StringRequest(Request.Method.POST, url, Response.Listener { response: String ->
////                    progressBar.visibility = View.GONE
////                    signIn.setBackground( getDrawable(R.drawable.button))
//
//                    if (response == "Login successful") {
//                        val intent = Intent(applicationContext, TechnicianDashboard::class.java)
//                        startActivity(intent)
//                        finish()
//                    } else if (response == "Invalid email or password") {
//                        Toast.makeText(this, "Invalid log in Id/Password", Toast.LENGTH_SHORT)
//                            .show()
//                    }
//
//                }, Response.ErrorListener { error ->
//                    Toast.makeText(applicationContext, error.localizedMessage, Toast.LENGTH_SHORT).show()
//                    Log.d("Volley", error.toString().trim())
//
//                }) {
//                override fun getBody(): ByteArray {
//                    // Convert the JSON object to a byte array
//                    return jsonObject.toString().toByteArray(Charsets.UTF_8)
//                }
//
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

