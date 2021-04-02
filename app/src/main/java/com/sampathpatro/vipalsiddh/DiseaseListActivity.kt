package com.sampathpatro.vipalsiddh

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_disease_list.*

class DiseaseListActivity : AppCompatActivity(), DiseasesAdapter.onDiseaseClickListener{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disease_list)

        val diseaseListRecyclerView = diseaseListRecyclerView
        diseaseListRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        val adapter = DiseasesAdapter(diseasesList, this)
        diseaseListRecyclerView.adapter = adapter

        let_us_know_text.setOnClickListener {
            startActivity(Intent(this, ReportBugActivity::class.java))
        }

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
}