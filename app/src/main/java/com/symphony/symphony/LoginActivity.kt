package com.symphony.symphony

import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.PrecomputedText.Params
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.symphony.symphony.databinding.ActivityLoginBinding
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.Method
import kotlin.jvm.Throws

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root


        val progressBar = binding.progressBar
        val contactAdmin = binding.txvCAdministrator

        contactAdmin.paint.isUnderlineText = true

        setContentView(view)

    }

    fun login(view: View) {
        val url = "https://symphony.co.ke/symphony_api/login.php"
        val email = binding.edtEmail
        val password = binding.edtPassword
        val emailText = email.text.toString().trim()
        val passwordText = password.text.toString().trim()

        if (emailText.isNotEmpty() && passwordText.isNotEmpty()) {

            val volley = Volley.newRequestQueue(this)
            val stringRequest = object: StringRequest(Request.Method.POST, url, Response.Listener{ response: String ->
                if (response == "success") {
                    val intent = Intent(applicationContext, TechnicianDashboard::class.java)
                    startActivity(intent)
                    finish()
                } else if (response == "failed") {
                    Toast.makeText(this, "Invalid log in Id/Password", Toast.LENGTH_SHORT)
                        .show()
                }

            }, Response.ErrorListener{ error ->
                Toast.makeText(applicationContext, error.localizedMessage, Toast.LENGTH_SHORT)
                    .show()
                Log.d("Volley", error.toString().trim())

            }) {
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["email"] = emailText
                    params["password"] = passwordText
                    return params
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val params: HashMap<String, String> = HashMap<String, String>()
                    params["Content-Type"] = "application/x-www-form-urlencoded"
                    return params
                }

            }
            volley.add(stringRequest)
        }
        else{
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_LONG).show()
        }


    }
}
