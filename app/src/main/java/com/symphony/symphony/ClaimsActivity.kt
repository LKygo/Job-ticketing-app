package com.symphony.symphony

import android.os.Bundle
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
//
//        binding.edtKMCovered.isEnabled = false
//        binding.txvKMCovered.alpha = 0.5f
//
//        binding.edtFarePaid.isEnabled = true
//        binding.txvPsvFare.alpha = 1f

        binding.ClaimsRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == binding.RadioPsv.id) {
                // show views for PSV option
                binding.edtKMCovered.isEnabled = false
                binding.txvKMCovered.alpha = 0.5f

                binding.edtFarePaid.isEnabled = true
                binding.txvPsvFare.alpha = 1f

            } else if (checkedId == binding.RadioPrivate.id) {
                // show views for Private option
                binding.edtFarePaid.isEnabled = false
                binding.txvPsvFare.alpha = 0.5f

                binding.edtKMCovered.isEnabled = true
                binding.txvKMCovered.alpha = 1f

            } else {
                // show default views
                binding.edtKMCovered.isEnabled = false
                binding.txvKMCovered.alpha = 0.5f

                binding.edtFarePaid.isEnabled = true
                binding.txvPsvFare.alpha = 1f
            }
        }

    }
}