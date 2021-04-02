package com.sampathpatro.vipalsiddh

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_chosen_symptoms.*

class ChosenSymptomsActivity : AppCompatActivity(), ChosenSymptomsAdapter.OnSymptomClickListener {

    private var swipeBackground: ColorDrawable = ColorDrawable(Color.parseColor("#FF0000"))
    private lateinit var deleteIcon: Drawable
    private var changes = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chosen_symptoms)

        btn_confirmSymptoms.setOnClickListener(){
            when {
                chosenSymptomsList.isEmpty() -> {
                    startActivity(Intent(baseContext, SymptomsActivity::class.java))
                    finish()
                    Toast.makeText(baseContext, "No Symptoms Chosen.", Toast.LENGTH_SHORT).show()
                }
                chosenSymptomsList.size in 2..5 -> {
                    val intent = Intent(this, DiseaseActivity::class.java)
                    intent.putExtra("CHANGES", changes)
                    for (dis in diseasesList){
                        if (dis.diseaseSymptoms.size < 4){
                            dis.match = 1
                        } else
                            dis.match = 0
                    }
                    startActivity(intent)
                }
                chosenSymptomsList.size < 2 -> {
                    startActivity(Intent(baseContext, SymptomsActivity::class.java))
                    finish()
                    Toast.makeText(baseContext, "Choose at least 2 symptoms.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        deleteIcon = ContextCompat.getDrawable(this, R.drawable.ic_delete_24px)!!

        val recyclerView = chosenSymptomsRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        val adapter = ChosenSymptomsAdapter(chosenSymptomsList, this)
        recyclerView.adapter = adapter

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, position: Int) {
                adapter.removeItem(viewHolder)
                adapter.notifyDataSetChanged()
                changes = true
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
        itemTouchHelper.attachToRecyclerView(chosenSymptomsRecyclerView)

    }

    override fun onChosenItemClick(item: String, position: Int) {
        Toast.makeText(this, "Swipe Right to Delete", Toast.LENGTH_SHORT).show()
    }

}