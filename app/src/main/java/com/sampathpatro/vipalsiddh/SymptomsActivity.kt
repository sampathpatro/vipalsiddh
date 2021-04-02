package com.sampathpatro.vipalsiddh

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_symptoms.*

val symptomsList = ArrayList<String>()
val chosenSymptomsList = mutableListOf<String>()

class SymptomsActivity : AppCompatActivity(), SymptomsAdapter.OnSymptomClickListener, ChosenSymptomsAdapter.OnSymptomClickListener {

    private var swipeBackground: ColorDrawable = ColorDrawable(Color.parseColor("#FF0000"))
    private lateinit var deleteIcon: Drawable

    val symptomAdapter = ChosenSymptomsAdapter(chosenSymptomsList, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_symptoms)
        val view = findViewById<ConstraintLayout>(R.id.constraintView)

        potDiseases.clear()

        deleteIcon = ContextCompat.getDrawable(this, R.drawable.ic_delete_24px)!!

        btn_chosenSymptomsPage.setOnClickListener {
            when {
                chosenSymptomsList.size in 2..5 -> {
                    val intent = Intent(this, ChosenSymptomsActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                chosenSymptomsList.size > 5 -> {
                    Snackbar.make(view, "Choose at the most 5 symptoms.", Snackbar.LENGTH_LONG)
                        .show()
                }
                chosenSymptomsList.size < 2 -> {
                    Snackbar.make(view, "Choose at least 2 symptoms.", Snackbar.LENGTH_LONG)
                        .show()
                }
            }
        }

        symptomsList.sort()

        val recyclerView = recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        val symptomsRecyclerView = symptomsRecyclerView
        symptomsRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        val adapter = SymptomsAdapter(symptomsList.distinct() as ArrayList<String>, this)

        recyclerView.adapter = adapter
        symptomsRecyclerView.adapter = symptomAdapter


        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, position: Int) {
                symptomAdapter.removeItem(viewHolder)
                adapter.notifyDataSetChanged()
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val itemMargin = (itemView.height - deleteIcon.intrinsicHeight)/2

                swipeBackground.setBounds(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                deleteIcon.setBounds(itemView.left+itemMargin, itemView.top+itemMargin, itemView.left+itemMargin+deleteIcon.intrinsicWidth, itemView.bottom-itemMargin)
                swipeBackground.draw(c)
                deleteIcon.draw(c)

                c.save()

                c.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)

                c.restore()

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive
                )
            }

        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(symptomsRecyclerView)

        //----------------------------------------------------------------------------------------------------------------------------------------------------

        symptom_search.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                adapter.filter.filter(p0)
                return true
            }
        })

    }

    override fun onItemClick(item: String, position: Int) {
        if (!chosenSymptomsList.contains(item)) {
            var count = chosenSymptomsList.size
            if (count <= 4) {
                chosenSymptomsList.add(item)
                if (chosenSymptomsList.isEmpty()){
                    symptomAdapter.notifyDataSetChanged()
                } else {
                    symptomAdapter.notifyDataSetChanged()
                }
            } else {
                Toast.makeText(this, "Exceeding 5 symptoms limit.", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            chosenSymptomsList.remove(item)
            symptomAdapter.notifyDataSetChanged()
        }
    }

    override fun onChosenItemClick(item: String, position: Int) {
        Toast.makeText(baseContext, "Swipe Right to Delete.", Toast.LENGTH_SHORT).show()
    }

}

