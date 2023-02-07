package com.symphony.symphony

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.symphony.symphony.databinding.ActivityTechnicianDashboardBinding
import java.util.Calendar
import java.util.Stack



class TechnicianDashboard : AppCompatActivity() {
    private lateinit var binding: ActivityTechnicianDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTechnicianDashboardBinding.inflate(layoutInflater)
        fun onResponse(response: String) {
            val view = binding.root
            setContentView(view)


            val recyclerView = binding.rcvRecyclerView
            recyclerView.layoutManager = LinearLayoutManager(this)

            binding.txvHello.setText(greeting())

            val data = ArrayList<ItemsViewModel>()
            for (i in 1..20) {
                data.add(ItemsViewModel("12345678", "31/01/2023", 200000, "KCB Gigiri"))
            }

            val adapter = CustomAdapter(data)

            recyclerView.adapter = adapter


        }


//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        val inflater: MenuInflater = menuInflater
//        inflater.inflate(R.menu.tech_menu, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.mnuHelp -> {
//                Toast.makeText(this, "${item.itemId} has been selected", Toast.LENGTH_SHORT).show()
//                true
//            }
//
//            R.id.mnuPrivacy -> {
//                Toast.makeText(this, "${item.itemId} has been selected", Toast.LENGTH_SHORT).show()
//                true
//            }
//
//            R.id.mnuSignOut -> {
//                Toast.makeText(this, "${item.itemId} has been selected", Toast.LENGTH_SHORT).show()
//                true
//            }
//
//            else -> {super.onOptionsItemSelected(item)}
//        }
//    }
//
    }
    fun greeting(): String {
        val calendar = Calendar.getInstance()
        val timeOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        var greeting: String = "hello"


        return when (timeOfDay) {
            in 0..11 -> "Good Morning"
            in 12..15 -> "Good Afternoon"
            in 16..20 -> "Good Evening"
            in 21..23 -> "Good Night"
            else -> "Hello"
        }
    }

}