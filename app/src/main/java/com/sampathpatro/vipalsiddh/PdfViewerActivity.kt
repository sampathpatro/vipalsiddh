package com.sampathpatro.vipalsiddh

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sampathpatro.vipalsiddh.utils.FileUtils
import kotlinx.android.synthetic.main.activity_about_me.*
import kotlinx.android.synthetic.main.activity_pdf_viewer.*

class PdfViewerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_viewer)

        showPdfFromAssets(FileUtils.getPdfFromAssets())

    }

    private fun showPdfFromAssets(pdfName: String){
        pdfView.fromAsset(pdfName)
            .password(null)
            .defaultPage(0)
            .onPageError{ page, t ->  
                Toast.makeText(
                    this@PdfViewerActivity,
                    "Error Loading Page: $page", Toast.LENGTH_SHORT
                ).show()
            }.load()
    }

}