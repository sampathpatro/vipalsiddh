package com.sampathpatro.vipalsiddh

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_terms_conditions.*

class TermsConditionsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_conditions)

        val preferences: SharedPreferences = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
        val acceptedTnC = preferences.getBoolean("AcceptedTnC", false)
        val editor: SharedPreferences.Editor = preferences.edit()

        if (acceptedTnC){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        proceed_btn.setOnClickListener {
            if (accepttc.isChecked){
                editor.putBoolean("AcceptedTnC", true)
                editor.apply()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "You will have to accept the terms of usage in order to proceed.",
                    Toast.LENGTH_LONG).show()
            }
        }

        termsofusage.setOnClickListener {
            startActivity(Intent(this, PdfViewerActivity::class.java))
        }

    }
}