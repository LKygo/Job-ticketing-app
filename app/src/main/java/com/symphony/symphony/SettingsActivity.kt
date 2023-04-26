package com.symphony.symphony

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.NetworkError
import com.android.volley.NoConnectionError
import com.android.volley.ParseError
import com.android.volley.Response
import com.android.volley.ServerError
import com.android.volley.TimeoutError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.symphony.symphony.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var currentPass: EditText
    private lateinit var newPass: EditText
    private lateinit var confirmPass: EditText
    private lateinit var btnChangePass: Button
    private lateinit var progressReset: ProgressBar
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        val view = binding.root
        super.onCreate(savedInstanceState)
        setContentView(view)

        val toolbar = binding.myToolbar
        setSupportActionBar(toolbar)

        // Enable the back button on the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

// Define the action when the back button is pressed
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }





        currentPass = binding.edtCurrentPass
        newPass = binding.edtNewPassword
        confirmPass = binding.edtConfirmPassword
        btnChangePass = binding.btnChangePass
        progressReset = binding.progressBReset




        btnChangePass.setOnClickListener {
            val sharedPref = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
            val userId = sharedPref.getString("userID", null)
            val oldPass = binding.edtCurrentPass.text.toString()
            val newPass = binding.edtNewPassword.text.toString()
            val confirmPass = binding.edtConfirmPassword.text.toString()

            if (newPass != confirmPass) {
                Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show()
            } else {
                if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                } else {

                    if (userId.isNullOrEmpty()) {
                        Toast.makeText(
                            this,
                            "No user Id found. Please log out and log in again",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else
                        inAppPassReset(userId, oldPass, newPass)
                }
            }
        }
        binding.txvForgotPassword.setOnClickListener {

        }


        var isPasswordVisible = false // keep track of current state

        currentPass.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawable = currentPass.compoundDrawables[2] // index 2 is for end drawable
                if (drawable != null && event.x >= currentPass.width - currentPass.paddingEnd - drawable.bounds.width()) {
                    // The touch event is inside the bounds of the drawable
                    isPasswordVisible = !isPasswordVisible // toggle state
                    if (isPasswordVisible) {
                        // show password
                        currentPass.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        currentPass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_off_24, 0)
                    } else {
                        // hide password
                        currentPass.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        currentPass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_24, 0)
                    }
                    true
                } else {
                    false
                }
            } else {
                false
            }
        }

        newPass.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawable = newPass.compoundDrawables[2] // index 2 is for end drawable
                if (drawable != null && event.x >= newPass.width - newPass.paddingEnd - drawable.bounds.width()) {
                    // The touch event is inside the bounds of the drawable
                    isPasswordVisible = !isPasswordVisible // toggle state
                    if (isPasswordVisible) {
                        // show password
                        newPass.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        newPass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_off_24, 0)
                    } else {
                        // hide password
                        newPass.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        newPass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_24, 0)
                    }
                    true
                } else {
                    false
                }
            } else {
                false
            }
        }

        confirmPass.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawable = confirmPass.compoundDrawables[2] // index 2 is for end drawable
                if (drawable != null && event.x >= confirmPass.width - confirmPass.paddingEnd - drawable.bounds.width()) {
                    // The touch event is inside the bounds of the drawable
                    isPasswordVisible = !isPasswordVisible // toggle state
                    if (isPasswordVisible) {
                        // show password
                        confirmPass.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        confirmPass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_off_24, 0)
                    } else {
                        // hide password
                        confirmPass.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        confirmPass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_24, 0)
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


    private fun inAppPassReset(userId: String, oldPass: String, newPass: String) {


//        Set disable views and show progress
        btnChangePass.isClickable = false
        btnChangePass.text = ""
        progressReset.visibility = View.VISIBLE


        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)

        // Set up the resetPass endpoint URL and request parameters.
        val url = "https://backend.api.symphony.co.ke/inAppReset"
        val params = HashMap<String, String>()
        params["id"] = userId
        params["oldPass"] = oldPass
        params["newPass"] = newPass

        Log.d("reset", params.toString())
        // Create a new POST request with the login endpoint URL and parameters.
        val stringRequest =
            object : StringRequest(Method.POST, url, Response.Listener<String> { response ->

                progressReset.visibility = View.GONE // hide the progress bar
                btnChangePass.text = "Change Password"
                btnChangePass.isClickable = true // enable the sign in button


                // Handle the API response on success.
                Toast.makeText(applicationContext, "Password successfully reset", Toast.LENGTH_SHORT)
                    .show()
                Log.d("reset", response.toString())
            }, Response.ErrorListener { error ->


                progressReset.visibility = View.GONE // hide the progress bar
                btnChangePass.text = "Change Password"
                btnChangePass.isClickable = true // enable the sign in button

                // Handle the API response on error.
                val errorMessage = when (error) {
                    is NoConnectionError -> "No internet connection"
                    is TimeoutError -> "Connection timed out"
                    is ServerError -> "Error with the server"
                    is AuthFailureError -> "Authentication failure"
                    is NetworkError -> "Network error"
                    is ParseError -> "Parse error"
                    else -> "Unknown error"
                }
                val statusCode = error.networkResponse?.statusCode ?: -1
                val errorString = String(error.networkResponse?.data ?: ByteArray(0))

                val message = errorMessage


                Toast.makeText(this, "Error code: $statusCode $errorString", Toast.LENGTH_SHORT)
                    .show()
                Log.e("reset", message)

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
