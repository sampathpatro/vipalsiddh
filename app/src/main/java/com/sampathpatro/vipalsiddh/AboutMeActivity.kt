package com.sampathpatro.vipalsiddh

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_about_me.*
import kotlinx.android.synthetic.main.activity_pdf_viewer.*

class AboutMeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_me)
        showPdfFromAssets()

    }

    private fun showPdfFromAssets() {
        aboutmepdf.fromAsset("About Me.pdf")
            .password(null)
            .defaultPage(0)
            .onPageError { page, t ->
                Toast.makeText(
                    this@AboutMeActivity,
                    "Error Loading Page: $page", Toast.LENGTH_SHORT
                ).show()
            }.load()

    }
}