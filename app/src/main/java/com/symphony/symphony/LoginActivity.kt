package com.symphony.symphony

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.auth.api.credentials.Credentials
import com.symphony.symphony.databinding.ActivityLoginBinding
import java.security.MessageDigest

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var progressBar: View
    private lateinit var signIn: Button
    private lateinit var forgotPass: TextView
    private lateinit var lnLayout: View
    private lateinit var hashedPassword: String

    private val RC_SAVE = 1
    private val RC_READ = 2

    @SuppressLint("ClickableViewAccessibility")
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


            } else loginNew().also{
                val credentialsClient = Credentials.getClient(this)
                val credential = com.google.android.gms.auth.api.credentials.Credential.Builder(
                    "user@example.com")
                    .setPassword("password123")
                    .build()
            }
        }

        val pass = binding.edtPassword
        var isPasswordVisible = false // keep track of current state

        pass.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawable = pass.compoundDrawables[2] // index 2 is for end drawable
                if (drawable != null && event.x >= pass.width - pass.paddingEnd - drawable.bounds.width()) {
                    // The touch event is inside the bounds of the drawable
                    isPasswordVisible = !isPasswordVisible // toggle state
                    if (isPasswordVisible) {
                        // show password
                        pass.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        pass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_off_24, 0)
                    } else {
                        // hide password
                        pass.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        pass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_24, 0)
                    }
                    true
                } else {
                    false
                }
            } else {
                false
            }
        }


    }


    fun loginNew() {


        signIn.isClickable = false
        signIn.text = ""
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
            object : StringRequest(Method.POST, url, Response.Listener<String> { response ->

                progressBar.visibility = View.GONE // hide the progress bar
                signIn.text = "sign in"
                signIn.isClickable = true // enable the sign in button

                // Parse the response to get the user ID
                val userID = response.substringAfter("User ID :").trim()
                val userName = response.substringAfter("User name :").substringBefore("User").trim()
                val user = userName.substring(0 , userName.indexOf(" ") +2)
                val userN = "$user."


                hashedPassword = hash(password)
                storeLoginCredentials(email, hashedPassword, userID, userN, applicationContext)


                // Handle the API response on success.
                Toast.makeText(applicationContext, "Login successful", Toast.LENGTH_LONG).show()
                val intent = Intent(applicationContext, TechnicianDashboard::class.java)
                intent.putExtra("id", userID)
                intent.putExtra("name", userN)
                startActivity(intent)
                finish()
            }, Response.ErrorListener { error ->
                progressBar.visibility = View.GONE // hide the progress bar
                signIn.text = "sign in"
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


    // Hash password using SHA-256 algorithm
    fun hash(password: String): String {
        val bytes = password.toByteArray(Charsets.UTF_8)
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("", { str, it -> str + "%02x".format(it) })
    }

    fun storeLoginCredentials(username: String, password: String, userID: String, userName: String ,context: Context) {
        // Initialize SharedPreferences
        val sharedPref = context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)

        // Get a SharedPreferences.Editor to modify the preferences
        val editor = sharedPref.edit()

        // Store approved login credentials
        editor.putString("username", username)
        editor.putString("password", password)
        editor.putString("userID", userID)
        editor.putString("userName", userName)

        // Commit the changes to SharedPreferences
        editor.apply()
    }


//    private fun readCredentials(domain: String) {
//        val options = HintRequest.Builder()
//            .setAccountTypes(domain)
//            .setPasswordLoginSupported(true)
//            .build()
//
//        val client = Credentials.getClient(this)
//        val intent = client.getHintPickerIntent(options)
//        try {
//            startIntentSenderForResult(intent.intentSender, RC_READ, null, 0, 0, 0)
//        } catch (e: IntentSender.SendIntentException) {
//            Log.e("MainActivity", "Error reading credentials: $e")
//        }
//    }
//    private fun saveCredentials(email: String, password: String) {
//        val credentialsClient = Credentials.getClient(this)
//        val credential = Credential.Builder(email)
//            .setPassword(password)
//            .build()
//        val request = SaveRequest.Builder()
//            .setCredentials(listOf(credential))
//            .build()
//        credentialsClient.save(request).addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                Log.d("Login", "Credentials saved")
//            } else {
//                Log.e("Login", "Error saving credentials: ${task.exception?.message}")
//            }
//        }
//    }
//    private fun getSavedCredentials() {
//        val credentialsClient = Credentials.getClient(this)
//        credentialsClient.getSavedCredentials(CredentialPickerConfig.Builder().build()).addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                val credentials = task.result
//                if (credentials.isNotEmpty()) {
//                    val credential = credentials[0]
//                    val email = credential.id
//                    val password = credential.password
//                    Log.d("Login", "Retrieved credentials: $email, $password")
//                    // TODO: Use the retrieved credentials to log in
//                } else {
//                    Log.d("Login", "No saved credentials found")
//                }
//            } else {
//                Log.e("Login", "Error getting saved credentials: ${task.exception?.message}")
//            }
//        }
//    }

}

