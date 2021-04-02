package com.sampathpatro.vipalsiddh

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_on_boarding.*

val headingList = ArrayList<String>()
val imageList = ArrayList<Int>()
val descList = ArrayList<String>()

class OnBoardingActivity : AppCompatActivity(), SliderAdapter.OnButtonClick {
    @SuppressLint("ApplySharedPref")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)

        sliderViewPager.alpha = 0F
        skipBtn.alpha = 0F
        tabLayout.alpha = 0F

        val handler = Handler()
        handler.postDelayed({ splashScreenOff() }, 3000)

        val preferences: SharedPreferences = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
        val FirstTime = preferences.getString("FirstTimeInstall", "")

        if (FirstTime.equals("Yes")){
            startActivity(Intent(baseContext, MainActivity::class.java))
            finish()
        } else {
            val editor: SharedPreferences.Editor = preferences.edit()
            editor.putString("FirstTimeInstall", "Yes")
            editor.apply()
        }

        skipBtn.setOnClickListener {
            startActivity(Intent(this, TermsConditionsActivity::class.java))
            finish()
        }

        headingList.add("Welcome!")
        headingList.add("Self Diagnosis")
        headingList.add("Nearby Hospitals")

        imageList.add(R.drawable.ic_logo)
        imageList.add(R.drawable.ic_surgeon)
        imageList.add(R.drawable.ic_hospital)

        descList.add("Thank You for trusting Vipal Siddh!\nHere's a short tutorial on how to use the app.\n \n" +
                "Swipe right to continue.")
        descList.add("This is the main feature of the app.\nYou can let the app know how you feel, " +
                "and it will show you any potential diseases you might be having.")
        descList.add("This feature will let you know the locations of nearby hospitals through Google Maps.\n" +
                "(Location services will be required)")


        val pagerLayout = sliderViewPager
        pagerLayout.adapter = SliderAdapter(headingList.distinct() as ArrayList<String>,
            imageList.distinct() as ArrayList<Int>, descList.distinct() as ArrayList<String>,
            this)

        TabLayoutMediator(tabLayout, sliderViewPager){ tab, position ->
        }.attach()

    }

    private fun splashScreenOff() {
        val aniFadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)
        val aniFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        sliderViewPager.apply {
            startAnimation(aniFadeIn)
            alpha = 1F
        }
        skipBtn.apply {
            startAnimation(aniFadeIn)
            alpha = 1F
        }
        tabLayout.apply {
            startAnimation(aniFadeIn)
            alpha = 1F
        }
        imageView4.startAnimation(aniFadeOut)
        imageView4.visibility = View.GONE

    }

    override fun buttonClick(heading: String, position: Int) {
        startActivity(Intent(this, TermsConditionsActivity::class.java))
        finish()
    }
}