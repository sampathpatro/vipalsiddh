package com.sampathpatro.vipalsiddh

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_on_boarding.tabLayout
import kotlinx.android.synthetic.main.activity_tutorial.*

class TutorialActivity : AppCompatActivity(), SliderAdapter.OnButtonClick {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)

        val tutorialHeadingList = ArrayList<String>()
        val tutorialImageList = ArrayList<Int>()
        val tutorialDescList = ArrayList<String>()

        tutorialSkipBtn.setOnClickListener {
            startActivity(Intent(this, TermsConditionsActivity::class.java))
            finish()
        }

        tutorialHeadingList.add("Welcome!")
        tutorialHeadingList.add("Self Diagnosis")
        tutorialHeadingList.add("Nearby Hospitals")

        tutorialImageList.add(R.drawable.ic_logo)
        tutorialImageList.add(R.drawable.ic_surgeon)
        tutorialImageList.add(R.drawable.ic_hospital)

        tutorialDescList.add("Thank You for trusting Vipal Siddh!\nHere's a short tutorial on how to use the app.\n \n" +
                "Swipe right to continue.")
        tutorialDescList.add("This is the main feature of the app.\nYou can let the app know how you feel, " +
                "and it will show you any potential diseases you might be having.")
        tutorialDescList.add("This feature will let you know the locations of nearby hospitals through Google Maps.\n" +
                "(Location services will be required)")


        val pagerLayout = tutorialViewPager
        pagerLayout.adapter = SliderAdapter(tutorialHeadingList.distinct() as ArrayList<String>,
            tutorialImageList.distinct() as ArrayList<Int>, tutorialDescList.distinct() as ArrayList<String>,
            this)

        TabLayoutMediator(tabLayout, tutorialViewPager){ tab, position ->
        }.attach()
        
    }

    override fun buttonClick(heading: String, position: Int) {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}