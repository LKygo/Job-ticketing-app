package com.symphony.symphony

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.symphony.symphony.databinding.ActivityClaimsBinding

class ClaimsActivity : AppCompatActivity() {
    private lateinit var binding : ActivityClaimsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityClaimsBinding.inflate(layoutInflater)
        val view = binding.root
        super.onCreate(savedInstanceState)
        setContentView(view)

        val toolbar = findViewById<Toolbar>(R.id.my_toolbar)
        setSupportActionBar(toolbar)


        binding.ClaimsRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == binding.RadioPsv.id) {
                // show views for PSV option
                binding.txvPsvFare.visibility = View.VISIBLE
                binding.edtFarePaid.visibility = View.VISIBLE
                binding.txvKMCovered.visibility = View.GONE
                binding.edtKMCovered.visibility = View.GONE
            } else if (checkedId == binding.RadioPrivate.id) {
                // show views for Private option
                binding.txvPsvFare.visibility = View.GONE
                binding.edtFarePaid.visibility = View.GONE
                binding.txvKMCovered.visibility = View.VISIBLE
                binding.edtKMCovered.visibility = View.VISIBLE
            } else {
                // show default views
                binding.txvPsvFare.visibility = View.VISIBLE
                binding.edtFarePaid.visibility = View.VISIBLE
                binding.txvKMCovered.visibility = View.GONE
                binding.edtKMCovered.visibility = View.GONE
            }
        }

    }
}