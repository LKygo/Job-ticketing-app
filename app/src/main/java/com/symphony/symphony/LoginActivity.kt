package com.symphony.symphony

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.symphony.symphony.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var progressBar: View
    private lateinit var signIn: Button
    private lateinit var forgotPass: TextView
    private lateinit var lnLayout: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        lnLayout = binding.lnlLinearLayout
        forgotPass = binding.txvForgotPassword
        signIn = binding.btnSignIn
        progressBar = binding.progressBar
        progressBar.visibility = View.GONE
        val signUp = binding.txvSignUp

        forgotPass.paint.isUnderlineText = true
        forgotPass.setOnClickListener {
            val intent = Intent(applicationContext, ResetPass::class.java)
            startActivity(intent)
        }


        signIn.setOnClickListener {

            var email = binding.edtEmail.text.toString()
            var password = binding.edtPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT)
                    .show()


            } else loginNew()
        }

    }

//
//    fun loginNew() {
//        signIn.isClickable = false
//        signIn.visibility = View.GONE
//        progressBar.visibility = View.VISIBLE
//        val email = binding.edtEmail.text.toString()
//        val password = binding.edtPassword.text.toString()
//
//        // Instantiate the RequestQueue.
//        val queue = Volley.newRequestQueue(this)
//
//        // Set up the login endpoint URL and request parameters.
//        val url = "https://backend.api.symphony.co.ke/login"
//        val params = HashMap<String, String>()
//        params["email"] = email
//        params["password"] = password
//
//        // Create a new POST request with the login endpoint URL and parameters.
//        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url,
//            (params as Map<*, *>?)?.let { JSONObject(it) },
//            { response ->
//                progressBar.visibility = View.GONE // hide the progress bar
//                signIn.visibility = View.VISIBLE
//                signIn.isClickable = true // enable the sign in button
//
//                // Handle the API response on success.
//                Toast.makeText(applicationContext, response.toString(), Toast.LENGTH_LONG).show()
//
//                // Parse the response and extract relevant data
//                val success = response.getBoolean("success")
//                val message = response.getString("message")
//                if (success) {
//                    // User authentication successful, start new activity
//                    val intent = Intent(applicationContext, TechnicianDashboard::class.java)
//                    startActivity(intent)
//                    //finish()
//                } else {
//                    // User authentication failed, show error message
//                    Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
//                }
//            },
//            { error ->
//                progressBar.visibility = View.GONE // hide the progress bar
//                signIn.visibility = View.VISIBLE
//                signIn.isClickable = true // enable the sign in button
//
//                // Handle the API response on error.
//                val errorMessage = error.networkResponse?.statusCode?.let {
//                    when (it) {
//                        401 -> "Incorrect email or password"
//                        else -> "Internal Server Error"
//                    }
//                } ?: "Network Error"
//                Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_LONG).show()
//            })
//
//        // Add the request to the RequestQueue.
//        queue.add(jsonObjectRequest)
//    }













    fun loginNew() {


        signIn.isClickable = false
        signIn.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
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
        val stringRequest =
            object : StringRequest(Request.Method.POST, url, Response.Listener<String> { response ->
                progressBar.visibility = View.GONE // hide the progress bar
                signIn.visibility = View.VISIBLE
                signIn.isClickable = true // enable the sign in button

                // Parse the response to get the user ID
                val prefix = "User ID :"
                val userID = response.substringAfter("User ID :").trim()
                // Handle the API response on success.
                Toast.makeText(applicationContext, response.toString(), Toast.LENGTH_LONG).show()
                val intent = Intent(applicationContext, TechnicianDashboard::class.java)
                intent.putExtra("id", userID)
                startActivity(intent)
//                finish()
            }, Response.ErrorListener { error ->
                progressBar.visibility = View.GONE // hide the progress bar
                signIn.visibility = View.VISIBLE
                signIn.isClickable = true // enable the sign in button
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

}

