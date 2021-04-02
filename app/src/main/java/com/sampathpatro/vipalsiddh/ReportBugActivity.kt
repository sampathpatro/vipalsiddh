package com.sampathpatro.vipalsiddh

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_report_bug.*

class ReportBugActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_bug)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        report_btn.setOnClickListener {
            val message = bug_message.text.toString().trim()
            val recipient = "sampathpatro26@gmail.com"
            sendMail(message, recipient)
        }

    }

    private fun sendMail(message: String, recipient: String) {

         val mIntent = Intent(Intent.ACTION_SENDTO).apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient)) // recipients
            putExtra(Intent.EXTRA_SUBJECT, "Vipal Siddh Bug Report")
            putExtra(Intent.EXTRA_TEXT, message)
             data = Uri.parse("mailto:")
        }

        val activities: List<ResolveInfo> = packageManager.queryIntentActivities(
            mIntent,
            PackageManager.MATCH_DEFAULT_ONLY
        )
        val isIntentSafe: Boolean = activities.isNotEmpty()

        if (isIntentSafe){
            if (mIntent.resolveActivity(packageManager) != null) {
                startActivity(Intent.createChooser(mIntent, "Choose E-mail Option"))
            }
        } else {
            Toast.makeText(this, "Compatible App Not Found.", Toast.LENGTH_SHORT).show()
        }

    }
}