package com.sampathpatro.vipalsiddh

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_disease.*

var potDiseases = mutableListOf<Disease>()
var requiredDisease = listOf<Disease>()

class DiseaseActivity : AppCompatActivity(), DiseasesAdapter.onDiseaseClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disease)

        suggestionBtn.setOnClickListener {
            startActivity(Intent(this, ReportBugActivity::class.java))
            finish()
        }

        val sharedPreferences = getSharedPreferences("PREFS", 0)
        val showAgain = sharedPreferences.getBoolean("don'tShow", false)

        val changed = intent.getBooleanExtra("CHANGES", false)

        if (!showAgain){
            showDialog()
        }

        if (chosenSymptomsList.isEmpty()) {
            Toast.makeText(baseContext, "Error occured, please try again.", Toast.LENGTH_SHORT)
                .show()
        }

        val diseaseRecyclerView = diseaseRecyclerView
        diseaseRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        if (chosenSymptomsList.isNotEmpty()){
            for (dis in diseasesList){
                dis.match = 0
                for (symptom in chosenSymptomsList){
                    if (dis.diseaseSymptoms.contains(symptom)){
                        if (!potDiseases.contains(dis)){
                            potDiseases.add(dis)
                        }
                    } else {
                        Log.d("Exceeded", "Size exceeded")
                    }
                }
            }

            for (dis in potDiseases){
                for (chosenSymptom in chosenSymptomsList){
                    if (dis.diseaseSymptoms.contains(chosenSymptom)){
                        dis.match += 1
                    }
                }
            }


                val iterator = potDiseases.iterator()
                while (iterator.hasNext()){
                    val item = iterator.next()
                    if (chosenSymptomsList.size <=3){
                        if (item.match < 2){
                            iterator.remove()
                        }
                    } else
                        if (item.match<3){
                            iterator.remove()
                        }
                }


            potDiseases.sortByDescending { it.spread }

            requiredDisease = potDiseases.distinct()

        }

        if (requiredDisease.isEmpty()){
            suggestionTxt.visibility = View.VISIBLE
            suggestionTxt2.visibility = View.VISIBLE
            suggestionBtn.visibility = View.VISIBLE
        } else {
            suggestionTxt.visibility = View.GONE
            suggestionTxt2.visibility = View.GONE
            suggestionBtn.visibility = View.GONE
        }

        if (requiredDisease.size > 4){
            showDisclaimer(showAgain)
        }

        val adapter = DiseasesAdapter(requiredDisease, this)
        diseaseRecyclerView.adapter = adapter
    }

    private fun showDisclaimer(showAgain: Boolean) {
        val adb = AlertDialog.Builder(this)
        adb.apply {
            setTitle("Too Many Diseases!")
            setIcon(R.drawable.ic_logo)
            setMessage("The symptoms you have chosen are common to many diseases, it is recommended " +
                    "to be more specific while choosing your symptoms.")
            setPositiveButton("UNDERSTOOD!") { dialog, which ->
                dialog.dismiss()
            }
        }

        val alertDialog = adb.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    override fun onDiseaseClick(item: Disease, position: Int) {
        val intent = Intent(this, DisDescActivity::class.java)
        intent.putExtra("DISNAME", item.name)
        intent.putExtra("DISSYM", item.diseaseSymptoms)
        intent.putExtra("DISDES", item.desc)
        intent.putExtra("DISSER", item.seriousness)
        intent.putExtra("DISSPR", item.spread)
        startActivity(intent)
    }

    private fun showDialog(){
        val adb = AlertDialog.Builder(this@DiseaseActivity)
        adb.setTitle("Disclaimer")
        adb.setMessage("This app is developed for educational purposes, and must not be used in case " +
                "of emergency. If you feel uncomfortable, it is strongly recommended that you consult a doctor " +
                "before making taking any decisions.")
        adb.setIcon(R.drawable.ic_logo)
        adb.setPositiveButton("OK, UNDERSTOOD!") { dialog, which ->
            dialog.dismiss()
        }
        adb.setNeutralButton("DON'T SHOW AGAIN") { dialog, which ->
            dialog.dismiss()
            val sharedPreferences = getSharedPreferences("PREFS", 0)
            val editor = sharedPreferences.edit()
            editor.putBoolean("don'tShow", true)
            editor.apply()
        }

        val alertDialog = adb.create()
        alertDialog.setCancelable(false)
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()

    }


}